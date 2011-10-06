package eglx.persistence.sql;

public class SQLWarning extends SQLException {
	private static final long serialVersionUID = 10L;
	
	public SQLWarning nextWarning;
	
	public SQLWarning() {
		super();
	}
	
	public void ezeInitialize() {
		super.ezeInitialize();
		nextWarning = null;
	}
	
	public SQLWarning getNextWarning() {
		return nextWarning;
	}
	public void setNextWarning(SQLWarning warn) {
		this.nextWarning = warn;
	}
}
