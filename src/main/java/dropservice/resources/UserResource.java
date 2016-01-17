package dropservice.resources;

import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Signer;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenClaim;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import dropservice.core.Token;
import dropservice.core.User;
import dropservice.db.TokenDAO;
import dropservice.db.UserDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.hibernate.UnitOfWork;
import org.joda.time.DateTime;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private final UserDAO userDao;
	private final TokenDAO tokenDao;
	private final byte[] tokenSecret;

	public UserResource(UserDAO dao, TokenDAO tokenDao, byte[] tokenSecret) {
		this.userDao = dao;
		this.tokenDao = tokenDao;
		this.tokenSecret = tokenSecret;
	}

	@POST
	@UnitOfWork
	@Path("/createUser")
	public User createUser(@Auth User user) {
		return userDao.create(user);
	}

	@GET
	@UnitOfWork
	@Path("/findAll")
	public List<User> listPeople() {
		return userDao.findAll();
	}

	@GET
	@UnitOfWork
	@Path("/messager/{query}")
	public Map<String, String> message(@Auth @PathParam("query") String query) {
		return singletonMap("message", "Hello " + query);

	}

	@POST
	@UnitOfWork
	@Path("/loginPage")
	public Map<String, String> login(User user) throws AuthenticationException {
		String login = user.getLogin();
		String password = user.getPassword();
		User userFromDB = userDao.getUserByLogin(login).get(0);
		if (userFromDB.getPassword().equals(password) && userFromDB.getLogin().equals(login)) {
			final HmacSHA512Signer signer = new HmacSHA512Signer(tokenSecret);
			final JsonWebToken token = JsonWebToken.builder().header(JsonWebTokenHeader.HS512())
					.claim(JsonWebTokenClaim.builder().subject("isAuthorized").issuedAt(DateTime.now()).build())
					.build();
			final String signedToken = signer.sign(token);
			Token tokenDB = new Token(signedToken);
			tokenDB.setUser(userFromDB);
			tokenDao.addToken(tokenDB);
			userFromDB.setToken(tokenDB);
			return singletonMap("token", signedToken);
		}
		throw new AuthenticationException("Your Credentials Is Not Appropriate");
	}
}