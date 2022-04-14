package com.quickbase;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

public class FreshdeskRequest extends APIRequest<JSONObject>{

    private final JSONObject contactInfo;
    private final String inputAsk = "Please, enter the domain you wish to use: ";
    private String domain;

    private final StringBuilder uri_req_address = new StringBuilder();

    private HttpResponse<String> response;

    private String id;

    public
    FreshdeskRequest(JSONObject contactInfo, String api_token){
        this.api_token = api_token;
        this.api_endpoint = ".freshdesk.com/api/v2/contacts";
        this.contactInfo = contactInfo;
    }

    public String parseUserID() throws ParseException {
        JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response.body());
        JSONArray arrInJson = (JSONArray) jsonResponse.get("errors");
        JSONObject addInfo = (JSONObject) arrInJson.get(0);
        JSONObject addInfoObj = (JSONObject) addInfo.get("additional_info");
        return addInfoObj.get("user_id").toString();
    }

    @Override
    public JSONObject getRequest() throws Exception {

        uri_req_address.append("/").append(id); // Do not touch !

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((this.api_token).getBytes())) // pass token for authorization
                .uri(URI.create(uri_req_address.toString()))
                .timeout(Duration.ofSeconds(5)) // timeout request after 5 seconds
                .build();


        response = client.send(request, HttpResponse.BodyHandlers.ofString()); // send the request synchronously
        int response_code = response.statusCode();

        if(response_code>299) throw new Exception("RESPONSE CODE " + response_code);

        return (JSONObject) new JSONParser().parse(response.body());
    }

    public Integer postRequest() throws Exception {

        this.domain = getData(inputAsk);

        if(this.api_token == null) throw new Exception("Unprovided token");

        if(domain.isEmpty()) throw new Exception("Empty subdomain");

        String http_encode = "https://";

        uri_req_address.append(http_encode).append(domain).append(this.api_endpoint);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(contactInfo.toJSONString())) // pass the object in the body of request
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((this.api_token).getBytes()))
                .uri(URI.create(uri_req_address.toString()))
                .timeout(Duration.ofSeconds(5))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString()); // send the request synchronously
        int response_code = response.statusCode();
        if(response_code == 400) throw new Exception("At least an email or twitter id should be provided");
        else if(response_code == 404) throw new Exception("Subdomain not found");
        else if(response_code == 401) throw new Exception("Unauthorized access to subdomain");
        else if(response_code > 400 && response_code < 500 && response_code != 409) throw new Exception("Client side error");
        else if(response_code > 500) throw new Exception("Server side error");
        if(response_code>300) this.id = parseUserID();

        System.out.println(contactInfo.toString());
        return response_code;
    }

    public String putRequest() throws Exception {

        StringBuilder uri_put_address = uri_req_address;
        uri_put_address.append("/").append(id).toString();
        HttpRequest put_request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(contactInfo.toJSONString()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((this.api_token).getBytes()))
                .uri(URI.create(uri_put_address.toString()))
                .timeout(Duration.ofSeconds(5))
                .build();

        response = client.send(put_request, HttpResponse.BodyHandlers.ofString());
        int response_code = response.statusCode();
        System.out.println(uri_req_address);
        System.out.println(response.body());
        if(response_code > 299) throw new Exception("Unknown error");
        return "Contact updated successfully";
    }

    public String doRequest() throws Exception {

        int post_request_response = postRequest();
        String program_response = "";

        try{
            if(post_request_response < 300){
                program_response = "Contact successfully added";
            } else {
                JSONObject jsonObject = removeRedundantFields(getRequest(), true);

                if(jsonObject.equals(removeRedundantFields(contactInfo, false))) return "There is no new data in order to update contact";
                program_response = putRequest();
            }
        } catch (Exception e){
            return e.getMessage();
        }

        return program_response;
    }
}
