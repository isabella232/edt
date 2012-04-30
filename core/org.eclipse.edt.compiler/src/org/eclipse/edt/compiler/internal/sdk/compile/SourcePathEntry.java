/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.sdk.compile;

import java.io.File;

import org.eclipse.edt.compiler.EGL2IREnvironment;
import org.eclipse.edt.compiler.internal.sdk.utils.Util;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.compiler.BindingCompletor;
import org.eclipse.edt.compiler.internal.core.dependency.NullDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.compiler.internal.core.lookup.EnvironmentScope;
import org.eclipse.edt.compiler.internal.core.lookup.FileScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.SystemScope;
import org.eclipse.edt.compiler.internal.core.utils.PartBindingCache;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 */
public class SourcePathEntry {
	
    private static final SourcePathEntry INSTANCE = new SourcePathEntry();
    
	private PartBindingCache bindingCache = new PartBindingCache();
	private IProcessor processor;
    private IEnvironment declaringEnvironment;
     
    private SourcePathEntry() { }
    
    public static SourcePathEntry getInstance(){
        return INSTANCE;
    }
    
    public int hasPart(String packageName, String partName) {
        return SourcePathInfo.getInstance().hasPart(packageName, partName);
    }

    public boolean hasPackage(String packageName) {
        return SourcePathInfo.getInstance().hasPackage(packageName);
    }
    
    public IPartBinding getPartBindingFromCache(String packageName, String partName){
        return bindingCache.get(packageName, partName);
    }

    public IPartBinding getPartBinding(String packageName, String partName) {
        return getPartBinding(packageName, partName, false);
    }
    
    /**
     * Return this part if we are currently compiling it, or if we have already compiled it
     */
    public IPartBinding getPartBinding(String packageName, String partName, boolean force) {
        IPartBinding result = null;
        
        // Check to see if it is pending or currently being compiled
        result = processor.requestCompilationFor(packageName, partName, force);
        
        // if not, see if we compiled it already or request that it be compiled
        if(result == null){
            result = bindingCache.get(packageName, partName);
        }        
        
        return result;
    }
    
    public IPartBinding getOrCompilePartBinding(String packageName, String partName){
    	return getOrCompilePartBinding(packageName, partName, false);
    }
    
    private IPartBinding getOrCompilePartBinding(String packageName, String partName, boolean force){
    	IPartBinding result = null;
    	
    	result = getPartBinding(packageName, partName, force);
    	
    	if(result == null){
    		File declaringFile = SourcePathInfo.getInstance().getDeclaringFile(packageName, partName);
    		SourcePathEntry.getInstance().getProcessor().addPart(packageName, SourcePathInfo.getInstance().getCaseSensitivePartName(packageName, Util.getFilePartName(declaringFile)));
    		SourcePathEntry.getInstance().getProcessor().addPart(packageName, SourcePathInfo.getInstance().getCaseSensitivePartName(packageName, partName));
    		result = getPartBinding(packageName, partName, force);
    	}
    	
    	return result;
    }
    
    /**
     * Called when a part is on the queue but cannot be completly compiled
     */
    public IPartBinding compileLevel2Binding(String packageName, String caseSensitivePartName) {
    	String caseInsensitivePartName = NameUtile.getAsName(caseSensitivePartName);
        File declaringFile = SourcePathInfo.getInstance().getDeclaringFile(packageName, caseInsensitivePartName);
        
        Node partAST = ASTManager.getInstance().getAST(declaringFile, caseInsensitivePartName);
        declaringEnvironment.getNewPartBinding(packageName, caseSensitivePartName, Util.getPartType(partAST));
        IPartBinding partBinding = declaringEnvironment.getNewPartBinding(packageName, caseSensitivePartName, Util.getPartType(partAST));
        
        Scope scope;
        if(partBinding.getKind() == ITypeBinding.FILE_BINDING){
            scope = new EnvironmentScope(declaringEnvironment, NullDependencyRequestor.getInstance());
        }else{
        	String fileName = Util.getFilePartName(declaringFile);
			IPartBinding fileBinding = getOrCompilePartBinding(packageName, fileName, true);
			scope = new SystemScope(new FileScope(new EnvironmentScope(declaringEnvironment, NullDependencyRequestor.getInstance()), (FileBinding)fileBinding, NullDependencyRequestor.getInstance()), declaringEnvironment.getSystemEnvironment());
        }
        BindingCompletor.getInstance().completeBinding(partAST, partBinding, scope, new ICompilerOptions(){
        });
        partBinding.setEnvironment(declaringEnvironment);
       
        bindingCache.put(packageName, caseInsensitivePartName, partBinding);
        
        return partBinding;
    }
    
    public IPartBinding getNewPartBinding(String packageName, String caseSensitivePartName, int kind) {
    	String caseInsensitivePartName = NameUtile.getAsName(caseSensitivePartName);
        IPartBinding partBinding = bindingCache.get(packageName, caseInsensitivePartName);
        if(partBinding == null || partBinding.getKind() != kind) {
	    	partBinding = BindingUtil.createPartBinding(kind, packageName, caseSensitivePartName);
	    	partBinding.setValid(false);
            bindingCache.put(packageName, caseInsensitivePartName, partBinding);
        }
        else {
        	partBinding.clear();
        	partBinding.setValid(false);
        }
        return partBinding;
    }
    
    public long lastModified(String packageName, String partName){
    	File declaringFile = SourcePathInfo.getInstance().getDeclaringFile(packageName, partName);
    	return declaringFile.lastModified();
    }
	
    public void setProcessor(IProcessor processor) {
      this.processor = processor;        
    }
    
    public IProcessor getProcessor(){
    	return processor;
    }
    
    public void setDeclaringEnvironment(IEnvironment declaringEnvironment){
        this.declaringEnvironment = declaringEnvironment;
    }    
   
    public void reset() {
    	bindingCache = new PartBindingCache();
    	processor = null;
    	declaringEnvironment = null;
    }
}
