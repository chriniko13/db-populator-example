package com.chriniko.db.populator.example.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration


@EnableTransactionManagement

public class PersistenceConfiguration implements TransactionManagementConfigurer {

    @Autowired
    private Environment environment;

    @Bean
    public DataSource dataSource() {

        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setUsername(environment.getProperty("jdbc.user"));
        hikariDataSource.setPassword(environment.getProperty("jdbc.pass"));
        hikariDataSource.setJdbcUrl(environment.getProperty("jdbc.url"));
        hikariDataSource.setDriverClassName(environment.getProperty("jdbc.driverClassName"));

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
                setProperty("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
                setProperty("hibernate.dialect", environment.getProperty("hibernate.dialect"));
                setProperty("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
                setProperty("hibernate.globally_quoted_identifiers", "true");
            }
        };
    }

    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager();
    }
}
