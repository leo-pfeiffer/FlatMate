package cs5031.groupc.practical3.database;

import cs5031.groupc.practical3.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:persistence.properties")
@ComponentScan({"cs5031.groupc.practical3.*"})
@Import({SecurityConfiguration.class})
public class SQLiteDataSource {

    @Autowired
    Environment env;

    public SQLiteDataSource(Environment env) {
        this.env = env;
    }

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String driverClass = env.getProperty("driverClassName");
        assert driverClass != null;
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(env.getProperty("url"));
        // dataSource.setUsername(env.getProperty("user"));
        // dataSource.setPassword(env.getProperty("password"));
        return dataSource;
    }
}

