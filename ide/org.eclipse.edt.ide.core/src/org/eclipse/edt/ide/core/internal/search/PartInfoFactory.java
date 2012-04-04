/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.eglar.FileInEglar;
import org.eclipse.edt.ide.core.internal.model.index.impl.JarFileEntryDocument;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLProject;

import com.ibm.icu.util.StringTokenizer;

public class PartInfoFactory {
	
	private String[] fProjects;
	private PartInfo fLast;
	private char[] fBuffer;

	private static final String EGL= "egl"; //$NON-NLS-1$
	
	public PartInfoFactory() {
		super();
		fProjects= getProjectList();
		fLast= null;
		fBuffer= new char[512];
	}


	//path: the path where the eglar/ir file is located. For external eglar, it is the external file system location; for
	//non-external eglar, it is the location that contains the eglar
	//projectPath: the project that imports the eglar
	public PartInfo create(char[] packageName, char[] typeName, char[][] enclosingName, char partType, String path, IPath projectPath) {
		String pn= getPackageName(packageName);
		String tn= new String(typeName);
		PartInfo result= null;
		String eglarDefProject= getProject(path);	//the project in which the eglar is contained (not necessarily the project importing eglar)
		if (eglarDefProject != null) {
			result= createPartDeclarationInfo(pn, tn, enclosingName, partType, path, getIFilePartInfo(fLast), projectPath.toString());
		}
		else if(eglarDefProject == null){	//for external eglar file
			result = createExternalPartDeclarationInfo(pn, tn, enclosingName, partType, path, getIFilePartInfo(fLast), projectPath.toString());
		}
		if (result == null) {
			result= new UnresolvablePartInfo(pn, tn, enclosingName, path);
		} else {
			fLast= result;
		}
		return result;
	}
	
	private static IFilePartInfo getIFilePartInfo(PartInfo info) {
		if (info == null || info.getElementType() != PartInfo.IFILE_TYPE_INFO)
			return null;
		return (IFilePartInfo)info;
	}
	
	//example of path:
	//1. /Test1/Test.eglar|rec_without_package.eglxml
	//2. /Test1/Test.eglar|int1/rec1.eglxml
	//3. /Test1/EGLSource/pgm1.egl
	//4. /Test1/EGLSource/mypkg/pkg1/pgm1.egl
	//example of projecT:
	//1. /Test1
	private PartInfo createPartDeclarationInfo(String packageName, String typeName, char[][] enclosingName, char partType, String path, IFilePartInfo last, String project) {
		String src = null;
		String file= null;
		String extension= null; 
		String container = null;
		
		int index = -1;
		if(path.startsWith("/")){
			index = path.indexOf("/");
			if(index == -1){
				return null;
			}
		}
		index = path.indexOf("/", index + 1);
		if(index == -1){
			return null;
		}
		container = path.substring(0, index);
		if(container.startsWith("/")){
			container = container.substring(1);
		}
		String rest = path.substring(index); // the first slashes.
		//example of rest:
		//1. /Test.eglar|rec_without_package.eglxml
		//2. /Test.eglar|int1/rec1.eglxml
		//3. /EGLSource/pgm1.egl
		//4. /EGLSource/mypkg/pkg1/pgm1.egl	
		
		index = rest.indexOf(FileInEglar.EGLAR_SEPARATOR);
		if(index == -1){	//for source part			
			index = rest.lastIndexOf(PartInfo.SEPARATOR);
			if (index == -1)
				return null;
			//middle: the path without project part and file part
			String middle= rest.substring(0, index + 1);	//middle includes the last '/'
			rest = rest.substring(index + 1);
			index= rest.lastIndexOf(PartInfo.EXTENSION_SEPARATOR);
			
			if (index != -1) {
				file= rest.substring(0, index);
				extension= rest.substring(index + 1);
			} else {
				return null;
			}
			
			// If the package name does not match the directory structure, there is no way to determine what part of the path is
			// the source directory and what part is the package name. So, treat the entire path as the source directory and the
			// package as default
		
			if (!packageMatchesDirectoryStructure(middle, packageName)) {
				packageName = "";
				src = middle.substring(1, middle.length());
			}
			else {			
				int ml= middle.length() - 1;	//eliminates the length of last '/'
				int pl= packageName.length();
				// if we have a source or package then we have to substract the leading '/'
				if (ml > 0 && ml - 1 > pl) {
					 // If we have a package then we have to substract the '/' between src and package
					src= middle.substring(1, ml - pl - (pl > 0 ? 1 : 0));
				}
			}

			if (last != null) {
				if (src != null && src.equals(last.getFolder()))
					src= last.getFolder();
			}
			if (typeName.equals(file)) {
				file= typeName;
			} else {
				file= createString(file);
			}
			if (EGL.equals(extension))
				extension= EGL;
			else
				extension= createString(extension);
		}
		else{	//for binary part
			src = rest.substring(1, index);	//e.g. Test.eglar
			rest = rest.substring(index + 1);

			if(packageName != null && packageName.length() > 0){	//not in default package
				int ind = rest.lastIndexOf(PartInfo.SEPARATOR);
				if(ind == -1){
					return null;
				}
				rest = rest.substring(ind + 1);	//substract the package name
			}			
			index = rest.lastIndexOf(PartInfo.EXTENSION_SEPARATOR);
			if(index == -1)
				return null;
			file= rest.substring(0, index);
			extension= rest.substring(index + 1);
		}
		
		if(project.startsWith(File.separator) || project.startsWith("/")){
			project = project.substring(1);
		}
		return new PartDeclarationInfo(packageName, typeName, enclosingName, project, container, src, file, extension, partType, false);
	}
	 
