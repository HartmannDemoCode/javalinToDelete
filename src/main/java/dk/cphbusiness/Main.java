package dk.cphbusiness;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        Javalin app = Javalin
                .create()
                .start(7008)
                .get("/today", ctx -> ctx.result(LocalDate.now().toString()))
                .get("/hello", ctx -> ctx.result("Hello World"))
                .get("/hello2", new Handler(){
                    @Override
                    public void handle(Context ctx) throws Exception {
                        ctx.result("Hello World");
                    }
                });
    }
}