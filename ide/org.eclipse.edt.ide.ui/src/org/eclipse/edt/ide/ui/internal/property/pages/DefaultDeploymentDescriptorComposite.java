/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.property.pages;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.PartWrapper;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.deployment.ui.FileBrowseDialog;
import org.eclipse.edt.ide.ui.internal.deployment.ui.SOAMessages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

public class DefaultDeploymentDescriptorComposite extends Composite {
	protected static final int INDENT_SIZE = 20;
	
	private Button specifyValueButton;
	private DDSettings defaultDDSettings;
	private IProject containingProject = null;
	
	public DefaultDeploymentDescriptorComposite( Composite parent, int style, IResource resource) {
		super( parent, style );
		if(resource != null) {
			containingProject = resource.getProject();
		}
		createContentArea( this );
	}
	
	private void createContentArea( Composite parent ) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		setLayout( layout );
		setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		
		createCompositeSection( layout.numColumns );
	}
	
	protected void createCompositeSection( int numColumns ) {
		defaultDDSettings = new DDSettings(this,containingProject);
	}
	
	protected void doCreateCompositeSection( int numColumns ) {
		specifyValueButton = new Button(this, SWT.RADIO);
		specifyValueButton.setText(UINlsStrings.DefaultDDPropertiesPageSpecifyValuesLabel);
		
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.horizontalSpan = numColumns;
		gd.horizontalIndent = INDENT_SIZE/2;
		specifyValueButton.setLayoutData(gd);
	}
	
	public DDSettings getDefaultDDSettings() {
		return defaultDDSettings;
	}
	
	public static class DDSettings {
		protected Text bdText;
		protected Button browseBtn;
		protected Button clearBtn;
		
		private PartWrapper initialDD;
		private PartWrapper currentDD;
		private boolean hasValueSet;
		
		private final Shell shell;
		
		public DDSettings( Composite parent, final IProject resource){
			shell = parent.getShell();
			GridData gd;
			
			bdText = new Text( parent, SWT.BORDER | SWT.READ_ONLY );
			gd = new GridData( GridData.FILL_HORIZONTAL );
			bdText.setLayoutData( gd );
			
			final String dialogLabel = UINlsStrings.DefaultDDPropertiesPageBrowseDialogRuntimeJavaScriptDesc;
			
			browseBtn = new Button( parent, SWT.PUSH );
			browseBtn.setText( UINlsStrings.DefaultDDPropertiesPageBrowseButtonLabel );
			browseBtn.addSelectionListener( new SelectionAdapter() {
				public void widgetSelected( SelectionEvent event ) {
					clickBrowseButton( browseBtn, bdText, dialogLabel, resource);
				}
			} );
			
			clearBtn = new Button( parent, SWT.PUSH );
			clearBtn.setText( UINlsStrings.DefaultDDPropertiesPageClearButtonLabel );
			clearBtn.setEnabled( false );
			clearBtn.addSelectionListener( new SelectionAdapter() {
				public void widgetSelected( SelectionEvent event ) {
					PartWrapper wrapper = new PartWrapper();
					wrapper.setPartName(""); //$NON-NLS-1$
					wrapper.setPartPath(""); //$NON-NLS-1$
					setCurrentBD(wrapper);
				}
			} );
		}
		
		protected void clickBrowseButton( Button button, Text text, String dialogDescription,IProject resource) {
			IFile initialSelection = null;
			if (currentDD != null && currentDD.getPartPath() != null) {
				IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(currentDD.getPartPath());
				if (r != null && r.getType() == IResource.FILE) {
					initialSelection = (IFile)r;
				}
			}
			
			ElementTreeSelectionDialog dialog = FileBrowseDialog.openBrowseFileOnEGLPathDialog(shell, 
					resource, initialSelection, IUIHelpConstants.EGLDDWIZ_INCLUDEEGLDD, 
					EGLDDRootHelper.EXTENSION_EGLDD,
					SOAMessages.DefaultDeploymentDescription,
					SOAMessages.IncludeDialogDescription,
					SOAMessages.IncludeDialogMsg, null, null);
			
			if(dialog.open() == IDialogConstants.OK_ID) {
				Object obj = dialog.getFirstResult();
				if ( obj instanceof IFile ) {
					IPath path = ((IFile)obj).getFullPath();
					
					PartWrapper wrapper = new PartWrapper();
					wrapper.setPartName(path.removeFileExtension().lastSegment());
					wrapper.setPartPath(path.toString());
					setCurrentBD(wrapper);
				}
			}
			
		}
		
		protected void setDDText( String bdName, String bdFile ) {
			if ( bdName == null ) {
				bdName = ""; //$NON-NLS-1$
			}
			if ( bdFile == null ) {
				bdFile = ""; //$NON-NLS-1$
			}
			
			String value;
			if (bdFile.length() == 0 ) {
				value = UINlsStrings.DefaultBDPropertiesPageNoValueSetText;
				hasValueSet = false;
				updateClearButton();
			} else {
				value = bdName + " <" + bdFile + ">"; //$NON-NLS-1$ //$NON-NLS-2$
				hasValueSet = true;
				updateClearButton();
			}
			bdText.setText( value );
			bdText.setToolTipText( value );
		}
		
		protected void updateClearButton(){
			if(hasValueSet){
				clearBtn.setEnabled(true);
			} else {
				clearBtn.setEnabled(false);
			}
		}
		
		public boolean isModified() {
			return !currentDD.equals( initialDD );
		}
		
		public PartWrapper getCurrentDD() {
			return currentDD;
		}
		
		public void setInitialDD(PartWrapper wrapper) {
			if(wrapper == null){
				wrapper = new PartWrapper();
				wrapper.setPartName(""); //$NON-NLS-1$
				wrapper.setPartPath(""); //$NON-NLS-1$
			}
			this.initialDD = wrapper;	
			setCurrentBD(wrapper);
		}
		
		private void setCurrentBD(PartWrapper wrapper){
			currentDD = wrapper;		
			setDDText(currentDD.getPartName(), currentDD.getPartPath());
		}
		
		public boolean hasValueSet(){
			return hasValueSet;
		}
	}
}
