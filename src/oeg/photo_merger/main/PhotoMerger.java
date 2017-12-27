package oeg.photo_merger.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.MidiDevice.Info;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mp4.Mp4Directory;

import oeg.photo_merger.utils.ExifTool;
import oeg.photo_merger.utils.ExifTool.Feature;
import oeg.photo_merger.utils.PhotoItem;
import oeg.photo_merger.utils.PhotoMergerUtils;

/**
 * Class used to merge image files taken with multiple cameras.  If both cameras
 * have the date set appropriately, this class will merge the files based on 
 * when the picture was taken.
 * 
 * If only the input directory is given, then the application just renames all
 * the image files from that directory and saves them in the output directory
 * using the prefix and start index to generate all the names
 * 
 * @author Oscar E. Ganteaume
 *
 */
public class PhotoMerger
{
  /* The name of the input directory where the image files are located */
  private String inputDir = null;
  /* The name of the directory where the image files will be stored */
  private String outputDir = null;
  /* The name of the directory where the image files are located */
  private String mergeDir = null;
  /* The prefix to be used to generate file names */
  private String prefix = null;
  /* The index number to start the sequence to generate file names */
  private int startIndex = 1;
  /* The object used to print messages to the screen */
  private static Logger logger = null;
  /**
   * Stores a list of file names that could not be processed
   */
  private List<String> failedExtr = new ArrayList<>();
  /**
   * Stores a list of file names that could not be processed
   */
  private List<String> dupFiles = new ArrayList<>();
  /**
   * Whether or not it should use the last modified date in case the actual 
   * property of when it was taken can't be found
   */
  private boolean useLastModDate = false;
  /**
   * Whether or not to remove files taken at the exact same time with the exact
   * size
   */
  private boolean avoidDups = true;
  
  /**
   * Parses the creation date used by QuickTime
   */
  private SimpleDateFormat qtFormatter = 
                          new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
  /**
   * Parses the creation date used by the ExifTool
   */
  private SimpleDateFormat exifFormatter = 
                      new SimpleDateFormat("yyyy:MM:dd HH:mm:ssXXX");
  
  /**
   * Stores the object to last attempt to parse an file.  Specially MTS files
   * which are not currently recognized by the metadata-extractor library
   */
  private ExifTool exifTool = new ExifTool();
  
  /**
   * Runs the application using all the arguments given by the user.  If all
   * the arguments are set properly it runs the command line.
   * 
   * @param inputDir The name of the directory containing all the files to 
   *        rename or merge
   * @param outDir The name of the directory to place the files after renaming 
   *        or merging them
   * @param mergeDir The name of the directory containing all the files to 
   *        merge
   * @param startIndx The index number used when generating the name of the 
   *        files
   * @param prefix The prefix used when generating the name of the files
   */
  public PhotoMerger(String inputDir, String outDir, String mergeDir, 
                     int startIndx, String prefix)
  {
    this(inputDir, outDir, mergeDir, startIndx, prefix, 
        Level.INFO, new ConsoleHandler());
  }
  
  /**
   * Runs the application using all the arguments given by the user.  If all
   * the arguments are set properly it runs the command line.
   * 
   * @param inputDir The name of the directory containing all the files to 
   *        rename or merge
   * @param outDir The name of the directory to place the files after renaming 
   *        or merging them
   * @param mergeDir The name of the directory containing all the files to 
   *        merge
   * @param startIndx The index number used when generating the name of the 
   *        files
   * @param prefix The prefix used when generating the name of the files
   * @param lvl The verbosity level or amount of messages print to the screen
   * @param handler The handler where the debug messages will be displayed
   */  
  public PhotoMerger(String inputDir, String outDir, String mergeDir, 
      int startIndx, String prefix, Level level, Handler handler)
  {
    this(inputDir, outDir, mergeDir, startIndx, prefix, level, handler, 
        false, true);
  }

