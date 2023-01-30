FROM cr.registry.res.cloud.longi.com/ldp-dev/centos7-jdk:v1.1
ARG env=dev
ENV run_env=${env}
RUN mkdir -p /usr/local/ldp/ldp-osca
ADD ldp-osca.jar /usr/local/ldp/ldp-osca/
#RUN mkdir -p /usr/local/ldp/tomcat-ssl
#ADD 7283249__longi.com.pfx /usr/local/ldp/tomcat-ssl/
WORKDIR /usr/local/ldp/ldp-osca
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${run_env}","-Djasypt.encryptor.password=LongiLmsp@2022","-DrunInDocker=true","ldp-osca.jar"]