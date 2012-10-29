package org.eclipse.edt.ide.core;

import org.eclipse.edt.compiler.ASTValidator;
import org.eclipse.edt.compiler.BaseCompilerExtension;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;

public abstract class IDEBaseCompilerExtension implements IIDECompilerExtension {
	
	/**
	 * The id.
	 */
	protected String id;
	
	/**
	 * The (display) name.
	 */
	protected String name;
	
	/**
	 * The compiler extension version.
	 */
	protected String version;
	
	/**
	 * Another extension this one requires
	 */
	protected String requires;
	
	/**
	 * The compiler this extends.
	 */
	protected String compilerId;
	
	protected org.eclipse.edt.compiler.ICompilerExtension baseExtension;
	
	public IDEBaseCompilerExtension() {
		this.baseExtension = new BaseCompilerExtension();
	}
	
	@Override
	public Class[] getExtendedTypes() {
		return baseExtension.getExtendedTypes();
	}
	
	@Override
	public ElementGenerator getElementGeneratorFor(Node node) {
		return baseExtension.getElementGeneratorFor(node);
	}
	
	@Override
	public ASTValidator getValidatorFor(Node node) {
		return baseExtension.getValidatorFor(node);
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setName(String name) {
		if (name != null && name.length() == 0) { // treat blank like null
			this.name = null;
		}
		else {
			this.name = name;
		}
	}
	
	@Override
	public String getName() {
		return name == null ? id : name;
	}
	
	@Override
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public String getVersion() {
		return version == null ? "" : version; //$NON-NLS-1$
	}
	
	@Override
	public void setRequires(String requires) {
		this.requires = requires;
	}
	
	@Override
	public String getRequires() {
		return requires == null ? "" : requires; //$NON-NLS-1$
	}
	
	@Override
	public void setCompilerId(String id) {
		this.compilerId = id;
	}
	
	@Override
	public String getCompilerId() {
		return compilerId == null ? "" : compilerId; //$NON-NLS-1$
	}
	
	@Override
	public ICompiler getCompiler() {
		return baseExtension.getCompiler();
	}
	
	@Override
	public void setCompiler(ICompiler compiler) {
		baseExtension.setCompiler(compiler);
	}
}
