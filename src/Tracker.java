import java.util.List;
import java.util.Vector;

/**
 * Created by ydai on 9/9/17.
 */
public class Tracker {
    private List<IGame> serverList;

    protected Vector<IGame> servers;

    private Integer n;
    private Integer k;



    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getK() {
        return k;
    }

    public void setK(Integer k) {
        this.k = k;
    }
}
