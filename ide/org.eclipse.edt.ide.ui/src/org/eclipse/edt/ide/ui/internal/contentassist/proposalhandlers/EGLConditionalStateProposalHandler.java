/*******************************************************************************
 * Copyright ©2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.List;

import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;

public class EGLConditionalStateProposalHandler extends EGLAbstractProposalHandler {

	public static final int ALL_STATE = 0; //really all except for KEY_VALUE and SYS_VALUE
	public static final int ITEM_STATE = 1;
	public static final int SQL_ITEM_STATE = 2;
	public static final int TEXT_FIELD_STATE = 3;
	public static final int WEB_ITEM_STATE = 4;
	public static final int IO_ERROR_STATE = 5;
	public static final int MODIFIED_STATE = 6;
	public static final int KEY_VALUE = 7;
	public static final int SYS_VALUE = 8;

	public static final String[] ITEM_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_BLANKS,
		IEGLConstants.MNEMONIC_NUMERIC };
	public static final String[] SQL_ITEM_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_BLANKS,
		IEGLConstants.MNEMONIC_NUMERIC,
		IEGLConstants.MNEMONIC_TRUNC };
	public static final String[] TEXT_FIELD_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_BLANKS,
		IEGLConstants.MNEMONIC_CURSOR,
		IEGLConstants.MNEMONIC_DATA,
		IEGLConstants.MNEMONIC_MODIFIED,
		IEGLConstants.MNEMONIC_NUMERIC };
	public static final String[] WEB_ITEM_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_BLANKS,
		IEGLConstants.MNEMONIC_MODIFIED,
		IEGLConstants.MNEMONIC_NUMERIC };
	public static final String[] IO_ERROR_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_DEADLOCK,
		IEGLConstants.MNEMONIC_DUPLICATE,
		IEGLConstants.MNEMONIC_ENDOFFILE,
		IEGLConstants.MNEMONIC_FILENOTAVAILABLE,
		IEGLConstants.MNEMONIC_FILENOTFOUND,
		IEGLConstants.MNEMONIC_FULL,
		IEGLConstants.MNEMONIC_HARDIOERROR,
		IEGLConstants.MNEMONIC_INVALIDFORMAT,
		IEGLConstants.MNEMONIC_IOERROR,
		IEGLConstants.MNEMONIC_NORECORDFOUND,
		IEGLConstants.MNEMONIC_SOFTIOERROR,
		IEGLConstants.MNEMONIC_UNIQUE };
	public static final String[] MODIFIED_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_MODIFIED };
	public static final String[] KEY_VALUE1_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_BYPASS,
		IEGLConstants.MNEMONIC_ENTER,
		IEGLConstants.MNEMONIC_PAKEY,
		IEGLConstants.MNEMONIC_PFKEY };
	public static final String[] KEY_VALUE2_STATE_STRINGS = new String[] { "pan", "pfn" }; //$NON-NLS-1$ //$NON-NLS-2$

	private boolean isQualified;
	private IDataBinding targetBinding;
	
	public EGLConditionalStateProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IDataBinding targetBinding) {
		super(viewer, documentOffset, prefix);
		this.targetBinding = targetBinding;
	}

	public List getProposals() {
		String[] availableStrings;
		String additionalInfo = ""; //$NON-NLS-1$
		switch (getType()) {
			case ITEM_STATE :
				availableStrings = ITEM_STATE_STRINGS;
				additionalInfo = UINlsStrings.CAProposal_ItemState;
				break;

			case SQL_ITEM_STATE :
				availableStrings = SQL_ITEM_STATE_STRINGS;
				additionalInfo = UINlsStrings.CAProposal_SQLItemState;
				break;

			case TEXT_FIELD_STATE :
				availableStrings = TEXT_FIELD_STATE_STRINGS;
				additionalInfo = UINlsStrings.CAProposal_TextFieldState;
				break;

			case WEB_ITEM_STATE :
				availableStrings = WEB_ITEM_STATE_STRINGS;
				additionalInfo = UINlsStrings.CAProposal_WebItemState;
				break;

			case IO_ERROR_STATE :
				availableStrings = IO_ERROR_STATE_STRINGS;
				additionalInfo = UINlsStrings.CAProposal_IOErrorState;
				break;

			case MODIFIED_STATE :
				availableStrings = MODIFIED_STATE_STRINGS;
				additionalInfo = UINlsStrings.CAProposal_ModifiedState;
				break;

			case KEY_VALUE :
				availableStrings = KEY_VALUE1_STATE_STRINGS;
				additionalInfo = UINlsStrings.CAProposal_KeyValue;
				List proposals = getProposals(availableStrings, additionalInfo);
				availableStrings = KEY_VALUE2_STATE_STRINGS;
				proposals.addAll(
					getProposals(availableStrings, additionalInfo, EGLCompletionProposal.RELEVANCE_MEDIUM, 1, EGLCompletionProposal.NO_IMG_KEY));
				return proposals;

			case SYS_VALUE :
				//FIXME build part not in EDT, fix this
//				TreeSet systemStrings = EGLBuildPartModelContributions.getInstance().getSystemStrings();
//				systemStrings.add(IEGLConstants.MNEMONIC_DEBUG);
//				availableStrings = (String[]) systemStrings.toArray(new String[systemStrings.size()]);
//				additionalInfo = UINlsStrings.CAProposal_SystemValue;
//				availableStrings = new String[0];	
				
//				break;

				
			default :
				String[] strings = new String[] { //item states
					IEGLConstants.MNEMONIC_BLANKS,
					IEGLConstants.MNEMONIC_CURSOR,
					IEGLConstants.MNEMONIC_DATA,
					IEGLConstants.MNEMONIC_NUMERIC,
					IEGLConstants.MNEMONIC_TRUNC };
				additionalInfo = UINlsStrings.CAProposal_ItemState;
				proposals = getProposals(strings, additionalInfo, EGLCompletionProposal.RELEVANCE_ITEM_STATE, 0, EGLCompletionProposal.NO_IMG_KEY);

				strings = new String[] { //IO error states
					IEGLConstants.MNEMONIC_DEADLOCK,
					IEGLConstants.MNEMONIC_DUPLICATE,
					IEGLConstants.MNEMONIC_ENDOFFILE,
					IEGLConstants.MNEMONIC_FILENOTFOUND,
					IEGLConstants.MNEMONIC_FULL,
					IEGLConstants.MNEMONIC_HARDIOERROR,
					IEGLConstants.MNEMONIC_INVALIDFORMAT,
					IEGLConstants.MNEMONIC_IOERROR,
					IEGLConstants.MNEMONIC_NORECORDFOUND,
					IEGLConstants.MNEMONIC_SOFTIOERROR,
					IEGLConstants.MNEMONIC_UNIQUE };
				additionalInfo = UINlsStrings.CAProposal_IOErrorState;
				proposals.addAll(
					getProposals(strings, additionalInfo, EGLCompletionProposal.RELEVANCE_ITEM_STATE - 1, 0, EGLCompletionProposal.NO_IMG_KEY));
				return proposals;
		}
		return getProposals(availableStrings, additionalInfo);
	}

	private int getType() {
		if(targetBinding != null) {
			switch(targetBinding.getKind()) {
				case IDataBinding.FORM_BINDING:
					return MODIFIED_STATE;
				case IDataBinding.FORM_FIELD:
					return TEXT_FIELD_STATE;
				case IDataBinding.STRUCTURE_ITEM_BINDING:
				case IDataBinding.FLEXIBLE_RECORD_FIELD:
					return ITEM_STATE;
				case IDataBinding.FUNCTION_PARAMETER_BINDING:
					if(((FunctionParameterBinding) targetBinding).isField()) {
						return TEXT_FIELD_STATE;
					}
					break;
			}
			
			ITypeBinding typeBinding = targetBinding.getType();
			if(typeBinding != null && IBinding.NOT_FOUND_BINDING != typeBinding) {
				if(ITypeBinding.FIXED_RECORD_BINDING == typeBinding.getKind() ||
				   ITypeBinding.FLEXIBLE_RECORD_BINDING == typeBinding.getKind()) {
					return IO_ERROR_STATE;
				}
			}
		}
		
		return ALL_STATE;
	}
}
