/*******************************************************************************
 * Copyright © 2004, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.templates;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.internal.errors.TokenStream;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLTemplateProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.swt.graphics.Point;

public class TemplateEngine {

	/** The result proposals. */
	private ArrayList fProposals = new ArrayList();

	/** The context type. */
	private CoreContextType fContextType;

	/**
	 * Creates the template engine
	 */
	public TemplateEngine() {
		fContextType = new CoreContextType();
	}

	/**
	 * Empties the collector.
	 */
	public void reset() {
		fProposals.clear();
	}

	/**
	 * Returns the array of matching templates.
	 */
	public EGLTemplateProposal[] getResults() {
		return (EGLTemplateProposal[]) fProposals.toArray(new EGLTemplateProposal[fProposals.size()]);
	}

	public void complete(TokenStream tokenStream, ITextViewer viewer, int completionPosition, ParseStack parseStack, String prefix) {
		IDocument document = viewer.getDocument();
		String lineDelimiter = "\n";
		try {
			lineDelimiter =document.getLineDelimiter(0);
		} catch (BadLocationException e) {
		}
		
		Point selection = viewer.getSelectedRange();
		EGLTemplateContext context = fContextType.createContext(document, completionPosition - prefix.length(), selection.y + prefix.length(), parseStack, prefix);
		int start = context.getStart();
		int end = context.getEnd();
		IRegion region = new Region(start, end - start);
		Template[] templates = EDTUIPlugin.getDefault().getTemplateStore().getTemplates();
		for (int i = 0; i != templates.length; i++) {
			// If the template matches the prefix, then set up the necessary info and add to list of completions.
			if (context.canEvaluate(templates[i])) {
				fProposals.add(
					new EGLTemplateProposal(
						templates[i],
						context,
						region,
						PluginImages.get(PluginImages.IMG_OBJS_TEMPLATE),lineDelimiter));
			}
		}
	}

	// Get a template from the customized templates file (if it exists) that
	// matches the specified template name and template description
	public static String getCustomizedTemplateString(String name, String id) {
		
		//Template[] templates = EGLUIPlugin.getDefault().getTemplateStore().getTemplates();
		TemplatePersistenceData[] templateDatas = EDTUIPlugin.getDefault().getTemplateStore().getTemplateData(false);
		for (int i = 0; i != templateDatas.length; i++) {
			TemplatePersistenceData templatedata = templateDatas[i];
			
			// If the template matches the name and description, return the pattern
			if ((templatedata.getTemplate().getName().equals(name)) && (templatedata.getId().equals(id)))
				return templatedata.getTemplate().getPattern();
		}
		return "";	 //$NON-NLS-1$
			
		}

	// Get a template from the default templates file that
	// matches the specified template name and template description
	public static String getDefaultTemplateString(String name, String id) {
		
		//Template[] templates = EGLUIPlugin.getDefault().getDefaultTemplateStore().getTemplates();
		TemplatePersistenceData[] templateDatas = EDTUIPlugin.getDefault().getDefaultTemplateStore().getTemplateData(false);
		for (int i = 0; i != templateDatas.length; i++) {
			TemplatePersistenceData templatedata = templateDatas[i];
			
			// If the template matches the name and description, return the pattern
			if ((templatedata.getTemplate().getName().equals(name)) && (templatedata.getId().equals(id)))
				return templatedata.getTemplate().getPattern();
		}
		return "";	 //$NON-NLS-1$
			
		}
	
	public static String getCustomizedTemplateString(String name, String id, Map variables) {
		return replaceVariables(getCustomizedTemplateString(name, id), variables);
	}
	
	public static String getDefaultTemplateString(String name, String id, Map variables) {
		return replaceVariables(getDefaultTemplateString(name, id), variables);
	}
	
	// The method replaces all variables in the template string as delimited by ${} with its matching
	// value it found in the variables map
	public static String replaceVariables(String template, Map variables) {
		StringBuffer completed_template = new StringBuffer();
		for(int i=0; i<template.length();i++) {
			char c = template.charAt(i);
			if (c == '$') {
				// is this a variable
				if (template.charAt(i+1) == '{') {
					// opening delimiter detected
					// increment past the delimiter
					i = i + 2;
					// capture charaters until closing delimter detected
					StringBuffer variableKey = new StringBuffer();
					while (template.charAt(i) != '}') {
						variableKey.append(template.charAt(i++));
					}
					// get the replacement value
					String variableValue = (String)variables.get(variableKey.toString());
					if (variableValue != null)
						completed_template.append(variableValue);
					else {
						// if a value wasn't found for the variable, then put the 
						// variable back in
						completed_template.append("${"); //$NON-NLS-1$
						completed_template.append(variableKey.toString());
						completed_template.append("}"); //$NON-NLS-1$
					}
				}
				else
					// not a char we're interested in; just copy it
					completed_template.append(c);
			}
			else
				// not a char we're interested in; just copy it
				completed_template.append(c);
		}
		return completed_template.toString();
	}
}
