Feature: Delete company search

  Scenario Outline: Delete company search successfully
    Given the company search entity resource exists for "<company_number>"
    When a DELETE request is sent to the company search endpoint for "<company_number>"
    And the company search entity does not exist for "<company_number>"
    Then I should receive 200 status code


    Examples:
      | data_file               | company_number |
      | with_links_resource     | 00006400       |
    
  
  Scenario Outline: Delete company search unsuccessfully - user not authenticated
    When a DELETE request is sent to the primary search endpoint for "<company_number>" without valid ERIC headers
    Then I should receive 401 status code

    Examples:
      | company_number |
      | 00006400       |


  Scenario Outline: Delete company search unsuccessfully - forbidden request
    When a DELETE request is sent to the primary search endpoint for "<company_number>" with insufficient access
    Then I should receive 403 status code

    Examples:
      | company_number |
      | 00006400       |


  Scenario Outline: Delete company search unsuccessfully while service is down
    Given the company search entity resource exists for "<company_number>"
    And the company profile database is down
    When a DELETE request is sent to the company search endpoint for "<company_number>"
    Then I should receive 503 status code

    Examples:
      | data_file               | company_number |
      | with_links_resource     | 00006400       |

