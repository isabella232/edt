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
package org.eclipse.edt.ide.ui.internal.formatting.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.formatting.CodeFormatterConstants;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Category;
import org.eclipse.edt.ide.ui.internal.formatting.profile.DefaultProfile;
import org.eclipse.edt.ide.ui.internal.formatting.profile.EGLFormatProfileRoot;
import org.eclipse.edt.ide.ui.internal.formatting.profile.FormatProfiles;
import org.eclipse.edt.ide.ui.internal.formatting.profile.Profile;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;

public class ModifyDialog extends StatusDialog {

    /**
     * The key to store the number (beginning at 0) of the tab page which had the 
     * focus last time.
     */
    private static final String DS_KEY_LAST_FOCUS= EDTUIPlugin.PLUGIN_ID + "formatter_page.modify_dialog.last_focus"; //$NON-NLS-1$ 
    /**
     * The keys to retrieve the preferred area from the dialog settings.
     */
    private static final String DS_KEY_PREFERRED_WIDTH= EDTUIPlugin.PLUGIN_ID + "formatter_page.modify_dialog.preferred_width"; //$NON-NLS-1$
    private static final String DS_KEY_PREFERRED_HEIGHT= EDTUIPlugin.PLUGIN_ID + "formatter_page.modify_dialog.preferred_height"; //$NON-NLS-1$
    private static final String DS_KEY_PREFERRED_X= EDTUIPlugin.PLUGIN_ID + "formatter_page.modify_dialog.preferred_x"; //$NON-NLS-1$
    private static final String DS_KEY_PREFERRED_Y= EDTUIPlugin.PLUGIN_ID + "formatter_page.modify_dialog.preferred_y"; //$NON-NLS-1$
	
    /**
     * key is a string in the format: "categoryID.prefID"
     * value is PreferenceSettingValue: initValue in the profile and newValue from UI 
     * 
     * this map holds ALL the preference setting values from a profile(or DefaultProfile)
     * this is passed as an input parameter, and is passed to the each of the tab page 
     * to set initial value of the UI control and set current value change from the UI control
     */
    private Map fAllPreferenceSettings;
    
	private ProfileManager fProfileManager ;
	private final boolean fIsNewProfile ;
	private EObject fProfile ;		//type is either Profile or DefaultProfile
	
	private IStatus fStandardStatus;
	private final String fTitle;
	final IDialogSettings fDialogSettings;
	protected final List fTabPages;
	private TabFolder fTabFolder;
	private Button fApplyButton;
	
	protected ModifyDialog(Shell parentShell, EObject profileOrDefaultProfile, ProfileManager profileManager, boolean isNewProfile){
		super(parentShell);
		fProfileManager = profileManager;
		fIsNewProfile = isNewProfile;
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		
		fProfile = profileOrDefaultProfile;
		if (fProfileManager.isProfileBuildIn(fProfile)){
		    fStandardStatus= new Status(IStatus.INFO, EDTUIPlugin.getPluginId(), IStatus.OK, 
		    		NewWizardMessages.INFO_EnterNewProfileName, null); 
		    fTitle= NewWizardMessages.bind(NewWizardMessages.ShowProfile, fProfileManager.getProfileDisplayName(fProfile)); 
		} else {
		    fStandardStatus= new Status(IStatus.OK, EDTUIPlugin.getPluginId(), IStatus.OK, "", null); //$NON-NLS-1$
		    fTitle= NewWizardMessages.bind(NewWizardMessages.EditProfile, fProfileManager.getProfileDisplayName(fProfile)); 
		}
		fAllPreferenceSettings = fProfileManager.getCurrentPreferenceSettingMap();
		updateStatus(fStandardStatus);

		//commenting out for RATLC01154666:
		//setStatusLineAboveButtons(false);
		
		fTabPages= new ArrayList();
		fDialogSettings= EDTUIPlugin.getDefault().getDialogSettings();			
	}
	
