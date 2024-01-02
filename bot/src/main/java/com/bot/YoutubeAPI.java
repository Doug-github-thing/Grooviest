package com.bot;

import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class YoutubeAPI {

    private static final String API_KEY = System.getenv("YOUTUBE_API_KEY");
    private static final String youtubeAPIAddr = "https://www.googleapis.com/youtube/v3/";

    /**
     * Get the title of a video given its Youtube ID.
     * Making a GET request at https://www.googleapis.com/youtube/v3/ with params:
     * videos?part=snippet&id=<videoID>&key=<API_KEY>
     * Parses the response into a Song object with an arbitrary Position value,
     * which can be later set prior to adding to the database.
     * 
     * @param videoID Youtube ID of the video to be queried
     * @return
     */
    public static Song getSongData(String videoID) {
        String name = "";
        String channel = "";

        // Build request
        String request = youtubeAPIAddr + "videos?part=snippet&id=" + videoID + "&key=" + API_KEY;

        // Make request
        try {
            URL url = new URL(request);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            con.setRequestMethod("GET");

            // Read the response from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            // Loop through the whole input stream and parse to string
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            name = extractTitleFromJSON(response.toString());
            channel = extractChannelFromJSON(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Song(-1, name, channel, videoID);
    }

    /**
     * Makes a request to the YoutubeAPI for a song with given search terms.
     * Returns the YoutubeID of the FIRST search result, if any.
     * Request is made to
     * "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=<TERMS>&key=<KEY>"
     * 
     * @param searchTerms String representing search terms passed to the bot CLI.
     * @return The YoutubeID of the first result. Null if no results.
     */
    public static String getIDfromSearchTerms(String searchTerms) {
        String request = youtubeAPIAddr + "search?part=snippet&type=video&q=" + searchTerms + "&key=" + API_KEY;

        // Make request
        try {
            URL url = new URL(request);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            con.setRequestMethod("GET");

            // Read the response from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            // Loop through the whole input stream and parse to string
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String stringResponse = response.toString();

            return extractIDFromJSON(stringResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Looks for the first instance of the key <"title":> in the json array.
     * Gets the string in between the next set of ""
     * 
     * @param json The String representation of the video json object
     * @return The title of the given video
     */
    private static String extractTitleFromJSON(String json) {
        String title = "";

        try {
            // Trim off everything just after the word \"title\"
            int startingIndex = json.indexOf("\"title\"") + 9;
            String titleSubstring = json.substring(startingIndex, json.length() - 1);

            // Look for the first instance of a ". Trim it off the front.
            int firstQuoteIndex = titleSubstring.indexOf("\"");
            String firstQuoteSubstring = titleSubstring.substring(firstQuoteIndex + 1, titleSubstring.length() - 1);

            // Look for the next instance of a ". Parse everything before it.
            int secondQuoteIndex = firstQuoteSubstring.indexOf("\"");
            title = firstQuoteSubstring.substring(0, secondQuoteIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return title;
    }

    /**
     * Looks for the first instance of the key <"channelTitle":> in the json array.
     * Gets the string in between the next set of ""
     * 
     * @param json The String representation of the video json object
     * @return The title of the given video
     */
    private static String extractChannelFromJSON(String json) {
        String title = "";

        try {
            // Trim off everything just after the word \"title\"
            int startingIndex = json.indexOf("\"channelTitle\"") + 14;
            String titleSubstring = json.substring(startingIndex, json.length() - 1);

            // Look for the first instance of a ". Trim it off the front.
            int firstQuoteIndex = titleSubstring.indexOf("\"");
            String firstQuoteSubstring = titleSubstring.substring(firstQuoteIndex + 1, titleSubstring.length() - 1);

            // Look for the next instance of a ". Parse everything before it.
            int secondQuoteIndex = firstQuoteSubstring.indexOf("\"");
            title = firstQuoteSubstring.substring(0, secondQuoteIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return title;
    }

    /**
     * Looks for the first instance of the key <"videoId":> in the json array.
     * Gets the string in between the next set of ""
     * 
     * @param json The String representation of the video json object
     * @return The title of the given video
     */
    private static String extractIDFromJSON(String json) {
        String title = "";

        try {
            // Trim off everything just after the word \"title\"
            int startingIndex = json.indexOf("\"videoId\"") + 11;
            String titleSubstring = json.substring(startingIndex, json.length() - 1);

            // Look for the first instance of a ". Trim it off the front.
            int firstQuoteIndex = titleSubstring.indexOf("\"");
            String firstQuoteSubstring = titleSubstring.substring(firstQuoteIndex + 1, titleSubstring.length() - 1);

            // Look for the next instance of a ". Parse everything before it.
            int secondQuoteIndex = firstQuoteSubstring.indexOf("\"");
            title = firstQuoteSubstring.substring(0, secondQuoteIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return title;
    }
}
