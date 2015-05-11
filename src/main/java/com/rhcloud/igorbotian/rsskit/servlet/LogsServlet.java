package com.rhcloud.igorbotian.rsskit.servlet;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class LogsServlet extends RssKitServlet {

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Objects.requireNonNull(req);
        Objects.requireNonNull(resp);

        resp.setCharacterEncoding(StandardCharsets.UTF_8.name().toLowerCase());
        resp.setContentType("text/plain");

        Path logFile = logFile();

        if(Files.exists(logFile)) {
            IOUtils.copy(Files.newInputStream(logFile()), resp.getOutputStream());
        } else {
            resp.getOutputStream().print("No logs");
        }
    }

    private Path logFile() {
        return Paths.get(tomcatPath().toAbsolutePath().toString(), "logs", "rsskit.log");
    }
}
