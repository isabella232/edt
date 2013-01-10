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
package org.eclipse.edt.ide.ui.internal.record.conversion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.ui.templates.parts.Part;

public abstract class RecordConversion implements IMessageHandler {

	private boolean _isOK = false;
	private List<String> _errors = new ArrayList<String>();
	private List<String> _messages = new ArrayList<String>();
	private String _result = "";
	private Part[] _resultParts = null;

	protected void ok(boolean isOK) {
		_isOK = isOK;
	}

	public boolean isOK() {
		return _isOK;
	}

	protected void error(String error) {
		_errors.add(error);
	}

	public List<String> getErrors() {
		return _errors;
	}

	public String getResult() {
		return _result;
	}

	public Part[] getResultParts() {
		return _resultParts;
	}

	protected abstract Part[] doConvert(RecordSource input);

	public boolean convert(RecordSource input) {
		_isOK = false;
		_result = "";
		_resultParts = null;
		Part[] parts = doConvert(input);
		if (_isOK) {
			_resultParts = parts;
			_result = new PartsWrapper(parts).toString();
		}
		return _isOK;
	}

	public IMessageHandler getMessageHandler() {
		return this;
	}

	public void addMessage(String message) {
		if (!_messages.contains(message))
			_messages.add(message);
	}

	public List<String> getMessages() {
		return _messages;
	}
}
