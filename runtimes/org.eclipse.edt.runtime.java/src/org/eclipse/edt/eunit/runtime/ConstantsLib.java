package org.eclipse.edt.eunit.runtime;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EInt;
import java.lang.Integer;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
@javax.xml.bind.annotation.XmlRootElement(name="ConstantsLib")
public class ConstantsLib extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	private static final String ezeConst_NEWLINE = "\r\n";
	public final String NEWLINE = ezeConst_NEWLINE;
	private static final String ezeConst_EXIT_PREFIX = "STATUS: ";
	public final String EXIT_PREFIX = ezeConst_EXIT_PREFIX;
	private static final int ezeConst_SPASSED = (int)((short) 0);
	public final int SPASSED = ezeConst_SPASSED;
	private static final int ezeConst_SFAILED = (int)((short) 1);
	public final int SFAILED = ezeConst_SFAILED;
	private static final int ezeConst_SERROR = (int)((short) 2);
	public final int SERROR = ezeConst_SERROR;
	private static final int ezeConst_SNOT_RUN = (int)((short) 3);
	public final int SNOT_RUN = ezeConst_SNOT_RUN;
	private static final int ezeConst_SBAD = (int)((short) 4);
	public final int SBAD = ezeConst_SBAD;
	public ConstantsLib() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
	}
	@org.eclipse.edt.javart.json.Json(name="NEWLINE", clazz=EString.class, asOptions={})
	public String getNEWLINE() {
		return (NEWLINE);
	}
	@org.eclipse.edt.javart.json.Json(name="EXIT_PREFIX", clazz=EString.class, asOptions={})
	public String getEXIT_PREFIX() {
		return (EXIT_PREFIX);
	}
	@org.eclipse.edt.javart.json.Json(name="SPASSED", clazz=EInt.class, asOptions={})
	public int getSPASSED() {
		return (SPASSED);
	}
	@org.eclipse.edt.javart.json.Json(name="SFAILED", clazz=EInt.class, asOptions={})
	public int getSFAILED() {
		return (SFAILED);
	}
	@org.eclipse.edt.javart.json.Json(name="SERROR", clazz=EInt.class, asOptions={})
	public int getSERROR() {
		return (SERROR);
	}
	@org.eclipse.edt.javart.json.Json(name="SNOT_RUN", clazz=EInt.class, asOptions={})
	public int getSNOT_RUN() {
		return (SNOT_RUN);
	}
	@org.eclipse.edt.javart.json.Json(name="SBAD", clazz=EInt.class, asOptions={})
	public int getSBAD() {
		return (SBAD);
	}
}
