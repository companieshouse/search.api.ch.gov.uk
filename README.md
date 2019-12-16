# search.api.ch.gov.uk
The companies house search API for searching and upserting to an elastic search database.
This application is written in [Spring Boot](http://projects.spring.io/spring-boot/) Java framework.

## Requirements
In order to build document-generator locally you will need the following:
- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)

## Getting started
1. Run make
2. Run ./start.sh

Environment Variables
---------------------
The supported environmental variables have been categorised by use case and are as follows.

### Deployment Variables
Name                                      | Description                                                                  | Mandatory | Default | Example
----------------------------------------- | ---------------------------------------------------------------------------- | --------- | ------- | ----------------------------------------
SEARCH_API_HOST                           | elastic search database host name                                            | ✓         |         | es7-database-host-name.aws.chdev.org
ALPHABETICAL_SEARCH_INDEX                 | elastic search index name for the alphabetical search                        | ✓         |         | alpha-search
ALPHABETICAL_SEARCH_RESULT_MAX            | max results return for alphabetical search before filtering to 20            | ✓         |         | 250
