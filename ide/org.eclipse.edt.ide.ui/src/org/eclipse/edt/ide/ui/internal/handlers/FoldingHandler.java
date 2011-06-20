/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.folding.FoldingStructureProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class FoldingHandler extends AbstractHandler implements IHandler {

	protected IStructuredSelection fSelection;
	protected IWorkbenchSite fSite;	
	protected EGLEditor fEditor;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		// Initialize editor 
		IEditorPart editor = HandlerUtil.getActiveEditor( event );
		if( editor instanceof EGLEditor ) {
			fEditor = (EGLEditor)editor;
			if(editor != null)
			{	
				IEditorInput editorInput = editor.getEditorInput();
				if (editorInput instanceof IFileEditorInput) {
					IResource resource = ((IFileEditorInput) editorInput).getFile();
					IEGLElement element = EGLCore.create(resource);
					fSite = editor.getSite();
					fSelection = new StructuredSelection( element );
				}			
		    }			
		}			
		if( fSelection != null )
		{
			run();
		}
		return null;
	}
	
	public void run() {
		IPreferenceStore store= EDTUIPlugin.getDefault().getPreferenceStore();
		boolean current= store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_ENABLED);
		if(!current)		//enable folding
			store.setValue(EDTUIPreferenceConstants.EDITOR_FOLDING_ENABLED, !current);		
		
		addFoldingRegions();
	}
	
	private void addFoldingRegions()
	{
		try {
			ProjectionAnnotationModel model = (ProjectionAnnotationModel) fEditor.getAdapter(ProjectionAnnotationModel.class);
			if (model == null) {
				return;
			}
			
			IDocumentProvider provider = fEditor.getDocumentProvider();
			IEGLDocument doc = (IEGLDocument)(provider.getDocument(fEditor.getEditorInput()));
			
			//get the starting line and ending line of the selected text
			//use getselectionProvider, otherwise the offset is not accurate if there are text folded
			//Point highlightRange = fEditor.getSourceViewerStyledText().getSelectionRange();
			ISelection sel = fEditor.getSelectionProvider().getSelection();
			if(sel instanceof ITextSelection)
			{	
				ITextSelection selText = (ITextSelection)sel;
				int startLine = selText.getStartLine();
				int endLine = selText.getEndLine();
				
				if(endLine - startLine >= 1)
				{
					int start = doc.getLineOffset(startLine);
					int end = doc.getLineOffset(endLine) + doc.getLineLength(endLine);
					Position position = new Position(start, end-start);
				
					if(!doc.containsPositionCategory(FoldingStructureProvider.CATEGORY_EGLFOLDING))
						doc.addPositionCategory(FoldingStructureProvider.CATEGORY_EGLFOLDING);
					
					if(!doc.containsPosition(FoldingStructureProvider.CATEGORY_EGLFOLDING, position.offset, position.length))
					{					
						doc.addPosition(FoldingStructureProvider.CATEGORY_EGLFOLDING, position);						
						model.modifyAnnotationPosition(new ProjectionAnnotation(true), position);
	
						//the above line will add the position to the default category
						//remove the position from the default category, since we're handling with our own position updater				
						//doc.removePosition(position);
					}
					else{
						for (Iterator it = model.getAnnotationIterator(); it.hasNext();){
							//ProjectionAnnotation annot = (ProjectionAnnotation)it.next();
							Object obj = it.next();
							if(obj instanceof ProjectionAnnotation){
								ProjectionAnnotation annot = (ProjectionAnnotation)obj;
								Position pos = model.getPosition(annot);
								if(pos.equals(position))
									model.collapse(annot);
							}					
						}
					}
				}
			}
		} catch (BadLocationException be) {
			//ignore as document has changed
		} catch (BadPositionCategoryException e) {
			EDTUIPlugin.log(e);
		}
		
	}
}
