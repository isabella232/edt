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
