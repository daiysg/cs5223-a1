import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class TrackerStarter {

    public static void main(String[] args) throws RemoteException, NotBoundException, AlreadyBoundException, MalformedURLException {

        // Get hostname and port (if given). Use defaults otherwise.
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        int k = args.length > 1 ? Integer.parseInt(args[1]) : 5;
        ITracker tracker =  new Tracker(n, k);
        createTracker(tracker);
        tracker.initGame(n, k);
    }

    private static void createTracker(ITracker tracker) throws RemoteException, NotBoundException, AlreadyBoundException, MalformedURLException {
        Naming.rebind("tracker", tracker);
        Logging.printInfo("Tracker is Created");
    }
}
