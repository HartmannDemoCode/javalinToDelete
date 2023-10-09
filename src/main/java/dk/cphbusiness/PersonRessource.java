package dk.cphbusiness;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PersonRessource {
    private static PersonController pc = new PersonController();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        runServer(7007);
    }

    public static void runServer(int port){
        Javalin app = Javalin.create().start(port);
        app.routes(getPersonRessource());
        app.error(404, ctx -> {
            ctx.result("404 - Not found");
        });
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            JsonObject exception = new JsonObject();
            exception.addProperty("msg", e.getMessage());
            ctx.json(gson.toJson(exception));
        });
    }

    public static EndpointGroup getPersonRessource(){


        return ()->{
            path("/person", ()->{
                    get("/", pc.getAllPersons());
                    get("/{id}", pc.getPersonById());
//                get("/query-param-demo/", ctx->{
//                    String name = ctx.queryParam("name");
//                    int age = Integer.parseInt(ctx.queryParam("age"));
//                    ctx.json("{\"name\": \""+name+"\", \"age\": "+age+"}");
//                });
//                get("/header-demo", ctx->{
//                    String header = ctx.header("X-myHeader");
//                    ctx.json("{\"header\": \""+header+"\"}");
//                });
//               // PATH DEMO
//                get("/{id}", pc.getPersonById());
            });
        };
    }

}
