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
package org.eclipse.edt.compiler.internal.sdk.compile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.edt.compiler.internal.sdk.utils.Util;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.compiler.internal.core.lookup.EnvironmentScope;
import org.eclipse.edt.compiler.internal.core.lookup.FileScope;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.SystemScope;
import org.eclipse.edt.compiler.internal.util.TopLevelFunctionInfo;

/**
 * @author svihovec
 *
 */
public class TopLevelFunctionProcessor {

    private class TopLevelFunctionProcessingUnit {
        IPartBinding function;

        String contextSpecificCaseSensitiveInternedFunctionName;

        TopLevelFunctionProcessingUnit(IPartBinding function) {
            this.function = function;

            this.contextSpecificCaseSensitiveInternedFunctionName = createContextSpecificFunctionName();
        }

        private String createContextSpecificFunctionName() {
            StringBuffer contextSpecificFunctionNameBuffer = new StringBuffer();
            contextSpecificFunctionNameBuffer.append(containerContext.getName() + "$"); //$NON-NLS-1$
            for (int i = 0; i < function.getPackageName().length; i++) {
                contextSpecificFunctionNameBuffer.append(function.getPackageName()[i] + "."); //$NON-NLS-1$
            }
            contextSpecificFunctionNameBuffer.append(function.getName());

            return contextSpecificFunctionNameBuffer.toString().intern();
        }
    }
	 
	public class TopLevelFunctionProcessingUnitKey {
        IPartBinding function;

        public TopLevelFunctionProcessingUnitKey(IPartBinding function) {
            this.function = function;
        }

        public boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject instanceof TopLevelFunctionProcessingUnitKey) {
                TopLevelFunctionProcessingUnitKey otherPUKey = (TopLevelFunctionProcessingUnitKey) otherObject;
                return otherPUKey.function == function;
            }
            return false;
        }

        public int hashCode() {
            return function.getName().hashCode();
        }
    }
	
	private LinkedHashMap pendingUnits = new LinkedHashMap();
	private IPartBinding functionBeingProcessed;
	private HashSet processedUnits = new HashSet();
	private List processedFunctionInfos = new ArrayList();
	
    private FunctionContainerScope functionContainerScope;
    private DependencyInfo dependencyInfo;
    private IPartBinding containerContext;
    private IEnvironment environment;
	private ICompilerOptions compilerOptions;
	private ISDKProblemRequestorFactory problemRequestorFactory;

    public TopLevelFunctionProcessor(IEnvironment environment, FunctionContainerScope functionContainerScope, DependencyInfo dependencyInfo, ICompilerOptions compilerOptions, ISDKProblemRequestorFactory problemRequestorFactory){
        this.environment = environment;
        this.functionContainerScope = functionContainerScope;
        this.dependencyInfo = dependencyInfo;
        this.compilerOptions = compilerOptions;
        this.problemRequestorFactory = problemRequestorFactory;
        
        this.containerContext = functionContainerScope.getPartBinding();      
    }
    
    /**
	  * Only add a top level function if it is not already somewhere in this queue
	  */
	 protected void addPart(IPartBinding function){
	 	if(functionBeingProcessed != function && !processedUnits.contains(new TopLevelFunctionProcessingUnitKey(function))){
	 		pendingUnits.put(new TopLevelFunctionProcessingUnitKey(function), new TopLevelFunctionProcessingUnit(function));
	 	}
	 }
	 
	 public TopLevelFunctionInfo[] process() {
        while(!pendingUnits.isEmpty()) {
            Iterator iterator = pendingUnits.values().iterator();
            TopLevelFunctionProcessingUnit processingUnit = (TopLevelFunctionProcessingUnit) iterator.next();
            process(processingUnit);
        }
        
        return (TopLevelFunctionInfo[])processedFunctionInfos.toArray(new TopLevelFunctionInfo[processedFunctionInfos.size()]);
    }
    
    private void process(TopLevelFunctionProcessingUnit processingUnit) {
    	pendingUnits.remove(new TopLevelFunctionProcessingUnitKey(processingUnit.function));
        functionBeingProcessed = processingUnit.function;
        
        compile(processingUnit);
        
        functionBeingProcessed = null;
        processedUnits.add(new TopLevelFunctionProcessingUnitKey(processingUnit.function));
        
    }
	 
	 private Part compile(TopLevelFunctionProcessingUnit functionUnit) {
		
			String[] functionPackageName = functionUnit.function.getPackageName();
			String functionPartName = functionUnit.function.getName();
			String[] contextPackageName = containerContext.getPackageName();
			String contextPartName = containerContext.getName();
			String contextSpecificCaseSensitiveInternedFunctionName = functionUnit.contextSpecificCaseSensitiveInternedFunctionName;
				
			// compile the top level function within the context of the part stored in the processing unit
			// get declaring file from other project
			File functionDeclaringFile = SourcePathInfo.getInstance().getDeclaringFile(functionPackageName, functionPartName);
			
			if (functionDeclaringFile == null || !functionDeclaringFile.exists()){
				throw new BuildException("Unable to compile top level function " + functionUnit.function.getPackageQualifiedName() + " : EGL source code not be found.");
			}
				
			
			TopLevelFunction functionAST  = (TopLevelFunction)ASTManager.getInstance().getPartAST(functionDeclaringFile, functionPartName);
			boolean isContainerContextDependent = functionAST.isContainerContextDependent();
			IProblemRequestor pRequestor = problemRequestorFactory.getContainerContextTopLevelProblemRequestor(functionDeclaringFile,contextPartName,isContainerContextDependent);
			processedFunctionInfos.add(new TopLevelFunctionInfo(functionAST,functionDeclaringFile.getAbsolutePath(), pRequestor));
			IPartBinding functionBinding = new BindingCreator(environment, functionPackageName, contextSpecificCaseSensitiveInternedFunctionName, functionAST).getPartBinding();
			functionBinding.setEnvironment(environment);
			
			Scope scope = createScope(functionPackageName, contextPackageName, contextPartName, functionDeclaringFile, isContainerContextDependent);
			
			Compiler.getInstance().compileTopLevelFunction(functionAST, functionBinding, scope, containerContext, dependencyInfo, pRequestor, compilerOptions);
			
			for (Iterator iter = dependencyInfo.getTopLevelFunctions().iterator(); iter.hasNext();) {
				IPartBinding function = (IPartBinding) iter.next();
				addPart(function);
			}
			
			return functionAST;

	}

	private Scope createScope(String[] functionPackageName, String[] contextPackageName, String contextPartName, File functionDeclaringFile, boolean isContainerContextDependent) {
		Scope fileScope;
		if(isContainerContextDependent){
			String fileName = Util.getFilePartName(SourcePathInfo.getInstance().getDeclaringFile(contextPackageName, contextPartName));
			IPartBinding fileBinding = environment.getPartBinding(contextPackageName, fileName);
			fileScope = new FileScope(new EnvironmentScope(environment, dependencyInfo), (FileBinding)fileBinding, dependencyInfo);
		}else{
			String fileName = Util.getFilePartName(functionDeclaringFile);
			IPartBinding fileBinding = environment.getPartBinding(functionPackageName, fileName);
			fileScope = new FileScope(new EnvironmentScope(environment, dependencyInfo), (FileBinding)fileBinding, dependencyInfo);	
			dependencyInfo.recordTypeBinding(fileBinding);			
		}
		Scope scope = new FunctionContainerScope(new SystemScope(fileScope, environment.getSystemEnvironment()), functionContainerScope);
		return scope;
	}
	

}
