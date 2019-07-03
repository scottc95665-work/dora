# CWDS Dora

The CWDS Dora provides REST API for access to Elasticsearch with custom layer of authentication and authorization.

## Architectural Decisions
* Dora was created as a wrapper around original Elasticsearch REST API to encapsulate security concerns and provide re-usability of search functionality. All applications in CARES uses Dora to access Elasticsearch indexes.
* Elasticsearch XPack is used to implement custom security policies around CARES search indexes (Client Sealed/Sensitive logic).
* Custom realm plugin was developed for integration of Elasticsearch with Perry. 
* Perry Realm plugin receives Perry token and validates it in Perry. It uses user information from Perry token to assign roles to elasticsearch user. Assigned roles then can be used in our custom XPack policies file.
* Dora API provides proxy API for Elasticsearch search API. Native Elasticsearch queries can be used as input.
* Dora API provides ability to create object in some index as well as update it
* Dora API is used in CANS, CALS, CAP and Intake applications as the only API which has access to Elasticsearch         
* Dora containers diagram 
![Dora_containers_diagram](https://user-images.githubusercontent.com/3201038/60618407-fbf9a380-9d8a-11e9-9bb0-8b708fc0c1e0.png)
* Example of Dora usage in CANS application
![CANS_context_diagram](https://user-images.githubusercontent.com/3201038/60618521-40853f00-9d8b-11e9-8d9e-44d302376a7e.png)

## Project Structure
* **docker-es-xpack** - has code responsible for publishing https://cloud.docker.com/u/cwds/repository/docker/cwds/elasticsearch_xpack_data docker Image. elasticsearch_xpack_data is a modified Elasticsearch image which includes custom XPack plugin, XPack policies and test data for indexes used in CARES.
* **docker-dora** - docker file for Dora Docker image
* **x-pack-perry-realm** - XPack plugin used for integration with Perry
* **dora-api** - REST API for access to Elasticsearch

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

### Run Dora on local environment
In order to start Dora on local environment developer should configure connection to Perry and Elasticsearch. 
1. Using docker-compose.yaml in the root folder of the project you can start Dora, Perry, Elasticsearch on local machine. Versions of docker images can be channged in docker-compose.yml file so that you have the recent versions of dependencies. Use following command to start all containers: `docker-compose up`. Dora will be available on http://localhost:8083/swagger

2. During active Dora development it is desirable to have faster feedback after code change. So first approach can be not convenient because will take time to build docker images. 
Recommended way to setup local environment is to use docker-compose.yml file in the root folder of the project to start only Perry in DEV mode and Elasticsearch-xpack-data Docker images. You can comment portion related to Dora container and start dora from your IDE.   
Use the gradlew command to execute the run task: `% ./gradlew :dora-api:run`. This will run the server on your local machine http://localhost:8083/swagger.


**Note:** For Windows OS there can be an issue with starting application the way described in item 2 above where Gradle will return 406 error "Long classpath". You can start application using DoraApplication class by configuring it manually to run with configuration in ./config/dora.yml. Intellij IDEA provide workaround for 406 error. You can see how DoraApplication should be configured to run in Intellij IDEA ![DoraApplication Configuration](https://user-images.githubusercontent.com/3201038/60617223-20a04c00-9d88-11e9-9582-99ec490b63f2.png): 

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

### Unit Testing

Use the gradlew command to execute the test task:

    % ./gradlew test

### Integration Testing

Use the gradlew command to execute the test task:

    % ./gradlew integrationTest
    
### Commiting Changes

Before commiting changes to the repository please run the following to ensure the build is successful.

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

# Questions

If you have any questions regarding the contents of this repository, please email the Office of Systems Integration at FOSS@osi.ca.gov.
