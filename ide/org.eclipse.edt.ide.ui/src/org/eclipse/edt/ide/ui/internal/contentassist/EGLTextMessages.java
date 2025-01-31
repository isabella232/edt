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
package org.eclipse.edt.ide.ui.internal.contentassist;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get NLSed messages.
 */
final class EGLTextMessages extends NLS {

	private static final String BUNDLE_NAME= EGLTextMessages.class.getName();

	private EGLTextMessages() {
		// Do not instantiate
	}

	public static String CompletionProposalComputerRegistry_messageAvoidanceHint;
	public static String CompletionProposalComputerRegistry_messageAvoidanceHintWithWarning;
	public static String ContentAssistProcessor_all_disabled_message;
	public static String ContentAssistProcessor_all_disabled_preference_link;
	public static String ContentAssistProcessor_all_disabled_title;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, EGLTextMessages.class);
	}

	public static String ContentAssistProcessor_computing_proposals;
	public static String ContentAssistProcessor_collecting_proposals;
	public static String ContentAssistProcessor_sorting_proposals;
	public static String CompletionProposalComputerDescriptor_illegal_attribute_message;
	public static String CompletionProposalComputerDescriptor_reason_invalid;
	public static String CompletionProposalComputerDescriptor_reason_instantiation;
	public static String CompletionProposalComputerDescriptor_reason_runtime_ex;
	public static String CompletionProposalComputerDescriptor_blame_message;
	public static String CompletionProposalComputerRegistry_invalid_message;
	public static String CompletionProposalComputerRegistry_error_dialog_title;
	public static String ContentAssistProcessor_defaultProposalCategory;
	public static String ContentAssistProcessor_toggle_affordance_press_gesture;
	public static String ContentAssistProcessor_toggle_affordance_click_gesture;
	public static String ContentAssistProcessor_toggle_affordance_update_message;
	public static String ContentAssistProcessor_empty_message;
	public static String EGLEditor_codeassist_noCompletions;
}
