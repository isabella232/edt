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
package org.eclipse.edt.ide.ui.internal.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.refactoring.RefactoringExecutionStarter;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.ReorgPolicyFactory;
import org.eclipse.edt.ide.ui.internal.util.FileProvidingView;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchSite;

public class MoveAction extends SelectionDispatchAction {
	private org.eclipse.edt.ide.ui.internal.packageexplorer.MoveAction oldAction = new org.eclipse.edt.ide.ui.internal.packageexplorer.MoveAction();

	private class PartNameAndFile {
		String partName;

		IFile file;

		public PartNameAndFile(String partName, IFile file) {
			this.partName = partName;
			this.file = file;
		}

	}

	private EGLEditor fEditor;

	private FileProvidingView fileView;

	public MoveAction(IWorkbenchSite site, FileProvidingView fileView) {
		super(site);
		this.fileView = fileView;
		setText(UINlsStrings.Move);
	}

	public MoveAction(IWorkbenchSite site) {
		this(site, null);
	}

	public MoveAction(EGLEditor editor) {
		this(editor.getEditorSite());
		fEditor = editor;
	}

	// ---- Structured selection
	// ------------------------------------------------

	private static Node[] getNodes(IStructuredSelection selection) {

		if (selection.size() == 0) {
			return null;
		}

		Node[] nodes = new Node[selection.size()];

		Iterator i = selection.iterator();
		int index = 0;
		while (i.hasNext()) {
			Object sel = i.next();

			if (sel instanceof Node) {
				nodes[index] = (Node) sel;
			} else {
// TODO EDT Uncomment when parts ref cache is ready				
//				if (sel instanceof EGLPartsRefElementCache && !(sel instanceof EGLPartsRefElementContainerBase)) {
//					EGLPartsRefElementCache elementCache = (EGLPartsRefElementCache) sel;
//					Object element = elementCache.getElement();
//					if (element instanceof Node) {
//						nodes[index] = (Node) element;
//					} else {
//						return null;
//					}
//				} else {
					return null;
//				}
			}
			index = index + 1;
		}
		return nodes;
	}

	private static IEGLElement[] getEGLElements(IStructuredSelection selection) {
		if (selection.size() == 0) {
			return null;
		}
		
		IEGLElement[] eglFiles = new IEGLElement[selection.size()];
		int index = 0;
		Iterator i = selection.iterator(); 
		while (i.hasNext()){
			Object sel = i.next();
			if (sel instanceof IEGLFile || sel instanceof IClassFile) {
				eglFiles[index] = (IEGLElement)sel;
				index = index + 1;
			}
			else {
				return null;
			}
		}
		return eglFiles;
	}

	public void run(IStructuredSelection selection) {
		if (selection.size() == 0) {
			return;
		}

		Node[] nodes = getNodes(selection);
		if (nodes == null) {
			IEGLElement[] eglFiles = getEGLElements(selection);
			if (eglFiles == null) {
				// TODO: This is to launch a simple rename dialog for the
				// elements that are not yet supported
				// for rename refactoring, i.e. packages and source folders.
				// This should be removed when those
				// elements have rename refactoring defined.
				oldAction.selectionChanged(selection);
				if (oldAction.isEnabled()) {
					oldAction.run();
				}
				return;
			}

			try {
				run(eglFiles);
			} catch (CoreException e) {
				EGLLogger.log(this, UINlsStrings.TypeSelectionDialog_errorMessage, e);
			}
		} else {
			try {
					run(getPartNames(nodes), getFile());
			} catch (CoreException e) {
				EGLLogger.log(this, UINlsStrings.TypeSelectionDialog_errorMessage, e);
			}
		}
	}
	
	private IFile getFile() {
		if (fileView != null) {
			return fileView.getFile();
		}
		return null;
	}
	
	public boolean willLaunchOldDialog(IStructuredSelection selection) {
		return getEGLElements(selection) == null;
	}
	
