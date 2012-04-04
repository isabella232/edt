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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvHelp;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Tooltips;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.PageDataNode;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.util.EGLNameValidator;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.util.NameFinder;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;


public class InsertWidgetWizardPage extends WizardPage{

	private CheckboxTreeViewer configureWidgetsTableViewer;
	private InsertDataModel insertDataModel;
	private String purpose;
	private ErrorMessageManager errorMessageManager;
	private Object[] checkedElements;
	private boolean isFirstDisplay = true;
	private Button isAddFormattingAndValidationButton;
	private Button isAddErrorMessageButton;
	private Button upButton;
	private Button downButton;
	
	private Button displayWidgetsButton;
	private Button createWidgetsButton;
	private Button updateWidgetsButton;

	protected InsertWidgetWizardPage(PageDataNode selectedDataItem, IProject project, IEditorInput editorInput) {
		super(Messages.NL_IWWP_Title);
		this.setTitle(Messages.NL_IWWP_Title);
		this.setDescription(Messages.NL_IWWP_Description);
		this.purpose = InsertDataNode.Purpose.FOR_DISPLAY;
		this.errorMessageManager = new ErrorMessageManager();
		NameFinder.getInstance().initralize(editorInput);
		this.insertDataModel = InsertDataModelBuilder.getInstance().create(selectedDataItem, project, editorInput);
		insertDataModel.setPurpose(purpose);
	}
	
	public CheckboxTreeViewer getConfigureWidgetsTableViewer(){
		return configureWidgetsTableViewer;
	}

