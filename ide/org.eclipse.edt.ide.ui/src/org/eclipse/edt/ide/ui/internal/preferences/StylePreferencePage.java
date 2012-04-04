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
package org.eclipse.edt.ide.ui.internal.preferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.DocumentProvider;
import org.eclipse.edt.ide.ui.internal.editor.EGLSourceViewerConfiguration;
import org.eclipse.edt.ide.ui.internal.editor.TextTools;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class StylePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	protected TextTools tools;
	private OverlayPreferenceStore fOverlayStore;
	private Map fColorButtons= new HashMap();
	
	private Map fCheckBoxes= new HashMap();

	private List fStyleColorList;	
	private ColorEditor fStyleForegroundColorEditor;
	private ColorEditor fBackgroundColorEditor;
	private Button fBackgroundDefaultRadioButton;
	private Button fBackgroundCustomRadioButton;
	private Button fBackgroundColorButton;
	private Button fBoldCheckBox;
	private SourceViewer fPreviewViewer;
	private Color fBackgroundColor;
	
	private static final String BOLD= EDTUIPreferenceConstants.EDITOR_BOLD_SUFFIX;
	
	public final OverlayPreferenceStore.OverlayKey[] fKeys= new OverlayPreferenceStore.OverlayKey[] {
		
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, EDTUIPreferenceConstants.EDITOR_FOREGROUND_COLOR),
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_FOREGROUND_DEFAULT_COLOR),

		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, EDTUIPreferenceConstants.EDITOR_BACKGROUND_COLOR),
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_BACKGROUND_DEFAULT_COLOR),
	
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, EDTUIPreferenceConstants.EDITOR_MULTI_LINE_COMMENT_COLOR),
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_MULTI_LINE_COMMENT_BOLD),
	
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, EDTUIPreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR),
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_BOLD),
	
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, EDTUIPreferenceConstants.EDITOR_KEYWORD_COLOR),
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_KEYWORD_BOLD),
			
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, EDTUIPreferenceConstants.EDITOR_STRING_COLOR),
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_STRING_BOLD),
	
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, EDTUIPreferenceConstants.EDITOR_DEFAULT_COLOR),
		new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, EDTUIPreferenceConstants.EDITOR_DEFAULT_BOLD),
	};
	
	// make sure second entry in each is the preferencesKeyValue
	private final String[][] fStyleColorListModel= new String[][] {
		{ UINlsStrings.SourceStyleDefault, EDTUIPreferenceConstants.EDITOR_DEFAULT_COLOR }, //$NON-NLS-1$
		{ UINlsStrings.SourceStyleKeywords, EDTUIPreferenceConstants.EDITOR_KEYWORD_COLOR }, //$NON-NLS-1$
		{ UINlsStrings.SourceStyleLiterals, EDTUIPreferenceConstants.EDITOR_STRING_COLOR }, //$NON-NLS-1$
		{ UINlsStrings.SourceStyleSingleComment, EDTUIPreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR }, //$NON-NLS-1$
		{ UINlsStrings.SourceStyleMultiComment, EDTUIPreferenceConstants.EDITOR_MULTI_LINE_COMMENT_COLOR }, //$NON-NLS-1$

	};
	
	public StylePreferencePage() {  
		setPreferenceStore(EDTUIPlugin.getDefault().getPreferenceStore());
		fOverlayStore= new OverlayPreferenceStore(getPreferenceStore(), fKeys);
	}
	
	/*
	 * @see IWorkbenchPreferencePage#init()
	 */	
	public void init(IWorkbench workbench) {
	}

	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		// TODO Hook in help...?
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IUIHelpConstants.SOURCE_STYLES_PREFERENCE_CONTEXT);
	}
	
	protected Control createContents(Composite parent) {
		
		initializeDefaults();
		
		fOverlayStore.load();
		fOverlayStore.start();
		
		Composite colorComposite= new Composite(parent, SWT.NULL);
		colorComposite.setLayout(new GridLayout());

		Group backgroundComposite= new Group(colorComposite, SWT.SHADOW_ETCHED_IN);
		backgroundComposite.setLayout(new RowLayout());
		backgroundComposite.setText(UINlsStrings.SourceStyleBackgroundColor);//$NON-NLS-1$
	
		SelectionListener backgroundSelectionListener= new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {				
				boolean custom= fBackgroundCustomRadioButton.getSelection();
				fBackgroundColorButton.setEnabled(custom);
				fOverlayStore.setValue(EDTUIPreferenceConstants.EDITOR_BACKGROUND_DEFAULT_COLOR, !custom);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		};

		fBackgroundDefaultRadioButton= new Button(backgroundComposite, SWT.RADIO | SWT.LEFT);
		fBackgroundDefaultRadioButton.setText(UINlsStrings.SourceStyleSystemDefault); //$NON-NLS-1$
		fBackgroundDefaultRadioButton.addSelectionListener(backgroundSelectionListener);

		fBackgroundCustomRadioButton= new Button(backgroundComposite, SWT.RADIO | SWT.LEFT);
		fBackgroundCustomRadioButton.setText(UINlsStrings.SourceStyleCustom); //$NON-NLS-1$
		fBackgroundCustomRadioButton.addSelectionListener(backgroundSelectionListener);

		fBackgroundColorEditor= new ColorEditor(backgroundComposite);
		fBackgroundColorButton= fBackgroundColorEditor.getButton();

		Label label= new Label(colorComposite, SWT.LEFT);
		label.setText(UINlsStrings.SourceStyleForeground); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite editorComposite= new Composite(colorComposite, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		editorComposite.setLayout(layout);
		GridData gd= new GridData(GridData.FILL_BOTH);
		editorComposite.setLayoutData(gd);		

		fStyleColorList= new List(editorComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		gd= new GridData(GridData.FILL_BOTH);
		gd.heightHint= convertHeightInCharsToPixels(5);
		fStyleColorList.setLayoutData(gd);
						
		Composite stylesComposite= new Composite(editorComposite, SWT.NONE);
		layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.numColumns= 2;
		stylesComposite.setLayout(layout);
		stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		label= new Label(stylesComposite, SWT.LEFT);
		label.setText(UINlsStrings.SourceStyleColor); //$NON-NLS-1$
		gd= new GridData();
		gd.horizontalAlignment= GridData.BEGINNING;
		label.setLayoutData(gd);

		fStyleForegroundColorEditor= new ColorEditor(stylesComposite);
		Button foregroundColorButton= fStyleForegroundColorEditor.getButton();
		gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment= GridData.BEGINNING;
		foregroundColorButton.setLayoutData(gd);
		
		fBoldCheckBox= new Button(stylesComposite, SWT.CHECK);
		fBoldCheckBox.setText(UINlsStrings.SourceStyleBold); //$NON-NLS-1$
		gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment= GridData.BEGINNING;
		gd.horizontalSpan= 2;
		fBoldCheckBox.setLayoutData(gd);
		
		label= new Label(colorComposite, SWT.LEFT);
		label.setText(UINlsStrings.SourceStylePreview); //$NON-NLS-1$
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Control previewer= createPreviewer(colorComposite);
		gd= new GridData(GridData.FILL_BOTH);
		gd.widthHint= convertWidthInCharsToPixels(20);
		gd.heightHint= convertHeightInCharsToPixels(5);
		previewer.setLayoutData(gd);
		
		fStyleColorList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				handleStyleColorListSelection();
			}
		});
		
		foregroundColorButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				int i= fStyleColorList.getSelectionIndex();
				String key= fStyleColorListModel[i][1];
				
				PreferenceConverter.setValue(fOverlayStore, key, fStyleForegroundColorEditor.getColorValue());
			}
		});

		fBackgroundColorButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				PreferenceConverter.setValue(fOverlayStore, EDTUIPreferenceConstants.EDITOR_BACKGROUND_COLOR, fBackgroundColorEditor.getColorValue());					
			}
		});

		fBoldCheckBox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
			public void widgetSelected(SelectionEvent e) {
				int i= fStyleColorList.getSelectionIndex();
				String key= fStyleColorListModel[i][1];
				fOverlayStore.setValue(key + BOLD, fBoldCheckBox.getSelection());
			}
		});
				
		initialize();		
		Dialog.applyDialogFont(colorComposite);				
		return colorComposite;
	}
	
	private Control createPreviewer(Composite parent) {	
		tools = new TextTools(fOverlayStore);
	
		fPreviewViewer= new SourceViewer(parent, null, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		fPreviewViewer.configure(new EGLSourceViewerConfiguration(tools, null));
		Font font = JFaceResources.getFont(EDTUIPreferenceConstants.EDITOR_TEXT_FONT);
		fPreviewViewer.getTextWidget().setFont(font);
		fPreviewViewer.setEditable(false);
	
		initializeViewerColors(fPreviewViewer);
	
		String content= getSampleText(); //$NON-NLS-1$
		IDocument document= new Document(content);
		
		IDocumentPartitioner partitioner = ((DocumentProvider) EGLUI.getDocumentProvider()).createDocumentPartitioner();
		document.setDocumentPartitioner(partitioner);
		partitioner.connect(document);
		
		fPreviewViewer.setDocument(document);
	
		fOverlayStore.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				String p= event.getProperty();
				if (p.equals(EDTUIPreferenceConstants.EDITOR_BACKGROUND_COLOR) ||
					p.equals(EDTUIPreferenceConstants.EDITOR_BACKGROUND_DEFAULT_COLOR))
				{
					initializeViewerColors(fPreviewViewer);
				}
				
				fPreviewViewer.invalidateTextPresentation();
			}
		});	
		return fPreviewViewer.getControl();
	}
	
	/**
	 * Initializes the given viewer's colors.
	 * 
	 * @param viewer the viewer to be initialized
	 */
	private void initializeViewerColors(ISourceViewer viewer) {	
		IPreferenceStore store= fOverlayStore;
		if (store != null) {		
			StyledText styledText= viewer.getTextWidget();					
			// ---------- background color ----------------------
			Color color= store.getBoolean(EDTUIPreferenceConstants.EDITOR_BACKGROUND_DEFAULT_COLOR)
				? null
				: createColor(store, EDTUIPreferenceConstants.EDITOR_BACKGROUND_COLOR, styledText.getDisplay());
			styledText.setBackground(color);			
			if (fBackgroundColor != null)
				fBackgroundColor.dispose();
			
			fBackgroundColor= color;
		}
	}
	
	/**
	 * Creates a color from the information stored in the given preference store.
	 * Returns <code>null</code> if there is no such information available.
	 */
	private Color createColor(IPreferenceStore store, String key, Display display) {
		RGB rgb= null;			
		if (store.contains(key)) {		
			if (store.isDefault(key))
				rgb= PreferenceConverter.getDefaultColor(store, key);
			else
				rgb= PreferenceConverter.getColor(store, key);
	
			if (rgb != null)
				return new Color(display, rgb);
		}	
		return null;
	}	
	
	private void handleStyleColorListSelection() {	
		int i= fStyleColorList.getSelectionIndex();
		String key= fStyleColorListModel[i][1];
		RGB rgb= PreferenceConverter.getColor(fOverlayStore, key);
		fStyleForegroundColorEditor.setColorValue(rgb);		
		fBoldCheckBox.setSelection(fOverlayStore.getBoolean(key + BOLD));		
	}
	
	public String getSampleText() {
		return internal();
	}
	public static String internal() {
		return "/* This function adds the employee to the SQL\n   table EMPLOYEE. SQL updates are backed out\n   when there is a duplicate key. */\n\nFunction addEmp()\n  try\n    add employee;\n  onException(exception SQLException)\n    if (employee is unique)    // record with duplicate key\n      sysLib.rollback();       // back out SQL add\n      msg = \"Duplicate key\";\n    else                       // database error on add\n      msg = \"Database error\";\n    end\n    setMsg();\n  end\nend"; //$NON-NLS-1$
	}
	
	private void initialize() {
	  			
		initializeFields();
		
		for (int i= 0; i < fStyleColorListModel.length; i++)
			fStyleColorList.add(fStyleColorListModel[i][0]);
		fStyleColorList.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (fStyleColorList != null && !fStyleColorList.isDisposed()) {
					fStyleColorList.select(0);
					handleStyleColorListSelection();
				}
			}
		});		  
	}
	
	private void initializeFields() {
	
		Iterator e= fColorButtons.keySet().iterator();
		while (e.hasNext()) {
			ColorEditor c= (ColorEditor) e.next();
			String key= (String) fColorButtons.get(c);
			RGB rgb= PreferenceConverter.getColor(fOverlayStore, key);
			c.setColorValue(rgb);
		}
		
		e= fCheckBoxes.keySet().iterator();
		while (e.hasNext()) {
			Button b= (Button) e.next();
			String key= (String) fCheckBoxes.get(b);
			b.setSelection(fOverlayStore.getBoolean(key));
		}
		
		RGB rgb= PreferenceConverter.getColor(fOverlayStore, EDTUIPreferenceConstants.EDITOR_BACKGROUND_COLOR);
		fBackgroundColorEditor.setColorValue(rgb);		
		
		boolean default_= fOverlayStore.getBoolean(EDTUIPreferenceConstants.EDITOR_BACKGROUND_DEFAULT_COLOR);
		fBackgroundDefaultRadioButton.setSelection(default_);
		fBackgroundCustomRadioButton.setSelection(!default_);
		fBackgroundColorButton.setEnabled(!default_);

	}

	private void initializeDefaults() {
		
		IPreferenceStore prefs = getPreferenceStore();
			
		if (!prefs.contains(EDTUIPreferenceConstants.EDITOR_BACKGROUND_COLOR)) {
			RGB rgb= getControl().getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND).getRGB();
			PreferenceConverter.setDefault(fOverlayStore, EDTUIPreferenceConstants.EDITOR_BACKGROUND_COLOR, rgb);
			PreferenceConverter.setDefault(prefs, EDTUIPreferenceConstants.EDITOR_BACKGROUND_COLOR, rgb);
		}
		if (!prefs.contains(EDTUIPreferenceConstants.EDITOR_FOREGROUND_COLOR)) {
			RGB rgb= getControl().getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND).getRGB();
			PreferenceConverter.setDefault(fOverlayStore, EDTUIPreferenceConstants.EDITOR_FOREGROUND_COLOR, rgb);
			PreferenceConverter.setDefault(prefs, EDTUIPreferenceConstants.EDITOR_FOREGROUND_COLOR, rgb);
		}			
	}		

	/*
	 * @see PreferencePage#performOk()
	 */
	public boolean performOk() {			
		fOverlayStore.propagate();
		EDTUIPlugin.getDefault().saveUIPluginPreferences();
		return true;		
	}
	
   /*
	* @see PreferencePage#performDefaults()
	*/
	protected void performDefaults() {		
		fOverlayStore.loadDefaults();
		initializeFields();
		handleStyleColorListSelection();		 
		super.performDefaults();
		fPreviewViewer.invalidateTextPresentation();
	}
	
	/*
	 * @see DialogPage#dispose()
	 */
	public void dispose() {				
		if (fOverlayStore != null) {
			fOverlayStore.stop();
			fOverlayStore= null;
		}
		if (fBackgroundColor != null && !fBackgroundColor.isDisposed())
			fBackgroundColor.dispose();		
		if (tools != null) {
			tools.dispose();
			tools = null;
		}		
		  super.dispose();
	}
	
}
