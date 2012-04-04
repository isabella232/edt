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
package org.eclipse.edt.ide.ui.internal;

import java.util.Map;

import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.ide.ui.editor.CodeFormatter;
import org.eclipse.edt.ide.ui.internal.formatting.EGLCodeFormatter;
import org.eclipse.edt.ide.ui.internal.formatting.ui.ProfileManager;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.TextEdit;

public class CodeFormatterUtil {
		
	/**
	 * Gets the current tab width.
	 * 
	 * @param project The project where the source is used, used for project
	 *        specific options or <code>null</code> if the project is unknown
	 *        and the workspace default should be used
	 * @return The tab width
	 */
	public static int getTabWidth(IJavaProject project) {
		/*
		 * If the tab-char is SPACE, FORMATTER_INDENTATION_SIZE is not used
		 * by the core formatter.
		 * We piggy back the visual tab length setting in that preference in
		 * that case.
		 */
		String key;
		if (JavaCore.SPACE.equals(getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR)))
			key= DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE;
		else
			key= DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE;
		
		return getCoreOption(project, key, 4);
	}

	/**
	 * Returns the current indent width.
	 * 
	 * @param project the project where the source is used or <code>null</code>
	 *        if the project is unknown and the workspace default should be used
	 * @return the indent width
	 * @since 3.1
	 */
	public static int getIndentWidth(IJavaProject project) {
		String key;
		if (DefaultCodeFormatterConstants.MIXED.equals(getCoreOption(project, DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR)))
			key= DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE;
		else
			key= DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE;
		
		return getCoreOption(project, key, 4);
	}

	/**
	 * Returns the possibly <code>project</code>-specific core preference
	 * defined under <code>key</code>.
	 * 
	 * @param project the project to get the preference from, or
	 *        <code>null</code> to get the global preference
	 * @param key the key of the preference
	 * @return the value of the preference
	 * @since 3.1
	 */
	private static String getCoreOption(IJavaProject project, String key) {
		if (project == null)
			return JavaCore.getOption(key);
		return project.getOption(key, true);
	}

	/**
	 * Returns the possibly <code>project</code>-specific core preference
	 * defined under <code>key</code>, or <code>def</code> if the value is
	 * not a integer.
	 * 
	 * @param project the project to get the preference from, or
	 *        <code>null</code> to get the global preference
	 * @param key the key of the preference
	 * @param def the default value
	 * @return the value of the preference
	 * @since 3.1
	 */
	private static int getCoreOption(IJavaProject project, String key, int def) {
		try {
			return Integer.parseInt(getCoreOption(project, key));
		} catch (NumberFormatException e) {
			return def;
		}
	}
		
	/**
	 * get the option map given a profile name, 
	 * if the profile is null or empty, use the current selected profile option map in the preferece
	 * else change the current selected profile to the given profile name, 
	 * 		so user should save returned original selected profile before calling this method 
	 * 	    then reset it back after it's done by calling profileMgr.setSelectedProfile(originalSelProfile);
	 * 
	 * @param formatProfileName
	 * @param profileMgr
	 * @return
	 */
	public static Map getFormattingOptionMapByProfileName(String formatProfileName, ProfileManager profileMgr){
		if(formatProfileName != null && formatProfileName.length()>0){			
			//select the passed in profile as the current profile
			EObject profile = profileMgr.getProfileByName(formatProfileName, true);
			profileMgr.setSelectedProfile(profile);
		}			

		return profileMgr.getCurrentPreferenceSettingMap();		
	}
	
	private static TextEdit format(IDocument document, int offset, int length, int indentationLevel, String lineSeprator, Map options, ISyntaxErrorRequestor syntaxErrRequestor){
		CodeFormatter eglCodeFormatter = new EGLCodeFormatter(options);
		return eglCodeFormatter.format(document, offset, length, indentationLevel, lineSeprator, syntaxErrRequestor);
	}
	
	public static TextEdit format(IDocument document, int offset, int length, Map options, ISyntaxErrorRequestor syntaxErrRequestor){
		int indentationLevel = 0;
		String lineSeprator = TextUtilities.getDefaultLineDelimiter(document);
		
		return format(document, offset, length, indentationLevel, lineSeprator, options, syntaxErrRequestor);
	}	
	
}
