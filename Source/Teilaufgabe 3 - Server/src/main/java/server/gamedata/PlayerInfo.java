package server.gamedata;

public class PlayerInfo {

	private final String playerId;
	private final String firstName;
	private final String lastName;
	private final String uaccount;

	private EPlayerStatus actFlag;
	private boolean alreadyDeliveredMap;
	private boolean treasureInventory;

	public PlayerInfo(String playerId, String firstName, String lastName, String uaccount) {
		this.playerId = playerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.uaccount = uaccount;
		this.actFlag = EPlayerStatus.MustWait;
		this.treasureInventory = false;
		this.alreadyDeliveredMap = false;
	}

	public String getPlayerId() {
		return playerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getUaccount() {
		return uaccount;
	}

	public String getLastName() {
		return lastName;
	}

	public boolean hasAlreadyDeliveredMap() {
		return alreadyDeliveredMap;
	}

	public void setAlreadyDeliveredMap(boolean alreadyDeliveredMap) {
		this.alreadyDeliveredMap = alreadyDeliveredMap;
	}

	public EPlayerStatus getActFlag() {
		return actFlag;
	}

	public void setActFlag(EPlayerStatus actFlag) {
		this.actFlag = actFlag;
	}

	public boolean isTreasureInventory() {
		return treasureInventory;
	}

	public void setTreasureInventory(boolean treasureInventory) {
		this.treasureInventory = treasureInventory;
	}
}
