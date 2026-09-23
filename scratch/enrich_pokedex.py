import json
import re

INPUT_FILE = "src/main/resources/aws_api_sanitized.json"
OUTPUT_FILE = "src/main/resources/aws_api_sanitized.json"

# Known docs mapping for official AWS documentation URL prefixes
DOCS_MAP = {
    "amazon-ec2": "https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/",
    "amazon-s3": "https://docs.aws.amazon.com/AmazonS3/latest/userguide/",
    "amazon-simple-storage-service": "https://docs.aws.amazon.com/AmazonS3/latest/userguide/",
    "amazon-lambda": "https://docs.aws.amazon.com/lambda/latest/dg/",
    "aws-lambda": "https://docs.aws.amazon.com/lambda/latest/dg/",
    "amazon-dynamodb": "https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/",
    "amazon-rds": "https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/",
    "amazon-efs": "https://docs.aws.amazon.com/efs/latest/ug/",
    "amazon-elastic-block-store": "https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AmazonEBS.html",
    "amazon-cloudfront": "https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/",
    "amazon-route-53": "https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/",
    "amazon-virtual-private-cloud": "https://docs.aws.amazon.com/vpc/latest/userguide/",
    "aws-identity-and-access-management": "https://docs.aws.amazon.com/IAM/latest/UserGuide/",
    "elastic-load-balancing": "https://docs.aws.amazon.com/elasticloadbalancing/latest/userguide/",
    "aws-cloudformation": "https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/",
    "amazon-cloudwatch": "https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/",
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
    "aws-kms": "https://docs.aws.amazon.com/kms/latest/developerguide/",
    "aws-key-management-service": "https://docs.aws.amazon.com/kms/latest/developerguide/",
    "aws-secrets-manager": "https://docs.aws.amazon.com/secretsmanager/latest/userguide/"
}

