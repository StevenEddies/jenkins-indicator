/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.net.URI;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import uk.me.eddies.apps.jenkinsindicator.model.JenkinsServer;

public class ApiServerTest {

	private static final URI URI_1 = URI.create("https://jenkins.eddies.me.uk/");
	private static final URI URI_2 = URI.create("https://jenkins.eddies.me.uk/job/jenkins-indicator/");
	
	@Mock private JenkinsServer model;
	@Mock private ApiTrigger apiTrigger;
	@Mock private ApiJobContainer rootJobContainer;
	@Mock private Map<URI, String> rootJobs;
	@Mock private ApiJob job1;
	@Mock private ApiJob job2;
	
	private ApiServer systemUnderTest;
	
	@Before
	public void setUp() {
		initMocks(this);
		when(job1.getUri()).thenReturn(URI_1);
		when(job2.getUri()).thenReturn(URI_2);
		
		systemUnderTest = new ApiServer(model, apiTrigger, m -> rootJobContainer);
	}
	
	@Test
	public void shouldDelegateRootJobsUpdate() {
		systemUnderTest.updateRootJobs(rootJobs);
		verify(rootJobContainer).updateJobs(rootJobs);
	}
	
	@Test
	public void shouldProvideAccessToApiTrigger() {
		assertThat(systemUnderTest.getApiTrigger(), sameInstance(apiTrigger));
	}
	
	@Test
	public void shouldProvideAccessToModel() {
		assertThat(systemUnderTest.getModel(), sameInstance(model));
	}
	
	@Test
	public void shouldRecordJobsAdded() {
		systemUnderTest.notifyAdded(job1);
		systemUnderTest.notifyAdded(job2);
		
		assertThat(systemUnderTest.getJob(URI_1), sameInstance(job1));
		assertThat(systemUnderTest.getJob(URI_2), sameInstance(job2));
	}
	
	@Test
	public void shouldRecordJobsRemoved() {
		systemUnderTest.notifyAdded(job1);
		systemUnderTest.notifyAdded(job2);
		
		systemUnderTest.notifyRemoved(job1);
		
		assertThat(systemUnderTest.getJob(URI_1), nullValue());
		assertThat(systemUnderTest.getJob(URI_2), sameInstance(job2));
	}
}
