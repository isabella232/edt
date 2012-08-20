package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

public class MemberScope extends Scope {

	private Member member;
	
	public MemberScope(Scope parentScope, Member member) {
		super(parentScope);
		this.member = member;
	}

	@Override
	public List<Type> findType(String simpleName) {
		return parentScope.findType(simpleName);
	}

	@Override
	public List<Member> findMember(String simpleName) {
		List<Member> result = find(simpleName);
		if (result != null) {
			return result;
		}
		return parentScope.findMember(simpleName);
	}

	@Override
	public IPackageBinding findPackage(String simpleName) {
		return parentScope.findPackage(simpleName);
	}
	
	private List<Member> find(String simpleName) {
		Type type = member.getType();
		if (type == null) {
			return null;
		}
		Member mbr = BindingUtil.createExplicitDynamicAccessMember(type, simpleName);
		if (mbr != null) {
			List<Member> list = new ArrayList<Member>();
			list.add(mbr);
			return list;
		}
		
		type = BindingUtil.getBaseType(type);
		return BindingUtil.findMembers(type, simpleName);		
	}

	@Override
	public Type getType() {
		return member.getType();
	}
	
	public Member getMember() {
		return member;
	}
}
