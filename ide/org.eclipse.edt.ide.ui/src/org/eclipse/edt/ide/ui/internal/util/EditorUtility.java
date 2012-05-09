/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.util;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.core.internal.model.BinaryPart;
import org.eclipse.edt.ide.core.internal.model.ClassFile;
import org.eclipse.edt.ide.core.internal.model.util.EGLModelUtil;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.ISourceRange;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.editor.BinaryEditorInput;
import org.eclipse.edt.ide.ui.internal.editor.BinaryReadOnlyFile;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.EGLReadOnlyEditorInput;
import org.eclipse.edt.ide.ui.internal.editor.EGLReadOnlyFile;
import org.eclipse.edt.ide.ui.internal.editor.IEvEditor;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

/**
 * A number of routines for working with EGLElements in editors
 *
 * Use 'isOpenInEditor' to test if an element is already open in a editor  
 * Use 'openInEditor' to force opening an element in a editor
 * With 'getWorkingCopy' you get the working copy (element in the editor) of an element
 */
public class EditorUtility {
	
	
	public static boolean isEditorInput(Object element, IEditorPart editor) {
		if (editor != null) {
			try {
				return editor.getEditorInput().equals(getEditorInput(element));
			} catch (EGLModelException x) {
				EDTUIPlugin.log(x.getStatus());
			}
		}
		return false;
	}
	
	/** 
	 * Tests if a cu is currently shown in an editor
	 * @return the IEditorPart if shown, null if element is not open in an editor
	 */	
	public static IEditorPart isOpenInEditor(Object inputElement) {
		IEditorInput input= null;
		
		try {
			input= getEditorInput(inputElement);
		} catch (EGLModelException x) {
			EDTUIPlugin.log(x.getStatus());
		}
		
		if (input != null) {
			IWorkbenchPage p= EDTUIPlugin.getActivePage();
			if (p != null) {
				return p.findEditor(input);
			}
		}
		
		return null;
	}
	
	/**
	 * Opens a EGL editor for an element such as <code>IEGLElement</code>, <code>IFile</code>, or <code>IStorage</code>.
	 * The editor is activated by default.
	 * @return the IEditorPart or null if wrong element type or opening failed
	 */
	public static IEditorPart openInEditor(Object inputElement) throws EGLModelException, PartInitException {
		return openInEditor(inputElement, true);
	}
		
	/**
	 * Opens a EGL editor for an element (IEGLElement, IFile, IStorage...)
	 * @return the IEditorPart or null if wrong element type or opening failed
	 */
	public static IEditorPart openInEditor(Object inputElement, boolean activate) throws EGLModelException, PartInitException {
		
		if (inputElement instanceof IFile)
			return openInEditor((IFile) inputElement, activate);
		
		if(inputElement instanceof BinaryPart){
			BinaryPart part = (BinaryPart) inputElement;
			String fullyqualifiedPartName = part.getFullyQualifiedName();
			IProject project = part.getEGLProject().getProject();
//TODO EDT Fix for BinaryPart when Util is available			
//			IFile file = Util.findPartFile(fullyqualifiedPartName, EGLCore.create(project));
//			if(file != null){
//				EditorUtility.openClassFile(project, file.getFullPath().toString(), fullyqualifiedPartName, BinaryFileEditor.BINARY_FILE_EDITOR_ID);
//			} else{
//				String filePath = Util.findPartFilePath(fullyqualifiedPartName, EGLCore.create(project));
//				openClassFile(project, filePath, fullyqualifiedPartName, BinaryFileEditor.BINARY_FILE_EDITOR_ID);
//			}
		}
		
		IEditorInput input= getEditorInput(inputElement);
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput= (IFileEditorInput) input;
			return openInEditor(fileInput.getFile(), activate);
		}
		
		if (input != null)
			return openInEditor(input, getEditorID(input, inputElement), activate);
			
