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
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.builder.CappedProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.GenericTopLevelFunctionProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.*;
import org.eclipse.edt.compiler.internal.egl2mof.Egl2Mof;
import org.eclipse.edt.compiler.internal.util.TopLevelFunctionInfo;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.compiler.Compiler;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.core.internal.dependency.DependencyGraph;
import org.eclipse.edt.ide.core.internal.dependency.DependencyGraphManager;
import org.eclipse.edt.ide.core.internal.dependency.DependencyInfo;
import org.eclipse.edt.ide.core.internal.dependency.IFunctionRequestor;
import org.eclipse.edt.ide.core.internal.generation.IDEContext;
import org.eclipse.edt.ide.core.internal.lookup.AbstractProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.FileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathEntryManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.osgi.util.NLS;

/**
 * @author svihovec
 *
 */
public abstract class AbstractProcessingQueue extends org.eclipse.edt.compiler.internal.core.builder.AbstractProcessingQueue {

	protected ProjectInfo projectInfo;
	
    private static final int MAX_COMPILE_LOOP = 5;
    private IProject project;
    private ProjectEnvironment projectEnvironment;
    private IProcessorRequestor requestor;
   
    public class TopLevelFunctionPartKey {
    	private String projectName;
	    private String[] packageName;
	    private String partName;
	    
	    public TopLevelFunctionPartKey(String projectName, String[] packageName, String partName){
	        this.projectName = projectName;
	        this.packageName = packageName;
	        this.partName = partName;
	    }
	    
	    public boolean equals(Object obj) {
	        if(this == obj){
	            return true;
	        }
	        
            if(obj instanceof TopLevelFunctionPartKey){
                return ((TopLevelFunctionPartKey)obj).projectName.equals(projectName) && ((TopLevelFunctionPartKey)obj).packageName == packageName && ((TopLevelFunctionPartKey)obj).partName == partName;
            }
            return false;
        }
	    
	    public int hashCode() {
            return partName.hashCode();
        }
	}
    
    public AbstractProcessingQueue(IProject project, IBuildNotifier notifier) {
    	super(notifier, DefaultCompilerOptions.getInstance());
        this.project = project;
        this.projectInfo = ProjectInfoManager.getInstance().getProjectInfo(project);
        this.projectEnvironment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
    }
    
    protected boolean hasExceededMaxLoop() {
        return compileLoop >= MAX_COMPILE_LOOP;
    }

