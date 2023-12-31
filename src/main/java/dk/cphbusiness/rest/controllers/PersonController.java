package dk.cphbusiness.rest.controllers;

import dk.cphbusiness.dto.PersonDTO;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonController {
//    Map<Integer,Person> persons = new HashMap();
//    public PersonController(){
//        persons.put(1,new Person("Hans","Hansen", 1970));
//        persons.put(2,new Person("Peter","Hansen", 1990));
//        persons.put(3,new Person("Helle","Hansen", 1980));
//    }
    List<PersonDTO> persons = List.of(
            new PersonDTO("Hans","Hansen", 1970),
            new PersonDTO("Peter","Hansen", 1990),
            new PersonDTO("Helle","Hansen", 1980)
    );
    public Handler getAllPersons() {

        boolean isExceptionTest= false;

        return new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                if(isExceptionTest)
                    throw new Exception("Something went wrong"); // To show the use of app.exception() in PersonRessource file
                else   // To show the use of app.error() in PersonRessource file
                    ctx.json(persons);
            }
        };
    }
//    public Handler getPersonById(){
//        return ctx -> {
//            int id = Integer.parseInt(ctx.pathParam("id"));
//            if(persons.containsKey(id))
//                ctx.json(persons.get(id));
//            else {
//                ctx.status(404);
//                ctx.json("{\"error\": \"No person with that id\"}");
//            }
//        };
//    }
    public Handler getPersonById(){
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if(id < persons.size())
                ctx.json(persons.get(id));
            else {
                ctx.status(404);
                ctx.json("{\"error\": \"No person with that id\"}");
            }
        };
        }

}
