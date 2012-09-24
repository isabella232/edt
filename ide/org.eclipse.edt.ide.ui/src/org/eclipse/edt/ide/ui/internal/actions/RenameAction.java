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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragment;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRoot;
import org.eclipse.edt.ide.core.internal.search.matching.MatchLocator2;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.util.BoundNodeModelUtility;
import org.eclipse.edt.ide.ui.internal.editor.util.IBoundNodeRequestor;
import org.eclipse.edt.ide.ui.internal.refactoring.RefactoringExecutionStarter;
import org.eclipse.edt.ide.ui.internal.util.FileProvidingView;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchSite;

public class RenameAction extends SelectionDispatchAction {
	
	private org.eclipse.edt.ide.ui.internal.packageexplorer.RenameAction oldAction = new org.eclipse.edt.ide.ui.internal.packageexplorer.RenameAction();

	private class PartNameAndFile {
		String partName;
		IFile file;
		NestedFunction nestedFunction;
		
		public PartNameAndFile(String partName, IFile file) {
			this.partName = partName;
			this.file = file;
		}
		
		public PartNameAndFile(NestedFunction function, IFile file) {
			this.nestedFunction = function;
			this.file = file;
		}
	}

	private EGLEditor fEditor;
	private FileProvidingView fileView;
	
	public RenameAction(IWorkbenchSite site, FileProvidingView fileView) {
		super(site);
		this.fileView = fileView;
		setText(UINlsStrings.Rename);		
	}
	
	public RenameAction(IWorkbenchSite site) {
		this(site, null);		
	}
	
	public RenameAction(EGLEditor editor) {
		this(editor.getEditorSite());
		fEditor= editor;
	}

	//---- Structured selection ------------------------------------------------

	public void selectionChanged(IStructuredSelection selection) {
		if (selection.size() == 1) {
			Object firstElem = selection.getFirstElement();
			if ( firstElem instanceof EglarPackageFragment || firstElem instanceof EglarPackageFragmentRoot  || firstElem instanceof IClassFile ) {
				setEnabled(false);
				return;
			}
			oldAction.selectionChanged(selection);
			setEnabled(canRun(selection) || oldAction.isEnabled());
			return;
		}
		setEnabled(false);
	}
	
	private static Node getNode(IStructuredSelection selection) {
		if (selection.size() != 1)
			return null;
		Object first= selection.getFirstElement();
		if (first instanceof Node) {
			return (Node)first;
		}
// TODO EDT Uncomment when parts ref cache is ready		
//		else if (first instanceof EGLPartsRefElementCache && !(first instanceof EGLPartsRefElementContainerBase)) {
//			EGLPartsRefElementCache elementCache = (EGLPartsRefElementCache) first;
//			Object element = elementCache.getElement();
//			if(element instanceof Node) {
//				return (Node) element;
//			}
//		}
		
		return null;
	}
	
	private static IEGLFile getEGLFile(IStructuredSelection selection) {
		if (selection.size() != 1)
			return null;
		Object first= selection.getFirstElement();
		if (! (first instanceof IEGLFile))
			return null;
		return (IEGLFile)first;
	}
	
