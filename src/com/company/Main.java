package com.company;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class Creative {
    public int clicks;
    public int conversions;
    public String id;
    public int impressions;
    public String name;
    public String parentId;
    public int views;
}

public class Main {


    public static void main(String[] args) {
        Main thisMain = new Main();
        Creative[] creatives = thisMain.getCreatives("http://homework.ad-juster.com/api/creatives");
        thisMain.countCampaignIndicator(creatives);
        thisMain.printCampaignData("http://homework.ad-juster.com/api/campaigns");
    }


    /** This returns an array of Creative objects */
    public Creative[] getCreatives(String campaignEndpoint) {

        /** ObjectMapper provides functionality for reading and writing JSON
         * We use it to - deserialize JSON string into Java objects
         * https://www.baeldung.com/jackson-object-mapper-tutorial
         *
         * We can now configure the full ObjectMapper to ignore unknown properties in the JSON:
         * https://www.baeldung.com/jackson-deserialize-json-unknown-properties
         *
         * ObjectMapper - JSON deserializer - tries to deserialize string
         * not through error, if fails to deserialize string into object - has other properties */
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            /** Class URL represents a Uniform Resource Locator, a pointer to a "resource" on the World Wide Web
             * https://docs.oracle.com/javase/8/docs/api/java/net/URL.html */
            URL url = new URL(campaignEndpoint);
            /** Each HttpURLConnection instance is used to make a single request but the underlying network connection to
             * the HTTP server may be transparently shared by other instances */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.connect();
            connection.setConnectTimeout(5000);

            int responseCode = connection.getResponseCode();
            /** If status is 200 */
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String inputLine = null;
                /** BufferedReader - Reads text from a character-input stream, buffering characters so as to provide for the
                 * efficient reading of characters, arrays, and lines. */
                /** An InputStreamReader is a bridge from byte streams to character streams:
                 * It reads bytes and decodes them into characters using a specified charset.
                 * The charset that it uses may be specified by name or may be given explicitly,
                 * or the platform's default charset may be accepted.
                 *
                 * BufferedReader reads in string - can be shared object among threads. Thread safety synchronized
                 * scanner is not */
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                /** A string buffer is like a String, but can be modified.
                 * Also it is threadsafe whereas a StringBuilder is not */
                StringBuffer response = new StringBuffer();

                while((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                /** https://www.baeldung.com/jackson-object-mapper-tutorial
                 * generate the type of collection we want from a JSON Array response
                 * Creative[] because the JSON object is an array */
                return objectMapper.readValue(response.toString(), Creative[].class);
            } else {
                /** Always have an "else" in case the request fails */
                System.out.println("Failed to fetch campaigns: " +responseCode);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /* Assumption made: the id for each Creative object represents a unique campaign ID */
    /* Creates hashmaps of campaign id mapped to impression count and to clicks
     * Checks if campaign id is in map, add current impression/click to previous impression/click
     * if no previous, previous impression/click 0 is added to current impression/click
     * Iterate through maps and print results */
    public void countCampaignIndicator(Creative[] creatives) {

        HashMap<String, Integer> campaignImpressionCounts = new HashMap<String, Integer>();
        for (Creative creative : creatives) {
            int count = campaignImpressionCounts.containsKey(creative.parentId) ? campaignImpressionCounts.get(creative.parentId) : 0;
            campaignImpressionCounts.put(creative.parentId, count + creative.impressions);
        }
        HashMap<String, Integer> campaignClickCounts = new HashMap<String, Integer>();
        for (Creative creative : creatives) {
            int count = campaignClickCounts.containsKey(creative.parentId) ? campaignClickCounts.get(creative.parentId) : 0;
            campaignClickCounts.put(creative.parentId, count + creative.clicks);
        }
        Iterator<Map.Entry<String, Integer>> impressIter = campaignImpressionCounts.entrySet().iterator();
        while (impressIter.hasNext()) {
            Map.Entry<String, Integer> impressEntry = impressIter.next();
            System.out.println("Campaign ID = "+impressEntry.getKey()+", Impressions = "+impressEntry.getValue());
        }
        Iterator<Map.Entry<String, Integer>> clickIter = campaignClickCounts.entrySet().iterator();
        while (clickIter.hasNext()) {
            Map.Entry<String, Integer> clickEntry = clickIter.next();
            System.out.println("Campaign ID = "+clickEntry.getKey()+", Clicks = "+clickEntry.getValue());
        }
    }

    /** Very similar to getCreatives except does not return Creative[] */
    public void printCampaignData(String campaignEndpoint) {

        try {
            URL url = new URL(campaignEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setConnectTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String inputLine = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                while((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                }
            } else {
                System.out.println("Failed to fetch campaigns: " +responseCode);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
