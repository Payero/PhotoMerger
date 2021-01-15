package oeg.photo.runner;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

import oeg.photo_merger.main.PhotoMerger;
import oeg.photo_merger.utils.PhotoMergerUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;


public class PhotoMergerArgs
{
  public static Level ARGS_LEVEL = Level.DEBUG;
  
  public static final String KEY_IN_DIR = "input-dir";
  public static final String KEY_OUT_DIR = "output-dir";
  public static final String KEY_MERGE_DIR = "merge-dir";
  public static final String KEY_PREFIX = "prefix";
  public static final String KEY_START_INDEX = "start-index";
  public static final String KEY_LAST_MOD_DATE = "use-last-mod-date";
  public static final String KEY_REM_DUPS = "remove-duplicates";
  public static final String KEY_DUPS_TOL = "duplicates-tolerance";
  public static final String KEY_KEEP_FAILED = "keep-failed";
  public static final String KEY_MAKE_DIRS = "make-monthly-dirs";
  public static final String KEY_SHOW_WINDOWS = "show-popup-windows";
  public static final String KEY_VERB_LEVEL = "verbosity-level";
  
  public static final String DEF_PREFIX = "MEDIA";
  public static final int DEF_START_INDEX = 1;
  public static final Level DEF_VERB_LEVEL = Level.INFO;
  
  
  private String inputDir = null;
  private String mergeDir = null;
  private String outputDir = null;
  private String prefix = PhotoMergerArgs.DEF_PREFIX;
  private int startIndex = PhotoMergerArgs.DEF_START_INDEX;
  private Level verbosityLevel = PhotoMergerArgs.DEF_VERB_LEVEL;
  private boolean useLastModDate = false;
  private boolean removeDuplicates = true;
  private boolean keepFailed = true;
  private boolean makeMonthlyDirs = true;
  private boolean showPopupWindows = false;
  private double tolerance = PhotoMerger.FILESIZE_PERCENT_TOLERANCE;
  
  private Logger logger = null;
  
  
  /**
   * Instantiates a new object using the contents of a properties file.
   * 
   * @param propsFile the full path of the file containing all the desired
   *        configuration
   */
  public PhotoMergerArgs(String propsFile)
  {
    this(new File(propsFile) );
  }
  
  /**
   * Instantiates a new object using the contents of a properties file.
   * 
   * @param propsFile the full path of the file containing all the desired
   *        configuration
   */
  public PhotoMergerArgs(File propsFile)
  {
    this.logger = PhotoMergerUtils.getLogger("PhotoArgs", ARGS_LEVEL);
    this.logger.debug("Creating Arguments using a configuration file");
    
    Properties props = new Properties();
    FileInputStream fis = null;

    try
    {
      fis = new FileInputStream(propsFile);
      props.load(fis);
      fis.close();
    } catch (IOException e)
    {
      e.printStackTrace();
    }
    this.setInputDir(props.getProperty(KEY_IN_DIR) );
    this.setOutputDir(props.getProperty(KEY_OUT_DIR) );
    this.setMergeDir(props.getProperty(KEY_MERGE_DIR) );
    
    if (props.getProperty(KEY_PREFIX) != null)
      this.setPrefix( props.getProperty(KEY_PREFIX).trim() );

    if (props.getProperty(KEY_DUPS_TOL) != null)
      this.setTolerance( props.getProperty(KEY_DUPS_TOL).trim() );

    if (props.getProperty(KEY_START_INDEX) != null)
      this.setStartIndex( props.getProperty(KEY_START_INDEX).trim() );

    if (props.getProperty(KEY_LAST_MOD_DATE) != null)
      this.setUseLastModDate( props.getProperty(KEY_LAST_MOD_DATE).trim() );

    if (props.getProperty(KEY_REM_DUPS) != null)
      this.setRemoveDuplicates( props.getProperty(KEY_REM_DUPS).trim() );

    if (props.getProperty(KEY_MAKE_DIRS) != null)
      this.setMakeMonthlyDirs( props.getProperty(KEY_MAKE_DIRS).trim() );

    if (props.getProperty(KEY_KEEP_FAILED) != null)
      this.setKeepFailed( props.getProperty(KEY_KEEP_FAILED).trim() );

    if (props.getProperty(KEY_SHOW_WINDOWS) != null)
      this.setShowPopupWindows( props.getProperty(KEY_SHOW_WINDOWS).trim() );

    if (props.getProperty(KEY_VERB_LEVEL) != null)
      this.setVerbosityLevel( props.getProperty(KEY_VERB_LEVEL).trim() );
  }
  
