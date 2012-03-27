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
package org.eclipse.edt.ide.ui.internal.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.packageexplorer.EGLElementLabelProvider;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringButtonStatusDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLWizardUtilities;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class EGLFileWizardPage extends EGLPackageWizardPage {
	private static final boolean isWindows = SWT.getPlatform().toLowerCase().startsWith("win"); //$NON-NLS-1$	
	private int nColumns = 5;

	protected StringButtonStatusDialogField fPackageDialogField;

	protected StringDialogField fEGLFileDialogField;
	protected StatusInfo fEGLFileStatus;

	private PackageFieldAdapter adapter = new PackageFieldAdapter();

	private Button bFileOverWriteCheckBox;
	
	/*Advance section */
    protected Composite advancedComposite;   
	protected boolean showAdvanced = false;
	protected boolean advancedControlsBuilt = false;	
	protected AdvancedSizeController advancedController;
	protected boolean isFirstTimeToPage = true;	
	protected Button advancedButton;	
	
	protected class AdvancedSizeController implements ControlListener {
		private int advancedHeight = -1;
		private Point originalSize;
		private boolean ignoreShellResize = false;

		protected AdvancedSizeController(Shell aShell) {
			originalSize = aShell.getSize();
			aShell.addControlListener(this);
		}
		public void controlMoved(ControlEvent e) {}
		public void controlResized(ControlEvent e) {
			if (!ignoreShellResize) {
				Control control = (Control) e.getSource();
				if (control.isVisible()) {
					originalSize = control.getSize();
					if (advancedHeight == -1)
						setShellSizeForAdvanced();
				}
			}
		}		
		
		protected void resetOriginalShellSize() {
			setShellSize(originalSize.x, originalSize.y);
		}
		
		private void setShellSize(int x, int y) {
			ignoreShellResize = true;
			try {
				getShell().setSize(x, y);
			} finally {
				ignoreShellResize = false;
			}
		}
		
		protected void setShellSizeForAdvanced() {
			int height = calculateAdvancedShellHeight();
			if (height != -1)
				setShellSize(getShell().getSize().x, height);
		}

		private int calculateAdvancedShellHeight() {
			Point advancedCompSize = advancedComposite.getSize();
			if (advancedCompSize.x == 0)
				return -1;
			int height = computeAdvancedHeight();
			if (!showAdvanced && height != -1)
				height = height - advancedComposite.getSize().y;
			return height;
		}
		/*
		 * Compute the height with the advanced section showing.
		 * @return
		 */
		private int computeAdvancedHeight() {
			if (advancedHeight == -1) {
				Point controlSize = getControl().getSize();
				if (controlSize.x != 0) {
					int minHeight = originalSize.y - controlSize.y;
					Point pageSize = getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
					advancedHeight = pageSize.y + minHeight;
				}
			}
			return advancedHeight;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.frameworks.internal.ui.WTPWizardPage#enter()
	 */
	protected void enter() {
		if(advancedControlsBuilt){
			if (isFirstTimeToPage)
				initializeAdvancedController();
			if(isWindows){
				advancedController.setShellSizeForAdvanced();
			}
		}
	}
	
	private void initializeAdvancedController() {
		advancedController = new AdvancedSizeController(getShell());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.frameworks.internal.ui.WTPWizardPage#exit()
	 */
	protected void exit() {
		if(advancedControlsBuilt && isWindows){
			advancedController.resetOriginalShellSize();
		}
	}
	
	protected void toggleAdvanced(boolean setSize) {
		if(advancedControlsBuilt){
			//showAdvanced = !showAdvanced;
			advancedComposite.setVisible(showAdvanced);
			if (setSize && isWindows)
				advancedController.setShellSizeForAdvanced();
		}
	}
	
	protected Composite createAdvancedComposite(Composite parent, int cols) {
		advancedControlsBuilt= true;
	    advancedButton = new Button(parent, SWT.TOGGLE);	
		setAdvancedLabelText();
		final Cursor hand = new Cursor(advancedButton.getDisplay(), SWT.CURSOR_HAND);
		advancedButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				hand.dispose();
			}
		});	    
		advancedComposite = new Composite(parent, SWT.NONE);
		//advancedComposite.setBackground(new Color(advancedComposite.getDisplay(), 23,45,67));
		GridLayout layout = new GridLayout();
		layout.numColumns = cols;
		advancedComposite.setLayout(layout);		
		GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = cols;
		advancedComposite.setLayoutData(gd);
		
		advancedButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
			    showAdvanced = !showAdvanced;	
			    setAdvancedLabelText();
				toggleAdvanced(true);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		advancedButton.addListener(SWT.MouseHover, new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.MouseHover)
					advancedButton.setCursor(hand);
			}
		});
		
		addToAdvancedComposite(advancedComposite);
		enter();
		toggleAdvanced(true);					
		return advancedComposite;
	}

	/**
	 * child class can override this method to add controls to the advanced section
	 * @param parent
	 */
	protected void addToAdvancedComposite(Composite parent) {
	}	
	
	private void setAdvancedLabelText() {
		if(advancedControlsBuilt){
			if (showAdvanced)
				advancedButton.setText(NewWizardMessages.HideAdvance);
			else
				advancedButton.setText(NewWizardMessages.ShowAdvance);
		}
	}
	
	
	/**
	 * @param pageName
	 */
	public EGLFileWizardPage(String pageName) {
		super(pageName);
		
		setTitle(NewWizardMessages.NewEGLFileWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLFileWizardPageDescription);
		
		fEGLFileStatus= new StatusInfo();
	}

	private class PackageFieldAdapter implements IStringButtonAdapter, IDialogFieldListener {

		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
		    if(field==fEGLFileDialogField)
		        handleFileBrowseButtonSelected();
		    else if(field==fPackageDialogField)
		        handlePackageBrowseButtonSelected();
		}

        // -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			if(field==fContainerDialogField){
				handleContainerDialogFieldChanged();
			}
			if(field==fPackageDialogField){
				handlePackageDialogFieldChanged();
				updatePackageStatusLabel();
			}
			else if(field==fEGLFileDialogField){
				handleEGLFileDialogFieldChanged();
			}
		}
	}
	
	private EGLFileConfiguration getConfiguration() {
		return (EGLFileConfiguration)((EGLFileWizard) getWizard()).getConfiguration();
	}
 
	protected EGLFileConfiguration getFileConfiguration() {
		return getConfiguration();
	}

	protected EGLPackageConfiguration getPackageConfiguration() {
		return getFileConfiguration();
	}
	
	public void updateControlValues()
	{
	    super.updateControlValues();
	    if(fPackageDialogField != null &&
	    		fPackageDialogField.getText() != null &&
	    		!fPackageDialogField.getText().equals( getPackageConfiguration().getFPackage() ))
	        fPackageDialogField.setText(getPackageConfiguration().getFPackage());	    	    
	    if(fEGLFileDialogField != null &&
	    		fEGLFileDialogField.getText() != null &&
	    		!fEGLFileDialogField.getText().equals( getFileConfiguration().getFileName() ))
	        fEGLFileDialogField.setText(getFileConfiguration().getFileName());
		if(bFileOverWriteCheckBox != null &&
				bFileOverWriteCheckBox.getSelection() != getFileConfiguration().isOverwrite() )
			bFileOverWriteCheckBox.setSelection(getFileConfiguration().isOverwrite());			    
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_EMPTY_FILE_DEFINITION);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = nColumns;
		composite.setLayoutData(gd);		
		
		createContainerControls(composite, nColumns);
		createPackageControls(composite);
		
		createSeparator(composite, nColumns);
		
		createEGLFileControls(composite);
		
		setControl(composite);
		
		validatePage();
		Dialog.applyDialogFont(parent);
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			if(fEGLFileDialogField != null)
				fEGLFileDialogField.setFocus();
		}
	}
	
	protected void createContainerControls(Composite parent, int nColumns, String textLabel) {
		super.createContainerControls(parent, nColumns, textLabel);
		fContainerDialogField.setDialogFieldListener(adapter);		
	}
	
	protected void createContainerControls(Composite parent, int nColumns) {
		createContainerControls(parent, nColumns, NewWizardMessages.NewContainerWizardPageContainerLabel);
	}
	
	protected void createPackageControls(Composite parent) {
		createPackageControls(parent, NewWizardMessages.NewTypeWizardPagePackageLabel);
	}
	
	protected void createPackageControls(Composite parent, String textLabel) {		
		fPackageDialogField = new StringButtonStatusDialogField(adapter);
		fPackageDialogField.setLabelText(textLabel);
		fPackageDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPagePackageButton);
		fPackageDialogField.setStatusWidthHint(NewWizardMessages.NewTypeWizardPageDefault);
		fPackageDialogField.setText(getFileConfiguration().getFPackage());
		updatePackageStatusLabel();
		fPackageDialogField.setDialogFieldListener(adapter);
		fPackageDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fPackageDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fPackageDialogField.getTextControl(null));
	}
	
	protected void createEGLFileControls(Composite parent, String labelText) {
		fEGLFileDialogField = new StringDialogField();
		fEGLFileDialogField.setLabelText(labelText);
		fEGLFileDialogField.setText(getFileConfiguration().getFileName());
		fEGLFileDialogField.setDialogFieldListener(adapter);
		fEGLFileDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fEGLFileDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fEGLFileDialogField.getTextControl(null));	
	}
	
	protected void createEGLFileControls(Composite parent) {
		createEGLFileControls(parent, NewWizardMessages.NewTypeWizardPageTypenameLabel);
	}
	
	protected void createEGLFileWithBrowseControls(Composite parent){
		createEGLFileWithBrowseControls(parent, NewWizardMessages.NewTypeWizardPageTypenameLabel);
	}
	
	protected void createEGLFileWithBrowseControls(Composite parent, String labelText) {
		fEGLFileDialogField = new StringButtonDialogField(adapter);
		fEGLFileDialogField.setDialogFieldListener(adapter);
		fEGLFileDialogField.setLabelText(labelText);
		((StringButtonDialogField)fEGLFileDialogField).setButtonLabel(NewWizardMessages.NewContainerWizardPageFileButton);
		fEGLFileDialogField.setText(getFileConfiguration().getFileName());

		fEGLFileDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fEGLFileDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fEGLFileDialogField.getTextControl(null));	
	}
	
	/**
	 * child class should overwrite this method if using the handleFileBrowseButtonSelected method
	 * @return
	 */
