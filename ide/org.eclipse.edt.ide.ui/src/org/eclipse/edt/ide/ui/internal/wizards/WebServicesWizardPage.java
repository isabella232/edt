/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.util.PatternConstructor;
import org.eclipse.edt.ide.ui.internal.util.SWTUtil;
import org.eclipse.edt.ide.ui.wizards.WebServicesConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class WebServicesWizardPage extends EGLElementWizardPage {

	public static final String WIZPAGENAME_WebServicesWizardPage = "WIZPAGENAME_WebServicesWizardPage"; //$NON-NLS-1$
	private int nColumns =3;
	private Text fFilterText;
	private TableViewer fAvailableListViewer;
	private Label fCountLabel;
	private TableViewer fWSListViewer;
	private StatusInfo fWSGenTypeStatus;

	private class WebServicesAvailableContentProvider implements IStructuredContentProvider{

		public Object[] getElements(Object inputElement) {
			return getConfiguration().getServicesNeed2BeWS().toArray();
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}		
	}
	
	private class WebServicesContentProvider implements IStructuredContentProvider{

		public Object[] getElements(Object inputElement) {
			return getConfiguration().getSelectedServices2BeWS().toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		
	}
	
	private class WebServicesLabelProvider extends LabelProvider{
		public Image getImage(Object element) {	
			if(element instanceof PartDeclarationInfo){
				PartDeclarationInfo partinfo = (PartDeclarationInfo)element;
				if((partinfo.getPartType() & IEGLSearchConstants.EXTERNALTYPE) != 0)
					return PluginImages.get(PluginImages.IMG_OBJS_EXTERNALTYPE);				
			}
			
			return PluginImages.get(PluginImages.IMG_OBJS_SERVICE);
		}
		
		public String getText(Object element) {
			String text = ""; //$NON-NLS-1$
			if(element instanceof PartDeclarationInfo){
				PartDeclarationInfo partinfo = (PartDeclarationInfo)element;
				text = partinfo.getFullyQualifiedName();
				text += " ("; //$NON-NLS-1$
				text += partinfo.getProject();
				text += ")"; //$NON-NLS-1$
			}
			return text;
		}
	}
	
	public WebServicesWizardPage(String pageName) {
		super(pageName);
		setTitle(NewWizardMessages.WebServicesWizPageTitle);
		setDescription(NewWizardMessages.WebServicesWizPageDescription);
		
		fWSGenTypeStatus = new StatusInfo();
	}

	protected void validatePage() {
		fWSGenTypeStatus.setOK();
		
		if(!getConfiguration().isGenAsSOAP() && !getConfiguration().isGenAsRest()){
			fWSGenTypeStatus.setError(NewWizardMessages.errMustSelectOneTypeWS2Gen); 
		} else if(fWSListViewer.getTable().getItemCount() < 1) {
			fWSGenTypeStatus.setError(NewWizardMessages.errMustSelectOneTypeWS2Gen);
		}
				
		updateStatus(new IStatus[]{fWSGenTypeStatus});
	}
	
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite = createCompositeControl(parent);
		createFilterControl(composite);
		createWebServicesTypeControl(composite);
		createAvailableListControl(composite).setLayoutData(new GridData(GridData.FILL_BOTH));
		createButtonArea(composite);
		createGenerateList(composite);
		updateCount();
		
		addViewerListeners();
		
		setControl(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGLDDWIZ_ADDWS);	
		validatePage();
		Dialog.applyDialogFont(parent);
	}

	private void addViewerListeners() {
		fAvailableListViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				handleAdd();
			}
		});
				
		fWSListViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				handleRemove();
			}
		});
	}
	
	private WebServicesConfiguration getConfiguration(){
		return (WebServicesConfiguration)((WebServicesWizard)getWizard()).getConfiguration(getName());
	}

	private Composite createGenerateList(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label = new Label(container, SWT.NONE);
		label.setText(NewWizardMessages.EGLServicePartsListLabel); 

		Table table = new Table(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 225;
		table.setLayoutData(gd);

		fWSListViewer = new TableViewer(table);
		fWSListViewer.setContentProvider(new WebServicesContentProvider());
		fWSListViewer.setLabelProvider(new WebServicesLabelProvider());
		fWSListViewer.setInput(""); //$NON-NLS-1$
		fWSListViewer.setSorter(new ViewerSorter());
		
		return container;
	}

	private Composite createButtonArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		Composite container = new Composite(comp, SWT.NONE);
		layout = new GridLayout();
		layout.marginWidth = 0;
		container.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_VERTICAL);
		gd.verticalIndent = 15;
		container.setLayoutData(gd);
		
		Button button = new Button(container, SWT.PUSH);
		button.setText(NewWizardMessages.AddWSLabel); 
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleAdd();
			}
		});
		SWTUtil.setButtonDimensionHint(button);
		
		button = new Button(container, SWT.PUSH);
		button.setText(NewWizardMessages.AddAllWSLabel); 
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleAddAll();
			}
		});
		SWTUtil.setButtonDimensionHint(button);
		
		button = new Button(container, SWT.PUSH);
		button.setText(NewWizardMessages.RemoveWSLabel); 
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleRemove();
			}
		});
		SWTUtil.setButtonDimensionHint(button);
		
		button = new Button(container, SWT.PUSH);
		button.setText(NewWizardMessages.RemoveAllWSLabel); 
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleRemoveAll();
			}
		});
		SWTUtil.setButtonDimensionHint(button);
		
		fCountLabel = new Label(comp, SWT.NONE);
		fCountLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));		
		return container;		
	}

	protected void handleRemoveAll() {
		getConfiguration().moveAllServicesNot2BeWS();
		updateControls();
	}

	protected void handleRemove() {
		IStructuredSelection ssel = (IStructuredSelection)fWSListViewer.getSelection();
		if (ssel.size() > 0) {
			getConfiguration().moveServicesNot2BeWS(ssel.toList());
			updateControls();
		}
	}

	protected void handleAddAll() {
		getConfiguration().moveAllServices2BeWS();
		updateControls();
	}

	protected void handleAdd() {
		IStructuredSelection ssel = (IStructuredSelection)fAvailableListViewer.getSelection();
		if (ssel.size() > 0) {
			getConfiguration().moveServices2BeWS(ssel.toList());
			updateControls();
		}
		
	}

	private void updateControls() {
		fAvailableListViewer.refresh();
		fWSListViewer.refresh();
		updateCount();
		validatePage();
	}

	private Composite createAvailableListControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);
		container.setLayoutData(new GridData());

		Label label = new Label(container, SWT.NONE);
		label.setText(NewWizardMessages.EGLServicePartsFoundLabel); 

		Table table = new Table(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 225;
		table.setLayoutData(gd);

		fAvailableListViewer = new TableViewer(table);
		fAvailableListViewer.setContentProvider(new WebServicesAvailableContentProvider());
		fAvailableListViewer.setLabelProvider(new WebServicesLabelProvider());
		fAvailableListViewer.setInput(""); //$NON-NLS-1$
		fAvailableListViewer.setSorter(new ViewerSorter());
		return container;
	}

	protected Composite createCompositeControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_EXTERNALSERVICE);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		return composite;
	}
	
	private void createFilterControl(Composite parent){
		Group grp = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth = layout.marginHeight = 6;
		grp.setLayout(layout);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan=3;
		grp.setLayoutData(gd);
		grp.setText(NewWizardMessages.LocateEGLServicePartGrpLabel);
		
		Label filterLabel = new Label(grp, SWT.NONE);
		filterLabel.setText(NewWizardMessages.FilterLabel);
		
		fFilterText = new Text(grp, SWT.BORDER);
		fFilterText.setText(""); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fFilterText.setLayoutData(gd);		
		
		fFilterText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				handleFilter();
			}
		});		
	}
	
	private void createWebServicesTypeControl(Composite parent){
		Group grp = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = layout.marginHeight = 6;
		grp.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan=3;
		grp.setLayoutData(gd);
		grp.setText(NewWizardMessages.WSGenAs);
		
		/*final Button btnSOAP = new Button(grp, SWT.CHECK);
		btnSOAP.setText(NewWizardMessages.SOAPService);
		btnSOAP.setSelection(getConfiguration().isGenAsSOAP());
		btnSOAP.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				getConfiguration().setGenAsSOAP(btnSOAP.getSelection());
				validatePage();
			}
		});
		btnSOAP.setEnabled(false);*/
		
		final Button btnRest = new Button(grp, SWT.CHECK);
		btnRest.setText(NewWizardMessages.RestService);
		btnRest.setSelection(getConfiguration().isGenAsRest());
		btnRest.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				getConfiguration().setGenAsRest(btnRest.getSelection());
				validatePage();
			}
		});

		if(!EDTCoreIDEPlugin.SUPPORT_SOAP){
			//btnSOAP.setEnabled(false);
			btnRest.setEnabled(false);
		}

	}

	protected void handleFilter() {
		if(fFilterText == null || fFilterText.getText().trim().length()==0)
			return;
		
		String text = fFilterText.getText().trim();
		if(!text.endsWith("*")) //$NON-NLS-1$
			text += "*"; //$NON-NLS-1$
		Pattern pattern = PatternConstructor.createPattern(text, true);
		TableItem[] tableItems = fAvailableListViewer.getTable().getItems();
		ArrayList results = new ArrayList();
		for (int i = 0; i<tableItems.length; i++){
			Object data = tableItems[i].getData();
			if(data instanceof PartDeclarationInfo){
				PartDeclarationInfo partInfo = (PartDeclarationInfo)data;
				String dispalyText = ((LabelProvider)(fAvailableListViewer.getLabelProvider())).getText(partInfo);
				if(pattern.matcher(dispalyText).matches())
					results.add(tableItems[i]);
			}
		}
		if(results.size()>0){
			TableItem[] selectionList = (TableItem[])results.toArray(new TableItem[results.size()]);
			fAvailableListViewer.getTable().setSelection(selectionList);
		}
		else
			fAvailableListViewer.setSelection(null);
	}

	private void updateCount() {
		fCountLabel.setText(
			NewWizardMessages.bind(NewWizardMessages.EGLServicePartCount, (new String[] {
			new Integer(fWSListViewer.getTable().getItemCount()).toString(),
			new Integer(getConfiguration().getInitialAvailableServicesCount()).toString()})));
		fCountLabel.getParent().layout();
	}
	
	
}
