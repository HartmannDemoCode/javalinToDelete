package dk.cphbusiness;

import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

public class PersonController {
    Map<Integer,Person> persons = new HashMap();
    public PersonController(){
        persons.put(1,new Person("Hans","Hansen", 1990));
        persons.put(2,new Person("Hans","Hansen", 1990));
        persons.put(3,new Person("Hans","Hansen", 1990));
    }
    public Handler getAllPersons() {

        boolean isExceptionTest= true;

        return new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                if(isExceptionTest)
                    throw new RuntimeException("Something went wrong"); // To show the use of app.exception() in PersonRessource file
                else   // To show the use of app.error() in PersonRessource file
                    ctx.json(persons);
            }
        };
    }
    public Handler getPersonById(){
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if(persons.containsKey(id))
                ctx.json(persons.get(id));
            else {
                ctx.status(404);
                ctx.json("{\"error\": \"No person with that id\"}");
            }
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
