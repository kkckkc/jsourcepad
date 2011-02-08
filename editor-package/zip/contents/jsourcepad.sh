#!/bin/bash

_path_of_script="$(cd "${0%/*}" 2>/dev/null; echo "$PWD"/"${0##*/}")"
_pwd=`dirname "$_path_of_script"`

java -client -Xms32m -Xmx256m -Xrs -Xverify:none -Djava.util.logging.config.class=kkckkc.jsourcepad.BootstrapLogger -noclassgc -classpath "@UNIXCLASSPATH@" kkckkc.jsourcepad.Bootstrap $@