  /**
   * Instantiates a new object using the contents of the map.
   * 
   * @param arguments a key/value object containing all the desired 
   *        configuration
   */
  public PhotoMergerArgs(Map<String, String> arguments)
  {
    this.logger = PhotoMergerUtils.getLogger("PhotoArgs", ARGS_LEVEL);
    this.logger.debug("Creating Arguments using a map");
    
    // first lets do all the ones that are required
    if( arguments.containsKey(PhotoMergerArgs.KEY_IN_DIR) )
      this.setInputDir(arguments.get(PhotoMergerArgs.KEY_IN_DIR));
    else
      throw new IllegalArgumentException("The input directory is required");
    
    if( arguments.containsKey(PhotoMergerArgs.KEY_OUT_DIR) )
      this.setOutputDir(arguments.get(PhotoMergerArgs.KEY_OUT_DIR));
    else
      throw new IllegalArgumentException("The output directory is required");

    // Now we can do the optional ones
    if( arguments.containsKey(PhotoMergerArgs.KEY_MERGE_DIR) )
      this.setMergeDir(arguments.get(PhotoMergerArgs.KEY_MERGE_DIR));

    if( arguments.containsKey(PhotoMergerArgs.KEY_PREFIX) )
      this.setPrefix(arguments.get(PhotoMergerArgs.KEY_PREFIX));

    if( arguments.containsKey(PhotoMergerArgs.KEY_START_INDEX) )
      this.setStartIndex(arguments.get(PhotoMergerArgs.KEY_START_INDEX));

    if( arguments.containsKey(PhotoMergerArgs.KEY_LAST_MOD_DATE) )
      this.setUseLastModDate(arguments.get(PhotoMergerArgs.KEY_LAST_MOD_DATE));
    
    if( arguments.containsKey(PhotoMergerArgs.KEY_REM_DUPS) )
      this.setRemoveDuplicates(arguments.get(PhotoMergerArgs.KEY_REM_DUPS));

    if( arguments.containsKey(PhotoMergerArgs.KEY_DUPS_TOL) )
      this.setTolerance(arguments.get(PhotoMergerArgs.KEY_DUPS_TOL));

    if( arguments.containsKey(PhotoMergerArgs.KEY_KEEP_FAILED) )
      this.setKeepFailed(arguments.get(PhotoMergerArgs.KEY_KEEP_FAILED));

    if( arguments.containsKey(PhotoMergerArgs.KEY_SHOW_WINDOWS) )
      this.setShowPopupWindows(arguments.get(PhotoMergerArgs.KEY_SHOW_WINDOWS));

    if( arguments.containsKey(PhotoMergerArgs.KEY_VERB_LEVEL) )
      this.setVerbosityLevel(arguments.get(PhotoMergerArgs.KEY_VERB_LEVEL));
    
  }
  
  
  /**
   * Instantiates a new object with the required parameters only and using all 
   * the default values for the remaining arguments
   * 
   * @param inputDir the directory with all the source files
   * @param outputDir the directory to store the ordered media files
   */
  public PhotoMergerArgs(String inputDir, String outputDir)
  {
    this(inputDir, outputDir, null);
  }

  /**
   * Instantiates a new object with the required parameters as well as the 
   * optional merge directory. All the remaining arguments will use their 
   * default values
   * 
   * @param inputDir the directory with all the source files
   * @param outputDir the directory to store the ordered media files
   * @param mergeDir an optional additional source directory to use
   */  
  public PhotoMergerArgs(String inputDir, String outputDir, String mergeDir)
  {
    this(inputDir, outputDir, mergeDir, DEF_PREFIX);
  }

