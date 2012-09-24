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
package org.eclipse.edt.ide.core.internal.search.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.ide.core.internal.model.Util;
import org.eclipse.edt.ide.core.internal.model.util.HashtableOfLong;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.ICompiledFileUnit;
import org.eclipse.edt.ide.core.search.IEGLSearchResultCollector;
import org.eclipse.edt.mof.egl.FunctionPart;

/**
 * A set of matches and potential matches.
 */
public class MatchingNodeSet { 

	MatchLocator2 locator;
	int matchContainer;
	boolean cuHasBeenResolved = false;

	/**
	 * Set of matching ast nodes that don't need to be resolved.
	 */
	Map matchingNodes = new HashMap(5);
	HashtableOfLong matchingNodesKeys = new HashtableOfLong(5);

	/**
	 * Set of potential matching ast nodes. They need to be resolved
	 * to determine if they really match the search pattern.
	 */
	Map potentialMatchingNodes = new HashMap(5);
	HashtableOfLong potentialMatchingNodesKeys = new HashtableOfLong(5);

	public MatchingNodeSet(MatchLocator2 locator) {
		super();
		this.locator = locator;
		this.matchContainer = locator.matchContainer();
	}
	
	public void addPossibleMatch(Node node) {

		// remove existing node at same position from set
		// (case of recovery that created the same node several time
		// see http://bugs.eclipse.org/bugs/show_bug.cgi?id=29366)
		long key = (((long) node.getOffset()) << 32) + (node.getOffset() + node.getLength());
		Node existing = (Node) this.potentialMatchingNodesKeys.get(key);
		if (existing != null && existing.getClass().equals(node.getClass())) {
			this.potentialMatchingNodes.remove(existing);
		}

		// add node to set
		this.potentialMatchingNodes.put(node, new Integer(SearchPattern.POSSIBLE_MATCH));
		this.potentialMatchingNodesKeys.put(key, node);
	}
	public void addTrustedMatch(Node node) {

		// remove existing node at same position from set
		// (case of recovery that created the same node several time
		// see http://bugs.eclipse.org/bugs/show_bug.cgi?id=29366)
		long key = (((long) node.getOffset()) << 32) + (node.getOffset() + node.getLength());
		Node existing = (Node) this.matchingNodesKeys.get(key);
		if (existing != null && existing.getClass().equals(node.getClass())) {
			this.matchingNodes.remove(existing);
		}

		// add node to set
		this.matchingNodes.put(node, new Integer(SearchPattern.ACCURATE_MATCH));
		this.matchingNodesKeys.put(key, node);
	}

	public void addInaccurateMatch(Node node) {

		// remove existing node at same position from set
		// (case of recovery that created the same node several time
		// see http://bugs.eclipse.org/bugs/show_bug.cgi?id=29366)
		long key = (((long) node.getOffset()) << 32) + (node.getOffset() + node.getLength());
		Node existing = (Node) this.matchingNodesKeys.get(key);
		if (existing != null && existing.getClass().equals(node.getClass())) {
			this.matchingNodes.remove(existing);
		}

		// add node to set
		this.matchingNodes.put(node, new Integer(SearchPattern.INACCURATE_MATCH));
		this.matchingNodesKeys.put(key, node);
	}

	public int getMatchingLevel(Node node){
		return this.locator.matchCheck(node);
	}
	
