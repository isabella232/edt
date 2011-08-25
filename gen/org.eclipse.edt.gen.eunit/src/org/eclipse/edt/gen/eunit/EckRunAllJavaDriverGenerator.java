package org.eclipse.edt.gen.eunit;

import java.util.List;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;

public class EckRunAllJavaDriverGenerator extends EckRunAllDriverGenerator {

	public EckRunAllJavaDriverGenerator(AbstractGeneratorCommand processor,
			IGenerationMessageRequestor msgReq, String driverPartNameAppend, IEckGenerationNotifier eckGenerationNotifier) {
		super(processor, msgReq, driverPartNameAppend, eckGenerationNotifier);
	}

	public void generateRunAllDriver(List<String> listOfGenedLibs, TestCounter totalCnts){	
		genPackageDeclaration();
		
		genImports();
		
		out.println("program " + RunAllTest + fDriverPartNameAppend + " type BasicProgram {}");
		out.pushIndent();
		out.println("function main()");
		out.pushIndent();
		for(String genLibName : listOfGenedLibs){
			out.println(genLibName + "." + CommonUtilities.exeTestMethodName + "();");				
		}
		out.println("TestExecutionLib.writeResultSummary(" + totalCnts.getCount() + ");");
		out.popIndent();
		out.println("end");
		out.popIndent();
		out.println("end");		
		out.close();
	}
	
}
