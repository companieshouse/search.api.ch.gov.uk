# search.api.ch.gov.uk
Provides functionality to query the alpha_search index, and the dissolved search index within Elastic Search to return company profile information that is sorted in alphabetical order. In the case of the dissolved search index, this will currently only be companies which have been dissolved prior to 2009. The service also provides the ability to ‘upsert’ a company. This feature allows the data present within elastic search to be up to date, providing an option to add new companies or update the existing. The search.api.ch.gov.uk at present relates only to alphabetical-search & dissolved search, though potentially may encapsulate advanced search and search in general. 

## Requirements
In order to build document-generator locally you will need the following:
- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)

## Getting started
1. Run make
2. Run ./start.sh

Environment Variables
---------------------
The supported environmental variables have been categorised by use case and are as follows.

### Deployment Variables
Name                                        | Description                                                                  | Mandatory | Default | Example
------------------------------------------- | ---------------------------------------------------------------------------- | --------- | ------- | ------------------------------------------------
ELASTIC\_SEARCH\_URL                          | elastic search cluster hostname for alphabetical search index                | ✓         |         | https://es7-alphabetical-hostname.aws.chdev.org
DISSOLVED\_SEARCH\_URL                        | elastic search cluster hostname for dissolved search index                   | ✓         |         | https://es7-dissolved-hostname.aws.chdev.org
ADVANCED\_SEARCH\_URL                         | elastic search cluster hostname for advanced search index                    | ✓         |         | https://es7-advanced-hostname.aws.chdev.org
PRIMARY\_SEARCH\_URL                          | elastic search cluster hostname for primary search index                     | ✓         |         | https://es7-primary-hostname.aws.chdev.org
------------------------------------------- | ---------------------------------------------------------------------------- | --------- | ------- | ------------------------------------------------
ALPHAKEY\_SERVICE\_URL                        | pointer to alphakey service                                                  | ✓         |         | http://alpha-key-hostname/alphakey?name=
------------------------------------------- | ---------------------------------------------------------------------------- | --------- | ------- | ------------------------------------------------
ALPHABETICAL\_SEARCH\_INDEX                   | elastic search index name for the alphabetical search                        | ✓         |         | alpha-search
DISSOLVED\_SEARCH\_INDEX                      | elastic search index name for the dissolved search                           | ✓         |         | dissolved-search
ADVANCED\_SEARCH\_INDEX                       | elastic search index name for the advanced search                            | ✓         |         | advanced-search
PRIMARY\_SEARCH\_INDEX                        | elastic search index name for the primary search                             | ✓         |         | primary search
------------------------------------------- | ---------------------------------------------------------------------------- | --------- | ------- | ------------------------------------------------
ALPHABETICAL\_SEARCH\_RESULT\_MAX              | max results returned for alphabetical search before filtering to 20          | ✓         |         | 20
DISSOLVED\_SEARCH\_RESULT\_MAX                 | max results for the dissolved search before filtering to 20                  | ✓         |         | 20
DISSOLVED\_ALPHABETICAL\_SEARCH\_RESULT\_MAX    | max results for the dissolved alphabetical search before filtering           | ✓         |         | 20
ADVANCED\_SEARCH\_MAX\_SIZE                    | max results for the advanced search                                          | ✓         |         | 5000
ADVANCED\_SEARCH\_DEFAULT\_SIZE                | default size for advanced search                                             | ✓         |         | 20
MAX\_SIZE\_PARAM                              | maximum value of size parameter                                              | ✓         |         | 100
ALPHABETICAL\_FALLBACK\_QUERY\_LIMIT           | fallback query limit for alphabetical search                                 | ✓         |         | 25
DISSOLVED\_ALPHABETICAL\_FALLBACK\_QUERY\_LIMIT | fallback query limit for dissolved alphabetical search                       | ✓         |         | 15

### Endpoints

| Method | Path                                                      | Description                                           |
|--------|-----------------------------------------------------------|-------------------------------------------------------|
| GET    | `/dissolved-search/companies`                             | Returns the dissolved company search results          |
| PUT    | `/disqualified-search/disqualified-officers/{officer_id}` | Insert/update the officer details                     |
| DELETE | `/disqualified-search/delete/{officer_id}`                | Delete the officer details                            |
| GET    | `/advanced-search/companies`                              | Returns the advanced search company results           |
| PUT    | `/advanced-search/companies/{company_number}`             | Insert/update the company details                     |
| DELETE | `/advanced-search/companies/{company_number}`             | Delete the company details                            |
| PUT    | `/officers-search/officers/{officer_id}`                  | Insert/update the officer details                     |
| DELETE | `/officers-search/officers/{officer_id}`                  | Delete the officer details                            |
| GET    | `/alphabetical-search/companies`                          | Returns the alphabetical company search results       |
| PUT    | `/alphabetical-search/companies/{company_number}`         | Insert/update the company details                     |
| DELETE | `/alphabetical-search/companies/{company_number}`         | Delete the company details                            |
| GET    | `/search/healthcheck`                                     | Returns the healthcheck result for the search service |
| PUT    | `/company-search/companies/{company_number}`              |                                                       |
| DELETE | `/company-search/companies/{company_number}`              | Delete the company details                            |

