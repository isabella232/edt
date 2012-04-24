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
package resources.edt.binding;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.egldd.RuntimeDeploymentDesc;

import eglx.java.JavaObjectException;
import eglx.lang.AnyException;
import eglx.lang.Resources.ResourceLocator;

/**
 * Implements SysLib.ResourceLocator to handle locating deployment descriptors.
 */
public class RuntimeResourceLocator implements ResourceLocator {
	
	protected static Map<String, RuntimeDeploymentDesc> deploymentDescs = new HashMap<String, RuntimeDeploymentDesc>();
	
	/**
	 * Constructor.
	 */
	public RuntimeResourceLocator() {
	}
	
	public RuntimeDeploymentDesc getDeploymentDesc(URI propertyFileUri) throws AnyException{
		RuntimeDeploymentDesc dd = deploymentDescs.get(propertyFileUri.toString());
		if(dd == null){
			if(!propertyFileUri.toString().endsWith("-bnd.xml")){
				try {
					propertyFileUri = new URI(propertyFileUri.toString() + "-bnd.xml");
				} catch (URISyntaxException e) {
					JavaObjectException jox = new JavaObjectException();
					jox.exceptionType = URI.class.getName();
					jox.initCause( e );
					throw jox.fillInMessage( Message.RESOURCE_URI_EXCEPTION, propertyFileUri );
				}
			}
			InputStream is = null;
			File f = new File(propertyFileUri.getSchemeSpecificPart());
			if(f.isAbsolute()){
				try {
					is = f.toURI().toURL().openStream();
				} catch (MalformedURLException e) {
				} catch (IOException e) {
				}
			}
			else{
				is = org.eclipse.edt.javart.Runtime.getRunUnit().getClass().getClassLoader().getResourceAsStream(f.getName());
			}
			if(is == null){
				AnyException ex = new AnyException();
				throw ex.fillInMessage( Message.RESOURCE_FILE_NOT_FOUND, propertyFileUri.toString() );
			}
			else{
				try {
					dd = RuntimeDeploymentDesc.createDeploymentDescriptor(propertyFileUri.toString(), is);
				} catch (Exception e) {
					AnyException ex = new AnyException();
					throw ex.fillInMessage( Message.ERROR_PARSING_RESOURCE_FILE, propertyFileUri.toString(), e );
				}
				deploymentDescs.put(propertyFileUri.toString(), dd);
			}
		}
		return dd;
	}
}
