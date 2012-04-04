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
package org.eclipse.edt.ide.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.ui.internal.preferences.ColorProvider;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;

public class MultilineCommentsScanner extends AbstractCodeScanner {

	private ColorProvider colorProvider = null;
	
	public MultilineCommentsScanner(ColorProvider provider){
		super();
		colorProvider = provider;
		setRules();
	}
	
	public void setRules(){
		List rules = new ArrayList();
		
		TextAttribute attr = colorProvider.getTextAttribute(ColorProvider.MULTI_LINE_COMMENT);
		Token currentToken = new Token(attr);
		rules.add(new MultiLineRule(CodeConstants.EGL_MULTI_LINE_COMMENT_START, CodeConstants.EGL_MULTI_LINE_COMMENT_END, currentToken));
		
		currentToken = new Token(attr);
		setDefaultReturnToken(currentToken);
		
		result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}
