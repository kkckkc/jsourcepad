cd %~dp0
javaw -client -Xms32m -Xmx256m -Xrs -XX:+UseTLAB -Xverify:none -noclassgc -Xshare:on -DexecutableName=jsourcepad.bat -Djava.util.logging.config.class=kkckkc.jsourcepad.BootstrapLogger -classpath "@CLASSPATH@" kkckkc.jsourcepad.Bootstrap %1