  /**
   * Instantiates a new object with the required parameters as well as the 
   * optional merge directory and prefix. All the remaining arguments will use 
   * their default values
   * 
   * @param inputDir the directory with all the source files
   * @param outputDir the directory to store the ordered media files
   * @param mergeDir an optional additional source directory to use
   * @param prefix the prefix to use when renaiming the media files
   */  
  public PhotoMergerArgs(String inputDir, String outputDir, 
      String mergeDir, String prefix)
  {
    this(inputDir, outputDir, mergeDir, prefix, DEF_START_INDEX);
  }

  /**
   * Instantiates a new object with the required parameters as well as the 
   * optional merge directory and prefix. All the remaining arguments will use 
   * their default values
   * 
   * @param inputDir the directory with all the source files
   * @param outputDir the directory to store the ordered media files
   * @param mergeDir an optional additional source directory to use
   * @param prefix the prefix to use when renaiming the media files
   * @param startIndex the first number to use when renaiming the media files
   */  
  public PhotoMergerArgs(String inputDir, String outputDir, 
      String mergeDir, String prefix, int startIndex)
  {
    this.logger = PhotoMergerUtils.getLogger("PhotoArgs", ARGS_LEVEL);
    this.logger.debug("Creating Arguments using arguments");
    
    this.setInputDir(inputDir);
    this.setOutputDir(outputDir);
    this.setMergeDir(mergeDir);
    this.setPrefix(prefix);
    this.setStartIndex(startIndex);
    
  }
  
  /**
   * Gets the location where all the media files are located.  
   * 
   * @return the location where all the media files are located
   * 
   */
  public String getInputDir()
  {
    return inputDir;
  }

  /**
   * Sets the location where all the media files are located.  If the directory
   * name is null or it does not exists, the method throws an exception
   * 
   * @param inputDir the location where all the media files are located
   * 
   * @throws IllegalArgumentException an IllegalArgumentsException is throws if 
   *         the directory name is null or it does not exists 
   */
  public void setInputDir(String inputDir)
  {
    this.logger.debug("Setting the Input Directory to " + inputDir);
    
    if( inputDir == null )
      throw new IllegalArgumentException("The input directory cannot be null");
    
    File file = new File(inputDir);
    if( !file.isDirectory() )
      throw new IllegalArgumentException("The input directory (" + 
                                      file.getAbsolutePath() + ") is invalid");
    
    this.inputDir = inputDir;
  }

  /**
   * Gets the location where all the optional merge media files are located.  
   * The default value is 'null' to indicate that directories will not be 
   * merged.
   * 
   * @return the location where all the optional merge media files are located
   * 
   */
  public String getMergeDir()
  {
    return mergeDir;
  }

  /**
   * Sets the optional directory to merge the files.  If the argument is not 
   * null, but the path is invalid then it throws an exception.  The default 
   * value is null to indicate that directories will not be merged.
   * 
   * @param mergeDir the optional directory to merge the files
   * 
   * @throws IllegalArgumentException an IllegalArgumentException is thrown if 
   *         the argument is provided, but is invalid.
   */
  public void setMergeDir(String mergeDir)
  {
    this.logger.debug("Setting the merge directory to " + mergeDir);
    if( mergeDir != null )
    {
      File file = new File(mergeDir);
      if( !file.isDirectory() )
        throw new IllegalArgumentException("The merge directory (" + 
                     file.getAbsolutePath() + ") was provided but is invalid");
      this.mergeDir = mergeDir;  
    }
  }

  /**
   * Gets the location where all the media files will be stored after sorting 
   * them chronologically.  
   * 
   * @return the location where all the media files will be stored after 
   *         sorting them chronologically
   * 
   */
  public String getOutputDir()
  {
    return outputDir;
  }