		return null;
	}
	
	/** 
	 * Selects a EGL Element in an editor
	 */	
	public static void revealInEditor(IEditorPart part, Node node) {
		if (node != null && (part instanceof EGLEditor || part instanceof IEvEditor)) {
			int start = node.getOffset();
			int length = node.getLength();
			if(part instanceof EGLEditor){
				((EGLEditor) part).selectAndReveal(start, length);
			}else if(part instanceof IEvEditor){
				((IEvEditor) part).selectAndReveal(start, length);
			}
		}
	}

	/** 
	 * Selects a EGL Element in an editor
	 */	
	public static void revealInEditor(IEditorPart part, IEGLElement element) {
		if (element != null && (part instanceof EGLEditor || part instanceof IEvEditor)) {
			try {
				if (element instanceof IMember) {
					ISourceRange range = ((IMember)element).getNameRange();
					int start = range.getOffset();
					int length = range.getLength() - 1;
					if(part instanceof EGLEditor){
						((EGLEditor) part).selectAndReveal(start, length);
					}else if(part instanceof IEvEditor){
						((IEvEditor) part).selectAndReveal(start, length);
					}
				}
			} catch (EGLModelException e) {}
		}
	}
	
	private static IEditorPart openInEditor(IFile file, boolean activate) throws PartInitException {
		if (file != null) {
			IWorkbenchPage p= EDTUIPlugin.getActivePage();
			if (p != null) {
				IEditorPart editorPart= IDE.openEditor(p, file, activate);
				return editorPart;
			}
		}
		return null;
	}

	private static IEditorPart openInEditor(IEditorInput input, String editorID, boolean activate) throws PartInitException {
		if (input != null) {
			IWorkbenchPage p= EDTUIPlugin.getActivePage();
			if (p != null) {
				IEditorPart editorPart= p.openEditor(input, editorID, activate);
				
				return editorPart;
			}
		}
		return null;
	}

	
	/**
	 *@deprecated	Made it public again for java debugger UI.
	 */
	public static String getEditorID(IEditorInput input, Object inputObject) {
		IEditorRegistry registry= PlatformUI.getWorkbench().getEditorRegistry();
		IEditorDescriptor descriptor= registry.getDefaultEditor(input.getName());
		if (descriptor != null)
			return descriptor.getId();
		return null;
	}

	//TODO handle cases when element is an IClassFile
	private static IEditorInput getEditorInput(IEGLElement element) throws EGLModelException {
		while (element != null) {
			if (element instanceof IWorkingCopy && ((IWorkingCopy) element).isWorkingCopy()) 
				element= ((IWorkingCopy) element).getOriginalElement();
				
			if (element instanceof IEGLFile) {
				IEGLFile unit= (IEGLFile) element;
					IResource resource= unit.getResource();
					if (resource instanceof IFile)
						return new FileEditorInput((IFile) resource);
			}
			
			element= element.getParent();
		}
		
		return null;
	}	

	public static IEditorInput getEditorInput(Object input) throws EGLModelException {
		if (input instanceof IEGLElement)
			return getEditorInput((IEGLElement) input);
			
		if (input instanceof IFile) 
			return new FileEditorInput((IFile) input);
		
		return null;
	}
	
	/**
	 * If the current active editor edits a java element return it, else
	 * return null
	 */
	public static IEGLElement getActiveEditorEGLInput() {
		IWorkbenchPage page= EDTUIPlugin.getActivePage();
		if (page != null) {
			IEditorPart part= page.getActiveEditor();
			if (part != null) {
				IEditorInput editorInput= part.getEditorInput();
				if (editorInput != null) {
					return (IEGLElement)editorInput.getAdapter(IEGLElement.class);
				}
			}
		}
		return null;	
	}
	
	/** 
	 * Gets the working copy of an compilation unit opened in an editor
	 * @param part the editor part
	 * @param cu the original compilation unit (or another working copy)
	 * @return the working copy of the compilation unit, or null if not found
	 */	
	public static IEGLFile getWorkingCopy(IEGLFile cu) {
		if (cu == null)
			return null;
		if (cu.isWorkingCopy())
			return cu;
			
		return (IEGLFile)cu.findSharedWorkingCopy(EGLUI.getBufferFactory());
	}
	
	/** 
	 * Gets the working copy of an member opened in an editor
	 *
	 * @param member the original member or a member in a working copy
	 * @return the corresponding member in the shared working copy or <code>null</code> if not found
	 */	
	public static IMember getWorkingCopy(IMember member) throws EGLModelException {
		IEGLFile cu= member.getEGLFile();
		if (cu != null) {
			IEGLFile workingCopy= getWorkingCopy(cu);
			if (workingCopy != null) {
				return EGLModelUtil.findMemberInEGLFile(workingCopy, member);
			}
		}
		return null;
	}
	
	/**
	 * Returns the compilation unit for the given java element.
	 * @param element the java element whose compilation unit is searched for
	 * @return the compilation unit of the given java element
	 */
	private static IEGLFile getEGLFile(IEGLElement element) {
		
		if (element == null)
			return null;
			
		if (element instanceof IMember)
			return ((IMember) element).getEGLFile();
		
		int type= element.getElementType();
		if (IEGLElement.EGL_FILE == type)
			return (IEGLFile) element;
		if (IEGLElement.CLASS_FILE == type)
			return null;
			
		return getEGLFile(element.getParent());
	}
	
	/** 
	 * Returns the working copy of the given java element.
	 * @param javaElement the javaElement for which the working copyshould be found
	 * @param reconcile indicates whether the working copy must be reconcile prior to searching it
	 * @return the working copy of the given element or <code>null</code> if none
	 */	
	public static IEGLElement getWorkingCopy(IEGLElement element, boolean reconcile) throws EGLModelException {
		IEGLFile unit= getEGLFile(element);
		if (unit == null)
			return null;
			
		if (unit.isWorkingCopy())
			return element;
			
		IEGLFile workingCopy= getWorkingCopy(unit);
		if (workingCopy != null) {
			if (reconcile) {
				synchronized (workingCopy) {
					workingCopy.reconcile();
					return EGLModelUtil.findInEGLFile(workingCopy, element);
				}
			} else {
					return EGLModelUtil.findInEGLFile(workingCopy, element);
			}
		}
		
		return null;
	}

	/**
	 * Maps the localized modifier name to a code in the same
	 * manner as #findModifier.
	 * 
	 * @return the SWT modifier bit, or <code>0</code> if no match was found
	 * @see findModifier
	 * @since 2.1.1
	 */
	public static int findLocalizedModifier(String token) {
		if (token == null)
			return 0;
		
		if (token.equalsIgnoreCase(Action.findModifierString(SWT.CTRL)))
			return SWT.CTRL;
		if (token.equalsIgnoreCase(Action.findModifierString(SWT.SHIFT)))
			return SWT.SHIFT;
		if (token.equalsIgnoreCase(Action.findModifierString(SWT.ALT)))
			return SWT.ALT;
		if (token.equalsIgnoreCase(Action.findModifierString(SWT.COMMAND)))
			return SWT.COMMAND;

		return 0;
	}
	
	public static IEditorPart openClassFile(final String eglarFilePath, final String irFullQualifiedFile, final String editorId){
		return openClassFile(null, eglarFilePath, irFullQualifiedFile, editorId);
	}
	
	/**
	 * If the ir file was already opened, then return null, else return the BinaryEditorInput;
	 * @param proj
	 * @param eglarFilePath
	 * @param irFullQualifiedFile
	 * @param editorId
	 * @return
	 */
	private static BinaryEditorInput getClassfileEditor(IProject proj, String eglarFilePath, String irFullQualifiedFile, String editorId) {
		BinaryReadOnlyFile storage = getBinaryReadonlyFile(proj, eglarFilePath, irFullQualifiedFile);
		
		BinaryEditorInput input = new BinaryEditorInput(storage);
		if (input != null) {
			IWorkbenchPage p= EDTUIPlugin.getActivePage();
			if (p != null) {
				IEditorPart editorPart = p.findEditor(input);
				if(editorPart != null) {
					p.bringToTop(editorPart);
					return null;
				}
			}
		}
		return input;
	}
	
	private static EGLReadOnlyEditorInput getClassfileSourceEditor(IProject proj, String eglarFilePath, String irFullQualifiedFile, String editorId) {
		EGLReadOnlyFile storage = getEglReadonlyFile(proj, eglarFilePath, irFullQualifiedFile);
		
		EGLReadOnlyEditorInput input = new EGLReadOnlyEditorInput(storage);
		if (input != null) {
			IWorkbenchPage p= EDTUIPlugin.getActivePage();
			if (p != null) {
				IEditorPart editorPart = p.findEditor(input);
				if(editorPart != null) {
					p.bringToTop(editorPart);
					return null;
				}
			}
		}
		return input;
	}
	
	public static BinaryReadOnlyFile getBinaryReadonlyFile(IProject proj, String eglarFilePath, String irFullQualifiedFile) {
		if(proj.getWorkspace().getRoot().findMember(new Path(eglarFilePath)) == null){	//external eglar file
			return new BinaryReadOnlyFile(eglarFilePath, irFullQualifiedFile, proj.getName(), true);
		} else{	
			return new BinaryReadOnlyFile(eglarFilePath, irFullQualifiedFile, proj.getName(), false);
		}
	}
	
	public static EGLReadOnlyFile getEglReadonlyFile(IProject proj, String eglarFilePath, String irFullQualifiedFile) {
		if(proj.getWorkspace().getRoot().findMember(new Path(eglarFilePath)) == null){	//external eglar file
			return new EGLReadOnlyFile(eglarFilePath, irFullQualifiedFile, proj.getName(), true);
		} else{	
			return new EGLReadOnlyFile(eglarFilePath, irFullQualifiedFile, proj.getName(), false);
		}
	}
	
