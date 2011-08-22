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
package org.eclipse.edt.ide.rui.wizard.pages;

import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;

import org.eclipse.edt.ide.rui.internal.project.CommonUtilities;
import org.eclipse.edt.ide.rui.wizards.WebClientProjectTemplateWizard;
import org.eclipse.edt.ide.ui.internal.project.wizard.pages.ProjectWizardPage;
import org.eclipse.edt.ide.ui.internal.project.wizards.NewEGLProjectWizard;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.widgetLibProvider.IWidgetLibProvider;
import org.eclipse.edt.ide.widgetLibProvider.WidgetLibProviderManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


public class ProjectWizardRUILibraryPage extends ProjectWizardPage {	
	
	private Group widgetLibraryGroup;
	private Composite contentSection;
	/** The table presenting the templates. */
	private CheckboxTableViewer fTableViewer;
	private ArrayList<LibraryNode> fLibraryNodes = new ArrayList<LibraryNode>();
	private IWidgetLibProvider[] libProviders;


	public ProjectWizardRUILibraryPage(String pageName) {
		super(pageName);
		setTitle(NewWizardMessages.RUILibraryPageTitle);
		setDescription(NewWizardMessages.RUILibraryPageDescription);
		populateLibraryData();
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
							fLibraryNodes.add(new LibraryNode(id, libName, libProviders[i].getFullVersion(), libProviders[i].getProvider()));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}							
				}
			}
		}
		
	}

	
//	private void validatePage() {
//		boolean isChecked= widgetLibraryList.isChecked(IEGLWidgetProjectsConstants.RUI_WIDGETS);
//		if (!isChecked) {
//			widgetLibraryList.setCheckedWithoutUpdate(IEGLWidgetProjectsConstants.RUI_WIDGETS, true);
//		}
//	}
	
	public void createContents(Composite ancestor) {
		GridData adata = new GridData(GridData.FILL_BOTH);
		ancestor.setLayoutData(adata);
		ancestor.setLayout(new GridLayout());	

        Composite tableComposite= new Composite(ancestor, SWT.NONE);
        GridData data= new GridData(GridData.FILL_BOTH);
        data.widthHint= 360;
        data.heightHint= convertHeightInCharsToPixels(10);
        tableComposite.setLayoutData(data);

        ColumnLayout columnLayout= new ColumnLayout();
        tableComposite.setLayout(columnLayout);
		Table table= new Table(tableComposite, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

//		GC gc= new GC(getShell());
//		gc.setFont(JFaceResources.getDialogFont());
//		
//		gc.dispose();

		fTableViewer= new CheckboxTableViewer(table);
		
		TableViewerColumn column1= new TableViewerColumn(fTableViewer, new TableColumn(table, SWT.NONE));
		column1.getColumn().setText(NewWizardMessages.RUILibraryPage_libname_label);
//		int minWidth= computeMinimumColumnWidth(gc, NewWizardMessages.RUILibraryPage_libname_label);		
		columnLayout.addColumnData(new ColumnWeightData(2, 50, true));

		TableViewerColumn column2= new TableViewerColumn(fTableViewer, new TableColumn(table, SWT.NONE));
		column2.getColumn().setText(NewWizardMessages.RUILibraryPage_version_label);
//		minWidth= computeMinimumColumnWidth(gc, NewWizardMessages.RUILibraryPage_version_label);
		column2.setEditingSupport(new VersionColumnEditingSupport(fTableViewer));
		columnLayout.addColumnData(new ColumnWeightData(1, 50, true));

		TableColumn column3= new TableColumn(table, SWT.NONE);
		column3.setText(NewWizardMessages.RUILibraryPage_provider_label);
//		minWidth= computeMinimumColumnWidth(gc, NewWizardMessages.RUILibraryPage_provider_label);
		columnLayout.addColumnData(new ColumnWeightData(3, 50, true));

//		fTableViewer.setUseHashlookup(true);
//		fTableViewer.setColumnProperties(columnNames);
		
//		CellEditor[] editors = new CellEditor[columnNames.length];	

		
		fTableViewer.setLabelProvider(new LibraryLabelProvider());
		fTableViewer.setContentProvider(new LibraryContentProvider());
		fTableViewer.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object object1, Object object2) {
				if ((object1 instanceof LibraryNode) && (object2 instanceof LibraryNode)) {
					LibraryNode left= (LibraryNode) object1;
					LibraryNode right= (LibraryNode) object2;
					int result= Collator.getInstance().compare(left.getName(), right.getName());
					if (result != 0)
						return result;
					return Collator.getInstance().compare(left.getVersion(), right.getVersion());
				}
				return super.compare(viewer, object1, object2);
			}

			public boolean isSorterProperty(Object element, String property) {
				return true;
			}
		});

