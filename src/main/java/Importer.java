import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.OptionalIterable;
import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.sun.jersey.api.client.UniformInterfaceException;

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
        final SearchResult searchResult = restClient.getSearchClient().searchJql("project = AEDMOSS", pm);
        final IssueRestClient issueClient = restClient.getIssueClient();

        for (BasicIssue i : searchResult.getIssues()) {
            System.out.println(i.getKey());
        }

        // Get the project
        Project project = restClient.getProjectClient().getProject("AEDMOSS", pm);
        System.out.println("Project name: " + project.getName());
        final OptionalIterable<IssueType> issueTypes = project.getIssueTypes();
        final IssueType issueType = issueTypes.iterator().next();
        System.out.println("Issue type: " + issueType.getName());

        User user = restClient.getUserClient().getUser(username, pm);
        System.out.println("Reporter: " + user.getDisplayName());

        IssueInputBuilder issueBuilder = new IssueInputBuilder(project, issueType);

        // Populate issue fields
        System.out.println("IssueInputBuilder created");
        issueBuilder.setAssignee(user);
        System.out.println("Assignee set: " + user.getName());
        issueBuilder.setReporter(user);
        System.out.println("Reporter set: " + user.getName());
        issueBuilder.setDescription("issue description");
        System.out.println("Description set");
        issueBuilder.setSummary("issue summary");
        System.out.println("Summary set");
        // List<String> affectedVersionsNames = Collections.emptyList();
        // issueBuilder.setAffectedVersionsNames(affectedVersionsNames);
        final IssueInput issueInput = issueBuilder.build();

        try {
            final BasicIssue issue = issueClient.createIssue(issueInput, pm);
            System.out.println("Issue successfully created.");
            // Get the newly created issue
            Issue newlyCreated = issueClient.getIssue(issue.getKey(), pm);
            System.out.println("Newly created issue key: " + newlyCreated.getKey());
        } catch (UniformInterfaceException e) {
            System.out.println("UniformInterfaceException:");
            System.out.println("Status: " + e.getResponse().getStatus());
            System.out.println("Headers: " + e.getResponse().getHeaders());
            System.out.println("Entity: " + e.getResponse().hasEntity());
        }
    }
}