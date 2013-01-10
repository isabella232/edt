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
package org.eclipse.edt.ide.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;

public class BindingSQLDatabaseConfiguration extends BindingEGLConfiguration {
	
	private boolean useUri;
	private String uri;
	
	private String dbVendorAndVersion="";
	private String driverClass="";
	private String connUrl="";
	private String userName="";
	private String password="";
	private String dbName="";
	private String connLocation;
	private String bindingName;
	private String defaultSchema;

	public BindingSQLDatabaseConfiguration() {
		super();
	}
		
	public BindingSQLDatabaseConfiguration(EGLDeploymentRoot root, IProject proj){
		super(root, proj);
	}

	protected void setDefaultAttributes() {
	}
	
	public String getDbms(){
		return dbVendorAndVersion;
	}
	
	public void setDbms(String dbms){
		this.dbVendorAndVersion = dbms;
	}
	
	public String getDriverClass(){
		return driverClass;
	}
	
	public void setDriverClass(String driverClass){
		this.driverClass = driverClass;
	}
	
	public String getConnUrl(){
		return connUrl;
	}
	
	public void setConnUrl(String connUrl){
		this.connUrl = connUrl;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getDbName(){
		return dbName;
	}
	
	public void setDbName(String dbName){
		this.dbName = dbName;
	}
	
	public boolean useUri() {
		return this.useUri;
	}
	
	public void setUseUri(boolean useUri) {
		this.useUri = useUri;
	}
	
	public String getUri(){
		return uri;
	}
	
	public void setUri(String uri){
		this.uri = uri;
	}
	
	public String getConnLocation(){
		return connLocation;
	}
	
	public void setConnLocation(String connLocation){
		this.connLocation = connLocation;
	}
	
	public String getBindingName(){
		return bindingName;
	}
	
	public void setBindingName(String bindingName){
		this.bindingName = bindingName;
	}
	
	public String getDefaultSchema(){
		return defaultSchema;
	}
	
	public void setDefaultSchema(String defaultSchema){
		this.defaultSchema = defaultSchema;
	}
	
	public String getValidBindingName(){
		return getValidBindingName(getBindingName());
	}
	
	public Object executeAddBinding(Bindings bindings){
		Binding sqlBinding = DeploymentFactory.eINSTANCE.createBinding();
		bindings.getBinding().add(sqlBinding);
		sqlBinding.setType(org.eclipse.edt.javart.resources.egldd.Binding.BINDING_DB_SQL);
		String bindingName =  getValidBindingName(getBindingName());
		sqlBinding.setName(bindingName);
		
		Parameters params = null;
		if (getUri() == null || !getUri().startsWith("jndi://")) {
			params = DeploymentFactory.eINSTANCE.createParameters();
			sqlBinding.setParameters(params);
			EGLDDRootHelper.addOrUpdateParameter(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_deployAsJndi, true);
			EGLDDRootHelper.addOrUpdateParameter(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jndiName, "jdbc/" + bindingName);
		}
		
		if (useUri()) {
			sqlBinding.setUseURI(true);
			sqlBinding.setUri(getUri());
		}
		else {
			if (params == null) {
				params = DeploymentFactory.eINSTANCE.createParameters();
				sqlBinding.setParameters(params);
			}
			
			sqlBinding.setUseURI(false);
		
			EGLDDRootHelper.addOrUpdateParameter(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_dbms, getDbms());
			EGLDDRootHelper.addOrUpdateParameter(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlID, getUserName());
			EGLDDRootHelper.addOrUpdateParameter(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlPassword, getPassword());
//			EGLDDRootHelper.addOrUpdateParameter(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlValidationConnectionURL, getConnUrl());
			
			EGLDDRootHelper.addOrUpdateParameter(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jarList, getConnLocation());
			EGLDDRootHelper.addOrUpdateParameter(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlJDBCDriverClass, getDriverClass());
			EGLDDRootHelper.addOrUpdateParameter(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlDB, getConnUrl());
			EGLDDRootHelper.addOrUpdateParameter(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlSchema, getDefaultSchema());
		}
		
		return sqlBinding;
	}
}
