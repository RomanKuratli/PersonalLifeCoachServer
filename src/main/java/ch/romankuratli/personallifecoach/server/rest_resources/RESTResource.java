package ch.romankuratli.personallifecoach.server.rest_resources;

import ch.romankuratli.personallifecoach.server.utils.NotImplementedRoute;
import spark.Route;

public interface RESTResource {
    String getSubPath();
    default Route handleGet() { return new NotImplementedRoute();}
    default Route handlePost() {return new NotImplementedRoute();}
    default Route handleDelete() {return new NotImplementedRoute();}
    default RESTResource[] getSubResources() {return new RESTResource[]{};}
}
