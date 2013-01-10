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
package org.eclipse.edt.ide.ui.internal.search;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;


public class EGLContextMenuSearchAction extends Action {
	public static final int LIMIT_DECLARATIONS = 0;
	public static final int LIMIT_REFERENCES    = 1;
	public static final int LIMIT_ALL_OCCURRENCES = 2;
	
	private Viewer viewer = null;
	private int iScope = ISearchPageContainer.WORKSPACE_SCOPE;
	private int iLimitTo = LIMIT_ALL_OCCURRENCES;
	private IWorkingSet workingset = null;
	private Node[] eglParts = null;
	private IFile declaringFile = null;
	

	public EGLContextMenuSearchAction(Viewer viewer,int scope,int limit,String title,boolean enable,IFile file,Node[] parts) {
		super(title);
		this.viewer = viewer;
		iScope = scope;
		iLimitTo = limit;
		setEnabled(enable);
		eglParts = parts;
		this.declaringFile = file;
	}
	
	
	public EGLContextMenuSearchAction(Viewer viewer,int scope,int limit,IWorkingSet wset,String title,boolean enable,IFile file,Node[] parts) {
		this(viewer,scope,limit,title,enable,file,parts);
		workingset = wset;
	}

	public void run(){
		org.eclipse.search.ui.NewSearchUI.activateSearchResultView();
		
// TODO EDT Uncomment when Parts Ref is ready		
//		EGLPartsRefElementCache viewerElement = null;
//		viewerElement = getSelectedViewerElement(viewer);
		
//		if (viewerElement == null || !viewerElement.isAssociate() || (declaringFile == null && eglParts == null))
//			return;
		
		// Setup search scope
		IEGLSearchScope scope= null;
		String scopeDescription= ""; //$NON-NLS-1$
		
		if (eglParts != null){
			scopeDescription=EGLSearchMessages.PartsScope;
			scope= SearchEngine.createEGLSearchScope(eglParts);			
						
		}
		else {
			switch (iScope) {
				case ISearchPageContainer.WORKSPACE_SCOPE:
					scopeDescription= EGLSearchMessages.WorkspaceScope;
					scope= SearchEngine.createWorkspaceScope();
					break;
				case ISearchPageContainer.SELECTED_PROJECTS_SCOPE:
					IEGLElement[] projArray = new IEGLElement[1];
					IProject project = declaringFile.getProject();
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
		}
		
		String pattern = getPackageName(declaringFile);
		if (pattern.length() > 0){
			pattern += "."; //$NON-NLS-1$
		}
		// TODO EDT Uncomment when Parts Ref is ready			
//		if (viewerElement.getElement() instanceof NestedFunction) {
//			NestedFunction func = (NestedFunction) viewerElement.getElement();
//			pattern += ((Part) func.getParent()).getName() + "." + func.getName().getCanonicalName();
//		}else{
//			pattern += viewerElement.getAssociateText();
//		}
			
		EGLSearchQuery wsJob = new EGLSearchQuery(pattern, false, IEGLSearchConstants.ALL_ELEMENTS, iLimitTo, scope, scopeDescription,true);
		NewSearchUI.runQueryInBackground(wsJob);

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
//	private String getContainerName(INode element){
//		String sRetVal = ""; //$NON-NLS-1$
//		if (element instanceof IEGLContainedElement){
//			IEGLContainer container = ((IEGLContainedElement)element).getContainer();
//			if (container instanceof IEGLNamedElement){
//				sRetVal = ((IEGLNamedElement)container).getName().getName() + "."; //$NON-NLS-1$
//			}
//		}
//		
//		return sRetVal;
//	}
	
	private static void appendWorkingSets(IMenuManager menu, Viewer viewer,int limit,boolean bEnabled,IFile file){
		IWorkingSet[] wsets = PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets();
		
		for (int i = 0; i < wsets.length;i++){
			IWorkingSet wset = wsets[i];
			menu.add(new EGLContextMenuSearchAction(viewer,ISearchPageContainer.WORKING_SET_SCOPE,limit,wset,wset.getName(),bEnabled,file,null));
		}
	}

// TODO EDT Uncomment when Parts Ref is ready
//	protected static EGLPartsRefElementCache getSelectedViewerElement(Viewer viewer){
//		EGLPartsRefElementCache viewerElement = null;
//		if(!viewer.getSelection().isEmpty()) {
//			if(viewer.getSelection()instanceof IStructuredSelection) {		
//				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
//				Object object = selection.getFirstElement();
//				if (object instanceof Node){
//					//eglElement = (INode)object;
//					viewerElement = com.ibm.etools.egl.internal.partsReference.EGLPartsRefAdapterFactory.getInstance().createAdapter(object,null);
//				}else if(object instanceof EGLPartsRefElementCache){
//					viewerElement = (EGLPartsRefElementCache)object;
//					
//				}
//			}
//		}
//		
//		if (viewerElement != null){
//			if (viewerElement.isAssociate() ){
//				viewerElement = com.ibm.etools.egl.internal.partsReference.EGLPartsRefAdapterFactory.getInstance().createAdapter(viewerElement.getAssociateObject(),null);
//				if(viewerElement != null){
//					return viewerElement;
//				}
//			}
//		}
//		
//		return null;
//	}

	public static void appendToMenu(IMenuManager menu, Viewer viewer,IFile declaringFile,Node[] parts){
// TODO EDT		
//		boolean bEnabled = getSelectedViewerElement(viewer) != null;
		boolean bEnabled = false;
		
		MenuManager refMenu= new MenuManager(EGLSearchMessages.EGLSearchActionReference);
		refMenu.add(new EGLContextMenuSearchAction(viewer,ISearchPageContainer.WORKSPACE_SCOPE,LIMIT_REFERENCES,EGLSearchMessages.EGLSearchActionWorkspace,bEnabled,declaringFile,null));
		refMenu.add(new EGLContextMenuSearchAction(viewer,ISearchPageContainer.SELECTED_PROJECTS_SCOPE,LIMIT_REFERENCES,EGLSearchMessages.EGLSearchActionProject,bEnabled && declaringFile != null,declaringFile,null));
		if (parts != null){
			refMenu.add(new EGLContextMenuSearchAction(viewer,ISearchPageContainer.SELECTED_PROJECTS_SCOPE,LIMIT_REFERENCES,EGLSearchMessages.EGLSearchActionPartslist,(bEnabled && parts != null),declaringFile,parts));
		}
		refMenu.add(new EGLContextMenuSearchAction(viewer,ISearchPageContainer.WORKING_SET_SCOPE,LIMIT_REFERENCES,EGLSearchMessages.EGLSearchActionWorkingset,bEnabled,declaringFile,null));
		refMenu.add(new Separator());
		appendWorkingSets(refMenu,viewer,LIMIT_REFERENCES,bEnabled,declaringFile);
		
		MenuManager declMenu= new MenuManager(EGLSearchMessages.EGLSearchActionDeclaration);
		declMenu.add(new EGLContextMenuSearchAction(viewer,ISearchPageContainer.WORKSPACE_SCOPE,LIMIT_DECLARATIONS,EGLSearchMessages.EGLSearchActionWorkspace,bEnabled,declaringFile,null));
		declMenu.add(new EGLContextMenuSearchAction(viewer,ISearchPageContainer.SELECTED_PROJECTS_SCOPE,LIMIT_DECLARATIONS,EGLSearchMessages.EGLSearchActionProject,bEnabled && declaringFile != null,declaringFile,null));
		if (parts != null){
			declMenu.add(new EGLContextMenuSearchAction(viewer,ISearchPageContainer.SELECTED_PROJECTS_SCOPE,LIMIT_DECLARATIONS,EGLSearchMessages.EGLSearchActionPartslist,bEnabled && parts != null,declaringFile,parts));
		}
		declMenu.add(new EGLContextMenuSearchAction(viewer,ISearchPageContainer.WORKING_SET_SCOPE,LIMIT_DECLARATIONS,EGLSearchMessages.EGLSearchActionWorkingset,bEnabled,declaringFile,null));
		declMenu.add(new Separator());
		appendWorkingSets(declMenu,viewer,LIMIT_DECLARATIONS,bEnabled,declaringFile);

		menu.add(refMenu);
		menu.add(declMenu);

	
	}

}