	public int isMatchingFunctionType(Name node,FunctionPart partBinding) {
		int level = getMatchingLevel(node);
		if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
			return locator.matchesFunctionPartType(node,partBinding);
			
		}
		return level;
	}
	
	public int isMatchingNestedFunctionPart(NestedFunction node) {
		int level = getMatchingLevel(node);
		if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
			return locator.matchesFunctionPartType(node.getName(),null);
			
		}
		return level;
	}
	
	public int isMatchingType(Name node,org.eclipse.edt.mof.egl.Part partBinding) {
		int level = getMatchingLevel(node);
		if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
			return locator.matchesPartType(node,partBinding);
			
		}
		return level;
	}
	
	public int isMatchingPart(Part part) {
		int level = getMatchingLevel(part);
		if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
			return locator.matchesPart(part);
			
		}
		return level;
	}
	
	public void checkMatching(Node node) {
		this.locator.matchCheck(node, this);
	}
	public boolean isEmpty() {
		return this.potentialMatchingNodes.size() == 0 && this.matchingNodes.size() == 0;
	}
	/**
	 * Returns the matching nodes that are in the given range.
	 */
	private Node[] matchingNodes(int start, int end) {
		return this.nodesInRange(start, end, this.matchingNodes);
	}
	public boolean needsResolve() {
		return this.potentialMatchingNodes.size() > 0;
	}
	/**
	 * Returns the matching nodes that are in the given range in the source order.
	 */
	private Node[] nodesInRange(int start, int end, Map set) {
		// collect nodes in the given range
		ArrayList nodes = new ArrayList();
		for (Iterator keys = set.keySet().iterator(); keys.hasNext();) {
			Node node = (Node)keys.next();
			if (start <= node.getOffset() && (node.getOffset() + node.getLength()) <= end) {
				nodes.add(node);
			}
		}
		Node[] result = new Node[nodes.size()];
		nodes.toArray(result);

		// sort nodes by source starts
		Util.Comparer comparer = new Util.Comparer() {
			public int compare(Object o1, Object o2) {
				Node node1 = (Node) o1;
				Node node2 = (Node) o2;
				return node1.getOffset() - node2.getOffset();
			}
		};
		Util.sort(result, comparer);
			
		return result;
	}
	public Integer removePossibleMatch(Node node) {
		long key = (((long) node.getOffset()) << 32) + (node.getOffset() + node.getLength());
		this.potentialMatchingNodesKeys.put(key, null);
		return (Integer) this.potentialMatchingNodes.remove(node);
	}
	public Integer removeTrustedMatch(Node node) {
		long key = (((long) node.getOffset()) << 32) + (node.getOffset() + node.getLength());
		this.matchingNodesKeys.put(key, null);
		return (Integer) this.matchingNodes.remove(node);
	}
