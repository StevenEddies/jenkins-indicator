/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api;

import java.net.URI;

/**
 * Represents something which can trigger asynchronous calls to API URIs.
 */
public interface ApiTrigger {

	public void triggerCall(URI uri);
}
