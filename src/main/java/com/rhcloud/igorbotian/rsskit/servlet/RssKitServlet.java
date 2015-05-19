package com.rhcloud.igorbotian.rsskit.servlet;

import com.j256.ormlite.db.H2DatabaseType;
import com.rhcloud.igorbotian.rsskit.db.RsskitDataSource;
import com.rhcloud.igorbotian.rsskit.utils.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class RssKitServlet extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(RssKitServlet.class);
    private RsskitDataSource dataSource;

    public static void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    @Override
    public void init() throws ServletException {
        try {
            enableSSLSocket();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new ServletException("Failed to enable SSL socket factory", e);
        }

        if (!Configuration.isSuccessfullyLoaded()) {
            LOGGER.fatal("Configuration file is not found!");
            throw new ServletException(Configuration.FILE + " configuration file is not found!");
        }

        String dbURL = String.format("jdbc:h2:%s/db/rsskit", tomcatPath());
        dataSource = new RsskitDataSource(dbURL, "sa", "sa", new H2DatabaseType());
    }

    @Override
    public void destroy() {
        try {
            dataSource.get().close();
        } catch (SQLException e) {
            LOGGER.error("Failed to stop database", e);
        } finally {
            super.destroy();
        }
    }

    protected RsskitDataSource dataSource() throws SQLException {
        return dataSource;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            LOGGER.error("Failed to process request", e);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            LOGGER.error("Failed to process request", e);
        }
    }

    protected Path tomcatPath() {
        return Paths.get(System.getProperty("catalina.home"));
    }

    protected void respond(byte[] data, String contentType, Charset encoding, HttpServletResponse response)
            throws IOException {

        assert data != null;
        assert contentType != null;
        assert encoding != null;
        assert response != null;

        sendHeader(data, contentType, encoding, response);
        sendBody(data, response);
    }

    private void sendHeader(byte[] data, String contentType, Charset encoding, HttpServletResponse response) throws IOException {
        assert data != null;
        assert contentType != null;
        assert response != null;

        response.setStatus(200); // OK
        response.setCharacterEncoding(encoding != null ? encoding.name().toLowerCase() : null);
        response.setContentType(contentType);
        response.setContentLength(data.length);
    }

    private void sendBody(byte[] data, HttpServletResponse response) throws IOException {
        assert response != null;

        try (InputStream is = new ByteArrayInputStream(data)) {
            try (OutputStream os = response.getOutputStream()) {
                IOUtils.copy(is, os);
            }
        }
    }

    protected abstract void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException;
}
