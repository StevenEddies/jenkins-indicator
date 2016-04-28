/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class BuildNumberComparatorTest {
	
	private BuildNumberComparator systemUnderTest;
	
	@Before
	public void setUp() {
		systemUnderTest = new BuildNumberComparator();
	}
	
	public Object[][] parametersForShouldBeUnequal() {
		return new Object[][] {
			{ null, 1L },
			{ 1L, 2L },
			{ 5L, 100L }
		};
	}

	@Test
	@Parameters
	public void shouldBeUnequal(Long lowerNumber, Long higherNumber) {
		assertThat(systemUnderTest.compare(lowerNumber, higherNumber), lessThan(0));
		assertThat(systemUnderTest.compare(higherNumber, lowerNumber), greaterThan(0));
	}
	
	public Object[][] parametersForShouldEqualItself() {
		return new Object[][] {
			{ null },
			{ 1L },
			{ 5L},
			{ 100L }
		};
	}

	@Test
	@Parameters
	public void shouldEqualItself(Long number) {
		assertThat(systemUnderTest.compare(number, number), equalTo(0));
	}
	
	public Object[][] parametersForShouldBeInvalid() {
		return new Object[][] {
			{ 0L },
			{ -1L },
			{ -100L }
		};
	}

	@Test(expected=IllegalArgumentException.class)
	@Parameters
	public void shouldBeInvalid(Long number) {
		systemUnderTest.compare(1L, number);
	}

	@Test(expected=IllegalArgumentException.class)
	@Parameters(method="parametersForShouldBeInvalid")
	public void shouldBeInvalidReversed(Long number) {
		systemUnderTest.compare(number, 1L);
	}

	@Test(expected=IllegalArgumentException.class)
	@Parameters(method="parametersForShouldBeInvalid")
	public void shouldBeInvalidDoubled(Long number) {
		systemUnderTest.compare(number, number);
	}
}
