package com.kbb.analytics;

import com.kbb.analytics.db.WishDAO;
import com.kbb.analytics.resource.WishResource;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import java.util.EnumSet;
import jakarta.servlet.FilterRegistration;

public class AnalyticsApp extends Application<AnalyticsConfig> {
    public static void main(String[] args) throws Exception {
        new AnalyticsApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<AnalyticsConfig> bootstrap) {
    }

    @Override
    public void run(AnalyticsConfig config, Environment env) throws Exception {
        AnalyticsConfig.DbConfig db = config.getDb();
        WishDAO dao = new WishDAO(db.url, db.user, db.password);
        dao.createTable();
        env.jersey().register(new WishResource(dao));

        FilterRegistration.Dynamic cors = env.servlets().addFilter("CORS", CrossOriginFilter.class);

        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM,
                "http://localhost:3000,http://127.0.0.1:3000,http://127.0.0.1:5500,http://localhost:5500");

        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,PUT,DELETE,OPTIONS");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Content-Type,Accept,Origin,Authorization,X-Requested-With");

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
