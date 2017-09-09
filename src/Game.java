/**
 * Created by ydai on 9/9/17.
 */
public class Game {

    private PlayerInfo playerInfo;
    private PlayerStatus playerStatus;

    private String masterPlayerName;
    private String slavePlayerName;

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public String getMasterPlayerName() {
        return masterPlayerName;
    }

    public void setMasterPlayerName(String masterPlayerName) {
        this.masterPlayerName = masterPlayerName;
    }

    public String getSlavePlayerName() {
        return slavePlayerName;
    }

    public void setSlavePlayerName(String slavePlayerName) {
        this.slavePlayerName = slavePlayerName;
    }
}
