/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;
import org.eclipse.edt.runtime.java.eglx.lang.EDictionary;

import resources.edt.binding.BindingResourceProcessor;

import eglx.java.JavaObjectException;
import eglx.lang.AnyException;
import eglx.persistence.sql.SQLDataSource;
import eglx.persistence.sql.SQLJNDIDataSource;

public class IDEBindingResourceProcessor extends BindingResourceProcessor {
	
	/**
	 * The name of the default DD file.
	 */
	private URI defaultDD;
	
	/**
	 * URL on which the IDE server is running.
	 */
	private final String ideURL;
	
	public IDEBindingResourceProcessor(int idePort, IDEResourceLocator resourceLocator) {
		this.resourceLocator = resourceLocator;
		this.ideURL = "http://localhost:" + idePort + "/__testServer"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	protected Object getResource(Binding binding, URI dd) throws AnyException {
		if (binding instanceof SQLDatabaseBinding) {
			SQLDatabaseBinding sqlBinding = (SQLDatabaseBinding)binding;
			if (sqlBinding.isUseURI()) {
				String uri = sqlBinding.getUri();
				if (uri != null && uri.startsWith("workspace://")) { //$NON-NLS-1$
					if (sqlBinding.isDeployAsJndi()) {
						String jndiName = sqlBinding.getJndiName();
						if (jndiName != null && (jndiName = jndiName.trim()).length() > 0) {
							EDictionary props = null;
							if (sqlBinding.isApplicationAuthentication()) {
								SQLConnectionInfo info = getParsedConnectionProfileSettings(uri.substring(12)); // "workspace://".length()
								if (info != null) {
									props = new EDictionary();
									if (info.username != null && info.username.length() > 0) {
										props.put("user", info.username); //$NON-NLS-1$
									}
									if (info.password != null && info.password.length() > 0) {
										props.put("password", info.password); //$NON-NLS-1$
									}
								}
							}
							return new SQLJNDIDataSource(jndiName, props);
						}
						else {
							System.err.println("WARN: jndiName parameter missing for resource binding " + sqlBinding.getName() + ". JNDI connection will NOT be used"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
					
					SQLConnectionInfo info = getParsedConnectionProfileSettings(uri.substring(12)); // "workspace://".length()
					if (info != null) {
						EDictionary props = new EDictionary();
						if (info.username != null && info.username.length() > 0) {
							props.put("user", info.username); //$NON-NLS-1$
						}
						if (info.password != null && info.password.length() > 0) {
							props.put("password", info.password); //$NON-NLS-1$
						}
						
						// Try to load the class so that it registers itself, in case it's not a Type 4 driver.
						// This must be done before any connection is made, such as by invoking setCurrentSchema below.
						if (info.driverClass != null && info.driverClass.length() > 0) {
							try {
								Class.forName(info.driverClass);
							}
							catch (Throwable t) {
							}
						}
						
						SQLDataSource ds = new SQLDataSource(info.url, props);
						if (info.defaultSchema != null && info.defaultSchema.length() > 0) {
							ds.setCurrentSchema(info.defaultSchema);
						}
						
						return ds;
					}
				}
			}
			else if (sqlBinding.isDeployAsJndi()) {
				String jndiName = sqlBinding.getJndiName();
				if (jndiName != null && (jndiName = jndiName.trim()).length() > 0) {
					EDictionary props = null;
					if (sqlBinding.isApplicationAuthentication()) {
						props = new EDictionary();
						String user = sqlBinding.getSqlID();
						String pass = sqlBinding.getSqlPassword();
						if (user.length() > 0) {
							props.put("user", user); //$NON-NLS-1$
						}
						if (pass.length() > 0) {
							props.put("password", pass); //$NON-NLS-1$
						}
					}
					return new SQLJNDIDataSource(jndiName, props);
				}
				else {
					System.err.println("WARN: jndiName parameter missing for resource binding " + sqlBinding.getName() + ". JNDI connection will NOT be used"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		return super.getResource(binding, dd);
	}
	
	@Override
	protected URI getDefaultDDURI() {
		if (defaultDD == null) {
			AnyException ae = new AnyException();
			throw ae.fillInMessage( Message.MISSING_DEFAULT_DD );
		}
		return defaultDD;
	}
	
	public void setDefaultDD(String dd) throws AnyException {
		if (dd == null || (dd = dd.trim()).length() == 0) {
			this.defaultDD = null;
		}
		else {
			try {
				this.defaultDD = createFileURI(dd);
			} catch (URISyntaxException e) {
				JavaObjectException jox = new JavaObjectException();
				jox.exceptionType = URI.class.getName();
				jox.initCause( e );
				throw jox.fillInMessage( Message.RESOURCE_URI_EXCEPTION, defaultDD );
			}
		}
	}
	
	public IDEResourceLocator getResourceLocator() {
		return (IDEResourceLocator)resourceLocator;
	}
	
	/**
	 * @return the connection settings as a parsed, decoded array; values are: url, username, password, default schema, driver class name.
	 */
	public SQLConnectionInfo getParsedConnectionProfileSettings(String profileName) {
		String profileInfo = getConnectionProfileSettings(profileName);
		if (profileInfo != null && profileInfo.length() > 0) {
			String[] tokens = profileInfo.split(";"); //$NON-NLS-1$
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
					url = URLDecoder.decode(url, "UTF-8"); //$NON-NLS-1$
					if (user != null) {
						user = URLDecoder.decode(user, "UTF-8"); //$NON-NLS-1$
					}
					if (pass != null) {
						pass = URLDecoder.decode(pass, "UTF-8"); //$NON-NLS-1$
					}
					if (schema != null) {
						schema = URLDecoder.decode(schema, "UTF-8"); //$NON-NLS-1$
					}
					if (className != null) {
						className = URLDecoder.decode(className, "UTF-8"); //$NON-NLS-1$
					}
				}
				catch (UnsupportedEncodingException e) {
					// Shouldn't happen.
				}
				
				SQLConnectionInfo info = new SQLConnectionInfo();
				info.url = url;
				info.username = user;
				info.password = pass;
				info.defaultSchema = schema;
				info.driverClass = className;
				return info;
			}
		}
		return null;
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
	
	public static class SQLConnectionInfo {
		public String username;
		public String password;
		public String driverClass;
		public String url;
		public String defaultSchema;
	}
}
