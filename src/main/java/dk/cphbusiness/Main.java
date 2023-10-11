package dk.cphbusiness;

import dk.cphbusiness.rest.controllers.ISecurity;
import dk.cphbusiness.rest.controllers.SecurityController;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.time.LocalDate;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Main {
    private static ISecurity securityController = new SecurityController();

    public static void main(String[] args) {

        Javalin app = Javalin
                .create()
                .start(7007)
                .routes(() -> {
                            path("auth", () -> {
                                try {
                                    post("login", securityController.login());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                );
    }
}