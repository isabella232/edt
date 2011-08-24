package org.eclipse.edt.gen.eck;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.eck.templates.EckTemplate;
import org.eclipse.edt.mof.egl.Part;

public class EckDriverGenerator extends EckGenerator {
	
	public EckDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor req, String driverPartNameAppend, IEckGenerationNotifier eckGenerationNotifier){
		super(processor, req, eckGenerationNotifier);
		fDriverPartNameAppend = driverPartNameAppend;
	}	
	
	@Override
	protected void ContextInvoke(Part part, TestCounter counter){
		context.invoke(EckTemplate.genLibDriver, part, context, out, fDriverPartNameAppend, counter);
	}
	
	public String getRelativeFileName(Part part){		
		String fileName = part.getTypeSignature();
		fileName += fDriverPartNameAppend;
		fileName = CommonUtilities.prependECKGen(fileName);		
		return fileName.replaceAll("\\.", "/") + this.getFileExtension();
	}	
}
