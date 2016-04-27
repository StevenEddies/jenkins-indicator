/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class JobTest {

	private static final String TEST_NAME = "JenkinsIndicator";
	
	@Mock private Build build;
	
	private Job systemUnderTest;
	
	@Before
	public void setUp() {
		initMocks(this);
		systemUnderTest = new Job(TEST_NAME);
	}
	
	@Test
	public void shouldStoreName() {
		assertThat(systemUnderTest.getName(), is(TEST_NAME));
	}
	
	@Test
	public void shouldInitiallyHaveNotBuiltStatus() {
		assertThat(systemUnderTest.getStatus(), is(BuildStatus.NOT_BUILT));
	}
	
	@Test
	public void shouldStoreGivenLastBuild() {
		assertThat(systemUnderTest.getLastBuild(), not(sameInstance(build)));
		systemUnderTest.setLastBuild(build);
		assertThat(systemUnderTest.getLastBuild(), sameInstance(build));
	}
	
	@Test
	public void shouldAssumeStatusOfLastBuild() {
		when(build.getStatus()).thenReturn(BuildStatus.UNSTABLE);
		systemUnderTest.setLastBuild(build);
		assertThat(systemUnderTest.getStatus(), is(BuildStatus.UNSTABLE));
	}
}
