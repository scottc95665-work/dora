# CWDS Dora

The CWDS Dora provides RESTful services with search capabilities.

## Documentation

The development team uses [Swagger](http://swagger.io/) for documenting the API.  
NOTE: At this time there is not a publicly available link to the documentation, a link will be provided as soon as one is available.

Dora does not use any default index or document type. Its URL has the following format: `/dora/{index}/{type}/_search`.
For example `/dora/facilities/facility/_search` or `/dora/people/person/_search`.

## Configuration

Configuration options are available in the file config/dora.yml.

### Security Configuration

#### Disabling Security
- Elasticsearch Server:
    - file: <ELASTICSEARCH_ROOT>config/elasticsearch.yml
    - option: xpack.security.enabled: false
- Dora:
    - file: <DORA_PROJECT_ROOT>/config/dora.yml
    - option: elasticsearch.xpack.enabled: false

#### Enabling Security
- Elasticsearch Server:
    - file: <ELASTICSEARCH_ROOT>/config/elasticsearch.yml
    - option: xpack.security.enabled: true
- Dora:
    - file: <DORA_PROJECT_ROOT>/config/dora.yml
      - option: elasticsearch.xpack.enabled: true
    - file: <DORA_PROJECT_ROOT>/config/shiro.ini
      - option: perryClient.serviceProviderId = dora
      - option: perryRealm = gov.ca.cwds.auth.realms.PerryAccountRealm
      - option: /** = noSession, perry

## Development Environment

### Prerequisites

1. Source code, available at [GitHub](https://github.com/ca-cwds/dora)
1. Java SE 8 development kit

### Environment variables of interest

- ES_HOST - IP address of the Elasticsearch server
- ES_PORT - port of the Elasticsearch server (default: `9200`)
- APP_STD_PORT - default: `8080`
- APP_ADMIN_PORT - default: `8081`
- LOGIN_URL - default: `http://localhost:8090/authn/login`
- SHOW_SWAGGER - set to `true` to have a link like `http://localhost:8080/swagger` (default: `false`)

### Run Dora

Use the gradlew command to execute the run task:

    % ./gradlew run

This will run the server on your local machine, port 8080.

### Unit Testing

Use the gradlew command to execute the test task:

    % ./gradlew test

### Integration Testing

Use the gradlew command to execute the test task:

    % ./gradlew integrationTest
    
### Commiting Changes

Before commiting changes to the reporsitory please run the following to ensure the build is successful.

    % ./gradlew clean test integrationTest javadoc
