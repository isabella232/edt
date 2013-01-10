/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.record.conversion.xmlschema;

import java.io.InputStream;
import java.io.Reader;

import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.eclipse.edt.ide.ui.internal.record.conversion.RecordConversion;
import org.eclipse.edt.ide.ui.internal.record.conversion.RecordSource;
import org.eclipse.edt.ide.ui.templates.parts.Part;
import org.eclipse.edt.ide.ui.templates.parts.Record;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.LSInput;

public class XMLSchemaConversion extends RecordConversion implements DOMErrorHandler {
	
	protected Part[] doConvert(final RecordSource input) {
		String xmlSchema = input.getContent();
		Part[] parts = null;
		ok(false);

		if (xmlSchema.trim().length() > 0) {
			try {
				DOMImplementationRegistry domRegistry = DOMImplementationRegistry.newInstance();
				XSImplementation xsImpl = (XSImplementation) domRegistry.getDOMImplementation("XS-Loader");
				XSLoader xsLoader = xsImpl.createXSLoader(null);
				xsLoader.getConfig().setParameter("error-handler", this);
				
				XSModel xsModel;
				if(input.getSource() == 1) {
					//Create record from copied string
					xsModel = xsLoader.load(new GenericLSInput(xmlSchema));
				} else {
					//Create record from URL or file
					xsLoader.getConfig().setParameter("http://apache.org/xml/features/honour-all-schemaLocations", true);
					xsModel = xsLoader.loadURI(xmlSchema);
				}
			
				if (xsModel != null)
					parts = createParts(xsModel);
				ok(xsModel != null);
			} catch (Throwable ex) {
				//ex.printStackTrace();
				error(ex.getMessage());
			}
		}
		return parts;
	}

	private Part[] createParts(XSModel xsModel) {
		Record rec = new Record();
		return new PartsFromXMLSchemaUtil(getMessageHandler()).process(xsModel, rec);
	}

	public boolean handleError(DOMError error) {
		StringBuffer buffer = new StringBuffer();
		if (error.getLocation().getUri() != null)
			buffer.append("[" + error.getLocation().getUri() + "] ");
		if (error.getLocation().getLineNumber() != -1)
			buffer.append("[" + error.getLocation().getLineNumber() + "," + error.getLocation().getColumnNumber() + "] ");
		if (buffer.length() > 0) {
			buffer.append(error.getMessage());
			if (error.getSeverity() == DOMError.SEVERITY_FATAL_ERROR)
				this.error(buffer.toString());
			else
				this.addMessage(buffer.toString());
		} else {
			buffer.append(error.getMessage());
			if ((error.getSeverity() == DOMError.SEVERITY_FATAL_ERROR) 
				|| (error.getSeverity() == DOMError.SEVERITY_ERROR)) {
				this.error(buffer.toString());
			}
		}
		return error.getSeverity() != DOMError.SEVERITY_FATAL_ERROR;
	}

	public static class GenericLSInput implements LSInput {
		
		private String xmlSchema;

		public GenericLSInput(String xmlSchema) {
			super();
			this.xmlSchema = xmlSchema;
		}

		public String getBaseURI() {
			return null;
		}

		public InputStream getByteStream() {
			return null;
		}

		public boolean getCertifiedText() {
			return false;
		}

		public Reader getCharacterStream() {
			return null;
		}

		public String getEncoding() {
			return null;
		}

		public String getPublicId() {
			return null;
		}

		public String getStringData() {
			return xmlSchema;
		}

		public String getSystemId() {
			return null;
		}

		public void setBaseURI(String arg0) {
		}

		public void setByteStream(InputStream arg0) {
		}

		public void setCertifiedText(boolean arg0) {
		}

		public void setCharacterStream(Reader arg0) {
		}

		public void setEncoding(String arg0) {
		}

		public void setPublicId(String arg0) {
		}

		public void setStringData(String arg0) {
		}

		public void setSystemId(String arg0) {
		}
	}
}
