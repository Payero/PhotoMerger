package oeg.photo_merger.gui;

import java.awt.BorderLayout;


import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.Configurator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import oeg.photo.runner.PhotoMergerArgs;
import oeg.photo_merger.main.PhotoMerger;
import oeg.photo_merger.utils.PhotoMergerUtils;
import oeg.photo_merger.utils.TextAreaAppender;

import java.awt.Color;

/**
 * Graphical interface to the PhotoMerger application.  It was built using
 * Eclipse's IDE Window Builder.  It uses a combination of default values and
 * required parameters.  If one of the required parameters is missing and/or 
 * miss-configured the GUI pops up an error message to the screen.  If all the
 * parameters are configured properly the command is passed to the PhotoMerger.
 * 
 * The pictures are merged based on the date and time where the picture was 
 * taken.  
 *   
 * @author Oscar E. Ganteaume
 *
 */
public class PhotoGUI extends JFrame
{
  /* Required Unique ID for each Class */
  private static final long serialVersionUID = 8856845378051984123L;
  /* Object used to print debug messages to the screen */
  private static Logger logger = PhotoMergerUtils.getLogger();
  
  /* Main graphical component where all other components reside */
  private JPanel contentPane;
  /* Stores the directory to rename and/or merge all the pictures in it */
  private JTextField in_textField;
  /* Stores the directory to merge all the pictures in it */
  private JTextField merge_textField;
  /* Stores the directory to rename and/or merge all the pictures in it */
  private JTextField out_textField;
  /* Stores the prefix used to generate the new files sequence */
  private JTextField prefix_textField;
  /* The index number used to generate the file names */
  private JTextField startIndx_textField;
  /* Stores the default path to select directories */
  private String default_path = null;
  private JCheckBox useLastModChkBox = new JCheckBox("Use last modified date.");
  private JCheckBox avoidDuplicatesChkBox = new JCheckBox("Avoid Duplicates.");
  private JCheckBox keepFailedChkBox = new JCheckBox("Keep Failed.");
  private JCheckBox monthlyChkBox = new JCheckBox("Create Monthly Folders.");
  private JTextField tolerance_textField;
  