//	/**
//	 * Visit the given method declaration and report the nodes that match exactly the
//	 * search pattern (ie. the ones in the matching nodes set)
//	 * Note that the method declaration has already been checked.
//	 */
//	private void reportMatching(AbstractMethodDeclaration method, IJavaElement parent, boolean typeInHierarchy) throws CoreException {
//		// declaration in this method
//		// (NB: declarations must be searched first (see bug 20631 Declaration of local binary type not found)
//		if ((method.bits & AstNode.HasLocalTypeMASK) != 0) {
//			LocalDeclarationVisitor localDeclarationVisitor = new LocalDeclarationVisitor();
//			localDeclarationVisitor.enclosingElement =
//				(parent instanceof IType) ? this.locator.createMethodHandle(method, (IType) parent) : parent;
//			try {
//				method.traverse(localDeclarationVisitor, (ClassScope) null);
//			} catch (WrappedCoreException e) {
//				throw e.coreException;
//			}
//		}
//
//		// references in this method
//		if (typeInHierarchy) {
//			AstNode[] nodes = this.matchingNodes(method.declarationSourceStart, method.declarationSourceEnd);
//			for (int i = 0; i < nodes.length; i++) {
//				AstNode node = nodes[i];
//				Integer level = (Integer) this.matchingNodes.get(node);
//				if ((this.matchContainer & SearchPattern.METHOD) != 0) {
//					this.locator.reportReference(
//						node,
//						method,
//						parent,
//						level.intValue() == SearchPattern.ACCURATE_MATCH
//							? IJavaSearchResultCollector.EXACT_MATCH
//							: IJavaSearchResultCollector.POTENTIAL_MATCH);
//					this.matchingNodes.remove(node);
//				}
//			}
//		}
//		if (this.potentialMatchingNodes(method.declarationSourceStart, method.declarationSourceEnd).length == 0) {
//			// no need to resolve the statements in the method
//			method.statements = null;
//		}
//	}
	/**
	 * Visit the given parse tree and report the nodes that match exactly the
	 * search pattern.
	 */
	public void reportMatching(ICompiledFileUnit unit) throws CoreException {
		if (this.cuHasBeenResolved) {
			// move the potential matching nodes that exactly match the search pattern to the matching nodes set
			// EGLTODO: Support this?
			for (Iterator potentialMatches = this.potentialMatchingNodes.keySet().iterator(); potentialMatches.hasNext();) {
				Node node = (Node) potentialMatches.next();
				int level;
				if (node instanceof ImportDeclaration) {
					// special case for import refs: they don't know their binding

					// import ref cannot be in the hirarchy of a type
//					if (this.locator.hierarchyResolver != null)
//						continue;

//					IEGLImportStatement importRef = (IEGLImportStatement) node;
//					Binding binding;
//					if (importRef.isOnDemandImportStatement()) {
//						binding = unit.scope.getTypeOrPackage(CharOperation.subarray(importRef.tokens, 0, importRef.tokens.length));
//					} else {
//						binding = unit.scope.getTypeOrPackage(importRef.tokens);
//					}
//					level = this.locator.pattern.matchLevel(binding);
//
//					if (level == SearchPattern.ACCURATE_MATCH || level == SearchPattern.INACCURATE_MATCH) {
//						// create defining import handle
//						IEGLElement importHandle = this.locator.createImportHandle(importRef);
//						this.locator.pattern.matchReportImportRef(
//							importRef,
//							binding,
//							importHandle,
//							level == SearchPattern.ACCURATE_MATCH
//								? IEGLSearchResultCollector.EXACT_MATCH
//								: IEGLSearchResultCollector.POTENTIAL_MATCH,
//							this.locator);
//					}
				} else {
					level = this.locator.matchLevel(node, true);
					if (level == SearchPattern.ACCURATE_MATCH || level == SearchPattern.INACCURATE_MATCH) {
						this.matchingNodes.put(node, new Integer(level));
					}
				}
			}
			this.potentialMatchingNodes = new HashMap();
		}

		// package declaration
		PackageDeclaration pkg = unit.getFile().getPackageDeclaration();
		Integer level;
		if (pkg != null && (level = (Integer) this.matchingNodes.remove(pkg)) != null) {
			if ((this.matchContainer & SearchPattern.EGL_FILE) != 0) {
				this.locator.reportPackageDeclaration(pkg);
			}
		}

		// import declarations
		if (!this.cuHasBeenResolved) {
			ImportDeclaration[] imports = (ImportDeclaration[])unit.getFile().getImportDeclarations().toArray(new ImportDeclaration[unit.getFile().getImportDeclarations().size()]);
			if (imports != null) {
				for (int i = 0; i < imports.length; i++) {
					ImportDeclaration importRef = imports[i];
					Name importName = importRef.getName();
					Node[] importNodes = nodesInRange(importName.getOffset(),importName.getOffset() + importName.getLength(),this.matchingNodes);
					if (importNodes != null && importNodes.length > 0){
						Node node = importNodes[0];
						if ((level = (Integer) this.matchingNodes.remove(node)) != null) {
							if ((this.matchContainer & SearchPattern.EGL_FILE) != 0) {
								this.locator.reportImport(
									importRef,
									level.intValue() == SearchPattern.ACCURATE_MATCH
										? IEGLSearchResultCollector.EXACT_MATCH
										: IEGLSearchResultCollector.POTENTIAL_MATCH);
							}
						}	
					}
					
				}
			}
		} // else import declarations have already been processed above

		// parts
		Part[] parts = (Part[])unit.getFileParts().toArray(new Part[unit.getFileParts().size()]);
		if (parts != null) {
			for (int i = 0; i < parts.length; i++) {
				Part part = parts[i];
				if ((level = (Integer) this.matchingNodes.remove(part)) != null) {
					if ((this.matchContainer & SearchPattern.EGL_FILE) != 0) {
						this.locator.reportPartDeclaration(
							part,
							null,
							level.intValue() == SearchPattern.ACCURATE_MATCH
								? IEGLSearchResultCollector.EXACT_MATCH
								: IEGLSearchResultCollector.POTENTIAL_MATCH);
					}
				}
				this.reportMatching(part, null);
			}
		}
	}
	
	/**
	 * Visit the given field declaration and report the nodes that match exactly the
	 * search pattern (ie. the ones in the matching nodes set)
	 * Note that the field declaration has already been checked.
	 */
	private void reportMatchingField(Node field, IEGLElement parent) // IEGLPart part) , boolean typeInHierarchy)
		throws CoreException {
		// EGLTODO: Need typeInHierarchy?
		//if (typeInHierarchy) {
			Node[] nodes = this.matchingNodes(((Node)field).getOffset(), ((Node)field).getOffset() + ((Node)field).getLength());
			for (int i = 0; i < nodes.length; i++) {
				Node node = nodes[i];
				Integer level = (Integer) this.matchingNodes.get(node);
				if ((this.matchContainer & SearchPattern.FIELD) != 0) {
					this.locator.reportFieldReference(
						node,
						//part,
						field,
						parent,
						level.intValue() == SearchPattern.ACCURATE_MATCH
							? IEGLSearchResultCollector.EXACT_MATCH
							: IEGLSearchResultCollector.POTENTIAL_MATCH);
					this.matchingNodes.remove(node);
				}
			}
		//}
	}
	
	
	private void reportMatchingUse(Node use, IEGLElement parent, Part part) //, boolean typeInHierarchy)
		throws CoreException {
		// EGLTODO: Need typeInHierarchy?
		//if (typeInHierarchy) {
		
		List useNames;
		if(use instanceof UseStatement){
			useNames = ((UseStatement)use).getNames();
		 
			
			for(int i=0; i<useNames.size(); i++)
			{
				Node name = (Node)useNames.get(i);
				Node[] nodes = this.matchingNodes(name.getOffset(), (name.getOffset() + name.getLength()));
				for (int j = 0; j < nodes.length; j++) {
					Node node = nodes[j];
					Integer level = (Integer) this.matchingNodes.get(node);
					if ((this.matchContainer & SearchPattern.USE) != 0) {
						this.locator.reportUseReference(
							node,
							part,
							(Name)name,
							parent,
							level.intValue() == SearchPattern.ACCURATE_MATCH
								? IEGLSearchResultCollector.EXACT_MATCH
								: IEGLSearchResultCollector.POTENTIAL_MATCH);
						this.matchingNodes.remove(node);
					}
				}
			}
		}
	}
	
	/**
	 * Visit the given type declaration and report the nodes that match exactly the
	 * search pattern (ie. the ones in the matching nodes set)
	 * Note that the type declaration has already been checked.
	 */
	public void reportMatching(Part part, IEGLElement parent) throws CoreException {

		// filter out element not in hierarchy scope
		//boolean typeInHierarchy = part == null || this.locator.typeInHierarchy(type.binding);

		// create type handle
		IEGLElement enclosingElement;
		if (parent == null) {
			enclosingElement = this.locator.createPartHandle(part.getName().getCanonicalName());
		} else if (parent instanceof IPart) {
			enclosingElement = this.locator.createPartHandle((IPart) parent, part.getName().getCanonicalName());
			if (enclosingElement == null)
				return;
		} else {
			enclosingElement = parent;
		}
		
		switch (part.getPartType()) {
			case Part.PROGRAM :
				reportMatchingProgram((Program) part, enclosingElement);
				break;
			case Part.LIBRARY :
				reportMatchingLibrary((Library) part, enclosingElement);
				break;
			case Part.RECORD :
				reportMatchingRecord((Record)part, enclosingElement);
				break;				
			case Part.HANDLER :
				reportMatchingHandler((Handler) part, enclosingElement);
				break;
			case Part.SERVICE :
				reportMatchingService((Service) part, enclosingElement);
				break;
			case Part.INTERFACE :
				reportMatchingInterface((Interface) part, enclosingElement);
				break;
			case Part.DELEGATE :
				reportMatchingDelegate((Delegate) part, enclosingElement);
				break;
			case Part.EXTERNALTYPE :
				reportMatchingExternalType((ExternalType) part, enclosingElement);
				break;
			case Part.ENUMERATION :
				reportMatchingEnumeration((Enumeration) part, enclosingElement);
				break;
			case Part.CLASS :
				reportMatchingClass((EGLClass) part, enclosingElement);
				break;
		}
		
		Name subtype = part.getSubType();
		if (subtype != null) {
			reportMatchingField(subtype, enclosingElement);
		}
		
		reportMatchingSettings(part,enclosingElement);
	}
	
	private void reportMatchingDelegate(final Delegate delegate, IEGLElement enclosingElement) throws CoreException{
//		this.reportMatchingFields(delegate, enclosingElement);
		Node[] nodes = this.matchingNodes(delegate.getOffset(), delegate.getOffset() + delegate.getLength());
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			Integer level = (Integer) this.matchingNodes.get(node);
			if ((this.matchContainer & SearchPattern.FIELD) != 0) {
				this.locator.reportReference(
					node,
					//part,
					delegate,
					enclosingElement,
					level.intValue() == SearchPattern.ACCURATE_MATCH
						? IEGLSearchResultCollector.EXACT_MATCH
						: IEGLSearchResultCollector.POTENTIAL_MATCH);
				this.matchingNodes.remove(node);
			}
		}
		
		Integer level;
		if ((level = (Integer) this.matchingNodes.remove(delegate)) != null) {
			if ((this.matchContainer & SearchPattern.PART) != 0) {
				this.locator.reportPartDeclaration(
						delegate,
					enclosingElement,
					level.intValue() == SearchPattern.ACCURATE_MATCH
						? IEGLSearchResultCollector.EXACT_MATCH
						: IEGLSearchResultCollector.POTENTIAL_MATCH);
			}
		}
	}
	
	private void reportMatchingExternalType(final ExternalType externalType, IEGLElement enclosingElement) throws CoreException{
		reportMatchingFields(externalType, enclosingElement);
		reportMatchingNestedFunctions(externalType, enclosingElement);
		for(Iterator iter = externalType.getExtendedTypes().iterator(); iter.hasNext();) {
			Name name = (Name) iter.next();
			Integer level = (Integer) this.matchingNodes.get(name);
			if (( level != null && (this.matchContainer & SearchPattern.FIELD) != 0)) {
				this.locator.reportFieldReference(
					name,
					//part,
					externalType,
					enclosingElement,
					level.intValue() == SearchPattern.ACCURATE_MATCH
						? IEGLSearchResultCollector.EXACT_MATCH
						: IEGLSearchResultCollector.POTENTIAL_MATCH);
				this.matchingNodes.remove(name);
			}
		}
		
	}
	
	private void reportMatchingEnumeration(final Enumeration enumeration, IEGLElement enclosingElement) throws CoreException{

		//TODO search
	}
	
	
	private void reportMatchingProgram(final Program program, final IEGLElement enclosingElement) throws CoreException{		
		reportMatchingFields(program, enclosingElement);
		reportMatchingUses(program, enclosingElement);
		reportMatchingNestedFunctions(program, enclosingElement);
	}
	
	private void reportMatchingLibrary(final Library library, final IEGLElement enclosingElement) throws CoreException{		
		reportMatchingFields(library, enclosingElement);
		reportMatchingUses(library, enclosingElement);
		reportMatchingNestedFunctions(library, enclosingElement);		 
	}
	
	private void reportMatchingNestedFunctions(Node node,final IEGLElement enclosingElement){
		node.accept(new AbstractASTVisitor(){
			public boolean visit(NestedFunction nestedFunction) {
				try {
					reportMatchingFunction(nestedFunction, enclosingElement);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return false;
			}
		});

	}
	
	private void reportMatchingHandler(final Handler handler, IEGLElement enclosingElement) throws CoreException{		
		reportMatchingFields(handler, enclosingElement);
		reportMatchingUses(handler, enclosingElement);
		reportMatchingNestedFunctions(handler, enclosingElement);
	}

	private void reportMatchingClass(final EGLClass eglClass, IEGLElement enclosingElement) throws CoreException{		
		reportMatchingFields(eglClass, enclosingElement);
		reportMatchingUses(eglClass, enclosingElement);
		reportMatchingNestedFunctions(eglClass, enclosingElement);
	}

	private void reportMatchingService(final Service service, final IEGLElement enclosingElement) throws CoreException{		
	    reportMatchingFields(service, enclosingElement);
		reportMatchingUses(service, enclosingElement);
		reportMatchingNestedFunctions(service, enclosingElement);
		reportMatchingImplementedInterfaces(service, enclosingElement);
	}
	
	private void reportMatchingImplementedInterfaces(Service service, IEGLElement enclosingElement) throws CoreException{
	    List definedInterfaceNames = service.getImplementedInterfaces();
		for (Iterator iter = definedInterfaceNames.iterator(); iter.hasNext();) {
            Name interfaceName = (Name) iter.next();
            
            int rangeOffset = ((Node)interfaceName).getOffset();
			int rangeLength = ((Node)interfaceName).getLength();
			
			Integer level;
			// Check for interfaces that are referenced by this service
			Node[] nodes = this.matchingNodes(rangeOffset, rangeOffset + rangeLength);
			for (int i = 0; i < nodes.length; i++) {
				Node node = nodes[i];
				level = (Integer) this.matchingNodes.get(node);
				if ((this.matchContainer & SearchPattern.PART) != 0) {
					this.locator.reportImplementsReference(
						node,
						//part,
						service,
						interfaceName,
						enclosingElement,
						level.intValue() == SearchPattern.ACCURATE_MATCH
							? IEGLSearchResultCollector.EXACT_MATCH
							: IEGLSearchResultCollector.POTENTIAL_MATCH);
					this.matchingNodes.remove(node);
				}
			}
		}
	}
	
	private void reportMatchingInterface(final Interface intrface, IEGLElement enclosingElement) throws CoreException{
		reportMatchingNestedFunctions(intrface, enclosingElement);
	}
	
	private void reportMatchingRecord(Record part, IEGLElement enclosingElement)throws CoreException{
		reportMatchingFields(part,enclosingElement);
	}
				
	private void reportMatchingFunction(NestedFunction function, IEGLElement enclosingElement) throws CoreException {
		reportMatchingFields(function, enclosingElement);
	
		Integer level;
		// Check for fields that are being referenced within this function
		Node[] nodes = this.matchingNodes(function.getOffset(), function.getOffset() + function.getLength());
		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			level = (Integer) this.matchingNodes.get(node);
			if ((this.matchContainer & SearchPattern.FIELD) != 0) {
				this.locator.reportReference(
					node,
					//part,
					function,
					enclosingElement,
					level.intValue() == SearchPattern.ACCURATE_MATCH
						? IEGLSearchResultCollector.EXACT_MATCH
						: IEGLSearchResultCollector.POTENTIAL_MATCH);
				this.matchingNodes.remove(node);
			}
		}
		
		// check to see if this function declaration is being searched for
		if ((level = (Integer) this.matchingNodes.remove(function)) != null) {
			if ((this.matchContainer & SearchPattern.FUNCTION) != 0) {
				this.locator.reportFunctionDeclaration(
					function,
					enclosingElement,
					level.intValue() == SearchPattern.ACCURATE_MATCH
						? IEGLSearchResultCollector.EXACT_MATCH
						: IEGLSearchResultCollector.POTENTIAL_MATCH);
			}
		}
	}
	
	private void reportMatchingFields(Node node, final IEGLElement enclosingElement) throws CoreException{
		node.accept(new AbstractASTVisitor(){
			Integer level;
			private void reportNamedNode(Node node,IEGLElement parent){
				try {
					if ((level = (Integer) matchingNodes.remove(node)) != null
							//&& typeInHierarchy // EGLTODO: Type Hierarchy?
							&& (matchContainer & SearchPattern.PART) != 0) {
								// Only report the field if it has a name (i.e. not filler structure items
								locator.reportField(
									node,
									enclosingElement,
									level.intValue() == SearchPattern.ACCURATE_MATCH
										? IEGLSearchResultCollector.EXACT_MATCH
										: IEGLSearchResultCollector.POTENTIAL_MATCH);

					}
					
					reportMatchingField(node, parent);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			
			public boolean visit(ClassDataDeclaration classDataDeclaration) {
				reportNamedNode(classDataDeclaration,enclosingElement);
				return false;
			}
			
			public boolean visit(FunctionParameter functionParameter) {
				reportNamedNode(functionParameter,enclosingElement);
				return false;
			}
			
			public boolean visit(StructureItem structureItem){
				reportNamedNode(structureItem,enclosingElement);
				return false;
			}
		});

	}
	
	private void reportMatchingUses(final Part part, final IEGLElement enclosingElement) throws CoreException {
		part.accept(new AbstractASTVisitor(){
			public boolean visit(UseStatement useStatement) {
				try {
					reportMatchingUse(useStatement,enclosingElement,part);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return false;
			}
		});

	}
	
	private void reportMatchingSettings(final Part part,final IEGLElement enclosingElement) throws CoreException {
		part.accept(new DefaultASTVisitor(){
			public boolean visit(Delegate delegate) {
				return true;
			}
			
			public boolean visit(ExternalType externalType) {
				return true;
			}

			public boolean visit(Interface interfaceNode) {
				return true;
			}

			public boolean visit(Library library) {
				return true;
			}

			public boolean visit(Program program) {
				return true;
			}

			public boolean visit(Record record) {
				return true;
			}

			public boolean visit(Service service) {
				return true;
			}
			
			public boolean visit(Handler handler) {
				return true;
			}

			public boolean visit(Enumeration enumeration) {
				return true;
			}

			public boolean visit(SettingsBlock settingsBlock) {
				reportMatchingSettingsBlock(part,enclosingElement,settingsBlock);
				return false;
			}
		});
	}
	
	private void reportMatchingSettingsBlock(final Part part,final IEGLElement enclosingElement,SettingsBlock settingsBlock){
		List decls = settingsBlock.getSettings();
		for (Iterator iter = decls.iterator(); iter.hasNext();) {
			Node decl = (Node) iter.next();
			decl.accept(new DefaultASTVisitor() {

				private void reportNamedNode(Node node,IEGLElement parent){
					try {
						reportMatchingField(node, parent);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}

				public boolean visit(SetValuesExpression setValuesExpression) {
					Expression expr = setValuesExpression.getExpression();
					if (expr instanceof AnnotationExpression) {
						Name name = ((AnnotationExpression)expr).getName();
						if (name.resolveType() != null){
							reportNamedNode(name,enclosingElement);
						}
						else if (name.resolveElement() != null) {
							reportNamedNode(name,enclosingElement);
						}
					}
					
					SettingsBlock block = setValuesExpression.getSettingsBlock();
					reportMatchingSettingsBlock(part,enclosingElement,block);
					return false;
				}

				public boolean visit(Assignment assignment) {
					Expression expr = assignment.getRightHandSide();
					expr.accept(new AbstractASTExpressionVisitor(){
						public boolean visitName(Name name) {
							if (name.resolveType() != null){
								reportNamedNode(name,enclosingElement);
							}
						    return false;
						}
						
						public boolean visitExpression(Expression expression) {
						    return true;
						}
					});
					assignment.getLeftHandSide().accept(new AbstractASTExpressionVisitor(){
						public boolean visitName(Name name) {
							if (name.resolveType() != null){
								reportNamedNode(name,enclosingElement);
							}
						    return false;
						}
					});
					
					
					return false;
				}
			});

		}

	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Exact matches:"); //$NON-NLS-1$
		for (Iterator iter = this.matchingNodes.keySet().iterator(); iter.hasNext();) {
			result.append("\n"); //$NON-NLS-1$
			Node node = (Node) iter.next();
			Object value = this.matchingNodes.get(node);
			if (value instanceof Integer) {
				result.append('\t');
				int accuracy = ((Integer) value).intValue();
				switch (accuracy) {
					case SearchPattern.IMPOSSIBLE_MATCH :
						result.append("IMPOSSIBLE_MATCH: "); //$NON-NLS-1$
						break;
					case SearchPattern.POSSIBLE_MATCH :
						result.append("POSSIBLE_MATCH: "); //$NON-NLS-1$
						break;
					case SearchPattern.INACCURATE_MATCH :
						result.append("INACCURATE_MATCH: "); //$NON-NLS-1$
						break;
					case SearchPattern.ACCURATE_MATCH :
						result.append("ACCURATE_MATCH: "); //$NON-NLS-1$
						break;
				}
			}
			result.append(node.toString());
		}
		result.append("\nPotential matches:"); //$NON-NLS-1$
		for (Iterator iter = this.potentialMatchingNodes.keySet().iterator(); iter.hasNext();) {
			result.append("\n"); //$NON-NLS-1$
			Node node = (Node) iter.next();
			Object value = this.potentialMatchingNodes.get(node);
			if (value instanceof Integer) {
				result.append("\t"); //$NON-NLS-1$
				int accuracy = ((Integer) value).intValue();
				switch (accuracy) {
					case SearchPattern.IMPOSSIBLE_MATCH :
						result.append("IMPOSSIBLE_MATCH: "); //$NON-NLS-1$
						break;
					case SearchPattern.POSSIBLE_MATCH :
						result.append("POSSIBLE_MATCH: "); //$NON-NLS-1$
						break;
					case SearchPattern.INACCURATE_MATCH :
						result.append("INACCURATE_MATCH: "); //$NON-NLS-1$
						break;
					case SearchPattern.ACCURATE_MATCH :
						result.append("ACCURATE_MATCH: "); //$NON-NLS-1$
						break;
				}
			}
			result.append(node.toString());
		}
		return result.toString();
	}
}
