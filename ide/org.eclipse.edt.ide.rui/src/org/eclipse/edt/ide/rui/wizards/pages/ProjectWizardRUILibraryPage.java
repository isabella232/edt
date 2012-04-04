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
package org.eclipse.edt.ide.rui.wizards.pages;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.rui.internal.project.CommonUtilities;
import org.eclipse.edt.ide.rui.internal.project.IWidgetLibraryConflict;
import org.eclipse.edt.ide.rui.internal.wizards.RuiNewWizardMessages;
import org.eclipse.edt.ide.rui.wizards.WebClientProjectTemplateWizard;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.project.wizard.pages.ProjectWizardPage;
import org.eclipse.edt.ide.ui.internal.project.wizard.pages.SourceProjectWizardCapabilityPage;
import org.eclipse.edt.ide.ui.internal.project.wizards.NewEGLProjectWizard;
import org.eclipse.edt.ide.ui.internal.project.wizards.ProjectWizardUtils;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.wizards.EGLWizardUtilities.NameValidatorProblemRequestor;
import org.eclipse.edt.ide.widgetLibProvider.IWidgetLibProvider;
import org.eclipse.edt.ide.widgetLibProvider.WidgetLibProviderManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;


public class ProjectWizardRUILibraryPage extends ProjectWizardPage {	
	
	private static final String RUI_WIDGET_LIBRARY_ID = "org.eclipse.edt.rui.widgets_0.8.0";
	public static IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.edt.ide.rui", 0, "OK", null); //$NON-NLS-1$
	private static final String BASE_PACKAGE_HINT = "com.mycompany.myapp"; //$NON-NLS-1$
	private Label basePackageLabel;
	protected Text basePackage;
	
	/** The table presenting the templates. */
	private CheckboxTableViewer fTableViewer;
	private Label fDetailLabel;
	private Label fDetailTitleLabel;
	private Label fLogoLabel;
	private HashMap<String, LibraryNode> fLibraryNodes = new HashMap<String, LibraryNode>();
	private IWidgetLibProvider[] libProviders;
	
	private Hashtable libraryImages = new Hashtable();

	public ProjectWizardRUILibraryPage(String pageName) {
		super(pageName);
		setTitle(RuiNewWizardMessages.RUILibraryPageTitle);
		setDescription(RuiNewWizardMessages.RUILibraryPageDescription);
		populateLibraryData();
	}
	
