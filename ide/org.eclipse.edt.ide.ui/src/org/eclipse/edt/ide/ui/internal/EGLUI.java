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
package org.eclipse.edt.ide.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBufferFactory;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.ISourceReference;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.editor.DocumentProvider;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * Central access point for the EGL UI plug-in (id <code>"org.eclipse.edt.ide.ui"</code>).
 * This class provides static methods for:
 * <ul>
 *  <li> creating various kinds of selection dialogs to present a collection
 *       of EGL elements to the user and let them make a selection.</li>
 *  <li> opening a EGL editor on a compilation unit.</li> 
 * </ul>
 * <p>
 * This class provides static methods and fields only; it is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 */
public final class EGLUI {
	
	private EGLUI() {
		// prevent instantiation of EGLUI.
	}
	
	/**
	 * The id of the EGL action set
	 * (value <code>"org.eclipse.edt.ide.ui.EGLActionSet"</code>).
	 */
	public static final String ID_ACTION_SET= "org.eclipse.edt.ide.ui.EGLActionSet"; //$NON-NLS-1$
	
	/**
	 * The id of the EGL Wizard action set
	 * (value <code>"org.eclipse.edt.ide.ui.EGLWizardActionSet"</code>).
	 */
	public static final String ID_NON_WEB_WIZARD_ACTION_SET= "org.eclipse.edt.ide.ui.EGLProjectWizardActionSet"; //$NON-NLS-1$

	/**
	 * The editor part id of the editor that presents EGL compilation units
	 * (value <code>"org.eclipse.edt.ide.ui.CompilationUnitEditor"</code>).
	 */	
	public static final String ID_CU_EDITOR=			"org.eclipse.edt.ide.ui.CompilationUnitEditor"; //$NON-NLS-1$
	
	/**
	 * The view part id of the Packages view
	 * (value <code>"org.eclipse.edt.ide.ui.PackageExplorer"</code>).
	 * <p>
	 * When this id is used to access
	 * a view part with <code>IWorkbenchPage.findView</code> or 
	 * <code>showView</code>, the returned <code>IViewPart</code>
	 * can be safely cast to an <code>IPackagesViewPart</code>.
	 * </p>
	 *
	 * @see IPackagesViewPart
	 * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
	 * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
	 */ 
	public static final String ID_PACKAGES= 			"org.eclipse.edt.ide.ui.PackageExplorer"; //$NON-NLS-1$

	/**
	 * Opens a EGL editor on the given EGL element. The element can be a compilation unit 
	 * or class file. If there already is an open EGL editor for the given element, it is returned.
	 *
	 * @param element the input element; either a compilation unit 
	 *   (<code>ICompilationUnit</code>) or a class file (</code>IClassFile</code>)
	 * @return the editor, or </code>null</code> if wrong element type or opening failed
	 * @exception PartInitException if the editor could not be initialized
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its underlying resource
	 */
	public static IEditorPart openInEditor(IEGLElement element) throws EGLModelException, PartInitException {
		return EditorUtility.openInEditor(element);
	}

	/** 
	 * Reveals the source range of the given source reference element in the
	 * given editor. No checking is done if the editor displays a compilation unit or
	 * class file that contains the given source reference. The editor simply reveals
	 * the source range denoted by the given source reference.
	 *
	 * @param part the editor displaying the compilation unit or class file
	 * @param element the source reference element defining the source range to be revealed
	 * 
	 * @deprecated use <code>revealInEditor(IEditorPart, IEGLElement)</code> instead
	 */	
	public static void revealInEditor(IEditorPart part, ISourceReference element) {
		if (element instanceof IEGLElement)
			revealInEditor(part, (IEGLElement) element);
	}
	
	/** 
	 * Reveals the given java element  in the given editor. If the element is not an instance
	 * of <code>ISourceReference</code> this method result in a NOP. If it is a source
	 * reference no checking is done if the editor displays a compilation unit or class file that 
	 * contains the source reference element. The editor simply reveals the source range 
	 * denoted by the given element.
	 * 
	 * @param part the editor displaying a compilation unit or class file
	 * @param element the element to be revealed
	 * 
	 * @since 2.0
	 */
	public static void revealInEditor(IEditorPart part, IEGLElement element) {
		EditorUtility.revealInEditor(part, element);
	}
	 
	/**
	 * Answers the shared working copies currently registered for the EGL plug-in.
	 * Note that the returned array can include working copies that are
	 * not on the class path of a EGL project.
	 * 
	 * @return the list of shared working copies
	 * 
	 * @see org.eclipse.edt.ide.core.model.core.EGLCore#getSharedWorkingCopies(org.eclipse.edt.ide.core.model.core.IBufferFactory)
	 * @since 2.0
	 */
	public static IWorkingCopy[] getSharedWorkingCopies() {
		return EGLCore.getSharedWorkingCopies(getBufferFactory());
	}
	
	/**
	 * Answers the shared working copies that are on the class path of a EGL
	 * project currently registered for the EGL plug-in.
	 * 
	 * 
	 * @return the list of shared working copies
	 * 
	 * @see #getSharedWorkingCopies()
	 * @since 2.1
	 */
	public static IWorkingCopy[] getSharedWorkingCopiesOnEGLPath() {
		IWorkingCopy[] wcs= getSharedWorkingCopies();
		List result= new ArrayList(wcs.length);
		for (int i = 0; i < wcs.length; i++) {
			IWorkingCopy wc= wcs[i];
			if (wc instanceof IEGLElement) {
				IEGLElement je= (IEGLElement)wc;
				if (je.getEGLProject().isOnEGLPath(je)) {
					result.add(wc);
				}
			}
		}
		return (IWorkingCopy[])result.toArray(new IWorkingCopy[result.size()]);
	}
	
	/**
	 * Returns the BufferFactory for the EGL UI plug-in.
	 *
	 * @return the BufferFactory for the EGL UI plug-in
	 * 
	 * @see org.eclipse.edt.ide.core.model.core.IBufferFactory
	 * @since 2.0
	 */
	public static IBufferFactory getBufferFactory() {
		DocumentProvider provider= EDTUIPlugin.getDefault().getEGLDocumentProvider();
		if (provider != null)
			return provider.getBufferFactory();
		return null;
	}

	/**
	 * Returns the DocumentProvider used for EGL compilation units.
	 *
	 * @return the DocumentProvider for EGL compilation units.
	 * 
	 * @see IDocumentProvider
	 * @since 2.0
	 */
	public static IDocumentProvider getDocumentProvider() {
		return EDTUIPlugin.getDefault().getEGLDocumentProvider();
	}
		
}
