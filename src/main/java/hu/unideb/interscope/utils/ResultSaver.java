package hu.unideb.interscope.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResultSaver {
    private static final Logger logger = LoggerFactory.getLogger(ResultSaver.class);
    private static final String RESULTS_FILE = "results/searchHistory.txt";


    public static void saveResults(String toolName, String content) {


        try {
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            try (FileWriter writer = new FileWriter(RESULTS_FILE, true)) {
                writer.write(toolName + " - " + timestamp + "\n");
                writer.write("[---------------------------------------------]\n");
                writer.write(content);
                writer.write("\n\n");
            }
        } catch (IOException e) {
            logger.error("Failed to save results: {}", e.getMessage(), e);
        }
    }
}