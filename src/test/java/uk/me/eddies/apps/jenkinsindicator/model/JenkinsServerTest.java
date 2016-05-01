/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class JenkinsServerTest {
	
	private static final String SERVER_NAME = "Teatime";
	private static final String JOB_1_NAME = "Job 1";
	private static final String JOB_2_NAME = "Job 2";
	private static final Long BUILD_NUMBER_1 = null;
	private static final Long BUILD_NUMBER_2 = 124L;
	
	@Mock private Job job1;
	@Mock private Job job2;
	@Mock private Function<Job, Build> buildCreator;

	private JenkinsServer systemUnderTest;
	
	@Before
	public void setUp() {
		initMocks(this);
		systemUnderTest = new JenkinsServer(SERVER_NAME);
	}
	
	@Test
	public void shouldStoreServerName() {
		assertThat(systemUnderTest.getServerName(), is(SERVER_NAME));
	}
	
	@Test
	public void shouldInitiallyContainNoJobs() {
		assertThat(systemUnderTest.getAllJobs(), emptyCollectionOf(Job.class));
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailWithNullServerName() {
		new JenkinsServer(null);
	}
	
	@Test
	public void shouldAddJobs() {
		when(job1.getName()).thenReturn(JOB_1_NAME);
		systemUnderTest.updateForNewJobInformation(JOB_1_NAME, BUILD_NUMBER_1, () -> job1, buildCreator);
		assertThat(systemUnderTest.getAllJobs(), contains(equalTo(job1)));
		verify(job1).updateBuildIfLater(BUILD_NUMBER_1, buildCreator);
		
		when(job2.getName()).thenReturn(JOB_2_NAME);
		systemUnderTest.updateForNewJobInformation(JOB_2_NAME, BUILD_NUMBER_2, () -> job2, buildCreator);
		assertThat(systemUnderTest.getAllJobs(), contains(Arrays.asList(equalTo(job1), equalTo(job2))));
		verify(job2).updateBuildIfLater(BUILD_NUMBER_2, buildCreator);
	}
	
	@Test
	public void shouldUpdateExistingJob() {
		when(job1.getName()).thenReturn(JOB_1_NAME);
		systemUnderTest.updateForNewJobInformation(JOB_1_NAME, BUILD_NUMBER_2, () -> job1, buildCreator);
		
		systemUnderTest.updateForNewJobInformation(JOB_1_NAME, BUILD_NUMBER_1, () -> null, buildCreator);
		
		assertThat(systemUnderTest.getAllJobs(), contains(equalTo(job1)));
		verify(job1).updateBuildIfLater(BUILD_NUMBER_1, buildCreator);
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldFailToAddJobWithInconsistentName() {
		when(job1.getName()).thenReturn(JOB_1_NAME);
		systemUnderTest.updateForNewJobInformation(JOB_2_NAME, BUILD_NUMBER_1, () -> job1, buildCreator);
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailToAddWithNullJobName() {
		when(job1.getName()).thenReturn(JOB_1_NAME);
		systemUnderTest.updateForNewJobInformation(null, BUILD_NUMBER_1, () -> job1, buildCreator);
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailToAddWithNullJobCreator() {
		when(job1.getName()).thenReturn(JOB_1_NAME);
		systemUnderTest.updateForNewJobInformation(JOB_1_NAME, BUILD_NUMBER_1, null, buildCreator);
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailToAddWithNullBuildCreator() {
		when(job1.getName()).thenReturn(JOB_1_NAME);
		systemUnderTest.updateForNewJobInformation(JOB_1_NAME, BUILD_NUMBER_1, () -> job1, null);
	}
	
	@Test
	public void shouldRemoveJobs() {
		when(job1.getName()).thenReturn(JOB_1_NAME);
		systemUnderTest.updateForNewJobInformation(JOB_1_NAME, BUILD_NUMBER_1, () -> job1, buildCreator);
		when(job2.getName()).thenReturn(JOB_2_NAME);
		systemUnderTest.updateForNewJobInformation(JOB_2_NAME, BUILD_NUMBER_2, () -> job2, buildCreator);
		
		systemUnderTest.updateForDeletedJob(JOB_1_NAME);
		assertThat(systemUnderTest.getAllJobs(), contains(equalTo(job2)));
		
		systemUnderTest.updateForDeletedJob(JOB_2_NAME);
		assertThat(systemUnderTest.getAllJobs(), emptyCollectionOf(Job.class));
	}
}
