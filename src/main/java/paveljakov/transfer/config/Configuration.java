package paveljakov.transfer.config;

public interface Configuration {

    int getServerPort();

    String getJdbcDriver();

    String getJdbcUrl();

    String getJdbcUser();

    String getJdbcPasswd();

}
