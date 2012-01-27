/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.internal.testserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.services.servlet.rest.rpc.PreviewServiceServlet;

import resources.edt.binding.IDEBindingResourceProcessor;


public class ConfigServlet extends HttpServlet {
	
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * The path of the servlet running inside the context root.
	 */
	public static final String SERVLET_PATH = "/config"; //$NON-NLS-1$
	
	/**
	 * The name of the argument for setting the default deployment descriptor name.
	 */
	public static final String ARG_DEFAULT_DD = "defaultDD"; //$NON-NLS-1$
	
	/**
	 * The name of the argument for passing deployment descriptor additions.
	 */
	public static final String ARG_DD_ADDED = "ddAdded"; //$NON-NLS-1$
	
	/**
	 * The name of the argument for passing deployment descriptor removals.
	 */
	public static final String ARG_DD_REMOVED = "ddRemoved"; //$NON-NLS-1$
	
	/**
	 * The name of the argument for passing mapping additions.
	 */
	public static final String ARG_MAPPING_ADDED = "mappingAdded"; //$NON-NLS-1$
	
	/**
	 * The name of the argument for passing mapping removals.
	 */
	public static final String ARG_MAPPING_REMOVED = "mappingRemoved"; //$NON-NLS-1$
	
	/**
	 * The delimeter used between mappings.
	 */
	public static final String MAPPING_ARG_DELIMETER = "|"; //$NON-NLS-1$
	
	/**
	 * The delimeter used between components of a mapping.
	 */
	public static final String MAPPING_DELIMETER = ";"; //$NON-NLS-1$
	
	/**
	 * The servlet which is being updated.
	 */
	private final PreviewServiceServlet previewServlet;
	
	/**
	 * The resource locator for resource bindings.
	 */
	private final IDEBindingResourceProcessor bindingProcessor;
	
	public ConfigServlet(PreviewServiceServlet previewServlet, IDEBindingResourceProcessor bindingProcessor) {
		this.previewServlet = previewServlet;
		this.bindingProcessor = bindingProcessor;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String addedMappings = req.getParameter(ARG_MAPPING_ADDED);
		String removedMappings = req.getParameter(ARG_MAPPING_REMOVED);
		String addedDDs = req.getParameter(ARG_DD_ADDED);
		String removedDDs = req.getParameter(ARG_DD_REMOVED);
		String defaultDD = req.getParameter(ARG_DEFAULT_DD);
		
		if (addedMappings != null && addedMappings.length() > 0) {
			parseMappings(addedMappings, true);
		}
		if (removedMappings != null && removedMappings.length() > 0) {
			parseMappings(removedMappings, false);
		}
		if (addedDDs != null && addedDDs.length() > 0) {
			parseDDFiles(addedDDs, true);
		}
		if (removedDDs != null && removedDDs.length() > 0) {
			parseDDFiles(removedDDs, false);
		}
		if (defaultDD != null) {
			TestServer.log("Default DD changed: " + defaultDD); //$NON-NLS-1$
			bindingProcessor.setDefaultDD(defaultDD);
		}
	}
	
	public void parseMappings(String mappings, boolean added) {
		StringTokenizer tok = new StringTokenizer(mappings, MAPPING_ARG_DELIMETER);
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			StringTokenizer subtok = new StringTokenizer(token, MAPPING_DELIMETER);
			
			if (subtok.countTokens() == 3) {
				String uri = subtok.nextToken();
				String className = subtok.nextToken();
				boolean stateful = Boolean.valueOf(subtok.nextToken());
				
				// uri and className will be encoded
				try {
					uri = URLDecoder.decode(uri, "UTF-8"); //$NON-NLS-1$
					className = URLDecoder.decode(className, "UTF-8"); //$NON-NLS-1$
				}
				catch (UnsupportedEncodingException e) {
					// Shouldn't happen.
					uri = uri.replaceAll("%7C", "|").replaceAll("%3B", ";").replaceAll("%25", "%"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				}
				
				if (added) {
					TestServer.log("Service mapping added or changed: " + uri + ", " + className); //$NON-NLS-1$ //$NON-NLS-2$
					previewServlet.addServiceMapping(uri, className, stateful);
				}
				else {
					TestServer.log("Service mapping removed: " + uri + ", " + className); //$NON-NLS-1$ //$NON-NLS-2$
					previewServlet.removeServiceMapping(uri);
				}
			}
			else {
				TestServer.log("Invalid number of tokens in service mapping " + (added ? "addition: " : "removal: ") + token); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}
	
	public void parseDDFiles(String ddFiles, boolean added) {
		List<String[]> parsed = bindingProcessor.getResourceLocator().parseDDArgument(ddFiles, added);
		for (String[] next : parsed) {
			if (added) {
				TestServer.log("DD file added or changed: " + next[0] + ", " + next[1]); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				TestServer.log("DD file removed: " + next[0] + ", " + next[1]); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
}
