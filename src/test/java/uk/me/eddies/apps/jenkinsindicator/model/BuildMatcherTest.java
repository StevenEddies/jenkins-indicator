/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.ZonedDateTime;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class BuildMatcherTest {
	
	private static final String JOB_NAME = "1234";
	private static final Long BUILD_NUMBER = 12345L;
	private static final ZonedDateTime START_TIME = ZonedDateTime.now();
	private static final BuildStatus STATUS = BuildStatus.FAILED;

	@Mock private Job job;
	@Mock private Build build;
	@Mock private Matcher<String> jobNameMatcher;
	@Mock private Matcher<Long> buildNumberMatcher;
	@Mock private Matcher<ZonedDateTime> startTimeMatcher;
	@Mock private Matcher<BuildStatus> statusMatcher;
	
	private BuildMatcher systemUnderTest;
	
	@Before
	public void setUp() {
		initMocks(this);
		when(build.getJob()).thenReturn(job);
		when(job.getName()).thenReturn(JOB_NAME);
		when(build.getNumber()).thenReturn(BUILD_NUMBER);
		when(build.getStartTime()).thenReturn(START_TIME);
		when(build.getStatus()).thenReturn(STATUS);
		
		systemUnderTest = new BuildMatcher(jobNameMatcher, buildNumberMatcher, startTimeMatcher, statusMatcher);
	}
	
	public Object[][] getCases() {
		return new Object[][] {
			{ true, true, true, true, true },
			{ false, true, true, true, false },
			{ true, false, true, true, false },
			{ true, true, false, true, false },
			{ true, true, true, false, false },
			{ false, false, false, false, false }
		};
	}
	
	@Test
	@Parameters(method="getCases")
	public void shouldMatchAppropriately(
			boolean jobNameMatches,
			boolean buildNumberMatches,
			boolean startTimeMatches,
			boolean statusMatches,
			boolean shouldMatchOverall
	) {
		when(jobNameMatcher.matches(JOB_NAME)).thenReturn(jobNameMatches);
		when(buildNumberMatcher.matches(BUILD_NUMBER)).thenReturn(buildNumberMatches);
		when(startTimeMatcher.matches(START_TIME)).thenReturn(startTimeMatches);
		when(statusMatcher.matches(STATUS)).thenReturn(statusMatches);
		
		assertThat(systemUnderTest.matches(build), is(shouldMatchOverall));
	}
}
