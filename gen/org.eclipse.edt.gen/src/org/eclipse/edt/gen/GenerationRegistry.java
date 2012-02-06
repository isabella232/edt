package org.eclipse.edt.gen;

public interface GenerationRegistry {
	public void registerCommandOptions(CommandOption[] options);
	public void registerTemplatePath(String[] path);
	public void registerNativeTypePath(String[] path);
	public void registerPrimitiveTypePath(String[] path);
	public void registerMessagePath(String[] path);
}
