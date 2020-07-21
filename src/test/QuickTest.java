package test;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import oeg.photo.runner.PhotoRunner;
import oeg.photo.runner.PhotoRunnerUtils;

public class QuickTest
{

  /**
   * Generates debug print statements based on the verbosity level.
   */
  private Logger logger = LogManager.getLogger(PhotoRunner.class.getName());
  private List<File> files = new ArrayList<>();
  
  public QuickTest()
  {
    this.logger.debug("Running Quick Test");
    File file = new File("/home/oeg/workspace/eclipse-workspace/PhotoMerger/data/test/2000");
    List<File> files = this.doListing(file, PhotoRunnerUtils.getFileFilter() );
    List<File> trimmed_files = new ArrayList<>();
    
    for( File tst : files) {
      this.logger.debug("File " + tst.getName() );
      boolean has_files = false;
      for( File inner : tst.listFiles() ) {
        if( inner.isFile() ) {
          has_files = true;
          break;
        }
      }
      if( has_files ) {
        trimmed_files.add(tst);
      }
    }
    this.logger.debug("Found " + trimmed_files.size() + " directories");
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
    new QuickTest();
  }
}
