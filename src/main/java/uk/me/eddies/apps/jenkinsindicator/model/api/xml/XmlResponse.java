/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api.xml;

import java.net.URI;

import uk.me.eddies.apps.jenkinsindicator.model.api.Action;

/**
 * Represents a response from the Jenkins API which has been unmarshalled via JAXB.
 */
public interface XmlResponse {

	public Action constructAction(URI currentUri);
}
