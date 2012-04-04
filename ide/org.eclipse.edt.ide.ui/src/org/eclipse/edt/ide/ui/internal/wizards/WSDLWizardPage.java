/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.util.TabFolderLayout;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.InterfaceConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class WSDLWizardPage extends EGLFileWizardPage implements PropertyChangeListener{
	 private int nColumns = 5;
	    
	// private int indexInWSDLConfig;

	protected StringDialogField fInterfaceDialogField;
	protected CheckedListDialogField fFunctionListField;
	protected InterfaceFieldAdapter adapter = new InterfaceFieldAdapter();

	private CTabFolder fTabFolder;

	private Button bis4RuiCheckBox;

	// cache the control, so whenever recreate the tabitem, no need to create
	// the control again
	protected Composite[] tabControls;

	// hashtable, the key is the dialogField, the value is the interface Index
	// in WSDL
	private Hashtable fieldcontrol2InterfaceIndex;

	private StatusInfo fInterfaceNameStatus, fFuncSelSatus;

	private class InterfaceFieldAdapter implements IDialogFieldListener {
		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			Integer InterfaceIndexInWSDL = (Integer) (fieldcontrol2InterfaceIndex.get(field));
			if (field instanceof StringDialogField)
				handleInterfaceNameDialogFieldChanged(
						InterfaceIndexInWSDL.intValue(),(StringDialogField) field);
			else if (field instanceof CheckedListDialogField)
				handleFunctionListFieldChanged(InterfaceIndexInWSDL.intValue(),(CheckedListDialogField) field);
		}
	}
	        
	protected class FunctionsListLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			/*if (element instanceof Function) {
				Function func = (Function) element;
				DataTypeStatus funcStatus = func.getDataTypeStatus();
				int overlayFlag = 0;
				switch (funcStatus.getStatus()) {
				case DataTypeStatus.STATUS_ERROR:
					overlayFlag = EGLElementImageDescriptor.ERROR;
					break;
				case DataTypeStatus.STATUS_WARNING:
					overlayFlag = EGLElementImageDescriptor.WARNING;
					break;
				}
				EGLElementImageDescriptor overlayedDescriptor = new EGLElementImageDescriptor(
						PluginImages.DESC_OBJS_FUNCTION, overlayFlag,
						EGLElementImageDescriptor.SMALL_SIZE);
				return EDTUIPlugin.getImageDescriptorRegistry().get(
						overlayedDescriptor);
			}*/
			return null;
		}

		public String getText(Object element) {
			/*if (element instanceof Function)
				return ((Function) element).getXML().getName();*/
			return element == null ? "" : element.toString();//$NON-NLS-1$
		}
	}

	/**
	 * @param pageName
	 */
	public WSDLWizardPage(String pageName) {
		super(pageName);
		// indexInWSDLConfig = index;
		fieldcontrol2InterfaceIndex = new Hashtable();

		fInterfaceNameStatus = new StatusInfo();
		fFuncSelSatus = new StatusInfo();
	}

	protected boolean shouldResetErrorStatus() {
		return false;
	}
	
	//TODO: return WSDLConfiguration
	private EGLFileConfiguration getConfiguration() {
		return (EGLFileConfiguration) ((EGLPartWizard) getWizard())
				.getConfiguration(getName());
	}

	protected EGLFileConfiguration getFileConfiguration() {
		return getConfiguration();
	}

	public void updateControlValues() {
		super.updateControlValues();
		updateTabFolder();
	}
		
	/**
	 * recreate the tabItem based on the interfaces selections
	 */
	private void updateTabFolder() {
		if (fTabFolder != null) {
			CTabItem[] items = fTabFolder.getItems();
			for (int i = items.length - 1; i >= 0; i--) {
				items[i].dispose();
			}
			createTabItems(fTabFolder);
			fTabFolder.setSelection(0);
		}
	}
		
	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(composite, IUIHelpConstants.WSDL2EGL_INTERFACE);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		createContainerControls(composite, nColumns);
		createPackageControls(composite);
		createEGLFileControls(composite);
		createSeparator(composite, nColumns);

		// createInterfaceEGLNameControls(composite);
		createTabControls(composite);

		createCheckBoxIsRuiControl(composite);
		createCheckBoxOverwriteFileControl(composite);
		setControl(composite);

		getConfiguration().addPropertyChangeListener(this);
		validatePage();
		Dialog.applyDialogFont(parent);
	}
	    
	protected void handleContainerDialogFieldChanged() {
		super.handleContainerDialogFieldChanged();
		updateOtherConfiguration();
	}

	protected void updateOtherConfiguration() {
		/*if (getConfiguration().getServiceBindingLibConfiguration() != null) {
			String currProjName = getPackageConfiguration().getProjectName();

			// update the initialProject for the binding page
			getConfiguration().getServiceBindingLibConfiguration()
					.setInitialProjectName(currProjName);

			// init the binding page's continer name to be the same as
			// interface's
			getConfiguration().getServiceBindingLibConfiguration()
					.setContainerName(
							getPackageConfiguration().getContainerName());
		}*/
	}
		
	protected void createTabControls(Composite parent) {
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalAlignment = GridData.FILL;
		gd.horizontalSpan = nColumns - 1;

		fTabFolder = new CTabFolder(parent, SWT.TOP | SWT.BORDER);
		fTabFolder.setLayout(new TabFolderLayout());
		fTabFolder.setLayoutData(gd);
		fTabFolder.setSimple(false);

		createTabItems(fTabFolder);
		fTabFolder.setSelection(0);
	}
		
	protected void createTabItems(CTabFolder tabfolder) {
		/*boolean selInterfaceState[] = getConfiguration().getInterfacesSelectionState();
		int interfaceCnt = selInterfaceState.length;

		if (tabControls == null)
			tabControls = new Composite[interfaceCnt];

		if (interfaceCnt != tabControls.length)
			tabControls = new Composite[interfaceCnt];

		for (int i = 0; i < interfaceCnt; i++) {
			if (selInterfaceState[i]) {
				EInterface eglInterface = getConfiguration().getInterfaceInWSDL(i);
				String interfaceName = getConfiguration().getInterfaceInWSDL(i).getName();
				String tabItemName = eglInterface.getXML().getName();

				createTabItem(tabfolder, i, interfaceName, tabItemName,
						new FunctionsListLabelProvider());
			}
		}*/
	}

	protected void createTabItem(CTabFolder tabfolder, int i,
			String interfaceName, String tabItemName,
			ILabelProvider funcListLabelProvider) {
		CTabItem tabitem = new CTabItem(tabfolder, SWT.NONE);
		tabitem.setText(tabItemName);
		tabitem.setImage(PluginImages.DESC_OBJS_INTERFACE.createImage());
		if (tabControls[i] == null) {
			tabControls[i] = new Composite(tabfolder, SWT.NONE);

			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.numColumns = nColumns;
			tabControls[i].setLayout(layout);

			createInterfaceEGLNameControls(tabControls[i], interfaceName, i);
			createFunctionListControls(tabControls[i], funcListLabelProvider, i);
		}

		tabitem.setControl(tabControls[i]);
	}
		
	protected void createInterfaceEGLNameControls(Composite parent,
			String strText, int indexInWSDLConfig) {
		createInterfaceEGLNameControls(parent,
				NewWizardMessages.NewEGLWSDLInterfaceWizardPagePartlabel,strText, indexInWSDLConfig);
	}
		
	protected void createInterfaceEGLNameControls(Composite parent,
			String strLabelText, String strText, int indexInWSDLConfig) {
		fInterfaceDialogField = new StringDialogField();
		fieldcontrol2InterfaceIndex.put(fInterfaceDialogField, new Integer(
				indexInWSDLConfig));
		fInterfaceDialogField.setDialogFieldListener(adapter);
		fInterfaceDialogField.setLabelText(strLabelText);

		fInterfaceDialogField.setText(strText);

		fInterfaceDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fInterfaceDialogField.getTextControl(null),getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fInterfaceDialogField.getTextControl(null));
	}
		
		
	protected void createCheckBoxIsRuiControl(Composite parent) {
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = nColumns - 1;

		bis4RuiCheckBox = new Button(parent, SWT.CHECK);
		bis4RuiCheckBox.setLayoutData(gd);
		bis4RuiCheckBox.setText(NewWizardMessages.NewEGLWSDLInterfaceWizardPageIs4RUILabel);
		//bis4RuiCheckBox.setSelection(getConfiguration().is4RUI());
		bis4RuiCheckBox.addSelectionListener(new SelectionListener() {
			private void setIs4RuiSelection(SelectionEvent e) {
				if (e.getSource() instanceof Button) {
					Button btn = (Button) (e.getSource());
					//getConfiguration().setIs4RUI(btn.getSelection());

					validatePage();
				}
			}

			public void widgetSelected(SelectionEvent e) {
				setIs4RuiSelection(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				setIs4RuiSelection(e);
			}
		});
	}
		
	protected void handleInterfaceNameDialogFieldChanged(int indexInWSDLConfig,
			StringDialogField field) {
		// Update Configuration
		//getConfiguration().setInterfaceEGLName(indexInWSDLConfig,field.getText());

		// Validate Page
		validatePage();
	}
		
	protected boolean validatePage() {
		fInterfaceNameStatus.setOK();
		fFuncSelSatus.setOK();

		if (super.validatePage()) {
			// check to see if the interface name is empty
			int selFuncCnt = 0;
			EGLFileConfiguration fileConfig = getFileConfiguration();
			/*if (fileConfig instanceof WSDLConfiguration) {
				WSDLConfiguration wsdlConfig = (WSDLConfiguration) fileConfig;
				boolean[] selInterfaces = wsdlConfig
						.getInterfacesSelectionState();
				for (int i = 0; i < selInterfaces.length; i++) {
					if (selInterfaces[i]) {
						InterfaceConfiguration interfaceConfiguration = wsdlConfig
								.getInterfaceConfiguration(i);
						if (interfaceConfiguration.getInterfaceName().trim()
								.length() == 0) {
							// user need to specify an interface name
							fInterfaceNameStatus
									.setError(NewWizardMessages.ErrEmptyInterfaceName);
						}

						boolean[] selFuncs = interfaceConfiguration.getFunctionSelectionStates();
						for (int j = 0; j < selFuncs.length; j++) {
							if (selFuncs[j])
								selFuncCnt++;
						}
					}
				}

				// check to see if there is any function selected
				if (selFuncCnt == 0) {
					// user must select at least one function
					fFuncSelSatus
							.setError(NewWizardMessages.ErrEmptyFunctionList);
				}
			}*/
			updateStatus(new IStatus[] { fInterfaceNameStatus, fFuncSelSatus });
		}

		return true;
	}
			
	protected Composite createFunctionListControls(Composite parent,
			ILabelProvider labelProvider, int indexInWSDLConfig) {
		String[] buttonLabels = new String[] {
		/* 0 */NewWizardMessages.BuildPathsBlockClasspathCheckallButton,
		/* 1 */NewWizardMessages.BuildPathsBlockClasspathUncheckallButton

		};

		fFunctionListField = new CheckedListDialogField(null, buttonLabels,
				labelProvider);
		fieldcontrol2InterfaceIndex.put(fFunctionListField, new Integer(
				indexInWSDLConfig));
		fFunctionListField.setLabelText(NewWizardMessages.NewEGLWSDLInterfaceWizardPageFunctionsLabel);
		fFunctionListField.setCheckAllButtonIndex(0);
		fFunctionListField.setUncheckAllButtonIndex(1);

		fFunctionListField.doFillIntoGrid(parent, nColumns);
		GridData gd = (GridData) fFunctionListField.getListControl(null)
				.getLayoutData();
		gd.heightHint = convertHeightInCharsToPixels(6);
		gd.grabExcessVerticalSpace = false;
		gd.widthHint = getMaxFieldWidth();

		populateFuncList(indexInWSDLConfig, fFunctionListField);

		// add the list check listener after populating the list box
		fFunctionListField.setDialogFieldListener(adapter);
		return parent;
	}

	private InterfaceConfiguration getInterfaceConfiguration(
			int indexInWSDLConfig) {
		//return getConfiguration().getInterfaceConfiguration(indexInWSDLConfig);
		return null;
	}

	protected void populateFuncList(int indexInWSDLConfig,
			CheckedListDialogField funcListField) {
		/*InterfaceConfiguration interfaceConf = getInterfaceConfiguration(indexInWSDLConfig);
		Function[] functions = interfaceConf.getFunctionsInInterface(false);
		//
		if (functions != null) {
			for (int i = 0; i < functions.length; i++) {
				funcListField.addElement(functions[i]);
				funcListField.setChecked(functions[i],
						interfaceConf.getFunctionSelectionStates()[i]);
			}
		}*/
	}
		
	/**
	 * when the check box gets checked/unchecked
	 * 
	 */
	protected void handleFunctionListFieldChanged(int indexInWSDLConfig,
			CheckedListDialogField funcListField) {

		// need to update the config
		/*boolean[] oldSelState = getInterfaceConfiguration(indexInWSDLConfig).getFunctionSelectionStates();
		int size = funcListField.getSize();
		for (int i = 0; i < size; i++) {
			Object elem = funcListField.getElement(i);
			boolean isChecked = funcListField.isChecked(elem);
			if (isChecked) {
				if (elem instanceof Function) {
					Function func = (Function) elem;
					DataTypeStatus funcStatus = func.getDataTypeStatus();
					String errMsg = ""; //$NON-NLS-1$
					int statusCode = funcStatus.getStatus();
					if (statusCode == DataTypeStatus.STATUS_ERROR
							|| statusCode == DataTypeStatus.STATUS_WARNING) {
						// check to see if this function has any parsing errors,
						// if so, pop up error messages, not allow user to check
						// this
						String[] errMsgs = funcStatus.getMessages();
						for (int x = 0; x < errMsgs.length; x++) {
							errMsg += errMsgs[x];
							errMsg += "\n"; //$NON-NLS-1$
						}
						if (statusCode == DataTypeStatus.STATUS_ERROR) {
							MessageDialog
									.openError(
											EDTUIPlugin.getActiveWorkbenchShell(),
											NewWizardMessages.OpenWSDL2EGLWizardActionParsingErrorTitle,
											errMsg);
							funcListField.setChecked(elem, false);
							isChecked = false;
						} else if (statusCode == DataTypeStatus.STATUS_WARNING
								&& !oldSelState[i]) { // should be warning
							MessageDialog
									.openWarning(
											EDTUIPlugin.getActiveWorkbenchShell(),
											NewWizardMessages.OpenWSDL2EGLWizardActionParsingWarningTitle,
											errMsg);
						}
					}
				}
			}
			getInterfaceConfiguration(indexInWSDLConfig).setFunctionsSelectionState(i, isChecked);
		}*/

		validatePage();
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getPropertyName().equals(
				EGLFileConfiguration.PROPERTY_OVERWRITE)) {
			validatePage();
		}
	}
}
