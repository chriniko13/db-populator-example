package com.chriniko.db.populator.example.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Configuration


@EnableTransactionManagement

public class PersistenceConfiguration implements TransactionManagementConfigurer {


    static Properties PROPERTIES;

    static {
        PROPERTIES = new Properties();

        try (InputStream inputStream = Files.newInputStream(Paths.get("configFile.properties"))) {

            PROPERTIES.load(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Bean
    public DataSource dataSource() {

        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setUsername(PROPERTIES.getProperty("jdbc.user"));
        hikariDataSource.setPassword(PROPERTIES.getProperty("jdbc.pass"));
        hikariDataSource.setJdbcUrl(PROPERTIES.getProperty("jdbc.url"));
        hikariDataSource.setDriverClassName(PROPERTIES.getProperty("jdbc.driverClassName"));

        return hikariDataSource;
    }


    @Bean
    public LocalSessionFactoryBean sessionFactory() {

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        sessionFactory.setDataSource(dataSource());

        sessionFactory.setPackagesToScan("com.chriniko.db.populator.example.domain");

        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {

        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();

        hibernateTransactionManager.setSessionFactory(sessionFactory().getObject());

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


    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties hibernateProperties() {
        return new Properties() {
            {
                setProperty("hibernate.hbm2ddl.auto", PROPERTIES.getProperty("hibernate.hbm2ddl.auto"));
                setProperty("hibernate.dialect", PROPERTIES.getProperty("hibernate.dialect"));
                setProperty("hibernate.show_sql", PROPERTIES.getProperty("hibernate.show_sql"));
                setProperty("hibernate.globally_quoted_identifiers", "true");
            }
        };
    }

    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager();
    }
}
