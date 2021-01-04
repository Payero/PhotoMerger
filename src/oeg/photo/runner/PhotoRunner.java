package oeg.photo.runner;


import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import oeg.photo_merger.main.PhotoMerger;



public class PhotoRunner 
{

  private Logger logger = LogManager.getLogger(PhotoRunner.class.getName());
  SimpleDateFormat qtFormatter = 
      new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
  
  private List<File> files = new ArrayList<>();
  
  public PhotoRunner(Map<String, String> map)
  {
    
    this.logger.debug("Ok, I got this");
    
    try
    {
      map.forEach((k, v) -> {
        this.logger.info("Config[" + k + "] = " + v);
      });
      
      String inName = map.get("input-dir");
      
      if( this.populateFiles( inName ) ) 
      {
        String outName = map.get("output-dir");
        if( outName == null ) {
          outName = inName + File.separator + "mod";
          this.logger.warn("The output directory was not specified, using " + outName );
        }
        
        File out = new File(outName);
        if( !out.isDirectory() ) {
          out.mkdirs();
        }
        int start = Integer.parseInt( map.get("start-index") );
        String prefix = map.get("prefix");
        this.logger.debug("Using last mod date? " + map.get("use-last-mod-date")  );
        boolean useLastMod = Boolean.parseBoolean(map.get("use-last-mod-date") );
        this.logger.debug("Using last mod date? " + useLastMod  );
        boolean remDups = Boolean.parseBoolean(map.get("rem-duplcates") );
        boolean makeDirs = Boolean.parseBoolean(map.get("make-dirs") );
        
        
        for( File file : this.files ) 
        {
          this.logger.info("Processing Directory: " + file.getAbsolutePath() );
          String path = outName + File.separator + file.getName();
          this.logger.debug("Output Dir: " + path);
          
          new PhotoMerger(file.getAbsolutePath(), path, null, start, prefix, Level.DEBUG,
              useLastMod, remDups, makeDirs, false);
          
        }
      }
      
      
    }
    catch( Exception e )
    {
      this.logger.error("Message: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  private boolean populateFiles( String dirName ) 
  {
    File dir = new File(dirName);
    
    if( dir.isDirectory() ) 
    {
      this.logger.debug("The input is a directory");
      this.doListing(dir, PhotoRunnerUtils.getFileFilter() );
      this.logger.debug("Found " + this.files.size() + " directories to process");
      return true;
    }
    else 
    {
      this.logger.warn("The directory  "+ dir.getAbsolutePath() + " is invalid");
    }    
    return false;
  }
  
  
  
  public List<File> doListing( File dirName, FileFilter filter ) {
    
    File[] listFiles = dirName.listFiles(filter);
    for( File file : listFiles ) {
      if( file.isDirectory() ) {
        this.files.add(file);
        doListing(file, filter);
      }
    }
    
    return this.files;
  }
  
  
  public static void main(String[] args)
  {
    Map<String, String> map = PhotoRunnerUtils.parseCommandLineArgs(args);
    
    new PhotoRunner(map);
  }
}
