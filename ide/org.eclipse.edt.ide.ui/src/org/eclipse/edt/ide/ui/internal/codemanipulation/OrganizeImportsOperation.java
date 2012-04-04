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
package org.eclipse.edt.ide.ui.internal.codemanipulation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AccumulatingSyntaxErrorMessageRequestor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SyntaxError;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.internal.search.PartInfoRequestor;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.DocumentAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;


public class OrganizeImportsOperation implements IWorkspaceRunnable {
	protected IEGLFile feglfile;
	protected IEGLFile workingCopy = null;
	
	private boolean needSave;			//save the file if the file is not opened in editor
	
	protected IChooseImportQuery fChooseImportQuery;
	
	private int fNumberOfImportsAdded;
	private int fNumberOfImportsRemoved;
	
	private SyntaxErrorHelper fSyntaxError;

	private static class SystemPartInfo extends PartInfo{

		public SystemPartInfo(String pkg, String name) {
			super(pkg, name, new char[0][]);
		}
		
		public int getElementType() {
			return PartInfo.UNRESOLVABLE_PART_INFO;
		}
		
		public String getPath() {
			return "";
		}
		
		public IPath getPackageFragmentRootPath() {
			return new Path("");
		}
		
		protected IEGLElement getEGLElement(IEGLSearchScope scope) {
			return null;
		}

	    /* (non-Javadoc)
	     * @see org.eclipse.edt.ide.core.internal.search.PartInfo#getPartType()
	     */
	    public char getPartType() {
	        return 0;
	    }
		
	}
	
	public static class SyntaxErrorHelper
	{
		public SyntaxError fSynErr;
		public String fErrMsg;
		
		public SyntaxErrorHelper(SyntaxError synErr, String errMsg)
		{
			this.fSynErr = synErr;
			this.fErrMsg = errMsg;
		}
	}
	
	public static interface IChooseImportQuery {
		/**
		 * Selects imports from a list of choices.
		 * @param mapOpenChoices Map <Name>, <ArrayList <PartInfo>> 
		 * key is the Name node of the unresolvedTypeName
		 * value is the list of PartInfo - the mulitple open choices
		 * @return <code>null</code> to cancel the operation, or the
		 *         selected imports.
		 */
		PartInfo[] chooseImports(Map mapOpenChoices);

	}	
		
	static public class OrganizedImportSection
	{
		//key is package name(i.e. "pkg1.pkg2"), value is sorted Set(part names)
		private final Map /*<String(package name), TreeSet(part name)>*/ imports = new TreeMap(new ImportComparator());
		private final Map /*<String(package name), TreeSet(part name)>*/ ignoredImports = new TreeMap(new ImportComparator());
		private String fCurrentPackageName;

		public OrganizedImportSection(String currentPackageName)
		{
			fCurrentPackageName = currentPackageName;
		}
		
		private static void addImport(String packageName, String partName, String currentPackageName, Map imports)
		{
			Set parts = null;
			if(!currentPackageName.equals(packageName))			//if the part is in the same package, no need to add an entry
			{
				Object obj = imports.get(packageName);
				if(obj != null)
					parts = (TreeSet)obj;
				else
				{
					parts = new TreeSet();
					imports.put(packageName, parts);					
				}
				if(!parts.contains("*"))		//if already on demand, no need to add the specific one //$NON-NLS-1$
					parts.add(partName);
			}
		}
		
		/**
		 * the import will be added to the sorted map in the order based on import preferences
		 * 
		 * @param packageName, i.e. "pkg1.pkg2"
		 * @param partName
		 */
		public void addImport(String packageName, String partName) {
			Set partsToIgnore = (Set) ignoredImports.get(packageName);
			if(partsToIgnore == null || !(partsToIgnore.contains("*") || partsToIgnore.contains(partName))) { //$NON-NLS-1$
				addImport(packageName, partName, fCurrentPackageName, imports);
			}
		}

		
		public void ignoreImport(String packageName, String partName) {
			addImport(packageName, partName, fCurrentPackageName, ignoredImports);
		}
		
