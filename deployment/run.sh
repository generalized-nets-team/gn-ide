#!/bin/bash

SWT=swt-linux32.jar
if [ ! -z "`uname -a | grep x86_64`" ]; then
	SWT=swt-linux64.jar
fi

java -cp Genedit.jar:$SWT net.generalised.genedit.main.Main "$@"

