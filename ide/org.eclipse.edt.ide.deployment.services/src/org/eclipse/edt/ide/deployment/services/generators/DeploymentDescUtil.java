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
package org.eclipse.edt.ide.deployment.services.generators;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.edt.compiler.internal.util.Encoder;
import org.eclipse.edt.gen.deployment.util.CommonUtilities;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.Parameter;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;

public class DeploymentDescUtil {
	private static final String indent1 = "    ";
	private static final String indent2 = indent1 + indent1;
	private static final String indent3 = indent2 + indent1;
	private static final String indent4 = indent2 + indent2;
	
	/**
	 * Converts the deployment descriptor to XML. This will convert certain bindings into a 'deployed' format,
	 * for example the "workspace://" URI for SQL bindings will be changed to hard-coded bindings because
	 * deployment has no support for such bindings.
	 */
	public static String convertToBindXML(DeploymentDesc desc, IProject targetProject){
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buf.append("<egldd>\n");
		buf.append(indent1);
		buf.append("<bindings>\n");
		for(Binding binding : desc.getBindings())
		{
			buf.append(toBindXML(binding, targetProject));
		}
		buf.append(indent1);
		buf.append("</bindings>\n");
		buf.append(indent1);
		buf.append("<includes>\n");
		for(String include : desc.getIncludes())
		{
			buf.append(toIncludeXML(include));
		}
		buf.append(indent1);
		buf.append("</includes>\n");
		buf.append("</egldd>\n");
		return buf.toString();

	}
	
	public static String toBindXML(Binding binding, IProject targetProject)
	{
		binding = tweakBinding(binding, targetProject);
		
		StringBuilder builder = new StringBuilder(indent2);
		builder.append("<binding name=\"");
		if(binding.getName() != null){
			builder.append(binding.getName());
		}
		builder.append("\"");
		if(binding.getType() != null){
			builder.append(" type=\"").append(binding.getType());
			builder.append("\"");
		}
		if(binding.getUri() != null){
			builder.append(" uri=\"").append(binding.getUri());
			builder.append("\"");
		}
		builder.append(" useURI=\"").append(String.valueOf((binding.isUseURI())));
		builder.append("\">\n");
		Collection<Parameter> parms = binding.getParameters();
		if (parms.size() > 0) {
			builder.append(indent3);
			builder.append("<parameters>\n");
			for(Parameter parameter : parms)
			{
				builder.append(toBindXML(parameter));
			}
			builder.append(indent3);
			builder.append("</parameters>\n");
		}
		builder.append(indent2);
		builder.append("</binding>\n");
		return builder.toString();
	}

	public static String toBindXML(Parameter parameter)
	{
		StringBuilder builder = new StringBuilder(indent4);
		builder.append("<parameter name=\"");
		if(parameter.getName() != null){
			builder.append(parameter.getName());
		}
		builder.append("\"");
		if(parameter.getType() != null){
			builder.append(" type=\"");
			builder.append(parameter.getType());
			builder.append("\"");
		}
		if(parameter.getValue() != null){
			builder.append(" value=\"").append(parameter.getValue());
			builder.append("\"");
		}
		builder.append("/>\n");
		return builder.toString();
	}
	
	public static String toIncludeXML(String include)
	{
		StringBuilder builder = new StringBuilder(indent2);
		builder.append("<include location=\"");
		builder.append( CommonUtilities.toIncludeDDName( include ) + "\"");
		builder.append("/>\n");
		return builder.toString();
	}
	
	/**
	 * Certain bindings will be changed, such as converting an SQL workspace:// URI. The original
	 * binding should be left alone since other operations may need to see their actual values.
	 * If the binding is not tweaked, it is returned as-is.
	 */
	private static Binding tweakBinding(Binding binding, IProject targetProject) {
		if (binding instanceof SQLDatabaseBinding) {
			SQLDatabaseBinding sqlBinding = (SQLDatabaseBinding)binding;
			
			String uri = sqlBinding.getUri();
			if (sqlBinding.isDeployAsJndi()) {
				if (!sqlBinding.isUseURI() || (uri != null && uri.startsWith("workspace://"))) {
					// Switch to JNDI.
					String jndiName = sqlBinding.getJndiName();
					if (jndiName == null || (jndiName = jndiName.trim()).length() == 0) {
						jndiName = "jdbc/" + sqlBinding.getName();
					}
					
					SQLDatabaseBinding newBinding = new SQLDatabaseBinding(sqlBinding);
					newBinding.setParameters(new HashMap<String, Parameter>());
					newBinding.setUseURI(true);
					newBinding.setUri("jndi://" + jndiName);
					
					// Add the username and password when using application authentication.
					if (sqlBinding.isApplicationAuthentication()) {
						String user = null;
						String password = null;
						
						if (!sqlBinding.isUseURI()) {
							user = sqlBinding.getSqlID();
							password = sqlBinding.getSqlPassword();
						}
						else {
							// Comes from connection profile.
							IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(uri.substring(12)); // Remove the "workspace://"
							if (profile != null) {
								user = EGLSQLUtility.getSQLUserId(profile);
								password = Encoder.encode(EGLSQLUtility.getSQLPassword(profile));
							}
						}
						
						if (user != null && (user = user.trim()).length() > 0) {
							newBinding.addParameter(new Parameter(SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlID, user));
						}
						if (password != null && (password = password.trim()).length() > 0) {
							newBinding.addParameter(new Parameter(SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlPassword, password));
						}
					}
					return newBinding;
				}
			}
			else if (sqlBinding.isUseURI() && uri != null && uri.trim().startsWith("workspace://")) {
				// Switch to hard-coded.
				IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(uri.substring(12)); // Remove the "workspace://"
				if (profile != null) {
					SQLDatabaseBinding newBinding = new SQLDatabaseBinding(sqlBinding);
					newBinding.setParameters(new HashMap<String, Parameter>());
					newBinding.setUseURI(false);
					
					newBinding.addParameter(new Parameter(SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_dbms, 
							EGLSQLUtility.getSQLVendorProperty(profile) + " "
									+ EGLSQLUtility.getSQLProductVersion(profile)));
					
					newBinding.addParameter(new Parameter(SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlJDBCDriverClass, 
										EGLSQLUtility.getSQLJDBCDriverClassPreference(profile)));
	
					newBinding.addParameter(new Parameter(SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jarList, 
							EGLSQLUtility.getLoadingPath(profile)));
	
					newBinding.addParameter(new Parameter(SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlID, 
							EGLSQLUtility.getSQLUserId(profile)));
	
					String password = EGLSQLUtility.getSQLPassword(profile);
					newBinding.addParameter(new Parameter(SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlPassword, 
						Encoder.encode(password)));
	
					newBinding.addParameter(new Parameter(SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlDB, 
							EGLSQLUtility.getSQLConnectionURLPreference(profile)));
					
					newBinding.addParameter(new Parameter(SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlSchema, 
							EGLSQLUtility.getDefaultSchema(profile)));
					return newBinding;
				}
			}
		}
		return binding;
	}
}
