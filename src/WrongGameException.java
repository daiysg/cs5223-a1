public class WrongGameException extends Exception {

    private String message;

    WrongGameException(String message) {
        this.message = message;
    }
}
