import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TrackerStarter {

    public static void main(String[] args) throws RemoteException, NotBoundException, AlreadyBoundException {

        // Get hostname and port (if given). Use defaults otherwise.
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        int k = args.length > 1 ? Integer.parseInt(args[1]) : 5;
        Tracker tracker =  new Tracker(n, k);
        createTracker(tracker);
    }

    private static void createTracker(Tracker tracker) throws RemoteException, NotBoundException, AlreadyBoundException {
        System.setProperty(
                "java.rmi.server.codebase",
                Tracker.class.getProtectionDomain().getCodeSource().getLocation().toString());
        Registry registry = LocateRegistry.getRegistry();
        try {
            registry.bind("tracker", tracker);
        } catch (AlreadyBoundException e) {
            registry.unbind("tracker");
            registry.bind("tracker", tracker);
        }
        Logging.printInfo("Tracker is Created");
    }
}
