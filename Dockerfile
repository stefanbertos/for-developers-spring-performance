# --- Stage 1: Build & CDS ---
FROM eclipse-temurin:24-jdk-alpine as build

WORKDIR /workspace/app

COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Enable Spring AOT
RUN ./gradlew clean build -PspringAot -x test

# Extract layers
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*aot.jar)

# Generate CDS archive
WORKDIR /workspace/app/build
RUN java -Xshare:off -XX:DumpLoadedClassList=classlist.lst -cp libs/*aot.jar com.example.demo.DemoApplication || true
RUN java -Xshare:dump -XX:SharedClassListFile=classlist.lst -XX:SharedArchiveFile=app-cds.jsa -cp libs/*aot.jar

# --- Stage 2: Runtime ---
FROM eclipse-temurin:24-jre-alpine

ENV JAVA_TOOL_OPTIONS="\
  -XX:+UseZGC \
  -XX:+ZGenerational \
  -XX:SharedArchiveFile=/app/app-cds.jsa \
  -XX:+UseAppCDS \
  -Xms512m -Xmx2g \
  -XX:+TieredCompilation \
  -Xlog:gc*:file=/tmp/gc.log:time,level,tags \
  --enable-preview \
"

WORKDIR /app

# Copy AOT jar + extracted app + CDS archive
COPY --from=build /workspace/app/build/libs/*aot.jar /app/app.jar
COPY --from=build /workspace/app/build/app-cds.jsa /app/app-cds.jsa
COPY --from=build /workspace/app/build/dependency/BOOT-INF/lib /app/lib
COPY --from=build /workspace/app/build/dependency/META-INF /app/META-INF
COPY --from=build /workspace/app/build/dependency/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.example.demo.DemoApplication"]
