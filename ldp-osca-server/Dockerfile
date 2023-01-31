FROM cr.registry.res.cloud.longi.com/ldp-dev/centos7-jdk:v1.1

ENV BASE_PATH=/usr/local/ldp/ldp-osca
ENV SOFT_PATH=/$BASE_PATH/soft
RUN mkdir -p $SOFT_PATH/font && mkdir -p $SOFT_PATH/SourceHanSerifCN

#--- 拷贝插件----
COPY LibreOffice_7.3.1_Linux_x86-64_rpm_langpack_zh-CN.tar.gz $SOFT_PATH/
COPY LibreOffice_7.3.1_Linux_x86-64_rpm.tar.gz $SOFT_PATH/
COPY SourceHanSerifCN.zip $SOFT_PATH/
COPY font.zip $SOFT_PATH/

#--- 安装依赖rpm包----
RUN yum -y install unzip cups-libs cairo libSM

RUN cd $SOFT_PATH/ && tar -xvf LibreOffice_7.3.1_Linux_x86-64_rpm.tar.gz && cd $SOFT_PATH/LibreOffice_7.3.1.3_Linux_x86-64_rpm/RPMS/ && yum -y install *.rpm

RUN cd $SOFT_PATH/ && tar -xvf LibreOffice_7.3.1_Linux_x86-64_rpm_langpack_zh-CN.tar.gz && cd $SOFT_PATH/LibreOffice_7.3.1.3_Linux_x86-64_rpm_langpack_zh-CN/RPMS && yum -y install *.rpm

# --- 安装系统字体-----
RUN cd $SOFT_PATH/ && unzip -d $SOFT_PATH/SourceHanSerifCN SourceHanSerifCN.zip && fc-cache -fv
RUN cd $SOFT_PATH/ && unzip -d $SOFT_PATH/font font.zip && yum -y install fontconfig ttmkfdir mkfontscale && mkfontscale && mkfontdir&& fc-cache

#RUN kill -9 `ps -ef|grep libreoffice | grep -v "grep" | grep libreoffice | awk '{print $2}'` 2 >/dev/null
RUN /opt/libreoffice7.3/program/soffice --headless --accept="socket,host=127.0.0.1,port=8100;urp;" --nofirststartwizard &
RUN rm -rf $SOFT_PATH

#---拷贝服务----
#RUN mkdir -p /usr/local/lmsp/tomcat-ssl/
#ADD 7283249__longi.com.pfx /usr/local/lmsp/tomcat-ssl/
ADD ldp-osca.jar /usr/local/ldp/ldp-osca/
WORKDIR /usr/local/ldp/ldp-osca

ARG env=dev
ENV run_env=${env}
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=${run_env}","-Djasypt.encryptor.password=LongiLmsp@2022","-DrunInDocker=true","ldp-osca.jar"]
