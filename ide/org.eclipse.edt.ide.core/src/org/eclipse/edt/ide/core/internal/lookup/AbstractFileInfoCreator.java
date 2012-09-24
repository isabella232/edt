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
package org.eclipse.edt.ide.core.internal.lookup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;

public abstract class AbstractFileInfoCreator {

	protected class ASTPartSourceReader {
		private Reader stringReader;
		private int currentOffset;
		
		public ASTPartSourceReader(Reader stringReader){
			this.stringReader = stringReader;
		}
		
		public void seekToElement(int elementOffset) throws IOException{
			
			if(currentOffset > elementOffset){
				throw new BuildException("Error seeking to element"); //$NON-NLS-1$
			}
			while(currentOffset != elementOffset){
				read();
			}
		}
		
		public int read() throws IOException{
			currentOffset += 1;
			return stringReader.read();
		}
		
		public void close() throws IOException{
			stringReader.close();
		}
	}

	protected AbstractProjectInfo projectInfo;
	protected String packageName;
	protected IFile file;
	protected IDuplicatePartRequestor duplicatePartRequestor;
	protected ASTFileInfo result = new ASTFileInfo();;
	protected HashSet addedNames = new HashSet();
	private File fileAST;
	private boolean hasGeneratablePart = false;
	private MessageDigest messageDigest;
	private List errors = new ArrayList();
	
	public AbstractFileInfoCreator(AbstractProjectInfo projectInfo, String packageName, IFile file, File fileAST, IDuplicatePartRequestor duplicatePartRequestor){
		
		this.projectInfo = projectInfo;
		this.packageName = packageName;
		this.file = file;
		this.fileAST = fileAST;
		this.duplicatePartRequestor = duplicatePartRequestor;
		
		try {
			messageDigest = MessageDigest.getInstance("MD5");  //$NON-NLS-1$
		} catch (NoSuchAlgorithmException e) {
			throw new BuildException("Error creating AST FileInfo", e); //$NON-NLS-1$
		}
	}
	
	protected abstract String getContents() throws CoreException, IOException;
	
	private void initializeASTInfo() {
		try{
			initializeLineNumbers();
			initializeParts();
		}catch(IOException e){
			throw new BuildException("Error creating AST FileInfo", e); //$NON-NLS-1$
		}catch(CoreException e){
			throw new BuildException("Error creating AST FileInfo", e); //$NON-NLS-1$
		} catch (BadLocationException e) {
			throw new BuildException("Error creating AST FileInfo", e); //$NON-NLS-1$
		}	
	
	}

	private void initializeLineNumbers() throws CoreException, IOException, BadLocationException {
		DefaultLineTracker lineTracker = new DefaultLineTracker();
		
		lineTracker.set(getContents());
		
		int numberOfLines = lineTracker.getNumberOfLines();
		int[] offsets = new int[numberOfLines];
		for(int i=0; i<numberOfLines; i++){
			offsets[i] = lineTracker.getLineOffset(i);
		}
		
		result.setLineOffsets(offsets);
	}

	private void initializeParts() throws CoreException, IOException {
		final ASTPartSourceReader reader = new ASTPartSourceReader(new BufferedReader(new StringReader(getContents())));
		
		try{
			fileAST.accept(new DefaultASTVisitor(){
			
				public boolean visit(File fileNode){
					
					int headerStartOffset = 0;
					int headerEndOffset = 0;
					
					PackageDeclaration packageDeclaration = fileNode.getPackageDeclaration();
					List importDeclarations = fileNode.getImportDeclarations();	
					
					if(packageDeclaration != null){
						headerStartOffset = packageDeclaration.getOffset();
					}else if(importDeclarations.size() > 0){
						headerStartOffset = ((ImportDeclaration)importDeclarations.get(0)).getOffset();
					}else{
						headerStartOffset = 0;
					}
					
					if(importDeclarations.size() > 0){
						ImportDeclaration lastDecl = (ImportDeclaration)importDeclarations.get(importDeclarations.size() - 1);
						headerEndOffset = (lastDecl.getOffset() + lastDecl.getLength());
					}else if(packageDeclaration != null){
						headerEndOffset = packageDeclaration.getOffset() + packageDeclaration.getLength();
					}else{
						headerEndOffset = 0;
					}
						
					processFilePart(reader, Util.getFilePartName(file), Util.getCaseSensitiveFilePartName(file), ITypeBinding.FILE_BINDING, headerStartOffset, (headerEndOffset - headerStartOffset));
					return true;				
				}
				
				public boolean visit(Handler handler){
					processPart(reader, handler, ITypeBinding.HANDLER_BINDING, handler.getOffset(), handler.getLength());
					return false;
				}
				
				public boolean visit(DataItem dataItem) {
					processPart(reader, dataItem, ITypeBinding.DATAITEM_BINDING, dataItem.getOffset(), dataItem.getLength());
					return false;
				}
	
				public boolean visit(Interface interfaceNode) {
					processPart(reader, interfaceNode, ITypeBinding.INTERFACE_BINDING, interfaceNode.getOffset(), interfaceNode.getLength());
					return false;
				}
	
				public boolean visit(Library library) {
					processPart(reader, library, ITypeBinding.LIBRARY_BINDING, library.getOffset(), library.getLength());
					return false;
				}
	
				public boolean visit(Program program) {
					processPart(reader, program, ITypeBinding.PROGRAM_BINDING, program.getOffset(), program.getLength());
					return false;
				}

				public boolean visit(EGLClass eglClass) {
					processPart(reader, eglClass, ITypeBinding.CLASS_BINDING, eglClass.getOffset(), eglClass.getLength());
					return false;
				}

				public boolean visit(Record record) {
					if(record.isFlexible()){
						processPart(reader, record, ITypeBinding.FLEXIBLE_RECORD_BINDING, record.getOffset(), record.getLength());
					}else{
						processPart(reader, record, ITypeBinding.FIXED_RECORD_BINDING, record.getOffset(), record.getLength());
					}
					return false;
				}
	
				public boolean visit(Service service) {
					processPart(reader, service, ITypeBinding.SERVICE_BINDING, service.getOffset(), service.getLength());
					return false;
				}
	
				public boolean visit(Enumeration enumeration) {
					processPart(reader, enumeration, ITypeBinding.ENUMERATION_BINDING, enumeration.getOffset(), enumeration.getLength());
					return false;
				}
	
				public boolean visit(ExternalType externalType) {
					processPart(reader, externalType, ITypeBinding.EXTERNALTYPE_BINDING, externalType.getOffset(), externalType.getLength());
					return false;
				}
	
				public boolean visit(Delegate delegate) {
					processPart(reader, delegate, ITypeBinding.DELEGATE_BINDING, delegate.getOffset(), delegate.getLength());
					return false;
				}
			});
		}finally{
			reader.close();
		}	
		
		result.setErrors(errors);
	}
	
