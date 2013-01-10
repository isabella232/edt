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
package org.eclipse.edt.ide.deployment.core;

import java.io.FileReader;
import java.io.InputStream;

import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.core.model.DeploymentProject;
import org.eclipse.edt.ide.deployment.core.model.DeploymentTarget;
import org.eclipse.edt.ide.deployment.core.model.RUIApplication;
import org.eclipse.edt.ide.deployment.core.model.RUIHandler;
import org.eclipse.edt.ide.deployment.core.model.Service;
import org.eclipse.edt.javart.resources.egldd.Parameter;
import org.eclipse.edt.javart.resources.egldd.RuntimeDeploymentDescParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;


public class DeploymentDescParser extends RuntimeDeploymentDescParser {

	private Service currentService;
	private RUIApplication currentRuiApplication;
	private RUIHandler currentRUIHandler;
	private DeploymentTarget currentTarget;

	public DeploymentDesc parse(DeploymentDesc desc, String path)
			throws Exception {
		this.desc = desc;
		return (DeploymentDesc)parse(new InputSource(new FileReader(path)));
	}
	
	public DeploymentDesc parse(DeploymentDesc desc, InputStream is) throws Exception
	{
		this.desc = desc;
		return (DeploymentDesc)parse(new InputSource(is));
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (localName.equals("deployment")) {
		} 
		else if (localName.equals("service")) {
			currentService = new Service(attributes.getValue("type"),
                    attributes.getValue("implementation"));
		}
		else if (localName.equals( "target.project")) {
			currentTarget = new DeploymentProject(attributes.getValue("name"));
			((DeploymentDesc)desc).setTarget(currentTarget);
		}
		else if (localName.equals("ruiapplication")) {
			currentRuiApplication = new RUIApplication(
					attributes.getValue("name"),
					attributes.getValue("deployAllHandlers")
					);
			((DeploymentDesc)desc).setRUIApplication(currentRuiApplication);
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
				((DeploymentDesc)desc).addResourceOmission(attributes.getValue("id"));
			}
		}
		else{
			super.startElement(uri, localName, qName, attributes);
		}
	}

	protected void addParameter(Parameter param){
		if (currentService != null) {
			currentService.addParameter(param);
		}
		else if (currentRUIHandler != null) { // must be checked before solution
			currentRUIHandler.addParameter(param);
		}
		else if (currentRuiApplication != null) {
			currentRuiApplication.addParameter(param);
		}else if (currentTarget != null) {
			currentTarget.addParameter(param);
		}
		else{
			super.addParameter(param);
		}
	}
	public void endElement(String uri, String localName, String qName) {
		if (localName.equals("service")) {
			((DeploymentDesc)desc).addService(currentService);
			currentService = null;
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
		else{
			super.endElement(uri, localName, qName);
		}
	}
}
