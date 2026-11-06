package com.star.bank;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.star.bank.repository.jpa", // Пакет, где будет лежать RuleRepository
        entityManagerFactoryRef = "rulesEntityManagerFactory",
        transactionManagerRef = "rulesTransactionManager"
)
public class DataSourceConfig {

    // === 1. Основная база (H2, READ-ONLY) ===
    @Primary
    @Bean(name = "defaultDataSource")
    @ConfigurationProperties("spring.datasource")
    public DataSource defaultDataSource() {
        return DataSourceBuilder.create().build();
    }

    // === 2. Вторая база (PostgreSQL, для правил) ===
    @Bean(name = "rulesDataSource")
    @ConfigurationProperties("rules.datasource")
    public DataSource rulesDataSource() {
        return DataSourceBuilder.create().build();
    }

    // === 3. Настройка JPA для второй базы (PostgreSQL) ===
    @Bean(name = "rulesEntityManagerFactory")
    @DependsOn("rulesLiquibase")
    public LocalContainerEntityManagerFactoryBean rulesEntityManagerFactory(
            @Qualifier("rulesDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.star.bank.entity"); // Пакет, где будут JPA-сущности (RuleEntity, QueryEntity)
        em.setPersistenceUnitName("rules");

        // Указываем Hibernate как провайдер JPA
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        // Дополнительные свойства Hibernate
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "validate"); // Таблицы создаём только через Liquibase
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "rulesTransactionManager")
    public PlatformTransactionManager rulesTransactionManager(
            @Qualifier("rulesEntityManagerFactory") LocalContainerEntityManagerFactoryBean rulesEntityManagerFactory) {
        return new JpaTransactionManager(rulesEntityManagerFactory.getObject());
    }

    @Bean(name = "rulesLiquibase")
    public SpringLiquibase rulesLiquibase(@Qualifier("rulesDataSource") DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:changelog-rules.xml");
        return liquibase;
    }

    // === 4. Явно указываем JdbcTemplate, какую базу использовать ===
    @Bean
    public JdbcTemplate jdbcTemplate(
            @Qualifier("defaultDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