	public void createControl(Composite parent) {
		EvHelp.setHelp( parent, EvHelp.INSERT_WIDGET_WIZARD );
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Label createWidgetsLabel = new Label(composite, SWT.NONE);
		createWidgetsLabel.setText(Messages.NL_IWWP_Create_Widgets_Label);
		
		//for display radio
		displayWidgetsButton = new Button(composite, SWT.RADIO);
		displayWidgetsButton.setText(Messages.NL_IWWP_Display_Widgets_Button);
		SelectionListener listener = new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(displayWidgetsButton.getSelection()){
					isAddFormattingAndValidationButton.setSelection(true);
					isAddFormattingAndValidationButton.setEnabled(true);
					isAddErrorMessageButton.setSelection(false);
					isAddErrorMessageButton.setEnabled(false);
					checkedElements = configureWidgetsTableViewer.getCheckedElements();
					errorMessageManager.clean();
					purpose = InsertDataNode.Purpose.FOR_DISPLAY;
					insertDataModel.updatePurpose(purpose);
					configureWidgetsTableViewer.setInput(insertDataModel.getRootDataNodes());
					configureWidgetsTableViewer.expandToLevel(10);
					if(isFirstDisplay){
						configureWidgetsTableViewer.setAllChecked(true);
						isFirstDisplay = false;
					}
					else{
						configureWidgetsTableViewer.setCheckedElements(checkedElements);
					}
					
					checkInsertDataNodeSelection();
					errorMessageManager.refresh();
					insertDataModel.setPurpose(purpose);
				};
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
			
		};
		displayWidgetsButton.addSelectionListener(listener);
		
		
		//for create radio
		createWidgetsButton = new Button(composite, SWT.RADIO);
		createWidgetsButton.setText(Messages.NL_IWWP_Create_Widgets_Button);
		createWidgetsButton.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(createWidgetsButton.getSelection()){
					isAddFormattingAndValidationButton.setSelection(true);
					isAddFormattingAndValidationButton.setEnabled(true);
					isAddErrorMessageButton.setEnabled(true);
					checkedElements = configureWidgetsTableViewer.getCheckedElements();
					errorMessageManager.clean();
					purpose = InsertDataNode.Purpose.FOR_CREATE;
					insertDataModel.updatePurpose(purpose);
					configureWidgetsTableViewer.setInput(insertDataModel.getRootDataNodes());
					configureWidgetsTableViewer.expandToLevel(10);
					configureWidgetsTableViewer.setCheckedElements(checkedElements);
					
					checkInsertDataNodeSelection();
					errorMessageManager.refresh();
					insertDataModel.setPurpose(purpose);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		
		//for update radio
		updateWidgetsButton = new Button(composite, SWT.RADIO);
		updateWidgetsButton.setText(Messages.NL_IWWP_Update_Widgets_Button);
		updateWidgetsButton.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(updateWidgetsButton.getSelection()){
					isAddFormattingAndValidationButton.setSelection(true);
					isAddFormattingAndValidationButton.setEnabled(true);
					isAddErrorMessageButton.setEnabled(true);
					checkedElements = configureWidgetsTableViewer.getCheckedElements();
					errorMessageManager.clean();
					purpose = InsertDataNode.Purpose.FOR_UPDATE;
					insertDataModel.updatePurpose(purpose);
					configureWidgetsTableViewer.setInput(insertDataModel.getRootDataNodes());
					configureWidgetsTableViewer.expandToLevel(10);
					configureWidgetsTableViewer.setCheckedElements(checkedElements);
					
					checkInsertDataNodeSelection();
					errorMessageManager.refresh();
					insertDataModel.setPurpose(purpose);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		
		Label separator1 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		
		Label configureWidgetsLabel = new Label(composite, SWT.NONE);
		configureWidgetsLabel.setText(Messages.NL_IWWP_Configure_Widgets_Label);
		
		//configureWidgetsTableViewer
		createConfigureWidgetsTableViewer(composite);
		
		//select all button
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		buttonComposite.setLayout(new RowLayout());
		Button selectAllButton = new Button(buttonComposite, SWT.NONE);
		selectAllButton.setText(Messages.NL_IWWP_Select_All_Button);
		selectAllButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent event) {}

			public void widgetSelected(SelectionEvent event) {
				configureWidgetsTableViewer.setAllChecked(true);
				
				errorMessageManager.removeGloabelError(ErrorMessageManager.GLOBAL_ERROR_TYPE_NO_ELEMENT_SELECTED);
				errorMessageManager.refresh();
			}

		});

		//select none button
		Button deselectAllButton = new Button(buttonComposite, SWT.NONE);
		deselectAllButton.setText(Messages.NL_IWWP_Deselect_All_Button);
		deselectAllButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent event) {}

			public void widgetSelected(SelectionEvent event) {
				configureWidgetsTableViewer.setAllChecked(false);
				
				errorMessageManager.addGloabelError(ErrorMessageManager.GLOBAL_ERROR_TYPE_NO_ELEMENT_SELECTED, Messages.NL_IWWP_Error_Message_No_Element_Selected);
				errorMessageManager.refresh();
			}

		});
		
		
		Label separator2 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite mvcComposite = new Composite(composite, SWT.NONE); 
		GridData mvcCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
		mvcCompositeGridData.heightHint = 50;
		mvcComposite.setLayoutData(mvcCompositeGridData);
		mvcComposite.setLayout(new GridLayout(2, false));
		
		isAddFormattingAndValidationButton = new Button(mvcComposite, SWT.CHECK);
		isAddFormattingAndValidationButton.setText(Messages.NL_IWWP_Is_Add_Formatting_And_Validation_Button);
		GridData isAddFormattingAndValidationButtonGridData = new GridData();
		isAddFormattingAndValidationButtonGridData.horizontalSpan = 2;
		isAddFormattingAndValidationButton.setLayoutData(isAddFormattingAndValidationButtonGridData);
		isAddFormattingAndValidationButton.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(isAddFormattingAndValidationButton.getSelection()){
					insertDataModel.setAddFormattingAndValidation(true);
					if(!displayWidgetsButton.getSelection()){
						isAddErrorMessageButton.setEnabled(true);
					}
				}else{
					isAddErrorMessageButton.setSelection(false);
					isAddErrorMessageButton.setEnabled(false);
					insertDataModel.setAddFormattingAndValidation(false);
					insertDataModel.setAddErrorMessage(false);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		
		Label separator3 = new Label(mvcComposite, SWT.NONE);
		separator3.setText("   ");
		
		isAddErrorMessageButton = new Button(mvcComposite, SWT.CHECK);
		isAddErrorMessageButton.setText(Messages.NL_IWWP_Is_Add_Error_Message_Button);
		isAddErrorMessageButton.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(isAddErrorMessageButton.getSelection()){
					if(insertDataModel.isAddErrorMessage()){
						insertDataModel.setAddErrorMessage(false);
					}else{
						insertDataModel.setAddErrorMessage(true);
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		
		// This won't fire a selection event, and not doing this causes no radio buttons selected on Linux. Manually invoke the listener.
		displayWidgetsButton.setSelection( true );
		listener.widgetSelected(null);// This is okay right now since the function doesn't use the event. but you might want to either dummy up a
		                              // real event, or move the code from the listener to a function that can be invoked separately.
		
		this.setControl(composite);
	}
	
	private void checkInsertDataNodeSelection(){
		checkIfAllInsertDataNodeSelected();
		checkIfParentInsertDataNodeSelected();
	}
	
	private void checkIfAllInsertDataNodeSelected(){
		if(configureWidgetsTableViewer.getCheckedElements().length == 0){
			errorMessageManager.addGloabelError(ErrorMessageManager.GLOBAL_ERROR_TYPE_NO_ELEMENT_SELECTED, Messages.NL_IWWP_Error_Message_No_Element_Selected);
		}else{
			errorMessageManager.removeGloabelError(ErrorMessageManager.GLOBAL_ERROR_TYPE_NO_ELEMENT_SELECTED);
		}
	}
	
	private void checkIfParentInsertDataNodeSelected(){
		for(Object object : configureWidgetsTableViewer.getCheckedElements()){
			InsertDataNode insertDataNode = (InsertDataNode)object;
			if(insertDataNode.getParent() != null && !isInsertDataNodeChecked(insertDataNode.getParent())){
				errorMessageManager.addGloabelError(ErrorMessageManager.GLOBAL_ERROR_TYPE_PARENT_ELEMENT_IS_NOT_SELECTED, Messages.NL_IWWP_Error_Message_Parent_Element_Is_Not_Selected);
			}else{
				errorMessageManager.removeGloabelError(ErrorMessageManager.GLOBAL_ERROR_TYPE_PARENT_ELEMENT_IS_NOT_SELECTED);
			}
		}
	}
	
	private boolean isInsertDataNodeChecked(InsertDataNode insertDataNode){
		Object[] objects = configureWidgetsTableViewer.getCheckedElements();
		for(Object object : objects){
			if(object.equals(insertDataNode)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isWidgetNameDuplicate(InsertDataNode insertDataNode, String widgetName){
		boolean isWidgetNameDuplicate = false;
		List<InsertDataNode> rootInsertDataNodes = insertDataModel.getRootDataNodes(); 
		for(int i=0; i<rootInsertDataNodes.size(); i++){
			InsertDataNode rootInsertDataNode = rootInsertDataNodes.get(i);
			if(!rootInsertDataNode.equals(insertDataNode)){
				String rootWidgetName = rootInsertDataNode.getWidgetName();
				if(rootWidgetName == null){
					rootWidgetName = rootInsertDataNode.getDefaultWidgetName();
				}
				if(rootWidgetName.equalsIgnoreCase(widgetName)){
					isWidgetNameDuplicate = true;
					break;
				}else{
					List<InsertDataNode> allChildren = getAllChildren(rootInsertDataNode);
					for(int j=0; j<allChildren.size(); j++){
						InsertDataNode childInsertDataNode = allChildren.get(i);
						if(!childInsertDataNode.equals(insertDataNode)){
							String childWidgetName = childInsertDataNode.getWidgetName();
							if(childWidgetName == null){
								childWidgetName = childInsertDataNode.getDefaultWidgetName();
							}
							if(childWidgetName.equalsIgnoreCase(widgetName)){
								isWidgetNameDuplicate = true;
								break;
							}
						}
					}
				}
			}
		}
		
		return isWidgetNameDuplicate;
	}
	
	private List<InsertDataNode> getAllChildren(InsertDataNode insertDataNode){
		List<InsertDataNode> allChildren = new ArrayList<InsertDataNode>();
		getAllChildren(insertDataNode, allChildren);
		return allChildren;
	}
	
	private void getAllChildren(InsertDataNode insertDataNode, List<InsertDataNode> allChildren){
		for(InsertDataNode child : insertDataNode.getChildren()){
			allChildren.add(child);
			getAllChildren(child, allChildren);
		}
	}
	
	private void createConfigureWidgetsTableViewer(Composite composite){
		Composite configureComposite = new Composite(composite, SWT.NONE);
		GridData configureCompositeGridData = new GridData(GridData.FILL_BOTH);
		configureComposite.setLayoutData(configureCompositeGridData);
		configureComposite.setLayout(new GridLayout(2, false));
		
		Tree configureWidgetsTree = new Tree(configureComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK | SWT.MULTI);
		configureWidgetsTableViewer = new CheckboxTreeViewer(configureWidgetsTree);
		configureWidgetsTableViewer.setContentProvider(new TreeTableViewerContentProvider());
		configureWidgetsTableViewer.getTree().setHeaderVisible(true);
		configureWidgetsTableViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		TableLayout tableLayout = new TableLayout();
		configureWidgetsTableViewer.getTree().setLayout(tableLayout);
		
		configureWidgetsTableViewer.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				InsertDataNode insertDataNode =(InsertDataNode)event.getElement();	
				configureWidgetsTableViewer.setGrayed(insertDataNode, false);
				
				if(isInsertDataNodeChecked(insertDataNode)){
					//also check parent of the node
					InsertDataNode parent = insertDataNode.getParent();
					if(parent != null){
						if(isOneChildUnchecked(parent)){
							configureWidgetsTableViewer.setGrayChecked(parent, true);
						}else{
							configureWidgetsTableViewer.setGrayChecked(parent, false);
							configureWidgetsTableViewer.setChecked(parent, true);
						}
					}
					
					//also check all children of the node
					for(InsertDataNode child : getAllChildren(insertDataNode)){
						configureWidgetsTableViewer.setChecked(child, true);
					}
				}
				else{					
					//also uncheck parent of the node
					InsertDataNode parent = insertDataNode.getParent();
					if(parent != null){
						if(isOneChildUnchecked(parent)){
							configureWidgetsTableViewer.setGrayChecked(parent, true);
						}
						if(isAllChildrenUnchecked(parent)){
							configureWidgetsTableViewer.setGrayChecked(parent, false);
							configureWidgetsTableViewer.setChecked(parent, false);
						}
					}
					//also uncheck all children of the node
					for(InsertDataNode child : getAllChildren(insertDataNode)){
						configureWidgetsTableViewer.setGrayChecked(child, false);
					}
				}
				
				checkInsertDataNodeSelection();			
				errorMessageManager.refresh();
			}
			
			private boolean isOneChildUnchecked(InsertDataNode parent){
				List<InsertDataNode> children = parent.getChildren();
				for(InsertDataNode child : children){
					if(!configureWidgetsTableViewer.getChecked(child)){
						return true;
					};
				}
				return false;
			}
			
			private boolean isAllChildrenUnchecked(InsertDataNode parent){
				List<InsertDataNode> children = parent.getChildren();
				for(InsertDataNode child : children){
					if(configureWidgetsTableViewer.getChecked(child)){
						return false;
					};
				}
				return true;
			}
			
		});
		
		configureWidgetsTableViewer.getTree().addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent event) {
				if(event.keyCode == SWT.KEYPAD_ADD && upButton.isEnabled()){
					reorderSelectedInsertDataNodes(Order.UP);
				}
				
				if(event.keyCode == SWT.KEYPAD_SUBTRACT && downButton.isEnabled()){
					reorderSelectedInsertDataNodes(Order.DOWN);
				}
			}

			public void keyReleased(KeyEvent event) {}
			
		});
		
		configureWidgetsTableViewer.getTree().addListener(SWT.Collapse, new Listener(){

			public void handleEvent(Event event) {
				Widget widget = event.item;
				if(widget instanceof TreeItem){
					TreeItem treeItem = (TreeItem)widget;
					InsertDataNode insertDataNode = (InsertDataNode)treeItem.getData();
					if(!isSelected(insertDataNode, (IStructuredSelection)configureWidgetsTableViewer.getSelection())){
						configureWidgetsTableViewer.setSelection(null);
						setUpAndDownButtonsStatus(null);
					}
				}
			}
			
		});
		
		configureWidgetsTableViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if(event.getSelection().isEmpty()) {
					return;
			    }
				
				setUpAndDownButtonsStatus(event.getSelection());
			}
		});
		
		//field name column
		TreeViewerColumn fieldNameColumn = new TreeViewerColumn(configureWidgetsTableViewer, SWT.NONE);
		fieldNameColumn.getColumn().setText(Messages.NL_IWWP_CWT_Field_Name_Column);
		tableLayout.addColumnData(new ColumnWeightData(150));
		fieldNameColumn.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				InsertDataNode insertDataNode = (InsertDataNode)element;
				return insertDataNode.getFieldName();
			}
		});
		
		//field type column
		TreeViewerColumn fieldTypeColumn = new TreeViewerColumn(configureWidgetsTableViewer, SWT.NONE);
		fieldTypeColumn.getColumn().setText(Messages.NL_IWWP_CWT_Field_Type_Column);
		tableLayout.addColumnData(new ColumnWeightData(100));
		fieldTypeColumn.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				InsertDataNode insertDataNode = (InsertDataNode)element;
				return insertDataNode.getFieldType();
			}
		});
		
		//label text column
		TreeViewerColumn labelTextColumn = new TreeViewerColumn(configureWidgetsTableViewer, SWT.NONE);
		labelTextColumn.getColumn().setText(Messages.NL_IWWP_CWT_Label_Text_Column);
		tableLayout.addColumnData(new ColumnWeightData(100));
		labelTextColumn.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				InsertDataNode insertDataNode = (InsertDataNode)element;
				return insertDataNode.getLabelText();
			}
		});
		labelTextColumn.setEditingSupport(new LabelTextColumnEditingSupport(configureWidgetsTableViewer));
		
		//widget type column
		TreeViewerColumn widgetTypeColumn = new TreeViewerColumn(configureWidgetsTableViewer, SWT.NONE);
		widgetTypeColumn.getColumn().setText(Messages.NL_IWWP_CWT_Widget_Type_Column);
		tableLayout.addColumnData(new ColumnWeightData(180));
		widgetTypeColumn.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				InsertDataNode insertDataNode = (InsertDataNode)element;
				String widgetType = insertDataNode.getWidgetType().getName();
				
				if(!isParentGenChildrenWidget(insertDataNode)){
					return "";
				}
				
				if(widgetType.equals(InsertDataNode.RUIWidgetType.NONE)){
					if(InsertWidgetWizardUtil.isAPrimitiveArrayInRecord(insertDataNode)){
						configureWidgetsTableViewer.setGrayed(insertDataNode, true);
					}else{
						String errorMessage = Messages.NL_IWWP_Error_Message_Can_Not_Find_Widget + " " + insertDataNode.getBindingName();
						errorMessageManager.addError(insertDataNode, ErrorMessageManager.ERROR_TYPE_CANNOT_FIND_WIDGET, errorMessage);
					}
				}else{
					errorMessageManager.reomveError(insertDataNode, ErrorMessageManager.ERROR_TYPE_CANNOT_FIND_WIDGET);
				}
				return widgetType;
			}
		});
		widgetTypeColumn.setEditingSupport(new WidgetTypeColumnEditingSupport(configureWidgetsTableViewer));
		
		//widget name column
		TreeViewerColumn widgetNameColumn = new TreeViewerColumn(configureWidgetsTableViewer, SWT.NONE);
		widgetNameColumn.getColumn().setText(Messages.NL_IWWP_CWT_Widget_Name_Column);
		tableLayout.addColumnData(new ColumnWeightData(150));
		widgetNameColumn.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				InsertDataNode insertDataNode = (InsertDataNode)element;
				
				if(!isParentGenChildrenWidget(insertDataNode)){
					return "";
				}
				
				String widgetName = insertDataNode.getWidgetName();
				if(widgetName == null || widgetName.equals("")){
					widgetName = insertDataNode.getDefaultWidgetName();
				}
				if(!isWidgetNameValid(widgetName)){
					String errorMessage = widgetName + " " + Messages.NL_IWWP_Error_Message__Widget_Name_Is_Not_Valid;
					errorMessageManager.addError(insertDataNode, ErrorMessageManager.ERROR_TYPE_INVALID_WIDGET_NAME, errorMessage);
				}
				else if(NameFinder.getInstance().isFieldNameExist(widgetName) || isWidgetNameDuplicate(insertDataNode, widgetName)){
					String errorMessage = widgetName + " " + Messages.NL_IWWP_Error_Message_Duplicate_Widget_Name_Found;
					errorMessageManager.addError(insertDataNode, ErrorMessageManager.ERROR_TYPE_DUPLICATE_WIDGET_NAME, errorMessage);
				}
				else{
					errorMessageManager.reomveError(insertDataNode, ErrorMessageManager.ERROR_TYPE_INVALID_WIDGET_NAME);
					errorMessageManager.reomveError(insertDataNode, ErrorMessageManager.ERROR_TYPE_DUPLICATE_WIDGET_NAME);
				};
				
				errorMessageManager.refresh();
				
				return widgetName;
			}
			
			private boolean isWidgetNameValid(String widgetName){
				List messages = EGLNameValidator.validateEGLName(widgetName, EGLNameValidator.PART, null);
				if (!messages.isEmpty()){
					return false;
				}
				return true;
			}
		});
		widgetNameColumn.setEditingSupport(new WidgetNameColumnEditingSupport(configureWidgetsTableViewer));
		