  /**
   * Sets the location where all the media files will be stored.  If the 
   * directory name is null or it does not exists, the method throws an 
   * exception
   * 
   * @param outputDir the location where all the media files will be stored
   * 
   * @throws IllegalArgumentException an IllegalArgumentsException is throws if 
   *         the directory name is null or it does not exists 
   */
  public void setOutputDir(String outputDir)
  {
    this.logger.debug("Setting the output Directory to " + outputDir);
    
    if( outputDir == null )
      throw new IllegalArgumentException("The output directory cannot be null");
    
    File file = new File(outputDir);
    try
    {
      if( !file.isDirectory() )
      {
        file.mkdirs();
      }
    }
    catch( Exception e )
    {
      throw new IllegalArgumentException("The output directory (" + 
          file.getAbsolutePath() + ") is invalid");
    }
    
    this.outputDir = outputDir;
  }

  /**
   * Gets the prefix to use when renaming all the media files.  The default 
   * value used is 'MEDIA'
   * 
   * @return the prefix to use when renaming all the media files
   */
  public String getPrefix()
  {
    return prefix;
  }

  /**
   * Sets the prefix to use when renaming the files.  If the prefix is null an
   * IllegalArgumentException is thrown.
   * 
   * @param prefix the prefix to use when renaming the files
   * @throws IllegalArgumentException and IllegalArgumentException is thrown if
   *         the prefix argument is null
   */
  public void setPrefix(String prefix)
  {
    this.logger.debug("Setting prefix to " + prefix);
    
    if( prefix == null )
      throw new IllegalArgumentException("The prefix cannot be null");
    
    this.prefix = prefix;
  }

  /**
   * Gets the first number of the renaming series.  The default value is set to 
   * 1
   * @return the first number of the renaming series
   */
  public int getStartIndex()
  {
    return startIndex;
  }

  /**
   * Sets the first number of the renaming series.  The default value is set to 
   * 1
   * 
   * @param startIndex the first number of the renaming series
   */
  public void setStartIndex(int startIndex)
  {
    this.logger.debug("Setting the start index to " + startIndex);
    
    this.startIndex = startIndex;
  }

  /**
   * Sets the first number of the renaming series.  The default value is set to 
   * 1.  Throws an IllegalArgumentException if the string argument cannot be 
   * parsed into an integer.
   * 
   * @param startIndex the first number of the renaming series
   */
  public void setStartIndex(String startIndex)
  {
    try 
    {
      this.setStartIndex( Integer.parseInt(startIndex) );
    }
    catch(Exception e )
    {
      throw new IllegalArgumentException("The start index argument must be an integer");
    }
  }

  /**
   * Gets the PhotoMerger verbosity level, the default value is INFO.
   * 
   * @return the PhotoMerger verbosity level, the default value is INFO.
   */
  public Level getVerbosityLevel()
  {
    return verbosityLevel;
  }

  /**
   * Sets the PhotoMerger verbosity level, the default value is INFO.
   * 
   * @param level the PhotoMerger verbosity level, the default value is INFO.
   */
  public void setVerbosityLevel(Level level)
  {
    this.logger.debug("Setting verbosity to " + level);
    this.verbosityLevel = level;
  }

  /**
   * Sets the PhotoMerger verbosity level, the default value is INFO.
   * 
   * @param level the PhotoMerger verbosity level, the default value is INFO.
   */
  public void setVerbosityLevel(String level)
  {
    try 
    {
      this.setVerbosityLevel( Level.getLevel(level) );
    }
    catch(Exception e )
    {
      throw new IllegalArgumentException("Illegal level (" + level + ")");
    }
  }

  
  /**
   * Gets the flag indicating whether or not to use the last modification date
   * of the media file if any other method fails.
   * 
   * @return the flag indicating whether or not to use the last modification
   *         date of the media file if any other method fails
   */
  public boolean isUseLastModDate()
  {
    return useLastModDate;
  }

  /**
   * Sets the flag indicating whether or not to use the last modification date
   * of the media file if any other method fails.
   * 
   * @param useLastModDate the flag indicating whether or not to use the last 
   *        modification date of the media file if any other method fails
   */
  public void setUseLastModDate(boolean useLastModDate)
  {
    this.useLastModDate = useLastModDate;
  }

