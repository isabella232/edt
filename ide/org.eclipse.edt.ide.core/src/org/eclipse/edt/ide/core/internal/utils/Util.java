/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.eglar.FileInEglar;
import org.eclipse.edt.ide.core.internal.model.EGLModel;
import org.eclipse.edt.ide.core.internal.model.index.impl.JarFileEntryDocument;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfoRequestor;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchResultCollector;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.ISearchPattern;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.core.utils.EGLProjectFileUtility;
import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * @author svihovec
 */
public class Util {

	public static IPath stringArrayToPath(String[] stringArray){
		IPath path = new Path(""); //$NON-NLS-1$
		for (int i = 0; i < stringArray.length; i++){
			path = path.append(stringArray[i]);
		}
		return path;
	}
	
	public static String[] pathToStringArray(IPath path){
		String[] result = new String[path.segmentCount()];
		for (int i = 0; i < result.length; i++) {
			result[i] = path.segment(i);
		}
		return result;
	}
	
	// TODO Can we intern file names?  Should we use Sun.Intern and don't normalize?  What about files with the same name but different case on Linux?
	public static String getFilePartName(IFile file){
		return InternUtil.intern(file.getProjectRelativePath().toString());
	}
	
	public static String getCaseSensitiveFilePartName(IFile file){
		return InternUtil.internCaseSensitive(file.getProjectRelativePath().toString());
	}
	
	public static String getFileContents(IFile file) throws IOException, CoreException{
		InputStream fileContents = new BufferedInputStream(file.getContents(true));
		byte[] bytes = new byte[fileContents.available()];
		
		try{
			fileContents.read(bytes);
		}finally{
			fileContents.close();
		}
		
		return new String(bytes, file.getCharset());
	}
	
	/**
	 * Returns a <code>List</code> containing the projects that make up the project path of the passed project
	 * 
	 * @param project The project to build the project path for
	 * @return The project path list
	 */
	public static List getEGLProjectPath(IProject project){
		ArrayList projectList = new ArrayList();
		initializeEGLPathHelper(projectList, new HashSet(), project, project, false, true);
		return projectList;
	}

	/**
	 * Returns a <code>List</code> containing the projects and file paths of Eglars (String) that make up the project path of the passed project
	 * 
	 * @param project The project to build the project path for
	 * @param includeEGLARs Returned list should include String objects representing referenced EGLARs
	 * @param searchThroughProjects Retuned list should contain all projects in the project tree, including projects not directly reference by the given project
	 * @return The project path list
	 */
	public static List getEGLProjectPath(IProject project, boolean includeEGLARs, boolean searchThroughProjects){
		ArrayList projectList = new ArrayList();
		initializeEGLPathHelper(projectList, new HashSet(), project, project, includeEGLARs, searchThroughProjects);
		return projectList;
	}
	
