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
package org.eclipse.edt.ide.core.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.model.ClassFile;
import org.eclipse.edt.ide.core.internal.model.EGLModelManager;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragment;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRoot;
import org.eclipse.edt.ide.core.internal.model.indexing.EGLModelSearchResources;
import org.eclipse.edt.ide.core.internal.model.indexing.IndexManager;
import org.eclipse.edt.ide.core.internal.model.indexing.IndexSelector;
import org.eclipse.edt.ide.core.internal.search.EGLSearchScope;
import org.eclipse.edt.ide.core.internal.search.EGLWorkspaceScope;
import org.eclipse.edt.ide.core.internal.search.IIndexSearchRequestor;
import org.eclipse.edt.ide.core.internal.search.IInfoConstants;
import org.eclipse.edt.ide.core.internal.search.IndexSearchAdapter;
import org.eclipse.edt.ide.core.internal.search.PathCollector;
import org.eclipse.edt.ide.core.internal.search.PatternSearchJob;
import org.eclipse.edt.ide.core.internal.search.matching.MatchLocator2;
import org.eclipse.edt.ide.core.internal.search.matching.PartDeclarationPattern;
import org.eclipse.edt.ide.core.internal.search.matching.PartReferencePattern;
import org.eclipse.edt.ide.core.internal.search.matching.SearchPattern;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IWorkingCopy;

/**
 * A <code>SearchEngine</code> searches for egl elements following a search pattern.
 * The search can be limited to a search scope.
 * <p>
 * Various search patterns can be created using the factory functions 
 * <code>createSearchPattern(String, int, int, boolean)</code>, <code>createSearchPattern(IEGLElement, int)</code>,
 * <code>createOrSearchPattern(ISearchPattern, ISearchPattern)</code>.
 * </p>
 * <p>For example, one can search for references to a function in the hierarchy of a type, 
 * or one can search for the declarations of types starting with "Abstract" in a project.
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class SearchEngine {

/**
 * A list of working copies that take precedence over their original 
 * egl files.
 */
private IWorkingCopy[] workingCopies = null;


/**
 * For tracing purpose.
 */	
public static boolean VERBOSE = false;	

/**
 * Creates a new search engine.
 */
public SearchEngine() {
}

/**
 * Creates a new search engine with a list of working copies that will take precedence over 
 * their original egl files in the subsequent search operations.
 * <p>
 * Note that passing an empty working copy will be as if the original compilation
 * unit had been deleted.</p>
 * 
 * @param workingCopies the working copies that take precedence over their original egl files
 * @since 2.0
 */
public SearchEngine(IWorkingCopy[] workingCopies) {
	this.workingCopies = workingCopies;
}
/*
 * Removes from the given list of working copies the ones that cannot see the given focus.
 */
private IWorkingCopy[] filterWorkingCopies(IWorkingCopy[] workingCopies, IEGLElement focus, boolean isPolymorphicSearch) {
	if (focus == null || workingCopies == null) return workingCopies;
	while (!(focus instanceof IEGLProject)) {
		focus = focus.getParent();
	}
	int length = workingCopies.length;
	IWorkingCopy[] result = null;
	int index = -1;
	for (int i=0; i<length; i++) {
		IWorkingCopy workingCopy = workingCopies[i];
		IPath projectOrJar = IndexSelector.getProjectOrJar((IEGLElement)workingCopy).getPath();
		if (!IndexSelector.canSeeFocus(focus, isPolymorphicSearch, projectOrJar)) {
			if (result == null) {
				result = new IWorkingCopy[length-1];
				System.arraycopy(workingCopies, 0, result, 0, i);
				index = i;
			}
		} else if (result != null) {
			result[index++] = workingCopy;
		}
	}
	if (result != null) {
		if (result.length != index) {
			System.arraycopy(result, 0, result = new IWorkingCopy[index], 0, index);
		}
		return result;
	} else {
		return workingCopies;
	}
}
/**
 * Returns a java search scope limited to the given resources.
 * The java elements resulting from a search with this scope will
 * have their underlying resource included in or equals to one of the given
 * resources.
 * <p>
 * Resources must not overlap, for example, one cannot include a folder and its children.
 * </p>
 *
 * @param resources the resources the scope is limited to
 * @return a new java search scope
 * @deprecated Use createEGLSearchScope(IEGLElement[]) instead
 */
