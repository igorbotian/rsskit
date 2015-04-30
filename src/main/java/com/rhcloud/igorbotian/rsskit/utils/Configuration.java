package com.rhcloud.igorbotian.rsskit.utils;

import java.io.IOException;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public final class Configuration {

    public static final String FILE = "rsskit.properties";

    private static final boolean successfullyLoaded;
    private static final Map<String, String> props;

    static {
        boolean loaded = false;
        Properties properties = new Properties();
        Map<String, String> entries = new HashMap<>();

        try {
            properties.load(Configuration.class.getResourceAsStream(FILE));

            for (Object name : properties.keySet()) {
                entries.put(name.toString(), properties.getProperty(name.toString()));
            }

            loaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        successfullyLoaded = loaded;
        props = Collections.unmodifiableMap(entries);
    }

    private Configuration() {
        //
    }

    public static boolean isSuccessfullyLoaded() {
        return successfullyLoaded;
    }

    public static String getProperty(String name) {
        return props.get(Objects.requireNonNull(name));
    }
}
