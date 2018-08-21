# CWDS Dora

The CWDS Dora provides RESTful services with search capabilities.

## Documentation

The development team uses [Swagger](http://swagger.io/) for documenting the API.  
NOTE: At this time there is not a publicly available link to the documentation, a link will be provided as soon as one is available.

Dora does not use any default index or document type. Its URL has the following format: `/dora/{index}/{type}/_search`.
For example `/dora/facilities/facility/_search` or `/dora/people/person/_search`.

## Configuration

Configuration options are available in the file config/dora.yml.

Dora can be configured to run in `PROD` or `DEV` mode.
The default mode is `PROD`, and it can be changed with the `DORA_MODE` environment variable accepting values `'PROD'` and `'DEV'`.
When Dora is configured to run in `PROD` mode, it does not fire health checks of Elasticsearch XPack Roles.
Any other mode will cause Dora to fire that health checks.

### Security Configuration

#### Disabling Security
- Elasticsearch Server:
    - file: `<ELASTICSEARCH_ROOT>/config/elasticsearch.yml`
    - option: `xpack.security.enabled: false`
- Dora:
    - file: `<DORA_PROJECT_ROOT>/config/dora_nosec.yml` (note **_nosec** suffix)

#### Enabling Security
- Elasticsearch Server:
    - file: `<ELASTICSEARCH_ROOT>/config/elasticsearch.yml`
    - option: `xpack.security.enabled: true`
- Dora:
    - use configuration file with pre-configured security: `<DORA_PROJECT_ROOT>/config/dora.yml`

## Development Environment

### Prerequisites

1. Source code, available at [GitHub](https://github.com/ca-cwds/dora)
1. Java SE 8 development kit

### Environment variables of interest

- ES_HOST - IP address of the Elasticsearch server
- ES_PORT - port of the Elasticsearch server (default: `9200`)
- APP_STD_PORT - default: `8080`
- APP_ADMIN_PORT - default: `8081`
- PERRY_URL - default: `http://localhost:8090/authn/login`
- SP_ID - service provider ID for Identity Mapping (defines security attributes for specific application)
- SHOW_SWAGGER - set to `true` to have a link like `http://localhost:8080/swagger` (default: `false`)
- XPACK_ENABLED - true/false, has effect only when used while running a docker container based on the cwds/dora

### Run Dora

Use the gradlew command to execute the run task:

    % ./gradlew :dora-api:run

This will run the server on your local machine in non-secured mode, port 8080.

### Unit Testing

Use the gradlew command to execute the test task:

    % ./gradlew test

### Integration Testing

Use the gradlew command to execute the test task:

    % ./gradlew integrationTest
    
### Commiting Changes

Before commiting changes to the reporsitory please run the following to ensure the build is successful.

    % ./gradlew clean test integrationTest javadoc

## Building and publishing docker image with Dora
 
The following command will build a versioned docker image with Dora and publish it to DockerHub as `cwds/dora` 

    % ./gradlew :docker-dora:dockerDoraPublish

A developer might want to set the following environment variables prior running that command locally: 
- DOCKERHUB_ORG=\<own Docker ID\>

## Running docker container with Dora in non-secured mode

    % docker pull cwds/dora
    % docker run -d --name=<container name> -p 8080:8080 -p 8081:8081 -e ES_HOST=<ELASTICSEARCH_IP> -e ES_PORT=9200 -e XPACK_ENABLED=false cwds/dora

For example:

    % docker run -d --name=dora1 -p 8080:8080 -p 8081:8081 -e ES_HOST=192.168.56.1 -e ES_PORT=9200 -e XPACK_ENABLED=false cwds/dora
    
Add `-e SHOW_SWAGGER=true` to turn on swagger for development purposes:

    % docker run -d --name=dora1 -p 8080:8080 -p 8081:8081 -e ES_HOST=192.168.56.1 -e ES_PORT=9200 -e XPACK_ENABLED=false -e SHOW_SWAGGER=true cwds/dora
    
Assuming that Dora's IP address is 192.168.99.100, swagger should be available at: `http://192.168.99.100:8080/swagger`

Assuming that Dora's IP address is 192.168.99.100, the Dora should be able to handle **POST** requests to URLs like:
- `http://192.168.99.100:8080/dora/people/person/_search`
- `http://192.168.99.100:8080/dora/facilities/facilitiy/_search`
    
for example:

```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{ \ 
           "query" : { \ 
               "term" : { "name" : "John" } \ 
           } \ 
       }' 'http://192.168.99.100:8080/dora/people/person/_search'
```

## Dora availability quick check

Assuming that Dora's IP address is 192.168.99.100, the Dora should be able to handle **GET** requests like:

    % curl -X GET --header 'Accept: application/json' http://192.168.99.100:8080/system-information

## Running docker container with Dora in secured mode

Security is enabled by default in Dora. To run Dora in secured mode, simply omit `-e XPACK_ENABLED=false` from the docker commands above.
Or use `-e XPACK_ENABLED=true`.

Add `-e PERRY_URL=…` to configure Perry location which is used when you click the login button on the swagger UI.

For example: `-e PERRY_URL=http://localhost:8090/authn/login`

## Building and publishing docker image with Elasticsearch + X-Pack
 
The following command will build a versioned docker image with Elasticsearch 5.5.2 + X-Pack and publish it to DockerHub as `cwds/elasticsearch_xpack_data` 

    % ./gradlew :docker-es-xpack:dockerEsXpackPublish
    
A developer might want to set the following environment variables prior running that command locally: 
- DOCKERHUB_ORG=\<own Docker ID\>

_**It is not recommended to publish the docker image with Elasticsearch + X-Pack to public repository**_

## Running docker container with Elasticsearch + X-Pack

There is a Docker Image with Elasticsearch 5.5.2 and X-Pack.

Pull the Docker image:

    % docker pull cwds/elasticsearch_xpack_data

Run the container:

    % docker run -d --name=<container name> -p 9200:9200 -p 9300:9300 -e http.host=0.0.0.0 -e transport.host=127.0.0.1 cwds/elasticsearch_xpack_data
    
Consider adding the following environment variable to the container to integrate it with Perry:

TOKEN_VALIDATION_URL=http://10.0.75.1:8080/perry/authn/validate?token=

*Note*: the actual value of the parameter depends on the environment.  
    
## Clean Up

Run ```gradlew :docker-dora:dockerCleanUp``` - to remove the dora image from local docker environment.

Run ```gradlew :docker-es-xpack:dockerCleanUp``` - to remove the elasticsearch_xpack_data image from local docker environment.
