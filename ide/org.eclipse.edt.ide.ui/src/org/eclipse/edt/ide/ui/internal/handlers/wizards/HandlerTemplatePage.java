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
package org.eclipse.edt.ide.ui.internal.handlers.wizards;

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
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
import org.eclipse.jface.wizard.IWizardContainer2;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class HandlerTemplatePage extends WizardSelectionPage implements	ISelectionChangedListener, IDoubleClickListener {

	protected HandlerTemplatePage(String pageName) {
		super(pageName);

		setTitle(NewHandlerWizardMessages.HandlerTemplatePage_title);
		setDescription(NewHandlerWizardMessages.HandlerTemplatePage_description);
		// TODO #jiyong# Change the image descriptor
		setImageDescriptor(PluginImages.DESC_WIZBAN_NEWTEMPLATESELECTION);
	}

	protected TableViewer templateViewer;
	protected Text descriptionText;

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, 0);
		// TODO #jiyong# Change the help content
		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, IUIHelpConstants.EGL_NEW_RECORD_TEMPLATE_SELECTION_PAGE);

		container.setLayout(new GridLayout(2, true));

		Label label = new Label(container, 0);
		label.setText(NewHandlerWizardMessages.HandlerTemplatePage_templatesLabel);

		label = new Label(container, 0);
		label.setText(NewHandlerWizardMessages.HandlerTemplatePage_descriptionLabel);

		templateViewer = new TableViewer(container, SWT.BORDER);
		Table control = templateViewer.getTable();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 350;
		data.widthHint = 250;
		control.setLayoutData(data);

		ITemplate[] templates = TemplateManager.getInstance().getTemplates("org.eclipse.edt.ide.ui.handlers");

		templateViewer.setContentProvider(new TreeContentProvider());
		templateViewer.setLabelProvider(new TreeLabelProvider());
		templateViewer.addSelectionChangedListener(this);
		templateViewer.setInput(templates);
		templateViewer.addDoubleClickListener(this);
		
		descriptionText = new Text(container, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
		descriptionText.setLayoutData(new GridData(GridData.FILL_BOTH));
		descriptionText.setBackground(control.getBackground());
		descriptionText.setForeground(control.getForeground());

		if (templates != null) {
			for (int i = 0; i < templates.length; i++) {
				if (templates[i].isDefault()) {
					templateViewer.setSelection(new StructuredSelection(new Object[] { templates[i] }), true);
				}
			}
		}

		setControl(container);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		Object o = ((IStructuredSelection) event.getSelection()).getFirstElement();
		if (o instanceof ITemplate) {
			handleSelectedTemplate();

			setTemplateDescription(((ITemplate) o).getDescription());
		}

		validatePage();
	}

	protected void validatePage() {
		Object o = ((IStructuredSelection) templateViewer.getSelection()).getFirstElement();
		if (o instanceof ITemplate) {
			ITemplate template = (ITemplate) o;
			TemplateWizardNode wizNode = (TemplateWizardNode) template.getWizardNode();

			handleSelectedTemplate();

			String templateID = ((ITemplate) o).getCodeTemplateId();
			boolean b = (wizNode != null && wizNode.isContentCreated() && wizNode.getWizard() != null && wizNode.getWizard().canFinish()) || templateID != null;
			setPageComplete(b);
		} else {
			setPageComplete(false);
		}
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
			validatePage();
			
			templateViewer.getTable().setFocus();
		}
	}

	private void setTemplateDescription(String text) {
		descriptionText.setText(text != null ? text : "");
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

	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (event.getSource() == templateViewer) {
			if (getSelectedNode() != null) {
				IWizard wiz = getWizard();
				IWizardContainer2 con =(IWizardContainer2) wiz.getContainer();
				WizardDialog d =(WizardDialog)wiz.getContainer();
				d.showPage(getNextPage());
			} else if (getWizard().canFinish()) {
				if (getWizard().performFinish()) {
					getWizard().getContainer().getShell().close();
				}
			}
		}		
	}

}
