package org.eclipse.edt.compiler;

import java.util.Stack;

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;

public class PartEnvironmentStack {
	
	
	private static ThreadLocal<Stack<IEnvironment>> currentEnvs = new ThreadLocal<Stack<IEnvironment>>() {
		protected synchronized Stack<IEnvironment> initialValue() {
            return new Stack<IEnvironment>();
        }
	};
	
	public static synchronized IEnvironment getCurrentEnv() {
		return currentEnvs.get().size() == 0 ? null : currentEnvs.get().peek();
	}
	
	public static synchronized void pushEnv(IEnvironment env) {
		currentEnvs.get().push(env);
	}
	
	public static synchronized IEnvironment popEnv() {
		if (currentEnvs.get().size() > 0) {
			return currentEnvs.get().pop();
		}
		return null;
	}


}
