/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import java.time.Instant;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * {@link Matcher} for a {@link Build}.
 */
public class BuildMatcher extends TypeSafeMatcher<Build> {
	
	private final Matcher<? super String> jobName;
	private final Matcher<? super Long> buildNumber;
	private final Matcher<? super Instant> startTime;
	private final Matcher<? super BuildStatus> status;
	
	public BuildMatcher(
			Matcher<? super String> jobName,
			Matcher<? super Long> buildNumber,
			Matcher<? super Instant> startTime,
			Matcher<? super BuildStatus> status
	) {
		super(Build.class);
		this.jobName = jobName;
		this.buildNumber = buildNumber;
		this.startTime = startTime;
		this.status = status;
	}
	
	@Override
	protected boolean matchesSafely(Build item) {
		return jobName.matches(item.getJob().getName())
				&& buildNumber.matches(item.getNumber())
				&& startTime.matches(item.getStartTime())
				&& status.matches(item.getStatus());
	}
	
	@Override
	public void describeTo(Description description) {
		description.appendText("build with [")
			.appendText("job: ").appendDescriptionOf(jobName).appendText(", ")
			.appendText("no: ").appendDescriptionOf(buildNumber).appendText(", ")
			.appendText("time: ").appendDescriptionOf(startTime).appendText(", ")
			.appendText("status: ").appendDescriptionOf(status).appendText("]");
	}
}
