package dk.cphbusiness;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        Javalin app = Javalin.create().start(7007);
        app.routes(getPersonRessource());
    }

    public static EndpointGroup getPersonRessource(){


        return ()->{
            path("/person", ()->{
                get("/", pc.getAllPersons());
                get("/query-param-demo/", ctx->{
                    String name = ctx.queryParam("name");
                    int age = Integer.parseInt(ctx.queryParam("age"));
                    ctx.json("{\"name\": \""+name+"\", \"age\": "+age+"}");
                });
                get("/header-demo", ctx->{
                    String header = ctx.header("X-myHeader");
                    ctx.json("{\"header\": \""+header+"\"}");
                });
               // PATH DEMO
                get("/{id}", pc.getPersonById());
            });
        };
    }

}
