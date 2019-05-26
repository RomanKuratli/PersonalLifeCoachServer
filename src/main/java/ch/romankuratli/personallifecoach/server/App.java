package ch.romankuratli.personallifecoach.server;
import ch.romankuratli.personallifecoach.server.rest_resources.RESTResource;
import ch.romankuratli.personallifecoach.server.rest_resources.RootResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import static spark.Spark.*;

import ch.romankuratli.personallifecoach.server.utils.NotImplementedRoute;
import org.apache.velocity.app.Velocity;
import spark.Route;

/**
 * Hello world!
 *
 */
public class App 
{
    public final static String URL_ROOT = "/rest";
    private final static int PORT = 4567;
    private final static Logger LOGGER = Logger.getLogger(App.class.getName());

    private static void recursiveSetupResource(RESTResource resource, String parentPath, List<String> availableRoutes) {
        String path = parentPath + resource.getSubPath();

        Route route = resource.handleGet();
        if(!(route instanceof NotImplementedRoute)) {
            availableRoutes.add("GET: " + path);
            get(path, route);
        }

        route = resource.handlePost();
        if(!(route instanceof NotImplementedRoute)) {
            availableRoutes.add("POST: " + path);
            post(path, route);
        }

        route = resource.handleDelete();
        if(!(route instanceof NotImplementedRoute)) {
            availableRoutes.add("DELETE: " + path);
            delete(path, route);
        }

        for(RESTResource subResource : resource.getSubResources()) {
            recursiveSetupResource(subResource, path, availableRoutes);
        }
    }

    private static void setupRootResource() {
        List<String> availableRoutes = new ArrayList<>();
        RootResource root = new RootResource();
        recursiveSetupResource(root,"", availableRoutes);
        root.setAvailableRoutes(availableRoutes);
    }


    public static void main( String[] args )
    {
        try {
            // specify port
            port(PORT);

            // connect to MongoDB
            MongoDbConnector.connect();

            // CORS
            options("/*", (req, res) -> {
                String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
                if (accessControlRequestHeaders != null) {
                    res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
                }

                String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
                if (accessControlRequestMethod != null) {
                    res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
                }

                return "";
            });

            // initialize Velocity Template Engine
            Velocity.init();
            LOGGER.info("Velocity Template Path: " + Velocity.getProperty("file.resource.loader.path").toString());

            before("/rest/*", (req, res) -> { // not for /rest itself which returns html
                res.header("Access-Control-Allow-Origin", "http://localhost:4200");
                res.type("application/json");
            });

            exception(Exception.class, (exception, request, response) -> {
                LOGGER.severe("Exception handling request:" + exception);
            });

            // setup Resources AFTER connecting to MongoDB
            setupRootResource();

            LOGGER.info("Server running on http://localhost:" + PORT);
        } catch (IOException ioe) {
            LOGGER.severe("Problem connectiong to MongoDB:" + ioe.getMessage());
        }
    }
}
