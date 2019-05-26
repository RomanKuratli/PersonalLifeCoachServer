package ch.romankuratli.personallifecoach.server.rest_resources;

import ch.romankuratli.personallifecoach.server.utils.VelocityUtil;
import spark.ModelAndView;
import spark.Route;
import spark.TemplateEngine;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RootResource implements RESTResource {

    private final static Logger LOGGER = Logger.getLogger(RootResource.class.getName());
    private TemplateEngine templateEngine = new VelocityTemplateEngine();
    private List<String> availableRoutes;

    public void setAvailableRoutes(List<String> availableRoutes) {
        this.availableRoutes = availableRoutes;
    }

    @Override
    public String getSubPath() {
        return "/rest";
    }

    @Override
    public RESTResource[] getSubResources() {
        return new RESTResource[]{new Quotes()};
    }

    @Override
    public Route handleGet() {
        return (req, res) ->  {
            LOGGER.info("GET /rest called");
            Map<String, Object> model = new HashMap<>();
            model.put("availablePaths", availableRoutes);
            return VelocityUtil.render("resources/templates/welcome.html", model);
        };
    }
}
