/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
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

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.AsExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.IsAExpression;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.ReturningToNameClause;
import org.eclipse.edt.compiler.core.ast.ReturnsDeclaration;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.StructuredRecord;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


/**
 * @author svihovec
 *
 */
public class MatchVisitor extends AbstractASTExpressionVisitor {

	public MatchingNodeSet matchSet;
	private org.eclipse.edt.mof.egl.Part binding;
	
	public boolean visit(Enumeration enumeration) {
		visitPart(enumeration);
		return true;
	}
	
	public boolean visit(Delegate delegate) {
		visitPart(delegate);
		return true;
	}
	
	public boolean visit(ExternalType externalType) {
		visitPart(externalType);
		for(Iterator iter = externalType.getExtendedTypes().iterator(); iter.hasNext();) {
			matchName((Name) iter.next());
		}
		return true;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		handlePropertyBlock(settingsBlock);
		return false;
	}
	
	private void handleProperty(final Node decl) {
		decl.accept(new DefaultASTVisitor() {

			public boolean visit(AnnotationExpression annotationExpression) {
				matchName(annotationExpression.getName());
				return false;
			}
			public boolean visit(SetValuesExpression setValuesExpression) {
				Expression expr = setValuesExpression.getExpression();
				if (expr instanceof AnnotationExpression) {
					matchName(((AnnotationExpression)expr).getName());
				}
				
				SettingsBlock block = setValuesExpression.getSettingsBlock();
				handlePropertyBlock(block);
				return false;
			}

			public boolean visit(Assignment assignment) {
				Expression expr = assignment.getRightHandSide();
				expr.accept(new AbstractASTExpressionVisitor(){
					public boolean visitName(Name name) {
						matchName(name);
					    return false;
					}
					
					public boolean visitExpression(Expression expression) {
					    return true;
					}
				});
				assignment.getLeftHandSide().accept(new AbstractASTExpressionVisitor(){
					public boolean visitName(Name name) {
						matchName(name);
					    return false;
					}
				});

				return false;
			}
		});
	}

	private void handlePropertyBlock(SettingsBlock block) {
		List decls = block.getSettings();
		for (Iterator iter = decls.iterator(); iter.hasNext();) {
			handleProperty((Node) iter.next());
		}

	}

	public boolean visit(NestedFunction function) {
		if ((this.matchSet.matchContainer & SearchPattern.ALL_FUNCTIONS) != 0 ||
			(this.matchSet.matchContainer & SearchPattern.PART) != 0){ // functions
				int level = matchSet.isMatchingNestedFunctionPart(function) ; 
				if (level == SearchPattern.ACCURATE_MATCH){
					matchSet.addTrustedMatch(function);
				}else if (level == SearchPattern.INACCURATE_MATCH){
					matchSet.addInaccurateMatch(function);
				}
		}
		function.accept(new SpecialVisitor(this));
		return super.visit(function);
	}

	public boolean visit(Library library) {
		visitPart(library);
		return super.visit(library);
	}
	
	public boolean visit(Handler handler) {
		visitPart(handler);
		return super.visit(handler);
	}
	
	public boolean visit(Service service) {
		visitPart(service);

	    for (Iterator iter = service.getImplementedInterfaces().iterator(); iter.hasNext();) {
	    	this.matchName((Name)iter.next());
	    }
	    
		// visit the service interfaces
		if ((this.matchSet.matchContainer & SearchPattern.PART) != 0) // part
		{
//		    for (Iterator iter = service.getImplementedInterfaces().iterator(); iter.hasNext();) {
//		    	this.matchSet.checkMatching((Node)iter.next());
//		    }
		}
		return super.visit(service);
	}
	
	public boolean visit(Interface intrface) {
		visitPart(intrface);
		return super.visit(intrface);
	}


	public boolean visit(Program program) {
		visitPart(program);
		return super.visit(program);
	}
	
	public boolean visit(EGLClass eglClass) {
		visitPart(eglClass);
		return super.visit(eglClass);
	}

	public boolean visit(Record record) {
		visitPart(record);
		return super.visit(record);
	}
	