    protected IPartBinding level03Compile(String[] packageName, String caseSensitiveInternedPartName) {
    	String caseInsensitiveInternedString = InternUtil.intern(caseSensitiveInternedPartName);
        String qualifiedName = getQualifiedName(packageName, caseInsensitiveInternedString);
		
		if(Builder.DEBUG){
		    System.out.println("\nProcessing: " + getQualifiedName(packageName, caseInsensitiveInternedString)); //$NON-NLS-1$
		}
		
		notifier.subTask(NLS.bind(BuilderResources.buildCompiling, qualifiedName));
		
		IFile declaringFile = projectInfo.getPartOrigin(packageName, caseInsensitiveInternedString).getEGLFile();
		Node partAST = ASTManager.getInstance().getAST(declaringFile, caseInsensitiveInternedString);
		
		IPartBinding binding = new BindingCreator(projectEnvironment, packageName, caseSensitiveInternedPartName, partAST).getPartBinding();
		binding.setEnvironment(projectEnvironment);
      
		DependencyInfo dependencyInfo = new DependencyInfo();
		Scope scope = createScope(packageName, declaringFile, binding, dependencyInfo);
		CappedProblemRequestor cappedProblemRequestor = new CappedProblemRequestor();
		cappedProblemRequestor.setRequestor(createProblemRequestor(caseInsensitiveInternedString, declaringFile, partAST, binding));
		
		Compiler.getInstance().compilePart(partAST, binding, scope, dependencyInfo, cappedProblemRequestor, compilerOptions);
		
		if(binding.getKind() == ITypeBinding.FILE_BINDING){
			validatePackageDeclaration(packageName, declaringFile, partAST, ((FileBinding)binding), cappedProblemRequestor);
		}
		
		if(binding.getKind() != ITypeBinding.FILE_BINDING){
	        processCompiledPart(packageName, caseInsensitiveInternedString, qualifiedName, (Part)partAST, binding, declaringFile, dependencyInfo, cappedProblemRequestor);
		}else{
			processCompiledFilePart(packageName, caseInsensitiveInternedString, declaringFile);
		}
		
		// record dependency info
		DependencyGraphManager.getInstance().getDependencyGraph(project).putPart(packageName, caseInsensitiveInternedString, org.eclipse.edt.ide.core.internal.utils.Util.getFilePartName(declaringFile), dependencyInfo);
		
		if(Builder.DEBUG){
			numErrorsReported += cappedProblemRequestor.getNumberOfProblemsReported();
		    totalUnitsCompiled++;
		    System.out.println("Finished Processing: " + qualifiedName + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
			
		return binding;
    }

	private void processCompiledFilePart(String[] packageName, String caseInsensitiveInternedString, IFile declaringFile) {
		IFileInfo fileInfo = FileInfoManager.getInstance().getFileInfo(project, declaringFile.getProjectRelativePath());
		
		// For a file part, always add all other parts that are in the file
		// Do not broadcast the file part change to other projects
		for (Iterator iter = fileInfo.getPartNames().iterator(); iter.hasNext();) {
			String nextName = (String) iter.next();
			
			// don't add the file part again
			if(nextName != caseInsensitiveInternedString){
				addPartFromCompiledFile(packageName, fileInfo.getCaseSensitivePartName(nextName));
			}
		}
	}
    
	private void processCompiledPart(String[] packageName, String caseInsensitiveInternedString, String qualifiedName, Part partAST, IPartBinding binding, IFile declaringFile, DependencyInfo dependencyInfo, CappedProblemRequestor cappedProblemRequestor) {
    	TopLevelFunctionInfo[] functionInfos = null;
		if(dependencyInfo.getFunctionContainerScope() != null){
			notifier.subTask(BuilderResources.buildProcessingTopLevelFunctions);
			functionInfos = processTopLevelFunctions(dependencyInfo.getTopLevelFunctions(), dependencyInfo.getFunctionContainerScope(), declaringFile, dependencyInfo, cappedProblemRequestor);
		}
		
		notifier.subTask(NLS.bind(BuilderResources.buildCreatingIR, qualifiedName));
		File fileAST = ASTManager.getInstance().getFileAST(declaringFile);

		MofSerializable previousPart;
		try {
			EObject eobj = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project).findPart(packageName, caseInsensitiveInternedString);
			if (eobj instanceof MofSerializable) {
				previousPart = (MofSerializable)eobj;
			}
			else {
				previousPart = null;
			}
		}
		catch (Exception e) {
			// if we couldn't load the part, just assume it's structurally different
			previousPart = null;
		}
		
		try {
			MofSerializable part = createIRFromBoundAST(partAST, declaringFile, functionInfos, fileAST.getImportDeclarations(), cappedProblemRequestor);
			if(previousPart == null || !TypeUtils.areStructurallyEquivalent(previousPart, part)){
				notifier.subTask(NLS.bind(BuilderResources.buildAddingDependentsOf, qualifiedName));
				
				// If a part binding does not previously exist, do not add dependents of this part, because we have added dependents already before we processed the part.
				if(previousPart != null){
					addDependents(packageName, caseInsensitiveInternedString);
				}
				
				if(requestor != null){
					requestor.recordStructuralChange(packageName, caseInsensitiveInternedString, binding.getKind());
				}
			}
			
			notifier.subTask(NLS.bind(BuilderResources.buildSavingIR, qualifiedName));
			
			if (canSave(caseInsensitiveInternedString)) {
				ProjectEnvironmentManager.getInstance().getProjectEnvironment(project).getIREnvironment().save(part, true);
			}
		} catch (RuntimeException e) {
			cappedProblemRequestor.acceptProblem(partAST.getName(), IProblemRequestor.COMPILATION_EXCEPTION, new String[]{partAST.getName().getCanonicalName()});
			Compiler.getInstance().logIRCreationException(e);
		}
	}
	
    private MofSerializable createIRFromBoundAST(Part partAST, IFile declaringFile,TopLevelFunctionInfo[] functions, List imports, IProblemRequestor problemRequestor) {
    	IEnvironment env = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project).getIREnvironment();
        Egl2Mof generator = new Egl2Mof(env);
        return (MofSerializable)generator.convert(partAST, new IDEContext(declaringFile, projectEnvironment.getCompiler()), problemRequestor);
    }

	protected void addPartFromCompiledFile(String[] packageName, String partName){}

