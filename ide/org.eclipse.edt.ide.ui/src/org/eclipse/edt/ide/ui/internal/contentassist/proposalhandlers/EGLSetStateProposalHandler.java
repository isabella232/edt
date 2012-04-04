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
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;

public class EGLSetStateProposalHandler extends EGLAbstractProposalHandler {
	public static final int NONE_STATE = -1;
	public static final int ALL_STATE = 0;
	public static final int ITEM_STATE = 1;
	public static final int RECORD_ITEM_STATE = 2;
	public static final int RECORD_STATE = 3;
	public static final int INDEXED_STATE = 4;
	public static final int PRINT_FORM_STATE = 5;
	public static final int TEXT_FORM_STATE = 6;
	public static final int TEXT_FIELD_STATE = 7;
	public static final int RECORD_ARRAY_STATE = 8;
	public static final int DLI_RECORD_STATE = 9;

	public static final String[] NONE_STATE_STRINGS = new String[] {};

	public static final String[] ITEM_STATE_STRINGS = new String[] {};

	public static final String[] RECORD_ITEM_STATE_STRINGS = new String[] {};

	public static final String[] RECORD_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_INITIAL,
		IEGLConstants.MNEMONIC_EMPTY};

	public static final String[] INDEXED_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_EMPTY,
		IEGLConstants.MNEMONIC_INITIAL,
		IEGLConstants.MNEMONIC_POSITION};

	public static final String[] DLI_RECORD_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_EMPTY,
		IEGLConstants.MNEMONIC_INITIAL,
		IEGLConstants.MNEMONIC_POSITION};

	public static final String[] PRINT_FORM_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_EMPTY,
		IEGLConstants.MNEMONIC_INITIAL,
		IEGLConstants.MNEMONIC_INITIALATTRIBUTES };

	public static final String[] TEXT_FORM_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_ALARM,
		IEGLConstants.MNEMONIC_EMPTY,
		IEGLConstants.MNEMONIC_INITIAL,
		IEGLConstants.MNEMONIC_INITIALATTRIBUTES };

	public static final String[] TEXT_FIELD_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_CURSOR,
		IEGLConstants.MNEMONIC_EMPTY,
		IEGLConstants.MNEMONIC_FULL,
		IEGLConstants.MNEMONIC_INITIAL,
		IEGLConstants.MNEMONIC_INITIALATTRIBUTES,
		IEGLConstants.MNEMONIC_MODIFIED,
		IEGLConstants.MNEMONIC_NORMAL,

		//colors
		IEGLConstants.MNEMONIC_BLACK,
		IEGLConstants.MNEMONIC_BLUE,
		IEGLConstants.MNEMONIC_CYAN,
		IEGLConstants.MNEMONIC_DEFAULTCOLOR,
		IEGLConstants.MNEMONIC_GREEN,
		IEGLConstants.MNEMONIC_MAGENTA,
		IEGLConstants.MNEMONIC_RED,
		IEGLConstants.MNEMONIC_WHITE,
		IEGLConstants.MNEMONIC_YELLOW,

		//extended highlighting
		IEGLConstants.MNEMONIC_BLINK,
		IEGLConstants.MNEMONIC_NOHIGHLIGHT,
		IEGLConstants.MNEMONIC_REVERSE,
		IEGLConstants.MNEMONIC_UNDERLINE,

		//intensity
		IEGLConstants.MNEMONIC_BOLD,
		IEGLConstants.MNEMONIC_DIM,
		IEGLConstants.MNEMONIC_INVISIBLE,
		IEGLConstants.MNEMONIC_MASKED,
		IEGLConstants.MNEMONIC_NORMALINTENSITY,

		//protection
		IEGLConstants.MNEMONIC_PROTECT,
		IEGLConstants.MNEMONIC_SKIP,
		IEGLConstants.MNEMONIC_UNPROTECT };

	public static final String[] RECORD_ARRAY_STATE_STRINGS = new String[] {
		IEGLConstants.MNEMONIC_EMPTY};

	public EGLSetStateProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		super(viewer, documentOffset, prefix);
	}

	public List getProposals(IDataBinding targetBinding, IPartBinding enclosingPart, List currentStates) {
		List availableStrings = new ArrayList();
		String additionalInfo = ""; //$NON-NLS-1$
		currentStates = toLowerCase(currentStates);
		switch (getType(targetBinding)) {
			case NONE_STATE :
				availableStrings = getAvailableStates(NONE_STATE_STRINGS, currentStates);
				additionalInfo = UINlsStrings.CAProposal_ItemState;
				break;

			case ITEM_STATE :
				availableStrings = getAvailableStates(ITEM_STATE_STRINGS, currentStates);
				additionalInfo = UINlsStrings.CAProposal_ItemState;
				break;

			case RECORD_ITEM_STATE :
				if(IDataBinding.STRUCTURE_ITEM_BINDING == targetBinding.getKind() && !((StructureItemBinding) targetBinding).getChildren().isEmpty()) {
					availableStrings = getAvailableStates(new String[] {IEGLConstants.MNEMONIC_EMPTY}, currentStates);
				}
				else {
					availableStrings = getAvailableStates(RECORD_ITEM_STATE_STRINGS, currentStates);
				}
				additionalInfo = UINlsStrings.CAProposal_RecordItemState;
				break;

			case RECORD_STATE :
				availableStrings = getAvailableStates(RECORD_STATE_STRINGS, currentStates);
				additionalInfo = UINlsStrings.CAProposal_RecordState;
				break;

			case INDEXED_STATE :
				availableStrings = getAvailableStates(INDEXED_STATE_STRINGS, currentStates);
				additionalInfo = UINlsStrings.CAProposal_IndexedState;
				break;

			case PRINT_FORM_STATE :
				availableStrings = getAvailableStates(PRINT_FORM_STATE_STRINGS, currentStates);
				additionalInfo = UINlsStrings.CAProposal_PrintFormState;
				break;

			case TEXT_FORM_STATE :
				availableStrings = getAvailableStates(TEXT_FORM_STATE_STRINGS, currentStates);
				additionalInfo = UINlsStrings.CAProposal_TextFormState;
				break;

			case TEXT_FIELD_STATE :
				availableStrings = getAvailableStates(TEXT_FIELD_STATE_STRINGS, currentStates);
				additionalInfo = UINlsStrings.CAProposal_TextFieldState;
				break;

			case RECORD_ARRAY_STATE :
				availableStrings = getAvailableStates(RECORD_ARRAY_STATE_STRINGS, currentStates);
				additionalInfo = UINlsStrings.CAProposal_RecordArrayState;
				break;

			case DLI_RECORD_STATE :
				availableStrings = getAvailableStates(DLI_RECORD_STATE_STRINGS, currentStates);
				additionalInfo = UINlsStrings.CAProposal_DLISegmentRecordState;
				break;

			default :
				String[] strings = getStrings(new String[] { //text field states
					IEGLConstants.MNEMONIC_ALARM,
					IEGLConstants.MNEMONIC_CURSOR,
					IEGLConstants.MNEMONIC_EMPTY,
					IEGLConstants.MNEMONIC_FULL,
					IEGLConstants.MNEMONIC_INITIAL,
					IEGLConstants.MNEMONIC_INITIALATTRIBUTES,
					IEGLConstants.MNEMONIC_MODIFIED,
					IEGLConstants.MNEMONIC_POSITION }, currentStates);
				additionalInfo = UINlsStrings.CAProposal_SetState;
				List proposals =
					getProposals(
						strings,
						additionalInfo,
						EGLCompletionProposal.RELEVANCE_ITEM_STATE,
						0,
						EGLCompletionProposal.NO_IMG_KEY);

				strings = getStrings(new String[] { //colors
					IEGLConstants.MNEMONIC_BLACK,
					IEGLConstants.MNEMONIC_BLUE,
					IEGLConstants.MNEMONIC_CYAN,
					IEGLConstants.MNEMONIC_DEFAULTCOLOR,
					IEGLConstants.MNEMONIC_GREEN,
					IEGLConstants.MNEMONIC_MAGENTA,
					IEGLConstants.MNEMONIC_RED,
					IEGLConstants.MNEMONIC_WHITE,
					IEGLConstants.MNEMONIC_YELLOW }, currentStates);
				additionalInfo = UINlsStrings.CAProposal_ColorState;
				proposals.addAll(
					getProposals(
						strings,
						additionalInfo,
						EGLCompletionProposal.RELEVANCE_ITEM_STATE,
						0,
						EGLCompletionProposal.NO_IMG_KEY));

				strings = getStrings(new String[] { //extended highlighting
					IEGLConstants.MNEMONIC_BLINK,
					IEGLConstants.MNEMONIC_NOHIGHLIGHT,
					IEGLConstants.MNEMONIC_REVERSE,
					IEGLConstants.MNEMONIC_UNDERLINE }, currentStates);
				additionalInfo = UINlsStrings.CAProposal_HighlightingState;
				proposals.addAll(
					getProposals(
						strings,
						additionalInfo,
						EGLCompletionProposal.RELEVANCE_ITEM_STATE,
						0,
						EGLCompletionProposal.NO_IMG_KEY));

				strings = getStrings(new String[] { //intensity
					IEGLConstants.MNEMONIC_BOLD,
					IEGLConstants.MNEMONIC_INVISIBLE,
					IEGLConstants.MNEMONIC_DIM,
					IEGLConstants.MNEMONIC_MASKED,
					IEGLConstants.MNEMONIC_NORMALINTENSITY }, currentStates);
				additionalInfo = UINlsStrings.CAProposal_IntensityState;
				proposals.addAll(
					getProposals(
						strings,
						additionalInfo,
						EGLCompletionProposal.RELEVANCE_ITEM_STATE,
						0, 
						EGLCompletionProposal.NO_IMG_KEY));

				strings = getStrings(new String[] { //protection
					IEGLConstants.MNEMONIC_PROTECT,
					IEGLConstants.MNEMONIC_SKIP,
					IEGLConstants.MNEMONIC_UNPROTECT }, currentStates);
				additionalInfo = UINlsStrings.CAProposal_ProtectionState;
				proposals.addAll(
					getProposals(
						strings,
						additionalInfo,
						EGLCompletionProposal.RELEVANCE_ITEM_STATE,
						0,
						EGLCompletionProposal.NO_IMG_KEY));

				return proposals;
		}
		return getProposals((String[]) availableStrings.toArray(new String[availableStrings.size()]), additionalInfo);
	}

	private String[] getStrings(String[] strings, List currentStates) {
		List result = new ArrayList();
		for(int i = 0; i < strings.length; i++) {
			if(!currentStates.contains(strings[i].toLowerCase())) {
				result.add(strings[i]);
			}
		}
		return (String[]) result.toArray(new String[0]);
	}

	private List toLowerCase(List currentStates) {
		List result = new ArrayList();
		for(Iterator iter = currentStates.iterator(); iter.hasNext();) {
			result.add(((String) iter.next()).toLowerCase());
		}
		return result;
	}

	/**
	 * @param ITEM_STATE_STRINGS
	 * @param currentStates
	 * @return
	 */
	private List getAvailableStates(String[] allStrings, List currentStates) {
		List available = new ArrayList();
		for (int i = 0; i < allStrings.length; i++) {
			String string = allStrings[i];
			if (currentStates.contains(string.toLowerCase()))
				continue;
			else
			available.add(string);
		}
		return available;
	}

	/**
	 * 
	 */
	private int getType(IDataBinding targetBinding) {
		if(targetBinding == null || IBinding.NOT_FOUND_BINDING == targetBinding) {
			return ALL_STATE;
		}
		
		switch(targetBinding.getKind()) {
			case IDataBinding.FORM_FIELD:
				return TEXT_FIELD_STATE;
				
			case IDataBinding.STRUCTURE_ITEM_BINDING:
			case IDataBinding.FLEXIBLE_RECORD_FIELD:
				return RECORD_ITEM_STATE;
				
			default:
				ITypeBinding typeBinding = targetBinding.getType();
				if(typeBinding == null || IBinding.NOT_FOUND_BINDING == typeBinding || typeBinding.isDynamic()) {
					return ALL_STATE;
				}
				
				switch(typeBinding.getKind()) {
					case ITypeBinding.FIXED_RECORD_BINDING:
					case ITypeBinding.FLEXIBLE_RECORD_BINDING:
						return RECORD_STATE;
						
					case ITypeBinding.DATATABLE_BINDING:
						return NONE_STATE;
						
					case ITypeBinding.PRIMITIVE_TYPE_BINDING:
						return ITEM_STATE;
						
					case ITypeBinding.ARRAY_TYPE_BINDING:
						int elementKind = ((ArrayTypeBinding) typeBinding).getElementType().getKind();
						if(ITypeBinding.FIXED_RECORD_BINDING == elementKind ||
						   ITypeBinding.FLEXIBLE_RECORD_BINDING == elementKind) {
							return RECORD_ARRAY_STATE;
						}
						return NONE_STATE;
						
					default:
						return ALL_STATE;
				}			
		}		
	}
}
