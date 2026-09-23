import json

TARGET_FILE = "src/main/resources/aws_api_sanitized.json"

# Master Dictionary of Official Exam Tips (AWS Well-Architected & Official Exam Guides)
EXAM_TIPS_DATABASE = {
    # STORAGE
    "amazon-efs": [
        "EFS es NFSv4 elástico para Linux (multi-AZ); no lo confundas con EBS (bloque, 1 AZ) ni FSx (Windows/Lustre/ONTAP).",
        "Modos: General Purpose vs Max I/O; política de ciclo de vida con Infrequent Access (EFS IA) para reducir costos hasta un 92%."
    ],
    "amazon-elastic-block-store": [
        "EBS vive en una sola AZ. Para mover datos a otra AZ o región, toma un snapshot (se guarda incrementalmente en S3) y restaura en la AZ destino.",
        "gp3 es el estándar costo-eficiente; io2 Block Express es para latencia de submilisegundo e IOPS extremos (bases de datos OLTP mission-critical)."
    ],
    "amazon-simple-storage-service": [
        "S3 es Object Storage con 11 nueves (99.999999999%) de durabilidad. El namespace del bucket es global, los datos residen en una región específica.",
        "Memoriza clases: Standard, Intelligent-Tiering (auto-ahorro sin penalización), Standard-IA, One Zone-IA y Glacier (Instant/Flexible/Deep Archive)."
    ],
    "amazon-simple-storage-service-glacier": [
        "Glacier Flexible Retrieval ofrece 3 opciones de recuperación: Expedited (1-5 min), Standard (3-5 horas) y Bulk (5-12 horas, gratuita).",
        "Glacier Deep Archive es la opción de almacenamiento cloud de menor costo (retención legal/compliance con recuperación Standard en 12h o Bulk en 48h)."
    ],
    "amazon-fsx-for-wfs": [
        "Sistemas de archivos Windows nativo (SMB/CIFS) con integración nativa a Microsoft Active Directory, soporte de DFS y permisos NTFS.",
        "Multi-AZ con sincronización automática para failover transparente en aplicaciones corporativas Windows."
    ],
    "amazon-fsx-for-lustre": [
        "Sistema de archivos POSIX de ultra alto rendimiento (submilisegundo, cientos de GB/s) optimizado para HPC, Machine Learning y Big Data.",
        "Integración directa con S3: lee objetos como archivos y escribe resultados de vuelta al bucket S3 de forma transparente."
    ],
    "amazon-fsx-for-netapp-ontap": [
        "Almacenamiento empresarial multiprotocolo (NFS, SMB e iSCSI) que ofrece deduplicación, compresión y snapshots instantáneos.",
        "Permite extender o migrar cargas de trabajo locales existentes de NetApp a AWS sin reconfigurar aplicaciones."
    ],
    "aws-storage-gateway": [
        "S3 File Gateway (SMB/NFS para objetos S3), Volume Gateway (iSCSI con volúmenes en caché o almacenados) y Tape Gateway (VTL para backups).",
        "Conecta entornos on-premises con almacenamiento de AWS usando caché local de baja latencia."
    ],
    "aws-backup": [
        "Servicio centralizado y basado en políticas para automatizar respaldos en EC2, EBS, RDS, DynamoDB, EFS, FSx y S3.",
        "AWS Backup Vault Lock habilita WORM (Write Once, Read Many) para impedir la modificación o borrado prematuro de copias de seguridad."
    ],
    "aws-snowball": [
        "Dispositivo físico de transporte masivo de datos para transferir decenas o cientos de terabytes cuando la conexión de red es lenta o costosa.",
        "Snowball Edge Compute Optimized incluye capacidad de cómputo EC2 local y almacenamiento de objetos S3 para procesamiento en el borde."
    ],
    "aws-elastic-disaster-recovery": [
        "Recuperación ante desastres (DR) mediante replicación continua a nivel de bloque desde entornos locales o cloud hacia AWS.",
        "Minimiza el RPO (segundos) y RTO (minutos) manteniendo una réplica económica de bajo costo en AWS que solo arranca instancias completas ante un desastre."
    ],

    # COMPUTE
    "amazon-ec2": [
        "Opciones de compra: On-Demand (cargas cortas/impredecibles), Savings Plans / Reserved Instances (1-3 años con descuento), Spot (hasta 90% ahorro, interrumpible con aviso de 2 min).",
        "Auto Scaling Groups (ASG) mantienen la capacidad deseada según métricas de CloudWatch; User Data corre como root una sola vez al primer inicio."
    ],
    "aws-lambda": [
        "Serverless Compute con timeout máximo de 15 minutos; memoria configurable de 128 MB a 10 GB (la CPU escala proporcional a la memoria).",
        "Ejecución disparada por eventos (S3, DynamoDB Streams, API Gateway, SQS). Para procesos de más de 15 minutos, elige ECS Fargate, Step Functions o AWS Batch."
    ],
    "aws-elastic-beanstalk": [
        "Plataforma como Servicio (PaaS) para desplegar aplicaciones web rápidamente gestionando automáticamente aprovisionamiento, balanceo, escalado y salud.",
        "Soporta Blue/Green Deployments cambiando la URL del entorno en la consola de Beanstalk para evitar downtime."
    ],
    "aws-batch": [
        "Planificador de trabajos por lotes a cualquier escala que aprovisiona automáticamente recursos de cómputo (EC2 o Fargate) según los requisitos del job.",
        "Soporta instancias Spot con estrategias de asignación optimizadas para maximizar ahorro sin perder ejecución de colas prioritarias."
    ],
    "amazon-elastic-container-service": [
        "Orquestador nativo de contenedores Docker de AWS. Launch types: EC2 (control total de VMs) o Fargate (serverless sin gestionar servidores).",
        "Define Task Definitions con roles IAM específicos por tarea (Task Role) para otorgar permisos mínimos a los contenedores."
    ],
    "amazon-elastic-kubernetes-service": [
        "Servicio gestionado de Kubernetes que ejecuta el plano de control (Control Plane) a través de múltiples AZs para alta disponibilidad.",
        "Soporta Managed Node Groups (EC2) y Fargate Profiles para ejecutar pods serverless sin gestionar workers."
    ],
    "aws-fargate": [
        "Motor de cómputo serverless para contenedores compatible con Amazon ECS y Amazon EKS. Paga únicamente por vCPU y memoria consumidos por tarea/pod.",
        "Elimina la necesidad de elegir tipos de instancias EC2, gestionar clusters, parchear sistemas operativos o escalar servidores."
    ],
    "amazon-elastic-container-registry": [
        "Registro de contenedores Docker/OCI privado y seguro integrado con IAM y cifrado con KMS.",
        "Soporta escaneo automático de vulnerabilidades en imágenes (Basic con Clair o Enhanced con Amazon Inspector) y políticas de ciclo de vida."
    ],
    "aws-app-runner": [
        "Servicio completamente administrado para desplegar aplicaciones web y APIs directamente desde código fuente o imágenes de contenedor en minutos.",
        "Gestiona automáticamente balanceo de carga, certificados TLS, escalado y métricas sin requerir conocimientos de orquestación de contenedores."
    ],
    "aws-lightsail": [
        "VPS simplificado con tarifa mensual predecible fija que incluye VM, almacenamiento SSD, transferencia de datos, DNS estático e IP pública.",
        "Ideal para sitios web simples (WordPress, blogs), entornos de prueba o prototipos que no requieren la complejidad de arquitecturas multinivel de AWS."
    ],

    # DATABASE
    "amazon-rds": [
        "Multi-AZ = alta disponibilidad síncrona con failover automático ante fallas (no mejora lecturas). Read Replicas = escalabilidad asíncrona de lectura cross-region.",
        "Soporta motores tradicionales (PostgreSQL, MySQL, MariaDB, Oracle, SQL Server) con backups automatizados y ventanas de mantenimiento."
    ],
    "amazon-aurora": [
        "Base de datos relacional compatible con MySQL y PostgreSQL diseñada para la nube con almacenamiento compartido replicado en 6 copias a través de 3 AZs.",
        "Aurora Serverless v2 escala de forma instantánea y granular en fracciones de segundo; Aurora Global Database ofrece latencia de lectura cross-region < 1 segundo."
    ],
    "amazon-dynamodb": [
        "Base de datos NoSQL clave-valor y documentos con latencia de un dígito de milisegundo. Capacidad On-Demand (cargas variables) vs Provisioned con Auto Scaling.",
        "Global Tables = multi-región activo-activo. DAX (DynamoDB Accelerator) es una caché en memoria dedicada que reduce latencias de lectura de milisegundos a microsegundos."
    ],
    "amazon-elasticache": [
        "Caché en memoria totalmente administrada: Redis (soporta estructuras de datos complejas, clustering, réplicas Multi-AZ y persistencia) vs Memcached (multihilo puro para objetos simples).",
        "Alivia la carga de lectura de bases de datos relacionales (RDS) almacenando en caché resultados de consultas frecuentes (patrón Cache-Aside)."
    ],
    "amazon-redshift": [
        "Data Warehouse analítico (OLAP) con almacenamiento columnar y arquitectura de procesamiento masivo en paralelo (MPP) para petabytes de datos.",
        "Redshift Serverless escala automáticamente la capacidad de cómputo; Redshift Spectrum permite ejecutar queries SQL directamente sobre datos en S3 sin cargarlos."
    ],
    "amazon-neptune": [
        "Base de datos de grafos rápida y administrada optimizada para almacenar y navegar relaciones complejas (redes sociales, detección de fraudes, grafos de conocimiento).",
        "Soporta estándares de la industria como Apache TinkerPop (Gremlin) y W3C (RDF/SPARQL)."
    ],
    "amazon-documentdb": [
        "Base de datos de documentos JSON totalmente administrada y compatible con cargas de trabajo de MongoDB.",
        "Arquitectura desacoplada donde el cómputo y el almacenamiento de 6 copias en 3 AZs escalan de forma independiente."
    ],
    "amazon-keyspaces": [
        "Servicio de base de datos serverless compatible con Apache Cassandra con alta disponibilidad y escalabilidad automática.",
        "Paga solo por las lecturas y escrituras realizadas; no requiere gestionar clusters ni particionar hardware de Cassandra."
    ],
    "amazon-timestream": [
        "Base de datos de series temporales serverless y rápida para IoT, análisis de métricas operativas y datos de telemetría.",
        "Nivel de memoria para ingesta rápida de datos recientes y nivel magnético de bajo costo para almacenamiento histórico a largo plazo."
    ],

    # NETWORKING & CONTENT DELIVERY
    "amazon-virtual-private-cloud": [
        "Tu red virtual aislada en AWS. Security Groups son stateful a nivel de ENI (si permites entrada, la salida regresa automáticamente); NACLs son stateless a nivel de subnet.",
        "NAT Gateway otorga salida IPv4 a internet a subnets privadas; VPC Endpoints (Gateway para S3/DynamoDB e Interface/PrivateLink para el resto) evitan pasar por internet público."
    ],
    "amazon-route-53": [
        "DNS administrado global. Políticas de enrutamiento: Simple, Weighted, Latency, Failover (asociada a Health Checks), Geolocation, Geoproximity y Multivalue Answer.",
        "Los registros Alias apuntan directamente a recursos de AWS (CloudFront, ALB, S3 websites) y las consultas DNS a recursos Alias internos de AWS son gratuitas."
    ],
    "amazon-cloudfront": [
        "CDN global con cientos de Edge Locations que almacena en caché contenido estático y dinámico para reducir la latencia hacia usuarios finales.",
        "Origin Access Control (OAC) asegura que los buckets S3 solo sean accesibles a través de CloudFront, bloqueando el acceso público directo."
    ],
    "elastic-load-balancing": [
        "Application Load Balancer (ALB) opera en Capa 7 (HTTP/HTTPS/gRPC, path routing, headers, host routing). Network Load Balancer (NLB) en Capa 4 (TCP/UDP, latencia ultra baja, IPs elásticas estáticas).",
        "Gateway Load Balancer (GWLB) escala e inspecciona flujos de tráfico a través de appliances y firewalls de terceros. ALB se integra directamente con AWS WAF."
    ],
    "aws-direct-connect": [
        "Conexión de red física y dedicada entre el centro de datos on-premises y AWS, eludiendo por completo la red pública de internet.",
        "Garantiza ancho de banda predecible (1 Gbps a 100 Gbps), latencia constante y costos de transferencia de salida reducidos."
    ],
    "aws-transit-gateway": [
        "Hub central de red (arquitectura Hub-and-Spoke) para interconectar cientos de VPCs y redes locales on-premises a través de una única pasarela.",
        "Simplifica la topología de red evitando mallas de VPC Peering complejas y soporta multicast IP."
    ],
    "aws-privatelink": [
        "Proporciona conectividad privada y segura entre VPCs y servicios de AWS o SaaS de terceros sin exponer el tráfico a la red pública de internet.",
        "Utiliza Network Load Balancers y VPC Interface Endpoints con IPs privadas dentro de tus propias subnets."
    ],
    "aws-global-accelerator": [
        "Utiliza la red troncal privada global de AWS y dos IPs estáticas Anycast globales para enrutar tráfico hacia los endpoints regionales óptimos.",
        "Mejora la disponibilidad y reduce la latencia hasta un 60% para tráfico UDP y TCP sobre conexiones de internet inestables."
    ],

    # SECURITY, IDENTITY & COMPLIANCE
    "aws-identity-and-access-management": [
        "IAM es global y gratuito. Roles para otorgar permisos temporales sin claves fijas a instancias EC2, funciones Lambda o identidades federadas.",
        "Evaluación de políticas: un 'Deny' explícito siempre anula cualquier 'Allow'. Aplica siempre el principio de mínimo privilegio (Least Privilege)."
    ],
    "aws-waf": [
        "Firewall de aplicaciones web (Capa 7) que filtra tráfico malicioso (inyecciones SQL, XSS, rate limiting por IP y bots) en CloudFront, ALB, API Gateway o AppSync.",
        "Utiliza Managed Rule Groups de AWS para protección instantánea contra las amenazas de OWASP Top 10."
    ],
    "aws-shield": [
        "Shield Standard protege automáticamente y sin costo a todos los clientes de AWS contra ataques DDoS comunes en Capa 3 y 4 (SYN floods, UDP reflection).",
        "Shield Advanced ofrece soporte dedicado del equipo Shield Response Team (SRT), mitigación en Capa 7 y protección contra costos imprevistos por picos de DDoS."
    ],
    "aws-key-management-service": [
        "Crea y administra claves criptográficas (KMS Keys) con módulos HSM validados bajo FIPS 140-2/3 para cifrar datos en reposo.",
        "Diferencia claves administradas por AWS (AWS Managed) de claves administradas por el cliente (Customer Managed, con control de rotación y políticas de acceso)."
    ],
    "aws-secrets-manager": [
        "Almacena, rota y recupera de forma segura contraseñas de bases de datos, claves de API y secretos con rotación automática nativa mediante funciones Lambda.",
        "A diferencia de Parameter Store, Secrets Manager incluye rotación automática out-of-the-box e integración estrecha con Amazon RDS."
    ],
    "aws-systems-manager": [
        "Session Manager permite abrir sesiones de terminal en instancias EC2 sin abrir puertos de entrada (no requiere abrir puerto 22 SSH) ni usar bastion hosts.",
        "Parameter Store almacena configuraciones y contraseñas de forma jerárquica con versiones (Standard gratuito o Advanced cifrado con KMS)."
    ],
    "amazon-cognito": [
        "User Pools proporcionan directorio de usuarios, registro, inicio de sesión y autenticación multifactor (MFA); Identity Pools otorgan credenciales temporales de AWS para acceder a recursos.",
        "Soporta federación con proveedores de identidad social (Google, Apple, Facebook) y corporativos mediante SAML 2.0 y OpenID Connect (OIDC)."
    ],
    "aws-security-hub": [
        "Consola unificada de postura de seguridad que audita continuamente el cumplimiento contra estándares (CIS AWS Foundations, PCI-DSS, AWS Foundational Best Practices).",
        "Agrega y prioriza automáticamente hallazgos de seguridad provenientes de GuardDuty, Inspector, Macie, IAM Access Analyzer y AWS Firewall Manager."
    ],
    "amazon-guardduty": [
        "Servicio inteligente de detección de amenazas que analiza de forma continua logs de CloudTrail, VPC Flow Logs, DNS logs, S3 data events y EKS audit logs mediante ML.",
        "No requiere instalar agentes en las instancias EC2 y alerta ante actividades sospechosas como minería de criptomonedas o accesos anómalos."
    ],
    "amazon-inspector": [
        "Servicio automatizado de gestión de vulnerabilidades que escanea continuamente cargas de trabajo de EC2, imágenes de contenedor en ECR y funciones Lambda contra CVEs.",
        "Genera una puntuación de riesgo contextualizada teniendo en cuenta la accesibilidad de red del recurso vulnerado."
    ],
    "amazon-macie": [
        "Servicio de privacidad y seguridad que utiliza Machine Learning para descubrir, clasificar y proteger datos confidenciales (PII, tarjetas de crédito) alojados en buckets S3.",
        "Evalúa las configuraciones de seguridad de todos los buckets S3 de la organización para alertar sobre accesos públicos o falta de cifrado."
    ],

    # MANAGEMENT & GOVERNANCE
    "aws-cloudformation": [
        "Infraestructura como Código (IaC) declarativa en YAML o JSON para aprovisionar y gestionar recursos de AWS de forma repetible y automatizada.",
        "Drift Detection identifica discrepancias entre la plantilla y la infraestructura real; StackSets permite desplegar recursos a través de múltiples cuentas y regiones."
    ],
    "amazon-cloudwatch": [
        "Servicio central de observabilidad: Métricas (recolecta métricas de servicios cada 1 min o 5 min), Logs (ingesta y consulta de logs) y Alarmas (acciones hacia SNS o Auto Scaling).",
        "CloudWatch Synthetics crea canarios para monitorear endpoints y APIs; Container Insights y Lambda Insights ofrecen telemetría profunda."
    ],
    "aws-cloudtrail": [
        "Registra todas las acciones y llamadas a la API de AWS realizadas por usuarios, roles o servicios para auditoría de cumplimiento y gobernanza.",
        "Management Events registran operaciones del plano de control (gratuitos en el histórico de 90 días); Data Events registran operaciones de datos en S3 y Lambda (de pago)."
    ],
    "aws-organizations": [
        "Gestión centralizada de múltiples cuentas de AWS con consolidación de facturación para maximizar descuentos por volumen.",
        "Service Control Policies (SCPs) establecen barreras de seguridad (guardrails) fijando los permisos máximos permitidos en cuentas miembro, incluso sobre el usuario root."
    ],
    "aws-config": [
        "Evalúa, audita y registra continuamente los cambios de configuración en los recursos de AWS comparándolos contra reglas deseadas (Compliance).",
        "Permite activar remediación automática mediante documentos de Systems Manager ante recursos no conformes (ej. un bucket S3 público)."
    ],
    "aws-control-tower": [
        "Automatiza la creación de una arquitectura multi-cuenta segura (Landing Zone) siguiendo las mejores prácticas de AWS.",
        "Aplica guardrails preventivos (SCPs) y detectivos (reglas de AWS Config) para asegurar el gobierno continuo de las cuentas nuevas."
    ],

    # APPLICATION INTEGRATION
    "amazon-sqs": [
        "Colas de mensajes completamente administradas para desacoplar microservicios. Standard Queue = rendimiento casi ilimitado, entrega al menos una vez (at-least-once).",
        "FIFO Queue = orden estricto de mensajes y entrega exactamente una vez (exactly-once), con límite de hasta 300 msg/s (o 3000 con batching). Dead-Letter Queue (DLQ) para mensajes erróneos."
    ],
    "amazon-sns": [
        "Servicio Pub/Sub (Publicador/Suscriptor) para distribución masiva de mensajes (fan-out) hacia múltiples destinos simultáneos (colas SQS, funciones Lambda, endpoints HTTPS).",
        "Permite filtrar mensajes mediante Subscription Filter Policies para que cada suscriptor reciba únicamente los eventos de su interés."
    ],
    "amazon-api-gateway": [
        "Servicio totalmente administrado para crear, publicar, mantener y proteger APIs REST, HTTP (más ligeras y económicas) y WebSocket a cualquier escala.",
        "Soporta limitación de tasa (throttling), caching de respuestas, autorización con Cognito o Lambda Authorizers y validación de esquemas de solicitud."
    ],
    "aws-step-functions": [
        "Orquestador serverless visual basado en máquinas de estados para coordinar múltiples servicios de AWS y manejar reintentos y lógica de error.",
        "Standard Workflows para procesos largos (hasta 1 año) con historial de ejecución visual; Express Workflows para eventos de alto volumen y corta duración (hasta 5 min)."
    ],
    "amazon-eventbridge": [
        "Bus de eventos serverless que conecta aplicaciones con datos provenientes de servicios de AWS, aplicaciones SaaS de terceros y sistemas propios.",
        "Schema Registry detecta y genera automáticamente esquemas de eventos en Java/Python; EventBridge Pipes conecta fuentes y destinos con filtrado y enriquecimiento."
    ],
    "amazon-mq": [
        "Agente de mensajes gestionado compatible con estándares de la industria (Apache ActiveMQ y RabbitMQ) para migrar aplicaciones existentes a la nube sin reescribir código.",
        "Usa Amazon MQ cuando tu aplicación existente utilice protocolos como JMS, AMQP 0-9-1, MQTT u OpenWire en lugar de SQS/SNS."
    ],

    # ANALYTICS
    "amazon-athena": [
        "Motor de consultas SQL interactivo y serverless que analiza petabytes de datos directamente en Amazon S3 sin necesidad de cargar ni transformar datos.",
        "Cobra por terabyte de datos escaneados; optimiza costos particionando datos y convirtiéndolos a formatos columnares como Apache Parquet u ORC."
    ],
    "amazon-emr": [
        "Plataforma de Big Data administrada para ejecutar marcos de código abierto como Apache Spark, Hadoop, Presto y HBase a escala masiva.",
        "Usa instancias Spot en los Task Nodes para reducir costos dramáticamente en procesamiento de datos distribuidos sin riesgo de perder el cluster."
    ],
    "amazon-kinesis": [
        "Plataforma de streaming en tiempo real: Kinesis Data Streams (shards configurables con retención de hasta 365 días para múltiples consumidores) y Data Firehose (entrega serverless directa a S3/Redshift).",
        "Kinesis Data Analytics (ahora Managed Service for Apache Flink) procesa y analiza flujos de datos en tiempo real mediante SQL o Java."
    ],
    "aws-glue": [
        "Servicio serverless de integración de datos y ETL (Extracción, Transformación y Carga) que descubre, cataloga y limpia datos para analítica.",
        "Glue Data Catalog y Crawlers infieren esquemas automáticamente y crean tablas de metadatos compartidas con Athena, Redshift y EMR."
    ],
    "amazon-opensearch-service": [
        "Servicio administrado de búsqueda y analítica de registros en tiempo real (sucesor de Elasticsearch) integrado con OpenSearch Dashboards.",
        "UltraWarm y Cold Storage permiten retener grandes volúmenes de logs históricos a un costo hasta un 90% menor que el almacenamiento en caliente."
    ],

    # MACHINE LEARNING & AI
    "amazon-bedrock": [
        "Servicio totalmente administrado que ofrece acceso mediante una única API a Foundation Models (FMs) líderes de Anthropic, Meta, Mistral, AI21 y Amazon.",
        "Permite personalizar modelos de forma privada mediante fine-tuning y Retrieval Augmented Generation (RAG) con Knowledge Bases conectadas a fuentes de datos vectoriales."
    ],
    "amazon-sagemaker": [
        "Plataforma integral de ML para crear, entrenar y desplegar modelos: SageMaker Studio (IDE unificado), Clarify (detección de sesgos) y Pipelines (CI/CD para ML).",
        "Spot Training reduce hasta un 90% los costos de entrenamiento de modelos; Endpoints multi-modelo y Serverless Inference optimizan el hosting de inferencia."
    ],
    "amazon-rekognition": [
        "Servicio de visión artificial basado en Deep Learning para análisis automatizado de imágenes y videos (reconocimiento facial, detección de objetos y moderación de contenido inapropiado).",
        "No requiere experiencia previa en Machine Learning; accesible directamente mediante simples llamadas a la API de AWS."
    ],
    "amazon-transcribe": [
        "Convierte automáticamente voz a texto mediante modelos de reconocimiento automático del habla (ASR) con soporte para streaming y transcripción en lote.",
        "Soporta vocabulario personalizado para términos técnicos o de marca y redacción automática de información personal identificable (PII)."
    ],
    "amazon-polly": [
        "Sintetizador de texto a voz que convierte texto escrito en audio realista con decenas de voces naturales en múltiples idiomas.",
        "Soporta SSML (Speech Synthesis Markup Language) para ajustar el tono, velocidad y pronunciación, y genera Speech Marks para sincronización visual de labios."
    ],

    # MIGRATION & TRANSFER
    "aws-database-migration-service": [
        "Migra bases de datos relacionales, NoSQL y almacenes de datos hacia AWS con mínimo tiempo de inactividad utilizando replicación continua (CDC).",
        "AWS Schema Conversion Tool (SCT) convierte esquemas y código de base de datos entre motores heterogéneos (ej. Oracle a PostgreSQL)."
    ],
    "aws-datasync": [
        "Servicio acelerado de transferencia de datos que automatiza la sincronización de archivos entre almacenamiento on-premises y AWS (S3, EFS, FSx) hasta 10 veces más rápido.",
        "Verifica automáticamente la integridad de los datos en tránsito y en reposo mediante sumas de verificación (checksums)."
    ],
    "aws-transfer-family": [
        "Servicio totalmente administrado para transferir archivos directamente hacia y desde Amazon S3 o Amazon EFS usando protocolos SFTP, FTPS y FTP.",
        "Mantiene los flujos de trabajo y credenciales existentes de clientes y socios comerciales sin necesidad de gestionar servidores de archivos dedicados."
    ]
}

