FROM cwds/alpinejre

# Performance tests
RUN apk add curl
RUN curl https://chromedriver.storage.googleapis.com/2.41/chromedriver_linux64.zip -o /usr/local/bin/chromedriver
RUN chmod +x /usr/local/bin/chromedriver
RUN apk add wget
RUN cd /opt/ \
    && wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-4.0.tgz \
    && tar -xvzf apache-jmeter-4.0.tgz \
    && rm apache-jmeter-4.0.tgz
ENV JMETER_HOME /opt/apache-jmeter-4.0/
RUN cd /opt/apache-jmeter-4.0/lib/ext \
    && wget -O jmeter-plugins-manager-1.3.jar http://search.maven.org/remotecontent?filepath=kg/apc/jmeter-plugins-manager/1.3/jmeter-plugins-manager-1.3.jar
RUN java -cp /opt/apache-jmeter-4.0/lib/ext/jmeter-plugins-manager-1.3.jar org.jmeterplugins.repository.PluginManagerCMDInstaller
RUN cd /opt/apache-jmeter-4.0/lib \
    && wget -O cmdrunner-2.2.jar http://search.maven.org/remotecontent?filepath=kg/apc/cmdrunner/2.2/cmdrunner-2.2.jar
RUN /opt/apache-jmeter-4.0/bin/PluginsManagerCMD.sh install jpgc-webdriver
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
ENTRYPOINT ["/opt/cans-api-test/entrypoint.sh"]
