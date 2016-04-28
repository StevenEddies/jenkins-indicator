/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BuildStatusTest {

	@Test
	public void shouldBeInvalidActualBuildStatus() {
		assertThat(BuildStatus.NOT_BUILT.isValidActualBuildStatus(), is(false));
	}
	
	@Test
	public void shouldBeValidActualBuildStatus() {
		assertThat(BuildStatus.FAILED.isValidActualBuildStatus(), is(true));
		assertThat(BuildStatus.STABLE.isValidActualBuildStatus(), is(true));
		assertThat(BuildStatus.UNSTABLE.isValidActualBuildStatus(), is(true));
	}
}
