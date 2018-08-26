# CWDS CANS API

The CWDS CANS API provides RESTful services for the CWDS CANS Digital Service.

## Wiki

The development team is actively using the [Github Wiki](https://github.com/ca-cwds/cans-api/wiki).

## Documentation

The development team uses [Swagger](http://swagger.io/) for documenting the API.
NOTE : At this time there is not a publicly available link to the documentation, a link will be provided as soon as one is available.


## Configuration

### Application Configuration Parameters
- APP_VERSION -- Version of application

### SSL Configuration Parameters
- KEY_STORE_FILE -- Path to keystore file
- KEY_STORE_PASSWORD -- Keystore password

### Data Stores Configuration Parameters

The CWDS API currently utilizes four persistent stores:

In order for the CANS API successfully connect to the databases the following environment variables are required to be set:

#### Postgres - NS database
- DB_NS_JDBC_URL -- the NS database URL in Java Database Connectivity format
- DB_NS_SCHEMA -- the NS database schema
- DB_NS_USER -- the NS database username
- DB_NS_PASSWORD -- the NS database password
- DB_NS_CP_MAX_SIZE -- the NS connections pool maximum size (default: 8)


#### Swagger Configuration Parameters**
- LOGIN_URL -- Login URL
- LOGOUT_URL -- Logout URL 
- SHOW_SWAGGER -- Show swagger (true | false) default - true
- SWAGGER_JSON_URL -- default - http://localhost:8080/swagger.json
- SWAGGER_CALLBACK_URL -- default - http://localhost:8080/swagger

#### Shiro Configuration Parameters
- SHIRO_CONFIG_PATH -- path to Shiro configuration file
 
The Docker env-file option provides a convenient method to supply these variables. These instructions assume an env file called .env located in the current directory. The repository contains a sample env file called env.sample.

Further configuration options are available in the file config/cans-api.yml.

## Testing

### Functional testing
To run Functional tests set "api.url" and "perry.url" properties to point to environment host. Use gradle FunctionalTest task. In this case token will be generated for default test user, so it's possible to test environment with Perry running in dev mode.

### Smoke Testing
Smoke test suite is part of Functional tests. Set "api.url", use gradle smokeTestSuite task. Smoke test endpoint is not protected by Perry.

## Development

### Running the Application

_Make sure you have the CANS postgres and Perry containers running or the build will fail_

`./gradlew run`



