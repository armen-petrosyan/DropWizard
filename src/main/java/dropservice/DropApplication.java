package dropservice;

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import dropservice.auth.AuthorizationCl;
import dropservice.core.Token;
import dropservice.core.User;
import dropservice.db.TokenDAO;
import dropservice.db.UserDAO;
import dropservice.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.EnumSet;

public class DropApplication extends Application<DropConfiguration> {

    public static void main(String[] args) throws Exception {
        new DropApplication().run(args);
    }

    private final HibernateBundle<DropConfiguration> hibernate = new HibernateBundle<DropConfiguration>(User.class,
            Token.class) {
        public DataSourceFactory getDataSourceFactory(DropConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public void initialize(Bootstrap<DropConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(DropConfiguration config, Environment environment)
            throws ClassNotFoundException, UnsupportedEncodingException {
        final FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders",
                "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");

        final UserDAO userDAO = new UserDAO(hibernate.getSessionFactory());
        final TokenDAO tokenDao = new TokenDAO(hibernate.getSessionFactory());
        final JsonWebTokenParser tokenParser = new DefaultJsonWebTokenParser();
        final HmacSHA512Verifier tokenVerifier = new HmacSHA512Verifier(config.getJwtTokenSecret());
        environment.jersey()
                .register(new AuthDynamicFeature(new JWTAuthFilter.Builder<>().setTokenParser(tokenParser)
                        .setTokenVerifier(tokenVerifier).setRealm("realm").setPrefix("Bearer")
                        .setAuthenticator(new AuthorizationCl()).buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Principal.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new UserResource(userDAO, tokenDao, config.getJwtTokenSecret()));
    }

}