package resources.edt.binding;

import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.Parameter;

import eglx.jtopen.JTOpenConnection;
import eglx.jtopen.JTOpenConnections;



public class IbmiconnectionFactory extends BindingFactory{

	@Override
	public Object createResource(Binding binding) throws Exception{
		Parameter system = binding.getParameter("system");
		Parameter userID = binding.getParameter("userId");
		Parameter password = binding.getParameter("password");
		Parameter library = binding.getParameter("library");
		return new JTOpenConnection(JTOpenConnections.getAS400ConnectionPool().getConnection((system == null ? null : system.getValue()), 
						(userID == null ? null : userID.getValue()), 
						(password == null ? null : password.getValue())), 
						(library == null ? null : library.getValue()));
	}

}
