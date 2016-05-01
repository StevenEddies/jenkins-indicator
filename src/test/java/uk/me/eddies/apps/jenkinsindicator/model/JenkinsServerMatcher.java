/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import uk.me.eddies.apps.jenkinsindicator.utility.TypedMatcher;

/**
 * {@link Matcher} for a {@link JenkinsServer}.
 */
public class JenkinsServerMatcher extends TypedMatcher<JenkinsServer> {
	
	private final Matcher<? super String> name;
	private final Matcher<? super Iterable<Job>> jobs;
	
	public JenkinsServerMatcher(
			Matcher<? super String> name,
			Matcher<? super Iterable<Job>> jobs
	) {
		super(JenkinsServer.class);
		this.name = name;
		this.jobs = jobs;
	}
	
	@Override
	protected boolean internalMatches(JenkinsServer item) {
		return name.matches(item.getServerName())
				&& jobs.matches(item.getAllJobs());
	}
	
	@Override
	public void describeTo(Description description) {
		description.appendText("server with [")
			.appendText("name: ").appendDescriptionOf(name).appendText(", ")
			.appendText("jobs: ").appendDescriptionOf(jobs).appendText("]");
	}
}
