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
package org.eclipse.edt.ide.rui.utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.gen.deployment.javascript.Constants;
import org.eclipse.edt.ide.core.model.IEGLProject;

public abstract class FileLocator {
	//VE deployment files
	public static final List<String> RUI_DEVELOPMENT_JAVASCRIPT_FILES = new ArrayList<String>();
	static{
		RUI_DEVELOPMENT_JAVASCRIPT_FILES.add("egl_development.js");  //$NON-NLS-1$
	};
	
	//RUI runtime property files
	public static final List<String> RUI_RUNTIME_PROPERTIES_FILES = new ArrayList<String>();
	static{
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-ar.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-cs.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-de.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-en_US.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-es.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-fr.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-hu.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-it.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-ja.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-ko.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-pl.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-pt_BR.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-ru.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-zh_HK.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-zh_TW.js");
		RUI_RUNTIME_PROPERTIES_FILES.add("egl/messages/RuiMessages-zh.js");
	}

	public class FileComparator implements Comparator{

		private Map fileMap = new HashMap();
		
		private int getLocation(String fileName){
			if(fileMap.containsKey(fileName)){
				return ((Integer)fileMap.get(fileName)).intValue();
			}
			
			int i = 0;
			for (Iterator iterator = eglProjectPath.iterator(); iterator.hasNext(); i++) {
				IEGLProject eglProject = (IEGLProject) iterator.next();
				
				EGLResource file = doFindResource(fileName, eglProject);	
				if(file != null){
					fileMap.put(fileName, new Integer(i));
					return i;
				}
			}			
			return -1;
		}
		
		public int compare(Object arg0, Object arg1){
			int file1Location = getLocation((String)arg0);
			int file2Location = getLocation((String)arg1);
			
			if(file1Location < file2Location){
				return -1;
			}else if(file1Location == file2Location){
				return 0;
			}else{
				return 1;
			}
		}
		
	}
	
	protected static final String WEB_CONTENT = "WebContent";
	public static Map RUI_JAVASCRIPT_FILES = getRUIJavaScriptFiles();
	private List eglProjectPath;
	protected IProject project;
	private String[] resourceLocations;
	private String[] resourceLocationsInEglar;
	
	public FileLocator(IProject project) throws CoreException{
		this.project = project;
		this.eglProjectPath = org.eclipse.edt.ide.core.internal.utils.Util.getEGLProjectPath(project);
		resourceLocations = initResourceLocations(project);
		resourceLocationsInEglar = initResourceLocationsInEglar(project);
	}
	
	protected FileLocator(){
	}
	
	public EGLResource findResource(String name){		
		EGLResource result = null;
		for (Iterator iter1 = eglProjectPath.iterator(); iter1.hasNext();) {
			IEGLProject eglProject = (IEGLProject)iter1.next();
			result = doFindResource(name, eglProject);			
			if(result != null){
				break;
			}
		}
		
		if(result == null && RUI_JAVASCRIPT_FILES.containsKey(name)) {
			// Look in the EGL RUI Runtime for files - returns null otherwise
			result = new EGLFileResource( (File)RUI_JAVASCRIPT_FILES.get(name) );
		}

		return result;
	}

