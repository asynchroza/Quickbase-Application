package com.quickbase;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class App {

    private static final String GITHUB_TOKEN = System.getenv("GITHUB_TOKEN");
    private static final String FRESHDESK_TOKEN = System.getenv("FRESHDESK_TOKEN");
    private static final String GITHUB_API = "https://api.github.com/user";
    private static final String FRESHDESK_API = ".freshdesk.com/api/v2/contacts";

    public static Map<String, String> getGithubInfo(String GITHUB_TOKEN) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/vnd.github.v3+json")
                .header("Authorization", "token " + GITHUB_TOKEN) // pass token for authorization
                .uri(URI.create(GITHUB_API))
                .timeout(Duration.ofSeconds(5)) // timeout request after 5 seconds
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // send the request synchronously
        int response_code = response.statusCode();
        if(response_code>299)throw new Exception("Invalid token"); // throw exception if sent information is incorrect


        JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.body()); // parses response as JSON Object

        Map<String, String> dictionary = new HashMap<String, String>();

        // Map received values so we can exclude null values afterwards when doing a post request

        buildMapElements(jsonObject, "name", "name", dictionary); // name of user in github
        buildMapElements(jsonObject, "email", "email", dictionary); // email address (unique value in freshdesk)
        buildMapElements(jsonObject, "twitter_id", "twitter_username", dictionary); // twitter username (unique value in freshdesk)
        buildMapElements(jsonObject, "description", "bio", dictionary);
        buildMapElements(jsonObject, "address", "location", dictionary);
        buildMapElements(jsonObject, "company_id", "company", dictionary);

        if(dictionary == null) throw new Exception("No response data provided"); // the possibility of fulfilling this check is only if Freshdesk change their api
        return dictionary;

    }

    public static void buildMapElements(JSONObject jsonObject,String flashdeskJSONKey, String type_of_data, Map<String, String> dictionary){
        if(jsonObject.get(type_of_data) == null) return;
        dictionary.put(flashdeskJSONKey, jsonObject.get(type_of_data).toString());
    }

    public static void postFreshdeskInfo(String FRESHDESK_TOKEN, Map<String, String> dictionary) throws Exception {


        StringBuffer http_encode = new StringBuffer("https://"); // get subdomain
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, insert Freshdesk subdomain:");
        StringBuffer subdomain = new StringBuffer(scanner.nextLine());
        if(subdomain.isEmpty()) throw new Exception("Empty subdomain");
        scanner.close();

        // using StringBuffer because they are more efficient when it comes to concatenation

        StringBuffer uri_put_req = new StringBuffer();
        uri_put_req.append(http_encode).append(subdomain).append(FRESHDESK_API);

        JSONObject jsonObject = new JSONObject(dictionary); //create a json object from map -- incoming_info

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toJSONString())) // pass the object in the body of request
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((FRESHDESK_TOKEN).getBytes()))
                .uri(URI.create(uri_put_req.toString()))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // send the request synchronously
        int response_code = response.statusCode();

        System.out.println("RESPONSE on post: " + response_code);
        if(response_code < 300) System.out.println("Contact added successfully!");
        else if(response_code == 400) throw new Exception("At least an email, phone number or twitter id should be provided");
        else if(response_code == 404) throw new Exception("Subdomain not found");
        else if(response_code == 401) throw new Exception("Unauthorized access to subdomain");
        else if(response_code == 409) {

            // if user is already created, and we want to just change the data, we need to get the user id for the new put request
            // JSON response has an inner array where the id object is placed

            JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response.body());
            JSONArray arrInJson = (JSONArray) jsonResponse.get("errors");
            JSONObject addInfo = (JSONObject) arrInJson.get(0);
            JSONObject addInfoObj = (JSONObject) addInfo.get("additional_info");
            String id = addInfoObj.get("user_id").toString();

            HttpRequest put_request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonObject.toJSONString()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((FRESHDESK_TOKEN).getBytes()))
                    .uri(URI.create(uri_put_req.append("/").append(id).toString()))
                    .timeout(Duration.ofSeconds(5))
                    .build();

            response = client.send(put_request, HttpResponse.BodyHandlers.ofString());
            response_code = response.statusCode();

            System.out.println("RESPONSE on put:" + response_code);
            if(response_code < 300) System.out.println("Contact updated"); else throw new Exception("Could not update contact");
        } else throw new Exception("Unexpected error");
    }



    public static void main(String[] args) {
        Map<String, String> dictionary;

        try{
            dictionary = getGithubInfo(GITHUB_TOKEN);
            postFreshdeskInfo(FRESHDESK_TOKEN, dictionary);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        System.exit(0);
    }
}