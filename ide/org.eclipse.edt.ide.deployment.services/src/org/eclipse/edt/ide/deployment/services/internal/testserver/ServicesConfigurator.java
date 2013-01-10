/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.services.internal.testserver;

import java.io.InputStream;
import java.util.List;

import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.testserver.AbstractConfigurator;
import org.eclipse.edt.ide.testserver.LogLevel;
import org.eclipse.edt.ide.testserver.TestServer;
import org.eclipse.edt.javart.ide.IDEBindingResourceProcessor;
import org.eclipse.edt.javart.ide.IDEResourceLocator;
import org.eclipse.edt.javart.services.servlet.proxy.AjaxProxyServlet;
import org.eclipse.edt.javart.services.servlet.rest.rpc.PreviewServiceServlet;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import eglx.lang.Resources;


public class ServicesConfigurator extends AbstractConfigurator {
	
	private String initialOrderedDDNames;
	private String initialDDFiles;
	private String initialDefaultDD;
	private ConfigServlet configServlet;
	private PreviewServiceServlet previewServlet;
	private IDEBindingResourceProcessor bindingProcessor;
	
	/**
	 * This configurator supports the following arguments:
	 * <ul>
	 * <li>-odd &lt;DD names&gt; (a string in the format: <i>ddName</i> [|<i>ddName</i>...]) where <i>ddName</i> is the name of a deployment descriptor,
	 *     and the order of the ddNames is the order in which the files take precedence</li>
	 * <li>-dd &lt;DD files&gt; (a string where the delimiter is File.pathSeparatorChar and each token alternates between a dd name and its absolute path, with
	 *     the name listed first, e.g. ddName1;/path/to/ddName1.egldd;ddName2;/path/to/ddName2.egldd</li>
	 * <li>-ddd &lt;default DD name&gt; (the name of the default deployment descriptor)</li>
	 * </ul>
	 */
	@Override
	public int processNextArgument(List<String> arguments, int currentIndex) {
		String arg = arguments.get(currentIndex);
		if ("-odd".equals(arg)) { //$NON-NLS-1$
			if (currentIndex + 1 < arguments.size()) {
				initialOrderedDDNames = arguments.get(currentIndex + 1).trim();
				currentIndex++;
			}
			else {
				TestServer.logWarning("Missing ordered deployment descriptor name list value for argument \"" + arg + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			currentIndex++;
		}
		else if ("-dd".equals(arg)) { //$NON-NLS-1$
			if (currentIndex + 1 < arguments.size()) {
				initialDDFiles = arguments.get(currentIndex + 1).trim();
				currentIndex++;
			}
			else {
				TestServer.logWarning("Missing deployment descriptor file list value for argument \"" + arg + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			currentIndex++;
		}
		else if ("-ddd".equals(arg)) { //$NON-NLS-1$
			if (currentIndex + 1 < arguments.size()) {
				initialDefaultDD = arguments.get(currentIndex + 1).trim();
				currentIndex++;
			}
			else {
				TestServer.logWarning("Missing default deployment descriptor value for argument \"" + arg + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			currentIndex++;
		}
		
		return currentIndex;
	}
	
	@Override
	public void preStartup() throws Exception {
		// JNDI requires EnvConfiguration which creates java:comp/env.
		server.appendConfiguration(new EnvConfiguration());
	}
	
	@Override
	public void configure() {
		WebAppContext context = server.getWebApp();
		
		// Servlet to handle REST services
		previewServlet = new PreviewServiceServlet();
		context.addServlet(new ServletHolder(previewServlet), "/restservices/*"); //$NON-NLS-1$
		
		// Servlet to handle dedicated services
		context.addServlet(new ServletHolder(new AjaxProxyServlet()), "/___proxy"); //$NON-NLS-1$
		
		// Register the resource locator for dynamically finding resource bindings.
		bindingProcessor = new IDEBindingResourceProcessor(server.getIDEPort(), new IDEResourceLocator());
		Resources.setBindingResourceProcessor(bindingProcessor);
		
		// Use the full DD parser which supports more than just bindings.
		try {
			bindingProcessor.getResourceLocator().setDDParser(DeploymentDesc.class.getMethod("createDeploymentDescriptor", new Class[]{String.class, InputStream.class})); //$NON-NLS-1$
		}
		catch (Exception e) {
			server.log("Could not use full deployment descriptor parser. REST services will not be available!", LogLevel.WARN); //$NON-NLS-1$ //$NON-NLS-2$
			server.log(e);
		}
		
		// Servlet to handle configuration changes automatically.
		configServlet = new ConfigServlet(previewServlet, bindingProcessor, server);
		context.addServlet(new ServletHolder(configServlet), "/" + ConfigServlet.SERVLET_PATH); //$NON-NLS-1$
		if (initialDDFiles != null && initialDDFiles.length() > 0) {
			configServlet.parseDDFiles(initialDDFiles, true);
		}
		if (initialOrderedDDNames != null && initialOrderedDDNames.length() > 0) {
			configServlet.parseOrderedDDs(initialOrderedDDNames);
		}
		if (initialDefaultDD != null) {
			configServlet.setDefaultDD(initialDefaultDD);
		}
	}
	
	@Override
	public void postConfigure() throws Exception {
		configServlet.configureServiceMappings();
		configServlet.configureJNDIEnvironment();
	}
}
