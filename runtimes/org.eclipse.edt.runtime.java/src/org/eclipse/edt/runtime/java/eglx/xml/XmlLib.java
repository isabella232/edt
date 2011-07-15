package org.eclipse.edt.runtime.java.eglx.xml;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.resources.ExecutableBase;

import egl.lang.AnyValue;

public class XmlLib extends ExecutableBase {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	public XmlLib( RunUnit ru ) {
		super( ru );
	}
	
	public String convertToXML(Object storage, boolean buildDocument) throws JavartException{
		if(storage instanceof AnyBoxedObject){
			storage = ((AnyBoxedObject)storage).ezeUnbox();
		}
		try{
			Writer writer = new StringWriter();
			JAXBContext.newInstance(storage.getClass()).createMarshaller().marshal(storage, writer);
			return writer.toString();
		}
		catch(Exception e){
			throw new JavartException(e);
		}
	}

	public void convertFromXML(String xml, final Object storage) throws JavartException{
		try{
			Object egl;
			if(storage instanceof AnyBoxedObject){
				egl = ((AnyBoxedObject)storage).ezeUnbox();
			}
			else{
				egl = storage;
			}
			
			Reader reader = new StringReader(xml);
			Object obj = JAXBContext.newInstance(egl.getClass()).createUnmarshaller().unmarshal(reader);
			Method method;
			if((method = egl.getClass().getMethod("ezeCopy", AnyValue.class)) != null){
				method.invoke(egl, obj);
			}
		}
		catch(Exception e){
			throw new JavartException(e);
		}
	}

}
