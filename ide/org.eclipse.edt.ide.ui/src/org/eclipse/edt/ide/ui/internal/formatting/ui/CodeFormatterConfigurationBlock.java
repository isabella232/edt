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
package org.eclipse.edt.ide.ui.internal.formatting.ui;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Profile;
import org.eclipse.edt.ide.ui.internal.util.PixelConverter;
import org.eclipse.edt.ide.ui.internal.util.SWTUtil;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;

public class CodeFormatterConfigurationBlock {

    private static final String DIALOGSTORE_LASTIMPORTPATH= EDTUIPlugin.PLUGIN_ID + ".codeformatter.importpath"; //$NON-NLS-1$
	private static final String DIALOGSTORE_LASTEXPORTPATH= EDTUIPlugin.PLUGIN_ID + ".codeformatter.exportpath"; //$NON-NLS-1$
	
	/**
	 * The GUI controls
	 */
	protected Composite fComposite;
	protected Combo fProfileCombo;
	protected Button fEditButton;
	protected Button fRenameButton;
	protected Button fDeleteButton;
	protected Button fNewButton;
	protected Button fLoadButton;
	protected Button fSaveButton;
	
	protected EGLPreview fEGLPreview;
	private PixelConverter fPixConv;
	
	protected final ProfileManager fProfileManager;
	private ButtonController fBtnController ;
	private ProfileComboController fComboController ;
	private PreviewController fPreviewController ;

	private class ProfileComboController implements Observer, SelectionListener {
		public ProfileComboController() {
			fProfileCombo.addSelectionListener(this);
			fProfileManager.addObserver(this);
			updateProfiles();
			updateSelection();
		}
		
		public void widgetSelected(SelectionEvent e) {
			final int index = fProfileCombo.getSelectionIndex();
			fProfileManager.setSelectedProfile(index);
		}
		
		public void widgetDefaultSelected(SelectionEvent e) {}

		public void update(Observable o, Object arg) {
			if (arg == null) return;
			final int value= ((Integer)arg).intValue();
			switch (value) {
			case ProfileManager.PROFILE_CREATED_EVENT:
			case ProfileManager.PROFILE_DELETED_EVENT:
			case ProfileManager.PROFILE_RENAMED_EVENT:
				updateProfiles();
				updateSelection();
				break;			
			case ProfileManager.SELECTION_CHANGED_EVENT:
				updateSelection();
				break;			
			}			
		}
		
		private void updateProfiles() {
			fProfileCombo.setItems(fProfileManager.getAllProfileDisplayNames());
		}		
	
		private void updateSelection() {
			fProfileCombo.setText(fProfileManager.getSelectedProfileDisplayName());
		}
		
		public void dispose(){
			fProfileCombo.removeSelectionListener(this);
			fProfileManager.deleteObserver(this);			
		}
	}
	
	private class ButtonController implements Observer, SelectionListener {
		
		public ButtonController() {
			fProfileManager.addObserver(this);
			fNewButton.addSelectionListener(this);
			fRenameButton.addSelectionListener(this);
			fEditButton.addSelectionListener(this);
			fDeleteButton.addSelectionListener(this);
			fSaveButton.addSelectionListener(this);
			fLoadButton.addSelectionListener(this);
			update(fProfileManager, null);
		}

		public void update(Observable o, Object arg) {
			ProfileManager profileManager = (ProfileManager)o;
			final boolean isBuildIn = profileManager.isSelectedProfileBuildIn();
			fEditButton.setText(isBuildIn ? NewWizardMessages.Show : NewWizardMessages.Edit);
			fDeleteButton.setEnabled(!isBuildIn);
			fSaveButton.setEnabled(!isBuildIn);
			fRenameButton.setEnabled(!isBuildIn);
		}

		public void widgetSelected(SelectionEvent e) {
			final Button button= (Button)e.widget;
			if (button == fSaveButton)
				exportButtonPressed();
			else if (button == fEditButton)
				modifyButtonPressed();
			else if (button == fDeleteButton) 
				deleteButtonPressed();
			else if (button == fNewButton)
				newButtonPressed();
			else if (button == fLoadButton)
				importButtonPressed();
			else if (button == fRenameButton) 
				renameButtonPressed();
		}
		
		private void renameButtonPressed() {
			if(fProfileManager.isSelectedProfileBuildIn())
				return;
			
			final Profile profile = (Profile)fProfileManager.getSelectedProfile();
			final RenameProfileDialog renameDialog = new RenameProfileDialog(fComposite.getShell(), NewWizardMessages.RenameFormatProfile, profile, fProfileManager);
			if (renameDialog.open() == Window.OK) {
				fProfileManager.setSelectedProfile(fProfileCombo.getSelectionIndex());
				fProfileManager.profileRenamed();
			}			
		}