	private void processFilePart(ASTPartSourceReader reader, String caseInsensitiveFileName, String caseSensitiveFileName, int partType, int sourceStart, int sourceLength){
		if (checkForDuplicates()){
			if(!isDuplicatePart(caseInsensitiveFileName)){
				addPart(reader, caseInsensitiveFileName, caseSensitiveFileName, partType, sourceStart, sourceLength);
			}else{
				duplicatePartRequestor.acceptDuplicatePart(caseInsensitiveFileName);
				errors.add(new FileInfoError(sourceStart, sourceLength, IProblemRequestor.DUPLICATE_NAME_IN_NAMESPACE, new String[]{caseInsensitiveFileName}));
			}
		}else{
			addPart(reader, caseInsensitiveFileName, caseSensitiveFileName, partType, sourceStart, sourceLength);
		}
	}
	
	private void processPart(ASTPartSourceReader reader, Part part, int partType, int sourceStart, int sourceLength) {
		if(partRequiresOnePerFile(part)){
			validateGeneratablePart(part);
		}
		
		String caseInsensitivePartName = part.getIdentifier();
		if (checkForDuplicates()){
			if(!isDuplicatePart(caseInsensitivePartName)){
				addPart(reader, caseInsensitivePartName, part.getName().getCaseSensitiveIdentifier(), partType, sourceStart, sourceLength);
			}else{
				duplicatePartRequestor.acceptDuplicatePart(caseInsensitivePartName);
				errors.add(new FileInfoError(part.getName().getOffset(), part.getName().getLength(), IProblemRequestor.DUPLICATE_NAME_IN_NAMESPACE, new String[]{caseInsensitivePartName}));
			}
		}else{
			addPart(reader, caseInsensitivePartName, part.getName().getCaseSensitiveIdentifier(), partType, sourceStart, sourceLength);
		}
	}

	private void addPart(ASTPartSourceReader reader, String caseInsensitivePartName, String caseSensitivePartName, int partType, int sourceStart, int sourceLength) {
		addedNames.add(caseInsensitivePartName);
		
		try{
			result.addPart(caseInsensitivePartName, partType, sourceStart, sourceLength, caseSensitivePartName, calculateMD5Key(reader, sourceStart, sourceLength));
		}catch(IOException e){
			throw new BuildException("Error creating AST FileInfo", e); //$NON-NLS-1$
		}
	}
	
	protected boolean checkForDuplicates(){
		return true;
	}

	protected boolean isDuplicatePart(String caseInsensitivePartName) {
		boolean duplicate = false;
		IFile declaringFile = projectInfo.getPartOrigin(packageName, caseInsensitivePartName).getEGLFile();
		if(declaringFile != null && !declaringFile.equals(file)){
			// duplicate part in different file
			duplicate = true;
		}else if(addedNames.contains(caseInsensitivePartName)){
			// duplicate part in same file
			duplicate = true;
		}else{
			duplicate = false;
		}
		
		return duplicate;
	}

	private void validateGeneratablePart(Part part) {
		if(hasGeneratablePart == true){
			String fileName = file.getFullPath().removeFileExtension().lastSegment();
			errors.add(new FileInfoError(part.getName().getOffset(), part.getName().getLength(), IProblemRequestor.ONLY_ONE_GENERATABLE_PART_PER_FILE, new String[]{part.getPartTypeName(), part.getName().getCanonicalName(), fileName}));
		}else{
			String fileName = file.getFullPath().removeFileExtension().lastSegment();
			hasGeneratablePart = true;
			if(!part.getName().getCanonicalName().equals(fileName)){
				errors.add(new FileInfoError(part.getName().getOffset(), part.getName().getLength(), IProblemRequestor.GENERATABLE_PART_NAME_MUST_MATCH_FILE_NAME, new String[]{part.getPartTypeName(), part.getName().getCanonicalName(), fileName}));
			}
		}
	}
	
	private byte[] calculateMD5Key(ASTPartSourceReader reader, int offset, int length) throws IOException {
		byte[] partBytes = new byte[length];
		reader.seekToElement(offset);
		
		for(int i=0; i< length; i++){
			partBytes[i] = (byte)reader.read();
		}
		
		return messageDigest.digest(partBytes);
	}

	public IASTFileInfo getASTInfo() {
		initializeASTInfo();
		return result;		
	}
	
	private boolean partRequiresOnePerFile(Part part) {
		switch (part.getPartType()) {
			case Part.PROGRAM:
			case Part.LIBRARY:
			case Part.CLASS:
			case Part.HANDLER:
			case Part.SERVICE:
				return true;
			default:
				return false;
		}
	}
}
