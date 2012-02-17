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
package org.eclipse.edt.ide.deployment.services.internal.testserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.testserver.TestServer;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JEERunUnit;
import org.eclipse.edt.javart.ide.IDEBindingResourceProcessor;
import org.eclipse.edt.javart.ide.IDEBindingResourceProcessor.SQLConnectionInfo;
import org.eclipse.edt.javart.resources.StartupInfo;
import org.eclipse.edt.javart.resources.egldd.RuntimeDeploymentDesc;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;
import org.eclipse.edt.javart.services.servlet.rest.rpc.PreviewServiceServlet;
import org.eclipse.jetty.plus.jndi.NamingEntry;
import org.eclipse.jetty.plus.webapp.PlusDescriptorProcessor;
import org.eclipse.jetty.xml.XmlConfiguration;

import eglx.lang.AnyException;


/**
 * Servlet that handles configuration of the test server, providing support for JNDI, service mappings,
 * and resource bindings.
 */
public class ConfigServlet extends HttpServlet {
	
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * The path of the servlet running inside the context root.
	 */
	public static final String SERVLET_PATH = "config"; //$NON-NLS-1$
	
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
	 * The name of the argument for setting the order of the deployment descriptors.
	 */
	public static final String ARG_ORDERED_DDS = "orderedDDs"; //$NON-NLS-1$
	
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
	 * The binding processor.
	 */
	private final IDEBindingResourceProcessor bindingProcessor;
	
	/**
	 * The test server.
	 */
	private final TestServer server;
	
	/**
	 * ID for the next data source that gets created.
	 */
	private int nextDataSourceId;
	
	/**
	 * List of DD names in the order that they take precedence.
	 */
	private List<String> orderedDDNames;
	
	/**
	 * List of resource-ref names added to the server.
	 */
	private List<String> addedResourceRefs;
	
	/**
	 * List of data source names added to the server.
	 */
	private List<NamingEntry> addedDataSources;
	
	/**
	 * Class to use for connection pooling, possibly null.
	 */
	private final String basicDataSourceClassName;
	
