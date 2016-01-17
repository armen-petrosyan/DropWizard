package dropservice.core;

import javax.persistence.*;

@Entity
@Table(name = "token")
public class Token {

	@Id
	@Column(name = "token", nullable = false)
	String token;

	@OneToOne
	User user;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Token() {
	}

	public Token(String token) {
		this.token = token;
	}
}