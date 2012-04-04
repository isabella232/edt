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

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.CompiledFileUnit;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.model.BinaryPart;
import org.eclipse.edt.ide.core.internal.model.ClassFile;
import org.eclipse.edt.ide.core.internal.model.EGLElement;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.model.EGLModelManager;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.model.HandleFactory;
import org.eclipse.edt.ide.core.internal.model.Openable;
import org.eclipse.edt.ide.core.internal.model.Util;
import org.eclipse.edt.ide.core.internal.model.WorkingCopy;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IField;
import org.eclipse.edt.ide.core.model.IFunction;
import org.eclipse.edt.ide.core.model.IMember;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IUseDeclaration;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.model.Signature;
import org.eclipse.edt.ide.core.search.ICompiledFileUnit;
import org.eclipse.edt.ide.core.search.IEGLSearchResultCollector;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;

public class MatchLocator2 {//extends MatchLocator { 
	
	private MatchVisitor matchVisitor;
	
	// implements IPartRequestor {
	public static final int MAX_AT_ONCE = 500;
	public static final PotentialMatch[] NO_POTENTIAL_MATCH = new PotentialMatch[0];
	
	// permanent state
	private SearchPattern pattern;
	private boolean forceQualification;
	public int detailLevel;
	public boolean matchImports;
	public IEGLSearchResultCollector collector;
	public IEGLSearchScope scope;
	public IProgressMonitor progressMonitor;

	public IWorkingCopy[] workingCopies;
	public HandleFactory handleFactory; 

	// the following is valid for the current project
//	public Parser parser;
//	public INameEnvironment nameEnvironment;
	//public NameLookup nameLookup;
	//public LookupEnvironment lookupEnvironment;
	//public HierarchyResolver hierarchyResolver;
	boolean compilationAborted;

	// cache of all super type names if scope is hierarchy scope
	//public char[][][] allSuperTypeNames;

	public PotentialMatchSet potentialMatches;

	public int parseThreshold = -1;
	//public CompilerOptions options;
	
	// management of unit to be processed
	public ICompiledFileUnit[] unitsToProcess;
	public PotentialMatch[] matchesToProcess;
	public int totalUnits; // (totalUnits-1) gives the last unit in unitToProcess
	
	public PotentialMatch currentPotentialMatch;
	
