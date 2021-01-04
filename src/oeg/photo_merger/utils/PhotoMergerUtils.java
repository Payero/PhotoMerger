package oeg.photo_merger.utils;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class containing a collection of static methods required to perform
 * some common operations.
 * 
 * @author Oscar E. Ganteaume
 *
 */
public class PhotoMergerUtils
{
  /**
   * Stores the prefix used to generate file names sequentially based on when 
   * the image was taken
   */
  public static final String PREFIX = "IMG";
  /**
   * Stores the index number used to generate file names sequentially based on 
   * when the image was taken
   */
  public static int START_INDEX = 1;
  /**
   * Stores the verbosity level to determine the amount of messages sent to the
   * handlers
   */
  public static Level LEVEL = Level.INFO;
  /**
   * Stores the object responsible for sending messages to the handlers
   */
  private static Logger logger = null;
  
  /**
   * Stores all the valid image extensions.  
   */
  public static final ArrayList<String> EXTS = new ArrayList<String>()
  {/**
     * 
     */
    private static final long serialVersionUID = 5617990179466185360L;

  {
    add("EXIF");
    add("IPTC"); 
    add("XMP");
    add("JFIF"); 
    add("JFXX"); 
    add("ICC"); 
    add("PNG");
    add("BMP");
    add("GIF"); 
    add("JPEG");
    add("JPG"); 
    add("RAW");
    add("TIFF"); 
    add("PSD"); 
    add("PNG"); 
  }};

//  /**
//   * Generates a new Logger object using INFO as the default verbosity level and
//   * a ConsoleHandler as the default handler
//   * 
//   * @return a logger object responsible for depicting messages to the handlers
//   */
//  public static Logger getLogger()
//  {
//    return PhotoMergerUtils.getLogger(PhotoMergerUtils.LEVEL, 
//                                      PhotoMergerUtils.HANDLER);
//  }
//  
//  /**
//   * Generates a new Logger object using the given Level verbosity level and
//   * a ConsoleHandler as the default handler
//   * 
//   * @param level the verbosity level to set the screen
//   * 
//   * @return a logger object responsible for depicting messages to the handlers
//   */
//  public static Logger getLogger(Level level)
//  {
//    return PhotoMergerUtils.getLogger(level, PhotoMergerUtils.HANDLER);
//  }
  
  /**
   * Generates a new Logger object using the given Level verbosity level and
   * the given handler handler
   * 
   * @return a logger object responsible for depicting messages to the handlers
   */
  public static Logger getLogger()
  {
    return PhotoMergerUtils.getLogger("PhotoMerger");
  }

  /**
   * Generates a new Logger object using the given Level verbosity level and
   * the given handler handler
   * 
   * @return a logger object responsible for depicting messages to the handlers
   */
  public static Logger getLogger( String name )
  {
    return PhotoMergerUtils.getLogger(name, PhotoMergerUtils.LEVEL);
  }

  
  
  /**
   * Generates a new Logger object using the given Level verbosity level and
   * a ConsoleHandler as the default handler.  If the current logger is not 
   * null ift first removes all the handlers from it and then appends it.
   * 
   * @param level the verbosity level to set the screen
   * 
   * @return a logger object responsible for depicting messages to the handlers
   */
  public static Logger getLogger(String name , Level level)
  {
    System.err.println("Getting a logger called " + name);
    
    if( logger == null )
      logger = LogManager.getLogger(name);
    
    PhotoMergerUtils.LEVEL = level;

     logger.atLevel(level);
    
    return logger;
  }
  
  /**
   * Gets the path of the directory chosen by the user.  It uses a JFileChooser
   * to get the path.  It returns true if the user selects a directory and 
   * clicks on OK otherwise it returns false.
   * 
   * @param title The title of the screen to display when accessing the 
   *        directory
   * @param path The initial path where to start looking the directory to select
   * 
   * @return The path of the selected directory or null if Cancel
   */
  public static String getDirectoryName(String title, String path)
  {
    JFileChooser chooser = new JFileChooser();
    chooser = new JFileChooser(); 
    if( path == null )
      chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
    else
      chooser.setCurrentDirectory(new java.io.File(path));
    
    chooser.setDialogTitle(title);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    //
    // disable the "All files" option.
    //
    chooser.setAcceptAllFileFilterUsed(false);
    int rv = chooser.showOpenDialog(null);
    if (rv == JFileChooser.APPROVE_OPTION) 
    {
      File dir = chooser.getSelectedFile();
      return dir.getAbsolutePath();
    }
    
    return null;
  }
  
  /**
   * Creates a customized JFileChooser without file specific components.
   * 
   * @param path The initial location to start looking for directories
   * @return a Directory only file chooser without unwanted components
   */
  public static JFileChooser getCustomFileChooser(String path)
  {
    JFileChooser fc = new JFileChooser(path);
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fc.setControlButtonsAreShown(false);
    removeUnwantedComponents(fc);
    
    return fc;
  }
  
