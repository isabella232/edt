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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.compiler.internal.core.lookup.EnvironmentScope;
import org.eclipse.edt.compiler.internal.core.lookup.FileScope;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.SystemScope;
import org.eclipse.edt.compiler.internal.egl2mof.Egl2Mof;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.compiler.internal.sdk.compile.ASTManager;
import org.eclipse.edt.compiler.internal.sdk.compile.Compiler;
import org.eclipse.edt.compiler.internal.sdk.compile.DefaultSDKProblemRequestorFactory;
import org.eclipse.edt.compiler.internal.sdk.compile.DependencyInfo;
import org.eclipse.edt.compiler.internal.sdk.compile.IProcessor;
import org.eclipse.edt.compiler.internal.sdk.compile.ISDKProblemRequestorFactory;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathEntry;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathInfo;
import org.eclipse.edt.compiler.internal.sdk.compile.TopLevelFunctionProcessor;
import org.eclipse.edt.compiler.internal.util.TopLevelFunctionInfo;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.SerializationException;


/**
 * @author svihovec
 *
 */
public class Processor extends AbstractProcessingQueue implements IProcessor {

    private EGL2IREnvironment environment;
    private ISDKProblemRequestorFactory problemRequestorFactory;
    private ISystemEnvironment sysEnv;
    private ICompiler compiler;

    public Processor(IBuildNotifier notifier, ICompilerOptions compilerOptions, ISDKProblemRequestorFactory problemRequestorFactory, ISystemEnvironment sysEnv, ICompiler compiler) {
        super(notifier, compilerOptions);
        this.problemRequestorFactory = problemRequestorFactory;
        if (problemRequestorFactory == null){
        	this.problemRequestorFactory = new DefaultSDKProblemRequestorFactory();

        }
        this.sysEnv = sysEnv;
        this.compiler = compiler;
    }
    
    public void setEnvironment(EGL2IREnvironment environment){
        this.environment = environment;
    }
   
    public boolean hasExceededMaxLoop() {
        return false;
    }