		private void importButtonPressed() {
			final FileDialog filedlg = new FileDialog(fComposite.getShell(), SWT.OPEN);
			filedlg.setText(NewWizardMessages.ImportFormatProfile);
			filedlg.setFilterExtensions(new String[]{"*.xml"}); //$NON-NLS-1$
			
			final String lastPath = EDTUIPlugin.getDefault().getDialogSettings().get(DIALOGSTORE_LASTIMPORTPATH);
			if(lastPath != null)
				filedlg.setFilterPath(lastPath);
			
			final String path = filedlg.open();
			if(path == null)
				return;
			EDTUIPlugin.getDefault().getDialogSettings().put(DIALOGSTORE_LASTEXPORTPATH, filedlg.getFilterPath());
			
			final File file = new File(path);
			Profile importedProfile = null;
			try{	//check the imported xml file, make sure it is in the right schema and format  
				importedProfile = fProfileManager.getFirstProfileByParsing(file);			
			}
			catch(RuntimeException e){
				//eat the runtime exception, 
				//show the error messages in the finally block
			}
			finally{
				if(importedProfile == null){
					MessageDialog.openError(fComposite.getShell(), NewWizardMessages.ImportFormatProfile,  
							NewWizardMessages.bind(NewWizardMessages.Err_InvalidImportedFormatProfile, path));
					return;
				}				
			}
			
			String importedProfileName = importedProfile.getName();
			if(fProfileManager.containsProfile(importedProfileName)){				
				//ask user they want to rename or overwrite existing one
				final AlreadyExistsDialog aeDlg = new AlreadyExistsDialog(fComposite.getShell(), importedProfile, fProfileManager);
				if(aeDlg.open() != Window.OK)
					return;
			}
			//add the imported profile to the list, and use this one as selection
			fProfileManager.addProfile(importedProfile, true);				
		}

		private void newButtonPressed() {
			final CreateProfileDialog p = new CreateProfileDialog(fComposite.getShell(), fProfileManager);
			if(p.open() != Window.OK)
				return;
			if(!p.openEditDialog())
				return;
			final ModifyDialog modifyDialog = new ModifyDialog(fComposite.getShell(), p.getCreatedProfile(), fProfileManager, true);
			modifyDialog.open();
		}

		private void deleteButtonPressed() {
			if(fProfileManager.isSelectedProfileBuildIn())
				return;
			
			if(MessageDialog.openQuestion(fComposite.getShell(), 
					NewWizardMessages.ConfirmRemove, 
					NewWizardMessages.bind(NewWizardMessages.Q_ConfirmRemoveProfile, fProfileManager.getSelectedProfileDisplayName())))
				fProfileManager.deleteProfile(fProfileCombo.getSelectionIndex(), true);	
		}

		private void modifyButtonPressed() {
			final ModifyDialog modifyDialog = new ModifyDialog(fComposite.getShell(), fProfileManager.getSelectedProfile(), fProfileManager, false);
			modifyDialog.open();
		}

		private void exportButtonPressed() {
			final FileDialog filedlg = new FileDialog(fComposite.getShell(), SWT.SAVE);
			filedlg.setText(NewWizardMessages.ExportFormatProfile);
			filedlg.setFilterExtensions(new String[]{"*.xml"}); //$NON-NLS-1$
			
			final String lastPath = EDTUIPlugin.getDefault().getDialogSettings().get(DIALOGSTORE_LASTEXPORTPATH);
			if(lastPath != null)
				filedlg.setFilterPath(lastPath);
			
			final String path = filedlg.open();
			if(path == null)
				return;
			
			EDTUIPlugin.getDefault().getDialogSettings().put(DIALOGSTORE_LASTEXPORTPATH, filedlg.getFilterPath());
			
			final File file = new File(path);
			if(file.exists() && 
					!MessageDialog.openQuestion(fComposite.getShell(), 
							NewWizardMessages.ExportFormatProfile,  
							NewWizardMessages.bind(NewWizardMessages.Q_ReplaceExportedFormatProfile, path)))
				return;
				
			fProfileManager.exportSelectedProfile(file);
		}

		public void widgetDefaultSelected(SelectionEvent e) {}
		
		public void dispose(){
			fProfileManager.deleteObserver(this);
			fNewButton.removeSelectionListener(this);
			fRenameButton.removeSelectionListener(this);
			fEditButton.removeSelectionListener(this);
			fDeleteButton.removeSelectionListener(this);
			fSaveButton.removeSelectionListener(this);
			fLoadButton.removeSelectionListener(this);			
		}
	}
	
	private class PreviewController implements Observer {

		public PreviewController() {
			fProfileManager.addObserver(this);
			fEGLPreview.update();
		}
		
