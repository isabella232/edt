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
package org.eclipse.edt.ide.ui.internal.project.wizard.pages;

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.project.wizard.fragments.SourceProjectContentFragment;
import org.eclipse.edt.ide.ui.internal.project.wizards.NewEGLProjectWizard;
import org.eclipse.edt.ide.ui.internal.project.wizards.ProjectWizardUtils;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.project.templates.IProjectTemplate;
import org.eclipse.edt.ide.ui.project.templates.ProjectTemplateManager;
import org.eclipse.edt.ide.ui.project.templates.ProjectTemplateWizardNode;
import org.eclipse.edt.ide.ui.wizards.EGLWizardUtilities.NameValidatorProblemRequestor;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer2;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledFormText;

public class ProjectWizardMainPage extends WizardSelectionPage implements ISelectionChangedListener, IDoubleClickListener {	

	public static IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.edt.ide.ui", 0, "OK", null); //$NON-NLS-1$
	
	private Label projectNameLabel;
	private Text projectName;	
	private Composite contentSection;
	private SourceProjectContentFragment contentFragment;
	private ProjectConfiguration model;	
	
	protected TableViewer templateViewer;
	protected ScrolledFormText descriptionText;


	/**
	 * @param pageName
	 */
	public ProjectWizardMainPage(String pageName, ProjectConfiguration model) {
		super(pageName);
		this.model = model;
		setTitle(NewWizardMessages.EGLNewProjectWizard_0);
		setDescription(NewWizardMessages.EGLProjectWizardTypePage_1);
		setImageDescriptor(PluginImages.DESC_WIZBAN_NEWEGLPROJECT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite c = new Composite(parent, 0);
		c.setLayoutData( new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2,false);
		layout.verticalSpacing = layout.verticalSpacing * 2;
		c.setLayout(layout);
		
		createProjectNameEntry(c);
		createLocationArea(c);
		createTemplateArea(c);
		setControl(c);
		
		this.projectName.setFocus();
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IUIHelpConstants.NEW_PROJECT_WIZARD_TYPE_PAGE);
		Dialog.applyDialogFont(parent);
		/**
		 * if the wizard has been invoked from the tool bar then the current page will be null
		 * We need to check for this situation before updating the buttons
		 */
		if (getContainer().getCurrentPage() != null) {
			getContainer().updateButtons();
		}		
	}
	
