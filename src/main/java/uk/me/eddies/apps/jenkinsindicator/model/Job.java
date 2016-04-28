/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static java.util.Objects.requireNonNull;

/**
 * Represents a job built on Jenkins.
 */
public class Job {

	private final String name;
	private volatile Build lastBuild;
	
	public Job(String name) {
		requireNonNull(name);
		this.name = name;
		this.lastBuild = new NoBuilds(this);
	}
	
	public String getName() {
		return name;
	}
	
	public Build getLastBuild() {
		return lastBuild;
	}

	public void setLastBuild(Build lastBuild) {
		requireNonNull(lastBuild);
		this.lastBuild = lastBuild;
	}

	public BuildStatus getStatus() {
		return lastBuild.getStatus();
	}
}
