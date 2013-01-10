/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfoRequestor;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentProject;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Parameter;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.ResourceOmissions;
import org.eclipse.edt.ide.ui.internal.deployment.Service;
import org.eclipse.edt.ide.ui.internal.deployment.Services;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;

public class EGLDDRootHelper {
	public static final int PORT_MAXVALUE = 65536;	
	public static final String EXTENSION_EGLDD = "egldd"; //$NON-NLS-1$
	
	//key is the path string of the .eglbind file
	//value is EGLModulexFileInfo
	private static HashMap fSharedModuleModel;	
	
	private static class EGLDDFileInfo{
		public EGLDeploymentRoot serviceBindingRoot = null;
		public int actionClientCount = 0;
		public int userClientCount = 0;
	}
			
	private static void persistEGLDDFile(IFile eglBindFile, EGLDeploymentRoot documentRoot) throws IOException {
		//persist the file to disk

		//URI uri = URI.createFileURI(moduleFile.getFullPath().toOSString());
		URI uri = URI.createPlatformResourceURI(eglBindFile.getFullPath().toOSString(), true);
		Resource savedResource = documentRoot.eResource();
		Resource resource = savedResource;
		Map options = Collections.EMPTY_MAP;
		if(!savedResource.getURI().equals(uri)){		
			ResourceSet resourceSet = new ResourceSetImpl();
			resource = resourceSet.createResource(uri);

			//by default, create it with EGL preference
			String encoding = EGLBasePlugin.getPlugin().getPreferenceStore().getString(EGLBasePlugin.OUTPUT_CODESET);
			if(encoding != null && encoding.length()>0){
				options = new HashMap();			
				options.put(XMLResource.OPTION_ENCODING, encoding);
			}			
		}

		resource.getContents().add(documentRoot);
		resource.save(options);		//create the file			
	}	
	
	public static void releaseSharedWorkingModel(IFile moduleFile, boolean isUserClient)
	{
		if(fSharedModuleModel != null){
			String moduleFilePathString = moduleFile.getFullPath().toOSString();
			Object obj = fSharedModuleModel.get(moduleFilePathString);
			if(obj != null){
				EGLDDFileInfo fileinfo = (EGLDDFileInfo)obj;
				if(isUserClient){
					if(fileinfo.userClientCount>0)
						fileinfo.userClientCount--;
				}
				else{
					if(fileinfo.actionClientCount > 0)
						fileinfo.actionClientCount--;
				}
				
				if(fileinfo.actionClientCount + fileinfo.userClientCount == 0)
					fSharedModuleModel.remove(moduleFilePathString);		//if the use count is 0, 
			}			
		}
	}
	
	public static boolean isWorkingModelSharedByUserClients(IFile moduleFile)
	{
		if(fSharedModuleModel != null){
			String moduleFilePathString = moduleFile.getFullPath().toOSString();
			Object obj = fSharedModuleModel.get(moduleFilePathString);
			if(obj != null){
				EGLDDFileInfo fileinfo = (EGLDDFileInfo)obj;
				if(fileinfo.userClientCount > 0)
					return true;				
			}			
		}
		return false;		
	}
	
	/** 
	 * clien must call releaseSharedWorkingModel after finish using the returned EGLModuleRoot
	 * 
	 * @param serviceBindFile
	 * @param resourceSet
	 * @return
	 */
	public static EGLDeploymentRoot getEGLDDFileSharedWorkingModel(IFile serviceBindFile, ResourceSet resourceSet, boolean isUserClient)
	{
		EGLDDFileInfo eglbindfileinfo = null;
		
		if(fSharedModuleModel == null)
			fSharedModuleModel = new HashMap();
		
		String serviceBindFilePathString = serviceBindFile.getFullPath().toOSString();
		
		Object obj = fSharedModuleModel.get(serviceBindFilePathString);
		if(obj == null)		//not found in the map
		{
			//URI uri = URI.createFileURI(moduleFile.getLocation().toOSString());
			URI uri = URI.createPlatformResourceURI(serviceBindFile.getFullPath().toOSString(), true);
			
			Resource resource = resourceSet.getResource(uri, true);
//			System.out.println("Loaded " + uri); //$NON-NLS-1$

			// Validate the contents of the loaded resource.
			//
			//TODO fix before checkin
			EGLDeploymentRoot serviceBindRoot = (EGLDeploymentRoot)(resource.getContents().get(0));
			//EGLDeploymentRoot serviceBindRoot = DeploymentPackage.
			
			eglbindfileinfo = new EGLDDFileInfo();
			eglbindfileinfo.serviceBindingRoot = serviceBindRoot;
			
			//add to the hash table
			fSharedModuleModel.put(serviceBindFilePathString, eglbindfileinfo);
		}
		else 
		{
			eglbindfileinfo = (EGLDDFileInfo) obj;
		}
		
		if(isUserClient)
			eglbindfileinfo.userClientCount ++;
		else
			eglbindfileinfo.actionClientCount ++;		//increment the use count of this shared working copy model
		
		return eglbindfileinfo.serviceBindingRoot;		
	}
	
