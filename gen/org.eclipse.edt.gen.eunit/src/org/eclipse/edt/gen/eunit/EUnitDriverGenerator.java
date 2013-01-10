/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.eunit;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.eunit.templates.EUnitTemplate;
import org.eclipse.edt.mof.egl.Part;

public class EUnitDriverGenerator extends EUnitGenerator {
	
	public EUnitDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor req, String driverPartNameAppend, IEUnitGenerationNotifier eckGenerationNotifier){
		super(processor, req, eckGenerationNotifier);
		fDriverPartNameAppend = driverPartNameAppend;
	}	
	
	@Override
	protected void ContextInvoke(Part part, TestCounter counter){
		context.invoke(EUnitTemplate.genLibDriver, part, context, out, fDriverPartNameAppend, counter);
	}
	
	public String getRelativeFileName(Part part){		
		String fileName = part.getTypeSignature();
		fileName += fDriverPartNameAppend;
		fileName = CommonUtilities.prependECKGen(fileName);		
		return fileName.replaceAll("\\.", "/") + this.getFileExtension();
	}	
}
