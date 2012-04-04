/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.wizards.insertwidget;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlay;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvDesignOverlayDropLocation;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.PageDataNode;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;


public class InsertWidgetWizard extends Wizard {

	private EvDesignOverlay evDesignOverlay;
	private EvDesignOverlayDropLocation	dropLocation;
	private InsertWidgetWizardPage insertWidgetWizardPage;
	
	public InsertWidgetWizard(PageDataNode selectedEGLNode, EvDesignOverlay evDesignOverlay, EvDesignOverlayDropLocation dropLocation){
		setWindowTitle(Messages.NL_IWW_Title);
		setDefaultPageImageDescriptor(Activator.getImageDescriptor(EvConstants.ICON_INSERT_WIDGET_WIZARD));
		this.evDesignOverlay = evDesignOverlay;
		this.dropLocation = dropLocation;
		IProject project = evDesignOverlay.getDesignPage().getEditor().getProject();
		IEditorInput editorInput = evDesignOverlay.getDesignPage().getEditor().getEditorInput();
		insertWidgetWizardPage = new InsertWidgetWizardPage(selectedEGLNode, project, editorInput);
	}
	
	public void addPages() {
		addPage(insertWidgetWizardPage);
	}

	@Override
	public boolean performFinish() {
		//get the selected InsertDataNodes
		CheckboxTreeViewer configureWidgetsTableViewer = insertWidgetWizardPage.getConfigureWidgetsTableViewer();
		Object[] checkedElements = configureWidgetsTableViewer.getCheckedElements();
		for(int i=0; i<checkedElements.length; i++){
			Object checkedElement = checkedElements[i];
			if(checkedElement instanceof InsertDataNode){
				InsertDataNode insertDataNode = (InsertDataNode)checkedElement;
				Activator.getDefault().getPreferenceStore().putValue(insertDataNode.getBindingName() + insertDataNode.getPurpose(), insertDataNode.getWidgetType().getName());
				if(!InsertWidgetWizardUtil.isAnEmbedRecord(insertDataNode) && !InsertWidgetWizardUtil.isAPrimitiveArrayInRecord(insertDataNode)){
					insertDataNode.setGen(true);
					if(insertDataNode.getWidgetName() == null){
						insertDataNode.setWidgetName(insertDataNode.getDefaultWidgetName());
					}
				}
			}
		}
		
		//generate
		TreeItem[] treeItems = configureWidgetsTableViewer.getTree().getItems();
		if(treeItems != null && treeItems.length > 0){
			TreeItem firstTreeItem = treeItems[0];
			if(firstTreeItem.getData() instanceof InsertDataNode){
				InsertDataNode rootInsertDataNode = (InsertDataNode)firstTreeItem.getData();
				if(rootInsertDataNode.isGen()){
					IProgressService progressService = PlatformUI.getWorkbench().getProgressService();  
					try {
						progressService.runInUI(PlatformUI.getWorkbench().getProgressService(), 
								new GenerationProgress(rootInsertDataNode, evDesignOverlay, dropLocation), 
								ResourcesPlugin.getWorkspace().getRoot());
					} catch (InvocationTargetException e) {
						Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "InvocationTargetException while insert widget.", e));
					} catch (InterruptedException e) {
						Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "InterruptedException while insert widget.", e));
					}  
				}
			}
		}
		
		return true;
	}	
	
	

}
