/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.document.utils;

import java.util.List;

import org.eclipse.core.resources.IFile;

import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;

public class DocumentUtil {

	public static void addQualifiedImports(IFile currentFile, IEGLDocument currentDocument, List imports){
		for ( int i = 0; i < imports.size(); i ++ ) {
			String fullTypeName = (String)imports.get( i );
			AddImportOperation addImportOp = new AddImportOperation(currentDocument, currentFile);
			addImportOp.addFullyQualifiedImport(fullTypeName);
		}
	}
	
	public static String handleTypeNameVaraibles(IFile currentFile, IEGLDocument currentDocument, String template, List imports){
		int startIndex = 0;
		while(true){
			startIndex = template.indexOf(IVEConstants.TYPE_NAME_VARIABLE);
			if(startIndex == -1){
				break;
			}else{
				String typeNameVaraible = template.substring(startIndex, template.indexOf("}", startIndex)+1);
				String fullTypeName = typeNameVaraible.substring(IVEConstants.TYPE_NAME_VARIABLE.length(), typeNameVaraible.length()-1).trim();
				int index = fullTypeName.lastIndexOf(".");
				String packageName = fullTypeName.substring(0, index);
				String typeName = fullTypeName.substring(index+1);
				TypeNameResolver typeNameResolver = new TypeNameResolver(currentFile);
				typeNameResolver.resolveTypeName(packageName, typeName);
				template = replaceAll(template, typeNameVaraible, typeNameResolver.getTypeName());
				if(typeNameResolver.requiresQualifiedImport()){
					imports.add( fullTypeName );
				}
			}
		}
		return template;
	}
	
	public static Node getWidgetNode( IEGLDocument currentDocument, int iStatementOffset, int iStatementLength ) {
		Node widgetNode = currentDocument.getNewModelNodeAtOffset(iStatementOffset, iStatementLength);
		if (  widgetNode instanceof ClassDataDeclaration ) {
			List names = ((ClassDataDeclaration)widgetNode).getNames();
			if ( names.size() == 1 && names.get(0) instanceof SimpleName ) {
				return (SimpleName)names.get(0);
			}
		}
		
		return widgetNode;
	}
	
	private static String replaceAll(String template, String oldString, String newString){
		int startIndex = 0;
		while(true){
			startIndex = template.indexOf(oldString);
			if(startIndex == -1){
				break;
			}else{
				String start = template.substring(0, startIndex);
				String end = template.substring(startIndex + oldString.length());
				template = start + newString + end;
			}
		}
		return template;
	}
}
