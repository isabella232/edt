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
package org.eclipse.edt.ide.ui.internal.handlers;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LocalVariableBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.core.ast.ServiceReference;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.SuperExpression;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.utils.BoundNodeLocationUtility;
import org.eclipse.edt.ide.core.internal.utils.IBoundNodeAddress;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.utils.BinaryReadOnlyFile;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.BinaryEditorInput;
import org.eclipse.edt.ide.ui.internal.editor.BinaryFileEditor;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.IEvEditor;
import org.eclipse.edt.ide.ui.internal.editor.util.BoundNodeModelUtility;
import org.eclipse.edt.ide.ui.internal.editor.util.IBoundNodeRequestor;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

public class OpenOnSelectionHandler extends EGLHandler {

	private static class LocalVariableDeclarationFinder extends AbstractASTVisitor {
		Name localVariableDeclarationName;
		LocalVariableBinding referenceBinding;
		
		public LocalVariableDeclarationFinder(LocalVariableBinding binding) {
			referenceBinding = binding;
		}

		private void handleName(Name name) {
			if(referenceBinding == name.resolveDataBinding()) {
				localVariableDeclarationName = name;
			}
		}
		
		public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
			for(Iterator iter = functionDataDeclaration.getNames().iterator(); iter.hasNext();) {
				handleName((Name) iter.next());
			}
			return false;
		}
		
		public boolean visit(ForStatement forStatement) {
			if(forStatement.hasVariableDeclaration()) {
				handleName(forStatement.getVariableDeclarationName());
			}
			return true;
		}
		
