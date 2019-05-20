package ch.romankuratli.personallifecoach.server.resources;

import spark.Route;

import java.util.List;

public class RootResource implements Resource {

    private List<String> availableRoutes;

    public void setAvailableRoutes(List<String> availableRoutes) {
        this.availableRoutes = availableRoutes;
    }

    @Override
    public String getSubPath() {
        return "/rest";
    }

    @Override
    public Resource[] getSubResources() {
        return new Resource[]{new Quotes()};
    }

    @Override
    public Route handleGet() {
        return (req, res) ->  availableRoutes;
    }
}
