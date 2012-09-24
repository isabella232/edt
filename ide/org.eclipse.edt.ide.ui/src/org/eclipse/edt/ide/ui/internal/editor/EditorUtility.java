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
package org.eclipse.edt.ide.ui.internal.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfoRequestor;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLElementImageDescriptor;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.dialogs.SaveDirtyEditorsDialog;
import org.eclipse.edt.ide.ui.internal.outline.OutlineAdapterFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;

public class EditorUtility {
	
	public static Node getNonWhitespaceNewNodeAtOffset(int selection, IEGLDocument document) {

		// Find out what low level node is at that cursor location
		Node node = document.getNewModelNodeAtOffset(selection);

		return node;
	}	
	
	
	public static void addNodeAtOffsetToHashMap(int selection, IEGLDocument document, EGLEditor editor, HashMap hashMap) {

		// Find out what low level node is at that cursor location
		Node node = document.getNewModelNodeAtOffset(selection);
		OutlineAdapterFactory outlineAdapterFactory = editor.getOutlineAdapterFactory();

		while (node != null) {
			if (outlineAdapterFactory.hasOutlineAdapter(node))
				hashMap.put(node, node);
			node = node.getParent();
		}
	}

	// Go through the markers for this resource.  Figure out the highest
	// severity and return that value.  Also, update the error/warning hashMaps
	// that are used by the outline view to show red X's or warnings 
	public static int populateNodeErrorWarningHashMaps(EGLEditor editor) {
		editor.setNodesWithSavedErrors(new HashMap());
		editor.setNodesWithSavedWarnings(new HashMap());
		int fImageFlags = 0;

		try {
			IMarker[] eglMarkers = null;
			int searchDepth = IResource.DEPTH_ZERO;

			DocumentProvider provider = (DocumentProvider) editor.getDocumentProvider();
			IEditorInput input = editor.getEditorInput();
			int charStart;

			if (provider == null)
				return fImageFlags;

			IResource resource = provider.getUnderlyingResource(input);
			if (resource == null)
				return fImageFlags;

			eglMarkers = resource.findMarkers(IMarker.MARKER, true, searchDepth);
			if (eglMarkers.length > 0) {
				IDocument doc = provider.getDocument(input);
				if(doc instanceof IEGLDocument){
					IEGLDocument document = (IEGLDocument) doc;				
					for (int markerIndex = 0; markerIndex < eglMarkers.length; markerIndex++) {
						IMarker marker = eglMarkers[markerIndex];
						if (marker.getAttribute(IMarker.SEVERITY, 0) == IMarker.SEVERITY_WARNING) {
							if (fImageFlags == 0)
								fImageFlags = EGLElementImageDescriptor.WARNING;
							charStart = ((Integer) marker.getAttribute(IMarker.CHAR_START)).intValue();
							addNodeAtOffsetToHashMap(charStart, document, editor, editor.getNodesWithSavedWarnings());
						}
						if (marker.getAttribute(IMarker.SEVERITY, 0) == IMarker.SEVERITY_ERROR) {
							fImageFlags = EGLElementImageDescriptor.ERROR;
							charStart = ((Integer) marker.getAttribute(IMarker.CHAR_START)).intValue();
							addNodeAtOffsetToHashMap(charStart, document, editor, editor.getNodesWithSavedErrors());
						}
					}
				}
			}
		} catch (CoreException e3) {
			//Error occurred during testing the nature
		}
		
		//Force the icons to refresh only if the outline view is already created
		if (editor.primGetOutlinePage() != null) {
			editor.getOutlinePage().refresh();
		}
		
		return fImageFlags;
	}

	public static Node getCurrentNode(TextEditor editor) {
		if (editor == null) {
			return null;
		}
		// find out the current cursor location
		ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
		int offset = selection.getOffset();
		
		// Find out what node is at that cursor location
		IEGLDocument document = (IEGLDocument) editor.getDocumentProvider().getDocument(editor.getEditorInput());
		Node node = document.getNewModelNodeAtOffset(offset);
	
		return node;
	}

	public static boolean isWithinFunction(TextEditor editor) {
		Node node = getCurrentNode(editor);
		if (node == null) {
			return false;
		}
		NestedFunction embeddedFunction = getEmbeddedFunction(node);
		if (embeddedFunction != null) {
			return true;
		} 
		else {
			TopLevelFunction standAloneFunction = getStandAloneFunction(node);
			if ( standAloneFunction != null) {
				return true;
			}
		}
		return false;
	}

	public static TopLevelFunction getStandAloneFunction(Node node) {
		while (node != null && !(node instanceof TopLevelFunction)) {
			node = node.getParent();
		}
		if (node != null && node instanceof TopLevelFunction) {
			return (TopLevelFunction) node;
		}
		return null;
	}

