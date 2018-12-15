#!/bin/bash

SWT=swt-linux32.jar
if [ ! -z "`uname -a | grep x86_64`" ]; then
	SWT=swt-linux64.jar
fi

java -cp `dirname "$0"`/Genedit.jar:`dirname "$0"`/$SWT net.generalised.genedit.main.Main "$@"

