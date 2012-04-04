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
package org.eclipse.edt.ide.core.internal.lookup;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.AbstractProcessingQueue;
import org.eclipse.edt.compiler.internal.core.builder.CircularBuildRequestException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.compiler.BindingCompletor;
import org.eclipse.edt.compiler.internal.core.dependency.NullDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.*;
import org.eclipse.edt.compiler.internal.core.utils.PartBindingCache;
import org.eclipse.edt.ide.core.internal.binding.PartRestoreFailedException;
import org.eclipse.edt.ide.core.internal.builder.ASTManager;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.CachingObjectStore;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.ObjectStore;

/**
 * @author winghong
 */
public class ProjectBuildPathEntry implements IBuildPathEntry {
	
	private class RealizingEnvironment implements IEnvironment {

		public IPartBinding getPartBinding(String[] packageName, String partName) {
			return ProjectBuildPathEntry.this.getPartBinding(packageName, partName, true);
        }

		public IPartBinding getNewPartBinding(String[] packageName, String caseSensitiveInternedPartName, int kind) {
			return ProjectBuildPathEntry.this.getNewPartBinding(packageName, caseSensitiveInternedPartName, kind);
		}

		public boolean hasPackage(String[] packageName) {
			return ProjectBuildPathEntry.this.hasPackage(packageName);
		}

		public IPackageBinding getRootPackage() {
			return declaringEnvironment.getRootPackage();
		}
		
		@Override
		public ISystemEnvironment getSystemEnvironment() {
			return ProjectBuildPathEntry.this.getSystemEnvironment();
		}
		
		@Override
		public ICompiler getCompiler() {
			return ProjectBuildPathEntry.this.getCompiler();
		}
	}
	
	private ProjectInfo projectInfo;
    private PartBindingCache bindingCache;
    private AbstractProcessingQueue processingQueue;
    private ProjectEnvironment declaringEnvironment;
    private IEnvironment realizingEnvironment;
    private ObjectStore[] stores;
    public static final ObjectStore[] EMPTY_STORES = new ObjectStore[0];
    
    protected ProjectBuildPathEntry(ProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
        this.bindingCache = new PartBindingCache();
        this.stores = EMPTY_STORES;
        this.realizingEnvironment = new RealizingEnvironment();
    }
    
    protected ProjectEnvironment getDeclaringEnvironment() {
		return this.declaringEnvironment;
	}
    
    protected void setDeclaringEnvironment(ProjectEnvironment projectEnvironment) {
        this.declaringEnvironment = projectEnvironment;
    }
    
    protected void setObjectStores(ObjectStore[] stores) {
    	if (stores == null) {
    		this.stores = EMPTY_STORES;
    	}
    	else {
    		this.stores = stores;
    	}
    }
    
    @Override
	public ObjectStore[] getObjectStores() {
		return stores;
	}
    
    public void setProcessingQueue(AbstractProcessingQueue processingQueue) {
        this.processingQueue = processingQueue;
    }
    
    /** 
	 * Called by a level_01 compile to create a binding for a part when the processing queue is too long.
	 * This should only be called on 'Source' projects, as a read only project would result in the part being loaded
	 * directly from an IR instead of being compiled.
	 */
    public int hasPart(String[] packageName, String partName) {
        return projectInfo.hasPart(packageName, partName);
    }

    public boolean hasPackage(String[] packageName) {
        return projectInfo.hasPackage(packageName);
    }
    
    public IPartBinding getPartBindingFromCache(String[] packageName, String partName){
        return bindingCache.get(packageName, partName);
    }
    
    public IPartBinding getPartBinding(String[] packageName, String partName) {
        return getPartBinding(packageName, partName, false);
    }
    