	public void createContents(Composite ancestor) {
		GridData adata = new GridData(GridData.FILL_BOTH);
		ancestor.setLayoutData(adata);
		ancestor.setLayout(new GridLayout());	
		
		createBasePackageEntry(ancestor);
        createSelectionTable(ancestor);
		createDetailGroup(ancestor);
		
		fTableViewer.getTable().setFocus();
		
		//TODO: <jiyong> Help
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HelpContextIDs.New_EGL_Base_Project_Advanced_Page);
		
	}
	
	protected void createBasePackageEntry(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData( new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout(2,false);
//		layout.verticalSpacing = layout.verticalSpacing * 2;
		layout.marginWidth = 0;
		c.setLayout(layout);
		
		this.basePackageLabel = new Label(c, SWT.NULL);
		this.basePackageLabel.setText(NewWizardMessages.EGLProjectWizardTypePage_BasePackage);
		this.basePackage = new Text(c, SWT.BORDER);
		this.basePackage.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String name = ((Text)e.widget).getText();
				((NewEGLProjectWizard)((WebClientProjectTemplateWizard)getWizard()).getParentWizard()).getModel().setBasePackageName(name);		
			}
			
		});
		
		this.basePackage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		String defaultBasePackageName = ((NewEGLProjectWizard)((WebClientProjectTemplateWizard)getWizard()).getParentWizard()).getModel().getBasePackageName();
		if(defaultBasePackageName != null && !defaultBasePackageName.isEmpty()){
			basePackage.setText(defaultBasePackageName);
		}
		
		hookListenerPackageName(basePackage);
		new Label(c, SWT.NULL).setText(NewWizardMessages.EGLProjectWizardTypePage_BasePackage_Example);
		new Label(c, SWT.NULL).setText(BASE_PACKAGE_HINT);
	}
	
	private void hookListenerPackageName(Text text) {
		text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				IStatus status = validatePackageName(basePackage.getText());
				// Check whether the project name is valid
				if (status != OK_STATUS) {
					setErrorMessage(status.getMessage());
				} else {
					setErrorMessage(null);
				}
			    getWizard().getContainer().updateButtons();
			}	
		});
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
	
	public boolean isPageComplete() {
		return super.isPageComplete() && validatePage();
	}
		
	private boolean validatePage() {
		// This method is invoked before modifyText listener, so we need to check the project name
		IStatus status = validatePackageName(basePackage.getText());
		if(status != OK_STATUS)
			return false;
		
		return true;
	}
	
	private void populateLibraryData() {
		// Get the project name
		libProviders = WidgetLibProviderManager.getInstance().getProviders();
		
		if (libProviders != null) {
			String id, libName, resourcePluginName, resourceFolder, projectName;
			boolean selected;
			for (int i = 0; i < libProviders.length; i++) {
				id = libProviders[i].getId();
				libName = libProviders[i].getLibName();
				selected = libProviders[i].isSelected();
				resourcePluginName = libProviders[i].getResourcePluginName();
				resourceFolder = libProviders[i].getResourceFolder();
				projectName = libProviders[i].getProjectName();
				if(id != null){
					try {
						URL url = CommonUtilities.getWidgetProjectURL(resourcePluginName, resourceFolder, projectName);
						if(url != null) {
							if(!fLibraryNodes.containsKey(libName)){
								fLibraryNodes.put(libName, new LibraryNode(libName));
							}
							fLibraryNodes.get(libName).addProvider(libProviders[i]);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
		}
		
	}

	private void createSelectionTable(Composite ancestor) {
		Composite tableComposite= new Composite(ancestor, SWT.NONE);
        GridData data= new GridData(GridData.FILL_BOTH);
        data.widthHint= 360;
        tableComposite.setLayoutData(data);

        ColumnLayout columnLayout= new ColumnLayout();
        tableComposite.setLayout(columnLayout);
		Table table= new Table(tableComposite, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		fTableViewer= new CheckboxTableViewer(table);
		fTableViewer.setComparator(new ViewerSorter());		
		fTableViewer.setContentProvider(new LibraryContentProvider());
		
		TableViewerColumn column1= new TableViewerColumn(fTableViewer, new TableColumn(table, SWT.NONE));
		column1.getColumn().setText(RuiNewWizardMessages.RUILibraryPage_libname_label);
		column1.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				LibraryNode insertDataNode = (LibraryNode)element;
				return insertDataNode.getName();
			}
		});		
		columnLayout.addColumnData(new ColumnWeightData(4, 70, true));

		final TableViewerColumn column2= new TableViewerColumn(fTableViewer, new TableColumn(table, SWT.NONE));
		column2.getColumn().setText(RuiNewWizardMessages.RUILibraryPage_version_label);
		column2.setEditingSupport(new VersionColumnEditingSupport(fTableViewer));
		column2.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				LibraryNode insertDataNode = (LibraryNode)element;
				return insertDataNode.getVersion();
			}
		});
		columnLayout.addColumnData(new ColumnWeightData(2, 70, true));

		final TableViewerColumn column3= new TableViewerColumn(fTableViewer, new TableColumn(table, SWT.NONE));
		column3.getColumn().setText(RuiNewWizardMessages.RUILibraryPage_provider_label);
		column3.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				LibraryNode insertDataNode = (LibraryNode)element;
				return insertDataNode.getProvider();
			}
		});
