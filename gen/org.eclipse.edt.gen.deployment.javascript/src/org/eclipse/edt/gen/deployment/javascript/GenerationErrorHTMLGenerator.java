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
package org.eclipse.edt.gen.deployment.javascript;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.deployment.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.gen.deployment.util.WorkingCopyGenerationResult;
import org.eclipse.edt.mof.egl.Part;

public class GenerationErrorHTMLGenerator extends ErrorHTMLGenerator {
	
	public GenerationErrorHTMLGenerator(AbstractGeneratorCommand processor, WorkingCopyGenerationResult requestor, String message) {
		super(processor, requestor, message);
	}
	
	public void generate(Object part) throws GenerationException {
		this.generate((Part) part, JavaScriptTemplate.genErrorHTML);
	}
}
