package paveljakov.transfer;

public class App {

    public static void main(final String[] args) {
        DaggerAppService
                .create()
                .restService()
                .start();
    }

}
