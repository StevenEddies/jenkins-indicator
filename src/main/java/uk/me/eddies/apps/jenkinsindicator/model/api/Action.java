/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

import java.util.function.Consumer;

/**
 * Represents an action to be performed as a result of a Jenkins API call.
 */
@FunctionalInterface
public interface Action extends Consumer<ActionConnector> {

	// Defined by superinterface
}
