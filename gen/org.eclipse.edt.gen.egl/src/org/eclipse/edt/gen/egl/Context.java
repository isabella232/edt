package org.eclipse.edt.gen.egl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class Context  extends EglContext{

	private static final long serialVersionUID = 6429116299734843162L;
	private String generationTime;
	private LogicAndDataPart part;
	protected IEnvironment env;
	
	public Context(AbstractGeneratorCommand processor) {
		super(processor);
		Date dt = new Date();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		generationTime = df.format(dt);
	}
	public String getGenerationTime() {
		return generationTime;
	}
	@Override
	public void handleValidationError(Element ex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handleValidationError(Annotation ex) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handleValidationError(Type ex) {
		// TODO Auto-generated method stub
		
	}
	public void setResult(LogicAndDataPart part) {
		this.part = part;
	}
	public LogicAndDataPart getResult() {
		return part;
	}
	public void setEnvironment(IEnvironment env) {
		this.env = env;
	}
	public IEnvironment getEnvironment() {
		return env;
	}
}
