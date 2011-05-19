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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.ide.core.internal.binding.BinaryFileManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfo;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.egl.Part;

public class GenerateBuildPathEntry implements IGenerateBuildPathEntry {

	private ProjectInfo projectInfo;
	private PartCache partCache = new PartCache();
	
	public GenerateBuildPathEntry (ProjectInfo pInfo){
		projectInfo = pInfo;
	}
	
	public void clear(){
		partCache = new PartCache();
	}
	
	public InputStream getResourceAsStream(String relativePath){
		IFile file = ProjectBuildPathManager.getInstance().getProjectBuildPath(projectInfo.getProject()).getOutputLocation().getFile(new Path(IRFileNameUtility.toIRFileName(relativePath)));
		if (file.exists()){
			try {
				return new BufferedInputStream(new FileInputStream(file.getLocation().toFile()));
			} catch (FileNotFoundException e) {

			}
		}
		
		return null;
	}
	
	public String getResourceLocation(String relativePath){
		IFile file = ProjectBuildPathManager.getInstance().getProjectBuildPath(projectInfo.getProject()).getOutputLocation().getFile(new Path(IRFileNameUtility.toIRFileName(relativePath)));
		if (file.exists()){
			return file.getLocation().toString();
		}
		
		return "";
	}
	
	public Part findPart(String packageName[], String partName){
		Part result;
        
        // Conceptually should check whether it has that part or not, but for performance reason we will try to grab it from
    	// the cache first.
    	// The existance of a part in the cache implies that the part does physically exist
        result = partCache.get(packageName, partName);
        if(result != null) {
            return result;
        }
        else {
        	if(hasPart(packageName, partName)) {
        		return readPart(packageName, partName);
            }
            else {
                return null;
            }
        }
	}
	
	public boolean hasPart(String[] packageName, String partName) {
		//RMERUI 
		if(ProjectBuildPathManager.getInstance().getProjectBuildPath(projectInfo.getProject()).isReadOnly()){
			//TODO EDT it's appending the IR extension, for now hardcoded to eglxml, but might be the binary format
			IFile file = ProjectBuildPathManager.getInstance().getProjectBuildPath(projectInfo.getProject()).getOutputLocation().getFile(Util.stringArrayToPath(IRFileNameUtility.toIRFileName(packageName)).append(IRFileNameUtility.toIRFileName(partName)).addFileExtension("eglxml"));
			return file.exists();
		}else{
			return projectInfo.hasPart(packageName,partName) != ITypeBinding.NOT_FOUND_BINDING;
		}
    }

 	private Part readPart(String[] packageName, String partName) {
 		Part part = BinaryFileManager.getInstance().readPart(packageName,partName,projectInfo.getProject());
 		if (part != null){
	 		partCache.put(packageName,partName,part);
	 		//TODO EDT
//	 		part.setEnvironment(GenerateEnvironmentManager.getInstance().getGenerateEnvironment(projectInfo.getProject(), false));
 		}
 		return part;
    }
 	
 	public boolean hasPackage(String[] packageName) {
		return projectInfo.hasPackage(packageName);
	}
}
