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
package org.eclipse.edt.compiler.core.ast;

/**
 * @author svihovec
 *  
 */
public abstract class AbstractASTPartVisitor extends AbstractASTVisitor {

	public boolean visit(DataItem dataItem) {
		visitPart(dataItem);
		return false;
	}

	public boolean visit(Delegate delegate) {
		visitPart(delegate);
		return false;
	}
	
	public boolean visit(ExternalType externalType) {
		visitPart(externalType);
		return false;
	}

	public boolean visit(Interface interfaceNode) {
		visitPart(interfaceNode);
		return false;
	}

	public boolean visit(Library library) {
		visitPart(library);
		return false;
	}

	public boolean visit(Program program) {
		visitPart(program);
		return false;
	}

	public boolean visit(Record record) {
		visitPart(record);
		return false;
	}

	public boolean visit(Service service) {
		visitPart(service);
		return false;
	}
	
	public boolean visit(Handler handler) {
		visitPart(handler);
		return false;
	}

	public boolean visit(Enumeration enumeration) {
		visitPart(enumeration);
		return false;
	}
	
	public boolean visit(Class clazz) {
		visitPart(clazz);
		return false;
	}

	public abstract void visitPart(Part part);
}
