# CWDS Dora

The CWDS Dora provides RESTful services with search capabilities.

## Documentation

The development team uses [Swagger](http://swagger.io/) for documenting the API.  
NOTE : At this time there is not a publicy available link to the documentation, a link will be provided as soon as one is available.

## Configuration

Configuration options are available in the file config/dora.yml.

## Development Environment

### Prerequisites

1. Source code, available at [GitHub](https://github.com/ca-cwds/dora)
1. Java SE 8 development kit

### Development Server

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
