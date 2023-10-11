package dk.cphbusiness.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dk.cphbusiness.data.ISecurityDAO;
import dk.cphbusiness.data.User;
import dk.cphbusiness.data.UserDAO;
import io.javalin.http.Handler;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import dk.cphbusiness.data.Role;


public class SecurityController implements ISecurity {
    private String dev_secret_key = "AAAAAAAAAAAAABBBBBBBBBBBBCCCCCCCCCCCCCCC";
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

    @Override
    public Handler authorize() {
        // Purpose: before filter -> Check for Authorization header, find user inside the token, forward the ctx object with username on attribute
        ObjectNode returnObject = objectMapper.createObjectNode();
        return (ctx) -> {
            try {
                String header = ctx.header("Authorization");
                if(header == null)
                    ctx.status(401).json(returnObject.put("msg","Authorization header missing"));
                String token = header.split(" ")[1];
                if (token == null) {
                    ctx.status(401).json(returnObject.put("msg","Authorization header malformed"));
                }
                User verifiedTokenUserEntity = verifyToken(token);
                if (verifiedTokenUserEntity == null) {
                    ctx.status(401).json(returnObject.put("msg","Invalid User or Token"));
                }
                String userName = verifiedTokenUserEntity.getUsername();
                ctx.attribute("userName", userName);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Cannot authenticate");
            }
        };
    }

    private User verifyToken(String token) throws Exception {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            boolean isDeployed = (System.getenv("DEPLOYED") != null);
            String SECRET = isDeployed ? System.getenv("SECRET_KEY") : dev_secret_key;

            JWSVerifier verifier = new MACVerifier(SECRET);

            if (signedJWT.verify(verifier)) {
                if (new Date().getTime() > signedJWT.getJWTClaimsSet().getExpirationTime().getTime()) {
                    throw new RuntimeException("Token is expired or could not be verified");
                }

                System.out.println("TOKEN VERIFIED");

                return jwt2user(signedJWT);

            } else {
                throw new JOSEException("UserEntity could not be extracted from token");
            }
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Something went wrong while verifying token");
        }
    }
    private User jwt2user(SignedJWT jwt) throws ParseException {
        String roles = jwt.getJWTClaimsSet().getClaim("roles").toString();
        String username = jwt.getJWTClaimsSet().getClaim("username").toString();

        Set<Role> rolesSet = Arrays
                .stream(roles.split(","))
                .map(role -> new Role(role))
                .collect(Collectors.toSet());
        return new User(username, rolesSet);
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
            SECRET_KEY = dev_secret_key;
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

