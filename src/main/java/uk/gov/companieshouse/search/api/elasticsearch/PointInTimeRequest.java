package uk.gov.companieshouse.search.api.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch.core.ClearScrollResponse;
import co.elastic.clients.elasticsearch.core.ClosePointInTimeRequest;
import co.elastic.clients.elasticsearch.core.ClosePointInTimeResponse;
import co.elastic.clients.elasticsearch.core.OpenPointInTimeRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PointInTimeRequest {

    ElasticsearchClient client;

    public PointInTimeRequest(ElasticsearchClient client) {
        this.client = client;
    }

    public String getPointInTimeID() throws IOException {
        OpenPointInTimeRequest request = new OpenPointInTimeRequest.Builder()
                .keepAlive(new Time.Builder().time("30m").build()).build();
        return client.openPointInTime(request).toString();
    }

    public boolean closePointInTimeID(String pitId) throws IOException {
        ClosePointInTimeRequest closeRequest = new ClosePointInTimeRequest.Builder().id(pitId).build();
        return client.closePointInTime(closeRequest).succeeded();
    }
}
