package resources.edt.binding;

import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;

import eglx.lang.EDictionary;
import eglx.persistence.sql.SQLDataSource;
import eglx.persistence.sql.SQLJNDIDataSource;


public class SqlFactory extends BindingFactory{

	@Override
	public Object createResource(Binding binding) {
		Object resource = null;
		if(binding instanceof SQLDatabaseBinding){
			SQLDatabaseBinding sqlBinding = (SQLDatabaseBinding)binding;
			if (sqlBinding.isUseURI()) {
				String uri = sqlBinding.getUri();
				if (uri != null && uri.startsWith("jndi://")) {
					// Create a data source that obtains connections via JNDI.
					String jndiName = uri.substring(7);
					String user = sqlBinding.getSqlID();
					String password = sqlBinding.getSqlPassword();
					if (user.length() > 0 || password.length() > 0) {
						EDictionary props = new org.eclipse.edt.runtime.java.eglx.lang.EDictionary();
						if (user.length() > 0) {
							props.put("user", user);
						}
						if (password.length() > 0) {
							props.put("password", password);
						}
						resource = new SQLJNDIDataSource(jndiName, props);
					}
					else {
						resource = new SQLJNDIDataSource(jndiName);
					}
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
