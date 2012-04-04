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
package org.eclipse.edt.ide.ui.internal.refactoring.reorg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.codemanipulation.ImportComparator;
import org.eclipse.edt.ide.ui.internal.editor.DocumentAdapter;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.TextChangeCompatibility;
import org.eclipse.edt.ide.ui.internal.refactoring.util.TextChangeManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

public class ImportManager {
	IPackageFragment packageFragment;
	HashMap fileToImportInfoMap = new HashMap();
	
	
	class ImportInfo {
		IEGLFile eglFile;
		HashSet existingDecls;
		File fileAst;
		Set newImportStrings = new TreeSet(new ImportComparator());
		ImportDeclaration onDemandImport;
		
		public ImportInfo(IEGLFile eglFile) {
			super();
			this.eglFile = eglFile;
		}

		public File getFileAst() {
			if (fileAst == null) {
				fileAst = parseFile();
			}
			return fileAst;
		}
		
		private File parseFile() {
	        try {
	        	IFile file = (IFile) getEglFile().getResource();
	        	ErrorCorrectingParser parser;
	        	Reader reader = new BufferedReader(new InputStreamReader(file.getContents(true), file.getCharset()));
           		parser = new ErrorCorrectingParser(new Lexer(reader));
	           	return (File)parser.parse().value;
	        } catch (Exception e) {
	           return null;
	        }
		
		}
		
		public IEGLFile getEglFile() {
			return eglFile;
		}

		public ImportDeclaration[] getExistingDecls() {
			if (existingDecls == null) {
				existingDecls = new HashSet();
				if (getFileAst() != null) {
					Iterator i = getFileAst().getImportDeclarations().iterator();
					while (i.hasNext()) {
						ImportDeclaration decl = (ImportDeclaration)i.next();
						if (decl.isOnDemand()) {
							if (decl.getName().getCanonicalName().equals(getPackageFragment().getElementName())) {
								onDemandImport = decl;
							}
						}
						else {
							String imp = getPackage(decl);
							if (imp.equals(getPackageFragment().getElementName())) {
								existingDecls.add(decl);
							}
						}
					}
				}
			}
			return (ImportDeclaration[])existingDecls.toArray(new ImportDeclaration[existingDecls.size()]);
		}

		public void addImportForPart(String partName) {
			if (onDemandImport != null) {
				//there is already on on demand import, don't need a single type
				return;
			}
			
			String imp = getPackageFragment().getElementName();
			if (imp.length() > 0) {
				imp = imp + "." + partName;
			}
			else {
				imp = partName;
			}
			
			//check the existing imports to see if there is already one for this part
			ImportDeclaration[] decls = getExistingDecls();
			for (int i = 0; i < decls.length; i++) {
				if (getPackage(decls[i]).equals(getPackageFragment().getElementName()) && getPartName(decls[i]).equalsIgnoreCase(partName)) {
					// found an existing one...no need to add a new one
					return;
				}
			}
			
			newImportStrings.add(imp);
		}
		
		
		private String getPackage(ImportDeclaration decl) {
			String pkg = decl.getName().getCanonicalName();
			if (decl.isOnDemand()) {
				return pkg;
			}
			else {
				int index = pkg.lastIndexOf(".");
				if (index > -1) {
					return pkg.substring(0, index);
				}
			}
			return pkg;
		}
		
		private String getPartName(ImportDeclaration decl) {
			String pkg = decl.getName().getCanonicalName();
			if (decl.isOnDemand()) {
				return "*";
			}
			else {
				int index = pkg.lastIndexOf(".");
				if (index > -1) {
					return pkg.substring(index+1, pkg.length());
				}
			}
			return pkg;
		}
		
		
		public ImportDeclaration[] getDeclsToDelete() {
			if (newImportsOnDemand()) {
				return getExistingDecls();
			}
			else {
				return new ImportDeclaration[0];
			}
		}
		
		public String[] getNewImports() {
			if (onDemandImport != null) {
				return new String[0];
			}
			
			if (newImportsOnDemand())  {
				return new String[] {getPackageFragment().getElementName()};
			}
			else {
				return (String[])newImportStrings.toArray(new String[newImportStrings.size()]);
			}
		}
		
		public boolean newImportsOnDemand() {
			return getPackageFragment().getElementName().length() > 0 && getExistingDecls().length + newImportStrings.size() >= getOnDemandThreshold();
		}
		
		private int getOnDemandThreshold() {
			return EDTUIPlugin.getDefault().getPreferenceStore().getInt(EDTUIPreferenceConstants.ORGIMPORTS_ONDEMANDTHRESHOLD);

		}
		
		private void createChanges(TextChangeManager changeManager) {
			if (getFileAst() == null) {
				return;
			}
			
			boolean changedFile = false;
			ASTRewrite rewrite = ASTRewrite.create(getFileAst());
			ImportDeclaration[] decls = getDeclsToDelete();
			for (int i = 0; i < decls.length; i++) {
				rewrite.removeNode(decls[i]);
				changedFile = true;
			}
			
			String[] imps = getNewImports();
			for (int i = 0; i < imps.length; i++) {
				rewrite.addImport(getFileAst(), imps[i], newImportsOnDemand(), new ImportComparator());
				changedFile = true;
			}
			
			if (!changedFile) {
				return;
			}
			
			// create a textEditChange!
			IDocument doc = getDocument();
			if (doc != null) {
				TextEdit edit = rewrite.rewriteAST(doc);
				TextChangeCompatibility.addTextEdit(changeManager.get(getEglFile()), UINlsStrings.MoveRefactoring_updateImports, edit);
				return;
			}
		}
		
		private IDocument getDocument() {
			try {
				IEGLFile workingCopy = ((IEGLFile) getEglFile().getWorkingCopy(null, EGLUI.getBufferFactory(), null));
				IBuffer buffer = workingCopy.getBuffer();
				if (buffer instanceof DocumentAdapter) {
					return ((DocumentAdapter) buffer).getDocument();
				}
			} catch (EGLModelException e) {
				e.printStackTrace();
				EDTUIPlugin.log(e);
				return null;
			}
			return null;

		}		
	}


	public ImportManager(IPackageFragment packageFragment) {
		super();
		this.packageFragment = packageFragment;
	}


	public IPackageFragment getPackageFragment() {
		return packageFragment;
	}
	
	public ImportInfo getInfoFor(IEGLFile file) {
		ImportInfo info = (ImportInfo)fileToImportInfoMap.get(file);
		if (info == null) {
			info = new ImportInfo(file);
			fileToImportInfoMap.put(file, info);
		}
		return info;
	}
	
	public ImportInfo[] getImportInfos() {
		return (ImportInfo[])fileToImportInfoMap.values().toArray(new ImportInfo[fileToImportInfoMap.keySet().size()]);
	}
	
	public void createChanges(TextChangeManager changeManager) {
		ImportInfo[] infos = getImportInfos();
		for (int i = 0; i < infos.length; i++) {
			infos[i].createChanges(changeManager);
		}
	}
	
}