    public IPartBinding getPartBinding(String[] packageName, String partName, boolean force) {
    	
    	//Short circuit...do not look in this enry for binary porject parts
    	if (ProjectBuildPathManager.getInstance().getProjectBuildPath(projectInfo.getProject()).isBinary()) {
    		return null;
    	}
    	
        IPartBinding result = null;
        if(processingQueue != null) {
            result = processingQueue.requestCompilationFor(packageName, partName, force);
		}
        if(result == null){
        	// Conceptually should check whether it has that part or not, but for performance reason we will try to grab it from
        	// the cache first.
        	// The existance of a part in the cache implies that the part does physically exist
            result = bindingCache.get(packageName, partName);
            if(result != null) {
                return result;
            }
            else {
            	//RMERUI
            	if(ProjectBuildPathManager.getInstance().getProjectBuildPath(projectInfo.getProject()).isReadOnly()){
            		// It is a project with no source, read the IRs
            		return readPartBinding(packageName, partName);
            	}else{
            		// This project has source, compile from the source
	            	if(projectInfo.hasPart(packageName, partName) != ITypeBinding.NOT_FOUND_BINDING) {
	            		IFile declaringFile = projectInfo.getPartOrigin(packageName, partName).getEGLFile();
	            		if(Util.getFilePartName(declaringFile) == partName || projectInfo.hasPart(packageName,partName) == ITypeBinding.FUNCTION_BINDING){
	            			// File and function parts are not stored on disk, create a new one
	            			try{
	            				return compileLevel2Binding(packageName, projectInfo.getCaseSensitivePartName(packageName, partName));
	            			}catch(CircularBuildRequestException e){
            					// Remove this part from the cache, so that it is not used incorrectly at a different time
	            				removePartBindingInvalid(packageName, partName);
            					throw e;
            				}
	            		}
		                else{
		                	return readPartBinding(packageName, partName);
		                }
	                }
	                else {
	                    return null;
	                }
            	}
            }
        }
        
        return result;
    }
    
    public IPartBinding getNewPartBinding(String[] packageName, String caseSensitiveInternedPartName, int kind) {
    	String caseInsensitiveInternedPartName = InternUtil.intern(caseSensitiveInternedPartName);
        IPartBinding partBinding = bindingCache.get(packageName, caseInsensitiveInternedPartName);
        if(partBinding == null || partBinding.getKind() != kind) {
            partBinding = PartBinding.newPartBinding(kind, packageName, caseSensitiveInternedPartName);
            bindingCache.put(packageName, caseInsensitiveInternedPartName, partBinding);
        }
        else {
        	partBinding.clear();
        	partBinding.setValid(false);
        }
        return partBinding;
    }
    
    private IPartBinding readPartBinding(String[] packageName, String partName) {
    	try {
	    	EObject ir = readPart(packageName, partName);
	    	if (ir != null) {
	    		IPartBinding partBinding = declaringEnvironment.getConverter().convert(ir);
	    		if (partBinding != null) {
	    			bindingCache.put(packageName, partName, partBinding);
	    			return partBinding;
	    		}
	    	}
	    	return null;
    	}
    	catch(Exception e) {
    		throw new PartRestoreFailedException(packageName, partName, e);
    	}
    }
    
    /**
     * Called when a part is on the queue but cannot be completly compiled
     */
    public IPartBinding compileLevel2Binding(String[] packageName, String caseSensitiveInternedPartName) {
    	String caseInsensitiveInternedPartName = InternUtil.intern(caseSensitiveInternedPartName);
        IFile declaringFile = projectInfo.getPartOrigin(packageName, caseInsensitiveInternedPartName).getEGLFile();
        
        Node partAST = ASTManager.getInstance().getAST(declaringFile, caseInsensitiveInternedPartName);
        IPartBinding partBinding = new BindingCreator(declaringEnvironment, packageName, caseSensitiveInternedPartName, partAST).getPartBinding();
 
        partBinding.setEnvironment(declaringEnvironment);

        
        Scope scope;
        if(partBinding.getKind() == ITypeBinding.FILE_BINDING){
            scope = new EnvironmentScope(declaringEnvironment, NullDependencyRequestor.getInstance());
        }else{
        	String fileName = Util.getFilePartName(declaringFile);
			IPartBinding fileBinding = getPartBinding(packageName, fileName, true);
			if(!fileBinding.isValid()){
				scope = new SystemScope(new FileASTScope(new EnvironmentScope(declaringEnvironment, NullDependencyRequestor.getInstance()), (FileBinding)fileBinding, ASTManager.getInstance().getFileAST(declaringFile)),getSystemEnvironment());
			}else{
				scope = new SystemScope(new FileScope(new EnvironmentScope(declaringEnvironment, NullDependencyRequestor.getInstance()), (FileBinding)fileBinding, NullDependencyRequestor.getInstance()),getSystemEnvironment());
			}
        }
        BindingCompletor.getInstance().completeBinding(partAST, partBinding, scope, DefaultCompilerOptions.getInstance());
               
        bindingCache.put(packageName, caseInsensitiveInternedPartName, partBinding);
        
        return partBinding;
    }
    
