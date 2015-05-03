package com.rhcloud.igorbotian.rsskit.rest.buffer;

import org.apache.http.NameValuePair;

import java.net.URL;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class BufferAPIImpl implements BufferAPI {

    private static final DateComparator DATE_COMPARATOR = new DateComparator();
    private static final AuthenticationEndpoint authentication = new AuthenticationEndpoint();
    private static final OAuthTokenEndpoint oAuthToken = new OAuthTokenEndpoint();
    private static final ProfilesEndpoint profiles = new ProfilesEndpoint();
    private static final UpdatesEndpoint updates = new UpdatesEndpoint();

    @Override
    public URL authorize(String clientID, URL redirectURI) throws BufferException {
        return authentication.authorize(clientID, redirectURI);
    }

    @Override
    public String getAccessToken(String clientID, String clientSecret, URL redirectURI,
                                 String authorizationCode) throws BufferException {

        return oAuthToken.getAccessToken(clientID, clientSecret, redirectURI, authorizationCode);
    }

    @Override
    public List<String> getProfilesIDs(String accessToken) throws BufferException {
        return profiles.getIDs(accessToken);
    }

    @Override
    public List<NameValuePair> getPendingUpdates(List<String> profileIDs, String accessToken, int count) throws BufferException {
        TreeMap<Date, NameValuePair> pendingUpdates = new TreeMap<>(DATE_COMPARATOR);

        for(String profileID : profileIDs) {
            pendingUpdates.putAll(updates.getPending(profileID, accessToken, count));
        }

        return truncate(pendingUpdates, count);
    }

    private static List<NameValuePair> truncate(TreeMap<Date, NameValuePair> src, int count) {
        assert src != null;
        assert count > 0;

        List<NameValuePair> truncated = new ArrayList<>(count);
        Iterator<Date> it = src.descendingKeySet().descendingIterator();

        for(int i = 0; i < count; i++) {
            if(it.hasNext()) {
                truncated.add(src.get(it.next()));
            }
        }

        return truncated;
    }

}
