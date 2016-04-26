/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

/**
 * Represents a job built on Jenkins.
 */
public class Job {

	private String name;
	
	public Job(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public JobStatus getStatus() {
		return JobStatus.UNKNOWN;
	}
}