	/**
     * Perform a depth first search on the EGL Path, adding all source directories from required projects and their exported projects.
     * 
     * If a project has already been visited once during this search, it will not be visited again.  This allows us to avoid problems
     * with projects that require each other and export each other (infinite cycle).  This also means that required project chains will
     * only be added to the list once.  For example, given a chain of required projects M<->N<->O, where -> means Requires and <- means exports,
     * and projects A and B, where A->M and A->B and B<->M, we would normally end up with an EGL Path of A,M,N,O,B,M,N,O.  The following algorithm
     * will not traverse the M<->N<->O chain twice, because if we have managed to get to project B in the EGLPath, we have already searched M,N,O and
     * have not found any of the required parts.  This algorithm will produce an EGLPath of A,M,N,O,B.
     * 
     * If the request is to include EGLARs, then Binary Projects that do not include source will NOT be returned. However,
     * if EGLARs are not requested, this means that the caller is only interested in the list of Projects involved. Therefore,
     * the Binary Project is forcibly added to the returned list.
     */
    private static void initializeEGLPathHelper(List entries, Set visitedProjects, IProject project, IProject requestingProject, boolean includeEglars, boolean searchThroughProjects) {
        visitedProjects.add(project);
        IEGLProject eglProject = EGLCore.create(project);
        try {
        	if(((IProject)eglProject.getResource()).isOpen()){
	            IEGLPathEntry[] resolvedEGLPath = eglProject.getResolvedEGLPath(true);
	            boolean projectAdded = false;
	            for(int i = 0; i < resolvedEGLPath.length; i++) {
	            	// TODO a required projects source folder may not be exported
	                if(resolvedEGLPath[i].getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
	                    entries.add(eglProject);
	                    projectAdded = true;
	                } else if (resolvedEGLPath[i].getEntryKind() == IEGLPathEntry.CPE_LIBRARY){
	                	if (includeEglars) {
		                	if((project == requestingProject) || (resolvedEGLPath[i].isExported())) {
		                		entries.add(getAbsolutePathStringForEGLAR(resolvedEGLPath[i].getPath(), project));
		                	}
	                	}
	                	else {
	                		//If then entry is for the EGLAR and the project is binary, force the project into the list
	                		//if our caller doesnt care about EGLARs and only cares about Projects
	                		if (!projectAdded && isPathEntryForBinaryProjectEglar(project, resolvedEGLPath[i].getPath())) {
	                			entries.add(eglProject);
	                		}
	                	}
	                }else if(resolvedEGLPath[i].getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
	                    if((project == requestingProject) || (resolvedEGLPath[i].isExported() || searchThroughProjects)) {
	                        IProject requiredProject = ResourcesPlugin.getWorkspace().getRoot().getProject(resolvedEGLPath[i].getPath().toString());
	                        if(!visitedProjects.contains(requiredProject)) {
	                            initializeEGLPathHelper(entries, visitedProjects, requiredProject, requestingProject, includeEglars, searchThroughProjects);
	                        }
	                    }
	                }
	            }
        	}
        } catch (EGLModelException e) {
			throw new BuildException(e);
        }
    }
    
