/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.core;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.edt.ide.deployment.core.model.DeploymentBuildDescriptor;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.core.model.DeploymentProject;
import org.eclipse.edt.ide.deployment.core.model.DeploymentTarget;
import org.eclipse.edt.ide.deployment.core.model.EGLBinding;
import org.eclipse.edt.ide.deployment.core.model.NativeBinding;
import org.eclipse.edt.ide.deployment.core.model.Parameter;
import org.eclipse.edt.ide.deployment.core.model.RUIApplication;
import org.eclipse.edt.ide.deployment.core.model.RUIHandler;
import org.eclipse.edt.ide.deployment.core.model.RestBinding;
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.deployment.core.model.SQLDatabaseBinding;
import org.eclipse.edt.ide.deployment.core.model.WebBinding;
import org.eclipse.edt.ide.deployment.core.model.Webservice;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


public class DeploymentDescParser extends DefaultHandler {

	private DeploymentDesc desc;

	private EGLBinding currentEGLBinding;
	private NativeBinding currentNativeBinding;
	private Webservice currentWebService;
	private Restservice currentRestService;
	private RUIApplication currentRuiApplication;
	private RUIHandler currentRUIHandler;
	private DeploymentTarget currentTarget;
	private StringBuffer handledCharacters;

	public DeploymentDesc parse(DeploymentDesc desc, String path)
			throws Exception {
		this.desc = desc;
		return parse(new InputSource(new FileReader(path)));
	}
	
	public DeploymentDesc parse(DeploymentDesc desc, InputStream is) throws Exception
	{
		this.desc = desc;
		return parse(new InputSource(is));
	}

	private DeploymentDesc parse(InputSource source) throws IOException,
			SAXException {
		currentEGLBinding = null;
		XMLReader xr = XMLReaderFactory.createXMLReader();
		xr.setContentHandler(this);
		xr.setErrorHandler(this);
		xr.parse(source);

		return desc;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		if (localName.equals("deployment")) {
			desc.setAlias(attributes.getValue("alias"));
		} 
		else if (localName.equals("eglBinding")) {
			currentEGLBinding = new EGLBinding(attributes.getValue("name"), 
					                           attributes.getValue("serviceName"), 
					                           attributes.getValue("alias"));
			desc.addEGLBinding(currentEGLBinding);
		} 
		else if (localName.equals("nativeBinding")) {
			currentNativeBinding = new NativeBinding(attributes.getValue("name")); 

			desc.addNativeBinding(currentNativeBinding);
		} 
		else if (localName.equals("webBinding")) {
			desc.addWebBinding(new WebBinding(attributes.getValue("name"),
											  attributes.getValue("interface"),
											  attributes.getValue("wsdlLocation"),
											  attributes.getValue("wsdlPort"),
											  attributes.getValue("wsdlService"),
											  attributes.getValue("uri"),
											  attributes.getValue("soapVersion"),
											  attributes.getValue("enableGeneration")));
		}
		else if (localName.equals("restBinding")) {
			desc.addRestBinding(new RestBinding(attributes.getValue("name"),
											    attributes.getValue("baseURI"),
											    attributes.getValue("sessionCookieId"),
											    attributes.getValue("preserveRequestHeaders"),
											    attributes.getValue("enableGeneration")));
		}
		else if (localName.equals("sqlDatabaseBinding")) {
			desc.addSqlDatabaseBindings(new SQLDatabaseBinding(attributes.getValue("name"),
												attributes.getValue("dbms"),
											    attributes.getValue("sqlJDBCDriverClass"),
											    attributes.getValue("sqlDB"),
											    attributes.getValue("sqlID"),
											    attributes.getValue("sqlPassword"),
											    attributes.getValue("sqlJNDIName"),
											    attributes.getValue("sqlSchema"),
											    attributes.getValue("sqlValidationConnectionURL"),
											    attributes.getValue("jarList")));
		}
		else if (localName.equals("webservice")) {
			currentWebService = new Webservice(attributes.getValue("implementation"),
                    attributes.getValue("style"),
                    attributes.getValue("protocol"),
                    attributes.getValue("transaction"),
                    attributes.getValue("uri"),
                    attributes.getValue("userID"),
                    attributes.getValue("useExistingWSDL"),
                    attributes.getValue("wsdlLocation"),
                    attributes.getValue("wsdlService"),
                    attributes.getValue("wsdlPort"),
					  attributes.getValue("soapVersion"),
                    attributes.getValue("enableGeneration"));
			desc.addWebservice(currentWebService);
		}
		else if (localName.equals("restservice")) {
			currentRestService = new Restservice(attributes.getValue("implementation"),
                    attributes.getValue("uri"),
                    attributes.getValue("protocol"),
                    attributes.getValue("stateful"),
                    attributes.getValue("enableGeneration"));
			desc.addRestservice(currentRestService);
		}
		else if (localName.equals("include")) {
			String location = attributes.getValue("location");
			// only add location if not null or empty
			if (location != null && location.trim().length() > 0)
				desc.addInclude(location);
		}
		else if (localName.equals( "target.project")) {
			currentTarget = new DeploymentProject(attributes.getValue("name"));
			desc.setTarget(currentTarget);
		}
		else if (localName.equals( "target.buildDescriptor")) {
			currentTarget = new DeploymentBuildDescriptor(
									attributes.getValue("name"),
									attributes.getValue("fileName"));
			desc.setTarget(currentTarget);
		}
		else if (localName.equals("ruiapplication")) {
			currentRuiApplication = new RUIApplication(
					attributes.getValue("name"),
					attributes.getValue("deployAllHandlers"),
					attributes.getValue("supportDynamicLoading")
					);
			desc.setRUIApplication(currentRuiApplication);
		}
		else if (localName.equals("ruihandler")) {
			currentRUIHandler = new RUIHandler(
					attributes.getValue("implementation"),
					attributes.getValue("enableGeneration"));
			if (currentRuiApplication != null) {
				currentRuiApplication.addRUIHandler(currentRUIHandler);
			}
		}
		else if (localName.equals("resource")) {
			if (desc != null) {
				desc.addResourceOmission(attributes.getValue("id"));
			}
		}
		else if (localName.equals("parameter")) {
			Parameter param = new Parameter(
					attributes.getValue("name"),
					attributes.getValue("value"));
			if (currentRestService != null) {
				currentRestService.addParameter(param);
			}
			else if (currentWebService != null) {
				currentWebService.addParameter(param);
			}
			else if (currentRUIHandler != null) { // must be checked before solution
				currentRUIHandler.addParameter(param);
			}
			else if (currentRuiApplication != null) {
				currentRuiApplication.addParameter(param);
			}else if (currentTarget != null) {
				currentTarget.addParameter(param);
			}
		}
		else if (localName.equals("webserviceRuntime")) {
			handledCharacters = new StringBuffer();
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (localName.equals("eglBinding")) {
			currentEGLBinding = null;
		}
		else if (localName .equals("nativeBinding")) {
			currentNativeBinding = null;
		}
		else if (localName.equals("webservice")) {
			currentWebService = null;
		}
		else if (localName.equals("restservice")) {
			currentRestService = null;
		}
		else if (localName.equals("ruiapplication")) {
			currentRuiApplication = null;
		}
		else if (localName.equals("ruihandler")) {
			currentRUIHandler = null;
		}
		else if (localName.equals("target.project") || localName.equals("target.directory")) {
			currentTarget = null;
		}
		else if (localName.equals("webserviceRuntime")) {
			if (desc != null) {this.
				desc.setWebserviceRuntime(handledCharacters == null ? "" : handledCharacters.toString());
				handledCharacters = null;
			}
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
