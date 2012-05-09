/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.internal.model.util.Util;

public class EGLReadOnlyFile implements IStorage {
	  private String pathString;	//full path for eglar file
	  private String fileString;	//full qualified ir file name (with no extension)
	  private boolean isExternal;
	  private String project;
	  public static final String EGLAR_IR_SEPARATOR = "|";
	  
	  public EGLReadOnlyFile(String path, String file) {
	    this.pathString = path;
	    this.fileString = resolveFileString(file);
	    this.isExternal = false;
	  }
	  
	  public EGLReadOnlyFile(String path, String file, String project, boolean isExternal) {
		  this.pathString = path;
		  this.fileString = resolveFileString(file);
		  this.project = project;
		  this.isExternal = isExternal;		  
	  }
	 
	  public InputStream getContents() throws CoreException {
		  //TODO get the contents of source attachment
		  FileInputStream fs = null;
		  try {
			  fs = new FileInputStream(pathString);
		  } catch (FileNotFoundException e) {
			e.printStackTrace();
		  }
		  return fs;
	  }
	  
	  public Object getAdapter(Class adapter) {
		    return null;
	  }
	  
	  /*
	 * get the full path for the ir file
	 * e.g.
	 * TestProj1/Test.eglar|com/ibm/egl/test/interfaces/demointerface.ir
	 * C:/Temp/Test.eglar|com/ibm/egl/test/interfaces/demointerface.ir
	 * TestProj1/Test.eglar|demointerface.ir
	 * 
	 */
	  public IPath getFullPath() {
		  String path = pathString;
		  path += EGLAR_IR_SEPARATOR;
		  path += fileString;
		  return new Path(path);
	  }
	  
	  public String getName() {
			  String irFileName = "";
			  int index = fileString.lastIndexOf("/");
			  if(index > -1){	//has package
				  irFileName = fileString.substring(index + 1);
			  }
			  else{	//no package
				  irFileName = fileString;
			  }
			  return irFileName;
	  }
	  
	  public IPath getEGLARPath() {
		  return new Path(pathString);
	  }
	  
	  
	  public boolean isReadOnly() {
		  return true;
	  }
	  
	  public boolean isExternal() {
		  return isExternal;
	  }

	  public String getProject() {
		  return project;
	  }
	  
	  public String getSource() {
		  ZipFile zip = null;
		  String out = "";
		  try {
			  zip = new ZipFile(this.getEGLARPath().toFile());
			  ZipEntry ze = new ZipEntry(fileString);
			  out = new String(Util.getZipEntryByteContent(ze, zip));
		  } catch (ZipException e) {
			// TODO Auto-generated catch block
			  e.printStackTrace();
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			  e.printStackTrace();
		  }
		  
		  return out;
	  }

	  //for Forms in FormGroup, to eliminate the Form section, only keep the FormGroup section, which exactly is the ClassFile name
	  private static String resolveFileString(String file){
		  String fileString = file;
		  int index = fileString.indexOf(":");
		  if(index != -1){
			  fileString = fileString.substring(0, index);
		  }
		  return fileString;
	  }
	   
}
