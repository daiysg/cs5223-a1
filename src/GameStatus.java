import java.util.List;
import java.util.Set;

/**
 * Created by ydai on 9/9/17.
 */
public class GameStatus {

    private Integer n;
    private Integer k;

    private Set<Position> treasurePlaces;
    private List<PlayerInfo> playerInfoList;

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

    public Set<Position> getTreasurePlaces() {
        return treasurePlaces;
    }

    public void setTreasurePlaces(Set<Position> treasurePlaces) {
        this.treasurePlaces = treasurePlaces;
    }

    public List<PlayerInfo> getPlayerInfoList() {
        return  playerInfoList;
    }

    public void setPlayerInfoList(List<PlayerInfo> playerInfoList) {
        this. playerInfoList = playerInfoList;
    }
}
