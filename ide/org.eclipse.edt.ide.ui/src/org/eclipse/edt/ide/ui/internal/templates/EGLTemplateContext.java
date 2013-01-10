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

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.edt.compiler.core.ast.NodeTypes;
import org.eclipse.edt.ide.core.internal.errors.ErrorLexer;
import org.eclipse.edt.ide.core.internal.errors.IErrorLexer;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.CodeFormatterUtil;
import org.eclipse.edt.ide.ui.internal.Strings;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateTranslator;
import org.eclipse.jface.text.templates.TemplateVariable;

public class EGLTemplateContext extends DocumentTemplateContext {
	/** The platform default line delimiter. */
	private static final String PLATFORM_LINE_DELIMITER = System.getProperty("line.separator"); //$NON-NLS-1$

	//This is relatively expensive, so reuse it 
	private static IErrorLexer lexer = new ErrorLexer(new StringReader("")); //$NON-NLS-1$
	private ParseStack parseStack;
	private String prefix;
	/**
	 * @param contextType
	 */
	public EGLTemplateContext(TemplateContextType type, IDocument document, int completionOffset, int completionLength, ParseStack parseStack, String prefix) {
		super(type, document, completionOffset, completionLength);
		this.parseStack = parseStack;
		this.prefix = prefix;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.templates.TemplateContext#evaluate(org.eclipse.jface.text.templates.Template)
	 */
	public TemplateBuffer evaluate(Template template) throws BadLocationException, TemplateException {
		String lineDelimiter = null;
		int numtabs = 0;
		try {
			lineDelimiter = getDocument().getLineDelimiter(0);
			numtabs = getIndentation();
		} catch (BadLocationException e) {
		}

		if (lineDelimiter == null)
			lineDelimiter = PLATFORM_LINE_DELIMITER;

		TemplateTranslator translator = new TemplateTranslator() {
			protected TemplateVariable createVariable(String type, String name, int[] offsets) {
				return new MultiVariable(type, name, offsets);
			}
		};
		TemplateBuffer buffer = translator.translate(formatString(template.getPattern(), lineDelimiter, numtabs));
		getContextType().resolve(buffer, this);
		return buffer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.templates.TemplateContext#canEvaluate(org.eclipse.jface.text.templates.Template)
	 */
	public boolean canEvaluate(Template template) {

		//Only show template proposals if there is a prefix
		if (prefix == "") //$NON-NLS-1$
			return false;

		if (!template.getName().toUpperCase().startsWith(prefix.toUpperCase()))
			return false;
		
		TemplateTranslator translator = new TemplateTranslator() {
			protected TemplateVariable createVariable(String type, String name, int[] offsets) {
				return new MultiVariable(type, name, offsets);
			}
		};
		try {
			TemplateBuffer templateBuffer = translator.translate(template);
			lexer.yyreset(new StringReader(templateBuffer.getString()));
			int nodeType = lexer.yylex();
			//skip leading whitespace
			while (nodeType == NodeTypes.WS)
				nodeType = lexer.yylex();
			return isKeywordAllowed(nodeType, parseStack);
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
	}
		return true;
	}

	/**
	 * check if this keyword is allowed
	 */
	private boolean isKeywordAllowed(int type, ParseStack parseStack) {
		return parseStack.isTerminalShiftable(type);
	}

	/**
	 * Returns the indentation level at the position of code completion.
	 */
	private int getIndentation() {
		int start = getStart();
		IDocument document = getDocument();
		try {
			IRegion region = document.getLineInformationOfOffset(start);
			String lineContent = document.get(region.getOffset(), region.getLength());
			int tabWidth = CodeFormatterUtil.getTabWidth(null);
			return Strings.computeIndentUnits(lineContent, tabWidth, tabWidth);
		} catch (BadLocationException e) {
			EDTUIPlugin.log(e);
			return 0;
		}
	}

	private String formatString(String pattern, String lineDelimiter, int numtabs) {
		String result = ""; //$NON-NLS-1$

		char[] temp = pattern.toCharArray();

		for (int i = 0; i < temp.length; i++) {

			//custom templates have \r\n.  without this check, get extra carriage return
			if (temp[i] == '\r') {}
			else if (temp[i] != '\n') {
				result += temp[i];
			} else { // if char is '\n'
				result += lineDelimiter;

				for (int k = 0; k < numtabs; k++)
					result += '\t';

			}

		}
		return result;
	}

}
