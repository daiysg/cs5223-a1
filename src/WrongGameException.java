/**
 * Created by ydai on 23/9/17.
 */
public class WrongGameException extends Exception {

    private String message;

    WrongGameException(String message) {
        this.message = message;
    }
}
