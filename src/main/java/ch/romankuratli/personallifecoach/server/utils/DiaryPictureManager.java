package ch.romankuratli.personallifecoach.server.utils;

import ch.romankuratli.personallifecoach.server.App;
import spark.Request;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class DiaryPictureManager {
    private static final Path DIARY_PICTURES_DIR =
            Paths.get(App.class.getProtectionDomain().getCodeSource().getLocation().getPath()).subpath(0, 2)
                    .resolve("resources").resolve("diary").resolve("pictures");
    private static final Set<String> ALLOWED_EXT = new HashSet<>(Arrays.asList("jpg", "png", "jpeg"));
    private static final Logger LOGGER = Logger.getLogger(DiaryPictureManager.class.getName());
    static {
        LOGGER.info("diary picture location: " + DIARY_PICTURES_DIR.toString());
    }

    private DiaryPictureManager(){} // hide constructor, utility class

    private static String getExtension(String fileName) {
        return fileName.split("\\.")[1].toLowerCase();
    }

    private static boolean isFileAllowed(String fileName) {
        return fileName.contains(".") && ALLOWED_EXT.contains(getExtension(fileName));
    }

    private static boolean doesFileExist(String fileName, List<String> files) {
        for (String f : files) {
            if (f.contains(fileName)) return true;
        }
        return false;
    }

    private static String composeFileName(String entryDate, String ext, int index) {
        return entryDate + "_" + index + "." + ext;
    }

    private static List<String> getFileNames() {
        List<String> fileNames = new ArrayList<>();
        for (File f: DIARY_PICTURES_DIR.toFile().listFiles()) {
            fileNames.add(f.getName());
        }
        return fileNames;
    }

    private static boolean findFile(String file, List<String> files) {
        for(String existingFile: files) {
            if (existingFile.contains(file)) return true;
        }
        return false;
    }

    public static boolean addPicture(Request req) {
        // TO allow for multipart file uploads
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(""));
        try {
            // "file" is the key of the form data with the file itself being the value
            Part filePart = req.raw().getPart("picture");
            // The name of the file user uploaded
            String fileNameIn = filePart.getSubmittedFileName();
            if(!isFileAllowed(fileNameIn)) return false;

            int index = 0;
            List<String> files = getFileNames();
            String ext = getExtension(fileNameIn);
            // get date for picture
            String entryDate = Utils.getBodyJsonDoc(req).getString("entryDate");
            String fileName = composeFileName(entryDate, ext, index);
            while (findFile(fileName, files)) {
                index ++;
                fileName = composeFileName(entryDate, ext, index);
            }

            InputStream stream = filePart.getInputStream();
            // Write stream to file under storage folder
            Files.copy(stream, Paths.get("storage").resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            LOGGER.severe("Error uploading file to server: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    public static List<String> getPicUrlsForEntry(DiaryDate dd) {
        List<String> ret = new ArrayList<>();
        for (String s : getFileNames()) {
            if (s.startsWith(dd.toString())) ret.add(s);
        }
        return ret;
    }
}