//		fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent e) {
//				edit();
//			}
//		});
		// TODO <jiyong>change the description here
//		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
//			public void selectionChanged(SelectionChangedEvent e) {
//				selectionChanged1();
//			}
//		});

		fTableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object[] checkElements =  fTableViewer.getCheckedElements();
				ArrayList<String> selectedWidgetLibraries = new ArrayList<String>();
				for(Object element : checkElements){
					selectedWidgetLibraries.add(((LibraryNode) element).getId());
				}
				((NewEGLProjectWizard)((WebClientProjectTemplateWizard)getWizard()).getParentWizard()).getModel().setSelectedWidgetLibraries(selectedWidgetLibraries);
			}

		});
		// TODO <jiyong>Add the detail view here
//		fPatternViewer= doCreateViewer(parent);
//
//		if (isShowFormatterSetting()) {
//			fFormatButton= new Button(parent, SWT.CHECK);
//			fFormatButton.setText(TemplatesMessages.TemplatePreferencePage_use_code_formatter);
//	        GridData gd1= new GridData();
//	        gd1.horizontalSpan= 2;
//	        fFormatButton.setLayoutData(gd1);
//	        fFormatButton.setSelection(getPreferenceStore().getBoolean(getFormatterPreferenceKey()));
//		}

		fTableViewer.setInput(fLibraryNodes);
		fTableViewer.setAllChecked(false);
		//TODO <jiyong> set selected library
//		fTableViewer.setCheckedElements();

//		Dialog.applyDialogFont(parent);
//		parent.layout();
		
		//TODO: <jiyong> Help
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, HelpContextIDs.New_EGL_Base_Project_Advanced_Page);
		
	}
	
	
	
	private int computeMinimumColumnWidth(GC gc, String string) {
		return gc.stringExtent(string).x + 10; // pad 10 to accommodate table header trimmings
	}

	public void setProjectName(String projectName) {
		((NewEGLProjectWizard)((WebClientProjectTemplateWizard)getWizard()).getParentWizard()).getModel().setProjectName(projectName);		
	}

//	public boolean isWidgetLibrarySelected( String libName ) {
////		List<String> checked = widgetLibraryList.getCheckedElements();
////		if( checked.contains(libName)) {
////			return true;
////		}
//		return false;
//	}	
	
	
	protected static class LibraryNode{
		private String fId;
		private String fName;
		private String fVersion;
		private String fProvider;
	
		public LibraryNode(String id, String name, String version, String provider){
			fId = id;
			fName = name;
			fVersion = version;
			fProvider = provider;
		}
		
		public String getId(){
			return fId;
		}
		
		public String getName(){
			return fName;
		}	
		
		public String getVersion(){
			return fVersion;
		}
		
		public String getProvider(){
			return fProvider;
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
	
	protected static class LibraryLabelProvider extends LabelProvider implements ITableLabelProvider{
		public String getColumnText(Object element, int columnIndex) {
			switch( columnIndex ) {
			case 0: 
				return ((LibraryNode)element).getName();
			case 1:
				return ((LibraryNode)element).getVersion();
			case 2:
				return ((LibraryNode)element).getProvider();
			}
			return "";
		}
		 public Image getColumnImage(Object element, int columnIndex) {
			 return null;
		 }		 
	}
	
	class VersionColumnEditingSupport extends EditingSupport{
		
		private ComboBoxCellEditor comboCellEditor;
		
		public VersionColumnEditingSupport(TableViewer viewer) {
			super(viewer);
			// TODO return the real
			comboCellEditor = new ComboBoxCellEditor(viewer.getTable(), new String[]{"0.7.0", "1.0.0"}, SWT.READ_ONLY);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return this.comboCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			LibraryNode insertDataNode = (LibraryNode)element;
//			String[] versions = insertDataNode.getWidgetTypes();
//			String[] widgetTypeNames = new String[widgetTypes.length];
//			for(int i=0; i<widgetTypes.length; i++){
//				widgetTypeNames[i] = widgetTypes[i].getName();
//			}
//			comboBoxCellEditor = new ComboBoxCellEditor(viewer.getTree(), widgetTypeNames, SWT.READ_ONLY);
//			((CCombo)comboBoxCellEditor.getControl()).setVisibleItemCount(10);
//			if(isParentGenChildrenWidget(insertDataNode) && widgetTypes.length > 1){
//				return true;
//			}else{
//				return false;
//			}
			return false;
		}

		@Override
		protected Object getValue(Object element) {
			LibraryNode insertDataNode = (LibraryNode)element;
			return insertDataNode.getVersion();
		}

		@Override
		protected void setValue(Object element, Object value) {
//			LibraryNode insertDataNode = (LibraryNode)element;
//			insertDataNode.((String)value);
			getViewer().update(element, null);
		}
		
	}
	
}