public static IEGLSearchScope createEGLSearchScope(IResource[] resources) {
	int length = resources.length;
	IEGLElement[] elements = new IEGLElement[length];
	for (int i = 0; i < length; i++) {
		elements[i] = EGLCore.create(resources[i]);
	}
	return createEGLSearchScope(elements);
}
/**
 * Returns a java search scope limited to the given java elements.
 * The java elements resulting from a search with this scope will
 * be children of the given elements.
 * <p>
 * If an element is an IEGLProject, then the project's source folders, 
 * its jars (external and internal) and its referenced projects (with their source 
 * folders and jars, recursively) will be included.
 * If an element is an IPackageFragmentRoot, then only the package fragments of 
 * this package fragment root will be included.
 * If an element is an IPackageFragment, then only the egl file and class 
 * files of this package fragment will be included. Subpackages will NOT be 
 * included.</p>
 * <p>
 * In other words, this is equivalent to using SearchEngine.createEGLSearchScope(elements, true).</p>
 *
 * @param elements the java elements the scope is limited to
 * @return a new java search scope
 * @since 2.0
 */
public static IEGLSearchScope createEGLSearchScope(IEGLElement[] elements) {
	return createEGLSearchScope(elements, true);
}
/**
 * Returns a java search scope limited to the given java elements.
 * The java elements resulting from a search with this scope will
 * be children of the given elements.
 * 
 * If an element is an IEGLProject, then the project's source folders, 
 * its jars (external and internal) and - if specified - its referenced projects 
 * (with their source folders and jars, recursively) will be included.
 * If an element is an IPackageFragmentRoot, then only the package fragments of 
 * this package fragment root will be included.
 * If an element is an IPackageFragment, then only the egl file and class 
 * files of this package fragment will be included. Subpackages will NOT be 
 * included.
 *
 * @param elements the java elements the scope is limited to
 * @param includeReferencedProjects a flag indicating if referenced projects must be 
 * 									 recursively included
 * @return a new java search scope
 * @since 2.0
 */
public static IEGLSearchScope createEGLSearchScope(IEGLElement[] elements, boolean includeReferencedProjects) {
	EGLSearchScope scope = new EGLSearchScope();
	HashSet visitedProjects = new HashSet(2);
	for (int i = 0, length = elements.length; i < length; i++) {
		IEGLElement element = elements[i];
		if (element != null) {
			try {
				if (element instanceof IEGLProject) {
					scope.add((IEGLProject)element, includeReferencedProjects, visitedProjects);
				} else {
					scope.add(element);
					if(element instanceof EglarPackageFragmentRoot ||
							element instanceof EglarPackageFragment ||
							element instanceof ClassFile) {
						scope.putIntoEglarProjectsMap(element.getPath(), element.getEGLProject());
					}
				}
			} catch (EGLModelException e) {
				// ignore
			}
		}
	}
	return scope;
}

