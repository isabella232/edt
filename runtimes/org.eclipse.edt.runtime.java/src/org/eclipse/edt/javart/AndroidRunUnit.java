package org.eclipse.edt.javart;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.StartupInfo;

import eglx.lang.AnyException;
import eglx.lang.InvocationException;

public class AndroidRunUnit extends JEERunUnit {

	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public AndroidRunUnit(StartupInfo startInfo) throws AnyException {
		super(startInfo);
	}

	@Override
	public Object jndiLookup(String name) throws Exception {
		InvocationException ix = new InvocationException();
		throw ix.fillInMessage(Message.RUN_COMMAND_FAILED);
	}
}
