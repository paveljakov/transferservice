package paveljakov.transfer;

public class Main {

    public static void main(final String[] args) {
        DaggerApplication
                .create()
                .appService()
                .start();
    }

}
