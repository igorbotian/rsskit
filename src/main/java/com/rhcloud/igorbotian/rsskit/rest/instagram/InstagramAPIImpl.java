package com.rhcloud.igorbotian.rsskit.rest.instagram;

import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramAPIImpl implements InstagramAPI {

    private final AuthenticationEndpoint authentication = new AuthenticationEndpoint();
    private final UsersEndpoints users = new UsersEndpoints();

    @Override
    public URL getAuthorizationURL(String clientID, URL callbackURL) throws InstagramException {
        Objects.requireNonNull(clientID);
        Objects.requireNonNull(callbackURL);

        return authentication.getAuthorizationURL(clientID, callbackURL);
    }

    @Override
    public String requestAccessToken(String clientID, String clientSecret, String authorizationCode, URL callbackURL)
            throws InstagramException {

        Objects.requireNonNull(clientID);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(authorizationCode);
        Objects.requireNonNull(callbackURL);

        return authentication.requestAccessToken(clientID, clientSecret, authorizationCode, callbackURL);
    }

    @Override
    public InstagramFeed getSelfFeed(String accessToken) throws InstagramException {
        Objects.requireNonNull(accessToken);

        return users.getSelfFeed(accessToken);
    }
}
