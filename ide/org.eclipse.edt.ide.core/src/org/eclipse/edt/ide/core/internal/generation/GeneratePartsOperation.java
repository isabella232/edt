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
package org.eclipse.edt.ide.core.internal.generation;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.ide.core.CoreIDEPluginStrings;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.Logger;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;
import org.eclipse.edt.ide.core.internal.utils.StringOutputBuffer;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.InvalidPartTypeException;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import com.ibm.icu.util.StringTokenizer;

public class GeneratePartsOperation {
	
//	protected static class MessageContributor implements IEGLComponentMessageContributor {
//
//		private String resourceName;
//
//		private IEGLLocation startLocation;
//
//		private CommandRequestor commandRequestor;
//
//		/**
//		 * 
//		 */
//
//		protected MessageContributor(Element element, CommandRequestor commandRequestor) {
//			super();
//			this.commandRequestor = commandRequestor;
//			resourceName = getResourceName(element);
//			startLocation = getLocation(element, resourceName);
//		}
//
//		private String getResourceName(Element element) {
//			if (element == null) {
//				return null;
//			}
//			if (element instanceof Part) {
//				return ((Part) element).getFileName();
//			}
//			if (element instanceof Function) {
//				Function func = (Function) element;
//				//TODO EDT
////				if (func.getFileName() != null) {
////					return func.getFileName();
////				}
//			}
//			
//			if (element instanceof Member) {
//				Member member = (Member)element;
//				if (member.getContainer() instanceof Member) {
//					return getResourceName((Member) member.getContainer());
//				}
//			}
//			return null;
//		}
//
//		private Location getLocation(Element element, String resourceName) {
//			if (element == null || resourceName == null) {
//				return null;
//			}
//			Annotation ann = element.getAnnotation(EGLLineNumberAnnotationTypeBinding.name);
//			if (ann == null) {
//				if (element instanceof Member) {
//					return getLocation(((Member) element).getContainer(), resourceName);
//				}
//				return null;
//			} else {
//				return getLocation(((Integer) ann.getValue()).intValue(), resourceName);
//			}
//		}
//
//		private Location getLocation(int lineNumber, String resourceName) {
//			try {
//				String contents = commandRequestor.getFileContents(resourceName);
//				SimpleLineTracker tracker = new SimpleLineTracker(contents);
//				int[] offsets = tracker.getOffsetsForLine(lineNumber);
//				return new Location(lineNumber, 0, offsets[0], offsets[1] - offsets[0]);
//			} catch (Exception e) {
//				return null;
//			}
//
//		}
//
//		public IEGLComponentMessageContributor getMessageContributor() {
//			return this;
//		}
//
//		public String getResourceName() {
//			return resourceName;
//		}
//
//		public IEGLLocation getStart() {
//			return startLocation;
//		}
//
//		public IEGLLocation getEnd() {
//			IEGLLocation start = getStart();
//			if (start == null) {
//				return null;
//			}
//			return new Location(start.getLine(), start.getColumn(), start.getOffset() + start.getLength() - 1, start.getLength());
//		}
//
//	}
	
	/**
	 * A Generation Lock... only one generateParts can be running at a time,
	 * across all instances of this type.
	 */
	private static final Object lock = new Object();

//	protected CommandRequestor requestor;
	
	private ArrayList<GenerationRequest> genRequestList;
	
	public GeneratePartsOperation() {
		this.genRequestList = new ArrayList<GenerationRequest>();
	}

	public void addGenerationRequest(GenerationRequest request) {
		synchronized (genRequestList) {
			genRequestList.add(request);
		}
	}

	public void generate(GenerationRequest request) {

		addGenerationRequest(request);

		// Create a new job for each generation request
		Job generationJob = createGenerationJob(true, false);
		generationJob.schedule();
	}
	
	public void generate(GenerationRequest[] requests, boolean respectBuildBeforeGenerate, boolean runInBackground) {

		for (int i = 0; i < requests.length; i++) {
			addGenerationRequest(requests[i]);
		}		

		// Create a new job for each generation request
		Job generationJob = createGenerationJob(respectBuildBeforeGenerate, runInBackground);
		generationJob.schedule();
	}

	public void generate(IEGLPartWrapper[] parts, boolean respectBuildBeforeGenerate, boolean runInBackground) {

		GenerationRequest gr = new GenerationRequest(parts);

		synchronized (genRequestList) {
			genRequestList.add(gr);
		}

		// Create a new job for each generation request
		Job generationJob = createGenerationJob(respectBuildBeforeGenerate, runInBackground);
		generationJob.schedule();
	}

