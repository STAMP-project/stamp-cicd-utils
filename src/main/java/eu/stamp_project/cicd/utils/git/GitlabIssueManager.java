package eu.stamp_project.cicd.utils.git;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gitlab4j.api.Constants.IssueState;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.IssueFilter;

/**
 * Manage Gitlab issues
 *
 */
public class GitlabIssueManager
{
	public static final String ISSUE_OPENED = "OPENED";
	public static final String ISSUE_CLOSED = "CLOSED";
	public static final String ISSUE_REOPENED = "REOPENED";
	
    public static void main(String[] args) throws Exception {
    	List<Issue> issues = GitlabIssueManager.listIssues("https://gitlab.ow2.org", "sbTdFFqEQnA9zCS-eWzz", 23, GitlabIssueManager.ISSUE_CLOSED);
    	for (Issue issue : issues) {
    		BufferedReader in = new BufferedReader(new StringReader(issue.getDescription()));
    		List<String> exceptions = eu.stamp_project.cicd.utils.botsing.ExceptionExtractor.extractExceptions(in);
    		if(exceptions != null && exceptions.size() > 0) {
    			System.out.println("Issue:" + issue.getTitle());
    			for(String exception : exceptions) {
    	        	System.out.println("\n========== EXCEPTION FOUND issue=" + issue.getIid() + ": ================\n"
    	        			+ exception);
    	        }
    			System.out.println("\nDetails:" + issue.getDescription());
    		}
    	}
    }


    /**
     * List gitlab issues for a given project
     * @param gitlabUrl Gitlab server URL
     * @param privateToken Gitlab private token for authentication
     * @param projectIdOrPath Gitlab project ID or path
     * @param state Gitlab issue state (one of OPENED, CLOSED, REOPENED)
     * @return The list of gitlabs issues that meet the criteria
     * @throws IOException
     */
    public static List<Issue> listIssues(String gitlabUrl, String privateToken, Object projectIdOrPath, String state) throws IOException {
    	try {
    		return listIssues(new GitLabApi(gitlabUrl, privateToken), projectIdOrPath, IssueState.forValue(state));
    	} catch(GitLabApiException e) {
    		throw new IOException(e);
    	}
    }
    
    /**
     * List gitlab issues for a given project
     * @param gitlabUrl Gitlab server URL
     * @param user Gitlab user for authentication
     * @param password Gitlab password for authentication
     * @param projectIdOrPath Gitlab project ID or path
     * @param state Gitlab issue state (one of OPENED, CLOSED, REOPENED)
     * @return The list of gitlabs issues that meet the criteria
     * @throws IOException
     */
    public static List<Issue> listIssues(String gitlabUrl, String user, String password, Object projectIdOrPath, String state) throws IOException {
    	try {
    		return listIssues(new GitLabApi(gitlabUrl, user, password), projectIdOrPath, IssueState.forValue(state));
    	} catch(GitLabApiException e) {
    		throw new IOException(e);
    	}
    }
    
    /**
     * List gitlab issues for a given project
     * @param properties Gitlab config with gitlab.url, gitlab.token and gitlab.project expected
     * @param state Gitlab issue state (one of OPENED, CLOSED, REOPENED)
     * @return The list of gitlabs issues that meet the criteria
     * @throws IOException
     */
    public static List<Issue> listIssues(Properties gitlabConfig, String state) throws IOException {
    	String gitlabUrl = gitlabConfig.getProperty("gitlab.url");
		String gitlabToken = gitlabConfig.getProperty("gitlab.token");
		String gitlabProject = gitlabConfig.getProperty("gitlab.project");
		if(gitlabUrl == null || gitlabToken == null || gitlabProject == null) {
			throw new IOException("Missing Gitlab URL and/or private token and/or project ID or path in gitlab.properties");
		}
		return listIssues(gitlabUrl, gitlabToken, gitlabProject, state);
    }

    /**
     * 
     * @param gitlabUrl Gitlab server URL
     * @param privateToken Gitlab private token for authentication
     * @param projectIdOrPath Gitlab project ID or path
     * @param iid Issue ID
     * @return The requested issue, null if not found
     * @throws IOException
     */
    public static Issue getIssue(String gitlabUrl, String privateToken, Object projectIdOrPath, int iid) throws IOException {
    	return getIssue(new GitLabApi(gitlabUrl, privateToken), projectIdOrPath, iid);
    }
    
    /**
     * 
     * @param properties Gitlab config with gitlab.url, gitlab.token and gitlab.project expected
     * @param iid Issue ID
     * @return The requested issue, null if not found
     * @throws IOException
     */
    public static Issue getIssue(Properties gitlabConfig, int iid) throws IOException {
    	String gitlabUrl = gitlabConfig.getProperty("gitlab.url");
		String gitlabToken = gitlabConfig.getProperty("gitlab.token");
		String gitlabProject = gitlabConfig.getProperty("gitlab.project");
		if(gitlabUrl == null || gitlabToken == null || gitlabProject == null) {
			throw new IOException("Missing Gitlab URL and/or private token and/or project ID or path in gitlab.properties");
		}
		return getIssue(gitlabUrl, gitlabToken, gitlabProject, iid);
    }
    
    public static boolean isExceptionLikely(Issue issue) {
    	String title = issue.getTitle().toLowerCase();
    	if(title.contains("exception") || title.contains("npe")) return true;
    	String description = issue.getDescription();
    	Pattern atMore = Pattern.compile("(\\t|\\s\\s+)at |(\\t|\\s\\s+)... \\d+ more");
    	Matcher matcher = atMore.matcher(description);
    	return matcher.find();
    	/*try {
			return (ExceptionExtractor.extractExceptions(new BufferedReader(new StringReader(description))) != null);
		} catch (IOException e) {
			return false;
		}*/
    }

    /**
     * Retrieve a gitlab issue from a given project
     * @param api Gitlab4j session
     * @param projectIdOrPath Gitlab project ID or path
     * @param iid Issue ID
     * @return The requested issue, null if not found
     * @throws IOException
     */
    private static Issue getIssue(GitLabApi api, Object projectIdOrPath, int iid) throws IOException {
    	try {
    		return api.getIssuesApi().getIssue(projectIdOrPath, iid);
    	} catch(GitLabApiException e) {
    		throw new IOException(e);
    	}
    }

    /**
     * List gitlab issues for a given project
     * @param api Gitlab4j session
     * @param projectIdOrPath Gitlab project ID or path
     * @param state Gitlab4j issue state
     * @return The list of gitlabs issues that meet the criteria
     * @throws GitLabApiException
     */
    private static List<Issue> listIssues(GitLabApi api, Object projectIdOrPath, IssueState state) throws GitLabApiException {
    	IssueFilter issueFilter = new IssueFilter();
    	issueFilter.setState(state);
    	return api.getIssuesApi().getIssues(projectIdOrPath, issueFilter);
    }

}
