ZipDiff compares two .zip (.jar, .war, .jar) files and creates a list of differences.
Plain text, .xml, .html and even a .zip file are supported as output formats.

ZipDiff can be executed as command line tool or Ant task.


Command line arguments
----------------------

java -jar zipdiff.jar --source foo.zip --target bar.zip [options]

Valid options are:

--comparecrcvalues     compares the crc values instead of the file content
--comparetimestamps    compares timestamps instead of file content
--ignorecvsfiles       ignores differences in CVS folders
--output <name>        name of the output file
--skipoutputlevels <n> number of path segment to skip in the output file
--skipsourcelevels <n> number of path segment to skip in the source file
--skiptargetlevels <n> number of path segment to skip in the target file
--errorondifference    use "error" return code (2) if differences have been detected rather than 1
--verbose              print detailed messages



This version can be found at https://github.com/nhnb/zipdiff


The original zipdiff project was developed by Sean C. Sullivan and James Stewart at http://zipdiff.sourceforge.net/

License:  see LICENSE.txt
