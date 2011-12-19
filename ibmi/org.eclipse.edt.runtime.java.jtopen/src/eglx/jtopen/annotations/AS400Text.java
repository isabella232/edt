package eglx.jtopen.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AS400Text {
	int length();
	String encoding() default "";
	boolean preserveTrailingSpaces();
}