  /**
   * Sets the flag indicating whether or not to use the last modification date
   * of the media file if any other method fails.
   * 
   * @param useLastModDate the flag indicating whether or not to use the last 
   *        modification date of the media file if any other method fails
   */
  public void setUseLastModDate(String useLastModDate)
  {
    this.setUseLastModDate(Boolean.parseBoolean(useLastModDate));
  }

  /**
   * Gets the flag indicating whether or not to remove duplicate media files
   * 
   * @return the flag indicating whether or not to remove duplicate media files
   */
  public boolean isRemoveDuplicates()
  {
    return removeDuplicates;
  }

  /**
   * Sets the flag indicating whether or not to remove duplicate media files
   * 
   * @param removeDuplicates the flag indicating whether or not to remove 
   *        duplicate media files
   */
  public void setRemoveDuplicates(boolean removeDuplicates)
  {
    this.removeDuplicates = removeDuplicates;
  }

  /**
   * Sets the flag indicating whether or not to remove duplicate media files
   * 
   * @param removeDuplicates the flag indicating whether or not to remove 
   *        duplicate media files
   */
  public void setRemoveDuplicates(String removeDuplicates)
  {
    this.setRemoveDuplicates( Boolean.parseBoolean(removeDuplicates) );
  }
  
  /**
   * Gets the flag indicating whether or not to keep the media files that the
   * program failed to extract date and time information
   * 
   * @return the flag indicating whether or not to keep the media files that
   *         the program failed to extract date and time information
   */
  public boolean isKeepFailed()
  {
    return keepFailed;
  }

  /**
   * Sets the flag indicating whether or not to keep the media files that the
   * program failed to extract date and time information
   * 
   * @param keepFailed a flag indicating whether or not to keep the media files
   *        that the program failed to extract date and time information
   */
  public void setKeepFailed(boolean keepFailed)
  {
    this.keepFailed = keepFailed;
  }

  /**
   * Sets the flag indicating whether or not to keep the media files that the
   * program failed to extract date and time information
   * 
   * @param keepFailed a flag indicating whether or not to keep the media files
   *        that the program failed to extract date and time information
   */
  public void setKeepFailed(String keepFailed)
  {
    this.setKeepFailed( Boolean.parseBoolean(keepFailed) );
  }
  
  /**
   * Gets the flag indicating whether or not make the yyyy-mm directories based
   * on the date the media was taken if available
   * 
   * @return the flag indicating whether or not make the yyyy-mm directories
   *         based on the date the media was taken if available
   */
  public boolean isMakeMonthlyDirs()
  {
    return makeMonthlyDirs;
  }

  /**
   * Sets the flag indicating whether or not make the yyyy-mm directories based
   * on the date the media was taken if available
   * 
   * @param makeMonthlyDirs the flag indicating whether or not make the yyyy-mm 
   *        directories based on the date the media was taken if available
   */
  public void setMakeMonthlyDirs(boolean makeMonthlyDirs)
  {
    this.makeMonthlyDirs = makeMonthlyDirs;
  }

  /**
   * Sets the flag indicating whether or not make the yyyy-mm directories based
   * on the date the media was taken if available
   * 
   * @param makeMonthlyDirs the flag indicating whether or not make the yyyy-mm 
   *        directories based on the date the media was taken if available
   */
  public void setMakeMonthlyDirs(String makeMonthlyDirs)
  {
    this.setMakeMonthlyDirs( Boolean.parseBoolean(makeMonthlyDirs) );
  }

  /**
   * Gets the flag indicating whether or not create and start the popup windows
   * showing the duplicate and failed files.
   * 
   * @return the flag indicating whether or not create and start the popup 
   *         windows showing the duplicate and failed files.
   */
  public boolean isShowPopupWindows()
  {
    return showPopupWindows;
  }

