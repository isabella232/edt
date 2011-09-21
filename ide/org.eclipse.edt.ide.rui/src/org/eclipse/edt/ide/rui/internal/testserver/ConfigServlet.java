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
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.services.servlet.rest.rpc.PreviewServiceServlet;
import org.eclipse.osgi.util.NLS;

public class ConfigServlet extends HttpServlet {
	
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * The path of the servlet running inside the context root.
	 */
	public static final String SERVLET_PATH = "/config"; //$NON-NLS-1$
	
	/**
	 * The name of the argument for passing mapping additions.
	 */
	public static final String ARG_ADDED = "added"; //$NON-NLS-1$
	
	/**
	 * The name of the argument for passing mapping removals.
	 */
	public static final String ARG_REMOVED = "removed"; //$NON-NLS-1$
	
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
	
	public ConfigServlet(PreviewServiceServlet previewServlet) {
		this.previewServlet = previewServlet;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String added = req.getParameter(ARG_ADDED);
		String removed = req.getParameter(ARG_REMOVED);
		
		if (added != null && added.length() > 0) {
			parse(added, true);
		}
		if (removed != null && removed.length() > 0) {
			parse(removed, false);
		}
	}
	
	public void parse(String mappings, boolean added) {
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
					previewServlet.addServiceMapping(uri, className, stateful);
				}
				else {
					previewServlet.removeServiceMapping(uri);
				}
			}
			else {
				log(NLS.bind(TestServerMessages.ServiceMappingAdditionsInvalidTokens, token));
			}
		}
	}
}