	/**
	 * Constructor.
	 */
	public ConfigServlet(PreviewServiceServlet previewServlet, IDEBindingResourceProcessor bindingProcessor, TestServer server) {
		this.previewServlet = previewServlet;
		this.bindingProcessor = bindingProcessor;
		this.server = server;
		this.orderedDDNames = new ArrayList<String>();
		this.addedResourceRefs = new ArrayList<String>();
		this.addedDataSources = new ArrayList<NamingEntry>();
		
		// See if a connection pooling data source is available.
		String className = null;
		try {
			Class.forName("org.apache.tomcat.dbcp.dbcp.BasicDataSource"); //$NON-NLS-1$
			className = EGLBasicDataSource1.class.getCanonicalName();
		}
		catch (Throwable t) {
		}
		if (className == null) {
			try {
				Class.forName("org.apache.commons.dbcp.BasicDataSource"); //$NON-NLS-1$
				className = EGLBasicDataSource2.class.getCanonicalName();
			}
			catch (Throwable t) {
			}
		}
		basicDataSourceClassName = className;
		
		if (basicDataSourceClassName == null) {
			TestServer.logWarning("Apache's BasicDataSource class is not on the classpath so connection pooling for JNDI data sources will not be used. " +
					"To enable connection pooling you can add an Apache Tomcat runtime to your workspace via Preferences > Server > Runtime Environments.");
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		initRunUnit();
		
		String addedDDs = req.getParameter(ARG_DD_ADDED);
		String removedDDs = req.getParameter(ARG_DD_REMOVED);
		String defaultDD = req.getParameter(ARG_DEFAULT_DD);
		String orderedDDs = req.getParameter(ARG_ORDERED_DDS);
		
		boolean reconfigure = false;
		if (addedDDs != null && addedDDs.length() > 0) {
			reconfigure = true;
			parseDDFiles(addedDDs, true);
		}
		if (removedDDs != null && removedDDs.length() > 0) {
			reconfigure = true;
			parseDDFiles(removedDDs, false);
		}
		if (defaultDD != null) {
			server.logInfo("Default DD changed: " + defaultDD); //$NON-NLS-1$
			try {
				bindingProcessor.setDefaultDD(defaultDD);
			}
			catch (AnyException e) {
				// Not fatal - log error and continue with other changes.
				server.log(e);
			}
		}
		if (orderedDDs != null) {
			reconfigure = parseOrderedDDs(orderedDDs) || reconfigure;
		}
		
		if (reconfigure) {
			initRunUnit();
			
			// Remove everything we previously processed and re-configure the server.
			previewServlet.clearServiceMappings();
			clearJNDIEnvironment();
			
			configureServiceMappings();
			configureJNDIEnvironment();
		}
	}
	
	protected void initRunUnit() {
		if (org.eclipse.edt.javart.Runtime.getRunUnit() == null) {
			org.eclipse.edt.javart.Runtime.setThreadRunUnit(new JEERunUnit(new StartupInfo("ConfigServlet", "", null))); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	public void parseDDFiles(String ddFiles, boolean added) {
		initRunUnit();
		List<String[]> parsed = bindingProcessor.getResourceLocator().parseDDArgument(ddFiles, added);
		for (String[] next : parsed) {
			if (added) {
				server.logInfo("DD file added or changed: " + next[0] + ", " + next[1]); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				server.logInfo("DD file removed: " + next[0] + ", " + next[1]); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Parses the value for a list of DD names, which are in the order they should be processed.
	 * 
	 * @return true if the ordering of DD names has changed.
	 */
	public boolean parseOrderedDDs(String ddFiles) {
		server.logInfo("Parsing DD file order argument: " + ddFiles);
		List<String> oldNames = orderedDDNames;
		
		orderedDDNames = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(ddFiles, File.pathSeparator);
		while (tok.hasMoreTokens()) {
			orderedDDNames.add(tok.nextToken());
		}
		
		int size = orderedDDNames.size();
		if (size == oldNames.size()) {
			for (int i = 0; i < size; i++) {
				if (!oldNames.get(i).equals(orderedDDNames.get(i))) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	public void configureServiceMappings() {
		initRunUnit();
		
		// We go in reverse order so that the correct mapping is used when there are duplicate URIs.
		List<String> names = orderedDDNames;
		int size = names.size();
		for (int i = size - 1; i >= 0; i--) {
			RuntimeDeploymentDesc dd = bindingProcessor.getResourceLocator().getDeploymentDesc(names.get(i));
			if (dd instanceof DeploymentDesc) {
				List<Restservice> services = ((DeploymentDesc)dd).getRestservices();
				for (Restservice service : services) {
					String className = service.getImplementation();
					String uri;
					
					// We use the unaliased class name for the uri, prefixed with a '/'
					if (className.length() > 0 && className.charAt(0) != '/') {
						uri = '/' + className;
					}
					else {
						uri = className;
					}
					
					previewServlet.addServiceMapping(uri, className, service.isStateful());
					server.logInfo("rest service mapping added: " + uri);
				}
			}
		}
	}
	
	/**
	 * Uninstalls all JNDI configurations from the server.
	 */
	public void clearJNDIEnvironment() {
		for (String name : addedResourceRefs) {
			removeResourceRef(name);
		}
		
		for (NamingEntry entry : addedDataSources) {
			removeDataSource(entry);
		}
		
		addedResourceRefs.clear();
		addedDataSources.clear();
	}
	
	/**
	 * Registers resource-refs and data sources for use in JNDI environments, based on the DD files.
	 */
	public void configureJNDIEnvironment() {
		initRunUnit();
		
		// 1. Define data sources.
		
		// If Apache's BasicDataSource is available, use it. It supports connection pooling so it's much faster.
		// If not, use our own basic data source which does not support connection pooling.
		Map<String, SQLConnectionInfo> dataSources = getSQLDataSources();
		for (Entry<String, SQLConnectionInfo> entry : dataSources.entrySet()) {
			try {
				SQLConnectionInfo info = entry.getValue();
				
				String dataSourceClass;
				Properties settings = new Properties();
				settings.put("driverClassName", info.driverClass); //$NON-NLS-1$
				settings.put("username", info.username); //$NON-NLS-1$
				settings.put("password", info.password); //$NON-NLS-1$
				settings.put("url", info.url); //$NON-NLS-1$
				if (basicDataSourceClassName != null) {
					settings.put("maxActive", "5"); //$NON-NLS-1$ //$NON-NLS-2$
					settings.put("maxIdle", "2"); //$NON-NLS-1$ //$NON-NLS-2$
					settings.put("maxWait", "5000"); //$NON-NLS-1$ //$NON-NLS-2$
					dataSourceClass = basicDataSourceClassName;
				}
				else {
					dataSourceClass = SimpleDataSource.class.getCanonicalName();
				}
				
				addDataSource(entry.getKey(), dataSourceClass, settings);
			}
			catch (Exception e) {
				server.log(e);
			}
		}
		
		// 2. Register resource-refs. This must be done after the data sources are defined.
		Set<String> refs = getSQLResourceRefs();
		for (String jndiName : refs) {
			// Only add a resource-ref if we added the data source of that name, to prevent an error.
			for (NamingEntry entry : addedDataSources) {
				if (entry.getJndiName().equals(jndiName)) {
					addResourceRef(jndiName, DataSource.class);
					break;
				}
			}
		}
	}
	
	/**
	 * Adds a data source to the JNDI environment.
	 * 
	 * @param name  The JNDI name.
	 * @param className  The data source class name.
	 * @param settings  The properties of the data source instance (url, username, password, etc).
	 * @throws Exception
	 */
	public void addDataSource(String name, String className, Properties settings) throws Exception {
		StringBuilder buf = new StringBuilder(200);
		String id = "ds" + (++nextDataSourceId); //$NON-NLS-1$
		buf.append("<Configure id=\"Server\" class=\"org.eclipse.jetty.webapp.WebAppContext\"><New id=\""); //$NON-NLS-1$
		buf.append(id);
		buf.append("\" class=\"org.eclipse.jetty.plus.jndi.Resource\"><Arg><Ref id=\"Server\"/></Arg><Arg>"); //$NON-NLS-1$
		buf.append(name);
		buf.append("</Arg><Arg><New class=\""); //$NON-NLS-1$
		buf.append(className);
		buf.append("\">"); //$NON-NLS-1$
		
		for (Entry<Object,Object> entry : settings.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (key instanceof String && value instanceof String) {
				buf.append("<Set name=\""); //$NON-NLS-1$
				buf.append(key);
				buf.append("\">"); //$NON-NLS-1$
				buf.append(value);
				buf.append("</Set>"); //$NON-NLS-1$
			}
		}
		buf.append("</New></Arg></New></Configure>"); //$NON-NLS-1$
		
		ClassLoader savedLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(server.getWebApp().getClassLoader());
        try {
			XmlConfiguration config = new XmlConfiguration(buf.toString());
			config.configure(server.getWebApp());
			Object boundResource = config.getIdMap().get(id);
			if (boundResource instanceof NamingEntry) {
				addedDataSources.add((NamingEntry)boundResource);
			}
			
			server.logInfo("data source added: name=" + name + " class=" + className); //$NON-NLS-1$ //$NON-NLS-2$
        }
        finally {
        	Thread.currentThread().setContextClassLoader(savedLoader);
        }
	}
	
	/**
	 * Removes a data source from the JNDI environment.
	 * 
	 * @param name  The JNDI name.
	 * @return true if the data source was successfully removed.
	 */
	public boolean removeDataSource(String name) {
		for (NamingEntry entry : addedDataSources) {
			if (entry.getJndiName().equals(name)) {
				return removeDataSource(entry);
			}
		}
		
		return false;
	}
	
	/**
	 * Removes a data source from the JNDI environment.
	 * 
	 * @param entry  The bound resource.
	 * @return true if the data source was successfully removed.
	 */
	public boolean removeDataSource(NamingEntry dataSource) {
		ClassLoader savedLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(server.getWebApp().getClassLoader());
        try {
        	dataSource.release();
        	server.logInfo("data source removed: name=" + dataSource.getJndiName()); //$NON-NLS-1$
        	return true;
        }
        finally {
        	Thread.currentThread().setContextClassLoader(savedLoader);
        }
	}
	
	/**
	 * Adds a resource-ref to the web application context. This must be called AFTER the corresponding resource has been created.
	 */
	public void addResourceRef(String name, Class type) {
		ClassLoader savedLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(server.getWebApp().getClassLoader());
		try {
			PlusDescriptorProcessor processor = new PlusDescriptorProcessor();
			processor.bindResourceRef(server.getWebApp(), name, type);
			if (!addedResourceRefs.contains(name)) {
				addedResourceRefs.add(name);
			}
			server.logInfo("resource-ref added: name=" + name + " type=" + type.getCanonicalName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (Exception e) {
			server.log(e);
		}
		finally {
			Thread.currentThread().setContextClassLoader(savedLoader);
		}
	}
	
	/**
	 * Removes a resource-ref from the web application context.
	 * 
	 * @return true if the resource-ref was successfully removed.
	 */
	public boolean removeResourceRef(String name) {
		ClassLoader savedLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(server.getWebApp().getClassLoader());
		try {
			if (!name.startsWith("java:comp/env/")) { //$NON-NLS-1$
				name = "java:comp/env/" + name; //$NON-NLS-1$
			}
			InitialContext ctx = new InitialContext();
			ctx.unbind(name);
			server.logInfo("resource-ref removed: name=" + name); //$NON-NLS-1$
			return true;
		}
		catch (Exception e) {
			server.log(e);
			return false;
		}
		finally {
			Thread.currentThread().setContextClassLoader(savedLoader);
		}
	}
	
	/**
	 * @return a map whose key is the JNDI name and value is an SQLConnectionInfo.
	 */
	protected Map<String, SQLConnectionInfo> getSQLDataSources() {
		initRunUnit();
		
		Map<String, SQLConnectionInfo> dataSources = new HashMap<String, SQLConnectionInfo>();
		
		// We go in reverse order so that the correct mapping is used when there are duplicate URIs.
		List<String> names = orderedDDNames;
		int size = names.size();
		for (int i = size - 1; i >= 0; i--) {
			try {
				RuntimeDeploymentDesc dd = bindingProcessor.getResourceLocator().getDeploymentDesc(names.get(i));
				List<SQLDatabaseBinding> bindings = dd.getSqlDatabaseBindings();
				
				for (SQLDatabaseBinding binding : bindings) {
					if (binding.isDeployAsJndi()) {
						SQLConnectionInfo info = null;
						if (binding.isUseURI()) {
							String uri = binding.getUri();
							if (uri != null && uri.startsWith("workspace://")) { //$NON-NLS-1$
								info = bindingProcessor.getParsedConnectionProfileSettings(uri.substring(12)); // "workspace://".length()
							}
						}
						else {
							// Need at least a URL and class name
							info = new SQLConnectionInfo();
							info.url = binding.getSqlDB();
							info.driverClass = binding.getSqlJDBCDriverClass();
							info.username = binding.getSqlID();
							info.password = binding.getSqlPassword();
							info.defaultSchema = binding.getSqlSchema();
						}
						
						if (info != null) {
							String jndiName = binding.getJndiName();
							if (jndiName != null && (jndiName = jndiName.trim()).length() > 0) {
								// First one in wins, not last.
								if (!dataSources.containsKey(jndiName)) {
									dataSources.put(jndiName, info);
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return dataSources;
	}
	
	/**
	 * @return a list of JNDI names from the DD files.
	 */
	protected Set<String> getSQLResourceRefs() {
		initRunUnit();
		
		Set<String> refs = new HashSet<String>();
		
		// We go in reverse order so that the correct mapping is used when there are duplicate URIs.
		List<String> names = orderedDDNames;
		int size = names.size();
		for (int i = size - 1; i >= 0; i--) {
			try {
				RuntimeDeploymentDesc dd = bindingProcessor.getResourceLocator().getDeploymentDesc(names.get(i));
				List<SQLDatabaseBinding> bindings = dd.getSqlDatabaseBindings();
				
				for (SQLDatabaseBinding binding : bindings) {
					String jndiName = null;
					if (binding.isDeployAsJndi()) {
						jndiName = binding.getJndiName();
					}
					else if (binding.isUseURI() && binding.getUri() != null && binding.getUri().startsWith("jndi://")) { //$NON-NLS-1$
						// JNDI name comes from the URI
						jndiName = binding.getUri().substring(7); // "jndi://".length()
					}
					
					if (jndiName != null && (jndiName = jndiName.trim()).length() > 0) {
						refs.add(jndiName);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return refs;
	}
}
