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
package org.eclipse.edt.ide.ui.internal.editor.folding;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.model.document.IEGLModelChangeListener;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.editor.IFoldingStructureProvider;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class FoldingStructureProvider implements IProjectionListener, IEGLModelChangeListener, IFoldingStructureProvider, IPropertyChangeListener{

	public final static String CATEGORY_EGLFOLDING = "EGL.FOLD.CUSTOM";	 //$NON-NLS-1$
	private EGLEditor fEditor;
	private IEGLDocument fEGLDocument;
	private ProjectionViewer fViewer;
	private DefaultPositionUpdater fCustomFoldingPositionUpdater;
		
	private Reconciler reconciler = new Reconciler();
	private boolean fAllowCollapsing;
	private IPreferenceStore fstore;
	//private boolean reconcilerScheduled;
	private static final int DELAY = 2000;
	
	private class Reconciler implements Runnable{

		public void run() {
			if(fEGLDocument != null)
			{
				long currentTime = System.currentTimeMillis();
				if (currentTime - fEGLDocument.getLastUpdateTime() < DELAY) {
					if(!fViewer.getControl().isDisposed())
					{
						fViewer.getControl().getDisplay().timerExec(DELAY, reconciler);
					}
					return;
				}
				
				if(!fViewer.getControl().isDisposed())
				{
					//update the pgm model to match user's typing
					//fEGLDocument.reconcile();
					
					//update the folding regions
					updateFoldingRegions(fEGLDocument.getNewModelEGLFile());
				}			
				//reconcilerScheduled = false;
			}
		}		
	}


	//default constructor
	public FoldingStructureProvider() {
		super();
		fstore = EDTUIPlugin.getDefault().getPreferenceStore();		
	}
	
	public void modelChanged() {
		if(/*!reconcilerScheduled &&*/ fViewer != null && fViewer.getControl() != null && !fViewer.getControl().isDisposed())
			fViewer.getControl().getDisplay().timerExec(DELAY, reconciler);		
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if(EDTUIPreferenceConstants.EDITOR_FOLDING_PROPERTIESBLOCKS_THRESHOLD.equals(property))
		{
			modelChanged();
		}		
	}
	
	
	public void updateFoldingRegions(File eglFile) {
		try {
			ProjectionAnnotationModel model = (ProjectionAnnotationModel) fEditor.getAdapter(ProjectionAnnotationModel.class);
			if (model == null) {
				return;
			}
	
			Hashtable currentRegions= new Hashtable();
			addFoldingRegions(currentRegions, eglFile);
			updateFoldingRegions(model, currentRegions);
		} catch (BadLocationException be) {
			//ignore as document has changed
		}
	}
	private void updateFoldingRegions(ProjectionAnnotationModel model, Hashtable currentRegions) {
		Annotation[] deletions= computeDifferences(model, currentRegions);
		
		Map additionsMap= new HashMap();
		for (Enumeration enums= currentRegions.keys(); enums.hasMoreElements();)
		{
			Object key = enums.nextElement();			//position
			Boolean IsCollapsed = (Boolean)(currentRegions.get(key));		//is it collapsed
			additionsMap.put(new ProjectionAnnotation(IsCollapsed.booleanValue()), key);
		}
		
		if ((deletions.length != 0 || additionsMap.size() != 0))
			model.modifyAnnotations(deletions, additionsMap, new Annotation[] {});
	}

	private Annotation[] computeDifferences(ProjectionAnnotationModel model, Hashtable current) {
		List deletions= new ArrayList();
		for (Iterator iter= model.getAnnotationIterator(); iter.hasNext();) {
			Object annotation= iter.next();
			if (annotation instanceof ProjectionAnnotation) {
				Position position= model.getPosition((Annotation) annotation);
				
				//ignore the ones that's in the custom category, those are controled by the user
				if(!fEGLDocument.containsPosition(CATEGORY_EGLFOLDING, position.getOffset(), position.getLength()))
				{
					if (current.containsKey(position))
						current.remove(position);
					else
						deletions.add(annotation);
				}
			}
		}
		return (Annotation[]) deletions.toArray(new Annotation[deletions.size()]);
	}
	
	private void addFoldingRegions(Hashtable regions, File eglFile) throws BadLocationException {
		FoldingVisitor visitor = new FoldingVisitor(fEGLDocument, regions, fAllowCollapsing);
		eglFile.accept(visitor);
	}	
		
	
	public void projectionEnabled() {
		// http://home.ott.oti.com/teams/wswb/anon/out/vms/index.html
		// projectionEnabled messages are not always paired with projectionDisabled
		// i.e. multiple enabled messages may be sent out.
		// we have to make sure that we disable first when getting an enable
		// message.
		projectionDisabled();
		
		if(fEditor instanceof EGLEditor)
		{
			fAllowCollapsing = true;
			initialize();
			//fEGLModelListener = new EGLModelChangeListener();
			if(fEGLDocument != null){
				fEGLDocument.addModelChangeListener(this);
				updateFoldingRegions(fEGLDocument.getNewModelEGLFile());
			}
			fAllowCollapsing = false;
		}	
		if(fstore != null)
		{
			fstore.removePropertyChangeListener(this);
			fstore.addPropertyChangeListener(this);
		}
	}

	public void projectionDisabled() {
    	try {		
			if(fEGLDocument != null)
		    {
		    	fEGLDocument.removeModelChangeListener(this);
		    	if(fEGLDocument.containsPositionCategory(CATEGORY_EGLFOLDING))
					fEGLDocument.removePositionCategory(CATEGORY_EGLFOLDING);		//remove all the positions in this category
		    	fEGLDocument.removePositionUpdater(fCustomFoldingPositionUpdater);
		    	fEGLDocument = null;
		    }
			fstore.removePropertyChangeListener(this);
		} catch (BadPositionCategoryException e) {
			EDTUIPlugin.log(e);
		}		
	}

	public void install(ITextEditor editor, ProjectionViewer viewer) {
		if(editor instanceof EGLEditor)
		{
			fEditor = (EGLEditor)editor;
			fViewer = viewer;
			fViewer.addProjectionListener(this);			
		}
		
	}

	public void uninstall() {
		if(isInstalled())
		{
			projectionDisabled();
			fViewer.removeProjectionListener(this);
			fViewer = null;
			fEditor = null;
		}		
	}
	
	protected boolean isInstalled()
	{
		return fEditor != null;
	}

	public void initialize() {
		if(!isInstalled())			
			return;
						
		//initializePreferences();
		//try
		{
			//fAllowCollapsing = true;
			IDocumentProvider provider = fEditor.getDocumentProvider();
			IDocument doc = provider.getDocument(fEditor.getEditorInput());
			if(doc instanceof IEGLDocument){
				fEGLDocument = (IEGLDocument)doc;
				
//				fCustomFoldingPositionUpdater = new DefaultPositionUpdater(CATEGORY_EGLFOLDING)
//				fEGLDocument.addPositionUpdater(fCustomFoldingPositionUpdater);
			}
			//if
		}
	}

	

	
}
