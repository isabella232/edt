package org.eclipse.edt.gen.eunit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;

public class EGL2JavascriptAsyncDriver extends EGL2JavascriptDriver {

	public EGL2JavascriptAsyncDriver(){
		super();
	}
	
	public static void main(String[] args){
		start(args, null, new NullEUnitGenerationNotifier());
	}
	
	public static void start(String[] args, ICompiler compiler, IEUnitGenerationNotifier eckGenerationNotifier) {
		List<String> arguments = new ArrayList();
		for (int i = 0; i < args.length; i++) {
			arguments.add(args[i]);
		}
		arguments.add("-c");
		arguments.add("org.eclipse.edt.gen.eunit.EUnitJavascriptAsyncDriverGenConfig");
		arguments.add("org.eclipse.edt.gen.eunit.EUnitJavascriptDriverGenConfig");
		arguments.add("org.eclipse.edt.gen.eunit.EUnitDriverGenConfig");
		EGL2JavascriptAsyncDriver genPart = new EGL2JavascriptAsyncDriver();
		genPart.startGeneration(arguments.toArray(new String[arguments.size()]), compiler, eckGenerationNotifier);
	}
	
	@Override
	protected EUnitRunAllDriverGenerator getEckRunAllDriverGenerator(
			AbstractGeneratorCommand processor,
			IGenerationMessageRequestor req,
			IEUnitGenerationNotifier eckGenerationNotifier) {		
		return new EUnitRunAllJavascriptAsyncDriverGenerator(processor, req, javascriptDriverPartNameAppend, eckGenerationNotifier);
	}

}
