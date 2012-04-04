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
package org.eclipse.edt.ide.core.internal.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.builder.AccumulatingProblemrRequestor;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.CappedProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.FunctionContainerContextTopLevelFunctionProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;
import org.eclipse.edt.compiler.internal.core.lookup.EnvironmentScope;
import org.eclipse.edt.compiler.internal.core.lookup.FileScope;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.SystemScope;
import org.eclipse.edt.compiler.internal.util.TopLevelFunctionInfo;
import org.eclipse.edt.ide.core.internal.compiler.Compiler;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.core.internal.dependency.DependencyInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;
import org.eclipse.edt.ide.core.internal.model.ClassFile;
import org.eclipse.edt.ide.core.internal.model.ClassFileElementInfo;
import org.eclipse.edt.ide.core.internal.model.EGLElement;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.internal.search.IFilePartInfo;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;

/**
 * @author svihovec
 *
 */
public class TopLevelFunctionProcessingQueue {

	private LinkedHashMap pendingUnits = new LinkedHashMap();
	private IPartBinding functionBeingProcessed;
	private HashSet processedUnits = new HashSet();
	private List processedFunctionInfos = new ArrayList();
	
	private ProjectEnvironment contextProjectEnvrionment;
	private FunctionContainerScope functionContainerScope;
	private DependencyInfo dependencyInfo;
	private ProjectInfo contextProjectInfo;
	private IPartBinding containerContext;
    private CappedProblemRequestor cappedProblemRequestor;
    private ICompilerOptions compilerOptions;
    
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
	 
	public class TopLevelFunctionProcessingUnitKey{
	 	 IPartBinding function;
    	
    	public TopLevelFunctionProcessingUnitKey(IPartBinding function){
    		this.function = function;
    	}
    	
    	public boolean equals(Object otherObject){
    		if(this == otherObject){
    			return true;
    		}
    		if(otherObject instanceof TopLevelFunctionProcessingUnitKey){
    			TopLevelFunctionProcessingUnitKey otherPUKey = (TopLevelFunctionProcessingUnitKey)otherObject;
    			return otherPUKey.function == function;
    		}
    		return false;
    	}
    	
    	public int hashCode(){
    		return function.getName().hashCode();
    	}
    }
	
