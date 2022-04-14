package com.quickbase;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GithubRequest extends APIRequest<JSONObject>{

    private String githubContact;
    private String inputAsk = "Please, enter the Github username of the contact you wish to add: ";

    public
    GithubRequest(String api_token){
        this.api_endpoint = "https://api.github.com/users/";
        this.api_token = api_token;
    }

    @Override
    public JSONObject getRequest() throws Exception {

        this.githubContact = getData(inputAsk);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/vnd.github.v3+json")
                .header("Authorization", "token " + this.api_token) // pass token for authorization
                .uri(URI.create(this.api_endpoint + this.githubContact))
                .timeout(Duration.ofSeconds(5)) // timeout request after 5 seconds
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // send the request synchronously
        int response_code = response.statusCode();

//        System.out.println("GITHUB response: code - " + response_code + " and body: " + response.body());

        if(response_code>500) throw new Exception("Unable to reach GITHUB Servers");
        else if(response_code == 404) throw new Exception("No such user");
        else if(response_code>299) throw new Exception("Invalid or unprovided token"); // throw exception if sent information is incorrect
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.body());
        System.out.println(jsonObject);
        return (JSONObject) new JSONParser().parse(response.body());
    }
}
