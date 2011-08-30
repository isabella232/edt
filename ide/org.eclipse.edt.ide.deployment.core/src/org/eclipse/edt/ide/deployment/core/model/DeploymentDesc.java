/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.core.model;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.edt.ide.deployment.core.DeploymentDescFileLocator;
import org.eclipse.edt.ide.deployment.core.DeploymentDescParser;
import org.eclipse.edt.ide.deployment.core.SimpleWriter;




public class DeploymentDesc {

	private String name;
	
	private String alias;
	
	private ArrayList eglBindings;
	
	private ArrayList webBindings;
	
	private ArrayList nativeBindings;
	
	private ArrayList restBindings;
	
	private ArrayList webservices;
	
	private ArrayList restservices;

	private ArrayList sqlDatabaseBindings;

	private RUIApplication ruiApplication;
	
	private ArrayList includes;
	
	private ArrayList eglParts;
	
	private List<String> resourceOmissions;

	private DeploymentTarget target;
	
	private boolean genOnWebsphere;
	
	private String webserviceRuntime;
	


	public static DeploymentDesc createDeploymentDescriptor(String name, boolean isOnWebspere) throws Exception
	{
		DeploymentDesc desc = new DeploymentDesc();
		desc.setName(name);
		desc.setGenOnWebsphere( isOnWebspere );
		return desc;
	}
	
	public static DeploymentDesc createDeploymentDescriptor(String filepath) throws Exception
	{
		DeploymentDesc desc = new DeploymentDesc();
		desc.setName(getNameFromFilePath(filepath));
		DeploymentDescParser parser = new DeploymentDescParser();
		parser.parse(desc, filepath);	
		return desc;
	}
	
	public static DeploymentDesc createDeploymentDescriptor(String name, InputStream is) throws Exception
	{
		DeploymentDesc desc = new DeploymentDesc();
		desc.setName(name);
		DeploymentDescParser parser = new DeploymentDescParser();
		parser.parse(desc, is);	
		return desc;
	}
	

