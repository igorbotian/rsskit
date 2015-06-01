package com.rhcloud.igorbotian.rsskit.rest.meduza.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaException;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaIndex;
import com.rhcloud.igorbotian.rsskit.rest.meduza.json.MeduzaIndexParser;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.util.Collections;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class IndexEndpoint extends MeduzaEndpoint {

    private static final String ENDPOINT = "index";
    private static final MeduzaIndexParser PARSER = new MeduzaIndexParser();

    public MeduzaIndex get() throws MeduzaException {
        try {
            JsonNode response = makeRequest(ENDPOINT, Collections.<NameValuePair>emptySet());
            return PARSER.parse(response);
        } catch (IOException e) {
            throw new MeduzaException("Failed to obtain news summary from Meduza", e);
        } catch (RestParseException e) {
            throw new MeduzaException("Unexpected response received from Meduza", e);
        }
    }
}
