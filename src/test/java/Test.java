import com.quickbase.App;
import com.quickbase.FreshdeskRequest;
import com.quickbase.GithubRequest;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class Test {


    // Unsuccessful GITHUB authentication
    @org.junit.jupiter.api.Test
    public void testIncorrectGithubToken() {
        Exception exception = assertThrows(Exception.class, () -> {
            ByteArrayInputStream cin = new ByteArrayInputStream("mihailbozhilov".getBytes());
            System.setIn(cin);
            GithubRequest object = new GithubRequest("gpg-ranodm-pat-key");
            object.getRequest();
        });
        String expectedMessage = "Invalid or unprovided token";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    // Non-existing user
    @org.junit.jupiter.api.Test
    public void testInvalidUsername() {
        Exception exception = assertThrows(Exception.class, () -> {
            ByteArrayInputStream cin = new ByteArrayInputStream("randomnamethatdoesnotexist".getBytes());
            System.setIn(cin);
            GithubRequest object = new GithubRequest(System.getenv("GITHUB_TOKEN"));
            object.getRequest();
        });
        String expectedMessage = "No such user";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    // Test whether future implementations of buildMapElements parse to the same format
    @org.junit.jupiter.api.Test
    public void testReturnedObjectGithubReq() throws Exception {
        JSONObject returnedJsonObject = new JSONObject();
        ByteArrayInputStream cin = new ByteArrayInputStream("mihailbozhilov".getBytes());
        System.setIn(cin);
        GithubRequest object = new GithubRequest(System.getenv("GITHUB_TOKEN"));
        returnedJsonObject = object.getRequest();
        assertEquals(HelperMethods.githubContactInformation, HelperMethods.buildMapElements(returnedJsonObject));
//        assertEquals(HelperMethods.buildMapElements(HelperMethods.buildContactInformation()), HelperMethods.buildMapElements(returnedJsonObject));
    }

    // Unsuccessful authorization through wrong FRESHDESK_TOKEN
    @org.junit.jupiter.api.Test
    public void testIncorrectFreshToken() {
        Exception exception = assertThrows(Exception.class, () -> {
            ByteArrayInputStream cin = new ByteArrayInputStream(("mihailbozhilov").getBytes());
            System.setIn(cin);
            GithubRequest gh_obj = new GithubRequest(System.getenv("GITHUB_TOKEN"));
            cin = new ByteArrayInputStream(("devmisho").getBytes());
            System.setIn(cin);
            FreshdeskRequest fd_obj = new FreshdeskRequest(HelperMethods.githubContactInformation, "gpg-ranodm-pat-key");
            fd_obj.postRequest();
        });
        String expectedMessage = "Unauthorized access to subdomain";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    // Empty subdomain through Freshdesk post request
    @org.junit.jupiter.api.Test
    public void testEmptySubdomain() {
        Exception exception = assertThrows(Exception.class, () -> {
            ByteArrayInputStream cin = new ByteArrayInputStream(("mihailbozhilov").getBytes());
            System.setIn(cin);
            cin = new ByteArrayInputStream(("\n").getBytes());
            System.setIn(cin);
            FreshdeskRequest fd_obj = new FreshdeskRequest(HelperMethods.githubContactInformation, "gpg-ranodm-pat-key");
            fd_obj.postRequest();
        });
        String expectedMessage = "Empty subdomain";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    // Illegal github username
    @org.junit.jupiter.api.Test
    public void testIncorrectUsername() {
        Exception exception = assertThrows(Exception.class, () -> {
            ByteArrayInputStream cin = new ByteArrayInputStream(("mihail  bozhilov").getBytes());
            System.setIn(cin);
            GithubRequest gh_obj = new GithubRequest(System.getenv("GITHUB_TOKEN"));
            gh_obj.getRequest();
        });
        String expectedMessage = "Illegal input";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    // Illegal subdomain inputs template
    public void testIncorrectDomain(String subdomain, String expected_message) throws Exception {
        ByteArrayInputStream cin = new ByteArrayInputStream(("mihailbozhilov").getBytes());
        System.setIn(cin);
        GithubRequest gh_obj = new GithubRequest(System.getenv("GITHUB_TOKEN"));
        gh_obj.getRequest();

        Exception exception = assertThrows(Exception.class, () -> {
            ByteArrayInputStream cin_two = new ByteArrayInputStream((subdomain).getBytes());
            System.setIn(cin_two);
            FreshdeskRequest fd_obj = new FreshdeskRequest(HelperMethods.githubContactInformation, System.getenv("FRESHDESK_TOKEN"));
            fd_obj.postRequest();
        });

        String expectedMessage = expected_message;
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    // Combined implementation
    @org.junit.jupiter.api.Test
    public void combinedUnsupportedDomain() throws Exception {
        testIncorrectDomain("dev misho", "Illegal input");
        testIncorrectDomain("dev!misho", "unsupported URI https://dev!misho.freshdesk.com/api/v2/contacts");
    }

    @org.junit.jupiter.api.Test
    public void getRequest(){
        Exception exception = assertThrows(Exception.class, ()-> {
            FreshdeskRequest fd_obj = new FreshdeskRequest(HelperMethods.githubContactInformation, System.getenv("FRESHDESK_TOKEN"));
            fd_obj.getRequest();
        });
        String expectedMessage = "Cannot invoke get request without user parameters";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }







}