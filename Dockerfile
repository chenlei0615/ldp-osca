FROM cr.registry.res.cloud.longi.com/ldp-dev/centos7-jdk:v1.1
ARG env=dev
ENV run_env=${env}
RUN mkdir -p /usr/local/lmsp/lmsp-osca
ADD lmsp-osca.jar /usr/local/lmsp/lmsp-osca/
#RUN mkdir -p /usr/local/lmsp/tomcat-ssl
#ADD 7283249__longi.com.pfx /usr/local/lmsp/tomcat-ssl/
WORKDIR /usr/local/lmsp/lmsp-osca
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${run_env}","-Djasypt.encryptor.password=LongiLmsp@2022","-DrunInDocker=true","lmsp-osca.jar"]