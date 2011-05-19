/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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

import org.eclipse.edt.javart.JavartException;

public interface JsonVisitor {
	boolean visit(ArrayNode array) throws JavartException;
	void endVisit(ArrayNode array);
	
	boolean visit(NameValuePairNode pair) throws JavartException;
	void endVisit(NameValuePairNode pair);
	
	boolean visit(BooleanNode bool) throws JavartException;
	void endVisit(BooleanNode bool);
	
	boolean visit(DecimalNode dec) throws JavartException;
	void endVisit(DecimalNode dec);
	
	boolean visit(FloatingPointNode fp) throws JavartException;
	void endVisit(FloatingPointNode fp);
	
	boolean visit(IntegerNode i) throws JavartException;
	void endVisit(IntegerNode i);
	
	boolean visit(NullNode n) throws JavartException;
	void endVisit(NullNode n);
	
	boolean visit(ObjectNode object) throws JavartException;
	void endVisit(ObjectNode object);
	
	boolean visit(StringNode string) throws JavartException;
	void endVisit(StringNode string);
}
