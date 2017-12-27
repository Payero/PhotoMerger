package test;


import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiDevice.Info;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import com.drew.metadata.StringValue;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.media.QuickTimeMediaDirectory;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import com.drew.metadata.mp4.Mp4Directory;

import oeg.photo_merger.utils.ExifTool;
import oeg.photo_merger.utils.ExifTool.Feature;
import oeg.photo_merger.utils.ExifTool.Tag;
import oeg.photo_merger.utils.PhotoItem;
import oeg.photo_merger.utils.PhotoMergerUtils;


public class PhotoTest
{
  private Logger logger = Logger.getLogger(PhotoTest.class.getName());
  SimpleDateFormat qtFormatter = 
      new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

  private List<String> dupFiles = new ArrayList<>();
  
  private static Logger getLogger()
  {
    Logger log = Logger.getLogger("my.logger");
    log.setLevel(Level.INFO);
    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel(Level.INFO);
    handler.setFormatter(new SimpleFormatter());
    log.addHandler(handler);
    
    return log;
  }
  
  public PhotoTest()
  {
    this.logger = PhotoTest.getLogger();
    try
    {
      this.runTest();
    }
    catch( Exception e )
    {
      this.logger.severe("Message: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void runTest() throws Exception
  {
    this.logger.fine("Running the PhotoTest");
    String path = "/home/oeg/Desktop/Sebastian/STREAM/00091.MTS";
    ExifTool tool = new ExifTool(Feature.STAY_OPEN);
    
    Map<Tag, String> valueMap = 
      tool.getImageMeta(new File(path), ExifTool.Tag.CREATION_DATE, ExifTool.Tag.DATE_TIME_ORIGINAL);
    this.logger.info("Creation Date " + valueMap.get(ExifTool.Tag.CREATION_DATE));
    this.logger.info("Date Time Original " + valueMap.get(ExifTool.Tag.DATE_TIME_ORIGINAL));
    
//    
//    try
//    {
//      
//      logger.info("Adding file to list");
//      File metafile = new File(path);
//      
//      Metadata metadata = 
//          ImageMetadataReader.readMetadata( metafile );
//      //this.printMetadata(metadata);
//      
//      Directory directory = null;
//      Date date = null;
//      if( metadata.containsDirectoryOfType(ExifSubIFDDirectory.class ) )
//      {
//        directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
//        date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
//      }
//      else if( metadata.containsDirectoryOfType(QuickTimeDirectory.class ) )
//      {
//        directory = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);
//        date = directory.getDate(QuickTimeDirectory.TAG_CREATION_TIME);
//      }
//      else if( metadata.containsDirectoryOfType(Mp4Directory.class ) )
//      {
//        directory = metadata.getFirstDirectoryOfType(Mp4Directory.class);
//        date = directory.getDate(Mp4Directory.TAG_CREATION_TIME);
//      }
//      
//    }
//    catch (Exception e)
//    {
//      System.err.println("Exception " + e.getMessage());
//      e.printStackTrace();
//      Map<Tag, String> map = 
//          tool.getImageMeta(new File(path), ExifTool.Tag.DATE_TIME_ORIGINAL);
//        this.logger.info("Date Time Original " + map.get(ExifTool.Tag.DATE_TIME_ORIGINAL));
//        
//    }
  }

  
  public static void main(String[] args)
  {
    new PhotoTest();
  }
}
