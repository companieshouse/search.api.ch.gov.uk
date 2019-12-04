package uk.gov.companieshouse.search.api.service.upsert;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.search.api.model.esdatamodel.company.Company;
import uk.gov.companieshouse.search.api.model.response.ResponseObject;
import uk.gov.companieshouse.search.api.model.response.ResponseStatus;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Service
public class UpsertCompanyService {

    @Autowired
    private RestHighLevelClient client;

    public ResponseObject upsert(Company company) throws IOException {


        IndexRequest indexRequest = new IndexRequest("alpha_search", "_doc", company.getId())
            .source(jsonBuilder()
                .startObject()
                .field("ID", company.getId())
                .field("company_type", company.getCompanyType())
                .startObject("items")
                    .field("company_number", company.getItems().getCompanyNumber())
                    .field("company_status", company.getItems().getCompanyStatus())
                    .field("corporate_name", company.getItems().getCorporateName())
                    .field("record_type", company.getItems().getRecordType())
                .endObject()
                .startObject("links")
                    .field("self", company.getLinks().getSelf())
                .endObject()
                .endObject());

        UpdateRequest updateRequest = new UpdateRequest("alpha_search", "_doc", company.getId())
            .doc(jsonBuilder()
            .startObject()
                .field("ID", company.getId())
                .field("company_type", company.getCompanyType())
                .startObject("items")
                    .field("company_number", company.getItems().getCompanyNumber())
                    .field("company_status", company.getItems().getCompanyStatus())
                    .field("corporate_name", company.getItems().getCorporateName())
                    .field("record_type", company.getItems().getRecordType())
                .endObject()
                .startObject("links")
                    .field("self", company.getLinks().getSelf())
                .endObject()
            .endObject())
        .upsert(indexRequest);

        client.update(updateRequest, RequestOptions.DEFAULT);

        return new ResponseObject(ResponseStatus.SEARCH_FOUND);
    }
}
