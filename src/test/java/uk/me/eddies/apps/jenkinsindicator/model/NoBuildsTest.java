/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class NoBuildsTest {

	@Mock private Job job;
	
	private NoBuilds systemUnderTest;
		
	@Before
	public void setUp() {
		initMocks(this);
		systemUnderTest = new NoBuilds(job);
	}
	
	@Test
	public void shouldStoreJob() {
		assertThat(systemUnderTest.getJob(), sameInstance(job));
	}
	
	@Test(expected=NullPointerException.class)
	public void shouldFailWithNullJob() {
		systemUnderTest = new NoBuilds(null);
	}
	
	@Test
	public void shouldHaveNoNumber() {
		assertThat(systemUnderTest.getNumber(), nullValue());
	}
	
	@Test
	public void shouldHaveNoStartTime() {
		assertThat(systemUnderTest.getStartTime(), nullValue());
	}
	
	@Test
	public void shouldHaveNotBuiltStatus() {
		assertThat(systemUnderTest.getStatus(), is(BuildStatus.NOT_BUILT));
	}
}
