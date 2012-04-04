/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.codemanipulation.ImportComparator;
import org.eclipse.jface.text.BadLocationException;

public class ImportUtility {
	private IEGLDocument document;
	private String packageName;
	private String partName;

	/*
	 * if necessary add the new import statement.  Only add the statement if it does
	 * not already exist as an exact match or a wildcard (asterisk) match
	 */	
	public static boolean addImportStatement(IEGLDocument document, String packageName, String partName) {
		return new ImportUtility(document, packageName, partName).addImportStatement();
	}

	/**
	 * constructor
	 */
	public ImportUtility(IEGLDocument document, String packageName, String partName) {
		super();
		this.document = document;
		this.packageName = packageName;
		this.partName = partName;
	}

	/*
	 * if necessary add the new import statement.  Only add the statement if it does
	 * not already exist as an exact match or a wildcard (asterisk) match
	 */	
	public boolean addImportStatement() {
		if (!containsImport())
			return addImport();
		return false;
	}

	/*
	 * check to see if the new import statement needs to be added or not
	 */
	private boolean containsImport() {
		//first check if new import statement is the same package as this files package
		//get the package name from the model
		File eglFile = document.getNewModelEGLFile();
		PackageDeclaration packageDeclaration = eglFile.getPackageDeclaration();
		String currentPackageName = getCurrentPackageName(packageDeclaration);
		
		//check if the package names are the same
		if (currentPackageName.equalsIgnoreCase(packageName))
			return true;
		
		List importStatements = eglFile.getImportDeclarations();
		String importSpecification = ""; //$NON-NLS-1$
		//Assuming importString is always a specific part import (not asterisk)
		for (Iterator iter = importStatements.iterator(); iter.hasNext();) {
			ImportDeclaration importStatement = (ImportDeclaration) iter.next();
			importSpecification = importStatement.getName().getCanonicalString();

			//first check for exact match
			if (importSpecification.equalsIgnoreCase(packageName + "." + partName)) //$NON-NLS-1$
				return true;
				
			//next check for a match for a generic import (import with asterisk)
			if (importStatement.isOnDemand() && importSpecification.equalsIgnoreCase(packageName))
				return true;
		}
		return false;
	}
	
	/**
	 * @param packageDeclaration
	 * @return
	 */
	private String getCurrentPackageName(PackageDeclaration packageDeclaration) {
		if (packageDeclaration != null) {
			return packageDeclaration.getName().getCanonicalName();
		}
		return ""; //$NON-NLS-1$
	}

	/*
	 * add the new import statement to the end of the import statements
	 */
	private boolean addImport() {

		try {
			File fileAST = document.getNewModelEGLFile();
			final ASTRewrite rewrite = ASTRewrite.create(fileAST);
			rewrite.addImport(fileAST, getImportString(), false, new ImportComparator());
			rewrite.rewriteAST(document).apply(document);
		} catch (BadLocationException e) {
			EGLLogger.log(this, e);
			return false;
		}
		return true;
	}

	/*
	 * build the text for the import string to be added
	 */	
	private String getImportString() {
		StringBuffer buffer = new StringBuffer(packageName);
		buffer.append("."); //$NON-NLS-1$
		buffer.append(partName);
		return buffer.toString();
	}

}
