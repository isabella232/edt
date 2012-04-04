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
package org.eclipse.edt.ide.rui.visualeditor.internal.wizards.newvariable;


import java.util.List;
import java.util.Locale;

import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvEditor;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvHelp;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.newvariable.NewEGLVariableWizardUtil.DataType;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.util.EGLNameValidator;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.util.NameFinder;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class NewEGLVariableWizardPage extends WizardPage {
	private EvEditor evEditor;
	private PartDeclarationInfo selectedPartDeclarationInfo;
	private String fieldName;
	private String fieldType;
	private String fieldTypePackage;
	private boolean isArray;
	private boolean hasDimensions;
	private String firstDimension;
	private String secondDimension;
	private String[] supportedPrimitiveTypes;
	
	private Group typeCreationGroup;
	private Composite typeAreaComposite;
	private Composite primitiveTypeComposite;
	private Composite dataItemComposite;
	private Composite recordComposite;
	
	private Text fieldNameText;
	private Combo dataTypeCombo;
	private Text searchDataItemText;
	private Text searchRecordText;
	private Table searchDataItemTable;
	private Table searchRecordTable;
	private Text previewText;
	private Text firstDimensionsText;
	private Text secondDimensionsText;
	private Button isArrayButton;
	
	class SupportedPrimitiveType{
		public static final String STRING = "string";
		public static final String DATE = "date";
		public static final String TIME = "time";
		public static final String TIMESTAMP = "timestamp";
		public static final String SMALLINT = "smallint";
		public static final String INT = "int";
		public static final String BIGINT = "bigint";
		public static final String BIN = "bin";
		public static final String SMALLFLOAT = "smallfloat";
		public static final String FLOAT = "float";
		public static final String DECIMAL = "decimal";
		public static final String NUM = "num";
		public static final String NUMBER = "number";
		public static final String MONEY = "money";
		public static final String BOOLEAN = "boolean";
	}
	
	protected NewEGLVariableWizardPage(EvEditor evEditor) {
		super(Messages.NL_NEVWP_Title);
		this.setTitle(Messages.NL_NEVWP_Title);
		this.setDescription(Messages.NL_NEVWP_Description);
		this.evEditor = evEditor;
		this.supportedPrimitiveTypes = new String[]{SupportedPrimitiveType.STRING, SupportedPrimitiveType.BOOLEAN,
				SupportedPrimitiveType.DATE, /*SupportedPrimitiveType.TIME,*/ SupportedPrimitiveType.TIMESTAMP,
				SupportedPrimitiveType.SMALLINT, SupportedPrimitiveType.INT, SupportedPrimitiveType.BIGINT,
				/*SupportedPrimitiveType.BIN,*/ SupportedPrimitiveType.SMALLFLOAT, SupportedPrimitiveType.FLOAT,
				SupportedPrimitiveType.DECIMAL, /*SupportedPrimitiveType.NUM, SupportedPrimitiveType.MONEY, SupportedPrimitiveType.NUMBER*/};
		
		NameFinder.getInstance().initralize(evEditor.getEditorInput());
	}
	
	public String getFieldTypePackage(){
		return fieldTypePackage;
	}
	
	public String getFieldName(){
		return fieldName;
	}
	
	public String getFieldType(){
		return fieldType;
	}
	
	public String getTemplate(){
		String preivew = previewText.getText();
		String template = preivew.substring(preivew.lastIndexOf(" "), preivew.length()-1).trim();
		return template;
	}
	
	public void createControl(Composite parent) {
		EvHelp.setHelp( parent, EvHelp.NEW_EGL_VARIABLE_WIZARD );
		
		setPageComplete(false);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		createNewDataVariableArea(composite);
		createPreviewArea(composite);
		this.setControl(composite);
	}
	
	private void clean(){
		setPageComplete(false);
		
		fieldName = null;
		fieldType = null;
		fieldTypePackage = null;
		firstDimension = null;
		secondDimension = null;
		isArray = false;
		
		fieldNameText.setText("");
		dataTypeCombo.deselectAll();
		searchDataItemText.setText("");
		searchRecordText.setText("");
		searchDataItemTable.deselectAll();
		searchRecordTable.deselectAll();
		previewText.setText("");
		firstDimensionsText.setText("");
		secondDimensionsText.setText("");
		firstDimensionsText.setEnabled(false);
		secondDimensionsText.setEnabled(false);
	}
	
	private void createNewDataVariableArea(Composite composite){
		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setLayout(new GridLayout(3, false));

		//Type Selection Group
		Group typeSelectionGroup = new Group(group, SWT.NONE);
		typeSelectionGroup.setText(Messages.NL_NEVWP_Type_Selection_Group);
		typeSelectionGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		typeSelectionGroup.setLayout(new GridLayout(1, false));
		
		final Button recordButton = new Button(typeSelectionGroup, SWT.RADIO);
		recordButton.setText(Messages.NL_NEVWP_Record_Button);
		recordButton.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(recordButton.getSelection()){
					clean();
					((StackLayout)typeAreaComposite.getLayout()).topControl = recordComposite;
					typeAreaComposite.layout();
					isArrayButton.setEnabled(true);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
//TODO EDT 0.7 does not support dataitem		
//		final Button dataItemButton = new Button(typeSelectionGroup, SWT.RADIO);
//		dataItemButton.setText(Messages.NL_NEVWP_DataItem_Button);
//		dataItemButton.addSelectionListener(new SelectionListener(){
//
//			public void widgetSelected(SelectionEvent e) {
//				if(dataItemButton.getSelection()){
//					clean();
//					((StackLayout)typeAreaComposite.getLayout()).topControl = dataItemComposite;
//					typeAreaComposite.layout();
//				}
//			}
//
//			public void widgetDefaultSelected(SelectionEvent e) {}
//			
//		});
		
		final Button primitiveTypeButton = new Button(typeSelectionGroup, SWT.RADIO);
		primitiveTypeButton.setText(Messages.NL_NEVWP_Primitive_Type_Button);
		primitiveTypeButton.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(primitiveTypeButton.getSelection()){
					clean();
					((StackLayout)typeAreaComposite.getLayout()).topControl = primitiveTypeComposite;
					typeAreaComposite.layout();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
		
		//-------------------
		
		
		//Type Creation Group
		typeCreationGroup = new Group(group, SWT.NONE);
		typeCreationGroup.setText(Messages.NL_NEVWP_Type_Creation_Group);
		typeCreationGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		typeCreationGroup.setLayout(new GridLayout(1, false));
		
		typeAreaComposite = new Composite(typeCreationGroup, SWT.NONE);
		typeAreaComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		typeAreaComposite.setLayout(new StackLayout());
		
		createPrimitiveTypeArea();		
		createDataItemArea();
		createRecordArea();
		
		Label fieldNameLabel = new Label(typeCreationGroup, SWT.NONE);
		fieldNameLabel.setText(Messages.NL_NEVWP_Field_Name_Label);
		
		fieldNameText = new Text(typeCreationGroup, SWT.BORDER);
		fieldNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fieldNameText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				if(isFieldNameValid(fieldNameText.getText())){
					fieldName = fieldNameText.getText();
					updatePreview();
				}else{
					setPageComplete(false);
				};
			}
			
		});
		//-------------------
		
		
		//Array Properties Group
		Group arrayPropertiesGroup = new Group(group, SWT.NONE);
		arrayPropertiesGroup.setText(Messages.NL_NEVWP_Array_Properties_Group);
		arrayPropertiesGroup.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		arrayPropertiesGroup.setLayout(new GridLayout(1, false));
		
		isArrayButton = new Button(arrayPropertiesGroup, SWT.CHECK);
		isArrayButton.setText(Messages.NL_NEVWP_Is_Array_Button);
		
		//isArrayButton
		isArrayButton.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				if(isArrayButton.getSelection()){
					isArray = true;
					updatePreview();
				}else{
					isArray = false;
					updatePreview();
				}
				
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
	}
	
	private boolean isFieldNameValid(String fieldName){
		setErrorMessage(null);
		if(fieldName == null || fieldName.equals("")){
			setErrorMessage(Messages.NL_NEVWP_Error_Message_Field_Name_Is_None);
			return false;
		}
		else if(NameFinder.getInstance().isFieldNameExist(fieldName)){
			setErrorMessage(Messages.NL_NEVWP_Error_Message_Field_Name_Is_Duplicate);
			return false;
		}
		else{
			List messages = EGLNameValidator.validateEGLName(fieldName, EGLNameValidator.PART, null);
			if (!messages.isEmpty()){
				setErrorMessage(Messages.NL_NEVWP_Error_Message_Field_Name_Is_Not_Valid);
				return false;
			}
		}
		return true;
	}
	
	private void createPrimitiveTypeArea(){
		primitiveTypeComposite = new Composite(typeAreaComposite, SWT.NONE);
		primitiveTypeComposite.setLayout(new GridLayout(1, false));
		
		Group primitiveTypeDetailGroup = new Group(primitiveTypeComposite, SWT.NONE);
		primitiveTypeDetailGroup.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Group);
		primitiveTypeDetailGroup.setLayout(new GridLayout(1, false));
		primitiveTypeDetailGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite primitiveTypeDetailComposite = new Composite(primitiveTypeDetailGroup, SWT.NONE);
		primitiveTypeDetailComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		primitiveTypeDetailComposite.setLayout(new FillLayout(SWT.VERTICAL));
		
		//dataTypeComposite
		Composite dataTypeComposite = new Composite(primitiveTypeDetailComposite, SWT.NONE);
		dataTypeComposite.setLayout(new GridLayout(1, false));
		Label dataTypeLabel = new Label(dataTypeComposite, SWT.NONE);
		dataTypeLabel.setText(Messages.NL_NEVWP_Primitive_Type_Label);
		dataTypeCombo = new Combo(dataTypeComposite, SWT.BORDER|SWT.READ_ONLY);
		dataTypeCombo.setItems(supportedPrimitiveTypes);
		
		//dimensionsComposite
		Composite dimensionsComposite = new Composite(primitiveTypeDetailComposite, SWT.NONE);
		dimensionsComposite.setLayout(new GridLayout(3, false));
		Label dimensionsLabel = new Label(dimensionsComposite, SWT.NONE);
		GridData dimensionsLabelGridData = new GridData();
		dimensionsLabelGridData.horizontalSpan = 3;
		dimensionsLabel.setText(Messages.NL_NEVWP_Primitive_Dimensions_Label);
		dimensionsLabel.setLayoutData(dimensionsLabelGridData);
		firstDimensionsText = new Text(dimensionsComposite, SWT.BORDER);
		firstDimensionsText.setEnabled(false);
		Label middleDimensionsLabel = new Label(dimensionsComposite, SWT.NONE);
		middleDimensionsLabel.setText(",");
		secondDimensionsText = new Text(dimensionsComposite, SWT.BORDER);
		secondDimensionsText.setEnabled(false);
		
		firstDimensionsText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				String firstDim = firstDimensionsText.getText();
				String secondDim = secondDimensionsText.getText();
				if(isDimensionsValid(firstDim, secondDim)){
					firstDimension = firstDim;
					updatePreview();
				}else{
					setPageComplete(false);
				}
			}
		});
		
		secondDimensionsText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				String firstDim = firstDimensionsText.getText();
				String secondDim = secondDimensionsText.getText();
				if(isDimensionsValid(firstDim, secondDim)){
					secondDimension = secondDim;
					updatePreview();
				}else{
					setPageComplete(false);
				}
			}
		});
		
		
		//typeDetailMessageText
		final Text typeDetailMessageText = new Text(primitiveTypeDetailGroup, SWT.READ_ONLY);
		typeDetailMessageText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		dataTypeCombo.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				int index = dataTypeCombo.getSelectionIndex();
				fieldType = supportedPrimitiveTypes[index];
				setDefaultFieldName(fieldNameText);
				updateTypeDetailMessageText();
				updatePreview();
			}
			
			private void disableDimensionsComposite(){
				firstDimensionsText.setEnabled(false);
				secondDimensionsText.setEnabled(false);
				hasDimensions = false;
			}
			
			private void enableDimensionsComposite(String firstDim, String secondDim){
				firstDimensionsText.setEnabled(true);
				secondDimensionsText.setEnabled(true);
				secondDimensionsText.setEditable(true);
				hasDimensions = true;
				firstDimensionsText.setText(firstDim);
				secondDimensionsText.setText(secondDim);
				firstDimension = firstDim;
				secondDimension = secondDim;
			}
			
			private void updateTypeDetailMessageText(){
				if(fieldType != null){
					if(fieldType.equals(SupportedPrimitiveType.BIGINT)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Bigint);
						disableDimensionsComposite();
					}
					if(fieldType.equals(SupportedPrimitiveType.BIN)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Bin);
						enableDimensionsComposite("4", "0");
						secondDimensionsText.setEditable(false);
					}
					if(fieldType.equals(SupportedPrimitiveType.BOOLEAN)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Boolean);
						disableDimensionsComposite();
					}
					if(fieldType.equals(SupportedPrimitiveType.DATE)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Date);
						disableDimensionsComposite();
					}
					if(fieldType.equals(SupportedPrimitiveType.DECIMAL)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Decimal);
						enableDimensionsComposite("8", "0");
					}
					if(fieldType.equals(SupportedPrimitiveType.FLOAT)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Float);
						disableDimensionsComposite();
					}
					if(fieldType.equals(SupportedPrimitiveType.INT)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Int);
						disableDimensionsComposite();
					}
					if(fieldType.equals(SupportedPrimitiveType.MONEY)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Money);
						enableDimensionsComposite("16", "2");
					}
					if(fieldType.equals(SupportedPrimitiveType.NUM)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Num);
						enableDimensionsComposite("16", "2");
					}
					if(fieldType.equals(SupportedPrimitiveType.SMALLFLOAT)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Smallfloat);
						disableDimensionsComposite();
					}
					if(fieldType.equals(SupportedPrimitiveType.SMALLINT)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Smallint);
						disableDimensionsComposite();
					}
					if(fieldType.equals(SupportedPrimitiveType.STRING)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_String);
						disableDimensionsComposite();
					}
					if(fieldType.equals(SupportedPrimitiveType.TIME)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Time);
						disableDimensionsComposite();
					}
					if(fieldType.equals(SupportedPrimitiveType.TIMESTAMP)){
						typeDetailMessageText.setText(Messages.NL_NEVWP_Primitive_Type_Detail_Message_Timestamp);
						disableDimensionsComposite();
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
	}
	
	private boolean isDimensionsValid(String firstDim, String secondDim){
		setErrorMessage(null);
		
		//must has firstDimension
		if(firstDimensionsText.isEnabled() && secondDimensionsText.isEnabled()){
			if(firstDim == null || firstDim.equals("")){
				setErrorMessage(Messages.NL_NEVWP_Error_Message_Primitive_Type_Dimensions_Is_None);
				return false;
			}
		
			try{
				int iFirstDim = Integer.parseInt(firstDim);	
				if(iFirstDim < 0){
					setErrorMessage(Messages.NL_NEVWP_Error_Message_Primitive_Type_Dimensions_Is_None);
					return false;
				}
				
				//has secondDimension
				if(secondDim != null && !secondDim.equals("")){
					int iSecondDim	= Integer.parseInt(secondDim);
					if(iSecondDim < 0){
						setErrorMessage(Messages.NL_NEVWP_Error_Message_Primitive_Type_Dimensions_Is_None);
						return false;
					}
					
					if(iFirstDim < iSecondDim){
						setErrorMessage(Messages.NL_NEVWP_Error_Message_Primitive_Type_Dimensions_First_Is_Small_Then_Second);
						return false;
					}
				}
				
				//verify firstDimension
				if(fieldType.equals(SupportedPrimitiveType.BIN)){
					if((iFirstDim != 4 && iFirstDim != 9 && iFirstDim != 18)){
						setErrorMessage(Messages.NL_NEVWP_Error_Message_Primitive_Type_Dimensions_Is_Not_Valid);	
						return false;
					}
				}
				if(fieldType.equals(SupportedPrimitiveType.DECIMAL)){
					if((iFirstDim <1 || iFirstDim > 32)){
						setErrorMessage(Messages.NL_NEVWP_Error_Message_Primitive_Type_Dimensions_Is_Not_Valid);	
						return false;
					}
				}
				if(fieldType.equals(SupportedPrimitiveType.MONEY)){
					if((iFirstDim <2 || iFirstDim > 32)){
						setErrorMessage(Messages.NL_NEVWP_Error_Message_Primitive_Type_Dimensions_Is_Not_Valid);	
						return false;
					}
				}
				if(fieldType.equals(SupportedPrimitiveType.NUM)){
					if((iFirstDim <1 || iFirstDim > 32)){
						setErrorMessage(Messages.NL_NEVWP_Error_Message_Primitive_Type_Dimensions_Is_Not_Valid);	
						return false;
					}
				}
			}catch(Exception e){
				setErrorMessage(Messages.NL_NEVWP_Error_Message_Primitive_Type_Dimensions_Is_None);
				return false;
			}
		}
		return true;
	}
	
	private void createDataItemArea(){
		dataItemComposite = new Composite(typeAreaComposite, SWT.NONE);
		dataItemComposite.setLayout(new GridLayout(1, false));
		Group searchDataItemGroup = new Group(dataItemComposite, SWT.NONE);
		searchDataItemGroup.setText(Messages.NL_NEVWP_Search_DataItem_Group);
		searchDataItemGroup.setLayout(new GridLayout(1, false));
		searchDataItemGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		searchDataItemText = new Text(searchDataItemGroup, SWT.BORDER);
		searchDataItemText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchDataItemText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				searchDataItemTable.removeAll();
				fillSearchTable(searchDataItemTable, DataType.DataItem, searchDataItemText.getText());
			}

		});
		
		searchDataItemTable = new Table(searchDataItemGroup, SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL);
		GridData searchDataItemTableGridData = new GridData(GridData.FILL_BOTH);
		searchDataItemTableGridData.heightHint = 100;
		searchDataItemTable.setLayoutData(searchDataItemTableGridData);
		fillSearchTable(searchDataItemTable, DataType.DataItem, null);
		searchDataItemTable.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				handleSearchTableSelection(e);
				fieldType = selectedPartDeclarationInfo.getPartName();
				fieldTypePackage = selectedPartDeclarationInfo.getPackageName();
				setDefaultFieldName(fieldNameText);
				updatePreview();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});
	}
	
	private void createRecordArea(){
		recordComposite = new Composite(typeAreaComposite, SWT.NONE);
		recordComposite.setLayout(new GridLayout(1, false));
		
		Group searchRecordGroup = new Group(recordComposite, SWT.NONE);
		searchRecordGroup.setText(Messages.NL_NEVWP_Search_Record_Group);
		searchRecordGroup.setLayout(new GridLayout(1, false));
		searchRecordGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		searchRecordText = new Text(searchRecordGroup, SWT.BORDER);
		searchRecordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchRecordText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				searchRecordTable.removeAll();
				fillSearchTable(searchRecordTable, DataType.Record, searchRecordText.getText());
			}

		});
		
		searchRecordTable = new Table(searchRecordGroup, SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL);
		GridData searchRecordTableGridData = new GridData(GridData.FILL_BOTH);
		searchRecordTableGridData.heightHint = 100;
		searchRecordTable.setLayoutData(searchRecordTableGridData);
		fillSearchTable(searchRecordTable, DataType.Record, null);
		searchRecordTable.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				handleSearchTableSelection(e);
				fieldType = selectedPartDeclarationInfo.getPartName();
				fieldTypePackage = selectedPartDeclarationInfo.getPackageName();
				setDefaultFieldName(fieldNameText);
				updatePreview();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
			
		});

	}
	
	private void setDefaultFieldName(Text fieldNameText){
		String tempFieldName = "my" + fieldType;
		fieldName = tempFieldName;
		int i = 0;
		while(NameFinder.getInstance().isFieldNameExist(fieldName)){
			i++;
			fieldName = tempFieldName + i; 
		}
		fieldNameText.setText(fieldName);
	}
	
	private void updatePreview(){
		StringBuffer sbPreview = new StringBuffer();
		if(fieldName != null && fieldType != null){
			sbPreview.append(fieldName).append(" ").append(fieldType);
			if(hasDimensions){
				sbPreview.append("(");
				if(firstDimension != null && !firstDimension.equals("")){
					sbPreview.append(firstDimension);
					if(secondDimension != null && !secondDimension.equals("")){
						sbPreview.append(",").append(secondDimension);
					}
				}
				sbPreview.append(")");
			}
			if(fieldType.equals(SupportedPrimitiveType.TIMESTAMP)){
				sbPreview.append("(\"HHmmss\")");
			}
			if(isArray){
				sbPreview.append("[").append("]");
			}
			
			sbPreview.append(";");
			setPageComplete(true);
		}
		previewText.setText(sbPreview.toString());
	}
	
	private void fillSearchTable(Table searchTable, DataType dataType, String partName){
		List<PartDeclarationInfo> partDeclarationInfos = NewEGLVariableWizardUtil.getAvailableParts(EGLCore.create(evEditor.getProject()), dataType);
		for(PartDeclarationInfo partDeclarationInfo : partDeclarationInfos){
			boolean isAddToTable = false;
			if(partName == null){
				isAddToTable = true;
				
			}else if(partDeclarationInfo.getPartName().toUpperCase().startsWith(partName.toUpperCase())){
				isAddToTable = true;
			}
			
			if(isAddToTable){
				StringBuffer sbRecordlabel = new StringBuffer(partDeclarationInfo.getPartName()).append(" (");
				if(partDeclarationInfo.getPackageName() != null){
					sbRecordlabel.append(partDeclarationInfo.getPackageName()).append("/");
				}
				sbRecordlabel.append(partDeclarationInfo.getFileName()).append(".").append(partDeclarationInfo.getExtension()).append(")");
				TableItem tableItem = new TableItem(searchTable, SWT.NONE);
				tableItem.setImage(NewEGLVariableWizardUtil.getImage(dataType));
				tableItem.setText(sbRecordlabel.toString());
				tableItem.setData(partDeclarationInfo);
			}
			
		}
	};
	
	private void handleSearchTableSelection(SelectionEvent e){
		Table table = (Table)e.getSource();
		TableItem tableItem = (TableItem)table.getSelection()[0];
		selectedPartDeclarationInfo = (PartDeclarationInfo)tableItem.getData();
	}
	
	private void createPreviewArea(Composite composite){
		//@bd1a Start 
		int style = SWT.NONE;
		if(Locale.getDefault().toString().toLowerCase().indexOf("ar") != -1) {
			style |= SWT.LEFT_TO_RIGHT; 
		}
		//@bd1a End
		Group group = new Group(composite, style); //@bd1c
		
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(1, false));
		group.setText(Messages.NL_NEVWP_Preview_Group);
		
		previewText = new Text(group, SWT.READ_ONLY);
		previewText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
}
