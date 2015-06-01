package com.rhcloud.igorbotian.rsskit.rest.meduza.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaDocument;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaException;
import com.rhcloud.igorbotian.rsskit.rest.meduza.json.MeduzaDocumentParser;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class DocumentEndpoint extends MeduzaEndpoint {

    private static final String ENDPOINT = "/";
    private static final MeduzaDocumentParser PARSER = new MeduzaDocumentParser();

    public MeduzaDocument get(String url) throws MeduzaException {
        Objects.requireNonNull(url);

        try {
            JsonNode response = makeRequest(ENDPOINT + url, Collections.<NameValuePair>emptySet());
            response = MeduzaResponse.parse(response, "root");
            return PARSER.parse(response);
        } catch (IOException e) {
            throw new MeduzaException("Failed to obtain a specified document from Meduza: " + url, e);
        } catch (RestParseException e) {
            throw new MeduzaException("Unexpected response received from Meduza", e);
        }
    }
}
