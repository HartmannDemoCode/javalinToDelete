package dk.cphbusiness.rest.controllers;

import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

import java.util.Set;

public interface ISecurity {
    Handler login() throws Exception; // return token
    Handler authenticate();
    boolean authorize(String username, Set<String> allowedRoles);
}

