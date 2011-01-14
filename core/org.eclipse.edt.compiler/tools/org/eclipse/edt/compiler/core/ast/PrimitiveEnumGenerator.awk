
BEGIN {
	i = 0;
}

/^#/ {
	next;
}

/^$/ {
	next;
}

{
	constName = toupper( $1 );
	
	print( "\tpublic static final int " constName "_PRIMITIVE = " i ";" );
	line = "\tpublic static final Primitive " constName " = new Primitive(" i ", \"" $1 "\"";
	for( j = 2; j <= NF; j++ ) {
		line = line ", " $j;
	}
	line = line ");";
	print( line );
	print( "\t" );
	
	i += 1;
	primitiveNames[ i ] = constName;
}

END {
	print( "\tprivate static Primitive[] types;" );
    print( "\t" );
    print( "\tstatic {" );
    print( "\t\tList primitiveList = new ArrayList();" );
    print( "\t\t" );
    for( j = 1; j <= i; j++ ) {
    	print( "\t\tprimitiveList.add(" primitiveNames[j] ");" );
    }
    print( "\t\t" );
    print( "\t\ttypes = (Primitive[])primitiveList.toArray(new Primitive[primitiveList.size()]);" );
    print( "\t}" );
}