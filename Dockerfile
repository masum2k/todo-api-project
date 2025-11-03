# --- 1. Aşama: Build (Projenin .jar dosyasını oluşturma) ---
FROM eclipse-temurin:21-jdk-jammy as builder

# Proje dosyaları için bir klasör oluştur
WORKDIR /app

# Maven wrapper dosyalarını kopyala (bu, 'mvnw' komutunu kullanmamızı sağlar)
COPY mvnw .
COPY .mvn .mvn

# Projenin bağımlılıklarını kopyala ve indir (Docker katman önbelleğini kullanır)
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Tüm proje kaynak kodunu kopyala
COPY src src

# Projeyi build et (testleri atla)
RUN ./mvnw clean package -Dmaven.test.skip=true

# --- 2. Aşama: Run (Projenin son imajını oluşturma) ---
# Daha küçük bir "sadece çalıştırma" (JRE) imajı kullan
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Sadece 1. aşamada (builder) oluşturulan .jar dosyasını kopyala
COPY --from=builder /app/target/*.jar app.jar

# Uygulamanın 8080 portunu açtığını belirt
EXPOSE 8080

# Konteyner başladığında çalıştırılacak komut
ENTRYPOINT ["java", "-jar", "app.jar"]