/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class ActualBuildTest {
	
	private static final long BUILD_NUMBER = 1234;
	private static final ZonedDateTime START_TIME = ZonedDateTime.ofInstant(
			Instant.ofEpochSecond(100000L),
			ZoneId.ofOffset("GMT", ZoneOffset.ofHours(1)));
	private static final BuildStatus STATUS = BuildStatus.FAILED;

	@Mock private Job job;
	
	private ActualBuild systemUnderTest;
		
	@Before
	public void setUp() {
		initMocks(this);
		systemUnderTest = new ActualBuild(job, BUILD_NUMBER, START_TIME, STATUS);
	}
	
	@Test
	public void shouldStoreJob() {
		assertThat(systemUnderTest.getJob(), sameInstance(job));
	}
	
	@Test
	public void shouldStoreBuildNumber() {
		assertThat(systemUnderTest.getNumber(), is(BUILD_NUMBER));
	}
	
	@Test
	public void shouldStoreStartTime() {
		assertThat(systemUnderTest.getStartTime(), is(START_TIME));
	}
	
	@Test
	public void shouldStoreStatus() {
		assertThat(systemUnderTest.getStatus(), is(BuildStatus.FAILED));
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailWithNullJob() {
		systemUnderTest = new ActualBuild(null, BUILD_NUMBER, START_TIME, STATUS);
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailWithNullStartTime() {
		systemUnderTest = new ActualBuild(job, BUILD_NUMBER, null, STATUS);
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailWithNullStatus() {
		systemUnderTest = new ActualBuild(job, BUILD_NUMBER, START_TIME, null);
	}
	
	public Collection<Object[]> parametersForShouldFailSettingInvalidStatus() {
		return Arrays.stream(BuildStatus.values())
				.filter(status -> !status.isValidActualBuildStatus())
				.map(status -> new Object[] { status })
				.collect(Collectors.toSet());
	}
	
	@Test(expected=IllegalArgumentException.class)
	@Parameters
	public void shouldFailSettingInvalidStatus(BuildStatus eachStatus) {
		systemUnderTest = new ActualBuild(job, BUILD_NUMBER, START_TIME, eachStatus);
	}
	
	public Collection<Object[]> parametersForShouldSucceedSettingValidStatus() {
		return Arrays.stream(BuildStatus.values())
				.filter(BuildStatus::isValidActualBuildStatus)
				.map(status -> new Object[] { status })
				.collect(Collectors.toSet());
	}
	
	@Test
	@Parameters
	public void shouldSucceedSettingValidStatus(BuildStatus eachStatus) {
		systemUnderTest = new ActualBuild(job, BUILD_NUMBER, START_TIME, eachStatus);
	}
}