//		column3.getColumn().setToolTipText(NewWizardMessages.RUILibraryPage_version_tooltip);
		
		columnLayout.addColumnData(new ColumnWeightData(2, 40, true));		
		

		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				setDetails();
			}
			
		});

		fTableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				LibraryNode newElement = (LibraryNode) event.getElement();
				if(!(event.getChecked()) && newElement.getId().equals(RUI_WIDGET_LIBRARY_ID)){
					fTableViewer.setChecked(newElement, true);
					return;
				}
				setSelectedWidgetLibrary((LibraryNode) event.getElement());
			}			

		});
		fTableViewer.setInput(new ArrayList<LibraryNode>(fLibraryNodes.values()));
		fTableViewer.setAllChecked(false);		
		setSelectedWidgetLibrary();		
	}

	private void createDetailGroup(Composite ancestor) {
		Group group = new Group(ancestor, SWT.NULL);
		group.setText(RuiNewWizardMessages.RUILibraryPage_details_label);

		//GridLayout
		GridLayout layout = new GridLayout(2, false);
		group.setLayout(layout);

		//GridData
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalIndent = 0;
        data.heightHint= 150;
		group.setLayoutData(data);
				
		Composite innerComposite = new Composite(group, SWT.NONE);
		data = new GridData(GridData.FILL_BOTH);
		innerComposite.setLayoutData(data);		
		innerComposite.setLayout(new FillLayout());
		
		/*fDetailTitleLabel = new Label(innerComposite, SWT.BOLD);
		data = new GridData(GridData.FILL);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		fDetailTitleLabel.setLayoutData(data);*/
		fDetailLabel = new Label( innerComposite, SWT.NONE | SWT.WRAP);
		/*data = new GridData(GridData.FILL);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		fDetailLabel.setLayoutData(data);*/

		fLogoLabel = new Label( group, SWT.NONE);		 
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		fLogoLabel.setLayoutData(data);		
	}	
	
	private Object[] getSelectedLib() {
		Iterator iter = fLibraryNodes.values().iterator();
		ArrayList<LibraryNode> nodes = new ArrayList<LibraryNode>();
		while(iter.hasNext()){
			LibraryNode node = (LibraryNode) iter.next();
			if(node.isSelected())
				nodes.add(node);
		}
		return nodes.toArray(new Object[nodes.size()]);
	}

	public void setProjectName(String projectName) {
		((NewEGLProjectWizard)((WebClientProjectTemplateWizard)getWizard()).getParentWizard()).getModel().setProjectName(projectName);		
	}
	
	private void setSelectedWidgetLibrary() {
		Object[] selectedLib = getSelectedLib();
		for(Object element : selectedLib){
			// New element has conflict with the current selected library, stop adding it
			fTableViewer.setChecked(element, true);

			if(((LibraryNode)element).getId().equals(RUI_WIDGET_LIBRARY_ID)){
				fTableViewer.setGrayed(element, true);
			}
			
			setSelectedWidgetLibrary((LibraryNode) element);
		}
	}
	
	private void setSelectedWidgetLibrary(LibraryNode newElement) {
		Object[] checkElements =  fTableViewer.getCheckedElements();
		ArrayList<String> selectedWidgetLibraries = new ArrayList<String>();
		for(Object element : checkElements){
			// New element has conflict with the current selected library, stop adding it
			LibraryNode node = (LibraryNode) element;
			if(element != newElement && (newElement.isConflict(node.getId()) || node.isConflict(newElement.getId()))){
				fTableViewer.setChecked(newElement, false);
				return;
			}				
			else
				selectedWidgetLibraries.add(((LibraryNode) element).getId());
		}
		((NewEGLProjectWizard)((WebClientProjectTemplateWizard)getWizard()).getParentWizard()).getModel().setSelectedWidgetLibraries(selectedWidgetLibraries);
		updateNextPageBuildPathSelections(selectedWidgetLibraries);
	}
	
	private void updateNextPageBuildPathSelections(List<String> selectedWidgetLibraries){
		IWizardPage nextPage = getNextPage();
		SourceProjectWizardCapabilityPage apage = (SourceProjectWizardCapabilityPage)nextPage;
		apage.setfSelectedImportProjectList(selectedWidgetLibraries);
	}
	
	private void setDetails() {
		IStructuredSelection selection= (IStructuredSelection) fTableViewer.getSelection();

		if (selection.size() == 1) {
			LibraryNode data = (LibraryNode) selection.getFirstElement();
			fDetailLabel.setText(data.getDetail());
			ImageDescriptor logo = data.getLogo();
			if (logo != null) {
				Image image = null;
				if (libraryImages.containsKey(data))
					image = (Image)libraryImages.get(data);
				else {
					image = logo.createImage();
					libraryImages.put(data, image);
				}
				fLogoLabel.setImage(image);
			} else {
				fLogoLabel.setImage(null);
			}
			fLogoLabel.getParent().layout(true);
		} else {
			fDetailLabel.setText(""); //$NON-NLS-1$
		}
		
	}
	
	
	protected static class LibraryNode{
		private String fName;
		private String fVersion;
		private boolean fSelected;
		private HashSet<IWidgetLibProvider> fProviders = new HashSet<IWidgetLibProvider>();
		public LibraryNode(String name){
			fName = name;
			fVersion = "";
			fSelected = false;
		}		

		public boolean isSelected() {
			return fSelected;
		}

		protected void setSelected(boolean selected) {
			this.fSelected = selected;
		}		
		
		public void addProvider(IWidgetLibProvider wProvider){			
			if(wProvider.isSelected() || fProviders.isEmpty()){
				fVersion = wProvider.getFullVersion();
				fSelected = wProvider.isSelected();
			}
			fProviders.add(wProvider);
		}	
		
		public String getName(){
			return fName;
		}
		
		public int getVersionSel(){
			String[] versions = getVersions();
			for(int i=0;i<versions.length;i++){
				if(versions[i].equals(fVersion))
					return i;
			}
			return 0;
		}
		
		public void setVersionSel(int index){
			fVersion = getVersions()[index];
		}
		
		public String getVersion(){
			return fVersion;
		}
		
		public void setVersion(String version){
			fVersion = version;
		}
		
		public String getId(){
			return getWProvider().getId();
		}
		
		public String getProvider(){
			return getWProvider().getProvider();
		}
		
		public String getDetail() {
			return getWProvider().getDetail();
		}
		
		public ImageDescriptor getLogo() {
			return getWProvider().getLogo();
		}
		
		public boolean isConflict(String aLib){
			IWidgetLibraryConflict conflictClass = getWProvider().getConflictClass();
			if(conflictClass != null)
				return conflictClass.isConflict(aLib);
			return false;
		}
		
		public String[] getVersions(){
			HashSet<String> versions = new HashSet<String>();
			Iterator iter = fProviders.iterator();
		    while (iter.hasNext()) {
		    	IWidgetLibProvider wProvider = (IWidgetLibProvider) iter.next();
		    	versions.add(wProvider.getFullVersion());
		    }
		    return versions.toArray(new String[versions.size()]);
		}		
		
		private IWidgetLibProvider getWProvider(){
			Iterator iter = fProviders.iterator();
		    while (iter.hasNext()) {
		    	IWidgetLibProvider wProvider = (IWidgetLibProvider) iter.next();
		    	if(wProvider.getFullVersion().equals(getVersion()))
		    		return wProvider;
		    }
		    return null;
		}
				
	}
	
	protected static class LibraryContentProvider implements IStructuredContentProvider{
		
		/** The library store. */
		private ArrayList<LibraryNode> nodes;

		public Object[] getElements(Object input) {
			return nodes.toArray();
		}

		public void dispose() {	}
	
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			nodes = (ArrayList<LibraryNode>) newInput;
		}
	}

	private class VersionColumnEditingSupport extends EditingSupport{
		private TableViewer viewer;
		private ComboBoxCellEditor comboCellEditor;
		
		public VersionColumnEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return this.comboCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			LibraryNode insertDataNode = (LibraryNode)element;
			String[] versions = insertDataNode.getVersions();
			comboCellEditor = new ComboBoxCellEditor(viewer.getTable(), versions, SWT.READ_ONLY);
			((CCombo)comboCellEditor.getControl()).setVisibleItemCount(10);
			if( versions.length > 1){
				return true;
			}else{
				return false;
			}
		}

		@Override
		protected Object getValue(Object element) {
			return ((LibraryNode)element).getVersionSel();
		}

		@Override
		protected void setValue(Object element, Object value) {
			((LibraryNode)element).setVersionSel((Integer) value);
			getViewer().update(element, null);
			setSelectedWidgetLibrary((LibraryNode) element);
			setDetails();
		}
		
	}

	@Override
	public void dispose() {
		try {
			Collection<Image> images = libraryImages.values();
			for (Iterator iterator = images.iterator(); iterator.hasNext();) {
				Image e = (Image) iterator.next();
				if (!e.isDisposed()) {
					e.dispose();
				}
			}
			libraryImages.clear();
		} catch (Exception ex) {
			
		}
			
		super.dispose();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		// Select the first library in the table so that its details show up
		if (visible && fTableViewer.getTable().getItemCount() > 0 && fTableViewer.getTable().getSelectionCount() == 0) {
			fTableViewer.setSelection(new StructuredSelection(fTableViewer.getElementAt(0)));
		}
	}
	
	
	
}
