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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IImportDeclaration;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.ISourceRange;
import org.eclipse.edt.ide.core.model.ISourceReference;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchResultCollector;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.RefactoringScopeFactory;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.TextChangeCompatibility;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.ImportManager.ImportInfo;
import org.eclipse.edt.ide.ui.internal.refactoring.util.TextChangeManager;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.ReplaceEdit;

public class MoveReferenceUpdater {
	TextChangeManager manager;
	IProgressMonitor pm;
	IPackageFragment newPackage;
	ImportManager importManager;
	HashMap allPartsMap;
	
	public MoveReferenceUpdater(TextChangeManager manager, IProgressMonitor pm, IPackageFragment newPackage, IEGLFile[] files) {
		super();
		this.manager = manager;
		this.pm = pm;
		this.newPackage = newPackage;
		importManager = new ImportManager(newPackage);
		setupMap(files);
	}

	public MoveReferenceUpdater(TextChangeManager manager, IProgressMonitor pm, IPackageFragment newPackage, IPart[] parts) {
		super();
		this.manager = manager;
		this.pm = pm;
		this.newPackage = newPackage;
		importManager = new ImportManager(newPackage);
		setupMap(parts);
	}
	
	private void setupMap(IEGLFile[] files) {
		allPartsMap = new HashMap();
		for (int i = 0; i < files.length; i++) {
			try {
				allPartsMap.put(files[i], files[i].getParts());
			} catch (EGLModelException e) {
				// TODO should probably put this into a refactoring results object
				e.printStackTrace();
				EDTUIPlugin.log(e);
			}
		}
	}
	
	private void setupMap(IPart[] parts) {
		HashMap tempMap = new HashMap();
		for (int i = 0; i < parts.length; i++) {
			IEGLFile file  = parts[i].getEGLFile();
			List list = (List)tempMap.get(file);
			if (list == null) {
				list = new ArrayList();
				tempMap.put(file, list);
			}
			list.add(parts[i]);
		}
		
		allPartsMap = new HashMap();
		Iterator i = tempMap.keySet().iterator();
		while(i.hasNext()) {
			Object key = i.next();
			List list = (List)tempMap.get(key);
			IPart[] arr = (IPart[])list.toArray(new IPart[list.size()]);
			allPartsMap.put(key, arr);
		}
	}

	//Build the changes!
	public void run() {
		
		Iterator i = allPartsMap.values().iterator();
		while (i.hasNext()) {
			IPart[] parts = (IPart[])i.next();
			processParts(parts);
		}
		importManager.createChanges(manager);
	}
	
	private void processParts(IPart[] parts) {
		for (int i = 0; i < parts.length; i++) {
			try {
				processPart(parts[i]);
			} catch (EGLModelException e) {
				// TODO should probably put this into a refactoring results object
				e.printStackTrace();
				EDTUIPlugin.log(e);
			}			
		}
	}
	
	//Add a part that is going to be moved to the new package
	private void processPart(IPart part) throws EGLModelException{
		IPackageFragment oldPkg = part.getPackageFragment();
		//if moving the part to the same package, there is nothing to do
		
		if (oldPkg == newPackage) {
			return;
		}
		
		HashSet addImportFiles = new HashSet();
		HashSet removeImportFiles = new HashSet();
		
		processReferences(part, addImportFiles, removeImportFiles);
		addImports(part, addImportFiles);
		//TODO remove imports from the other list!
		
	}
	
	private void addImports(IPart part, HashSet addImportFiles) {
		Iterator i = addImportFiles.iterator();
		while (i.hasNext()) {
			IEGLFile file = (IEGLFile)i.next();
			if (file != null) {
				ImportInfo info = importManager.getInfoFor(file);
				info.addImportForPart(part.getElementName());
			}
		}
	}
		
