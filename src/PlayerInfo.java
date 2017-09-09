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

        if (playerName.length() == 2) {
            this.playerName = playerName;
        } else {
            throw new RuntimeException("Wrong player Name, should be 2 character");
        }
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