		public boolean visit(OnExceptionBlock onExceptionBlock) {
			if(onExceptionBlock.hasExceptionDeclaration()) {
				handleName(onExceptionBlock.getExceptionName());
			}
			return true;
		}
	}
	
	private boolean beep;
	private IBinding targetBinding;

	public void run() {
		beep = true;

		ISelection selection = fEditor.getSelectionProvider().getSelection();
		
		//We are working with the editor's model (no bindings) and an identical bound AST tree
		//First find the node associated with the selection in the bound AST.
		//Get the binding for the node
		//Using the bound AST tree look if the binding is in the tree.
		//If there is a match, then the binding is in the same part as the cursor position.
		//Using the bound target node, find the matching node in the editor's model and reveal the node
		//If there is no match, need to find the target in another file.
		//Get the IFile from the binding and open the file
		//Reveal the node
		
		final IFile currentFile;
		if(fEditor instanceof BinaryFileEditor){
			currentFile = (BinaryReadOnlyFile)((BinaryEditorInput)fEditor.getEditorInput()).getStorage();
		} else {
			currentFile = ((FileEditorInput)fEditor.getEditorInput()).getFile();
		}
		final IProject currentProject = currentFile.getProject();
		
		int currentPosition = ((ITextSelection) selection).getOffset();
		final IEGLDocument document = (IEGLDocument) fEditor.getDocumentProvider().getDocument(fEditor.getEditorInput());
		
		final int[][] localVariableDefinition = new int[][] {null};
		final IBoundNodeAddress[]  address = new IBoundNodeAddress[]{null};
		final String[] selectedNodeName = new String[]{null};
		BoundNodeModelUtility.getBoundNodeAtOffset(currentFile, currentPosition, new IBoundNodeRequestor(){

			public void acceptNode(final Node boundPart, final Node selectedNode) {
				
				if(!(selectedNode instanceof Part) && !(selectedNode instanceof Statement)) {
				
					selectedNode.accept(new AbstractASTExpressionVisitor(){
						public boolean visit(org.eclipse.edt.compiler.core.ast.File file) {
							//short circuit here so we do not end up visiting the imports of the file
							return false;
						}
						
						public boolean visitName(Name name){
							IBinding binding = name.resolveBinding();
							if (binding != null && binding != IBinding.NOT_FOUND_BINDING){
								if(binding.isDataBinding() && IDataBinding.LOCAL_VARIABLE_BINDING == ((IDataBinding) binding).getKind()) {
									localVariableDefinition[0] = findLocalVariableDeclaration((LocalVariableBinding) binding, boundPart);
								}
								else {
									binding = getActualBinding(binding);
									address[0] = BoundNodeLocationUtility.getInstance().createBoundNodeAddress(binding, name, currentFile.getProject());
									setProjectIfMissed(address[0]);
									
								}
								selectedNodeName[0] = name.getIdentifier();
							}				
							return false;
						}
						
						public boolean visit(org.eclipse.edt.compiler.core.ast.PrimitiveType primitiveType) {
							IBinding binding = getActualBinding(primitiveType.resolveTypeBinding());
							if (Binding.isValidBinding(binding)) {
								address[0] = BoundNodeLocationUtility.getInstance().createBoundNodeAddress(binding, null, currentFile.getProject());
								setProjectIfMissed(address[0]);
								selectedNodeName[0] = binding.getName();
							}
							return false;
						}
						
					    public boolean visit(SuperExpression superExpression){
					    	IBinding binding = getActualBinding(superExpression.resolveTypeBinding());
					    	if (Binding.isValidBinding(binding)) {
					    		address[0] = BoundNodeLocationUtility.getInstance().createBoundNodeAddress(binding, null, currentFile.getProject());
					    		setProjectIfMissed(address[0]);
					    		selectedNodeName[0] = binding.getName();
					    	}
							
							return false;
						}
						
						public boolean visit(ThisExpression thisExpression){
							IBinding binding = getActualBinding(thisExpression.resolveTypeBinding());
					    	if (Binding.isValidBinding(binding)) {
					    		address[0] = BoundNodeLocationUtility.getInstance().createBoundNodeAddress(binding, null, currentFile.getProject());
					    		setProjectIfMissed(address[0]);
					    		selectedNodeName[0] = binding.getName();
					    	}
					    	
							return false;
						}
						
						private IBinding getActualBinding(IBinding binding) {
							if (currentFile.getProject() != null && Binding.isValidBinding(binding) && binding.getActualBindingName() != null) {
								return WorkingCopyProjectEnvironmentManager.getInstance().getProjectEnvironment(currentFile.getProject()).getPartBinding(binding.getActualBindingPackage(), binding.getActualBindingName());
							}
							return binding;
						}
						
						public boolean visit(FieldAccess fieldAccess){
							IDataBinding binding = fieldAccess.resolveDataBinding();
							if(Binding.isValidBinding(binding)) {
								ITypeBinding type = binding.getType();
								if(IDataBinding.LOCAL_VARIABLE_BINDING == binding.getKind()) {
									localVariableDefinition[0] = findLocalVariableDeclaration((LocalVariableBinding) binding, boundPart);
								}
								else {
									address[0] = BoundNodeLocationUtility.getInstance().createBoundNodeAddress(binding, fieldAccess, currentFile.getProject());
									
									setProjectIfMissed(address[0]);
								}
								selectedNodeName[0] = fieldAccess.getID();
							}
							return false;
						}

						public boolean visit(StringLiteral stringLiteral){
							ITypeBinding typeBinding = stringLiteral.resolveTypeBinding();
							if (Binding.isValidBinding(typeBinding)) {
								if (typeBinding.isPartBinding()) {
									address[0] = BoundNodeLocationUtility.getInstance().createBoundNodeAddress((IPartBinding) typeBinding, stringLiteral, currentFile.getProject());
								}
							}
							return false;
						}

						public boolean visit(NestedFunction nestedFunction) {
							return false;
						}

						public boolean visit(Constructor constructor) {
							return false;
						}

						private int[] findLocalVariableDeclaration(LocalVariableBinding binding, Node boundPart) {
							final int[][] result = new int[][] {null};
							LocalVariableDeclarationFinder finder = new LocalVariableDeclarationFinder(binding);
							boundPart.accept(finder);
							if(finder.localVariableDeclarationName != null) {
								result[0] = new int[] {finder.localVariableDeclarationName.getOffset(), finder.localVariableDeclarationName.getLength()};
							}
							return result[0];
						}
						
						private void setProjectIfMissed(final IBoundNodeAddress address) {
							IFile boundFile = null;
							if(address != null) {
								boundFile = address.getDeclaringFile();
							}
							
							if(boundFile != null) {
								if (boundFile.getProject() == null 
									&& boundFile instanceof BinaryReadOnlyFile) {
									((BinaryReadOnlyFile)boundFile).setProject( currentProject );
								}
							} else {
									//for files in MOFAR
							}
						}
					} ); //selectedNode.accept
							
				}
			}
		});
		
		if(localVariableDefinition[0] != null) {
			fEditor.selectAndReveal(localVariableDefinition[0][0], localVariableDefinition[0][1]);
			beep = false;
		} else if(address[0] != null ){
			Node boundNode = BoundNodeLocationUtility.getInstance().getASTNodeForAddress(address[0], EGLUI.getSharedWorkingCopies());
			if(boundNode != null) {
				AbstractASTVisitor nodeFinder = new NodeFinderASTPartVisitor(selectedNodeName,document,address,currentFile);
				boundNode.accept(nodeFinder);
			}
		}

		if (beep)
			fEditor.getSite().getShell().getDisplay().beep();
	}
	
	private IEditorPart openInEditor(IFile file) {
		try {
			 return EditorUtility.openInEditor(file, true);
		} catch (CoreException x) {
			EGLLogger.log(this, UINlsStrings.OpenPartErrorMessage);
		}
		return null;
	}
	
	public IBinding getTargetBinding() {
		return targetBinding;
	}
	
	class NodeFinderASTPartVisitor extends AbstractASTPartVisitor {
		final String[] selectedNodeName;
		final IEGLDocument document;
		final IBoundNodeAddress[] address;
		final IFile currentFile;
		
		public NodeFinderASTPartVisitor(String[] selectedNodeName,IEGLDocument document,
				IBoundNodeAddress[] address,IFile currentFile) {
			this.selectedNodeName = selectedNodeName;
			this.document = document;
			this.address = address;
			this.currentFile = currentFile;
		}
		
		
		public boolean visit(ClassDataDeclaration classDataDeclaration) {
			for (Iterator iter = classDataDeclaration.getNames().iterator(); iter.hasNext();) {
				Name name = (Name) iter.next();

				if(name.getIdentifier() == selectedNodeName[0]){
					selectAndReveal(name);
				}
			}
			return true;						
		}
		
		public boolean visit(FunctionParameter functionParameter) {
			selectAndReveal(functionParameter.getName());
			return false;
		}
		
		public boolean visit(NestedFunction nestedFunction) {
			selectAndReveal(nestedFunction.getName());								
			return false;
		}

		public boolean visit(VariableFormField field) {
			selectAndReveal(field.getName());								
			return false;
		}

		public boolean visit(NestedForm nestedForm) {
			selectAndReveal(nestedForm.getName());								
			return false;
		}

		public boolean visit(ProgramParameter programParameter) {
			selectAndReveal(programParameter.getName());
			return false;
		}

		public boolean visit(StructureItem structureItem){
			selectAndReveal(structureItem.getName());
			return false;
		}

		public boolean visit(ServiceReference serviceReference){
			selectAndReveal(serviceReference.getName());
			return false;
		}

		public void visitPart(Part part) {
			selectAndReveal(part.getName());
		}
		
		private void selectAndReveal(Name name) {
			final IFile file = address[0].getDeclaringFile();
			if(file.equals(currentFile)){
				document.reconcile();
				EditorUtility.revealInEditor(fEditor, name);
			}else{	
				if(file.isReadOnly()) {
					// Resolve the IR part's matching name from the node.
					String irName = file instanceof BinaryReadOnlyFile ? ((BinaryReadOnlyFile)file).getIrName() : IRFileNameUtility.toIRFileName(name.getIdentifier());
					
					IEditorPart part = EditorUtility.openSourceFromEglarInBinaryEditor(file.getProject(), file.getFullPath().toString(), file.getProjectRelativePath().toString(), irName, BinaryFileEditor.BINARY_FILE_EDITOR_ID);
					int start = name.getOffset();
					int length = name.getLength();
					if(part instanceof EGLEditor){
						((EGLEditor) part).selectAndReveal(start, length);
					}else if(part instanceof IEvEditor){
						((IEvEditor) part).selectAndReveal(start, length);
					}
				} else {
					EditorUtility.revealInEditor(openInEditor(file), name);		
				}
			}
			beep = false;
		}
	}
	
}
