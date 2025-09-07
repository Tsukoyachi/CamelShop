package tsukoyachi.camelshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EntityScan(basePackages = "tsukoyachi.camelshop.entity")
@ComponentScan(basePackages = "tsukoyachi.camelshop.repository")
public class SqliteConfig {

    @Value("${camelshop.datasource.url}")
    private String databaseUrl;

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.sqlite.JDBC")
                .url(databaseUrl)
                .build();
    }

    @Bean
    @ConfigurationProperties("spring.jpa")
    public Properties jpaProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        properties.setProperty("hibernate.ddl-auto", "update");
        return properties;
    }
}
