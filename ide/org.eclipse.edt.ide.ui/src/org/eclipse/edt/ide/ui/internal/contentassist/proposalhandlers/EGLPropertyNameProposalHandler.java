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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.internal.EGLNewPropertiesHandler;
import org.eclipse.edt.compiler.internal.EGLPropertyRule;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.util.CapabilityFilterUtility;
import org.eclipse.jface.text.ITextViewer;

public class EGLPropertyNameProposalHandler extends EGLAbstractProposalHandler {
	
	private boolean isAnnotationSetting;
	private boolean startFromAnnoHead;

	public EGLPropertyNameProposalHandler(ITextViewer viewer, int documentOffset, String prefix, boolean isAnnotationSetting, boolean startFromAnnoIcon) {
		super(viewer, documentOffset, prefix, null);
		this.isAnnotationSetting = isAnnotationSetting;
		this.startFromAnnoHead = startFromAnnoIcon;
	}

	public List getProposals(int location, List propertyBlockList) {
		Collection propertyRules = CapabilityFilterUtility.filterPropertyRules(EGLNewPropertiesHandler.getPropertyRules(location));
		return getProposals(location, propertyRules, propertyBlockList);
	}
	
	public List getProposals(Collection propertyRules, List propertyBlockList) {
		return getProposals(0, propertyRules, propertyBlockList);
	}
	
	public List getProposals(int location, Collection propertyRules, List propertyBlockList) {
		List proposals = new ArrayList();		
		if (propertyRules != null) {
			for(Iterator iter = propertyRules.iterator(); iter.hasNext();) {
				EGLPropertyRule propertyRule = (EGLPropertyRule) iter.next();
				String propertyName = propertyRule.getName();
				if (propertyName.toUpperCase().startsWith(getPrefix().toUpperCase())) {
					if (!containsProperty(propertyName, propertyBlockList)) {
						proposals.add(createProposal(location, propertyRule));
					}
				}
			}
		}
		return proposals;
	}

	private EGLCompletionProposal createProposal(int location, EGLPropertyRule propertyRule) {
		int cursorPosition = 0;
		int selectionLength = 0;
		
		StringBuffer buffer = new StringBuffer();
		if ( isAnnotationSetting && !this.startFromAnnoHead){
			buffer.append("@"); //$NON-NLS-1$
		}
			
		buffer.append(propertyRule.getName());		
		if (propertyRule.hasType(EGLNewPropertiesHandler.nestedValue)) {
			if(!propertyRule.isValueless()){
				buffer.append(" {"); //$NON-NLS-1$
				cursorPosition = buffer.length();
				buffer.append("}"); //$NON-NLS-1$
			}
		}
		
		String img_src = isAnnotationSetting ? PluginImages.IMG_OBJS_ANNOTATION : PluginImages.IMG_OBJS_ENV_VAR;
		return
			new EGLCompletionProposal(viewer,
				propertyRule.getName(),
				buffer.toString(),
				getAdditionalInfo(location, propertyRule.getName()),
				getDocumentOffset() - getPrefix().length(),
				getPrefix().length(),
				cursorPosition,
				EGLCompletionProposal.RELEVANCE_MEDIUM,
				selectionLength,
				img_src);
	}

	/**
	 * @param property
	 * @return
	 */
	private String getAdditionalInfo(int location, String propertyName) {
		if (location == EGLNewPropertiesHandler.locationUseDeclaration) {
			//give unique additional information to tell customer which part
			//types allow each program property
			if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_HELPGROUP))
				return UINlsStrings.CAProposal_PropertyNameFormGroups;
			else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_VALIDATIONBYPASSKEYS))
				return UINlsStrings.CAProposal_PropertyNameFormGroups;
			else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_HELPKEY))
				return UINlsStrings.CAProposal_PropertyNameFormGroups;
			else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_PFKEYEQUATE))
				return UINlsStrings.CAProposal_PropertyNameFormGroups;
			else if (propertyName.equalsIgnoreCase(IEGLConstants.PROPERTY_DELETEAFTERUSE))
				return UINlsStrings.CAProposal_PropertyNameDataTables;
			else
				return UINlsStrings.CAProposal_PropertyName;
		}
		else
			return UINlsStrings.CAProposal_PropertyName;
	}
}