//	public static void openClassFile(final IProject proj, final String eglarFilePath, final String irFullQualifiedFile, final String editorId){
//		final IWorkbenchWindow ww = EGLUIPlugin.getActiveWorkbenchWindow();
//		final BinaryEditorInput input = getClassfileEditor(proj,eglarFilePath, irFullQualifiedFile, editorId);
//		if(input == null) {
//			return;
//		}
//		Display d = ww.getShell().getDisplay();
//		d.asyncExec(new Runnable() {
//			public void run() {
//				try {
//					ww.getActivePage().openEditor(
//					input, editorId, true);
//				} catch (PartInitException e) {
//					EGLUIPlugin.log( e );
//				}
//			}
//		});
//	}
	public static IEditorPart openClassFile(final IProject proj, final String eglarFilePath, final String irFullQualifiedFile, final String editorId){
		final BinaryEditorInput input = getClassfileEditor(proj, eglarFilePath, irFullQualifiedFile, editorId);
		if(input == null) {
			return null;
		}
		if(org.eclipse.edt.ide.core.internal.model.util.Util.isBinaryProject(proj)){
			IClassFile classFile = input.getClassFile();
			if(classFile instanceof ClassFile){
				return openClassFile((ClassFile)classFile, editorId);
			}
		}
		return null;
	}
	
	private static String getClassFileSource(final ClassFile classFile, IProject proj){
		String sourceName = null;
		ISystemEnvironment sysEnv = SystemEnvironmentManager.findSystemEnvironment(proj, null);
		IEnvironment sysIREnv = sysEnv.getIREnvironment();
		String mofSignature = IRUtils.concatWithSeparator(classFile.getPackageName(), ".") + "." + classFile.getTypeName();
		String eglSignature = org.eclipse.edt.mof.egl.Type.EGL_KeyScheme + ":" + mofSignature;
		EObject irPart = null;
		try {
			irPart = sysIREnv.find(eglSignature);
			sourceName = irPart.eGet("filename").toString();
		} catch (MofObjectNotFoundException e1) {
			e1.printStackTrace();
		} catch (DeserializationException e1) {
			e1.printStackTrace();
		}
		
		return sourceName;
	}
	
	private static String getClassFilePartSignature(final ClassFile classFile, IProject proj){
		ISystemEnvironment sysEnv = SystemEnvironmentManager.findSystemEnvironment(proj, null);
		IEnvironment sysIREnv = sysEnv.getIREnvironment();
		return(IRUtils.concatWithSeparator(classFile.getPackageName(), ".") + "." + classFile.getTypeName());
	}
	
	public static IEditorPart openClassFile(final ClassFile classFile, final String editorId){
		if(classFile != null){
			IProject proj = classFile.getEGLProject().getProject();
			if(org.eclipse.edt.ide.core.internal.model.util.Util.isBinaryProject(proj)){
				IFile eglFile = ((ClassFile)classFile).getFileInSourceFolder();	//find the corresponding source file
				classFile.setSourceFileSearchRequired(false);
				if(eglFile != null){
					try {
						//currently, it always open the EGLEditor
						//TODO open it in different egl editors according to the project type (RUI/JSF/TUI/...)						
						IEditorPart editorPart = IDE.openEditor(EDTUIPlugin.getActiveWorkbenchWindow().getActivePage(), eglFile);
						classFile.setSourceFileSearchRequired(true);
						return editorPart;
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
			//1. ir not in the BP
			//or
			//2. ir doesn't have corresponding source file
			String eglarPath = classFile.getPath().toString();
			String sourceFileName = getClassFileSource(classFile, proj);
			if(null != sourceFileName && "" != sourceFileName){
				openSourceFromEglarInBinaryEditor(classFile, proj, eglarPath, sourceFileName, editorId);
			}else{
				String fullyqualifiedPartName = getClassFilePartSignature(classFile,proj);
				return EditorUtility.openClassFileInBinaryEditor(classFile, proj, eglarPath, fullyqualifiedPartName, editorId);
			}
		}
		
		return null;
	}
	
//	public static IEditorPart openClassFileInBinaryEditor(final ClassFile classFile, final IProject proj, final String eglarFilePath, final String irFullQualifiedFile){
//		return openClassFileInBinaryEditor(classFile, proj, eglarFilePath, irFullQualifiedFile, BinaryFileEditor.BINARY_FILE_EDITOR_ID);
//	}
	
	public static IEditorPart openClassFileInBinaryEditor(final ClassFile classFile, final IProject proj, final String eglarFilePath, final String irFullQualifiedFile, final String editorId){
		final IWorkbenchWindow ww = EDTUIPlugin.getActiveWorkbenchWindow();		
		BinaryEditorInput input = getClassfileEditor(proj, eglarFilePath, irFullQualifiedFile, editorId);
		input.setClassFile(classFile);
		
		if(input == null) {
			return null;
		}
		try {
			return ww.getActivePage().openEditor(
			input, editorId, true);
		} catch (PartInitException e) {
			EDTUIPlugin.log( e );
		}
		return null;
	}
	
	public static IEditorPart openSourceFromEglarInBinaryEditor(final ClassFile classFile, final IProject proj, final String eglarFilePath, final String irFullQualifiedFile, final String editorId){
		final IWorkbenchWindow ww = EDTUIPlugin.getActiveWorkbenchWindow();		
		EGLReadOnlyEditorInput input = getClassfileSourceEditor(proj, eglarFilePath, irFullQualifiedFile, editorId);
		
		if(input == null) {
			return null;
		}
		try {
			return ww.getActivePage().openEditor(
			input, editorId, true);
		} catch (PartInitException e) {
			EDTUIPlugin.log( e );
		}
		return null;
	}


}