    /**
     * 
     * Mark this part binding as invalid if it is in the cache.
     */
    protected void markPartBindingInvalid(String[] packageName, String partName) {
    	IPartBinding result = bindingCache.get(packageName, partName);
    	if(result != null){
    		result.setValid(false);
    	}
    }

	/**
	 * Remove this part binding from the cache since it has been removed from the workspace
	 */
	public void removePartBindingInvalid(String[] packageName, String partName) {
		bindingCache.remove(packageName, partName);		
	}
	
	/**
	 * Return this entry's IProject
	 */
	public IProject getProject(){
		return projectInfo.getProject();		
	}

	public IEnvironment getRealizingEnvironment() {
		return realizingEnvironment;
	}

	public void clear(boolean clean) {
		bindingCache = new PartBindingCache();
		
		if (clean) {
			for (ObjectStore store : stores) {
				if (store instanceof CachingObjectStore) {
					((CachingObjectStore)store).clearCache();
				}
			}
		}
	}
	
	public boolean isZipFile(){
		return false;
	}
	
	public boolean isProject(){
		return true;
	}
	
	
	public String getID(){
		return getProject().getName();
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
        
        BindingCompletor.getInstance().completeBinding(fileAST, fileBinding, scope, DefaultCompilerOptions.getInstance());
               
        bindingCache.put(packageName, caseInsensitiveInternedFileName, fileBinding);
        
        return fileBinding;
    }

    public FileBinding getFileBindingFromCache(String[] packageName, String partName){
        return (FileBinding)bindingCache.get(packageName, partName);
    }
    
	public IPartBinding getCachedPartBinding(String[] packageName, String partName) {
		return getPartBindingFromCache(packageName, partName);
	}

	public void addPartBindingToCache(IPartBinding partBinding) {
		bindingCache.put(partBinding.getPackageName(), partBinding.getCaseSensitiveName(), partBinding);
	}
	
	private EObject readPart(String[] packageName, String name) throws DeserializationException {
		StringBuilder keyBuf = new StringBuilder( 100 );
		keyBuf.append(":");
    	if (packageName != null && packageName.length > 0) {
    		keyBuf.append(IRUtils.concatWithSeparator(packageName, "."));
    		keyBuf.append('.');
    	}
   		keyBuf.append(name);
    	
    	for (int i = 0; i < stores.length; i++) {
    		EObject ir = stores[i].get(stores[i].getKeyScheme() + keyBuf.toString());
    		if (ir != null) {
    			return ir;
    		}
    	}
    	return null;
	}

	@Override
	public Part findPart(String[] packageName, String name) throws PartNotFoundException {
		if(ProjectBuildPathManager.getInstance().getProjectBuildPath(projectInfo.getProject()).isReadOnly()
				|| projectInfo.hasPart(packageName, name) != ITypeBinding.NOT_FOUND_BINDING){
			try {
				EObject ir = readPart(packageName, name);
				if (ir instanceof Part) {
					return (Part)ir;
				}
			}
			catch (DeserializationException e) {
				throw new PartNotFoundException(e);
			}
		}
		return null;
	}
	
	protected ISystemEnvironment getSystemEnvironment() {
		return SystemEnvironmentManager.findSystemEnvironment(getProject(), getNotifier());
	}
	
	protected ICompiler getCompiler() {
		return ProjectSettingsUtility.getCompiler(getProject());
	}
	
	public IBuildNotifier getNotifier() {
		return processingQueue == null ? null : processingQueue.getNotifier();
	}
}
