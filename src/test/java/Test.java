import com.quickbase.App;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Test {

    @org.junit.jupiter.api.Test
    public void checkInvalidGithubToken(){
        Exception exception = assertThrows(Exception.class, () -> {
            App.getGithubInfo("random_nonworking_token");
        });
        String expectedMessage = "Invalid or unprovided token";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
    @org.junit.jupiter.api.Test
    public void checkInvalidFreshdeskToken(){
        Exception exception = assertThrows(Exception.class, ()-> {
            Map<String, String> map = new HashMap<>();
            map.put("email", "michaelbozhilov@gmail.com");
            App.postFreshdeskInfo(null, map);
        });
        String expectedMessage = "Unprovided token";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @org.junit.jupiter.api.Test
    public void noAuthorizationSubdomain(){
        Exception exception = assertThrows(Exception.class, () -> {
            Map<String,String> map = new HashMap<>();
            map.put("email", "michaelbozhilov@gmail.com");
            String data = "devmisho";
            ByteArrayInputStream cin = new ByteArrayInputStream(data.getBytes());
            System.setIn(cin);
            App.postFreshdeskInfo("adjsdkjgdf", map);
        });
        String expectedMessage = "Unauthorized access to subdomain";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @org.junit.jupiter.api.Test
    public void noInputSubdomain(){
        Exception exception = assertThrows(Exception.class, () -> {
            Map<String, String> map = new HashMap<>();
            map.put("email", "michaelbozhilov@gmail.com");
            String data = "\n";
            ByteArrayInputStream cin = new ByteArrayInputStream(data.getBytes());
            System.setIn(cin);
            App.postFreshdeskInfo("asdhadfdj", map);
        });
        String expectedMessage = "Empty subdomain";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @org.junit.jupiter.api.Test
    public void checkMapEmptyWhenObject(){
        Map<String, String> map = new HashMap<>();
        map.put("value","working");
        JSONObject jsonObject = new JSONObject(map);
        map = new HashMap<>();
        App.buildMapElements(jsonObject,"email","email", map);
        assertNull(map.get("value"));
    }

    @org.junit.jupiter.api.Test
    public void checkDomainFound(){
        Exception exception = assertThrows(Exception.class, () -> {
            Map<String, String> map = new HashMap<>();
            map.put("email", "michaelbozhilov@gmail.com");
            String data = "randomnotrealdomainnamewhichisoutofbounds";
            ByteArrayInputStream cin = new ByteArrayInputStream(data.getBytes());
            System.setIn(cin);
            App.postFreshdeskInfo("asdhadfdj", map);
        });
        String expectedMessage = "Subdomain not found";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @org.junit.jupiter.api.Test
    public void checkBuildMapElements(){
        Map<String, String> map = new HashMap<>();
        map.put("email","michaelbozhilov@gmail.com");
        Map<String, String> dictionary = new HashMap<>();
        JSONObject jsonObject = new JSONObject(map);
        App.buildMapElements(jsonObject,"email", "email", dictionary);
        assertEquals(map,dictionary);
    }

    // Skip tests for a private API_KEY or PAT should be provided
    // Hence 400, 409 error codes
}