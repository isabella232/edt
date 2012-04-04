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
package org.eclipse.edt.ide.ui.internal.formatting;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.core.ast.AccumulatingSyntaxErrorMessageRequestor;
import org.eclipse.edt.compiler.core.ast.SyntaxError;
import org.eclipse.edt.ide.ui.internal.CodeFormatterUtil;
import org.eclipse.edt.ide.ui.internal.codemanipulation.OrganizeImportsOperation.SyntaxErrorHelper;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.formatter.ContextBasedFormattingStrategy;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class FormattingStrategy extends ContextBasedFormattingStrategy {
	/** Documents to be formatted by this strategy */
	private final LinkedList fDocuments= new LinkedList();
	/** Partitions to be formatted by this strategy */
	private final LinkedList fPartitions= new LinkedList();
	
	private SyntaxErrorHelper fSyntaxError ;

	public FormattingStrategy(){
		super();
		fSyntaxError = null;
	}
	
	public void format() {
		super.format();
		
		final IDocument document = (IDocument)fDocuments.removeFirst();
		final TypedPosition partition = (TypedPosition)fPartitions.removeFirst();
		
		if(document != null && partition != null){
			Map partitioners = null;
			try{
				AccumulatingSyntaxErrorMessageRequestor syntaxErrRequestor = new AccumulatingSyntaxErrorMessageRequestor();
				final TextEdit edit = CodeFormatterUtil.format(document, partition.getOffset(), partition.getLength(), getPreferences(), syntaxErrRequestor);
				if(edit != null){
					if (edit.getChildrenSize() > 20)
						partitioners= TextUtilities.removeDocumentPartitioners(document);

					edit.apply(document);					
				}
				else if(syntaxErrRequestor != null){
					Map syntaxErrs = syntaxErrRequestor.getSyntaxErrors();
					if(!syntaxErrs.isEmpty()){
						Set keys = syntaxErrs.keySet();
						Iterator it = keys.iterator();
						SyntaxError SyntaxErr = (SyntaxError)it.next();
						String errmsg = (String)(syntaxErrs.get(SyntaxErr));
						fSyntaxError = new SyntaxErrorHelper(SyntaxErr, errmsg);
					}					
				}
			}
			catch (MalformedTreeException exception) {
				exception.printStackTrace();
			}
			catch(BadLocationException exception) {
//				 Can only happen on concurrent document modification - log and bail out
				exception.printStackTrace();
			}
			finally{
				if(partitioners != null)
					TextUtilities.addDocumentPartitioners(document, partitioners);
			}
		}
	}
	
	public void formatterStarts(IFormattingContext context) {
		super.formatterStarts(context);
		fPartitions.addLast(context.getProperty(FormattingContextProperties.CONTEXT_PARTITION));
		fDocuments.addLast(context.getProperty(FormattingContextProperties.CONTEXT_MEDIUM));		
	}
	
	public void formatterStops() {
		super.formatterStops();
		fPartitions.clear();
		fDocuments.clear();			
	}

	/**
	 * caller should call this after the format() has been ran
	 * 
	 * @return SyntaxErrorHelper, null means there is no syntax error
	 */
	public SyntaxErrorHelper get1stSyntaxErrorMsg(){
		return fSyntaxError;
	}
}
