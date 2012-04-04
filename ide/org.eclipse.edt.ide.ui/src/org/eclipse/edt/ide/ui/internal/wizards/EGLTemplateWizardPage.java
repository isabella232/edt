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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.templates.ITemplate;
import org.eclipse.edt.ide.ui.templates.TemplateManager;
import org.eclipse.edt.ide.ui.templates.wizards.TemplateWizardNode;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledFormText;

public abstract class EGLTemplateWizardPage extends EGLPartWizardPage 
       implements ISelectionChangedListener, IDoubleClickListener{
	protected TableViewer templateViewer;
	protected ScrolledFormText descriptionText;
	
	public EGLTemplateWizardPage(String pageName) {
		super(pageName);
	}
	
	protected abstract String getTemplateID();
	
	public String getSelectedCodeTemplateId(){

		Object o = ((IStructuredSelection) templateViewer.getSelection()).getFirstElement();
		if (o instanceof ITemplate) {
			ITemplate template = (ITemplate) o;
			return template.getCodeTemplateId();
		}
		return null;
	}
	
	protected void createTemplateArea(Composite container, int nColumns) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, IUIHelpConstants.EGL_NEW_RECORD_TEMPLATE_SELECTION_PAGE);

		Composite ownerInfo = new Composite(container, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = nColumns;
		gridLayout.marginWidth = 0;
		ownerInfo.setLayout(gridLayout);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = nColumns;
		ownerInfo.setLayoutData(gridData);
		
		int templateCol = nColumns % 2;
		int descriptionCol = nColumns - templateCol;
		
		Label label = new Label(ownerInfo, 0);
		label.setText(NewWizardMessages.TemplateSelectionPage_selectTemplate);
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= nColumns;
		label.setLayoutData(gd);

		templateViewer = new TableViewer(ownerInfo, SWT.BORDER);
		Table control = templateViewer.getTable();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 250;
		data.widthHint = 200;
		data.horizontalSpan= templateCol;
		control.setLayoutData(data);
		
		String templateID = getTemplateID();
		ITemplate[] templates = TemplateManager.getInstance().getTemplates(templateID);
		
		templateViewer.setContentProvider(new TreeContentProvider());
		templateViewer.setLabelProvider(new TreeLabelProvider());
		templateViewer.addSelectionChangedListener(this);
		templateViewer.setInput(templates);
		templateViewer.addDoubleClickListener(this);
				
		FormToolkit toolkit = new FormToolkit(container.getDisplay());
		descriptionText = new ScrolledFormText(ownerInfo, SWT.V_SCROLL | SWT.H_SCROLL, false);
		
		int borderStyle = toolkit.getBorderStyle() == SWT.BORDER ? SWT.NULL : SWT.BORDER;
		if (borderStyle == SWT.NULL) {
			descriptionText.setData(FormToolkit.KEY_DRAW_BORDER,
                    FormToolkit.TREE_BORDER);
            toolkit.paintBordersFor(ownerInfo);
        }
		
        FormText ftext = toolkit.createFormText(descriptionText, false);        
        descriptionText.setFormText(ftext);
        descriptionText.setExpandHorizontal(false);
        descriptionText.setExpandVertical(false);
        descriptionText.setBackground(toolkit.getColors().getBackground());
        descriptionText.setForeground(toolkit.getColors().getForeground());
        
        ftext.marginWidth = 2;
        ftext.marginHeight = 2;
        data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 200;
		data.horizontalSpan= descriptionCol;
        descriptionText.setLayoutData(data);

		if (templates != null) {
			for (int i = 0; i < templates.length; i++) {
				if (templates[i].isDefault()) {
					templateViewer.setSelection(new StructuredSelection(new Object[] { templates[i] }), true);
				}
			}
		}
	}
	
	public void selectionChanged(SelectionChangedEvent event) {
		Object o = ((IStructuredSelection) event.getSelection()).getFirstElement();
		if (o instanceof ITemplate) {
			handleSelectedTemplate();
			setTemplateDescription(((ITemplate) o).getDescription());
		}
		validateTemplatePage();
	}
	
	protected boolean isValidTemplateToCompletePage(){
		boolean res = false;
		Object o = ((IStructuredSelection) templateViewer.getSelection()).getFirstElement();
		if (o instanceof ITemplate) {
			ITemplate template = (ITemplate) o;
			TemplateWizardNode wizNode = (TemplateWizardNode) template.getWizardNode();

			String templateID = ((ITemplate) o).getCodeTemplateId();
			res = (wizNode != null && wizNode.isContentCreated() && wizNode.getWizard() != null && wizNode.getWizard().canFinish()) 
					|| (templateID != null );
		}
		return res;
	}
	protected void validateTemplatePage() {
		Object o = ((IStructuredSelection) templateViewer.getSelection()).getFirstElement();
		if (o instanceof ITemplate) {
			ITemplate template = (ITemplate) o;
			handleSelectedTemplate();
			
			boolean b = isValidTemplateToCompletePage();
			
			if(template.hasWizard()) {
				b = b && isPageComplete();
			} else {
				b = b && validateEGLPartName();
			}
			
			setPageComplete(b);
		} else {
			setPageComplete(false);
		}
	}
	
	public boolean isPageComplete(){
		return super.isPageComplete() && validateEGLPartName() && isValidTemplateToCompletePage() && isValidPage();
	}
	
	/**
	 * Sets the wizard node, etc, based on the selected template
	 * 
	 */
	protected void handleSelectedTemplate() {
		Object o = ((IStructuredSelection) templateViewer.getSelection()).getFirstElement();
		if (o instanceof ITemplate) {
			ITemplate template = (ITemplate) o;
			TemplateWizardNode wizNode = (TemplateWizardNode) template.getWizardNode();
			if (wizNode != null) {
				wizNode.setParentWizard(getWizard());
				setSelectedNode(wizNode);
			} else {
				setSelectedNode(null);
			}
		}
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			validateTemplatePage();
			/**Make Name field the focus since we combine the two page.*/
			//templateViewer.getTable().setFocus();
		}
	}
	
	private void setTemplateDescription(String text) {
		try {
			descriptionText.setText(text != null ? text : "");
		} catch (Exception ex) {
			descriptionText.setText("");
		}
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage() && validateEGLPartName() && isValidPage();
	}
	
	private boolean validateEGLPartName() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IStatus status = workspace.validateName(getFileConfiguration().getFileName(), IResource.FILE);
		return status.isOK() ? true : false;
	}
	
	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (event.getSource() == templateViewer && validatePage(false)) {
			if (getSelectedNode() != null ) {
				IWizard wiz = getWizard();
				WizardDialog d =(WizardDialog)wiz.getContainer();
				d.showPage(getNextPage());
			} else if (getWizard().canFinish()) {
				if (getWizard().performFinish()) {
					getWizard().getContainer().getShell().close();
				}
			}
		}		
	}
	
	private class TreeContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object input) {
			return (ITemplate[]) input;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}
	}
	
	public class TreeLabelProvider implements ILabelProvider {

		public Image getImage(Object element) {
			if (element instanceof ITemplate) {
				if (((ITemplate) element).getIcon() != null) {
					return ((ITemplate) element).getIcon().createImage(); 
				}
			}
			return null;
		}

		public String getText(Object element) {
			if (element instanceof ITemplate) {
				return ((ITemplate) element).getName();
			} else {
				return null;
			}
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	} //class TreeLabelProvider
}
