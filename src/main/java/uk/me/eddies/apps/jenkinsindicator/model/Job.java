/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

/**
 * Represents a job built on Jenkins.
 */
public class Job {

	private String name;
	private Build lastBuild;
	
	public Job(String name) {
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
		this.lastBuild = lastBuild;
	}

	public BuildStatus getStatus() {
		return lastBuild.getStatus();
	}
}
