/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the {@link Job} class.
 */
public class JobTest {

	private static final String TEST_NAME = "JenkinsIndicator";
	
	private Job systemUnderTest;
	
	@Before
	public void setUp() {
		systemUnderTest = new Job(TEST_NAME);
	}
	
	@Test
	public void shouldStoreName() {
		assertThat(systemUnderTest.getName(), is(TEST_NAME));
	}
	
	@Test
	public void shouldHaveUnknownStatus() {
		assertThat(systemUnderTest.getStatus(), is(JobStatus.UNKNOWN));
	}
}
