/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collection;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class JenkinsServerMatcherTest {
	
	private static final String NAME = "1234";

	@Mock private Collection<Job> jobs;
	@Mock private JenkinsServer server;
	@Mock private Matcher<String> nameMatcher;
	@Mock private Matcher<Iterable<Job>> jobsMatcher;
	
	private JenkinsServerMatcher systemUnderTest;
	
	@Before
	public void setUp() {
		initMocks(this);
		
		when(server.getServerName()).thenReturn(NAME);
		when(server.getAllJobs()).thenReturn(jobs);
		
		systemUnderTest = new JenkinsServerMatcher(nameMatcher, jobsMatcher);
	}
	
	public Object[][] getCases() {
		return new Object[][] {
			{ true, true, true },
			{ false, true, false },
			{ true, false, false },
			{ false, false, false }
		};
	}
	
	@Test
	@Parameters(method="getCases")
	public void shouldMatchAppropriately(
			boolean nameMatches,
			boolean buildMatches,
			boolean shouldMatchOverall
	) {
		when(nameMatcher.matches(NAME)).thenReturn(nameMatches);
		when(jobsMatcher.matches(jobs)).thenReturn(buildMatches);
		
		assertThat(systemUnderTest.matches(server), is(shouldMatchOverall));
	}
}
