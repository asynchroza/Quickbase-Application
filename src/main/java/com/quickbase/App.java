package com.quickbase;


public class App {


    public static void main(String[] args) {

        try{
            GithubRequest github_req = new GithubRequest();
            FreshdeskRequest freshdesk_req = new FreshdeskRequest(APIRequest.buildMapElements(github_req.getRequest()));
            System.out.println(freshdesk_req.doRequest());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        System.exit(0);
    }
}