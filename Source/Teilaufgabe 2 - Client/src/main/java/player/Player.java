package player;

public class Player {

	private String playerId;
	private String firstName;
	private String lastName;
	private String uaccount;

	public Player(String firstName, String lastName, String uaccount) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.uaccount = uaccount;
	}

	public Player(String playerId, String firstName, String lastName, String uaccount) {
		this.playerId = playerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.uaccount = uaccount;
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

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setUaccount(String uaccount) {
		this.uaccount = uaccount;
	}

}
