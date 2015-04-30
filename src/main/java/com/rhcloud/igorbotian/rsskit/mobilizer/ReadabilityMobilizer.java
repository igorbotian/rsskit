package com.rhcloud.igorbotian.rsskit.mobilizer;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ReadabilityMobilizer implements Mobilizer {

    @Override
    public String mobilize(URL url) throws IOException {
        Objects.requireNonNull(url);
        return IOUtils.toString(url);
    }
}
