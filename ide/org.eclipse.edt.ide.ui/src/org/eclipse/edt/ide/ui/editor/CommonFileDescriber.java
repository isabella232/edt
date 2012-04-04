/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

public class CommonFileDescriber implements ITextContentDescriber {
	private Pattern handlerPattern; 
	private Pattern widgetPattern;
	
	public CommonFileDescriber() {
		//to match handler XXX type RUIhandler
		handlerPattern = Pattern.compile("\\bHANDLER\\b\\s\\s*.*\\s*\\s\\bTYPE\\b\\s\\s*\\bRUIHANDLER", Pattern.CASE_INSENSITIVE);
		//to match handler XXX type RUIwidget
		widgetPattern = Pattern.compile("\\bHANDLER\\b\\s\\s*.*\\s*\\s\\bTYPE\\b\\s\\s*\\bRUIWIDGET", Pattern.CASE_INSENSITIVE);
	}
	
	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException {		
		BufferedReader reader = new BufferedReader(new InputStreamReader(contents, "UTF-8"));
		String line;
		while((line = reader.readLine()) != null){
			Matcher handlerMatcher = handlerPattern.matcher(line);
			Matcher widgetMatcher = widgetPattern.matcher(line);
			if(handlerMatcher.find() || widgetMatcher.find()){
				return IContentDescriber.INVALID;
			}
		}
		return IContentDescriber.VALID;
	}

	@Override
	public QualifiedName[] getSupportedOptions() {
		return new QualifiedName[0];
	}

	@Override
	public int describe(Reader contents, IContentDescription description) throws IOException {
		return IContentDescriber.INVALID;
	}
}
