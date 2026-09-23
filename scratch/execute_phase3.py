import json

TARGET_FILE = "src/main/resources/aws_api_sanitized.json"

# CLI namespace overrides where default slug stripping doesn't match official AWS CLI command
CLI_NAMESPACE_OVERRIDES = {
    "amazon-simple-storage-service": "s3",
    "amazon-simple-storage-service-glacier": "glacier",
    "amazon-elastic-block-store": "ebs",
    "amazon-virtual-private-cloud": "ec2",
    "elastic-load-balancing": "elbv2",
    "aws-identity-and-access-management": "iam",
    "aws-key-management-service": "kms",
    "aws-systems-manager": "ssm",
    "aws-secrets-manager": "secretsmanager",
    "aws-step-functions": "stepfunctions",
    "amazon-eventbridge": "events",
    "aws-waf": "wafv2",
    "amazon-api-gateway": "apigateway",
    "amazon-elastic-container-service": "ecs",
    "amazon-elastic-kubernetes-service": "eks",
    "amazon-elastic-container-registry": "ecr",
    "amazon-cognito": "cognito-idp",
    "amazon-opensearch-service": "opensearch",
    "aws-database-migration-service": "dms",
    "aws-elastic-disaster-recovery": "drs",
    "aws-application-migration-service": "mgn"
}

# Core Serverless services recognized in AWS architecture
SERVERLESS_SLUGS = {
    "aws-lambda", "amazon-simple-storage-service", "amazon-simple-storage-service-glacier",
    "amazon-dynamodb", "amazon-sqs", "amazon-sns", "amazon-eventbridge", "aws-step-functions",
    "amazon-api-gateway", "aws-appsync", "amazon-athena", "amazon-bedrock", "aws-fargate",
    "aws-glue", "amazon-kinesis-data-firehose", "amazon-cognito", "amazon-simple-email-service",
    "aws-aurora-serverless", "amazon-timestream"
}

# IaaS services
IAAS_SLUGS = {
    "amazon-ec2", "aws-lightsail"
}

# Core Cloud Practitioner (Foundational) syllabus services
FOUNDATIONAL_CORE_SLUGS = {
    "amazon-ec2", "amazon-simple-storage-service", "amazon-elastic-block-store", "amazon-efs",
    "amazon-rds", "amazon-aurora", "amazon-dynamodb", "aws-lambda", "amazon-virtual-private-cloud",
    "amazon-route-53", "amazon-cloudfront", "elastic-load-balancing", "aws-identity-and-access-management",
    "aws-cloudformation", "amazon-cloudwatch", "aws-cloudtrail", "aws-trusted-advisor",
    "aws-organizations", "aws-pricing-calculator", "aws-budgets", "aws-cost-explorer",
    "aws-shield", "aws-waf", "aws-key-management-service", "amazon-sqs", "amazon-sns",
    "aws-elastic-beanstalk", "amazon-elastic-container-service", "amazon-elastic-kubernetes-service",
    "aws-fargate", "aws-support", "aws-health-dashboard", "aws-direct-connect", "aws-storage-gateway",
    "aws-snowball"
}

# Specialty / Advanced categories
SPECIALTY_CATEGORIES = {
    "cat-satelite", "cat-tecnologias-cuanticas", "cat-blockchain", "cat-servicios-multimedia",
    "cat-videojuegos", "cat-internet-de-las-cosas-iot"
}

def get_cli_namespace(slug: str) -> str:
    if slug in CLI_NAMESPACE_OVERRIDES:
        return CLI_NAMESPACE_OVERRIDES[slug]
    clean = slug.replace("amazon-", "").replace("aws-", "")
    return clean

def get_deployment_model(slug: str) -> str:
    if slug in SERVERLESS_SLUGS or "serverless" in slug:
        return "serverless"
    if slug in IAAS_SLUGS:
        return "iaas"
    return "managed"

def get_certification_level(slug: str, cat_id: str) -> str:
    if slug in FOUNDATIONAL_CORE_SLUGS:
        return "foundational"
    if cat_id in SPECIALTY_CATEGORIES or "specialty" in slug:
        return "specialty"
    return "associate"

def run_phase3():
    with open(TARGET_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)

    for s in data["results"]:
        slug = s["slug"]
        cat_id = s["category"]["id"]

        cli_ns = get_cli_namespace(slug)
        dep_model = get_deployment_model(slug)
        cert_level = get_certification_level(slug, cat_id)

        # In details: add architectural classifications
        s["details"]["cli_namespace"] = cli_ns
        s["details"]["deployment_model"] = dep_model

        # In learning: add formal certification level
        s["learning"]["primary_certification_level"] = cert_level

    # Update metadata
    data["info"]["version"] = "2.2.0"
    data["info"]["contract_version"] = "2.2.0"

    with open(TARGET_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print("Phase 3 executed successfully! 3 high-value architectural fields added.")

if __name__ == "__main__":
    run_phase3()
