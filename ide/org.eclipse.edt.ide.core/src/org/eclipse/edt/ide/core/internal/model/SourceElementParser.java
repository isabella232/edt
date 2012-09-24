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
package org.eclipse.edt.ide.core.internal.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.AsExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.IsAExpression;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.utils.CharOperation;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.core.internal.model.index.IDocument;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.Flags;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;

/**
 * @author twilson created Aug 5, 2003
 */
public class SourceElementParser {

	boolean doTiming = false;

	ISourceElementRequestor requestor;

	Stack partStack;

	private boolean reportReferencesInStatements; // determine if we should
													// visit statements

	public class LocalReferenceVisitor extends AbstractASTVisitor {

		public boolean visit(final FunctionInvocation functionInvocation) {
			functionInvocation.getTarget().accept(new DefaultASTVisitor() {
				public boolean visit(SimpleName simpleName) {
					String functionName = simpleName.getCanonicalName();
					requestor.acceptFunctionReference(functionName.toCharArray(),
							functionInvocation.getArguments().size(),
							functionInvocation.getOffset());
					return false;
				}
				

				public boolean visit(QualifiedName qualifiedName) {
					String functionName = qualifiedName.getCaseSensitiveIdentifier();
					requestor.acceptFunctionReference(functionName.toCharArray(),
							functionInvocation.getArguments().size(),
							functionInvocation.getOffset());
					
					Name qualifier = qualifiedName.getQualifier();
					requestor.acceptUnknownReference(CharOperation.splitOn('.',
						qualifier.getCanonicalString().toCharArray()),
						qualifier.getOffset(), qualifier.getOffset()
								+ qualifier.getLength());
					
					return false;
				}
				public boolean visit(FieldAccess fieldAccess) {
					String functionName = fieldAccess.getID();
					requestor.acceptFunctionReference(functionName.toCharArray(),
							functionInvocation.getArguments().size(),
							functionInvocation.getOffset());
					
					Expression qualifier = fieldAccess.getPrimary();
					requestor.acceptUnknownReference(CharOperation.splitOn('.',
						qualifier.getCanonicalString().toCharArray()),
						qualifier.getOffset(), qualifier.getOffset()
								+ qualifier.getLength());
					
					return false;
				}
			});			

			return true;
		}

		public boolean visit (SimpleName name){
			requestor.acceptUnknownReference(name.getCaseSensitiveIdentifier().toCharArray(),name.getOffset());
			return false;
		}
		
		public boolean visit(QualifiedName qualifiedName) {
			Expression access = getDataAccessHelper(qualifiedName);
			if (access != null) {
				requestor.acceptUnknownReference(CharOperation.splitOn('.',
						access.getCanonicalString().toCharArray()), access
						.getOffset(), access.getOffset() + access.getLength());
			}
			return true;
		}

		public boolean visit(ArrayAccess arrayAccess) {
			Expression access = getDataAccessHelper(arrayAccess);
			if (access != null) {
				requestor.acceptUnknownReference(CharOperation.splitOn('.',
						access.getCanonicalString().toCharArray()), access
						.getOffset(), access.getOffset() + access.getLength());
			}
			return true;
		}

		public boolean visit(SubstringAccess substringAccess) {
			Expression access = getDataAccessHelper(substringAccess);
			if (access != null) {
				requestor.acceptUnknownReference(CharOperation.splitOn('.',
						access.getCanonicalString().toCharArray()), access
						.getOffset(), access.getOffset() + access.getLength());
			}
			return true;
		}

		public boolean visit(FieldAccess fieldAccess) {
			Expression access = getDataAccessHelper(fieldAccess);
			if (access != null) {
				requestor.acceptUnknownReference(CharOperation.splitOn('.',
						access.getCanonicalString().toCharArray()), access
						.getOffset(), access.getOffset() + access.getLength());
			}
			return true;
		}

