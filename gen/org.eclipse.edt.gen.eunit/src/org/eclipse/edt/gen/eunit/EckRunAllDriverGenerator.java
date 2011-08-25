package org.eclipse.edt.gen.eunit;

import java.util.List;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.mof.egl.Part;

public abstract class EckRunAllDriverGenerator extends EckGenerator {

	protected static final String RunAllTest = "RunAllTests";
	
	public EckRunAllDriverGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor msgReq, String driverPartNameAppend, IEUnitGenerationNotifier eckGenerationNotifier) {
		super(processor, msgReq, eckGenerationNotifier);
		fDriverPartNameAppend = driverPartNameAppend;
	}
	
	@Override
	public String getRelativeFileName(Part part) {
		String fileName = RunAllTest;
		fileName += fDriverPartNameAppend;
		fileName = CommonUtilities.prependECKGen(fileName);
		return fileName.replaceAll("\\.", "/") + this.getFileExtension();		
	}

	protected void genImports() {
		out.println("import " + CommonUtilities.EUNITRUNTIME_PACKAGENAME + ".TestExecutionLib;");
		out.println();
	}

	protected void genPackageDeclaration() {
		out.println("package " + CommonUtilities.EUNITGEN_ROOT + ";");
		out.println();
	}
	
	public abstract void generateRunAllDriver(List<String> listOfGenedLibs, TestCounter totalCnts);
}