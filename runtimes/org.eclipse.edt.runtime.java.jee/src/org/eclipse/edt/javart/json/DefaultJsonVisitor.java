/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

public abstract class DefaultJsonVisitor implements JsonVisitor {

	public void endVisit(ArrayNode array) {
	}

	public void endVisit(NameValuePairNode pair) {
	}

	public void endVisit(BooleanNode bool) {
	}

	public void endVisit(DecimalNode dec) {
	}

	public void endVisit(FloatingPointNode fp) {
	}

	public void endVisit(IntegerNode i) {
	}

	public void endVisit(NullNode n) {
	}

	public void endVisit(ObjectNode object) {
	}

	public void endVisit(StringNode string){
	}

	public boolean visit(ArrayNode array)  throws AnyException{
		return false;
	}

	public boolean visit(NameValuePairNode pair)  throws AnyException{
		return false;
	}

	public boolean visit(BooleanNode bool)  throws AnyException{
		return false;
	}

	public boolean visit(DecimalNode dec) throws AnyException {
		return false;
	}

	public boolean visit(FloatingPointNode fp) throws AnyException {
		return false;
	}

	public boolean visit(IntegerNode i) throws AnyException {
		return false;
	}

	public boolean visit(NullNode n) throws AnyException {
		return false;
	}

	public boolean visit(ObjectNode object) throws AnyException {
		return false;
	}

	public boolean visit(StringNode string) throws AnyException {
		return false;
	}

}
