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
/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.Context;
import org.eclipse.edt.compiler.SDKContext;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.EGLGenerationModeSetting;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.egl2mof.Egl2Mof;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.compiler.tools.EGLG;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.deployment.javascript.DeploymentHTMLGenerator;
import org.eclipse.edt.gen.javascript.JavaScriptGenerator;
import org.eclipse.edt.gen.javascriptdev.ide.JavaScriptDevGenerator;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IProblemRequestorFactory;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.generation.IDEContext;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.model.EGLModelStatus;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.editor.util.EGLModelUtility;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.impl.IrFactoryImpl;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.serialization.SerializationFactory;
import org.eclipse.edt.mof.serialization.Serializer;

public class WorkingCopyGenerationOperation {

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
		final String workspaceLocation = ((org.eclipse.edt.ide.core.model.IEGLProject) modelFile.getAncestor(org.eclipse.edt.ide.core.model.IEGLElement.EGL_PROJECT)).getProject().getWorkspace().getRoot().getLocation().toString();
		final org.eclipse.edt.compiler.core.ast.File fileAST = EGLModelUtility.getEGLFileAST(modelFile, EGLUI.getBufferFactory());
		final String partName = new Path(file.getName()).removeFileExtension().toString();
		
		if(notifier.isCancelled()){ return; }
		
		IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
		
		try{
			if(sharedWorkingCopy.getPart(partName).exists()){
				
				if(notifier.isCancelled()){
					return;
				}
				
				WorkingCopyCompiler.getInstance().compilePart(file.getProject(), ((EGLFile)modelFile).getPackageName(), file, new IWorkingCopy[]{sharedWorkingCopy}, partName, new IWorkingCopyCompileRequestor(){
			
					public void acceptResult(final WorkingCopyCompilationResult result) {
						Node boundPart = result.getBoundPart();	
						
						if(notifier.isCancelled()){ 
							return;
						}
						boundPart.accept(new AbstractASTPartVisitor() {							
							public void visitPart(Part partAST) {
								if(partAST.isGeneratable()) {
									if(!problemRequestorFactory.getProblemRequestor(file, partName).hasError()){
										IrFactory factory = new IrFactoryImpl();
								        Context context = new SDKContext(file.getLocation().toFile());
//								        context.setFileName(context.getFileName().substring(workspaceLocation.length()));
//								        PartGenerator generator = new Generator(factory, context, problemRequestorFactory.getProblemRequestor(file, partName));
//								        generator.setTopLevelFunctionInfos(result.getTopLevelFunctionInfos());
//								        generator.setImportDeclarations(fileAST.getImportDeclarations());
//								        partAST.accept(generator);
//								        org.eclipse.edt.mof.egl.Part part = generator.getPart();								        
								        
								        Egl2Mof generator = new Egl2Mof(ProjectEnvironmentManager.getInstance().getIREnvironment(file.getProject()));
								        EObject mof = generator.convert(partAST, new IDEContext(file), problemRequestorFactory.getProblemRequestor(file, partName));
								        org.eclipse.edt.mof.egl.Part part = null;
								        if(mof instanceof org.eclipse.edt.mof.egl.Part){
								        	part = (org.eclipse.edt.mof.egl.Part)mof;
								        }
								        
								        if(notifier.isCancelled()){ 
								        	return;	
								        }
								      
								        if(!problemRequestorFactory.getProblemRequestor(file, partName).hasError()){
								        	
								        	// Write IR to temporary directory
								        	IPath genDir = outputLocation;
								        	try{
								        		File IRFile = getIRFile(((EGLFile)modelFile).getPackageName(), partName, genDir);
								        		FileOutputStream fileOutputStream = new FileOutputStream(IRFile);
								        		try{
													BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream);
//													SerializationManagerImpl manager = new SerializationManagerImpl();
//													manager.serializePart(part, outputStream);
													
													//TODO fix format
													SerializationFactory serializationFactory = 
														SerializationFactory.Registry.INSTANCE.getFactory(ObjectStore.XML);

													Serializer serializer = serializationFactory.createSerializer();
													serializer.serialize(part);
													
													Object contents = serializer.getContents();
													byte[] bytes;
													if (contents instanceof String) {
														bytes = ((String)contents).getBytes();
													}
													else {
														bytes = (byte[])contents;
													}
													outputStream.write(bytes);
													
												}finally{
													fileOutputStream.close();
												}
												
												if(notifier.isCancelled()){
													return;
												}
												
												ProjectEnvironment environment = null;
												try {
													environment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(file.getProject());
													environment.getIREnvironment().initSystemEnvironment(environment.getSystemEnvironment()); 
													
													if (part != null && !part.hasCompileErrors()) {
														JavaScriptDevGenerator jsDevGenerator = new JavaScriptDevGenerator() {
															@Override
															protected String getOutputDirectory(IFile eglFile) {
																return outputLocation.toOSString();
															}
														};
														
														jsDevGenerator.generate(file.getFullPath().toOSString(), part, environment.getIREnvironment(), generationMessageRequestor);

													}
												} catch (PartNotFoundException e) {
													e.printStackTrace();
//													buildPartNotFoundMessage(e, messageRequestor, partName);
												} catch (RuntimeException e) {
													e.printStackTrace();
//													handleRuntimeException(e, messageRequestor, partName, new HashSet());
												} catch (final Exception e) {
													e.printStackTrace();
//													handleUnknownException(e, messageRequestor);
												}
											
//												JavaScriptGenerator javaScriptGenerator = new JavaScriptGenerator();
//												javaScriptGenerator.setRUIGenerationMode(EGLGenerationModeSetting.DEVELOPMENT_GENERATION_MODE);
//												javaScriptGenerator.setEnableEditing(editingMode);
//												javaScriptGenerator.setProjectContainerLocation(workspaceLocation);
//												
//												// Compile the IR
//												EGLG.generate(new String[]{"-eglpath", genDir + File.pathSeparator + eglPath, "-buildDescriptorFile", buildDescriptorFile.toOSString(), "-buildDescriptorName", buildDescriptorName, "-genDirectory", genDir.toOSString(), IRFile.getCanonicalPath()}, javaScriptGenerator, generationMessageRequestor);
											}catch(Exception e){
												throw new BuildException("IOException", e); //$NON-NLS-1$
											}											        	
								        }
									}
								}
								else {
									//Throw error?
								}
							}
						});
					}							
				}, problemRequestorFactory);
			}else{
				throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.NO_ELEMENTS_TO_PROCESS));
			}
		}finally{
			sharedWorkingCopy.destroy();
		}
	}
	
	private File getIRFile(String[] packageName, String partName, IPath outputLocation) throws CoreException {
		IPath packagePath = Util.stringArrayToPath(IRFileNameUtility.toIRFileName(packageName));
		outputLocation = outputLocation.append(packagePath);
		outputLocation.toFile().mkdirs(); // create the folders
		
		IPath filePath = outputLocation.append(new Path(IRFileNameUtility.toIRFileName(partName)).addFileExtension("ir"));
		
		return filePath.toFile();
	}
	
	/**
	 * Sets the editing mode.
	 */
	public void setEditingMode( boolean editingMode ){
		this.editingMode = editingMode;
	}
}
