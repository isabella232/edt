/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.edt.ide.ui.internal.editor.folding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.editor.IFoldingPreferenceBlock;
import org.eclipse.edt.ide.ui.internal.preferences.OverlayPreferenceStore;
import org.eclipse.edt.ide.ui.internal.preferences.OverlayPreferenceStore.OverlayKey;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FoldingPreferenceBlock implements IFoldingPreferenceBlock {

	private IPreferenceStore fStore;
	private OverlayPreferenceStore fOverlayStore;
	private OverlayKey[] fKeys;
	private Map fCheckBoxes= new HashMap();
	private Text fThreshold;
	private SelectionListener fCheckBoxListener= new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		public void widgetSelected(SelectionEvent e) {
			Button button= (Button) e.widget;
			fOverlayStore.setValue((String) fCheckBoxes.get(button), button.getSelection());
		}
	};

	private ModifyListener fThresholdListener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			Text text = (Text)e.widget;
			String str = text.getText();
			try{
				int threshold = Integer.parseInt(str);
				fOverlayStore.setValue(EDTUIPreferenceConstants.EDITOR_FOLDING_PROPERTIESBLOCKS_THRESHOLD, threshold);
			}
			catch(NumberFormatException ex){
				fOverlayStore.setValue(EDTUIPreferenceConstants.EDITOR_FOLDING_PROPERTIESBLOCKS_THRESHOLD, 1);				
			}
		}
		
	};

	public FoldingPreferenceBlock() {
		fStore= EDTUIPlugin.getDefault().getPreferenceStore();
		fKeys= createKeys();
		fOverlayStore= new OverlayPreferenceStore(fStore, fKeys);
	}

	private OverlayKey[] createKeys() {
		ArrayList overlayKeys= new ArrayList();

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_FOLDING_COMMENTS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_FOLDING_PARTS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_FOLDING_FUNCTIONS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_FOLDING_IMPORTS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_FOLDING_PARTITIONS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_FOLDING_PROPERTIESBLOCKS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.INT, EDTUIPreferenceConstants.EDITOR_FOLDING_PROPERTIESBLOCKS_THRESHOLD));

		return (OverlayKey[]) overlayKeys.toArray(new OverlayKey[overlayKeys.size()]);
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.folding.IJavaFoldingPreferences#createControl(org.eclipse.swt.widgets.Group)
	 */
	public Control createControl(Composite composite) {
		fOverlayStore.load();
		fOverlayStore.start();
		
		Composite inner= new Composite(composite, SWT.NONE);
		GridLayout layout= new GridLayout(1, true);
		layout.verticalSpacing= 3;
		layout.marginWidth= 0;
		inner.setLayout(layout);
		
		Composite prop1 = new Composite(inner, SWT.NONE);
		GridLayout layoutp = new GridLayout(3, false);
		layoutp.verticalSpacing=0;
		layoutp.horizontalSpacing=0;
		layoutp.marginWidth=0;
		prop1.setLayout(layoutp);		
		Label labelThreshold = new Label(prop1, SWT.NONE);
		labelThreshold.setText("     " + FoldingMessages.EGLFoldingPreferenceBlock_propertiesBlockThreshold); //$NON-NLS-1$
		//addCheckBox(prop1, FoldingMessages.EGLFoldingPreferenceBlock_propertiesBlockThreshold, EGLPreferenceConstants.EDITOR_FOLDING_ENABLEPROPERTIESBLOCKS, 0);
		addTextBox(prop1, EDTUIPreferenceConstants.EDITOR_FOLDING_PROPERTIESBLOCKS_THRESHOLD, 0);		

		Label label= new Label(inner, SWT.LEFT);
		label.setText(FoldingMessages.EGLFoldingPreferenceBlock_title);

		addCheckBox(inner, FoldingMessages.EGLFoldingPreferenceBlock_comments, EDTUIPreferenceConstants.EDITOR_FOLDING_COMMENTS, 0);
		addCheckBox(inner, FoldingMessages.EGLFoldingPreferenceBlock_parts, EDTUIPreferenceConstants.EDITOR_FOLDING_PARTS, 0);
		//addCheckBox(inner, FoldingMessages.EGLFoldingPreferenceBlock_subparts, EGLPreferenceConstants.EDITOR_FOLDING_SUBPARTS, 0);
		addCheckBox(inner, FoldingMessages.EGLFoldingPreferenceBlock_functions, EDTUIPreferenceConstants.EDITOR_FOLDING_FUNCTIONS, 0);
		addCheckBox(inner, FoldingMessages.EGLFoldingPreferenceBlock_imports, EDTUIPreferenceConstants.EDITOR_FOLDING_IMPORTS, 0);
		addCheckBox(inner, FoldingMessages.EGLFoldingPreferenceBlock_partitions, EDTUIPreferenceConstants.EDITOR_FOLDING_PARTITIONS, 0);
		addCheckBox(inner, FoldingMessages.EGLFoldingPreferenceBlock_propertiesBlock, EDTUIPreferenceConstants.EDITOR_FOLDING_PROPERTIESBLOCKS, 0);
		return inner;
	}

	private Text addTextBox(Composite parent, String key, int indentation) {
		fThreshold = new Text(parent, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL);
		gd.horizontalIndent= indentation;
		gd.horizontalSpan= 1;
		gd.grabExcessVerticalSpace= false;
		gd.grabExcessHorizontalSpace= false;
		gd.widthHint = 50;
		
//		label.setLayoutData(gd);
		fThreshold.setLayoutData(gd);
		fThreshold.addModifyListener(fThresholdListener);
		return fThreshold;
	}
	
	private Button addCheckBox(Composite parent, String label, String key, int indentation) {
		Button checkBox= new Button(parent, SWT.CHECK);
		checkBox.setText(label);

		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent= indentation;
		gd.horizontalSpan= 1;
		gd.grabExcessVerticalSpace= false;
		checkBox.setLayoutData(gd);
		checkBox.addSelectionListener(fCheckBoxListener);

		fCheckBoxes.put(checkBox, key);

		return checkBox;
	}

	private void initializeFields() {
		Iterator it= fCheckBoxes.keySet().iterator();
		while (it.hasNext()) {
			Button b= (Button) it.next();
			String key= (String) fCheckBoxes.get(b);
			b.setSelection(fOverlayStore.getBoolean(key));
		}
		int threshold = fOverlayStore.getInt(EDTUIPreferenceConstants.EDITOR_FOLDING_PROPERTIESBLOCKS_THRESHOLD);
		fThreshold.setText(String.valueOf(threshold));
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.folding.AbstractJavaFoldingPreferences#performOk()
	 */
	public void performOk() {
		fOverlayStore.propagate();
	}


	/*
	 * @see org.eclipse.jdt.internal.ui.text.folding.AbstractJavaFoldingPreferences#initialize()
	 */
	public void initialize() {
		initializeFields();
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.folding.AbstractJavaFoldingPreferences#performDefaults()
	 */
	public void performDefaults() {
		fOverlayStore.loadDefaults();
		initializeFields();
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.folding.AbstractJavaFoldingPreferences#dispose()
	 */
	public void dispose() {
		fOverlayStore.stop();
	}

}