    protected IPartBinding level02Compile(String[] packageName, String caseSensitiveInternedPartName) {
        return ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project).compileLevel2Binding(packageName, caseSensitiveInternedPartName);
    }

    protected IPartBinding level01Compile(String[] packageName, String caseSensitiveInternedPartName) {
        return projectEnvironment.level01Compile(packageName, caseSensitiveInternedPartName);
    }

    protected IPartBinding getPartBindingFromCache(String[] packageName, String partName) {
        return ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project).getPartBindingFromCache(packageName, partName);
    }
    
    protected String getQualifiedName(String[] packageName, String partName){
		if (new Path(partName).segmentCount() > 1 || packageName.length == 0){
			//partName is a file part or no packageName
			return partName;
		}		
		
		String retVal = org.eclipse.edt.ide.core.internal.utils.Util.stringArrayToPath(packageName).addTrailingSeparator().toString() + partName;
		return retVal.replace(IPath.SEPARATOR,'.');
	}
    
    private Scope createScope(String[] packageName, IFile declaringFile, IPartBinding binding, DependencyInfo dependencyInfo) {
		Scope scope;
		if(binding.getKind() == ITypeBinding.FILE_BINDING){
			scope = new EnvironmentScope(projectEnvironment, dependencyInfo);
		}else{
			String fileName = org.eclipse.edt.ide.core.internal.utils.Util.getFilePartName(declaringFile);
			IPartBinding fileBinding = projectEnvironment.getPartBinding(packageName, fileName);
			scope = new SystemScope(new FileScope(new EnvironmentScope(projectEnvironment, dependencyInfo), (FileBinding)fileBinding, dependencyInfo), SystemEnvironmentManager.findSystemEnvironment(project, notifier));
		}
		return scope;
	}
    
    private IProblemRequestor createProblemRequestor(String partName, IFile declaringFile, Node partAST, IPartBinding binding) {
		IProblemRequestor problemRequestor = new MarkerProblemRequestor(declaringFile, partName);
		if(binding.getKind() == ITypeBinding.FUNCTION_BINDING){
			problemRequestor = new GenericTopLevelFunctionProblemRequestor(problemRequestor, ((TopLevelFunction)partAST).isContainerContextDependent());
		}
		return problemRequestor;
	}

	private TopLevelFunctionInfo[] processTopLevelFunctions(Set topLevelFunctions, FunctionContainerScope contextScope, IFile contextFile, DependencyInfo dependencyInfo, CappedProblemRequestor cappedProblemRequestor){
	    TopLevelFunctionProcessingQueue queue = new TopLevelFunctionProcessingQueue(project, contextScope, dependencyInfo, cappedProblemRequestor, compilerOptions);
		for (Iterator iter = topLevelFunctions.iterator(); iter.hasNext();) {
			IPartBinding function = (IPartBinding) iter.next();
			queue.addPart(function);
			
			if(Builder.DEBUG){
			    numTopLevelFunctionsCompiled++;
			}
		}
		
		TopLevelFunctionInfo[] functionInfos = queue.process();
		
		removeMarkersFromUnusedFunctions(contextScope.getPartBinding().getPackageName(), contextScope.getPartBinding().getName(), contextFile, dependencyInfo.getTopLevelFunctions());
		
		return functionInfos;
	}
	
	private void removeMarkersFromUnusedFunctions(String[] contextPackageName, String contextPartName, IFile contextPartFile, Set newFunctions) {
	    final HashSet newFunctionKeys = new HashSet();

	    // put new functions in key form
	    for (Iterator iter = newFunctions.iterator(); iter.hasNext();) {
            IPartBinding function = (IPartBinding) iter.next();
            
            newFunctionKeys.add(new TopLevelFunctionPartKey(((AbstractProjectEnvironment)function.getEnvironment()).getProjectName(), function.getPackageName(), function.getName()));
        }
	    
	    // locate previous functions
	    final ArrayList unusedFunctions = new ArrayList();
	    DependencyGraph dependencyGraph = DependencyGraphManager.getInstance().getDependencyGraph(project);
        dependencyGraph.findFunctionDependencies(contextPackageName, contextPartName, new IFunctionRequestor(){

            public void acceptFunction(String projectName, String[] packageName, String partName) {
                TopLevelFunctionPartKey functionKey = new TopLevelFunctionPartKey(projectName, packageName, partName);
                if(!newFunctionKeys.contains(functionKey)){
                    unusedFunctions.add(functionKey);
                }
            }            
        });
	    
	    // remove markers from the rest
        for (Iterator iter = unusedFunctions.iterator(); iter.hasNext();) {
            TopLevelFunctionPartKey functionKey = (TopLevelFunctionPartKey) iter.next();
            
            Util.removeMarkersFromInvokedFunctions(contextPartName, contextPartFile.getFullPath(), functionKey.projectName, functionKey.packageName, functionKey.partName);           
        }        
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

	protected abstract void addDependents(String[] packageName, String partName);
	
	protected abstract void addDependents(String[] qualifiedName);

	protected abstract void addDependents(String partName);

	public void setProcessorRequestor(IProcessorRequestor requestor){
    	this.requestor = requestor;
    }    
	
	public void removePart(String[] packageName, String partName) {
		pendingUnits.remove(new ProcessingUnitKey(packageName, partName));	
	}
	
	protected void doAddPart(String[] packageName, String caseInsensitiveInternedPartName) {
		addPart(packageName, projectInfo.getCaseSensitivePartName(packageName, caseInsensitiveInternedPartName));		
	}
}
