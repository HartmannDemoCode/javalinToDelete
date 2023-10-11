package dk.cphbusiness;

import dk.cphbusiness.rest.controllers.ISecurity;
import dk.cphbusiness.rest.controllers.SecurityController;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

import java.time.LocalDate;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Main {
    private static ISecurity securityController = new SecurityController();

    public static void main(String[] args) {

        Javalin app = Javalin
                .create()
                .start(7007)
                .updateConfig(config -> {

                    config.accessManager((handler, ctx, permittedRoles) -> {
                        // permitted roles are defined in the routes: get("/", ctx -> ctx.result("Hello World"), Role.ANYONE);
                        ISecurity securityController = new SecurityController();

                        String userName = ctx.attribute("userName");
                        System.out.println("NOW CHECKING THE USERS ROLES");
                        if (securityController.authorize(userName, permittedRoles))
                            handler.handle(ctx);
                        else
                            throw new ApiException(401, "Unauthorized");
                    });
                })
                .routes(() -> {
                            path("auth", () -> {
                                try {
                                    post("login", securityController.login());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                )
                .routes(() -> {
                            path("protected", () -> {
                            before(securityController.authenticate());
                            get("demo", ctx -> ctx.result("Hello from Protected"), Role.USER);
                        });
                });
    }
    private enum Role implements RouteRole { ANYONE, USER, ADMIN }
}