	/**
	 * clien must call releaseSharedWorkingModel after finish using the returned EGLModuleRoot
	 * 
	 * @param moduleFile
	 * @return
	 */
	public static EGLDeploymentRoot getEGLDDFileSharedWorkingModel(IFile serviceBindFile, boolean isUserClient)
	{
		ResourceSet resourceSet = new ResourceSetImpl();
		if(serviceBindFile!=null && serviceBindFile.exists())
			return getEGLDDFileSharedWorkingModel(serviceBindFile, resourceSet, isUserClient);
		return null;
	}	
		
	/**
	 * 
	 * 	
	 * @param eglBindFile	- IFile of the egl.modulex file, only a handle, may not exist yet
	 * @return - the EMF model module of the newly created egl.modulex file
	 */
	public static void createNewEGLDDFile(IFile eglBindFile, String encoding)
	{			
		EGLDeploymentRoot documentRoot = DeploymentFactory.eINSTANCE.createEGLDeploymentRoot();		
		Deployment root = DeploymentFactory.eINSTANCE.createDeployment();		
		documentRoot.setDeployment(root);
//		IPreferenceStore preferenceStore = EDTUIPlugin.getDefault().getPreferenceStore();

		//TODO fix before checkin
//		String prefValue = preferenceStore.getString(IEGLPreferenceConstants.SERVICE_EGLDD_SERVICERUNTIME_DEFAULT);
//		if( WebserviceRuntimeType.JAXWS_LITERAL.getLiteral().equalsIgnoreCase(prefValue) ){
//			root.setWebserviceRuntime(WebserviceRuntimeType.JAXWS_LITERAL);
//		}
		
		try{				
//			Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
			URI uri = URI.createPlatformResourceURI(eglBindFile.getFullPath().toOSString(), true);			

			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.createResource(uri);
			resource.getContents().add(documentRoot);	

			if(encoding != null && encoding.length()>0)
			{
				Map options = new HashMap();
				options.put(XMLResource.OPTION_ENCODING, encoding);		
				resource.save(options);		//create the file
			}
			else
				resource.save(null);			
		}
		catch (IOException exception) {
			EDTUIPlugin.log( exception );
		}
		//return documentRoot;
	}
	
	public static void saveEGLDDFile(IFile eglBindFile, EGLDeploymentRoot documentRoot)
	{
		//persist the file using the emf model	
		try{					
			persistEGLDDFile(eglBindFile, documentRoot);
		}
		catch (IOException exception) {
			EDTUIPlugin.log( exception );
		}		
	}
		
