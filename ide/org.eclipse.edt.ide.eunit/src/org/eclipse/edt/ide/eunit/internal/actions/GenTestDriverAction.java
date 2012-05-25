/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.eunit.internal.actions;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.compiler.internal.PartWrapper;
import org.eclipse.edt.gen.eunit.CommonUtilities;
import org.eclipse.edt.gen.eunit.EGL2Base;
import org.eclipse.edt.gen.eunit.GenPartsXMLFile;
import org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier;
import org.eclipse.edt.ide.core.IIDECompiler;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPath;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.PPListElement;
import org.eclipse.edt.ide.core.utils.DefaultDeploymentDescriptorUtility;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.eunit.Activator;
import org.eclipse.edt.ide.ui.wizards.EGLProjectUtility;
import org.eclipse.edt.ide.ui.wizards.EGLWizardUtilities;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;

import com.ibm.icu.text.SimpleDateFormat;


public abstract class GenTestDriverAction implements	IObjectActionDelegate{

	private static final String RUNUNIT_PROPERTIES = "rununit.properties";
	protected static final String RESULTROOT_KEY = "EGLTestResultRoot";
	protected static final String RESULTROOT_DIR_APPEND = "ResultRoot";
	
	protected static final String GENERATORID_JAVA = "org.eclipse.edt.ide.gen.JavaGenProvider";
	protected static final String GENERATORID_JAVACORE = "org.eclipse.edt.ide.gen.JavaCoreGenProvider";
	protected static final String GENERATORID_JAVASCRIPT = "org.eclipse.edt.ide.gen.JavaScriptGenProvider";
	protected static final String GENERATORID_JAVASCRIPT_DEV = "org.eclipse.edt.ide.gen.JavaScriptDevGenProvider";
	
	/**
	 * The root relative path of the EGL Resources plugin to retrieve the files to copy from
	 */
	protected static final String token = "eunit_runtime"; //$NON-NLS-1$
	
	/**
	 * The list of file names that need to be copied
	 */
	protected static final String[] EUNITRUNTIME_FILE = {
		 	"dataDef.egl", //$NON-NLS-1$
			"TestExecutionLib.egl", //$NON-NLS-1$
			"WriteResultLib.egl", //$NON-NLS-1$
			"ExternalTypes.egl"}; //$NON-NLS-1$
	
	protected static final String[] EUNITRUNTIME_FILE_MUSTJAVAGEN = {"TestResultService.egl", "ExternalTypes.egl"};
	
	protected String[] EUNITRUNTIME_FILE_BYLANG={};
	
	protected IStructuredSelection fSelection;
	protected IWorkbenchSite fSite;	

