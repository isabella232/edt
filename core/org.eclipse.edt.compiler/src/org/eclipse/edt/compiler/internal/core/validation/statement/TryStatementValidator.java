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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.EGLNotInCurrentReleaseAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class TryStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	private IPartBinding enclosingPart;
	private Set caughtExceptionTypes = new HashSet();
		
	public TryStatementValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart) {
		this.problemRequestor = problemRequestor;
		this.enclosingPart = enclosingPart;		
	}
	
	public boolean visit(TryStatement tryStatement) {
		for(Iterator iter = tryStatement.getOnExceptionBlocks().iterator(); iter.hasNext();) {
			((OnExceptionBlock) iter.next()).accept(this);
		}
		return false;
	}
	
	public boolean visit(OnExceptionBlock onExceptionBlock) {
		if(enclosingPart != null) {
			IAnnotationBinding aBinding = enclosingPart.getAnnotation(new String[] {"egl", "core"}, "V60ExceptionCompatibility");
			boolean isV60ExceptionCompatibility = aBinding != null && Boolean.YES == aBinding.getValue();
			
			if(isV60ExceptionCompatibility) {
				if(onExceptionBlock.hasExceptionDeclaration()) {
					problemRequestor.acceptProblem(
						onExceptionBlock.getExceptionName(),
						IProblemRequestor.EXCEPTION_FILTER_NOT_VALID_WITH_V60EXCEPTIONCOMPATIBILITY);
				}
			}
			else {
				if(onExceptionBlock.hasExceptionDeclaration()) {
					Type exceptionType = onExceptionBlock.getExceptionType();
					ITypeBinding exceptionTypeBinding = exceptionType.resolveTypeBinding();
					
					if(exceptionTypeBinding != IBinding.NOT_FOUND_BINDING && exceptionTypeBinding != null) {
						if(caughtExceptionTypes.contains(exceptionTypeBinding)) {
							problemRequestor.acceptProblem(
								exceptionType,
								IProblemRequestor.DUPLICATE_ONEXCEPTION_EXCEPTION,
								new String[] {exceptionType.getCanonicalName()});
						}
						else {
							if(!isAnyException(exceptionTypeBinding) &&
							   exceptionTypeBinding.getAnnotation(new String[] {"eglx", "lang"}, "Exception") == null) {
								problemRequestor.acceptProblem(
									exceptionType,
									IProblemRequestor.TYPE_IN_CATCH_BLOCK_NOT_EXCEPTION,
									new String[] {exceptionType.getCanonicalName()});
							}
							else {
								caughtExceptionTypes.add(exceptionTypeBinding);
								
								if(exceptionTypeBinding.getAnnotation(EGLNotInCurrentReleaseAnnotationTypeBinding.getInstance()) != null) {
									problemRequestor.acceptProblem(
										exceptionType,
										IProblemRequestor.SYSTEM_PART_NOT_SUPPORTED,
										new String[] {
											exceptionTypeBinding.getCaseSensitiveName()	
										});									
								}
							}
						}
					}
				}
				else {
					problemRequestor.acceptProblem(
						onExceptionBlock,
						IProblemRequestor.EXCEPTION_FILTER_REQUIRED
					);
				}
			}			
		}
		return false;
	}
	
	private boolean isAnyException(ITypeBinding type) {
		
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		
		if (type.getName() == InternUtil.intern("anyException") &&
			type.getPackageName() == InternUtil.intern(new String[] {"eglx", "lang"})) {
			return true;
		}
		
		if (type.getKind() != ITypeBinding.EXTERNALTYPE_BINDING) {
			return false;
		}
		
		if (type.getName() != InternUtil.intern("anyException")) {
			// Check if it's a type that extends anyException.
			ExternalTypeBinding etBinding = (ExternalTypeBinding)type;
			List supers = ((ExternalTypeBinding)etBinding.getNonNullableInstance()).getExtendedTypes();
			if (supers.size() > 0) {
				for (Object o : supers) {
					if (o instanceof ITypeBinding && isAnyException((ITypeBinding)o)) {
						return true;
					}
				}
			}
			return false;
		}
		
		if (type.getPackageName() != InternUtil.intern(new String[] {"eglx", "lang"})) {
			return false;
		}
		
		return true;
	}

}