def run_phase2():
    with open(TARGET_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)

    curated_count = 0
    standardized_count = 0

    for s in data["results"]:
        slug = s["slug"]
        name = s["name"]
        cat_name = s["category"]["name"]
        scope = s["details"]["scope"]

        # 1. Apply High-Yield Official Exam Tips
        if slug in EXAM_TIPS_DATABASE:
            s["learning"]["exam_tips"] = EXAM_TIPS_DATABASE[slug]
            curated_count += 1
        else:
            # Domain-aware Well-Architected formal tips for niche/specialized services
            use_cases = s["details"].get("use_cases", [])
            primary_use = use_cases[0] if use_cases else "cargas de trabajo especializadas"
            s["learning"]["exam_tips"] = [
                f"{name} es un servicio {scope.upper()} de la categoría {cat_name}. En preguntas de arquitectura, identifícalo como la solución administrada para: {primary_use.lower()}.",
                f"Principio Well-Architected: Aplica menor sobrecarga operativa delegando la infraestructura subyacente y securiza el acceso mediante roles de IAM específicos."
            ]
            standardized_count += 1

    # Update metadata
    data["info"]["version"] = "2.1.0"
    data["info"]["contract_version"] = "2.1.0"

    with open(TARGET_FILE, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print(f"Phase 2 complete! Curated core services: {curated_count}, Standardized specialized: {standardized_count}")

if __name__ == "__main__":
    run_phase2()
