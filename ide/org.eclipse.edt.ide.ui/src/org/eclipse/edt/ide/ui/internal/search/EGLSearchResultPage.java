/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
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

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.edt.ide.core.internal.model.BinaryPart;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.BinaryFileEditor;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.SearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IShowInTargetList;

public class EGLSearchResultPage extends AbstractTextSearchViewPage implements IAdaptable
{
	private static final String KEY_SORTING= "com.etools.egl.ui.search.resultpage.sorting"; //$NON-NLS-1$
	
	private EGLSearchResultContentProvider fContentProvider;
	private int fCurrentSortOrder;
	private SortAction fSortByNameAction;
	private SortAction fSortByPathAction;
    
	private EGLEditorOpener fEditorOpener= new EGLEditorOpener();
	
	private static final String[] SHOW_IN_TARGETS= new String[] { IPageLayout.ID_RES_NAV };
	private  static final IShowInTargetList SHOW_IN_TARGET_LIST= new IShowInTargetList() {
		public String[] getShowInTargetIds() {
			return SHOW_IN_TARGETS;
		}
	};
	
	public EGLSearchResultPage()
	{
		fSortByNameAction= new SortAction(EGLSearchMessages.EGLSearchResultPageSort_nameLabel, this, EGLSearchResultLabelProvider.SHOW_LABEL_PATH); //$NON-NLS-1$
		fSortByPathAction= new SortAction(EGLSearchMessages.EGLSearchResultPageSort_pathLabel, this, EGLSearchResultLabelProvider.SHOW_PATH_LABEL); //$NON-NLS-1$
	}
	
	public StructuredViewer getViewer() {
		return super.getViewer();
	}
	
	
	
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#showMatch(org.eclipse.search.ui.text.Match, int, int)
     */
	protected void showMatch(Match match, int offset, int length, boolean activate) throws PartInitException {
		Object matchElement = match.getElement();
		if(matchElement instanceof IFile){	//for EGL source search result match
			IFile file= (IFile) matchElement;
			IEditorPart editor= fEditorOpener.open(match);
			if (editor != null && activate)
				editor.getEditorSite().getPage().activate(editor);		
			if (editor instanceof EGLEditor) {
			    EGLEditor textEditor= (EGLEditor) editor;
				textEditor.selectAndReveal(offset, length);
			} else if (editor != null){
				showWithMarker(editor, file, offset, length);
			}
		}
		else if(matchElement instanceof BinaryPart){	//for EGL binary search result match
			BinaryPart part = (BinaryPart) matchElement;
			String fullyqualifiedPartName = part.getFullyQualifiedName();
			IProject project = part.getEGLProject().getProject();
			IEGLProject eglProj = EGLCore.create(project);
			IFile file = Util.findPartFile(fullyqualifiedPartName, eglProj);
			if(file != null && file.exists()){
				IEditorPart editor = EditorUtility.openClassFile(project, file.getFullPath().toString(), fullyqualifiedPartName, BinaryFileEditor.BINARY_FILE_EDITOR_ID);
			}
			else{
				String filePath = Util.findPartFilePath(fullyqualifiedPartName, eglProj);
				IEditorPart editor = EditorUtility.openClassFile(project, filePath, fullyqualifiedPartName, BinaryFileEditor.BINARY_FILE_EDITOR_ID);
			}
			
		} else if(matchElement instanceof IClassFile ) {
		  IPart type = ((IClassFile) matchElement).getPart();
		  if (type != null)
			 openInEditor(type,match);
		//EditorUtility.openClassFile((ClassFile)firstElement, BinaryFileEditor.BINARY_FILE_EDITOR_ID);
	}
	}
    
