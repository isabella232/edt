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
package org.eclipse.edt.ide.core.internal.compiler.workingcopy;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.builder.AbstractProcessingQueue;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.*;
import org.eclipse.edt.compiler.internal.util.TopLevelFunctionInfo;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.compiler.Binder;
import org.eclipse.edt.ide.core.internal.compiler.Compiler;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.core.internal.dependency.AbstractDependencyInfo;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyBuildNotifier;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectBuildPathEntryManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectInfoManager;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.Environment;

public class WorkingCopyProcessingQueue extends AbstractProcessingQueue {

	private IProject project;
	private IProblemRequestorFactory problemRequestorFactory;
	private IWorkingCopyCompileRequestor requestor;

	private WorkingCopyProjectEnvironment projectEnvironment;
	private WorkingCopyProjectInfo projectInfo;
	
	// Need to pop the environment if it got pushed. Will only be false if an error was thrown before we pushed.
	private boolean pushedEnvironment;
	
	private class WorkingCopyDependencyInfo extends AbstractDependencyInfo {

		protected void recordQualifiedName(String[] strings) {
			// noop			
		}

		public Set getQualifiedNames() {
			// noop
			return Collections.EMPTY_SET;
		}

		public Set getSimpleNames() {
			// noop
			return Collections.EMPTY_SET;
		}

		public void recordBinding(IBinding binding) {
			// noop			
		}

		public void recordName(Name name) {
			// noop			
		}

		public void recordPackageBinding(IPackageBinding binding) {
			// noop			
		}

		public void recordSimpleName(String simpleName) {
			// noop			
		}

		public void recordTypeBinding(ITypeBinding binding) {
			// noop			
		}		
	}
	
	public WorkingCopyProcessingQueue(IProject project, IProblemRequestorFactory problemRequestorFactory){
		super(WorkingCopyBuildNotifier.getInstance(), DefaultCompilerOptions.getInstance());
		
		this.project = project;
		this.problemRequestorFactory = problemRequestorFactory;
		
        WorkingCopyProjectBuildPathEntry entry = WorkingCopyProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project);
        entry.setProcessingQueue(this);
        