		public void update(Observable o, Object arg) {
			final int value= ((Integer)arg).intValue();
			switch (value) {
				case ProfileManager.PROFILE_CREATED_EVENT:
				case ProfileManager.PROFILE_DELETED_EVENT:
				case ProfileManager.SELECTION_CHANGED_EVENT:
				case ProfileManager.SETTINGS_CHANGED_EVENT:
					fEGLPreview.update();
			}
		}
		
		public void dispose(){
			fProfileManager.deleteObserver(this);
		}
	}	
	
	public CodeFormatterConfigurationBlock(IProject project){
		fProfileManager = ProfileManager.getInstance();
	}
	
	/**
	 * Create the contents
	 * @param parent Parent composite
	 * @return Created control
	 */
	public Composite createContents(Composite parent) {

		final int numColumns = 5;
		fPixConv = new PixelConverter(parent);
		fComposite = createComposite(parent, numColumns);

		fProfileCombo= createProfileCombo(fComposite, numColumns - 3, fPixConv.convertWidthInCharsToPixels(20));
		fEditButton= createButton(fComposite, NewWizardMessages.Edit, GridData.HORIZONTAL_ALIGN_BEGINNING);  
		fRenameButton= createButton(fComposite, NewWizardMessages.Rename, GridData.HORIZONTAL_ALIGN_BEGINNING); 
		fDeleteButton= createButton(fComposite, NewWizardMessages.Remove, GridData.HORIZONTAL_ALIGN_BEGINNING); 

		final Composite group= createComposite(fComposite, 4);
		final GridData groupData= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		groupData.horizontalSpan= numColumns;
		group.setLayoutData(groupData);

		fNewButton= createButton(group, NewWizardMessages.New, GridData.HORIZONTAL_ALIGN_BEGINNING); 
		((GridData)createLabel(group, "", 1).getLayoutData()).grabExcessHorizontalSpace= true; //$NON-NLS-1$
		fLoadButton= createButton(group, NewWizardMessages.Import, GridData.HORIZONTAL_ALIGN_END); 
		fSaveButton= createButton(group, NewWizardMessages.Export, GridData.HORIZONTAL_ALIGN_END); 

		createLabel(fComposite, NewWizardMessages.Preview, numColumns); 
		configurePreview(fComposite, numColumns);
		
		fBtnController = new ButtonController();
		fComboController = new ProfileComboController();
		fPreviewController = new PreviewController();
		
		return fComposite;
		
	}
	
	public void dispose() {
		//need to clean up the observers, since ProfileManager is a singleton
		if (fBtnController != null)
			fBtnController.dispose();
		if (fComboController != null)
			fComboController.dispose();
		if (fPreviewController != null)
			fPreviewController.dispose();
		if (fEGLPreview != null)
			fEGLPreview.dispose();
	}
	
	private static Button createButton(Composite composite, String text, final int style) {
		final Button button= new Button(composite, SWT.PUSH);
		button.setFont(composite.getFont());
		button.setText(text);

		final GridData gd= new GridData(style);
		gd.widthHint= SWTUtil.getButtonWidthHint(button);
		button.setLayoutData(gd);
		return button;
	}
	
	private static Combo createProfileCombo(Composite composite, int span, int widthHint) {
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = span;
		gd.widthHint= widthHint;

		final Combo combo= new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY );
		combo.setFont(composite.getFont());
		combo.setLayoutData(gd);
		return combo;
	}
	
	private static Label createLabel(Composite composite, String text, int numColumns) {
		final GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = numColumns;
		gd.widthHint= 0;

		final Label label = new Label(composite, SWT.WRAP);
		label.setFont(composite.getFont());
		label.setText(text);
		label.setLayoutData(gd);
		return label;		
	}
	
	private Composite createComposite(Composite parent, int numColumns) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		
		final GridLayout layout = new GridLayout(numColumns, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		return composite;
	}
	
	private void configurePreview(Composite composite, int numColumns){
		fEGLPreview = new EGLPreview(composite, fProfileManager.getCurrentPreferenceSettingMap());
		fEGLPreview.setPreviewText(fProfileManager.getDefaultPreviewCode());
		
		final GridData gd = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = numColumns;
		gd.verticalSpan= 7;
		gd.widthHint = 0;
		gd.heightHint = 0;
		fEGLPreview.getControl().setLayoutData(gd);
	}
	
	public final boolean hasProjectSpecificOptions(IProject project) {
		if (project != null) {
			//return ProfileManager.hasProjectSpecificSettings(new ProjectScope(project));
		}
		return false;
	}	
	
	public boolean performOk(){
		performApply();
		return true;
	}
	
	public void performApply(){
		//persist all the changes
		fProfileManager.saveCustomWorkspaceFormatProfile(true);
	}

	public void performDefaults() {
		//select the DefaultProfile as the selection
		fProfileManager.setSelectedProfile(0);
		
	}

	public boolean performCancel() {
		fProfileManager.clearCachedModel(true);
		return false ;
	}
	
	
}