	public MatchLocator2(
		SearchPattern pattern,
		boolean matchImports,
		boolean forceQualification,
		int detailLevel,
		IEGLSearchResultCollector collector,
		IEGLSearchScope scope,
		IProgressMonitor progressMonitor) {
			
		this.pattern = pattern;
		this.detailLevel = detailLevel;
		this.collector = collector;
		this.scope = scope;
		this.progressMonitor = progressMonitor;
		this.matchImports = matchImports;
		this.forceQualification = forceQualification;
	}
//	/**
//	 * Add an additional compilation unit into the loop
//	 *  ->  build compilation unit declarations, their bindings and record their results.
//	 */
//	public void accept(IEGLFile sourceUnit) {
//		// Switch the current policy and compilation result for this unit to the requested one.
//		CompilationResult unitResult =
//			new CompilationResult(sourceUnit, totalUnits, totalUnits, this.options.maxProblemsPerUnit);
//		try {
//			// diet parsing for large collection of unit
//			com.ibm.etools.egl.internal.pgm.model.IEGLFile parsedUnit;
//			MatchingNodeSet originalMatchSet = this.parser.matchSet;
//			try {
//				this.parser.matchSet = new MatchingNodeSet(this);
//				if (totalUnits < parseThreshold) {
//					parsedUnit = parser.parse(sourceUnit, unitResult);
//				} else {
//					parsedUnit = parser.dietParse(sourceUnit, unitResult);
//				}
//			} finally {
//				this.parser.matchSet = originalMatchSet;
//			}
//		
//			// initial type binding creation
//			lookupEnvironment.buildTypeBindings(parsedUnit);
//			this.addEGLFile(sourceUnit, parsedUnit);
//	
//			// binding resolution
//			lookupEnvironment.completeTypeBindings(parsedUnit);
//		} catch (AbortCompilationUnit e) {
//			// at this point, currentCompilationUnitResult may not be sourceUnit, but some other
//			// one requested further along to resolve sourceUnit.
//			if (unitResult.compilationUnit == sourceUnit) { // only report once
//				//requestor.acceptResult(unitResult.tagAsAccepted());
//			} else {
//				throw e; // want to abort enclosing request to compile
//			}
//		}
//	}
//	/**
//	 * Add additional source types
//	 */
//	public void accept(ISourceType[] sourceTypes, PackageBinding packageBinding) {
//		// case of SearchableEnvironment of an IJavaProject is used
//		ISourceType sourceType = sourceTypes[0];
//		while (sourceType.getEnclosingType() != null)
//			sourceType = sourceType.getEnclosingType();
//		if (sourceType instanceof SourceTypeElementInfo) {
//			// get source
//			SourceTypeElementInfo elementInfo = (SourceTypeElementInfo) sourceType;
//			IType type = elementInfo.getHandle();
//			ICompilationUnit sourceUnit = (ICompilationUnit)type.getCompilationUnit();
//			this.accept(sourceUnit);
//		} else {
//			CompilationResult result =
//				new CompilationResult(sourceType.getFileName(), 0, 0, 0);
//			CompilationUnitDeclaration unit =
//				SourceTypeConverter.buildCompilationUnit(
//					sourceTypes,
//					true, // need field and methods
//					true, // need member types
//					false, // no need for field initialization
//					lookupEnvironment.problemReporter,
//					result);
//			this.lookupEnvironment.buildTypeBindings(unit);
//			this.lookupEnvironment.completeTypeBindings(unit, true);
//		}
//	}	
	protected void addEGLFile(
		PotentialMatch sourceUnit,
		ICompiledFileUnit parsedUnit) {

		// append the unit to the list of ones to process later on
		int size = this.unitsToProcess.length;
		if (this.totalUnits == size) {
			// when growing reposition units starting at position 0
			int newSize = size == 0 ? 1 : size * 2;
			System.arraycopy(
				this.unitsToProcess,
				0,
				(this.unitsToProcess = new ICompiledFileUnit[newSize]),
				0,
				this.totalUnits);
			System.arraycopy(
				this.matchesToProcess,
				0,
				(this.matchesToProcess = new PotentialMatch[newSize]),
				0,
				this.totalUnits);
		}
		this.matchesToProcess[this.totalUnits] = sourceUnit;
		this.unitsToProcess[this.totalUnits] = parsedUnit;
		this.totalUnits++;
	}	
	void addPotentialMatch(IResource resource, Openable openable) {
		PotentialMatch potentialMatch = new PotentialMatch(this, resource, openable);
		this.potentialMatches.add(potentialMatch);
	}
//	/*
//	 * Computes the super type names of the focus type if any.
//	 */
//	private void computeSuperTypeNames() {
//		if (this.allSuperTypeNames != null) 
//			return; // all super types have already been computed
//		
//		IType focusType = getFocusType();
//		if (focusType == null) return;
//
//		String fullyQualifiedName = focusType.getFullyQualifiedName();
//		int lastDot = fullyQualifiedName.lastIndexOf('.');
//		char[] qualification = lastDot == -1 ? CharOperation.NO_CHAR : fullyQualifiedName.substring(0, lastDot).toCharArray();
//		char[] simpleName = focusType.getElementName().toCharArray();
//
//		SuperTypeNamesCollector superTypeNamesCollector = 
//			new SuperTypeNamesCollector(
//				this.pattern, 
//				simpleName,
//				qualification,
//				new MatchLocator2(this.pattern, this.detailLevel, this.collector, this.scope, this.progressMonitor), // clone MatchLocator so that it has no side effect
//				focusType, 
//				this.progressMonitor);
//		try {
//			this.allSuperTypeNames = superTypeNamesCollector.collect();
//		} catch (EGLModelException e) {
//		}
//	}
	/**
	 * Add the initial set of PGM trees into the loop
	 */
	protected void createAndResolveBindings(PotentialMatch[] potentialMatches, int start, int length) {
		for (int i = start, maxUnits = start+length; i < maxUnits; i++) {
			if (this.progressMonitor != null && this.progressMonitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			PotentialMatch potentialMatch = potentialMatches[i];
			try {
				if (potentialMatch != null) {
					this.matchVisitor.matchSet = potentialMatch.matchingNodeSet;
				}
				
				if (SearchEngine.VERBOSE) {
					System.out.println("Parsing " + potentialMatch.openable.toStringWithAncestors()); //$NON-NLS-1$
				}

				if(potentialMatch.openable instanceof WorkingCopy)
				{
					// EGLTODO: Brian: Handle working copies - need to somehow get their PGM model
					throw new RuntimeException();
				}
				
//	        	ErrorCorrectingParser parser;
//	        	IFile ioFile = (IFile)((IEGLFile)potentialMatch.openable).getResource();
//	        	Reader reader = new BufferedReader(new InputStreamReader(ioFile.getContents(), ioFile.getCharset()));
//	           	if(EGLVAGCompatibilitySetting.isVAGCompatibility()){
//	           		parser = new ErrorCorrectingParser(new VAGLexer(reader));
//	           	}else{
//	           		parser = new ErrorCorrectingParser(new Lexer(reader));
//	           	}
				ICompiledFileUnit searchTarget = null;
				if(potentialMatch.openable instanceof IClassFile) {
					//TODO Is ok? Rocky
					searchTarget = new CompiledFileUnit();

				} else {
					IEGLFile eglFile = (IEGLFile)potentialMatch.openable;
					IFile ioFile = (IFile)eglFile.getResource();
					 
					searchTarget = WorkingCopyCompiler.getInstance().compileFile(ioFile);
	//				Platform.getJobManager().join(WorkingCopyResourceChangeProcessor.FAMILY_WORKING_COPY_JOB, null);
	//				searchTarget.getFile().accept(matchVisitor);
					for (int j = 0; j < searchTarget.getFileParts().size();j++){
						Node node = (Node)searchTarget.getFileParts().get(j);
						node.accept(matchVisitor);
					}
					
					if (matchImports){
						String fileastPartName = org.eclipse.edt.ide.core.internal.utils.Util.getFilePartName(ioFile);
						IPackageFragment packageFragment = (IPackageFragment)eglFile.getAncestor(IEGLElement.PACKAGE_FRAGMENT);
						String[] packageName;
						if(packageFragment.isDefaultPackage()){
							packageName = new String[0];
						}else{
							packageName = packageFragment.getElementName().split("\\.");
						}
						
						WorkingCopyCompiler.getInstance().compilePart(ioFile.getProject(),packageName, ioFile, new IWorkingCopy[0], fileastPartName,new IWorkingCopyCompileRequestor(){
							public void acceptResult(WorkingCopyCompilationResult result){
								result.getBoundPart().accept(new AbstractASTVisitor(){
									public boolean visit (ImportDeclaration importDecl){
										importDecl.accept(matchVisitor);
										return false;
									}
								});
							}
						} );
						
	//					Platform.getJobManager().join(WorkingCopyResourceChangeProcessor.FAMILY_WORKING_COPY_JOB, null);
					
					}
		           	

				}
	           	
				// Locate the PGM tree for this TModel element
//				ProcessingUnitWrapper puWrapper = new ProcessingUnitWrapper((IEGLFile)potentialMatch.openable, (NameEnvironment)nameEnvironment);
//				com.ibm.etools.egl.internal.pgm.model.IEGLFile pgmTree = puWrapper.getParsedUnit();
				
				// Find nodes that match the search pattern
//				pgmTree.traverse(matchVisitor);

				this.addEGLFile(potentialMatch, searchTarget);
				
				// progress reporting
				if (this.progressMonitor != null) {
					this.progressMonitor.worked(4);
				}
			}catch (Exception e){
				if (SearchEngine.VERBOSE) {
					System.out.println("Exception occurred! See stack trace.");
					e.printStackTrace();
				}
			}finally {
				this.matchVisitor.matchSet = null;
				potentialMatches[i] = null; // no longer hold onto the unit
			}
		}
	}
	/**
	 * Creates an IField from the given field declaration and type. 
	 */
	public IField createFieldHandle(Node field,	IPart part) {
		if (part == null) return null;
		final String []fieldname = new String[]{null};
		field.accept(new AbstractASTExpressionVisitor(){
			public boolean visitName(Name name) {
				fieldname[0] = name.getCanonicalName();
			    return false;
			}	
		});
		
		if (fieldname[0] != null){
			return part.getField(fieldname[0]);
		}
		
		return null;

	}
	
	public IUseDeclaration createUseHandle(
		Name use,
		IPart part) {
		
		if (part == null) return null;
		
		return part.getUseDeclaration(use.getCanonicalName());
	}
//	/*
//	 * Creates hierarchy resolver if needed. 
//	 * Returns whether focus is visible.
//	 */
//	protected boolean createHierarchyResolver(PotentialMatch[] potentialMatches) {
//		// create hierarchy resolver if scope is a hierarchy scope
//		IType focusType = getFocusType();
//		if (focusType != null) {
//			// cache focus type if not a potential match
//			char[][] compoundName = CharOperation.splitOn('.', focusType.getFullyQualifiedName().toCharArray());
//			boolean isPotentialMatch = false;
//			for (int i = 0, length = potentialMatches.length; i < length; i++) {
//				if (CharOperation.equals(potentialMatches[i].compoundName, compoundName)) {
//					isPotentialMatch = true;
//					break;
//				}
//			}
//			if (!isPotentialMatch) {
//				if (focusType.isBinary()) {
//					// cache binary type
//					try {
//						this.cacheBinaryType(focusType);
//					} catch (EGLModelException e) {
//						return false;
//					}
//				} else {
//					// cache all types in the focus' compilation unit (even secondary types)
//					this.accept((ICompilationUnit)focusType.getCompilationUnit());
//				}
//			}
//			
//			// resolve focus type
//			this.hierarchyResolver = new HierarchyResolver(this.lookupEnvironment, null/*hierarchy is not going to be computed*/);
//			ReferenceBinding focusTypeBinding = this.hierarchyResolver.setFocusType(compoundName);
//			if (focusTypeBinding == null || !focusTypeBinding.isValidBinding() || (focusTypeBinding.tagBits & TagBits.HierarchyHasProblems) > 0) {
//				// focus type is not visible from this project
//				return false;
//			}
//		} else {
//			this.hierarchyResolver = null;
//		}
//		return true;
//	}
	/**
	 * Creates an IImportDeclaration from the given import statement
	 */
	public IEGLElement createImportHandle(ImportDeclaration importRef) {
		char[] importName = importRef.getName().getCanonicalName().toCharArray();
		if (importRef.isOnDemand()) {
			importName = CharOperation.concat(importName, ".*" .toCharArray()); //$NON-NLS-1$
		}
		Openable currentOpenable = this.getCurrentOpenable();
		if (currentOpenable instanceof EGLFile) {
			return ((EGLFile)currentOpenable).getImport(
				new String(importName));
		}
		return null;
	}
	/**
	 * Creates an IFunction from the given function declaration and part. 
	 */
	public IFunction createFunctionHandle(TopLevelFunction function,IPart part) {
		if (part == null) return null;
		List parameters = function.getFunctionParameters();
		int length = parameters.size();
		
		String[] parameterTypeSignatures = new String[length];
		for (int i = 0; i < length; i++) {
			Type parameterType = ((FunctionParameter)parameters.get(i)).getType();
			char[] typeName = new char[0];
			while(parameterType.isArrayType())
			{
				typeName = CharOperation.concat(typeName, "[]" .toCharArray()); //$NON-NLS-1$
				parameterType = ((ArrayType) parameterType).getElementType();
			}
			typeName = CharOperation.concat( parameterType.getCanonicalName().toCharArray(), typeName );
			parameterTypeSignatures[i] = Signature.createTypeSignature(typeName, false);
		}
		return part.getFunction(function.getName().getCanonicalName(), parameterTypeSignatures);
	}
	
	/**
	 * Creates an IFunction from the given function declaration and part. 
	 */
	public IFunction createFunctionHandle(NestedFunction function,IPart part) {
		if (part == null) return null;
		List parameters = function.getFunctionParameters();
		int length = parameters.size();
		
		String[] parameterTypeSignatures = new String[length];
		for (int i = 0; i < length; i++) {
			Type parameterType = ((FunctionParameter)parameters.get(i)).getType();
			char[] typeName = new char[0];
			while(parameterType.isArrayType())
			{
				typeName = CharOperation.concat(typeName, "[]" .toCharArray()); //$NON-NLS-1$
				parameterType = ((ArrayType) parameterType).getElementType();
			}
			typeName = CharOperation.concat( parameterType.getCanonicalName().toCharArray(), typeName );
			parameterTypeSignatures[i] = Signature.createTypeSignature(typeName, false);
		}
		return part.getFunction(function.getName().getCanonicalName(), parameterTypeSignatures);
	}
	
	/**
	 * Creates an IPart from the given simple top level type name. 
	 */
	public IEGLElement createPartHandle(String simplePartName) {
		
		Openable currentOpenable = this.getCurrentOpenable();
		if (currentOpenable instanceof IEGLFile) {
			// creates compilation unit
			IEGLFile unit = (IEGLFile)currentOpenable;
	
			// create part
			return unit.getPart(simplePartName);
		}
		return null;
	}
	/**
	 * Creates an IPart from the given simple inner type name and parent type. 
	 */
	public IEGLElement createPartHandle(IPart parent, String simpleTypeName) {
		return parent.getPart(simpleTypeName);
	}
	protected Openable getCurrentOpenable() {
		return this.currentPotentialMatch.openable;
	}
	protected IResource getCurrentResource() {
		return this.currentPotentialMatch.resource;
	}
//	protected IType getFocusType() {
//		return null;
//		//return this.scope instanceof HierarchyScope ? ((HierarchyScope)this.scope).focusType : null;
//	}
	/**
	 * Create a new parser for the given project, as well as a lookup environment.
	 */
	public void initialize(EGLProject project) throws EGLModelException {
		initialize(project, NO_POTENTIAL_MATCH);
	}
	/**
	 * Create a new parser for the given project, as well as a lookup environment.
	 */
	public void initialize(EGLProject project, PotentialMatch[] potentialMatches) throws EGLModelException {
		// create name environment
//		if (this.nameEnvironment != null) { // cleanup
//			this.nameEnvironment.cleanup();
//		}
//		this.nameEnvironment = new NameEnvironment(project);

		// create parser
//		this.parser = new Parser();
		this.matchVisitor = new MatchVisitor();
				
		// initialize queue of units
		this.totalUnits = 0;
		int maxUnits = potentialMatches.length;
		this.unitsToProcess = new ICompiledFileUnit[maxUnits];
		this.matchesToProcess = new PotentialMatch[maxUnits];

	}
//	public boolean hasAlreadyDefinedType(CompilationUnitDeclaration parsedUnit) {
//		if (parsedUnit == null) return false;
//		CompilationResult result = parsedUnit.compilationResult;
//		if (result == null) return false;
//		for (int i = 0; i < result.problemCount; i++) {
//			IProblem problem = result.problems[i];
//			if (problem.getID() == IProblem.DuplicateTypes) {
//				return true;
//			}
//		}
//		return false;
//	}	
	/**
	 * Locate the matches amongst the potential matches.
	 */
	private void locateMatches(EGLProject eglProject) throws EGLModelException {
		PotentialMatch[] potentialMatches = this.potentialMatches.getPotentialMatches(eglProject.getPackageFragmentRoots());
		
		int length = potentialMatches.length;
		int index = 0;
		while (index < length) {
			int max = Math.min(MAX_AT_ONCE, length-index);
			locateMatches(eglProject, potentialMatches, index, max);
			index += max;
		}
	}
	private void locateMatches(EGLProject eglProject, PotentialMatch[] potentialMatches, int start, int length) throws EGLModelException {
		
		// copy array because elements  from the original are removed below
		PotentialMatch[] copy = new PotentialMatch[length];
		System.arraycopy(potentialMatches, start, copy, 0, length);
		this.initialize(eglProject, copy);
		
		this.compilationAborted = false;
		
		this.createAndResolveBindings(potentialMatches, start, length);
		
		// free memory
		copy = null;
		potentialMatches = null;
		
		// potential match resolution
			ICompiledFileUnit unit = null;
			for (int i = 0; i < this.totalUnits; i++) {
				if (this.progressMonitor != null && this.progressMonitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				unit = this.unitsToProcess[i];
				try {
					process(unit, i);
				} 
				catch (CoreException e) {
					if (e instanceof EGLModelException) {
						// problem with class path: it could not find base classes
						// continue and try next matching openable reporting innacurate matches (since bindings will be null)
						this.compilationAborted = true;
					} else {
						// core exception thrown by client's code: let it through
						throw new EGLModelException(e);
					}
				} finally {
					// cleanup compilation unit result
					
				}
				this.unitsToProcess[i] = null; // release reference to processed unit declaration
				this.matchesToProcess[i] = null; // release reference to processed potential match
				if (this.progressMonitor != null) {
					this.progressMonitor.worked(5);
				}
			}
	}
	
	/**
	 * Locate the matches in the given files and report them using the search requestor. 
	 */
	public void locateMatches(
		String[] filePaths, 
		IWorkspace workspace,
		IWorkingCopy[] workingCopies)
		throws EGLModelException {
			
		if (SearchEngine.VERBOSE) {
			System.out.println("Locating matches in files ["); //$NON-NLS-1$
			for (int i = 0, length = filePaths.length; i < length; i++) {
				String path = filePaths[i];
				System.out.println("\t" + path); //$NON-NLS-1$
			}
			System.out.println("]"); //$NON-NLS-1$
			if (workingCopies != null) {
				 System.out.println("and working copies ["); //$NON-NLS-1$
				for (int i = 0, length = workingCopies.length; i < length; i++) {
					IWorkingCopy wc = workingCopies[i];
					System.out.println("\t" + ((EGLElement)wc).toStringWithAncestors()); //$NON-NLS-1$
				}
				System.out.println("]"); //$NON-NLS-1$
			}
		}
		
		try {
			// initialize handle factory (used as a cache of handles so as to optimize space)
			if (this.handleFactory == null) {
				this.handleFactory = new HandleFactory(workspace);
			}
			
			// initialize locator with working copies
			this.workingCopies = workingCopies;
			
			// substitute compilation units with working copies
			HashMap wcPaths = new HashMap(); // a map from path to working copies
			int wcLength;
			if (workingCopies != null && (wcLength = workingCopies.length) > 0) {
				String[] newPaths = new String[wcLength];
				for (int i = 0; i < wcLength; i++) {
					IWorkingCopy workingCopy = workingCopies[i];
					String path = workingCopy.getOriginalElement().getPath().toString();
					wcPaths.put(path, workingCopy);
					newPaths[i] = path;
				}
				int filePathsLength = filePaths.length;
				System.arraycopy(filePaths, 0, filePaths = new String[filePathsLength+wcLength], 0, filePathsLength);
				System.arraycopy(newPaths, 0, filePaths, filePathsLength, wcLength);
			}
			
			int length = filePaths.length;
			if (progressMonitor != null) {
				if (this.pattern.needsResolve) {
					progressMonitor.beginTask("", length * 10); // 1 for file path, 4 for parsing and binding creation, 5 for binding resolution //$NON-NLS-1$
				} else {
					progressMonitor.beginTask("", length * 5); // 1 for file path, 4 for parsing and binding creation //$NON-NLS-1$
				}
			}
	
			// sort file paths projects
			Util.sort(filePaths); 
			
			// initialize pattern for polymorphic search (ie. method reference pattern)
			this.potentialMatches = new PotentialMatchSet();
			
			EGLProject previousEGLProject = null;
			for (int i = 0; i < length; i++) {
				if (progressMonitor != null && progressMonitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				String pathString = filePaths[i];
				
				// skip duplicate paths
				if (i > 0 && pathString.equals(filePaths[i-1])) continue;
				
				Openable openable;
				IWorkingCopy workingCopy = (IWorkingCopy)wcPaths.get(pathString);
				if (workingCopy != null) {
					openable = (Openable)workingCopy;
				} else {
					openable = this.handleFactory.createOpenable(pathString);
					if (openable == null)
						continue; // match is outside classpath
				}
	
				// create new parser and lookup environment if this is a new project
				IResource resource = null;
				EGLProject eglProject = null;
				try {
					eglProject = (EGLProject) openable.getEGLProject();
					if (workingCopy != null) {
						resource = workingCopy.getOriginalElement().getResource();
					} else {
						resource = openable.getResource();
					}
					if (resource == null) { // case of a file in an external jar
						resource = eglProject.getProject();
					}
					if (!eglProject.equals(previousEGLProject)) {
						// locate matches in previous project
						if (previousEGLProject != null) {
							try {
								this.locateMatches(previousEGLProject);
							} catch (EGLModelException e) {
								if (e.getException() instanceof CoreException) {
									throw e;
								} else {
									// problem with classpath in this project -> skip it
								}
							}
							this.potentialMatches = new PotentialMatchSet();
						}
	
						previousEGLProject = eglProject;
					}
				} catch (EGLModelException e) {
					// file doesn't exist -> skip it
					continue;
				}
	
				// add potential match
				this.addPotentialMatch(resource, openable);
	
				if (progressMonitor != null) {
					progressMonitor.worked(1);
				}
			}
			
			// last project
			if (previousEGLProject != null) {
				try {
					this.locateMatches(previousEGLProject);
				} catch (EGLModelException e) {
					if (e.getException() instanceof CoreException) {
						throw e;
					} else {
						// problem with classpath in last project -> skip it
					}
				}
				this.potentialMatches = new PotentialMatchSet();
			} 
			
			if (progressMonitor != null) {
				progressMonitor.done();
			}
		} finally {
//			if (this.nameEnvironment != null) {
//				this.nameEnvironment.cleanup();
//			}
		}	
	}
	/**
	 * Locates the package declarations corresponding to this locator's pattern. 
	 */
	public void locatePackageDeclarations(IWorkspace workspace)
		throws EGLModelException {
		this.locatePackageDeclarations(this.pattern, workspace);
	}

	/**
	 * Locates the package declarations corresponding to the search pattern. 
	 */
	private void locatePackageDeclarations(SearchPattern searchPattern,IWorkspace workspace)throws EGLModelException {
		
		if (searchPattern instanceof OrPattern) {
			OrPattern orPattern = (OrPattern) searchPattern;
			this.locatePackageDeclarations(orPattern.leftPattern, workspace);
			this.locatePackageDeclarations(orPattern.rightPattern, workspace);
		} else
			if (searchPattern instanceof PackageDeclarationPattern) {
				PackageDeclarationPattern pkgPattern = (PackageDeclarationPattern) searchPattern;
				IEGLProject[] projects = EGLModelManager.getEGLModelManager().getEGLModel().getEGLProjects();
				for (int i = 0, length = projects.length; i < length; i++) {
					IEGLProject eglProject = projects[i];
					IPackageFragmentRoot[] roots = eglProject.getPackageFragmentRoots();
					for (int j = 0, rootsLength = roots.length; j < rootsLength; j++) {
						IEGLElement[] pkgs = roots[j].getChildren();
						for (int k = 0, pksLength = pkgs.length; k < pksLength; k++) {
							IPackageFragment pkg = (IPackageFragment)pkgs[k];
							if (pkg.getChildren().length > 0 
									&& pkgPattern.matchesName(pkgPattern.pkgName, pkg.getElementName().toCharArray())) {
								IResource resource = pkg.getResource();
								if (resource == null) { // case of a file in an external jar
									resource = eglProject.getProject();
								}
								this.currentPotentialMatch = new PotentialMatch(this, resource, null);
								try {
									this.report(-1, -2, pkg, IEGLSearchResultCollector.EXACT_MATCH);
								} catch (CoreException e) {
									if (e instanceof EGLModelException) {
										throw (EGLModelException) e;
									} else {
										throw new EGLModelException(e);
									}
								}
							}
						}
					}
				}
			}
	}
	/*
	 * Process a compilation unit already parsed and build.
	 */
	public void process(ICompiledFileUnit unit, int i) throws CoreException {
		MatchingNodeSet matchingNodeSet = null;
		try {
			this.currentPotentialMatch = this.matchesToProcess[i];
			if (this.currentPotentialMatch == null) return;
			matchingNodeSet = this.currentPotentialMatch.matchingNodeSet;
			
			if (unit == null){
				 return;
			}
			
			if(unit.getFile() == null) { //Created from IR, no files
				if (this.currentPotentialMatch.openable instanceof ClassFile) {
					ClassFile classFile = (ClassFile) this.currentPotentialMatch.openable;
						try {
							new ClassFileMatchLocator(this).locateMatches(classFile/*, info*/);
						} finally {
//							this.patternLocator.mayBeGeneric = mayBeGeneric;
						}
						return;
				}
			}
	
			this.matchVisitor.matchSet = this.currentPotentialMatch.matchingNodeSet;

						
			// report matches that don't need resolve
			matchingNodeSet.cuHasBeenResolved = this.compilationAborted;
			matchingNodeSet.reportMatching(unit);

			if ((this.pattern.needsResolve || matchingNodeSet.needsResolve()/* TODO: do not need this check any longer */)  
					&& unit.getFileParts().size() > 0 
					&& !this.compilationAborted) {

				if (SearchEngine.VERBOSE) {
					System.out.println("Resolving " + this.currentPotentialMatch.openable.toStringWithAncestors()); //$NON-NLS-1$
				}

				// report matches that needed resolve
				matchingNodeSet.cuHasBeenResolved = true;
				matchingNodeSet.reportMatching(unit);
			}
		} 
		finally {
			this.matchVisitor.matchSet = null;
			this.currentPotentialMatch = null;
		}
	}

	public void report(int sourceStart,	int sourceEnd,IEGLElement element,int accuracy)	throws CoreException {

		if (this.scope.encloses(element)) {
			if (SearchEngine.VERBOSE) {
				IResource res = this.getCurrentResource();
				System.out.println("Reporting match"); //$NON-NLS-1$
				System.out.println("\tResource: " + (res == null ? " <unknown> " : res.getFullPath().toString())); //$NON-NLS-2$//$NON-NLS-1$
				System.out.println("\tPositions: [" + sourceStart + ", " + sourceEnd + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				System.out.println("\tJava element: " + ((EGLElement)element).toStringWithAncestors()); //$NON-NLS-1$
				if (accuracy == IEGLSearchResultCollector.EXACT_MATCH) {
					System.out.println("\tAccuracy: EXACT_MATCH"); //$NON-NLS-1$
				} else {
					System.out.println("\tAccuracy: POTENTIAL_MATCH"); //$NON-NLS-1$
				}
			}
			this.report(
				this.getCurrentResource(),
				sourceStart,
				sourceEnd,
				element,
				accuracy);
		}
	}
	
	public void report(
		IResource resource,
		int sourceStart,
		int sourceEnd,
		IEGLElement element,
		int accuracy)
		throws CoreException {

		this.collector.accept(
			resource,
			sourceStart,
			sourceEnd,
			element,
			accuracy);
	}
//	/**
//	 * Finds the accurate positions of the sequence of tokens given by qualifiedName
//	 * in the source and reports a reference to this this qualified name
//	 * to the search requestor.
//	 */
//	public void reportAccurateReference(
//		int sourceStart,
//		int sourceEnd,
//		char[][] qualifiedName,
//		IEGLElement element,
//		int accuracy)
//		throws CoreException {
//	
//		if (accuracy == -1) return;
//	
//		// compute source positions of the qualified reference 
//		Scanner scanner = this.parser.scanner;
//		scanner.setSource(
//			this.currentPotentialMatch.getContents());
//		scanner.resetTo(sourceStart, sourceEnd);
//	
//		int refSourceStart = -1, refSourceEnd = -1;
//		int tokenNumber = qualifiedName.length;
//		int token = -1;
//		int previousValid = -1;
//		int i = 0;
//		int currentPosition;
//		do {
//			// find first token that is an identifier (parenthesized expressions include parenthesises in source range - see bug 20693 - Finding references to variables does not find all occurrences  )
//			do {
//				currentPosition = scanner.currentPosition;
//				try {
//					token = scanner.getNextToken();
//				} catch (InvalidInputException e) {
//				}
//			} while (token !=  TerminalTokens.TokenNameIdentifier && token !=  TerminalTokens.TokenNameEOF);
//	
//			if (token != TerminalTokens.TokenNameEOF) {
//				char[] currentTokenSource = scanner.getCurrentTokenSource();
//				boolean equals = false;
//				while (i < tokenNumber
//					&& !(equals = this.pattern.matchesName(qualifiedName[i++], currentTokenSource))) {
//				}
//				if (equals && (previousValid == -1 || previousValid == i - 2)) {
//					previousValid = i - 1;
//					if (refSourceStart == -1) {
//						refSourceStart = currentPosition;
//					}
//					refSourceEnd = scanner.currentPosition - 1;
//				} else {
//					i = 0;
//					refSourceStart = -1;
//					previousValid = -1;
//				}
//				// read '.'
//				try {
//					token = scanner.getNextToken();
//				} catch (InvalidInputException e) {
//				}
//			}
//			if (i == tokenNumber) {
//				// accept reference
//				if (refSourceStart != -1) {
//					this.report(refSourceStart, refSourceEnd, element, accuracy);
//				} else {
//					this.report(sourceStart, sourceEnd, element, accuracy);
//				}
//				return;
//			}
//		} while (token != TerminalTokens.TokenNameEOF);
//	
//	}
//	/**
//	 * Finds the accurate positions of each valid token in the source and
//	 * reports a reference to this token to the search requestor.
//	 * A token is valid if it has an accurracy which is not -1.
//	 */
//	public void reportAccurateReference(
//		int sourceStart,
//		int sourceEnd,
//		char[][] tokens,
//		IEGLElement element,
//		int[] accuracies)
//		throws CoreException {
//
//		// compute source positions of the qualified reference 
//		Scanner scanner = this.parser.scanner;
//		scanner.setSource(
//			this.currentPotentialMatch.getContents());
//		scanner.resetTo(sourceStart, sourceEnd);
//
//		int refSourceStart = -1, refSourceEnd = -1;
//		int length = tokens.length;
//		int token = -1;
//		int previousValid = -1;
//		int i = 0;
//		int accuracyIndex = 0;
//		do {
//			int currentPosition = scanner.currentPosition;
//			// read token
//			try {
//				token = scanner.getNextToken();
//			} catch (InvalidInputException e) {
//			}
//			if (token != TerminalTokens.TokenNameEOF) {
//				char[] currentTokenSource = scanner.getCurrentTokenSource();
//				boolean equals = false;
//				while (i < length
//					&& !(equals = this.pattern.matchesName(tokens[i++], currentTokenSource))) {
//				}
//				if (equals && (previousValid == -1 || previousValid == i - 2)) {
//					previousValid = i - 1;
//					if (refSourceStart == -1) {
//						refSourceStart = currentPosition;
//					}
//					refSourceEnd = scanner.currentPosition - 1;
//				} else {
//					i = 0;
//					refSourceStart = -1;
//					previousValid = -1;
//				}
//				// read '.'
//				try {
//					token = scanner.getNextToken();
//				} catch (InvalidInputException e) {
//				}
//			}
//			if (accuracies[accuracyIndex] != -1) {
//				// accept reference
//				if (refSourceStart != -1) {
//					this.report(refSourceStart, refSourceEnd, element, accuracies[accuracyIndex]);
//				} else {
//					this.report(sourceStart, sourceEnd, element, accuracies[accuracyIndex]);
//				}
//				i = 0;
//			}
//			refSourceStart = -1;
//			previousValid = -1;
//			if (accuracyIndex < accuracies.length-1) {
//				accuracyIndex++;
//			}
//		} while (token != TerminalTokens.TokenNameEOF);
//
//	}
	/**
	 * Reports the given field declaration to the search requestor.
	 */
	public void reportField(Node field,final IEGLElement parent,final int accuracy)	throws CoreException {
		// accept data declaration
		field.accept(new AbstractASTVisitor(){
			public boolean visit(ClassDataDeclaration classDataDeclaration) {
				try {
					report(
							((SimpleName)classDataDeclaration.getNames().get(0)).getOffset(), 
							((SimpleName)classDataDeclaration.getNames().get(0)).getOffset() + ((SimpleName)classDataDeclaration.getNames().get(0)).getLength(),
							(parent instanceof IPart) ?
								((IPart)parent).getField(new String(((SimpleName)classDataDeclaration.getNames().get(0)).getCanonicalName())):
								parent,
							accuracy);
					
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return false;
			}
			
			public boolean visit(NestedForm nestedForm) {
				try {
					report(
							nestedForm.getName().getOffset(), 
							nestedForm.getName().getOffset() + nestedForm.getName().getLength(),
							(parent instanceof IPart) ?
								((IPart)parent).getPart(new String(nestedForm.getName().getCanonicalName())):
								parent,
							accuracy);
					
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return false;
			}
			
			
		});
	}
	/**
	 * Reports the given import to the search requestor.
	 */
	public void reportImport(ImportDeclaration reference, int accuracy)	throws CoreException {
		// create defining import handle
		IEGLElement importHandle = this.createImportHandle(reference);

		// accept reference
		this.pattern.matchReportImportRef(reference, importHandle, accuracy, this);
	}
	
	/**
	 * Reports the given package declaration to the search requestor.
	 */
	public void reportPackageDeclaration(PackageDeclaration node) {
		// TBD
	}
	/**
	 * Reports the given reference to the search requestor.
	 * It is done in the given method and the method's defining types 
	 * have the given simple names.
	 */
	public void reportReference(
		Node reference,
		TopLevelFunction function,
		IEGLElement parent,
		int accuracy)
		throws CoreException {

		// accept reference
		this.pattern.matchReportReference(reference, parent, accuracy, this);
	}
	
	public void reportReference(
			Node reference,
			Delegate delegate,
			IEGLElement parent,
			int accuracy)
			throws CoreException {

			// accept reference
			this.pattern.matchReportReference(reference, parent, accuracy, this);
		}
	
	public void reportReference(
			Node reference,
			NestedFunction function,
			IEGLElement parent,
			int accuracy)
			throws CoreException {

			// accept reference
			this.pattern.matchReportReference(reference, parent, accuracy, this);
		}
	
	public void reportReference(IPart element, IEGLElement parent, int accuracy){
		try {
			this.collector.accept(
					(parent == null) ? element.getClassFile() : ((parent instanceof BinaryPart) ? ((BinaryPart)parent).getClassFile() : parent), element.getSourceRange().getOffset(),
							element.getSourceRange().getOffset() + element.getSourceRange().getLength(),
					this.getCurrentResource(),
					accuracy);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reports the given reference to the search requestor.
	 * It is done in the given field and given type.
	 * The field's defining types have the given simple names.
	 */
	public void reportFieldReference(
		Node reference,
		//IEGLPart partDeclaration,
		Node decl,
		IEGLElement parent,
		int accuracy)
		throws CoreException {
		//TODO search
		IEGLElement enclosingElement;
		// EGLTODO: Remove commented code?
		//if (fieldDeclaration.isField()) {
			if (parent instanceof IPart) {
				// create defining field handle
				enclosingElement = this.createFieldHandle(decl, (IPart)parent);
				if (enclosingElement == null) return;
			} else {
				enclosingElement = parent;
			}

			// accept reference
			this.pattern.matchReportReference(reference, enclosingElement, accuracy, this);
//		} else { // initializer
//			if (parent instanceof IType) {
//				// create defining initializer
//				enclosingElement =
//					this.createInitializerHandle(
//						typeDeclaration,
//						fieldDeclaration,
//						(IType)parent);
//				if (enclosingElement == null) return;
//			} else {
//				enclosingElement = parent;
//			}
//
//			// accept reference
//			this.pattern.matchReportReference(reference, enclosingElement, accuracy, this);
//		}
	}
	
	public void reportUseReference(Node reference,Part partDeclaration,
			Name use,IEGLElement parent,	int accuracy)throws CoreException {

		IEGLElement enclosingElement;
		// EGLTODO: Remove commented code?
		//if (fieldDeclaration.isField()) {
			if (parent instanceof IPart) {
				// create defining field handle
				enclosingElement = this.createUseHandle(use, (IPart)parent);
				if (enclosingElement == null) return;
			} else {
				enclosingElement = parent;
			}

			// accept reference
			this.pattern.matchReportReference(reference, enclosingElement, accuracy, this);
	}
	
	public void reportImplementsReference(Node reference,Part partDeclaration,Name use,
			IEGLElement parent,	int accuracy)throws CoreException {

		// accept reference
		this.pattern.matchReportReference(reference, parent, accuracy, this);
	}
	
//	/**
//	 * Reports the given super type reference to the search requestor.
//	 * It is done in the given defining type (with the given simple names).
//	 */
//	public void reportSuperTypeReference(
//		TypeReference typeRef,
//		IJavaElement type,
//		int accuracy)
//		throws CoreException {
//
//		// accept type reference
//		this.pattern.matchReportReference(typeRef, type, accuracy, this);
//	}
	/**
	 * Reports the given type declaration to the search requestor.
	 */
	public void reportPartDeclaration(Part partDeclaration,	IEGLElement parent,	int accuracy)throws CoreException {
		Name name = partDeclaration.getName();
		// accept class or interface declaration
		this.report(
			name.getOffset(),
			name.getOffset() + name.getLength(),
			(parent == null) ?
				this.createPartHandle(name.getCanonicalName()) :
				parent,
			accuracy);
	}
	
	/**
	 * Reports the given type declaration to the search requestor.
	 */
	public void reportPartDeclaration(IPart partDeclaration,	IEGLElement parent,	int accuracy)throws CoreException {
		//TODO Name name = partDeclaration.getName();
		// accept class or interface declaration
		
		this.collector.accept(
				(parent == null) ? partDeclaration.getClassFile() : ((parent instanceof BinaryPart) ? ((BinaryPart)parent).getClassFile() : parent), partDeclaration.getNameRange().getOffset(),
				partDeclaration.getNameRange().getOffset() + partDeclaration.getNameRange().getLength(),
				this.getCurrentResource(),
				accuracy);
	}
	
	/**
	 * Reports the given type declaration to the search requestor.
	 */
	public void reportPartDeclaration(IMember member,	IEGLElement parent,	int accuracy)throws CoreException {
		//TODO Name name = partDeclaration.getName();
		// accept class or interface declaration
		this.collector.accept(
				(parent == null) ? member.getClassFile() : ((parent instanceof BinaryPart) ? ((BinaryPart)parent).getClassFile() : parent), 0,
				0,
				this.getCurrentResource(),
				accuracy);
	}
		
	/**
	 * Reports the given function declaration to the search requestor.
	 */
	public void reportFunctionDeclaration(TopLevelFunction functionDeclaration,	IEGLElement parent,
		int accuracy)throws CoreException {

		Name name = functionDeclaration.getName();
		// accept class or interface declaration
		this.report(
			name.getOffset(),
			name.getOffset() + name.getLength(),
			(parent == null) ?
				this.createPartHandle(name.getCanonicalName()) :
				parent,
			accuracy);
	}
	
	public void reportFunctionDeclaration(NestedFunction functionDeclaration,	IEGLElement parent,
			int accuracy)throws CoreException {

		Name name = functionDeclaration.getName();
		// accept class or interface declaration
		this.report(
			name.getOffset(),
			name.getOffset() + name.getLength(),
			(parent == null) ?
				this.createPartHandle(name.getCanonicalName()) :
				parent,
			accuracy);
	}
	
	public void reportFunctionDeclaration(IFunction functionDeclaration, IEGLElement parent,
			int accuracy) throws CoreException {
		//TODO Name name = partDeclaration.getName();
		this.collector.accept(
				(parent == null) ? functionDeclaration.getClassFile() : ((parent instanceof BinaryPart) ? ((BinaryPart)parent).getClassFile() : parent), 0,
				0,
				this.getCurrentResource(),
				accuracy);
	}

	//	public boolean typeInHierarchy(ReferenceBinding binding) {
//		if (this.hierarchyResolver == null) return true; // not a hierarchy scope
//		if (this.hierarchyResolver.subOrSuperOfFocus(binding)) {
//			return true;
//		} else if (this.allSuperTypeNames != null) {
//			char[][] compoundName = binding.compoundName;
//			for (int i = 0, length = this.allSuperTypeNames.length; i < length; i++) {
//				if (CharOperation.equals(compoundName, this.allSuperTypeNames[i])) 
//					return true;
//			}
//			return false;
//		} else {
//			return false;
//		}	
//	}
	
	public int matchContainer(){
		return pattern.matchContainer();
	}
	
	public int matchesPartType(Name node,IPartBinding partBinding){
		return pattern.matchesPartType(node,partBinding,forceQualification);
	}
	
	public int matchesNestedFormPart(NestedForm node){
		return pattern.matchesNestedFormPart(node);
	}
	public int matchesPart(IPart iPart) {
		return pattern.matchesPart(iPart);
	}
	public int matchesPart(Part node){
		return pattern.matchesPart(node);
	}
	public int matchesFunctionPartType(Name node,IPartBinding partBinding){
		return pattern.matchesFunctionPartType(node,partBinding);
	}
	
	public int matchesFunctionPart(TopLevelFunction function){
		return pattern.matchesFunctionPart(function);
	}
	
	public int matchesAnnotationType(Name node, IAnnotationTypeBinding binding){
		return pattern.matchesAnnotationType(node, binding, forceQualification);
	}
	
	protected int matchCheck(Node node) {
		return pattern.matchCheck(node);
	}
	
	protected int matchCheck(IMember part) {
		return pattern.matchCheck(part);
	}
	
	protected void matchCheck(Node node, MatchingNodeSet set) {
		pattern.matchCheck(node, set);
	}
	
	public int matchLevel(Node node, boolean resolve){
		return pattern.matchLevel(node, resolve);
	}
	
	public SearchPattern getPattern(){
		return pattern;
	}
}
