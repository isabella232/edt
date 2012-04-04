/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
 * VariableFormField AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public abstract class FormField extends Node {

	protected SettingsBlock settingsBlockOpt;
	protected Expression initializerOpt;

	public FormField(SettingsBlock settingsBlockOpt, Expression initializerOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
		if(initializerOpt != null) {
			this.initializerOpt = initializerOpt;
			initializerOpt.setParent(this);
		}
	}

	public boolean hasSettingsBlock() {
		return settingsBlockOpt != null;
	}

	public SettingsBlock getSettingsBlock() {
		return settingsBlockOpt;
	}
	
	public boolean hasInitializer() {
		return initializerOpt != null;
	}
	
	public Expression getInitializer() {
		return initializerOpt;
	}
	
	protected abstract Object clone() throws CloneNotSupportedException;
}
