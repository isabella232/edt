/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.CICSWSProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentBuildDescriptor;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentProject;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget;
import org.eclipse.edt.ide.ui.internal.deployment.EGLBinding;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.IMSJ2CProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.IMSTCPProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.Include;
import org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.LocalProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.NativeBinding;
import org.eclipse.edt.ide.ui.internal.deployment.Parameter;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.Protocols;
import org.eclipse.edt.ide.ui.internal.deployment.ReferenceProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.ResourceOmissions;
import org.eclipse.edt.ide.ui.internal.deployment.Restservice;
import org.eclipse.edt.ide.ui.internal.deployment.Restservices;
import org.eclipse.edt.ide.ui.internal.deployment.StyleTypes;
import org.eclipse.edt.ide.ui.internal.deployment.SystemIProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.WebBinding;
import org.eclipse.edt.ide.ui.internal.deployment.Webservice;
import org.eclipse.edt.ide.ui.internal.deployment.Webservices;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Widget;

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
		IPreferenceStore preferenceStore = EDTUIPlugin.getDefault().getPreferenceStore();

		//TODO fix before checkin
//		String prefValue = preferenceStore.getString(IEGLPreferenceConstants.SERVICE_EGLDD_SERVICERUNTIME_DEFAULT);
//		if( WebserviceRuntimeType.JAXWS_LITERAL.getLiteral().equalsIgnoreCase(prefValue) ){
//			root.setWebserviceRuntime(WebserviceRuntimeType.JAXWS_LITERAL);
//		}
		
		//---- test
