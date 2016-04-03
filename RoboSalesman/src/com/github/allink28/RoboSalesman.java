package com.github.allink28;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Allen on 4/2/2016.
 * Walmart API Homework
 * Interacts with the Walmart Open API.
 * Requirements:
 * Search for products based upon a user-provided search string
 * Use the first item in the search response as input for a product recommendation search
 * Retrieve reviews of the first 10 product recommendations
 * Rank order the recommended products based upon the review sentiments
 */
public class RoboSalesman {

    //http://api.walmartlabs.com/v1/search?query=ipod&format=json&apiKey=9e6e85c6pxvt99anbzfcwmzf
    private static final String SEARCH_QUERY_START = "http://api.walmartlabs.com/v1/search?query=";
    private static final String JSON_RESPONSE = "&format=json";
    private static final String API_KEY = "&apiKey=9mhzxs4dp7eemnce7xt2426w";
    private static final String NUM_ITEMS = "&numItems=";
    private static final String RECOMMEND_QUERY_START = "http://api.walmartlabs.com/v1/nbp?&itemId=";
    private static final String REVIEW_QUERY_START = "http://api.walmartlabs.com/v1/reviews/";

    private int queryResponseCode = -1;
//    private ArrayList

    public static void main(String[] args) {
        RoboSalesman wally = new RoboSalesman();
        wally.startWork(args);
        //TODO intro text
        //TODO buffered input reader on a loop
    }

    public RoboSalesman() {

    }

    /**
     *  Runs the program.
     * @param args
     */
    private void startWork(String[] args) {
        String userInput = parseInput(args);
        if (userInput == null || userInput.trim().length() == 0) {
            System.err.println("No search string provided!");
            return;
        }
        String searchResults = null;
        boolean searchAgain = false;
        do {
            try {
                //TODO if queryResponseCode 2xx, keep results, else ask to retry
                //or return null result if bad response code?
                searchResults = search(userInput);
                if (queryResponseCode / 200 == 2) {
                    searchAgain = false;
                } else {
                    //TODO prompt for retry
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (searchAgain);
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
        URL url = new URL(SEARCH_QUERY_START + searchString.replace(" ", "%20") + JSON_RESPONSE + API_KEY + NUM_ITEMS + "1");
        return makeGETRequest(url);
    }

    /**
     * Parse out the first product id and return it.
     * Uses org.json library.
     * @param searchResult The JSON response from the search query.
     * @return The first item's id number.
     */
    public ArrayList<Integer> parseSearchResults(String searchResult) {
        try {
            JSONObject json = new JSONObject(searchResult);
            JSONArray items = json.getJSONArray("items");
            return extractItemIds(items, 1);
        } catch (org.json.JSONException e) {
            System.err.println("Error parsing search results: " + searchResult);
        }
        return null;
    }

    /**
     * Example request: http://api.walmartlabs.com/v1/nbp?apiKey={apiKey}&itemId={itemID}
     * @param itemId The ID used to look up recommendations
     * @return JSON response of product recommendations
     */
    public String requestRecommendations(int itemId) throws IOException {
        URL url = new URL(RECOMMEND_QUERY_START + itemId + API_KEY + NUM_ITEMS + 10);
        return makeGETRequest(url);
    }

    /**
     * Uses org.json library.
     * @param recommendations The JSON response from
     * @return An arraylist of the first 10 product IDs of the recommended items.
     *         null if an error occurred.
     */
    public ArrayList<Integer> parseRecommendations(String recommendations) {
        try {
            JSONArray items = new JSONArray(recommendations);
            return extractItemIds(items, 10);
        } catch (org.json.JSONException e) {
            System.err.println("Error parsing search results: " + recommendations);
        }
        return null;
    }

    /**
     * Example request: http://api.walmartlabs.com/v1/reviews/33093101?apiKey={apiKey}&lsPublisherId={Your LinkShare Publisher Id}&format=json
     * @param itemId Product ID to retrieve review for.
     */
    public String requestReview(int itemId) throws IOException {
        URL url = new URL(REVIEW_QUERY_START + itemId + "?" + API_KEY);
        return makeGETRequest(url);
    }

    /**
     * Retrieves the average overall rating from the review statistics
     * @param reviewsResponse Response from Reviews API
     * @return Average overall review score
     */
    public double getAverageReviewScores(String reviewsResponse) {
        double score = 0;
        try {
            JSONObject json = new JSONObject(reviewsResponse);
            score = json.getJSONObject("reviewStatistics").getDouble("averageOverallRating");
        } catch (org.json.JSONException e) {
            System.err.println("Error parsing reviews: " + reviewsResponse);
        }
        return score;
    }

    //    ---    Helper methods    ---    //

    /**
     * Helper method to make HTTP GET requests
     * @param url URL to call
     * @return Response
     * @throws IOException
     */
    @NotNull
    private String makeGETRequest(URL url) throws IOException {
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
     * Helper method to parse through JSON responses and extract Product IDs
     * Uses org.json library.
     * @param items JSONArray of products
     * @param numberOfIds The total number of product IDs to be returned
     * @return A list of product IDs. The length of which depends on numberOfIds parameter
     */
    private ArrayList<Integer> extractItemIds(JSONArray items, int numberOfIds) {
        ArrayList<Integer> itemList = new ArrayList<>(numberOfIds);
        if (items != null) {
            for (int i = 0; i < numberOfIds; ++i) {
                int itemId = items.getJSONObject(i).getInt("itemId");
                String name = items.getJSONObject(i).getString("name");
                System.out.println(name + " " + itemId);
                itemList.add(itemId);
            }
        }
        return itemList;
    }
}