	public void create() {
		super.create() ;
		int lastFocusNr= 0;
		try {
			lastFocusNr= fDialogSettings.getInt(DS_KEY_LAST_FOCUS);
			if (lastFocusNr < 0) lastFocusNr= 0;
			if (lastFocusNr > fTabPages.size() - 1) lastFocusNr= fTabPages.size() - 1;
		} catch (NumberFormatException x) {
			lastFocusNr= 0;
		}
		
		if (!fIsNewProfile) {
			fTabFolder.setSelection(lastFocusNr);
			((ModifyDialogTabPage)fTabFolder.getSelection()[0].getData()).setInitialFocus();
		}		
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell) ;
		shell.setText(fTitle);		
	}
	
	protected Control createDialogArea(Composite parent) {
		
		final Composite composite= (Composite)super.createDialogArea(parent);
		
		fTabFolder = new TabFolder(composite, SWT.NONE);
		fTabFolder.setFont(composite.getFont());
		fTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createCategoryTabPages();		
		
		applyDialogFont(composite);
		
		fTabFolder.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				final TabItem tabItem= (TabItem)e.item;
				final ModifyDialogTabPage page= (ModifyDialogTabPage)tabItem.getData();
//				page.fSashForm.setWeights();
				fDialogSettings.put(DS_KEY_LAST_FOCUS, fTabPages.indexOf(page));
				page.makeVisible();
			}
		});
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.FORMATTER_MODIFY_PROFILE_PAGE);
		
		return composite;
	}
	
	
	private void createCategoryTabPages() {
		EGLFormatProfileRoot Root = fProfileManager.getBuildInFormatProfileRoot();
		FormatProfiles profilesRoot = Root.getFormatProfiles();
		DefaultProfile defaultProfile = profilesRoot.getDefaultProfile();
		if(defaultProfile != null){
			EList categories = defaultProfile.getCategory();
			for(Iterator it = categories.iterator(); it.hasNext();){
				Category category = (Category)it.next();
				String tabTitle = FormatProfileRootHelper.getFormattingProfileNLSString(category.getDisplay());
				addTabPage(fTabFolder, tabTitle, defaultProfile, category);
			}
		}		
	}

	private final void addTabPage(TabFolder tabFolder, String title, DefaultProfile defaultProfile, Category category) {
		final TabItem tabItem= new TabItem(tabFolder, SWT.NONE);
		applyDialogFont(tabItem.getControl());
		tabItem.setText(title);
		String categoryId = category.getId();
		ModifyDialogTabPage tabPage = null;
		if(categoryId.equals(CodeFormatterConstants.FORMATTER_CATEGORY_WS))
			tabPage = new WhiteSpaceTabPage(this, defaultProfile, category, fAllPreferenceSettings);
		else if(categoryId.equals(CodeFormatterConstants.FORMATTER_CATEGORY_WRAPPING))
			tabPage = new LineWrappingTabPage(this, defaultProfile, category, fAllPreferenceSettings);
		else
			tabPage = new ModifyDialogTabPage(this, defaultProfile, category, fAllPreferenceSettings);
		tabItem.setData(tabPage);
		tabItem.setControl(tabPage.createContents(tabFolder));
		fTabPages.add(tabPage);
	}
	
	public void updateStatus(IStatus status) {
		IStatus statusArg = status != null ? status : fStandardStatus;
	    super.updateStatus(statusArg);    
		if (fApplyButton != null && !fApplyButton.isDisposed()) {
			fApplyButton.setEnabled(hasChanges() && !statusArg.matches(IStatus.ERROR));
		}	    
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#getInitialSize()
	 */
	protected Point getInitialSize() {
    	Point initialSize= super.getInitialSize();
        try {
        	int lastWidth= fDialogSettings.getInt(DS_KEY_PREFERRED_WIDTH);
        	if (initialSize.x > lastWidth)
        		lastWidth= initialSize.x;
        	int lastHeight= fDialogSettings.getInt(DS_KEY_PREFERRED_HEIGHT);
        	if (initialSize.y > lastHeight)
        		lastHeight= initialSize.x;
       		return new Point(lastWidth, lastHeight);
        } catch (NumberFormatException ex) {
        }
        return initialSize;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#getInitialLocation(org.eclipse.swt.graphics.Point)
	 */
	protected Point getInitialLocation(Point initialSize) {
        try {
        	return new Point(fDialogSettings.getInt(DS_KEY_PREFERRED_X), fDialogSettings.getInt(DS_KEY_PREFERRED_Y));
        } catch (NumberFormatException ex) {
        	return super.getInitialLocation(initialSize);
        }
	}
	
	private void disposePages(){
		for(Iterator it = fTabPages.iterator(); it.hasNext();){
			Object obj = it.next();
			if(obj instanceof ModifyDialogTabPage){
				ModifyDialogTabPage page = (ModifyDialogTabPage)obj;
				page.dispose();
			}
		}
	}
	
	public boolean close() {
		final Rectangle shell= getShell().getBounds();
		
		fDialogSettings.put(DS_KEY_PREFERRED_WIDTH, shell.width);
		fDialogSettings.put(DS_KEY_PREFERRED_HEIGHT, shell.height);
		fDialogSettings.put(DS_KEY_PREFERRED_X, shell.x);
		fDialogSettings.put(DS_KEY_PREFERRED_Y, shell.y);
		
		disposePages();
		return super.close();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		if(hasChanges()){
			if(!applyPressed())		//if apply is not successful, stay in the dialog
				return;
		}
		super.okPressed();
	}
	
    protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLIENT_ID) {
			applyPressed();
		} else {
			super.buttonPressed(buttonId);
		}
    }
    
    protected void cancelPressed() {
    	//reset all the current value back to its initial value
    	fProfileManager.undoPreferenceSettings(fAllPreferenceSettings);
    	super.cancelPressed() ;
    }
	
	private boolean applyPressed() {
		if(fProfileManager.isProfileBuildIn(fProfile)){
			RenameProfileDialog dialog = new RenameProfileDialog(getShell(), NewWizardMessages.SaveAsProfile, fProfile, fProfileManager);
			if(dialog.open() != Window.OK)
				return false;
			
			fProfile = dialog.getRenamedProfile();
			
			fStandardStatus = new Status(IStatus.OK, EDTUIPlugin.getPluginId(), IStatus.OK, "", null); //$NON-NLS-1$
			updateStatus(fStandardStatus);
		}
		
		//right now fProfile should be type of Profile
		fProfileManager.updateProfileSettings((Profile)fProfile, fAllPreferenceSettings);
		fProfileManager.setSelectedProfile(fProfile);
		fApplyButton.setEnabled(false);
		return true;
	}    
	
    protected void createButtonsForButtonBar(Composite parent) {
	    fApplyButton= createButton(parent, IDialogConstants.CLIENT_ID, NewWizardMessages.Apply, false); 
		fApplyButton.setEnabled(false);
		
		GridLayout layout= (GridLayout) parent.getLayout();
		layout.numColumns++;
		layout.makeColumnsEqualWidth= false;
		Label label= new Label(parent, SWT.NONE);
		GridData data= new GridData();
		data.widthHint= layout.horizontalSpacing;
		label.setLayoutData(data);
		super.createButtonsForButtonBar(parent);
    }
	
	public void valuesModified() {
		if (fApplyButton != null && !fApplyButton.isDisposed()) {
			fApplyButton.setEnabled(hasChanges());
		}
	}
	
	private boolean hasChanges() {
		for(Iterator it = fAllPreferenceSettings.keySet().iterator(); it.hasNext();){
			Object key = it.next();
			ProfileManager.PreferenceSettingValue settingValue = (ProfileManager.PreferenceSettingValue)(fAllPreferenceSettings.get(key));
			if(settingValue.hasSettingValueChanged())
				return true;			
		}
		return false;
	}	
 }
