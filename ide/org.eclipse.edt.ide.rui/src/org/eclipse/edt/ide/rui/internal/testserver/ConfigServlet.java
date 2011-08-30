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

public class ConfigServlet extends HttpServlet {
	
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * The path of the servlet running inside the context root.
	 */
	public static final String SERVLET_PATH = "/config";
	
	/**
	 * The name of the argument for passing mapping additions.
	 */
	public static final String ARG_ADDED = "added";
	
	/**
	 * The name of the argument for passing mapping removals.
	 */
	public static final String ARG_REMOVED = "removed";
	
	/**
	 * The delimeter used between mappings.
	 */
	public static final String MAPPING_ARG_DELIMETER = "|";
	
	/**
	 * The delimeter used between components of a mapping.
	 */
	public static final String MAPPING_DELIMETER = ";";
	
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
			parseAdditions(added);
		}
		if (removed != null && removed.length() > 0) {
			parseRemovals(removed);
		}
	}
	
	public void parseAdditions(String mappings) {
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
					uri = URLDecoder.decode(uri, "UTF-8");
					className = URLDecoder.decode(className, "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					// Shouldn't happen.
					uri = uri.replaceAll("%7C", "|").replaceAll("%3B", ";").replaceAll("%25", "%");
				}
				
				previewServlet.addServiceMapping(uri, className, stateful);
			}
			else {
				log("Invalid number of tokens in service mapping addition: " + token);
			}
		}
	}
	
	public void parseRemovals(String mappings) {
		StringTokenizer tok = new StringTokenizer(mappings, MAPPING_ARG_DELIMETER);
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			StringTokenizer subtok = new StringTokenizer(token, MAPPING_DELIMETER);
			
			if (subtok.countTokens() == 1) {
				String uri = subtok.nextToken();
				
				// uri will be encoded
				try {
					uri = URLDecoder.decode(uri, "UTF-8");
				}
				catch (UnsupportedEncodingException e) {
					// Shouldn't happen.
					uri = uri.replaceAll("%7C", "|").replaceAll("%3B", ";").replaceAll("%25", "%");
				}
				
				previewServlet.removeServiceMapping(uri);
			}
			else {
				log("Invalid number of tokens in service mapping removal: " + token);
			}
		}
	}
}
