FROM cwds/javajdk

# Performance tests
RUN yum -y install wget
RUN cd /opt/ \
    && wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-4.0.tgz \
    && tar -xvzf apache-jmeter-4.0.tgz \
    && rm apache-jmeter-4.0.tgz
ENV JMETER_HOME /opt/apache-jmeter-4.0/
RUN mkdir /opt/cans-api-perf-test
ADD jmeter /opt/cans-api-perf-test
ENV JMETER_TESTS /opt/cans-api-perf-test
RUN mkdir $JMETER_TESTS/results && mkdir $JMETER_TESTS/results/api && mkdir $JMETER_TESTS/results/rails

# Smoke & Functional
RUN mkdir /opt/cans-api-test
ADD cans-api-test.jar /opt/cans-api-test/cans-api-test.jar
ADD resources /opt/cans-api-test/resources
ADD config /opt/cans-api-test/config
ADD entrypoint.sh /opt/cans-api-test/
RUN chmod +x /opt/cans-api-test/entrypoint.sh
WORKDIR /opt/cans-api-test
CMD ["/opt/cans-api-test/entrypoint.sh"]
