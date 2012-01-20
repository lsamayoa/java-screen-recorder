java-screen-recorder
--------------------
java-screen-recorder provides platform independent screen capture and 
playback, producing high quality screen recordings using an internal lossless 
compression algorithm. The recordings can be played back with the included 
screen player application, or optionally converted to the QT movie format, 
using the included screen converter application.

The output video can be post-processed to other formats, using a video 
conversion tool such as ffmpeg.

The screen recorder, player and converter tools are all self contained jar 
files, requiring only the Java Runtime Environment for execution. These 
tools operate independently of any other external software codecs and 
libraries, to provide true platform independent video capture.

License
-------
java-screen-recorder is licensed under the MIT License.

Source code is available from the Subversion repository at the project's
homepage:
    http://code.google.com/p/java-screen-recorder/

Setup
-----
Each of the tools within the suite can be compiled by running the ant 
build.xml script located in the directory of each tool.  The Java Developers 
Kit must be installed prior to compilation.

The projects are preconfigured for eclipse.  To import into the eclipse 
workspace, select File->Import and choose to import an existing project.

Screen Recorder
---------------
To run screen recorder, execute the following command:
   java -jar screen_recorder.jar

The screen cursor can optionally be changed to white, using the option:
   java -jar screen_recorder.jar -white_cursor

Screen Player
-------------
To run screen player, execute the following command:
   java -jar screen_player.jar

Screen Converter
----------------
Screen converter is a tool to be convert screen recordings to the QuickTime 
movie format, which can then be post-processed to other formats using a video
conversion tool such as ffmpeg.

To run the screen converter, execute the following command:
   java -jar screen_converter.jar <screen_cap_file.cap>

