package dk.cphbusiness.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import dk.cphbusiness.data.ISecurityDAO;
import dk.cphbusiness.data.User;
import dk.cphbusiness.data.UserDAO;
import io.javalin.http.Handler;

import java.util.Date;


public class SecurityController implements ISecurity {
    ISecurityDAO securityDAO = UserDAO.getInstance();
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Handler login() {
        return ctx -> {
            User user = ctx.bodyAsClass(User.class);
            String token = getToken(user.getUsername(), user.getPassword());
            ObjectNode on = objectMapper.createObjectNode().put("token", token).put("username", user.getUsername());
            ctx.json(on);
        };
    }

    private String getToken(String username, String password) throws Exception {
        if (securityDAO.authenticateUser(username, password)) {
            User found = securityDAO.getUser(username);
            try {
                return createToken(found);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Could not create token");
            }
        }
        throw new Exception("Wrong username or password");
    }

    private String createToken(User user) throws Exception {
        String ISSUER;
        String TOKEN_EXPIRE_TIME;
        String SECRET_KEY;

        boolean isDeployed = (System.getenv("DEPLOYED") != null);

        if (isDeployed) {
            ISSUER = System.getenv("ISSUER");
            TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
            SECRET_KEY = System.getenv("SECRET_KEY");
        } else {
            ISSUER = "Thomas Hartmann";
            TOKEN_EXPIRE_TIME = "1800000"; // 30 min
            SECRET_KEY = "AAAAAAAAAAAAABBBBBBBBBBBBCCCCCCCCCCCCCCC";
        }
        try {
            // https://codecurated.com/blog/introduction-to-jwt-jws-jwe-jwa-jwk/

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer(ISSUER)
                    .claim("username", user.getUsername())
                    .claim("roles", user.getRolesAsString())
                    .expirationTime(new Date(new Date().getTime() + Integer.parseInt(TOKEN_EXPIRE_TIME)))
                    .build();
            Payload payload = new Payload(claimsSet.toJSONObject());

            JWSSigner signer = new MACSigner(SECRET_KEY);
            JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            jwsObject.sign(signer);
            return jwsObject.serialize();

        } catch (JOSEException e) {
            e.printStackTrace();
            throw new Exception("Could not create token");
        }
    }
}