	private void processReferences(final IPart part, final HashSet addImportFiles, final HashSet removeImportFiles) throws EGLModelException {
		
		final HashSet filesWithSingleTypeImports = new HashSet();
		
		String searchString;
		String elementName = part.getPackageFragment().getElementName();
		if(elementName.equals("")) {
			searchString = part.getElementName();
		}
		else {
			searchString = new String(CharOperation.concat(elementName.toCharArray(), part.getElementName().toCharArray(), '.'));
		}
		
		new SearchEngine().search(ResourcesPlugin.getWorkspace(), SearchEngine.createSearchPattern(searchString, getSearchFor(part), IEGLSearchConstants.REFERENCES, false), RefactoringScopeFactory.create(part), true,true,new IEGLSearchResultCollector() {
			public void aboutToStart() {
			}

			public void accept(IResource resource, int start, int end, IEGLElement enclosingElement, int accuracy) throws CoreException {
				
				if (IEGLSearchResultCollector.POTENTIAL_MATCH == accuracy){
					return;
				}
				//Must be a single type import for this file.
				IPackageFragment pkg = getPackage(enclosingElement);
				
				if (enclosingElement instanceof IImportDeclaration) {
					IEGLFile enclosingFile = getFile(enclosingElement);
					if (samePackage(pkg , newPackage, enclosingElement)) {
						ISourceReference sourceRef = (ISourceReference) enclosingElement;
						ISourceRange sourceRange = sourceRef.getSourceRange();
						TextChangeCompatibility.addTextEdit(manager.get(enclosingFile), UINlsStrings.RenamePartRefactoring_update_reference, new DeleteEdit(sourceRange.getOffset(), sourceRange.getLength()));
						return;
					}
					IEGLFile file = getFile(enclosingElement);
					filesWithSingleTypeImports.add(file);

					//must add a text change to the manager to change the reference
					String newPartName = newPackage.getElementName();
					if (newPartName.length() > 0) {
						newPartName = newPartName + ".";
					}
					newPartName = newPartName + part.getElementName();					
					TextChangeCompatibility.addTextEdit(manager.get(enclosingFile), UINlsStrings.RenamePartRefactoring_update_reference, new ReplaceEdit(start, end - start, newPartName));

					return;
				}
				
				if ((end - start) == part.getElementName().length()) {
					//unqualified reference to the part
					if (samePackage(pkg , newPackage, enclosingElement)) {
						//nothing to do here
					}
					else {
						IEGLFile enclosingFile = getFile(enclosingElement);
						addImportFiles.add(enclosingFile);
					}
				}
				
				//qualified reference
				else {
					if (samePackage(pkg , newPackage, enclosingElement)){
						//remove the qualifier if enclosing element is in the new package
						IEGLFile enclosingFile = getFile(enclosingElement);
						TextChangeCompatibility.addTextEdit(manager.get(enclosingFile), UINlsStrings.RenamePartRefactoring_update_reference, new ReplaceEdit(start, end - start, part.getElementName()));
					}
					else {
						//add a change to modify the qualifier
						String newPartName = newPackage.getElementName();
						IEGLFile enclosingFile = getFile(enclosingElement);
						if (newPartName.length() > 0) {
							newPartName = newPartName + ".";
						}
						else {
							//moving to the default package, add an import for the part
							addImportFiles.add(enclosingFile);
						}
						newPartName = newPartName + part.getElementName();
						TextChangeCompatibility.addTextEdit(manager.get(enclosingFile), UINlsStrings.RenamePartRefactoring_update_reference, new ReplaceEdit(start, end - start, newPartName));
					}
				}
				
				Iterator i = filesWithSingleTypeImports.iterator();
				while (i.hasNext()) {
					IEGLFile file = (IEGLFile) i.next();
					if (addImportFiles.contains(file)) {
						addImportFiles.remove(file);
					}
				}
			}

			public void done() {
			}

			public IProgressMonitor getProgressMonitor() {
				return pm;
			}
			
			private boolean samePackage(IPackageFragment pkg1, IPackageFragment pkg2, IEGLElement enclosingElement) {
				if (pkg1 == pkg2) {
					return true;
				}
				
				if (pkg1 == null || pkg2 == null) {
					return false;
				}
				
				if (pkg1.equals(pkg2)){
					return true;
				}
				
				IPart part = getPart(enclosingElement);
				if (part == null) {
					return false;
				}
				
				IEGLFile file = part.getEGLFile();
				
				Iterator i = allPartsMap.keySet().iterator();
				while (i.hasNext()) {
					IEGLFile key = (IEGLFile)i.next();
					if (file.equals(key)) {
						IPart[] parts = (IPart[])allPartsMap.get(key);
						for (int j = 0; j < parts.length; j++) {
							if (parts[j].equals(part)) {
								return true;
							}
						}
						return false;
					}
				}
				return false;
			}

			public void accept(IEGLElement element, int start, int end,
					IResource resource, int accuracy) throws CoreException {
				// TODO Auto-generated method stub
				
			}
		});

	}
			
	private int getSearchFor(IPart part) {
		int result = IEGLSearchConstants.ALL_ELEMENTS;
		if(part instanceof SourcePart) {
			SourcePart sPart = ((SourcePart) part);
			if (sPart.isDelegate()) {
				result = IEGLSearchConstants.DELEGATE_PART;
			}
			else if (sPart.isExternalType()) {
				result = IEGLSearchConstants.EXTERNALTYPE_PART;
			}
			else if (sPart.isHandler()) {
				result = IEGLSearchConstants.HANDLER_PART;
			}
			else if (sPart.isInterface()) {
				result = IEGLSearchConstants.INTERFACE_PART;
			}
			else if (sPart.isLibrary()) {
				result = IEGLSearchConstants.LIBRARY_PART;
			}
			else if (sPart.isProgram()) {
				result = IEGLSearchConstants.PROGRAM_PART;
			}			
			else if (sPart.isRecord()) {
				result = IEGLSearchConstants.RECORD_PART;
			}
			else if (sPart.isService()) {
				result = IEGLSearchConstants.SERVICE_PART;
			}
			else if (sPart.isEnumeration()) {
				result = IEGLSearchConstants.ENUMERATION_PART;
			}
			else if (sPart.isClass()) {
				result = IEGLSearchConstants.CLASS_PART;
			}
			else if (sPart.isFunction()) {
				result = IEGLSearchConstants.ALL_FUNCTIONS;
			}
		}
		return result;
	}
	
	IPackageFragment getPackage(IEGLElement element) {
		if (element == null) {
			return null;
		}
		if (element instanceof IPackageFragment) {
			return (IPackageFragment)element;
		}
		return getPackage(element.getParent());
	}

	IEGLFile getFile(IEGLElement element) {
		if (element == null) {
			return null;
		}
		if (element instanceof IEGLFile) {
			return (IEGLFile)element;
		}
		return getFile(element.getParent());
	}

	IPart getPart(IEGLElement element) {
		if (element == null) {
			return null;
		}
		if (element instanceof IPart) {
			return (IPart)element;
		}
		return getPart(element.getParent());
	}


}
