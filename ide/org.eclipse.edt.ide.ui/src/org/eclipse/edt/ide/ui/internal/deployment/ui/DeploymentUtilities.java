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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.internal.model.BinaryPart;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.internal.search.AllPartsCache;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.Signature;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.Strings;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNode;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeFile;
import org.eclipse.edt.ide.ui.internal.deployment.ui.project.artifacts.TreeNodeFolder;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class DeploymentUtilities {

	private static final String RUIHANDLER = NameUtile.getAsName( "RUIHandler" ); //$NON-NLS-1$
	private DeploymentUtilities() {
	}
	private static DeploymentUtilities instance = new DeploymentUtilities();
	
	public static Map getAllRUIHandlersInProject( IEGLProject project) throws EGLModelException{
		return getAllRUIHandlersInProject(project, false);
	}
	
	public static Map getAllRUIHandlersInProject( IEGLProject project , boolean searchReferencedProjects) throws EGLModelException
	{
		List handlerList = new ArrayList();
		final IEGLSearchScope projSearchScope = SearchEngine.createEGLSearchScope( new IEGLElement[]{ project }, searchReferencedProjects );
		AllPartsCache.getParts( projSearchScope, IEGLSearchConstants.HANDLER, new NullProgressMonitor(), handlerList );
		
		Map ruiMap = new HashMap();
		for ( Iterator it = handlerList.iterator(); it.hasNext(); )
		{
			PartInfo partinfo = (PartInfo)it.next();
			IPart part = partinfo.resolvePart( projSearchScope );
			if(part instanceof SourcePart){
			SourcePart sourcePart = (SourcePart)partinfo.resolvePart( projSearchScope );
			if ( sourcePart.isHandler() && sourcePart.getSubTypeSignature() != null && NameUtile.equals( RUIHANDLER, NameUtile.getAsName( Signature.toString( sourcePart.getSubTypeSignature() ) ) ) )
			{
				String impl = partinfo.getFullyQualifiedName();
				String htmlDefault = impl;
				
				int idx = htmlDefault.lastIndexOf( '.' );
				if ( idx != -1 )
				{
					htmlDefault = htmlDefault.substring( idx + 1 );
				}
				
				ruiMap.put( impl, htmlDefault );
			}
		}
			else if(part instanceof BinaryPart){
				BinaryPart binaryPart = (BinaryPart)partinfo.resolvePart( projSearchScope );
				if ( binaryPart.isHandler() && binaryPart.getSubTypeSignature() != null && NameUtile.equals( RUIHANDLER, NameUtile.getAsName( Signature.toString( binaryPart.getSubTypeSignature() ) ) ) )
				{
					String impl = partinfo.getFullyQualifiedName();
					String htmlDefault = impl;
					
					int idx = htmlDefault.lastIndexOf( '.' );
					if ( idx != -1 )
					{
						htmlDefault = htmlDefault.substring( idx + 1 );
					}
					
					ruiMap.put( impl, htmlDefault );
				}
			}
		}
		
		return ruiMap;
	}
	
	public static final void buildResourceTree(final TreeNode seed, final List results) throws CoreException {
		/**
		 * create tree node for project. This will be the tree viewers
		 * root node
		 */
		final IResource seedResource = seed.getResource();
		try {
			seedResource.accept(new IResourceVisitor() {

				public boolean visit(IResource resource) throws CoreException {
					if ( !resource.equals( seedResource ) ) {
						switch (resource.getType()) {
						case IResource.FILE:
							if (validFile(resource)) {
								TreeNode fileNode = new TreeNodeFile(seed, resource);
								results.add(fileNode);
							}
							return false; // pretty much a no-op as a file has no children anyway
						case IResource.FOLDER:
							if (validFolder(resource)) {
								TreeNode folderNode = new TreeNodeFolder(seed, resource);
								results.add(folderNode);
							}
							/**
							 * don't want to go into folders so return false
							 */
							return false;
						}
						return false;
					}
					/**
					 * if it is the seed then return <code>true</code> as we want to
					 * traverse into this IResource
					 */
					return true;
				}

				/**
				 * We have the opportunity here to veto a folder. Currently all folders are valid however, you
				 * just never know what the future will bring
				 * 
				 * @param resource
				 * @return
				 */
				private boolean validFolder(IResource resource) {
						return true;
				}

				private boolean validFile(IResource resource) {
					String name = resource.getName();
					if (! name.startsWith(".")) {//$NON-NLS-1$
						return true;
					} else {
						return false;
					}
				}
				
			}
					, IResource.DEPTH_ONE, false);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	public static final void findFolder(final IProject project, final List<IResource> results, final String[] targetFolders) throws CoreException {
		if ( targetFolders == null ) {
			return;
		}
		for ( int i = 0; i < targetFolders.length; i ++ ) {
			findFolder( project, results, targetFolders[i] );
		}
	}
	
	
	public static final void findFolder(final IProject project, final List<IResource> results, final String targetFolder) throws CoreException {
		if (targetFolder == null || targetFolder.length() == 0) {
			return;
		}
		try {
			project.accept(new IResourceVisitor() {
				public boolean visit(IResource resource) throws CoreException {
					switch (resource.getType()) {
					case IResource.FILE:
						return false;
					case IResource.FOLDER:
						if (resource.getName().toLowerCase().equals(targetFolder.toLowerCase())) {
							results.add(resource);
						}
						return false;
					case IResource.PROJECT:
						/**
						 * return <code>true</code> only on the project as it is only the project's
						 * immediate children that we want to visit.
						 */
						return true;
					}
					return false;
				}
			}
			, IResource.DEPTH_ONE, false);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	public static void getJavaSourceFolders(IProject sourceProject, List<IResource> resources )
		throws CoreException, JavaModelException {
		
		//for the source project get all of it's Java source folders
		//and include java folders in this project classpath
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IJavaProject javaProject = JavaCore.create(sourceProject);
		if( PlatformUI.isWorkbenchRunning() && javaProject.exists() && javaProject.isOpen() ||
				!PlatformUI.isWorkbenchRunning() && javaProject.exists())
		{
			IClasspathEntry[] entries = javaProject.getResolvedClasspath( true );
			if( entries != null && entries.length > 0 )
			{
				for( int idx = entries.length-1; idx >= 0; idx-- )
				{
					if ( entries[idx].getEntryKind() == IClasspathEntry.CPE_SOURCE )
					{
						IResource element = root.findMember(entries[idx].getPath());
						
						if(element != null && element.exists()){
							resources.add(element);
						}
					}
				}
			}
		}
	}
	
	public static void displayErrorDialog(String title, String message) {
    	ErrorDialog.openError(getShell(), title, message, 
    			new Status(IStatus.ERROR, EDTUIPlugin.PLUGIN_ID ,IStatus.ERROR, message, null));
    }
	
	/**
	 * Try desperately to return a valid Shell (pulled out of thin air...). This
	 * is probably totally bogus.
	 * 
	 * @return Shell
	 */
	public static final Shell getShell() {

		Shell shell = null;

		Display display = getDisplay();
		if (display != null)
			shell = display.getActiveShell();

		if (shell == null) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (workbenchWindow != null)
				shell = workbenchWindow.getShell();
		}

		return shell;
	}
	
	/**
	 * Try desperately to return a valid Display. Return null if we fail.
	 * 
	 * @return Display
	 */
	public static final Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}
	
	public static List<PartDeclarationInfo> getSystemServices()
	{
		List<PartDeclarationInfo> services = new ArrayList<PartDeclarationInfo>();
		//TODO to be fixed later
		//SystemEnvironmentManager.findSystemEnvironment(project, notifier)
//		IPartBinding binding = SystemEnvironment.getInstance().getPartBinding(InternUtil.intern(new String[]{"egl", "ui", "gateway"}), InternUtil.intern("UIGatewayService"));
//		if( binding != null ){
//			services.add(instance.new SystemPartInfo(binding.getPackageName(), binding.getCaseSensitiveName(), Part.NAMETYPE));
//		}
		return services;
	}
	
	
	private class SystemPartInfo extends PartDeclarationInfo
	{

		public SystemPartInfo(String[] pkg, String name, char partType) {
			super(Strings.concatenate(pkg, "."), name, new char[0][0], "SYSTEM", "SYSTEM", "SYSTEM", "", partType);

		}
		
	}
	
	private static String convert(String[] pkg)
	{
		StringBuilder buff = new StringBuilder();
		if( pkg != null )
		{
			for ( int i = 0; i < pkg.length; i++ ) 
			{
				buff.append(pkg[i]);
			}
		}
		return buff.toString();
	}
}
