package com.chriniko.db.populator.example;

import com.chriniko.db.populator.example.configuration.AppConfiguration;
import com.chriniko.db.populator.example.configuration.PersistenceConfiguration;
import com.chriniko.db.populator.example.runner.PopulatorRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {

    public static void main(String[] args) {


        final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(
                AppConfiguration.class,
                PersistenceConfiguration.class
        );


        PopulatorRunner populatorRunner = applicationContext.getBean(PopulatorRunner.class);
        populatorRunner.run();

    }
}
