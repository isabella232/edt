/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.services;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.utils.NameUtile;

public class Utils {
	
	private static final String IHttpMofKey = MofConversion.EGL_KeyScheme + "eglx.http.IHttp";
	
	public static boolean isIHTTP(Type type) {
		return TypeUtils.isTypeOrSubtypeOf(type, IHttpMofKey);
	}
	public static Node getRequestFormat(NestedFunction nestedFunction) {
		return getAnnotationValueNode(IEGLConstants.PROPERTY_REQUESTFORMAT,nestedFunction);
	}

	public static Node getResponseFormat(NestedFunction nestedFunction) {
		return getAnnotationValueNode(IEGLConstants.PROPERTY_RESPONSEFORMAT, nestedFunction);
	}

	public static Node getUriTemplateNode(NestedFunction nestedFunction) {
		return getAnnotationValueNode(IEGLConstants.PROPERTY_URITEMPLATE, nestedFunction);
	}

	private static Node getAnnotationValueNode(String annName, NestedFunction nestedFunction) {
		final Node[] result = new Node[1];
		final String name = NameUtile.getAsName(annName);
		nestedFunction.accept(new AbstractASTVisitor() {
			public boolean visit(
					org.eclipse.edt.compiler.core.ast.Assignment assignment) {
				if (assignment.getLeftHandSide() instanceof SimpleName
						&& ((SimpleName)assignment.getLeftHandSide()).getIdentifier().equals(name)) {
					result[0] = assignment.getRightHandSide();
				}
				return false;
			}
		});

		return result[0];

	}

}