// TODO EDT Uncomment when PartSelectionDialog is ready	
//	protected PartSelectionDialog getEGLPartSelectionDialog(IFile eglFile)
//	{
//	    return null;
//	}

	/**
     * 
     */
    protected void handleFileBrowseButtonSelected() {
        IFile eglFile = getFileConfiguration().getFile();
// TODO EDT Uncomment when PartSelectionDialog is ready	        
//		PartSelectionDialog dialog = getEGLPartSelectionDialog(eglFile);
//		if(dialog != null)
//		{
//		    dialog.open();		
//		    updateControlValues();
//		}
		return;        
    }	
	
	protected void handlePackageBrowseButtonSelected() {
		// Choose container
		ElementListSelectionDialog dialog = openPackageDialog();
		
		if(dialog != null && dialog.open()==ElementListSelectionDialog.OK) {
			Object element = dialog.getFirstResult();
	
			//Update Configuration
			getFileConfiguration().setFPackage(((IPackageFragment)element).getElementName());
	
			//Update Container name
			fPackageDialogField.setText(getFileConfiguration().getFPackage());
	
			//Validate
			validatePage();
		}
	}

	protected void handleContainerDialogFieldChanged() {
	    super.handleContainerDialogFieldChanged();
		//Update Configuration
	    getPackageConfiguration().setContainerName(fContainerDialogField.getText());
		
		//Validate
		validatePage();
	}
	
	protected void handlePackageDialogFieldChanged() {
		//Update Configuration
	    getFileConfiguration().setFPackage(fPackageDialogField.getText());
		
		//Validate
		validatePage();
	}
	
	protected void handleEGLFileDialogFieldChanged() {
		//Update Configuration
	    getFileConfiguration().setFileName(fEGLFileDialogField.getText());
		
		//Validate
		validatePage();
	}
	
	private ElementListSelectionDialog openPackageDialog() {
		
		ILabelProvider lp= new EGLElementLabelProvider(EGLElementLabelProvider.SHOW_DEFAULT);
		
		IProject project = null;
		try {
			project = fWorkspaceRoot.getProject(getFileConfiguration().getProjectName());
		} catch (RuntimeException e1) {
			//should give error message here!!!
			return null;
		}
		IEGLProject eproject = EGLCore.create(project);
		IPackageFragmentRoot froot = null;
		
		IEGLElement[] packages= null;
		try {
			IPath sourcePath = new Path(getFileConfiguration().getContainerName());
			froot = eproject.findPackageFragmentRoot(sourcePath.makeAbsolute());
			if (froot != null && froot.exists()) {
				packages= froot.getChildren();
			}
		} catch (EGLModelException e) {
			EGLLogger.log(this, e);
		}
		if (packages == null) {
			packages= new IEGLElement[0];
		}

		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), lp);
		dialog.setIgnoreCase(false);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPageChoosePackageDialogTitle);
		dialog.setMessage(NewWizardMessages.NewTypeWizardPageChoosePackageDialogDescription);
		dialog.setEmptyListMessage(NewWizardMessages.NewTypeWizardPageChoosePackageDialogEmpty);
		dialog.setElements(packages);
