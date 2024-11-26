import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate; // Import for date handling
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Stock_Trading_Bot {

    static String API_KEY = "Yyg5DkZX7KAXJCkps5UsKE2YIyJy9hum";
    static String BASE_URL = "https://api.polygon.io/v2/aggs/ticker";
    public static String formattedDate;

    public static void main(String[] args) {
        // Stocks to track
        String stock1 = "AAPL"; // Example stock

        LocalDate yesterday = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formattedDate = yesterday.format(formatter);

        // Fetch and print stock data
        if (stock1 != null) {
            String stockData1 = getStockData(stock1);
            if (stockData1 != null) {
                System.out.println("Stock 1 Close Price: " + stockData1);
            }
        }
    }

    /**
     * Fetch stock data for a given symbol using the current day.
     */
    private static String getStockData(String stockSymbol) {
        // Get the current date
        String currentDate = LocalDate.now().toString(); // Format: YYYY-MM-DD

        // Build the API URL dynamically
        String urlString = String.format(
                "%s/%s/range/1/day/" + formattedDate + "/" + formattedDate + "?adjusted=true&sort=asc&apiKey=%s",
                BASE_URL, stockSymbol, API_KEY);

        try {
            System.out.println(urlString);
            // Fetch API response
            HttpURLConnection apiConn = fetchAPIResponse(urlString);

            // Check if API connection is valid
            if (apiConn == null || apiConn.getResponseCode() != 200) {
                System.out.println("Error connecting to API for stock: " + stockSymbol);
                return null;
            }

            System.out.println("Connected to API for stock: " + stockSymbol);

            // Parse the JSON response
            String jsonResponse = readAPIResponse(apiConn);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);

            // Retrieve the "results" array
            JSONArray results = (JSONArray) jsonObject.get("results");
            if (results != null && !results.isEmpty()) {
                // Get the close price ("c") from the first result
                JSONObject firstResult = (JSONObject) results.get(0);
                return firstResult.get("c").toString(); // Close price
            } else {
                System.out.println("No data available for stock: " + stockSymbol);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetch API response for the given URL.
     */
    private static HttpURLConnection fetchAPIResponse(String urlIn) {
        try {
            URL url = new URL(urlIn);
            HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
            myConnection.setRequestMethod("GET");
            return myConnection;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Read the API response into a string.
     */
    private static String readAPIResponse(HttpURLConnection apiConn) {
        try (Scanner scanner = new Scanner(apiConn.getInputStream())) {
            StringBuilder finalJSON = new StringBuilder();
            while (scanner.hasNext()) {
                finalJSON.append(scanner.nextLine());
            }
            return finalJSON.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
