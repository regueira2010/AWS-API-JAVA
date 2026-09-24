# ========================================================
# 1. Build Stage (Maven + Eclipse Temurin JDK 21 Alpine)
# ========================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Cache de dependencias de Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Compilación y empaquetado del artefacto ejecutable
COPY src ./src
RUN mvn clean package -DskipTests -B

# ========================================================
# 2. Runtime Stage (Lightweight JRE 21 Alpine - ~150 MB)
# ========================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# DevSecOps: Ejecución con usuario sin privilegios root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el artefacto generado en la etapa de build
COPY --from=builder /build/target/*.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser

# Parámetros JVM optimizados para Render Free (512 MB RAM)
# Limita el Heap al 75% de la memoria del contenedor y activa G1GC
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"
ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
