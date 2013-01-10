/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.builder.NullBuildNotifier;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.ide.core.CoreIDEPluginStrings;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.Logger;
import org.eclipse.edt.ide.core.internal.builder.MarkerProblemRequestor;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;
import org.eclipse.edt.ide.core.internal.utils.StringOutputBuffer;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.InvalidPartTypeException;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.utils.NameUtile;

import com.ibm.icu.util.StringTokenizer;

/**
 * Generates a set of parts. For each part, it is passed to all the generators configured to run for its corresponding .egl file.
 * A generation queue can only be run on a single project.
 */
public class GenerationQueue {
	
	/**
	 * The parts yet to be generated.
	 */
	protected LinkedHashMap<GenerationUnitKey, GenerationUnit> pendingUnits;
	
	/**
	 * Notifier used to report progress.
	 */
	protected IBuildNotifier notifier;
	
	
	/**
	 * The ProjectInfo for the project being processed.
	 */
	protected ProjectInfo projectInfo;
	
	/**
	 * The ProjectEnvironment for the project being processed.
	 */
	protected ProjectEnvironment projectEnvironment;
	
	// Progress reporting
	protected int unitsGenerated;
    private int generateThreshold;
	private float increment;
	
	// Debug
	public long startTime;
	public static final boolean DEBUG = false;
	
	public class GenerationUnit {
        public String packageName;
        public String caseSensitiveInternedPartName;
       
        GenerationUnit(String packageName, String caseSensitiveInternedPartName) {
            this.packageName = packageName;
            this.caseSensitiveInternedPartName = caseSensitiveInternedPartName; 
        }
    }
    
    public class GenerationUnitKey {
    	private String packageName;
    	private String caseInsensitiveInternedPartName;
    	
    	public GenerationUnitKey(String packageName, String caseInsensitiveInternedPartName) {
    		this.packageName = packageName;
    		this.caseInsensitiveInternedPartName = caseInsensitiveInternedPartName;
    	}
    	public boolean equals(Object otherObject) {
    		if (this == otherObject) {
    			return true;
    		}
    		if (otherObject instanceof GenerationUnitKey) {
    			GenerationUnitKey otherGUKey = (GenerationUnitKey)otherObject;
    			return NameUtile.equals(otherGUKey.packageName, packageName) && NameUtile.equals(otherGUKey.caseInsensitiveInternedPartName, caseInsensitiveInternedPartName);
    		}
    		return false;
    	}
    	
    	public int hashCode() {
    		return caseInsensitiveInternedPartName.hashCode();
    	}
    }
    
    public GenerationQueue(IBuildNotifier notifier, IProject project) {
    	if (notifier == null) {
    		this.notifier = NullBuildNotifier.getInstance();
    	}
    	else {
    		this.notifier = notifier;
    	}
    	
    	this.pendingUnits = new LinkedHashMap<GenerationQueue.GenerationUnitKey, GenerationQueue.GenerationUnit>();
    	this.increment = 0.40f;
    	this.projectInfo = ProjectInfoManager.getInstance().getProjectInfo(project);
    	this.projectEnvironment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
    }
	
    /**
     * Adds a part to the generation queue.
     */
	public void addPart(String packageName, String caseSensitiveInternedPartName) {
		pendingUnits.put(new GenerationUnitKey(packageName, NameUtile.getAsName(caseSensitiveInternedPartName)), new GenerationUnit(packageName, caseSensitiveInternedPartName));
	}
	