//		//Node type column (for debug)
//		TreeViewerColumn nodeTypeColumn = new TreeViewerColumn(configureWidgetsTableViewer, SWT.NONE);
//		nodeTypeColumn.getColumn().setText("Node Type");
//		tableLayout.addColumnData(new ColumnWeightData(70));
//		nodeTypeColumn.setLabelProvider(new ColumnLabelProvider(){
//			public String getText(Object element) {
//				InsertDataNode insertDataNode = (InsertDataNode)element;
//				return insertDataNode.getNodeType();
//			}
//		});
//		
//		//Is Array column (for debug)
//		TreeViewerColumn isArrayColumn = new TreeViewerColumn(configureWidgetsTableViewer, SWT.NONE);
//		isArrayColumn.getColumn().setText("Is Array");
//		tableLayout.addColumnData(new ColumnWeightData(70));
//		isArrayColumn.setLabelProvider(new ColumnLabelProvider(){
//			public String getText(Object element) {
//				InsertDataNode insertDataNode = (InsertDataNode)element;
//				return Boolean.toString(insertDataNode.isArray());
//			}
//		});
//		
//		//Is Container column (for debug)
//		TreeViewerColumn isContainerColumn = new TreeViewerColumn(configureWidgetsTableViewer, SWT.NONE);
//		isContainerColumn.getColumn().setText("Is Container");
//		tableLayout.addColumnData(new ColumnWeightData(70));
//		isContainerColumn.setLabelProvider(new ColumnLabelProvider(){
//			public String getText(Object element) {
//				InsertDataNode insertDataNode = (InsertDataNode)element;
//				return Boolean.toString(insertDataNode.isContainer());
//			}
//		});
//		
//		//Node type details column (for debug)
//		TreeViewerColumn nodeTypeDetailsColumn = new TreeViewerColumn(configureWidgetsTableViewer, SWT.NONE);
//		nodeTypeDetailsColumn.getColumn().setText("Node Type Details");
//		tableLayout.addColumnData(new ColumnWeightData(70));
//		nodeTypeDetailsColumn.setLabelProvider(new ColumnLabelProvider(){
//			public String getText(Object element) {
//				InsertDataNode insertDataNode = (InsertDataNode)element;
//				StringBuffer details = new StringBuffer();
//				for(String detail : insertDataNode.getNodeTypeDetails()){
//					details.append(detail).append(" + ");
//				}
//				return details.toString();
//			}
//		});
		
		
		Composite orderComposite = new Composite(configureComposite, SWT.NONE);
		orderComposite.setLayout(new GridLayout(1, false));
		
		upButton = new Button(orderComposite, SWT.NONE);
		upButton.setImage(Activator.getImage(EvConstants.ICON_ORDER_UP_BUTTON));
		upButton.setToolTipText(Tooltips.NL_IWWP_Order_Up);
		upButton.setEnabled(false);
		upButton.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent event) {
				reorderSelectedInsertDataNodes(Order.UP);
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {}
			
		});
		
		downButton = new Button(orderComposite, SWT.NONE);
		downButton.setImage(Activator.getImage(EvConstants.ICON_ORDER_DOWN_BUTTON));
		downButton.setToolTipText(Tooltips.NL_IWWP_Order_Down);
		downButton.setEnabled(false);
		downButton.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent event) {
				reorderSelectedInsertDataNodes(Order.DOWN);
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {}
			
		});
	}
	
	private void reorderSelectedInsertDataNodes(Order order){
		ISelection treeSelection = configureWidgetsTableViewer.getSelection();
		if(treeSelection instanceof IStructuredSelection) {
			checkedElements = configureWidgetsTableViewer.getCheckedElements();
			IStructuredSelection selection = (IStructuredSelection)treeSelection;
			InsertDataNode parent = ((InsertDataNode)selection.getFirstElement()).getParent();
			List<InsertDataNode> children = parent.getChildren();
			List<InsertDataNode> newChildren = new ArrayList<InsertDataNode>();
			for(int i=0; i<children.size(); i++){
				newChildren.add(null);
			}
			
			for(int i=0; i<children.size(); i++){
				InsertDataNode insertDataNode = children.get(i);
				if(isSelected(insertDataNode, selection)){
					int change = i;
					if(Order.UP.equals(order)){
						change = i-1;
					}
					if(Order.DOWN.equals(order)){
						change = i+1;
					}
					
					newChildren.set(change, insertDataNode);
					children.set(i, null);
				}
			}
			
			for(int i=0; i<children.size(); i++){
				InsertDataNode insertDataNode = children.get(i);
				if(insertDataNode != null){
					for(int j=0; j<newChildren.size(); j++){
						if(newChildren.get(j) == null){
							newChildren.set(j, insertDataNode);
							break;
						}
					}
				}
			}
			
			parent.setChildren(newChildren);
			
			configureWidgetsTableViewer.setInput(insertDataModel.getRootDataNodes());
			configureWidgetsTableViewer.expandToLevel(10);
			configureWidgetsTableViewer.setSelection(treeSelection);
			configureWidgetsTableViewer.setCheckedElements(checkedElements);
		}
	}
	
	private boolean isSelected(InsertDataNode insertDataNode, IStructuredSelection selection){
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			if(insertDataNode.equals(iterator.next())){
				return true;
			}
		}
		return false;
	}
		
	private void setUpAndDownButtonsStatus(ISelection treeSelection){
		upButton.setEnabled(false);
		downButton.setEnabled(false);
		
		if(treeSelection == null){
			return;
		}
		
		boolean disableUp = false;
		boolean disableDown = false;
		
		InsertDataNode parent = null;
		if(treeSelection instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection)treeSelection;
			for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
				Object object = iterator.next();
				if(object instanceof InsertDataNode){
					InsertDataNode insertDataNode = (InsertDataNode)object;
					//if any one of selections is the root node, disable up/down buttons
					if(insertDataNode.getParent() == null){
						return;
					}
					
					//if selections are not in the same level, disable up/down buttons
					if(parent == null){
						parent = insertDataNode.getParent();
					}else if(!parent.equals(insertDataNode.getParent())){
						return;
					} 
					
					//if any one of selections is the first node of parent children, disable up button
					if(parent.getChildren().get(0).equals(insertDataNode)){
						disableUp = true;
					}
					
					//if any one of selections is the last node of parent children, disable down button
					if(parent.getChildren().get(parent.getChildren().size() - 1).equals(insertDataNode)){
						disableDown = true;
					}
				}
			}
		}
		
		//do action to enable up/down buttons
		if(!disableUp){
			upButton.setEnabled(true);
		}
		if(!disableDown){
			downButton.setEnabled(true);
		}
	}

	private boolean isParentGenChildrenWidget(InsertDataNode insertDataNode){
		InsertDataNode parent = insertDataNode.getParent();
		if(parent == null || parent.getDataTemplate() == null || parent.getDataTemplate().getDataMapping().isGenChildWidget()){
			return true;
		}else{
			return false;
		}
	}
	
	public class TreeTableViewerContentProvider implements ITreeContentProvider {

		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof List){
				return ((List)inputElement).toArray();
			}
			return new Object[0];
		}

		public void dispose() {}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

		public Object[] getChildren(Object parentElement) {
			InsertDataNode insertDataNode = (InsertDataNode)parentElement;
			return insertDataNode.getChildren().toArray();
		}

		public Object getParent(Object element) {
			InsertDataNode insertDataNode = (InsertDataNode)element;
			return insertDataNode.getParent();
		}

		public boolean hasChildren(Object element) {
			InsertDataNode insertDataNode = (InsertDataNode)element;
			return insertDataNode.hasChildren();
		}
		
	}
	
	class LabelTextColumnEditingSupport extends EditingSupport{
		
		private TextCellEditor textCellEditor;
		
		public LabelTextColumnEditingSupport(TreeViewer viewer) {
			super(viewer);
			textCellEditor = new TextCellEditor(viewer.getTree());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return this.textCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			InsertDataNode insertDataNode = (InsertDataNode)element;
			if(insertDataNode.getLabelText() == null){
				return false;
			}else{
				return true;
			}
		}

		@Override
		protected Object getValue(Object element) {
			InsertDataNode insertDataNode = (InsertDataNode)element;
			return insertDataNode.getLabelText();
		}

		@Override
		protected void setValue(Object element, Object value) {
			InsertDataNode insertDataNode = (InsertDataNode)element;
			insertDataNode.setLabelText((String)value);
			getViewer().update(element, null);
		}
		
	}
	
	class WidgetTypeColumnEditingSupport extends EditingSupport{
		private TreeViewer viewer;
		private ComboBoxCellEditor comboBoxCellEditor;
		
		public WidgetTypeColumnEditingSupport(TreeViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		protected CellEditor getCellEditor(Object element) {
			return comboBoxCellEditor;
		}

		protected boolean canEdit(Object element) {
			InsertDataNode insertDataNode = (InsertDataNode)element;
			WidgetType[] widgetTypes = insertDataNode.getWidgetTypes();
			String[] widgetTypeNames = new String[widgetTypes.length];
			for(int i=0; i<widgetTypes.length; i++){
				widgetTypeNames[i] = widgetTypes[i].getName();
			}
			comboBoxCellEditor = new ComboBoxCellEditor(viewer.getTree(), widgetTypeNames, SWT.READ_ONLY);
			((CCombo)comboBoxCellEditor.getControl()).setVisibleItemCount(10);
			if(isParentGenChildrenWidget(insertDataNode) && widgetTypes.length > 1){
				return true;
			}else{
				return false;
			}
		}

		protected Object getValue(Object element) {
			return 0;
		}

		protected void setValue(Object element, Object value) {
			InsertDataNode insertDataNode = (InsertDataNode)element;
			int selected = Integer.parseInt(value.toString());
			if(selected >= 0){
				WidgetType[] widgetTypes = insertDataNode.getWidgetTypes();
				insertDataNode.setWidgetType(widgetTypes[selected]);
				getViewer().update(insertDataNode, null);
				for(InsertDataNode child : insertDataNode.getChildren()){
					getViewer().update(child, null);
				}
			}
		}
	}
	
	class WidgetNameColumnEditingSupport extends EditingSupport{
		
		private TextCellEditor textCellEditor;
		
		public WidgetNameColumnEditingSupport(TreeViewer viewer) {
			super(viewer);
			textCellEditor = new TextCellEditor(viewer.getTree());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return this.textCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			InsertDataNode insertDataNode = (InsertDataNode)element;
			if(!isParentGenChildrenWidget(insertDataNode)){
				return false;
			}
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			InsertDataNode insertDataNode = (InsertDataNode)element;
			String widgetName = insertDataNode.getWidgetName();
			if(widgetName == null){
				widgetName = insertDataNode.getDefaultWidgetName();
			}
			return widgetName;
		}

		@Override
		protected void setValue(Object element, Object value) {
			InsertDataNode insertDataNode = (InsertDataNode)element;
			String widgetName = (String)value;
			insertDataNode.setWidgetName(widgetName);
			getViewer().update(element, null);
		}
		
	}
	
	enum Order{
		UP,
		DOWN
	}
	
	class ErrorMessageManager{
		public static final int ERROR_TYPE_CANNOT_FIND_WIDGET = 0;
		public static final int ERROR_TYPE_DUPLICATE_WIDGET_NAME = 1;
		public static final int ERROR_TYPE_INVALID_WIDGET_NAME = 2;
		public static final String GLOBAL_ERROR_TYPE_NO_ELEMENT_SELECTED = "NO_ELEMENT_SELECTED";
		public static final String GLOBAL_ERROR_TYPE_PARENT_ELEMENT_IS_NOT_SELECTED = "PARENT_ELEMENT_IS_NOT_SELECTED";
		
		private Map<String, String> errorMessages;
		
		public ErrorMessageManager(){
			this.errorMessages = new HashMap<String, String>();
		}
		
		public void addError(InsertDataNode insertDataNode, int errorType, String message){
			String key = getKey(insertDataNode) + errorType;
			errorMessages.put(key, message);
		}
		
		public void addGloabelError(String errorType, String message){
			errorMessages.put(errorType, message);
		}
		
		public void removeGloabelError(String errorType){
			errorMessages.keySet().remove(errorType);
		}
		
		public void reomveError(InsertDataNode insertDataNode, int errorType){
			String key = getKey(insertDataNode) + errorType;
			errorMessages.keySet().remove(key);
		}
		
		private String getKey(InsertDataNode insertDataNode){
			return insertDataNode.getBindingName() + insertDataNode.getNodeType() + insertDataNode.isContainer();
		}
		
		public boolean hasError(){
			if(errorMessages.isEmpty()){
				return false;
			}else{
				return true;
			}
		}
		
		public String getErrors(){
			StringBuffer sbError = new StringBuffer();
			Collection<String> errors = errorMessages.values();
			for(String error : errors){
				sbError.append(error).append("\n");
			}
			return sbError.toString().trim();
		}
		
		public void clean(){
			errorMessages.clear();
		}
		
		public void refresh(){
			if(errorMessageManager.hasError()){
				setErrorMessage(errorMessageManager.getErrors());
				setPageComplete(false);
			}else{
				setErrorMessage(null);
				setPageComplete(true);
			}
		}
	}
}
