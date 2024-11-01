package hu.unideb.interscope.core;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class InterScopeApp {
    public static JSONObject getEmailData(String emailAddress) {
        emailAddress = emailAddress.trim();
        String urlString = "https://api.hunter.io/v2/email-verifier?email=" + emailAddress + "&api_key=";
        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn.getResponseCode() != 200) {
                System.out.println("HTTP response code: " + conn.getResponseCode());
                return null;
            } else {
                StringBuilder resultJSON = new StringBuilder();
                Scanner sc = new Scanner(conn.getInputStream());
                while (sc.hasNext()) {
                    resultJSON.append(sc.nextLine());
                }
                sc.close();
                conn.disconnect();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(resultJSON.toString());

                JSONObject dataObject = (JSONObject) jsonObject.get("data");
                if (dataObject != null) {
                    return buildObject(dataObject);
                } else {
                    System.out.println("No data found in the response.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Issue occurred while fetching email data.");
        return null;
    }

    private static JSONObject buildObject(JSONObject dataObject) {
        JSONObject emailObject = new JSONObject();
        emailObject.put("first_name", dataObject.get("first_name"));
        emailObject.put("last_name", dataObject.get("last_name"));
        emailObject.put("email", dataObject.get("email"));
        emailObject.put("phone_number", dataObject.get("phone_number"));
        emailObject.put("company", dataObject.get("company"));
        JSONArray sources = (JSONArray) dataObject.get("sources");
        if (sources != null) {
            emailObject.put("sources", sources);
        }
        return emailObject;
    }

    public static HttpURLConnection fetchApiResponse(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        return conn;
    }

    public static String getFormattedEmailData(JSONObject emailData) {
        StringBuilder formattedData = new StringBuilder();

        formattedData.append("First Name: ").append(emailData.get("first_name")).append("\n");
        formattedData.append("Last Name: ").append(emailData.get("last_name")).append("\n");
        formattedData.append("Email: ").append(emailData.get("email")).append("\n");
        formattedData.append("Phone Number: ").append(emailData.get("phone_number")).append("\n");
        formattedData.append("Company: ").append(emailData.get("company")).append("\n");

        // Check if sources exist
        JSONArray sources = (JSONArray) emailData.get("sources");
        if (sources != null && !sources.isEmpty()) {
            formattedData.append("Sources: ").append(sources.toString()).append("\n");
        } else {
            formattedData.append("Sources: None\n");
        }

        return formattedData.toString();
    }

}
