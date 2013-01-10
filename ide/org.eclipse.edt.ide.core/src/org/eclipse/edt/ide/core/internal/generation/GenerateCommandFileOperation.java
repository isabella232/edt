/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.generation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.CoreIDEPluginStrings;
import org.eclipse.edt.ide.core.Logger;
import org.eclipse.edt.ide.core.generation.IGenerationUnit;
import org.eclipse.edt.ide.core.internal.utils.ProjectPathUtility;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GenerateCommandFileOperation {
	/**
	 * Command File extension
	 */
	public static final String EGL_COMMAND_FILE_EXTENSION = "xml"; //$NON-NLS-1$

	/**
	 * genPartsOperation - this object.
	 */
	private static final GenerateCommandFileOperation generateCmdFileOperation = new GenerateCommandFileOperation();

	private static ArrayList commandFileRequestList = new ArrayList();

	private class CommandFileGenerator
	{
		private static final String EGLCOMMANDS_EGLPATH_ATTRIBUTE = "eglpath"; //$NON-NLS-1$
				
		private static final String GENERATE_ELEMENT = "generate"; //$NON-NLS-1$
		private static final String GENERATE_FILE_ATTRIBUTE = "file"; //$NON-NLS-1$
		
		private static final String BUILD_DESCRIPTOR_ELEMENT = "buildDescriptor"; //$NON-NLS-1$
		private static final String BUILD_DESCRIPTOR_NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
		private static final String BUILD_DESCRIPTOR_FILE_ATTRIBUTE = "file"; //$NON-NLS-1$
		
		private IDOMDocument document = null;
		private IPath path = null;
						
		private final String MODEL_ID = EDTCoreIDEPlugin.EGL_UTILITIES + "." + EGL_COMMAND_FILE_EXTENSION; //$NON-NLS-1$
		
		/**
		 * Constructor for EGLCreateCommandFileCommand.
		 */
		public CommandFileGenerator(String filePath) {
			super();
			
			setPath(filePath);
			initialize();			
		}

		/**
		 * Method setPath.
		 * @param filePath
		 */
		private void setPath(String filePath) {
			
			path = new Path(filePath);
			
			if(path.getFileExtension() == null)
			{
				path = path.addFileExtension(EGL_COMMAND_FILE_EXTENSION);	
			}
			else if(!path.getFileExtension().equalsIgnoreCase(EGL_COMMAND_FILE_EXTENSION))
			{
				path = path.addFileExtension(EGL_COMMAND_FILE_EXTENSION);
			}
		}

		
		/**
		 * Discard this documents model
		 */
		public void dispose()
		{
			if(document != null)
			{
				document.getModel().releaseFromEdit();
			}
		}
		
		/**
		 * Set up this command file document
		 */
		private void initialize()
		{
			IModelManager modelManager = StructuredModelManager.getModelManager();
			
			IDOMModel xmlModel;
			
			try {
				xmlModel =
					(IDOMModel) modelManager.getModelForEdit(
						MODEL_ID,
						new ByteArrayInputStream(CommandFile.createEmptyCommandFile().getBytes()),
						null);
						
				document = xmlModel.getDocument();
				
			} catch (UnsupportedEncodingException e) {
				Logger.log(this, "EGLCreateCommandFileOperation.EGLCreateCommandFileCommand::initialize() - Unsupported Encoding Exception", e); //$NON-NLS-1$
			} catch (IOException e) {
				Logger.log(this, "EGLCreateCommandFileOperation.EGLCreateCommandFileCommand::initialize() - IOException", e); //$NON-NLS-1$
			}
		}
		
		/**
		 * Set the egl path in this document
		 */
		private void setEGLPath(String path)
		{
			if(document != null)
			{
				document.getDocumentElement().setAttribute(EGLCOMMANDS_EGLPATH_ATTRIBUTE, path);
			}
		}
		
		/**
		 * Create a new generate command
		 */
		public void createGenerateCommand(String partFileName, String buildDescriptorFileName, String buildDescriptorName)
		{
			if(document != null)
			{
				Node genElement = createGenerateElement(document.getDocumentElement(), partFileName);
			
				// create child
				createBuildDescriptorElement(genElement, buildDescriptorFileName, buildDescriptorName);
			}
		}

		/**
		 * Method createBuildDescriptorElement.
		 * @param buildDescriptorFileName
		 * @param buildDescriptorPartName
		 * @return Node
		 */
		private Node createBuildDescriptorElement(Node parent, String buildDescriptorFileName, String buildDescriptorName) 
		{			
			Element bdElement = null;
			
			if(document != null)
			{
				bdElement = document.createElement(BUILD_DESCRIPTOR_ELEMENT);
			
				bdElement.setAttribute(BUILD_DESCRIPTOR_NAME_ATTRIBUTE, buildDescriptorName);
				bdElement.setAttribute(BUILD_DESCRIPTOR_FILE_ATTRIBUTE, buildDescriptorFileName);
			
				parent.appendChild(bdElement);
			}
			
			return bdElement;
		}

		/**
		 * Create a new generate element
		 */
		private Node createGenerateElement(Node parent, String partFileName)
		{
			Element genElement = null;
			
			if(document != null)
			{
				genElement = document.createElement(GENERATE_ELEMENT);
				genElement.setAttribute(GENERATE_FILE_ATTRIBUTE, partFileName);
			
				parent.appendChild(genElement);	
			}
			
			return genElement;
		}
			
		/**
		 * Write to disk
		 */
		public void write() throws IOException
		{
			if(document != null)
			{
				// extra safety check so that we don't write a file 
				// that is named a drive letter.  (ex: "c:\")
				if(path.segmentCount() > 0)
				{
					// create a directory if we need to
					if(createDirectoryIfNecessary(path) == true)
					{
						// check to see if we can create/ write to the file
						File file = path.toFile();
						if((file.exists() && file.canWrite())
							|| (!file.exists() && file.createNewFile()))
						{
							// format the document
							formatDocument();
						
							FileOutputStream stream = new FileOutputStream(file);
						
							try {
								// write the document
								document.getModel().save(stream);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
							stream.close();
						}
						else
						{
							// error
							openErrorDialog();
						}
					}
					else
					{
						// report error
						Logger.log(this, "EGLGenerateCommandFileOperation.EGLCommandFileGenerator::write() - error writing file"); //$NON-NLS-1$
					}
				}
				else
				{
					Logger.log(this, "EGLGenerateCommandFileOperation.EGLCommandFileGenerator::write() - invalid path"); //$NON-NLS-1$
				}
			}
		}

		/**
		 * Method openErrorDialog.
		 */
		private void openErrorDialog() {
			
			Display display = Display.getDefault();
			if (display != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						
						IWorkbenchWindow  window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						
						if(window != null)
						{
							MessageDialog.openError(window.getShell(), 
									CoreIDEPluginStrings.GenerateCommandFileOperation_ErrorMessage_Title, 
									CoreIDEPluginStrings.bind(CoreIDEPluginStrings.GenerateCommandFileOperation_ErrorMessage_Message, path.toOSString()));
						}
					}
				});
			}
		}

		/**
		 * Format this document
		 */
		private void formatDocument()
		{
			if(document != null)
			{
				FormatProcessorXML processor = new FormatProcessorXML();
				processor.formatModel(document.getModel());
			}
		}
		
		/**
		 * If the directory from this path does not exist, create it
		 */
		private boolean createDirectoryIfNecessary(IPath path)
		{
			boolean result = true;
			File file = null;
			
			// if we have more than 0 segments, remove the last segment
			if(path.segmentCount() > 0)
			{
				// get everything but the last segment
				path = path.removeLastSegments(1);
			}		
			
			file = new File(path.toOSString());
				
			// if this directory doesn't exist, create it
			if(!file.exists())
			{
				result = file.mkdir();
			}
			
			return result;
		}
			
	}
