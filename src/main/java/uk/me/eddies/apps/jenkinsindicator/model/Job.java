/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

import static java.util.Objects.requireNonNull;
import static uk.me.eddies.apps.jenkinsindicator.utility.ComparatorResult.EQUALS;
import static uk.me.eddies.apps.jenkinsindicator.utility.ComparatorResult.LESS_THAN;
import static uk.me.eddies.apps.jenkinsindicator.utility.ComparatorResult.compare;

import java.util.Objects;
import java.util.function.Function;

import uk.me.eddies.apps.jenkinsindicator.utility.ComparatorResult;

/**
 * Represents a job built on Jenkins.
 */
public class Job {

	private final String name;
	private volatile Build lastBuild;
	
	public Job(String name) {
		this.name = requireNonNull(name);
		this.lastBuild = new NoBuilds(this);
	}
	
	public String getName() {
		return name;
	}
	
	public Build getLastBuild() {
		return lastBuild;
	}

	private void setLastBuild(Build lastBuild) {
		this.lastBuild = lastBuild;
	}

	public BuildStatus getStatus() {
		return lastBuild.getStatus();
	}

	boolean updateBuildIfLater(Long buildNumber, Function<Job, Build> buildCreator) {
		
		requireNonNull(buildCreator);
		ComparatorResult newBuildToCurrent = compare(buildNumber, getLastBuild().getNumber(), new BuildNumberComparator());
		if (newBuildToCurrent == EQUALS) return false;
		if (newBuildToCurrent == LESS_THAN) throw new IllegalStateException("Build number jumps backwards.");
		
		Build build = buildCreator.apply(this);
		if (!Objects.equals(build.getNumber(), buildNumber)) throw new IllegalStateException("Wrong Build supplied.");
		setLastBuild(build);
		return true;
	}
}
