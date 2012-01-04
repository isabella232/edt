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
package org.eclipse.edt.javart.ide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.RuntimeDeploymentDesc;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;
import org.eclipse.edt.runtime.java.eglx.lang.EDictionary;

import eglx.lang.AnyException;
import eglx.lang.SysLib;
import eglx.lang.SysLib.ResourceLocator;
import eglx.persistence.sql.SQLDataSource;

/**
 * Implements SysLib.ResourceLocator to handle locating deployment descriptors for a Java test environment, as well as
 * supporting special binding URIs such as "workspace://".
 */
public class IDEResourceLocator extends SysLib implements ResourceLocator {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Maps DD names to absolute filesystem paths.
	 */
	private Map<String,String> ddPaths = new HashMap<String, String>();
	
	/**
	 * Port on which the IDE server is running.
	 */
	private final String ideURL;
	
	/**
	 * The name of the default DD file.
	 */
	private String defaultDD;
	
	/**
	 * Constructor.
	 * 
	 * @param idePort  The port on which the IDE can be reached.
	 */
	public IDEResourceLocator(int idePort) {
		this.ideURL = "http://localhost:" + idePort + "/__testServer"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public Object locateResource(String bindingKey) {
		return locateResource(bindingKey, defaultDD);
	}
	
	@Override
	public Object locateResource(String bindingKey, String propertyFileName) {
		return doGetResource(bindingKey, propertyFileName);
	}
	
	@Override
	public Object convertToResource(Binding binding) {
		if (binding instanceof SQLDatabaseBinding) {
			SQLDatabaseBinding sqlBinding = (SQLDatabaseBinding)binding;
			if (sqlBinding.isUseURI()) {
				String uri = sqlBinding.getUri();
				if (uri != null && uri.startsWith("workspace://")) { //$NON-NLS-1$
					String profileInfo = getConnectionProfileSettings(uri.substring(12)); // "workspace://".length()
					if (profileInfo != null && profileInfo.length() > 0) {
						String[] tokens = profileInfo.split(";");
						if (tokens.length > 0) {
							String url = tokens[0].trim();
							String user = null;
							String pass = null;
							String schema = null;
							String className = null;
							
							if (tokens.length > 1) {
								user = tokens[1].trim();
							}
							if (tokens.length > 2) {
								pass = tokens[2].trim();
							}
							if (tokens.length > 3) {
								schema = tokens[3].trim();
							}
							if (tokens.length > 4) {
								className = tokens[4].trim();
							}
							
							try {
								url = URLDecoder.decode(url, "UTF-8");
								if (user != null) {
									user = URLDecoder.decode(user, "UTF-8");
								}
								if (pass != null) {
									pass = URLDecoder.decode(pass, "UTF-8");
								}
								if (schema != null) {
									schema = URLDecoder.decode(schema, "UTF-8");
								}
								if (className != null) {
									className = URLDecoder.decode(className, "UTF-8");
								}
							}
							catch (UnsupportedEncodingException e) {
								// Shouldn't happen.
							}
							
							EDictionary props = new EDictionary();
							if (user != null && user.length() > 0) {
								props.put("user", user);
							}
							if (pass != null && pass.length() > 0) {
								props.put("password", pass);
							}
							
							// Try to load the class so that it registers itself, in case it's not a Type 4 driver.
							// This must be done before any connection is made, such as by invoking setCurrentSchema below.
							if (className != null && className.length() > 0) {
								try {
									Class.forName(className);
								}
								catch (Throwable t) {
								}
							}
							
							SQLDataSource ds = new SQLDataSource(url, props);
							if (schema != null && schema.length() > 0) {
								ds.setCurrentSchema(schema);
							}
							
							return ds;
						}
					}
				}
			}
		}
		return null;
	}
	
	public RuntimeDeploymentDesc getDeploymentDesc(String propertyFileName) {
		String normalized = normalizePropertyFileName(propertyFileName);
		RuntimeDeploymentDesc dd = deploymentDescs.get(normalized);
		if (dd == null) {
			String path = ddPaths.get(normalized);
			if (path != null && path.length() > 0) {
				try {
					InputStream is = new FileInputStream(new File(path));
					dd = RuntimeDeploymentDesc.createDeploymentDescriptor(normalized, is);
					deploymentDescs.put(normalized, dd);
				}
				catch (Exception e) {
					AnyException ex = new AnyException();
					throw ex.fillInMessage(Message.ERROR_PARSING_RESOURCE_FILE, propertyFileName, e);
				}
			}
			else {
				AnyException ex = new AnyException();
				throw ex.fillInMessage(Message.RESOURCE_FILE_NOT_FOUND, propertyFileName);
			}
		}
		return dd;
	}
	
	private String normalizePropertyFileName(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		
		int lastSlash = name.lastIndexOf('/');
		if (lastSlash != -1) {
			name = name.substring(lastSlash + 1);
		}
		
		if (name.endsWith("-bnd.xml")) { //$NON-NLS-1$
			name = name.substring(0, name.length() - 8); // "-bnd.xml".length()
		}
		else if (name.endsWith(".egldd")) {
			name = name.substring(0, name.length() - 6); // ".egldd".length()
		}
		
		return name.toLowerCase();
	}
	
	public void setDefaultDD(String dd) {
		this.defaultDD = dd;
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
		if (resources.size() > 0) {
			for (Iterator<QName> it = resources.keySet().iterator(); it.hasNext();) {
				QName q = it.next();
				if (ddName.equals(normalizePropertyFileName(q.getNamespaceURI()))) {
					it.remove();
				}
			}
		}
	}
	
	/**
	 * @return the connection settings for the connection profile. A connection to the IDE is used to dynamically obtain this information.
	 */
	private String getConnectionProfileSettings(String profileName) {
		String connectionURL = null;
		
		InputStream is = null;
		try {
			HttpURLConnection conn = (HttpURLConnection)new URL(ideURL).openConnection(); //$NON-NLS-1$ //$NON-NLS-2$
			conn.setDoOutput(true);
			conn.setRequestProperty("Accept-Charset", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
			
			OutputStream output = null;
			try {
			     output = conn.getOutputStream();
			     output.write(("connectionProfile=" + profileName).getBytes("UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			finally {
				if (output != null) {
					try {
						output.close();
					}
					catch (IOException e) {
					}
				}
			}
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				
				StringBuilder buf = new StringBuilder( 50 );
				String line;
				while ((line = br.readLine()) != null) {
					buf.append(line);
				}
				connectionURL = buf.toString();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (IOException e) {
				}
			}
		}
		
		return connectionURL;
	}
}
