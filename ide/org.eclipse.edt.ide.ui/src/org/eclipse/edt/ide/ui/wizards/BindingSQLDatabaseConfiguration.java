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
	
	private String dbms="";
	private String driverClass="";
	private String connUrl="";
	private String userName="";

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
		return dbms;
	}
	
	public void setDbms(String dbms){
		this.dbms = dbms;
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
	
	public Object executeAddSQLDatabaseBinding(Bindings bindings){
		SQLDatabaseBinding sqlBinding = DeploymentFactory.eINSTANCE.createSQLDatabaseBinding();
		bindings.getSqlDatabaseBinding().add(sqlBinding);

		sqlBinding.setDbms(getDbms());
		sqlBinding.setName(getUserName());
		sqlBinding.setSqlValidationConnectionURL(getConnUrl());
		sqlBinding.setSqlJDBCDriverClass(getDriverClass());
		
		return sqlBinding;
	}
}
