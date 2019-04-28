package paveljakov.transfer.config;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ConfigurationImpl implements Configuration {

    @Inject
    public ConfigurationImpl() {
    }

    @Override
    public int getServerPort() {
        return 8080;
    }

    @Override
    public String getJdbcDriver() {
        return "org.h2.Driver";
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:h2:mem:transferdb";
    }

    @Override
    public String getJdbcUser() {
        return "sa";
    }

    @Override
    public String getJdbcPasswd() {
        return null;
    }

}