public static IEGLSearchScope createEGLSearchScope(Node[] parts) {
	EGLSearchScope scope = new EGLSearchScope();
	for (int i = 0, length = parts.length; i < length; i++) {
		Node part = parts[i];
		if (part != null) {
			try {
				scope.add(part);
			} catch (EGLModelException e) {
				// ignore
			}
		}
	}
	return scope;
}
/**
 * Returns a search pattern based on a given string pattern. The string patterns support '*' wild-cards.
 * The remaining parameters are used to narrow down the type of expected results.
 *
 * <br>
 *	Examples:
 *	<ul>
 * 		<li>search for case insensitive references to <code>Object</code>:
 *			<code>createSearchPattern("Object", TYPE, REFERENCES, false);</code></li>
 *  	<li>search for case sensitive references to exact <code>Object()</code> constructor:
 *			<code>createSearchPattern("java.lang.Object()", CONSTRUCTOR, REFERENCES, true);</code></li>
 *  	<li>search for implementers of <code>java.lang.Runnable</code>:
 *			<code>createSearchPattern("java.lang.Runnable", TYPE, IMPLEMENTORS, true);</code></li>
 *  </ul>
 * @param stringPattern the given pattern
 * @param searchFor determines the nature of the searched elements
 *	<ul>
 * 		<li><code>IEGLSearchConstants.PART</code>: only look for parts</li>
 *		<li><code>IEGLSearchConstants.FUNCTION</code>: look for functions</li>
 *		<li><code>IEGLSearchConstants.PACKAGE</code>: look for packages</li>
 *	</ul>
 * @param limitTo determines the nature of the expected matches
 *	<ul>
 * 		<li><code>IEGLSearchConstants.DECLARATIONS</code>: will search declarations matching with the corresponding
 * 			element. In case the element is a function, declarations of matching functions in subtypes will also
 *  		be found, allowing to find declarations of abstract functions, etc.</li>
 *
 *		 <li><code>IEGLSearchConstants.REFERENCES</code>: will search references to the given element.</li>
 *
 *		 <li><code>IEGLSearchConstants.ALL_OCCURRENCES</code>: will search for either declarations or references as specified
 *  		above.</li>
 *
 *	</ul>
 *
 * @param isCaseSensitive indicates whether the search is case sensitive or not.
 * @return a search pattern on the given string pattern, or <code>null</code> if the string pattern is ill-formed.
 */
public static ISearchPattern createSearchPattern(String stringPattern, int searchFor, int limitTo, boolean isCaseSensitive) {
	int matchMode;
	if (stringPattern.indexOf('*') != -1 || stringPattern.indexOf('?') != -1) {
		matchMode = IIndexConstants.PATTERN_MATCH;
	} else {
		matchMode = IIndexConstants.EXACT_MATCH;
	}
	return SearchPattern.createPattern(stringPattern, searchFor, limitTo, matchMode, isCaseSensitive);
}
/**
 * Returns a search pattern based on a given EGL element. 
 * The pattern is used to trigger the appropriate search, and can be parameterized as follows:
 *
 * @param element the java element the search pattern is based on
 * @param limitTo determines the nature of the expected matches
 * 	<ul>
 * 		<li><code>IEGLSearchConstants.DECLARATIONS</code>: will search declarations matching with the corresponding
 * 			element. In case the element is a function, declarations of matching functions in subtypes will also
 *  		be found, allowing to find declarations of abstract functions, etc.</li>
 *
 *		 <li><code>IEGLSearchConstants.REFERENCES</code>: will search references to the given element.</li>
 *
 *		 <li><code>IEGLSearchConstants.ALL_OCCURRENCES</code>: will search for either declarations or references as specified
 *  		above.</li>
 *
 *		 <li><code>IEGLSearchConstants.IMPLEMENTORS</code>: for interface, will find all types which implements a given interface.</li>
 *	</ul>
 * @return a search pattern for a java element or <code>null</code> if the given element is ill-formed
 */
//public static ISearchPattern createSearchPattern(IEGLElement element, int limitTo) {
//
//	return SearchPattern.createPattern(element, limitTo);
//}
/**
 * Returns a java search scope with the workspace as the only limit.
 *
 * @return a new workspace scope
 */
