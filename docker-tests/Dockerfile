FROM cwds/javajdk
RUN mkdir /opt/cans-api-test
ADD cans-api-test.jar /opt/cans-api-test/cans-api-test.jar
ADD resources /opt/cans-api-test/resources
ADD config /opt/cans-api-test/config
ADD entrypoint.sh /opt/cans-api-test/
RUN chmod +x /opt/cans-api-test/entrypoint.sh
WORKDIR /opt/cans-api-test
CMD ["/opt/cans-api-test/entrypoint.sh"]