	public void generate() {
		
		if (DEBUG) {
			startTime = System.currentTimeMillis();
		}
		
		initProgress();
		
		while (!pendingUnits.isEmpty()) {
			notifier.checkCancel();
			Iterator<GenerationUnit> iterator = pendingUnits.values().iterator();
            GenerationUnit genUnit = iterator.next();
            generate(genUnit);
		}
		
		if (DEBUG) {
			System.out.println("Generation Finished: [Time: " + (System.currentTimeMillis() - startTime) + ", Num Parts: " + unitsGenerated); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private void initProgress(){
		generateThreshold = pendingUnits.size();
		unitsGenerated = 0;
		if (generateThreshold > 0){
			notifier.setProgressPerEGLPart(increment/generateThreshold);
		}
		
		increment = increment/2;	
	}
	
	private void updateProgress () {
   		notifier.compiled();
   		unitsGenerated++;
   		  		
   		if (unitsGenerated == generateThreshold && !pendingUnits.isEmpty()) {
   			initProgress();
   		}
	}
	
	private void generate(GenerationUnit genUnit) {
		pendingUnits.remove(new GenerationUnitKey(genUnit.packageName, NameUtile.getAsName(genUnit.caseSensitiveInternedPartName)));
		
		notifier.subTask(CoreIDEPluginStrings.bind(CoreIDEPluginStrings.GeneratePartsOperation_SubTaskName, genUnit.caseSensitiveInternedPartName));
		
		IGenerationMessageRequestor messageRequestor = createMessageRequestor();
		
		IFile file = null;
		try {
			if (projectInfo.hasPart(genUnit.packageName, genUnit.caseSensitiveInternedPartName) != ITypeBinding.NOT_FOUND_BINDING) {
				file = projectInfo.getPartOrigin(genUnit.packageName, genUnit.caseSensitiveInternedPartName).getEGLFile();
				if (file != null && file.exists()) {
					IGenerator[] generators = ProjectSettingsUtility.getGenerators(file);
					if (generators.length != 0) {
						try {
							Environment.pushEnv(projectEnvironment.getIREnvironment());
							Part part = projectEnvironment.findPart(NameUtile.getAsName(genUnit.packageName), NameUtile.getAsName(genUnit.caseSensitiveInternedPartName));
							
							//TODO should we skip generation if dependent parts have compile errors?
							if (part != null && !part.hasCompileErrors()) {
								invokeGenerators(file, part, messageRequestor, generators, projectEnvironment.getIREnvironment());
							}
						}
						finally {
							Environment.popEnv();
						}
					}
				}
			}
		} catch (PartNotFoundException e) {
			buildPartNotFoundMessage(e, messageRequestor, genUnit.caseSensitiveInternedPartName);
		} catch (RuntimeException e) {
			handleRuntimeException(e, messageRequestor, genUnit.caseSensitiveInternedPartName, new HashSet());
		} catch (final Exception e) {
			handleUnknownException(e, messageRequestor);
		}
		
		// Delete existing and create new markers
		if (file != null && file.exists()) {
			try {
				//TODO can change this to the commented out code if we enforce 1 part per file
				//file.deleteMarkers(EDTCoreIDEPlugin.GENERATION_PROBLEM, true, IResource.DEPTH_ONE);
				IMarker[] markers = file.findMarkers(EDTCoreIDEPlugin.GENERATION_PROBLEM, true, IResource.DEPTH_ONE);
				for (IMarker marker : markers) {
					String attr = NameUtile.getAsName(marker.getAttribute(MarkerProblemRequestor.PART_NAME, "")); //$NON-NLS-1$
					if (NameUtile.equals(attr, genUnit.caseSensitiveInternedPartName)) {
						marker.delete();
					}
				}
			}
			catch (CoreException e) {
				EDTCoreIDEPlugin.log(e);
			}
			
			for (IGenerationResultsMessage msg : messageRequestor.getMessages()) {
				if (msg.isError() || msg.isWarning()) {
					try {
						IMarker marker = file.createMarker(EDTCoreIDEPlugin.GENERATION_PROBLEM);
						marker.setAttribute(IMarker.LINE_NUMBER, msg.getStartLine());
						marker.setAttribute(IMarker.SEVERITY, msg.isError() ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING);
						marker.setAttribute(IMarker.MESSAGE, msg.getBuiltMessage());
						marker.setAttribute(IMarker.CHAR_START, msg.getStartOffset());
						marker.setAttribute(IMarker.CHAR_END, msg.getEndOffset());
						
						//TODO can remove this if we enforce 1 part per file
						marker.setAttribute(MarkerProblemRequestor.PART_NAME, genUnit.caseSensitiveInternedPartName);
					}
					catch (CoreException e) {
						throw new BuildException(e);
					}
				}
			}
		}
		else {
			// Last resort - write errors to the log.
			for (IGenerationResultsMessage msg : messageRequestor.getMessages()) {
				if (msg.isError()) {
					EDTCoreIDEPlugin.logErrorMessage(msg.getBuiltMessage());
				}
			}
		}
		
		updateProgress();
	}
	
	/**
	 * A file has zero or more generators associated with it. These may be specified directly on the file, or the settings
	 * can be inherited from a parent resource (including the workspace defaults).
	 */
	private void invokeGenerators(IFile file, Part part, IGenerationMessageRequestor messageRequestor, IGenerator[] generators, IEnvironment env) throws Exception {
		for (int i = 0; i < generators.length; i++) {
			IGenerationMessageRequestor requestor = createMessageRequestor();
			try {
				generators[i].generate(file.getFullPath().toString(), (Part)part.clone(), env, requestor);
			}
			catch (RuntimeException e) {
				handleRuntimeException(e, messageRequestor, part.getCaseSensitiveName(), new HashSet());
			}
			catch (final Exception e) {
				handleUnknownException(e, messageRequestor);
			}
			messageRequestor.addMessages(requestor.getMessages());
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
			buildPartNotFoundMessage((PartNotFoundException)cause, messageRequestor, partName);
			return;
		}
		if (cause instanceof InvalidPartTypeException) {
			buildInvalidPartTypeMessage((InvalidPartTypeException)cause, messageRequestor, partName);
			return;
		}
		if (cause instanceof RuntimeException) {
			handleRuntimeException((RuntimeException)cause, messageRequestor, partName, seen);
			return;
		}

		handleUnknownException(e, messageRequestor);
	}
	
	protected void handleUnknownException(Exception e, IGenerationMessageRequestor messageRequestor) {
		buildExceptionMessage(e, messageRequestor);
		buildStackTraceMessages(e, messageRequestor);
		Logger.log(this, "GenerationQueue.generate():  Error during generation", e); //$NON-NLS-1$

	}
	
	protected void buildPartNotFoundMessage(PartNotFoundException e, IGenerationMessageRequestor result, String partName) {
		EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_PARTNOTFOUND, null,
				new String[] { partName, e.getMessage() });
		result.addMessage(message);
	}

	protected void buildInvalidPartTypeMessage(InvalidPartTypeException e, IGenerationMessageRequestor result, String partName) {
		EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_PARTNOTFOUND, null, new String[] { partName,
				e.getMessage() });
		result.addMessage(message);
	}
	
