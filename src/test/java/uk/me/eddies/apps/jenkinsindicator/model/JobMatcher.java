/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * {@link Matcher} for a {@link Job}.
 */
public class JobMatcher extends TypeSafeMatcher<Job> {
	
	private final Matcher<? super String> name;
	private final Matcher<? super Build> lastBuild;
	
	public JobMatcher(
			Matcher<? super String> name,
			Matcher<? super Build> lastBuild
	) {
		super(Job.class);
		this.name = name;
		this.lastBuild = lastBuild;
	}
	
	@Override
	protected boolean matchesSafely(Job item) {
		return name.matches(item.getName())
				&& lastBuild.matches(item.getLastBuild());
	}
	
	@Override
	public void describeTo(Description description) {
		description.appendText("job with [")
			.appendText("name: ").appendDescriptionOf(name).appendText(", ")
			.appendText("lastBuild: ").appendDescriptionOf(lastBuild).appendText("]");
	}
}
