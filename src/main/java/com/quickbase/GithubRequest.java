package com.quickbase;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GithubRequest extends APIRequest {

    private String githubContact;

    public
    GithubRequest(String api_token){
        this.api_endpoint = "https://api.github.com/users/";
        this.api_token = api_token;
    }

    @Override
    public JSONObject getRequest() throws Exception {

        String inputAsk = "Please, enter the Github username of the contact you wish to add: ";
        this.githubContact = getData(inputAsk); // get user input

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/vnd.github.v3+json")
                .header("Authorization", "token " + this.api_token) // pass token for authorization
                .uri(URI.create(this.api_endpoint + this.githubContact))
                .timeout(Duration.ofSeconds(5)) // timeout request after 5 seconds
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // send the request synchronously
        int response_code = response.statusCode();


        if(response_code>499) throw new Exception("Github server side error"); // server side error
        else if(response_code == 404) throw new Exception("No such user"); // user not found
        else if(response_code>299) throw new Exception("Invalid or unprovided token"); // throw exception if sent information is incorrect
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.body());

        return (JSONObject) new JSONParser().parse(response.body());
    }
}
