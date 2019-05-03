package paveljakov.transfer;

import static paveljakov.transfer.DaggerApplication.builder;

import paveljakov.transfer.config.Configuration;

public class Main {

    public static void main(final String[] args) {
        final Configuration cfg = new Configuration(
                8080,
                "org.h2.Driver",
                "jdbc:h2:mem:transferdb",
                "sa",
                null
        );

        final Application app = builder()
                .configuration(cfg)
                .build();

        app.appService().start();
    }

}