public static IEGLSearchScope createWorkspaceScope() {
	return new EGLWorkspaceScope();
}
/**
 * Searches for the EGL element determined by the given signature. The signature
 * can be incomplete. For example, a call like 
 * <code>search(ws, "run()", METHOD,REFERENCES, col)</code>
 * searches for all references to the function <code>run</code>.
 *
 * Note that by default the pattern will be case insensitive. For specifying case s
 * sensitive search, use <code>search(workspace, createSearchPattern(patternString, searchFor, limitTo, true), scope, resultCollector);</code>
 * 
 * @param workspace the workspace
 * @param pattern the pattern to be searched for
 * @param searchFor a hint what kind of EGL element the string pattern represents.
 *  Look into <code>IEGLSearchConstants</code> for valid values
 * @param limitTo one of the following values:
 *	<ul>
 *	  <li><code>IEGLSearchConstants.DECLARATIONS</code>: search 
 *		  for declarations only </li>
 *	  <li><code>IEGLSearchConstants.REFERENCES</code>: search 
 *		  for all references </li>
 *	  <li><code>IEGLSearchConstants.ALL_OCCURENCES</code>: search 
 *		  for both declarations and all references </li>
 *	  <li><code>IEGLSearchConstants.IMPLEMENTORS</code>: search for
 *		  all implementors of an interface; the value is only valid if
 *		  the EGL element represents an interface</li>
 * 	</ul>
 * @param scope the search result has to be limited to the given scope
 * @param resultCollector a callback object to which each match is reported	 
 * @exception EGLModelException if the search failed. Reasons include:
 *	<ul>
 *		<li>the classpath is incorrectly set</li>
 *	</ul>
 */
public void search(IWorkspace workspace, String patternString, int searchFor, int limitTo, IEGLSearchScope scope, IEGLSearchResultCollector resultCollector) throws EGLModelException {
	search(workspace, createSearchPattern(patternString, searchFor, limitTo, true), scope, resultCollector);
}
/**
 * Searches for the given EGL element.
 *
 * @param workspace the workspace
 * @param element the EGL element to be searched for
 * @param limitTo one of the following values:
 *	<ul>
 *	  <li><code>IEGLSearchConstants.DECLARATIONS</code>: search 
 *		  for declarations only </li>
 *	  <li><code>IEGLSearchConstants.REFERENCES</code>: search 
 *		  for all references </li>
 *	  <li><code>IEGLSearchConstants.ALL_OCCURENCES</code>: search 
 *		  for both declarations and all references </li>
 *	  <li><code>IEGLSearchConstants.IMPLEMENTORS</code>: search for
 *		  all implementors of an interface; the value is only valid if
 *		  the EGL element represents an interface</li>
 * 	</ul>
 * @param scope the search result has to be limited to the given scope
 * @param resultCollector a callback object to which each match is reported
 * @exception EGLModelException if the search failed. Reasons include:
 *	<ul>
 *		<li>the element doesn't exist</li>
 *		<li>the classpath is incorrectly set</li>
 *	</ul>
 */
//public void search(IWorkspace workspace, IEGLElement element, int limitTo, IEGLSearchScope scope, IEGLSearchResultCollector resultCollector) throws EGLModelException {
//
//	search(workspace, createSearchPattern(element, limitTo), scope, resultCollector);
//}
/**
 * Searches for matches of a given search pattern. Search patterns can be created using helper
 * functions (from a String pattern or a EGL element) and encapsulate the description of what is
 * being searched (for example, search function declarations in a case sensitive way).
 *
 * @param workspace the workspace
 * @param searchPattern the pattern to be searched for
 * @param scope the search result has to be limited to the given scope
 * @param resultCollector a callback object to which each match is reported
 * @exception EGLModelException if the search failed. Reasons include:
 *	<ul>
 *		<li>the classpath is incorrectly set</li>
 *	</ul>
 */

