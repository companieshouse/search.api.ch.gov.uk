package uk.gov.companieshouse.search.api.model.response;

import com.google.gson.Gson;
import uk.gov.companieshouse.search.api.model.DissolvedSearchResults;

public class DissolvedResponseObject {

    private ResponseStatus status;

    private DissolvedSearchResults dissolvedSearchResults;

    public DissolvedResponseObject(ResponseStatus status) {
        this.status = status;
    }

    public DissolvedResponseObject(ResponseStatus status, DissolvedSearchResults dissolvedSearchResults) {
        this.status = status;
        this.dissolvedSearchResults = dissolvedSearchResults;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public DissolvedSearchResults getData() {
        return dissolvedSearchResults;
    }

    public void setData(DissolvedSearchResults dissolvedSearchResults) {
        this.dissolvedSearchResults = dissolvedSearchResults;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
