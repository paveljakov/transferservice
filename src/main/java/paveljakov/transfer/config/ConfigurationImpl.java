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

}
