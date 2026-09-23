import json
import re
from datetime import datetime, timezone

INPUT_FILE = "src/main/resources/aws_api_final.json"
OUTPUT_FILE = "src/main/resources/aws_api_sanitized.json"

JUNK_SLUGS = {
    "aws-marketplace-dark", "aws-marketplace-light",
    "open-3d-engine", "freertos",
    "pytorch-on-aws", "tensorflow-on-aws",
    "aws-thinkbox-frost", "aws-thinkbox-krakatoa", "aws-thinkbox-stoke", "aws-thinkbox-xmesh",
    "aws-backint-agent", "aws-console-mobile-application"
}

STRICT_PAID_SERVICES = {
    "aws-cloudhsm", "aws-ground-station", "aws-private-certificate-authority",
    "aws-payment-cryptography", "amazon-route-53", "aws-data-transfer-terminal",
    "aws-mainframe-modernization"
}

def clean_description(desc: str) -> str:
    # 1. Boilerplate pattern: "...vive en la familia de ... cubre un hueco real: <TEXT> Identifícalo en un segundo..."
    m = re.search(r'cubre un hueco real:\s*(.*?)(?:\s*Identif[íi]calo en un segundo.*)?$', desc, re.IGNORECASE)
    if m:
        text = m.group(1).strip()
        # Clean trailing punctuation and capitalize
        text = text.rstrip(".;")
        return text[0].upper() + text[1:] + "."
    
    # 2. Boilerplate pattern: "Si en el examen o en una entrevista te piden..."
    # Keep the hook and remove the pricing disclaimer at the end
    desc = re.sub(r'\s*La ficha de pricing oficial cubre n[úu]meros finos;.*$', '', desc).strip()
    return desc

def sanitize_catalog():
    with open(INPUT_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)

    raw_results = data.get("results", [])
    sanitized_services = []

    for s in raw_results:
        slug = s.get("slug")
        if slug in JUNK_SLUGS:
            continue

        # 1. Clean Description
        orig_desc = s["details"]["description"]
        s["details"]["description"] = clean_description(orig_desc)

        # 2. URLs: remove forced '/es/' locale
        if "aws_doc_url" in s["details"]:
            s["details"]["aws_doc_url"] = s["details"]["aws_doc_url"].replace("https://aws.amazon.com/es/", "https://aws.amazon.com/")

        # 3. Scope: EBS is Zonal
        if slug == "amazon-elastic-block-store":
            s["details"]["scope"] = "zonal"

        # 4. Business Logic & Pricing Enums
        p_model = s["details"]["pricing"].get("model")
        if slug in STRICT_PAID_SERVICES:
            s["details"]["free_tier"] = False
            s["details"]["pricing"]["free_tier_type"] = "none"
        elif p_model == "free":
            s["details"]["free_tier"] = True
            s["details"]["pricing"]["free_tier_type"] = "always_free"
        elif not s["details"].get("free_tier", False):
            s["details"]["pricing"]["free_tier_type"] = "none"
        elif s["details"]["pricing"].get("free_tier_type") == "none":
            s["details"]["free_tier"] = False

        # 5. Fix Broken Synergies (amazon-s3 -> amazon-simple-storage-service)
        synergies = s.get("learning", {}).get("architectural_synergies", [])
        for syn in synergies:
            if syn.get("service_slug") == "amazon-s3":
                syn["service_slug"] = "amazon-simple-storage-service"

        # 6. Remove Retired Certifications (DBS-C01)
        certs = s.get("learning", {}).get("certifications", [])
        s["learning"]["certifications"] = [c for c in certs if c != "DBS-C01"]
        
        cert_details = s.get("learning", {}).get("certification_details", [])
        s["learning"]["certification_details"] = [cd for cd in cert_details if cd.get("code") != "DBS-C01"]

        # 7. Clean Boilerplate Exam Tips
        tips = s.get("learning", {}).get("exam_tips", [])
        has_boilerplate = any("no por el logo" in t or "latencia, HA y cargos de transferencia" in t for t in tips)
        if has_boilerplate:
            scope_val = s["details"]["scope"].capitalize()
            use_case_sample = s["details"]["use_cases"][0] if s["details"]["use_cases"] else "cargas de trabajo cloud"
            s["learning"]["exam_tips"] = [
                f"{s['name']} opera con alcance {scope_val}. Evalúalo para arquitecturas que requieran alta disponibilidad y baja latencia.",
                f"Escenario de examen: Solución administrada para {use_case_sample.lower()}."
            ]

        sanitized_services.append(s)

    # 8. Re-index IDs contiguously (aws-srv-001 to aws-srv-N)
    for idx, s in enumerate(sanitized_services, start=1):
        s["id"] = f"aws-srv-{idx:03d}"

    # 9. Update Categories & Envelope Metadata
    distinct_categories = set(s["category"]["id"] for s in sanitized_services)
    now_iso = datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")

    data["info"]["total_services"] = len(sanitized_services)
    data["info"]["total_categories"] = len(distinct_categories)
    data["info"]["updated_at"] = now_iso
    data["info"]["enrichment_provider"] = "aws-catalog-pipeline"
    
    data["pagination"]["total"] = len(sanitized_services)
    data["pagination"]["limit"] = len(sanitized_services)
    data["pagination"]["page"] = 1
    data["pagination"]["pages"] = 1

    data["results"] = sanitized_services

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print(f"Sanitization complete! Total valid services: {len(sanitized_services)}, Categories: {len(distinct_categories)}")
    print(f"Output saved to: {OUTPUT_FILE}")

if __name__ == "__main__":
    sanitize_catalog()
