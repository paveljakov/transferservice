package paveljakov.transfer.persistence;

import javax.inject.Singleton;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dagger.Module;
import dagger.Provides;
import paveljakov.transfer.config.Configuration;

@Module
public class PersistenceModule {

    @Provides
    @Singleton
    DataSource provideDataSource(final Configuration configuration) {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(configuration.getJdbcDriver());
        hikariConfig.setJdbcUrl(configuration.getJdbcUrl());
        hikariConfig.setUsername(configuration.getJdbcUser());
        hikariConfig.setPassword(configuration.getJdbcPasswd());

        return new HikariDataSource(hikariConfig);
    }

    @Provides
    @Singleton
    Flyway provideFlyway(final DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .load();
    }

}
