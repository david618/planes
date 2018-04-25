#!/bin/bash

for file in /mnt/resource/s3/rttestdata/planes/lat88/planes0000?; do
	echo ${file}
	filedirname=$(dirname "${file}")
	filebasename=$(basename "${file}")
	#echo ${filedirname}
	#echo ${filebasename}
	fileout=${filedirname}/ascii/${filebasename}
	echo ${fileout}
	iconv -f UTF8 -t ASCII//TRANSLIT ${file} -o ${fileout}
done
