/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.outline.OutlineAdapterFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;

public class BinaryFileEditor extends EGLEditor {
	
	public static final String BINARY_FILE_EDITOR_ID = "org.eclipse.edt.ide.ui.BinaryFileEditor";
	
	private OutlineAdapterFactory factory;
	
	public BinaryFileEditor() {
		super(false);
	}
	
	protected void initializeEditor() {

//		DocumentProvider documentProvider = (DocumentProvider) EGLUI.getDocumentProvider();
//		setDocumentProvider(documentProvider);
		
		BinaryFileDocumentProvider documentProvider = new BinaryFileDocumentProvider();
		setDocumentProvider(documentProvider);

		setRangeIndicator(new DefaultRangeIndicator());

		setEditorContextMenuId("#EGLEditorContext"); //$NON-NLS-1$
		setRulerContextMenuId("#EGLRulerContext"); //$NON-NLS-1$
		setOutlinerContextMenuId("#EGLOutlinerContext"); //$NON-NLS-1$

		// ensure that the TextTools adds its listener before this editor adds its listener
		if (tools == null)
			tools = new TextTools(EDTUIPlugin.getDefault().getPreferenceStore());

		// adds PropertyChangeListener
		setPreferenceStore(createCombinedPreferenceStore());

		setSourceViewerConfiguration(new EGLSourceViewerConfiguration(tools, this));
		
		// Instantiate this factory here and cache the value since it is
		// used by the outline adapters for the outline view.
		factory = new OutlineAdapterFactory( null, this);
		
	}
	
	private IPreferenceStore createCombinedPreferenceStore() {
		List stores= new ArrayList(3);

		stores.add(EDTUIPlugin.getDefault().getPreferenceStore());
		stores.add(EditorsUI.getPreferenceStore());
		stores.add(PlatformUI.getPreferenceStore());

		return new ChainedPreferenceStore((IPreferenceStore[]) stores.toArray(new IPreferenceStore[stores.size()]));
	}
}
