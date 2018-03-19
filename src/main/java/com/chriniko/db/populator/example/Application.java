package com.chriniko.db.populator.example;

import com.chriniko.db.populator.example.configuration.AppConfiguration;
import com.chriniko.db.populator.example.configuration.PersistenceConfiguration;
import com.chriniko.db.populator.example.runner.PopulatorRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Application {

    public static void main(String[] args) throws IOException {

        final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                AppConfiguration.class,
                PersistenceConfiguration.class);

        DbPopulatorProperties dbPopulatorProperties;

        if (args.length != 1) {

            try (InputStream inputStream = Application.class.getClassLoader().getResourceAsStream("application.properties")) {
                dbPopulatorProperties = loadProperties(inputStream);
            }

        } else {

            try (InputStream inputStream = Files.newInputStream(Paths.get(args[0]))) {
                dbPopulatorProperties = loadProperties(inputStream);
            }
        }

        applicationContext
                .getBean(PopulatorRunner.class)
                .run(dbPopulatorProperties.getConcurrency(),
                        dbPopulatorProperties.getTrafficTarget(),
                        dbPopulatorProperties.getDuration(),
                        dbPopulatorProperties.isEqualDistribution()
                );

    }

    private static DbPopulatorProperties loadProperties(InputStream inputStream) throws IOException {

        Properties properties = new Properties();
        properties.load(inputStream);

        return new DbPopulatorProperties(
                Integer.parseInt(properties.getProperty("dbpopulator.concurrency")),
                Integer.parseInt(properties.getProperty("dbpopulator.trafficTarget")),
                Integer.parseInt(properties.getProperty("dbpopulator.duration")),
                Boolean.parseBoolean(properties.getProperty("dbpopulator.equal.distribution"))
        );
    }


    static class DbPopulatorProperties {

        private final int concurrency;
        private final int trafficTarget;
        private final int duration;
        private final boolean equalDistribution;

        DbPopulatorProperties(int concurrency, int trafficTarget, int duration, boolean equalDistribution) {
            this.concurrency = concurrency;
            this.trafficTarget = trafficTarget;
            this.duration = duration;
            this.equalDistribution = equalDistribution;
        }

        int getConcurrency() {
            return concurrency;
        }

        int getTrafficTarget() {
            return trafficTarget;
        }


        int getDuration() {
            return duration;
        }

        boolean isEqualDistribution() {
            return equalDistribution;
        }

    }
}
