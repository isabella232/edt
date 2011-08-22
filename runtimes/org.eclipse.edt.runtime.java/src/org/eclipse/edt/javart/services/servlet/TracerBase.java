package org.eclipse.edt.javart.services.servlet;

import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.resources.Trace;

public class TracerBase {
	private Trace tracer;
	final private RunUnit runUnit;
	
	protected TracerBase(RunUnit runUnit) {
		this.runUnit = runUnit;
	}
	protected Trace tracer()
	{
		if( tracer == null )
		{
			tracer = getRunUnit().getTrace();
		}
		return tracer;
	}
	
	protected boolean trace()
    {
		return tracer().traceIsOn( Trace.GENERAL_TRACE ); 
    }
	protected RunUnit getRunUnit() {
		return runUnit;
	}

}