public void search(IWorkspace workspace, ISearchPattern searchPattern, IEGLSearchScope scope, IEGLSearchResultCollector resultCollector) throws EGLModelException {
	search(workspace, searchPattern, scope, false,false, resultCollector);
}
public void search(IWorkspace workspace, ISearchPattern searchPattern, IEGLSearchScope scope, boolean matchImports,boolean forceQualification,IEGLSearchResultCollector resultCollector) throws EGLModelException {
	
	if (VERBOSE) {
		System.out.println("Searching for " + searchPattern + " in " + scope); //$NON-NLS-1$//$NON-NLS-2$
	}

	/* search is starting */
	resultCollector.aboutToStart();

	try {	
		if (searchPattern == null) return;

		/* initialize progress monitor */
		IProgressMonitor progressMonitor = resultCollector.getProgressMonitor();
		if (progressMonitor != null) {
			progressMonitor.beginTask(EGLModelSearchResources.EngineSearching, 100);
		}

		/* index search */
		PathCollector pathCollector = new PathCollector();
		
		// In the case of a hierarchy scope make sure that the hierarchy is not computed.
		// MatchLocator will filter out elements not in the hierarchy
		SearchPattern pattern = (SearchPattern)searchPattern;
//		if (scope instanceof HierarchyScope) {
//			((HierarchyScope)scope).needsRefresh = false;
//			pattern.needsResolve = true; // force resolve to compute type bindings
//		}

		IndexManager indexManager = ((EGLModelManager)EGLModelManager.getEGLModelManager()).getIndexManager();
		
		int detailLevel = IInfoConstants.PathInfo | IInfoConstants.PositionInfo;
		MatchLocator2 matchLocator = 
			new MatchLocator2(
				pattern, 
				matchImports,
				forceQualification,
				detailLevel, 
				resultCollector, 
				scope,
				progressMonitor == null ? null : new SubProgressMonitor(progressMonitor, 95));
		
		indexManager.performConcurrentJob(
			new PatternSearchJob(
				pattern, 
				scope, 
				pattern.focus,
				false,
				matchLocator.detailLevel,
				pathCollector, 
				indexManager),
			IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
			progressMonitor == null ? null : new SubProgressMonitor(progressMonitor, 5));
			
		/* eliminating false matches and locating them */
		if (progressMonitor != null && progressMonitor.isCanceled()) throw new OperationCanceledException();
		matchLocator.locateMatches(
			pathCollector.getPaths(), 
			workspace,
			filterWorkingCopies(this.workingCopies, pattern.focus, false)
		);
		
		if (progressMonitor != null && progressMonitor.isCanceled()) throw new OperationCanceledException();
		
		if (progressMonitor != null) {
			progressMonitor.done();
		}

		matchLocator.locatePackageDeclarations(workspace);
	} finally {
		/* search has ended */
		resultCollector.done();
	}
}
/**
 * Searches for all top-level types and member types in the given scope.
 * The search can be selecting specific types (given a package or a type name
 * prefix and match modes). 
 * 
 * @param workspace the workspace to search in
 * @param packageName the full name of the package of the searched types, or a prefix for this
 *						package, or a wild-carded string for this package.
 * @param typeName the dot-separated qualified name of the searched type (the qualification include
 *					the enclosing types if the searched type is a member type), or a prefix
 *					for this type, or a wild-carded string for this type.
 * @param matchMode one of
 * <ul>
 *		<li><code>IEGLSearchConstants.EXACT_MATCH</code> if the package name and type name are the full names
 *			of the searched types.</li>
 *		<li><code>IEGLSearchConstants.PREFIX_MATCH</code> if the package name and type name are prefixes of the names
 *			of the searched types.</li>
 *		<li><code>IEGLSearchConstants.PATTERN_MATCH</code> if the package name and type name contain wild-cards.</li>
 * </ul>
 * @param isCaseSensitive whether the search should be case sensitive
 * @param searchFor one of
 * <ul>
 * 		<li><code>IEGLSearchConstants.CLASS</code> if searching for classes only</li>
 * 		<li><code>IEGLSearchConstants.INTERFACE</code> if searching for interfaces only</li>
 * 		<li><code>IEGLSearchConstants.TYPE</code> if searching for both classes and interfaces</li>
 * </ul>
 * @param scope the scope to search in
 * @param nameRequestor the requestor that collects the results of the search
 * @param waitingPolicy one of
 * <ul>
 *		<li><code>IEGLSearchConstants.FORCE_IMMEDIATE_SEARCH</code> if the search should start immediately</li>
 *		<li><code>IEGLSearchConstants.CANCEL_IF_NOT_READY_TO_SEARCH</code> if the search should be cancelled if the
 *			underlying indexer has not finished indexing the workspace</li>
 *		<li><code>IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH</code> if the search should wait for the
 *			underlying indexer to finish indexing the workspace</li>
 * </ul>
 * @param progressMonitor the progress monitor to report progress to, or <code>null</code> if no progress
 *							monitor is provided
 * @exception EGLModelException if the search failed. Reasons include:
 *	<ul>
 *		<li>the classpath is incorrectly set</li>
 *	</ul>
 */
