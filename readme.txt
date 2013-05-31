ZipDiff compares two .zip (.jar, .war, .ear, .rar) files and creates a list of differences.
Plain text, .xml, .html and even a .zip file are supported as output formats.

ZipDiff can be executed as command line tool or Ant task.


Command line arguments
----------------------

java -jar zipdiff.jar --source foo.zip --target bar.zip [options]

Valid options are:

* --comparecrcvalues        compares the CRC values in addition to file size
* --comparetimestamps       compares timestamps in addition to file size
* --excluderegex *\<regex>* excludes file names matching regex from comparison
* --excludecvsfiles         excludes CVS folders from comparison
* --output *\<name>*        name of the output file
* --trimoutputlevels *\<n>* number of path segments to trim in the output file
* --trimsourcelevels *\<n>* number of path segments to trim in the source file
* --trimtargetlevels *\<n>* number of path segments to trim in the target file
* --errorondifference       use "error" return code (2) if differences have been detected rather than 1
* --verbose                 print detailed messages



This version can be found at https://github.com/nhnb/zipdiff


The original zipdiff project was developed by Sean C. Sullivan and James Stewart at http://zipdiff.sourceforge.net/

License:  see LICENSE.txt
