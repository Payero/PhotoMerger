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
or through a GUI.  It performs the following:


