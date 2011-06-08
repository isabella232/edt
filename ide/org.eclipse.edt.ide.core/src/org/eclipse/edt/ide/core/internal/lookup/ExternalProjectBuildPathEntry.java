/*******************************************************************************
 * Copyright © 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.internal.EGLAliasJsfNamesSetting;
import org.eclipse.edt.compiler.internal.EGLVAGCompatibilitySetting;
import org.eclipse.edt.compiler.internal.core.compiler.BindingCompletor;
import org.eclipse.edt.compiler.internal.core.dependency.NullDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.compiler.internal.core.lookup.EnvironmentScope;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.utils.PartBindingCache;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.IWorkingCopyBuildPathEntry;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.ObjectStore;

public class ExternalProjectBuildPathEntry implements IBuildPathEntry, IWorkingCopyBuildPathEntry{
	
	ExternalProject project;
	
	//This is used to cache the file bindings
    private PartBindingCache fileBindingCache = new PartBindingCache();

    private IEnvironment declaringEnvironment;

    
	public ExternalProjectBuildPathEntry(ExternalProject project) {
		super();
		this.project = project;
	}
	
	public void setDeclaringEnvironment(IEnvironment declaringEnvironment) {
		this.declaringEnvironment = declaringEnvironment;
	}

	public String getID() {
		return project.getName();
	}

	public IPartBinding getPartBinding(String[] packageName, String partName) {
		// All parts are stored in the EGLAR file(s) in the external project
		return null;
	}
	
	public IPartBinding getCachedPartBinding(String[] packageName, String partName) {
		return null;
	}


	public IEnvironment getRealizingEnvironment() {
		return null;
	}

	public boolean hasPackage(String[] packageName) {
		return false;
	}

	public int hasPart(String[] packageName, String partName) {
		return 0;
	}

	public boolean isProject() {
		return true;
	}
	public boolean isZipFile() {
		return false;
	}

	public IPartOrigin getPartOrigin(String[] packageName, String partName) {
		return new IPartOrigin() {

			
			//TODO implement for SourcAttachement
			public IFile getEGLFile() {
				return null;
			}

			//TODO implement for SourcAttachement
			public boolean isOriginEGLFile() {
				return false;
			}

			//TODO implement for SourcAttachement
			public boolean isSourceCodeAvailable() {
				return false;
			}

//			public String getSourcePath() {
//				// TODO Auto-generated method stub
//				return null;
//			}
			
		};
	
	}
	
	public ExternalProject getProject() {
		return project;
	}
	
    public FileBinding getFileBinding(String[] packageName, String fileName, File fileAST) {
       	
    	String caseInsensitiveInternedFileName = InternUtil.intern(fileName);
    	FileBinding fileBinding = getFileBindingFromCache(packageName, caseInsensitiveInternedFileName);
    	if (fileBinding != null) {
    		return fileBinding;
    	}
    	
        
        fileBinding = (FileBinding)new BindingCreator(declaringEnvironment, packageName, caseInsensitiveInternedFileName, fileAST).getPartBinding();
 
        fileBinding.setEnvironment(declaringEnvironment);

        
        Scope scope = new EnvironmentScope(declaringEnvironment, NullDependencyRequestor.getInstance());
        
        BindingCompletor.getInstance().completeBinding(fileAST, fileBinding, scope, new ICompilerOptions(){
            public boolean isVAGCompatible() {
                return EGLVAGCompatibilitySetting.isVAGCompatibility();
            }            
			public boolean isAliasJSFNames() {
				return EGLAliasJsfNamesSetting.isAliasJsfNames();
			}            
        });
               
        fileBindingCache.put(packageName, caseInsensitiveInternedFileName, fileBinding);
        
        return fileBinding;
    }

	public void clear() {
		fileBindingCache = new PartBindingCache();		
	}
    
    public FileBinding getFileBindingFromCache(String[] packageName, String partName){
        return (FileBinding)fileBindingCache.get(packageName, partName);
    }
	

    public IPartBinding getNewPartBinding(String[] packageName, String caseSensitiveInternedPartName, int kind) {
    	
    	if (kind != ITypeBinding.FILE_BINDING) {
    		throw new UnsupportedOperationException("ExternalProjectBuildPathEntry.getNewPartBinding : invalid part type");
    	}
    	
    	
    	String caseInsensitiveInternedPartName = InternUtil.intern(caseSensitiveInternedPartName);
        FileBinding fileBinding = (FileBinding)fileBindingCache.get(packageName, caseInsensitiveInternedPartName);
        
        if(fileBinding == null) {
            fileBinding = (FileBinding)PartBinding.newPartBinding(kind, packageName, caseSensitiveInternedPartName);
            fileBindingCache.put(packageName, caseInsensitiveInternedPartName, fileBinding);
        }
        else {
        	fileBinding.clear();
        }
        return fileBinding;
    }

	@Override
	public Part findPart( String[] packageName, String name ) throws PartNotFoundException {
		// TODO
		return null;
	}

	@Override
	public ObjectStore[] getObjectStores() {
		// TODO
		return new ObjectStore[0];
	}

	@Override
	public void addPartBindingToCache(IPartBinding partBinding) {
		// TODO
	}
}