	public TopLevelFunctionProcessingQueue(IProject project, FunctionContainerScope functionContainerScope, DependencyInfo dependencyInfo, CappedProblemRequestor cappedProblemRequestor, ICompilerOptions compilerOptions) {
        super();
        this.functionContainerScope = functionContainerScope;
        this.dependencyInfo = dependencyInfo;
        this.cappedProblemRequestor = cappedProblemRequestor;
        this.compilerOptions = compilerOptions;
        
        this.contextProjectInfo = ProjectInfoManager.getInstance().getProjectInfo(project);
        this.contextProjectEnvrionment= ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
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
	 
    private void compileUsingProjectEnvironment(TopLevelFunctionProcessingUnit functionUnit) {
		 
	 	IProject functionProject = ((ProjectEnvironment)functionUnit.function.getEnvironment()).getProject();
		String[] functionPackageName = functionUnit.function.getPackageName();
		String functionPartName = functionUnit.function.getName();
		String[] contextPackageName = containerContext.getPackageName();
		String contextPartName = containerContext.getName();
		String contextSpecificCaseSensitiveInternedFunctionName = functionUnit.contextSpecificCaseSensitiveInternedFunctionName;
			
		if(Builder.DEBUG){
		    System.out.println("\nProcessing Top Level Function: " + contextSpecificCaseSensitiveInternedFunctionName); //$NON-NLS-1$
		}
		
		// compile the top level function within the context of the part stored in the processing unit
		// get declaring file from other project
		
		IPartOrigin origin = ProjectInfoManager.getInstance().getProjectInfo(functionProject).getPartOrigin(functionPackageName, functionPartName);
		if (origin == null || origin.getEGLFile() == null) {
			compileUsingProjectEnvironmentWithEglar(functionUnit);
			return;
		}
		
		IFile functionDeclaringFile = origin.getEGLFile();
		
		if (!functionDeclaringFile.exists()){
			throw new BuildException("Unable to compile top level function " + functionUnit.function.getPackageQualifiedName() + " : EGL source code not be found.");
		}
		
		TopLevelFunction functionAST  = (TopLevelFunction)ASTManager.getInstance().getPartAST(functionDeclaringFile, functionPartName);

		boolean isContainerContextDependent = functionAST.isContainerContextDependent();
		IProblemRequestor functionProbReq = new FunctionContainerContextTopLevelFunctionProblemRequestor(new ContextSpecificMarkerProblemRequestor(functionDeclaringFile, functionPartName, contextPartName, contextProjectInfo.getPartOrigin(containerContext.getPackageName(), containerContext.getName()).getEGLFile().getFullPath()), isContainerContextDependent);
		processedFunctionInfos.add(new TopLevelFunctionInfo(functionAST,functionDeclaringFile.getFullPath().toString(), functionProbReq));
		IPartBinding functionBinding = new BindingCreator(contextProjectEnvrionment, functionPackageName, contextSpecificCaseSensitiveInternedFunctionName, functionAST).getPartBinding();
		functionBinding.setEnvironment(contextProjectEnvrionment);
		
		Scope scope = createScope(functionProject, functionPackageName, contextPackageName, contextPartName, functionDeclaringFile, isContainerContextDependent);
		
		IProblemRequestor saveReq = cappedProblemRequestor.getRequestor();
		cappedProblemRequestor.setRequestor(functionProbReq);
		Compiler.getInstance().compileTopLevelFunction(functionAST, functionBinding, scope, containerContext, dependencyInfo, cappedProblemRequestor, compilerOptions);
		
		for (Iterator iter = dependencyInfo.getTopLevelFunctions().iterator(); iter.hasNext();) {
			IPartBinding function = (IPartBinding) iter.next();
			addPart(function);
		}
		cappedProblemRequestor.setRequestor(saveReq);
		
		if(Builder.DEBUG){
		    System.out.println("Finished Processing: " + contextSpecificCaseSensitiveInternedFunctionName + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

    private void compileUsingProjectEnvironmentWithEglar(TopLevelFunctionProcessingUnit functionUnit) {
		
    	ProjectEnvironment functionProjectEnv = (ProjectEnvironment)functionUnit.function.getEnvironment();
	 	IProject functionProject = functionProjectEnv.getProject();
		String[] functionPackageName = functionUnit.function.getPackageName();
		String functionPackageNameString = asString(functionPackageName);
		String functionPartName = functionUnit.function.getName();
		String[] contextPackageName = containerContext.getPackageName();
		String contextPartName = containerContext.getName();
		String contextSpecificCaseSensitiveInternedFunctionName = functionUnit.contextSpecificCaseSensitiveInternedFunctionName;
			
		if(Builder.DEBUG){
		    System.out.println("\nProcessing Top Level Function: " + contextSpecificCaseSensitiveInternedFunctionName); //$NON-NLS-1$
		}
		
		// compile the top level function within the context of the part stored in the processing unit
		// get declaring file from other project
		
		IClassFile classFile = getClassFile(functionProject, functionPackageNameString, functionPartName);			
		String functionDeclaringFileSource = null;
		
		try {
			functionDeclaringFileSource = classFile.getSource();
		} catch (EGLModelException e) {
		}
		
		if (functionDeclaringFileSource == null || functionDeclaringFileSource.length() == 0){
			throw new BuildException("Unable to compile top level function " + functionUnit.function.getPackageQualifiedName() + " : EGL source code not be found.");
		}
		
		String fileAstKey = getFileAstKey(classFile, functionProject, functionPackageNameString);
		
		File fileAST  = ASTManager.getInstance().getFileAST(fileAstKey, functionDeclaringFileSource);
		TopLevelFunction functionAST  = (TopLevelFunction)ASTManager.getInstance().getPartAST(fileAST, null, functionPartName);
		boolean isContainerContextDependent = functionAST.isContainerContextDependent();
		
		AccumulatingProblemrRequestor accumulationProblemRequestor = new AccumulatingProblemrRequestor();
		IProblemRequestor functionProbReq = new FunctionContainerContextTopLevelFunctionProblemRequestor(accumulationProblemRequestor, isContainerContextDependent);
		
		//TODO will eventually need to set a more accurate filename in the functionInfo
		processedFunctionInfos.add(new TopLevelFunctionInfo(functionAST, fileAstKey, functionProbReq, classFile));
		IPartBinding functionBinding = new BindingCreator(contextProjectEnvrionment, functionPackageName, contextSpecificCaseSensitiveInternedFunctionName, functionAST).getPartBinding();
		functionBinding.setEnvironment(contextProjectEnvrionment);
		
		
		
		Scope scope = createScope(functionProjectEnv, functionPackageName, contextPackageName, contextPartName, fileAST, fileAstKey, isContainerContextDependent);
		
		IProblemRequestor saveReq = cappedProblemRequestor.getRequestor();
		cappedProblemRequestor.setRequestor(functionProbReq);
		Compiler.getInstance().compileTopLevelFunction(functionAST, functionBinding, scope, containerContext, dependencyInfo, cappedProblemRequestor, compilerOptions);
		
		if (functionProbReq.hasError()) {
			
			MarkerProblemRequestor contextReq = createContextProblemRequestor(contextPackageName, contextPartName);
			cappedProblemRequestor.setRequestor(contextReq);
			cappedProblemRequestor.acceptProblem(getContextAst(contextReq.file, contextPartName).getName(), IProblemRequestor.VALIDATION_ERROR_COMPILING_BINARY_FUNCTION, new String[]{buildPartName(functionPackageName, functionPartName), contextPartName});
		}
		
		for (Iterator iter = dependencyInfo.getTopLevelFunctions().iterator(); iter.hasNext();) {
			IPartBinding function = (IPartBinding) iter.next();
			addPart(function);
		}
		cappedProblemRequestor.setRequestor(saveReq);
		
		if(Builder.DEBUG){
		    System.out.println("Finished Processing: " + contextSpecificCaseSensitiveInternedFunctionName + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
    
    private void compile(TopLevelFunctionProcessingUnit functionUnit) {
		 
		if (functionUnit.function.getEnvironment() instanceof ProjectEnvironment) {
			compileUsingProjectEnvironment(functionUnit);
			return;
		}
		
		throw new BuildException("Unable to compile top level function " + functionUnit.function.getPackageQualifiedName() + " : EGL source code not be found.");
		
	}

	private Scope createScope(IProject functionProject, String[] functionPackageName, String[] contextPackageName, String contextPartName, IFile functionDeclaringFile, boolean isContainerContextDependent) {
		Scope fileScope;
		if(isContainerContextDependent){
			String fileName = org.eclipse.edt.ide.core.internal.utils.Util.getFilePartName(contextProjectInfo.getPartOrigin(contextPackageName, contextPartName).getEGLFile());
			IPartBinding fileBinding = contextProjectEnvrionment.getPartBinding(contextPackageName, fileName);
			fileScope = new FileScope(new EnvironmentScope(contextProjectEnvrionment, dependencyInfo), (FileBinding)fileBinding, dependencyInfo);
		}else{
			String fileName = org.eclipse.edt.ide.core.internal.utils.Util.getFilePartName(functionDeclaringFile);
			ProjectEnvironment functionProjectEnvironment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(functionProject);
			IPartBinding fileBinding = functionProjectEnvironment.getPartBinding(functionPackageName, fileName);
			fileScope = new FileScope(new EnvironmentScope(functionProjectEnvironment, dependencyInfo), (FileBinding)fileBinding, dependencyInfo);	
		}
		Scope scope = new FunctionContainerScope(new SystemScope(fileScope,SystemEnvironmentManager.findSystemEnvironment(functionProject, contextProjectEnvrionment.getDeclaringProjectBuildPathEntry().getNotifier())), functionContainerScope);
		return scope;
	}
	
	
	private Scope createScope(ProjectEnvironment env, String[] functionPackageName, String[] contextPackageName, String contextPartName, File fileAST, String fileNameKey, boolean isContainerContextDependent) {
		Scope fileScope;
		if(isContainerContextDependent){
			String fileName = org.eclipse.edt.ide.core.internal.utils.Util.getFilePartName(contextProjectInfo.getPartOrigin(contextPackageName, contextPartName).getEGLFile());
			IPartBinding fileBinding = contextProjectEnvrionment.getPartBinding(contextPackageName, fileName);
			fileScope = new FileScope(new EnvironmentScope(contextProjectEnvrionment, dependencyInfo), (FileBinding)fileBinding, dependencyInfo);
		}else{
			
			
			IPartBinding fileBinding = env.getDeclaringProjectBuildPathEntry().getFileBinding(functionPackageName, fileNameKey, fileAST);
			fileScope = new FileScope(new EnvironmentScope(env, dependencyInfo), (FileBinding)fileBinding, dependencyInfo);	
		}
		Scope scope = new FunctionContainerScope(new SystemScope(fileScope,SystemEnvironmentManager.findSystemEnvironment(env.getProject(), contextProjectEnvrionment.getDeclaringProjectBuildPathEntry().getNotifier())), functionContainerScope);
		return scope;
	}
	
	
	private String getFileAstKey(IClassFile classFile, IProject project, String packageName) {
		
		if (classFile instanceof ClassFile) {
			IFile file = ((ClassFile)classFile).getFileInSourceFolder();
			if (file != null) {
				return file.getFullPath().toString();
			}
		}
		
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
	
	private IClassFile getClassFile(IProject project, String packageName, String partName) {	
		String classFileName = partName.toLowerCase() + ".ir";
		return IFilePartInfo.getClassFile(project, packageName, IFilePartInfo.EGLAR_FILE, classFileName);
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
	
	private MarkerProblemRequestor createContextProblemRequestor(String[]  contextPackageName, String contextPartName) {		
		IFile file = contextProjectInfo.getPartOrigin(contextPackageName, contextPartName).getEGLFile();
		return new MarkerProblemRequestor(file, contextPartName, false);
	}
	
	private Part getContextAst(IFile containerFile, String contextPartName) {
		return ASTManager.getInstance().getPartAST(containerFile, contextPartName);
	}
	
	private String buildPartName(String[] pkg, String part) {
		if (pkg == null || pkg.length == 0) {
			return part;
		}
		
		return asString(pkg) + "." + part;
	}
}