  /**
   * Runs the application using all the arguments given by the user.  If all
   * the arguments are set properly it runs the command line.
   * 
   * @param inputDir The name of the directory containing all the files to 
   *        rename or merge
   * @param outDir The name of the directory to place the files after renaming 
   *        or merging them
   * @param mergeDir The name of the directory containing all the files to 
   *        merge
   * @param startIndx The index number used when generating the name of the 
   *        files
   * @param prefix The prefix used when generating the name of the files
   * @param lvl The verbosity level or amount of messages print to the screen
   * @param handler The handler where the debug messages will be displayed
   * @param useLastModDate use the last modified date for files that it could
   *        not be retrieved
   * @param avoidDup avoids copying more than one file with the same date taken
   *        and the same file size
   */  
  public PhotoMerger(String inputDir, String outDir, String mergeDir, 
      int startIndx, String prefix, Level level, Handler handler, 
      boolean useLastModDate, boolean remDups)
  {
    logger = PhotoMergerUtils.getLogger(level, handler);
    
    this.setInputDir(inputDir);
    this.setOutputDir(outDir);
    this.setMergeDir(mergeDir);
    this.setStartIndex(startIndx);
    this.setPrefix(prefix);
    this.useLastModifiedDate(useLastModDate);
    this.removeDuplicates(remDups);
    
    this.processRequest();
  }
  
  /**
   * Gets the selected input directory
   * 
   * @return the selected input directory
   */
  public String getInputDir()
  {
    return inputDir;
  }
  
  /**
   * Sets the selected input directory
   * 
   * @param inputDir the selected input directory
   */
  public void setInputDir(String inputDir)
  {
    logger.info("Setting input Directory to " + inputDir);
    if( this.testDirectory(inputDir) )
      this.inputDir = inputDir;
  }

  /**
   * Gets the selected output directory
   * 
   * @return the selected output directory
   */
  public String getOutputDir()
  {
    return outputDir;
  }

  /**
   * Sets the selected input directory
   * 
   * @param outptuDir the selected output directory
   */
  public void setOutputDir(String outputDir)
  {
    logger.fine("Setting Output to " + outputDir);
    if( outputDir == null || outputDir.length() == 0 )
    {
      logger.info("Setting output directory to " + this.inputDir);
      this.outputDir = inputDir;
    }
    else
    {
      logger.info("Setting output directory to " + outputDir);
      if( this.testDirectory(outputDir ) )
        this.outputDir = outputDir;
    }
    // appends the directory separator if not present
    if( !this.outputDir.endsWith(File.separator) )
      this.outputDir += File.separator;
    
    logger.fine("Output Directory set to " + this.outputDir);
  }

  /**
   * Gets the selected merge directory
   * 
   * @return the selected merge directory
   */
  public String getMergeDir()
  {
    return mergeDir;
  }

  /**
   * Sets the selected input directory
   * 
   * @return the selected merge directory
   */
  public void setMergeDir(String mergeDir)
  {
    logger.info("Setting merge directory to " + mergeDir);
    if( this.testDirectory(mergeDir ) )
      this.mergeDir = mergeDir;
  }

  /**
   * Gets the prefix used to generate file names
   * 
   * @return the prefix used to generate file names
   */
  public String getPrefix()
  {
    return prefix;
  }

  /**
   * Sets the prefix used to generate file names
   * 
   * @param the prefix used to generate file names
   */
  public void setPrefix(String prefix)
  {
    logger.info("Setting prefix to " + prefix);
    if( prefix != null )
    {
      this.prefix = prefix;
    }
    else
    {
      logger.info("The prefix is not set, using default IMG");
      this.prefix = "IMG_";
    }
  }
  
  /**
   * Sets whether or not to use the last modified date for those files whose 
   * metadata could not be retrieved.
   * 
   * @param useIt whether or not to use the last modified date for those files 
   *        whose metadata could not be retrieved
   */
  public void useLastModifiedDate( boolean useIt )
  {
    this.useLastModDate = useIt;
  }

  /**
   * Gets whether or not to use the last modified date for those files whose 
   * metadata could not be retrieved.
   * 
   * @return whether or not to use the last modified date for those files 
   *         whose metadata could not be retrieved
   */
  public boolean useLastModifiedDate()
  {
    return this.useLastModDate;
  }