	private void showWithMarker(IEditorPart editor, IFile file, int offset, int length) throws PartInitException {
		try {
			IMarker marker= file.createMarker(SearchUI.SEARCH_MARKER);
			HashMap attributes= new HashMap(4);
			attributes.put(IMarker.CHAR_START, new Integer(offset));
			attributes.put(IMarker.CHAR_END, new Integer(offset + length));
			marker.setAttributes(attributes);
			IDE.gotoMarker(editor, marker);
			marker.delete();
		} catch (CoreException e) {
			throw new PartInitException(EGLSearchMessages.EGLSearchResultPageErrorMarker, e);
		}
	}
	
//	//for locating. may be useful in later version
//	private void showWithMarker(IEditorPart editor, IResource resource, int offset, int length) throws PartInitException {
//		try {
//			IMarker marker= resource.createMarker(SearchUI.SEARCH_MARKER);
//			HashMap attributes= new HashMap(4);
//			attributes.put(IMarker.CHAR_START, new Integer(offset));
//			attributes.put(IMarker.CHAR_END, new Integer(offset + length));
//			marker.setAttributes(attributes);
//			IDE.gotoMarker(editor, marker);
//			marker.delete();
//		} catch (CoreException e) {
//			throw new PartInitException(EGLSearchMessages.EGLSearchResultPageErrorMarker, e);
//		}
//	}
//	//</ChongYuan>
	
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#elementsChanged(java.lang.Object[])
     */
    protected void elementsChanged(Object[] objects)
    {
		if (fContentProvider != null)
			fContentProvider.elementsChanged(objects);
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#clear()
     */
    protected void clear()
    {
		if (fContentProvider != null)
			fContentProvider.clear();
    }
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
     */
	protected void configureTreeViewer(TreeViewer viewer) {
		viewer.setUseHashlookup(true);		
		viewer.setLabelProvider(new DecoratingLabelProvider(new EGLSearchResultLabelProvider(this, EGLSearchResultLabelProvider.SHOW_LABEL), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));		
		viewer.setContentProvider(new EGLSearchResultTreeContentProvider(viewer));
		fContentProvider= (EGLSearchResultContentProvider) viewer.getContentProvider();
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTableViewer(org.eclipse.jface.viewers.TableViewer)
     */  
	protected void configureTableViewer(TableViewer viewer) {
		viewer.setUseHashlookup(true);		
		viewer.setLabelProvider(new DecoratingLabelProvider(new EGLSearchResultLabelProvider(this, EGLSearchResultLabelProvider.SHOW_LABEL), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));		
		viewer.setContentProvider(new EGLSearchResultTableContentProvider(viewer));
		setSortOrder(fCurrentSortOrder);
		fContentProvider= (EGLSearchResultContentProvider) viewer.getContentProvider();
	}
    
	protected void fillContextMenu(IMenuManager mgr) 
	{
		super.fillContextMenu(mgr);
		addSortActions(mgr);
	}
	
	private void addSortActions(IMenuManager mgr) {
		if (getLayout() != FLAG_LAYOUT_FLAT)
			return;
		MenuManager sortMenu= new MenuManager(EGLSearchMessages.EGLSearchResultPageSort_byLabel);
		sortMenu.add(fSortByNameAction);
		sortMenu.add(fSortByPathAction);
		
		fSortByNameAction.setChecked(fCurrentSortOrder == fSortByNameAction.getSortOrder());
		fSortByPathAction.setChecked(fCurrentSortOrder == fSortByPathAction.getSortOrder());
		
		mgr.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, sortMenu);
	}
	
	public void setSortOrder(int sortOrder) {
		fCurrentSortOrder= sortOrder;
		StructuredViewer viewer= getViewer();
		DecoratingLabelProvider lpWrapper= (DecoratingLabelProvider) viewer.getLabelProvider();
		((EGLSearchResultLabelProvider)lpWrapper.getLabelProvider()).setOrder(sortOrder);
		if (sortOrder == EGLSearchResultLabelProvider.SHOW_LABEL_PATH) {
			viewer.setSorter(new NameSorter());
		} else {
			viewer.setSorter(new PathSorter());
		}
		getSettings().put(KEY_SORTING, fCurrentSortOrder);
	}
	
	public void restoreState(IMemento memento) {
		super.restoreState(memento);
		try {
			fCurrentSortOrder= getSettings().getInt(KEY_SORTING);
		} catch (NumberFormatException e) {
			fCurrentSortOrder= fSortByNameAction.getSortOrder();
		}
		if (memento != null) {
			Integer value= memento.getInteger(KEY_SORTING);
			if (value != null)
				fCurrentSortOrder= value.intValue();
		}
	}
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putInteger(KEY_SORTING, fCurrentSortOrder);
	}	
    
	public Object getAdapter(Class adapter) {
		if (IShowInTargetList.class.equals(adapter)) {
			return SHOW_IN_TARGET_LIST;
		}
		return null;
	}
	@Override
	protected void handleOpen(OpenEvent event) {
		Object firstElement= ((IStructuredSelection)event.getSelection()).getFirstElement();
		if (firstElement instanceof IEGLFile ||
				firstElement instanceof IMember) {
			if (getDisplayedMatchCount(firstElement) == 0) {
				try {
					fEditorOpener.open((Match) firstElement);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return;
			}
		} 
		super.handleOpen(event);
	}
	
	private boolean openInEditor(IPart type,Match match) {
		boolean beep = false;
		try {
			IEditorPart part= EditorUtility.openInEditor(type, true);
			EditorUtility.revealInEditor(part, match);
		} catch (CoreException x) {
			beep = true;
			EGLLogger.log(this, UINlsStrings.OpenPartErrorMessage);
		}
		return beep;
	}
}
