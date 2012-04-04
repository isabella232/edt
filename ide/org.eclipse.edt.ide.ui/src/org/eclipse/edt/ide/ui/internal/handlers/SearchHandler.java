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
package org.eclipse.edt.ide.ui.internal.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.internal.codemanipulation.AddImportOperation;
import org.eclipse.edt.ide.ui.internal.codemanipulation.OrganizeImportsOperation;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.util.BoundNodeModelUtility;
import org.eclipse.edt.ide.ui.internal.editor.util.IBoundNodeRequestor;
import org.eclipse.edt.ide.ui.internal.search.EGLSearchMessages;
import org.eclipse.edt.ide.ui.internal.search.EGLSearchQuery;
import org.eclipse.edt.ide.ui.internal.search.EGLSearchScopeFactory;
import org.eclipse.edt.ide.ui.internal.search.SearchUtil;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.part.EditorActionBarContributor;

public class SearchHandler extends EGLHandler {

	protected int iScope;
	protected int iLimitTo = IEGLSearchConstants.ALL_OCCURRENCES;
	protected IWorkingSet workingset = null;
	
	/** <code>true</code> if the query dialog is showing. */
	private boolean fIsQueryShowing= false;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		// Initialize editor if called from EGL Editor
		if(isInvokedFromEditorContext(event)){
			IEditorPart editor = getCurrentActiveEditor( event );
			if( editor instanceof EGLEditor ) {
				fEditor = (EGLEditor)editor;
				if(editor != null)
				{	
					IEditorInput editorInput = editor.getEditorInput();
					// Could be a VirtualEditorInput if coming from PageDesigners QEV
					if (editorInput instanceof IFileEditorInput) {
						IResource resource = ((IFileEditorInput) editorInput).getFile();
						IEGLElement element = EGLCore.create(resource);
						fSite = editor.getSite();
						fSelection = new StructuredSelection( element );
					}			
			    }			
			}	
		}
		