  /**
   * Sets the requirement to whether to copy files taken at the same time and
   * that have the same size.
   * 
   * @param remDups flag indicating to copy duplicate files or not
   */
  public void removeDuplicates( boolean avoidDups )
  {
    this.avoidDups = avoidDups;
  }
  
  /**
   * Gets the flag to whether skip files taken at the same time and
   * that have the same size.
   * 
   * @return true if the program is removing duplicates or false otherwise
   */
  public boolean removeDuplicates( )
  {
    return this.avoidDups;
  }
  
  
  /**
   * Gets the start index used to generate file names
   * 
   * @return the start index used to generate file names
   */
  public int getStartIndex()
  {
    return startIndex;
  }

  /**
   * Sets the start index used to generate file names
   * 
   * @param startIndex the start index used to generate file names
   */
  public void setStartIndex(int startIndex)
  {
    if( startIndex >= 0 )
    {
      logger.info("Setting start index to " + startIndex);
      this.startIndex = startIndex;
    }
    else
    {
      logger.info("Start Index cannot be negative, using default: 1");
      this.startIndex = 1;
    }
  }
  /**
   * Process the given request based on the arguments.  If the merge directory 
   * is valid it merges all the files in the input and merge directory based on 
   * when the pictures were taken.  If the merge directory does not exist then
   * it only renames the pictures on the input directory using the prefix and
   * the start index.
   * 
   * The image files are selected based on the extension.  If the extension is
   * in the "approved" list then is added to the processing list.
   * 
   */
  private void processRequest()
  {
    try
    {
      logger.fine("Processing Request");
      ArrayList<PhotoItem> inItems = this.getPhotoItems(this.inputDir);
      // the merge directory is not set, so renaming files only
      if( this.mergeDir == null && this.prefix != null )
      {
        String msg = "Renaming files using prefix " + this.prefix + 
                     " and Index: "  + this.startIndex;
        logger.info(msg);
        this.renameItems(inItems);
      }
      else if( this.mergeDir != null )
      {
        String msg = "Merging folders " + this.inputDir + " and " + this.mergeDir;
        logger.info(msg);;
        ArrayList<PhotoItem> mergeItems = this.getPhotoItems(this.mergeDir);
        this.mergeItems(inItems, mergeItems);
      }
      
      //Create and set up the window.
      String title = this.failedExtr.size() + 
                      " Metadata extraction failed, used regular date in:";
      if( !this.useLastModDate )
        title = "Metadata extraction failed, " + this.failedExtr.size() + " files skipped";

      this.createAndShowSkippedFiles(title, this.failedExtr);
      if( this.avoidDups )
      {
        title = this.dupFiles.size() +  " Duplicate Files Avoided";
        this.createAndShowSkippedFiles(title, this.dupFiles);
      }

    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  /**
   * Merges all the image files from both lists based in when the picture was 
   * taken.  
   * 
   * @param inItems The input directory containing the first set of images
   * @param mergeItems The merge directory containing the second set of images
   * 
   * @throws IOException IOException is thrown if there is a problem accessing
   *         a file from either input sources
   */
  private void mergeItems(ArrayList<PhotoItem> inItems, 
                          ArrayList<PhotoItem> mergeItems) throws IOException
  {
    logger.info("Initial Number of Items: " + inItems.size());
    inItems.addAll(mergeItems);
    Collections.sort(inItems);
    logger.info("Final Number of Items: " + inItems.size());
    this.renameItems(inItems);
  }
  
  /**
   * Renames all the files in the list of files in the given ArrayList using 
   * the prefix and the start index.
   * 
   * @param inItems A list of files to rename.  The files are ordered based on
   *        when the image was taken
   * @throws IOException IOException is thrown if there is a problem accessing
   *         a file from either input sources
   */
  private void renameItems(List<PhotoItem> inItems) throws IOException
  {
    logger.info("Renaming " + inItems.size() + " Items");
    // If we need to remove duplicates, then do it before renaming files
    if( this.avoidDups )
      inItems = this.avoidDuplicates(inItems);
    
    for(PhotoItem item: inItems)
    {
      String name = this.outputDir + this.prefix + "_" + 
                    this.startIndex++ + "." + item.getExtension();
      logger.info("Renaming file " + item.getFilename() + " to " + name);
      File src = new File(item.getFilename());
      File tgt = new File(name);
      try
      {
        Files.copy(src.toPath(), tgt.toPath());
      }
      catch(FileAlreadyExistsException e)
      {
        logger.warning("File " + item.getFilename() + " already exists");
      }
    }  
  }
  
  /**
   * Removes all the duplicates from the incoming list.  A duplicate is a media
   * file taken at the same time and the file contains the same number of bytes
   * 
   * @param inItems the incoming list of items to compare
   * @return a list of items without duplicate files
   */
  private List<PhotoItem> avoidDuplicates( List<PhotoItem> inItems )
  {
    logger.fine("Removing duplicates from list with " + inItems.size() + " items" );
    List<PhotoItem> listToRet = new ArrayList<>();

    for (PhotoItem item : inItems)
    {
      if( !listToRet.contains(item) )
        listToRet.add(item);
      else
      {
        int indx = listToRet.indexOf(item);
        PhotoItem in = listToRet.get(indx);
        String txt = "Files " + in.getFilename() + " and " + 
                     item.getFilename() + " might be duplicates";
        this.dupFiles.add( txt );
      }
    }
    
    logger.fine("Returning " + listToRet.size() + " items");
    return listToRet;
  }
  
  /**
   * Returns a list of PhotoItem objects.  Each object contains the name of the 
   * file and the date and time where the image was taken.  The file is flagged
   * as an image file based on a list of approved extension files.
   * 
   * @param path the path to the directory where the image files are contained
   * 
   * @return a list PhotoItem list of image files based on a list of approved 
   *         extension
   */
  private ArrayList<PhotoItem> getPhotoItems(String path)
  {
    ArrayList<PhotoItem> items = new ArrayList<PhotoItem>();
    File root = new File(path);
    if( root != null && root.isDirectory() )
    {
      logger.info("The Path is valid: " + root.getAbsolutePath());
      File[] files = root.listFiles();
      logger.info("It has " + files.length + " Files" );
      for( File file : files )
      {
        Date date = null;
        long size = file.length();
        
        String fname = file.getAbsolutePath();
        logger.info("Testing file: " + fname);
        int end = fname.lastIndexOf('.');
        if ( end > 0 )
        {
          String ext = 
              fname.substring(end + 1, fname.length() ).toUpperCase();
          logger.info("Looking for extension: " + ext);
          try
          {
            
            logger.info("Adding file to list");
            File metafile = new File(fname);
            
            Metadata metadata = 
                ImageMetadataReader.readMetadata( metafile );
            //this.printMetadata(metadata);
            
            Directory directory = null;
            
            if( metadata.containsDirectoryOfType(ExifSubIFDDirectory.class ) )
            {
              directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
              date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            }
            else if( metadata.containsDirectoryOfType(QuickTimeDirectory.class ) )
            {
              directory = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);
              date = directory.getDate(QuickTimeDirectory.TAG_CREATION_TIME);
            }
            else if( metadata.containsDirectoryOfType(Mp4Directory.class ) )
            {
              directory = metadata.getFirstDirectoryOfType(Mp4Directory.class);
              date = directory.getDate(Mp4Directory.TAG_CREATION_TIME);
            }
            
            // if we were able to extract the date, then we are done
            if( date != null )
            {
              items.add(new PhotoItem(fname, date, ext, size));
            }
            else
            {
              date = this.getDateTimeOriginal(file);
              if (date != null )
              {
                items.add(new PhotoItem(fname, date, ext, size));
              }
              // could not get the date, using modified date?
              else if( this.useLastModDate )
              {
                date = new Date(file.lastModified());
                items.add(new PhotoItem(fname, date, ext, size));
              }
              else
              {
                logger.info("Skipping file " + fname);
                this.failedExtr.add(fname);
              } 
            }
          }
          catch (Exception e)
          {
            logger.warning("Error while processing: " + fname );
            
            date = this.getDateTimeOriginal(file);
            if( date != null )
            {
              items.add(new PhotoItem(fname, date, ext, size));
            }
            else if( this.useLastModDate )
            {
              items.add(new PhotoItem(fname, date, ext, size));
            }
            else
            {
              logger.info("Skipping file " + fname);
              this.failedExtr.add(fname);
              //e.printStackTrace();
            }
          }
        }
      }
      
      Collections.sort(items);// Sorts the array list
        
    }
    
    return items;
  }

  /**
   * Last desperate attempt to get the creation date.  This is used when the 
   * metadata-extractor fails to get the date.
   * 
   * @param file the file to get the creation date
   * @return the date when this file was created or null otherwise
   */
  private Date getDateTimeOriginal( File file )
  {
    Date date = null;
    try
    {
      Map<ExifTool.Tag, String> map = 
        this.exifTool.getImageMeta(file, ExifTool.Tag.DATE_TIME_ORIGINAL);
      String str = map.get(ExifTool.Tag.DATE_TIME_ORIGINAL);
      if( str.endsWith("DST") )
        str = str.substring(0,  str.length() - 4);
      date = this.exifFormatter.parse(str);
    }
    catch( Exception e )
    {
      logger.severe("Got an error " + e.getMessage());
    }
    return date;
  }
  
  /**
   * Gets a Date object formatted as Fri Sep 22 23:02:12 EDT 2017.  If the 
   * string cannot be parsed as a Date it returns null
   * 
   * @param dateStr the string containing the desired date
   * @return a date object if the string is parsed properly or null otherwise
   */
  private Date getDate( String dateStr )
  {
    Date date = null;
    try 
    {
      date = this.qtFormatter.parse(dateStr);

    } catch (ParseException e) 
    {
      // ignore formatting errors, just return null
    }
    return date;
  }
  
  /**
   * Creates a JOptionPane pop-up window with a list of all the files the 
   * application was not able to extract metadata from.  If the list of files 
   * is empty, then the method does not perform any operation and it just 
   * returns
   *   
   * @param skipped the list of files to add to the pop-up window
   */
  private void createAndShowSkippedFiles(String title, List<String> skipped) 
  {
    // if there are no failed files, just return
    if( skipped.size() == 0 )
      return;
    
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setSize(600, 800);
    //Add the ubiquitous "Hello World" label.
    JLabel label = new JLabel("Hello World");
    frame.getContentPane().add(label);

    JPanel dbgPanel = new JPanel();
    dbgPanel.setLayout(new BorderLayout(0, 0));
    frame.getContentPane().add(dbgPanel);
        
    JScrollPane scrollPane = new JScrollPane();
    dbgPanel.add(scrollPane, BorderLayout.CENTER);
    final JTextArea dbgTextArea = new JTextArea();
    
    dbgTextArea.setBackground(Color.LIGHT_GRAY);
    dbgTextArea.setEditable(false);
    dbgTextArea.setBounds(421, 5, 4, 22);
    
    scrollPane.setViewportView(dbgTextArea);
    
    dbgTextArea.setBackground(Color.LIGHT_GRAY);
    StringBuffer buff = new StringBuffer();
    for( String fname : skipped )
    {
      buff.append(fname);
      buff.append("\n");
    }
    dbgTextArea.setText(buff.toString());
    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }
  
  
  
  /**
   * Tests for the path and determines if it exists or not.  If is a valid path
   * and is a directory it returns true otherwise it returns false.
   * 
   * @param path the path of the directory to test
   * @return true if the path belongs to a valid directory
   */
  private boolean testDirectory(String path)
  {
    logger.fine("Testing Directory to " + path);
    if( path == null )
      return false;
    
    File file = new File(path);
    if( file.isDirectory() )
      return true;
    else
      return false;
  }
  
  /**
   * Runs the show, this is used for test only
   * 
   * @param args
   */
  public static void main(String[] args)
  {
    
    new PhotoMerger("/home/oeg/TestPhotos/in", 
        "/home/oeg/TestPhotos/out", 
        null, 
        1, "TST", Level.FINEST, new ConsoleHandler());
  }
}
