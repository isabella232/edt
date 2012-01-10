/*******************************************************************************
 * Copyright ©2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FunctionContainerBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LocalVariableBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.VariableBinding;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;

public class EGLDeclarationProposalHandler extends EGLAbstractProposalHandler {
	//DataItem subtypes
	public static final int INTEGER_DATAITEM = 1;
	public static final int NUMERIC_DATAITEM = 2;
	public static final int STRING_DATAITEMS = 3;
	public static final int NUMERIC_STRING_DATAITEMS = 4;
	public static final int ALL_DATAITEMS = 5;

	//Record subtypes
	public static int BASIC_RECORD = 1<<0;
	public static int ENTITY_RECORD = 1<<1;
	public static int EXCEPTION = 1<<12;
	public static int ALL_RECORDS =  ENTITY_RECORD | EXCEPTION;
	
	private static interface IDataBindingFilter {
		boolean dataBindingPasses(IDataBinding dataBinding);
	}
	
	private static class DefaultDataBindingFilter implements IDataBindingFilter {
		public boolean dataBindingPasses(IDataBinding dataBinding) {
			return true;
		}
	}
	
	private static class RecordSubtypeDataBindingFilter implements IDataBindingFilter {
		private int recordTypes;
		private boolean allowArray;
		
		public RecordSubtypeDataBindingFilter(int recordTypes, boolean allowArray) {
			this.recordTypes = recordTypes;
			this.allowArray = allowArray;
		}
		
		public boolean dataBindingPasses(IDataBinding dataBinding) {
			ITypeBinding type = dataBinding.getType();			
			if(type != null && IBinding.NOT_FOUND_BINDING != type) {
				if(allowArray) {
					type = type.getBaseType();
				}
				switch(type.getKind()) {
					case ITypeBinding.FIXED_RECORD_BINDING:
					case ITypeBinding.FLEXIBLE_RECORD_BINDING:	
					case ITypeBinding.EXTERNALTYPE_BINDING:
					case ITypeBinding.HANDLER_BINDING:
						if((ENTITY_RECORD & recordTypes) == ENTITY_RECORD) {
							if(type.getAnnotation(EGLPERSISTENCE, IEGLConstants.SUBTYPE_ENTITY) != null) {
								return true;
							}
						}
						break;
				}
			}			
			return false;
		}
	}
	
	private static class DataItemTypeDataBindingFilter implements IDataBindingFilter {
		private int dataItemType;
		
		public DataItemTypeDataBindingFilter(int dataItemType) {
			this.dataItemType = dataItemType;
		}
		
		public boolean dataBindingPasses(IDataBinding dataBinding) {
			ITypeBinding type = dataBinding.getType();			
			if(type != null && IBinding.NOT_FOUND_BINDING != type) {
				type = type.getBaseType();
				if(ITypeBinding.PRIMITIVE_TYPE_BINDING == type.getKind()) {
					PrimitiveTypeBinding primType = (PrimitiveTypeBinding) type;
					Primitive prim = primType.getPrimitive();
					
					switch(dataItemType) {
						case ALL_DATAITEMS:
							return true;
							
						case STRING_DATAITEMS:
							return Primitive.isStringType(prim);
							
						case NUMERIC_DATAITEM:
							return Primitive.isNumericType(prim);
							
						case NUMERIC_STRING_DATAITEMS:
							return Primitive.isNumericType(prim) || Primitive.isStringType(prim);
							
						case INTEGER_DATAITEM:
							if (Primitive.isIntegerType(prim))
								return true;
							if (primType.getDecimals() == 0)
								if (prim == Primitive.NUM
									|| prim == Primitive.BIN
									|| prim == Primitive.DECIMAL
									|| prim == Primitive.NUMC
									|| prim == Primitive.PACF)
										return true;
					}
				}
			}
			return false;
		}
	}
	
	private static class ServiceTypeDataBindingFilter implements IDataBindingFilter {
		
		public ServiceTypeDataBindingFilter() {
		}
		
		public boolean dataBindingPasses(IDataBinding dataBinding) {
			ITypeBinding type = dataBinding.getType();			
			if(type != null && IBinding.NOT_FOUND_BINDING != type) {
				type = type.getBaseType();
				if(ITypeBinding.SERVICE_BINDING == type.getKind() || ITypeBinding.INTERFACE_BINDING == type.getKind())
					return true;
			}
			return false;
		}
	}
	
	private static class DataSourceTypeDataBindingFilter implements IDataBindingFilter{

		public DataSourceTypeDataBindingFilter(){
		}
		
		public boolean dataBindingPasses(IDataBinding dataBinding) {
			ITypeBinding type = dataBinding.getType();			
			if (type != null && IBinding.NOT_FOUND_BINDING != type) {
				type = type.getBaseType();

				if (isDataSourceType(type)) {
					return true;
				}

				if (ITypeBinding.EXTERNALTYPE_BINDING == type.getKind()) {
					List extendType = ((ExternalTypeBinding) (type))
							.getExtendedTypes();
					for (Iterator iterator = extendType.iterator(); iterator
							.hasNext();) {
						ITypeBinding aType = (ITypeBinding) iterator.next();
						if (isDataSourceType(aType)) {
							return true;
						}
					}
				}
			}

			return false;
		}
		
	}
	
	private static boolean isDataSourceType(ITypeBinding type){
		if (type.getName().equalsIgnoreCase(
				IEGLConstants.MIXED_DATASOURCE_STRING)
				|| type.getName().equalsIgnoreCase(
						IEGLConstants.MIXED_SQLDATASOURCE_STRING)
				|| type.getName().equalsIgnoreCase(
						IEGLConstants.MIXED_SQLRESULTSET_STRING)
				|| type.getName().equalsIgnoreCase(
						IEGLConstants.MIXED_SCROLLABLEDATASOURCE_STRING)) {
			return true;
		}
		return false;
	}
	
	private static class SQLStatementTypeDataBindingFilter implements IDataBindingFilter{

		public SQLStatementTypeDataBindingFilter(){
		}
		
		public boolean dataBindingPasses(IDataBinding dataBinding) {
			ITypeBinding type = dataBinding.getType();			
			if (type != null && IBinding.NOT_FOUND_BINDING != type) {
				type = type.getBaseType();
				if (isSqlStatement(type)) {
					return true;
				}
			}

			return false;
		}
		
	}
	
	private static boolean isSqlStatement(ITypeBinding type) {
		if (type.getName().equalsIgnoreCase(
				IEGLConstants.MIXED_SQLSTATEMENT_STRING)) {
			return true;
		}

		return false;
	}
	
	private static class SQLStringTypeDataBindingFilter implements IDataBindingFilter{

		public SQLStringTypeDataBindingFilter(){
		}
		
		public boolean dataBindingPasses(IDataBinding dataBinding) {
			ITypeBinding type = dataBinding.getType();			
			if (type != null && IBinding.NOT_FOUND_BINDING != type) {
				type = type.getBaseType();
				if (isSqlString(type)) {
					return true;
				}
			}

			return false;
		}
		
	}
	
	private static boolean isSqlString(ITypeBinding type) {
		if (type.getName().equalsIgnoreCase(
				IEGLConstants.KEYWORD_STRING)) {
			return true;
		}

		return false;
	}
	
	private static class SQLActionTargetsExternalTypeDataBindingFilter implements IDataBindingFilter{

		public SQLActionTargetsExternalTypeDataBindingFilter() {
		}
		
		@Override
		public boolean dataBindingPasses(IDataBinding dataBinding) {
			ITypeBinding type = dataBinding.getType();			
			if(type != null && IBinding.NOT_FOUND_BINDING != type) {
				type = type.getBaseType();
				if (type.getKind() == ITypeBinding.EXTERNALTYPE_BINDING
						&& !isDataSourceType(type) && !isSqlStatement(type)) {
					return true;
				}
			}
			return false;
		}

	}
	
	private static class HandlerTypeDataBindingFilter implements IDataBindingFilter{

		public HandlerTypeDataBindingFilter() {
		}
		
		@Override
		public boolean dataBindingPasses(IDataBinding dataBinding) {
			ITypeBinding type = dataBinding.getType();			
			if(type != null && IBinding.NOT_FOUND_BINDING != type) {
				type = type.getBaseType();
				if(ITypeBinding.HANDLER_BINDING == type.getKind())
					return true;
			}
			
			return false;
		}
		
	}
	
	private static class RecordTypeDataBindingFilter implements IDataBindingFilter{

		public RecordTypeDataBindingFilter() {
		}
		
		@Override
		public boolean dataBindingPasses(IDataBinding dataBinding) {
			ITypeBinding type = dataBinding.getType();			
			if(type != null && IBinding.NOT_FOUND_BINDING != type) {
				type = type.getBaseType();
				if (type.getKind() == ITypeBinding.FIXED_RECORD_BINDING
						|| type.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING){
					return true;
				}
			}
			return false;
		}

	}
	

	
	private Node functionContainerPart;
	private Node functionPart;
	private boolean parens;
	
	public EGLDeclarationProposalHandler(ITextViewer viewer, int documentOffset, String prefix, Node boundNode) {
		super(viewer, documentOffset, prefix);		
		while(!(boundNode instanceof File)) {
			if(boundNode instanceof NestedFunction) {
				functionPart = boundNode;
				functionContainerPart = boundNode.getParent();
			}
			else if(boundNode instanceof TopLevelFunction) {
				functionPart = boundNode;
				for(Iterator iter = ((File) boundNode.getParent()).getParts().iterator(); iter.hasNext();) {
					Node next = (Node) iter.next();
					if(next instanceof Program || next instanceof Library || next instanceof Service || next instanceof Handler) {
						functionContainerPart = next;
						break;
					}
				}
			}
			else if(boundNode instanceof Part) {
				functionContainerPart = boundNode;
			}
			boundNode = boundNode.getParent();
		}
	}

	public List getProposals(boolean parens, boolean constants) {
		return getProposals(null, parens, constants);
	}

	public List getProposals(Node boundNode) {
		return getProposals(boundNode, false, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getProposals(int, java.lang.String)
	 * get proposals for all the available declarations,
	 */
	public List getProposals(Node node, boolean parens, boolean constants) {
		this.parens = parens;
		List proposals = new ArrayList();
		proposals.addAll(getParameterProposals(false));
		proposals.addAll(getContainerVariableProposals(constants, false));
		proposals.addAll(getVariableProposals(constants, false));		
		if (node != null)
			proposals.addAll(getExceptionDeclarationProposals(node));
		return proposals;
	}

	public List getRecordProposals(int recordTypes) {
		return getRecordProposals(recordTypes, false);
	}

	public List getRecordProposals(int recordTypes, boolean allowArray) {
		return getRecordProposals(recordTypes, allowArray, false);
	}

	public List getRecordProposals(int recordTypes, boolean allowArray, boolean quoted) {
		return getRecordProposals(recordTypes, allowArray, quoted, false);
	}

	public List getRecordProposals(int recordTypes, boolean allowArray, boolean quoted, boolean parens) {
		return getRecordProposals(recordTypes, null, allowArray, quoted, parens);
	}

	public List getRecordProposals(int recordTypes, Node node, boolean allowArray, boolean quoted, boolean parens) {
		this.parens = parens;
		List proposals = new ArrayList();
		proposals.addAll(getParameterRecordProposals(recordTypes, allowArray, quoted));
		proposals.addAll(getContainerVariableRecordProposals(recordTypes, allowArray, quoted));
		proposals.addAll(getVariableRecordProposals(recordTypes, allowArray, quoted));
		if (node != null)
			proposals.addAll(getExceptionDeclarationProposals(node));
		return proposals;
	}

	public List getDataItemProposals(int integerType) {
		return getDataItemProposals(integerType, true);
	}

	public List getDataItemProposals(int integerType, boolean constants) {
		return getDataItemProposals(integerType, constants, false);
	}

	public List getDataItemProposals(int integerType, boolean constants, boolean parens) {
		this.parens = parens;
		List proposals = new ArrayList();
		proposals.addAll(getParameterDataItemProposals(integerType));
		proposals.addAll(getContainerVariableDataItemProposals(integerType, constants));
		proposals.addAll(getVariableDataItemProposals(integerType, constants));
		return proposals;
	}

	public List getServiceProposals() {
		
		List proposals = new ArrayList();
		proposals.addAll(getVariableProposals(new ServiceTypeDataBindingFilter(), false, false));
		proposals.addAll(getContainerVariableProposals(new ServiceTypeDataBindingFilter(), false, false));
		return proposals;
	}
	
	public List getDataSourceProposals(){
		List proposals = new ArrayList();
		proposals.addAll(getVariableProposals(new DataSourceTypeDataBindingFilter(), false, false));
		proposals.addAll(getContainerVariableProposals(new DataSourceTypeDataBindingFilter(), false, false));
		return proposals;
	}
	
	public List getSQLStatementProposals(){
		List proposals = new ArrayList();
		proposals.addAll(getVariableProposals(new SQLStatementTypeDataBindingFilter(), false, false));
		proposals.addAll(getContainerVariableProposals(new SQLStatementTypeDataBindingFilter(), false, false));
		return proposals;
	}
	
	public List getSQLStringProposals(){
		List proposals = new ArrayList();
		proposals.addAll(getVariableProposals(new SQLStringTypeDataBindingFilter(), false, false));
		proposals.addAll(getContainerVariableProposals(new SQLStringTypeDataBindingFilter(), false, false));
		return proposals;
	}

	private List getAllRecordProposals(){
		List proposals = new ArrayList();
		proposals.addAll(getParameterProposals(new RecordTypeDataBindingFilter(), false));
		proposals.addAll(getVariableProposals(new RecordTypeDataBindingFilter(), false, false));
		proposals.addAll(getContainerVariableProposals(new RecordTypeDataBindingFilter(), false, false));
		return proposals;
	}
	
	private List getAllHandlerProposals(){
		List proposals = new ArrayList();
		proposals.addAll(getVariableProposals(new HandlerTypeDataBindingFilter(), false, false));
		proposals.addAll(getContainerVariableProposals(new HandlerTypeDataBindingFilter(), false, false));
		return proposals;
	}
	
	private List getAllExternalTypeProposals(){
		List proposals = new ArrayList();
		proposals.addAll(getVariableProposals(new SQLActionTargetsExternalTypeDataBindingFilter(), false, false));
		proposals.addAll(getContainerVariableProposals(new SQLActionTargetsExternalTypeDataBindingFilter(), false, false));
		return proposals;
	}
	
	/**
	 * 
	 * @return return all sql action targets from the context
	 */
	public List getSQLActioniTargets(){
		List proposals = new ArrayList();
		
		proposals.addAll(getAllRecordProposals());
		proposals.addAll(getAllHandlerProposals());
		proposals.addAll(getAllExternalTypeProposals());
		
		return proposals;
	}

	/**
	 * @param includeConstants 
	 * @return
	 */
	private List getVariableDataItemProposals(int dataItemType, boolean includeConstants) {
		return getVariableProposals(new DataItemTypeDataBindingFilter(dataItemType), includeConstants, false);
	}

	/**
	 * @param includeConstants 
	 * @return
	 */
	private List getContainerVariableDataItemProposals(int dataItemType, boolean includeConstants) {
		return getContainerVariableProposals(new DataItemTypeDataBindingFilter(dataItemType), includeConstants, false);
	}

	/**
	 * @return
	 */
	private List getParameterDataItemProposals(int dataItemType) {
		return getParameterProposals(new DataItemTypeDataBindingFilter(dataItemType), false);
	}

	/**
	 * @return
	 */
	private List getContainerParameterDataItemProposals(int dataItemType) {
		return getContainerVariableProposals(new DataItemTypeDataBindingFilter(dataItemType), true, false);
	}
	
	private List getContainerParameterRecordProposals(int recordType, boolean allowArray, boolean quoted) {
		return getContainerVariableProposals(new RecordSubtypeDataBindingFilter(recordType, allowArray), true, quoted);
	}
	
	private List getVariableRecordProposals(int recordTypes, boolean allowArray, boolean quoted) {
		return getVariableProposals(new RecordSubtypeDataBindingFilter(recordTypes, allowArray), false, quoted);
	}
	
	/*
	 * get the parameter proposals for the current function
	 */
	private List getParameterProposals(boolean quoted) {
		return getParameterProposals(new DefaultDataBindingFilter(), quoted);
	}

	/*
	 * get the parameter proposals for the current function
	 */
	private List getParameterProposals(IDataBindingFilter dataBindingFilter, boolean quoted) {
		List proposals = new ArrayList();
		if (functionPart != null) {
			IFunctionBinding fBinding = functionPart instanceof TopLevelFunction ?
				(IFunctionBinding) ((TopLevelFunction) functionPart).getName().resolveBinding() :
				(IFunctionBinding) ((IDataBinding) ((NestedFunction) functionPart).getName().resolveBinding()).getType();
					
			for(Iterator iter = fBinding.getParameters().iterator(); iter.hasNext();) {
				IDataBinding dBinding = (IDataBinding) iter.next();
				if (dBinding.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
					if(dataBindingFilter.dataBindingPasses(dBinding) && precondition(dBinding)) {
						String proposalString = getProposalString(dBinding.getCaseSensitiveName());
						proposals.add(createDeclarationProposal(dBinding, proposalString, EGLCompletionProposal.RELEVANCE_VARIABLE_CONTAINER, quoted, true));					
					}
				}
			}
		}
		return proposals;
	}

	/*
	 * get the parameter record proposals for the current function
	 */
	private List getParameterRecordProposals(int recordTypes, boolean allowArray, boolean quoted) {
		return getParameterProposals(new RecordSubtypeDataBindingFilter(recordTypes, allowArray), quoted);
	}
	
	public List getProgramParameterRecordProposals(int recordTypes, boolean allowArray, boolean quoted) {
		parens = false;
		List proposals = new ArrayList();
		if (functionContainerPart instanceof Program)
			proposals.addAll(getContainerParameterRecordProposals(recordTypes, allowArray, quoted));
		return proposals;
	}

	/**
	 * @param string
	 * @return
	 */
	private String getProposalString(String string) {
		if (parens)
			return "(" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		return string;
	}
	
	/*
	 * get the declaration proposals for the current function's container (program)
	 */
	private List getContainerVariableProposals(boolean includeConstants, boolean quoted) {
		return getContainerVariableProposals(new DefaultDataBindingFilter(), includeConstants, quoted);
	}

	/*
	 * get the declaration proposals for the current function's container (program)
	 */
	private List getContainerVariableProposals(IDataBindingFilter dataBindingFilter, boolean includeConstants, boolean quoted) {
		List proposals = new ArrayList();
		if (functionContainerPart != null) {
			IBinding binding = ((Part) functionContainerPart).getName().resolveBinding();
			if(binding instanceof FunctionContainerBinding) {
				FunctionContainerBinding pBinding = (FunctionContainerBinding) binding;
				for(Iterator iter = pBinding.getDeclaredData(true).iterator(); iter.hasNext();) {
					IDataBinding dBinding = (IDataBinding) iter.next();
					if (dBinding.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
						if(dataBindingFilter.dataBindingPasses(dBinding) && precondition(dBinding)) {
							if(includeConstants || IDataBinding.CLASS_FIELD_BINDING != dBinding.getKind() || !((ClassFieldBinding) dBinding).isConstant()) {
								String proposalString = getProposalString(dBinding.getCaseSensitiveName());
								proposals.add(createDeclarationProposal(dBinding, proposalString, EGLCompletionProposal.RELEVANCE_VARIABLE_CONTAINER, quoted, false));
							}
						}
					}
				}
			}
		}
		return proposals;
	}

	/*
	 * get the declaration record proposals for the current function's container (program)
	 */
	private List getContainerVariableRecordProposals(int recordTypes, boolean allowArray, boolean quoted) {
		return getContainerVariableProposals(new RecordSubtypeDataBindingFilter(recordTypes, allowArray), false, quoted);
	}
	
	/*
	 * get the declaration proposals for the current function
	 */
	private List getVariableProposals(boolean includeConstants, boolean quoted) {
		return getVariableProposals(new DefaultDataBindingFilter(), includeConstants, quoted);
	}

	/*
	 * get the declaration proposals for the current function
	 */
	private List getVariableProposals(IDataBindingFilter dataBindingFilter, boolean includeConstants, boolean quoted) {
		final List proposals = new ArrayList();
		if (functionPart != null) {
			final Stack blocks = new Stack();	//elements are List objects, representing lists of variables			
			blocks.push(new ArrayList());			
			
			List statements = functionPart instanceof TopLevelFunction ?
				((TopLevelFunction) functionPart).getStmts() :
				((NestedFunction) functionPart).getStmts();
				
			addDeclarations(statements, blocks);
			
			for(Iterator iter = blocks.iterator(); iter.hasNext();) {
				for(Iterator iter2 = ((List) iter.next()).iterator(); iter2.hasNext();) {
					IDataBinding dBinding = (IDataBinding) iter2.next();
					if (dBinding.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
						if(dataBindingFilter.dataBindingPasses(dBinding) && precondition(dBinding)) {
							if(includeConstants || IDataBinding.LOCAL_VARIABLE_BINDING != dBinding.getKind() || !((LocalVariableBinding) dBinding).isConstant()) {
								String proposalString = getProposalString(dBinding.getCaseSensitiveName());
								proposals.add(createDeclarationProposal(dBinding, proposalString, EGLCompletionProposal.RELEVANCE_VARIABLE_CONTAINER, quoted, true));
							}
						}
					}
				}
			}
		}
		return proposals;
	}

	/*
	 * get the exception declaration proposals for the current onException block
	 */
	private List getExceptionDeclarationProposals(Node node) {
		return getExceptionDeclarationProposals(node, new DefaultDataBindingFilter());
	}

	/*
	 * get the exception declaration proposals for the current onException block
	 */
	private List getExceptionDeclarationProposals(Node node, IDataBindingFilter dataBindingFilter) {
		List proposals = new ArrayList();
		OnExceptionBlock onExceptionBlock = getOnExceptionBlock(node);
		if (onExceptionBlock != null) {
			Name exceptionName = onExceptionBlock.getExceptionName();
			if (exceptionName != null && exceptionName.getCanonicalName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
				IDataBinding dBinding = exceptionName.resolveDataBinding();
				if (dBinding.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
					if(dataBindingFilter.dataBindingPasses(dBinding) && precondition(dBinding)) {
						String proposalString = getProposalString(dBinding.getCaseSensitiveName());
						proposals.add(createDeclarationProposal(dBinding, proposalString, EGLCompletionProposal.RELEVANCE_VARIABLE_CONTAINER, false, true));
					}
				}
			}
		}
		return proposals;
	}

	private OnExceptionBlock getOnExceptionBlock(Node node) {
		final OnExceptionBlock onExceptionBlock[] = new OnExceptionBlock[] {null};
		while (node != null) {
			node.accept(new DefaultASTVisitor() {
				public boolean visit(OnExceptionBlock onExceptionBlk) {
					onExceptionBlock[0] = onExceptionBlk;
					return false;
				}
			});
			node = node.getParent();
		}
		return onExceptionBlock[0];
	}

	private boolean addDeclarations(List statements, final Stack blocks) {
		boolean done = false;
		Statement lastStatement = null;
		for(Iterator iter = statements.iterator(); iter.hasNext() && !done;) {
			Node next = (Node) iter.next();
			
			if(next instanceof Statement) {
				Statement nextStmt = (Statement) next;
				lastStatement = nextStmt;
				
				if(nextStmt.getOffset() > getDocumentOffset()) {
					done = true;
				}
				else if(nextStmt.canIncludeOtherStatements()) {
					for(Iterator blockIter = nextStmt.getStatementBlocks().iterator(); blockIter.hasNext();) {
						blocks.push(new ArrayList());
						nextStmt.accept(new DefaultASTVisitor() {
							public boolean visit(org.eclipse.edt.compiler.core.ast.ForStatement forStatement) {
								if(forStatement.hasVariableDeclaration()) {
									IBinding binding = forStatement.getVariableDeclarationName().resolveBinding();
									if(binding != null && IBinding.NOT_FOUND_BINDING != binding && binding.isDataBinding()) {
										((List) blocks.peek()).add(binding);
									}
								}
								return false;
							}
						});
						if(addDeclarations((List) blockIter.next(), blocks)) {
							done = true;
						}
						else {
							blocks.pop();
						}
					}
				}
				else {
					nextStmt.accept(new DefaultASTVisitor() {
						public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
							for(Iterator iter = functionDataDeclaration.getNames().iterator(); iter.hasNext();) {
								IBinding binding = ((Name) iter.next()).resolveBinding();
								if(binding != null && IBinding.NOT_FOUND_BINDING != binding && binding.isDataBinding()) {
									((List) blocks.peek()).add(binding);
								}
							}
							return false;
						}
					});
				}				
			}
		}
		
		if(!done && lastStatement != null) {
			if(lastStatement.getOffset()+lastStatement.getLength() > getDocumentOffset()) {
				done = true;
			}
		}
		
		return done;
	}

	/**
	 * @param variable
	 * @param containerPart
	 */
	private String getAdditionalInfo1(IDataBinding variable) {
		if (variable.getType() != null) {
			String type = getTypeText(variable);
			switch(variable.getKind()) {
				case IDataBinding.SERVICE_REFERENCE_BINDING:
					return MessageFormat.format(UINlsStrings.CAProposal_ServiceReferenceDeclarationIn, new String[] {type, variable.getDeclaringPart().getCaseSensitiveName()});
				case IDataBinding.PROGRAM_PARAMETER_BINDING:
					return MessageFormat.format(UINlsStrings.CAProposal_ParameterDeclarationIn, new String[] {type, variable.getDeclaringPart().getCaseSensitiveName()});
				case IDataBinding.FUNCTION_PARAMETER_BINDING:
					return MessageFormat.format(UINlsStrings.CAProposal_ParameterDeclaration, new String[] {type});
				case IDataBinding.LOCAL_VARIABLE_BINDING:
					if (((VariableBinding) variable).isConstant())
						return MessageFormat.format(UINlsStrings.CAProposal_ConstantDeclaration, new String[] {type});
					else
						return MessageFormat.format(UINlsStrings.CAProposal_VariableDeclaration, new String[] {type});
				case IDataBinding.CLASS_FIELD_BINDING:
					if (((VariableBinding) variable).isConstant())
						return MessageFormat.format(UINlsStrings.CAProposal_ConstantDeclarationIn, new String[] {type, variable.getDeclaringPart().getCaseSensitiveName()});
					else
						return MessageFormat.format(UINlsStrings.CAProposal_VariableDeclarationIn, new String[] {type, variable.getDeclaringPart().getCaseSensitiveName()});
				default :
					if (functionContainerPart == null)
						return MessageFormat.format(UINlsStrings.CAProposal_VariableDeclaration, new String[] {type});
					else
						return MessageFormat.format(UINlsStrings.CAProposal_VariableDeclarationIn, new String[] {type, variable.getDeclaringPart().getCaseSensitiveName()});
			}
		}
		else {
			if (functionContainerPart == null)
				return MessageFormat.format(UINlsStrings.CAProposal_VariableDeclaration, new String[] {""}); //$NON-NLS-1$
			else
				return MessageFormat.format(UINlsStrings.CAProposal_VariableDeclarationIn, new String[] {"", variable.getDeclaringPart().getCaseSensitiveName()}); //$NON-NLS-1$
		}
	}

	/**
	 * @param variable
	 * @return
	 */
	private String getTypeText(IDataBinding variable) {
		if (variable.getType() != null) {
			return variable.getType().getCaseSensitiveName();
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * create the proposal
	 */
	private EGLCompletionProposal createDeclarationProposal(IDataBinding variable, String proposalString, int relevance, boolean quoted, boolean isLocalVariable) {
		if (quoted)
			proposalString = "\"" + proposalString + "\""; //$NON-NLS-1$ //$NON-NLS-2$
	
		String imgStr = isPrivateField(variable) ? imgStr = PluginImages.IMG_OBJS_ENV_VAR_PRIVATE : PluginImages.IMG_OBJS_ENV_VAR;
		String displayStr = isLocalVariable ? variable.getCaseSensitiveName() + " : " + getTypeText(variable) : variable.getCaseSensitiveName() + " : " + getTypeText(variable) + " - " + variable.getDeclaringPart().getCaseSensitiveName();
		return new EGLCompletionProposal(viewer,
			displayStr,
			proposalString,
			getAdditionalInfo1(variable),
			getDocumentOffset() - getPrefix().length(),
			getPrefix().length(),
			proposalString.length(),
			relevance,
			imgStr);
	}

	/**
	 * @return boolean
	 * 
	 * This allows subclases to restrict the proposals
	 */
	protected boolean precondition(IDataBinding dBinding) {
		return true;
	}

}