//		root.setAlias("MYBIND");
//		Protocol protocol = ServiceBindingFactory.eINSTANCE.createProtocol();
//		protocol.setCommType(CommTypes.CICSECI_LITERAL);
//		protocol.setName("NQA17C02");
//		protocol.setCtgPort("2113");
//		root.getProtocol().add(protocol);
//		
//		EGLBinding eglbinding = ServiceBindingFactory.eINSTANCE.createEGLBinding();
//		eglbinding.setName("HelloWorld");
//		protocol = ServiceBindingFactory.eINSTANCE.createProtocol();
//		protocol.setRef("NQA17C02");
//		eglbinding.setProtocol(protocol);
//		root.getEglBinding().add(eglbinding);
//		
//		eglbinding = ServiceBindingFactory.eINSTANCE.createEGLBinding();
//		eglbinding.setName("HelloWorld1");
//		protocol = ServiceBindingFactory.eINSTANCE.createProtocol();
//		protocol.setCommType(CommTypes.TCPIP_LITERAL);
//		protocol.setName("NQA17C02");
//		protocol.setLocation("2113");
//		eglbinding.setProtocol(protocol);
//		root.getEglBinding().add(eglbinding);
//		
//		WebBinding webbinding = ServiceBindingFactory.eINSTANCE.createWebBinding();
//		webbinding.setName("StockQuote");
//		webbinding.setInterface("com.nasdaq.StockQuote");
//		root.getWebBinding().add(webbinding);
		//---- test
		
		try{				
			Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
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
	
	public static String getProtocolCommTypeString(Protocol protocol){
		if(protocol instanceof ReferenceProtocol)
			return ((ReferenceProtocol)protocol).getRef();
		else
			return getProtocolCommType(protocol).getLiteral();			
	}
	
	public static CommTypes getProtocolCommType(Protocol protocol){
		if(protocol instanceof LocalProtocol)
			return CommTypes.LOCAL_LITERAL;
		else if(protocol instanceof TCPIPProtocol)
			return CommTypes.TCPIP_LITERAL;
		else if(protocol instanceof Java400Protocol)
			return CommTypes.JAVA400_LITERAL;
		else if(protocol instanceof Java400J2cProtocol)
			return CommTypes.JAVA400J2C_LITERAL;
		else if(protocol instanceof CICSECIProtocol)
			return CommTypes.CICSECI_LITERAL;
		else if(protocol instanceof CICSJ2CProtocol)
			return CommTypes.CICSJ2C_LITERAL;
		else if(protocol instanceof CICSSSLProtocol)
			return CommTypes.CICSSSL_LITERAL;
		else if(protocol instanceof IMSJ2CProtocol)
			return CommTypes.IMSJ2C_LITERAL;
		else if(protocol instanceof IMSTCPProtocol)
			return CommTypes.IMSTCP_LITERAL;
		else if(protocol instanceof SystemIProtocol)
			return CommTypes.SYSTEMI_LOCAL_LITERAL;
		else if(protocol instanceof CICSWSProtocol)			
			return CommTypes.CICSWS_LITERAL;		
		else if(protocol instanceof ReferenceProtocol)
			return null;
		return CommTypes.LOCAL_LITERAL;
	}
	
	/**
	 * 	remove the current protocol on eglBinding if the newCommType differs from current commType
	 *  create a new protocol instance based on newCommType, set it to eglBinding
	 *     	
	 * @param eglBinding
	 * @param newCommtype
	 */
	public static Protocol setNewProtocolOnEGLBinding(Protocol currProcotol, FeatureMap protocolgrp, CommTypes newCommtype){
		//Protocol currProcotol = eglBinding.getProtocol();
		CommTypes currCommtype = getProtocolCommType(currProcotol);		
		
		//FeatureMap protocolgrp = eglBinding.getProtocolGroup();		
		Protocol newProtocol = null;				
		if(currProcotol != null){			
			if(currCommtype == newCommtype)
				newProtocol = currProcotol;
			else //remove the current protocol
				protocolgrp.unset(currProcotol.eContainmentFeature());
		}
		   			
		if(newProtocol == null){
			newProtocol = createNewProtocol(newCommtype);
			setProtocolOnProtocolGroup(protocolgrp, newProtocol);
		}
		return newProtocol;
	}
	
	public static void setProtocolOnProtocolGroup(FeatureMap protocolgrp, Protocol protocol){
		DeploymentPackage pkg = DeploymentPackage.eINSTANCE;

		if(protocol instanceof ReferenceProtocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolRef(), protocol);
		}
		else if(protocol instanceof LocalProtocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolLocal(), protocol);
		}
		else if(protocol instanceof Java400Protocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolJava400(), protocol);
		}
		else if(protocol instanceof Java400J2cProtocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolJava400j2c(), protocol);
		}
		else if(protocol instanceof TCPIPProtocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolTcpip(), protocol);
		}
		else if(protocol instanceof CICSECIProtocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolCicseci(), protocol);
		}
		else if(protocol instanceof CICSJ2CProtocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolCicsj2c(), protocol);   				
		}
		else if(protocol  instanceof CICSSSLProtocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolCicsssl(), protocol);
		}   	
		else if(protocol instanceof IMSJ2CProtocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolImsj2c(), protocol);
		}
		else if(protocol instanceof IMSTCPProtocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolImstcp(), protocol);
		}
		else if(protocol instanceof SystemIProtocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolSystemILocal(), protocol);
		}
		else if(protocol instanceof CICSWSProtocol){
			protocolgrp.add(pkg.getEGLDeploymentRoot_ProtocolCicsws(), protocol);
		}
	}
	
	public static Protocol createNewProtocol(CommTypes commtype){
		DeploymentFactory factory = DeploymentFactory.eINSTANCE;
		Protocol newProtocol=null;
		if(commtype == CommTypes.LOCAL_LITERAL){
			newProtocol = factory.createLocalProtocol();
		}
		else if(commtype == CommTypes.JAVA400_LITERAL){
			newProtocol = factory.createJava400Protocol();
		}
		else if(commtype == CommTypes.JAVA400J2C_LITERAL){
			newProtocol = factory.createJava400J2cProtocol();
		}
		else if(commtype == CommTypes.TCPIP_LITERAL){
			newProtocol = factory.createTCPIPProtocol();
		}
		else if(commtype == CommTypes.CICSECI_LITERAL){
			newProtocol = factory.createCICSECIProtocol();
		}
		else if(commtype == CommTypes.CICSJ2C_LITERAL){
			newProtocol = factory.createCICSJ2CProtocol();   				
		}
		else if(commtype == CommTypes.CICSSSL_LITERAL){
			newProtocol = factory.createCICSSSLProtocol();
		}   	
		else if(commtype == CommTypes.IMSJ2C_LITERAL){
			newProtocol = factory.createIMSJ2CProtocol();
		}
		else if(commtype == CommTypes.IMSTCP_LITERAL){
			newProtocol = factory.createIMSTCPProtocol();
		}
		else if(commtype == CommTypes.SYSTEMI_LOCAL_LITERAL){
			newProtocol = factory.createSystemIProtocol();
		}
		else if(commtype == CommTypes.CICSWS_LITERAL){
			newProtocol = factory.createCICSWSProtocol();
		}
		return newProtocol;
	}
	
	public static ReferenceProtocol setNewReferenceProtocolOnEGLBinding(Protocol currProtocol, FeatureMap protocolgrp, String refName){
//		Protocol currProtocol = eglBinding.getProtocol();
//		FeatureMap protocolgrp = eglBinding.getProtocolGroup();
		ReferenceProtocol newProtocol = null;
		if(currProtocol != null){
			if(currProtocol instanceof ReferenceProtocol)
				newProtocol = (ReferenceProtocol)currProtocol;
			else{	//remove the currProtocol
				protocolgrp.unset(currProtocol.eContainmentFeature());
			}				
		}
		
		if(newProtocol == null){
			newProtocol = DeploymentFactory.eINSTANCE.createReferenceProtocol();
			setProtocolOnProtocolGroup(protocolgrp, newProtocol);
		}
		newProtocol.setRef(refName);
		return newProtocol;
	}
	
	public static String[] getStyleComboItems() {		
		List styleNames = new ArrayList();
		
		List styleTypes = StyleTypes.VALUES;
		for(Iterator it=styleTypes.iterator(); it.hasNext();) {
			StyleTypes itType = ((StyleTypes)it.next());
			if(itType!=null) {
				styleNames.add(itType.getLiteral());
			}
		}
		
		return (String[])(styleNames.toArray(new String[]{}));
	}
		
	public static String[] getProtocolComboItems(EGLDeploymentRoot root, Widget comboWidget, int bindingType) {		
		List protocolNames = new ArrayList();
		List protocolNodes = new ArrayList();
		if(root != null){
			collectProtocols(root, protocolNames, protocolNodes, CommTypes.getSupportedProtocol(bindingType), "");			 //$NON-NLS-1$
			collectProtocolsFromIncludes(root, protocolNames, protocolNodes, CommTypes.getSupportedProtocol(bindingType));
		}
		comboWidget.setData((Protocol[])(protocolNodes.toArray(new Protocol[]{})));
		return (String[])(protocolNames.toArray(new String[]{}));
	}

	private static void collectProtocolsFromIncludes(EGLDeploymentRoot root, List protocolNames, List protocolNodes, List filterIncludedCommTypes) {
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		//also add the shared protocols from included egldd files
		EList includes = root.getDeployment().getInclude();
		for(Iterator iti = includes.iterator(); iti.hasNext();){
			Include includedd = (Include)iti.next();
			IFile includeddfile = wsRoot.getFile(new Path(includedd.getLocation()));
			EGLDeploymentRoot includeRoot = EGLDDRootHelper.getEGLDDFileSharedWorkingModel(includeddfile, false);
			collectProtocols(includeRoot, protocolNames, protocolNodes, filterIncludedCommTypes, includeddfile.getName());
			EGLDDRootHelper.releaseSharedWorkingModel(includeddfile, false);
		}
	}

	private static void collectProtocols(EGLDeploymentRoot root, List protocolNames, List protocolNodes, List filterIncludedCommTypes, String ddfileName) {
		if(root != null){
			Protocols protocolS = root.getDeployment().getProtocols();
			if(protocolS != null){
				EList protocols = protocolS.getProtocol();		
				for(Iterator it = protocols.iterator(); it.hasNext();){
					Protocol protocol = (Protocol)it.next();
					if(filterIncludedCommTypes == null || 
							((filterIncludedCommTypes != null) && (filterIncludedCommTypes.contains(getProtocolCommType(protocol))))){
						protocolNodes.add(protocol);
						String displayName = getProtocolDisplayText(protocol) ;
						if(ddfileName.length()>0)
							displayName += " - " + ddfileName; //$NON-NLS-1$
						protocolNames.add(displayName); //$NON-NLS-1$ //$NON-NLS-2$						
					}
				}
			}
		}
	}

	public static String getProtocolDisplayText(Protocol protocol) {
		String commtypeString = getProtocolCommType(protocol).getLiteral();
		String displayName = protocol.getName()+ " (" + commtypeString + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		return displayName ;
	}	
	
	public static String[] getProtocolComboItemsByType(EGLDeploymentRoot root, List filterIncludedCommTypes, Widget comboWidget){
		List protocolNames = new ArrayList();
		List protocolNodes = new ArrayList();
		if(root != null){
			collectProtocols(root, protocolNames, protocolNodes, filterIncludedCommTypes, "");			 //$NON-NLS-1$
			collectProtocolsFromIncludes(root, protocolNames, protocolNodes, filterIncludedCommTypes);
		}
		comboWidget.setData((Protocol[])(protocolNodes.toArray(new Protocol[]{})));
		return (String[])(protocolNames.toArray(new String[]{}));
	}
	
	public static EGLBinding getEGLBindingByName(EGLDeploymentRoot root, String name){
		if(root != null){
			Deployment deployment = root.getDeployment();		
			Bindings bindings = deployment.getBindings();
			if(bindings != null){
				for(Iterator it = bindings.getEglBinding().iterator(); it.hasNext();){
					EGLBinding eglbinding = (EGLBinding)it.next();
					if(name.equals(eglbinding.getName()))
						return eglbinding;
				}
			}
		}
		return null;
	}
	
	public static NativeBinding getNativeBindingByName(EGLDeploymentRoot root, String name){
		if(root != null){
			Deployment deployment = root.getDeployment();		
			Bindings bindings = deployment.getBindings();
			if(bindings != null){
				for(Iterator it = bindings.getNativeBinding().iterator(); it.hasNext();){
					NativeBinding nativeBinding = (NativeBinding)it.next();
					if(name.equals(nativeBinding.getName()))
						return nativeBinding;
				}
			}			
		}
		return null;
	}
	
	public static WebBinding getWebBindingByName(EGLDeploymentRoot root, String name){
		if(root != null){
			Deployment deployment = root.getDeployment();		
			Bindings bindings = deployment.getBindings();
			if(bindings != null){
				for(Iterator it = bindings.getWebBinding().iterator(); it.hasNext();){
					WebBinding webbinding = (WebBinding)it.next();
					if(name.equals(webbinding.getName()))
						return webbinding;
				}			
			}
		}
		return null;
	}

	public static Protocol getProtocolByName(EGLDeploymentRoot root, String name){
		Deployment deployment = root.getDeployment();		
		Protocols protocols = deployment.getProtocols();
		if(protocols != null){
			for(Iterator it=protocols.getProtocol().iterator(); it.hasNext();){
				Protocol protocol = (Protocol)it.next();
				if(name.equals(protocol.getName()))
					return protocol;
			}
		}			
		return null;
	}
	
	public static List getJava400Protocols( EGLDeploymentRoot root )
	{
		Deployment deployment = root.getDeployment();		
		Protocols protocols = deployment.getProtocols();
		ArrayList protos = new ArrayList();
		if(protocols != null){
			Protocol protocol;
			for(Iterator it=protocols.getProtocol().iterator(); it.hasNext();){
				protocol = (Protocol)it.next();
				if(protocol instanceof Java400Protocol)
					protos.add(protocol);
			}
		}			
		return protos;
	}
	public static List getJava400J2cProtocols( EGLDeploymentRoot root )
	{
		Deployment deployment = root.getDeployment();		
		Protocols protocols = deployment.getProtocols();
		ArrayList protos = new ArrayList();
		if(protocols != null){
			Protocol protocol;
			for(Iterator it=protocols.getProtocol().iterator(); it.hasNext();){
				protocol = (Protocol)it.next();
				if(protocol instanceof Java400J2cProtocol)
					protos.add(protocol);
			}
		}			
		return protos;
	}
	public static Webservice getWebserviceByImpl(EGLDeploymentRoot root, String impl){
		Deployment deployment = root.getDeployment();		
		Webservices wss = deployment.getWebservices();
		if(wss != null){
			for(Iterator it=wss.getWebservice().iterator(); it.hasNext();){
				Webservice ws = (Webservice)it.next();
				if(impl.equals(ws.getImplementation()))
					return ws;
			}
		}
		return null;
	}
	public static Restservice getRestserviceByImpl(EGLDeploymentRoot root, String impl){
		Deployment deployment = root.getDeployment();		
		Restservices wss = deployment.getRestservices();
		if(wss != null){
			for(Iterator it=wss.getRestservice().iterator(); it.hasNext();){
				Restservice rs = (Restservice)it.next();
				if(impl.equals(rs.getImplementation()))
					return rs;
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
		else if( target instanceof DeploymentBuildDescriptor)
		{
			root.getDeployment().getTargetGroup().add( DeploymentPackage.eINSTANCE.getEGLDeploymentRoot_TargetBuildDescriptor(), target );
		}
	}
	
	public static void removeTarget( EGLDeploymentRoot root )
	{
		root.getDeployment().getTargetGroup().clear();
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
		
		if ( !found )
		{
			Parameter p = DeploymentFactory.eINSTANCE.createParameter();
			p.setName( name );
			p.setValue( value );
			parms.getParameter().add( p );
		}
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
}
