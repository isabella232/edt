/*******************************************************************************
 * Copyright © 2006, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.resources.egldd;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;






public class RuntimeDeploymentDesc {

	private String name;
	
//	private ArrayList<RestBinding> restBindings;
	
//	private ArrayList<SQLDatabaseBinding> sqlDatabaseBindings;
	
	protected ArrayList<String> includes;	
	
	private List<Binding> bindings = new ArrayList<Binding>();

	public static RuntimeDeploymentDesc createRuntimeDeploymentDesc(String filepath) throws Exception
	{
		RuntimeDeploymentDesc desc = new RuntimeDeploymentDesc();
		desc.setName(getNameFromFilePath(filepath));
		RuntimeDeploymentDescParser parser = new RuntimeDeploymentDescParser();
		parser.parse(desc, filepath);	
		return desc;
	}
	
	public static RuntimeDeploymentDesc createDeploymentDescriptor(String name, InputStream is) throws Exception
	{
		RuntimeDeploymentDesc desc = new RuntimeDeploymentDesc();
		desc.setName(name);
		RuntimeDeploymentDescParser parser = new RuntimeDeploymentDescParser();
		parser.parse(desc, is);	
		return desc;
	}
	
	protected static String getNameFromFilePath(String path)
	{
		String name = null;
		String fileName = new File(path).getName();
		int eIdx = fileName.lastIndexOf('.');
		if (eIdx < 1)
			eIdx = fileName.length();
		name = fileName.substring(0, eIdx);		
		return name;
	}
	
	protected RuntimeDeploymentDesc()
	{
		bindings = new ArrayList<Binding>();
		includes = new ArrayList<String>();
	}
	
	public List<SQLDatabaseBinding> getSqlDatabaseBindings() {
		List<SQLDatabaseBinding> sqlDatabaseBindings = new ArrayList<SQLDatabaseBinding>();
		for(Binding binding : bindings){
			if(binding instanceof SQLDatabaseBinding){
				sqlDatabaseBindings.add((SQLDatabaseBinding)binding);
			}
		}
		return sqlDatabaseBindings;
	}

	public void addSqlDatabaseBindingsAll(List<SQLDatabaseBinding> bindings)
	{
		bindings.addAll(bindings);
	}
	
	public void addSqlDatabaseBindings(SQLDatabaseBinding binding)
	{
		bindings.add(binding);
	}

	public void addRestBinding(RestBinding binding)
	{
		bindings.add(binding);
	}
	
	public void addRestBindingsAll(List<RestBinding> bindings)
	{
		this.bindings.addAll(bindings);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.toLowerCase();
	}

	public List<RestBinding> getRestBindings() {
		List<RestBinding> restBindings = new ArrayList<RestBinding>();
		for(Binding binding : bindings){
			if(binding instanceof RestBinding){
				restBindings.add((RestBinding)binding);
			}
		}
		return restBindings;
	}

	public void addInclude(String location)
	{
		this.includes.add(location);
	}
	
	public ArrayList<String> getIncludedDescs()
	{
		return includes;
	}
	
	public ArrayList<String> getIncludes()
	{
		return includes;
	}
	
	public void addBinding(Binding binding)
	{
		if(Binding.BINDING_DB_SQL.equalsIgnoreCase(binding.getType())){
			bindings.add(new SQLDatabaseBinding(binding));
		}
		else if(Binding.BINDING_SERVICE_REST.equalsIgnoreCase(binding.getType())){
			bindings.add(new RestBinding(binding));
		}
		else{
			bindings.add(new Binding(binding));
		}
	}
	
	public List<Binding> getBindings(){
		return bindings;
	}
}
