/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static java.util.Objects.requireNonNull;

import java.time.ZonedDateTime;

/**
 * Represents a single build of a {@link Job}.
 */
public class ActualBuild implements Build {

	private final Job job;
	private final long number;
	private final ZonedDateTime startTime;
	private final BuildStatus status;
	
	public ActualBuild(Job job, long number, ZonedDateTime startTime, BuildStatus status) {
		if (!status.isValidActualBuildStatus()) throw new IllegalArgumentException("Status not applicable for an ActualBuild.");
		this.job = requireNonNull(job);
		this.number = number;
		this.startTime = requireNonNull(startTime);
		this.status = status;
	}

	@Override
	public Job getJob() {
		return job;
	}
	
	@Override
	public Long getNumber() {
		return number;
	}
	
	@Override
	public ZonedDateTime getStartTime() {
		return startTime;
	}
	
	@Override
	public BuildStatus getStatus() {
		return status;
	}
}
