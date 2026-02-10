package com.example.myapp;

import com.example.myapp.core.User;
import com.example.myapp.db.UserDAO;
import com.example.myapp.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class RestAppApplication extends Application<RestAppConfiguration> {

    private final HibernateBundle<RestAppConfiguration> hibernate = new HibernateBundle<RestAppConfiguration>(User.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(RestAppConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(final String[] args) throws Exception {
        new RestAppApplication().run(args);
    }

    @Override
    public void initialize(final Bootstrap<RestAppConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));
    }

    @Override
    public void run(final RestAppConfiguration configuration, final Environment environment) {
        final UserDAO dao = new UserDAO(hibernate.getSessionFactory());
        environment.jersey().register(new UserResource(dao));
    }
}