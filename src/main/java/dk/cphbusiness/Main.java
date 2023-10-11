package dk.cphbusiness;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cphbusiness.rest.controllers.ISecurity;
import dk.cphbusiness.rest.controllers.SecurityController;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Main {
    private static ISecurity securityController = new SecurityController();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {

        Javalin app = Javalin
                .create()
                .start(7007)
                .updateConfig(config -> {

                    config.accessManager((handler, ctx, permittedRoles) -> {
                        // permitted roles are defined in the routes: get("/", ctx -> ctx.result("Hello World"), Role.ANYONE);
                        Set<String> allowedRoles = permittedRoles.stream().map(role -> role.toString().toUpperCase()).collect(Collectors.toSet());
                        System.out.println("Allowed roles: " + allowedRoles);

                        if(allowedRoles.contains("ANYONE")) {
                            handler.handle(ctx);
                            return;
                        }

                        String userName = ctx.attribute("userName");
                        System.out.println("NOW CHECKING THE USERS ROLES");
                        if (securityController.authorize(userName, allowedRoles))
                            handler.handle(ctx);
                        else
                            ctx.json(objectMapper.createObjectNode().put("msg","Not authorized with roles"+allowedRoles)).status(401);
                    });
                })
                .routes(() -> {
                    path("auth", () -> {
                        try {
                            post("login", securityController.login(), Role.ANYONE);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                })
                .routes(() -> {
                    path("protected", () -> {
                        before(securityController.authenticate());
                        get("user_role", ctx -> ctx.result("Hello from Protected"), Role.USER);
                        get("admin_role", ctx -> ctx.result("Hello from Protected"), Role.ADMIN);
                    });
                })
                ;
    }

    private enum Role implements RouteRole {ANYONE, USER, ADMIN}
}