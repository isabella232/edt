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
package org.eclipse.edt.ide.ui.internal.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;

import com.ibm.icu.text.MessageFormat;

public class BasePreferencePage extends AbstractPreferencePage {

	protected Text  sourceFolderName;
	protected Text  outputFolderName;

	protected ModifyListener modifyListener;
	
	public BasePreferencePage() {
		super();
		setPreferenceStore( EDTUIPlugin.getDefault().getPreferenceStore() );
		setDescription( "Specify general settings for EGL");

		// title used when opened programatically
		setTitle( "EGL" );
		
		modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlModified(e.widget);
			}
		};
	}
	
	/**
	 * Most of the preferences are stored in EDTUIPlugin's preference
	 * store.  (See EDTUIPreferenceConstants.)  However, the ones that
	 * need to be accessed from the org.eclipse.edt.ide.core plug-in are
	 * EDTCoreIDEPlugin's preference store.  (See EDTCorePreferenceConstants.)
	 * getPreferenceStore() returns the UI preference store.  This method
	 * returns the Core preference store.
	 * 
	 * @return IPreferenceStore
	 */
	protected IPreferenceStore getCorePreferenceStore() {
		return EDTCoreIDEPlugin.getPlugin().getPreferenceStore();
	}

	protected void saveCorePreferenceStore() {
		IEclipsePreferences preferences = new InstanceScope().getNode( EDTCoreIDEPlugin.PLUGIN_ID );
		try {
			preferences.flush();
		} catch( BackingStoreException e ) {
			EDTUIPlugin.log( e );
 		}
	}

	protected Control createContents(Composite parent) {
		Composite composite = (Composite) super.createContents(parent);
		composite.setFont(parent.getFont());
		createEGLFoldersGroup(composite);

		setSize(composite);
		loadPreferences();

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_BASE_PREFERENCES_CONTEXT);
		return composite;
	}

	protected void createEGLFoldersGroup(Composite parent) {
		Group baseGroup = createGroup(parent, 1);
		baseGroup.setText(UINlsStrings.BasePreferencePage_EGLFolderGroup_label);

		Composite internalComposite = createComposite(baseGroup, 2);
		Label sourceFolderLabel = new Label( internalComposite, SWT.NONE);
		sourceFolderLabel.setText( UINlsStrings.BasePreferencePage_EGLSourceFolder_label );
		sourceFolderName = createTextField( internalComposite );
		sourceFolderName.addModifyListener( modifyListener );
		
		Label outputFolderLabel = new Label( internalComposite, SWT.NONE);
		outputFolderLabel.setText( UINlsStrings.BasePreferencePage_EGLOutputFolder_label );
		outputFolderName = createTextField( internalComposite );
		outputFolderName.addModifyListener( modifyListener );
	}

	protected void performDefaults() {
		performDefaultsForEGLFoldersGroup();
		super.performDefaults();
	}

	protected void performDefaultsForEGLFoldersGroup() {
		sourceFolderName.setText( 
				EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getDefaultString( EDTCorePreferenceConstants.EGL_SOURCE_FOLDER ) );
		outputFolderName.setText( 
				EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getDefaultString( EDTCorePreferenceConstants.EGL_OUTPUT_FOLDER ) );
	}

	protected void initializeValues() {
		initializeEGLFoldersGroup();
	}

	protected void initializeEGLFoldersGroup() {
		sourceFolderName.setText( 
				EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.EGL_SOURCE_FOLDER ) );
		outputFolderName.setText( 
				EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.EGL_OUTPUT_FOLDER ) );
	}

	private void controlModified( Widget widget ) {
		if( widget == sourceFolderName || widget == outputFolderName ) {
			validateFolders();
		}
	}
	
	private void validateFolders() {
		String srcName= sourceFolderName.getText();
		String binName= outputFolderName.getText();
		if( srcName.length() == 0 ) {
			updateStatus( new StatusInfo( IStatus.ERROR,  "Enter source folder name." ) );
			return;
		}
		if( binName.length() == 0 ) {
			updateStatus( new StatusInfo( IStatus.ERROR,  "Enter output folder name." ) );
			return;
		}
		IWorkspace workspace = EDTUIPlugin.getWorkspace();
		IProject dmy = workspace.getRoot().getProject( "project" ); //$NON-NLS-1$

		IStatus status;
		IPath srcPath = dmy.getFullPath().append( srcName );
		if( srcName.length() != 0 ) {
			status = workspace.validatePath( srcPath.toString(), IResource.FOLDER );
			if( !status.isOK() ) {
				String message = MessageFormat.format( "Invalid source folder name: {0}", new Object[] { status.getMessage() } );
				updateStatus( new StatusInfo( IStatus.ERROR, message ) );
				return;
			}
		}
		IPath binPath = dmy.getFullPath().append( binName );
		if( binName.length() != 0 ) {
			status = workspace.validatePath( binPath.toString(), IResource.FOLDER );
			if (!status.isOK()) {
				String message = MessageFormat.format( "Invalid output folder name: {0}", new Object[] { status.getMessage() } );
				updateStatus( new StatusInfo( IStatus.ERROR, message ) );
				return;
			}
		}
		IEGLPathEntry entry = EGLCore.newSourceEntry(srcPath);
		status = EGLConventions.validateEGLPath(EGLCore.create(dmy), new IEGLPathEntry[] { entry }, binPath);
		if( !status.isOK() ) {
			String message = "Settings will result in an invalid build path. Check for nested folders.";
			updateStatus( new StatusInfo( IStatus.ERROR, message ) );
			return;
		}
		updateStatus( new StatusInfo() ); // set to OK
	}
	
	private void updateStatus( IStatus status ) {
		setValid( !status.matches( IStatus.ERROR ) );
		StatusUtil.applyToStatusLine( this, status );
	}
	
	protected void storeValues() {
		storeEGLFoldersGroup();
	}

	protected void storeEGLFoldersGroup() {		
		EDTCoreIDEPlugin.getPlugin().getPreferenceStore().setValue( 
				EDTCorePreferenceConstants.EGL_SOURCE_FOLDER, sourceFolderName.getText() );
		EDTCoreIDEPlugin.getPlugin().getPreferenceStore().setValue( 
				EDTCorePreferenceConstants.EGL_OUTPUT_FOLDER, outputFolderName.getText() );
	}

	public boolean performOk() {
		super.performOk();
		saveCorePreferenceStore();

		return true;
	}
}
