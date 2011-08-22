package org.eclipse.edt.javart.services;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.resources.ExecutableBase;

import egl.lang.AnyException;



public abstract class ServiceBase extends ExecutableBase {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public ServiceBase(RunUnit runUnit) throws AnyException {
		super(runUnit);
	}
//	public ServiceParameter[] getServiceParameters(String functionName){
//		throw ServiceUtilities.buildServiceInvocationException(_runUnit(), Message.SOA_E_FUNCTION_NOT_FOUND, new String[]{functionName, this.getClass().getName()}, null, ServiceKind.EGL);
//	}
}
