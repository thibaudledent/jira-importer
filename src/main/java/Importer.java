import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Jira importer.
 * Created by ledentth on 4/23/2016.
 */
public class Importer {
    @Parameter(names = {"--jira", "-j"})
    private static String jiraUrl;
    @Parameter(names = {"--username", "-u"})
    private static String username;
    @Parameter(names = {"--password", "-p"})
    private static String password;

    public static void main(String... args) throws URISyntaxException {
        Importer importer = new Importer();
        new JCommander(importer, args);
        System.out.println("Starting import with args: jira url " + jiraUrl + " username " + username + " password " + password);
        importer.run();
    }

    private void run() throws URISyntaxException {
        final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
        final URI jiraServerUri = new URI(jiraUrl);
        final JiraRestClient restClient = factory.create(jiraServerUri, new CustomAuthHandler(username, password));
        final NullProgressMonitor pm = new NullProgressMonitor();
        final SearchResult issue = restClient.getSearchClient().searchJql("project = AEDMOSS", pm);

        for (BasicIssue i : issue.getIssues()) {
            System.out.println(i.getKey());
        }
    }
}