		/**
		 * Test if the input import entry conflict with any existing import entry in the importSection
		 * It is conflict if the same partName already exist in a different packagename 
		 * @param packageName
		 * @param partName
		 * @param conflictPkgName	- if return true, check the 1st element of the array to get the conflict package name
		 * @return
		 */
		public boolean isConflict(String packageName, String partName, String[] conflictPkgName)
		{
			boolean isConflict = false;
			Set keySet = imports.keySet();
			Iterator itKeys = keySet.iterator();
			while(itKeys.hasNext() && !isConflict)
			{
				String existingPkgName = (String)(itKeys.next());
				if(!existingPkgName.equals(packageName))
				{
					Set parts = (Set)(imports.get(existingPkgName));
					//if parts.contains("*")
						//if existingPkgName.partName exists, then it is conflicts also
					if(parts.contains(partName))
					{
						isConflict = true;
						conflictPkgName[0] = existingPkgName;
					}
				}
			}
				
			return isConflict;
		}
		
		private static interface ImportCollector {
			void addImport(File fileAST, String onDemandPkgName, boolean isOnDemand, ImportComparator comparator);
		}
		
		/**
		 * write the import section to ASTRewrite based on on demond threshold preference
		 * 
		 * @param rewrite
		 * @param fileAST
		 */
		public Set addImportsToASTRewrite(final ASTRewrite rewrite, File fileAST) {
			return addImportsToImportCollector(new ImportCollector() {
				public void addImport(File fileAST, String onDemandPkgName, boolean isOnDemand, ImportComparator comparator) {
					rewrite.addImport(fileAST, onDemandPkgName, isOnDemand, comparator);
				}
			}, fileAST
			);
		}
		
		public Set addImportsToStringBuffer(final StringBuffer sb, File fileAST) {
			return addImportsToImportCollector(new ImportCollector() {
				public void addImport(File fileAST, String onDemandPkgName, boolean isOnDemand, ImportComparator comparator) {
					sb.append(IEGLConstants.KEYWORD_IMPORT);
					sb.append(" ");
					sb.append(onDemandPkgName);
					if(isOnDemand) {
						sb.append(".*");
					}
					sb.append(";");
					sb.append(System.getProperty("line.separator"));
				}
			}, fileAST
			);
		}
		
		private Set addImportsToImportCollector(ImportCollector collector, File fileAST)		
		{			
			Set /*<String>*/newImports = new LinkedHashSet();		//list of imported part or packages, i.e. "pkg1.pkg2.a", "pkg3.*"..., it is in the order that will be displayed 
			
			IPreferenceStore store = EDTUIPlugin.getDefault().getPreferenceStore();
			int onDemandThreshold = store.getInt(EDTUIPreferenceConstants.ORGIMPORTS_ONDEMANDTHRESHOLD);
			
			Set packages = imports.keySet();
			Iterator it = packages.iterator();
			while(it.hasNext())
			{
				String packageName = (String)it.next();
				Object obj = imports.get(packageName);
				if(obj != null)
				{
					Set parts = (Set)obj;
					int partCnts = parts.size();
					if(partCnts >= onDemandThreshold && packageName.length() != 0)
					{
						String onDemandPkgName = packageName;					
						collector.addImport(fileAST, onDemandPkgName, true, new ImportComparator());		
						onDemandPkgName = getFullImportName(onDemandPkgName, "*");
						newImports.add(onDemandPkgName);
					}
					else
					{
						Iterator itparts = parts.iterator();
						while(itparts.hasNext())
						{
							String importEntry = packageName;
							String partEntry = (String)(itparts.next());
							if(partEntry.equals("*")){		//on demand //$NON-NLS-1$
								collector.addImport(fileAST, importEntry, true, new ImportComparator());
								importEntry = getFullImportName(importEntry, partEntry);							
							}
							else
							{
								importEntry = getFullImportName(importEntry, partEntry);
								collector.addImport(fileAST, importEntry, false, new ImportComparator());
							}
							newImports.add(importEntry);
						}
					}
				}
			}
			
			return newImports; 
		}
		
		private String getFullImportName(String pkgName, String partName){
			String importEntry = pkgName;
			if(importEntry.length() > 0)
				importEntry += '.';								
			importEntry += partName;
			return importEntry;
		}
	}
	