  /**
   * Removes all the components that are not specific to the selection of a 
   * directory.
   * 
   * @param con The container with the unwanted components
   */
  private static void removeUnwantedComponents(Container con)
  {
    boolean found_folder_name = false;

    Component[] components = con.getComponents();
    // iterates through all the components in the given container
    for (Component component : components)
    {
      // removes the bottom FileChooser
      if (component.getClass().getName().contains("FileChooser")
          && found_folder_name)
      {
        con.remove(component);
      }
      // removes the File of Type component
      if (component instanceof JComboBox)
      {
        @SuppressWarnings("rawtypes")
        Object sel = ((JComboBox) component).getSelectedItem();
        
        if (sel.toString().contains("AcceptAllFileFilter"))
        {
          con.remove(component);
        }
      }
      
      // removes all the labels for the Files of Type as well as Folder name
      if (component instanceof JLabel)
      {
        String text = ((JLabel) component).getText();
        if (text.equals("Files of Type:"))
        {
          con.remove(component);
        }

        if (text.equals("Folder name:"))
        {
          con.remove(component);
          found_folder_name = true;
        }
        else
          found_folder_name = false;

      }

      // removing the New Folder component
      if (component instanceof JButton)
      {
        JButton btn = (JButton) component;
        String text = btn.getToolTipText();
        if (text != null && text.contains("Create New Folder"))
        {
          con.remove(component);
        }
      }

      if (component instanceof Container)
      {
        removeUnwantedComponents((Container) component);
      }
    } // end of the for loop
  } // end of the method


  /**
   * Stores all the options that can be used by this application
   */
  private static Options options = new Options();
  
