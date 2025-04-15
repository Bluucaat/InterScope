package hu.unideb.interscope.repository;

import hu.unideb.interscope.model.Log;
import hu.unideb.interscope.utils.JSONResultWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogRepository {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Logger logger = LoggerFactory.getLogger(LogRepository.class);

    public static List<Log> parseLogs() {
        List<Log> logs = new ArrayList<>();

        try {
            Path logFilePath = JSONResultWriter.LOG_PATH;
            Files.createDirectories(logFilePath.getParent());

            if (!Files.exists(logFilePath)) {
                return logs;
            }

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(logFilePath.toFile()));

            if (!(obj instanceof JSONArray)) {
                return logs;
            }

            for (Object item : (JSONArray) obj) {
                JSONObject jsonObj = (JSONObject) item;
                Log log = parseLogFromJson(jsonObj);
                if (log != null) {
                    logs.add(log);
                }
            }
        } catch (Exception e) {
            logger.error("Error reading log file: {}", e.getMessage());
        }

        return logs;
    }

    private static Log parseLogFromJson(JSONObject json) {
        try {
            String tool = (String) json.get("tool");
            String searchTarget = (String) json.get("searchTarget");
            String description = (String) json.get("description");
            String dateStr = (String) json.get("searchDate");

            Date searchDate;
            try {
                searchDate = DATE_FORMAT.parse(dateStr);
            } catch (ParseException e) {
                searchDate = new Date();
            }

            return new Log(tool, searchTarget, description, searchDate);
        } catch (Exception e) {
            logger.error("Error parsing log entry: {}", e.getMessage());
            return null;
        }
    }
}
