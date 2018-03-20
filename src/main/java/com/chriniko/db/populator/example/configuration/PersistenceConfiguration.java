package com.chriniko.db.populator.example.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Configuration


@EnableTransactionManagement

public class PersistenceConfiguration {

    // Note: not good coding practise.
    public static String CONFIG_FILE_NAME = null;

    @Lazy
    @Bean
    public Properties properties() throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(Paths.get(CONFIG_FILE_NAME))) {

            properties.load(inputStream);

            return properties;
        }
    }

    @Lazy
    @Bean
    public DataSource dataSource(Properties properties) {

        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setUsername(properties.getProperty("jdbc.user"));
        hikariDataSource.setPassword(properties.getProperty("jdbc.pass"));
        hikariDataSource.setJdbcUrl(properties.getProperty("jdbc.url"));
        hikariDataSource.setDriverClassName(properties.getProperty("jdbc.driverClassName"));

        return hikariDataSource;
    }


    @Lazy
    @Bean
    public LocalSessionFactoryBean sessionFactory(Properties properties) {

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        sessionFactory.setDataSource(dataSource(properties));

        sessionFactory.setPackagesToScan("com.chriniko.db.populator.example.domain");

        sessionFactory.setHibernateProperties(hibernateProperties(properties));

        return sessionFactory;
    }

    @Lazy
    @Bean
    public PlatformTransactionManager transactionManager(Properties properties) {

        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();

        hibernateTransactionManager.setSessionFactory(sessionFactory(properties).getObject());

        hibernateTransactionManager.setEntityInterceptor(new EmptyInterceptor() {

            @Override
            public void afterTransactionBegin(Transaction tx) {
                //System.out.println("EmptyInterceptor#afterTransactionBegin --- transaction status = " + tx.getStatus().name());
            }

            @Override
            public void beforeTransactionCompletion(Transaction tx) {
                //System.out.println("EmptyInterceptor#beforeTransactionCompletion --- transaction status = " + tx.getStatus().name());
            }

            @Override
            public void afterTransactionCompletion(Transaction tx) {
                //System.out.println("EmptyInterceptor#afterTransactionBegin --- transaction status = " + tx.getStatus().name());
            }

        });

        return hibernateTransactionManager;
    }


    @Lazy
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties hibernateProperties(Properties properties) {
        return new Properties() {
            {
                setProperty("hibernate.hbm2ddl.auto", properties.getProperty("hibernate.hbm2ddl.auto"));
                setProperty("hibernate.dialect", properties.getProperty("hibernate.dialect"));
                setProperty("hibernate.show_sql", properties.getProperty("hibernate.show_sql"));
                setProperty("hibernate.globally_quoted_identifiers", "true");
            }
        };
    }

}
