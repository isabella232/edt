/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.editor;

import java.util.Map;

import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.ide.ui.internal.CodeFormatterUtil;
import org.eclipse.edt.ide.ui.internal.formatting.ui.ProfileManager;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

public class EGLCodeFormatterUtil {

	/**
	 * formatting will be run on the entire document using the profile that's currently selected in the preference
	 * 
	 * @param document
	 * @param syntaxErrRequestor if there is any syntax error in the passing document, format will return null 
	 * 							caller can decicde what to do with the syntax errors
	 * 							pass in null if caller doesn't care to know about the syntax error
	 * 
	 * @return TextEdit, caller is responsible to decide what to do with the TextEdit, 
	 * 			whether or not to apply to the IDocument 
	 */
	public static TextEdit format(IDocument document, ISyntaxErrorRequestor syntaxErrRequestor){
		return format(document, 0, document.getLength(), null, syntaxErrRequestor);
	}
	
	/**
	 * allows partial formatting from offset for length long
	 * using the profile that's currently selected in the preference
	 * 
	 * @param document
	 * @param syntaxErrRequestor if there is any syntax error in the passing document, format will return null 
	 * 							caller can decicde what to do with the syntax errors
	 * 							pass in null if caller doesn't care to know about the syntax error
	 * 
	 * @return TextEdit, caller is responsible to decide what to do with the TextEdit, 
	 * 			whether or not to apply to the IDocument 
	 */	
	public static TextEdit format(IDocument document, int offset, int length, ISyntaxErrorRequestor syntaxErrRequestor){
		return format(document, offset, length, null, syntaxErrRequestor);
	}
	
	/**
	 * formatting will be run on the entire document based on the formatting preferences in the formatProfile
	 * - if the formatProfileName is null or empty string, the current selected profile will be used
	 * - else if the formatProfileName is not found, the default one (EGL built-in) will be used.
	 * returns a TextEdit, which caller has to decide whether or not to apply to the document
	 * 
	 * @param document
	 * @param formatProfileName - name attribute of the formatting profile name
	 * @param syntaxErrRequestor if there is any syntax error in the passing document, format will return null 
	 * 							caller can decicde what to do with the syntax errors
	 * 							pass in null if caller doesn't care to know about the syntax error
	 * 
	 * @return TextEdit, caller is responsible to decide what to do with the TextEdit, 
	 * 			whether or not to apply to the IDocument 
	 */
	public static TextEdit format(IDocument document, String formatProfileName, ISyntaxErrorRequestor syntaxErrRequestor){
		return format(document, 0, document.getLength(), formatProfileName, syntaxErrRequestor);
	}
	
	/**
	 * allows partial formatting from offset for length long
	 * formatting will be run on document based on the formatting preferences in the formatProfile
	 * - if the formatProfileName is null or empty string, the current selected profile will be used 
	 * - else if the formatProfileName is not found, the default one (EGL built-in) will be used.
	 * returns a TextEdit, which caller has to decide whether or not to apply to the document 
	 * 
	 * @param document
	 * @param offset
	 * @param length
	 * @param formatProfileName - name attribute of the formatting profile name
	 * @param syntaxErrRequestor if there is any syntax error in the passing document, format will return null 
	 * 							caller can decicde what to do with the syntax errors
	 * 							pass in null if caller doesn't care to know about the syntax error
	 * 
	 * @return TextEdit, caller is responsible to decide what to do with the TextEdit, 
	 * 			whether or not to apply to the IDocument 
	 */
	public static TextEdit format(IDocument document, int offset, int length, String formatProfileName, ISyntaxErrorRequestor syntaxErrRequestor){		
		ProfileManager profileMgr = ProfileManager.getInstance();				
		//save the original selected profile
		EObject originalSelProfile = profileMgr.getSelectedProfile();			
		
		Map options = CodeFormatterUtil.getFormattingOptionMapByProfileName(formatProfileName, profileMgr);	
		
		//run format on the document
		TextEdit formatResult = CodeFormatterUtil.format(document, offset, length, options, syntaxErrRequestor);
		
		//reset the current format profile selection back to its original
		profileMgr.setSelectedProfile(originalSelProfile);
		
		return formatResult;
	}

}
