package hu.unideb.interscope.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JSONResultWriter {
    private static final Logger logger = LoggerFactory.getLogger(JSONResultWriter.class);
    public static final Path LOG_PATH = Paths.get(System.getProperty("user.dir"), "results", "searchHistory.json");

    @SuppressWarnings("unchecked")
    public static void saveResults(String toolName, String content) {
        try {
            String[] parts = toolName.split("_", 2);
            JSONObject logEntry = new JSONObject();
            logEntry.put("tool", parts[0].trim());
            logEntry.put("searchTarget", parts[1].trim());
            logEntry.put("description", content);
            logEntry.put("searchDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            JSONArray logs = Files.exists(LOG_PATH) ?
                    (JSONArray) new JSONParser().parse(new FileReader(LOG_PATH.toFile())) : new JSONArray();

            logs.add(logEntry);
            try (FileWriter writer = new FileWriter(LOG_PATH.toFile())) {
                writer.write(logs.toJSONString());
            }

        } catch (Exception e) {
            logger.error("Failed to save results: {}", e.getMessage());
        }
    }
}
