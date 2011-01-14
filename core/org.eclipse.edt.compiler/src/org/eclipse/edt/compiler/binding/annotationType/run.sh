for i in $i do
	sed -e 's/import org.eclipse.edt.compiler.internal.core.utils.InternUtil;/import org.eclipse.edt.compiler.internal.core.utils.InternUtil; import org.eclipse.edt.compiler.binding.IBinding;/' $i > temp.file
	mv temp.file $i
	dos2unix $i
done
