package ch.romankuratli.personallifecoach.server.rest_resources;

import ch.romankuratli.personallifecoach.server.utils.DiaryDate;
import ch.romankuratli.personallifecoach.server.utils.DiaryPictureManager;
import ch.romankuratli.personallifecoach.server.utils.Utils;
import spark.Route;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Logger;


public class DiaryPictures implements RESTResource{

    private final static Logger LOGGER = Logger.getLogger(DiaryPictures.class.getName());

    @Override
    public String getSubPath() {
        return "/pictures";
    }

    @Override
    public Route handlePost() {
        return (req, res) -> DiaryPictureManager.addPicture(req);
    }

    @Override
    public RESTResource[] getSubResources() {
        return new RESTResource[]{new ForEntry()};
    }

    private static class ForEntry implements RESTResource {

        private static final SimpleDateFormat SDF = new SimpleDateFormat("YYYY-MM-DD");

        @Override
        public String getSubPath() {
            return "/:entryDate";
        }

        @Override
        public Route handleGet() {
            return (req, res) -> {
                try {
                    DiaryDate dd = DiaryDate.fromString(req.params("entryDate"));
                    return DiaryPictureManager.getPicUrlsForEntry(dd);
                } catch (Exception e) {
                    LOGGER.severe("Exception trying to format date: " + req.params("entryDate"));
                    e.printStackTrace();
                    return new ArrayList<String>();
                }
            };
        }
    }
}
