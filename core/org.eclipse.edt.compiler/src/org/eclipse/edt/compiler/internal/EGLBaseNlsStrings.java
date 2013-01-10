/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal;

import org.eclipse.osgi.util.NLS;



public class EGLBaseNlsStrings extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.edt.compiler.internal.EGLBaseResources"; //$NON-NLS-1$

	private EGLBaseNlsStrings() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, EGLBaseNlsStrings.class);
	}
	
	public static String CAProposalFunctionSystemWord;
	public static String CAProposalVariableSystemWord;
	public static String CAProposalArrayFunctionSystemWord;
	public static String CAProposalArrayVariableSystemWord;

	public static String PropertyNoValueSet;
	
	public static String SystemPackagesInit;
	public static String SystemPackagesProcessingArchive;

	public static String DataItemBINMnemonicString;
	public static String DataItemCHARMnemonicString;
	public static String DataItemDBCHARMnemonicString;
	public static String DataItemHEXMnemonicString;
	public static String DataItemMBCHARMnemonicString;
	public static String DataItemNUMMnemonicString;
	public static String DataItemNUMCMnemonicString;
	public static String DataItemPACFMnemonicString;
	public static String DataItemDECIMALMnemonicString;
	public static String DataItemUNICODEMnemonicString;
	public static String DataItemNUMBERMnemonicString;
	public static String DataItemVARCHARMnemonicString;
	public static String DataItemVARDBCHARMnemonicString;
	public static String DataItemVARMBCHARMnemonicString;
	public static String DataItemVARUNICODEMnemonicString;
	public static String DataItemBYTEMnemonicString;
	public static String DataItemSMALLINTMnemonicString;
	public static String DataItemINTMnemonicString;
	public static String DataItemBIGINTMnemonicString;
	public static String DataItemFLOATMnemonicString;
	public static String DataItemSMALLFLOATMnemonicString;
	public static String DataItemDECIMALFLOATMnemonicString;
	public static String DataItemMONEYMnemonicString;
	public static String DataItemDATEMnemonicString;
	public static String DataItemINTEGERDATEMnemonicString;
	public static String DataItemTIMEMnemonicString;
	public static String DataItemTIMESTAMPMnemonicString;
	public static String DataItemINTERVALMnemonicString;
	public static String DataItemCLOBMnemonicString;
	public static String DataItemBLOBMnemonicString;
	public static String DataItemDBCLOBMnemonicString;
	public static String DataItemSTRINGMnemonicString;
	public static String DataItemBOOLEANMnemonicString;
}	
