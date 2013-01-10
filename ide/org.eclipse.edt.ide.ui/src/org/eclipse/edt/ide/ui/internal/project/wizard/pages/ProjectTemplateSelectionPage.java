/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.project.templates.IProjectTemplate;
import org.eclipse.edt.ide.ui.project.templates.ProjectTemplateManager;
import org.eclipse.edt.ide.ui.project.templates.ProjectTemplateWizardNode;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledFormText;

public class ProjectTemplateSelectionPage extends WizardSelectionPage implements ISelectionChangedListener, IDoubleClickListener {
	
	protected TableViewer templateViewer;
	protected ScrolledFormText descriptionText;
	
	public ProjectTemplateSelectionPage(String pageName) {
		super(pageName);
		
		setTitle(NewWizardMessages.ProjectTemplateSelectionPage_title);
		setDescription(NewWizardMessages.ProjectTemplateSelectionPage_description);
		//setImageDescriptor(EDTUIPlugin.imageDescriptorFromPlugin(EDTUIPlugin.PLUGIN_ID, "icons/wizban/newxsltemplate_wiz.gif"));
		//setImageDescriptor(PluginImages.DESC_WIZBAN_NEWTEMPLATESELECTION);
	}	

	protected void setLabel(Composite container) {
		Label label = new Label(container, 0);
		label.setText(NewWizardMessages.ProjectTemplateSelectionPage_templatesLabel);

		label = new Label(container, 0);
		label.setText(NewWizardMessages.ProjectTemplateSelectionPage_descriptionLabel);
	}

	protected IProjectTemplate[] getTemplates() {
		return ProjectTemplateManager.getInstance().getTemplates("org.eclipse.edt.ide.ui.projects");
	}

	protected void setHelp(Composite container) {
//		TODO add help
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, IUIHelpConstants.EGL_NEW_RECORD_TEMPLATE_SELECTION_PAGE);
	}


	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, 0);
		
		setHelp(container);

		container.setLayout(new GridLayout(2, true));

		setLabel(container);

		templateViewer = new TableViewer(container, SWT.BORDER);
		Table control = templateViewer.getTable();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 350;
		data.widthHint = 250;
		control.setLayoutData(data);

		IProjectTemplate[] templates = getTemplates();

		templateViewer.setContentProvider(new TreeContentProvider());
		templateViewer.setLabelProvider(new TreeLabelProvider());
		templateViewer.addSelectionChangedListener(this);
		templateViewer.setInput(templates);
		templateViewer.addDoubleClickListener(this);
		
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		descriptionText = new ScrolledFormText(container, SWT.V_SCROLL | SWT.H_SCROLL, false);
		
		int borderStyle = toolkit.getBorderStyle() == SWT.BORDER ? SWT.NULL : SWT.BORDER;
		if (borderStyle == SWT.NULL) {
			descriptionText.setData(FormToolkit.KEY_DRAW_BORDER,
                    FormToolkit.TREE_BORDER);
            toolkit.paintBordersFor(container);
        }
		
        FormText ftext = toolkit.createFormText(descriptionText, false);        
        descriptionText.setFormText(ftext);
        descriptionText.setExpandHorizontal(false);
        descriptionText.setExpandVertical(false);
        descriptionText.setBackground(toolkit.getColors().getBackground());
        descriptionText.setForeground(toolkit.getColors().getForeground());
        
        ftext.marginWidth = 2;
        ftext.marginHeight = 2;
                
        descriptionText.setLayoutData(new GridData(GridData.FILL_BOTH));
        
		//descriptionText = new Text(container, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
		//descriptionText.setLayoutData(new GridData(GridData.FILL_BOTH));
		//descriptionText.setBackground(control.getBackground());
		//descriptionText.setForeground(control.getForeground());

		if (templates != null) {
			IPreferenceStore store = EDTUIPlugin.getDefault().getPreferenceStore();
			String selectedId = store.getString(EDTUIPreferenceConstants.NEWPROJECTWIZARD_SELECTEDTEMPLATE);
			int selectedIndex = -1, defaultIndex = -1;
			for (int i = 0; i < templates.length; i++) {
				if(templates[i].getId().equals(selectedId)){
					selectedIndex = i;
				}
				if (templates[i].isDefault()) {
					defaultIndex = i;
				}
			}
			if(selectedIndex < 0) selectedIndex = defaultIndex;
			if(selectedIndex < 0) selectedIndex = 0;
			templateViewer.setSelection(new StructuredSelection(new Object[] { templates[selectedIndex] }), true);
		}

		setControl(container);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		Object o = ((IStructuredSelection) event.getSelection()).getFirstElement();
		if (o instanceof IProjectTemplate) {
			handleSelectedTemplate();
			setTemplateDescription(((IProjectTemplate) o).getDescription());
		}

		validatePage();
	}

	protected void validatePage() {
		Object o = ((IStructuredSelection) templateViewer.getSelection()).getFirstElement();
		if (o instanceof IProjectTemplate) {
			IProjectTemplate template = (IProjectTemplate) o;
			ProjectTemplateWizardNode wizNode = (ProjectTemplateWizardNode) template.getWizardNode();

			handleSelectedTemplate();

			boolean b = (wizNode != null && ( wizNode.getTemplate().canFinish() || ( wizNode.isContentCreated() && wizNode.getWizard() != null && wizNode.getWizard().canFinish())));
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
		if (o instanceof IProjectTemplate) {
			IProjectTemplate template = (IProjectTemplate) o;
			ProjectTemplateWizardNode wizNode = (ProjectTemplateWizardNode) template.getWizardNode();
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
		}
	}

	private void setTemplateDescription(String text) {
		try {
			descriptionText.setText(text != null ? text : "");
		} catch (Exception ex) {
			descriptionText.setText("");
		}
	}

	private class TreeContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object input) {
			return (IProjectTemplate[]) input;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

	}

	public class TreeLabelProvider implements ILabelProvider {

		public Image getImage(Object element) {
			if (element instanceof IProjectTemplate) {
				if (((IProjectTemplate) element).getIcon() != null) {
					return ((IProjectTemplate) element).getIcon().createImage(); 
				}
			}

			return null;
		}

		public String getText(Object element) {
			if (element instanceof IProjectTemplate) {
				return ((IProjectTemplate) element).getName();
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
