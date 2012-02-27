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
package org.eclipse.edt.ide.rui.utils;

import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.egl2mof.Egl2Mof;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.gen.javascriptdev.ide.JavaScriptDevGenerator;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IProblemRequestorFactory;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.generation.IDEContext;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.model.EGLModelStatus;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.rui.internal.lookup.PreviewIREnvironmentManager;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.SerializationException;

public class WorkingCopyGenerationOperation {
	private String EGL_SCHEMA = Type.EGL_KeyScheme + Type.KeySchemeDelimiter;

	private IFile file;
	private IProblemRequestorFactory problemRequestorFactory;
	private IPath buildDescriptorFile;
	private String buildDescriptorName;
	private IPath outputLocation;
	private String eglPath;
	private IGenerationMessageRequestor generationMessageRequestor;
	private IWorkingCopyGenerationOperationNotifier notifier;
	private boolean editingMode = false;
	
	public WorkingCopyGenerationOperation(IFile file, IProblemRequestorFactory problemRequestorFactory, IGenerationMessageRequestor generationMessageRequestor, IPath buildDescriptorFile, String buildDescriptorName, IPath outputLocation, String eglPath, IWorkingCopyGenerationOperationNotifier notifier) {
		this.file = file;
		this.problemRequestorFactory = problemRequestorFactory;
		this.generationMessageRequestor = generationMessageRequestor;
		this.buildDescriptorFile = buildDescriptorFile;
		this.buildDescriptorName = buildDescriptorName;
		this.outputLocation = outputLocation;
		this.eglPath = eglPath;
		this.notifier = notifier;
	}
	
	public void generate() throws Exception {
		final IEGLFile modelFile = (IEGLFile)EGLCore.create(file);
		final String partName = new Path(file.getName()).removeFileExtension().toString();

		final HashSet<String> generatePartList = new HashSet<String>();
		
		if(notifier.isCancelled()){ return; }
		
		IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
		
		try{
			if(sharedWorkingCopy.getPart(partName).exists()){
				
				if(notifier.isCancelled()){
					return;
				}

				final IEnvironment previewEnvironment = PreviewIREnvironmentManager.getPreviewIREnvironment(file.getProject(), outputLocation.toFile());

				//compile all Parts in file, if there's another open file with working copy content, VE won't recognize it
				WorkingCopyCompiler.getInstance().compileAllParts(file.getProject(), ((EGLFile)modelFile).getPackageName(), file, new IWorkingCopy[]{sharedWorkingCopy}, new IWorkingCopyCompileRequestor(){
			
					public void acceptResult(final WorkingCopyCompilationResult result) {
						Node boundPart = result.getBoundPart();
						
						if(notifier.isCancelled()){ 
							return;
						}
						boundPart.accept(new AbstractASTPartVisitor() {							
							public void visitPart(Part partAST) {
								//send all parts to JSDevGenerator
								String qualifiedName  = getQualifiedName(((EGLFile)modelFile).getPackageName(),partAST.getIdentifier());
								generatePartList.add(qualifiedName);
							
								if(!problemRequestorFactory.getProblemRequestor(file, partName).hasError()){
									Egl2Mof generator = new Egl2Mof(previewEnvironment);
							        EObject mof = generator.convert(partAST, new IDEContext(file, ProjectSettingsUtility.getCompiler(file.getProject())), problemRequestorFactory.getProblemRequestor(file, partName));

							        if(notifier.isCancelled()){ 
							        	return;	
							        }
							      
							        if(!problemRequestorFactory.getProblemRequestor(file, partName).hasError()){
							        	if(mof instanceof MofSerializable){
							        		try{
							        			previewEnvironment.save((MofSerializable)mof);
							        		}catch (SerializationException se){
							        			throw new BuildException("Mof SerializationException", se); //$NON-NLS-1$
							        		}
							        	}
							        }
								}
							}
						});
					}							
				}, problemRequestorFactory);
				
				generateParts(generatePartList, previewEnvironment);

			}else{
				throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.NO_ELEMENTS_TO_PROCESS));
			}
		}finally{
			sharedWorkingCopy.destroy();
		}
	}
	
	private void generateParts(HashSet<String> generatePartList, IEnvironment environment){
		Environment.pushEnv(environment);
		try{
			for (String partName : generatePartList) {
				try {
					org.eclipse.edt.mof.egl.Part part = null;
	
					EObject eObject = environment.find(PreviewIREnvironmentManager.makeEGLKey(partName));
					if(eObject instanceof org.eclipse.edt.mof.egl.Part){
						part = (org.eclipse.edt.mof.egl.Part)eObject;
						JavaScriptDevGenerator jsDevGenerator = new JavaScriptDevGenerator() {
							@Override
							public String getOutputDirectory(IResource eglFile) {
								return outputLocation.toOSString();
							}
							@Override
							public String getId() {
								return "org.eclipse.edt.ide.gen.JavaScriptDevGenProvider";
							}
							@Override
							public boolean getEditMode() {
								return true;
							}
						};
						jsDevGenerator.generate(file.getFullPath().toOSString(), (org.eclipse.edt.mof.egl.Part)part.clone(), environment, generationMessageRequestor);
					}
				} catch (PartNotFoundException e) {
					e.printStackTrace();
	//														buildPartNotFoundMessage(e, messageRequestor, partName);
				} catch (RuntimeException e) {
					e.printStackTrace();
	//														handleRuntimeException(e, messageRequestor, partName, new HashSet());
				} catch (final Exception e) {
					e.printStackTrace();
	//					handleUnknownException(e, messageRequestor);
				}
			}
		}finally{
			if(environment != null){
				Environment.popEnv();
			}
		}
	}
	
	private String getQualifiedName(String[] packageName, String partName) {
		String normalizedPckageName[] = IRFileNameUtility.toIRFileName(packageName);
		StringBuffer qualifiedName = new StringBuffer(50);
		for (String string : normalizedPckageName) {
			qualifiedName.append(string).append(".");
		}
		qualifiedName.append(partName);
		return qualifiedName.toString();
	}
	

}
