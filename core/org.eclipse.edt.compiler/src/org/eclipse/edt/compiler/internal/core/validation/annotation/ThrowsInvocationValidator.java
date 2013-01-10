/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.utils.NameUtile;

public class ThrowsInvocationValidator implements IInvocationValidationRule {
	
	private final static String JavaObjectExceptionMofKey = NameUtile.getAsName(MofConversion.EGL_KeyScheme + "eglx.java.JavaObjectException");
	
	private List<StructPart> validExceptions = new ArrayList<StructPart>();
	
	@Override
	public void validate(Node node, Element element, Part declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		
		if (node == null || declaringPart == null || element == null) {
			return;
		}
		
		org.eclipse.edt.mof.egl.Type joe = TypeUtils.getType(JavaObjectExceptionMofKey);
		if (joe instanceof StructPart) {
			buildAllValidExceptions(validExceptions, (StructPart)joe);
		}
		
		if (!isInTryCatchException(node)) {
			problemRequestor.acceptProblem(
					node, 
					IProblemRequestor.INVOCATION_MUST_BE_IN_TRY, 
					new String[] {});
		}
	}
	
	private boolean isInTryCatchException(Node node) {

		TryStatement tryStmt = getTryStatement(node);
		if (tryStmt == null) {
			return false;
		}
		if (hasCatchException(tryStmt)) {
			return true;
		}
		return isInTryCatchException(tryStmt.getParent());		
	}
	
	private boolean hasCatchException(TryStatement tryStmt) {
		List<Node> blocks = tryStmt.getOnExceptionBlocks();
		
		// try ... end, with no onException, is the same as catching AnyException and doing nothing.
		if (blocks.size() == 0) {
			return true;
		}
		
		for (Node node : blocks) {
			Type type = ((OnExceptionBlock)node).getExceptionType();
			if (type != null && validExceptions.contains(type.resolveType())) {
				return true;
			}
		}
		return false;
	}

	
	private TryStatement getTryStatement(Node node) {
		if (node == null) {
			return null;
		}
		if (node instanceof TryStatement) {
			return (TryStatement) node;
		}
		
		if (node instanceof OnExceptionBlock) {
			if (node.getParent() != null) {
				return getTryStatement(node.getParent().getParent());
			}
			return null;
		}
		
		return getTryStatement(node.getParent());
	}
	
	private void buildAllValidExceptions(List<StructPart> list, StructPart baseException) {
		if (baseException == null || list.contains(baseException)) {
			return;
		}
		list.add(baseException);
		
		for (StructPart superType : ((StructPart)baseException).getSuperTypes()) {
			buildAllValidExceptions(list, superType);
		}
	}
}
