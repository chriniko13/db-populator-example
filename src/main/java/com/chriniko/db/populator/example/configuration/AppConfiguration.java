package com.chriniko.db.populator.example.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration

@PropertySource("classpath:application.properties")

@ComponentScan("com.chriniko.db.populator.example")

public class AppConfiguration {

}
