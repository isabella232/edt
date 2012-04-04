/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package eglx.xml;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;

import eglx.lang.AnyException;
import eglx.lang.AnyValue;
import eglx.lang.InvalidArgumentException;

public class XmlLib extends ExecutableBase {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public XmlLib() {
	}

	public static String convertToXML(Object storage, boolean buildDocument) throws AnyException {
		if (storage instanceof AnyBoxedObject) {
			storage = ((AnyBoxedObject<?>) storage).ezeUnbox();
		}
		try {
			Writer writer = new StringWriter();
			Marshaller marshaller = JAXBContext.newInstance(storage.getClass()).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, !buildDocument);
			marshaller.marshal(storage, writer);
			return writer.toString();
		}
		catch (Exception e) {
			InvalidArgumentException ex = new InvalidArgumentException();
			ex.initCause( e );
			throw ex.fillInMessage( Message.EGL2XML_ERROR, storage, e );
		}
	}

	public static void convertFromXML(String xml, final Object storage) throws AnyException {
		try {
			Object egl;
			if (storage instanceof AnyBoxedObject) {
				egl = ((AnyBoxedObject) storage).ezeUnbox();
			} else {
				egl = storage;
			}

			Reader reader = new StringReader(xml);
			Object obj = JAXBContext.newInstance(egl.getClass()).createUnmarshaller().unmarshal(reader);
			Method method;
			if ((method = egl.getClass().getMethod("ezeCopy", AnyValue.class)) != null) {
				method.invoke(egl, obj);
			}
		}
		catch (Exception e) {
			InvalidArgumentException ex = new InvalidArgumentException();
			ex.initCause( e );
			throw ex.fillInMessage( Message.XML2EGL_ERROR, xml, e );
		}
	}

}
