/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.service.xml;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Responsible for using JAXB to deserialise an {@link InputStream} containing XML
 * into a Java object tree.
 * @param <T> the type of Java object to produce.
 */
public class XmlDeserialiser<T> {

	private final JAXBContext context;
	private final Class<T> objectType;
	private final ThreadLocal<Unmarshaller> unmarshaller;
	
	public XmlDeserialiser(JAXBContext context, Class<T> objectType) {
		this.context = context;
		this.objectType = objectType;
		this.unmarshaller = ThreadLocal.withInitial(this::createUnmarshaller);
	}
	
	public T deserialise(InputStream stream) throws JAXBException {
		return objectType.cast(unmarshaller.get().unmarshal(stream));
	}
	
	private Unmarshaller createUnmarshaller() {
		try {
			return context.createUnmarshaller();
		} catch (JAXBException ex) {
			throw new RuntimeException(ex);
		}
	}
}