	private class SpecialVisitor extends AbstractASTVisitor {
		private MatchVisitor visitor = null;
		public SpecialVisitor(MatchVisitor mvisitor){
			visitor = mvisitor;
		}
		
		private void handleType (Type type){
			Type baseType = type.getBaseType();
			if (baseType.isNameType()) {
				visitor.visit((NameType)baseType);
				if (binding != null){
					matchSet.addTrustedMatch(baseType);
					binding = null;
				}
			}
		}
		
		public boolean visit (IsAExpression isa ){
			Type type = isa.getType();
			handleType(type);
			return true;
		}
		
		public boolean visit (AsExpression as ){
			if(as.hasType()) {
				Type type = as.getType();
				handleType(type);
			}
			return true;
		}
		
		public boolean visit (NewExpression newExpr ){
			Type type = newExpr.getType();
			handleType(type);
			
			Type root = type.getBaseType();
			if (root.isNameType()) {
				List list = ((NameType)root).getArguments();
				Iterator iter = list.iterator();
				while (iter.hasNext()){
					Node node = (Node)iter.next();
					node.accept(new SpecialVisitor(visitor));
				}
			}
			
			if (newExpr.hasSettingsBlock()) {
				newExpr.getSettingsBlock().accept(new SpecialVisitor(visitor));
			}
			
			newExpr.accept(new AbstractASTVisitor(){
				public boolean visit (QualifiedName name){
					name.accept(visitor);
					return false;
				}				
			});
			return false;
		}	
	}
	
	public void visitPart(Part part)
	{
		if ((this.matchSet.matchContainer & SearchPattern.PART) != 0) // part
		{
			int level = matchSet.isMatchingPart(part) ; 
			if (level == SearchPattern.ACCURATE_MATCH){
				matchSet.addTrustedMatch(part);
			}else if (level == SearchPattern.INACCURATE_MATCH){
				matchSet.addInaccurateMatch(part);
			}
		}
		
		part.accept(new SpecialVisitor(this));
	}
	
	protected void matchList(List list){
		Iterator iter = list.iterator();
		while(iter.hasNext()){
			this.matchSet.addTrustedMatch((Node)iter.next());
		}
	}
	