# Real core exam tips for critical certification services (CLF-C02 & SAA-C03)
CORE_EXAM_TIPS = {
    "amazon-efs": [
        "EFS es NFS elástico para Linux (multi-AZ); no lo confundas con EBS (bloque, 1 AZ) ni FSx (Windows/Lustre/ONTAP).",
        "Modos: General Purpose vs Max I/O; ciclo de vida con Infrequent Access (EFS IA) para reducir costos hasta un 92%."
    ],
    "amazon-elastic-block-store": [
        "EBS vive en UNA SOLA AZ. Para mover datos a otra AZ o región, toma un snapshot (se guarda en S3) y restaura.",
        "gp3 es el estándar balanceado; io2 Block Express es para latencia de submilisegundo e IOPS extremos (bases de datos mission-critical)."
    ],
    "amazon-simple-storage-service": [
        "S3 es Object Storage con 11 nueves (99.999999999%) de durabilidad. El namespace del bucket es global, los datos residen en una región.",
        "Memoriza clases: Standard, Intelligent-Tiering (auto-ahorro), Standard-IA, One Zone-IA y Glacier (Instant/Flexible/Deep Archive)."
    ],
    "amazon-dynamodb": [
        "Base de datos NoSQL clave-valor y documentos con latencia de un dígito de milisegundo. Capacidad On-Demand vs Provisioned.",
        "Global Tables = multi-región activo-activo. DAX (DynamoDB Accelerator) es caché en memoria exclusiva para lecturas ultra rápidas."
    ],
    "amazon-rds": [
        "Multi-AZ = alta disponibilidad síncrona con failover automático (no mejora lecturas). Read Replicas = escalabilidad asíncrona de lectura.",
        "Aurora multiplica el rendimiento por 5 (MySQL) o por 3 (Postgres), con almacenamiento compartido replicado en 6 copias a través de 3 AZs."
    ],
    "amazon-ec2": [
        "Modelos de compra: On-Demand (cargas cortas/impredecibles), Savings Plans / Reserved (1-3 años con descuento), Spot (hasta 90% descuento, interrumpible con aviso de 2 min).",
        "User Data corre como root solo en el primer arranque; Auto Scaling Groups (ASG) mantienen la capacidad deseada según métricas de CloudWatch."
    ],
    "aws-lambda": [
        "Serverless Compute: timeout máximo de 15 minutos; memoria de 128 MB a 10 GB (la CPU escala proporcional a la memoria asignada).",
        "Ideal para eventos efímeros (S3 triggers, DynamoDB Streams, API Gateway). Para procesos de más de 15 min usa ECS Fargate o AWS Batch."
    ],
    "amazon-cloudfront": [
        "CDN global con cientos de Edge Locations. Protege orígenes (S3/ALB) mediante Origin Access Control (OAC).",
        "Reduce latencia de estáticos/dinámicos y mitiga ataques DDoS integrado con AWS Shield y AWS WAF en el borde."
    ],
    "amazon-route-53": [
        "Servicio DNS administrado altamente disponible. Soporta políticas: Simple, Weighted, Latency-based, Failover (con health checks), Geolocation y Multivalue.",
        "Los registros Alias apuntan directamente a recursos de AWS (CloudFront, ALB, S3 websites) y las consultas a Alias internos de AWS son gratuitas."
    ],
    "amazon-virtual-private-cloud": [
        "VPC es tu red virtual aislada. Security Groups son stateful a nivel de interfaz de red (ENI); Network ACLs (NACL) son stateless a nivel de subnet.",
        "NAT Gateway permite salida a internet a subnets privadas en IPv4; VPC Endpoints (Interface y Gateway para S3/DynamoDB) evitan usar internet público."
    ],
    "elastic-load-balancing": [
        "ALB = Capa 7 (HTTP/HTTPS/gRPC, path routing, headers, WebSockets). NLB = Capa 4 (TCP/UDP/TLS, IPs estáticas elásticas, latencia ultrabaja).",
        "Gateway Load Balancer (GWLB) = appliances de seguridad y firewalls virtuales. ALB se integra nativamente con AWS WAF."
    ],
    "aws-identity-and-access-management": [
        "IAM es global y gratuito. Roles para otorgar permisos temporales a servicios o usuarios federados sin compartir credenciales estáticas.",
        "Evaluación de políticas: un 'Deny' explícito siempre anula cualquier 'Allow'. Aplica el principio de mínimo privilegio (Least Privilege)."
    ],
    "aws-waf": [
        "Firewall de aplicaciones web (Capa 7): protege contra inyecciones SQL (SQLi), Cross-Site Scripting (XSS) y ataques volumétricos L7.",
        "Se asocia directamente a CloudFront, Application Load Balancer (ALB), Amazon API Gateway y AWS AppSync."
    ],
    "aws-cloudformation": [
        "Infraestructura como Código (IaC) declarativa (YAML/JSON). Soporta Drift Detection para auditar cambios manuales fuera de plantilla.",
        "StackSets permite desplegar recursos a través de múltiples cuentas de AWS y regiones con un solo comando."
    ],
    "aws-key-management-service": [
        "Servicio administrado para crear y controlar claves criptográficas (KMS Keys). Integrado nativamente con casi todos los servicios de AWS.",
        "Las claves KMS nunca salen de los módulos de seguridad de hardware (HSM) de AWS; se audita todo uso en AWS CloudTrail."
    ],
    "aws-secrets-manager": [
        "Almacena y rota automáticamente credenciales de bases de datos (RDS, Redshift) mediante funciones Lambda nativas.",
        "A diferencia de Parameter Store, Secrets Manager incluye rotación automática out-of-the-box e integración directa con RDS."
    ],
    "amazon-api-gateway": [
        "Servicio totalmente administrado para crear, publicar y proteger APIs REST, HTTP y WebSocket a cualquier escala.",
        "Soporta throttling (rate limiting), caching de respuestas, autorización con Cognito o Lambda Authorizers y validación de payloads."
    ],
    "aws-step-functions": [
        "Orquestador serverless visual basado en máquinas de estados para coordinar múltiples servicios de AWS y flujos de negocio complejos.",
        "Standard Workflows para flujos largos (hasta 1 año) con semántica exactly-once; Express Workflows para eventos de alto volumen y corta duración."
    ],
    "amazon-sqs": [
        "Cola de mensajes totalmente administrada para desacoplar microservicios. Standard Queue = rendimiento casi ilimitado, entrega al menos una vez (at-least-once).",
        "FIFO Queue = orden estricto (First-In-First-Out) y entrega exactamente una vez (exactly-once), con límite de transacciones por segundo."
    ],
    "amazon-sns": [
        "Servicio de mensajería Pub/Sub (Publicador/Suscriptor) para distribución masiva de mensajes a múltiples suscriptores (fan-out).",
        "Envía notificaciones a SQS, funciones Lambda, endpoints HTTP y dispositivos móviles (SMS, push)."
    ]
}

