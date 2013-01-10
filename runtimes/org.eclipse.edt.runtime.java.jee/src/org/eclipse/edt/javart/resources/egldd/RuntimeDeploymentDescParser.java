/*******************************************************************************
 * Copyright © 2006, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.resources.egldd;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


public class RuntimeDeploymentDescParser extends DefaultHandler {

	protected RuntimeDeploymentDesc desc;

	private Binding currentBinding;
	private StringBuffer handledCharacters;

	public RuntimeDeploymentDesc parse(RuntimeDeploymentDesc desc, String path)
			throws Exception {
		this.desc = desc;
		return parse(new InputSource(new FileReader(path)));
	}
	
	public RuntimeDeploymentDesc parse(RuntimeDeploymentDesc desc, InputStream is) throws Exception
	{
		this.desc = desc;
		return parse(new InputSource(is));
	}

	protected RuntimeDeploymentDesc parse(InputSource source) throws IOException,
			SAXException {
		XMLReader xr = XMLReaderFactory.createXMLReader();
		xr.setContentHandler(this);
		xr.setErrorHandler(this);
		xr.parse(source);

		return desc;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (localName.equals("binding")) {
			currentBinding = new Binding(attributes.getValue("name"), 
					                           attributes.getValue("type"), 
					                           attributes.getValue("uri"),
					                           attributes.getValue("useURI"));
		} 
		else if (localName.equals("include")) {
			String location = attributes.getValue("location");
			// only add location if not null or empty
			if (location != null && location.trim().length() > 0)
				desc.addInclude(location);
		}
		else if (localName.equals("parameter")) {
			Parameter parameter = new Parameter(attributes.getValue("name"), 
						                    attributes.getValue("type"), 
						                    attributes.getValue("value"));
			addParameter(parameter);
		}
	}

	protected void addParameter(Parameter param){
		if(currentBinding != null){
			currentBinding.addParameter(param);
		}
	}
	public void endElement(String uri, String localName, String qName) {
		if (localName.equals("binding")) {
			desc.addBinding(currentBinding);
			currentBinding = null;
		}
	}
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if( handledCharacters != null ){
			for( int idx = 0; idx < length; idx ++){
				handledCharacters.append(ch[start + idx]);
			}
		}
		else{
			super.characters(ch, start, length);
		}
		
	}
}