  public static Map<String, String> parseCommandLineArgs(String[] args)
  {
    Map<String, String> map = new HashMap<>();
    map = new HashMap<>();
    String home = System.getProperty("user.home");
    //boolean useLastModDate, boolean remDups, boolean mkDirs
    map.put("input-dir", home); 
    map.put("output-dir", home); 
    map.put("merge-dir", null);
    map.put("start-index", Integer.toString(1) ); 
    map.put("prefix", PhotoMergerUtils.PREFIX);
    map.put("use-last-mod-date", Boolean.toString(false));
    map.put("remove-duplicates", Boolean.toString(true));
    map.put("keep-failed", Boolean.toString(true));
    map.put("make-monthly-dirs", Boolean.toString(true));
    map.put("debug-level", "INFO");
    
    
    String configFile = "";
    
    /**
     * CLI Options
     */
    Option help = new Option("h", "help", false, "prints this message");
    
    Option in = new Option( "i", "input-dir", true, 
        "The path to the folder with the pictures");
    
    Option md = new Option("m", "merge-dir", true, 
        "The path to the directory containing the pictures to merge");
    Option out = new Option("o", "output-dir", true, 
        "The path of the directory to store merged files");
    Option config = new Option("c", "config-file", true, 
        "Optional configuration file");;
    Option indx = new Option("s", "start-index", true, 
        "The initial value to start the file numbering. Default: 1");
    Option pfx = new Option("p", "prefix", true, 
        "The prefix to use when setting the file name. Default: IMG");
    Option useModDate = new Option("d", "use-last-mod-date", false, 
        "If present, it uses the last modified date as the date if no other is found");
    Option keepDups = new Option("k", "keep-duplicates", false, 
        "If present, it keeps the duplicates otherwise it removes them");
    Option skipMkDirs = new Option("m", "skip-make-monthly-dirs", false, 
        "If present, it does not create monthly directories");
    Option skipFailed = new Option("f", "skip-failed", false, 
        "If present, it skip keeping the images without metadata");
    Option verbosity = new Option("v", "verbosity", true, 
        "Verbosity level: [OFF|FATAL|WARNING|INFO|DEBUG|TRACE]");
  
  
    PhotoMergerUtils.options.addOption(help);
    PhotoMergerUtils.options.addOption(in);
    PhotoMergerUtils.options.addOption(md);
    PhotoMergerUtils.options.addOption(out);
    PhotoMergerUtils.options.addOption(config);
    PhotoMergerUtils.options.addOption(indx);
    PhotoMergerUtils.options.addOption(pfx);
    PhotoMergerUtils.options.addOption(useModDate);
    PhotoMergerUtils.options.addOption(keepDups);
    PhotoMergerUtils.options.addOption(skipFailed);
    PhotoMergerUtils.options.addOption(skipMkDirs);
    PhotoMergerUtils.options.addOption(verbosity);
    
    /**
     * CLI Parser
     */
    CommandLineParser parser = new DefaultParser();
    try
    {
      CommandLine line = parser.parse(options, args);
      if (line.hasOption("h"))
      {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("MainPhotoMerger", options);
        System.exit(0);
      } 
      else
      {
        /**
         * if there is a config file, it will override
         * the defaults in this file using it's values.
         * the command line options will override the
         * values from the config file
         */
        if (line.hasOption("c"))
        {
          configFile = line.getOptionValue("c");
          Properties props = new Properties();
          FileInputStream fis = null;
  
          try
          {
            fis = new FileInputStream(configFile);
            props.load(fis);
            fis.close();
          } catch (IOException e)
          {
            e.printStackTrace();
          }
  
          if (props.getProperty("input-dir") != null)
            map.put("input-dir", props.getProperty("input-dir") );
  
          if (props.getProperty("merge-dir") != null)
            map.put("merge-dir", props.getProperty("merge-dir") );
  
          if (props.getProperty("output-dir") != null)
            map.put("output-dir", props.getProperty("output-dir") );
          
          if (props.getProperty("prefix") != null)
            map.put("prefix", props.getProperty("prefix") );
          
          if (props.getProperty("start-index") != null)
            map.put("start-index", props.getProperty("start-index") );
          
          if (props.getProperty("use-last-mod-date") != null)
            map.put("use-last-mod-date", props.getProperty("use-last-mod-date") );

          // need the opposite of what we are getting
          if (props.getProperty("keep-duplicates") != null)
          {
            String val = props.getProperty("keep-duplicates");
            boolean rem = !Boolean.parseBoolean(val);
            map.put("remove-duplicates",  Boolean.toString(rem) );
          }
          
          // need the opposite of what we are getting
          if (props.getProperty("skip-make-monthly-dirs") != null)
          {
            String val = props.getProperty("skip-make-monthly-dirs");
            boolean mk = !Boolean.parseBoolean(val);
            map.put("make-monthly-dirs",  Boolean.toString(mk) );
          }

          // need the opposite of what we are getting
          if (props.getProperty("skip-failed") != null)
          {
            String val = props.getProperty("skip-failed");
            boolean failed = !Boolean.parseBoolean(val);
            map.put("keep-failed",  Boolean.toString(failed) );
          }
          
          if (props.getProperty("verbosity") != null)
          {
            String tmp =  props.getProperty("verbosity");
            switch(tmp)
            {
              case "OFF":
              case "FATAL":
              case "ERROR":
              case "WARNING":
              case "INFO":
              case "DEBUG":
              case "TRACE":
                map.put("debug-level", tmp);
                break;
              default:
                System.err.println("Invalid verbosity option: " + tmp);
                System.exit(-2);
            }
          }
        }
  
        if (line.hasOption("i"))
          map.put("input-dir", line.getOptionValue("i") );
  
        if (line.hasOption("m"))
          map.put("merge-dir", line.getOptionValue("m") );
  
        if (line.hasOption("o"))
          map.put("output-dir", line.getOptionValue("o") );
        
        if (line.hasOption("s"))
          map.put("start-index", line.getOptionValue("s") );
        
        if (line.hasOption("p"))
          map.put("prefix", line.getOptionValue("p") );
        
        if (line.hasOption("d") )
          map.put("use-last-mod-date", Boolean.toString(true) );

        // if --keep-duplicates is found then set remove-duplicates to false
        if (line.hasOption("k") )
          map.put("remove-duplicates", Boolean.toString(false) );
        
        // if --skip-make-monthly-dirs is found then set make-monthly-dirs to 
        // false
        if (line.hasOption("m") )
          map.put("make-monthly-dirs", Boolean.toString(false) );

        if (line.hasOption("f") )
          map.put("keep-failed", Boolean.toString(false) );

        if (line.hasOption("v"))
        {
          String tmp = line.getOptionValue("v");
          switch(tmp)
          {
            case "OFF":
            case "FATAL":  
            case "ERROR":
            case "WARNING":
            case "INFO":
            case "DEBUG":
            case "TRACE":
              map.put("debug-level", tmp);
              break;
            default:
              System.err.println("Invalid verbosity option: " + tmp);
              System.exit(-2);
          }
        }
      }
    } 
    catch (ParseException exp)
    {
      // oops, something went wrong
      System.err.println("Parsing failed. Reason: " + exp.getMessage());
    }
    
    return map;
  }
  
  /**
   * Stops a thread for the number of millisecond given as a parameter.  It 
   * ignores the InterruptedException, but throws a RuntimeException if the 
   * number is negative.
   * 
   * @param millisecs the number of milliseconds to wait
   * 
   * @throws RuntimeException a RuntimeException is thrown if the number of 
   *         milliseconds to wait is less than zero
   */
  public static void pause(long millisecs)
  {
    try
    {
      if( millisecs < 0 )
        throw new RuntimeException("The time needs to be positive");
      
      Thread.sleep(millisecs);
    }
    catch(InterruptedException e)
    {
      
    }
  }
}
