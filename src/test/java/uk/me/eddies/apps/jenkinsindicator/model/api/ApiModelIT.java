/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import uk.me.eddies.apps.jenkinsindicator.model.BuildMatcher;
import uk.me.eddies.apps.jenkinsindicator.model.BuildStatus;
import uk.me.eddies.apps.jenkinsindicator.model.JenkinsServer;
import uk.me.eddies.apps.jenkinsindicator.model.JenkinsServerMatcher;
import uk.me.eddies.apps.jenkinsindicator.model.Job;
import uk.me.eddies.apps.jenkinsindicator.model.JobMatcher;

/**
 * Integration test for the API-model and model together.
 */
public class ApiModelIT {
	
	/*
	 * Root
	 * |-job1
	 * |-job2
	 * | |-job3
	 * | |-job4
	 * |-job5
	 */
	
	private static final String SERVER_NAME = "server";
	private static final String JOB_1_NAME = "job1";
	private static final String JOB_2_NAME = "job2";
	private static final String JOB_3_NAME = "job2 » job3";
	private static final String JOB_4_NAME = "job2 » job4";
	private static final String JOB_5_NAME = "job5";
	private static final String JOB_3_SIMPLE_NAME = "job3";
	private static final String JOB_4_SIMPLE_NAME = "job4";
	private static final URI URI_1 = URI.create("https://jenkins.eddies.me.uk/");
	private static final URI URI_2 = URI.create("https://jenkins.eddies.me.uk/job/jenkins-indicator/");
	private static final URI URI_3 = URI.create("https://jenkins.eddies.me.uk/job/jenkins-indicator/job/dev0.1/");
	private static final URI URI_4 = URI.create("https://jenkins.eddies.me.uk/job/jenkins-indicator/job/iss3/");
	private static final URI URI_5 = URI.create("https://jenkins.eddies.me.uk/job/jenkins-indicator/job/iss4/");
	private static final Instant TIME_1 = Instant.ofEpochMilli(10000L);
	private static final Instant TIME_2 = Instant.ofEpochMilli(21000L);
	
	@Mock private ApiTrigger trigger;
	private Map<URI, String> rootJobs;
	private Map<URI, String> job2Children;
	
	private JenkinsServer model;
	private ApiServer apiModel;
	
	@Before
	public void setUp() {
		initMocks(this);
		
		rootJobs = new HashMap<>();
		rootJobs.put(URI_1, JOB_1_NAME);
		rootJobs.put(URI_2, JOB_2_NAME);
		job2Children = new HashMap<>();
		job2Children.put(URI_3, JOB_3_SIMPLE_NAME);
		job2Children.put(URI_4, JOB_4_SIMPLE_NAME);
		
		model = new JenkinsServer(SERVER_NAME);
		apiModel = new ApiServer(model, trigger);
	}
	
	@Test
	public void shouldUpdateForInitialStructure() {
		apiModel.updateRootJobs(rootJobs);
		verify(trigger).triggerCall(URI_1);
		verify(trigger).triggerCall(URI_2);
		apiModel.getJob(URI_2).updateChildren(job2Children);
		verify(trigger).triggerCall(URI_3);
		verify(trigger).triggerCall(URI_4);
		assertThat(model, new JenkinsServerMatcher(equalTo(SERVER_NAME), emptyIterable()));
	}
	
	@Test
	public void shouldUpdateForInitialBuildState() {
		apiModel.updateRootJobs(rootJobs);
		apiModel.getJob(URI_1).updateNotBuilt();
		apiModel.getJob(URI_2).updateNotBuilt();
		apiModel.getJob(URI_2).updateChildren(job2Children);
		apiModel.getJob(URI_3).updateLastBuild(1L, TIME_1, BuildStatus.SUCCESS);
		apiModel.getJob(URI_4).updateLastBuild(2L, TIME_2, BuildStatus.UNSTABLE);
		
		List<Matcher<? super Job>> jobs = Arrays.asList(
				new JobMatcher(is(JOB_1_NAME), new BuildMatcher(is(JOB_1_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))),
				new JobMatcher(is(JOB_2_NAME), new BuildMatcher(is(JOB_2_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))),
				new JobMatcher(is(JOB_3_NAME), new BuildMatcher(is(JOB_3_NAME), is(1L), is(TIME_1), is(BuildStatus.SUCCESS))),
				new JobMatcher(is(JOB_4_NAME), new BuildMatcher(is(JOB_4_NAME), is(2L), is(TIME_2), is(BuildStatus.UNSTABLE))));
		assertThat(model, new JenkinsServerMatcher(is(SERVER_NAME), containsInAnyOrder(jobs)));
	}
	
