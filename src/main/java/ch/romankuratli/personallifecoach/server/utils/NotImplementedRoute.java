package ch.romankuratli.personallifecoach.server.utils;

import spark.Request;
import spark.Response;
import spark.Route;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class NotImplementedRoute implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        throw new NotImplementedException();
    }
}