    private static boolean isPathEntryForBinaryProjectEglar(IProject proj, IPath path) {
    	if (!new EGLProjectFileUtility().isBinaryProject(proj)) {
    		return false;
    	}
    	
 		Object obj = EGLModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), path, true);
 		if (obj instanceof IFile) {
 			IFile file = (IFile) obj;
 			if (file.getProject() == proj && (proj.getName()+ ".eglar").equalsIgnoreCase(file.getName())) {
 				return true;
 			}
 		}
 		return false;
    }
	
    private static String getAbsolutePathStringForEGLAR(IPath path, IProject proj) {
    	return AbsolutePathUtility.getAbsolutePathString(path);
    }
	
	public static PartInfo getUsePart(IEGLProject project, String partDeclPackage, String partTypeName)
	{
		PartInfo[] parts = null;
		List partslist = new ArrayList();
		try
		{
			IEGLProject[] projects = new IEGLProject[] {project};
			IEGLSearchScope searchScope = SearchEngine.createEGLSearchScope(projects , true);
			PartInfoRequestor searchResult = new PartInfoRequestor(partslist);
			new SearchEngine().searchAllPartNames(
				ResourcesPlugin.getWorkspace(), 
				partDeclPackage.toCharArray(), 
				partTypeName.toCharArray(), 
				IIndexConstants.EXACT_MATCH, 
				IEGLSearchConstants.CASE_INSENSITIVE, 
				IEGLSearchConstants.PART, 
				searchScope, 
				searchResult, 
				IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, 
				null);
			parts = (PartInfo[]) partslist.toArray(new PartInfo[partslist.size()]);
		}
		catch (EGLModelException e)
		{
			e.printStackTrace();
		}
		if(parts != null && parts.length > 0)
			return parts[0];
		
		return null;
	}
	
	//copy from EGLDDRootHelper
	public static IFile findPartFile(String fullyqualifiedPartName, IEGLProject eglProj){
		try{
			PartDeclarationInfo part = find1stPartInfoInEGLProject(fullyqualifiedPartName, eglProj, IEGLSearchConstants.PART, null, true);
			if(part != null){
				if(!part.isExternal()){
					return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(part.getPath()));
				} else{
					return null;
				}
			}				
		}catch(CoreException e){
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public static String findPartFilePath(String fullyqualifiedPartName, IEGLProject eglProj){
		try{
			//for Form in FormGroup, only retrieve the FormGroup part path, 
			//which is the ir file path
			int index = fullyqualifiedPartName.indexOf(":");
			if(index != -1){
				fullyqualifiedPartName = fullyqualifiedPartName.substring(0, index);
			}
			PartDeclarationInfo part = find1stPartInfoInEGLProject(fullyqualifiedPartName, eglProj, IEGLSearchConstants.PART, null, true);
			if(part != null){
				return part.getPath();
			}				
		}catch(CoreException e){
			e.printStackTrace();
			return null;
		}
		return null;
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
	
	//get the nested Record/Dataitem/ inside a Record field
	public static PartInfo getRecordFieldNestedPart(IEGLProject project, String partDeclPackage, String partTypeName)
	{
		PartInfo[] parts = null;
		List partslist = new ArrayList();
		try
		{
			IEGLProject[] projects = new IEGLProject[] {project};
			IEGLSearchScope searchScope = SearchEngine.createEGLSearchScope(projects , true);
			PartInfoRequestor searchResult = new PartInfoRequestor(partslist);
			new SearchEngine().searchAllPartNames(
				ResourcesPlugin.getWorkspace(), 
				partDeclPackage.toCharArray(), 
				partTypeName.toCharArray(), 
				IIndexConstants.EXACT_MATCH, 
				IEGLSearchConstants.CASE_INSENSITIVE, 
				IEGLSearchConstants.RECORD | IEGLSearchConstants.ITEM, 
				searchScope, 
				searchResult, 
				IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, 
				null);
			parts = (PartInfo[]) partslist.toArray(new PartInfo[partslist.size()]);
		}
		catch (EGLModelException e)
		{
			e.printStackTrace();
		}
		if(parts != null && parts.length > 0)
			return parts[0];
		
		return null;
	}
	
	//get the part for data declaration
	//the part could be: Record, Dataitem, Delegate, Handler, Service, ExternalType, Interface
	public static PartInfo getDataDeclarationPart(IEGLProject project, String partDeclPackage, String partTypeName)
	{
		PartInfo[] parts = null;
		List partslist = new ArrayList();
		try
		{
			IEGLProject[] projects = new IEGLProject[] {project};
			IEGLSearchScope searchScope = SearchEngine.createEGLSearchScope(projects , true);
			PartInfoRequestor searchResult = new PartInfoRequestor(partslist);
			new SearchEngine().searchAllPartNames(
				ResourcesPlugin.getWorkspace(), 
				partDeclPackage.toCharArray(), 
				partTypeName.toCharArray(), 
				IIndexConstants.EXACT_MATCH, 
				IEGLSearchConstants.CASE_INSENSITIVE, 
				IEGLSearchConstants.RECORD | IEGLSearchConstants.ITEM | IEGLSearchConstants.DELEGATE
				| IEGLSearchConstants.HANDLER | IEGLSearchConstants.SERVICE | IEGLSearchConstants.EXTERNALTYPE | IEGLSearchConstants.INTERFACE, 
				searchScope, 
				searchResult, 
				IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, 
				null);
			parts = (PartInfo[]) partslist.toArray(new PartInfo[partslist.size()]);
		}
		catch (EGLModelException e)
		{
			e.printStackTrace();
		}
		if(parts != null && parts.length > 0)
			return parts[0];
		
		return null;
	}
	
	public static IEGLSearchScope createSearchScope(IResource resource) {
		IEGLElement[] elements = new IEGLElement[1];
		elements[0] = EGLCore.create(resource);
		return SearchEngine.createEGLSearchScope(elements);
	}
	
	//example:
	//1. /Test1/EGLSource/mypkg/interfaces/t1.egl -> /Test1/EGLSource/mypkg/interfaces
	//2. /Test1/EGLSource/t1.egl -> /Test1/EGLSource
	//3. /Test1/Test.eglar|t1.ir -> /Test1/Test.eglar
	//4. c:/example/temp/Test.eglar|mypkg/t1.ir -> c:/example/temp/Test.eglar
	public static String getPackageFragmentRootPath(String pathString){
		int index = pathString.indexOf(FileInEglar.EGLAR_SEPARATOR);
		if (index == -1){
			int ind = pathString.lastIndexOf("/");
			if(ind != -1){
				return pathString.substring(0, ind);
			}
		}
		
		String jarPath = pathString.substring(0, index);
		return jarPath;
	}
	
	public static ResourceAndTLFMap[] getSourceFileAndTLFsMaps(IEGLProject eglProj)
	{
		final HashMap<IResource, ResourceAndTLFMap> resMap = new HashMap<IResource, ResourceAndTLFMap>();
		
		IEGLSearchResultCollector collector = new IEGLSearchResultCollector(){
			private HashMap<IResource, String> fileCache = new HashMap<IResource, String>();
			
			public void aboutToStart() { /* do nothing */ }
			public void done() { /* do nothing */ }

			public void accept(IResource resource, int start, int end,
					IEGLElement enclosingElement, int accuracy)
					throws CoreException {
				String fileContents = fileCache.get(resource);
				if(fileContents == null) {
					if(resource instanceof IFile) {
						fileContents = getFileContent(((IFile)resource).getContents());
						fileCache.put(resource, fileContents);
					}
				}
				int lastCharIndex = -1;
				if(fileContents != null) {
					lastCharIndex = fileContents.length() - 1;
				}
				if(start > lastCharIndex || end > lastCharIndex) {
					System.out.println("Wrong position!");
					return;
				}
				String irFileName = fileContents.substring(start, end);
				IPath resPath = resource.getFullPath();
				//Get the package name
				resPath = resPath.removeFirstSegments(2).removeLastSegments(1);
				resPath = resPath.append(irFileName.toLowerCase());//The IR files always in lower case.
				ResourceAndTLFMap map = resMap.get(resource);
				if(map != null) {
					map.addPath(resPath);
				} else {
					map = new ResourceAndTLFMap(resource, resPath);
					resMap.put(resource, map);
				}
			}

			public void accept(IEGLElement element, int start, int end,
					IResource resource, int accuracy) throws CoreException {
				/* do nothing */
			}

			public IProgressMonitor getProgressMonitor() {
				return null;
			}
			
			private String getFileContent(InputStream inputStream) {
				InputStreamReader reader = new InputStreamReader(new BufferedInputStream(inputStream));
				StringBuffer s = new StringBuffer();
				try {
					char cbuf[] = new char[4096];
					int length = 0;
					while ((length = reader.read(cbuf)) >= 0) {
						s.append(cbuf, 0, length);
					}
				} catch (IOException e) {
				}
				return s.toString();
			}
			
		};
		
		try
		{
			IEGLSearchScope searchScope = SearchEngine.createEGLSearchScope(new IEGLProject[]{eglProj} , true);
			ISearchPattern pattern = SearchEngine.createSearchPattern("*", IEGLSearchConstants.FUNCTION_PART, IEGLSearchConstants.DECLARATIONS, false);
			new SearchEngine().search(ResourcesPlugin.getWorkspace(), pattern, searchScope, collector);
		}
		catch (EGLModelException e)
		{
			e.printStackTrace();
		}
		
		return resMap.values().toArray(new ResourceAndTLFMap[resMap.values().size()]);
	}
}
