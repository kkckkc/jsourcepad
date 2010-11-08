#!/bin/bash
java -client -Xms32m -Xrs -Xverify:none -Djava.util.logging.config.class=kkckkc.jsourcepad.BootstrapLogger -noclassgc -classpath "@UNIXCLASSPATH@" kkckkc.jsourcepad.Bootstrap $@