	protected void matchField(Type type ,Name name){
		int level = this.matchSet.getMatchingLevel(type);
		if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
//			this.matchSet.addPossibleMatch(name);
		}
	}
	
	protected void matchField(Type type ,List list){
		int level = this.matchSet.getMatchingLevel(type);
		if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
//			matchList(list);
		}
	}
	
	private boolean isAny(Type type){
		Type baseType = type.getBaseType();
		org.eclipse.edt.mof.egl.Type typeBinding = baseType.resolveType();
		if (typeBinding != null){
			return TypeUtils.Type_ANY.equals(typeBinding);
		}
		
		return false;
	}
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		
		if (classDataDeclaration.getSettingsBlockOpt() != null) {
			classDataDeclaration.getSettingsBlockOpt().accept(this);
		}
		
		if (classDataDeclaration.getInitializer() != null) {
			classDataDeclaration.getInitializer().accept(this);
		}
		classDataDeclaration.getType().accept(this);
		
		return false;
	}
	
	public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
		
		if (functionDataDeclaration.getSettingsBlockOpt() != null) {
			functionDataDeclaration.getSettingsBlockOpt().accept(this);
		}
		
		if (functionDataDeclaration.getInitializer() != null) {
			functionDataDeclaration.getInitializer().accept(this);
		}
		functionDataDeclaration.getType().accept(this);
		
		return false;
	}
	
	public void endVisit(FunctionDataDeclaration functionDataDeclaration){
		boolean bContinue = !isAny(functionDataDeclaration.getType());
		if (bContinue && binding != null){
			this.matchSet.addTrustedMatch(functionDataDeclaration.getType().getBaseType());
		}
		binding = null;
	}
	
	public void endVisit(FunctionParameter functionParameter) {
		if (binding != null){
			this.matchSet.addTrustedMatch(functionParameter.getType().getBaseType());
		}
		binding = null;
	}
	
	public void endVisit(ReturnsDeclaration returnsDeclaration){
		if (binding != null){
			this.matchSet.addTrustedMatch(returnsDeclaration.getType().getBaseType());
		}
		binding = null;
	}
	
	public void endVisit(ClassDataDeclaration classDataDeclaration) {
		boolean bContinue = !isAny(classDataDeclaration.getType());
		if (bContinue && binding != null){
			this.matchSet.addTrustedMatch(classDataDeclaration.getType().getBaseType());
		}
		binding = null;
	}
	
	public void endVisit(FunctionInvocation functionInvocation) {
		if (binding != null){
			this.matchSet.addTrustedMatch(functionInvocation.getTarget());
		}else if (functionInvocation.getTarget().isName()){
			Name target = (Name)functionInvocation.getTarget();
			org.eclipse.edt.mof.egl.Type typeBinding = target.resolveType();
			if (typeBinding != null){
				if (typeBinding instanceof FunctionPart){
					int level = matchSet.isMatchingFunctionType(target, (FunctionPart)typeBinding);
					
					if (level == SearchPattern.ACCURATE_MATCH){
						matchSet.addTrustedMatch(target);
					}else if (level == SearchPattern.INACCURATE_MATCH){
						matchSet.addInaccurateMatch(target);
					}
					
					return ;
				} else if (typeBinding instanceof org.eclipse.edt.mof.egl.Delegate) {
					/**
					 * stop processing if the invocation is a delegate, since the
					 * delegate will always be a variable, and should really not be
					 * found by any search...as we do not have a variable search
					 */
					int level = matchSet.isMatchingType(target,(org.eclipse.edt.mof.egl.Delegate)typeBinding);
					if (level == SearchPattern.ACCURATE_MATCH){
						matchSet.addTrustedMatch(target);
					}
					return ;
				}
			}
			else {
				Member m = target.resolveMember();
				if (m instanceof FunctionMember) {
					int level = matchSet.isMatchingFunctionType(target, null);
					
					if (level == SearchPattern.ACCURATE_MATCH){
						matchSet.addTrustedMatch(target);
					}else if (level == SearchPattern.INACCURATE_MATCH){
						matchSet.addInaccurateMatch(target);
					}
					
					return ;
				}
			}
			
			if (matchSet.getMatchingLevel(target) == SearchPattern.POSSIBLE_MATCH){
				matchSet.addInaccurateMatch(target);
			}

		}
		
		binding = null;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.internal.pgm.model.IEGLModelVisitor#visit(com.ibm.etools.egl.internal.pgm.model.IEGLEmbeddedRecordStructureItem)
	 */
//	public boolean visit(StructureItem embeddedRecordStructureItem) {
//		if (embeddedRecordStructureItem.isEmbedded()){
//			return true;
//		}
//		
//		return false;
////		return super.visit(embeddedRecordStructureItem);
//	}
	
	public void endVisit(StructureItem embeddedRecordStructureItem) {
		boolean bContinue = embeddedRecordStructureItem.getType() != null && !isAny(embeddedRecordStructureItem.getType());
		if (bContinue && binding != null){
//			if (embeddedRecordStructureItem.isEmbedded()||binding.getKind() == ITypeBinding.DATAITEM_BINDING){
				if (binding instanceof StructuredRecord || binding instanceof org.eclipse.edt.mof.egl.Record)
					this.matchSet.addTrustedMatch(embeddedRecordStructureItem.getType().getBaseType());
				else this.matchSet.addTrustedMatch(embeddedRecordStructureItem.getType().getBaseType());
//			}
		}
		binding = null;
	}
	
	
	public boolean visit(UseStatement useStatement) {
		List useTargets = useStatement.getNames();
		
		for(int i=0; i<useTargets.size(); i++)
		{
			//For qualified names, we may need to strip away the qualifiers to find the part we are interested in
			if (useTargets.get(i) instanceof QualifiedName) {
				((Name)useTargets.get(i)).accept(this);
			}
			else {
				matchName((Name)useTargets.get(i));
			}
		}
		return false;
	}
	
	public boolean visitExpression(Expression dataAccess) {
//		matchSet.checkMatching(dataAccess);
//		if(isMethodName(dataAccess)) {
//			matchSet.checkMatching((Node) dataAccess);
//		}else {
//			IEGLDataAccess nonMethodNameAccess = getDataAccessHelper(dataAccess);
//			if(nonMethodNameAccess != null) {
//				matchSet.checkMatching((Node)nonMethodNameAccess);
//			}
//		}
//		return super.visitExpression(dataAccess);
		return true;
	}
	
	public boolean visit(NameType nameType) {
		nameType.accept(new AbstractASTExpressionVisitor(){
			public boolean visitName(Name name) {
				org.eclipse.edt.mof.egl.Type type = name.resolveType();
				if (type != null){
					if (type instanceof org.eclipse.edt.mof.egl.Part){
						if (matchSet.isMatchingType(name, (org.eclipse.edt.mof.egl.Part)type) == SearchPattern.ACCURATE_MATCH){
							binding = (org.eclipse.edt.mof.egl.Part)type;
							return false;
						}

					}
				}else if (matchSet.getMatchingLevel(name) == SearchPattern.POSSIBLE_MATCH){
					matchSet.addInaccurateMatch(name);
				}

			    return true;
			}
		});
		return false;
	}
	
	//IsAExpression is handled in the SpecialVisitor prh
	public boolean visit (IsAExpression isa ){
		
		isa.getExpression().accept(this);
		return false;
	}
	
	//AsExpression is handled in the SpecialVisitor
	public boolean visit (AsExpression as ){
		
		as.getExpression().accept(this);
		return false;
	}
	
	//NewExpression is handled in the SpecialVisitor
	public boolean visit (NewExpression newExpr ){
		Type root = newExpr.getType().getBaseType();
		if (root.isNameType()) {
			List list = ((NameType)root).getArguments();
			Iterator iter = list.iterator();
			while (iter.hasNext()){
				Node node = (Node)iter.next();
				node.accept(this);
			}
		}

		return false;
	}	

	
	public boolean visitName(Name name) {
		org.eclipse.edt.mof.egl.Type type = name.resolveType();
//		if (b != null && b.isDataBinding() && ((IDataBinding)b).getKind() == IDataBinding.CLASS_FIELD_BINDING){
//			this.matchName(name,b);
//		}
		if (type instanceof AnnotationType) {
			matchName(name, type);
		}
		return false;
	}
	
	public boolean visit(QualifiedName name) {
		int level = this.matchSet.getMatchingLevel(name);
		if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
			Element element = name.resolveType();
			if (element == null) {
				element = name.resolveMember();
			}
			
			Name matchNode = name;
			Name loopname = name;
			while(loopname.isQualifiedName()){
				loopname = ((QualifiedName)loopname).getQualifier();
				level = matchSet.getMatchingLevel(loopname);
				if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
					matchNode = loopname;
					element = loopname.resolveType();
					//If we find a record binding, then throw it out. A record name that is used as a qualifier is not
					//a valid reference in EGL
					if (element instanceof org.eclipse.edt.mof.egl.Record || element instanceof StructuredRecord) {
						element = null;
					}
					
					
				}else break;
				
			}
			
			//don't match if classfield
			if (element == null) {
				return false;
			}
			
			matchName(matchNode, element);
			
//			if (b != null && b != IBinding.NOT_FOUND_BINDING){
//				ITypeBinding typeBinding = b.isTypeBinding()? (ITypeBinding)b : null;
//				if (b.isDataBinding()){
//					typeBinding = ((IDataBinding)b).getType();
//				}
//				
//				if (typeBinding != null && typeBinding != IBinding.NOT_FOUND_BINDING && typeBinding.isPartBinding()){
//					level = matchSet.isMatchingType(matchNode, (PartBinding)typeBinding); 
//					if (level == SearchPattern.ACCURATE_MATCH){
//						matchSet.addTrustedMatch(matchNode);
//					}else if (level == SearchPattern.INACCURATE_MATCH){
//						matchSet.addInaccurateMatch(matchNode);
//					}
//
//				}
//			}
			
		}

		return false;
	}
	
	private boolean isMethodName(Expression dataAccess) {
		return false;
		//TODO search
//		IProductionNode parent = (IProductionNode) ((INode) dataAccess).getParent();
//		return parent.getNonTerminalType() == EGLNodeTypes.functionInvocation;
	}
	
	/**
	 * @param dataAccess
	 * @return
	 */
	private boolean isFunctionArgument(Expression dataAccess) {
		boolean result = false;
		//TODO search
//		IProductionNode parent = (IProductionNode)((INode)dataAccess).getParent();
//		
//		while(parent.getNonTerminalType( )!= EGLNodeTypes.Stmt && parent.getNonTerminalType() != EGLNodeTypes.functionArgument)
//		{
//			parent = (IProductionNode)parent.getParent();
//		}
//		
//		if(parent.getNonTerminalType() == EGLNodeTypes.functionArgument){
//			result = true;
//		}
		
		return result;
	}

	/**
	 *  Iterate over this data access and find the longest non-simple data access from left to right that contains
	 *  an array or substring
	 */
	private Expression getDataAccessHelper(Expression dataAccess){
		return null;
		//TODO search
//		if(dataAccess.isSimpleAccess()){
//			return null;
//		}
//		
//		IEGLDataAccess result = dataAccess;
//		while(result != null && !dataAccess.isSimpleAccess()) {
//			if(dataAccess.isArrayAccess()) {
//				result = ((IEGLArrayDataAccess) dataAccess).getArray();
//				if(result.isSimpleAccess()) {
//					result = null;
//				}
//				dataAccess = result;
//			}
//			else if(dataAccess.isSubstringAccess()) {
//				result = ((IEGLSubstringDataAccess) dataAccess).getDataAccess();
//				if(result.isSimpleAccess()) {
//					result = null;
//				}
//				dataAccess = result;
//			}
//			else {
//				dataAccess = ((IEGLFieldDataAccess) dataAccess).getContainer();
//			}
//		}
//		
//		return result;
	}
	
	private Statement getStatement(Expression dataAccess){
		return null;
		//TODO search
//		IProductionNode parent = (IProductionNode)((INode)dataAccess).getParent();
//		
//		while(parent != null && parent.getNonTerminalType()!=EGLNodeTypes.Stmt)
//		{
//			parent = (IProductionNode)parent.getParent();
//		}
//		
//		return parent == null ? null : (IEGLStatement)parent;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.internal.pgm.model.IEGLModelVisitor#visit(com.ibm.etools.egl.internal.pgm.model.IEGLStatement)
	 */
	public boolean visit(CallStatement callStatement) {
		Expression program = callStatement.getInvocationTarget();
		if(program.isName()) {
			matchName((Name) program);
		}
//		int level = this.matchSet.getMatchingLevel(program);
//		if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
//			this.matchSet.isMatchingType(program, partBinding)()
//				this.matchSet.addPossibleMatch(program);
//
//		}

		return false;
	}
			
	protected void matchName(Name name){
		Element e = name.resolveType();
		if (e == null) {
			e = name.resolveMember();
		}
		matchName(name, e);
	}
	protected void matchName(Name name, Element element){
		int level = this.matchSet.getMatchingLevel(name);
		if (level == SearchPattern.POSSIBLE_MATCH || level == SearchPattern.ACCURATE_MATCH){
			if (element != null){
				if (element instanceof FunctionMember) {
					level = matchSet.isMatchingFunctionType(name,null);
					if (level == SearchPattern.ACCURATE_MATCH){
						matchSet.addTrustedMatch(name);
					}else if (level == SearchPattern.INACCURATE_MATCH){
						matchSet.addInaccurateMatch(name);
					}
					return;
				}else if (element instanceof org.eclipse.edt.mof.egl.Part){
					level = matchSet.isMatchingType(name, (org.eclipse.edt.mof.egl.Part)element); 
					if (level == SearchPattern.ACCURATE_MATCH){
						matchSet.addTrustedMatch(name);
					}else if (level == SearchPattern.INACCURATE_MATCH){
						matchSet.addInaccurateMatch(name);
					}
					return; 
				}else return;
				
			}
			
			this.matchSet.addInaccurateMatch(name);
		}
	}
}