def build_pokedex_catalog():
    with open(INPUT_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)

    services = data.get("results", [])

    for s in services:
        slug = s["slug"]
        name = s["name"]
        cat_name = s["category"]["name"]
        scope = s["details"]["scope"]
        free_tier = s["details"]["free_tier"]
        free_tier_type = s["details"]["pricing"]["free_tier_type"]

        # 1. Separar URLs: Technical Documentation vs Pricing
        clean_slug = slug.replace("amazon-", "").replace("aws-", "")
        doc_url = DOCS_MAP.get(slug, f"https://docs.aws.amazon.com/{clean_slug}/")
        pricing_url = f"https://aws.amazon.com/{clean_slug}/pricing/"

        s["details"]["documentation_url"] = doc_url
        s["details"]["pricing_url"] = pricing_url

        # 2. Resumen "Hook" de la tarjeta (Headline para el Card View de la Pokédex)
        full_desc = s["details"]["description"]
        # Extraer primera oración corta
        first_sentence = full_desc.split(".")[0].strip()
        if len(first_sentence) > 130:
            first_sentence = first_sentence[:127] + "..."
        s["details"]["short_summary"] = first_sentence + "."

        # 3. Badges coleccionables para el Frontend (Visual Chips)
        badges = []
        badges.append(scope.capitalize()) # "Regional", "Global", "Zonal"
        if free_tier:
            if free_tier_type == "always_free":
                badges.append("Always Free")
            elif free_tier_type == "free_trial":
                badges.append("Free Trial")
            elif free_tier_type == "twelve_months_free":
                badges.append("12 Meses Gratis")
            else:
                badges.append("Free Tier")
        else:
            badges.append("On-Demand")
        
        badges.append(cat_name)
        s["details"]["badges"] = badges

        # 4. Inyección de Exam Tips Reales (Cheat Sheet Pokédex)
        if slug in CORE_EXAM_TIPS:
            s["learning"]["exam_tips"] = CORE_EXAM_TIPS[slug]
        else:
            # Plantilla pedagógica oficial para el resto de servicios
            use_case = s["details"]["use_cases"][0] if s["details"]["use_cases"] else "soluciones en la nube"
            s["learning"]["exam_tips"] = [
                f"{name} tiene alcance {scope.upper()}. En preguntas de arquitectura, elígelo cuando busques una solución administrada para: {use_case.lower()}.",
                f"Sinergia de examen: Combínalo con CloudWatch para observabilidad y con IAM aplicando Least Privilege sobre sus recursos."
            ]

    # Actualizar info
    data["info"]["name"] = "AWS Services Pokédex API"
    data["info"]["version"] = "2.0.0"
    data["info"]["contract_version"] = "2.0.0"

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print("AWS Pokédex enrichment complete! 0 tokens spent.")

if __name__ == "__main__":
    build_pokedex_catalog()