  /**
   * Sets the flag indicating whether or not create and start the popup windows
   * showing the duplicate and failed files.
   * 
   * @param showPopupWindows the flag indicating whether or not create and 
   *        start the popup windows showing the duplicate and failed files.
   */
  public void setShowPopupWindows(boolean showPopupWindows)
  {
    this.showPopupWindows = showPopupWindows;
  }

  /**
   * Sets the flag indicating whether or not create and start the popup windows
   * showing the duplicate and failed files.
   * 
   * @param showPopupWindows the flag indicating whether or not create and 
   *        start the popup windows showing the duplicate and failed files.
   */
  public void setShowPopupWindows(String showPopupWindows)
  {
    this.setShowPopupWindows( Boolean.parseBoolean(showPopupWindows) );
  }
  

  /**
   * Gets the tolerance level to use when comparing images to determine whether
   * they might be duplicates or not.  The default value is set to 0.3% 
   * difference
   * 
   * @return the tolerance level to use when comparing images to determine
   *         whether they might be duplicates or not
   */
  public double getTolerance()
  {
    return tolerance;
  }

  /**
   * Sets the tolerance level to use when comparing images to determine whether
   * they might be duplicates or not.  The default value is set to 0.3% 
   * difference
   * 
   * @param tolerance the level to use when comparing images to determine
   *        whether they might be duplicates or not
   */
  public void setTolerance(double tolerance)
  {
    this.logger.debug("Setting tolerance to " + tolerance);
    if( tolerance < 0 || tolerance > 1 )
    {
      String msg = String.format("The tolerance has to be between 0 and 1 not %f", tolerance);
      throw new IllegalArgumentException(msg);
    }
    this.tolerance = tolerance;
  }

  /**
   * Sets the tolerance level to use when comparing images to determine whether
   * they might be duplicates or not.  The default value is set to 0.3% 
   * difference
   * 
   * @param tolerance the level to use when comparing images to determine
   *        whether they might be duplicates or not
   */
  public void setTolerance(String tolerance)
  {
    try
    {
      this.setTolerance( Double.parseDouble(tolerance) );
    }
    catch( Exception e )
    {
      throw new IllegalArgumentException("The tolerance is not a float");
    }
  }
  
  /**
   * Returns a string representation of the arguments object using the form
   * key: value.
   * 
   * @return a string representation of the arguments object using the form
   *         key: value.
   */
  public String toString()
  {
    StringBuffer buff = new StringBuffer();
    buff.append(String.format("%s: %s\n", KEY_IN_DIR, this.inputDir));
    buff.append(String.format("%s: %s\n", KEY_OUT_DIR, this.outputDir));
    buff.append(String.format("%s: %s\n", KEY_MERGE_DIR, this.mergeDir));
    buff.append(String.format("%s: %s\n", KEY_PREFIX, this.prefix));
    buff.append(String.format("%s: %s\n", KEY_START_INDEX, this.startIndex));
    buff.append(String.format("%s: %s\n", KEY_LAST_MOD_DATE, this.useLastModDate));
    buff.append(String.format("%s: %s\n", KEY_REM_DUPS, this.removeDuplicates));
    buff.append(String.format("%s: %s\n", KEY_DUPS_TOL, this.tolerance));
    buff.append(String.format("%s: %s\n", KEY_KEEP_FAILED, this.keepFailed));
    buff.append(String.format("%s: %s\n", KEY_MAKE_DIRS, this.makeMonthlyDirs));
    buff.append(String.format("%s: %s\n", KEY_SHOW_WINDOWS, this.showPopupWindows));
    buff.append(String.format("%s: %s\n", KEY_VERB_LEVEL, this.verbosityLevel));
    
    return buff.toString();
  }
  
  /**
   * Just use for quick development tests.
   * 
   * @param args
   */
  public static void main( String[] args )
  {
    String fname = "/home/oeg/workspace/eclipse-workspace/PhotoMerger/config/test-merger.properties";
    PhotoMergerArgs pa = new PhotoMergerArgs(fname);
    pa.setTolerance(-1);
    System.err.println("Args: \n" + pa.toString() );
  }
}