public void searchAllPartNames(
	IWorkspace workspace,
	char[] packageName, 
	char[] typeName,
	int matchMode, 
	boolean isCaseSensitive,
	int partTypes, 
	IEGLSearchScope scope, 
	final IPartNameRequestor nameRequestor,
	int waitingPolicy,
	IProgressMonitor progressMonitor)  throws EGLModelException {

	IndexManager indexManager = ((EGLModelManager)EGLModelManager.getEGLModelManager()).getIndexManager();
		
	SearchPattern pattern = new PartDeclarationPattern(
		packageName,
		null, // do find member types
		typeName,
		(char)partTypes,
		matchMode, 
		isCaseSensitive);
	
	IIndexSearchRequestor searchRequestor = new IndexSearchAdapter(){
		public void acceptPartDeclaration(IPath projectPath, String resourcePath, char[] simpleTypeName, char partType, char[][] enclosingTypeNames, char[] packageName) {
			if (enclosingTypeNames != IIndexConstants.ONE_ZERO_CHAR) { // filter out local and anonymous classes
				nameRequestor.acceptPart(packageName, simpleTypeName, partType, enclosingTypeNames, resourcePath, projectPath);
			}
		}		
	};

	try {
		if (progressMonitor != null) {
			progressMonitor.beginTask(EGLModelSearchResources.EngineSearching, 100);
		}
		indexManager.performConcurrentJob(
			new PatternSearchJob(pattern, scope, IInfoConstants.NameInfo | IInfoConstants.PathInfo, searchRequestor, indexManager),
			waitingPolicy,
			progressMonitor == null ? null : new SubProgressMonitor(progressMonitor, 100));	
	} finally {
		if (progressMonitor != null) {
			progressMonitor.done();
		}
	}
}
/**
 * Find all IPaths to IResources containing given reference name
 * @param workspace
 * @param qualifier
 * @param simpleName
 * @param matchMode
 * @param isCaseSensitive
 * @param scope
 * @param resultCollector
 * @throws EGLModelException
 */

public List searchAllFilesReferencing(
	IWorkspace workspace,
	char[] qualifier, 
	char[] simpleName,
	int matchMode, 
	boolean isCaseSensitive,
	IEGLSearchScope scope,
	IProgressMonitor progressMonitor)  throws EGLModelException {

	SearchPattern pattern = new PartReferencePattern(
		qualifier,
		simpleName,
		IIndexConstants.PART_SUFFIX,
		matchMode,
		isCaseSensitive);
	
	final List resourcePaths = new ArrayList();	
	try {	
		
		
		if (progressMonitor != null) {
			progressMonitor.beginTask(EGLModelSearchResources.EngineSearching, 100);
		}

		/* index search */
		IIndexSearchRequestor searchRequestor = new IndexSearchAdapter(){
			public void acceptPartDeclaration(IPath projectPath, String resourcePath, char[] simpleTypeName, char partType, char[][] enclosingTypeNames, char[] packageName) {
				resourcePaths.add(resourcePath);
			}		
		};
	
		IndexManager indexManager = ((EGLModelManager)EGLModelManager.getEGLModelManager())
										.getIndexManager();
	

		indexManager.performConcurrentJob(
			new PatternSearchJob(
				pattern, 
				scope, 
				pattern.focus,
				false,
				IInfoConstants.PathInfo, 
				searchRequestor, 
				indexManager),
			IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
			progressMonitor == null ? null : new SubProgressMonitor(progressMonitor, 5));
	}
	finally {
		return resourcePaths;
	}
	
}
/**
 * Searches for all declarations of the types referenced in the given element.
 * The element can be a egl file, a type, or a function.
 * Reports the type declarations using the given collector.
 * <p>
 * Consider the following code:
 * <code>
 * <pre>
 *		class A {
 *		}
 *		class B extends A {
 *		}
 *		interface I {
 *		  int VALUE = 0;
 *		}
 *		class X {
 *			void test() {
 *				B b = new B();
 *				this.foo(b, I.VALUE);
 *			};
 *		}
 * </pre>
 * </code>
 * then searching for declarations of referenced types in function <code>X.test()</code>
 * would collect the class <code>B</code> and the interface <code>I</code>.
 * </p>
 *
 * @param workspace the workspace
 * @param enclosingElement the function, type, or egl file to be searched in
 * @param resultCollector a callback object to which each match is reported
 * @exception EGLModelException if the search failed. Reasons include:
 *	<ul>
 *		<li>the element doesn't exist</li>
 *		<li>the classpath is incorrectly set</li>
 *	</ul>
 */	
