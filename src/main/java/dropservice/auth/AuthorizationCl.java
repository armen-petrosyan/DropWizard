package dropservice.auth;

import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenValidator;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import io.dropwizard.auth.Authenticator;

import java.security.Principal;

public class AuthorizationCl implements Authenticator<JsonWebToken, Principal> {

    public Optional<Principal> authenticate(JsonWebToken token) {
        final JsonWebTokenValidator expiryValidator = new ExpiryValidator();
        expiryValidator.validate(token);
        if ("isAuthorized".equals(token.claim().subject())) {
            final Principal principal = new Principal() {
                public String getName() {
                    return "isAuthorized";
                }
            };
            return Optional.of(principal);
        }
        return Optional.absent();
    }
}