#!/bin/bash
# First parameter is the path to directory which will be packed
{
	TEMP_FOLDER="Majewski_Marcin_${1: -1}"
	echo "Packing $1 to $TEMP_FOLDER"
	cp --recursive $1 $TEMP_FOLDER
	cd $TEMP_FOLDER
	find . -name '*.iml' -exec rm -r {} \;
	find . -name '*.idea' -exec rm -r {} \; 
	find . -name '*.git' -exec rm -r {} \; 
	find . -name '*target' -exec rm -r {} \; 
	cd ..
	TAR_FILE="Majewski_Marcin_${1: -1}"
	rm -rf $TEMP_FOLDER.tgz
	tar cvzf $TAR_FILE.tgz $TEMP_FOLDER
	rm -rf $TEMP_FOLDER
} &> /dev/null