	@Test
	public void shouldUpdateForRemovedJobs() {
		shouldUpdateForInitialBuildState();
		reset(trigger);
		
		rootJobs.remove(URI_1);
		job2Children.remove(URI_3);
		apiModel.updateRootJobs(rootJobs);
		verify(trigger, never()).triggerCall(URI_1);
		verify(trigger).triggerCall(URI_2);
		apiModel.getJob(URI_2).updateChildren(job2Children);
		verify(trigger, never()).triggerCall(URI_3);
		verify(trigger).triggerCall(URI_4);
		
		List<Matcher<? super Job>> jobs = Arrays.asList(
				new JobMatcher(is(JOB_2_NAME), new BuildMatcher(is(JOB_2_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))),
				new JobMatcher(is(JOB_4_NAME), new BuildMatcher(is(JOB_4_NAME), is(2L), is(TIME_2), is(BuildStatus.UNSTABLE))));
		assertThat(model, new JenkinsServerMatcher(is(SERVER_NAME), containsInAnyOrder(jobs)));
	}
	
	@Test
	public void shouldUpdateForRemovedJobTree() {
		shouldUpdateForInitialBuildState();
		reset(trigger);
		
		rootJobs.remove(URI_2);
		apiModel.updateRootJobs(rootJobs);
		verify(trigger).triggerCall(URI_1);
		verify(trigger, never()).triggerCall(URI_2);
		
		List<Matcher<? super Job>> jobs = Arrays.asList(
				new JobMatcher(is(JOB_1_NAME), new BuildMatcher(is(JOB_1_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))));
		assertThat(model, new JenkinsServerMatcher(is(SERVER_NAME), containsInAnyOrder(jobs)));
	}
	
	@Test
	public void shouldUpdateForAddedJob() {
		shouldUpdateForInitialBuildState();
		reset(trigger);
		
		rootJobs.put(URI_5, JOB_5_NAME);
		apiModel.updateRootJobs(rootJobs);
		verify(trigger).triggerCall(URI_1);
		verify(trigger).triggerCall(URI_2);
		verify(trigger).triggerCall(URI_5);
		apiModel.getJob(URI_5).updateNotBuilt();
		
		List<Matcher<? super Job>> jobs = Arrays.asList(
				new JobMatcher(is(JOB_1_NAME), new BuildMatcher(is(JOB_1_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))),
				new JobMatcher(is(JOB_2_NAME), new BuildMatcher(is(JOB_2_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))),
				new JobMatcher(is(JOB_3_NAME), new BuildMatcher(is(JOB_3_NAME), is(1L), is(TIME_1), is(BuildStatus.SUCCESS))),
				new JobMatcher(is(JOB_4_NAME), new BuildMatcher(is(JOB_4_NAME), is(2L), is(TIME_2), is(BuildStatus.UNSTABLE))),
				new JobMatcher(is(JOB_5_NAME), new BuildMatcher(is(JOB_5_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))));
		assertThat(model, new JenkinsServerMatcher(is(SERVER_NAME), containsInAnyOrder(jobs)));
	}
	
	@Test
	public void shouldUpdateForUpdatedJob() {
		shouldUpdateForInitialBuildState();
		reset(trigger);
		
		apiModel.getJob(URI_2).updateLastBuild(10L, TIME_1, BuildStatus.FAILURE);
		
		List<Matcher<? super Job>> jobs = Arrays.asList(
				new JobMatcher(is(JOB_1_NAME), new BuildMatcher(is(JOB_1_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))),
				new JobMatcher(is(JOB_2_NAME), new BuildMatcher(is(JOB_2_NAME), is(10L), is(TIME_1), is(BuildStatus.FAILURE))),
				new JobMatcher(is(JOB_3_NAME), new BuildMatcher(is(JOB_3_NAME), is(1L), is(TIME_1), is(BuildStatus.SUCCESS))),
				new JobMatcher(is(JOB_4_NAME), new BuildMatcher(is(JOB_4_NAME), is(2L), is(TIME_2), is(BuildStatus.UNSTABLE))));
		assertThat(model, new JenkinsServerMatcher(is(SERVER_NAME), containsInAnyOrder(jobs)));
	}
}
