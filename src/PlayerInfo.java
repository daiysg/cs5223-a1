/**
 * Created by ydai on 9/9/17.
 */
public class PlayerInfo {

    private String playerName;

    private Position position;

    private Integer treasuryAcquired;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Integer getTreasuryAcquired() {
        return treasuryAcquired;
    }

    public void setTreasuryAcquired(Integer treasuryAcquired) {
        this.treasuryAcquired = treasuryAcquired;
    }
}
