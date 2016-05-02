/* Copyright Steven Eddies, 2016. See the LICENCE file in the project root. */

package uk.me.eddies.apps.jenkinsindicator.service.xml;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class XmlDeserialiserTest {

	@Mock private JAXBContext context;
	@Mock private InputStream stream;
	@Mock private Unmarshaller unmarshaller;
	
	private XmlDeserialiser<T> systemUnderTest;
	
	private class T { /* Does nothing */ }
	
	@Before
	public void setUp() {
		initMocks(this);
		systemUnderTest = new XmlDeserialiser<>(context, T.class);
	}
	
	@Test
	public void shouldUnmarshal() throws JAXBException {
		T t = new T();
		when(context.createUnmarshaller()).thenReturn(unmarshaller);
		when(unmarshaller.unmarshal(stream)).thenReturn(t);
		
		assertThat(systemUnderTest.deserialise(stream), sameInstance(t));
	}
	
	@Test(expected=ClassCastException.class)
	public void shouldFailToUnmarshalWrongType() throws JAXBException {
		Object notT = new Object();
		when(context.createUnmarshaller()).thenReturn(unmarshaller);
		when(unmarshaller.unmarshal(stream)).thenReturn(notT);
		
		systemUnderTest.deserialise(stream);
	}
	
	@Test(expected=RuntimeException.class)
	public void shouldFailOnJaxbException() throws JAXBException {
		doThrow(JAXBException.class).when(context).createUnmarshaller();
		
		systemUnderTest.deserialise(stream);
	}
}
