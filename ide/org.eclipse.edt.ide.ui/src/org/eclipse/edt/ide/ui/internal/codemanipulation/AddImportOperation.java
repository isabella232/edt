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
package org.eclipse.edt.ide.ui.internal.codemanipulation;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLUIStatus;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.util.BoundNodeModelUtility;
import org.eclipse.edt.ide.ui.internal.editor.util.IBoundNodeRequestor;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;

public class AddImportOperation extends OrganizeImportsOperation {

	private IEGLDocument fEGLDoc;
	private int fSelectionOffset;
	private int fSelectionLength;
	private IStatus fStatus;
	
	public AddImportOperation(IEGLFile file, IEGLDocument egldoc, int offset, int length, boolean needSave, IChooseImportQuery query) {
		super(file, needSave, query);
		fEGLDoc = egldoc;
		fSelectionOffset = offset;
		fSelectionLength = length;
		fStatus = Status.OK_STATUS;
	}
	
	public IStatus getStatus(){
		return fStatus;
	}
	
	public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		if(monitor == null)
			monitor = new NullProgressMonitor();

		try{
			monitor.beginTask(UINlsStrings.AddImportsOperation_description, 15);
			//initial text selection
			String identifier = fEGLDoc.get(fSelectionOffset, fSelectionLength);
			
			IPackageFragment currPkgFrag =  (IPackageFragment)(feglfile.getParent());		
			String currPackageName = currPkgFrag.getElementName();		//package name string						
			final OrganizedImportSection resolvedTypes = new OrganizedImportSection(currPackageName);
			
			Node node = fEGLDoc.getNewModelNodeAtOffset(fSelectionOffset);
			Name nameNode = null;
			if(node instanceof Name)
			{
				nameNode = (Name)node;
				identifier = nameNode.getIdentifier();
			}
			
			if(nameNode != null)
			{				
				final IEGLProject eglProj = feglfile.getEGLProject();		
				final Set /*<importDeclaration>*/ oldImports = new HashSet();
				final int[] oldLastImportEndOffset = new int[1];
				File fileAST = fEGLDoc.getNewModelEGLFile();
				fileAST.accept(new DefaultASTVisitor() {
					public boolean visit(File file) {return true;};
					public boolean visit(ImportDeclaration importDeclaration) {
						
						oldImports.add(importDeclaration);
						
						Name importName = importDeclaration.getName();
						String packageName = ""; //$NON-NLS-1$
						if(!importDeclaration.isOnDemand())
						{
							String existingPartName = importName.getIdentifier();
							if(importName instanceof QualifiedName)
								packageName = ((QualifiedName)importName).getQualifier().getCanonicalName();							
							resolvedTypes.addImport(packageName, existingPartName);		//even if the part name could be duplicate, the data structure is a map, it will ignore the duplicate part name 				
						}
						else		//if onDemand
						{
							packageName = importName.getCanonicalName();
							resolvedTypes.addImport(packageName, "*");							 //$NON-NLS-1$
						}					
						return false;
					}	
					
					public void endVisit(ImportDeclaration importDeclaration) {
						oldLastImportEndOffset[0] = importDeclaration.getOffset() + importDeclaration.getLength();
					}
				});
				
				ASTRewrite rewrite = ASTRewrite.create(fileAST);
				
				//need to add to import section				
				Map /*<String>, <Name>*/ unresolvedTypes = new LinkedHashMap();
				unresolvedTypes.put(identifier, nameNode);
				try2ResolveUnresolvedTypes(resolvedTypes, unresolvedTypes, eglProj, oldImports, new SubProgressMonitor(monitor, 5));
				//update the file	
				writeToFile(resolvedTypes, oldImports, fEGLDoc, rewrite, fileAST, new SubProgressMonitor(monitor, 1));
				
				final int[] newLastImportEndOffset = new int[1];
				fileAST = fEGLDoc.getNewModelEGLFile();
				fileAST.accept(new DefaultASTVisitor() {
					public boolean visit(File file) {return true;};
					public void endVisit(ImportDeclaration importDeclaration) {
						newLastImportEndOffset[0] = importDeclaration.getOffset() + importDeclaration.getLength();
					}
				});
				
				int offsetdelta = newLastImportEndOffset[0]-oldLastImportEndOffset[0];
				
				//need to get bound ast to get the type binding
				//if it is part binding, then reduce to the simple name from qualified name if it doesn't conflict with the exsiting imports.
				IFile file = (IFile)(feglfile.getCorrespondingResource());							
				BoundNodeModelUtility.getBoundNodeAtOffset(file, fSelectionOffset+offsetdelta, new IBoundNodeRequestor(){

					public void acceptNode(Node boundPart, Node selectedNode) {
						if(selectedNode instanceof QualifiedName){
							QualifiedName qualifiednamenode = (QualifiedName)selectedNode;
							
							String fullyQualifiedPackageName = qualifiednamenode.getQualifier().getCanonicalName(); 
							String[] conflictPkgNames = new String[1];
							String qualifiedNameID = qualifiednamenode.getIdentifier();
							if(!resolvedTypes.isConflict(fullyQualifiedPackageName, qualifiedNameID, conflictPkgNames))
							{
								Type typeBinding = qualifiednamenode.resolveType();
								if(typeBinding instanceof Part){
									String names = qualifiednamenode.getNameComponents();
									int lastDot = names.lastIndexOf('.');
									ReplaceEdit replace = new ReplaceEdit(selectedNode.getOffset(), selectedNode.getLength(), lastDot == -1 ? names : names.substring(lastDot + 1));
									try {
										replace.apply(fEGLDoc);
									} catch (MalformedTreeException e) {
										EDTUIPlugin.log(e);
									} catch (BadLocationException e) {
										EDTUIPlugin.log(e);
									}
								}		
							}
							else
							{
								String conflictImportEntry = conflictPkgNames[0];
								if(conflictImportEntry.length()>0)
									conflictImportEntry += "."; //$NON-NLS-1$
								conflictImportEntry += qualifiedNameID;							
								fStatus= EGLUIStatus.createError(IStatus.ERROR, MessageFormat.format(UINlsStrings.AddImportsOperation_error_importclash, new String[]{conflictImportEntry}), null);														
							}
						}
						
					}
					
				});
			}
			else //if(nameNode == null)
			{
				//selection is not a name node
				fStatus = EGLUIStatus.createError(IStatus.ERROR, MessageFormat.format(UINlsStrings.AddImportsOperation_error_notresolved_message, new String[]{identifier}), null);
			}
			
		} catch (BadLocationException e) {
			throw new CoreException(EGLUIStatus.createError(IStatus.ERROR, e));
		}finally {
			monitor.done();
		}
	}
	
	/**
	 * get the fully qualified name's package name if possible
	 * if the input nameNode is simple name, it could be "pkg3" in pkg3.pkg4.partA
	 * we want to return the fully qualified name "partA" in pkg3.pkg4.partA
	 * 
	 * @param nameNode
	 * @param qualifiedPkgNameString
	 * @return
	 */
	private QualifiedName getFullyQualifedNameNodeIfPossible(Name nameNode)
	{
		final QualifiedName[] possibleQualifiedName = new QualifiedName[]{null};		//init the 1st element to be null
		
		if(nameNode instanceof QualifiedName)
		{
			possibleQualifiedName[0] = (QualifiedName)nameNode;
		}
		
		if(nameNode instanceof SimpleName)
		{
			SimpleName simpleName = (SimpleName)nameNode;
			simpleName.getParent().accept(new DefaultASTVisitor(){					
				public boolean visit(QualifiedName qualifiedName) {
					qualifiedName.getParent().accept(this);	
					return false;
				}
				
				public void endVisit(QualifiedName qualifiedName) {
					if(possibleQualifiedName[0] == null) {		//only want the 1st one
						possibleQualifiedName[0] = qualifiedName;
						
						if(qualifiedName.getParent() instanceof FunctionInvocation && qualifiedName == ((FunctionInvocation) qualifiedName.getParent()).getTarget()) {
							Name possibleQualifier = ((QualifiedName) qualifiedName).getQualifier();
							if(possibleQualifier.isQualifiedName()) {
								possibleQualifiedName[0] = (QualifiedName) possibleQualifier;
							}
							else {
								possibleQualifiedName[0] = null;
							}
						}
					}					
				};
			});			
		}		
		return possibleQualifiedName[0];				
	}
	
/*	private boolean foundPart(IEGLProject eglProj, String packageName, String partName, IProgressMonitor monitor) throws CoreException
	{

		IEGLSearchScope projScope = SearchEngine.createEGLSearchScope(new IEGLElement[]{eglProj}, true);		
		List typeList = new ArrayList();		
		try {
			new SearchEngine().searchAllPartNames(ResourcesPlugin.getWorkspace(),
					packageName.toCharArray(),
					partName.toCharArray(),
					IEGLSearchConstants.EXACT_MATCH,
					IEGLSearchConstants.CASE_INSENSITIVE,
					IEGLSearchConstants.PART,
					projScope,
					new PartInfoRequestor(typeList),
					IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
					monitor);
		} catch (EGLModelException e) {
			throw new CoreException(EGLUIStatus.createError(IStatus.ERROR, e));			
		}
			
			int foundCnts = typeList.size();
			if(foundCnts == 1)
				return true;
			else
				return false;		//if more than 1 found, something is wrong
	}
*/
}
