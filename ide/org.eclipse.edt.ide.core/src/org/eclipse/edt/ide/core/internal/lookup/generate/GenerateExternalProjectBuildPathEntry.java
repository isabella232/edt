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
package org.eclipse.edt.ide.core.internal.lookup.generate;

import java.io.InputStream;

import org.eclipse.edt.ide.core.internal.lookup.ExternalProject;
import org.eclipse.edt.mof.egl.Part;

public class GenerateExternalProjectBuildPathEntry implements IGenerateBuildPathEntry {//implements Environment {

	private ExternalProject project;
	
	public GenerateExternalProjectBuildPathEntry (ExternalProject project){
		this.project = project;
	}
	
	public void clear(){
	}
	
	public InputStream getResourceAsStream(String relativePath){
		return null;
	}
	
	public String getResourceLocation(String relativePath){		
		return "";
	}
	
	public Part findPart(String packageName[], String partName){
		return null;
	}
	
	public boolean hasPart(String[] packageName, String partName) {
		return false;
    }
	
	public boolean hasPackage(String[] packageName) {
		return false;
	}
}
