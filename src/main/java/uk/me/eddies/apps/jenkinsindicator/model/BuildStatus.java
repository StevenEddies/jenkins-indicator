/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

/**
 * Represents the possible statuses of a {@link Build}.
 */
public enum BuildStatus {
	
	FAILURE(true),
	SUCCESS(true),
	UNSTABLE(true),
	ABORTED(true),
	NOT_BUILT(false);
	
	private boolean validActualBuildStatus;
	
	private BuildStatus(boolean validActualBuildStatus) {
		this.validActualBuildStatus = validActualBuildStatus;
	}
	
	boolean isValidActualBuildStatus() {
		return validActualBuildStatus;
	}
}
