package dropservice.db;

import dropservice.core.Token;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

public class TokenDAO extends AbstractDAO<Token> {

	public TokenDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public Token addToken(Token token) {
		return persist(token);
	}
	
}