    public IPartBinding level03Compile(String[] packageName, String caseSensitiveInternedPartName) {
    	String caseInsensitiveInternedPartName = InternUtil.intern(caseSensitiveInternedPartName);
        File declaringFile = SourcePathInfo.getInstance().getDeclaringFile(packageName, caseInsensitiveInternedPartName);
        Node partAST = ASTManager.getInstance().getAST(declaringFile, caseInsensitiveInternedPartName);
        if (partAST instanceof org.eclipse.edt.compiler.core.ast.File){
        	org.eclipse.edt.compiler.core.ast.File errorFile =ASTManager.getInstance().getFileAST(declaringFile); 
        	errorFile.accept(problemRequestorFactory.getSyntaxErrorRequestor(declaringFile));//$NON-NLS-1$
        }
        
        IPartBinding binding = new BindingCreator(environment, packageName, caseSensitiveInternedPartName, partAST).getPartBinding();
        if(binding.getKind() != ITypeBinding.FILE_BINDING && binding.getKind() != ITypeBinding.FUNCTION_BINDING){
        	System.out.println();
            System.out.println("Processing Part: " + caseInsensitiveInternedPartName);	
        }
        
        binding.setEnvironment(environment);

        DependencyInfo dependencyInfo = new DependencyInfo();
        
        Scope scope = createPartScope(packageName, declaringFile, binding, dependencyInfo);
        IProblemRequestor problemRequestor = createProblemRequestor(declaringFile,partAST, binding);

		Compiler.getInstance().compilePart(partAST, binding, scope, dependencyInfo, problemRequestor, compilerOptions);
//		try {
//			Compiler.getInstance().compilePart(partAST, binding, scope, dependencyInfo, problemRequestor, compilerOptions);
//		} catch (CircularBuildRequestException e) {
//            reschedulePart(packageName, caseInsensitiveInternedPartName);           
//            ProcessingUnit processingUnit = (ProcessingUnit) pendingUnits.get(new ProcessingUnitKey(packageName, caseInsensitiveInternedPartName));
//            binding = process(processingUnit, LEVEL_TWO);
//            environment.addPartBindingToCache(binding);
//		}
        
        TopLevelFunctionInfo[] functions = null;
        if(dependencyInfo.getFunctionContainerScope() != null){
			functions = processTopLevelFunctions(dependencyInfo.getTopLevelFunctions(), dependencyInfo.getFunctionContainerScope(), dependencyInfo);
		}
        
        if(binding.getKind() != ITypeBinding.FILE_BINDING){
        	
            org.eclipse.edt.compiler.core.ast.File fileAST = ASTManager.getInstance().getFileAST(declaringFile);
            
//	        Part part = createIRFromBoundAST(partAST, declaringFile,functions, fileAST.getImportDeclarations(), problemRequestor);
	        try {
				MofSerializable part = createIRFromBoundAST2(partAST, declaringFile,functions, fileAST.getImportDeclarations(), problemRequestor);
				
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
        
    private File getPackage(File path,String[] packageName){
    	
    	File root = path;
    	for (int i = 0; i < packageName.length; i++) {
    		File subFolder = new File(root, IRFileNameUtility.toIRFileName(packageName[i]));
    		
			if(!subFolder.exists()){
				subFolder.mkdir();
			}
				root = subFolder;
		}
    	
    	return root;
    }
    
    private Scope createPartScope(String[] packageName, File declaringFile, IPartBinding binding, IDependencyRequestor dependencyRequestor) {
        Scope scope;
		if(binding.getKind() == ITypeBinding.FILE_BINDING){
			scope = new EnvironmentScope(environment, dependencyRequestor);
		}else{
			String fileName = Util.getFilePartName(declaringFile);
			IPartBinding fileBinding = environment.getPartBinding(packageName, fileName);
			scope = new SystemScope(new FileScope(new EnvironmentScope(environment, dependencyRequestor), (FileBinding)fileBinding, dependencyRequestor), sysEnv);
		}
		return scope;
	}
    

    private MofSerializable createIRFromBoundAST2(Node partAST, File declaringFile,TopLevelFunctionInfo[] functions, List imports, IProblemRequestor problemRequestor) {
    	
        Egl2Mof generator = new Egl2Mof(environment);
        return (MofSerializable)generator.convert((org.eclipse.edt.compiler.core.ast.Part)partAST, new SDKContext(declaringFile, compiler), problemRequestor);
    }
    
    private IProblemRequestor createProblemRequestor(File file,Node partAST, IPartBinding binding) {
		IProblemRequestor newRequestor = problemRequestorFactory.getProblemRequestor(file,binding.getName());
		if(binding.getKind() == ITypeBinding.FUNCTION_BINDING){
			newRequestor = problemRequestorFactory.getGenericTopLevelFunctionProblemRequestor(file,binding.getName(),((TopLevelFunction)partAST).isContainerContextDependent());
		}
		return newRequestor;
	}

    public IPartBinding level02Compile(String[] packageName, String caseSensitiveInternedPartName) {
        return SourcePathEntry.getInstance().compileLevel2Binding(packageName, caseSensitiveInternedPartName);
    }

    public IPartBinding level01Compile(String[] packageName, String caseSensitiveInternedPartName) {
        return environment.level01Compile(packageName, caseSensitiveInternedPartName);
    }

    public IPartBinding getPartBindingFromCache(String[] packageName, String partName) {
        return SourcePathEntry.getInstance().getPartBindingFromCache(packageName, partName);
    }

    private TopLevelFunctionInfo[] processTopLevelFunctions(Set topLevelFunctions, FunctionContainerScope contextScope, DependencyInfo dependencyInfo) {
    	TopLevelFunctionProcessor queue = new TopLevelFunctionProcessor(environment, contextScope, dependencyInfo, compilerOptions, problemRequestorFactory);
		for (Iterator iter = topLevelFunctions.iterator(); iter.hasNext();) {
			IPartBinding function = (IPartBinding) iter.next();
//			queue.addPart(function);
		}
		
		return queue.process();        
    }
    
    public void doAddPart(String[] packageName, String caseInsensitiveInternedPartName) {
		addPart(packageName, SourcePathInfo.getInstance().getCaseSensitivePartName(packageName, caseInsensitiveInternedPartName));		
	}	
}