		/**
		 * Iterate over this data access and find the longest non-simple data
		 * access from left to right that doesn't contain an array or substring
		 */
		private Expression getDataAccessHelper(Expression dataAccess) {
			Expression result = null;
			Expression temp = dataAccess;
			result = dataAccess;

			while (temp != null) {
				if (temp instanceof ArrayAccess
						|| temp instanceof SubstringAccess) {
					// Get the dataAccess attached to the array or substring
					Expression root;
					if (temp instanceof ArrayAccess) {
						root = ((ArrayAccess) temp).getArray();
					} else {
						root = ((SubstringAccess) temp).getPrimary();
					}

					// if it is a field, we should continue (i.e. A.C[])
					if (root instanceof FieldAccess || root instanceof QualifiedName) {
						result = root;
						temp = result;
					}
					else {
						// it is not a field, we are done (i.e. C[])
						temp = null;
					}
				} else if (temp instanceof FieldAccess) {
					temp = ((FieldAccess) temp).getPrimary();
				} else if (temp instanceof QualifiedName) {
					temp = ((QualifiedName) temp).getQualifier();
				}	
				else {
					temp = null;
				}
			}

			// Always remove the last field access if applicable
			if (result instanceof FieldAccess) {
				result = ((FieldAccess) result).getPrimary();
			}
			else if(result instanceof QualifiedName) {
				result = ((QualifiedName) result).getQualifier();
			}
			else {			
				result = null;
			}

			return result;
		}

		public boolean visit(CallStatement callStatement) {
			Expression expr = callStatement.getInvocationTarget();
			if(expr.isName()) {
				handleTypeNameReference((Name) expr, expr);
			}
			else {
				//TODO: could be a quoted string literal that resolved to a program. Decide whether we want to handle that here. Otherwise, there would be no search result, refactoring, etc... for referenced program 
			}
			return true;
		}

		public boolean visit (IsAExpression isa ){
			Type type = isa.getType();
			if (type.isNameType()){
				handleTypeNameReference(((NameType) type).getName(),type);
			}else{
				handleTypeReference(type);
			}
			return false;
		}
		
		public boolean visit (AsExpression as ){
			if(as.hasType()) {
				Type type = as.getType();
				if (type.isNameType()){
					handleTypeNameReference(((NameType) type).getName(),type);
				}else{
					handleTypeReference(type);
				}
			}
			return false;
		}
		
		public boolean visit (NewExpression newExpr ){
			Type type = newExpr.getType();
			if (type.isNameType()){
				handleTypeNameReference(((NameType) type).getName(),type);
			}else{
				handleTypeReference(type);
			}
			return true;
		}
	}

	public SourceElementParser(ISourceElementRequestor requestor) {
		this.requestor = requestor;
		partStack = new Stack();
	}

