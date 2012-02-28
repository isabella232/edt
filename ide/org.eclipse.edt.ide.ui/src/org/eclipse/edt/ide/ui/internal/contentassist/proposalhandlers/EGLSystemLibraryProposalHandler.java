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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.util.CapabilityFilterUtility;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class EGLSystemLibraryProposalHandler extends EGLAbstractProposalHandler {

	public EGLSystemLibraryProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor) {
		super(viewer, documentOffset, prefix, editor);
	}

	private static Map libraryNamesToStringConstants;
	
	public List getProposals() {
		List proposals = new ArrayList();
		List primitiveStrings = new ArrayList();
		
		IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
		ISystemEnvironment env = SystemEnvironmentManager.findSystemEnvironment(editorInput.getFile().getProject(), null); 
		List contentAssistPartList = new LinkedList();
		contentAssistPartList.addAll(env.getSystemLibraryManager().getLibraries().values());
		contentAssistPartList.addAll(env.getExternalTypePartsManager().getExternalTypeLibraries().values());
		
		for(Iterator iter = CapabilityFilterUtility.filterParts(contentAssistPartList).iterator(); iter.hasNext();) {
			IPartBinding nextLibrary = (IPartBinding) iter.next();
			String systemLibraryName = (String) getLibraryNamesToStringConstants().get(nextLibrary.getName().toLowerCase());
			if (systemLibraryName == null)
				systemLibraryName = UINlsStrings.CAProposal_EGLSystemLibrary;  //default value
			primitiveStrings.add(new String[] {nextLibrary.getCaseSensitiveName(), systemLibraryName});
		}
		
		for (int i = 0; i < primitiveStrings.size(); i++) {
			String primitiveString[] = (String[]) primitiveStrings.get(i);
			if (primitiveString[0].toUpperCase().startsWith(getPrefix().toUpperCase())) {
				proposals.add(
					new EGLCompletionProposal(
						viewer,
						primitiveString[0],
						primitiveString[0] + ".", //$NON-NLS-1$
						primitiveString[1],
						getDocumentOffset() - getPrefix().length(),
						getPrefix().length(),
						primitiveString[0].length() + 1,
						EGLCompletionProposal.RELEVANCE_SYSTEM_LIBRARY,
						PluginImages.IMG_OBJS_LIBRARY));
			}
		}
		return proposals;
	}

	public static Map getLibraryNamesToStringConstants() {
		if (libraryNamesToStringConstants == null) {
			libraryNamesToStringConstants = new HashMap();
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_DATETIMELIB.toLowerCase(), UINlsStrings.CAProposal_DataTimeLibrary);
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_MATHLIB.toLowerCase(), UINlsStrings.CAProposal_MathLibrary);
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_SQLLIB.toLowerCase(), UINlsStrings.CAProposal_SQLLibrary);
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_STRLIB.toLowerCase(), UINlsStrings.CAProposal_StringLibrary);
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_SYSLIB.toLowerCase(), UINlsStrings.CAProposal_SystemLibrary);
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_SYSVAR.toLowerCase(), UINlsStrings.CAProposal_SystemVarLibrary);
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_SERVICELIB.toLowerCase(), UINlsStrings.CAProposal_ServiceLibrary);
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_HTTPLIB.toLowerCase(), UINlsStrings.CAProposal_HttpLibrary);
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_STRINGLIB.toLowerCase(), UINlsStrings.CAProposal_StringLibrary);
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_RUILIB.toLowerCase(), UINlsStrings.CAProposal_RuiLibrary);
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_JSONLIB.toLowerCase(), UINlsStrings.CAProposal_JsonLibrary);
			libraryNamesToStringConstants.put(IEGLConstants.KEYWORD_XMLLib.toLowerCase(), UINlsStrings.CAProposal_XmlLibrary);
		}
		return libraryNamesToStringConstants;
	}
}
