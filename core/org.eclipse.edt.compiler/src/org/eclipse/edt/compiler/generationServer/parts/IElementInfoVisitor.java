/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.generationServer.parts;


public interface IElementInfoVisitor {
	boolean visit(IPartInfo info);
	void endVisit(IPartInfo info);

	boolean visit(IEmbeddedMemberInfo info);
	void endVisit(IEmbeddedMemberInfo info);

	boolean visit(IExternalMemberInfo info);
	void endVisit(IExternalMemberInfo info);

	boolean visit(IUnresolvedPartInfo info);
	void endVisit(IUnresolvedPartInfo info);
	
}
