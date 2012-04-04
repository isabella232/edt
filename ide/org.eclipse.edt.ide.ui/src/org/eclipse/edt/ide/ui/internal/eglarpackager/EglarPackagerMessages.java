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
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import org.eclipse.osgi.util.NLS;

public class EglarPackagerMessages {
	private static final String BUNDLE_NAME= "org.eclipse.edt.ide.ui.internal.eglarpackager.EglarPackagerMessages";//$NON-NLS-1$
	
	/**Messages for AbstractEglarDestinationWizardPase*/
	public static String AbstractEglarDestinationWizardPage_destinationCombo_AccessibilityText;
	
	public static String EglarPackageWizardPage_browseButton_text;
	
	public static String EglarPackageWizardPage_destination_label;
	
	public static String EglarPackageWizardPage_error_EglarFileExistsAndNotWritable;
	
	public static String EglarPackageWizardPage_error_cantExportEglarIntoItself;
	
	public static String EglarPackageWizardPage_info_relativeExportDestination;
	
	/** Messages for EglarPackageWizardPage Page*/
	public static String EglarPackageWizardPage_whatToExport_label;
	
	public static String EglarPackageWizardPage_whereToExport_label;
	
	public static String EglarPackageWizardPage_tree_accessibility_message;
	
	public static String EglarPackageWizardPage_exportEGLWithErrors_text;
	
	public static String EglarPackageWizardPage_exportEGLWithWarnings_text;
	
	public static String EglarPackageWizardPage_exportEGLSourceFiles_text;
	
	public static String EglarPackageWizardPage_no_refactorings_selected;
	
	public static String EglarPackageWizardPage_SelectDialogTitle;
	
	/** Messages for Option Page*/
	public static String EglarOptionsPage_title;
	
	public static String EglarOptionsPage_description;
	
    public static String EglarOptionsPage_Is_Generatable_text;
	
	public static String EglarOptionsPage_Are_EGL_source_exported_text;

	public static String EglarOptionsPage_vendor_label;
	
	public static String EglarOptionsPage_version_label;
	
	/** Messages for EglarPackagerUtil*/
	public static String EglarPackage_confirmReplace_message;
	
	public static String EglarPackage_confirmReplace_title;
	
	public static String EglarPackage_confirmCreate_title;
	
	public static String BinaryProjectPackage_confirmReplace_message;
	
	public static String EglarPackage_confirmCreate_message;
	
	public static String BinaryProjectPackage_confirmCreate_message;
	
	public static String EglarPackage_confirmExportTLFSource_title;
	
	public static String EglarPackage_confirmExportTLFSource_message;
	
	/** Messages for EglarPackagerWizard*/
	public static String EglarPackageWizard_windowTitle;
	
	public static String EglarPackageWizard_EglarExport_title;
	
	public static String EglarPackageWizard_EglarExportError_title;
	
	public static String EglarPackageWizard_EglarExportError_message;
	
    public static String EglarPackageWizardPage_title;
    
    public static String EglarPackageWizardPage_description;
	
	public static String EglarPackageWizardPage_selectAll;
	
	public static String EglarPackageWizardPage_deselectAll;
	
	
	/** Messages for EglarFileExportOperation */
	public static String EglarFileExportOperation_coreErrorDuringExport;
	
	public static String EglarFileExportOperation_exporting;
	
	public static String EglarFileExportOperation_exportFinishedWithInfo;
	
	public static String EglarFileExportOperation_exportFinishedWithWarnings;
	
	public static String EglarFileExportOperation_creationOfSomeEglarsFailed;
	
	public static String EglarFileExportOperation_EglarCreationFailed;
	
	public static String EglarFileExportOperation_eglarCreationFailedSeeDetails;
	
	public static String EglarFileExportOperation_noResourcesSelected;
	
	public static String EglarFileExportOperation_invalidEglarLocation;
	
	public static String EglarFileExportOperation_EglarFileExistsAndNotWritable;
	
	public static String EglarFileExportOperation_errorDuringProjectBuild;
	
	/** Messages for EglarWriterUtility */
	public static String EglarWriter_writeProblemWithMessage;
	
	public static String EglarWriter_writeProblem;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, EglarPackagerMessages.class);
	}

	private EglarPackagerMessages() {
		// Do not instantiate
	}
}
