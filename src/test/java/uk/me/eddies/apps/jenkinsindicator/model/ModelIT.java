/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for the model as a whole.
 */
public class ModelIT {
	
	private static final String SERVER_NAME = "server";
	private static final String JOB_1_NAME = "job1";
	private static final String JOB_2_NAME = "job2";
	private static final String JOB_3_NAME = "job3";
	private static final String JOB_4_NAME = "job4";
	private static final Instant TIME_1 = Instant.ofEpochMilli(10000L);
	private static final Instant TIME_2 = Instant.ofEpochMilli(21000L);

	private JenkinsServer server;
	
	@Before
	public void setUp() {
		server = new JenkinsServer(SERVER_NAME);
		updateJobWithBuild(JOB_1_NAME, 56L, TIME_1, BuildStatus.STABLE);
		updateJobWithBuild(JOB_2_NAME, 42L, TIME_2, BuildStatus.UNSTABLE);
		updateJobNoBuild(JOB_3_NAME);
	}
	
	@Test
	public void shouldSetUpCorrectly() {
		List<Matcher<? super Job>> jobs = Arrays.asList(
				new JobMatcher(is(JOB_1_NAME), new BuildMatcher(is(JOB_1_NAME), is(56L), is(TIME_1), is(BuildStatus.STABLE))),
				new JobMatcher(is(JOB_2_NAME), new BuildMatcher(is(JOB_2_NAME), is(42L), is(TIME_2), is(BuildStatus.UNSTABLE))),
				new JobMatcher(is(JOB_3_NAME), new BuildMatcher(is(JOB_3_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))));
		assertThat(server, new JenkinsServerMatcher(is(SERVER_NAME), Matchers.<Job>contains(jobs)));
	}
	
	@Test
	public void shouldAddNewUnbuiltJob() {
		updateJobNoBuild(JOB_4_NAME);
		
		List<Matcher<? super Job>> jobs = Arrays.asList(
				new JobMatcher(is(JOB_1_NAME), new BuildMatcher(is(JOB_1_NAME), is(56L), is(TIME_1), is(BuildStatus.STABLE))),
				new JobMatcher(is(JOB_2_NAME), new BuildMatcher(is(JOB_2_NAME), is(42L), is(TIME_2), is(BuildStatus.UNSTABLE))),
				new JobMatcher(is(JOB_3_NAME), new BuildMatcher(is(JOB_3_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))),
				new JobMatcher(is(JOB_4_NAME), new BuildMatcher(is(JOB_4_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))));
		assertThat(server, new JenkinsServerMatcher(is(SERVER_NAME), Matchers.<Job>contains(jobs)));
	}
	
	@Test
	public void shouldAddNewBuiltJob() {
		updateJobWithBuild(JOB_4_NAME, 2L, TIME_2, BuildStatus.FAILED);
		
		List<Matcher<? super Job>> jobs = Arrays.asList(
				new JobMatcher(is(JOB_1_NAME), new BuildMatcher(is(JOB_1_NAME), is(56L), is(TIME_1), is(BuildStatus.STABLE))),
				new JobMatcher(is(JOB_2_NAME), new BuildMatcher(is(JOB_2_NAME), is(42L), is(TIME_2), is(BuildStatus.UNSTABLE))),
				new JobMatcher(is(JOB_3_NAME), new BuildMatcher(is(JOB_3_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))),
				new JobMatcher(is(JOB_4_NAME), new BuildMatcher(is(JOB_4_NAME), is(2L), is(TIME_2), is(BuildStatus.FAILED))));
		assertThat(server, new JenkinsServerMatcher(is(SERVER_NAME), Matchers.<Job>contains(jobs)));
	}
	
	@Test
	public void shouldDeleteJob() {
		updateJobDeleted(JOB_2_NAME);
		
		List<Matcher<? super Job>> jobs = Arrays.asList(
				new JobMatcher(is(JOB_1_NAME), new BuildMatcher(is(JOB_1_NAME), is(56L), is(TIME_1), is(BuildStatus.STABLE))),
				new JobMatcher(is(JOB_3_NAME), new BuildMatcher(is(JOB_3_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))));
		assertThat(server, new JenkinsServerMatcher(is(SERVER_NAME), Matchers.<Job>contains(jobs)));
	}
	
	@Test
	public void shouldFailToUpdateJobToNoBuild() {
		try {
			updateJobNoBuild(JOB_1_NAME);
			fail();
		} catch (RuntimeException ex) {
			// Expected
		}
		
		List<Matcher<? super Job>> jobs = Arrays.asList(
				new JobMatcher(is(JOB_1_NAME), new BuildMatcher(is(JOB_1_NAME), is(56L), is(TIME_1), is(BuildStatus.STABLE))),
				new JobMatcher(is(JOB_2_NAME), new BuildMatcher(is(JOB_2_NAME), is(42L), is(TIME_2), is(BuildStatus.UNSTABLE))),
				new JobMatcher(is(JOB_3_NAME), new BuildMatcher(is(JOB_3_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))));
		assertThat(server, new JenkinsServerMatcher(is(SERVER_NAME), Matchers.<Job>contains(jobs)));
	}
	
	@Test
	public void shouldUpdateJobToNextBuild() {
		updateJobWithBuild(JOB_1_NAME, 57L, TIME_2, BuildStatus.UNSTABLE);
		
		List<Matcher<? super Job>> jobs = Arrays.asList(
				new JobMatcher(is(JOB_1_NAME), new BuildMatcher(is(JOB_1_NAME), is(57L), is(TIME_2), is(BuildStatus.UNSTABLE))),
				new JobMatcher(is(JOB_2_NAME), new BuildMatcher(is(JOB_2_NAME), is(42L), is(TIME_2), is(BuildStatus.UNSTABLE))),
				new JobMatcher(is(JOB_3_NAME), new BuildMatcher(is(JOB_3_NAME), nullValue(), nullValue(), is(BuildStatus.NOT_BUILT))));
		assertThat(server, new JenkinsServerMatcher(is(SERVER_NAME), Matchers.<Job>contains(jobs)));
	}
	
	private void updateJobNoBuild(String name) {
		server.updateForNewJobInformation(name, null, createJob(name), createNoBuild());
	}
	
	private void updateJobWithBuild(String name, long number, Instant startTime, BuildStatus status) {
		server.updateForNewJobInformation(name, number, createJob(name), createBuild(number, startTime, status));
	}
	
	private void updateJobDeleted(String name) {
		server.updateForDeletedJob(name);
	}
	
	private Function<Job, Build> createNoBuild() {
		return job -> new NoBuilds(job);
	}
	
	private Function<Job, Build> createBuild(long number, Instant startTime, BuildStatus status) {
		return job -> new ActualBuild(job, number, startTime, status);
	}
	
	private Supplier<Job> createJob(String name) {
		return () -> new Job(name);
	}
}
