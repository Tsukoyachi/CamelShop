package tsukoyachi.camelshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EntityScan(basePackages = "tsukoyachi.camelshop.entity")
@ComponentScan(basePackages = "tsukoyachi.camelshop.repository")
@EnableJpaRepositories(basePackages = "tsukoyachi.camelshop.repository")
public class SqliteConfig {

    @Value("${camelshop.datasource.url}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.sqlite.JDBC")
                .url(databaseUrl)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, DataSource dataSource) {
        Map<String, String> properties = Map.of(
                "hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect",
                "hibernate.hbm2ddl.auto", "update",
                "hibernate.show_sql", "true"
        );

        return builder
                .dataSource(dataSource)
                .packages("tsukoyachi.camelshop.entity")
                .properties(properties)
                .build();
    }
}
