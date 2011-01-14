/PROPNOTE: value type not determined/ {
	temp = $0;
	gsub(".*determined .", "", temp);
	gsub(".$", "", temp);
	noTypeNote = temp;
}

/PROPNOTE/ {
	totalNotes += 1;
	notesInClass += 1;
	numNotes[$0] += 1;
}

/^public class/ {
	notes[className] = notesInClass;
	className = $3;
	notesInClass = 0;
	if(noTypeNote != 0) {
		noTypeNotes[className] = noTypeNote;
		noTypeNote = 0;
	}
}

END {

	print totalNotes " notes total.";
	print "";
	for(i in notes) {
		if(i != "" && notes[i] == 0) {
			print i ".java_ has no notes.";
		}
	}
	print "";
	for(i in numNotes) {
		print numNotes[i] " of " i;
	}
}
