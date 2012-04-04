/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.compiler.workingcopy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.builder.AccumulatingProblemrRequestor;
import org.eclipse.edt.compiler.internal.core.builder.FunctionContainerContextTopLevelFunctionProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.compiler.internal.core.lookup.EnvironmentScope;
import org.eclipse.edt.compiler.internal.core.lookup.FileScope;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.SystemScope;
import org.eclipse.edt.compiler.internal.util.TopLevelFunctionInfo;
import org.eclipse.edt.ide.core.internal.compiler.Binder;
import org.eclipse.edt.ide.core.internal.compiler.Compiler;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.core.internal.dependency.AbstractDependencyInfo;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectInfoManager;
import org.eclipse.edt.ide.core.internal.model.ClassFileElementInfo;
import org.eclipse.edt.ide.core.internal.model.EGLElement;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.internal.search.IFilePartInfo;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;

public class WorkingCopyTopLevelFunctionProcessingQueue {
	private LinkedHashMap pendingUnits = new LinkedHashMap();
	private IPartBinding functionBeingProcessed;
	private HashSet processedUnits = new HashSet();
	private ArrayList topLevelFunctionInfos = new ArrayList();
	private WorkingCopyProjectEnvironment contextProjectEnvrionment;
	private FunctionContainerScope functionContainerScope;
	private AbstractDependencyInfo dependencyInfo;
	private WorkingCopyProjectInfo contextProjectInfo;
	private IPartBinding containerContext;
    private ICompilerOptions compilerOptions;
	private IProblemRequestorFactory problemRequestorFactory;

    
	private class WorkingCopyTopLevelFunctionProcessingUnit {
	 	 IPartBinding function;
    	 
    	 String contextSpecificCaseSensitiveInternedFunctionName;
		 
    	      
    	 WorkingCopyTopLevelFunctionProcessingUnit(IPartBinding function) {
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
    		
    		return contextSpecificFunctionNameBuffer.toString();
		}
     }
	 
	public class WorkingCopyTopLevelFunctionProcessingUnitKey{
	 	 IPartBinding function;
    	
    	public WorkingCopyTopLevelFunctionProcessingUnitKey(IPartBinding function){
    		this.function = function;
    	}
    	
    	public boolean equals(Object otherObject){
    		if(this == otherObject){
    			return true;
    		}
    		if(otherObject instanceof WorkingCopyTopLevelFunctionProcessingUnitKey){
    			WorkingCopyTopLevelFunctionProcessingUnitKey otherPUKey = (WorkingCopyTopLevelFunctionProcessingUnitKey)otherObject;
    			return otherPUKey.function == function;
    		}
    		return false;
    	}
    	
    	public int hashCode(){
    		return function.getName().hashCode();
    	}
    }
	
