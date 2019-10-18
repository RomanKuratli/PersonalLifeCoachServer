package ch.romankuratli.personallifecoach.server.rest_resources;

import ch.romankuratli.personallifecoach.server.MongoDbConnector;
import ch.romankuratli.personallifecoach.server.utils.DiaryDate;
import ch.romankuratli.personallifecoach.server.utils.DiaryPictureManager;
import ch.romankuratli.personallifecoach.server.utils.Utils;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import spark.Route;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

public class Diary implements RESTResource{
    private final static Logger LOGGER = Logger.getLogger(Diary.class.getName());
    private final static MongoCollection<Document> DIARY_COL = MongoDbConnector.getCollection("diary");

    private static String diaryEntryToJson(Document doc) {
        doc.put("_id", doc.get("_id").toString());

        Date d = (Date) doc.get("entry_date");
        DiaryDate dd;
        try {
            dd = DiaryDate.fromString("" + d.getYear() + "-" + d.getMonth() + "-" + d.getDay());
        } catch (ParseException e) {
            e.printStackTrace();
            LOGGER.severe("Error generating Diary Date: " + e.getMessage());
            return "{}";
        }
        doc.put("entry_date", dd.toString());
        doc.put("picture_urls", DiaryPictureManager.getPicUrlsForEntry((dd)));
        return doc.toJson();
    }

    @Override
    public String getSubPath() {
        return "/diary";
    }

    @Override
    public Route handleGet() {
        return (req, res) -> {
            List<String> resultList= new ArrayList<>();
            for (Document doc: DIARY_COL.find()) {
                resultList.add(diaryEntryToJson(doc));
            }
            return resultList;
        };
    }

    @Override
    public Route handlePost() {
        return (req, res) -> {
            Document doc = Utils.getBodyJsonDoc(req);
            DIARY_COL.insertOne(doc);
            return doc;
        };

    }

    @Override
    public RESTResource[] getSubResources() {
        return new RESTResource[]{
                new DiaryEntryByDate(),
                new GoodDay(),
                new DiaryPictures()
        };
    }

    private static class DiaryEntryByDate implements RESTResource {
        @Override
        public String getSubPath() {
            return "/:date";
        }

        @Override
        public Route handleGet() {
            return (req, res) -> {
                String dateStr = req.params("date");
                try {
                    DiaryDate dd = DiaryDate.fromString(dateStr);
                    for (Document doc : DIARY_COL.find()) {
                        Date d = doc.getDate("entry_date");
                        if (d.getYear() + 1900 == dd.getYear() && d.getMonth() + 1 == dd.getMonth() && d.getDay() == d.getDate()) {
                            return diaryEntryToJson(doc);
                        }
                    }
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                    LOGGER.severe("Cannot parse string '" + dateStr + "' to date");
                    return "{}";
                }
                return "{}";
            };
        }
    }

    private static class GoodDay implements RESTResource {
        @Override
        public String getSubPath() {
            return "/goodDay";
        }

        @Override
        public Route handleGet() {
            return (req, res) -> {
                List<Document> preselected = new ArrayList<>();
                for (Document doc: DIARY_COL.find()) {
                    if (doc.getList("entries", String.class).size() > 3) preselected.add(doc);
                }
                if (preselected.isEmpty()) return "[]";
                Document chosen = preselected.get(new Random().nextInt(preselected.size() - 1));
                return diaryEntryToJson(chosen);
            };
        }
    }
}
