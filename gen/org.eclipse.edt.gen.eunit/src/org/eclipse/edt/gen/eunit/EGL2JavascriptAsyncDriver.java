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
		EGL2JavascriptAsyncDriver genPart = new EGL2JavascriptAsyncDriver();
		genPart.startGeneration(args, compiler, eckGenerationNotifier);		
	}
	
	public String[] getTemplatePath() {
		List<String> templates = new ArrayList<String>();
		templates.add("org.eclipse.edt.gen.eunit.templates.javascriptasync.templates");
		String[] others = super.getTemplatePath();
		for (String other : others) {
			templates.add(other);
		}
		return (String[]) templates.toArray(new String[templates.size()]);
	}	
	
	@Override
	protected EUnitRunAllDriverGenerator getEckRunAllDriverGenerator(
			AbstractGeneratorCommand processor,
			IGenerationMessageRequestor req,
			IEUnitGenerationNotifier eckGenerationNotifier) {		
		return new EUnitRunAllJavascriptAsyncDriverGenerator(processor, req, javascriptDriverPartNameAppend, eckGenerationNotifier);
	}

}
