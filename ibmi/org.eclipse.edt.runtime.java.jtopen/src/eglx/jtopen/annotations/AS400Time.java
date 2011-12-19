package eglx.jtopen.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AS400Time {
	int ibmiFormat() default com.ibm.as400.access.AS400Time.FORMAT_ISO;
	String ibmiSeperator() default "";
}
