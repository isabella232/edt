/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.PPListElement;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.EGLFileWizardPage;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.project.templates.BasicProjectTemplate;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class EGLWizardUtilities {
	
	//Create Methods

	public static void createProject(String name) throws CoreException, InterruptedException, InvocationTargetException {
		createProject(name, getEGLFeatureMaskFrPreference(false));
	}
	
	public static void createProject(String name, int eglFeatureMask) throws CoreException, InterruptedException, InvocationTargetException {
		createProject(name, new ArrayList<String>(), eglFeatureMask);
	}
	
	public static void createProject(String name, List <String>dependencies) throws CoreException, InterruptedException, InvocationTargetException {
		createProject(name, dependencies, getEGLFeatureMaskFrPreference(false));
	}
	
	public static void createProject(String name, List <String>dependencies, int eglFeatureMask) throws CoreException, InterruptedException, InvocationTargetException {
		ProjectConfiguration conf = new ProjectConfiguration();
		conf.setDefaultAttributes();
		conf.setProjectName(name);
		createProject(name, dependencies, conf, eglFeatureMask);		
	}	
	
	public static void createProject(String name, List <String>dependencies, ProjectConfiguration projConfig) throws CoreException, InterruptedException, InvocationTargetException {
		createProject(name, dependencies, projConfig, getEGLFeatureMaskFrPreference(false));
	}
		
	public static void createProject(String name, List <String>dependencies, ProjectConfiguration projConfig, int eglFeatureMask) throws CoreException, InterruptedException, InvocationTargetException {
		//this is a list of PPLElement in the projConfig
		List <PPListElement>requiredProjs = projConfig.getRequiredProjects();
		
		List <PPListElement>projectDependencies = null;
		if (!dependencies.isEmpty()) {
			projectDependencies = getProjectDependencies(name, dependencies);
		}
		
		Set <PPListElement>set = new HashSet<PPListElement>();		//to avoid duplicates
		if(requiredProjs != null && !requiredProjs.isEmpty())
			set.addAll(requiredProjs);
		if(projectDependencies != null && !projectDependencies.isEmpty())
			set.addAll(projectDependencies);
				
		if(!set.isEmpty()){
			List <PPListElement>mergedProjDependencies = new ArrayList<PPListElement>();
			mergedProjDependencies.addAll(set);
			projConfig.setRequiredProjects(mergedProjDependencies);
		}
		
		ISchedulingRule rule= null;
		Job job= Job.getJobManager().currentJob();
		if (job != null)
			rule= job.getRule();

		if (rule == null)
			rule = ResourcesPlugin.getWorkspace().getRoot();
		// TODO Create the new API here, just apply the template?
		BasicProjectTemplate projectTemplate = new BasicProjectTemplate();
		List<WorkspaceModifyOperation> ops = ProjectFinishUtility.getCreateProjectFinishOperations(projectTemplate, projConfig, eglFeatureMask, rule);
		for(Iterator<WorkspaceModifyOperation> it = ops.iterator(); it.hasNext();){
			Object obj = it.next();
			if (obj instanceof WorkspaceModifyOperation) {
				WorkspaceModifyOperation op = (WorkspaceModifyOperation) obj;
				op.run(new NullProgressMonitor());
			}
		}
				
		//If project is closed, open it
		IProject myProject = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if(myProject.exists() && !myProject.isOpen())
			myProject.open(new NullProgressMonitor());	
	}
		
	
	public static int getEGLFeatureMaskFrPreference(boolean isWebProject){
		int eglfeatureMask = EGLBasePlugin.getPlugin().getPreferenceStore().getInt(EDTUIPreferenceConstants.EGLFEATURE_MASK);
		return eglfeatureMask;
	}

	/**
	 * @deprecated - no longer use J2EEVersionNum parameter
	 */


	public static void createSourceFolder(String name, String projectName) throws CoreException, InterruptedException, InvocationTargetException {
		EGLSourceFolderConfiguration conf;
		EGLSourceFolderOperation op;
		
		conf = new EGLSourceFolderConfiguration();
		conf.init(null, null);
		conf.setProjectName(projectName);
		conf.setSourceFolderName(name);
		
		op = new EGLSourceFolderOperation(conf);
		op.execute(new NullProgressMonitor());
	}
	
	public static void createPackage(String name, String projectName) throws CoreException, InterruptedException, InvocationTargetException {
		EGLPackageConfiguration conf;
		EGLPackageOperation op;
		
		conf = new EGLPackageConfiguration();
		conf.init(null, null);
		conf.setProjectName(projectName);
		conf.setFPackage(name);
		
		op = new EGLPackageOperation(conf);
		op.execute(new NullProgressMonitor());
	}
	
	public static void createPackage(String name, String projectName, String sourceFolderName) throws CoreException, InterruptedException, InvocationTargetException {
		EGLPackageConfiguration conf;
		EGLPackageOperation op;
		
		conf = new EGLPackageConfiguration();
		conf.init(null, null);
		conf.setProjectName(projectName);
		conf.setSourceFolderName(sourceFolderName);
		conf.setFPackage(name);
		
		op = new EGLPackageOperation(conf);
		op.execute(new NullProgressMonitor());
	}
	
	public static IFile createEGLFile(String name, String projectName, String packageName) throws CoreException, InterruptedException, InvocationTargetException {
		return createEGLFile(name, projectName, packageName, 
				EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.EGL_SOURCE_FOLDER ));
	}
	
	public static IFile createEGLFile(String name, String projectName, String packageName, String sourceFolderName) throws CoreException, InterruptedException, InvocationTargetException {
		EGLFileConfiguration conf;
		EGLFileOperation op;
		
		conf = new EGLFileConfiguration();
		conf.init(null, null);
		conf.setProjectName(projectName);
		conf.setSourceFolderName(sourceFolderName);
		conf.setFPackage(packageName);
		conf.setFileName(name);
		
		op = new EGLFileOperation(conf);
		op.execute(new NullProgressMonitor());
		
		return conf.getFile();
	}
	
	//Delete Methods
	
	public static void removeProject(String name) throws CoreException, InterruptedException, InvocationTargetException {
		ProjectConfiguration conf;
		ProjectRemoveOperation op;
		
		conf = new ProjectConfiguration();
		conf.setProjectName(name);
		
		op = new ProjectRemoveOperation(conf);
		op.execute(new NullProgressMonitor());
	}
	
	public static void removeSourceFolder(String name, String projectName) throws CoreException, InterruptedException, InvocationTargetException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IEGLProject eproject =  EGLCore.create(project);
		
		IPackageFragmentRoot root = eproject.getPackageFragmentRoot(new Path(name));

		IEGLModel model = EGLCore.create(ResourcesPlugin.getWorkspace().getRoot());
		IEGLElement[] elements = new IEGLElement[] { root };
		
		model.delete(elements, true, new NullProgressMonitor());		
	}
	
	public static void removePackage(String name, String projectName) throws CoreException, InterruptedException, InvocationTargetException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IEGLProject eproject =  EGLCore.create(project);
		
		IPackageFragmentRoot[] roots = eproject.getPackageFragmentRoots();
		IPackageFragment packageFragment = roots[0].getPackageFragment(name);

		IEGLModel model = EGLCore.create(ResourcesPlugin.getWorkspace().getRoot());
		IEGLElement[] elements = new IEGLElement[] { packageFragment };
		
		model.delete(elements, true, new NullProgressMonitor());	
	}
	
	public static void removePackage(String name, String projectName, String sourceFolderName) throws CoreException, InterruptedException, InvocationTargetException {		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IEGLProject eproject =  EGLCore.create(project);
		
		IPackageFragmentRoot roots = eproject.getPackageFragmentRoot(new Path(sourceFolderName));
		IPackageFragment packageFragment = roots.getPackageFragment(name);

		IEGLModel model = EGLCore.create(ResourcesPlugin.getWorkspace().getRoot());
		IEGLElement[] elements = new IEGLElement[] { packageFragment };
		
		model.delete(elements, true, new NullProgressMonitor());		
	}
	
	public static void removeEGLFile(String name, String projectName, String packageName) throws CoreException, InterruptedException, InvocationTargetException {
		EGLFileConfiguration conf;
		FileRemoveOperation op;
		
		conf = new EGLFileConfiguration();
		conf.init(null, null);
		conf.setProjectName(projectName);
		conf.setFPackage(packageName);
		conf.setFileName(name);
		
		op = new FileRemoveOperation(conf);
		op.execute(new NullProgressMonitor());
	}
	
	public static void removeEGLFile(String name, String projectName, String packageName, String sourceFolderName) throws CoreException, InterruptedException, InvocationTargetException {
		EGLFileConfiguration conf;
		FileRemoveOperation op;
		
		conf = new EGLFileConfiguration();
		conf.init(null, null);
		conf.setProjectName(projectName);
		conf.setSourceFolderName(sourceFolderName);
		conf.setFPackage(packageName);
		conf.setFileName(name);
		
		op = new FileRemoveOperation(conf);
		op.execute(new NullProgressMonitor());
	}
	
	/**  
	 * Creates a List of PPListElement out a List of project strings.
	 * This method merges the new dependencies that were not already in the project
	 * with the ones that were already there.
	 * The input dependencies is a List of String objects containing the project
	 * dependencies. 
	 * @param projectName The project whose dependencies list we need.
	 * @param dependencies The list of project names in the dependencies.
	 * @return the List of PPListElement objects containing the project dependencies.
	 */

	public static List <PPListElement>getProjectDependencies(String projectName, List <String>dependencies)
			throws CoreException {
		List <PPListElement>projectDependencies = new ArrayList<PPListElement>();
		List <PPListElement>newEGLPath = null;
		IPath newPath = null;
		IPath currPath = null;
		PPListElement newElement = null;
		PPListElement currElement = null;

		// Get the existing project dependencies		
		// newEGLPath contains all the existing class path entries.

		newEGLPath = getExistingProjectDependencies(projectName);

		// If necessary, add the new project dependencies to the existing ones.

		if (!dependencies.isEmpty()) {

			// Get the new project dependencies.

			projectDependencies = getNewProjectDependencies(projectName, dependencies);

			// Merge the new class path entries not already in this project
			// with the existing ones.

			boolean found = false;
			for (int i = 0; i < projectDependencies.size(); i++) {
				newElement = (PPListElement) projectDependencies.get(i);
				newPath = newElement.getEGLPathEntry().getPath();
				found = false;
				for (int j = 0; j < newEGLPath.size() && !found; j++) {
					currElement = (PPListElement) newEGLPath.get(j);
					currPath = currElement.getEGLPathEntry().getPath();
					if (currPath.equals(newPath)) {
						found = true;
					}
				}
				if (!found) {
					newEGLPath.add(newElement);
				}
			}
		}
		return newEGLPath;
	}

	/**  
	 * Extract the existing project dependencies for an existing project.
	 * Creates a List of PPListElement out a List of project path entries.
	 * @param projectName The project whose dependencies list we need.
	 * @return the List of PPListElement objects containing the existing project dependencies.
	 */

	public static ArrayList <PPListElement>getExistingProjectDependencies(String projectName)
			throws CoreException {

		IProject project = null;
		IEGLProject eglProject = null;
		IEGLPathEntry[] eglPathEntries = null;
		ArrayList <PPListElement>newEGLPath = new ArrayList<PPListElement>();
		IResource resource = null;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		// Get the existing project dependencies		

		resource = root.findMember(new Path(projectName));
		if(resource==null)
			return newEGLPath;
		project = resource.getProject();

		// EGLCore.create(IProject) returns the IEGLProject handle.  It assumes
		// that the project was already created.

		eglProject = EGLCore.create(project);
		eglPathEntries = eglProject.getRawEGLPath();

		// newEGLPath contains all the existing class path entries.

		if (eglPathEntries != null) {
			for (int i = 0; i < eglPathEntries.length; i++) {
				IEGLPathEntry curr = eglPathEntries[i];
				newEGLPath.add(PPListElement.createFromExisting(curr, eglProject));
			}
		}
		return newEGLPath;
	}

	/**  
	 * Calculate the new project dependencies for an existing project.
	 * Creates a List of PPListElement out a List of project strings.
	 * @param projectName The project whose dependencies list we need.
	 * @param dependencies The list of project names in the dependencies.
	 * @return the List of PPListElement objects containing the new project dependencies.
	 */


	public static ArrayList <PPListElement> getNewProjectDependencies(String projectName, List <String>dependencies) {

		// Get the new project dependencies and merge with the old ones.

		Iterator <String>dependenciesIterator = null;
		String dependentProjectName = ""; //$NON-NLS-1$
		IProject dependentProject = null;
		PPListElement currentDependent = null;
		IResource resource = null;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		ArrayList <PPListElement>projectDependencies = new ArrayList<PPListElement>();

		dependenciesIterator = dependencies.iterator();
		while (dependenciesIterator.hasNext()) {

			dependentProjectName = (String)dependenciesIterator.next();
			if (!projectName.equalsIgnoreCase(dependentProjectName)) {

				// Find the project for that associate

				resource = root.findMember(new Path(dependentProjectName));
				dependentProject = resource.getProject();
				currentDependent = new PPListElement(
										null,
										IEGLPathEntry.CPE_PROJECT,
										dependentProject.getFullPath(),
										dependentProject);
				projectDependencies.add(currentDependent);
			}
		}
		return projectDependencies;
	}

	/**
	 * recursively create the folder if the folder does not existed yet
	 * @param folder - a folder handler may or may not existed
	 * @throws CoreException
	 */
    public static void createFolderRecursiveIfNeeded(IContainer folder) throws CoreException
    {
        if(folder.getParent() != null && !folder.getParent().exists())
            createFolderRecursiveIfNeeded(folder.getParent());            
        
        if(folder instanceof IFolder)
        {
            if(!folder.exists())
                ((IFolder)folder).create(true, true, new NullProgressMonitor());
        }
    }
	
	
	/**  
	 * validate the EGL project name
	 * 
	 * @param projectName The project name
	 * @param projLocation The path of the project in string type
	 * @param isUseDefaults whether or not using default project location (the use default check box in the wizard page, usually)
	 * @param projNameStatus out parameter, to hold the project name field status
	 * @param locationStatus out parameter, to hold the project location field status
	 * @param strMessage out parameter, to hold the message
	 * @param strErrorMessage out parameter, to hold the error message
	 * @return true if no error, false otherwise
	 */	
	public static boolean validateProject (String projName, String projLocation, boolean isUseDefaults,
										   StatusInfo projNameStatus, StatusInfo locationStatus,
										   String strMessage, String strErrorMessage)
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		if (projName.equals(""))  //$NON-NLS-1$
		{ //$NON-NLS-1$
			strMessage = NewWizardMessages.WizardNewProjectCreationPage_projectNameEmpty;
			projNameStatus.setError(strMessage);
		}
		else 
		{			
			IStatus nameStatus = workspace.validateName(projName, IResource.PROJECT);
			if(nameStatus.getSeverity() != IStatus.ERROR) 
			{
				if(projName.indexOf("-") != -1) //$NON-NLS-1$
					projNameStatus.setWarning(NewWizardMessages.NewProjectCreationWizardPageWarningNamecontainsdash);
			}
			
			if (!nameStatus.isOK()) {
				if(nameStatus.getSeverity() == IStatus.ERROR)
					projNameStatus.setError(nameStatus.getMessage());
				else if(nameStatus.getSeverity() == IStatus.WARNING)
					projNameStatus.setWarning(nameStatus.getMessage());
			}
			else
			{						
				if (projLocation.equals(""))  //$NON-NLS-1$
				{ //$NON-NLS-1$
					strMessage = NewWizardMessages.WizardNewProjectCreationPage_projectLocationEmpty;
					locationStatus.setError(strMessage);
				}
				else
				{		
					IPath path = new Path(""); //$NON-NLS-1$
					if (!path.isValidPath(projLocation)) 
					{						
						locationStatus.setError(NewWizardMessages.WizardNewProjectCreationPage_locationError);
					}
					else 
					{
						IPath projectPath = new Path(projLocation);
						if (!isUseDefaults && Platform.getLocation().isPrefixOf(projectPath)) 
						{
							locationStatus.setError(NewWizardMessages.WizardNewProjectCreationPage_defaultLocationError);
						}
						else 
						{				
							IProject handle = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
							if (handle.exists()) 
							{
								projNameStatus.setError(NewWizardMessages.WizardNewProjectCreationPage_projectExistsMessage);
							}
							else 
							{					
								/*
								 * If not using the default value validate the location.
								 */
								if(!isUseDefaults)
								{
									IStatus currLocationStatus = workspace.validateProjectLocation(handle, projectPath);
									if (!currLocationStatus.isOK()) 
									{
										locationStatus.setError(currLocationStatus.getMessage());
									}
									else 
									{
										strErrorMessage = ""; //$NON-NLS-1$
										strMessage = ""; //$NON-NLS-1$
									}
										
								}
							}
						}
					}
				}
			}
		}	
		
		if(projNameStatus.getSeverity()==IStatus.ERROR || locationStatus.getSeverity()==IStatus.ERROR)
			return false;
		else
			return true;		
	}
		
	public static class NameValidatorProblemRequestor extends DefaultProblemRequestor{

		private StatusInfo fStatusInfo;
		private int worstProblemSeverity = -1;
		
		public NameValidatorProblemRequestor(StatusInfo statusInfo){
			fStatusInfo = statusInfo;
		}
		
		public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
			String[] shiftedInserts = shiftInsertsIfNeccesary(problemKind, inserts);
			String problemMsg = getMessageFromBundle(problemKind, shiftedInserts, bundle);

			if ( severity > worstProblemSeverity )
			{
				worstProblemSeverity = severity;
				switch(severity){
				case IMarker.SEVERITY_ERROR:
					setHasError( true );
					fStatusInfo.setError(problemMsg);
					break;
				case IMarker.SEVERITY_WARNING:
					fStatusInfo.setWarning(problemMsg);
					break;
				case IMarker.SEVERITY_INFO:
					fStatusInfo.setInfo(problemMsg);
					break;
				}
			}
		}
		
	}
	
	 public static boolean validateEglSourceFolder (String projName, String sourceFolderName)
	 {
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = workspaceRoot.getProject(projName);
			boolean isEglSource = false;
			if(project != null)
			{
				IEGLProject eglProject = EGLCore.create(project);
				if( eglProject != null )
				{
					try
					{
						IPackageFragmentRoot[] sources = eglProject.getAllPackageFragmentRoots();
						for( int idx = 0; sources != null && idx < sources.length; idx++ )
						{
							if(sourceFolderName.equalsIgnoreCase(sources[idx].getElementName()))
							{
								isEglSource = true;
								break;
							}
									
						}
					}
					catch(EGLModelException eglme){}
				}
			}
			return isEglSource;
		 
	 }

	/**  
	 * validate the EGL package name
	 * 
	 * @param projectName The project name
	 * @param containerName The cotainer name, usually the name of the project + file.sperator + sourcefolder name
	 * @param sourceFolderName The source folder name
	 * @param packageName The egl package name
	 * @param containerStatus out parameter, to hold the container name field status
	 * @param pkgStatus out parameter, to hold the package name field status
	 * @param callingobj The object that's calling this static method, usually the wizard page
	 * @return true if no error, false otherwise
	 */	
	 public static boolean validatePackage (String projName, String containerName, String sourceFolderName, String packageName,
	 										StatusInfo containerStatus, StatusInfo pkgStatus,
											Object callingobj)
	 {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPackageFragmentRoot currRoot= null;

		
		if (containerName.length() == 0) {
			containerStatus.setError(NewWizardMessages.NewContainerWizardPageErrorEnterContainerName);
		}
		else if(containerName.indexOf("\\\\")!=-1){ //$NON-NLS-1$
			containerStatus.setError(NewWizardMessages.NewContainerWizardPageErrorBackSlash);
		}
		else {
			IPath path= new Path(containerName);

			IResource res= workspaceRoot.findMember(path);
			if (res != null) {
				int resType= res.getType();
				if (resType == IResource.PROJECT || resType == IResource.FOLDER) {
					IProject proj= res.getProject();
					if (!proj.isOpen()) {
						containerStatus.setError(NewWizardMessages.bind(NewWizardMessages.NewContainerWizardPageErrorProjectClosed, proj.getFullPath().toString())); //$NON-NLS-1$
					}
					else {				
						IEGLProject eproject= EGLCore.create(proj);
						currRoot = eproject.getPackageFragmentRoot(res);
						if (res.exists()) {
							try {
								if (!proj.hasNature(EGLCore.NATURE_ID)) {
									if (resType == IResource.PROJECT) {
										containerStatus.setError(NewWizardMessages.NewContainerWizardPageWarningNotAnEGLProject);
									} else {
										containerStatus.setWarning(NewWizardMessages.NewContainerWizardPageWarningNotInAnEGLProject);
									}
								}
							} catch (CoreException e) {
								containerStatus.setWarning(NewWizardMessages.NewContainerWizardPageWarningNotAnEGLProject);
							}
							if (!eproject.isOnEGLPath(currRoot)) {
								containerStatus.setError(NewWizardMessages.bind(NewWizardMessages.NewContainerWizardPageWarningNotOnEGLPath, containerName)); //$NON-NLS-1$
							}		
							if (currRoot.isArchive()) {
								containerStatus.setError(NewWizardMessages.bind(NewWizardMessages.NewContainerWizardPageErrorContainerIsBinary, containerName)); //$NON-NLS-1$
							}
						}
					}
				} else {
					containerStatus.setError(NewWizardMessages.bind(NewWizardMessages.NewContainerWizardPageErrorNotAFolder, containerName)); //$NON-NLS-1$
				}
			} else {
				containerStatus.setError(NewWizardMessages.bind(NewWizardMessages.NewContainerWizardPageErrorContainerDoesNotExist, containerName)); //$NON-NLS-1$
			}
		}	
		
		//Validate Package Field
		if (packageName.length() > 0) {
//			IStatus val= EGLConventions.validatePackageName(packageName);
			ICompilerOptions compilerOption = DefaultCompilerOptions.getInstance();

	        NameValidatorProblemRequestor nameValidaRequestor = new NameValidatorProblemRequestor(pkgStatus);
			EGLNameValidator.validate(packageName, EGLNameValidator.PACKAGE, nameValidaRequestor, compilerOption);

			IEGLProject project;
			try{
				
				if(projName == null || projName.equals("")) { //$NON-NLS-1$
					pkgStatus.setError(NewWizardMessages.NewSourceFolderWizardPageErrorEnterProjectName);
				}
				else {
					project = EGLCore.create(workspaceRoot.getProject(projName));
					
					IPackageFragmentRoot root = project.getPackageFragmentRoot(new Path(sourceFolderName));
					if (root != null) {
						IPackageFragment pack= root.getPackageFragment(packageName);
						try {
							IPath rootPath= root.getPath();
							IPath outputPath= root.getEGLProject().getOutputLocation();
							if (/*rootPath.isPrefixOf(outputPath) &&*/ !rootPath.equals(outputPath)) {
								// if the bin folder is inside of our root, dont allow to name a package
								// like the bin folder
								IPath packagePath= pack.getPath();
								if (outputPath.isPrefixOf(packagePath)) {
									pkgStatus.setError(NewWizardMessages.NewPackageWizardPageErrorIsOutputFolder);
								}
								else {
									if (pack.exists() && !(callingobj instanceof EGLFileWizardPage)) {
										if (pack.containsEGLResources() || !pack.hasSubpackages()) {
											pkgStatus.setError(NewWizardMessages.NewPackageWizardPageErrorPackageExists);
										} else {
											pkgStatus.setError(NewWizardMessages.NewPackageWizardPageWarningPackageNotShown);
										}
									}
								}
							}		
						} catch (EGLModelException e) {
							EGLLogger.log(callingobj, e);
						}
					}
				}
			}
			catch (EGLModelException e) {
				EGLLogger.log(callingobj, e);
			}
		} 
		
		if(containerStatus.getSeverity()==IStatus.ERROR || pkgStatus.getSeverity()==IStatus.ERROR)
			return false;
		else
			return true;
	}	 	
	 
	public static boolean validateFile(String projName, String containerName, String pkgName, String fileName, String fileExtension,
			   StatusInfo fileStatus, Object callingobj, boolean checkExistance){
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();		
		if (fileName.length() == 0) {
			fileStatus.setError(NewWizardMessages.NewTypeWizardPageErrorEnterTypeName);
		}
		else {
			if (fileName.indexOf('.') != -1) {
				fileStatus.setError(NewWizardMessages.NewTypeWizardPageErrorQualifiedName);
			}
			else {
				ICompilerOptions compilerOption = DefaultCompilerOptions.getInstance();
		        NameValidatorProblemRequestor nameValidaRequestor = new NameValidatorProblemRequestor(fileStatus);
				EGLNameValidator.validate(fileName + "." + fileExtension, EGLNameValidator.FILENAME, nameValidaRequestor, compilerOption); //$NON-NLS-1$
				
				IFile filehandler = null;
				IProject project = workspaceRoot.getProject(projName);
				IEGLProject eproject = EGLCore.create(project);
				try{
					IPath sourcePath = new Path(containerName);
					IPackageFragmentRoot froot = eproject.findPackageFragmentRoot(sourcePath.makeAbsolute());
					if(froot != null)
					{
						IPackageFragment pack= froot.getPackageFragment(pkgName);
						 if (pack != null) {
							IResource packFolder = pack.getResource();
							if(packFolder != null)
							{
								IContainer container = (IContainer)packFolder;
								IPath filepath = new Path(fileName);
								filepath = filepath.addFileExtension(fileExtension);
								filehandler = container.getFile(filepath);
							}
						 }
					}
					else		//no fragmentroot found, usually means the containerName is not a EGLSource 
					{
					    //just append the name of the file to the container path, check for its existence
					    IPath filePath = sourcePath.append(fileName);
					    filePath = filePath.addFileExtension(fileExtension);
					    filehandler = workspaceRoot.getFile(filePath);
					}
				}
				catch(EGLModelException e) {
					e.printStackTrace();
					EGLLogger.log(callingobj, e);
				}
				
				try{
					if(filehandler != null && !filehandler.exists()){
						//check to see if a file with different case exists in window
						URI fileLocation = filehandler.getLocationURI();
						IFileStore filestore = EFS.getStore(fileLocation);
						if (filestore.fetchInfo().exists()) {
							fileStatus.setError(NewWizardMessages.ValidatePageErrorFileNameExistsDiffCase);
						}						
					}
				}
				catch (CoreException e) {
					e.printStackTrace();
				}				
				
				if(checkExistance)
				{
					if(filehandler != null && filehandler.exists())
					{
						fileStatus.setError(NewWizardMessages.NewTypeWizardPageErrorTypeNameExists);
					}
				}
			}			
		}

		if(fileStatus.getSeverity()==IStatus.ERROR)
			return false;
		else
			return true;			
	}
	 
	/**  
	 * validate the EGL file name
	 * 
	 * @param projName The project name
	 * @param containerName The cotainer name, usually the name of the project + file.sperator + sourcefolder name
	 * @param pkgName The egl package name
	 * @param fileName The egl source file name	 
	 * @param fileStatus out parameter, to hold the file name field status
	 * @param callingobj The object that's calling this static method, usually the wizard page
	 * @return true if no error, false otherwise
	 */		 
	public static boolean validateFile(String projName, String containerName, String pkgName, String fileName,
									   StatusInfo fileStatus, Object callingobj, boolean checkExistance)
	{
		return validateFile(projName, containerName, pkgName, fileName, "egl", fileStatus, callingobj, checkExistance); //$NON-NLS-1$
	}	
}
