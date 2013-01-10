#*******************************************************************************
# Copyright (c) 2012, 2013 IBM Corporation and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#     IBM Corporation - initial API and implementation
#*******************************************************************************
#!/bin/bash

# INSTRUCTIONS:
# This script will combine multiple p2 repositories into a single repository.
# You will need to config some variables below before it can run successfully.

# Set this to a directory containing an Eclipse installation
eclipseDir=/tmp/eclipse-jee-indigo

# Set this to the directory containing the repositories to be merged
repoDir=/tmp/repositories/

# Set this to the directories to be processed (relative to repoDir above),
# separated by a space
repos=(repository-lin32 repository-lin64 repository-win32 repository-win64)

# Set this to the target 'merged' directory. It does not need to exist yet.
destination=${repoDir}everything

# END SECTION REQUIRING CHANGES


cd ${eclipseDir}
for next in ${repos[@]}
do
	src=${repoDir}${next}
	echo "processing ${src}"
	
	# metadata
	./eclipse -nosplash -verbose -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source file:${src} -destination file:${destination}
	
	# artifacts
	./eclipse -nosplash -verbose -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source file:${src} -destination file:${destination}
done
