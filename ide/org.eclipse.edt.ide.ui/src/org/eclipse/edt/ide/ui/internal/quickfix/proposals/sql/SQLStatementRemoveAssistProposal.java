/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql;

import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLIOStatementUtility;
import org.eclipse.edt.ide.ui.internal.quickfix.AssistContext;
import org.eclipse.edt.ide.ui.internal.quickfix.IInvocationContext;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.AbstractSQLStatementProposal;
import org.eclipse.swt.graphics.Image;

public class SQLStatementRemoveAssistProposal extends AbstractSQLStatementProposal {
    private IInvocationContext fContext;
	
	public SQLStatementRemoveAssistProposal(String label,int relevance, Image image, IInvocationContext context) {
		super(label, context.getEGLFile(), relevance, image, context.getDocument());
		fContext = context;
		if(fContext instanceof AssistContext) {
			editor = (EGLEditor)((AssistContext)fContext).getEditor();
		}
	}
	
	@Override
	protected ASTRewrite getRewrite() {		
		try {
			ASTRewrite rewrite = ASTRewrite.create(fContext.getFileAST());
			
			Statement sqlNode = AbstractSQLStatementProposal.SQLStatementFinder(fContext);
			IEGLDocument document = fContext.getDocument();
			
			info = SQLIOStatementUtility.getAddSQLIoStatementActionInfo(document, sqlNode); 	
			initialize();
			
			if (info.getSqlStatementNode() != null && info.getIntoClauseNode() == null) {
				rewrite.removeNode(info.getSqlStatementNode());
			}
			
			if (info.getIntoClauseNode() != null && info.getSqlStatementNode() == null) {
				rewrite.removeNode(info.getIntoClauseNode());
			}
			
			if (info.getIntoClauseNode() != null && info.getSqlStatementNode() != null) {
				rewrite.removeNode(info.getIntoClauseNode());
				rewrite.removeNode(info.getSqlStatementNode());
			}
			
			rewrite.completeIOStatement( sqlNode, "" );
			return(rewrite);
		} catch (Exception e) {
			EDTUIPlugin.log(new Status(Status.ERROR, EDTUIPlugin.PLUGIN_ID, "Complete function: Error complete function", e));
		} 
		
		return(null);
	}
}
