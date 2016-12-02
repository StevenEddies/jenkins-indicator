/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class JobTest {

	private static final String TEST_NAME = "JenkinsIndicator";
	
	@Mock private Build build;
	@Mock private Build anotherBuild;
	@Mock private Function<Job, Build> buildCreator;
	@Mock private Function<Job, Build> anotherBuildCreator;
	
	private Job systemUnderTest;
	
	@Before
	public void setUp() {
		initMocks(this);
		systemUnderTest = new Job(TEST_NAME);
		when(buildCreator.apply(systemUnderTest)).thenReturn(build);
		when(anotherBuildCreator.apply(systemUnderTest)).thenReturn(anotherBuild);
	}
	
	@Test
	public void shouldStoreName() {
		assertThat(systemUnderTest.getName(), is(TEST_NAME));
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailWithNullName() {
		systemUnderTest = new Job(null);
	}
	
	@Test
	public void shouldInitiallyHaveNotBuiltStatus() {
		assertThat(systemUnderTest.getStatus(), is(BuildStatus.NOT_BUILT));
	}
	
	@Test
	public void shouldUpdateBuildIfLater() {
		when(build.getNumber()).thenReturn(1L);
		assertThat(systemUnderTest.updateBuildIfLater(1L, buildCreator), is(true));
		assertThat(systemUnderTest.getLastBuild(), sameInstance(build));
		
		when(anotherBuild.getNumber()).thenReturn(2L);
		assertThat(systemUnderTest.updateBuildIfLater(2L, anotherBuildCreator), is(true));
		assertThat(systemUnderTest.getLastBuild(), sameInstance(anotherBuild));
	}
	
	@Test
	public void shouldNotUpdateBuildIfSameNumber() {
		when(build.getNumber()).thenReturn(1L);
		assertThat(systemUnderTest.updateBuildIfLater(1L, buildCreator), is(true));
		assertThat(systemUnderTest.getLastBuild(), sameInstance(build));
		
		when(anotherBuild.getNumber()).thenReturn(1L);
		assertThat(systemUnderTest.updateBuildIfLater(1L, anotherBuildCreator), is(false));
		assertThat(systemUnderTest.getLastBuild(), sameInstance(build));
	}
	
	@Test
	public void shouldNotUpdateBuildIfBothUnbuilt() {
		when(build.getNumber()).thenReturn(null);
		assertThat(systemUnderTest.updateBuildIfLater(null, buildCreator), is(false));
		assertThat(systemUnderTest.getLastBuild(), not(sameInstance(build)));
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldFailWithInconsistentBuildNumbers() {
		when(build.getNumber()).thenReturn(1L);
		systemUnderTest.updateBuildIfLater(2L, buildCreator);
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldFailWithReversingBuildNumbers() {
		when(build.getNumber()).thenReturn(1L);
		assertThat(systemUnderTest.updateBuildIfLater(1L, buildCreator), is(true));
		
		when(anotherBuild.getNumber()).thenReturn(null);
		systemUnderTest.updateBuildIfLater(null, anotherBuildCreator);
	}
	
	@Test
	public void shouldAssumeStatusOfLastBuild() {
		when(build.getStatus()).thenReturn(BuildStatus.UNSTABLE);
		when(build.getNumber()).thenReturn(1L);
		assertThat(systemUnderTest.updateBuildIfLater(1L, buildCreator), is(true));
		assertThat(systemUnderTest.getStatus(), is(BuildStatus.UNSTABLE));
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailWithNullBuildCreatorNotUpdating() {
		systemUnderTest.updateBuildIfLater(null, null);
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailWithNullBuildCreatorUpdating() {
		systemUnderTest.updateBuildIfLater(1L, null);
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailWithNullBuild() {
		systemUnderTest.updateBuildIfLater(1L, (job) -> null);
	}
}
