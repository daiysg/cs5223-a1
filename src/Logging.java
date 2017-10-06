public class Logging {

    public static void printInfo(String message) {
        System.out.println("INFO:: " + message);
    }

    public static void printError(String message) {
        System.out.println("ERROR:: " + message);
    }

    public static void printDebug(String message){
        //System.out.println ("DEBUG:: " + message);
    }

    public static void printException (Exception e) {
        //System.out.println("=== EXCEPTION === :: " + e);
    }
}
