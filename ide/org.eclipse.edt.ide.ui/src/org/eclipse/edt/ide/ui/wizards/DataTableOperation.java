/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.wizards;

import org.eclipse.edt.ide.ui.internal.templates.TemplateEngine;

public class DataTableOperation extends EGLFileOperation {

	private DataTableConfiguration configuration;

	/**
	 * 
	 */
	public DataTableOperation(DataTableConfiguration configuration) {
		super(configuration);
		this.configuration = configuration;
	}

	protected String getFileContents() throws PartTemplateException {
		
//		String dataTableOutputString;		
//		String dataTableDefinition;
//		String dataTableProperties = null;
//		String dataTableStructureSpecification;
//		
//		dataTableDefinition = "DataTable " + configuration.getDataTableName() + " ";
//		if(configuration.getDataTableType()!=DataTableConfiguration.NONE){
//			dataTableDefinition = dataTableDefinition.concat("type ");
//		}
//		if(configuration.getDataTableType()==DataTableConfiguration.MESSAGE){
//			dataTableDefinition = dataTableDefinition.concat("msgTable ");
//		}
//		else if(configuration.getDataTableType()==DataTableConfiguration.MATCH_VALID){
//			dataTableDefinition = dataTableDefinition.concat("matchValidTable ");
//		}
//		else if(configuration.getDataTableType()==DataTableConfiguration.MATCH_INVALID){
//			dataTableDefinition = dataTableDefinition.concat("matchInvalidTable ");
//		}
//		else if(configuration.getDataTableType()==DataTableConfiguration.RANGE_CHECK){
//			dataTableDefinition = dataTableDefinition.concat("rangeChkTable ");
//		}
//
//		//Build Properties string here
//		//Adavanced Properties - NOT YET
//		//"alias=" + /*runtimeName*/
//		//"I4GLCompatibility=" + /*yes or no*/
//		//"allowUnqualifiedItemReferences=" + /*yes or no*/
//		//"includeReferencedFunctions=" + /*yes or no*/
//		if(dataTableProperties != null){
//			dataTableDefinition = dataTableDefinition.concat("{ " + dataTableProperties + " }");
//		}
//
//		dataTableDefinition = dataTableDefinition.concat("\n\n");
//
//		dataTableStructureSpecification = "\t//Define your table structure specification here";
//
//		//Add place holders for column support
//		String singleColumnSupport = null;
//		String doubleColumnSupport = null;
//		String columnHeaders = null;
//
//		if(configuration.getDataTableType()!=DataTableConfiguration.NONE){
//			columnHeaders = "\n\n\tColumn1 columnOne;\n";
//			singleColumnSupport = " with at least one column." + columnHeaders + "\n\n\t{contents = \n\t\t[\n\t\t\t[/*Your data here*/]\n\t\t]}\n\n";
//		}
//		if(configuration.getDataTableType()==DataTableConfiguration.MESSAGE || 
//			configuration.getDataTableType()==DataTableConfiguration.RANGE_CHECK){
//			columnHeaders = columnHeaders.concat("\tColumn2 columnTwo;\n");
//			doubleColumnSupport = " with at least two columns." + columnHeaders + "\n\t{contents = \n\t\t[\n\t\t\t[/*Your data here*/],\n\t\t\t[/*Your data here*/]\n\t\t]}\n\n";
//			dataTableStructureSpecification = dataTableStructureSpecification.concat(doubleColumnSupport);
//		}
//		else if (singleColumnSupport != null){
//			dataTableStructureSpecification = dataTableStructureSpecification.concat(singleColumnSupport);
//		}
//		if(configuration.getDataTableType()==DataTableConfiguration.NONE){
//			dataTableStructureSpecification = dataTableStructureSpecification.concat(".\n\n");
//		}
//
//		//Build the final string				
//		dataTableOutputString = dataTableDefinition + dataTableStructureSpecification + "end";
		
		String dataTableOutputString = ""; //$NON-NLS-1$
		
		String templateName = "dataTable"; //$NON-NLS-1$
		String templateid = "org.eclipse.edt.ide.ui.templates.basic_table";  //$NON-NLS-1$

		String templateVariableProgramName = "${dataTableName}"; //$NON-NLS-1$

		//Determine type of data table and update template Description
		if(configuration.getDataTableType()==DataTableConfiguration.NONE){
			templateid = "org.eclipse.edt.ide.ui.templates.basic_table"; //$NON-NLS-1$
		}
		else if(configuration.getDataTableType()==DataTableConfiguration.MATCH_INVALID){
			templateid = "org.eclipse.edt.ide.ui.templates.match_invalid_table"; //$NON-NLS-1$
		}
		else if(configuration.getDataTableType()==DataTableConfiguration.MATCH_VALID){
			templateid = "org.eclipse.edt.ide.ui.templates.match_valid_table"; //$NON-NLS-1$
		}
		else if(configuration.getDataTableType()==DataTableConfiguration.MESSAGE){
			templateid = "org.eclipse.edt.ide.ui.templates.message_table"; //$NON-NLS-1$
		}
		else if(configuration.getDataTableType()==DataTableConfiguration.RANGE_CHECK){
			templateid = "org.eclipse.edt.ide.ui.templates.range_check_table"; //$NON-NLS-1$
		}

		//Determine the template to use
		if(configuration.getChosenTemplateSelection()==EGLPartConfiguration.USE_CUSTOM){
			dataTableOutputString = TemplateEngine.getCustomizedTemplateString(templateName, templateid);	
		}
		else if(configuration.getChosenTemplateSelection()==EGLPartConfiguration.USE_DEFAULT){
			dataTableOutputString = TemplateEngine.getDefaultTemplateString(templateName, templateid);
		}
		//Check for error condition (no template found / template disabled)
		if(dataTableOutputString.compareTo("")!=0){ //$NON-NLS-1$

			//Find and replace variables
			int variableBegin;
	
			String firstHalfOutputString;
			String secondHalfOutputString;
	
			String newVariableValue = configuration.getDataTableName();
	
			variableBegin = dataTableOutputString.indexOf(templateVariableProgramName);
			
			if(variableBegin == -1){
				throw new PartTemplateException(templateName, templateid, EGLFileConfiguration.TEMPLATE_CORRUPTED);
			}			
			
			firstHalfOutputString = dataTableOutputString.substring(0, variableBegin);
			secondHalfOutputString = dataTableOutputString.substring(variableBegin + templateVariableProgramName.length(), dataTableOutputString.length());
	
			dataTableOutputString = firstHalfOutputString + newVariableValue + secondHalfOutputString;
	
			//Remove remaining template tags
			int tagStart;
			int tagEnd;
	
			while(dataTableOutputString.indexOf("${")!=-1){ //$NON-NLS-1$
				tagStart = dataTableOutputString.indexOf("${"); //$NON-NLS-1$
				tagEnd = dataTableOutputString.indexOf("}", tagStart); //$NON-NLS-1$
		
				firstHalfOutputString = dataTableOutputString.substring(0, tagStart);
				secondHalfOutputString = dataTableOutputString.substring(tagStart + 2, dataTableOutputString.length());
		
				dataTableOutputString = firstHalfOutputString + secondHalfOutputString;
	
				firstHalfOutputString = dataTableOutputString.substring(0, tagEnd -2);
				secondHalfOutputString = dataTableOutputString.substring(tagEnd - 1, dataTableOutputString.length());
	
				//Check for (and remove) cursor variable
				if(firstHalfOutputString.endsWith("cursor")) //$NON-NLS-1$
					firstHalfOutputString = firstHalfOutputString.substring(0, firstHalfOutputString.length()-6);	
	
				dataTableOutputString = firstHalfOutputString + secondHalfOutputString;
			}
	
			return dataTableOutputString;
		}
		else{
			throw new PartTemplateException(templateName, templateid, EGLFileConfiguration.TEMPLATE_NOT_FOUND);
		}
	}

}
