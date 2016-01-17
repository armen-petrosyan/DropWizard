package dropservice.db;

import dropservice.core.User;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class UserDAO extends AbstractDAO<User> {

	public UserDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public List<User> findAll() {
		return list(namedQuery("drop.core.User.findAll"));
	}

	public User create(User user) {
		return persist(user);
	}

	public List<User> getUserByLogin(String login) {
		return list(namedQuery("drop.core.User.findByLogin").setParameter("login", login));
	}
}
