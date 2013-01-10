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

package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.edt.ide.ui.internal.preferences.ColorProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class TextTools {	

	// Instantiate these immediately so they are ready for the editor,
	// template pages, and whatever else in the UI plugin needs them
	private ColorProvider fgColorProvider = null;
	private MultilineCommentsScanner fgMultilineCommentsScanner = null;
	private SinglelineCommentScanner fgSinglelineCommentScanner = null;
	private CodeScanner fgCodeScanner = null;
	private SQLCodeScanner fgSQLCodeScanner = null;
	private PartitionScanner fgPartitionScanner = null;
	private PreferenceListener fPreferenceListener= null;
	private IPreferenceStore preferenceStore = null;

	
	public TextTools(IPreferenceStore store){
		preferenceStore = store;
		
		fgColorProvider = new ColorProvider(preferenceStore);
		fgMultilineCommentsScanner = new MultilineCommentsScanner(fgColorProvider);
		fgSinglelineCommentScanner = new SinglelineCommentScanner(fgColorProvider);
		fgCodeScanner = new CodeScanner(fgColorProvider);
		fgSQLCodeScanner = new SQLCodeScanner(fgColorProvider);
		fgPartitionScanner = new PartitionScanner();
		fPreferenceListener= new PreferenceListener();
		preferenceStore.addPropertyChangeListener(fPreferenceListener);
	}
	
	/**
	 * Returns the scanner.
	 */
	public ITokenScanner getEGLCodeScanner() {
		return fgCodeScanner;
	}

	public ITokenScanner getEGLMultilineCommentScanner(){
		return fgMultilineCommentsScanner;
	}
	
	public ITokenScanner getEGLSinglelineCommentScanner(){
		return fgSinglelineCommentScanner;
	}
	
	/**
	 * Returns the scanner for SQL sections.
	 */
	public ITokenScanner getEGLSQLCodeScanner() {
		return fgSQLCodeScanner;
	}

	/**
	 * Returns the color provider.
	 */
	public ColorProvider getEGLColorProvider() {
		return fgColorProvider;
	}

	/**
	 * Returns the partition scanner.
	 */
	public PartitionScanner getEGLPartitionScanner() {
		return fgPartitionScanner;
	}
	
	private void adaptToPreferenceChange(PropertyChangeEvent event){
		// if affects CodeScanner, also affects SQLCodeScanner
		if (fgCodeScanner.affectsBehavior(event)) {
			fgMultilineCommentsScanner.setRules();
			fgSinglelineCommentScanner.setRules();
			fgCodeScanner.setRules();
			fgSQLCodeScanner.setRules();
		}
	}
	
	private class PreferenceListener implements IPropertyChangeListener, Preferences.IPropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			adaptToPreferenceChange(event);
		}
		public void propertyChange(Preferences.PropertyChangeEvent event) {
			adaptToPreferenceChange(new PropertyChangeEvent(event.getSource(), event.getProperty(), event.getOldValue(), event.getNewValue()));
		}
	};
	
	/**
	 * Determines whether the preference change encoded by the given event
	 * changes the behavior of one its contained components.
	 * 
	 * @param event the event to be investigated
	 * @return <code>true</code> if event causes a behavioral change
	 * @since 2.0
	 */
	public boolean affectsBehavior(PropertyChangeEvent event) {
				// Any change affecting CodeScanner will also affect SQLCodeScanner
		return  fgCodeScanner.affectsBehavior(event);
//				fgSQLCodeScanner.affectsBehavior(event) ||
//				fgPartitionScanner.affectsBehavior(event);
	}
	
	public void dispose(){

		fgColorProvider.dispose();
		fgColorProvider = null;
		fgCodeScanner = null;
		fgMultilineCommentsScanner = null;
		fgSinglelineCommentScanner = null;
		fgSQLCodeScanner = null;
		fgPartitionScanner = null;
		if (fPreferenceListener != null) {
			if (preferenceStore != null) {
				preferenceStore.removePropertyChangeListener(fPreferenceListener);
				preferenceStore = null;
			}
			fPreferenceListener = null;
		}
	}
	
	public void setupEGLDocumentPartitioner(IDocument document) {
		setupEGLDocumentPartitioner(document, IDocumentExtension3.DEFAULT_PARTITIONING);
	}

	public void setupEGLDocumentPartitioner(IDocument document, String partitioning) {
		String[] types= new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			IPartitions.EGL_MULTI_LINE_COMMENT,
			IPartitions.EGL_SINGLE_LINE_COMMENT,
			IPartitions.SQL_CONTENT_TYPE,
			IPartitions.SQL_CONDITION_CONTENT_TYPE
		};
		IDocumentPartitioner partitioner= new Partitioner(fgPartitionScanner, types);
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension3= (IDocumentExtension3) document;
			extension3.setDocumentPartitioner(partitioning, partitioner);
		} else {
			document.setDocumentPartitioner(partitioner);
		}
		partitioner.connect(document);
	}
}
