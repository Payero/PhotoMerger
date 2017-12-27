# PhotoMerger
Java tool used to merge media files (images and videos) into an output directory
in chronogical order.

## Abstract
The need for such a tool came after a big event in my family, a newborn.  All
members started taking pictures and videos and passing them around.  So having 
a large number of resources such as smartphones, cameras, video cameras etc. 
was making it very hard to have all the pictures in the right order.  It all 
started with the [metadata-extractor](https://github.com/drewnoakes/metadata-extractor) tool.
It allowed me to get the properties of almost all of the media files except for 
MTS files created by the Sony HandCam.  To get the creation date of this file I
had to incorporate an additional tool called [ExifTool](https://github.com/rkalla/exiftool)

## Features
The tool is a straightup Java based tool that can be used from the command line
or through a GUI.  It uses up to two directories to merge them into an output 
directory.  It has the following features:

* If both input directories are provided, it merges them in ascending 
  chronological order based on when the media file was taken
* If only one input directory is provided then it re-arranges the contents of 
  it in ascending chronological order in the output directory
* Checks for duplicates and it does not copy them into the output directory.
  A media files is considered a duplicate if it was taken at the same time as
  one of the current files in the list and has the exact same size
* If the properties of the media file cannot be retrieved and the flag to use
  the file's last modified date is set, then it uses that for the ordering
* Displays a window with all the files that could not be merged due to problems 
  such as the properties could not be retrieved
* Displays a window with the name of the potential duplicate files for review 
  if desired

## Build
The project uses old school ant to build and create the jar file.  The jar file
is an executable file and it will contain all the required libraries to run.

To build the project do the following
```bash ant -f ${PROJ_LOCATION}/build.xml ```
or 
```cd ${PROJ_LOCATION} && ant ```

## Usage

To run the GUI you have the following options

* Double click the ${PROJ_LOCATION}/lib/photo-merger.jar
* Run the ${PROJ_LOCATION}/bin/photomerger script
* In a terminal run java -jar ${PROJ_LOCATION}/lib/photo-merger.jar

## Configuration and Dialog Boxes
The following widgets are found in the GUI once is launched:

* **Input Directory:** (required) the directory with the media files to organize
  as a single form of files source or to merge with a second one.
* **Merge Directory:** An optional second directory with another set of files to
  merge.  If not provided then it just organizes the files in the "Input Directory"
* **Output Directory:** (required) Where to store the files organized in a 
  chronological order.
* **Prefix:** The prefix to start the names of each one of the organized files. 
  Files will be named <PREFIX>_<INDEX>.EXT where EXT is the same extension as the
  original file.
* **Start Index:** The number to use as the starting point when creating new file
  names
* **Verbosity:** The level of verbosity or debug statements to show on the screen
* **Use Last Modified Date:** Whether or not to use the last modified date as 
  the last resort if it cannot be retrieved by the tools
* **Avoid Duplicates:** Does not copy a file taken at the same time as other 
  files already in the list taken at the same time and having the same number
  of bytes (highly recommended).
  
