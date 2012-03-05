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
package org.eclipse.edt.ide.ui.internal.record;

import org.eclipse.osgi.util.NLS;

public class NewRecordWizardMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.ui.internal.record.NewRecordWizardMessages"; //$NON-NLS-1$

	private NewRecordWizardMessages() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, NewRecordWizardMessages.class);
	}

	public static String AbstractRecordFromStringInputPage_pageName;
	public static String AbstractRecordFromStringInputPage_createFromURL;
	public static String AbstractRecordFromStringInputPage_createFromFile;
	public static String AbstractRecordFromStringInputPage_createFromString;
	public static String AbstractRecordFromStringInputPage_fileLabel;
	public static String AbstractRecordFromStringInputPage_browseLabel;
	public static String AbstractRecordFromStringInputPage_urlLabel;
	public static String AbstractRecordFromStringInputPage_stringEntryLabel;
	public static String AbstractRecordFromStringInputPage_enterURLMessage;
	public static String AbstractRecordFromStringInputPage_enterFilenameMessage;
	public static String AbstractRecordFromStringInputPage_enterStringMessage;
	
	public static String NewRecordSummaryPage_pageName;
	public static String NewRecordSummaryPage_pageTitle;
	public static String NewRecordSummaryPage_pageDescription;
	public static String NewRecordSummaryPage_previewLabel;
	public static String NewRecordSummaryPage_warningLabel;
	public static String NewRecordSummaryPage_warningLabel2;
	
	public static String NewRecordWizard_title;
	
	public static String NewRecordWizardPage_title;
	public static String NewRecordWizardPage_description;
	public static String NewRecordWizardPage_selectContainer;
	
	public static String RecordConfigWizardPage_pageName;
	public static String RecordConfigWizardPage_title;
	public static String RecordConfigWizardPage_description;
	public static String RecordConfigWizardPage_nameLabel;
	public static String RecordConfigWizardPage_typeLabel;
	public static String RecordConfigWizardPage_addLabel;
	public static String RecordConfigWizardPage_removeLabel;
	public static String RecordConfigWizardPage_upLabel;
	public static String RecordConfigWizardPage_downLabel;
	
	public static String TemplateSelectionPage_title;
	public static String TemplateSelectionPage_description;
	public static String TemplateSelectionPage_templatesLabel;
	public static String TemplateSelectionPage_descriptionLabel;

	public static String PartsUtil_reservedWordRenameMessage;
	public static String PartsUtil_definedAsNullableMessage;
	public static String PartsUtil_renameFieldInUseMessage;
	public static String PartsUtil_reservedWordRename2Message;
	public static String PartsUtil_invalidCharactersRenameMessage;
	public static String PartsUtil_invalidCharactersRename2Message;
	
	public static String PartsFromXMLSchemaUtil_missingDurationPatternMessage;

	public static String RecordFromCsvPage_pageName;
	public static String RecordFromCsvPage_title;
	public static String RecordFromCsvPage_description;
	public static String RecordFromCsvPage_createFromFileLabel;
	public static String RecordFromCsvPage_fileLabel;
	public static String RecordFromCsvPage_browseLabel;
	public static String RecordFromCsvPage_attributesLabel;
	public static String RecordFromCsvPage_delimiterLabel;
	public static String RecordFromCsvPage_textQualifierLabel;
	public static String RecordFromCsvPage_quotedlabel;
	public static String RecordFromCsvPage_addToLabel;
	public static String RecordFromCsvPage_copyLabel;

	public static String RecordFromCsvWizard_title;
	
	public static String RecordFromJsonPage_title;
	public static String RecordFromJsonPage_description;

	public static String RecordFromXMLPage_title;
	public static String RecordFromXMLPage_description;
	
	public static String RecordFromXMLSchemaPage_title;
	public static String RecordFromXMLSchemaPage_description;
	
	public static String RecordFromSqlDatabasePage_Description;
	
	public static String GeneratingProgressMonitor_PromptionText;
}
