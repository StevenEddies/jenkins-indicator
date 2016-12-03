/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import java.time.Instant;

/**
 * Represents a single build of a {@link Job}.
 */
public interface Build {

	public Job getJob();
	
	public Long getNumber();
	
	public Instant getStartTime();
	
	public BuildStatus getStatus();
}