	private void createProjectNameEntry(Composite parent) {
		this.projectNameLabel = new Label(parent, SWT.NULL);
		this.projectNameLabel.setText(NewWizardMessages.EGLProjectWizardTypePage_2);
		this.projectName = new Text(parent, SWT.BORDER);
		this.projectName.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String name = ((Text)e.widget).getText();
				model.setProjectName(name);
				getContainer( ).updateButtons();
				//validatePage();
			}
			
		});
				
		this.projectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Add listener for any text change in project name field
		hookListenerProjectName(projectName);
	}
	
	private void createLocationArea(Composite parent) {
		contentFragment = new SourceProjectContentFragment(parent, this);
		contentSection = contentFragment.renderSection();
		//registerFragment(contentFragment); // WILL
		//contentFragment.registerIsCompleteListener(this); // WILL
		
		/*FormData data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(projectName, 20); // WILL
		data.right = new FormAttachment(100, -10);
		contentSection.setLayoutData(data);*/
		
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		contentSection.setLayoutData(data);
	}
	
	protected void createTemplateArea(Composite parent) {
		Composite container = new Composite(parent, 0);
		/*FormData data = new FormData();
		data.left = new FormAttachment(0, 10);
		data.top = new FormAttachment(contentSection, 20);
		data.right = new FormAttachment(100, -10);*/
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		data.heightHint = 300;
		container.setLayoutData(data);
		
		GridLayout layout = new GridLayout(2, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginBottom = 1;
		layout.marginRight = 1;
		container.setLayout(layout);

		Label label = new Label(container, 0);
		label.setText(NewWizardMessages.ProjectWizardMainPage_1);
		GridData labelData = new GridData(GridData.FILL_HORIZONTAL);
		labelData.horizontalSpan = 2;
		label.setLayoutData(labelData);
		
		//NewWizardMessages.ProjectTemplateSelectionPage_templatesLabel);

		//label = new Label(container, 0);
		//label.setText(NewWizardMessages.ProjectTemplateSelectionPage_descriptionLabel);

		templateViewer = new TableViewer(container, SWT.BORDER);
		templateViewer.setComparator(new ViewerSorter());
		Table control = templateViewer.getTable();
		GridData gddata = new GridData(GridData.FILL_BOTH);
		gddata.widthHint = 250;
		control.setLayoutData(gddata);

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
		
	}
	
	public void selectionChanged(SelectionChangedEvent event) {
		Object o = ((IStructuredSelection) event.getSelection()).getFirstElement();
		if (o instanceof IProjectTemplate) {
			handleSelectedTemplate();
			setTemplateDescription(((IProjectTemplate) o).getDescription());
		}

		validatePage();
	}

	protected void handleSelectedTemplate() {
		Object o = ((IStructuredSelection) templateViewer.getSelection()).getFirstElement();
		if (o instanceof IProjectTemplate) {
			IProjectTemplate template = (IProjectTemplate) o;
			
			model.setSelectedProjectTemplate(template);
			
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
	
	/**
	 * @param text - projectName
	 */
	private void hookListenerProjectName(Text text) {
		
		text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				IStatus status = validateName(projectName.getText());
				// Check whether the project name is valid
				if (status != OK_STATUS) {
					setErrorMessage(status.getMessage());
				} else {
					setErrorMessage(null);
				}
			}	
		});
	}	
	

	public boolean isPageComplete() {
		return super.isPageComplete() && validatePage();
	}
		
	private boolean validatePage() {
		// This method is invoked before modifyText listener, so we need to check the project name
		IStatus status = validateName(projectName.getText());
		if (status != OK_STATUS) {
			contentFragment.specifyProjectDirectory.setEnabled(false);
			return false;
		} else {
			contentFragment.specifyProjectDirectory.setEnabled(true);
			if(!contentFragment.isValidateProjectLocation()){
				return false;
			}
			Object o = ((IStructuredSelection) templateViewer.getSelection()).getFirstElement();
			if (o instanceof IProjectTemplate) {
				IProjectTemplate template = (IProjectTemplate) o;
				ProjectTemplateWizardNode wizNode = (ProjectTemplateWizardNode) template.getWizardNode();
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static IStatus validateName(String name) {
		IStatus status = validateProjectName(name);
		if (!status.isOK())
			return status;
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (project.exists()) {
			return ProjectWizardUtils.createErrorStatus(NewWizardMessages.error_project_already_exists);
		}
		if (!ProjectWizardUtils.isPlatformCaseSensitive()) {
			// now look for a matching case variant in the tree
			IResource variant = ((Resource) project).findExistingResourceVariant(project.getFullPath());
			if (variant != null) {
				return ProjectWizardUtils.createErrorStatus(NewWizardMessages.error_project_exists_different_case);
			}
		}		
		return OK_STATUS;
	}
	
	public static IStatus validateProjectName(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.validateName(projectName, IResource.PROJECT);
	}
	
	public IStatus validatePackageName(String packageName) {		
		if(packageName.length() > 0){
			if(packageName.length() != packageName.trim().length()){
				return ProjectWizardUtils.createErrorStatus(NewWizardMessages.error_basepackage_spaces);
			}
			StatusInfo pkgStatus= new StatusInfo();
			ICompilerOptions compilerOption = DefaultCompilerOptions.getInstance();
	        NameValidatorProblemRequestor nameValidaRequestor = new NameValidatorProblemRequestor(pkgStatus);
			EGLNameValidator.validate(packageName, EGLNameValidator.PACKAGE, nameValidaRequestor, compilerOption);
			if(!pkgStatus.isOK())
				return ProjectWizardUtils.createErrorStatus(pkgStatus.getMessage());
		}
		
		return OK_STATUS;
	}
	
	public String getProjectName() {
		return projectName.getText();
	}
	
	public ProjectConfiguration getModel() {
		return model;
	}

	public void setProjectName(String projectName) {
		((NewEGLProjectWizard)getWizard()).getModel().setProjectName(projectName);
	}
	
	protected IProjectTemplate[] getTemplates() {
		return ProjectTemplateManager.getInstance().getTemplates("org.eclipse.edt.ide.ui.projects");
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
		if (event.getSource() == templateViewer && isPageComplete()) {
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

	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage() && validatePage();
	}
	
	

}
