public class User {
	/**
	 * 
	 * 用户信息类，里面设定了用户的各类属性
	 * 
	 */
	private int tUserID;
	private String username;
	private String password;
	private String nick_name;
	private int port;

	public User() {
	}

	public User(int tUserID, String username, String password, String nick_name) {
		this.tUserID = tUserID;
		this.username = username;
		this.password = password;
		this.nick_name = nick_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public int getTUserID() {
		return this.tUserID;
	}

	public boolean equals(Object obj) {
		if (obj instanceof User) {
			User u = (User) obj;
			if (u.getTUserID() == this.getTUserID()
					&& u.getUsername().equals(this.getUsername())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.tUserID;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
