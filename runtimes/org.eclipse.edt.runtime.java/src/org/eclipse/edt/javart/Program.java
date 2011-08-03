/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart;

import java.util.List;

import egl.lang.EglAny;
import egl.lang.AnyValue;

public interface Program extends Executable {

	void main(List<String> args) throws Exception;
	
	void _start(String...args) throws Exception;
	void _transferToProgram(String name, AnyValue input) throws Transfer;
	void _transferToTransaction(String name, AnyValue input, boolean doCommit) throws Transfer;
	void _finishTransfer();
	EglAny[] _parameters() throws JavartException;
	boolean _retainOnExit(int action) throws JavartException;
	AnyValue _inputRecord();
}