	protected EGLResource doFindResource(String name, IEGLProject eglProject) {
		String[] resourceLocations = getResourceLocations();
		for (int i = 0; i < resourceLocations.length; i++) {
			String location = resourceLocations[i];
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(eglProject.getPath().append(location).append(name));
			if(file.exists()){
				return new EGLFileResource( file.getLocation().toFile() );
			}					
		}
//TODO EDT eglar
//		try {
//			IEGLPathEntry[] entries = eglProject.getRawEGLPath();
//			for ( int j = 0; j < entries.length; j ++ ) {
//				IEGLPathEntry entry = entries[j];
//				if ( entry.getEntryKind() == IEGLPathEntry.CPE_LIBRARY && "eglar".equalsIgnoreCase(entry.getPath().getFileExtension()) ) {
//					for (int i = 0; i < resourceLocationsInEglar.length; i++) {
//						String location = resourceLocationsInEglar[i];
//						EglarFile eglar = EglarFileCache.instance.getEglarFile(EGLProjectFileUtility.getEglarAbsolutePath(entry.getPath(), eglProject.getProject()));
//						EglarManifest manifest = eglar.getManifest();
//						if ( manifest == null ) {
//							continue;
//						}
//						location = location + ( location.length() > 1 ? IPath.SEPARATOR : "" );
//						ZipEntry zipentry = eglar.getEntry( location + name );
//						if(zipentry == null) {
//							zipentry = eglar.getEntry( "EGLSource/" +  name );
//						}
//						if(zipentry != null){
//							return new EGLARResource( eglar, zipentry );
//						}					
//					}
//				}
//			}
//		} catch ( Exception e ) {
//			//e.printStackTrace();
//			//do nothing
//		}
		return null;
	}
	
	private static Map<String, File> getRUIJavaScriptFiles(){
		
		byte[] bytes = null;
		File allInOneFile = null;
		FileOutputStream os = null;
		try {
			allInOneFile = File.createTempFile("allInOneFile", "js");
			allInOneFile.deleteOnExit();
			os = new FileOutputStream(allInOneFile);
		} catch (IOException e) {
		}
		
		Map<String, File> result = new HashMap<String, File>();
		for (Iterator<String> iterator = RUI_DEVELOPMENT_JAVASCRIPT_FILES.iterator(); iterator.hasNext();) {
			String fileName = (String) iterator.next();
			File file = doGetRUIJavaScriptFile(fileName);
			if(file != null){
				result.put(fileName, file);
			}
		}
		
		for (Iterator<String> iterator = Constants.RUI_RUNTIME_JAVASCRIPT_FILES.iterator(); iterator.hasNext();) {
			String fileName = (String) iterator.next();			
			File file = doGetRUIJavaScriptFile(fileName);
			if(file != null){
				result.put(fileName, file);

				//append content to allInOneFile
				if(os != null && !(fileName.equals(Constants.RUI_RUNTIME_BOOTSTRAP_FILE)) && !(fileName.equals(Constants.RUI_RUNTIME_LOADER_FILE))){
					try {
						DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
						bytes = new byte[is.available()];
						is.readFully(bytes);
						is.close();
						os.write(bytes);
						os.flush();
					} catch (FileNotFoundException e) {
					} catch (IOException e) {
					}
				}
			}
		}

		//if there's an allInOne file in runtime directory, ignore the temp file
		if(!result.containsKey(Constants.RUI_RUNTIME_JAVASCRIPT_ALL_IN_ONE_FILE)){
			result.put(Constants.RUI_RUNTIME_JAVASCRIPT_ALL_IN_ONE_FILE, allInOneFile);
		}
		
		for (Iterator<String> iterator = RUI_RUNTIME_PROPERTIES_FILES.iterator(); iterator.hasNext();) {
			String fileName = (String) iterator.next();
			File file = doGetRUIJavaScriptFile(fileName);
			if(file != null){
				result.put(fileName, file);
			}
		}
		return result;
	}
	
	private static File doGetRUIJavaScriptFile(String fileName){
		File result = null;
		IPath path = new Path("runtime").append(fileName);		
		URL resource = Platform.getBundle("org.eclipse.edt.runtime.javascript").getEntry(path.toOSString());
		if(resource != null){
			try{
				File file = new File(org.eclipse.core.runtime.FileLocator.resolve(resource).getFile());
				if(file.exists()){
					result = file;
				}
			}catch(IOException e){
				// do nothing
			}
		}
		return result;
	}
	
	protected abstract String[] initResourceLocations(IProject project) throws CoreException;
	protected abstract String[] initResourceLocationsInEglar(IProject project) throws CoreException;
	protected String[] getResourceLocations()
	{
		return resourceLocations;
	}
	
	public FileComparator getFileComparator(){
		return new FileComparator();
	}
}
