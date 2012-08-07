/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.core.ast;

import java.util.List;

import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * Function AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class TopLevelFunction extends Part {

	private boolean isPrivate;
	private List functionParameters;	// List of Symbols
	private ReturnsDeclaration returnsOpt;
	
	public TopLevelFunction(java.lang.Boolean privateAccessModifierOpt, SimpleName name, List functionParameters, ReturnsDeclaration returnsOpt, List stmts, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, stmts, startOffset, endOffset);
		
		isPrivate = privateAccessModifierOpt.booleanValue();
		this.functionParameters = setParent(functionParameters);
		if(returnsOpt != null) {
			this.returnsOpt = returnsOpt;
			returnsOpt.setParent(this);
		}		
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public List getFunctionParameters() {
		return functionParameters;
	}
	
	public ReturnsDeclaration getReturnDeclaration(){
		return returnsOpt;
	}
	
	public boolean hasReturnType() {
		return returnsOpt != null;
	}
	
	public Type getReturnType() {
		return returnsOpt.getType();
	}
	
	public List getStmts() {
		return contents;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			acceptChildren(visitor, functionParameters);
			if(returnsOpt != null) returnsOpt.accept(visitor);
			acceptChildren(visitor, contents);
		}
		visitor.endVisit(this);
	}
	
	public boolean isContainerContextDependent(){
		final boolean[] result = new boolean[]{false};
		accept(new DefaultASTVisitor(){
			public boolean visit(TopLevelFunction topLevelFunction) {
				return true;
			}
			public boolean visit(SettingsBlock settingsBlock) {
				settingsBlock.accept(new DefaultASTVisitor(){
					public boolean visit(SettingsBlock settingsBlock) {
						return true;
					}
					public boolean visit(Assignment assignment) {
						Expression lhs = assignment.getLeftHandSide();
						if(lhs.isName()){
							Name lhsName = (Name)lhs;
							if(lhsName.isSimpleName()){
								if(lhsName.getIdentifier() == InternUtil.intern(IEGLConstants.PROPERTY_CONTAINERCONTEXTDEPENDENT)){
									Expression rhs = assignment.getRightHandSide();
									rhs.accept(new DefaultASTVisitor() {
										public boolean visit(BooleanLiteral booleanLiteral) {
											result[0] = Boolean.YES == booleanLiteral.booleanValue();
											return false;
										}
									});
								}
							}
						}
						return false;
					}
				});
				return false;
			}
		});
	
		return result[0];
	}
	
	protected Object clone() throws CloneNotSupportedException {
		ReturnsDeclaration newReturnsOpt = returnsOpt != null ? (ReturnsDeclaration)returnsOpt.clone() : null;
		
		return new TopLevelFunction(new java.lang.Boolean(isPrivate), (SimpleName)name.clone(), cloneList(functionParameters), newReturnsOpt, cloneContents(), getOffset(), getOffset() + getLength());
	}

	public String getPartTypeName() {
		return IEGLConstants.KEYWORD_FUNCTION;
	}
	
	public int getPartType() {
		return FUNCTION;
	}
	
	public boolean hasSubType() {
		return false;
	}

	public Name getSubType() {
		return null;
	}
}
