/*******************************************************************************
 * Copyright © 2004, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;

public class EGLTemplateProposal extends TemplateProposal {

	private String delimiter;
	
	public EGLTemplateProposal(Template template, TemplateContext context,
			IRegion region, Image image, String adelimiter) {
		super(template, context, region, image);
		
		delimiter = adelimiter;
	}
	
	public String getAdditionalProposalInfo() {
	    try {
		    getContext().setReadOnly(true);
			TemplateBuffer templateBuffer;
			try {
				templateBuffer= getContext().evaluate(getTemplate());
			} catch (TemplateException e) {
				return null;
			}

			return templateBuffer.getString().replace(delimiter, "<br>");

	    } catch (BadLocationException e) {
			return null;
		}
	}

}