	public OrganizeImportsOperation(IEGLFile file, boolean needSave, IChooseImportQuery query) {
		super();
		feglfile = file;
		this.needSave = needSave;
		this.fChooseImportQuery = query;

		fNumberOfImportsAdded= 0;
		fNumberOfImportsRemoved= 0;		
		fSyntaxError = null;
	}

	boolean workingCopyCreationInProgress = false;
	public IEGLFile getEGLFileWorkingCopy() {
		
		if (feglfile != null)
		{
			try {
				if ((workingCopy == null) && !workingCopyCreationInProgress)
				{
					workingCopyCreationInProgress = true;
					workingCopy = ((IEGLFile)feglfile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null));					
					workingCopyCreationInProgress = false;
				}
				return workingCopy;
			} catch (EGLModelException e) {
				e.printStackTrace();
				EDTUIPlugin.log(e);
			}
			
		}
		return null;
	}	

	public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		if(monitor == null)
			monitor = new NullProgressMonitor();
		
		try
		{
			fNumberOfImportsAdded= 0;
			fNumberOfImportsRemoved= 0;
			monitor.beginTask(MessageFormat.format(UINlsStrings.OrganizeImportsOperation_description, new String[]{feglfile.getElementName()}), 5);
							
			workingCopy = getEGLFileWorkingCopy();		
			IBuffer buf = workingCopy.getBuffer();
			if(buf instanceof DocumentAdapter)
			{
				IDocument doc = ((DocumentAdapter)buf).getDocument();
				if(doc instanceof IEGLDocument)
				{
					IEGLDocument egldoc = (IEGLDocument)doc;
					File fileAST = egldoc.getNewModelEGLFile();
										
					//check for syntax error
					AccumulatingSyntaxErrorMessageRequestor syntaxErrorRequestor = new AccumulatingSyntaxErrorMessageRequestor();
					fileAST.accept(syntaxErrorRequestor);
					Map syntaxErrMsgs = syntaxErrorRequestor.getSyntaxErrors();
					if(syntaxErrMsgs.isEmpty())		//no syntax error
					{
						IPackageFragment packageFrag = (IPackageFragment)(feglfile.getParent());		
						String packageName = packageFrag.getElementName();		//package name string
						
						OrganizedImportSection resolvedTypes = new OrganizedImportSection(packageName);
						Map /*<String>, <Name>*/ unresolvedTypes = new LinkedHashMap();
						
						IEGLProject eglProj = feglfile.getEGLProject();
						IProject proj = eglProj.getProject();
						
						Set oldImports = getExistingImports(fileAST);							
						//compile file to get bound ASTTree
						bindFile(proj, packageName, resolvedTypes, unresolvedTypes, oldImports);
						monitor.worked(2);
																
						try2ResolveUnresolvedTypes(resolvedTypes, unresolvedTypes, eglProj, oldImports, new SubProgressMonitor(monitor, 3));
							
						//update the file	
						writeToFile(resolvedTypes, oldImports, egldoc, fileAST, new SubProgressMonitor(monitor, 1));
					}
					else
					{
						//get the 1st syntax error
						Set keys = syntaxErrMsgs.keySet();
						Iterator it = keys.iterator();
						SyntaxError SyntaxErr = (SyntaxError)it.next();
						String errmsg = (String)(syntaxErrMsgs.get(SyntaxErr));
						fSyntaxError = new SyntaxErrorHelper(SyntaxErr, errmsg);						
						return;
					}
				}
			}
		}finally{
			if(workingCopy != null)
				workingCopy.destroy();			
			monitor.done();
		}
	}
	
	protected Set getExistingImports(File fileAST)
	{
		final Set /*<ImportDeclaration>*/ oldImports = new LinkedHashSet();	//save the old imports info to calculate the import result summary in the original order		
		fileAST.accept(new DefaultASTVisitor() {
			public boolean visit(File file) {return true;};
			public boolean visit(ImportDeclaration importDeclaration) {
				oldImports.add(importDeclaration);
				return false;
			}					
		});
		return oldImports;
	}

	protected void try2ResolveUnresolvedTypes(OrganizedImportSection resolvedTypes, Map unresolvedTypes, IEGLProject eglProj, Set oldImports, IProgressMonitor monitor) throws EGLModelException, OperationCanceledException {
		try
		{
			//for unresolved types, search them						
			//key is the Name node of the unresolvedTypeName
			//value is the list of PartInfo - the mulitple open choices 		
			Map /*<Name>, <List <PartInfo>>*/mapOpenChoices = new Hashtable();		
			
			Set Keys = unresolvedTypes.keySet();
			Iterator unresolvedIt = Keys.iterator();
			IEGLSearchScope projScope = SearchEngine.createEGLSearchScope(new IEGLElement[]{eglProj}, true);
			while(unresolvedIt.hasNext())
			{
				String unresolvedTypeName = (String)unresolvedIt.next(); 
				List typeList = new ArrayList();
				new SearchEngine().searchAllPartNames(ResourcesPlugin.getWorkspace(),
					null,
					unresolvedTypeName.toCharArray(),
					IIndexConstants.EXACT_MATCH,
					IEGLSearchConstants.CASE_INSENSITIVE,
					IEGLSearchConstants.PART,
					projScope,
					new PartInfoRequestor(typeList),
					IEGLSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
					monitor);
				
				removeTypesInDefaultPackage(typeList);
				
				int foundCnts = typeList.size();
				if(foundCnts == 1)
				{
					String packageName="";
					String partName="";					
					Object listElem = typeList.get(0);
					if(listElem instanceof PartInfo){
						PartInfo foundPart = (PartInfo)listElem;
						packageName = foundPart.getPackageName();
						partName = foundPart.getPartName();						
					}
					resolvedTypes.addImport(packageName, partName);				
				}
				else if(foundCnts > 1)
				{
//					boolean fndExisting = false;
					//more than 1 found,
					//see if one is already exsited in the old imports				
//					if(!fndExisting)
					{
						//otherwise, need to ask user
						Name unresolvedTypeNameNode = (Name)(unresolvedTypes.get(unresolvedTypeName));
						mapOpenChoices.put(unresolvedTypeNameNode, typeList);
					}
				}
			}
			
			//ask user to choose, add the result to the resovledTypes 
			if(!mapOpenChoices.isEmpty() && fChooseImportQuery != null)
			{
				PartInfo[] result = fChooseImportQuery.chooseImports(mapOpenChoices);
				if(result == null)
				{
					//cancel pressed by the user
					throw new OperationCanceledException();
				}
				
				for(int i=0; i<result.length; i++)
					resolvedTypes.addImport(result[i].getPackageName(), result[i].getPartName());
			}
		}finally{
			monitor.done();
		}
	}

	private void addSystemTypes(List typeList, String unresolvedTypeName, IProject project) {		
		IPartBinding sysPartBinding = SystemEnvironmentManager.findSystemEnvironment(project, null).getPartBinding(null, unresolvedTypeName);
		if(sysPartBinding != null && sysPartBinding != IBinding.NOT_FOUND_BINDING){
			//conver the IPartBinding to PartInfo
			String[] pkgName = sysPartBinding.getPackageName();
			IPath pkgPath = Util.stringArrayToPath(pkgName);
			String packageName = pkgPath.toString().replace(IPath.SEPARATOR, '.');
			String partName = sysPartBinding.getName();
			
			typeList.add(new SystemPartInfo(packageName, partName));
		}
	}

	private void removeTypesInDefaultPackage(List typeList) {
		for(Iterator iter = typeList.iterator(); iter.hasNext();) {
			PartInfo pInfo = (PartInfo) iter.next();
			if(pInfo.getPackageName().length() == 0) {
				iter.remove();
			}
		}
	}

	private void bindFile(final IProject proj, String currPackageName, final OrganizedImportSection resolvedTypes, final Map unresolvedTypes, final Set oldImports) throws EGLModelException {
		//bind the ast tree with live env and scope
		IWorkingCopy[] currRegedWCs = EGLCore.getSharedWorkingCopies(EGLUI.getBufferFactory());
		
		IFile file = (IFile)(feglfile.getCorrespondingResource());
		Path pkgPath = new Path(currPackageName.replace('.', IPath.SEPARATOR)); 					
		String[] pkgName = Util.pathToStringArray(pkgPath);

		//visit AST part tree(already bound)		
		WorkingCopyCompiler.getInstance().compileAllParts(proj, pkgName, file, currRegedWCs,		
					new IWorkingCopyCompileRequestor(){			
						public void acceptResult(WorkingCopyCompilationResult result) {
							Node boundnode = result.getBoundPart();
							if(boundnode instanceof Part){
								Part boundPart = (Part)boundnode;
								IBinding boundPartBinding = result.getPartBinding();
								
								Boolean isIncludeRefFunc = isIncludeReferenceFunction(boundPartBinding);
								Boolean topFuncUseContainerContext = isUseContainerContextDependent(boundPartBinding);
															
								final OrganizeImportsVisitor visitor = new OrganizeImportsVisitor(resolvedTypes, unresolvedTypes, oldImports, isIncludeRefFunc, proj);							
								visitor.setCurrentPartName(boundPart.getName());
								
								//if boundPart is not a topLevel function
								//or
								//if boundPart is a topLevel function, then only resolve the imports when useContainerContextDependent is no							
								if(topFuncUseContainerContext == null || topFuncUseContainerContext == Boolean.NO)
									boundPart.accept(visitor);			
								
								if(isIncludeRefFunc == Boolean.YES)
								{
									TopLevelFunction[] topBoundFuncs = result.getBoundFunctions();
									for(int i=0; i<topBoundFuncs.length; i++)
									{
										IBinding funcBinding = topBoundFuncs[i].getName().resolveBinding();
										if(funcBinding instanceof TopLevelFunctionBinding)
										{
											TopLevelFunctionBinding topFuncBinding = (TopLevelFunctionBinding)funcBinding;
											
											Boolean topFuncUseContainerConext1 = isUseContainerContextDependent(topFuncBinding);
											
											//if the bound top level function has useContainerContextDependent=yes
											//we need to resolve this top level function(its parameter, return type, all of its statements)
											//in the context of the result part(i.e. program)
											if(topFuncUseContainerConext1 == Boolean.YES)
												topBoundFuncs[i].accept(visitor);
										}
									}
								}
							}
						}						
				
						private Boolean isUseContainerContextDependent(IBinding binding)
						{
							Boolean topFuncUseContainerContext = null;
							
							//get the annotation value
							IAnnotationBinding containerContextAnnotationBinding = binding.getAnnotation(SystemEnvironmentPackageNames.EGL_CORE, IEGLConstants.PROPERTY_CONTAINERCONTEXTDEPENDENT);
							if(containerContextAnnotationBinding != null)
								topFuncUseContainerContext = (Boolean)(containerContextAnnotationBinding.getValue());
							else
								topFuncUseContainerContext = Boolean.NO;

							return topFuncUseContainerContext;
						}
						
						private Boolean isIncludeReferenceFunction(IBinding binding)
						{							
							Boolean isIncludeRefFunc = null;
							
							//get the annotation value
							IAnnotationBinding inclRefFuncAnnotationBinding = binding.getAnnotation(SystemEnvironmentPackageNames.EGL_CORE, IEGLConstants.PROPERTY_INCLUDEREFERENCEDFUNCTIONS);
							if(inclRefFuncAnnotationBinding != null)
								isIncludeRefFunc = (Boolean)(inclRefFuncAnnotationBinding.getValue());
							else
								isIncludeRefFunc = Boolean.NO;
						
							return isIncludeRefFunc;
						}
				}
		);
	}

	protected void writeToFile(OrganizedImportSection resolvedTypes, Set/*<ImportDeclaration>*/ oldImports, IEGLDocument egldoc, File fileAST, final IProgressMonitor monitor) {		
		ASTRewrite rewrite = ASTRewrite.create(fileAST);
		writeToFile(resolvedTypes, oldImports, egldoc, rewrite, fileAST, monitor);
	}

	protected void writeToFile(OrganizedImportSection resolvedTypes, Set/*<ImportDeclaration>*/ oldImports, IEGLDocument egldoc, ASTRewrite rewrite, File fileAST, final IProgressMonitor monitor) 
	{		
		try {
			Iterator itOld = oldImports.iterator();
			int firstImportStartOffset = Integer.MAX_VALUE;
			int lastImportEndOffset = 0;
			while(itOld.hasNext())
			{
				ImportDeclaration oldImportDecl = (ImportDeclaration)(itOld.next());
				int offset = oldImportDecl.getOffset();
//				rewrite.removeNode(oldImportDecl);
				
				firstImportStartOffset = Math.min(offset, firstImportStartOffset);
				lastImportEndOffset = Math.max(offset + oldImportDecl.getLength(), lastImportEndOffset);
			}
			
			//Set newImports = resolvedTypes.addImportsToASTRewrite(rewrite, fileAST);
			StringBuffer newImportsSB = new StringBuffer();
			Set newImports;
			if(oldImports.isEmpty()) {
				newImports = resolvedTypes.addImportsToASTRewrite(rewrite, fileAST);
			}
			else {
				newImports = resolvedTypes.addImportsToStringBuffer(newImportsSB, fileAST);
			}
			
			//compare the newImports with the original imports, see if it's necessary to rewrite it
			if(!determineImportDifferences(oldImports, newImports))
			{
				TextEdit astRewriteEdit = rewrite.rewriteAST(egldoc);
				
				TextEdit compositeEdit = new MultiTextEdit();
				compositeEdit.addChild(astRewriteEdit);
				if(!oldImports.isEmpty()) {
					compositeEdit.addChild(new ReplaceEdit(firstImportStartOffset, lastImportEndOffset - firstImportStartOffset, newImportsSB.toString().trim()));
				}
				
				compositeEdit.apply(egldoc);
				
				//save the files that needs to be saved(the ones that's not opened in egl editor)
	    		//this needs to run in the UI thread
	    		Display.getDefault().syncExec(new Runnable(){
	    			public void run(){
	    				Action saveAction = new Action(){
	    					public void run() {
	    						try{
	    							if(needSave)
										workingCopy.commit(true, new SubProgressMonitor(monitor, 1));
								} catch (EGLModelException e) {
									e.printStackTrace();
								}
	    					}
	    				};
	    				saveAction.run();
	    			}
	    		});
			}
						
		} catch (Exception e) {
			EDTUIPlugin.log(e);
		}
	}
	
	/**
	 * 
	 * @param oldImports
	 * @param newImports
	 * @return - false, there are difference
	 * 			 true, it's the same as the original one, may not need to rewrite the import section
	 */
	private boolean determineImportDifferences(Set/*<ImportDeclaration>*/ oldImports, Set /*<String>*/ newImports)	
	{
		boolean isSame = true;
		
		int oldCnts = oldImports.size();
		int newCnts = newImports.size();
		if(oldCnts != newCnts)
			isSame = false;
		
		Iterator itOld = oldImports.iterator();
		Iterator itNew = newImports.iterator();
		while(itOld.hasNext())			
		{
			ImportDeclaration oldImport = (ImportDeclaration)itOld.next();
			String oldImportName = oldImport.getName().getCanonicalString();
			if(oldImport.isOnDemand()){
				if(oldImportName.length()>0)
					oldImportName += '.';
				oldImportName += '*';
			}
			
			if(itNew.hasNext() && isSame){
				String newImportName = (String)itNew.next();
				if(!newImportName.equals(oldImportName))
					isSame = false;
			}
			
			if(newImports.contains(oldImportName))
			{
				newCnts--;
				oldCnts--;
			}
		}		
		fNumberOfImportsAdded = newCnts;
		fNumberOfImportsRemoved = oldCnts;
		
		return isSame;
	}

	public int getNumberOfImportsAdded() {
		return fNumberOfImportsAdded;
	}
	
	public int getNumberOfImportsRemoved() {
		return fNumberOfImportsRemoved;
	}
	
	/**
	 * @return Returns the scheduling rule for this operation
	 */
	public ISchedulingRule getScheduleRule() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}	
	
	public SyntaxErrorHelper getSyntaxError()
	{
		return fSyntaxError;
	}
}