		if( fSelection != null )
		{
	    	run();
		}
		return null;
	}

	private void showOperationUnavailableDialog() {
		MessageDialog.openInformation(getShell(), EGLSearchMessages.EGLElementActionOperationUnavailableTitle, getOperationUnavailableMessage()); 
	}	

	private String getOperationUnavailableMessage() {
		return EGLSearchMessages.EGLElementActionOperationUnavailableGeneric; 
	}

	public void run() {
		performNewSearch();
	}
	
	protected Shell getShell()
	{
		return fEditor.getEditorSite().getShell();
	}
	
	protected void performNewSearch()
	{
		org.eclipse.search.ui.NewSearchUI.activateSearchResultView();		

		ISelection sel = fEditor.getSelectionProvider().getSelection();
		ITextSelection selectionRange = (ITextSelection)sel;
		//Point selectionRange = styleText.getSelectionRange();			//the selection offset is not accurate if there are text foled
		int selOffset = selectionRange.getOffset();	
				
		IFileEditorInput fileInput = (IFileEditorInput) fEditor.getEditorInput();
		IFile file = fileInput.getFile();
		String selText = selectionRange.getText().trim();
		
		BoundNodeAnalyzer bNodeAnalyzer = new BoundNodeAnalyzer(selText);
		
		BoundNodeModelUtility.getBoundNodeAtOffset(file, selOffset, bNodeAnalyzer);
		selText = bNodeAnalyzer.getExpandedSelectedText();
		
		if(bNodeAnalyzer.canOperateOnSelection()) {
			String scopeDescription = "";			 //$NON-NLS-1$
			IEGLSearchScope scope = null;
			switch(iScope)
			{
			case ISearchPageContainer.WORKSPACE_SCOPE:
				scopeDescription= EGLSearchMessages.WorkspaceScope;
				scope= SearchEngine.createWorkspaceScope();				
				break;
			case ISearchPageContainer.SELECTED_PROJECTS_SCOPE:				
				IEGLElement[] projArray = new IEGLElement[1];	
				IProject project = file.getProject();
				try {
					if (project != null && project.isAccessible() && project.hasNature(EGLCore.NATURE_ID)) {
						projArray[0] = EGLCore.create(project);
					}
				} catch (CoreException e) {
					// Since the java project is accessible, this should not happen, anyway, don't search this project
				}
				scope = SearchEngine.createEGLSearchScope(projArray);
				
				IProject[] projects= EGLSearchScopeFactory.getInstance().getProjects(scope);
				if (projects.length >= 1) {
					if (projects.length == 1)
						scopeDescription= EGLSearchMessages.bind(EGLSearchMessages.EnclosingProjectScope, projects[0].getName()); //$NON-NLS-1$
					else
						scopeDescription= EGLSearchMessages.bind(EGLSearchMessages.EnclosingProjectsScope, projects[0].getName()); //$NON-NLS-1$
				} else 
					scopeDescription= EGLSearchMessages.bind(EGLSearchMessages.EnclosingProjectScope, ""); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case ISearchPageContainer.WORKING_SET_SCOPE:
				IWorkingSet[] workingSets = null;
				if(workingset != null){
					workingSets = new IWorkingSet[1];
					workingSets[0] = workingset;
				}else{
					workingSets = EGLSearchScopeFactory.getInstance().queryWorkingSets();
				}
				
				if (workingSets == null || workingSets.length < 1)
					return ;
				
				scopeDescription=EGLSearchMessages.bind(EGLSearchMessages.WorkingSetScope, SearchUtil.toString(workingSets));
				scope= EGLSearchScopeFactory.getInstance().createEGLSearchScope(workingSets);				
				break;
			}
					
			String containerName = bNodeAnalyzer.getContainerName();
			if(containerName != null) {
				//prepend container name
				selText = containerName + "." + selText;
			}
			
			if (selText.indexOf('.')== -1){
				//prepend packagename
				String pattern = bNodeAnalyzer.packageName;
				if (pattern == null) {
					pattern = getPackageName(fileInput.getFile());
				}
				
				if (pattern.length() > 0){
					pattern += "."; //$NON-NLS-1$
				}
				
				selText = pattern + selText;
			}
			
			EGLSearchQuery query = new EGLSearchQuery(selText, 
						false, bNodeAnalyzer.getSearchFor(), iLimitTo, scope, scopeDescription,true);
	
			NewSearchUI.activateSearchResultView();
			NewSearchUI.runQueryInBackground(query);
		}
		else
		{
			showOperationUnavailableDialog();
		}
	}

	protected String getSelectionTrimedText()
	{
		ISelection sel = fEditor.getSelectionProvider().getSelection();
		ITextSelection selectionRange = (ITextSelection)sel;				
		return selectionRange.getText().trim();
	}
	
	protected static class BoundNodeAnalyzer implements IBoundNodeRequestor {
		private String selectedText;
		private String expandedSelectedText;
		private boolean canOperateOnSelection;
		private int searchFor;
		private String packageName;
		private String containerName;
		
		public BoundNodeAnalyzer(String selText) {
			this.selectedText = selText;
		}

		private boolean primCanOperateOnSelection(String selText, Node boundNode)
		{		
			if (boundNode instanceof NameType){
				boundNode = ((NameType)boundNode).getName();
			}
			
			if(boundNode instanceof Name){
				Name boundNameNode = (Name)boundNode;
				String nodeName = boundNameNode.getCanonicalName();
				nodeName = nodeName.trim();
				if(selText.equals(nodeName)){
					return true;			
				}else if (boundNameNode.isQualifiedName()){
					if (selText.equals(boundNameNode.getIdentifier())){
						return true;
					}
				}
			}
			return false;
		}
		
		private ITypeBinding getTypeBinding(Node boundNode) {
			Name boundNameNode = null;;
			if (boundNode instanceof NameType){
				boundNameNode = ((NameType)boundNode).getName();
			}else{
				boundNameNode = (Name)boundNode;
			}
			
			IBinding binding = boundNameNode.resolveBinding();
			if(binding != null && binding != IBinding.NOT_FOUND_BINDING){
				ITypeBinding typeBinding = null;
				if(binding instanceof ITypeBinding){
					typeBinding = (ITypeBinding)binding;
				}
				else if(binding instanceof IDataBinding){
					typeBinding = ((IDataBinding)binding).getType();
				}
				return typeBinding;
			}
			return null;
		}
		
		
		private int primGetSearchFor(Node boundNode) {
			int searchFor = IEGLSearchConstants.ALL_ELEMENTS;

			ITypeBinding typeBinding = getTypeBinding(boundNode);
			if (typeBinding != null) {
				int typeBindingKind = typeBinding.getKind();
				switch (typeBindingKind) {
				case ITypeBinding.PROGRAM_BINDING:
					searchFor = IEGLSearchConstants.PROGRAM_PART;
					break;
				case ITypeBinding.LIBRARY_BINDING:
					searchFor = IEGLSearchConstants.LIBRARY_PART;
					break;
				case ITypeBinding.DATAITEM_BINDING:
					searchFor = IEGLSearchConstants.ITEM_PART;
					break;
				case ITypeBinding.HANDLER_BINDING:
					searchFor = IEGLSearchConstants.HANDLER_PART;
					break;
				case ITypeBinding.FUNCTION_BINDING:
					searchFor = IEGLSearchConstants.ALL_FUNCTIONS;
					break;
				case ITypeBinding.DATATABLE_BINDING:
					searchFor = IEGLSearchConstants.TABLE_PART;
					break;
				case ITypeBinding.FORMGROUP_BINDING:
					searchFor = IEGLSearchConstants.FORMGROUP_PART;
					break;
				case ITypeBinding.SERVICE_BINDING:
					searchFor = IEGLSearchConstants.SERVICE_PART;
					break;
				case ITypeBinding.FIXED_RECORD_BINDING:
				case ITypeBinding.FLEXIBLE_RECORD_BINDING:
					searchFor = IEGLSearchConstants.RECORD_PART;
					break;
				case ITypeBinding.FORM_BINDING:
					searchFor = IEGLSearchConstants.FORM_PART;
					break;
				}
			}

			return searchFor;
		}

		
		public boolean canOperateOnSelection() {
			return canOperateOnSelection;
		}
		
		int getSearchFor() {			
			return searchFor;
		}
		
		private String getPackageName(Node node) {
			ITypeBinding type = getTypeBinding(node);
			if (type instanceof IPartBinding) {
				IPartBinding part = (IPartBinding) type;
				String[] pkg = part.getPackageName();
				if (pkg == null || pkg.length == 0) {
					return "";
				}
				StringBuffer buff = new StringBuffer();
				for (int i = 0; i < pkg.length; i++) {
					if (i>0) {
						buff.append(".");
					}
					buff.append(pkg[i]);
				}
				return buff.toString();
			}
			return null;
		}
		
		
		public void acceptNode(Node boundPart, Node selectedNode) {
			if(selectedNode instanceof Name && selectedText.length() == 0) {
				expandedSelectedText = ((Name) selectedNode).getCanonicalString();
			}
			else {
				expandedSelectedText = selectedText;
			}
			if(primCanOperateOnSelection(expandedSelectedText, selectedNode)) {
				canOperateOnSelection = true;
				searchFor = primGetSearchFor(selectedNode);
				packageName = getPackageName(selectedNode);
				
				if(selectedNode instanceof Name) {
					IBinding binding = ((Name) selectedNode).resolveBinding();
					if(binding instanceof NestedFunctionBinding) {
						containerName = ((NestedFunctionBinding) binding).getDeclaringPart().getCaseSensitiveName();
					}
				}
			}
		}
		
		public String getExpandedSelectedText() {
			return expandedSelectedText;
		}

		public String getContainerName() {
			return containerName;
		}
	}

	private String getPackageName(IFile file){
		IEGLFile eglFile = (IEGLFile)EGLCore.create(file);
		IPackageFragment packageFragment = (IPackageFragment)eglFile.getAncestor(IEGLElement.PACKAGE_FRAGMENT);
		if(packageFragment.isDefaultPackage()){
			return ""; 
		}else{
			String[] packageNameArray = packageFragment.getElementName().split("\\.");
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < packageNameArray.length; i++){
				if (i > 0){
					buf.append(".");
				}
				buf.append(packageNameArray[i]);
			}
			return buf.toString();
		}
	}

	protected void registerHelper(IEditingSupport helper) {
		if (fEditor == null)
			return;
		ISourceViewer viewer= fEditor.getViewer();
		if (viewer instanceof IEditingSupportRegistry) {
			IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
			registry.register(helper);
		}
	}
	
	protected void deregisterHelper(IEditingSupport helper) {
		if (fEditor == null)
			return;
		ISourceViewer viewer= fEditor.getViewer();
		if (viewer instanceof IEditingSupportRegistry) {
			IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
			registry.unregister(helper);
		}
	}
	
	protected void postRun(OrganizeImportsOperation op) {
		if(op instanceof AddImportOperation)
		{
			AddImportOperation addOp = (AddImportOperation)op;
			IStatus status = addOp.getStatus();
			if(!status.isOK())
				setStatusBarMessage(status.getMessage());
		}
	}
	
	protected void setStatusBarMessage(String message) {
		IEditorActionBarContributor contributor= fEditor.getEditorSite().getActionBarContributor();
		if (contributor instanceof EditorActionBarContributor) {
			IStatusLineManager manager= ((EditorActionBarContributor) contributor).getActionBars().getStatusLineManager();
			manager.setMessage(message);
		}
	}
	
	protected IEditingSupport createViewerHelper() {
		return new IEditingSupport() {
			public boolean isOriginator(DocumentEvent event, IRegion subjectRegion) {
				return true; // assume true, since we only register while we are active
			}
			public boolean ownsFocusShell() {
				return fIsQueryShowing;
			}
			
		};
	}
	
}
