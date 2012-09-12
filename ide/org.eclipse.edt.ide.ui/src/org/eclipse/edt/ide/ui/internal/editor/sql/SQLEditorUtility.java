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
package org.eclipse.edt.ide.ui.internal.editor.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.sql.util.SQLUtility;
import org.eclipse.edt.ide.core.internal.model.BinaryPart;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.dialogs.RecordPartSelectionDialog;
import org.eclipse.edt.ide.ui.internal.editor.EditorUtility;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.TextEditor;

public class SQLEditorUtility extends EditorUtility{
	public static boolean isRetrieveSQLValid(TextEditor editor) {
		Record record = getSQLRecord(editor);
		if (record != null)
			return true;
		
		return false;
	}
	
	public static Record getSQLRecord(TextEditor editor) {
		Node node = getCurrentNode(editor);
		// Search for a record.
		while (node != null && !(node instanceof Record)) {
			node = node.getParent();
		}
		if (node != null && node instanceof Record) {
			Record record = (Record) node;
			if (SQLUtility.isSQLRecordPart(record)) {
				return record;
			}
		}
		return null;
	}
	
	public static List getSQLRecords(TextEditor editor, String name, int matchMode, IEGLSearchScope scope) {
		// Returns a list of PartDeclarationInfo that are SQL records.
		List<PartDeclarationInfo> sqlParts = new ArrayList<PartDeclarationInfo>();
		if (editor != null && scope != null && name != null) {
			List parts = getRecordParts(editor, name, matchMode, scope);
			Iterator partItr = parts.iterator();
			PartDeclarationInfo partInfo;
			IPart part;
			while (partItr.hasNext()) {
				partInfo = (PartDeclarationInfo) partItr.next();
				part = resolvePart(partInfo, scope);
				if (part != null && part instanceof SourcePart && ((SourcePart) part).isSQLRecord()) {
					sqlParts.add(partInfo);
				}
				else if (part != null && part instanceof BinaryPart && ((BinaryPart)part).isSQLRecord()) {
					sqlParts.add(partInfo);
				}
			}
		}
		return sqlParts;
	}
	
	public static boolean isPrepareStatementAllowed(TextEditor editor) {
		if (isWithinFunction(editor)) {
			Node node = getCurrentNode(editor);
			while (node != null) {
				if (node instanceof Statement) {
					Statement statementNode = (Statement) node;
					if (!statementNode.canIncludeOtherStatements())
						return false;
				}
				node = node.getParent();
			}
			if (node == null) {
				return true;
			}
		}
		return false;
	}

	public static IPart getSQLRecordPartFromSelectionDialog(TextEditor editor, IEGLSearchScope scope) {
		if (editor == null) {
			return null;
		}
		//No SQL record found or multiple parts found, ask user for which one to open
		IPart part = null;
		Shell parent = EDTUIPlugin.getActiveWorkbenchShell();
		RecordPartSelectionDialog dialog = new RecordPartSelectionDialog(parent, 
				new ProgressMonitorDialog(parent), 
				IEGLSearchConstants.RECORD, 
				scope, 
				getSQLRecords(editor, "", IIndexConstants.PREFIX_MATCH, scope)); //$NON-NLS-1$
		dialog.setMatchEmptyString(true);
		dialog.setTitle(UINlsStrings.SQLRecordPartDialogTitle);
		dialog.setMessage(
			    UINlsStrings.SQLRecordPartDialogMessagePart1
				+ SQLConstants.CRLF
				+ SQLConstants.CRLF
				+ UINlsStrings.SQLRecordPartDialogMessagePart2);
		int result = dialog.open();
		if (result == IDialogConstants.OK_ID) {
			Object[] parts = dialog.getResult();
			if (parts != null && parts.length > 0)
				part = (IPart) parts[0];
		}
		return part;
	}
	
	/**********************************************************************************
	* Check to see if the selected items in the outline view are valid
	* for a Create Data Item action from the context menu
	* 
	* @return a boolean to determine whether to enable the action in the context menu
	**********************************************************************************/
	public static boolean isCreateDataItemValid(IStructuredSelection selection) {
		int i = 0;
		String recordName = ""; //$NON-NLS-1$
		boolean validSelection = false;
		Record record = null;

		for (Iterator iter = selection.iterator(); iter.hasNext();) {
			Object selectedElement = iter.next();
			//if items other than structure items are selected, create data item cannot be enabled
			if (selectedElement != null && selectedElement instanceof StructureItem) {
				StructureItem structureItem = (StructureItem) selectedElement;
				//need to make sure the container for the structure item is a sql record
				if (structureItem.getParent() instanceof Record) {
					record = (Record) structureItem.getParent();
					if (i == 0) {
						//check to make sure that structure item belongs to an sql record
						if (SQLUtility.isSQLRecordPart(record)) {
							recordName = record.getName().getCanonicalName();
							i++;
						} 
						else {
							return false;
						}
					}
					//if structure items are selected from different records, create data item cannot be enabled 
					if ((record.getName().getCanonicalName()).equals(recordName)) {
						//if one or more selected structure items are primitive, create data item will be enabled
						if (structureItem.getType().isPrimitiveType()) {
							validSelection = true;
						}
						else if (structureItem.getType().isArrayType()) {
							if (((ArrayType) structureItem.getType()).getBaseType().isPrimitiveType()) {
								validSelection = true;
							}
						}
					} 
					else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return validSelection;
	}
}
