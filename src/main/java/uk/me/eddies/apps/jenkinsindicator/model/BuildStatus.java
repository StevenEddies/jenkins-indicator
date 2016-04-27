/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model;

/**
 * Represents the possible statuses of a {@link Build}.
 */
public enum BuildStatus {
	
	FAILED,
	STABLE,
	UNSTABLE,
	NOT_BUILT;
}
