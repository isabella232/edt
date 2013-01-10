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
package org.eclipse.edt.ide.ui.internal.quickfix;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get NLSed messages.
 */
public final class CorrectionMessages extends NLS {

	private static final String BUNDLE_NAME= CorrectionMessages.class.getName();

	private CorrectionMessages() {
		// Do not instantiate
	}

	public static String EGLCorrectionProposal_error_title;
	public static String EGLCorrectionProposal_error_message;
	public static String EGLCorrectionProcessor_error_quickfix_message;
	public static String EGLCorrectionProcessor_error_quickassist_message;
	public static String EGLCorrectionProcessor_error_status;
	public static String EGLCorrectionProcessor_go_to_closest_using_menu;
	public static String EGLCorrectionProcessor_go_to_closest_using_key;
	public static String EGLCorrectionProcessor_go_to_original_using_menu;
	public static String EGLCorrectionProcessor_go_to_original_using_key;
	public static String NoCorrectionProposal_description;

	public static String SQLStatementAddProposalLabel;
	public static String SQLStatementAddWithIntoProposalLabel;
	public static String SQLStatementRemoveProposalLabel;
	public static String SQLStatementResetProposalLabel;
	public static String SQLExceptionMessage;

	static {
		NLS.initializeMessages(BUNDLE_NAME, CorrectionMessages.class);
	}

}