	public void buildExceptionMessage(Exception e, IGenerationMessageRequestor result) {
		String text = e.getMessage();
		if (text != null) {
			EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_EXCEPTION_MESSAGE, null, text);
			result.addMessage(message);
		}
	}
	
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

			EGLMessage message = EGLMessage.createEGLValidationErrorMessage(EGLMessage.EGLMESSAGE_EXCEPTION_STACKTRACE, null, stringBuffer.toString());
			result.addMessage(message);
		}
	}
	
	protected static IGenerationMessageRequestor createMessageRequestor() {
		return new IGenerationMessageRequestor() {
			ArrayList<IGenerationResultsMessage> list = new ArrayList<IGenerationResultsMessage>();
			
			boolean error;
			
			@Override
			public void addMessage(IGenerationResultsMessage message) {
				if (!list.contains(message)) {
					list.add(message);
					if (message.isError()) {
						error = true;
					}
				}
			}
			
			@Override
			public void addMessages(List<IGenerationResultsMessage> list) {
				Iterator<IGenerationResultsMessage> i = list.iterator();
				while (i.hasNext()) {
					addMessage(i.next());
				}
			}
			
			@Override
			public List<IGenerationResultsMessage> getMessages() {
				return list;
			}
			
			@Override
			public boolean isError() {
				return error;
			}
			
			@Override
			public void clear() {
				error = false;
				list = new ArrayList<IGenerationResultsMessage>();
			}
		};
	}
}