	protected void traverseASTTree(File result) {
		requestor.enterEGLFile();
		PackageDeclaration pkgDecl = result.getPackageDeclaration();
		List imports = result.getImportDeclarations();
		List parts = result.getParts();
		try {
			if (pkgDecl != null) {
				int declStart = pkgDecl.getOffset();
				int declEnd = declStart + pkgDecl.getLength();
				requestor.acceptPackage(declStart, declEnd, pkgDecl.getName()
						.getCanonicalName().toCharArray());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (Iterator iter = imports.iterator(); iter.hasNext();) {
			try {
				ImportDeclaration element = (ImportDeclaration) iter.next();
				int declStart = element.getOffset();
				int declEnd = declStart + element.getLength();
				requestor
						.acceptImport(declStart, declEnd, element.getName()
								.getCanonicalName().toCharArray(), element
								.isOnDemand());
				if (!element.isOnDemand()) {
					char[][] type = Util.toCompoundChars(element.getName()
							.getCanonicalName());
					int start = element.getOffset();
					int end = start + element.getLength();
					requestor.acceptPartReference(type, start, end);
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		handleParts(parts);
	}

	public void parseEGLFile(EGLFile file) {
		// doTiming = true;
		// long starttotal = System.currentTimeMillis();

		try {
			IBuffer buffer = file.getBuffer();
			if (buffer instanceof IEGLDocumentAdapter) {
				IEGLDocumentAdapter adapter = (IEGLDocumentAdapter) file
						.getBuffer();
				org.eclipse.jface.text.IDocument doc = adapter.getDocument();
				if(doc instanceof IEGLDocument){
					IEGLDocument eglDocument = (IEGLDocument) doc;
					traverseASTTree(eglDocument.getNewModelEGLFile());
				}
			} else {
				parseDocument(file, false);
			}
		} catch (EGLModelException e) {
			e.printStackTrace();
		}

		// long endtotal = System.currentTimeMillis();
		// long time = endtotal - starttotal;
		// System.out.println("File " + file.getElementName() + " time: " +
		// time);
	}

	public void parseDocument(IDocument file,
			boolean reportReferencesInStatements) {

		this.reportReferencesInStatements = reportReferencesInStatements;

		String content = null;
		try {
			content = file.getStringContent();
		} catch (Exception e) {
		}
		if (content == null)
			return;
		File result;
		try {
			// EGLTODO: Brian: Can this be refactored to use the IDocument that
			// is passed in?
			result = (new EGLDocument(content)).getNewModelEGLFile();
		} catch (Exception e) {
			return;
		}

		traverseASTTree(result);
	}

	private void handleParts(List parts) {
		for (Iterator iter = parts.iterator(); iter.hasNext();) {
			long strt = System.currentTimeMillis();
			Part part = (Part) iter.next();
			long end = System.currentTimeMillis();
			if (doTiming)
				System.out
						.println("Access part " + part.getName().getCanonicalName() + " time: " + (end - strt)); //$NON-NLS-1$ //$NON-NLS-2$
			int declEnd = 0;

			try {
				long starttotal = System.currentTimeMillis();
				declEnd = handleEnterPart(part, part.getName(), part.isPrivate(), part.getPartType(), part.getContents());

				for (Iterator contentIter = part.getContents().iterator(); contentIter
						.hasNext();) {
					((Node) contentIter.next()).accept(new DefaultASTVisitor() {
						public boolean visit(UseStatement useStatement) {
							handleUseDeclaration(useStatement);
							return false;
						}

						public boolean visit(ClassDataDeclaration dataDecl) {
							handleField(dataDecl);
							return false;
						}

						public boolean visit(NestedFunction nestedFunction) {
							handleFunction(nestedFunction);
							return false;
						}

						public boolean visit(StructureItem structureItem) {
							handleField(structureItem);
							return false;
						}
					});
				}

				part.accept(new DefaultASTVisitor() {
					public boolean visit(ExternalType externalType){
						for(Iterator iter = externalType.getExtendedTypes().iterator(); iter.hasNext();) {
							Name next = (Name) iter.next();
							requestor.acceptUnknownReference(next.getCanonicalName().toCharArray(), next.getOffset());
						}
						return false;
					}
					
					public boolean visit(Delegate delegate) {
						handleDelegate(delegate);
						return false;
					}
					
				});

				
				part.accept(new DefaultASTVisitor(){
					char[] name = null;
					public boolean visit(DataItem dataItem) {
						name = dataItem.getName().getCanonicalName().toCharArray();
						return true;
					}
					public boolean visit(Enumeration enumNode) {
						name = enumNode.getName().getCanonicalName().toCharArray();
						return true;
					}
					public boolean visit(SettingsBlock settingsBlock) {
						handlePropertyBlock(settingsBlock, name);
						return false;
					}
				});
				
				requestor.exitPart(declEnd);
				long endtotal = System.currentTimeMillis();
				long time = endtotal - starttotal;
				if (doTiming)
					System.out
							.println("Part " + part.getName().getCanonicalName() + " time: " + time); //$NON-NLS-1$ //$NON-NLS-2$

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static class FieldInfo {
		char[] type = new char[0];

		char[] name;

		int nameStart;

		int nameEnd;

		int modifiers = Flags.AccPublic;

		boolean hasOccurs = false;
	}

	private int handleEnterField(final Node decl) {
		int declStart = decl.getOffset();
		int declEnd = declStart + decl.getLength();
		final ArrayList list = new ArrayList();

		decl.accept(new DefaultASTVisitor() {

			public boolean visit(StructureItem structureItem) {
				FieldInfo fInfo = new FieldInfo();
				Type type = structureItem.getType();
				fInfo.name = structureItem.getName().getCanonicalName()
						.toCharArray();
				fInfo.nameStart = decl.getOffset();
				fInfo.nameEnd = fInfo.nameStart + fInfo.name.length;

				if (type != null) {
					fInfo.type = type.getCanonicalName().toCharArray();
				}

				if (type == null) {
					fInfo.hasOccurs = structureItem.hasOccurs();
				} else {
					fInfo.hasOccurs = type.isArrayType();
				}

				if (structureItem.hasSettingsBlock()) {
					handlePropertyBlock(structureItem.getSettingsBlock(),
							fInfo.name);
				}

				list.add(fInfo);
				return false;
			}

			public boolean visit(FunctionDataDeclaration dataDecl) {
				List names = dataDecl.getNames();
				Iterator iter = names.iterator();
				while(iter.hasNext()){
					FieldInfo fInfo = new FieldInfo();
					Name name = (Name) iter.next();
					fInfo.name = name.getCanonicalName().toCharArray();
					fInfo.nameStart = name.getOffset();
					fInfo.nameEnd = fInfo.nameStart + fInfo.name.length;
					fInfo.type = dataDecl.getType().getCanonicalName()
							.toCharArray();
									
					if (dataDecl.hasSettingsBlock()) {
						handlePropertyBlock(dataDecl.getSettingsBlockOpt(),
								fInfo.name);
					}
					
					list.add(fInfo);
				}
				return false;
			}

			public boolean visit(ClassDataDeclaration dataDecl) {
				List names = dataDecl.getNames();
				Iterator iter = names.iterator();
				while(iter.hasNext()){
					FieldInfo fInfo = new FieldInfo();
					Name name = (Name) iter.next();
					fInfo.name = name.getCanonicalName().toCharArray();
					fInfo.nameStart = name.getOffset();
					fInfo.nameEnd = fInfo.nameStart + fInfo.name.length;
					fInfo.type = dataDecl.getType().getCanonicalName()
							.toCharArray();
					if (dataDecl.isPrivate()) {
						fInfo.modifiers = Flags.AccPrivate;
					}
					if (dataDecl.hasSettingsBlock()) {
						handlePropertyBlock(dataDecl.getSettingsBlockOpt(),
								fInfo.name);
					}
					
					list.add(fInfo);
				}
				return false;
			}

			public boolean visit(FunctionParameter functionParameter) {
				FieldInfo fInfo = new FieldInfo();
				fInfo.name = functionParameter.getName().getCanonicalName()
						.toCharArray();
				fInfo.nameStart = functionParameter.getName().getOffset();
				fInfo.nameEnd = fInfo.nameStart + fInfo.name.length;
				fInfo.type = functionParameter.getType().getCanonicalName()
						.toCharArray();
				list.add(fInfo);
				return false;
			}
		});

		Iterator iter = list.iterator();
		while (iter.hasNext()){
			FieldInfo fInfo = (FieldInfo)iter.next();
			//TODO add type declared package info
			requestor.enterField(declStart, fInfo.modifiers, fInfo.type, null,
					fInfo.name, fInfo.nameStart, fInfo.nameEnd, fInfo.hasOccurs,declEnd);
			
		}
		

		return declEnd;
	}

	private static class PartInfo {
		char[] name;

		int nameStart;

		int nameEnd;

		int modifier;

		char[][] interfaceNames = null;
		
//		boolean hasParameters = false;
		char[][] parameterNames = null;
		char[][] parameterTypes = null;
		
		char[][] usagePartTypes = null;
		char[][] usagePartPackages = null;
	}

	private int handleEnterPart(Node partNode, Name partName, boolean isPrivate, int partType, List partContents) {
		int declStart = partNode.getOffset();
		int declEnd = declStart + partNode.getLength();
		final PartInfo pInfo = new PartInfo();

		pInfo.name = partName.getCanonicalName().toCharArray();
		pInfo.nameStart = partName.getOffset();
		pInfo.nameEnd = pInfo.nameStart + pInfo.name.length;
		pInfo.modifier = isPrivate ? Flags.AccPrivate : Flags.AccPublic;

		partNode.accept(new DefaultASTVisitor() {
			public boolean visit(Service service) {
				List implementedInterfaceNames = service
						.getImplementedInterfaces();

				int implementedInterfacesLength = implementedInterfaceNames
						.size();
				if (implementedInterfacesLength > 0) {
					pInfo.interfaceNames = new char[implementedInterfacesLength][];

					int i = 0;
					for (Iterator iter = implementedInterfaceNames.iterator(); iter
							.hasNext(); i++) {
						pInfo.interfaceNames[i] = ((Name) iter.next())
								.getCanonicalName().toCharArray();
					}
				}
				return false;
			}
		});
		requestor.enterPart(partType, getSubTypeString(partNode), partNode.hashCode(), // was: ((Node)
											// part).getText().hashCode(),
				declStart, pInfo.modifier, pInfo.name, pInfo.nameStart,
				pInfo.nameEnd, pInfo.interfaceNames, pInfo.parameterNames, pInfo.parameterTypes, pInfo.usagePartTypes, pInfo.usagePartPackages, null);

		for (Iterator contentsIter = partContents.iterator(); contentsIter
				.hasNext();) {
			((Node) contentsIter.next()).accept(new DefaultASTVisitor() {
				public boolean visit(SettingsBlock settingsBlock) {
					handlePropertyBlock(settingsBlock, pInfo.name);
					return false;
				}
			});
		}

		return declEnd;
	}

	// TODO: Encode subtype?
	private char[] getSubTypeString(Node part) {
		final String[] result = new String[1];

		part.accept(new DefaultASTVisitor() {
			public boolean visit(Program program) {
				result[0] = program.hasSubType() ? program.getSubType()
						.getCanonicalName() : null;
				return false;
			}

			public boolean visit(Library library) {
				result[0] = library.hasSubType() ? library.getSubType()
						.getCanonicalName() : null;
				return false;
			}

			public boolean visit(Record record) {
				result[0] = record.hasSubType() ? record.getSubType()
						.getCanonicalName() : null;
				return false;
			}

			public boolean visit(DataItem dataItem) {
				result[0] = dataItem.getType().getCanonicalName();
				return false;
			}

			public boolean visit(Handler handler) {
				result[0] = handler.hasSubType() ? handler.getSubType()
						.getCanonicalName() : null;
				return false;
			}

			public boolean visit(Interface interfacePart) {
				result[0] = interfacePart.hasSubType() ? interfacePart
						.getSubType().getCanonicalName() : null;
				return false;
			}
			
			public boolean visit(ExternalType externalType) {
				result[0] = externalType.hasSubType() ? externalType.getSubType().getCanonicalName() : null;
				return false;
			}
		});

		if (result[0] != null) {
			return result[0].toCharArray();
		}
		return null;
	}


	private List getFunctionVariableDeclarations(Node funcNode) {
		final List result = new ArrayList();
		funcNode.accept(new DefaultASTVisitor() {
			public boolean visit(NestedFunction nestedFunction) {
				return true;
			}

			public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
				result.add(functionDataDeclaration);
				return false;
			}
		});
		return result;
	}

	private int handleEnterDelegate(Delegate decl) {
		return handleEnterFunction(decl, decl.getName(), 
				decl.getParameters(),
				getFunctionVariableDeclarations(decl),
				decl.hasReturnType() ? decl.getReturnType() : null, !decl
						.isPrivate());
	}
	
	private int handleEnterFunction(NestedFunction decl) {
		return handleEnterFunction(decl, decl.getName(), decl
				.getFunctionParameters(),
				getFunctionVariableDeclarations(decl),
				decl.hasReturnType() ? decl.getReturnType() : null, !decl
						.isPrivate());
	}

	private int handleEnterFunction(Node funcNode, Name funcName, List parms,
			List decls, Type returnType, boolean isPublic) {
		int declStart = funcNode.getOffset();
		int declEnd = declStart + funcNode.getLength();
		char[] name = funcName.getCanonicalName().toCharArray();
		int nameStart = funcName.getOffset();
		int nameEnd = nameStart + name.length;

		char[][] parmNames = new char[parms.size()][];
		char[][] typeNames = new char[parms.size()][];
		char[][] useTypes = new char[parms.size()][];
		boolean[] areNullable = new boolean[parms.size()];
		int i = 0;
		for (Iterator iter = parms.iterator(); iter.hasNext();) {
			FunctionParameter parm = (FunctionParameter) iter.next();

			handleTypeReference(parm.getType());

			char[] typeName = parm.getType().getCanonicalName().toCharArray();
			char[] parmName = parm.getName().getCanonicalName().toCharArray();
			boolean isNullable = parm.isNullable();
			//TODO test use types. 06/18
			char[] useType = new char[0];
			if(parm.getUseType() != null){
				useType = parm.getUseType().toString().toCharArray();
			}
			parmNames[i] = parmName;
			typeNames[i] = typeName;
			useTypes[i] = useType;
			areNullable[i] = isNullable;
			i++;
		}
		
		int modifier = isPublic ? Flags.AccPublic : Flags.AccPrivate;
		char[] returnTypeStr = null;
		if (returnType != null) {
			returnTypeStr = returnType.getCanonicalName().toCharArray();

			handleTypeReference(returnType);
			// EGLTODO: Need to handle variable declarations
		}

		if (reportReferencesInStatements) {
			LocalReferenceVisitor refVisitor = new LocalReferenceVisitor();
			funcNode.accept(refVisitor);
		}
		//TODO add parameter packages and return type package info
		requestor.enterFunction(declStart, modifier, returnTypeStr, null, name,
				nameStart, nameEnd, typeNames, parmNames, useTypes, areNullable, null);
		
		for (Iterator iterator = decls.iterator(); iterator.hasNext();) {
			Object element = iterator.next();
			handleField((Node) element);
		}

		return declEnd;
	}

	private void handleDelegate(Delegate delegate){
		int end = handleEnterDelegate(delegate);
		requestor.exitFunction(end);
	}
	
	private void handleFunction(NestedFunction func) {
		int end = handleEnterFunction(func);
		requestor.exitFunction(end);
	}

	private void handleUseDeclaration(UseStatement decl) {
		int declStart = decl.getOffset();
		int declEnd = declStart + decl.getLength();

		for (Iterator iter = decl.getNames().iterator(); iter.hasNext();) {
			char[] name = ((Name) iter.next()).getCanonicalName().toCharArray();
			requestor.acceptUse(declStart, declEnd, name);
		}
	}

	private void handleInitializer(Expression expr){
		expr.accept(new AbstractASTExpressionVisitor(){
			
			public boolean visitExpression(Expression expression) {
			    return true;
			}

			private void handleType(Type type){
				if (type.isNameType()){
					handleTypeNameReference(((NameType) type).getName(),type);
				}else{
					handleTypeReference(type);
				}
			}
			public boolean visit (NewExpression newExpr){
				handleType(newExpr.getType());
				return true;
			}
			public boolean visit (AsExpression newExpr){
				if(newExpr.hasType()) {
					handleType(newExpr.getType());
				}
				return true;
			}
			public boolean visit (IsAExpression newExpr){
				handleType(newExpr.getType());
				return true;
			}
		});
	}
	
	private void handleField(final Node field) {
		int end = handleEnterField(field);

		field.accept(new DefaultASTVisitor() {
			public boolean visit(StructureItem structureItem) {
				Type type = structureItem.getType();
				if (type != null) {
					handleTypeReference(type);
				}
				
				if (structureItem.hasInitializer()){
					handleInitializer(structureItem.getInitializer());
				}
				return false;
			}

			public boolean visit(FunctionDataDeclaration dataDecl) {
				if (dataDecl.hasInitializer()){
					handleInitializer(dataDecl.getInitializer());
				}
				handleTypeReference(dataDecl.getType());
				return false;
			}

			public boolean visit(ClassDataDeclaration dataDecl) {
				if (dataDecl.hasInitializer()){
					handleInitializer(dataDecl.getInitializer());
				}
				handleTypeReference(dataDecl.getType());
				return false;
			}

			public boolean visit(FunctionParameter functionParameter) {
				handleTypeReference(functionParameter.getType());
				return false;
			}
		});

		requestor.exitField(end);

	}

	private void handlePropertyBlock(SettingsBlock block, char[] name) {
		int start = block.getOffset();
		int end = start + block.getLength();
		requestor.enterPropertyBlock(start, name);
		List decls = block.getSettings();
		for (Iterator iter = decls.iterator(); iter.hasNext();) {
			handleProperty((Node) iter.next());
		}
		requestor.exitPropertyBlock(end);
	}

	private void handleProperty(final Node decl) {
		decl.accept(new DefaultASTVisitor() {

			public boolean visit(AnnotationExpression annotationExpression) {
				// e.g. @Propery 
				Name exprName = annotationExpression.getName();
				requestor.acceptPropertyLiteralName(exprName.getOffset(), exprName.getOffset() + exprName.getLength(), exprName.getCanonicalString().toCharArray());
				return false;
			}
			
			public boolean visit(SetValuesExpression setValuesExpression) {
				Expression expr = setValuesExpression.getExpression();
				if (expr instanceof AnnotationExpression) {
					// e.g. @MyAnnotation{value = foo} - the "value = foo" part will be handled already in the
					// settings block, just need to index "MyAnnotation".
					Name exprName = ((AnnotationExpression)expr).getName();
					requestor.acceptPropertyLiteralName(exprName.getOffset(), exprName.getOffset() + exprName.getLength(), exprName.getCanonicalString().toCharArray());
				}
				
				char[] name = expr.getCanonicalString().toCharArray();
				SettingsBlock block = setValuesExpression.getSettingsBlock();
				handlePropertyBlock(block, name);
				return false;
			}

			public boolean visit(Assignment assignment) {
				final int start = decl.getOffset();
				final int end = start + decl.getLength();
				final String lhsText = assignment.getLeftHandSide().getCanonicalString();
				assignment.getRightHandSide().accept(new AbstractASTExpressionVisitor(){
					public boolean visitName(Name name) {
						char[] text = (lhsText + "=" + name.getCanonicalString()).toCharArray();
						// TODO Must be able to handle part/field references here
						requestor.acceptProperty(start, end, text);						
					    return false;
					}
					
					public boolean visitLiteral(LiteralExpression expression) {
						requestor.acceptPropertyLiteralName(start, end, lhsText.toCharArray());
						return false;
					}
					
					public boolean visitExpression(Expression expression) {
					    return true;
					}
				});

				return false;
			}
		});
	}

	private void handleTypeReference(Type type) {
		Type baseType = type.getBaseType();
		if (baseType.isNameType()) {
			handleTypeNameReference(((NameType) baseType).getName(), baseType);
		}
	}

	private void handleTypeNameReference(Name typeName, Node node) {
		if (typeName.isSimpleName()) {
			char[] name = typeName.getCanonicalName().toCharArray();
			requestor.acceptPartReference(name, node.getOffset());
		} else {
			char[][] names = Util.toCompoundChars(typeName.getCanonicalName());
			int start = node.getOffset();
			int end = start + node.getLength();
			requestor.acceptPartReference(names, start, end);
		}
	}
}
