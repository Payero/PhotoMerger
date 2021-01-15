package oeg.photo.runner;


import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import oeg.photo_merger.main.PhotoMerger;



public class PhotoRunner 
{

  private Logger logger = LogManager.getLogger(PhotoRunner.class.getName());
  SimpleDateFormat qtFormatter = 
      new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
  
  private List<File> files = new ArrayList<>();
  
  public PhotoRunner(PhotoMergerArgs args)
  {
    
    this.logger.debug("Ok, I got this");
    
    try
    {
      this.logger.info("Config " + args.toString() );
      String inName = args.getInputDir();
      
      if( this.populateFiles( inName ) ) 
      {
        String outName = args.getOutputDir();
        if( outName == null ) {
          outName = inName + File.separator + "mod";
          this.logger.warn("The output directory was not specified, using " + outName );
        }
        
        File out = new File(outName);
        if( !out.isDirectory() ) {
          out.mkdirs();
        }
        this.logger.debug("Using last mod date? " + args.isUseLastModDate()  );
        boolean useLastMod = args.isUseLastModDate();
        this.logger.debug("Using last mod date? " + useLastMod  );
        
        
        for( File file : this.files ) 
        {
          this.logger.info("Processing Directory: " + file.getAbsolutePath() );
          String path = outName + File.separator + file.getName();
          this.logger.debug("Output Dir: " + path);
          PhotoMerger pm = new PhotoMerger(args);
          pm.processRequest();
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
    PhotoMergerArgs photoArgs = PhotoRunnerUtils.parseCommandLineArgs(args);
    
    new PhotoRunner(photoArgs);
  }
}
