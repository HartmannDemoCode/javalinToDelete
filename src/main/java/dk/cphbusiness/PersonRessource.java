package dk.cphbusiness;

import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PersonRessource {

    private static Map<Integer,Person> persons = new HashMap(){
        {put(1,new Person("Hans","Hansen", 1990));}
        {put(2,new Person("Henrik","Hansen", 1990));}
        {put(3,new Person("Hanne","Hansen", 1990));}
    };

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7007);
        app.routes(getPersonRessource());

    }

    public static EndpointGroup getPersonRessource(){
        return ()->{
            path("/person", ()->{
                get("/", ctx->ctx.json(persons));
            });
        };
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    private static class Person{
        String firstName;
        String lastName;
        int yearOfBirth;

    }
}