	public String toBindXML() throws Exception
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buf.append("<bindings");
		if (genOnWebsphere)
			buf.append(" onWebsphere=\"true\"");
		buf.append(">\n");
		if (WebserviceRuntimeType.JAXWS.equalsIgnoreCase(webserviceRuntime))
			buf.append("   <webserviceRuntime>" + WebserviceRuntimeType.JAXWS + "</webserviceRuntime>\n");
		for (int i = 0; i < eglBindings.size(); i++)
		{
			buf.append(((EGLBinding) eglBindings.get(i)).toBindXML("   "));
		}
		for (int i = 0; i < webBindings.size(); i++)
		{
			buf.append(((WebBinding) webBindings.get(i)).toBindXML("   "));
		}
		for (int i = 0; i < restBindings.size(); i++)
		{
			buf.append(((RestBinding) restBindings.get(i)).toBindXML("   "));
		}
		for (int i = 0; i < nativeBindings.size(); i++)
		{
			buf.append(((NativeBinding) nativeBindings.get(i)).toBindXML("   "));
		}
		buf.append("</bindings>\n");
		return buf.toString();
	}
	private static String getNameFromFilePath(String path)
	{
		String name = null;
		String fileName = new File(path).getName();
		int eIdx = fileName.lastIndexOf('.');
		if (eIdx < 1)
			eIdx = fileName.length();
		name = fileName.substring(0, eIdx);		
		return name;
	}
	
	private DeploymentDesc()
	{
		eglBindings = new ArrayList();
		webBindings = new ArrayList();
		restBindings = new ArrayList();
		nativeBindings = new ArrayList();
		webservices = new ArrayList();
		restservices = new ArrayList();
		sqlDatabaseBindings = new ArrayList();
		includes = new ArrayList();
		resourceOmissions = new ArrayList<String>();
	}
	
	public ArrayList getSqlDatabaseBindings() {
		return sqlDatabaseBindings;
	}

	public void setSqlDatabaseBindings(ArrayList sqlDatabaseBindings) {
		this.sqlDatabaseBindings = sqlDatabaseBindings;
	}
	
	public void addSqlDatabaseBindingsAll(ArrayList bindings)
	{
		sqlDatabaseBindings.addAll(bindings);
	}
	
	public void addSqlDatabaseBindings(SQLDatabaseBinding binding)
	{
		sqlDatabaseBindings.add(binding);
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	
	public void addEGLBinding(EGLBinding binding)
	{
		eglBindings.add(binding);
	}
	
	public void addEGLBindingsAll(ArrayList bindings)
	{
		eglBindings.addAll(bindings);
	}
	
	public void addNativeBinding(NativeBinding binding)
	{
		nativeBindings.add(binding);
	}
	
	public void addNativeBindingsAll(ArrayList bindings)
	{
		nativeBindings.addAll(bindings);
	}
	
	public void addWebBinding(WebBinding binding)
	{
		webBindings.add(binding);
	}
	
	public void addWebBindingsAll(ArrayList bindings)
	{
		webBindings.addAll(bindings);
	}
	
	public void addRestBinding(RestBinding binding)
	{
		restBindings.add(binding);
	}
	
	public void addRestBindingsAll(ArrayList bindings)
	{
		restBindings.addAll(bindings);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList getWebservices() {
		return webservices;
	}

	public ArrayList getRestservices() {
		return restservices;
	}

	public RUIApplication getRUIApplication() {
		return ruiApplication;
	}

	public void setWebservices(ArrayList webservices) {
		this.webservices = webservices;
	}
	
	public void setRestservices(ArrayList restservices) {
		this.restservices = restservices;
	}
	
	public void setRUIApplication(RUIApplication application) {
		this.ruiApplication = application;
	}
	
	public void addWebservice(Webservice webservice)
	{
		webservices.add(webservice);
	}

	public void addRestservice(Restservice restservice)
	{
		restservices.add(restservice);
	}

	public ArrayList getEglBindings() {
		return eglBindings;
	}

	public void setEglBindings(ArrayList eglBindings) {
		this.eglBindings = eglBindings;
	}
	
	public ArrayList getNativeBindings() {
		return nativeBindings;
	}

	public void setNativeBindings(ArrayList nativeBindings) {
		this.nativeBindings = nativeBindings;
	}

	public ArrayList getWebBindings() {
		return webBindings;
	}

	public void setWebBindings(ArrayList webBindings) {
		this.webBindings = webBindings;
	}
	
	public ArrayList getRestBindings() {
		return restBindings;
	}

	public void setRestBindings(ArrayList restBindings) {
		this.restBindings = restBindings;
	}
	
	public void addInclude(String location)
	{
		this.includes.add(location);
	}
	
	public ArrayList getIncludedDescs()
	{
		return includes;
	}
	
	public ArrayList getIncludes()
	{
		return includes;
	}
	
	public void setTarget(DeploymentTarget target)
	{
		this.target = target;
	}
	
	public DeploymentTarget getDeploymentTarget()
	{
		return target;
	}
	
	public void addResourceOmission(String resource)
	{
		resourceOmissions.add(resource);
	}
	
	public void removeResourceOmission(String resource)
	{
		this.resourceOmissions.remove(resource);
	}

	public List<String> getResourceOmissions()
	{
		return resourceOmissions;
	}
	
	public ArrayList getEGLServiceParts()
	{
		if (eglParts == null)
		{
			eglParts = new ArrayList();
			ListIterator it = webBindings.listIterator();
			while (it.hasNext())
			{
				eglParts.add(((WebBinding) it.next()).getInterface());
			}
			it = webservices.listIterator();
			while (it.hasNext())
			{
				eglParts.add(((Webservice) it.next()).getImplementation());
			}			
			it = restservices.listIterator();
			while (it.hasNext())
			{
				eglParts.add(((Restservice) it.next()).getImplementation());
			}			
		}
		
		return eglParts;
	}
	

	public static void main (String [] args)
	{
		SimpleWriter out;
		if (args.length > 0)
		{
			String descpath = args[0];
			StringBuffer buffer = new StringBuffer(descpath);
			int idx = buffer.lastIndexOf("\\");
			if (idx == -1)
				idx = buffer.lastIndexOf("/");
			if (idx != -1)
				buffer.delete(idx, buffer.length());

			
			try
			{
				DeploymentDesc desc = DeploymentDesc.createDeploymentDescriptor(descpath);
				String fileExtension = "-bnd.xml";
				String directoryPath = buffer.toString();
				if (!directoryPath.endsWith("/"))
					directoryPath = directoryPath + "/";

				
				String filePath = directoryPath + desc.getName() + fileExtension;

				out = new SimpleWriter(filePath);
				out.println( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
				out.print(desc.toBindXML());
				out.close();
			
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Usage: DeploymentDesc <egldd path>");
		}
	}

	public boolean isGenOnWebsphere() {
		return genOnWebsphere;
	}

	public void setGenOnWebsphere(boolean genOnWebsphere) {
		this.genOnWebsphere = genOnWebsphere;
	}
	
	public void setWebserviceRuntime(String webserviceRuntime) {
		this.webserviceRuntime = webserviceRuntime;
	}
	
	public String getWebserviceRuntime() {
		return webserviceRuntime;
	}
	
	public void resolveIncludes(DeploymentDescFileLocator locator) throws Exception {
		resolveDeploymentDescriptors( this, this, locator );
	}
	
	private void resolveDeploymentDescriptors( DeploymentDesc root, DeploymentDesc deploymentDesc, DeploymentDescFileLocator locator ) throws Exception
	{
		for ( int i = 0; i < deploymentDesc.includes.size(); i ++ ) {
			String include = (String)includes.get( i );
			DeploymentDesc includedDeploymentDesc = createDeploymentDescriptor( include, locator.getInputStream( include ) );
			deploymentDesc.addEGLBindingsAll( includedDeploymentDesc.getEglBindings() );
			deploymentDesc.addWebBindingsAll( includedDeploymentDesc.getWebBindings() );
			deploymentDesc.addNativeBindingsAll(includedDeploymentDesc.getNativeBindings());
			deploymentDesc.addRestBindingsAll(includedDeploymentDesc.getRestBindings());
			resolveDeploymentDescriptors( root, includedDeploymentDesc, locator );
		}
	}
	
}
