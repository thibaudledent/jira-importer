import com.atlassian.jira.rest.client.AuthenticationHandler;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.Filterable;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;

/**
 * Custom authentication handler (follows HTTP redirection)
 * Created by ledentth on 4/23/2016.
 */
class CustomAuthHandler implements AuthenticationHandler {
    private String username;
    private String password;

    CustomAuthHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void configure(ApacheHttpClientConfig config) {
        config.getState().setCredentials(null, null, -1, username, password);
        config.getProperties().put(ApacheHttpClientConfig.PROPERTY_PREEMPTIVE_AUTHENTICATION, true);
        config.getProperties().put(ApacheHttpClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
    }

    public void configure(Filterable filterable, Client client) {

    }
}
