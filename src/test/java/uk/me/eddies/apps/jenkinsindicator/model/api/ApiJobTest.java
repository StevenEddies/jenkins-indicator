/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import uk.me.eddies.apps.jenkinsindicator.model.Build;
import uk.me.eddies.apps.jenkinsindicator.model.BuildMatcher;
import uk.me.eddies.apps.jenkinsindicator.model.BuildStatus;
import uk.me.eddies.apps.jenkinsindicator.model.JenkinsServer;
import uk.me.eddies.apps.jenkinsindicator.model.Job;
import uk.me.eddies.apps.jenkinsindicator.model.JobMatcher;

public class ApiJobTest {

	private static final URI JOB_URI = URI.create("http://jenkins.eddies.me.uk/");
	private static final String JOB_NAME = "jenkins_indicator";
	private static final long BUILD_NUMBER = 12L;
	private static final Instant BUILD_START_TIME = Instant.now();
	private static final BuildStatus BUILD_STATUS = BuildStatus.STABLE;
	
	@Mock private RootApiModel apiModel;
	@Mock private ApiJobContainer childContainer;
	@Mock private JenkinsServer model;
	@Mock private Map<URI, String> childUpdates;
	@Captor private ArgumentCaptor<Supplier<Job>> jobCaptor;
	@Captor private ArgumentCaptor<Function<Job, Build>> buildCaptor;
	
	private ApiJob systemUnderTest;
	
	@Before
	public void setUp() {
		initMocks(this);
		when(apiModel.getModel()).thenReturn(model);
		
		systemUnderTest = new ApiJob(apiModel, JOB_URI, JOB_NAME, childContainer);
	}
	
	@Test
	public void shouldRegisterItselfWithRootApiModel() {
		verify(apiModel).notifyAdded(systemUnderTest);
	}
	
	@Test
	public void shouldUnregisterItselfWithRootApiModelOnDestruction() {
		systemUnderTest.destroy();
		verify(apiModel).notifyRemoved(systemUnderTest);
	}
	
	@Test
	public void shouldUpdateHavingBuilt() {
		systemUnderTest.updateLastBuild(BUILD_NUMBER, BUILD_START_TIME, BUILD_STATUS);
		verify(model).updateForNewJobInformation(
				Mockito.eq(JOB_NAME), Mockito.eq(BUILD_NUMBER), jobCaptor.capture(), buildCaptor.capture());
		
		assertThat(jobCaptor.getValue().get(), new JobMatcher(equalTo(JOB_NAME), anything()));
		assertThat(buildCaptor.getValue().apply(new Job(JOB_NAME)),
				new BuildMatcher(equalTo(JOB_NAME), equalTo(BUILD_NUMBER), equalTo(BUILD_START_TIME), equalTo(BUILD_STATUS)));
	}
	
	@Test
	public void shouldUpdateHavingNotBuilt() {
		systemUnderTest.updateNotBuilt();
		verify(model).updateForNewJobInformation(
				Mockito.eq(JOB_NAME), Mockito.isNull(Long.class), jobCaptor.capture(), buildCaptor.capture());
		
		assertThat(jobCaptor.getValue().get(), new JobMatcher(equalTo(JOB_NAME), anything()));
		assertThat(buildCaptor.getValue().apply(new Job(JOB_NAME)),
				new BuildMatcher(equalTo(JOB_NAME), nullValue(), nullValue(), equalTo(BuildStatus.NOT_BUILT)));
	}
	
	@Test
	public void shouldUpdateChildren() {
		systemUnderTest.updateChildren(childUpdates);
		verify(childContainer).updateJobs(childUpdates);
	}
	
	@Test
	public void shouldDestroyItselfAndChildren() {
		systemUnderTest.destroy();
		verify(childContainer).destroyAll();
		verify(model).updateForDeletedJob(JOB_NAME);
	}
	
	@Test
	public void shouldProvideAccessToUri() {
		assertThat(systemUnderTest.getUri(), equalTo(JOB_URI));
	}
	
	@Test
	public void shouldProvideAccessToName() {
		assertThat(systemUnderTest.getFullName(), equalTo(JOB_NAME));
	}
}
