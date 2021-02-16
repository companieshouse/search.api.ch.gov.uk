# search.api.ch.gov.uk
Provides functionality to query the alpha_search index within Elastic Search to return company profile information that is sorted in alphabetical order. The service also provides the ability to ‘upsert’ a company. This feature allows the data present within elastic search to be up to date, providing an option to add new companies or update the existing. The search.api.ch.gov.uk at present relates only to alphabetical-search though potenial may encapsulate advanced search and search in general.

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
