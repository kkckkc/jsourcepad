# Config settings
lib_dir = "/Users/magnus/Documents/Projects/jsourcepad/editor-package/target/package/lib"
purge_command = "/usr/bin/purge"
jvm_args = [
	"-client",
	"-Xms64m",
	"-Dtheme=kkckkc.jsourcepad.theme.osx.OsxTheme",
	"-Xdock:name=JSourcePad",
	"-Dapple.awt.graphics.UseQuartz=true",
	"-d32",
	"-XX:+UseTLAB",
	"-Xverify:none",
	"-noclassgc",
	"-Djava.util.logging.config.file=/Users/kkckkc/Documents/projects/jsourcepad/editor/src/main/resources/logging.properties",
	"-DsupportPath=/Applications/TextMate.app/Contents/SharedSupport/Support"
]
java_path = "/usr/bin/java"

# Semi static settings
bootclass = "kkckkc.jsourcepad.Bootstrap"
num_rounds = 10

basedir = lib_dir
classpath = Dir.glob("#{lib_dir}/*.jar").join(":")

def time_diff_milli(start, finish)
   (finish - start) * 1000.0
end

total_time = 0
for i in (1..num_rounds) do
    puts "Purging"
	purge_result = `#{purge_command}`
    `sleep 10`
	puts "Executing round #{i}"

	t1 = Time.now
	editor_result = `#{java_path} -classpath #{classpath} #{jvm_args.join(" ")} -DimmediateExitForBenchmark=true #{bootclass}`
	t2 = Time.now
	msecs = time_diff_milli t1, t2

    puts editor_result
	puts "------------------------------------"
	puts "#{msecs} ms"
    puts ""
	total_time = total_time + msecs
end

puts "Average: #{total_time / num_rounds}"
	
