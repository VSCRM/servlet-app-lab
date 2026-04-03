package com.kbb.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

public class AnalyticsConfig extends Configuration {

    @JsonProperty("db")
    private DbConfig db = new DbConfig();

    public DbConfig getDb() { return db; }

    public static class DbConfig {
        public String url;
        public String user;
        public String password;
    }
}
