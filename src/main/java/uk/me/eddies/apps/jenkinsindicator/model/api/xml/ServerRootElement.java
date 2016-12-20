/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api.xml;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import uk.me.eddies.apps.jenkinsindicator.model.api.Action;

/**
 * {@link Action} from calling the API of the root Jenkins page.
 */
@XmlRootElement(name="hudson")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerRootElement extends JobContainer implements XmlResponse {
	
	public static final String QUERY_PARAMS = "tree=jobs[name,url]";

	@Override
	public Action constructAction(URI currentUri) {
		return null; // TODO
	}
}