	public static IFile findPartFile(String fullyqualifiedPartName, IEGLProject eglProj){
		try{
			PartDeclarationInfo part = find1stPartInfoInEGLProject(fullyqualifiedPartName, eglProj, IEGLSearchConstants.PART, null, true);
			if(part != null)
				return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(part.getPath()));
		}catch(CoreException e){
			EDTUIPlugin.log( e );
			return null;
		}
		return null;
	}

	public static IPart findPartInEGLProject(String fullyqualifiedPartName, IEGLProject eglProj) throws EGLModelException {
		IPart part = eglProj.findPart(fullyqualifiedPartName);					
		if(part == null){
			String[] refProjNames = eglProj.getRequiredProjectNames();
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();			
			for(int i=0; i<refProjNames.length&&(part == null); i++)
			{
				eglProj = EGLCore.create(workspaceRoot.getProject(refProjNames[i]));
				part = eglProj.findPart(fullyqualifiedPartName);							
			}
		}
		return part;
	}
	
	/**
	 * 
	 * @param fullyqualifiedPartName
	 * @param eglProj
	 * @param partKind - i.e. IEGLSearchConstants.SERVICE
	 * @param includeReferencedProjects - when create the project search scope, should include the referenced project?
	 * @return
	 * @throws EGLModelException 
	 */
	public static PartDeclarationInfo find1stPartInfoInEGLProject(String fullyqualifiedPartName, IEGLProject eglProj, int partKind, IProgressMonitor monitor, boolean includeReferencedProjects) throws EGLModelException{
		//parse the fully qualified name to package name and simple name
		int lastdot = fullyqualifiedPartName.lastIndexOf('.');
		String pkgName = ""; //$NON-NLS-1$
		String partSimpleName = fullyqualifiedPartName;
		if(lastdot != -1){
			pkgName = fullyqualifiedPartName.substring(0, lastdot);
			partSimpleName = fullyqualifiedPartName.substring(lastdot+1);
		}
		IEGLSearchScope projScope = SearchEngine.createEGLSearchScope(new IEGLElement[]{eglProj}, true);
		List typeList = new ArrayList();
		new SearchEngine().searchAllPartNames(ResourcesPlugin.getWorkspace(),
				pkgName.toCharArray(),
				partSimpleName.toCharArray(),
				IIndexConstants.EXACT_MATCH,
				IEGLSearchConstants.CASE_INSENSITIVE,
				partKind,
				projScope,
				new PartInfoRequestor(typeList),
				IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
				monitor);
			
			int foundCnts = typeList.size();
			if(foundCnts > 0)
			{
				PartDeclarationInfo foundPart = (PartDeclarationInfo)typeList.get(0);
				return foundPart;
			}		
		return null;
	}
	
	public static Binding getBindingByName(EGLDeploymentRoot root, String name){
		if(root != null){
			Deployment deployment = root.getDeployment();		
			Bindings bindings = deployment.getBindings();
			if(bindings != null){
				for (Binding binding : bindings.getBinding()) {
					if(name.equals(binding.getName())) {
						return binding;
					}
				}
			}
		}
		return null;
	}
	
	public static Service getServiceByImpl(EGLDeploymentRoot root, String impl){
		Deployment deployment = root.getDeployment();		
		Services services = deployment.getServices();
		if(services != null){
			for (Service service : services.getService()) {
				if(impl.equals(service.getImplementation())) {
					return service;
				}
			}
		}
		return null;
	}
	
	public static DeploymentTarget getDeploymentTarget( EGLDeploymentRoot root )
	{
		return root.getDeployment().getTarget();
	}
	
	public static String getTargetName( EGLDeploymentRoot root )
	{
		DeploymentTarget target = root.getDeployment().getTarget();
		if ( target == null )
		{
			return "";
		}
		return target.getName();
	}
	
	public static void setTarget( DeploymentTarget target, EGLDeploymentRoot root )
	{
		removeTarget( root );
		if ( target instanceof DeploymentProject )
		{
			root.getDeployment().getTargetGroup().add( DeploymentPackage.eINSTANCE.getEGLDeploymentRoot_TargetProject(), target );
		}
	}
	
	public static void removeTarget( EGLDeploymentRoot root )
	{
		root.getDeployment().getTargetGroup().clear();
	}
	
	public static Parameters getParameters( Binding binding )
	{
		Parameters p = binding.getParameters();
		if ( p == null )
		{
			p = DeploymentFactory.eINSTANCE.createParameters();
			binding.setParameters( p );
		}
		return p;
	}
	
	public static Parameters getParameters( Service service )
	{
		Parameters p = service.getParameters();
		if ( p == null )
		{
			p = DeploymentFactory.eINSTANCE.createParameters();
			service.setParameters( p );
		}
		return p;
	}
	
	public static void addOrUpdateParameter( Parameters parms, String name, boolean value )
	{
		addOrUpdateParameter( parms, name, String.valueOf( value ) );
	}
	
	public static void addOrUpdateParameter( Parameters parms, String name, char value )
	{
		addOrUpdateParameter( parms, name, String.valueOf( value ) );
	}
	
	public static void addOrUpdateParameter( Parameters parms, String name, int value )
	{
		addOrUpdateParameter( parms, name, String.valueOf( value ) );
	}
	
	public static void addOrUpdateParameter( Parameters parms, String name, String value )
	{
		boolean found = false;
		for ( Iterator it = parms.getParameter().iterator(); it.hasNext(); )
		{
			Parameter p = (Parameter)it.next();
			if ( p.getName().equals( name ) )
			{
				// Null or blank means remove it.
				if ( value == null || value.length() == 0 )
				{
					parms.getParameter().remove( p );
				}
				else
				{
					p.setValue( value );
				}
				found = true;
				break;
			}
		}
		
		if ( !found && value != null && value.length() != 0 )
		{
			Parameter p = DeploymentFactory.eINSTANCE.createParameter();
			p.setName( name );
			p.setValue( value );
			parms.getParameter().add( p );
		}
	}
	
	public static boolean getBooleanParameterValue( Parameters parms, String name )
	{
		return Boolean.parseBoolean(getParameterValue(parms, name));
	}
	
	public static String getParameterValue( Parameters parms, String name )
	{
		if ( parms != null )
		{
			for ( Iterator it = parms.getParameter().iterator(); it.hasNext(); )
			{
				Parameter p = (Parameter)it.next();
				if ( p.getName().equals( name ) )
				{
					String value = p.getValue();
					
					// Never return null.
					if ( value == null )
					{
						return "";
					}
					return value;
				}
			}
		}
		return "";
	}
	
	/**
	 * The lists should contain String elements and be non-null.
	 */
	public static void processResourceOmissionChanges( EGLDeploymentRoot root, List omissionsToAdd, List omissionsToRemove )
	{
		ResourceOmissions omissions = root.getDeployment().getResourceOmissions();
		if ( omissions == null && omissionsToAdd.size() > 0 )
		{
			omissions = DeploymentFactory.eINSTANCE.createResourceOmissions();
			root.getDeployment().setResourceOmissions( omissions );
		}
		
		if ( omissions != null )
		{
			// First pass - remove items from the 'add list' if already in the model, and
			// remove items from the 'remove list' from the model.
			for ( Iterator it = omissions.getResource().iterator(); it.hasNext(); )
			{
				String nextResource = ((org.eclipse.edt.ide.ui.internal.deployment.Resource)it.next()).getId();
				if ( omissionsToAdd.contains( nextResource ) )
				{
					omissionsToAdd.remove( nextResource );
				}
				else if ( omissionsToRemove.contains( nextResource ) )
				{
					it.remove();
				}
			}
			
			// Second pass - add new omissions.
			EList omissionList = omissions.getResource();
			for ( Iterator it = omissionsToAdd.iterator(); it.hasNext(); )
			{
				org.eclipse.edt.ide.ui.internal.deployment.Resource resource = DeploymentFactory.eINSTANCE.createResource();
				resource.setId( (String)it.next() );
				omissionList.add( resource );
			}
			
			if ( omissionList.size() == 0 )
			{
				// Clean up the XML...
				root.getDeployment().setResourceOmissions( null );
			}
		}
	}
	
	/**
	 * Returns a possible-null list of resource ids.
	 */
	public static List getResourceOmissionsAsStrings( EGLDeploymentRoot root )
	{
		ResourceOmissions omissions = root.getDeployment().getResourceOmissions();
		if ( omissions == null )
		{
			return null;
		}
		
		EList omissionList = omissions.getResource();
		List asStrings = new ArrayList( omissionList.size() );
		for ( Iterator it = omissionList.iterator(); it.hasNext(); )
		{
			asStrings.add( ((org.eclipse.edt.ide.ui.internal.deployment.Resource)it.next()).getId() );
		}
		return asStrings;
	}
	
	public static String getValidURI(Deployment deployment, String uriPrefix) {
		
		Services services = deployment.getServices();
		int maxSuffix = -1;
		for(Service ser:services.getService()){
			String uriValue = EGLDDRootHelper.getParameterValue(ser.getParameters(), Restservice.ATTRIBUTE_SERVICE_REST_uriFragment);
			if(uriValue != null){
				if(maxSuffix < 0 && uriValue.equals(uriPrefix)){
					maxSuffix = 0;
				}else if(uriValue.startsWith(uriPrefix)){
					String suffixStr = uriValue.substring(uriPrefix.length());
					try{
						int index = Integer.parseInt(suffixStr);
						if(index > maxSuffix){
							maxSuffix = index;
						}
					}catch(Exception e){
						continue;
					}
				}
			}
		}

		if(maxSuffix < 0){
			return uriPrefix;
		}else{
			return uriPrefix + (maxSuffix + 1);
		}
	}
}
