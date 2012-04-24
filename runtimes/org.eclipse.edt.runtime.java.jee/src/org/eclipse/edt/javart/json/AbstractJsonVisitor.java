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

public abstract class AbstractJsonVisitor implements JsonVisitor {

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

	public void endVisit(StringNode string) {
	}

	public boolean visit(ArrayNode array) {
		return true;
	}

	public boolean visit(NameValuePairNode pair) {
		return true;
	}

	public boolean visit(BooleanNode bool) {
		return true;
	}

	public boolean visit(DecimalNode dec) {
		return true;
	}

	public boolean visit(FloatingPointNode fp) {
		return true;
	}

	public boolean visit(IntegerNode i) {
		return true;
	}

	public boolean visit(NullNode n) {
		return true;
	}

	public boolean visit(ObjectNode object) {
		return true;
	}

	public boolean visit(StringNode string) {
		return true;
	}

}