	public static NestedFunction getEmbeddedFunction(Node node) {
		while (node != null && !(node instanceof NestedFunction)) {
			node = node.getParent();
		}
		if (node != null && node instanceof NestedFunction) {
			return (NestedFunction) node;
		}
		return null;
	}

	public static boolean saveEditors() {
		IEditorPart[] dirtyEditors = EDTUIPlugin.getDirtyEditors();
		if (dirtyEditors.length == 0) {
			return true;
		}
		if (!saveAllDirtyEditors()) {
			return false;
		}
		EDTUIPlugin.getActiveWorkbenchWindow().getWorkbench().saveAllEditors(false);
	
		return true;
	}

	public static boolean saveAllDirtyEditors() {
		SaveDirtyEditorsDialog dialog = new SaveDirtyEditorsDialog(EDTUIPlugin.getActiveWorkbenchShell());
		dialog.setTitle(UINlsStrings.SQLSaveDirtyEditorsDialogTitle);
		dialog.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return ((IEditorPart) element).getTitleImage();
			}
			public String getText(Object element) {
				return ((IEditorPart) element).getTitle();
			}
		});
		dialog.setMessage(UINlsStrings.SQLSaveDirtyEditorsDialogMessage);
		dialog.setContentProvider(new ListContentProvide());
		dialog.setInput(Arrays.asList(EDTUIPlugin.getDirtyEditors()));
		if (dialog.open() == Dialog.OK) {
			return true;
		}
	
		return false;
	}

	public static IEGLSearchScope createFileSearchScope(TextEditor editor) {
		IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
		return createSearchScope(editorInput.getFile().getProject());
	}

	public static IEGLSearchScope createProjectSearchScope(TextEditor editor) {
		IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
		return createSearchScope(editorInput.getFile().getProject());
	}

	public static IEGLSearchScope createSearchScope(IResource resource) {
		IEGLElement[] elements = new IEGLElement[1];
		elements[0] = EGLCore.create(resource);
		return SearchEngine.createEGLSearchScope(elements);
	}

	public static List searchIndex(int types, TextEditor editor, String name, int matchMode, IEGLSearchScope scope) {
		List parts = new ArrayList();
		try {
			new SearchEngine().searchAllPartNames(
				ResourcesPlugin.getWorkspace(),
				null,
				name.toCharArray(),
				matchMode,
				IEGLSearchConstants.CASE_INSENSITIVE,
				types,
				scope,
				new PartInfoRequestor(parts),
				IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
				null);
		} catch (EGLModelException e) {
			EGLLogger.log(EditorUtility.class, e);
		}
		return parts;
	}

	public static IEGLSearchScope createScope(TextEditor editor) {
		return createProjectSearchScope(editor);
	}

	public static IPart resolvePart(PartInfo partInfo, IEGLSearchScope scope) {
		IPart part;
		if (partInfo == null || scope == null) {
			return null;
		}
		try {
			part = partInfo.resolvePart(scope);
		} catch (EGLModelException e) {
			EGLLogger.log(EditorUtility.class, e);
			part = null;
		}
		return part;
	}

	protected static List getRecordParts(TextEditor editor, String name, int matchMode, IEGLSearchScope scope) {
		// Returns a list of PartDeclarationInfo that are records.
		return EditorUtility.searchIndex(IEGLSearchConstants.RECORD, editor, name, matchMode, scope);
	}
	
	public static String getPackageName(IFile file) {
    	IEGLFile eglFile = (IEGLFile) EGLCore.create(file);
    	IPackageFragment packageFragment = (IPackageFragment)eglFile.getAncestor(IEGLElement.PACKAGE_FRAGMENT);
    	if (packageFragment.isDefaultPackage()){
    		return "";
    	}
    	else{
    		return packageFragment.getElementName();
    	}
	}
	
	/**
	 * Returns an array of all editors that have an unsaved content. If the identical content is 
	 * presented in more than one editor, only one of those editor parts is part of the result.
	 * 
	 * @return an array of all dirty editor parts.
	 */
	public static IEditorPart[] getDirtyEditors() {
		Set inputs= new HashSet();
		List result= new ArrayList(0);
		IWorkbench workbench= PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
		for (int i= 0; i < windows.length; i++) {
			IWorkbenchPage[] pages= windows[i].getPages();
			for (int x= 0; x < pages.length; x++) {
				IEditorPart[] editors= pages[x].getDirtyEditors();
				for (int z= 0; z < editors.length; z++) {
					IEditorPart ep= editors[z];
					IEditorInput input= ep.getEditorInput();
					if (!inputs.contains(input)) {
						inputs.add(input);
						result.add(ep);
					}
				}
			}
		}
		return (IEditorPart[])result.toArray(new IEditorPart[result.size()]);
	}
}
