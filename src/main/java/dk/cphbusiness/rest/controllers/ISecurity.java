package dk.cphbusiness.rest.controllers;

import io.javalin.http.Handler;

public interface ISecurity {
    Handler login() throws Exception; // return token
    Handler authenticate();
}

