/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler;

import java.io.File;
import java.util.List;

import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.EnvironmentScope;
import org.eclipse.edt.compiler.internal.core.lookup.FileScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.egl2mof.Egl2Mof;
import org.eclipse.edt.compiler.internal.sdk.compile.ASTManager;
import org.eclipse.edt.compiler.internal.sdk.compile.Compiler;
import org.eclipse.edt.compiler.internal.sdk.compile.DefaultSDKProblemRequestorFactory;
import org.eclipse.edt.compiler.internal.sdk.compile.DependencyInfo;
import org.eclipse.edt.compiler.internal.sdk.compile.IProcessor;
import org.eclipse.edt.compiler.internal.sdk.compile.ISDKProblemRequestorFactory;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathEntry;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathInfo;
import org.eclipse.edt.compiler.internal.util.PackageAndPartName;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 *
 */
public class Processor extends AbstractProcessingQueue implements IProcessor {

    private EGL2IREnvironment environment;
    private ISDKProblemRequestorFactory problemRequestorFactory;
    private ICompiler compiler;
    
    public static boolean skipSerialization;

    public Processor(IBuildNotifier notifier, ICompilerOptions compilerOptions, ISDKProblemRequestorFactory problemRequestorFactory, ICompiler compiler) {
        super(notifier, compilerOptions);
        this.problemRequestorFactory = problemRequestorFactory;
        if (problemRequestorFactory == null){
        	this.problemRequestorFactory = new DefaultSDKProblemRequestorFactory();

        }
        this.compiler = compiler;
    }
    
    public void setEnvironment(EGL2IREnvironment environment){
        this.environment = environment;
    }
   
    public boolean hasExceededMaxLoop() {
        return false;
    }

    public IPartBinding level03Compile(PackageAndPartName ppName) {
        File declaringFile = SourcePathInfo.getInstance().getDeclaringFile(ppName.getPackageName(), ppName.getPartName());
        Node partAST = ASTManager.getInstance().getAST(declaringFile, ppName.getPartName());
        if (partAST instanceof org.eclipse.edt.compiler.core.ast.File){
        	org.eclipse.edt.compiler.core.ast.File errorFile =ASTManager.getInstance().getFileAST(declaringFile); 
        	errorFile.accept(problemRequestorFactory.getSyntaxErrorRequestor(declaringFile));//$NON-NLS-1$
        }
        
        IPartBinding binding = environment.getNewPartBinding(ppName, Util.getPartType(partAST));
        if(binding.getKind() != ITypeBinding.FILE_BINDING && binding.getKind() != ITypeBinding.FUNCTION_BINDING){
        	System.out.println();
            System.out.println("Processing Part: " + ppName.getPartName());	
        }
        
        binding.setEnvironment(environment);

        DependencyInfo dependencyInfo = new DependencyInfo();
        
        Scope scope = createPartScope(ppName.getPackageName(), declaringFile, binding, dependencyInfo);
        IProblemRequestor problemRequestor = problemRequestorFactory.getProblemRequestor(declaringFile,binding.getName());

		Compiler.getInstance().compilePart(partAST, binding, scope, dependencyInfo, problemRequestor, compilerOptions);
        
        if(!skipSerialization && binding.getKind() != ITypeBinding.FILE_BINDING){
        	
            org.eclipse.edt.compiler.core.ast.File fileAST = ASTManager.getInstance().getFileAST(declaringFile);
            
	        try {
		        MofSerializable part = createIRFromBoundAST2(partAST, declaringFile, fileAST.getImportDeclarations(), problemRequestor);
		        if(part == null) {
		        	System.out.println("Part is null!");
		        	return binding;
		        }
		        	        
		        environment.save(part, true);
		        		        
			} catch (RuntimeException e) {
				problemRequestor.acceptProblem(((Part)partAST).getName(), IProblemRequestor.COMPILATION_EXCEPTION, new String[]{((Part)partAST).getName().getCanonicalName()});
			}
        }
        
         return binding;
    }
            
    private Scope createPartScope(String packageName, File declaringFile, IPartBinding binding, IDependencyRequestor dependencyRequestor) {
        Scope scope;
		if(binding.getKind() == ITypeBinding.FILE_BINDING){
			scope = new EnvironmentScope(environment, dependencyRequestor);
		}else{
			String fileName = Util.getFilePartName(declaringFile);
			IPartBinding fileBinding = environment.getPartBinding(packageName, fileName);
			scope = new FileScope(new EnvironmentScope(environment, dependencyRequestor), (FileBinding)fileBinding, dependencyRequestor);
		}
		return scope;
	}
    

    private MofSerializable createIRFromBoundAST2(Node partAST, File declaringFile, List imports, IProblemRequestor problemRequestor) {
    	
        Egl2Mof generator = new Egl2Mof(environment);
        return (MofSerializable)generator.convert((org.eclipse.edt.compiler.core.ast.Part)partAST, new SDKContext(declaringFile, compiler), problemRequestor);
    }
    
    public IPartBinding level02Compile(PackageAndPartName ppName) {
        return SourcePathEntry.getInstance().compileLevel2Binding(ppName);
    }

    public IPartBinding level01Compile(PackageAndPartName ppName) {
        return environment.level01Compile(ppName);
    }

    public IPartBinding getPartBindingFromCache(String packageName, String partName) {
        return SourcePathEntry.getInstance().getPartBindingFromCache(packageName, partName);
    }

    public void doAddPart(String packageName, String caseInsensitivePartName) {
		addPart(SourcePathInfo.getInstance().getPackageAndPartName(packageName, caseInsensitivePartName));		
	}	
}
