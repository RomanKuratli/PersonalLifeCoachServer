package ch.romankuratli.personallifecoach.server.utils;

import spark.Request;
import spark.Response;
import spark.Route;

public class NotImplementedRoute implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        throw new PathNotImplementedException();
    }
}
