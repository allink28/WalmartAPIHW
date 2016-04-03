package com.github.allink28;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Allen on 4/2/2016.
 */
public class RoboSalesman {

    //http://api.walmartlabs.com/v1/search?query=ipod&format=json&apiKey=9e6e85c6pxvt99anbzfcwmzf
    private static final String QUERY_START = "http://api.walmartlabs.com/v1/search?query=";
    private static final String JSON_RESPONSE = "&format=json";
    private static final String API_KEY = "&apiKey=9e6e85c6pxvt99anbzfcwmzf";

    private int queryResponseCode = -1;

    public static void main(String[] args) {
        RoboSalesman wally = new RoboSalesman();
        wally.startWork(args);
        //TODO intro text
        //TODO buffered input reader on a loop
    }

    public RoboSalesman() {

    }

    private void startWork(String[] args) {
        String userInput = parseInput(args);
        if (userInput == null || userInput.trim().length() == 0) {
            System.err.println("No search string provided!");
            return;
        }
        String searchResults = null;
        try {
            //TODO if queryResponseCode 2xx, keep results, else ask to retry
                //or return null result if bad response code?
             searchResults = search(userInput);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args Input straight from the user. Presumably a product they're searching for.
     * @return null If no search string was provided. Otherwise returns what the User
     *  typed in as a string, including spaces between words.
     */
    public String parseInput(String[] args) {
        if (args.length > 0) {
            StringBuilder searchString = new StringBuilder();
            for (String s : args) {
                if (s != null) {
                    searchString.append(" ").append(s);
                }
            }
            if (searchString.length() > 1)
                return searchString.toString().trim();
        }
        return null;
    }

    /**
     * Builds a query for the Walmart Search API and returns the search response.
     * @param searchString Item user wants to search for. Spaces are replaced with %20 to make URL.
     * @return Search response
     * @throws IOException
     */
    public String search(String searchString) throws IOException {
        URL url = new URL(QUERY_START + searchString.replace(" ", "%20") + JSON_RESPONSE + API_KEY);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        queryResponseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + queryResponseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
        return response.toString();
    }

    public int getQueryResponseCode() {
        return queryResponseCode;
    }

    /**
     * Parse out the first product id and return it.
     * Uses org.json library.
     * @param searchResult The JSON response from the search query.
     * @return The first item's id number.
     */
    public int parseSearchResults(String searchResult) {
        if (searchResult != null) {
            try {
                JSONObject json = new JSONObject(searchResult);
                JSONArray items = json.getJSONArray("items");
                for (int i = 0; i < items.length(); ++i) {
                    int itemId = items.getJSONObject(i).getInt("itemId");
                    String name = items.getJSONObject(i).getString("name");
                    System.out.println(name + " " + itemId);
                    return itemId;
                }
            } catch (org.json.JSONException e) {
                System.err.println("Error parsing search results: " + searchResult);
                return -1;
            }
        }
        return -1;
    }

}
