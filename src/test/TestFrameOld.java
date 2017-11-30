package test;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JComboBox;

import oeg.photo_merger.utils.PhotoMergerHandler;
import oeg.photo_merger.utils.PhotoMergerUtils;

public class TestFrameOld extends JFrame
{

  private JPanel contentPane;
  private JTextField textField;
  private JTextField textField_1;
  private JTextField textField_2;
  private JTextField textField_3;
  private JTextField textField_4;

  private static Logger logger = null;
  
  /**
   * Launch the application.
   */
  public static void main(String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          TestFrame frame = new TestFrame();
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
  public TestFrameOld()
  {
    final JTextArea dbgTextArea = new JTextArea();
    logger = PhotoMergerUtils.getLogger(new PhotoMergerHandler(dbgTextArea));
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 708, 535);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new BorderLayout(0, 0));
    
    JSplitPane splitPane = new JSplitPane();
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    contentPane.add(splitPane, BorderLayout.CENTER);
    
    JPanel panel = new JPanel();
    splitPane.setLeftComponent(panel);
    panel.setLayout(new BorderLayout(0, 0));
    
//    JPanel panel_2 = new JPanel();
//    panel.add(panel_2, BorderLayout.CENTER);
//    panel_2.setLayout(null);
    
    JLabel lblInputDirectory = new JLabel("Input Directory");
    lblInputDirectory.setBounds(10, 11, 73, 14);
    panel.add(lblInputDirectory);
    
    textField = new JTextField();
    textField.setBounds(115, 8, 255, 20);
    panel.add(textField);
    textField.setColumns(10);
    
    JLabel lblMergeDirectory = new JLabel("Merge Directory");
    lblMergeDirectory.setBounds(10, 39, 83, 14);
    panel.add(lblMergeDirectory);
    
    textField_1 = new JTextField();
    textField_1.setColumns(10);
    textField_1.setBounds(115, 36, 255, 20);
    panel.add(textField_1);
    
    JLabel lblOutputDirectory = new JLabel("Output Directory");
    lblOutputDirectory.setBounds(10, 64, 83, 14);
    panel.add(lblOutputDirectory);
    
    textField_2 = new JTextField();
    textField_2.setColumns(10);
    textField_2.setBounds(115, 64, 255, 20);
    panel.add(textField_2);
    
    JLabel lblPrefix = new JLabel("Prefix");
    lblPrefix.setBounds(10, 102, 83, 14);
    panel.add(lblPrefix);
    
    textField_3 = new JTextField();
    textField_3.setColumns(10);
    textField_3.setBounds(115, 102, 45, 20);
    panel.add(textField_3);
    
    JLabel lblStartIndex = new JLabel("Start Index");
    lblStartIndex.setBounds(10, 127, 83, 14);
    panel.add(lblStartIndex);
    
    textField_4 = new JTextField();
    textField_4.setColumns(10);
    textField_4.setBounds(115, 127, 45, 20);
    panel.add(textField_4);
    
    JButton btnSelect = new JButton("Select");
    btnSelect.setBounds(380, 7, 83, 23);
    panel.add(btnSelect);
    
    JButton button = new JButton("Select");
    button.setBounds(380, 35, 83, 23);
    panel.add(button);
    
    JButton button_1 = new JButton("Select");
    button_1.setBounds(380, 64, 83, 23);
    panel.add(button_1);
    
    JButton btnGo = new JButton("Go");
    btnGo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      }
    });
    btnGo.setBounds(188, 165, 83, 23);
    panel.add(btnGo);
    
    JButton btnExit = new JButton("Exit");
    btnExit.setBounds(296, 165, 83, 23);
    panel.add(btnExit);
    
    JLabel lblVerbosity = new JLabel("Verbosity");
    lblVerbosity.setBounds(323, 115, 46, 14);
    panel.add(lblVerbosity);
    
    final JComboBox comboBox = new JComboBox();
    comboBox.setBounds(375, 117, 90, 20);
    comboBox.addItem(Level.SEVERE);
    comboBox.addItem(Level.WARNING);
    comboBox.addItem(Level.INFO);
    comboBox.addItem(Level.FINE);
    comboBox.addItem(Level.FINER);
    comboBox.addItem(Level.FINEST);
//    contentPane.add(comboBox);    
    panel.add(comboBox);
    
    comboBox.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        Level lvl = (Level) comboBox.getSelectedItem();
        logger = PhotoMergerUtils.getLogger(lvl, new PhotoMergerHandler(dbgTextArea));
        
        logger.severe("This is a SEVERE message");
        logger.warning("This is a WARNING message");
        logger.info("This is a INFO message");
        logger.fine("This is a FINE message");
        logger.finer("This is a FINER message");
        logger.finest("This is a FINEST message");
      }
    });    
    
    
    splitPane.setDividerLocation( 200 );
    
    JPanel panel_1 = new JPanel();
    splitPane.setRightComponent(panel_1);
    panel_1.setLayout(new BorderLayout(0, 0));
    
    panel_1.add(dbgTextArea);
    
    JScrollPane scrollPane = new JScrollPane();
    panel_1.add(scrollPane);
    
    JButton btnNewButton = new JButton("New button");
    scrollPane.setViewportView(btnNewButton);
  }
}
