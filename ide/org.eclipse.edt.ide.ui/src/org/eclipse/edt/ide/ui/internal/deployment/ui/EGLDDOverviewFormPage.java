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
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentProject;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentTarget;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.wizards.CopyEGLDDWizard;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.j2ee.project.JavaEEProjectUtilities;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetProjectCreationDataModelProperties;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectFirstPage;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

public class EGLDDOverviewFormPage extends EGLDDBaseFormPage {

	private Combo fTargetCombo;
//	private Combo fWebserviceRuntimeCombo;
	private Button fTargetButton;
	private TableViewer fClientBindingsTV ;
	private TableViewer fWSTV ;
	private Button browseBtn;
	private Button clearBtn;
	private Label fTargetProjectText;
	private TableViewer fImportsTV;
	private java.util.List contributedTableViewers;
	private boolean fProjectSelected = false;
	
	private static final String VALIDATION_KEY_TARGET = "target";
	private static final Integer VALIDATION_KEY_RUNTIME = Integer.valueOf( 999 );

	public EGLDDOverviewFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		contributedTableViewers = new ArrayList();
	}
	
	public void setActive(boolean active) {
		super.setActive(active);
		if(active){
			refreshTables();
		}
	}
	
	private void refreshTables(){
		fClientBindingsTV.refresh();
		fWSTV.refresh();
		fImportsTV.refresh();
		
		int size = contributedTableViewers.size();
		for ( int i = 0; i < size; i++ ) {
			((TableViewer)contributedTableViewers.get( i )).refresh();
		}
	}
	
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		form.setText(SOAMessages.OverviewFormTitle);
		EGLDeploymentRoot eglDDRoot = getModelRoot();
		managedForm.setInput(eglDDRoot);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);		
										
		createEGLDDSection(form, toolkit, eglDDRoot );
		
		createTargetSection(form, toolkit, SOAMessages.TargetsMainSectionTitle, SOAMessages.TargetsMainSectionDesc);

		addTableContributions(form, toolkit);
		
//		createWebserviceRuntimeSection(form, toolkit, SOAMessages.WebserviceRuntimeSectionTitle, SOAMessages.WebserviceRuntimeSectionDesc);
		
	    fWSTV = createTableSection(form, toolkit, SOAMessages.WSMainSectionTitle, SOAMessages.WSMainSectionDescription, 
				new EGLDDWebServicesBlock.WSListContentProvider(), 
				new EGLDDWebServicesBlock.WSListLabelProvider(), eglDDRoot, EGLDeploymentDescriptorEditor.PAGEID_WEBSERVICES, 
				IUIHelpConstants.EGLDD_EDITOR_OVERVIEWPAGE_WS);
		
		fClientBindingsTV = createBindingTableSection(form, toolkit, SOAMessages.BindingMainSectionTitle, SOAMessages.BindingMainSectionDescription,
				new EGLDDBindingBlock.ServiceBindingContentProvider(),
				new EGLDDBindingBlock.ServiceBindingLabelProvider(), eglDDRoot, EGLDeploymentDescriptorEditor.PAGEID_BINDINGS, 
				IUIHelpConstants.EGLDD_EDITOR_OVERVIEWPAGE_SERVICEBINDINGS);
		fImportsTV = createTableSection(form, toolkit, SOAMessages.ImportsSectionTitle, SOAMessages.ImportsSectionDescription, 
				new EGLDDImportsFormPage.IncludeListContentProvider(), 
				new EGLDDImportsFormPage.IncludeListLabelProvider(), eglDDRoot, EGLDeploymentDescriptorEditor.PAGEID_IMPORTS, 
				IUIHelpConstants.EGLDD_EDITOR_OVERVIEWPAGE_IMPORTS);
		
		managedForm.reflow( true );
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(), getHelpID());
	}
	
	private void addTableContributions(ScrolledForm form, FormToolkit toolkit) {
		EGLDeploymentRoot eglDDRoot = getModelRoot();
		IEGLDDContributionPageProvider[] providers = EGLDeploymentDescriptorEditor.getEditorProviders();
		for (int i = 0; i < providers.length; i++) {
			Composite client = EGLDDBaseFormPage.createExpandableSection( form, toolkit,
					providers[i].getOverviewTitle(), providers[i].getOverviewDescription(), 2, 1, true );
			if (client != null) {
				Table table = providers[i].createTable(client, toolkit);
				if (table != null) {
					contributedTableViewers.add(
							createTableSection(
									form,
									toolkit,
									providers[i].getOverviewTitle(),
									providers[i].getOverviewDescription(),
									client,
									table,
									providers[i].getTableContentProvider(getEditorProject()),
									providers[i].getTableLabelProvider(),
									eglDDRoot,
									providers[i].getDetailPageId(),
									providers[i].getHelpId()));
				}
			}
		}
	}
	
	private void createEGLDDSection(final ScrolledForm form, FormToolkit toolkit, final EGLDeploymentRoot serviceBindingRoot) {
		String sectionTitle = SOAMessages.EGLDDSectionTitle;
		IEditorInput editorInput = getEditorInput();
		if(editorInput instanceof FileEditorInput){
			FileEditorInput fileinput = (FileEditorInput)editorInput;
			String fullName = fileinput.getFile().getName();
			//remove the extension
			String name = fullName.substring(0, fullName.indexOf('.'));
			sectionTitle = sectionTitle + " - " + name;			 //$NON-NLS-1$
		}
		Composite client = createNonExpandableSectionWithoutDesc(form, toolkit, sectionTitle, 4);
		
		FormText text = toolkit.createFormText( client, false );
		text.setText( SOAMessages.OverviewInformationalMessage, true, false );
		GridData gd = new GridData();
		gd.horizontalSpan = 4;
		text.setLayoutData( gd );
		
		gd = new GridData();
		gd.horizontalSpan = 4;
		toolkit.createLabel( client, "" ).setLayoutData( gd ); //$NON-NLS-1$				
		toolkit.paintBordersFor(client);		
	}
	
