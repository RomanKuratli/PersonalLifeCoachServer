package ch.romankuratli.personallifecoach.server.resources;

import ch.romankuratli.personallifecoach.server.utils.NotImplementedRoute;
import spark.Route;

public interface Resource {
    String getSubPath();
    default Route handleGet() { return new NotImplementedRoute();}
    default Route handlePost() {return new NotImplementedRoute();}
    default Route handleDelete() {return new NotImplementedRoute();}
    default Resource[] getSubResources() {return new Resource[]{};}
}
