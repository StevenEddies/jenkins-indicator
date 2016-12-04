/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import uk.me.eddies.apps.jenkinsindicator.model.JenkinsServer;

public class ApiJobContainerTest {

	private static final String BASE_NAME = "jenkins_indicator";
	private static final String JOB_NAME_1 = "dev0.1";
	private static final String JOB_NAME_2 = "iss3";
	private static final URI URI_1 = URI.create("https://jenkins.eddies.me.uk/");
	private static final URI URI_2 = URI.create("https://jenkins.eddies.me.uk/job/jenkins-indicator/");
	
	@Mock private RootApiModel apiModel;
	@Mock private ApiTrigger apiTrigger;
	@Mock private JenkinsServer model;
	
	@Captor private ArgumentCaptor<ApiJob> createdJobCaptor;
	
	private ApiJobContainer systemUnderTest;
	
	@Before
	public void setUp() {
		initMocks(this);
		when(apiModel.getApiTrigger()).thenReturn(apiTrigger);
		when(apiModel.getModel()).thenReturn(model);
		doNothing().when(apiModel).notifyAdded(createdJobCaptor.capture());
		
		systemUnderTest = new ApiJobContainer(apiModel, BASE_NAME);
	}
	
	@Test
	public void shouldCreateJobsWithCorrectName() {
		systemUnderTest.updateJobs(singletonMap(URI_1, JOB_NAME_1));
		assertThat(createdJobCaptor.getValue().getFullName(), equalTo("jenkins_indicator » dev0.1"));
	}
	
	@Test
	public void shouldCreateJobsWithCorrectNameIfNoParent() {
		systemUnderTest = new ApiJobContainer(apiModel, null);
		systemUnderTest.updateJobs(singletonMap(URI_1, JOB_NAME_1));
		assertThat(createdJobCaptor.getValue().getFullName(), equalTo(JOB_NAME_1));
	}
	
	@Test
	public void shouldIdentifyNewJobsAndTriggerRefresh() {
		systemUnderTest.updateJobs(singletonMap(URI_1, JOB_NAME_1));
		verify(apiModel).notifyAdded(Mockito.any());
		verify(apiTrigger).triggerCall(URI_1);
	}
	
	@Test
	public void shouldIdentifyRemovedJobs() {
		systemUnderTest.updateJobs(singletonMap(URI_1, JOB_NAME_1));
		ApiJob job1 = createdJobCaptor.getValue();
		
		systemUnderTest.updateJobs(singletonMap(URI_2, JOB_NAME_2));
		
		verify(apiModel).notifyRemoved(job1);
		verify(apiModel, times(2)).notifyAdded(Mockito.any()); // Called during set up and during actual test
		verify(apiTrigger, times(1)).triggerCall(URI_1); // Called during set up
		verify(apiTrigger).triggerCall(URI_2);
	}
	
	@Test
	public void shouldTriggerRefreshForExistingJobs() {
		systemUnderTest.updateJobs(singletonMap(URI_1, JOB_NAME_1));
		ApiJob job1 = createdJobCaptor.getValue();
		
		systemUnderTest.updateJobs(singletonMap(URI_1, JOB_NAME_1));
		
		verify(apiModel, never()).notifyRemoved(job1);
		verify(apiModel).notifyAdded(Mockito.any()); // Called during set up
		verify(apiTrigger, times(2)).triggerCall(URI_1); // Called during set up and during actual test
	}
	
	@Test
	public void shouldDestroyAllJobs() {
		systemUnderTest.updateJobs(singletonMap(URI_1, JOB_NAME_1));
		ApiJob job1 = createdJobCaptor.getValue();
		
		systemUnderTest.destroyAll();
		
		verify(apiModel).notifyRemoved(job1);
	}
}