	private String[] getPartNames(Node[] nodes) {
		//each node must be a Part!!!!
		String[] names = new String[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] instanceof Part) {
				names[i] = ((Part)nodes[i]).getName().getCanonicalName();
			}
			else {
				return null;
			}
		}
		return names;
	}

	// ---- text selection
	// ------------------------------------------------------------

	public void selectionChanged(ITextSelection selection) {
		// if (selection instanceof JavaTextSelection) {
		// try {
		// IJavaElement[] elements=
		// ((JavaTextSelection)selection).resolveElementAtOffset();
		// if (elements.length == 1) {
		// setEnabled(isRenameAvailable(elements[0]));
		// } else {
		// setEnabled(false);
		// }
		// } catch (CoreException e) {
		// setEnabled(false);
		// }
		// } else {
		// setEnabled(true);
		// }
	}
	
	public void selectionChanged(IStructuredSelection selection) {
		if (selection.size() == 0) {
			setEnabled(false);
			return;
		}
		
		oldAction.selectionChanged(selection);

		setEnabled(canRun(selection) || oldAction.isEnabled());
	}

	public void run(ITextSelection selection) {
		try {
			PartNameAndFile nodeAndFile = getNodeAndFile();
			if (nodeAndFile != null) {
				run(new String[] { nodeAndFile.partName }, nodeAndFile.file);
				return;
			}
		} catch (CoreException e) {
			EGLLogger.log(this, UINlsStrings.TypeSelectionDialog_errorMessage, e);
		}
		MessageDialog.openInformation(getShell(), UINlsStrings.MoveSupport_dialog_title, UINlsStrings.RenameSupport_not_available);
	}

	public boolean canRun() {
		try {
			PartNameAndFile nodeAndFile = getNodeAndFile();
			return nodeAndFile != null;

		} catch (EGLModelException e) {
			EDTUIPlugin.log(e);
		}
		return false;
	}

	public boolean canRun(IStructuredSelection selection) {
		try {
			Node[] nodes = getNodes(selection);
			if (nodes != null) {
				return isMoveAvailable(nodes);
			} else {
				Iterator i = selection.iterator();
				
				boolean foundFile = false;
				while (i.hasNext()) {
					Object sel = i.next();
					if (sel instanceof IEGLProject || sel instanceof IPackageFragmentRoot || sel instanceof IPackageFragment) {
						return false;
					}
					if (sel instanceof IEGLFile) {
						foundFile = true;
						continue;
					}
					if (sel instanceof IEGLElement) {
						if (foundFile) {
							return false;
						}
						continue;
					}
					return false;
				}
				return selection.size() > 0 && ReorgPolicyFactory.createMovePolicy(new IResource[0], getEGLElements(selection)).canEnable();
			}
		} catch (EGLModelException e) {
			EDTUIPlugin.log(e);
		} catch (CoreException e) {
			EDTUIPlugin.log(e);
		}
		return false;
	}

	private PartNameAndFile getNodeAndFile() throws EGLModelException {
		ISelection selection = fEditor.getSelectionProvider().getSelection();
		int currentPosition = ((ITextSelection) selection).getOffset();
		IEGLDocument document = (IEGLDocument) fEditor.getDocumentProvider().getDocument(fEditor.getEditorInput());
		IFile file = ((IFileEditorInput) fEditor.getEditorInput()).getFile();

		Node node = document.getNewModelNodeAtOffset(currentPosition);
		if (node instanceof SimpleName) {
			Node parent = node.getParent();
			if (parent instanceof Part && ((Part) parent).getName() == node) {
				return new PartNameAndFile(((Part) parent).getName().getCanonicalName(), file);
			}
		}

		return null;
	}

	// ---- helper methods
	// -------------------------------------------------------------------

	private void run(String[] partNames, IFile file) throws CoreException {
		IEGLFile eglFile = EGLCore.createEGLFileFrom(file);
		if (eglFile != null && partNames != null) {
			IPart[] parts = new IPart[partNames.length];
			for (int i = 0; i < partNames.length; i++) {
				parts[i] = eglFile.getPart(partNames[i]);
			}
			RefactoringExecutionStarter.startMoveRefactoring(new IResource[0], parts, getShell());
		}
	}

	private void run(IEGLElement[] eglFiles) throws CoreException {
		RefactoringExecutionStarter.startMoveRefactoring(new IResource[0], eglFiles, getShell());
	}

	private static boolean isMoveAvailable(Node[] nodes) throws CoreException {
		
		for (int i = 0; i < nodes.length; i++) {
			if (!(nodes[i] instanceof Part)) {
				return false;
			}
		}
		return true;
	}
}
