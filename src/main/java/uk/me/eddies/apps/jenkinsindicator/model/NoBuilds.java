/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import java.time.OffsetDateTime;

/**
 * Represents the lack of a build, i.e. the state before any builds have built.
 */
public class NoBuilds implements Build {

	private Job job;
	
	public NoBuilds(Job job) {
		this.job = job;
	}
	
	@Override
	public Job getJob() {
		return job;
	}

	@Override
	public Long getNumber() {
		return null;
	}

	@Override
	public OffsetDateTime getStartTime() {
		return null;
	}

	@Override
	public BuildStatus getStatus() {
		return BuildStatus.NOT_BUILT;
	}
}
