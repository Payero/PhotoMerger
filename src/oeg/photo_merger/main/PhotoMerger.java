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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.logging.log4j.Logger;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mp4.Mp4Directory;

import oeg.photo.runner.PhotoMergerArgs;
import oeg.photo_merger.utils.ExifTool;
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
  /**
   * What is the percentage difference on the file size if avoiding duplicates
   */
  public static double FILESIZE_PERCENT_TOLERANCE = 0.3;

  /* The object used to print messages to the screen */
  private static Logger logger = null;
  /**
   * Stores all the runtime parameters used by the PhotoMerger object
   */
  private PhotoMergerArgs args = null;
  /**
   * Stores a list of file names that could not be processed
   */
  private List<String> failedExtr = new ArrayList<>();
  /**
   * Stores a list of file names that could not be processed
   */
  private List<String> dupFiles = new ArrayList<>();
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
   * Parses the date from a PhotoItem to create directories yyyy-MM
   */
  private SimpleDateFormat folderFormatter = 
                      new SimpleDateFormat("yyyy-MM");
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
  public PhotoMerger(PhotoMergerArgs args)
  {
    if( args == null )
      throw new IllegalArgumentException("The arguments cannot be null");
    logger = 
        PhotoMergerUtils.getLogger("PhotoMerger", args.getVerbosityLevel() );
    this.args = args;
    PhotoMerger.FILESIZE_PERCENT_TOLERANCE = this.args.getTolerance();
  }
  
  /**
   * Gets the object containing the runtime configuration 
   * 
   * @return the object containing the runtime configuration
   */
  public PhotoMergerArgs getPhotoMergerArguments()
  {
    return this.args;
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
  public void processRequest()
  {
    try
    {
      logger.debug("Processing Request");
      String inputDir = this.args.getInputDir();
      String mergeDir = this.args.getMergeDir();
      String prefix = this.args.getPrefix();
      int startIndex = this.args.getStartIndex();
          
      ArrayList<PhotoItem> inItems = this.getPhotoItems(inputDir);
      // the merge directory is not set, so renaming files only
      if( mergeDir == null && prefix != null )
      {
        String msg = "Renaming files using prefix " + prefix + 
                     " and Index: "  + startIndex;
        logger.info(msg);
        this.renameItems(inItems);
      }
      else if( mergeDir != null )
      {
        String msg = "Merging folders " + inputDir + " and " + mergeDir;
        logger.info(msg);;
        ArrayList<PhotoItem> mergeItems = this.getPhotoItems(mergeDir);
        this.mergeItems(inItems, mergeItems);
      }
      
      //Create and set up the window.
      String title = this.failedExtr.size() + 
                      " Metadata extraction failed, used regular date in:";
      if( !this.args.isUseLastModDate() )
        title = "Metadata extraction failed, " + this.failedExtr.size() + " files skipped";
      
      if( this.args.isShowPopupWindows() )
        this.createAndShowSkippedFiles(title, this.failedExtr);
      
      if( this.args.isRemoveDuplicates() && this.args.isShowPopupWindows())
      {
        title = this.dupFiles.size() +  " Duplicate Files Avoided";
        this.createAndShowSkippedFiles(title, this.dupFiles);
      }
      else
      {
        logger.info("Storing duplicates information in output folder");
        
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
    if( this.args.isRemoveDuplicates() )
      inItems = this.avoidDuplicates(inItems);
    
    int startIndex = this.args.getStartIndex();
    String prefix = this.args.getPrefix();
    String outputDir = this.args.getOutputDir();
    boolean makeMonthlyDirs = this.args.isMakeMonthlyDirs();
    
    for(PhotoItem item: inItems)
    {
      int nextIndex = startIndex++;
      String name = outputDir + prefix + "_" + 
                    nextIndex + "." + item.getExtension();
      
      if( makeMonthlyDirs )
      {
        String path = outputDir + File.separator;
        Date taken = item.getDateTaken();
        
        if ( taken != null ) 
         path += this.folderFormatter.format(taken);
        else
          path += "failed";
        
        Files.createDirectories(Paths.get(path));
        name = path + File.separator + prefix + "_" + 
            nextIndex + "." + item.getExtension();
        
      }
      
      logger.info("Renaming file " + item.getFilename() + " to " + name);
      File src = new File(item.getFilename());
      File tgt = new File(name);
      
      
      try
      {
        Files.copy(src.toPath(), tgt.toPath());
      }
      catch(FileAlreadyExistsException e)
      {
        logger.warn("File " + item.getFilename() + " already exists");
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
    logger.info("Removing duplicates from list with " + inItems.size() + " items" );
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
        logger.info(txt);
        this.dupFiles.add( txt );
      }
    }
    
    logger.info("Returning " + listToRet.size() + " items");
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
        String name = file.getName();
        
        logger.info("Processing file: " + fname);
        int end = fname.lastIndexOf('.');
        if ( end > 0 )
        {
          String ext = 
              fname.substring(end + 1, fname.length() ).toUpperCase();
          logger.debug("Looking for extension: " + ext);
          try
          {
            
            logger.debug("Adding file to list");
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
              if( date != null )
              {
                items.add(new PhotoItem(fname, date, ext, size));
              }
              // if we were able to get the date or we are not skipping failed files
              else if( this.args.isKeepFailed() )
              {
                logger.info("Could not get the date, but keeping the file: " + name );
                items.add(new PhotoItem(fname, date, ext, size));
              }
              else
                logger.warn("Skipping file " + name + " due to lack of date and time");
            }
          }
          catch (Exception e)
          {
            logger.warn("Error while processing: " + fname + ", ERROR: " + e.getMessage());
            date = this.getDateTimeOriginal(file);
            if( date != null )
            {
              items.add(new PhotoItem(fname, date, ext, size));
            }
            // if we were able to get the date or we are not skipping failed files
            else if( this.args.isKeepFailed() )
            {
              logger.info("Could not get the date, but keeping the file: " + name );
              items.add(new PhotoItem(fname, date, ext, size));
            }
            else
              logger.warn("Skipping file " + name + " due to lack of date and time");
          }
        }
      }
      
      Collections.sort(items);// Sorts the array list
        
    }
    
    return items;
  }
  
  /**
   * Runs through the list of media files in the input directory and attempts
   * to find all the files that might be duplicates.  Returns a list of String
   * objects with the name of the files that might be duplicates using the 
   * form:  The returned list contains a list of objects that might be 
   * duplicates:
   *      [
   *        [FILEA, FILEB],
   *        [FILEC, FILED]
   *      ]
   * In the case above FILEA and FILEB might be duplicates as well as FILEC and 
   * FILED.
   * 
   * @return a list of file names that might be duplicate as stated above
   */
  public List<List<String>> findDuplicates()
  {
    List<PhotoItem> items = this.getPhotoItems(this.args.getInputDir() );
    
    logger.info("Finding duplicates from list with " + items.size() + " items" );
    List<PhotoItem> listOfItems = new ArrayList<>();
    List<List<String>> dups = new ArrayList<>();
    
    for (PhotoItem item : items)
    {
      if( !listOfItems.contains(item) )
        listOfItems.add(item);
      else
      {
        int indx = listOfItems.indexOf(item);
        PhotoItem in = listOfItems.get(indx);
        List<String> match = new ArrayList<>();
        match.add( in.getFilename() );
        match.add( item.getFilename() );
        String txt = "Files " + in.getFilename() + " and " + 
                     item.getFilename() + " might be duplicates";
        logger.info(txt);
        dups.add( match );
      }
    }
    
    logger.info("Returning " + dups.size() + " items");
    // let's sort the list before returning it
    Collections.sort(dups, new SortByFirstElement() );
    return dups;
  }

  /**
   * Prints all the directories and tags contained as metadata in each media
   * file
   * 
   * @param metadata the information from the media file to print
   */
  public void printMetadata(Metadata metadata )
  {
    logger.debug("Printing Metadata");
    for (Directory directory : metadata.getDirectories()) 
    {
      for (Tag tag : directory.getTags()) 
      {
        logger.debug(String.format("\t[%s] - [%s] = %s", 
            directory.getName(), tag.getTagName(), tag.getDescription() ) );
      }
      if (directory.hasErrors()) 
      {
        for (String error : directory.getErrors()) 
        {
          logger.error("ERROR: " + error);
        }
      }
    }
  }
  
  /**
   * Last desperate attempt to get the creation date.  This is used when the 
   * metadata-extractor fails to get the date.  It first uses the ExifTool to
   * attempt to get the DATE_TIME_ORIGINAL or the date when the media was taken.
   * If it cannot be done and the use last modified flag is set, then it uses
   * that field from the file itself otherwise it returns null.
   * 
   * @param file the file to get the creation date
   * @return the date when this file was created or null otherwise
   * 
   */
  private Date getDateTimeOriginal( File file )
  {
    Date date = null;
    try
    {
      logger.info("Using ExifTool to get date for " + file.getName() );
      Map<ExifTool.Tag, String> map = 
        this.exifTool.getImageMeta(file, ExifTool.Tag.DATE_TIME_ORIGINAL);
      
      String str = map.get(ExifTool.Tag.DATE_TIME_ORIGINAL);
      if( str != null && str.endsWith("DST") )
      {
        str = str.substring(0,  str.length() - 4);
        date = this.exifFormatter.parse(str);
        logger.info("Date Extracted: " + date );
      }
      
    }
    catch( Exception e )
    {
      logger.error("Got an error " + e.getMessage());
    }
    
    if (date == null && this.args.isUseLastModDate() )
    {
      date = new Date(file.lastModified());
      logger.info("Using Last Modified Date: " + date);
    }
    
    if( date == null  && !this.args.isKeepFailed() )
    {
      String fname = file.getAbsolutePath();
      logger.info("Skipping file " + fname);
      this.failedExtr.add(fname);
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
  public Date getDate( String dateStr )
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
   * Sorts two lists of strings based only on the first element.
   * 
   * @author Oscar E. Ganteaume
   *
   */
  class SortByFirstElement implements Comparator<List<String>>
  {
    /**
     * Compares two lists and returns:
     *   1: if the first element in a is greater than the first element in b
     *   0: if the first element in a is equals to the first element in b
     *  -1: if the first element in a is less than the first element in b
     *  
     * @return an integer determining if whether or not the first element in a
     *         is the same, greater or equal to the first element in b
     */
    public int compare(List<String> a, List<String> b)
    {
      // lets first make sure we account for null lists
      if( a != null && b == null ) {
        return 1;
      }
      else if(a == null && b == null ) {
        return 0;      
      }
      else if( a == null && b != null ) {
        return -1;
      }
      
      // now let's consider empty lists
      if( !a.isEmpty() && b.isEmpty() ) {
        return 1;
      }
      else if(a.isEmpty() && b.isEmpty() ) {
        return 0;      
      }
      else if( a.isEmpty() && !b.isEmpty() ) {
        return -1;
      }
      
      return a.get(0).compareTo(b.get(0));
    }
  }
  
}