  private TextAreaAppender areaAppender = null;
  
  
  
  
  /**
   * Launch the application.
   */
  public static void main(String[] args)
  {
    final String[] finalArgs = args;
    
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          PhotoMergerArgs photoArgs = 
              PhotoMergerUtils.parseCommandLineArgs(finalArgs);
          PhotoGUI frame = new PhotoGUI(photoArgs);
          frame.setVisible(true);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public PhotoGUI(PhotoMergerArgs args)
  {
    String inputDir = args.getInputDir();
    String mergeDir = args.getMergeDir();
    String outputDir = args.getOutputDir();
    String prefix = args.getPrefix();
    int startIndx = args.getStartIndex() ;
    String debugLvl = args.getVerbosityLevel().name();
    double tolerance = PhotoMerger.FILESIZE_PERCENT_TOLERANCE;
    
    // creates the text area to display logger messages
    final JTextArea dbgTextArea = new JTextArea();
    dbgTextArea.setBackground(Color.LIGHT_GRAY);
    dbgTextArea.setEditable(false);
    dbgTextArea.setBounds(421, 5, 4, 20);
    
    this.areaAppender = this.getAppender();
    if(this.areaAppender != null )
      this.areaAppender.setTextArea(dbgTextArea);
    
    
    // Want to ask first, and if yes then close the frame
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener( new WindowAdapter()
    {
        public void windowClosing(WindowEvent e)
        {
            JFrame frame = (JFrame)e.getSource();

            int result = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to exit the application?",
                "Exit Application",
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION)
            {
              logger.info("Closing PhotoMerger");
              frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        }
    });
      
    setTitle("Photo Merger (Order files in chronological order)");
      
    this.setMinimumSize(new Dimension(890, 600));
    setBounds(100, 100, 882, 544);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new BorderLayout(0, 0));
    /*
     * The Split Pane is used so the panels within can adjust the size along 
     * with the Frame.  The upper panel contains all the control widgets and
     * the lower panel contains the debug text area.
     */
    JSplitPane splitPane = new JSplitPane();
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    splitPane.setDividerLocation(200);
    contentPane.add(splitPane, BorderLayout.CENTER);
    /***********************************************************************
     ***********************************************************************
     ******                    Controls Section                        *****
     ***********************************************************************
     ***********************************************************************/
    // Holds all the widgets used to set the parameters
    JPanel controlsPanel = new JPanel();
    splitPane.setLeftComponent(controlsPanel);
    controlsPanel.setLayout(null);
    
    /***********************************************************************
     ************             Input Directory                   ************
     ***********************************************************************/
    JLabel lblInputDirectory = new JLabel("Order files from here");
    lblInputDirectory.setToolTipText("The directory to rename or merge all pictures");
    lblInputDirectory.setBounds(10, 11, 150, 20);
    controlsPanel.add(lblInputDirectory);
    
    in_textField = new JTextField(inputDir);
    in_textField.setToolTipText("The directory to rename or merge all pictures");
    in_textField.setBounds(179, 12, 237, 20);
    controlsPanel.add(in_textField);
    in_textField.setColumns(10);
    
    JButton btnSelect = new JButton("Select");
    btnSelect.setToolTipText("The directory to rename or merge all pictures");
    btnSelect.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) 
      {
        String inputDir = 
              PhotoMergerUtils.getDirectoryName("Select Input Directory", null);
        if( inputDir != null )
          in_textField.setText(inputDir);

        if( inputDir != null ) {
          default_path = new File(inputDir).getParent();
          logger.info("Setting Input Directory to " + inputDir);
        }
        
      }
    });
    btnSelect.setBounds(425, 11, 89, 20);
    controlsPanel.add(btnSelect);
    
    /***********************************************************************
     ************             Use Modified Date                 ************
     ***********************************************************************/
    this.useLastModChkBox.setToolTipText("If the metadata cannot be extractied, it uses the last time the file was modified");
    //this.useLastModChkBox.setBounds(300, 140, 200, 14);
    this.useLastModChkBox.setBounds(520, 14, 200, 20);
    this.useLastModChkBox.setSelected(false);
    controlsPanel.add(this.useLastModChkBox);
    
    
    /***********************************************************************
     ************             Merge Directory                   ************
     ***********************************************************************/
    JLabel lblMergeDirectory = new JLabel("And here (Optional)");
    lblMergeDirectory.setToolTipText("The directory of the pictures to merge");
    lblMergeDirectory.setBounds(10, 43, 150, 20);
    controlsPanel.add(lblMergeDirectory);
    
    merge_textField = new JTextField(mergeDir);
    merge_textField.setToolTipText("The directory of the pictures to merge");
    merge_textField.setColumns(10);
    merge_textField.setBounds(179, 44, 237, 20);
    controlsPanel.add(merge_textField);
    merge_textField.setText(mergeDir);
    
    JButton button = new JButton("Select");
    button.setToolTipText("The directory of the pictures to merge");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) 
      {
        String mergeDir = 
            PhotoMergerUtils.getDirectoryName("Select Merge Directory", default_path);
        if( mergeDir != null )
          merge_textField.setText(mergeDir);
        logger.info("Setting Merge Directory to " + mergeDir);
      }
    });
    button.setBounds(425, 43, 89, 20);
    controlsPanel.add(button);
    
    /***********************************************************************
     ************             Remove Duplicates                 ************
     ***********************************************************************/
    this.avoidDuplicatesChkBox.setToolTipText("Skips more than one file taken at the same time with the same size");
    //this.avoidDuplicatesChkBox.setBounds(500, 140, 150, 14);
    this.avoidDuplicatesChkBox.setBounds(520, 46, 150, 20);
    this.avoidDuplicatesChkBox.setSelected(true);
    controlsPanel.add(this.avoidDuplicatesChkBox);
    
    
    /***********************************************************************
     ************             Output Directory                  ************
     ***********************************************************************/
    JLabel lblOutputDirectory = new JLabel("In Here..");
    lblOutputDirectory.setToolTipText("The directory where to store the pictures");
    lblOutputDirectory.setBounds(10, 72, 150, 20);
    controlsPanel.add(lblOutputDirectory);
    
    out_textField = new JTextField(outputDir);
    out_textField.setToolTipText("The directory where to store the pictures");
    out_textField.setColumns(10);
    out_textField.setBounds(179, 73, 237, 20);
    controlsPanel.add(out_textField);
    
    JButton button_1 = new JButton("Select");
    button_1.setToolTipText("The directory where to store the pictures");
    button_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) 
      {
        String outputDir = 
            PhotoMergerUtils.getDirectoryName("Select Output Directory", default_path);
        if( outputDir != null )
          out_textField.setText(outputDir);
        logger.info("Setting Output Directory to " + outputDir);
      }
    });
    button_1.setBounds(425, 72, 89, 20);
    controlsPanel.add(button_1);
    
    /***********************************************************************
     ************              Monthly Folders                  ************
     ***********************************************************************/
    this.monthlyChkBox.setToolTipText("Creates monthly folders to store files in them rather than a single destination folder");
    this.monthlyChkBox.setBounds(520, 76, 250, 20);  
    this.monthlyChkBox.setSelected(true);
    controlsPanel.add(this.monthlyChkBox);
    
    
    /***********************************************************************
     ************               Image Prefix                    ************
     ***********************************************************************/
    JLabel lblPrefix = new JLabel("Prefix");
    lblPrefix.setToolTipText("The prefix to use when generating the pictures name");
    lblPrefix.setBounds(10, 107, 90, 20);
    controlsPanel.add(lblPrefix);
    
    prefix_textField = new JTextField(prefix);
    prefix_textField.setToolTipText("The prefix to use when generating the pictures name");
    prefix_textField.setColumns(10);
    prefix_textField.setBounds(179, 104, 60, 20);
    controlsPanel.add(prefix_textField);

    /***********************************************************************
     ************                Start Index                    ************
     ***********************************************************************/
    JLabel lblStartIndex = new JLabel("Start Index");
    lblStartIndex.setToolTipText("The index number to begin the sequence");
    //lblStartIndex.setBounds(10, 132, 90, 20);
    lblStartIndex.setBounds(335, 104, 90, 20);
    controlsPanel.add(lblStartIndex);
    
    startIndx_textField = 
        new JTextField( startIndx );
    startIndx_textField.setToolTipText("The index number to begin the sequence");
    startIndx_textField.setColumns(10);
    //startIndx_textField.setBounds(179, 135, 60, 20);
    startIndx_textField.setBounds(425, 104, 60, 20);
    controlsPanel.add(startIndx_textField);
    
    /***********************************************************************
     ************             Keep Failed Media                 ************
     ***********************************************************************/
    this.keepFailedChkBox.setToolTipText("Keeps media files without metadata");
    this.keepFailedChkBox.setBounds(520, 104, 150, 20);
    this.keepFailedChkBox.setSelected(true);
    controlsPanel.add(this.keepFailedChkBox);
    
    
    /***********************************************************************
     ************               Tolerance                    ************
     ***********************************************************************/
    JLabel lblTolerance = new JLabel("Tolerance (%)");
    String tip = "% difference in file size flag files as duplicates.  " +
                 "Greater % might remove pictures taken in rapid shutter mode";
    lblTolerance.setToolTipText(tip);
    
    lblTolerance.setBounds(10, 140, 100, 20);
    controlsPanel.add(lblTolerance);
    
    tolerance_textField = new JTextField( Double.toString(tolerance) );
    tolerance_textField.setToolTipText(tip);
    tolerance_textField.setColumns(4);
    tolerance_textField.setBounds(179, 140, 50, 20);
    controlsPanel.add(tolerance_textField);
    
    
    /***********************************************************************
     ************             Verbosity Level                   ************
     ***********************************************************************/
    JLabel lblVerbosity = new JLabel("Verbosity");
    lblVerbosity.setToolTipText("How much output needs to be sent to the screen");
    //lblVerbosity.setBounds(305, 120, 75, 14);
    lblVerbosity.setBounds(250, 140, 75, 20);
    controlsPanel.add(lblVerbosity);
    
    final JComboBox<Level> comboBox = new JComboBox<Level>();
    comboBox.setToolTipText("How much output needs to be sent to the screen");
    //comboBox.setBounds(425, 117, 90, 20);
    comboBox.setBounds(355, 140, 90, 20);
    comboBox.addItem(Level.FATAL);
    comboBox.addItem(Level.ERROR);
    comboBox.addItem(Level.WARN);
    comboBox.addItem(Level.INFO);
    comboBox.addItem(Level.DEBUG);
    comboBox.addItem(Level.TRACE);
    
    
    comboBox.setSelectedItem(Level.getLevel(debugLvl));
    controlsPanel.add(comboBox);
    
    
    JButton btnPropsViewer = new JButton("Props. Viewer");
    btnPropsViewer.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) 
      {
        EventQueue.invokeLater(new Runnable()
        {
          public void run()
          {
            try
            {
              PropsViewer frame = new PropsViewer(default_path);
              frame.setVisible(true);
            }
            catch (Exception e)
            {
              e.printStackTrace();
            }
          }
        });
      }
    });
    btnPropsViewer.setBounds(425, 173, 150, 23);
    controlsPanel.add(btnPropsViewer);
    
    comboBox.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        Level lvl = (Level) comboBox.getSelectedItem();
        System.err.println("Setting the Level in the COMBO");
        Configurator.setLevel("PhotoMerger", lvl);
        PhotoGUI.logger.atLevel(lvl);
      }
    });
    
    JButton btnClearLog = new JButton("Clear Log");
    btnClearLog.setToolTipText("Clears the log screen");
    controlsPanel.add(btnClearLog);
    btnClearLog.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) 
      {
        dbgTextArea.setText("");
      }
    });
    btnClearLog.setBounds(585, 173, 120, 23);
    

    /***********************************************************************
     ************           Go, Reset, Exit Buttons             ************
     ***********************************************************************/
    JButton btnGo = new JButton("Go");
    btnGo.setToolTipText("Performs the operation (rename or merge)");
    btnGo.setBounds(10, 173, 79, 23);
    controlsPanel.add(btnGo);
    
    JButton resetBtn = new JButton("Reset");
    resetBtn.setToolTipText("Resets all the inputs to their degfault values");
    resetBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) 
      {
        resetValues();
      }
    });
    resetBtn.setBounds(99, 173, 79, 23);
    controlsPanel.add(resetBtn);
    
    
    JButton btnCancel = new JButton("Exit");
    btnCancel.setToolTipText("Exits the program");
    btnCancel.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e) 
      {
        closeFrame();
      }
    });
    btnCancel.setBounds(188, 173, 79, 23);
    controlsPanel.add(btnCancel);
    
    
    
    /***********************************************************************
     ***********************************************************************
     ******                  Debug Message Section                     *****
     ***********************************************************************
     ***********************************************************************/    
    JPanel dbgPanel = new JPanel();
    splitPane.setRightComponent(dbgPanel);
    dbgPanel.setLayout(new BorderLayout(0, 0));
    
    JScrollPane scrollPane = new JScrollPane();
    dbgPanel.add(scrollPane, BorderLayout.CENTER);
    scrollPane.setViewportView(dbgTextArea);
    dbgTextArea.setBackground(Color.LIGHT_GRAY);
    
    /***********************************************************************
     ******                   Executes the program                     *****
     ***********************************************************************/
    btnGo.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e) 
      {
        logger.info("Running PhotoMerger");
        
        
        dbgTextArea.setText(dbgTextArea.getText() + "Running PhotoMerger");
        String inDir = in_textField.getText();
        String mergeDir = merge_textField.getText();
        String outDir = out_textField.getText();
        String prefix = prefix_textField.getText();
        String indx = startIndx_textField.getText();
        String lvl = comboBox.getSelectedItem().toString();
        boolean useIt = useLastModChkBox.isSelected();
        boolean remDup = avoidDuplicatesChkBox.isSelected();
        boolean mkDirs = monthlyChkBox.isSelected();
        boolean keepFailed = keepFailedChkBox.isSelected();
        
        try
        {
          double tolerance = Double.parseDouble( tolerance_textField.getText() );
          if( tolerance < 0 || tolerance > 1 )
            throw new Exception();
          logger.info("Setting the Tolerance to " + tolerance + " %");
          PhotoMerger.FILESIZE_PERCENT_TOLERANCE = tolerance;
        }
        catch( Exception ex )
        {
          JOptionPane.showMessageDialog(null,  
                              "The Tolerance needs to be a positive number", 
                              "Invalid Tolerance", JOptionPane.ERROR_MESSAGE);
          return;
        }
        StringBuffer buf = new StringBuffer();
        buf.append("Input Directory ");
        buf.append( inDir );
        buf.append(", ");
        
        buf.append("Merge Directory ");
        buf.append( mergeDir );
        buf.append(", ");
        
        buf.append("Output Directory ");
        buf.append( outDir );
        buf.append(", ");

        buf.append("Start Index ");
        buf.append( indx );
        buf.append(", ");

        buf.append("Prefix ");
        buf.append( prefix );
        buf.append(", ");
        
        
        buf.append("Use Last Mod. Date ");
        buf.append( useIt );
        buf.append(", ");
        
        buf.append("Avoid Dups. ");
        buf.append( remDup );
        buf.append(", ");

        buf.append("Make Monthly Dirs. ");
        buf.append( mkDirs );
        buf.append(", ");

        buf.append("Keep Failed ");
        buf.append( keepFailed );
        buf.append(", ");
        
        buf.append("Verbosity: ");
        buf.append( lvl );
        
        logger.info(buf.toString());
        dbgTextArea.setText(dbgTextArea.getText() + buf.toString());
        
        int startIndex = PhotoMergerUtils.START_INDEX;
        String message = null;
        
        if( inDir == null || inDir.length() == 0 )
        {
          message = "The input directory cannot be empty";
        }
        else if( prefix == null || prefix.length() == 0 )
        {
          message = "The prefix cannot be empty";
        }
        else if( indx == null || indx.length() == 0 )
        {
          message = "The start index cannot be empty";
        }
        else if( indx.length() > 0 )
        {
          try
          {
            startIndex = Integer.parseInt(indx);
            if( startIndex < 0 )
              message = "The start index cannot be negative";
          }
          catch( Exception ne )
          {
            message = "The start index needs to be a positive integer";
          }
        }
        
        if( message != null )
        {
          showErrorMessage(message);
        }
        else
        {
          boolean showWindows = true;
          PhotoMergerArgs args = new PhotoMergerArgs(inDir, outDir, mergeDir, prefix, startIndex);
          args.setVerbosityLevel((Level)comboBox.getSelectedItem() );
          args.setUseLastModDate(useIt);
          args.setRemoveDuplicates(remDup);
          args.setKeepFailed(keepFailed);
          args.setMakeMonthlyDirs(mkDirs);
          args.setShowPopupWindows(showWindows);
          
          PhotoMerger pm = new PhotoMerger( args );
          pm.processRequest();
          
          if( mergeDir == null || mergeDir.length() == 0 )
            showMessage("Files were renamed successfullly");
          else
            showMessage("Files were merged successfully");
        }
      }
    });
    
    
  }

  private TextAreaAppender getAppender()
  {
    System.err.println("Setting the Text Area");
    org.apache.logging.log4j.core.Logger concrete = (org.apache.logging.log4j.core.Logger)PhotoGUI.logger;
    Map<String, Appender> objs = concrete.getAppenders();
    
    for( String name : objs.keySet() ) {
      PhotoGUI.logger.info("The Name " + name );
      Appender obj = objs.get(name);
      if( obj instanceof TextAreaAppender ) 
      {
        PhotoGUI.logger.info("Found it!!");
        System.err.println("Found it");
        return ((TextAreaAppender) obj);
      }
    }
    return null;
  }
  
  private void closeFrame()
  {
    int result = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to exit the application?",
        "Exit Application",
        JOptionPane.YES_NO_OPTION);
    if (result == JOptionPane.YES_OPTION)
    {
      logger.info("Closing PhotoMerger");
      this.dispose();
    }
  }
  
  private void showMessage(String message)
  {
    JOptionPane.showMessageDialog(
        this,
        message,
        "Merging Result",
        JOptionPane.INFORMATION_MESSAGE);
  }
  
  private void showErrorMessage(String text)
  {
    JOptionPane.showMessageDialog(
        this,
        text,
        "Error Message",
        JOptionPane.ERROR_MESSAGE);
  }
  
  private void resetValues()
  {
    logger.info("Resetting all values");
    this.in_textField.setText("");
    this.merge_textField.setText("");
    this.out_textField.setText("");
    this.prefix_textField.setText(PhotoMergerUtils.PREFIX);
    this.startIndx_textField.setText(
                        Integer.toString(PhotoMergerUtils.START_INDEX));
  }
}