	public void run(IStructuredSelection selection) {
		
		if (selection.size() == 0) {
			return;
		}
		
		Node node = getNode(selection);
		if (node == null) {
			IEGLFile eglFile = getEGLFile(selection);
			if(eglFile == null) {
				//TODO: This is to launch a simple rename dialog for the elements that are not yet supported
				//for rename refactoring, i.e. packages and source folders. This should be removed when those
				//elements have rename refactoring defined.
				oldAction.selectionChanged(selection);
				if (oldAction.isEnabled()) {
					oldAction.run();
				}
				return;
			}
			
			try {
				run(eglFile);	
			} catch (CoreException e){
				EGLLogger.log(this, UINlsStrings.TypeSelectionDialog_errorMessage, e);  
			}	
		}
		else {
			try {
				if(node instanceof Part) {
					run(((Part) node).getName().getCanonicalName(), getFile());
				}
				else if(node instanceof NestedFunction) {
					NestedFunction nestedFunction = (NestedFunction) node;
					run(nestedFunction, getFile());
				}					
			} catch (CoreException e){
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
		return getEGLFile(selection) == null;
	}
	
	//---- text selection ------------------------------------------------------------

	public void selectionChanged(ITextSelection selection) {
//		if (selection instanceof JavaTextSelection) {
//			try {
//				IJavaElement[] elements= ((JavaTextSelection)selection).resolveElementAtOffset();
//				if (elements.length == 1) {
//					setEnabled(isRenameAvailable(elements[0]));
//				} else {
//					setEnabled(false);
//				}
//			} catch (CoreException e) {
//				setEnabled(false);
//			}
//		} else {
//			setEnabled(true);
//		}
	}

	public void run(ITextSelection selection) {
		try {
			PartNameAndFile nodeAndFile = getNodeAndFile();
			if (nodeAndFile != null) {
				if(nodeAndFile.nestedFunction == null) {
					run(nodeAndFile.partName, nodeAndFile.file);
				}
				else {
					run(nodeAndFile.nestedFunction, nodeAndFile.file);
				}
				return;
			}
		} catch (CoreException e) {
			EGLLogger.log(this, UINlsStrings.TypeSelectionDialog_errorMessage, e);
		}
		MessageDialog.openInformation(getShell(), UINlsStrings.RenameSupport_dialog_title, UINlsStrings.RenameSupport_not_available);
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
			Node node = getNode(selection);
			if (node != null) {
				return isRenameAvailable(node);
			}
			else {
				return selection.size() == 1 && selection.getFirstElement() instanceof IEGLFile;
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
		
		final Node node = document.getNewModelNodeAtOffset(currentPosition);
		if(node instanceof Name) {
			Node parent = node.getParent();
			if(parent instanceof Part && ((Part) parent).getName() == node) {
				return new PartNameAndFile(((Part) parent).getName().getCanonicalName(), file);
			}
			else if(parent instanceof NestedFunction) {
				//TODO: uncomment when rename of function is supported
//				return new PartNameAndFile((NestedFunction) parent, file);
				return null;
			}
			else {
				
				final PartNameAndFile[] partNameAndFile = new PartNameAndFile[]{null};
				BoundNodeModelUtility.getBoundNodeAtOffset(file, currentPosition, new IBoundNodeRequestor(){

					public void acceptNode(final Node boundPart, final Node selectedNode) {
						selectedNode.accept(new AbstractASTExpressionVisitor(){
							public boolean visitName(Name name) {
								//TODO no code currently references this (requires rename support for text selections in the EGL editor).
								// To support it, we need the WorkingCopyProjectEnvironment passed in from the WCC since the new binding model doesn't store the environment
//								partNameAndFile[0] = getPartNameAndFile(name.resolveBinding());
								return false;
							}
							
//							private IPartBinding getPartBinding(IBinding binding){
//								IPartBinding result = null;
//								
//								if(Binding.isValidBinding(binding)) {
//									//TODO: remove functionBinding part of condition when rename of function is supported
//									if(binding.isTypeBinding() && !binding.isFunctionBinding()){
//										if(((ITypeBinding)binding).isPartBinding()){
//											result = (IPartBinding)binding;
//										}
//									}else if(binding.isDataBinding()){
//										switch(((IDataBinding)binding).getKind()){
//											case IDataBinding.EXTERNALTYPE_BINDING:
//												result = (IPartBinding) ((ExternalTypeDataBinding) binding).getType();
//												break;
//										}
//									}
//								}										
//								
//								return result;
//							}
//							
//							private PartNameAndFile getPartNameAndFile(IBinding binding){
//								
//								IPartBinding partBinding = getPartBinding(binding);
//								
//								if(partBinding != null){
//									IEnvironment env = partBinding.getEnvironment();
//									if(env instanceof WorkingCopyProjectEnvironment){
//										WorkingCopyProjectEnvironment environment = (WorkingCopyProjectEnvironment) env;
//										IPartOrigin origin = environment.getPartOrigin(partBinding.getPackageName(), partBinding.getName());
//										IFile declaringFile = origin.getEGLFile();
//										return new PartNameAndFile(partBinding.getCaseSensitiveName(), declaringFile);
//									}
//								}
//								
//								return null;
//							}
							
						});
						
					}});
				
				return partNameAndFile[0];
			}
		}
		
		return null;
	}
	
	//---- helper methods -------------------------------------------------------------------

	private void run(String partName, IFile file) throws CoreException {
		IEGLFile eglFile = EGLCore.createEGLFileFrom(file);
		if(eglFile != null) {
			IPart part = eglFile.getPart(partName);
			RefactoringExecutionStarter.startRenameRefactoring(part, getShell());
		}
	}
	
	private void run(NestedFunction nestedFunction, IFile file) throws CoreException {
		IEGLFile eglFile = EGLCore.createEGLFileFrom(file);
		if(eglFile != null) {
			IPart part = eglFile.getPart(((Part) nestedFunction.getParent()).getName().getCanonicalName());
			part = new MatchLocator2(null,false,false, 0, null, null, null).createFunctionHandle(nestedFunction, part);
			RefactoringExecutionStarter.startRenameRefactoring(part, getShell());
		}
	}

	
	private void run(IEGLFile eglFile) throws CoreException {
		RefactoringExecutionStarter.startRenameRefactoring(eglFile, getShell());
	}

	private static boolean isRenameAvailable(Node node) throws CoreException {
		//TODO: remove when rename of function is supported
		if(node instanceof NestedFunction) {
			return false;
		}
		
		return node instanceof Part || node instanceof NestedFunction;
	}
}
