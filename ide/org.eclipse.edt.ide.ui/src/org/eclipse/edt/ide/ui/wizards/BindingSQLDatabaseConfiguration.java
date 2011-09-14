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
package org.eclipse.edt.ide.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding;

public class BindingSQLDatabaseConfiguration extends BindingEGLConfiguration {
	
	private String dbVendorAndVersion="";
	private String driverClass="";
	private String connUrl="";
	private String userName="";
	private String password="";
	
	private String dbName="";
	private String jndiName="";
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

	protected int getBindingType() {
		return EGLDDBindingConfiguration.BINDINGTYPE_SQL;	
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
	
	public String getJndiName(){
		return jndiName;
	}
	
	public void setJndiName(String jndiName){
		this.jndiName = jndiName;
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
	
	public Object executeAddSQLDatabaseBinding(Bindings bindings){
		SQLDatabaseBinding sqlBinding = DeploymentFactory.eINSTANCE.createSQLDatabaseBinding();
		bindings.getSqlDatabaseBinding().add(sqlBinding);

		sqlBinding.setName(getBindingName());
		sqlBinding.setDbms(getDbms());
		sqlBinding.setSqlID(getUserName());
		sqlBinding.setSqlPassword(getPassword());
		//sqlBinding.setSqlValidationConnectionURL(getConnUrl());
		
		sqlBinding.setJarList(getConnLocation());
		sqlBinding.setSqlJDBCDriverClass(getDriverClass());
		sqlBinding.setSqlDB(getConnUrl());
		sqlBinding.setSqlJNDIName(getJndiName());
		sqlBinding.setSqlSchema(getDefaultSchema());
		
		return sqlBinding;
	}
}
