/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.search.matching;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.EGLElement;
import org.eclipse.edt.ide.core.internal.model.IRPartType;
import org.eclipse.edt.ide.core.internal.model.SourcePartElementInfo;
import org.eclipse.edt.ide.core.internal.model.Util;
import org.eclipse.edt.ide.core.internal.model.index.IEntryResult;
import org.eclipse.edt.ide.core.internal.model.index.impl.IndexInput;
import org.eclipse.edt.ide.core.internal.model.index.impl.IndexedFile;
import org.eclipse.edt.ide.core.internal.model.indexing.AbstractIndexer;
import org.eclipse.edt.ide.core.internal.search.EGLSearchScope;
import org.eclipse.edt.ide.core.internal.search.IIndexSearchRequestor;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.Type;

public class PartDeclarationPattern extends SearchPattern {

	private char[] pkg;
	private char[][] enclosingTypeNames;
	protected char[] simpleName;
	protected boolean isPartDecl;

	// set to "part"_SUFFIX for only matching a particular part type
	// set to PART_SUFFIX for matching all part types
	protected char partTypes; 

	private char[] decodedPackage;
	private char[][] decodedEnclosingTypeNames;
	protected char[] decodedSimpleName;
	protected char decodedPartTypes;
	
public PartDeclarationPattern(int matchMode, boolean isCaseSensitive) {
	super(matchMode, isCaseSensitive);
}
public PartDeclarationPattern(
	char[] pkg,
	char[][] enclosingTypeNames,
	char[] simpleName,
	char partTypes,
	int matchMode, 
	boolean isCaseSensitive) {

	super(matchMode, isCaseSensitive);

	this.pkg = isCaseSensitive ? pkg : CharOperation.toLowerCase(pkg);
	if (isCaseSensitive || enclosingTypeNames == null) {
		this.enclosingTypeNames = enclosingTypeNames;
	} else {
		int length = enclosingTypeNames.length;
		this.enclosingTypeNames = new char[length][];
		for (int i = 0; i < length; i++){
			this.enclosingTypeNames[i] = CharOperation.toLowerCase(enclosingTypeNames[i]);
		}
	}
	this.simpleName = isCaseSensitive ? simpleName : CharOperation.toLowerCase(simpleName);
	this.partTypes = partTypes;
	
	this.needsResolve = pkg != null && enclosingTypeNames != null;
}
public void decodeIndexEntry(IEntryResult entryResult){
	
	char[] word = entryResult.getWord();
	isPartDecl = true;
	//don't process entries that are refs
	if(CharOperation.prefixEquals(IIndexConstants.PART_DECL,word)){
		int size = word.length;
	
		this.decodedPartTypes = word[PART_DECL_LENGTH];
		int oldSlash = PART_DECL_LENGTH+1;
		int slash = CharOperation.indexOf(SEPARATOR, word, oldSlash+1);
		if (slash == oldSlash+1){ 
			this.decodedPackage = CharOperation.NO_CHAR;
		} else {
			this.decodedPackage = CharOperation.subarray(word, oldSlash+1, slash);
		}
		this.decodedSimpleName = CharOperation.subarray(word, slash+1, slash = CharOperation.indexOf(SEPARATOR, word, slash+1));
	
		if (slash+1 < size){
			if (slash+3 == size && word[slash+1] == ONE_ZERO[0]) {
				this.decodedEnclosingTypeNames = ONE_ZERO_CHAR;
			} else {
				this.decodedEnclosingTypeNames = CharOperation.splitOn('/', CharOperation.subarray(word, slash+1, size-1));
			}
		} else {
			this.decodedEnclosingTypeNames = CharOperation.NO_CHAR_CHAR;
		}
	}else isPartDecl = false;
}
/**
 * see SearchPattern.feedIndexRequestor
 */
public void feedIndexRequestor(IIndexSearchRequestor requestor, int detailLevel, int[] references, IndexInput input, IEGLSearchScope scope) throws IOException {
	for (int i = 0, max = references.length; i < max; i++) {
		IndexedFile file = input.getIndexedFile(references[i]);
		String path;
		if (file != null && scope.encloses(path =IndexedFile.convertPath(file.getPath()))) {
			
			//example of filePkgRootPath:
			//1. /Test1/EGLSource/mypkg/interfaces
			//2. /Test1/EGLSource
			//3. /Test1/Test.eglar
			//4. c:/example/temp/Test.eglar
			String filePkgRootPath = org.eclipse.edt.ide.core.internal.utils.Util.getPackageFragmentRootPath(path);
			String filePkg = "";
			if(!Util.isEGLARFileName(filePkgRootPath)){
				int ind = 0;
				for(int k=0; k<2; k++){		//locate the 3rd '/'					
					ind = filePkgRootPath.indexOf("/", ind + 1);
				}
				//ind == -1: no 3rd '/', i.e. default package
				if(ind != -1){	
					filePkg = filePkgRootPath.substring(ind + 1);
					filePkg = filePkg.replace("/", ".");
					filePkgRootPath = filePkgRootPath.substring(0, ind);
				}
				
			}
			
			if(Util.isEGLARFileName(filePkgRootPath)){
				if(scope instanceof EGLSearchScope){
					Map<IPath, Set> eglarProjectsMap = ((EGLSearchScope)scope).getEglarProjectsMap();
					if(eglarProjectsMap != null){
						Set<IEGLProject> projects = eglarProjectsMap.get(new Path(filePkgRootPath));
						if(projects != null){
							for(IEGLProject eglProj: projects){
								requestor.acceptPartDeclaration(eglProj.getPath(), path, decodedSimpleName, decodedPartTypes, decodedEnclosingTypeNames, decodedPackage);
							}
							return;
						}
					}
				}
			}

			IPath[] enclosingPaths = scope.enclosingProjects();
			IPath projectPath = null;
			
			//1. for resource in workspace (both source and eglar)
			//2. for external eglar
			for(int j=0; j<enclosingPaths.length; j++){
				if(enclosingPaths[j].segmentCount() > 1){	//not project, ignore this
					continue;
				}
				IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(enclosingPaths[j].toString());
				IEGLProject eglProj = EGLCore.create(proj);
				try {
					IPackageFragmentRoot pkgFragRoot = eglProj.getPackageFragmentRoot(new Path(filePkgRootPath));
					if(pkgFragRoot != null && pkgFragRoot.exists()){
						if(filePkg != null && !filePkg.trim().isEmpty()){	//find PackageFragment
							IPackageFragment pkgFrag = pkgFragRoot.getPackageFragment(filePkg);
							if(pkgFrag == null || !pkgFrag.exists()){
								continue;
							}
						}
						projectPath = enclosingPaths[j];
						requestor.acceptPartDeclaration(projectPath, path, decodedSimpleName, decodedPartTypes, decodedEnclosingTypeNames, decodedPackage);
					}					
				} catch (EGLModelException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
}
/**
 * see SearchPattern.indexEntryPrefix()
 */
public char[] indexEntryPrefix(){

	return AbstractIndexer.bestPartDeclarationPrefix(
			pkg,
			simpleName,
			partTypes,
			matchMode, 
			isCaseSensitive);
}
/**
 * @see SearchPattern#matchContainer()
 */
protected int matchContainer() {
	return EGL_FILE | PART | FUNCTION | FIELD;
}

public int matchesPart(Part type){
	if(!matchesPartType(type)){
		return IMPOSSIBLE_MATCH;
	}
	
	return this.matchLevel(type);
}

@Override
public int matchesPart(IPart part) {
	if(!matchesPartType(part)){
		return IMPOSSIBLE_MATCH;
	}
	
	return this.matchLevel(part);
}

/**
 * @see SearchPattern#matchLevel(AstNode, boolean)
 */
public int matchLevel(Node node, boolean resolve) {
	if (node instanceof Part){
		Part type = (Part)node;
		// type name
		if (this.simpleName != null && !this.matchesName(this.simpleName, type.getName().getCanonicalName().toCharArray())){
			return IMPOSSIBLE_MATCH;
		}
		
	}
	else return IMPOSSIBLE_MATCH;

	return POSSIBLE_MATCH ;
}

	@Override
	public int matchLevel(IMember member, boolean resolve) {
		if (member instanceof IPart){
			IPart type = (IPart)member;
			// type name
			if (this.simpleName != null && !this.matchesName(this.simpleName, type.getElementName().toCharArray())){
				return IMPOSSIBLE_MATCH;
			}
		}
		return POSSIBLE_MATCH ;
	}

/**
 * @see SearchPattern#matchLevel(IEGLPart)
 */
public int matchLevel(Part part) {
	// fully qualified name
	char[] enclosingTypeName = this.enclosingTypeNames == null ? null : CharOperation.concatWith(this.enclosingTypeNames, '.');
	return this.matchLevelForType(this.simpleName, this.pkg, enclosingTypeName, getPartBinding(part));
}

public int matchLevel(IPart part) {
	return IMPOSSIBLE_MATCH;
}

/**
 * Returns whether the given type binding matches the given simple name pattern 
 * package pattern and enclosing name pattern.
 */
protected boolean matchesType(char[] simpleNamePattern, char[] pkgPattern, char[] enclosingNamePattern, char[] fullyQualifiedTypeName) {
	if (enclosingNamePattern == null) {
		return this.matchesType(simpleNamePattern, pkgPattern, fullyQualifiedTypeName);
	} else {
		char[] pattern;
		if (pkgPattern == null) {
			pattern = enclosingNamePattern;
		} else {
			pattern = CharOperation.concat(pkgPattern, enclosingNamePattern, '.');
		}
		return this.matchesType(simpleNamePattern, pattern, fullyQualifiedTypeName);
	}
}



/**
 * Returns whether the given part matches the given simple name pattern 
 * qualification pattern and enclosing type name pattern.
 */
protected int matchLevelForType(char[] simpleNamePattern, char[] qualificationPattern, char[] enclosingNamePattern, org.eclipse.edt.mof.egl.Part partBinding) {
	if (partBinding != null){
		if (enclosingNamePattern == null) {
			return this.matchLevelForType(simpleNamePattern, qualificationPattern, partBinding);
			} else {
			if (qualificationPattern == null) {
				return matchLevelForType(simpleNamePattern, enclosingNamePattern, partBinding);
				} else {
						// pattern was created from a Java element: qualification is the package name.
//						char[] fullQualificationPattern = CharOperation.concat(qualificationPattern, enclosingNamePattern, '.');
			//			if ( CharOperation.equals(pkg, CharOperation.concatWith(type.getPackage().compoundName, '.'))) {
			//				return this.matchLevelForType(simpleNamePattern, fullQualificationPattern, type);
			//			} else {
							return IMPOSSIBLE_MATCH;
			//			}
					}
				}
	}
	return IMPOSSIBLE_MATCH;
}

protected boolean partTypesMatch() {
	if ((partTypes & decodedPartTypes) == 0) {
		// Annotations and Stereotypes are their own "search type", however they are both defined as Records. They do not
		// have their own unique part type constant in AST, so "decodedPartTypes" will be the record type. Do a special check
		// that searching for stereotype or annotation includes record declarations.
		return decodedPartTypes == IIndexConstants.RECORD_SUFFIX
				&& ((partTypes & IIndexConstants.ANNOTATION_SUFFIX) != 0
				|| (partTypes & IIndexConstants.STEREOTYPE_SUFFIX) != 0);
	}
	return true;
}

/**
 * see SearchPattern.matchIndexEntry
 */
protected boolean matchIndexEntry(){

	/* check part type nature */
	if (!partTypesMatch()) return false;
	if (!isPartDecl)return false;
	/* check qualification - exact match only */
	if (pkg != null && !CharOperation.equals(pkg, decodedPackage, isCaseSensitive))
		return false;
	/* check enclosingTypeName - exact match only */
	if (enclosingTypeNames != null){
		// empty char[][] means no enclosing type (in which case, the decoded one is the empty char array)
		if (enclosingTypeNames.length == 0){
			if (decodedEnclosingTypeNames != CharOperation.NO_CHAR_CHAR) return false;
		} else {
			if (!CharOperation.equals(enclosingTypeNames, decodedEnclosingTypeNames, isCaseSensitive)) return false;
		}
	}
	/* check simple name matches */
	if (simpleName != null){
		switch(matchMode){
			case EXACT_MATCH :
				if (!CharOperation.equals(simpleName, decodedSimpleName, isCaseSensitive)){
					return false;
				}
				break;
			case PREFIX_MATCH :
				if (!CharOperation.prefixEquals(simpleName, decodedSimpleName, isCaseSensitive)){
					return false;
				}
				break;
			case PATTERN_MATCH :
				if (!CharOperation.match(simpleName, decodedSimpleName, isCaseSensitive)){
					return false;
				}
		}
	}
	return true;
}
public String toString(){
	StringBuffer buffer = new StringBuffer(20);
	buffer.append("PartDeclarationPattern: pkg<"); //$NON-NLS-1$
	if (pkg != null) buffer.append(pkg);
	buffer.append(">, enclosing<"); //$NON-NLS-1$
	if (enclosingTypeNames != null) {
		for (int i = 0; i < enclosingTypeNames.length; i++){
			buffer.append(enclosingTypeNames[i]);
			if (i < enclosingTypeNames.length - 1)
				buffer.append('.');
		}
	}
	buffer.append(">, type<"); //$NON-NLS-1$
	if (simpleName != null) buffer.append(simpleName);
	buffer.append(">, "); //$NON-NLS-1$
	switch(matchMode){
		case EXACT_MATCH : 
			buffer.append("exact match, "); //$NON-NLS-1$
			break;
		case PREFIX_MATCH :
			buffer.append("prefix match, "); //$NON-NLS-1$
			break;
		case PATTERN_MATCH :
			buffer.append("pattern match, "); //$NON-NLS-1$
			break;
	}
	if (isCaseSensitive)
		buffer.append("case sensitive"); //$NON-NLS-1$
	else
		buffer.append("case insensitive"); //$NON-NLS-1$
	return buffer.toString();
}

/**
 * Check to make sure that the part type we have resolved to is of the type we are looking for.
 */
private boolean matchesPartType(Part part){
	boolean match = false;
	int partType = part.getPartType();
	switch(partTypes)
	{
		case IIndexConstants.PROGRAM_SUFFIX:
			match = partType == Part.PROGRAM;
			break;
		case IIndexConstants.RECORD_SUFFIX:
			match = partType == Part.RECORD && !isAnnotation(part);
			break;
		case IIndexConstants.ANNOTATION_SUFFIX:
			match = partType == Part.RECORD && isAnnotation(part);
			break;
		case IIndexConstants.STEREOTYPE_SUFFIX:
			match = partType == Part.RECORD && isStereotype(part);
			break;
		case IIndexConstants.LIBRARY_SUFFIX:
			match = partType == Part.LIBRARY;
			break;
		case IIndexConstants.FUNCTION_SUFFIX:
			match = partType == Part.FUNCTION;
			break;
		case IIndexConstants.HANDLER_SUFFIX:
			match = partType == Part.HANDLER;
			break;
		case IIndexConstants.SERVICE_SUFFIX:
			match = partType == Part.SERVICE;
			break;
		case IIndexConstants.INTERFACE_SUFFIX:
			match = partType == Part.INTERFACE;
			break;
		case IIndexConstants.DELEGATE_SUFFIX:
			match = partType == Part.DELEGATE;
			break;
		case IIndexConstants.EXTERNALTYPE_SUFFIX:
			match = partType == Part.EXTERNALTYPE;
			break;
		case IIndexConstants.ENUMERATION_SUFFIX:
			match = partType == Part.ENUMERATION;
			break;
		case IIndexConstants.CLASS_SUFFIX:
			match = partType == Part.CLASS;
			break;
		case IIndexConstants.PART_SUFFIX:
			match = true;
			break;
	}
	
	return match;
}

private boolean isAnnotation(IPart part) {
	try {
		SourcePartElementInfo partInfo = (SourcePartElementInfo)(((EGLElement)part).getElementInfo());

		return IRPartType.ANNOTATION.equals(new String(partInfo.getSubTypeName())) || isStereotype(part);
	} catch (EGLModelException e) {
	}
	return false;
}
private boolean isStereotype(IPart part) {
	try {
		SourcePartElementInfo partInfo = (SourcePartElementInfo)(((EGLElement)part).getElementInfo());

		return IRPartType.STEREOTYPETYPE.equals(new String(partInfo.getSubTypeName()));
	} catch (EGLModelException e) {
	}
	return false;
}
private boolean isAnnotation(Part part) {
	Name name = part.getName();
	Type binding = name.resolveType();
	return binding instanceof AnnotationType;
}

private boolean isStereotype(Part part) {
	Name name = part.getName();
	Type binding = name.resolveType();
	return binding instanceof StereotypeType;
}

/**
 * Check to make sure that the part type we have resolved to is of the type we are looking for.
 */
private boolean matchesPartType(IPart part){
	boolean match = false;
	try {
		SourcePartElementInfo partInfo = (SourcePartElementInfo)(((EGLElement)part).getElementInfo());
		
		switch(partTypes)
		{
			case IIndexConstants.PROGRAM_SUFFIX:
				match = partInfo.isProgram();
				break;
			case IIndexConstants.RECORD_SUFFIX:
				match = partInfo.isRecord() && !isAnnotation(part);
				break;
			case IIndexConstants.ANNOTATION_SUFFIX:
				match = partInfo.isRecord() && isAnnotation(part);
				break;
			case IIndexConstants.STEREOTYPE_SUFFIX:
				match = partInfo.isRecord() && isStereotype(part);
				break;
			case IIndexConstants.LIBRARY_SUFFIX:
				match = partInfo.isLibrary();
				break;
			case IIndexConstants.FUNCTION_SUFFIX:
				match = partInfo.isFunction();
				break;
			case IIndexConstants.HANDLER_SUFFIX:
				match = partInfo.isHandler();
				break;
			case IIndexConstants.SERVICE_SUFFIX:
				match = partInfo.isService();
				break;
			case IIndexConstants.INTERFACE_SUFFIX:
				match = partInfo.isInterface();
				break;
			case IIndexConstants.DELEGATE_SUFFIX:
				match = partInfo.isDelegate();
				break;
			case IIndexConstants.EXTERNALTYPE_SUFFIX:
				match = partInfo.isExternalType();
				break;
			case IIndexConstants.ENUMERATION_SUFFIX:
				match = partInfo.isEnumeration();
				break;
			case IIndexConstants.CLASS_SUFFIX:
				match = partInfo.isClass();
				break;
			case IIndexConstants.PART_SUFFIX:
				match = true;
				break;
		}
	} catch (EGLModelException e) {
		e.printStackTrace();
	}
	return match;
}
@Override
public int getPatternType() {
	return SearchPattern.DECLARATION;
}
}
