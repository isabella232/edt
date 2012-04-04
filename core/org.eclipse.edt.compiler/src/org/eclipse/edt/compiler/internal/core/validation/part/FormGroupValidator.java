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
package org.eclipse.edt.compiler.internal.core.validation.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class FormGroupValidator extends AbstractASTVisitor {

	private abstract static class Size {
		int width;
		int height;

		Size(int width, int height) {
			this.width = width;
			this.height = height;
		}

		boolean isPageSize() {
			return false;
		}

		boolean isScreenSize() {
			return false;
		}
		
		public int hashCode() {
			int result = 17;
			result = 37 * result + width;
			result = 37 * result + height;
			result = 37 * result + (isPageSize() ? 1 : 0);
			return result;
		}

		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof Size) {
				Size otherSize = (Size) obj;
				return (otherSize.width == width && otherSize.height == height && otherSize
						.isPageSize() == isPageSize());
			}
			return false;
		}
	}

	private static class PageSize extends Size {
		PageSize(int width, int height) {
			super(width, height);
		}

		boolean isPageSize() {
			return true;
		}
	}

	private static class ScreenSize extends Size {
		ScreenSize(int width, int height) {
			super(width, height);
		}

		boolean isScreenSize() {
			return true;
		}
	}
	
	private static class NodeAndName {
		Node node;
		String name;
		
		NodeAndName(Node node, String canonicalName) {
			this.node = node;
			this.name = canonicalName;
		}
	}

	protected IProblemRequestor problemRequestor;
	private FormGroupBinding formGroupBinding;
	
	private String formGroupCanonicalName;
	private Map exprNodesForSize = new HashMap();
	/*
	 * The values in the following maps are NodeAndName instances which
	 * contain:
	 * 	- either Node subclasses for form names or for use statement targets
	 *  - the canonical name of the relevant form
	 * 
	 * the intent is to use this information in calls to problemRequestor.acceptProblem()
	 */
	private Map nodesForAliases = new HashMap();
	private Map nodesForFormNames = new HashMap();
    private ICompilerOptions compilerOptions;

	public FormGroupValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(FormGroup formGroup) {
		formGroupCanonicalName = formGroup.getName().getCanonicalName();
		formGroupBinding = (FormGroupBinding) formGroup.getName().resolveBinding();
		
		EGLNameValidator.validate(formGroup.getName(), EGLNameValidator.FORMGROUP, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(formGroup);
		
		return true;
	}

	public boolean visit(NestedForm nestedForm) {
		nestedForm.accept(new FormValidator(problemRequestor, formGroupBinding, compilerOptions));
		
		gatherInfoFromForm(nestedForm.getName().resolveBinding(), new NodeAndName(nestedForm, nestedForm.getName().getCanonicalName()));
		return false;
	}

	public boolean visit(UseStatement useStatement) {
		for(Iterator iter = useStatement.getNames().iterator(); iter.hasNext();) {
			Name nextName = (Name) iter.next();
			gatherInfoFromForm(nextName.resolveBinding(), new NodeAndName(nextName, nextName.getCanonicalName()));
			
			EGLNameValidator.validate(nextName, EGLNameValidator.IDENTIFIER, problemRequestor, compilerOptions);
		}
		useStatement.accept(new FormValidator(problemRequestor, formGroupBinding, compilerOptions));
		return false;
	}

	public boolean visit(SettingsBlock settingsBlock) {		
		gatherFloatingAreaSizes(settingsBlock);		
		return false;
	}
	
	public void endVisit(FormGroup formGroup) {
		checkNoFloatingAreasWithSameSize();
		checkNoDuplicateAliases();
		checkNoAliasMatchesFormName();
	}
	
	private void gatherInfoFromForm(IBinding binding, NodeAndName formNodeAndName) {
		if(binding != IBinding.NOT_FOUND_BINDING && binding.isTypeBinding()) {
			ITypeBinding formBinding = (ITypeBinding) binding;
			if(ITypeBinding.FORM_BINDING == formBinding.getKind()) {
				IAnnotationBinding aliasABinding = formBinding.getAnnotation(new String[] {"egl", "core"}, "Alias");
				if(aliasABinding != null) {
					String alias = InternUtil.intern((String) aliasABinding.getValue());
					List nodesWithAlias = (List) nodesForAliases.get(alias);
					if(nodesWithAlias == null) {
						nodesWithAlias = new ArrayList();
						nodesForAliases.put(alias, nodesWithAlias);
					}
					nodesWithAlias.add(formNodeAndName);
				}
				
				List nodesWithName = (List) nodesForFormNames.get(formBinding.getName());
				if(nodesWithName == null) {
					nodesWithName = new ArrayList();
					nodesForFormNames.put(formBinding.getName(), nodesWithName);
				}
				nodesWithName.add(formNodeAndName);
			}
		}
	}
	
	private void gatherFloatingAreaSizes(SettingsBlock settingsBlock) {
		settingsBlock.accept(new DefaultASTVisitor() {
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			
			public boolean visit(Assignment assignment) {
				assignment.getRightHandSide().accept(new DefaultASTVisitor() {
					public boolean visit(ArrayLiteral arrayLiteral) {
						return true;
					}
					
					public boolean visit(SetValuesExpression setValuesExpression) {
						Expression expr = setValuesExpression.getExpression();
						ITypeBinding tBinding = expr.resolveTypeBinding();
						Size newSize = null;
						IAnnotationBinding floatingAreaABinding = null;
						
						if(AbstractBinder.annotationIs(tBinding, new String[] {"egl", "ui", "text"}, "ScreenFloatingArea")) {
							floatingAreaABinding = (IAnnotationBinding) expr.resolveDataBinding();
							IAnnotationBinding screenSizeABinding = getField(floatingAreaABinding, IEGLConstants.PROPERTY_SCREENSIZE);
							Object[] screenSizeValue = (Object[]) screenSizeABinding.getValue();
												
							if(screenSizeValue.length == 2) {
								newSize = new ScreenSize(((Integer) screenSizeValue[1]).intValue(), ((Integer) screenSizeValue[0]).intValue());
							}			
						}
						else if(AbstractBinder.annotationIs(tBinding, new String[] {"egl", "ui", "text"}, "PrintFloatingArea")) {
							floatingAreaABinding = (IAnnotationBinding) expr.resolveDataBinding();
							IAnnotationBinding pageSizeABinding = getField(floatingAreaABinding, IEGLConstants.PROPERTY_PAGESIZE);
							if(pageSizeABinding != null) {
								Object[] pageSizeValue = (Object[]) pageSizeABinding.getValue();
								if(pageSizeValue.length == 2) {
									newSize = new PageSize(((Integer) pageSizeValue[1]).intValue(), ((Integer) pageSizeValue[0]).intValue());
								}
							}
							else {
								problemRequestor.acceptProblem(
									expr,
									IProblemRequestor.INVALID_FORMGROUP_PRINTFLOATINGAREA_PROPERTY_VALUE,
									new String[] {IEGLConstants.PROPERTY_PRINTFLOATINGAREA, formGroupCanonicalName});						
							}
						}
						
						if(newSize != null) {
							List exprList = (List) exprNodesForSize.get(newSize);
							if(exprList == null) {
								exprList = new ArrayList();
								exprNodesForSize.put(newSize, exprList);
							}
							exprList.add(expr);
							
							checkFloatingAreaMargins(expr, floatingAreaABinding, newSize);
						}
						
						return false;
					}
				});
				return false;
			}
		});
	}
	
	private void checkFloatingAreaMargins(Node floatingAreaNode, IAnnotationBinding floatingAreaABinding, Size floatingAreaSize) {
		IAnnotationBinding topMarginABinding = getField(floatingAreaABinding, IEGLConstants.PROPERTY_TOPMARGIN);
		IAnnotationBinding bottomMarginABinding = getField(floatingAreaABinding, IEGLConstants.PROPERTY_BOTTOMMARGIN);
		IAnnotationBinding leftMarginABinding = getField(floatingAreaABinding, IEGLConstants.PROPERTY_LEFTMARGIN);
		IAnnotationBinding rightMarginABinding = getField(floatingAreaABinding, IEGLConstants.PROPERTY_RIGHTMARGIN);
		
		int topMargin; 
		int bottomMargin;
		int leftMargin;
		int rightMargin;
		try {
			topMargin = ((Integer) topMarginABinding.getValue()).intValue(); 
			bottomMargin = ((Integer) bottomMarginABinding.getValue()).intValue();
			leftMargin = ((Integer) leftMarginABinding.getValue()).intValue();
			rightMargin = ((Integer) rightMarginABinding.getValue()).intValue();
		}
		catch(ClassCastException e) {
			//TODO: issue error?
			topMargin = 0; 
			bottomMargin = 0;
			leftMargin = 0;
			rightMargin = 0;
		}
		
		if(floatingAreaSize.height > 0 && topMargin + bottomMargin >= floatingAreaSize.height) {
			problemRequestor.acceptProblem(
				floatingAreaNode,
				IProblemRequestor.INVALID_MARGINS_VERSES_HEIGHT,
				new String[] {
					IEGLConstants.PROPERTY_TOPMARGIN,
					IEGLConstants.PROPERTY_BOTTOMMARGIN,
				});
		}
		
		if(floatingAreaSize.width > 0 && leftMargin + rightMargin >= floatingAreaSize.width) {
			problemRequestor.acceptProblem(
				floatingAreaNode,
				IProblemRequestor.INVALID_MARGINS_VERSES_WIDTH,
				new String[] {
						IEGLConstants.PROPERTY_LEFTMARGIN,
						IEGLConstants.PROPERTY_RIGHTMARGIN,
				});
		}
		
	}
	
	private IAnnotationBinding getField(IAnnotationBinding aBinding, String fieldName) {
		IDataBinding fieldBinding = aBinding.findData(fieldName);
		return IBinding.NOT_FOUND_BINDING == fieldBinding ? null : (IAnnotationBinding) fieldBinding;
	}

	private void checkNoFloatingAreasWithSameSize() {
		for(Iterator iter = exprNodesForSize.keySet().iterator(); iter.hasNext();) {
			Size nextSize = (Size) iter.next();
			List exprList = (List) exprNodesForSize.get(nextSize);
			if(exprList.size() != 1) {
				for(Iterator exprIter = exprList.iterator(); exprIter.hasNext();) {
					Expression nextExpr = (Expression) exprIter.next();
					problemRequestor.acceptProblem(
						nextExpr,
						IProblemRequestor.INVALID_FORMGROUP_FLOATINGAREA_DUPLICATION,
						new String[] {formGroupCanonicalName});
				}
			}
		}
	}
	
	private void checkNoDuplicateAliases() {
		for(Iterator iter = nodesForAliases.keySet().iterator(); iter.hasNext();) {
			String nextAlias = (String) iter.next();

			List nodeAndNameList = (List) nodesForAliases.get(nextAlias);
			if(nodeAndNameList.size() != 1) {
				for(Iterator nodeAndNameIter = nodeAndNameList.iterator(); nodeAndNameIter.hasNext();) {
					NodeAndName nextNodeAndName = (NodeAndName) nodeAndNameIter.next();
					problemRequestor.acceptProblem(
						getAliasAnnotationNode(nextNodeAndName.node),
						IProblemRequestor.INVALID_FORMGROUP_ALIAS_PROPERTY_DUPLICATE,
						new String[] {
							IEGLConstants.PROPERTY_ALIAS,
							"\"" + nextAlias + "\"",
							nextNodeAndName.name,
							formGroupCanonicalName});
				}
			}
		}
	}
	
	private void checkNoAliasMatchesFormName() {
		for(Iterator iter = nodesForAliases.keySet().iterator(); iter.hasNext();) {
			String nextAlias = (String) iter.next();
			List nodeAndNameList = (List) nodesForAliases.get(nextAlias);
			
			if(nodesForFormNames.containsKey(nextAlias)) {
				for(Iterator nodeAndNameIter = nodeAndNameList.iterator(); nodeAndNameIter.hasNext();) {
					NodeAndName nextNodeAndName = (NodeAndName) nodeAndNameIter.next();
					problemRequestor.acceptProblem(
						getAliasAnnotationNode(nextNodeAndName.node),
						IProblemRequestor.INVALID_FORMGROUP_ALIAS_PROPERTY_FORM_NAME,
						new String[] {
							IEGLConstants.PROPERTY_ALIAS,
							"\"" + nextAlias + "\"",
							nextNodeAndName.name,
							formGroupCanonicalName});
				}
			}			
		}
	}
	
	private Node getAliasAnnotationNode(Node parentNode) {
		final Node[] result = new Node[] {parentNode};
		parentNode.accept(new DefaultASTVisitor() {
			public boolean visit(NestedForm nestedForm) {
				return true;
			}
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			public boolean visit(Assignment assignment) {
				Expression lhs = assignment.getLeftHandSide();
				if(AbstractBinder.annotationIs(lhs.resolveTypeBinding(), new String[] {"egl", "core"}, "Alias")) {
					result[0] = lhs;
				}
				return false;
			}
		});
		return result[0];
	}
}
