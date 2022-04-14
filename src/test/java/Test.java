import com.quickbase.App;
import com.quickbase.FreshdeskRequest;
import com.quickbase.GithubRequest;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class Test {

    // GithubRequest
    // incorrect GITHUB_TOKEN

    @org.junit.jupiter.api.Test
    public void testIncorrectGithubToken(){
        Exception exception = assertThrows(Exception.class, ()->{
            ByteArrayInputStream cin = new ByteArrayInputStream("mihailbozhilov".getBytes());
            System.setIn(cin);
            GithubRequest object = new GithubRequest("gpg-ranodm-pat-key");
            object.getRequest();
        });
        String expectedMessage = "Invalid or unprovided token";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    // incorrect username
    @org.junit.jupiter.api.Test
    public void testInvalidUsername(){
        Exception exception = assertThrows(Exception.class, ()->{
            ByteArrayInputStream cin = new ByteArrayInputStream("randomnamethatdoesnotexist".getBytes());
            System.setIn(cin);
            GithubRequest object = new GithubRequest(System.getenv("GITHUB_TOKEN"));
            object.getRequest();
        });
        String expectedMessage = "No such user";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    //test if returned object is correct
    @org.junit.jupiter.api.Test
    public void testReturnedObjectGithubReq() throws Exception {
        JSONObject returnedJsonObject = new JSONObject();
            ByteArrayInputStream cin = new ByteArrayInputStream("mihailbozhilov".getBytes());
            System.setIn(cin);
            GithubRequest object = new GithubRequest(System.getenv("GITHUB_TOKEN"));
            returnedJsonObject = object.getRequest();
        assertEquals(HelperMethods.buildMapElements(HelperMethods.buildContactInformation()), HelperMethods.buildMapElements(returnedJsonObject));
    }

    //FreshdeskRequest
    // incorrect FRESHDESK_TOKEN
    @org.junit.jupiter.api.Test
    public void testIncorrectFreshToken(){
        Exception exception = assertThrows(Exception.class, ()->{
            ByteArrayInputStream cin = new ByteArrayInputStream(("mihailbozhilov").getBytes());
            System.setIn(cin);
            GithubRequest gh_obj = new GithubRequest(System.getenv("GITHUB_TOKEN"));
            cin = new ByteArrayInputStream(("devmisho").getBytes());
            System.setIn(cin);
            FreshdeskRequest fd_obj = new FreshdeskRequest(HelperMethods.githubContactInformation,"gpg-ranodm-pat-key");
            fd_obj.postRequest();
        });
        String expectedMessage = "Unauthorized access to subdomain";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    // empty subdomain
    @org.junit.jupiter.api.Test
    public void testEmptySubdomain(){
        Exception exception = assertThrows(Exception.class, ()->{
            ByteArrayInputStream cin = new ByteArrayInputStream(("mihailbozhilov").getBytes());
            System.setIn(cin);
            cin = new ByteArrayInputStream(("\n").getBytes());
            System.setIn(cin);
            FreshdeskRequest fd_obj = new FreshdeskRequest(HelperMethods.githubContactInformation,"gpg-ranodm-pat-key");
            fd_obj.postRequest();
        });
        String expectedMessage = "Empty subdomain";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @org.junit.jupiter.api.Test
    public void testFreshPostUserAlreadyCreated() throws Exception {
        ByteArrayInputStream cin = new ByteArrayInputStream("devmisho".getBytes());
        System.setIn(cin);
        FreshdeskRequest fd_obj = new FreshdeskRequest(HelperMethods.buildMapElements(HelperMethods.githubContactInformation), System.getenv("FRESHDESK_TOKEN"));
        int response_code = fd_obj.postRequest();

        assertEquals(409, response_code);
    }
    @org.junit.jupiter.api.Test
    public void testPutRequest() throws Exception {
        HelperMethods.buildContactInformation();
        ByteArrayInputStream cin = new ByteArrayInputStream("devmisho".getBytes());
        System.setIn(cin);
        FreshdeskRequest fd_obj = new FreshdeskRequest(HelperMethods.buildMapElements(HelperMethods.githubContactInformation), System.getenv("FRESHDESK_TOKEN"));
        fd_obj.postRequest();
        String result = fd_obj.putRequest();
        assertEquals("Contact updated successfully", result);
    }

    @org.junit.jupiter.api.Test
    public void testDoRequest() throws Exception {
        HelperMethods.buildContactInformation();
        ByteArrayInputStream cin = new ByteArrayInputStream("devmisho".getBytes());
        System.setIn(cin);
        FreshdeskRequest fd_obj = new FreshdeskRequest(HelperMethods.buildMapElements(HelperMethods.githubContactInformation), System.getenv("FRESHDESK_TOKEN"));
        String result = fd_obj.doRequest();
        boolean flag = false;
        if(result.equals("There is no new data in order to update contact") || result.equals("Contact added successfully") ||
        result.equals("Contact updated successfully")){
            flag = true;
        }
        assertTrue(flag);
    }

    // APP main
//    @org.junit.jupiter.api.Test
//    public void testMainMethod(){
//        ByteArrayInputStream cin = new ByteArrayInputStream(("mihailbozhilov" + "\n" + "devmisho").getBytes());
//        System.setIn(cin);
//        App.main(new String[] {"args1", "args2"});
//    }

}