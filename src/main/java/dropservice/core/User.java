package dropservice.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity	
@Table(name = "user")
@NamedQueries({ @NamedQuery(name = "drop.core.User.findAll", query = "SELECT u FROM User u"), 
				@NamedQuery(name = "drop.core.User.findByLogin", query = "SELECT u FROM User u WHERE u.login= :login")})
public class User {

	@Id
	@Column(name = "login", nullable = false)
	String login;

	@Column(name = "password", nullable = false)
	String password;

	@JsonIgnore
	@OneToOne
	Token token;

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public User() {
	}

	public User(String login, String password) {
		this.login = login;
		this.password = password;
	}
	
	

}
