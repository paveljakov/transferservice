package paveljakov.transfer.config;

import lombok.Data;

@Data
public class Configuration {

    private final int serverPort;
    private final String jdbcDriver;
    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPasswd;

}
