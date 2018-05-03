FROM cwds/javajdk
RUN mkdir /opt/cans-api-tests
ADD cans-api-tests.jar /opt/cans-api-tests/cans-api-tests.jar
ADD resources /opt/cans-api-tests/resources
ADD config /opt/cans-api-tests/config
ADD entrypoint.sh /opt/cans-api-tests/
RUN chmod +x /opt/cans-api-tests/entrypoint.sh
WORKDIR /opt/cans-api-tests
CMD ["/opt/cans-api-tests/entrypoint.sh"]
