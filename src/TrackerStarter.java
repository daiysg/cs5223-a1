import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class TrackerStarter {

    public static void main(String[] args) throws RemoteException, NotBoundException, AlreadyBoundException, MalformedURLException {

        // Get hostname and port (if given). Use defaults otherwise.
        int port = args.length > 0? Integer.parseInt(args[0]) : 1099;
        int n = args.length > 1 ? Integer.parseInt(args[0]) : 5;
        int k = args.length > 2 ? Integer.parseInt(args[1]) : 5;
        ITracker tracker =  new Tracker(port, n, k);
        createTracker(tracker);
        tracker.initGame(n, k);
    }

    private static void createTracker(ITracker tracker) throws RemoteException, NotBoundException, AlreadyBoundException, MalformedURLException {

        String url = new String("rmi://localhost:" + tracker.getPort() + "/tracker");
        Logging.printDebug("tracker binding url = " + url.toString());

        Naming.rebind(url, tracker);
        Logging.printInfo("Tracker is Created");
    }
}