	/**
	 * Create a job to run the part generation. This job will block the entire
	 * worskpace for writing
	 */
	private Job createGenerationJob(boolean respectBuildBeforeGenerate, final boolean runInBackground) {

		if (respectBuildBeforeGenerate && !ResourcesPlugin.getWorkspace().isAutoBuilding()) {
			//TODO EDT for now we only generate on build. no need to run a build first.
//			IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), "com.ibm.etools.egl.ui");//$NON-NLS-1$
//			boolean build = store.getBoolean("com.ibm.etools.egl.parteditor.buildBeforeGenerate");//$NON-NLS-1$
			boolean build = false;
			if (build) {
				try {
					ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}

		Job generationJob = new Job(CoreIDEPluginStrings.GeneratePartsOperation_JobName) {
			public boolean shouldRun() {
				return super.shouldRun() && !isWorkbenchClosing();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public IStatus run(IProgressMonitor monitor) {

				WorkspaceModifyOperation modifyOperation = new WorkspaceModifyOperation() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
					 */
					protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
						generateParts(monitor);
					}
				};

				try {

					modifyOperation.run(monitor);
				} catch (InvocationTargetException e) {
					Logger.log(this, "GeneratePartsOperation.createGenerationJob():  InvocationTargetException", e); //$NON-NLS-1$
				} catch (InterruptedException e) {
					Logger.log(this, "GeneratePartsOperation.createGenerationJob():  InterruptedException", e); //$NON-NLS-1$
				}
				return Status.OK_STATUS;
			}
		};

		generationJob.setRule(ResourcesPlugin.getWorkspace().getRoot());
		generationJob.setUser(!runInBackground);
		generationJob.setSystem(false);
		generationJob.setPriority(Job.LONG);

		return generationJob;
	}

	protected static IGenerationMessageRequestor createMessageRequestor() {
		return new IGenerationMessageRequestor() {
			ArrayList<IGenerationResultsMessage> list = new ArrayList<IGenerationResultsMessage>();

			boolean error = false;

			public void addMessage(IGenerationResultsMessage message) {
				list.add(message);
				if (message.isError()) {
					error = true;
				}
			}

			public void addMessages(List<IGenerationResultsMessage> list) {
				Iterator<IGenerationResultsMessage> i = list.iterator();
				while (i.hasNext()) {
					addMessage(i.next());
				}
			}

			public List<IGenerationResultsMessage> getMessages() {
				return list;
			}

			public boolean isError() {
				return error;
			}

			public void clear() {
				error = false;
				list = new ArrayList();
			}
		};
	}
	
	protected boolean isWorkbenchClosing() {
		try {
			return PlatformUI.getWorkbench().isClosing();
		} catch (RuntimeException e) {
			return false;
		}
	}

//	public CommandRequestor getRequestor() {
//		if (requestor == null) {
//			if (isDebug()) {
//				requestor = new IDECommandRequestor() {
//					public int getGenerationMode() {
//						return EGLGenerationModeSetting.DEVELOPMENT_GENERATION_MODE;
//					}
//				};
//			}
//			else {
//				requestor = new IDECommandRequestor(){
//					public int getGenerationMode() {
//						return EGLGenerationModeSetting.DEPLOYMENT_GENERATION_MODE;
//					}
//				};
//			}
//		}
//		return requestor;
//	}
	
	public void generateParts(final IProgressMonitor monitor) {

		synchronized (lock) {

			GenerationRequest genRequest = null;
			IEGLPartWrapper[] partList = null;

			while(true){
				synchronized (genRequestList) {
					if (genRequestList.size() > 0) {
						genRequest = genRequestList.remove(0);
					}else{
						break;
					}
				}
	
				if (genRequest != null) {
					partList = genRequest.getGenerationUnits();
	
					// setup the monitor
					if (monitor != null) {
						monitor.beginTask(CoreIDEPluginStrings.GeneratePartsOperation_TaskName, (partList.length * 2));
					}
					
					for (int i = 0; i < partList.length; i++) {
						if (isWorkbenchClosing()) {
							break;
						}
	
						if (monitor != null) {
							monitor.worked(1);
	
							if (monitor.isCanceled()) {
								break;
							}
						}
	
//						requestor = null;
						
						String partFile = partList[i].getPartPath();
						String partName = partList[i].getPartName();
						
						if (monitor != null) {
							monitor.subTask(CoreIDEPluginStrings.bind(CoreIDEPluginStrings.GeneratePartsOperation_SubTaskName, partName));
						}
						
						IGenerationMessageRequestor messageRequestor = createMessageRequestor();
	
						IProject project = getProject(partFile);
						if (project == null || !project.exists()) {
							EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_PROJECT_NOT_FOUND, null,
									partFile);
							messageRequestor.addMessage(message);
						} else {
							Part part = null;
							try {
								ProjectEnvironment environment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
								
								String[] packageName;
								if (EGLCore.create(project).isBinary()) {
									packageName = getPackageName(partFile, environment);
								}
								else {
									packageName = getPackageName(partFile, ProjectInfoManager.getInstance().getProjectInfo(project));
								}
	
								part = environment.findPart(InternUtil.intern(packageName), InternUtil.intern(partName));
								
								//TODO EDT check for errors of dependent parts?
								if (part != null && !part.hasCompileErrors()) {
									IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(partFile));
									if (file != null && file.exists()) {
										invokeGenerators(file, part, messageRequestor);
									}
								}
							} catch (PartNotFoundException e) {
								buildPartNotFoundMessage(e, messageRequestor, partName);
							} catch (RuntimeException e) {
								handleRuntimeException(e, messageRequestor, partName, new HashSet());
							} catch (final Exception e) {
								handleUnknownException(e, messageRequestor);
							}
						}
						if (monitor != null) {
							monitor.worked(1);
						}
	
						//TODO EDT post the results (e.g. do we want to create error markers for generation errors?)
					}
				}
			}
			
			if (monitor != null) {
				monitor.done();
			}
		}
	}
	
	protected void handleRuntimeException(RuntimeException e, IGenerationMessageRequestor messageRequestor, String partName, HashSet seen) {
		if (seen.contains(e)) {
			handleUnknownException(e, messageRequestor);
			return;
		}
		seen.add(e);

		Throwable cause = e.getCause();
		if (cause instanceof PartNotFoundException) {
			buildPartNotFoundMessage((PartNotFoundException) cause, messageRequestor, partName);
			return;
		}
		if (cause instanceof InvalidPartTypeException) {
			buildInvalidPartTypeMessage((InvalidPartTypeException) cause, messageRequestor, partName);
			return;
		}

		if (cause instanceof RuntimeException) {
			handleRuntimeException((RuntimeException) cause, messageRequestor, partName, seen);
			return;
		}

		handleUnknownException(e, messageRequestor);
		return;
	}

	protected void handleUnknownException(Exception e, IGenerationMessageRequestor messageRequestor) {
		buildExceptionMessage(e, messageRequestor);
		buildStackTraceMessages(e, messageRequestor);
		Logger.log(this, "GeneratePartsOperation.generateParts():  Error during generation", e); //$NON-NLS-1$

	}

	protected boolean hasError(IFile file) {
		if (!ResourcesPlugin.getWorkspace().isAutoBuilding()) {
			return false;
		}

		if (file == null || !file.exists()) {
			return false;
		}

		try {
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
			if (markers == null) {
				return false;
			}
			for (int i = 0; i < markers.length; i++) {
				int severity = markers[i].getAttribute(IMarker.SEVERITY, 0);
				if (severity >= IMarker.SEVERITY_ERROR) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	protected void buildPartNotFoundMessage(PartNotFoundException e, IGenerationMessageRequestor result, String partName) {
		EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_PARTNOTFOUND, null, new String[] { partName,
				e.getMessage() });
		result.addMessage(message);
	}

	protected void buildInvalidPartTypeMessage(InvalidPartTypeException e, IGenerationMessageRequestor result, String partName) {
		EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_PARTNOTFOUND, null, new String[] { partName,
				e.getMessage() });
		result.addMessage(message);
	}

	/**
	 * A project has a single generator associated with it. Look for it in the list of generators, and invoke it.
	 */
	private void invokeGenerators(IFile file, Part part, IGenerationMessageRequestor req) throws Exception {
		IGenerator[] generators = ProjectSettingsUtility.getGenerators(file);
		if (generators.length != 0) {
			IEnvironment env = ProjectEnvironmentManager.getInstance().getProjectEnvironment(file.getProject()).getIREnvironment();
			for (int i = 0; i < generators.length; i++) {
				generators[i].generate(file.getFullPath().toString(), (Part)part.clone(), env, req);
			}
		}
	}
	
//	protected static boolean checkForCompileErrors(final Part part, final IGenerationMessageRequestor msgReq,
//			final CommandRequestor commandRequestor, boolean outputErrors) {
//		return checkForCompileErrors(part, part, msgReq, commandRequestor, new HashSet(), outputErrors);
//	}
//
//	private static boolean checkForCompileErrors(final Part mainPart, final Part part, final IGenerationMessageRequestor msgReq,
//			final CommandRequestor commandRequestor, HashSet alreadyVisited, boolean outputErrors) {
//
//		if (alreadyVisited.contains(part)) {
//			return false;
//		}
//
//		alreadyVisited.add(part);
//
//		final boolean[] errors = new boolean[1];
//		if (part.hasCompileErrors()) {
//			if (outputErrors) {
//				if (part == mainPart) {
//					EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_COMPILE_ERRORS,
//							new MessageContributor(part, commandRequestor), new String[] { part.getId() });
//					msgReq.addMessage(message);
//				} else {
//					EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_COMPILE_ERRORS_IN_SUBPART,
//							new MessageContributor(part, commandRequestor), new String[] { mainPart.getId(), part.getId() });
//					msgReq.addMessage(message);
//				}
//			}
//			errors[0] = true;
//		}
//		part.accept(new AbstractVisitor() {
//			public boolean visit(Function function) {
//				if (function.hasCompileErrors()) {
//					EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_COMPILE_ERRORS_IN_SUBPART,
//							new MessageContributor(function, commandRequestor), new String[] { mainPart.getId(), function.getId() });
//					msgReq.addMessage(message);
//					errors[0] = true;
//				}
//				return false;
//			}
//		});
//
//		Part[] refParts = part.getReferencedParts();
//		if (refParts != null) {
//			for (int i = 0; i < refParts.length; i++) {
//				if (!refParts[i].isSystemPart() && refParts[i].getAnnotation("annotation") == null) {
//					if (refParts[i].getPartType() == Part.PART_RECORD || refParts[i].getPartType() == Part.PART_STRUCTURED_RECORD
//							|| refParts[i].getPartType() == Part.PART_DATAITEM || refParts[i].getPartType() == Part.PART_INTERFACE
//							|| refParts[i].getPartType() == Part.PART_EXTERNALTYPE || refParts[i].getPartType() == Part.PART_FORM) {
//						errors[0] = errors[0]
//								|| checkForCompileErrors(mainPart, refParts[i], msgReq, commandRequestor, alreadyVisited, outputErrors);
//					}
//				}
//			}
//		}
//		return errors[0];
//	}

	/**
	 * Insert the method's description here. Creation date: (1/21/2002 9:32:15
	 * PM)
	 * 
	 * @param e
	 *            java.lang.Exception
	 * @param compilationUnit
	 *            com.ibm.etools.egl.internal.compiler.env.api.CompilationUnit
	 */
	public void buildExceptionMessage(Exception e, IGenerationMessageRequestor result) {
		String text = e.getMessage();
		if (text != null) {
			EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_EXCEPTION_MESSAGE, null, text);
			result.addMessage(message);
		}
	}
	
	/**
	 * Insert the method's description here. Creation date: (1/21/2002 9:32:15
	 * PM)
	 * 
	 * @param e
	 *            java.lang.Throwable
	 * @param compilationUnit
	 *            com.ibm.etools.egl.internal.compiler.env.api.CompilationUnit
	 */
	public static void buildStackTraceMessages(Throwable e, IGenerationMessageRequestor result) {
		StringOutputBuffer buffer = new StringOutputBuffer();
		PrintWriter writer = new PrintWriter(buffer);
		e.printStackTrace(writer);
		writer.flush();
		String text = buffer.toString();
		char[] token;
		StringTokenizer tokenizer = new StringTokenizer(text, "\n\r\f"); //$NON-NLS-1$
		while (tokenizer.hasMoreElements()) {
			token = tokenizer.nextToken().toCharArray();
			StringBuffer stringBuffer = new StringBuffer();
			for (int i = 0; i < token.length; i++) {
				if (token[i] == '\t') {
					stringBuffer.append("      "); //$NON-NLS-1$
				} else {
					stringBuffer.append(token[i]);
				}
			}

			EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_EXCEPTION_STACKTRACE, null, stringBuffer
					.toString());
			result.addMessage(message);
		}
	}
	
	protected String[] getPackageName(String filename, ProjectInfo projectInfo) {
		IPath path = new Path(filename);
		path = path.removeFirstSegments(1); // project name
		path = path.removeLastSegments(1);// filename
		String[] retVal = Util.pathToStringArray(path);
		while (retVal.length > 0) {
			if (projectInfo.hasPackage(InternUtil.intern(retVal))) {
				break;
			}
			path = path.removeFirstSegments(1);// source folder
			retVal = Util.pathToStringArray(path);
		}
		return retVal;
	}
	
	protected String[] getPackageName(String filename, ProjectEnvironment env) {
		IPath path = new Path(filename);
		path = path.removeFirstSegments(1); // project name
		path = path.removeLastSegments(1);// filename
		String[] retVal = Util.pathToStringArray(path);
		while (retVal.length > 0) {
			if (env.hasPackage(InternUtil.intern(retVal))) {
				break;
			}
			path = path.removeFirstSegments(1);// source folder
			retVal = Util.pathToStringArray(path);
		}
		return retVal;
	}

	protected IProject getProject(String filename) {
		IPath path = new Path(filename);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
		return project;
	}
}