/**
 * GenerationWizardGeneratePartsOperation constructor comment.
 */
private GenerateCommandFileOperation()
{
	super();
}

public static GenerateCommandFileOperation getInstance()
{
	return generateCmdFileOperation;
}

public void generate(IGenerationUnit[] parts, String cmdFilePath, IProject[] projects, boolean createEGLPath) {
	
	GenerateCommandFileRequest request = new GenerateCommandFileRequest(parts, cmdFilePath, projects, createEGLPath);
	
	synchronized(commandFileRequestList){
		commandFileRequestList.add(request);
	}
	
	// Create a new job for each generation request
	Job generationJob = createGenerationJob();
	generationJob.schedule();
}

/**
 * Insert the method's description here.
 * Creation date: (12/17/2001 2:12:24 PM)
 * @return java.util.List
 */
private void generateCommandFile(org.eclipse.core.runtime.IProgressMonitor monitor) {

	synchronized(generateCmdFileOperation)
	{
		GenerateCommandFileRequest request = null;
		IGenerationUnit[] partList = null;
		
		synchronized(commandFileRequestList)
		{
			if(commandFileRequestList.size() > 0)
			{
				request = (GenerateCommandFileRequest)commandFileRequestList.remove(0);
			}
		}
			
		if(request != null)
		{
			// create a new createCommand
			CommandFileGenerator createCommand = new CommandFileGenerator(request.getEglCommandFilePath());
			
			// create the egl path if necessary
			if(request.isCreateEGLPath())
			{
				// set the EGLPath for this create command
				createCommand.setEGLPath(ProjectPathUtility.getEglPathString(request.getProjects()));
			}
			
			// get a list of the parts to put in the command file
			partList = request.getGenerationUnits();
			
			// setup the monitor
			monitor.beginTask(CoreIDEPluginStrings.GenerateCommandFileOperation_TaskName, (partList.length * 2));
		
			// generate the commands
			for(int i=0; i<partList.length; i++)
			{
				monitor.worked(1);
			
				if(monitor.isCanceled())
				{
					break;
				}
				
				PartWrapper part = partList[i].getPart();
				IEGLPartWrapper bdWrapper = partList[i].getBuildDescriptor();
				
				Path partPath = new Path(part.getPartPath());
				Path bdPath = new Path(bdWrapper.getPartPath());
								
				monitor.subTask(CoreIDEPluginStrings.bind(CoreIDEPluginStrings.GenerateCommandFileOperation_SubTaskName, part.getPartName()));
						
				// invoke the command file creation
				createCommand.createGenerateCommand(partPath.makeRelative().toOSString(), 
													bdPath.makeRelative().toOSString(),
													bdWrapper.getPartName());
													
				monitor.worked(1);
			}
				
			try
			{
				createCommand.write();
			}
			catch (IOException e)
			{
				Logger.log(this, "EGLGenerateCommandFileOperation.process():  Error during command file write", e); //$NON-NLS-1$
			}
		
			createCommand.dispose();
			
			monitor.done();
		}
	}
}

	/**
	 * Create a job to run the part generation.  This job will block the entire worskpace for writing
	 */
	private Job createGenerationJob() {
		Job generationJob = new Job(CoreIDEPluginStrings.GenerateCommandFileOperation_JobName) {
			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public IStatus run(IProgressMonitor monitor) {
				
				WorkspaceModifyOperation modifyOperation = new WorkspaceModifyOperation(){
					/* (non-Javadoc)
					 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
					 */
					protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
						generateCommandFile(monitor);
					}};
					
				try {
					modifyOperation.run(monitor);
				} catch (InvocationTargetException e) {
					Logger.log(this, "EGLGenerateCommandFileOperation.createGenerationJob():  InvocationTargetException", e); //$NON-NLS-1$
				} catch (InterruptedException e) {
					Logger.log(this, "EGLGenerateCommandFileOperation.createGenerationJob():  InterruptedException", e); //$NON-NLS-1$
				}
				return Status.OK_STATUS;
			}
		};
		
		generationJob.setRule(ResourcesPlugin.getWorkspace().getRoot());
		generationJob.setSystem(false);
		generationJob.setPriority(Job.LONG);
		generationJob.setUser(true);
		
		return generationJob;
	}

}
