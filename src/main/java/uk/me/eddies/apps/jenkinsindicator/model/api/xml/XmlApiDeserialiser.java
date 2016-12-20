/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.model.api.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import uk.me.eddies.apps.jenkinsindicator.service.xml.XmlDeserialiser;

/**
 * {@link XmlDeserialiser} specialised to work with Jenkins API {@link XmlResponse}s.
 */
public class XmlApiDeserialiser extends XmlDeserialiser<XmlResponse> {
	
	private static final Class<?>[] API_ROOT_CLASSES = {
			ServerRootElement.class,
			JobRootElement.class
	};
	
	public XmlApiDeserialiser() throws JAXBException {
		super(JAXBContext.newInstance(API_ROOT_CLASSES), XmlResponse.class);
	}
}
