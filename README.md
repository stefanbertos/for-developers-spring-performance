# for-developers-spring-performance
1. goal is to showcase that Spring Boot is a good choice for performance-critical applications
2. What are the options we can use? GraalVM, Java 25, Reactive, Virtual Threads, ... profiler
   https://medium.com/@mohitbajaj1995/how-i-optimized-a-spring-boot-application-to-handle-1m-requests-second-0cbb2f2823ed
   To measure actual live metrics in your Spring Boot app, use:

Micrometer for instrumentation (built into Spring Boot Actuator)

Prometheus to scrape metrics

Grafana to visualize RPM, latency, etc.

