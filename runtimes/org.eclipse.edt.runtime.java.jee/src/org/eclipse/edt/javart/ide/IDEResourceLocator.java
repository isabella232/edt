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
package org.eclipse.edt.javart.ide;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.egldd.RuntimeDeploymentDesc;

import resources.edt.binding.BindingResourceProcessor;
import resources.edt.binding.RuntimeResourceLocator;
import eglx.lang.AnyException;

/**
 * Implements SysLib.ResourceLocator to handle locating deployment descriptors for a Java test environment, as well as
 * supporting special binding URIs such as "workspace://".
 */
public class IDEResourceLocator extends RuntimeResourceLocator {
	
	/**
	 * Maps DD names to absolute filesystem paths.
	 */
	private Map<String,String> ddPaths = new HashMap<String, String>();
	
	/**
	 * An alternate (static) method that parses the DD file. If unspecified then {@link RuntimeDeploymentDesc#createDeploymentDescriptor(String, InputStream)}
	 * will be used. It MUST have the same signature as {@link RuntimeDeploymentDesc#createDeploymentDescriptor(String, InputStream)}.
	 */
	private Method ddParserMethod;
	
	/**
	 * Constructor.
	 * 
	 * @param idePort  The port on which the IDE can be reached.
	 */
	public IDEResourceLocator() {
	}
	
	/**
	 * Sets the alternate (static) method that parses the DD file. If unspecified then {@link RuntimeDeploymentDesc#createDeploymentDescriptor(String, InputStream)}
	 * will be used. It MUST have the same signature as {@link RuntimeDeploymentDesc#createDeploymentDescriptor(String, InputStream)}.
	 */
	public void setDDParser(Method method) {
		this.ddParserMethod = method;
	}
	
	
	
	@Override
	public RuntimeDeploymentDesc getDeploymentDesc(URI propertyFileUri) {
		RuntimeDeploymentDesc dd = getDeploymentDesc(propertyFileUri.toString());
		if (dd == null) {
			// Not a file we know about - see if there's a -bnd.xml on the classpath.
			return super.getDeploymentDesc(propertyFileUri);
		}
		return dd;
	}
	
	public RuntimeDeploymentDesc getDeploymentDesc(String propertyFileName) {
		String normalized = normalizePropertyFileName(propertyFileName);
		RuntimeDeploymentDesc dd = deploymentDescs.get(normalized);
		if (dd == null) {
			String path = ddPaths.get(normalized);
			if (path != null && path.length() > 0) {
				try {
					InputStream is = new FileInputStream(new File(path));
					if (ddParserMethod == null) {
						dd = RuntimeDeploymentDesc.createDeploymentDescriptor(normalized, is);
					}
					else {
						dd = (RuntimeDeploymentDesc)ddParserMethod.invoke(null, new Object[]{normalized, is});
					}
					deploymentDescs.put(normalized, dd);
				}
				catch (Exception e) {
					AnyException ex = new AnyException();
					throw ex.fillInMessage(Message.ERROR_PARSING_RESOURCE_FILE, propertyFileName, e);
				}
			}
		}
		return dd;
	}
	
	public String normalizePropertyFileName(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		
		if (name.startsWith("file:")) { //$NON-NLS-1$
			name = name.substring(5); // "file:".length()
		}
		
		int lastSlash = name.lastIndexOf('/');
		if (lastSlash != -1) {
			name = name.substring(lastSlash + 1);
		}
		
		if (name.endsWith("-bnd.xml")) { //$NON-NLS-1$
			name = name.substring(0, name.length() - 8); // "-bnd.xml".length()
		}
		else if (name.endsWith(".egldd")) { //$NON-NLS-1$
			name = name.substring(0, name.length() - 6); // ".egldd".length()
		}
		
		name = name.toLowerCase();
		try {
			return URLDecoder.decode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			return name;
		}
	}
	
	public List<String[]> parseDDArgument(String ddFiles, boolean added) {
		// For simplicity there's just the one delimeter.
		// name1;path1;name2;path2... using File.pathSeparator
		StringTokenizer tok = new StringTokenizer(ddFiles, File.pathSeparator);
		List<String[]> ddEntries = new ArrayList<String[]>();
		while (tok.hasMoreTokens()) {
			String name = tok.nextToken();
			if (tok.hasMoreTokens()) {
				String path = tok.nextToken();
				
				try {
					name = URLDecoder.decode(name, "UTF-8"); //$NON-NLS-1$
					path = URLDecoder.decode(path, "UTF-8"); //$NON-NLS-1$
				}
				catch (UnsupportedEncodingException e) {
					// Shouldn't happen.
				}
				
				ddEntries.add(new String[]{name, path});
				if (added) {
					addDDFile(name, path);
				}
				else {
					removeDDFile(name);
				}
			}
		}
		return ddEntries;
	}
	
	public void addDDFile(String name, String path) {
		this.ddPaths.put(name, path);
		removeFromCache(name);
	}
	
	public void removeDDFile(String name) {
		this.ddPaths.remove(name);
		removeFromCache(name);
	}
	
	private void removeFromCache(String ddName) {
		// Remove cached DDs.
		if (deploymentDescs.size() > 0) {
			deploymentDescs.remove(ddName);
		}
		
		// Remove cached resources. Key is a QName whose namespace is the file name.
		if (BindingResourceProcessor.getBindings().size() > 0) {
			for (Iterator<QName> it = BindingResourceProcessor.getBindings().keySet().iterator(); it.hasNext();) {
				QName q = it.next();
				if (ddName.equals(normalizePropertyFileName(q.getNamespaceURI()))) {
					it.remove();
				}
			}
		}
	}
}
