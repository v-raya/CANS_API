FROM cwds/alpinejre
RUN mkdir /opt/cans-api
RUN mkdir /opt/cans-api/logs
RUN mkdir /opt/cans-api/config
ADD config/*.yml /opt/cans-api/
ADD config/shiro*.ini /opt/cans-api/config/
ADD config/enc.jceks /opt/cans-api/config/enc.jceks
ADD config/testKeyStore.jks /opt/cans-api/config/testKeyStore.jks
ADD build/libs/cans-api-dist.jar /opt/cans-api/cans-api.jar
ADD build/entrypoint.sh /opt/cans-api/
EXPOSE 8080
RUN chmod +x /opt/cans-api/entrypoint.sh
WORKDIR /opt/cans-api
CMD ["/opt/cans-api/entrypoint.sh"]
