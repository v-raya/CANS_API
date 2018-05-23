# CWDS CANS API TESTS

This image can be used to run a set of tests on CANS API 


# Configuration
## Application Configuration Parameters
- **CANS_API_URL** -- URL of the cans-api instance to test. 
**Required:** true.
**Example:** "http://cans.dev.cwds.io:8089/" 
- **TEST_TYPE** -- test type to be started in the container. 
**Required:** false.
**Possible values:** smoke; functional.
**Default value:** smoke.
- **DB_NS_JDBC_URL** -- JDBC URL of the PostgreSQL instance the cans-api under test is using.
**Required:** true, when TEST_TYPE=functional.
**Example:** "jdbc:postgresql://192.168.99.100:5432/postgres_data"
- **DB_NS_SCHEMA** -- Schema name in the PostgreSQL instance. 
**Required:** true, when TEST_TYPE=functional.
**Example:** "CANS"
- **DB_NS_USER** -- PostgreSQL instance username.
**Required:** true, when TEST_TYPE=functional.
**Example:** "user"
- **DB_NS_PASSWORD** -- PostgreSQL instance password.
**Required:** true, when TEST_TYPE=functional. 
**Example:** "pass"

# Example commands to run:
## Run smoke test example
docker run -e "CANS_API_URL=http://cans.dev.cwds.io:8089/" -e "TEST_TYPE=smoke" -it cwds/cans-api-test

## Run functional test example
docker run -e "CANS_API_URL=http://cans.dev.cwds.io:8080/" \
	-e "TEST_TYPE=functional" \
	-e "DB_NS_JDBC_URL=jdbc:postgresql://postgres.dev.cwds.io:5432/postgres_data" \
	-e "DB_NS_SCHEMA=CANS" \
	-e "DB_NS_USER=postgres_data" \
	-e "DB_MS_PASSWORD=postgres_data" \
	-it cwds/cans-api-test

# Result
The container returns 0 if tests finished successfully and 1 if tests failed.
