package resources.edt.binding;

import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;

import eglx.lang.EDictionary;
import eglx.persistence.sql.SQLDataSource;


public class SqlFactory extends BindingFactory{

	@Override
	public Object createResource(Binding binding) {
		Object resource = null;
		if(binding instanceof SQLDatabaseBinding){
			SQLDatabaseBinding sqlBinding = (SQLDatabaseBinding)binding;
			if (sqlBinding.isUseURI()) {
				String uri = sqlBinding.getUri();
				if (uri != null && uri.startsWith("jndi://")) {
					//TODO need to actually support JNDI. This code will not work but serves as a placeholder.
					resource = new SQLDataSource(uri.substring(7)); // "jndi://".length()
				}
			}
			else {
				EDictionary props = new org.eclipse.edt.runtime.java.eglx.lang.EDictionary();
				String user = sqlBinding.getSqlID();
				String password = sqlBinding.getSqlPassword();
				String schema = sqlBinding.getSqlSchema();
				if (user != null) {
					props.put("user", user);
				}
				if (password != null) {
					props.put("password", password);
				}
				resource = new SQLDataSource(sqlBinding.getSqlDB(), props);
				
				// Try to load the class so that it registers itself, in case it's not a Type 4 driver.
				// This must be done before any connection is made, such as by invoking setCurrentSchema below.
				String className = sqlBinding.getSqlJDBCDriverClass();
				if (className != null && className.length() > 0) {
					try {
						Class.forName(className);
					}
					catch (Throwable t) {
					}
				}
				
				if (schema != null && (schema = schema.trim()).length() > 0) {
					((SQLDataSource)resource).setCurrentSchema(schema);
				}
			}
		}
		return resource;
	}

}
