package com.quickbase;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

public class FreshdeskRequest extends APIRequest {

    private final JSONObject contactInfo;
    private String domain;

    private HttpResponse<String> response;

    private String id;

    public
    FreshdeskRequest(JSONObject contactInfo, String api_token){
        this.api_token = api_token;
        this.api_endpoint = ".freshdesk.com/api/v2/contacts";
        this.contactInfo = contactInfo;
    }

    public String parseUserID() throws ParseException { // get Freshdesk contact id from request response
        JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response.body());
        JSONArray arrInJson = (JSONArray) jsonResponse.get("errors");
        JSONObject addInfo = (JSONObject) arrInJson.get(0);
        JSONObject addInfoObj = (JSONObject) addInfo.get("additional_info");
        return addInfoObj.get("user_id").toString();
    }
    @Override
    public JSONObject getRequest() throws Exception { // get user contact information

        if(id == null || id.isEmpty()) throw new Exception("Cannot invoke get request without user parameters");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((this.api_token).getBytes())) // pass token for authorization
                .uri(URI.create("https://"+domain+api_endpoint+"/"+id))
                .timeout(Duration.ofSeconds(5))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int response_code = response.statusCode();
        if(response_code == 404) throw new Exception("Contact not found");
        else if(response_code>=500) throw new Exception("Server side exception");
        else if(response_code>=400) throw new Exception("Client side exception");
        else if(response_code>299) throw new Exception("Additional action required to complete the request (Redirected) ");
        return (JSONObject) new JSONParser().parse(response.body());
    }

    public Integer postRequest() throws Exception {

        String inputAsk = "Please, enter the domain you wish to use: ";
        this.domain = getData(inputAsk);

        if(this.api_token == null) throw new Exception("Unprovided token");

        if(domain == null || domain.isEmpty()) throw new Exception("Empty subdomain");


        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(contactInfo.toJSONString())) // pass the object in the body of request
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((this.api_token).getBytes()))
                .uri(URI.create("https://"+domain+api_endpoint))
                .timeout(Duration.ofSeconds(5))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int response_code = response.statusCode();
        if(response_code == 400) throw new Exception("At least an email or twitter id should be provided");
        else if(response_code == 404) throw new Exception("Subdomain not found");
        else if(response_code == 401) throw new Exception("Unauthorized access to subdomain");
        else if(response_code > 499) throw new Exception("Server side error");
        else if(response_code > 399 && response_code != 409) throw new Exception("Client side error");
        if(response_code>300) this.id = parseUserID();
        return response_code;
    }

    public String putRequest() throws Exception {

        HttpRequest put_request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(contactInfo.toJSONString()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((this.api_token).getBytes()))
                .uri(URI.create("https://"+domain+api_endpoint+"/"+id))
                .timeout(Duration.ofSeconds(5))
                .build();

        response = client.send(put_request, HttpResponse.BodyHandlers.ofString());
        int response_code = response.statusCode();
        if(response_code > 299) throw new Exception(response.toString());
        return "Contact updated successfully";
    }

    public String doRequest() throws Exception {

        int post_request_response = postRequest();
        String program_response;
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
