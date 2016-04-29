/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.utility;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.me.eddies.apps.jenkinsindicator.utility.ComparatorResult.EQUALS;
import static uk.me.eddies.apps.jenkinsindicator.utility.ComparatorResult.GREATER_THAN;
import static uk.me.eddies.apps.jenkinsindicator.utility.ComparatorResult.LESS_THAN;
import static uk.me.eddies.apps.jenkinsindicator.utility.ComparatorResult.compare;
import static uk.me.eddies.apps.jenkinsindicator.utility.ComparatorResult.naturalCompare;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class ComparatorResultTest {

	@Mock TestObject object1;
	@Mock TestObject object2;
	@Mock Comparator<TestObject> comparator;
	
	public interface TestObject extends Comparable<TestObject> {
		// Intentionally empty interface
	}
	
	@Before
	public void setUp() {
		initMocks(this);
	}
	
	public Object[][] getCases() {
		return new Object[][] {
			{ 0, EQUALS },
			{ -1, LESS_THAN },
			{ -100, LESS_THAN },
			{ 1, GREATER_THAN },
			{ 100, GREATER_THAN }
		};
	}
	
	@Test
	@Parameters(method="getCases")
	public void shouldInterpretCorrectlyWithComparator(int givenResult, ComparatorResult expectedResult) {
		when(comparator.compare(object1, object2)).thenReturn(givenResult);
		assertThat(compare(object1, object2, comparator), is(expectedResult));
	}
	
	@Test
	@Parameters(method="getCases")
	public void shouldInterpretCorrectlyWithNaturalOrdering(int givenResult, ComparatorResult expectedResult) {
		when(object1.compareTo(object2)).thenReturn(givenResult);
		assertThat(naturalCompare(object1, object2), is(expectedResult));
	}
}
