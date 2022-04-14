package com.quickbase;

import org.json.simple.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

abstract class APIRequest<T>{

    public static HttpClient client = HttpClient.newHttpClient();
    public Scanner scanner = new Scanner(System.in);
    private static Map<String, String> gitToFreshFields = new HashMap<>(){{
        put("bio", "description");
        put("location", "address");
        put("twitter_username", "twitter_id");
        put("email", "email");
        put("name", "name");
    }};
    protected String api_token;
    protected String api_endpoint;

    private static String[] viableFields = {"description", "address", "name"};
    private static String[] uniqueFields = {"email", "twitter_id"};

    public static Map<String, String> getGitToFreshFields() {
        return gitToFreshFields;
    }

    public String getData(String askDescription) throws Exception{
        System.out.println(askDescription);
        String input = scanner.nextLine();
        if(input.contains(" ")) throw new Exception("Illegal username");
        return input;
    };

    abstract public T getRequest() throws Exception; // do request

    public static JSONObject buildMapElements(JSONObject jsonObject){ // rebuild map elements , take json
        Map<String, String> finalized_json_fields = new HashMap<>();
        getGitToFreshFields().forEach((k,v)->{
            if(jsonObject.get(k) != null){
                finalized_json_fields.put(v, jsonObject.get(k).toString());
            }
        });

        return new JSONObject(finalized_json_fields);
    }

    public static JSONObject removeRedundantFields(JSONObject jsonObject, boolean flag){


        HashMap<String, String> finalized_json_fields = new HashMap<>();

        if(flag) {
            for (int i = 0; i < viableFields.length; i++) {
                if (jsonObject.get(viableFields[i]) != null) {
                    finalized_json_fields.put(viableFields[i], jsonObject.get(viableFields[i]).toString());
                }
            }
            return new JSONObject(finalized_json_fields);

        } else {
            for(int i = 0; i < uniqueFields.length; i++){
                if(jsonObject.get(uniqueFields[i]) != null){
                    jsonObject.remove(uniqueFields[i]);
                }
            }
        }
        return jsonObject;
    }

    public static void checkInternetConnect(URL url) {

        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
        } catch (Exception e){
            System.out.println("No Internet connection");
            System.exit(0);
        }
    }
}
