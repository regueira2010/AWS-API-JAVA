import json
import re

TARGET_FILE = "src/main/resources/aws_api_sanitized.json"

# Curated documentation links for major AWS services
DOCS_MAP = {
    "amazon-ec2": "https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/",
    "amazon-simple-storage-service": "https://docs.aws.amazon.com/AmazonS3/latest/userguide/",
    "aws-lambda": "https://docs.aws.amazon.com/lambda/latest/dg/",
    "amazon-dynamodb": "https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/",
    "amazon-rds": "https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/",
    "amazon-aurora": "https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/",
    "amazon-efs": "https://docs.aws.amazon.com/efs/latest/ug/",
    "amazon-elastic-block-store": "https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AmazonEBS.html",
    "amazon-cloudfront": "https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/",
    "amazon-route-53": "https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/",
    "amazon-virtual-private-cloud": "https://docs.aws.amazon.com/vpc/latest/userguide/",
    "aws-identity-and-access-management": "https://docs.aws.amazon.com/IAM/latest/UserGuide/",
    "elastic-load-balancing": "https://docs.aws.amazon.com/elasticloadbalancing/latest/userguide/",
    "aws-cloudformation": "https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/",
    "amazon-cloudwatch": "https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/",
    "aws-cloudtrail": "https://docs.aws.amazon.com/awscloudtrail/latest/userguide/",
    "aws-step-functions": "https://docs.aws.amazon.com/step-functions/latest/dg/",
    "amazon-sqs": "https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/",
    "amazon-sns": "https://docs.aws.amazon.com/sns/latest/dg/",
    "amazon-api-gateway": "https://docs.aws.amazon.com/apigateway/latest/developerguide/",
    "aws-elastic-beanstalk": "https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/",
    "amazon-elastic-container-service": "https://docs.aws.amazon.com/AmazonECS/latest/developerguide/",
    "amazon-elastic-kubernetes-service": "https://docs.aws.amazon.com/eks/latest/userguide/",
    "aws-fargate": "https://docs.aws.amazon.com/AmazonECS/latest/userguide/what-is-fargate.html",
    "aws-waf": "https://docs.aws.amazon.com/waf/latest/developerguide/",
    "aws-shield": "https://docs.aws.amazon.com/waf/latest/developerguide/shield-chapter.html",
    "aws-key-management-service": "https://docs.aws.amazon.com/kms/latest/developerguide/",
    "aws-secrets-manager": "https://docs.aws.amazon.com/secretsmanager/latest/userguide/"
}

def extract_short_summary(desc: str) -> str:
    # If it contains "Si en el examen..." pattern, extract the formal definition sentence
    if "Si en el examen" in desc:
        sentences = re.split(r'(?<=[.!?])\s+', desc)
        for s in sentences[1:]:
            s_clean = s.strip()
            if s_clean.startswith("Servicio") or s_clean.startswith("Base de datos") or s_clean.startswith("Sistema"):
                return s_clean.rstrip(".") + "."
        if len(sentences) > 1:
            return sentences[1].strip().rstrip(".") + "."
            
    # Standard sentence extraction
    sentences = re.split(r'(?<=[.!?])\s+', desc)
    first = sentences[0].strip().rstrip(".") + "."
    if len(first) > 135:
        first = first[:132] + "..."
    return first

def run_phase1():
    with open(TARGET_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)

    for s in data["results"]:
        slug = s["slug"]
        
        # 1. Standardize Scope to Regional or Global only
        if s["details"].get("scope") not in ["regional", "global"]:
            s["details"]["scope"] = "regional"
        if slug == "amazon-elastic-block-store":
            s["details"]["scope"] = "regional"

        # 2. Extract Service Path from existing URLs
        current_url = s["details"].get("aws_doc_url", "")
        m = re.search(r'https://aws\.amazon\.com/(?:es/)?([^/]+)/', current_url)
        if m:
            service_path = m.group(1)
        else:
            service_path = slug.replace("amazon-", "").replace("aws-", "")

        # 3. Create the 3 official URLs
        service_url = f"https://aws.amazon.com/{service_path}/"
        pricing_url = f"https://aws.amazon.com/{service_path}/pricing/"
        documentation_url = DOCS_MAP.get(slug, f"https://docs.aws.amazon.com/{service_path}/")

        s["details"]["service_url"] = service_url
        s["details"]["documentation_url"] = documentation_url
        s["details"]["pricing_url"] = pricing_url
        s["details"]["aws_doc_url"] = documentation_url  # keep for backwards compatibility

        # 4. Extract short_summary for frontend card view
        s["details"]["short_summary"] = extract_short_summary(s["details"]["description"])

    with open(TARGET_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print("Phase 1 executed successfully on aws_api_sanitized.json!")

if __name__ == "__main__":
    run_phase1()