	//example of path:
	//1. /Test.eglar|inter1
	//2. /Test.eglar|mypkg/inter1/
	//3. /EGLSource/dojo/widgets/
	//example of pkgName:
	//1. (empty string)
	//2. mypkg.inter1
	private boolean packageMatchesDirectoryStructure(String path, String pkgName) {
		if (pkgName == null || pkgName.length() == 0) {
			return true;
		}
		
		//parse through the package name and store the package name elements in a list is reverse order	
		List list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(pkgName, ".", false);
		while (tokenizer.hasMoreTokens()) {
			String pkgElem = tokenizer.nextToken() + PartInfo.SEPARATOR;
			list.add(0, pkgElem);
		}
 
		//Check the package name elements against the directory structure
		Iterator i = list.iterator();
		while (i.hasNext()) {
			String pkgElem = (String) i.next();
			if (path.endsWith(pkgElem)) {
				path = path.substring(0, path.length() - pkgElem.length());
			}
			else {
				return false;
			}
		}
		return true;
	}	
	
	//example of path:
	//1. c:\temp\Test.eglar|rec_without_package.eglxml
	//2. c:\temp\Test.eglar|int1/rec1.eglxml
	private PartInfo createExternalPartDeclarationInfo(String packageName, String typeName, char[][] enclosingName, char partType, String path, IFilePartInfo last, String project) {
		String rest = path;
		if ( rest.startsWith( FileInEglar.EGLAR_PREFIX ) ) {
			rest = rest.substring( FileInEglar.EGLAR_PREFIX.length() );
		}
		int index = rest.lastIndexOf(FileInEglar.EGLAR_SEPARATOR);
		if (index == -1)
			return null;
		
		String src = rest.substring(0, index);
		rest = rest.substring(index + 1);
		
		String file= null;
		String extension= null;
		if(packageName != null && packageName.length() > 0){	//not in default package
			int ind = rest.lastIndexOf(PartInfo.SEPARATOR);
			if(ind == -1){
				return null;
			}
			rest = rest.substring(ind + 1);	//substract the package name
		}
		index = rest.lastIndexOf(PartInfo.EXTENSION_SEPARATOR);
		if(index == -1)
			return null;
		file= rest.substring(0, index);
		extension= rest.substring(index + 1);
					
		index = src.lastIndexOf(File.separator);
		String fileFolder = null;
		if(index != -1){
			fileFolder = src.substring(0, index);
			src = src.substring(index + 1);
		}
		else{
			return null;
		}
				

		if(project.startsWith(File.separator) || project.startsWith("/")){
			project = project.substring(1);
		}
		return new PartDeclarationInfo(packageName, typeName, enclosingName, project, fileFolder, src, file, extension, partType, true);
	}
	
	private String getPackageName(char[] packageName) {
		if (fLast == null)
			return new String(packageName);
		char[] lastPackageName= fLast.getPackageName().toCharArray();
		if (lastPackageName.equals(packageName))
			return lastPackageName.toString();
		return new String(packageName);
	}
	
	private String getProject(String path) {
		for (int i= 0; i < fProjects.length; i++) {
			String project= fProjects[i];
			if (path.startsWith(project, 1))
				return project;
		}
		return null;
	}
	
	private String createString(String s) {
		if (s == null)
			return null;
		int length= s.length();
		if (length > fBuffer.length)
			fBuffer= new char[length];
		s.getChars(0, length, fBuffer, 0);
		return new String(fBuffer, 0, length);
	}
	
	private static String[] getProjectList() {
		IEGLModel model= EGLCore.create(ResourcesPlugin.getWorkspace().getRoot());
		String[] result;
		try {
			IEGLProject[] projects= model.getEGLProjects();
			result= new String[projects.length];
			for (int i= 0; i < projects.length; i++) {
				result[i]= projects[i].getElementName();
			}
		} catch (EGLModelException e) {
			result= new String[0];
		}
		// We have to sort the list of project names to make sure that we cut of the longest
		// project from the path, if two projects with the same prefix exist. For example
		// com.ibm.etools.egl.internal.model.ui and com.ibm.etools.egl.internal.model.ui.tests.
		Arrays.sort(result, new Comparator() {
			public int compare(Object o1, Object o2) {
				int l1= ((String)o1).length();
				int l2= ((String)o2).length();
				if (l1 < l2)
					return 1;
				if (l2 < l1)
					return -1; 
				return  0;
			}
			public boolean equals(Object obj) {
				return super.equals(obj);
			}
		});
		return result;
	}		
}