//public void searchDeclarationsOfReferencedParts(IWorkspace workspace, IEGLElement enclosingElement, IEGLSearchResultCollector resultCollector) throws EGLModelException {
//	SearchPattern pattern = new DeclarationOfReferencedPartsPattern(enclosingElement);
//	IEGLSearchScope scope = createEGLSearchScope(new IEGLElement[] {enclosingElement});
//	IResource resource = this.getResource(enclosingElement);
//	if (resource instanceof IFile) {
//		if (VERBOSE) {
//			System.out.println("Searching for " + pattern + " in " + resource.getFullPath()); //$NON-NLS-1$//$NON-NLS-2$
//		}
//		MatchLocator locator = new MatchLocator2(
//			pattern,
//			IInfoConstants.DeclarationInfo,
//			resultCollector,
//			scope,
//			resultCollector.getProgressMonitor());
//		locator.locateMatches(
//			new String[] {resource.getFullPath().toString()}, 
//			workspace,
//			this.getWorkingCopies(enclosingElement));
//	} else {
//		search(workspace, pattern, scope, resultCollector);
//	}
//}
/**
 * Searches for all declarations of the functions invoked in the given element.
 * The element can be a egl file, a type, or a function.
 * Reports the function declarations using the given collector.
 * <p>
 * Consider the following code:
 * <code>
 * <pre>
 *		class A {
 *			void foo() {};
 *			void bar() {};
 *		}
 *		class B extends A {
 *			void foo() {};
 *		}
 *		class X {
 *			void test() {
 *				A a = new B();
 *				a.foo();
 *				B b = (B)a;
 *				b.bar();
 *			};
 *		}
 * </pre>
 * </code>
 * then searching for declarations of sent messages in function 
 * <code>X.test()</code> would collect the functions
 * <code>A.foo()</code>, <code>B.foo()</code>, and <code>A.bar()</code>.
 * </p>
 *
 * @param workspace the workspace
 * @param enclosingElement the function, type, or egl file to be searched in
 * @param resultCollector a callback object to which each match is reported
 * @exception EGLModelException if the search failed. Reasons include:
 *	<ul>
 *		<li>the element doesn't exist</li>
 *		<li>the classpath is incorrectly set</li>
 *	</ul>
 */	
//public void searchDeclarationsOfReferencedFunctions(IWorkspace workspace, IEGLElement enclosingElement, IEGLSearchResultCollector resultCollector) throws EGLModelException {
//	SearchPattern pattern = new DeclarationOfReferencedFunctionsPattern(enclosingElement);
//	IEGLSearchScope scope = createEGLSearchScope(new IEGLElement[] {enclosingElement});
//	IResource resource = this.getResource(enclosingElement);
//	if (resource instanceof IFile) {
//		if (VERBOSE) {
//			System.out.println("Searching for " + pattern + " in " + resource.getFullPath()); //$NON-NLS-1$//$NON-NLS-2$
//		}
//		MatchLocator locator = new MatchLocator2(
//			pattern,
//			IInfoConstants.DeclarationInfo,
//			resultCollector,
//			scope,
//			resultCollector.getProgressMonitor());
//		locator.locateMatches(
//			new String[] {resource.getFullPath().toString()}, 
//			workspace,
//			this.getWorkingCopies(enclosingElement));
//	} else {
//		search(workspace, pattern, scope, resultCollector);
//	}
//}

}