//	private void createWebserviceRuntimeSection(final ScrolledForm form, FormToolkit toolkit, String title, String description) {
//		Composite client = createExpandableSection(form, toolkit, title, description, 1, 1, true);
//		createWebserviceRuntimeSection(toolkit, client);
//	}
//
//	private void createWebserviceRuntimeSection(FormToolkit toolkit, Composite client) {
//		GridData gd = new GridData(GridData.FILL_BOTH);
//		gd.heightHint = 100;
//		gd.widthHint = 100;
//		Group grp = new Group(client, SWT.NONE);
//		toolkit.adapt(grp);
//		GridLayout g1 = new GridLayout(4, false);
//		grp.setLayout(g1);
//		grp.setLayoutData(gd);
//		
//		fWebserviceRuntimeCombo = new Combo(grp, SWT.READ_ONLY|SWT.BORDER);
//		gd = new GridData( GridData.FILL_HORIZONTAL );
//		gd.widthHint = 200;
//		fWebserviceRuntimeCombo.setLayoutData( gd );
//		
//		WebserviceRuntimeType webserviceRuntime = getModelRoot().getDeployment().getWebserviceRuntime();
//		List<WebserviceRuntimeType> webserviceRuntimeTypes = WebserviceRuntimeType.VALUES;
//		String[] runtimesArray = new String[webserviceRuntimeTypes.size()];
//		int idx = 0;
//		int selectedIdx = -1;
//		for( Iterator<WebserviceRuntimeType> itr = webserviceRuntimeTypes.iterator();itr.hasNext();idx++){
//			WebserviceRuntimeType wsRuntime = itr.next();
//			runtimesArray[idx] = wsRuntime.getLiteral();
//			if( webserviceRuntime.getValue() == wsRuntime.getValue() ){
//				selectedIdx = idx;
//			}
//		}
//		fWebserviceRuntimeCombo.setItems( runtimesArray );
//		fWebserviceRuntimeCombo.select(selectedIdx);
//
//		fWebserviceRuntimeCombo.addSelectionListener( new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent event) {
//				String webserviceRuntimeStr = fWebserviceRuntimeCombo.getItems()[fWebserviceRuntimeCombo.getSelectionIndex()].trim();
//				if( WebserviceRuntimeType.JAXWS.getLiteral().equalsIgnoreCase(webserviceRuntimeStr) &&
//						hasExternalTypeService() ){
//					IStatus status = EGLUIStatus.createError(-1, SOAMessages.JAXWSNotSupportedforExternalType, null);
//					ErrorDialog.openError(getEditorSite().getShell(), null, null, status);
//					fWebserviceRuntimeCombo.select(0);
//				}
//				else{
//					if(WebserviceRuntimeType.JAXRPC.getLiteral().equalsIgnoreCase(webserviceRuntimeStr)){
//						getModelRoot().getDeployment().unsetWebserviceRuntime();
//					}
//					else{
//						getModelRoot().getDeployment().setWebserviceRuntime(WebserviceRuntimeType.get(webserviceRuntimeStr));
//					}
//				}
//			}
//		});
//	}
//	
//	private boolean hasExternalTypeService(){
//		Webservices wss = getModelRoot().getDeployment().getWebservices();
//		if(wss != null){
//			List<PartDeclarationInfo> eglExternalTypePartsList = getAllExternalTypeParts();
//			for(Iterator<Webservice> it = wss.getWebservice().iterator(); it.hasNext();){
//				String impl = it.next().getImplementation();
//				for(Iterator<PartDeclarationInfo> et = eglExternalTypePartsList.iterator(); et.hasNext();){
//					PartDeclarationInfo partInfo = et.next();
//					if(partInfo.getFullyQualifiedName().equalsIgnoreCase(impl))
//						return true;
//				}		
//			}
//		}
//
//		return false;
//	}
//	
//	private List<PartDeclarationInfo> getAllExternalTypeParts(){
//		EGLDeploymentDescriptorEditor eglDDEditor = (EGLDeploymentDescriptorEditor)getEditor();		
//		final IEGLProject eglProj = EGLCore.create(eglDDEditor.getProject());
//		final List<PartDeclarationInfo> eglExternalTypePartsList = new ArrayList();
//		try{
//			IRunnableWithProgress searchOp = new IRunnableWithProgress(){
//				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//					try {
//						//search for the service part in the curr project
//						final IEGLSearchScope projSearchScope = SearchEngine.createEGLSearchScope(new IEGLElement[]{eglProj}, false);						
//						AllPartsCache.getParts(projSearchScope, IEGLSearchConstants.EXTERNALTYPE, EGLDDWebServicesBlock.HOSTPGM, monitor, eglExternalTypePartsList);
//					} catch (EGLModelException e) {
//						EDTUIPlugin.log( e );
//					}					
//				}
//			};
//			
//			new ProgressMonitorDialog(getEditorSite().getShell()).run(true, false, searchOp);
//		}catch (InvocationTargetException e) {
//			EDTUIPlugin.log( e );
//		} catch (InterruptedException e) {
//			EDTUIPlugin.log( e );
//		}
//		return eglExternalTypePartsList;
//	}
	private void createTargetSection(final ScrolledForm form, FormToolkit toolkit, String title, String description) {
		Composite client = createExpandableSection(form, toolkit, title, description, 1, 1, true);
		createTargetSection(toolkit, client);
	}

	private void createTargetSection(FormToolkit toolkit, Composite client) {
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 100;
		Group grp = new Group(client, SWT.NONE);
		toolkit.adapt(grp);
		GridLayout g1 = new GridLayout(4, false);
		grp.setLayout(g1);
		grp.setLayoutData(gd);
		
		fTargetProjectText = toolkit.createLabel(grp, SOAMessages.TargetWebProjectLabel);
		fTargetCombo = new Combo(grp, SWT.READ_ONLY|SWT.BORDER);
		gd = new GridData( GridData.FILL_HORIZONTAL );
		gd.widthHint = 200;
		fTargetCombo.setLayoutData( gd );
		fTargetCombo.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String project = fTargetCombo.getItems()[fTargetCombo.getSelectionIndex()].trim();
				if ( project.length() == 0 ) {
					EGLDDRootHelper.removeTarget( getModelRoot() );
					projectDeselected();
				}
				else {
					DeploymentProject target = DeploymentFactory.eINSTANCE.createDeploymentProject();
					target.setName( project );
					EGLDDRootHelper.setTarget( target, getModelRoot() );
					projectSelected();
				}
			}
		});
		
		
		fTargetButton = toolkit.createButton( grp, SOAMessages.NewLabel, SWT.PUSH );
		gd = new GridData( );
		gd.horizontalAlignment = GridData.FILL;
		fTargetButton.setLayoutData(gd);
		fTargetButton.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				WebProjectWizard wizard = new WebProjectWizard() {
					protected IWizardPage createFirstPage() {
						// Force 'add to ear' checkbox unchecked. Otherwise Apache runtimes are initially hidden.
						this.model.setProperty( IJ2EEFacetProjectCreationDataModelProperties.ADD_TO_EAR, Boolean.FALSE );
						this.model.setProperty( IJ2EEFacetProjectCreationDataModelProperties.EAR_PROJECT_NAME, "" ); //$NON-NLS-1$
						
						// Force a target runtime.
						WebProjectFirstPage page = new WebProjectFirstPage(model, "first.page"){ //$NON-NLS-1$
							protected void validatePage(boolean showMessage) {
								setOKStatus( VALIDATION_KEY_RUNTIME );
								if (showMessage) {
									Object runtime = model.getProperty(FACET_RUNTIME);
									if (serverTargetCombo.isEnabled()) {
										if (!(runtime instanceof IRuntime)) {
											setErrorStatus( VALIDATION_KEY_RUNTIME, SOAMessages.NewWebProjectNoRuntimeError );										
										}
									}
									if (runtime instanceof IRuntime && ((IRuntime)runtime).supports(IJ2EEFacetConstants.ENTERPRISE_APPLICATION_FACET)) {
										if (!model.getBooleanProperty(IJ2EEFacetProjectCreationDataModelProperties.ADD_TO_EAR)) {
											setErrorStatus( VALIDATION_KEY_RUNTIME, SOAMessages.NewWebProjectNoEARWarning );
										}
									}
								}
								super.validatePage(showMessage);
							}
							
							protected Composite createTopLevelComposite( Composite parent ) {
								Composite composite = super.createTopLevelComposite(parent);
								if ( earPanel != null ) {
									projectNameGroup.projectNameField.addModifyListener( new ModifyListener() {
										public void modifyText( ModifyEvent e ) {
											model.setProperty( IJ2EEFacetProjectCreationDataModelProperties.EAR_PROJECT_NAME,
													((Text)e.widget).getText() + "EAR" ); //$NON-NLS-1$
										}
									});
								}
								return composite;
							}
						};
						return page;
					}
					
					protected String getFinalPerspectiveID() {
						// Don't prompt about switching perspectives.
						return null;
					}
				};
				
				WizardDialog dialog = new WizardDialog( getEditorSite().getShell(), wizard );
		        dialog.create();
		        if ( dialog.open() == Window.OK ) {
					// Select it and add to list.
		        	DeploymentProject project = DeploymentFactory.eINSTANCE.createDeploymentProject();
		        	project.setName( wizard.getProjectName() );
		        	EGLDDRootHelper.setTarget( project, getModelRoot() );
		        	initTargets();
		        }
			}
		});
		Label spacer = toolkit.createLabel( grp, "" ); //$NON-NLS-1$
		gd = new GridData( GridData.GRAB_HORIZONTAL);
		spacer.setLayoutData(gd);
		
		projectDeselected();
		initTargets();
	}

	private void projectSelected()
	{
		fProjectSelected = true;
	}
	
	private void projectDeselected()
	{
		fProjectSelected = false;
	}
	
	public void initTargets() {
		DeploymentTarget target = EGLDDRootHelper.getDeploymentTarget(getModelRoot());
		
		IProject[] iprojects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List webProjects = new ArrayList( iprojects.length );
		webProjects.add( "" );
		
		for ( int i = 0; i < iprojects.length; i++ )
		{
			if ( JavaEEProjectUtilities.isDynamicWebProject( iprojects[ i ] ) )
			{
				webProjects.add( iprojects[i].getName() );
			}
		}
		
		String[] projectArray = (String[])webProjects.toArray( new String[webProjects.size()] );
		fTargetCombo.setItems( projectArray );
		if( target instanceof DeploymentProject )
		{
			String projectToSelect = ((DeploymentProject)target).getName();
			if (webProjects.size() > 0) {
				int selectIndex = 0;
				if (projectToSelect != null) {
					for ( int i = 0; i < projectArray.length; i++ ) {
						if ( projectToSelect.equals( projectArray[ i ] ) ) {
							selectIndex = i;
							break;
						}
					}
				}
				fTargetCombo.select( selectIndex );
				if ( selectIndex > 0 )
				{
					projectSelected();
				}
			}
		}
	}

	protected void CopyFromAnotherEGLDD() {
		CopyEGLDDWizard wizard = new CopyEGLDDWizard();
		EGLDeploymentDescriptorEditor eglDDEditor = (EGLDeploymentDescriptorEditor)getEditor();		
		wizard.init(getModelRoot(), ((IFileEditorInput)getEditorInput()).getFile(), eglDDEditor.getProject());
		openWizard(wizard);
		refreshTables();
	}
	
	private TableViewer createTableSection(final ScrolledForm form, FormToolkit toolkit, String title, String description, 
			IContentProvider contentProvider, IBaseLabelProvider labelProvider, Object input, final String detailPageId, String helpId) {
		Composite client = createExpandableSection(form, toolkit, title, description, 2, 1, true);
		
		Table t = toolkit.createTable(client, SWT.NULL);
		return createTableSection(form, toolkit, title, description, client, t, contentProvider, labelProvider, input, detailPageId, helpId);
	}
	
	
	private TableViewer createBindingTableSection(final ScrolledForm form, FormToolkit toolkit, String title, String description, 
			IContentProvider contentProvider, IBaseLabelProvider labelProvider, Object input, final String detailPageId, String helpId) {
		Composite client = createExpandableSection(form, toolkit, title, description, 2, 1, true);
		
		Table t = EGLDDBindingBlock.createTableControl(toolkit, client);
		return createTableSection(form, toolkit, title, description, client, t, contentProvider, labelProvider, input, detailPageId, helpId);
	}
	
	private TableViewer createTableSection(final ScrolledForm form, FormToolkit toolkit, String title, String description, Composite client, Table t,
			IContentProvider contentProvider, IBaseLabelProvider labelProvider, Object input, final String detailPageId, String helpId) {
		//Composite client = createExpandableSection(form, toolkit, title, description, 2, true);		
		//Table t = toolkit.createTable(client, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		gd.widthHint = 100;
		t.setLayoutData(gd);
		//t.setLinesVisible(true);
		toolkit.paintBordersFor(client);		
		
		final TableViewer tableviewer = new TableViewer(t);
		if(contentProvider != null)
			tableviewer.setContentProvider(contentProvider);
		if(labelProvider != null)
			tableviewer.setLabelProvider(labelProvider);
		tableviewer.setSorter( new ViewerSorter() );
		tableviewer.setInput(input);
		
		//double click on the item will be the same as the detail button
		t.addSelectionListener(new SelectionAdapter(){
			public void widgetDefaultSelected(SelectionEvent e) {
				selectAndRevealDetail(detailPageId, tableviewer);
			}			
		});
		
		Button b = toolkit.createButton(client, SOAMessages.DetailLabel, SWT.PUSH);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		b.setLayoutData(gd);
		b.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				selectAndRevealDetail(detailPageId, tableviewer);
			}
		});		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(client, helpId);
		
		return tableviewer;
	}	
	
	private void selectAndRevealDetail(final String detailPageId, final TableViewer tableviewer) {
		IFormPage activePage = getEditor().setActivePage(detailPageId);
		ISelection sel = tableviewer.getSelection();
		if(!sel.isEmpty())
		{
			if(activePage instanceof EGLDDBaseFormPage)
			{
				EGLDDBaseFormPage basePage = (EGLDDBaseFormPage)activePage;
				basePage.selectReveal(sel);
			}
		}
	}
	
	public void setFocus() {
		if (fTargetCombo != null) {
			fTargetCombo.setFocus();
		}
	}
	
	protected String getHelpID() {
		return IUIHelpConstants.EGLDD_EDITOR_OVERVIEWPAGE;
	}
}
