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

import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.AssignmentStatement;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.CaseStatement;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.ContinueStatement;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ElseBlock;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.ExitStatement;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.ForwardStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.GotoStatement;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.IfStatement;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.MoveStatement;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.ReturnStatement;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SetStatement;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IWorkbenchPart;


public class EGLUIHelp {

	private static class EGLUIHelpContextProvider implements IContextProvider {
		private String fId;
		
		public EGLUIHelpContextProvider(String id) {
			this.fId = id;
		}
		
		public int getContextChangeMask() {
			return SELECTION;
		}
		
		public IContext getContext(Object target) {
			return HelpSystem.getContext(fId);
		}
		
		public String getSearchExpression(Object target) {
			return null;
		}
	}

	/**
	 * Creates and returns a help context provider for the given part.
	 * 
	 * @param part the part for which to create the help context provider
	 * @param contextId	the optional context ID used to retrieve static help
	 * @return the help context provider 
	 */
	public static IContextProvider getHelpContextProvider(IWorkbenchPart part, String contextId) {
		return new EGLUIHelpContextProvider(getNodeSelectionID(part, contextId));
	}
	
	private static String getNodeSelectionID(IWorkbenchPart part, String contextId) {
		if (part instanceof EGLEditor) {
			EGLEditor eglEditor = (EGLEditor) part; 
			ITextSelection textSelection = (ITextSelection) eglEditor.getSelectionProvider().getSelection();
			
			IEGLDocument document = (IEGLDocument) eglEditor.getDocumentProvider().getDocument(eglEditor.getEditorInput());
			Node node = document.getNewModelNodeAtOffset(textSelection.getOffset());
			while (node != null) {
				//declarations
				if (node instanceof PackageDeclaration)
					return IUIHelpConstants.EGL_EDITOR_PACKAGE_DECLARATION;
				if (node instanceof ImportDeclaration)
					return IUIHelpConstants.EGL_EDITOR_IMPORT_DECLARATION;
				if (node instanceof ClassDataDeclaration)
					return IUIHelpConstants.EGL_EDITOR_CLASS_DATA_DECLARATION;
				if (node instanceof FunctionDataDeclaration)
					return IUIHelpConstants.EGL_EDITOR_FUNCTION_DATA_DECLARATION;

				//part types
				if (node instanceof DataItem)
					return IUIHelpConstants.EGL_EDITOR_DATA_ITEM;
				if (node instanceof Delegate)
					return IUIHelpConstants.EGL_EDITOR_DELEGATE;
				if (node instanceof ExternalType)
					return IUIHelpConstants.EGL_EDITOR_EXTERNAL_TYPE;
				if (node instanceof Interface)
					return IUIHelpConstants.EGL_EDITOR_INTERFACE;
				if (node instanceof Library) {
					Library library = (Library) node;
					if (library.hasSubType()) {
						if(NameUtile.equals(library.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.LIBRARY_SUBTYPE_BASIC)))
							return IUIHelpConstants.EGL_EDITOR_LIBRARY_BASICLIBRARY;
						if(NameUtile.equals(library.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.LIBRARY_SUBTYPE_NATIVE)))
							return IUIHelpConstants.EGL_EDITOR_LIBRARY_NATIVELIBRARY;
					}
					return IUIHelpConstants.EGL_EDITOR_LIBRARY;
				}
				if (node instanceof Program) {
					Program program = (Program) node;
					if (program.hasSubType()) {
						if(NameUtile.equals(program.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.PROGRAM_SUBTYPE_BASIC)))
							return IUIHelpConstants.EGL_EDITOR_PROGRAM_BASICPROGRAM;
						if(NameUtile.equals(program.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.PROGRAM_SUBTYPE_TEXT_UI)))
							return IUIHelpConstants.EGL_EDITOR_PROGRAM_TEXTUI;
						if(NameUtile.equals(program.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.PROGRAM_SUBTYPE_VG_WEB_TRANSACTION)))
							return IUIHelpConstants.EGL_EDITOR_PROGRAM_VGWEBTRANSACTION;
					}
					return IUIHelpConstants.EGL_EDITOR_PROGRAM;
				}
				if (node instanceof Record) {
					Record record = (Record) node;
					if (record.hasSubType()) {
						if(NameUtile.equals(record.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.RECORD_SUBTYPE_BASIC)))
							return IUIHelpConstants.EGL_EDITOR_RECORD_BASICRECORD;
						if(NameUtile.equals(record.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.RECORD_SUBTYPE_EXCEPTION)))
							return IUIHelpConstants.EGL_EDITOR_RECORD_EXCEPTION;
						if(NameUtile.equals(record.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.RECORD_SUBTYPE_INDEXED)))
							return IUIHelpConstants.EGL_EDITOR_RECORD_INDEXRECORD;
						if(NameUtile.equals(record.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.RECORD_SUBTYPE_MQ)))
							return IUIHelpConstants.EGL_EDITOR_RECORD_MQRECORD;
						if(NameUtile.equals(record.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.RECORD_SUBTYPE_RELATIVE)))
							return IUIHelpConstants.EGL_EDITOR_RECORD_RELATIVERECORD;
						if(NameUtile.equals(record.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.RECORD_SUBTYPE_SERIAL)))
							return IUIHelpConstants.EGL_EDITOR_RECORD_SERIALRECORD;
						if(NameUtile.equals(record.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.RECORD_SUBTYPE_SQl)))
							return IUIHelpConstants.EGL_EDITOR_RECORD_SQLRECORD;
					}
					return IUIHelpConstants.EGL_EDITOR_RECORD;
				}
				if (node instanceof Handler) {
					Handler handler = (Handler) node;
					if (handler.hasSubType()) {
						if ( NameUtile.equals(handler.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.HANDLER_SUBTYPE_JSF)))
							return IUIHelpConstants.EGL_EDITOR_HANDLER_JSF;
						if ( NameUtile.equals(handler.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.HANDLER_SUBTYPE_JASPER)))
							return IUIHelpConstants.EGL_EDITOR_HANDLER_REPORT;
					}
					return IUIHelpConstants.EGL_EDITOR_HANDLER;
				}
				if (node instanceof Service)
					return IUIHelpConstants.EGL_EDITOR_SERVICE;

				//nested part types
				if (node instanceof NestedFunction)
					return IUIHelpConstants.EGL_EDITOR_NESTED_FUNCTION;
				
				//statements
				if (node instanceof AddStatement)
					return IUIHelpConstants.EGL_EDITOR_ADD_STATEMENT;
				if (node instanceof AssignmentStatement)
					return IUIHelpConstants.EGL_EDITOR_ASSIGNMENT_STATEMENT;
				if (node instanceof CallStatement)
					return IUIHelpConstants.EGL_EDITOR_CALL_STATEMENT;
				if (node instanceof CaseStatement)
					return IUIHelpConstants.EGL_EDITOR_CASE_STATEMENT;
				if (node instanceof CloseStatement)
					return IUIHelpConstants.EGL_EDITOR_CLOSE_STATEMENT;
				if (node instanceof ContinueStatement)
					return IUIHelpConstants.EGL_EDITOR_CONTINUE_STATEMENT;
				if (node instanceof DeleteStatement)
					return IUIHelpConstants.EGL_EDITOR_DELETE_STATEMENT;
				if (node instanceof ElseBlock)
					return IUIHelpConstants.EGL_EDITOR_ELSE_BLOCK;
				if (node instanceof ExecuteStatement)
					return IUIHelpConstants.EGL_EDITOR_EXECUTE_STATEMENT;
				if (node instanceof ExitStatement)
					return IUIHelpConstants.EGL_EDITOR_EXIT_STATEMENT;
				if (node instanceof ForStatement)
					return IUIHelpConstants.EGL_EDITOR_FOR_STATEMENT;
				if (node instanceof ForEachStatement)
					return IUIHelpConstants.EGL_EDITOR_FOREACH_STATEMENT;
				if (node instanceof ForwardStatement)
					return IUIHelpConstants.EGL_EDITOR_FORWARD_STATEMENT;
				if (node instanceof FunctionInvocation)
					return IUIHelpConstants.EGL_EDITOR_FUNCTION_INVOCATION_STATEMENT;
				if (node instanceof GetByKeyStatement)
					return IUIHelpConstants.EGL_EDITOR_GET_STATEMENT_BY_KEY;
				if (node instanceof GetByPositionStatement)
					return IUIHelpConstants.EGL_EDITOR_GET_STATEMENT_BY_POSITION;
				if (node instanceof GotoStatement)
					return IUIHelpConstants.EGL_EDITOR_GOTO_STATEMENT;
				if (node instanceof IfStatement)
					return IUIHelpConstants.EGL_EDITOR_IF_STATEMENT;
				if (node instanceof MoveStatement)
					return IUIHelpConstants.EGL_EDITOR_MOVE_STATEMENT;
				if (node instanceof OpenStatement)
					return IUIHelpConstants.EGL_EDITOR_OPEN_STATEMENT;
				if (node instanceof PrepareStatement)
					return IUIHelpConstants.EGL_EDITOR_PREPARE_STATEMENT;
				if (node instanceof ReplaceStatement)
					return IUIHelpConstants.EGL_EDITOR_REPLACE_STATEMENT;
				if (node instanceof ReturnStatement)
					return IUIHelpConstants.EGL_EDITOR_RETURN_STATEMENT;
				if (node instanceof SetStatement)
					return IUIHelpConstants.EGL_EDITOR_SET_STATEMENT;
				if (node instanceof TryStatement)
					return IUIHelpConstants.EGL_EDITOR_TRY_STATEMENT;
				if (node instanceof WhileStatement)
					return IUIHelpConstants.EGL_EDITOR_WHILE_STATEMENT;
				node = node.getParent();			
			}
		}
		return contextId;
	}
}
