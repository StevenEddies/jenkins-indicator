/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.utility;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;

public class TypedMatcherTest {
	
	private static final String DESIRED_VALUE = "1";
	private static final String OTHER_VALUE = "2";
	private static final Integer INCORRECT_TYPE_VALUE = 1;
	
	private TestMatcher systemUnderTest;

	private class TestMatcher extends TypedMatcher<String> {
		private String toMatch;
		
		public TestMatcher(String toMatch) {
			super(String.class);
			this.toMatch = toMatch;
		}
		
		@Override
		protected boolean internalMatches(String item) {
			return item.equals(toMatch);
		}
		
		@Override
		public void describeTo(Description description) {
			description.appendText(toMatch);
		}
	}
	
	@Before
	public void setUp() {
		systemUnderTest = new TestMatcher(DESIRED_VALUE);
	}
	
	@Test
	public void shouldMatchAllConditionsMet() {
		assertThat(systemUnderTest.matches(DESIRED_VALUE), is(true));
	}
	
	@Test
	public void shouldNotMatchWhenInternalMatchesDoesnt() {
		assertThat(systemUnderTest.matches(OTHER_VALUE), is(false));
	}
	
	@Test
	public void shouldNotMatchValueOfWrongType() {
		assertThat(systemUnderTest.matches(INCORRECT_TYPE_VALUE), is(false));
	}
	
	@Test
	public void shouldNotMatchNullValue() {
		assertThat(systemUnderTest.matches(null), is(false));
	}
}
