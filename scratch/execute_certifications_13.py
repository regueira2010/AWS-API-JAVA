import json

TARGET_FILE = "src/main/resources/aws_api_sanitized.json"

OFFICIAL_13_CERTS = {
    "AIB-C01": "AWS Certified AI Business Strategist",
    "CLF-C02": "AWS Certified Cloud Practitioner",
    "AIF-C01": "AWS Certified AI Practitioner",
    "SAA-C03": "AWS Certified Solutions Architect - Associate",
    "MLA-C01": "AWS Certified Machine Learning Engineer - Associate",
    "DEA-C01": "AWS Certified Data Engineer - Associate",
    "DVA-C02": "AWS Certified Developer - Associate",
    "SOA-C02": "AWS Certified CloudOps Engineer - Associate",
    "AGD-C01": "AWS Certified Generative AI Developer - Professional",
    "DOP-C02": "AWS Certified DevOps Engineer - Professional",
    "SAP-C02": "AWS Certified Solutions Architect - Professional",
    "ANS-C01": "AWS Certified Advanced Networking - Specialty",
    "SCS-C02": "AWS Certified Security - Specialty"
}

# Targeted service slugs for the newly added certifications
AIB_SLUGS = {
    "amazon-bedrock", "amazon-sagemaker", "amazon-comprehend", "amazon-textract",
    "amazon-rekognition", "amazon-kendra", "amazon-transcribe", "amazon-fraud-detector",
    "amazon-q", "amazon-q-business", "amazon-q-developer", "aws-healthscribe"
}

AGD_SLUGS = {
    "amazon-bedrock", "amazon-q-developer", "amazon-q-business", "amazon-q",
    "amazon-sagemaker", "amazon-opensearch-service", "aws-lambda", "amazon-api-gateway",
    "amazon-kendra"
}

DOP_SLUGS = {
    "aws-cloudformation", "amazon-cloudwatch", "aws-systems-manager", "aws-elastic-beanstalk",
    "aws-config", "amazon-eventbridge", "aws-service-catalog", "aws-lambda",
    "amazon-elastic-container-service", "amazon-elastic-kubernetes-service", "aws-x-ray",
    "amazon-ecr", "amazon-elastic-container-registry", "aws-app-runner"
}

SAP_SLUGS = {
    "amazon-ec2", "amazon-simple-storage-service", "amazon-rds", "amazon-aurora",
    "amazon-dynamodb", "amazon-route-53", "amazon-cloudfront", "aws-transit-gateway",
    "aws-direct-connect", "aws-organizations", "aws-control-tower", "elastic-load-balancing",
    "aws-shield", "aws-waf", "aws-key-management-service", "aws-elastic-disaster-recovery",
    "aws-database-migration-service", "aws-storage-gateway", "amazon-efs", "amazon-fsx",
    "amazon-fsx-for-netapp-ontap", "amazon-fsx-for-wfs", "amazon-elastic-block-store",
    "amazon-virtual-private-cloud", "aws-global-accelerator"
}

def apply_13_certifications():
    with open(TARGET_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)

    for s in data["results"]:
        slug = s["slug"]
        certs = set(s.get("learning", {}).get("certifications", []))

        # 1. Add specific new certifications
        if slug in AIB_SLUGS:
            certs.add("AIB-C01")
        if slug in AGD_SLUGS:
            certs.add("AGD-C01")
        if slug in DOP_SLUGS:
            certs.add("DOP-C02")
        if slug in SAP_SLUGS:
            certs.add("SAP-C02")

        # 2. Filter only official 13 codes (removes any legacy or retired codes like DBS-C01)
        valid_certs = [c for c in sorted(certs) if c in OFFICIAL_13_CERTS]

        # 3. Build strictly synchronized certification_details
        cert_details = [{"code": code, "name": OFFICIAL_13_CERTS[code]} for code in valid_certs]

        s["learning"]["certifications"] = valid_certs
        s["learning"]["certification_details"] = cert_details

    # Update metadata
    data["info"]["version"] = "2.3.0"
    data["info"]["contract_version"] = "2.3.0"

    with open(TARGET_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print("Successfully mapped all 13 official AWS certifications across the catalog!")

if __name__ == "__main__":
    apply_13_certifications()