	public WorkingCopyTopLevelFunctionProcessingQueue(IProject project, FunctionContainerScope functionContainerScope, AbstractDependencyInfo dependencyInfo,ICompilerOptions compilerOptions, IProblemRequestorFactory problemRequestorFactory) {
        super();
        this.functionContainerScope = functionContainerScope;
        this.dependencyInfo = dependencyInfo;
        this.compilerOptions = compilerOptions;
        this.problemRequestorFactory = problemRequestorFactory;
        
        this.contextProjectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project);
        this.contextProjectEnvrionment= WorkingCopyProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
        this.containerContext = functionContainerScope.getPartBinding();
    }
	 
	 /**
	  * Only add a top level function if it is not already somewhere in this queue
	  */
	 protected void addPart(IPartBinding function){
	 	if(functionBeingProcessed != function && !processedUnits.contains(new WorkingCopyTopLevelFunctionProcessingUnitKey(function))){
	 		pendingUnits.put(new WorkingCopyTopLevelFunctionProcessingUnitKey(function), new WorkingCopyTopLevelFunctionProcessingUnit(function));
	 	}
	 }
	 
	 public void process() {
        while(!pendingUnits.isEmpty()) {
            Iterator iterator = pendingUnits.values().iterator();
            WorkingCopyTopLevelFunctionProcessingUnit processingUnit = (WorkingCopyTopLevelFunctionProcessingUnit) iterator.next();
            process(processingUnit);
            
        }
     }
    
    public TopLevelFunctionInfo[] getTopLevelFunctionInfos() {
		return (TopLevelFunctionInfo[])topLevelFunctionInfos.toArray(new TopLevelFunctionInfo[topLevelFunctionInfos.size()]);
	}

	private void process(WorkingCopyTopLevelFunctionProcessingUnit processingUnit) {
    	pendingUnits.remove(new WorkingCopyTopLevelFunctionProcessingUnitKey(processingUnit.function));
        functionBeingProcessed = processingUnit.function;
        
        compile(processingUnit);
        
        functionBeingProcessed = null;
        processedUnits.add(new WorkingCopyTopLevelFunctionProcessingUnitKey(processingUnit.function));
    }
	 
    private void compile(WorkingCopyTopLevelFunctionProcessingUnit functionUnit) {
		 
		if (functionUnit.function.getEnvironment() instanceof WorkingCopyProjectEnvironment) {
			compileUsingProjectEnvironment(functionUnit);
			return;
		}		
	}

	 private void compileUsingProjectEnvironmentWithEglar(WorkingCopyTopLevelFunctionProcessingUnit functionUnit) {
		 	WorkingCopyProjectEnvironment functionProjectEnv = (WorkingCopyProjectEnvironment)functionUnit.function.getEnvironment();
		 	IProject functionProject = functionProjectEnv.getProject();
			String[] functionPackageName = functionUnit.function.getPackageName();
			String functionPackageNameString = asString(functionPackageName);
			String functionPartName = functionUnit.function.getName();
			String[] contextPackageName = containerContext.getPackageName();
			String contextPartName = containerContext.getName();
			String contextSpecificCaseSensitiveInternedFunctionName = functionUnit.contextSpecificCaseSensitiveInternedFunctionName;
				
			// compile the top level function within the context of the part stored in the processing unit
			// get declaring file from other project
			
			IClassFile classFile = getClassFile(functionProject, functionPackageNameString, functionPartName);			
			String functionDeclaringFileSource = null;
			
			try {
				functionDeclaringFileSource = classFile.getSource();
			} catch (EGLModelException e) {
			}
						
			String fileAstKey = getFileAstKey(classFile, functionProject, functionPackageNameString);
			
			File fileAST  = WorkingCopyASTManager.getInstance().getFileAST(fileAstKey, functionDeclaringFileSource);
			TopLevelFunction functionAST  = (TopLevelFunction)WorkingCopyASTManager.getInstance().getPartAST(fileAST, null, functionPartName);
						
			boolean isContainerContextDependent = functionAST.isContainerContextDependent();
			AccumulatingProblemrRequestor accumulationProblemRequestor = new AccumulatingProblemrRequestor();
			IProblemRequestor functionProbReq = new FunctionContainerContextTopLevelFunctionProblemRequestor(accumulationProblemRequestor, isContainerContextDependent);
			
			topLevelFunctionInfos.add(new TopLevelFunctionInfo(functionAST, fileAstKey, functionProbReq, classFile));;
			IPartBinding functionBinding = new BindingCreator(contextProjectEnvrionment, functionPackageName, contextSpecificCaseSensitiveInternedFunctionName, functionAST).getPartBinding();
			functionBinding.setEnvironment(contextProjectEnvrionment);
			
			Scope scope = createScope(functionProjectEnv, functionPackageName, contextPackageName, contextPartName, fileAST, fileAstKey, isContainerContextDependent);
			IFile file = contextProjectInfo.getPartOrigin(contextPackageName, contextPartName).getEGLFile();
			
			IProblemRequestor contextReq = problemRequestorFactory.getFileProblemRequestor(file);
			
			if(contextReq != null && contextReq != NullProblemRequestor.getInstance()){
				Compiler.getInstance().compilePart(functionAST, functionBinding, scope, dependencyInfo, functionProbReq, compilerOptions);
				
				//if we found a compile error on the TLF when compiling in the context of the container, add an error to the context's problem requestor
				if (functionProbReq.hasError()) {					
					contextReq.acceptProblem(getContextAst(file, contextPartName).getName(), IProblemRequestor.VALIDATION_ERROR_COMPILING_BINARY_FUNCTION, new String[]{buildPartName(functionPackageName, functionPartName), contextPartName});
				}
				
			}else{
				Binder.getInstance().bindPart(functionAST, functionBinding, scope, dependencyInfo, functionProbReq, compilerOptions);
			}
	
			IProblemRequestor problemRequestor = problemRequestorFactory.getContainerContextTopLevelProblemRequestor(null, functionPartName, contextPartName, contextProjectInfo.getPartOrigin(containerContext.getPackageName(), containerContext.getName()).getEGLFile().getFullPath(), isContainerContextDependent);
			if (problemRequestor != null && problemRequestor != NullProblemRequestor.getInstance()){
				//add any problems to the correct problem requestor
				Iterator i = accumulationProblemRequestor.getProblems().iterator();
				while(i.hasNext()) {
					Problem prob = (Problem)i.next();
					problemRequestor.acceptProblem(prob.getStartOffset(), prob.getEndOffset(), prob.getSeverity(), prob.getProblemKind(), prob.getInserts());
				}
			}
			for (Iterator iter = dependencyInfo.getTopLevelFunctions().iterator(); iter.hasNext();) {
				IPartBinding function = (IPartBinding) iter.next();
				addPart(function);
			}
			
		}
	
	 private void compileUsingProjectEnvironment(WorkingCopyTopLevelFunctionProcessingUnit functionUnit) {
	 	IProject functionProject = ((WorkingCopyProjectEnvironment)functionUnit.function.getEnvironment()).getProject();
		String[] functionPackageName = functionUnit.function.getPackageName();
		String functionPartName = functionUnit.function.getName();
		String[] contextPackageName = containerContext.getPackageName();
		String contextPartName = containerContext.getName();
		String contextSpecificCaseSensitiveInternedFunctionName = functionUnit.contextSpecificCaseSensitiveInternedFunctionName;
			
		// compile the top level function within the context of the part stored in the processing unit
		// get declaring file from other project
		
		IPartOrigin origin = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(functionProject).getPartOrigin(functionPackageName, functionPartName);
		if (origin == null || origin.getEGLFile() == null) {
			compileUsingProjectEnvironmentWithEglar(functionUnit);
			return;
		}

		IFile functionDeclaringFile = origin.getEGLFile();
		
		TopLevelFunction functionAST  = (TopLevelFunction)WorkingCopyASTManager.getInstance().getPartAST(functionDeclaringFile, functionPartName);
		
		boolean isContainerContextDependent = functionAST.isContainerContextDependent();
		IProblemRequestor problemRequestor = problemRequestorFactory.getContainerContextTopLevelProblemRequestor(functionDeclaringFile, functionPartName, contextPartName, contextProjectInfo.getPartOrigin(containerContext.getPackageName(), containerContext.getName()).getEGLFile().getFullPath(), isContainerContextDependent);
		topLevelFunctionInfos.add(new TopLevelFunctionInfo(functionAST,functionDeclaringFile.getFullPath().toString(), problemRequestor));;
		IPartBinding functionBinding = new BindingCreator(contextProjectEnvrionment, functionPackageName, contextSpecificCaseSensitiveInternedFunctionName, functionAST).getPartBinding();
		functionBinding.setEnvironment(contextProjectEnvrionment);
		
		Scope scope = createScope(functionProject, functionPackageName, contextPackageName, contextPartName, functionDeclaringFile, isContainerContextDependent);
		
		if(problemRequestor != NullProblemRequestor.getInstance()){
			Compiler.getInstance().compilePart(functionAST, functionBinding, scope, dependencyInfo, problemRequestor, compilerOptions);
		}else{
			Binder.getInstance().bindPart(functionAST, functionBinding, scope, dependencyInfo, problemRequestor, compilerOptions);
		}
				
		for (Iterator iter = dependencyInfo.getTopLevelFunctions().iterator(); iter.hasNext();) {
			IPartBinding function = (IPartBinding) iter.next();
			addPart(function);
		}
		
	}

	private Scope createScope(WorkingCopyProjectEnvironment env, String[] functionPackageName, String[] contextPackageName, String contextPartName, File fileAST, String fileNameKey, boolean isContainerContextDependent) {
		Scope fileScope;
		if(isContainerContextDependent){
			String fileName = Util.getFilePartName(contextProjectInfo.getPartOrigin(contextPackageName, contextPartName).getEGLFile());
			IPartBinding fileBinding = contextProjectEnvrionment.getPartBinding(contextPackageName, fileName);
			fileScope = new FileScope(new EnvironmentScope(contextProjectEnvrionment, dependencyInfo), (FileBinding)fileBinding, dependencyInfo);
		}else{
			IPartBinding fileBinding = env.getDeclaringProjectBuildPathEntry().getFileBinding(functionPackageName, fileNameKey, fileAST);
			fileScope = new FileScope(new EnvironmentScope(env, dependencyInfo), (FileBinding)fileBinding, dependencyInfo);	
			dependencyInfo.recordTypeBinding(fileBinding);			
		}
		Scope scope = new FunctionContainerScope(new SystemScope(fileScope,SystemEnvironmentManager.findSystemEnvironment(env.getProject(), null)), functionContainerScope);
		return scope;
	}
	 
	private Scope createScope(IProject functionProject, String[] functionPackageName, String[] contextPackageName, String contextPartName, IFile functionDeclaringFile, boolean isContainerContextDependent) {
		Scope fileScope;
		if(isContainerContextDependent){
			String fileName = Util.getFilePartName(contextProjectInfo.getPartOrigin(contextPackageName, contextPartName).getEGLFile());
			IPartBinding fileBinding = contextProjectEnvrionment.getPartBinding(contextPackageName, fileName);
			fileScope = new FileScope(new EnvironmentScope(contextProjectEnvrionment, dependencyInfo), (FileBinding)fileBinding, dependencyInfo);
		}else{
			String fileName = Util.getFilePartName(functionDeclaringFile);
			WorkingCopyProjectEnvironment functionProjectEnvironment = WorkingCopyProjectEnvironmentManager.getInstance().getProjectEnvironment(functionProject);
			IPartBinding fileBinding = functionProjectEnvironment.getPartBinding(functionPackageName, fileName);
			fileScope = new FileScope(new EnvironmentScope(functionProjectEnvironment, dependencyInfo), (FileBinding)fileBinding, dependencyInfo);	
			dependencyInfo.recordTypeBinding(fileBinding);			
		}
		Scope scope = new FunctionContainerScope(new SystemScope(fileScope,SystemEnvironmentManager.findSystemEnvironment(functionProject, null)), functionContainerScope);
		return scope;
	}

	private String asString(String[] arr) {
		if (arr == null) {
			return null;
		}
		
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				buffer.append(".");
			}
			buffer.append(arr[i]);
		}
		return buffer.toString();
	}

	private IClassFile getClassFile(IProject project, String packageName, String partName) {
		String classFileName = partName.toLowerCase() + ".ir";
		return IFilePartInfo.getClassFile(project, packageName, IFilePartInfo.EGLAR_FILE, classFileName);
	}
	
	private String getFileAstKey(IClassFile classFile, IProject project, String packageName) {
		
		if (classFile instanceof EGLElement) {
			
			try {
				Object info  = ((EGLElement) classFile).getElementInfo();
				if (info instanceof ClassFileElementInfo) {
					IPath path = classFile.getPath();

					return path.toString() + ":" + project.getName() + "/" + packageName + "/" + ((ClassFileElementInfo)info).getEglFileName();
				}
			} catch (EGLModelException e) {
			}
		}
		
		return classFile.toString();

	}
	
	private Part getContextAst(IFile containerFile, String contextPartName) {
		return WorkingCopyASTManager.getInstance().getPartAST(containerFile, contextPartName);
	}

	private String buildPartName(String[] pkg, String part) {
		if (pkg == null || pkg.length == 0) {
			return part;
		}
		
		return asString(pkg) + "." + part;
	}
	
}