	@Override
	public void run(IAction action) {		
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(fSite.getShell());
		
		IResource selResource =  (IResource)(fSelection.getFirstElement());
		
		IProject baseProj = selResource.getProject();
		String baseProjName = baseProj.getName();
		IEGLProject baseEGLProj = EGLCore.create(baseProj);		
		IWorkspaceRoot wsRoot = baseProj.getWorkspace().getRoot();										
		
		try{			
			List<WorkspaceModifyOperation> ops = getGenTestDriverOperatoins(wsRoot, baseProjName, baseProj, baseEGLProj);			
			for(WorkspaceModifyOperation op : ops){
				progressDialog.run(true, true, op);
			}						

		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	protected abstract List<WorkspaceModifyOperation> getGenTestDriverOperatoins(IWorkspaceRoot wsRoot, String baseProjName, IProject baseProj, IEGLProject baseEGLProj);
	
	protected WorkspaceModifyOperation getRefreshWSOperation(final IProject baseProj, final IProject driverProj){
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {			
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException,
					InvocationTargetException, InterruptedException {
				monitor.subTask("Refresh base project and generated driver project");
				baseProj.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				driverProj.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				monitor.worked(2);
			}
		};
		return op;
	}
	
	protected WorkspaceModifyOperation getCopyECKRuntimeFilesOperation(final IProject driverProject, final String languageToken){	
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {			
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException,
					InvocationTargetException, InterruptedException {
				monitor.subTask("copying eunit runtime files...");
				copyFilesToProject(driverProject, CommonUtilities.EUNITRUNTIME_PACKAGENAME, EUNITRUNTIME_FILE, token, monitor);
				copyFilesToProject(driverProject, CommonUtilities.EUNITRUNTIME_PACKAGENAME, EUNITRUNTIME_FILE_BYLANG, token+"/"+languageToken, monitor);
				monitor.worked(1);
			}
		};
		return op;
	}

	protected WorkspaceModifyOperation getGenDriverOperation(final IWorkspaceRoot wsRoot, 
														   final IProject baseProject, final IEGLProject baseEGLProject, 
														   final IProject driverProject, final IEGLProject driverEGLProject) {
		
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {			
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException,
					InvocationTargetException, InterruptedException {
				try{
					IEUnitGenerationNotifier eckGenerationNotifier = new EUnitGenerationNotifier(monitor);
					
					IPath irRootPath = baseEGLProject.getOutputLocation(); 
					
					//get required projects here so we can return to eclipse if exception during build.
					ProjectBuildPath projBP = ProjectBuildPathManager.getInstance().getProjectBuildPath(driverProject);
					IContainer[] eglSrcs = projBP.getSourceLocations();					
					IPath eglSourcePath = eglSrcs[0].getFullPath();
					
					//String[] args = new String[4];
					List<String> argList = new ArrayList<String>();
					argList.add("-output");
					argList.add(wsRoot.getFolder(eglSourcePath).getLocation().toOSString());
					argList.add("-root");
					argList.add(wsRoot.getFolder(irRootPath).getLocation().toOSString());
			        IIDECompiler compiler = ProjectSettingsUtility.getCompiler(baseProject);
			        
			        //if the selection is not project, then calculate all the EGL parts needs to be generated
			        if(!isSelectionProject()){
			        	String genPartsFileLocation = getGenPartsArgument(wsRoot, baseProject);
			        	if(genPartsFileLocation != null && genPartsFileLocation.length() > 0){
			        		argList.add("-" + EGL2Base.ARG_PARM_GENPARTS);
			        		argList.add(genPartsFileLocation);
			        	}
			        }
			        String[] args = argList.toArray(new String[argList.size()]);
			        invokeDriverGenerator(args, compiler, eckGenerationNotifier);				
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
			}
		};
		return op;
	}
	
	protected boolean isSelectionProject(){
		Object sel = fSelection.getFirstElement();
		if(sel instanceof IProject){
			return true;
		}
		return false;
	}
	
	protected String getGenPartsArgument(IWorkspaceRoot wsRoot, IProject baseProject){
		String fileLocation = getGenFileFullLocation(wsRoot, baseProject);
		try {
			List<IEGLFile> eglFiles = getEGLFiles(fSelection);
			
			List<String> partNameEntries = new ArrayList<String>(); 
			for(IEGLFile eglFile : eglFiles){
				IPart[] parts = eglFile.getParts();
				for(IPart part : parts) {					
					partNameEntries.add(part.getFullyQualifiedName());					
				}
			}			
			writePart2ConfigurationFile(fileLocation, partNameEntries);
		} catch (EGLModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fileLocation;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getGenFileFullLocation(IWorkspaceRoot wsRoot, IProject baseProject) {
		
		Date now = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssZ");		
		IPath path = baseProject.getFullPath();
		path = path.append(EGL2Base.ARG_PARM_GENPARTS + String.valueOf(dateFormat.format(now)));
		path = path.addFileExtension("xml");
		return wsRoot.getFolder(path).getLocation().toOSString();
	}	

	/**
	 * 
	 * @param entry
	 */
	protected void writePart2ConfigurationFile(String fileName, List<String> entries) {
		GenPartsXMLFile driverXMLFile = new GenPartsXMLFile(fileName);
		try {
			driverXMLFile.saveGenerationEntries("", entries);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	private void getEGLElements(IEGLElement eglElem, List <IEGLFile> result) throws EGLModelException
	{
		if(eglElem != null)
		{
	        switch(eglElem.getElementType())
	        {
//	        case IEGLElement.EGL_PROJECT:
//	        	IPackageFragmentRoot[] pkgRoots = ((IEGLProject)eglElem).getPackageFragmentRoots();
//	        	for(int i=0; i<pkgRoots.length; i++)
//	        	{
//	        		collectEGLFiles(pkgRoots[i], result);
//	        	}		                	
//	            break;
	        case IEGLElement.PACKAGE_FRAGMENT_ROOT:
	        	collectEGLFiles((IPackageFragmentRoot)eglElem, result);
	            break;
	        case IEGLElement.PACKAGE_FRAGMENT:
	        	collectEGLFiles((IPackageFragment)eglElem, result);
	            break;
	        case IEGLElement.EGL_FILE:
	            result.add((IEGLFile)eglElem);	
	            break;
	        }
		}
	}
	
	private List <IEGLFile> getEGLFiles(IStructuredSelection selection) throws EGLModelException
	{
	    List <IEGLFile> result = new ArrayList<IEGLFile>();
	    if(selection != null)
	    {
	        Iterator it = selection.iterator();
	        while(it.hasNext())
	        {
	            Object element = it.next();
	            if(element instanceof IEGLElement)
	            {
	                IEGLElement eglElem = (IEGLElement)element;
	                getEGLElements(eglElem, result);
	            }
//	            else if(element instanceof IProject)
//	            {
//	            	 IEGLProject eglproj = EGLCore.create((IProject)element);
//	            	 getEGLElements(eglproj, result);
//	            }
	            else if(element instanceof IResource)
	            {
	            	IEGLElement eglResourceElem = EGLCore.create((IResource)element);
	            	getEGLElements(eglResourceElem, result);
	            }
				
	        }
	    }
	    return result;
	}	

	
	private void collectEGLFiles(IPackageFragment pkg, List <IEGLFile> result) throws EGLModelException
	{
    	IEGLFile[] eglfiles = pkg.getEGLFiles();
    	for(int i=0; i<eglfiles.length; i++)
    	{
    		result.add(eglfiles[i]);
    	}
	}
	
	private void collectEGLFiles(IPackageFragmentRoot pkgRoot, List <IEGLFile> result) throws EGLModelException
	{
		if(pkgRoot.getKind() == IPackageFragmentRoot.K_SOURCE)
		{
			IEGLElement[] children = pkgRoot.getChildren();
			for(int i=0; i<children.length; i++)
			{
				collectEGLFiles((IPackageFragment)children[i], result);
			}
		}
	}	
	
	protected abstract void invokeDriverGenerator(String[] args, IIDECompiler compiler, IEUnitGenerationNotifier eckGenerationNotifier);
	
	/**
	 * add dependedProjName as a depended project for projname, 
	 * 
	 * @param eglProj - EGLProject for projname
	 * @param projname 
	 * @param dependedProjName
	 * 
	 * 
	 */
	protected WorkspaceModifyOperation getSetEGLBuildPathOperation(final IEGLProject eglProj, final String projname, final String dependedProjName){
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				monitor.subTask("Setting EGL build path depends on project " + dependedProjName);
				List <String> eglDepends = new ArrayList<String>(); 
				eglDepends.add(dependedProjName);
				
				List<PPListElement>depends =  EGLWizardUtilities.getProjectDependencies(projname, eglDepends);
				
				int nEntries = depends.size();
				IEGLPathEntry[] classpath = new IEGLPathEntry[nEntries];

				// create and set the class path
				int i=0;
				for(PPListElement entry: depends){
					classpath[i] = entry.getEGLPathEntry();
					i++;
				}
				eglProj.setRawEGLPath(classpath, monitor);				
				monitor.worked(1);
			}					
		};
		return op;
	}
	
	protected WorkspaceModifyOperation getSetJavaBuildPathOperation(final IProject javaDriverProject, final IProject dependentProj){
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException,
					InvocationTargetException, InterruptedException {
				if (javaDriverProject.hasNature(JavaCore.NATURE_ID)) {
					monitor.subTask("Set java build path depends on project " + dependentProj.getName());
					IJavaProject javaProject = JavaCore.create(javaDriverProject);				
					
					IClasspathEntry[] classpath = javaProject.getRawClasspath();
					
					boolean javaProjBuildPathAlreadySet = false;				
					//check to see if the java build path already set for the same dependent project
					for(int p=0; p<classpath.length && !javaProjBuildPathAlreadySet; p++){
						if(classpath[p].getEntryKind() == IClasspathEntry.CPE_PROJECT){
							IPath dependentProjPath = classpath[p].getPath();
							if(dependentProj.getFullPath().equals(dependentProjPath))
								javaProjBuildPathAlreadySet = true;
						}
					}
					
					//if not set, set it
					if(!javaProjBuildPathAlreadySet){
						List<IClasspathEntry> additions = new ArrayList<IClasspathEntry>();
						
						IClasspathEntry newClsPathEntry = JavaCore.newProjectEntry(dependentProj.getFullPath());
						additions.add(newClsPathEntry);
						
						if (additions.size() > 0) {
							IClasspathEntry[] newEntries = new IClasspathEntry[classpath.length + additions.size()];
							System.arraycopy(classpath, 0, newEntries, 0, classpath.length);
							for (int i = 0; i < additions.size(); i++) {
								newEntries[classpath.length + i] = additions.get(i);
							}
							javaProject.setRawClasspath(newEntries, null);
						}				
					}
					monitor.worked(1);
				}				
			}
		};
		
		return op;
	}

	protected WorkspaceModifyOperation getCreateEGLProjectOperation(final IWorkspaceRoot wsRoot, final String newProjName, final String baseProj){
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				monitor.subTask("Creating driver project " + newProjName);
				IProject newProj = wsRoot.getProject(newProjName);
				if(!newProj.exists()) {
					EGLWizardUtilities.createProject(newProjName, 0);
					
					// Also set the Default DD of the new project to that of the original project.
					// Don't do this as a separate operation because we only want to do this when creating a new project.
					// The user should be allowed to modify this setting and then re-run EUnit generation without losing their change.
					IProject origProj = wsRoot.getProject(baseProj);
					if (origProj.exists()) {
						PartWrapper pw = DefaultDeploymentDescriptorUtility.getDefaultDeploymentDescriptor(origProj);
						if (pw != null && pw.getPartPath() != null && pw.getPartPath().length() > 0) {
							DefaultDeploymentDescriptorUtility.setDefaultDeploymentDescriptor(newProj, pw);
						}
					}
				}
				monitor.worked(1);
			}
		};
		return op;
	}
	
	protected WorkspaceModifyOperation getSetGeneratorIDOperation(final IProject driverProject, final String[] generatorIDs) {
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				monitor.subTask("set generator id " + generatorIDs[0] +" for project " + driverProject.getName());
				try {
					ProjectSettingsUtility.setGeneratorIds(driverProject, generatorIDs);
				} catch (BackingStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				monitor.worked(1);
			}			
		};
		return op;
	}
	
	protected WorkspaceModifyOperation getCreateRununitPropertyOperation(final IWorkspaceRoot wsRoot, final IProject driverProject){
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {			
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException,
					InvocationTargetException, InterruptedException {
				if (driverProject.hasNature(JavaCore.NATURE_ID)) {
					monitor.subTask("Creating rununit property " + RESULTROOT_KEY);
					IJavaProject javaProject = JavaCore.create(driverProject);
					IClasspathEntry[] classpath = javaProject.getRawClasspath();
					
					IClasspathEntry sourceClsPath = null;
					for(int i=0; (i<classpath.length) && (sourceClsPath == null); i++){
						if(classpath[i].getEntryKind() == IClasspathEntry.CPE_SOURCE)
							sourceClsPath = classpath[i];
					}
					if(sourceClsPath != null){
						IPath propertyFilePath = sourceClsPath.getPath().append(RUNUNIT_PROPERTIES);
						IFile propertyFile = wsRoot.getFile(propertyFilePath);
						String propertyOSPath = propertyFile.getLocation().toOSString();					
								
						try {
							Properties props = new Properties();	
							if(propertyFile.exists()){
								FileReader inReader = new FileReader(propertyOSPath);						
								props.load(inReader);						
								inReader.close();
							}					
							PrintWriter outWriter = new PrintWriter(propertyOSPath);											
							String resultRootFolder = driverProject.getFolder(RESULTROOT_DIR_APPEND).getLocation().toOSString();
							props.put(RESULTROOT_KEY, resultRootFolder);															
							props.store(outWriter, "");															
							outWriter.flush();							
							outWriter.close();					
														
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					monitor.worked(1);	
				}
			}
		};
		return op;
	}
	
	/**
	 * Convienence method for copy files from the EGL Resources plugin to an EGL project
	 * 
	 * @param toProject The project to copy to
	 * @param toPackage The package to copy to
	 * @param files The array of file names to copy
	 * @param resourceLocation The locacation relative to the plugin's root location, this plugin has the files you wan to copy 
	 * @throws InvocationTargetException 
	 * @throws InterruptedException 
	 * @throws CoreException 
	 *
	 */
	protected static void copyFilesToProject(IProject toProject, String toPackage, String[] files, String resourceLocation, IProgressMonitor monitor) throws CoreException, InterruptedException, InvocationTargetException  {		
			EGLWizardUtilities.createPackage(toPackage, toProject.getName());
			Bundle sourcePlugin = Platform.getBundle(Activator.PLUGIN_ID);
			String[] sourceFileNames = files;
			IPath sourceRelative2PluginPath = new Path(resourceLocation);

			IEGLProject eproject = EGLCore.create(toProject);

			List <PPListElement> eglSrcs = EGLProjectUtility.getDefaultClassPath(eproject);
			IPath eglSourcePath = eglSrcs.get(0).getPath();
			
			IPackageFragmentRoot root = eproject.findPackageFragmentRoot(eglSourcePath.makeAbsolute());
			IPackageFragment frag = root.getPackageFragment(toPackage);
			IPath targetRelative2ProjectPath = frag.getResource().getProjectRelativePath();
			copyFiles(sourcePlugin, sourceRelative2PluginPath, sourceFileNames,
					toProject, targetRelative2ProjectPath, monitor);			
	}
	
	
	/**
	 * This will copy the files specified in sourceFileNames, which are
	 * under sourcePlugin/sourceRelative2PluginPath to targetProject/targetRelative2ProjPath
	 * 
	 * Note: the copy will not occur if the target file already existed in the file system
	 * 
	 * @param sourcePlugin - source Plug in
	 * @param sourceRelative2PluginPath - the source path location relative to the source plug in 
	 * @param sourceFileNames	- list of files in the source location needs to be copied
	 * @param targetProj - target project 
	 * @param targetRelative2ProjPath - target files path location relative to the project
	 * @throws CoreException 
	 * @throws Exception
	 */
	protected static void copyFiles(Bundle sourcePlugin,
							 IPath sourceRelative2PluginPath,
							 String[] sourceFileNames,
							 IProject targetProj,
							 IPath targetRelative2ProjPath,
							 IProgressMonitor monitor) throws CoreException
	{
		for(int i=0; i<sourceFileNames.length; i++)
		{
			IFolder targetFolder = targetProj.getFolder(targetRelative2ProjPath);
		    EGLWizardUtilities.createFolderRecursiveIfNeeded(targetFolder);
		    IPath filepathRelative2Proj = targetRelative2ProjPath.append(sourceFileNames[i]);

			IFile targetFile = targetProj.getFile(filepathRelative2Proj);
			
			//check for existence
			if(!targetFile.exists()){
				IPath sourceFile = sourceRelative2PluginPath.append(sourceFileNames[i]);
				InputStream sourceStream;
				try {
					sourceStream = FileLocator.openStream(sourcePlugin, sourceFile, false);
					targetFile.create(sourceStream, true, monitor);
					sourceStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//if it's one of the file that must be generated with java generator
			//eunit runtime java external type and services parts must be generated with java generator
			try {
				for (int x=0; x<EUNITRUNTIME_FILE_MUSTJAVAGEN.length; x++){
					if(sourceFileNames[i].equals(EUNITRUNTIME_FILE_MUSTJAVAGEN[x])){
						//set the java generator for these files
						ProjectSettingsUtility.setGeneratorIds(targetFile, new String[]{GENERATORID_JAVA});						
					}
				}
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = (IStructuredSelection)selection;		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		fSite = targetPart.getSite();
	}

}