//		dialog.setInitialSelections( new Object[] { initElement });

		if (froot != null) {
			IPackageFragment pack= froot.getPackageFragment(getFileConfiguration().getFPackage());
			if (pack != null) {
				dialog.setInitialSelections(new Object[] { pack });
			}
		}

		return dialog;
		
	}
	
	/*
	 * Updates the 'default' label next to the package field.
	 */	
	protected void updatePackageStatusLabel() {
		String packName= getFileConfiguration().getFPackage();
	
		if (packName.length() == 0) {
			fPackageDialogField.setStatus(NewWizardMessages.NewTypeWizardPageDefault);
		} else {
			fPackageDialogField.setStatus(""); //$NON-NLS-1$
		}
	}
	
	protected void createCheckBoxOverwriteFileControl(Composite parent){
		createCheckBoxOverwriteFileControl(parent, NewWizardMessages.NewWSDL2EGLWizardPageOverwriteFile);
	}
	
	protected void createCheckBoxOverwriteFileControl(Composite parent, String labelText)
	{
	    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = nColumns-1;
	    
	    bFileOverWriteCheckBox = new Button(parent, SWT.CHECK);
	    bFileOverWriteCheckBox.setLayoutData(gd);
	    bFileOverWriteCheckBox.setText(labelText);
	    bFileOverWriteCheckBox.setSelection(getFileConfiguration().isOverwrite());
	    bFileOverWriteCheckBox.addSelectionListener(new SelectionListener(){
	        private void setOverwriteSelection(SelectionEvent e)
	        {
	            if(e.getSource() instanceof Button)
	            {
	                Button btn = (Button)(e.getSource());
	                getFileConfiguration().setOverwrite(btn.getSelection());
	                
	                validatePage();
	            }
	        }
	        
            public void widgetSelected(SelectionEvent e) {
                setOverwriteSelection(e);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                setOverwriteSelection(e);                
            }	        
	    });
	}	
	
	protected boolean validatePage() {				
		return validatePage(!getFileConfiguration().isOverwrite());
	}
	
	protected boolean validatePage(boolean checkFileExistance) {		
		boolean ret = false;
		//Validate the package
		if(super.validatePage())
		{
			ret = isValidPage(checkFileExistance);
			updateStatus(new IStatus[] { fContainerStatus, fPackageStatus, fEGLFileStatus });
		}
		
		return ret;
	}
	
	protected boolean isValidPage(){
		return isValidPage(!getFileConfiguration().isOverwrite());
	}
	protected boolean isValidPage(boolean checkFileExistance){
		boolean ret = false;
		fEGLFileStatus.setOK();
		
		String projectName = getFileConfiguration().getProjectName();
		String containerName = getFileConfiguration().getContainerName();
		String packageName = getFileConfiguration().getFPackage();
		String fileName= getFileConfiguration().getFileName();
		String fileExtensionName = getFileConfiguration().getFileExtension();
	
		ret = EGLWizardUtilities.validateFile(projectName, containerName, packageName, fileName, fileExtensionName, fEGLFileStatus, this, checkFileExistance);
		return ret;
	}
	
}
