package test;

import java.awt.*;  
import javax.swing.*;  
import javax.swing.table.DefaultTableModel;  
  
public class GcDisk {  
   private static final Dimension MAIN_SIZE = new Dimension(900, 500);  
     
   private JPanel mainPanel = new JPanel();  
   private DefaultTableModel gcDiskTableModel;  
   private JTable gcDiskUsageTable = new JTable();  
   private JScrollPane jScrollPane0 = new JScrollPane(gcDiskUsageTable);  
     
   public GcDisk() {  
      initComponents();  
   }  
  
   private void initComponents() {  
      setupTable();        
        
      JButton browseBtn = new JButton("Browse");  
      JButton searchBtn = new JButton("Search");  
        
      JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));  
      browsePanel.add(new JLabel("Start Folder"));  
      browsePanel.add(Box.createHorizontalStrut(10));  
      browsePanel.add(browseBtn);  
        
      JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));  
      searchPanel.add(searchBtn);  
      searchPanel.add(Box.createHorizontalStrut(15));  
      searchPanel.add(new JLabel("===> Start by selecting a start folder"));  
        
      JPanel topPanel = new JPanel();  
      topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));  
      topPanel.add(browsePanel);  
      topPanel.add(Box.createVerticalStrut(20));  
      topPanel.add(searchPanel);  
        
      int gap = 8;  
      mainPanel.setBorder(BorderFactory.createEmptyBorder(gap, gap, gap, gap));  
      mainPanel.setPreferredSize(MAIN_SIZE);  
      mainPanel.setLayout(new BorderLayout(10, 10));  
      mainPanel.add(topPanel, BorderLayout.NORTH);  
      mainPanel.add(jScrollPane0, BorderLayout.CENTER);  
   }  
  
   @SuppressWarnings("serial")  
   private void setupTable() {  
      gcDiskTableModel = new DefaultTableModel(new Object[][]{{null, null,  
         null,},}, new String[]{"Size", "dir", "File",}) {  
  
         @Override  
         public Class<?> getColumnClass(int columnIndex) {  
            if (columnIndex == 0) {  
               return Long.class;  
            } else {  
               return super.getColumnClass(columnIndex);  
            }  
         }  
      };  
      gcDiskUsageTable.setModel(gcDiskTableModel);  
  
      gcDiskUsageTable.setAutoCreateRowSorter(true);  
      gcDiskUsageTable.setFillsViewportHeight(true);  
   }  
     
   public JComponent getMainPanel() {  
      return mainPanel;  
   }  
     
   public JMenuBar createMenuBar() {  
      JMenu helpMenu = new JMenu("Help");  
        
      JMenuBar menubar = new JMenuBar();  
      menubar.add(helpMenu);  
      return menubar;  
   }  
  
   private static void createAndShowUI() {  
      GcDisk gcDisk = new GcDisk();  
        
      JFrame frame = new JFrame("GC Disk Usage");  
      frame.getContentPane().add(gcDisk.getMainPanel());  
      frame.setJMenuBar(gcDisk.createMenuBar());  
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
      frame.pack();  
      frame.setLocationRelativeTo(null);  
      frame.setVisible(true);  
   }  
  
   public static void main(String[] args) {  
      java.awt.EventQueue.invokeLater(new Runnable() {  
         public void run() {  
            createAndShowUI();  
         }  
      });  
   }  
}  