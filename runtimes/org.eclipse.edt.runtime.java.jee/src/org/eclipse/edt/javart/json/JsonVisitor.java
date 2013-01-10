/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.json;

import eglx.lang.AnyException;

public interface JsonVisitor {
	boolean visit(ArrayNode array) throws AnyException;
	void endVisit(ArrayNode array);
	
	boolean visit(NameValuePairNode pair) throws AnyException;
	void endVisit(NameValuePairNode pair);
	
	boolean visit(BooleanNode bool) throws AnyException;
	void endVisit(BooleanNode bool);
	
	boolean visit(DecimalNode dec) throws AnyException;
	void endVisit(DecimalNode dec);
	
	boolean visit(FloatingPointNode fp) throws AnyException;
	void endVisit(FloatingPointNode fp);
	
	boolean visit(IntegerNode i) throws AnyException;
	void endVisit(IntegerNode i);
	
	boolean visit(NullNode n) throws AnyException;
	void endVisit(NullNode n);
	
	boolean visit(ObjectNode object) throws AnyException;
	void endVisit(ObjectNode object);
	
	boolean visit(StringNode string) throws AnyException;
	void endVisit(StringNode string);
}
