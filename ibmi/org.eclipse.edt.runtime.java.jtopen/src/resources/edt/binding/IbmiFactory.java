package resources.edt.binding;

import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.Parameter;

import eglx.jtopen.JTOpenConnection;
import eglx.jtopen.JTOpenConnections;



public class IbmiFactory extends BindingFactory{

	@Override
	public Object createResource(Binding binding) throws Exception{
		Parameter system = binding.getParameter("system");
		Parameter userID = binding.getParameter("userid");
		Parameter password = binding.getParameter("password");
		Parameter library = binding.getParameter("library");
		return new JTOpenConnection(JTOpenConnections.getAS400ConnectionPool().getConnection(system.getValue(), userID.getValue(), password.getValue()), library.getValue());
	}

}
