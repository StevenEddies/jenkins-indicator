/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class JobMatcherTest {
	
	private static final String NAME = "1234";

	@Mock private Job job;
	@Mock private Build build;
	@Mock private Matcher<String> nameMatcher;
	@Mock private Matcher<Build> buildMatcher;
	
	private JobMatcher systemUnderTest;
	
	@Before
	public void setUp() {
		initMocks(this);
		when(job.getName()).thenReturn(NAME);
		when(job.getLastBuild()).thenReturn(build);
		
		systemUnderTest = new JobMatcher(nameMatcher, buildMatcher);
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
		when(buildMatcher.matches(build)).thenReturn(buildMatches);
		
		assertThat(systemUnderTest.matches(job), is(shouldMatchOverall));
	}
}