        this.projectEnvironment = WorkingCopyProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
        this.projectEnvironment.getIREnvironment().initSystemEnvironment(this.projectEnvironment.getSystemEnvironment());
		this.projectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project);
		
		Environment.pushEnv(this.projectEnvironment.getIREnvironment());
		this.pushedEnvironment = true;
	}
	
	public boolean pushedEnvironment() {
		return this.pushedEnvironment;
	}

	public void setCompileRequestor(IWorkingCopyCompileRequestor requestor){
		this.requestor = requestor;
	}
	
	protected boolean hasExceededMaxLoop() {
		return false;
	}

	protected IPartBinding level03Compile(String[] packageName, String caseSensitiveInternedPartName) {

		String caseInsensitiveInternedPartName = InternUtil.intern(caseSensitiveInternedPartName);
		IFile declaringFile = projectInfo.getPartOrigin(packageName, caseInsensitiveInternedPartName).getEGLFile();
		Node partAST = WorkingCopyASTManager.getInstance().getAST(declaringFile, caseInsensitiveInternedPartName);
		
		IPartBinding binding = new BindingCreator(projectEnvironment, packageName, caseSensitiveInternedPartName, partAST).getPartBinding();
		binding.setEnvironment(projectEnvironment);
      
		AbstractDependencyInfo dependencyInfo = new WorkingCopyDependencyInfo();
		Scope scope = createScope(packageName, declaringFile, binding, dependencyInfo);
		
		IProblemRequestor problemRequestor = createProblemRequestor(caseInsensitiveInternedPartName, declaringFile, partAST, binding);
		if(problemRequestor != NullProblemRequestor.getInstance()){
			Compiler.getInstance().compilePart(partAST, binding, scope, dependencyInfo, problemRequestor, compilerOptions);
			
			if(binding.getKind() == ITypeBinding.FILE_BINDING){
				validatePackageDeclaration(packageName, declaringFile, partAST, ((FileBinding)binding), problemRequestor);
			}
		}else{
			Binder.getInstance().bindPart(partAST, binding, scope, dependencyInfo, problemRequestor, compilerOptions);
		}
		
		TopLevelFunctionInfo[] topLevelFunctions = new TopLevelFunctionInfo[0];
		if(dependencyInfo.getFunctionContainerScope() != null){
			topLevelFunctions = processTopLevelFunctions(dependencyInfo.getTopLevelFunctions(), dependencyInfo.getFunctionContainerScope(), dependencyInfo);
		}
		  
		// Post result
		requestor.acceptResult(new WorkingCopyCompilationResult(partAST,binding,declaringFile,topLevelFunctions ));
		
		WorkingCopyASTManager.getInstance().reportNestedFunctions(partAST,declaringFile);
		return binding;
	}

	protected IPartBinding level02Compile(String[] packageName, String caseSensitiveInternedPartName) {
		return WorkingCopyProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project).compileLevel2Binding(packageName, caseSensitiveInternedPartName);
	}

	protected IPartBinding level01Compile(String[] packageName, String caseSensitiveInternedPartName) {
		  return projectEnvironment.level01Compile(packageName, caseSensitiveInternedPartName);
	}

	protected IPartBinding getPartBindingFromCache(String[] packageName, String partName) {
		return WorkingCopyProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project).getPartBindingFromCache(packageName, partName);
	}
	
	private Scope createScope(String[] packageName, IFile declaringFile, IPartBinding binding, AbstractDependencyInfo dependencyInfo) {
		Scope scope;
		if(binding.getKind() == ITypeBinding.FILE_BINDING){
			scope = new EnvironmentScope(projectEnvironment, dependencyInfo);
		}else{
			String fileName = Util.getFilePartName(declaringFile);
			IPartBinding fileBinding = projectEnvironment.getPartBinding(packageName, fileName);
			scope = new SystemScope(new FileScope(new EnvironmentScope(projectEnvironment, dependencyInfo), (FileBinding)fileBinding, dependencyInfo),SystemEnvironmentManager.findSystemEnvironment(project, null));
		}
		return scope;
	}
	
	private TopLevelFunctionInfo[] processTopLevelFunctions(Set topLevelFunctions, FunctionContainerScope contextScope, AbstractDependencyInfo dependencyInfo){
	    WorkingCopyTopLevelFunctionProcessingQueue queue = new WorkingCopyTopLevelFunctionProcessingQueue(project, contextScope, dependencyInfo, compilerOptions, problemRequestorFactory);
		for (Iterator iter = topLevelFunctions.iterator(); iter.hasNext();) {
			IPartBinding function = (IPartBinding) iter.next();
			queue.addPart(function);			
		}
		
		queue.process();
		
		return queue.getTopLevelFunctionInfos();
	}

	protected void doAddPart(String[] packageName, String caseInsensitiveInternedPartName) {
		addPart(packageName, projectInfo.getCaseSensitivePartName(packageName, caseInsensitiveInternedPartName));		
	}

	private IProblemRequestor createProblemRequestor(String partName, IFile file, Node partAST, IPartBinding binding) {
		IProblemRequestor newRequestor;
		if(binding.getKind() == ITypeBinding.FUNCTION_BINDING){
			newRequestor = problemRequestorFactory.getGenericTopLevelFunctionProblemRequestor(file, partName, ((TopLevelFunction)partAST).isContainerContextDependent());
		}else{
			 newRequestor = problemRequestorFactory.getProblemRequestor(file, partName);
		}
		return newRequestor;
	}
	
	private void validatePackageDeclaration(String[] packageName, IFile declaringFile, Node partAST, FileBinding binding, IProblemRequestor problemRequestor) {
		try{
		    IPackageBinding declaringPackage = binding.getDeclaringPackage();
		
			if(declaringPackage != null && declaringPackage.getPackageName() != packageName){
				if(packageName.length == 0){
					// package name specified in default package
					problemRequestor.acceptProblem(((File)partAST).getPackageDeclaration(), IProblemRequestor.PACKAGE_NAME_DOESNT_MATCH_DIRECTORY_STRUCTURE, new String[0]);
				}else if(((File)partAST).hasPackageDeclaration()){
					// incorrect package declaration
					problemRequestor.acceptProblem(((File)partAST).getPackageDeclaration(), IProblemRequestor.PACKAGE_NAME_DOESNT_MATCH_DIRECTORY_STRUCTURE, new String[0]);
				}else{
					// missing package declaration
					IPath packagePath = declaringFile.getProjectRelativePath().removeFileExtension().removeLastSegments(1);
					packagePath = packagePath.removeFirstSegments(packagePath.segmentCount() - packageName.length);
					problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.PACKAGE_NAME_NOT_PROVIDED, new String[]{packagePath.toString().replace(IPath.SEPARATOR, '.')});
				}
			}else{
				// package declaration must match package name exactly (case sensitive)
				if(((File)partAST).hasPackageDeclaration()){
					String packageDeclName = ((File)partAST).getPackageDeclaration().getName().getCanonicalName();
					// get package path, minus source folder
					IPath packagePath = declaringFile.getProjectRelativePath().removeFileExtension().removeLastSegments(1);
					packagePath = packagePath.removeFirstSegments(packagePath.segmentCount() - packageName.length);
					if(!packageDeclName.equals(packagePath.toString().replace(IPath.SEPARATOR, IEGLConstants.PACKAGE_SEPARATOR.charAt(0)))){
						//package name does not match case of package on file system
						problemRequestor.acceptProblem(((File)partAST).getPackageDeclaration(), IProblemRequestor.PACKAGE_NAME_DOESNT_MATCH_DIRECTORY_STRUCTURE, IMarker.SEVERITY_ERROR, new String[0]);
					}
				}
			}
		}catch(RuntimeException e){
		    problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.COMPILATION_EXCEPTION, new String[]{binding.getName()});
		    EDTCoreIDEPlugin.getPlugin().log("Part Validation Failure", e);  //$NON-NLS-1$
		}
	}
}
