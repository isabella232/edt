package eglx.jtopen.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AS400Timestamp {
	int ibmiFormat() default com.ibm.as400.access.AS400Timestamp.FORMAT_DEFAULT;
	String eglPattern();
}
