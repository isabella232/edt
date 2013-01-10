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
package org.eclipse.edt.ide.ui.internal.preferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.util.PixelConverter;
import org.eclipse.edt.ide.ui.internal.wizards.IStatusChangeListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IListAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;


public class ImportOrganizeConfigurationBlock extends OptionsConfigurationBlock {
	private static final Key PREF_IMPORTORDER= getEGLUIKey(EDTUIPreferenceConstants.ORGIMPORTS_IMPORTORDER);
	private static final Key PREF_ONDEMANDTHRESHOLD= getEGLUIKey(EDTUIPreferenceConstants.ORGIMPORTS_ONDEMANDTHRESHOLD);
	
	private static final String DIALOGSETTING_LASTLOADPATH= EDTUIPlugin.PLUGIN_ID + ".importorder.loadpath"; //$NON-NLS-1$
	private static final String DIALOGSETTING_LASTSAVEPATH= EDTUIPlugin.PLUGIN_ID + ".importorder.savepath"; //$NON-NLS-1$

	private static Key[] getAllKeys() {
		return new Key[] {
			PREF_IMPORTORDER, PREF_ONDEMANDTHRESHOLD
		};	
	}

	public static class ImportOrderEntry {
		
		public final String name;
		
		public ImportOrderEntry(String name) {
			this.name= name;
		}
		
		public String serialize() {
			return name;
		}
		
		public static ImportOrderEntry fromSerialized(String str) {
			return new ImportOrderEntry(str);
		}
		
	}
	
	private static class ImportOrganizeLabelProvider extends LabelProvider {
		
		private final Image PCK_ICON;

		public ImportOrganizeLabelProvider() {
			PCK_ICON= PluginImages.get(PluginImages.IMG_OBJS_PACKAGE);
		}
		
	    public Image getImage(Object element) {
	        return PCK_ICON;
	    }
		
		public String getText(Object element) {
			ImportOrderEntry entry= (ImportOrderEntry) element;
			String name= entry.name;
			if (name.length() > 0) {
				return name;
			}
			return UINlsStrings.ImportOrganizeConfigurationBlock_other_normal; 
		}
	}
	
	private class ImportOrganizeAdapter implements IListAdapter, IDialogFieldListener {

		private boolean canEdit(ListDialogField field) {
			List selected= field.getSelectedElements();
			return selected.size() == 1;
		}

        public void customButtonPressed(ListDialogField field, int index) {
        	doButtonPressed(index);
        }

        public void selectionChanged(ListDialogField field) {
			fOrderListField.enableButton(IDX_EDIT, canEdit(field));
        }

        public void dialogFieldChanged(DialogField field) {
        	updateModel(field);
        }
        
        public void doubleClicked(ListDialogField field) {
        	if (canEdit(field)) {
				doButtonPressed(IDX_EDIT);
        	}
        }
	}
	
	private static final int IDX_ADD= 0;
	private static final int IDX_EDIT= 2;
	private static final int IDX_REMOVE= 3;
	private static final int IDX_UP= 5;
	private static final int IDX_DOWN= 6;
	private static final int IDX_LOAD= 8;
	private static final int IDX_SAVE= 9;
	
	private ListDialogField fOrderListField;
	private StringDialogField fThresholdField;
	
	private PixelConverter fPixelConverter;
	
	public ImportOrganizeConfigurationBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getAllKeys(), container);
		String[] buttonLabels= new String[] { 
				UINlsStrings.ImportOrganizeConfigurationBlock_order_add_button,
				/* 1 */ null,
				UINlsStrings.ImportOrganizeConfigurationBlock_order_edit_button, 
				UINlsStrings.ImportOrganizeConfigurationBlock_order_remove_button, 
				/* 4 */  null,
				UINlsStrings.ImportOrganizeConfigurationBlock_order_up_button, 
				UINlsStrings.ImportOrganizeConfigurationBlock_order_down_button, 
				/* 7 */  null,
				UINlsStrings.ImportOrganizeConfigurationBlock_order_load_button, 
				UINlsStrings.ImportOrganizeConfigurationBlock_order_save_button
			};
					
			ImportOrganizeAdapter adapter= new ImportOrganizeAdapter();
			
			fOrderListField= new ListDialogField(adapter, buttonLabels, new ImportOrganizeLabelProvider());
			fOrderListField.setDialogFieldListener(adapter);
			fOrderListField.setLabelText(UINlsStrings.ImportOrganizeConfigurationBlock_order_label); 
			fOrderListField.setUpButtonIndex(IDX_UP);
			fOrderListField.setDownButtonIndex(IDX_DOWN);
			fOrderListField.setRemoveButtonIndex(IDX_REMOVE);
			
			fOrderListField.enableButton(IDX_EDIT, false);
			
			fThresholdField= new StringDialogField();
			fThresholdField.setDialogFieldListener(adapter);
			fThresholdField.setLabelText(UINlsStrings.ImportOrganizeConfigurationBlock_threshold_label); 
				
			updateControls();
	}

	
	private void doThresholdChanged() {
		StatusInfo status= new StatusInfo();
		String thresholdString= fThresholdField.getText();
		try {
			int threshold= Integer.parseInt(thresholdString);
			if (threshold < 0) {
				status.setError(UINlsStrings.ImportOrganizeConfigurationBlock_error_invalidthreshold); 
			}
		} catch (NumberFormatException e) {
			status.setError(UINlsStrings.ImportOrganizeConfigurationBlock_error_invalidthreshold); 
		}
		updateStatus(status);
	}
	
	private void doButtonPressed(int index) {
		if (index == IDX_ADD) { // add new
			List existing= fOrderListField.getElements();
			ImportOrganizeInputDialog dialog= new ImportOrganizeInputDialog(getShell(), existing);
			if (dialog.open() == Window.OK) {
				List selectedElements= fOrderListField.getSelectedElements();
				if (selectedElements.size() == 1) {
					int insertionIndex= fOrderListField.getIndexOfElement(selectedElements.get(0)) + 1;
					fOrderListField.insertElementAt(dialog.getResult(), insertionIndex);
				} else {
					fOrderListField.addElement(dialog.getResult());
				}
			}
		} else if (index == IDX_EDIT) { // edit
			List selected= fOrderListField.getSelectedElements();
			if (selected.isEmpty()) {
				return;
			}
			ImportOrderEntry editedEntry= (ImportOrderEntry) selected.get(0);
			
			List existing= fOrderListField.getElements();
			existing.remove(editedEntry);
			
			ImportOrganizeInputDialog dialog= new ImportOrganizeInputDialog(getShell(), existing);
			dialog.setInitialSelection(editedEntry);
			if (dialog.open() == Window.OK) {
				fOrderListField.replaceElement(editedEntry, dialog.getResult());
			}
		} else if (index == IDX_LOAD) { // load
			List order= loadImportOrder();
			if (order != null) {
				fOrderListField.setElements(order);
			}
		} else if (index == IDX_SAVE) { // save
			saveImportOrder(fOrderListField.getElements());
		}		
	}
	
	protected Control createContents(Composite parent) {
		setShell(parent.getShell());
		
		fPixelConverter= new PixelConverter(parent);
	
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		
		composite.setLayout(layout);
		
		fOrderListField.doFillIntoGrid(composite, 3);
		LayoutUtil.setHorizontalSpan(fOrderListField.getLabelControl(null), 2);
		LayoutUtil.setWidthHint(fOrderListField.getLabelControl(null), fPixelConverter.convertWidthInCharsToPixels(60));
		LayoutUtil.setHorizontalGrabbing(fOrderListField.getListControl(null));
		
		fThresholdField.doFillIntoGrid(composite, 2);
		((GridData) fThresholdField.getTextControl(null).getLayoutData()).grabExcessHorizontalSpace= false;
		
		
		Dialog.applyDialogFont(composite);

		return composite;
	}

	
	/*
	 * The import order file is a property file. The keys are
	 * "0", "1" ... last entry. The values must be valid package names.
	 */
	private List loadFromProperties(Properties properties) {
		ArrayList res= new ArrayList();
		int nEntries= properties.size();
		for (int i= 0 ; i < nEntries; i++) {
			String curr= properties.getProperty(String.valueOf(i));
			if (curr != null) {
				ImportOrderEntry entry= ImportOrderEntry.fromSerialized(curr);
				if (!EGLConventions.validatePackageName(entry.name).matches(IStatus.ERROR)) {
					res.add(entry);
				} else {
					return null;
				}
			} else {
				return res;
			}
		}
		return res;
	}
	
	private List loadImportOrder() {
		IDialogSettings dialogSettings= EDTUIPlugin.getDefault().getDialogSettings();
		
		FileDialog dialog= new FileDialog(getShell(), SWT.OPEN);
		dialog.setText(UINlsStrings.ImportOrganizeConfigurationBlock_loadDialog_title); 
		dialog.setFilterExtensions(new String[] {"*.importorder", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
		String lastPath= dialogSettings.get(DIALOGSETTING_LASTLOADPATH);
		if (lastPath != null) {
			dialog.setFilterPath(lastPath);
		}
		String fileName= dialog.open();
		if (fileName != null) {
			dialogSettings.put(DIALOGSETTING_LASTLOADPATH, dialog.getFilterPath());
					
			Properties properties= new Properties();
			FileInputStream fis= null;
			try {
				fis= new FileInputStream(fileName);
				properties.load(fis);
				List res= loadFromProperties(properties);
				if (res != null) {
					return res;
				}
			} catch (IOException e) {
				EDTUIPlugin.log(e);
			} finally {
				if (fis != null) {
					try { fis.close(); } catch (IOException e) {}
				}
			}
			String title= UINlsStrings.ImportOrganizeConfigurationBlock_loadDialog_error_title; 
			String message= UINlsStrings.ImportOrganizeConfigurationBlock_loadDialog_error_message; 
			MessageDialog.openError(getShell(), title, message);
		}
		return null;
	}
	
	private void saveImportOrder(List elements) {
		IDialogSettings dialogSettings= EDTUIPlugin.getDefault().getDialogSettings();
		
		FileDialog dialog= new FileDialog(getShell(), SWT.SAVE);
		dialog.setText(UINlsStrings.ImportOrganizeConfigurationBlock_saveDialog_title); 
		dialog.setFilterExtensions(new String[] {"*.importorder", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
		dialog.setFileName("example.importorder"); //$NON-NLS-1$
		String lastPath= dialogSettings.get(DIALOGSETTING_LASTSAVEPATH);
		if (lastPath != null) {
			dialog.setFilterPath(lastPath);
		}
		String fileName= dialog.open();
		if (fileName != null) {
			dialogSettings.put(DIALOGSETTING_LASTSAVEPATH, dialog.getFilterPath());
			
			Properties properties= new Properties();
			for (int i= 0; i < elements.size(); i++) {
				ImportOrderEntry entry= (ImportOrderEntry) elements.get(i);
				properties.setProperty(String.valueOf(i), entry.serialize());
			}
			FileOutputStream fos= null;
			try {
				fos= new FileOutputStream(fileName);
				properties.store(fos, "Organize Import Order"); //$NON-NLS-1$
			} catch (IOException e) {
				EDTUIPlugin.log(e);
				String title= UINlsStrings.ImportOrganizeConfigurationBlock_saveDialog_error_title; 
				String message= UINlsStrings.ImportOrganizeConfigurationBlock_saveDialog_error_message; 
				MessageDialog.openError(getShell(), title, message);				
			} finally {
				if (fos != null) {
					try { fos.close(); } catch (IOException e) {}
				}
			}
		}
	}

	private void updateStatus(IStatus status) {
		fContext.statusChanged(status);
	}
	
	
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		// no validation
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#updateControls()
	 */
	protected void updateControls() {
		ImportOrderEntry[] importOrder= getImportOrderPreference();
		int threshold= getImportNumberThreshold();
		
		fOrderListField.removeAllElements();
		for (int i= 0; i < importOrder.length; i++) {
			fOrderListField.addElement(importOrder[i]);
		}
		fThresholdField.setText(String.valueOf(threshold));
	}	
	
	
	protected final void updateModel(DialogField field) {
		// set values in working copy
		if (field == fOrderListField) {
	  		setValue(PREF_IMPORTORDER, packOrderList(fOrderListField.getElements()));
		} else if (field == fThresholdField) {
	  		setValue(PREF_ONDEMANDTHRESHOLD, fThresholdField.getText());
	  		
	  		doThresholdChanged();	  		
		}
	}
		
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null; // no build required
	}


	private static ImportOrderEntry[] unpackOrderList(String str) {
		ArrayList res= new ArrayList();
		int start= 0;
		do {
			int end= str.indexOf(';', start);
			if (end == -1) {
				end= str.length();
			}
			res.add(ImportOrderEntry.fromSerialized(str.substring(start, end)));
			start= end + 1;
		} while (start < str.length());
		
		return (ImportOrderEntry[]) res.toArray(new ImportOrderEntry[res.size()]);
	}
	
	private static String packOrderList(List orderList) {
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < orderList.size(); i++) {
			ImportOrderEntry entry= (ImportOrderEntry) orderList.get(i);
			buf.append(entry.serialize());
			buf.append(';');
		}
		return buf.toString();
	}
	
	private ImportOrderEntry[] getImportOrderPreference() {
		String str= getValue(PREF_IMPORTORDER);
		if (str != null) {
			return unpackOrderList(str);
		}
		return new ImportOrderEntry[0];
	}
	
	private int getImportNumberThreshold() {
		String thresholdStr= getValue(PREF_ONDEMANDTHRESHOLD);
		try {
			int threshold= Integer.parseInt(thresholdStr);
			if (threshold < 0) {
				threshold= Integer.MAX_VALUE;
			}
			return threshold;
		} catch (NumberFormatException e) {
			return Integer.MAX_VALUE;
		}
	}
	
}
