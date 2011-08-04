/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.project.wizard.pages;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.ui.internal.preferences.AbstractPreferencePage;
import org.eclipse.edt.ide.ui.internal.project.wizards.NewEGLProjectWizard;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.wizards.ProjectFinishUtility;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


public class ProjectWizardRUILibraryPage extends ProjectWizardPage {	
	
	private Group widgetLibraryGroup;
	private Composite contentSection;
	private CheckedListDialogField widgetLibraryList;


	public ProjectWizardRUILibraryPage(String pageName) {
		super(pageName);
		setTitle(NewWizardMessages.RUILibraryPageTitle);
		setDescription(NewWizardMessages.RUILibraryPageDescription);
	}
	
	private class WidgetLibrariesAdapter implements IStringButtonAdapter, IDialogFieldListener {
		public void changeControlPressed(DialogField field) {
		}
		public void dialogFieldChanged(DialogField field) {			
			handleWidgetLibrariesDialogFieldChanged(field);
		}
	}
	
	private void handleWidgetLibrariesDialogFieldChanged(DialogField field) {
//		validatePage();
		((NewEGLProjectWizard)getWizard()).getModel().setSelectedWidgetLibraries(((CheckedListDialogField)field).getCheckedElements());
	}  
	
//	private void validatePage() {
//		boolean isChecked= widgetLibraryList.isChecked(IEGLWidgetProjectsConstants.RUI_WIDGETS);
//		if (!isChecked) {
//			widgetLibraryList.setCheckedWithoutUpdate(IEGLWidgetProjectsConstants.RUI_WIDGETS, true);
//		}
//	}
	
	public void createContents(Composite parent) {
		GridData data = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(data);
		parent.setLayout(new FormLayout());	
		
		createDefaultWidgetsContent(parent);	
		
		//TODO: Help
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HelpContextIDs.New_EGL_Base_Project_Advanced_Page);
	}

	public void setProjectName(String projectName) {
		((NewEGLProjectWizard)getWizard()).getModel().setProjectName(projectName);
	}

	public boolean isWidgetLibrarySelected( String libName ) {
		List<String> checked = widgetLibraryList.getCheckedElements();
		if( checked.contains(libName)) {
			return true;
		}
		return false;
	}
	
	private void createDefaultWidgetsContent(Composite parent) {
		widgetLibraryGroup = AbstractPreferencePage.createGroup(parent, 1);
		widgetLibraryGroup.setText(NewWizardMessages.RUILibraryPage_widget_library);
		
		WidgetLibrariesAdapter adapter = new WidgetLibrariesAdapter();
		
		String[] buttonLabels = new String[] {
			/* 0 */ NewWizardMessages.BuildPathsBlockClasspathCheckallButton,
			/* 1 */ NewWizardMessages.BuildPathsBlockClasspathUncheckallButton};
	
		// List of widget libraries
		List<String> widgetLibraries = new ArrayList<String>(); 
		
		// List of widget libraries that are selected by default
		List<String> selectedLibraries = new ArrayList<String>();

		// Get the project name
		IConfigurationElement[] widgetLibraryContributions = Platform.getExtensionRegistry().getConfigurationElementsFor( ProjectFinishUtility.EXTENSIONPOINT_WIDGET_LIBRARY_PROVIDER );
		if( widgetLibraryContributions != null ){
			String libName, selected, resourcePluginName, libraryResourceFolder, widgetsProjectName;
			// Loop through all extensions
			for( IConfigurationElement currContribution : widgetLibraryContributions ) {
				libName = currContribution.getAttribute(ProjectFinishUtility.PROVIDER_LIBRARY_NAME);
				selected = currContribution.getAttribute(ProjectFinishUtility.PROVIDER_SELECTED);
				resourcePluginName = currContribution.getAttribute(ProjectFinishUtility.PROVIDER_RESOURCE_PLUGIN_NAME);
				libraryResourceFolder = currContribution.getAttribute(ProjectFinishUtility.PROVIDER_RESOURCE_FOLDER);
				widgetsProjectName = currContribution.getAttribute(ProjectFinishUtility.PROVIDER_PROJECT_NAME);
				if(libName != null){
					try {
						URL url = ProjectFinishUtility.getWidgetProjectURL(resourcePluginName, libraryResourceFolder, widgetsProjectName);
						if(url != null) {
							widgetLibraries.add(libName);
							if(selected != null && selected.toLowerCase().equals("true")){
								selectedLibraries.add(libName);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
		}
		
		widgetLibraryList = new CheckedListDialogField(null, buttonLabels, new LabelProvider());
		widgetLibraryList.setDialogFieldListener(adapter);
		widgetLibraryList.setCheckAllButtonIndex(0);
		widgetLibraryList.setUncheckAllButtonIndex(1);	
		
		LayoutUtil.doDefaultLayout(widgetLibraryGroup, new DialogField[] { widgetLibraryList }, true, 0, 0, 10, 10);
		LayoutUtil.setHorizontalGrabbing(widgetLibraryList.getListControl(null));
			
		widgetLibraryList.setElements(widgetLibraries);
		widgetLibraryList.setCheckedElements(selectedLibraries);
		
		FormData data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(contentSection, 10);
		data.right = new FormAttachment(100, -10);
		widgetLibraryGroup.setLayoutData(data);				
	}
	
	
	public CheckedListDialogField getWidgetLibraryList() {
		return widgetLibraryList;
	}
}
