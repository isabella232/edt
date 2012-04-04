/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.templates;

import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

public class CoreContextType extends TemplateContextType {
	/**
	 * Resolver that resolves to the variable defined in the context.
	 */
	public static class CodeTemplateVariableResolver extends TemplateVariableResolver {
		public CodeTemplateVariableResolver(String type, String description) {
			super(type, description);
		}
		
		protected String resolve(TemplateContext context) {
			return context.getVariable(getType());
		}
	}

	public static final String NAME= "egl-core"; //$NON-NLS-1$

	public CoreContextType() {
		super(NAME);
		addCompilationUnitVariables();
	}
	
	public EGLTemplateContext createContext(IDocument document, int offset, int length, ParseStack parseStack, String prefix) {
		return new EGLTemplateContext(this, document, offset, length, parseStack, prefix);
	}
	private void addCompilationUnitVariables() {
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new CodeTemplateVariableResolver("name", UINlsStrings.EGLTemplateVariableNameDescription)); //$NON-NLS-1$
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());
		addResolver(new GlobalTemplateVariables.Year());
	}
}
