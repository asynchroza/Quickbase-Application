import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;



public class HelperMethods {

    public static Map<String, String> gitToFreshFields = new HashMap<>(){{
        put("bio", "description");
        put("location", "address");
        put("twitter_username", "twitter_id");
        put("email", "email");
        put("name", "name");
    }};

    public static String userData = "{\"gists_url\":\"https:\\/\\/api.github.com\\/users\\/mihailbozhilov\\/gists{\\/gist_id}\",\"repos_url\":\"https:\\/\\/api.github.com\\/users\\/mihailbozhilov\\/repos\",\"two_factor_authentication\":false,\"following_url\":\"https:\\/\\/api.github.com\\/users\\/mihailbozhilov\\/following{\\/other_user}\",\"twitter_username\":\"mihailbozhilov\",\"bio\":\"Testing my program rn\",\"created_at\":\"2020-12-04T16:18:01Z\",\"login\":\"mihailbozhilov\",\"type\":\"User\",\"blog\":\"\",\"private_gists\":0,\"total_private_repos\":9,\"subscriptions_url\":\"https:\\/\\/api.github.com\\/users\\/mihailbozhilov\\/subscriptions\",\"updated_at\":\"2022-04-14T08:40:35Z\",\"site_admin\":false,\"disk_usage\":308927,\"collaborators\":0,\"company\":null,\"owned_private_repos\":9,\"id\":75493358,\"public_repos\":12,\"gravatar_id\":\"\",\"plan\":{\"private_repos\":9999,\"name\":\"pro\",\"collaborators\":0,\"space\":976562499},\"email\":\"michaelbozhilov@gmail.com\",\"organizations_url\":\"https:\\/\\/api.github.com\\/users\\/mihailbozhilov\\/orgs\",\"hireable\":null,\"starred_url\":\"https:\\/\\/api.github.com\\/users\\/mihailbozhilov\\/starred{\\/owner}{\\/repo}\",\"followers_url\":\"https:\\/\\/api.github.com\\/users\\/mihailbozhilov\\/followers\",\"public_gists\":1,\"url\":\"https:\\/\\/api.github.com\\/users\\/mihailbozhilov\",\"received_events_url\":\"https:\\/\\/api.github.com\\/users\\/mihailbozhilov\\/received_events\",\"followers\":6,\"avatar_url\":\"https:\\/\\/avatars.githubusercontent.com\\/u\\/75493358?v=4\",\"events_url\":\"https:\\/\\/api.github.com\\/users\\/mihailbozhilov\\/events{\\/privacy}\",\"html_url\":\"https:\\/\\/github.com\\/mihailbozhilov\",\"following\":7,\"name\":\"Misho\",\"location\":\"Sofi2a\",\"node_id\":\"MDQ6VXNlcjc1NDkzMzU4\"}";
    public static JSONObject githubContactInformation = buildMapElements(buildContactInformation());

    public static JSONObject buildContactInformation(){
        try {
            return githubContactInformation = (JSONObject) new JSONParser().parse(userData);
        } catch (ParseException e) {
            System.out.println("Cannot parse");
        }
        return new JSONObject();
    }


    public static JSONObject buildMapElements(JSONObject jsonObject){ // rebuild map elements , take json
        Map<String, String> finalized_json_fields = new HashMap<>();
        gitToFreshFields.forEach((k,v)->{
            if(jsonObject.get(k) != null){
                finalized_json_fields.put(v, jsonObject.get(k).toString());
            }
        });

        return new JSONObject(finalized_json_fields);
    }
}
