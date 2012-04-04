/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.dse.dialogs.ConnectionDisplayProperty;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class DTOConfiguration {
	
	//Data from page 1	
	private IConnectionProfile databaseConnection;
	private List selectedTables;
	private boolean useDelimitedIdentifiers;
	private boolean qualifiedTableNames;
	private boolean saveConnectionToDeploymentDescriptor;
		
	//Data from page 2
	private HashMap tableFieldHashMap;  //(Key: Table, Value: EGLDataPartsPagesWizardTableFieldConfiguration)
	
	//Data from page 3
	private HashMap tablePageDataHashMap; //(Key: Table, Value: EGLDataPartsPagesWizardTablePageConfiguration)	
	

	public DTOConfiguration() {
		setDefaultValues();
	}
	
	//Inner classes
	class EGLDataPartsPagesWizardTableFieldConfiguration {		
		private List keyFieldList;
		private List selectionConditionFieldList;
		
		EGLDataPartsPagesWizardTableFieldConfiguration() {
			keyFieldList = new ArrayList();
			selectionConditionFieldList = new ArrayList();
		}

		public List getKeyFieldList() {
			return keyFieldList;
		}

		public void setKeyFieldList(List keyFieldList) {
			this.keyFieldList = keyFieldList;
		}

		public List getSelectionConditionFieldList() {
			return selectionConditionFieldList;
		}

		public void setSelectionConditionFieldList(List selectionConditionFieldList) {
			this.selectionConditionFieldList = selectionConditionFieldList;
		}
	}

	class EGLDataPartsPagesWizardTablePageConfiguration {
		
		private HashMap displayNameList;
		private List summaryList;
		
		EGLDataPartsPagesWizardTablePageConfiguration() {
			displayNameList = new HashMap();
			summaryList = new ArrayList();
		}

		public HashMap getDisplayNameFieldHash() {
			return displayNameList;
		}

		public void setDisplayNameFieldList(HashMap displayNameList) {
			this.displayNameList = displayNameList;
		}

		public List getSummaryFieldList() {
			return summaryList;
		}

		public void setSummaryFieldList(List summaryList) {
			this.summaryList = summaryList;
		}
	}
	
	//Configuration Methods	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setDefaultValues();
	}
	
	protected void setDefaultValues() {
		//Data from page 1		
		databaseConnection = null;
		selectedTables = new ArrayList();
		qualifiedTableNames = false;		
				
		//Data from page 2
		tableFieldHashMap = new HashMap();
	
		
		//Data from page 3
		tablePageDataHashMap = new HashMap();		
	}

	public IConnectionProfile getDatabaseConnection() {
		return databaseConnection;
	}

	public void setDatabaseConnection(IConnectionProfile databaseConnection) {
		this.databaseConnection = databaseConnection;
	}


	public List getSelectedTables() {
		return selectedTables;
	}

	public void setSelectedTables(List newSelectedTables) {
		if(selectedTables!=null && !selectedTables.isEmpty())
			selectedTables.clear();
		selectedTables.addAll(newSelectedTables);
	}
	


	public EGLDataPartsPagesWizardTableFieldConfiguration getTableFields(Object targetTable) {
		EGLDataPartsPagesWizardTableFieldConfiguration tableFields = (EGLDataPartsPagesWizardTableFieldConfiguration)tableFieldHashMap.get(targetTable);
		
		if(tableFields==null) {
			tableFields = new EGLDataPartsPagesWizardTableFieldConfiguration();
			setTableFieldHashMap(targetTable, tableFields);
		}
		
		return tableFields;
	}

	public void setTableFieldHashMap(Object targetTable, EGLDataPartsPagesWizardTableFieldConfiguration newFieldValue) {
		tableFieldHashMap.put(targetTable, newFieldValue);
	}
	
	public List getKeyFields(Object targetTable) {
		EGLDataPartsPagesWizardTableFieldConfiguration fieldObject = getTableFields(targetTable);
		if(fieldObject != null)
			return fieldObject.getKeyFieldList();
		return new ArrayList();
	}
	
	public List getSelectionConditionFields(Object targetTable) {
		EGLDataPartsPagesWizardTableFieldConfiguration fieldObject = getTableFields(targetTable);
		if(fieldObject != null)
			return fieldObject.getSelectionConditionFieldList();
		return new ArrayList();
	}

	public EGLDataPartsPagesWizardTablePageConfiguration getTablePages(Object targetTable) {
		EGLDataPartsPagesWizardTablePageConfiguration tablePages = (EGLDataPartsPagesWizardTablePageConfiguration)tablePageDataHashMap.get(targetTable);
		
		if(tablePages==null) {
			tablePages = new EGLDataPartsPagesWizardTablePageConfiguration();
			setTablePageDataHashMap(targetTable, tablePages);
		}
		
		return tablePages;
	}

	public void setTablePageDataHashMap(Object targetTable, EGLDataPartsPagesWizardTablePageConfiguration newPageValue) {
		tablePageDataHashMap.put(targetTable, newPageValue);
	}
	
	public HashMap getDisplayName(Object targetTable) {
		EGLDataPartsPagesWizardTablePageConfiguration fieldObject = getTablePages(targetTable);
		if(fieldObject != null)
			return fieldObject.getDisplayNameFieldHash();
		return new HashMap();
	}
	
	public List getSummaryFields(Object targetTable) {
		EGLDataPartsPagesWizardTablePageConfiguration fieldObject = getTablePages(targetTable);
		if(fieldObject != null)
			return fieldObject.getSummaryFieldList();
		return new ArrayList();
	}
	

	public String getDatabaseName() {
		ConnectionDisplayProperty[] properties = EGLSQLUtility.getConnectionDisplayProperties(databaseConnection);
		return properties[1].getValue();
	}

	public boolean isQualifiedTableNames() {
		return qualifiedTableNames;
	}

	public void setQualifiedTableNames(boolean qualifiedTableNames) {
		this.qualifiedTableNames = qualifiedTableNames;
	}

	public boolean isUseDelimitedIdentifiers() {
		return useDelimitedIdentifiers;
	}

	public void setUseDelimitedIdentifiers(boolean useDelimitedIdentifiers) {
		this.useDelimitedIdentifiers = useDelimitedIdentifiers;
	}
	
	public boolean isSaveConnectionToDeploymentDescriptor() {
		return saveConnectionToDeploymentDescriptor;
	}

	public void setSaveConnectionToDeploymentDescriptor(boolean saveConnectionToDeploymentDescriptor) {
		this.saveConnectionToDeploymentDescriptor = saveConnectionToDeploymentDescriptor;
	}
}
