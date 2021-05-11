package main;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import graph.Graph;

@SuppressWarnings("serial")
public class GraphGUI extends JFrame implements ActionListener {

	private ContentPanel contentPanel;
	
	public GraphGUI(String title) {
		this(title, new Graph());
	}
	
	public GraphGUI(String title, Graph graph) {
		super(title);
		contentPanel = new ContentPanel(graph);
		this.add(contentPanel);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(900, 600);
		// Make the window appear in middle of screen
		this.setLocationRelativeTo(null);
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		JMenu imageMenu = new JMenu("Image");
		menuBar.add(fileMenu);
		menuBar.add(imageMenu);
		JMenuItem loadMenuItem = new JMenuItem("Load");
		loadMenuItem.addActionListener(this);
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(this);
		fileMenu.add(loadMenuItem);
		fileMenu.add(saveMenuItem);
		JMenuItem screenCaptureItem = new JMenuItem("Screen Capture");
		screenCaptureItem.addActionListener(this);
		imageMenu.add(screenCaptureItem);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new GraphGUI("Graph Path Finding Project").setVisible(true);;
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		
		if (event.getActionCommand().equals("Load")) {
			int response = fileChooser.showOpenDialog(null);
			FileNameExtensionFilter txt = new FileNameExtensionFilter("Text files", "txt");
			FileNameExtensionFilter rtf = new FileNameExtensionFilter("Rich Text files", "rtf");
			fileChooser.addChoosableFileFilter(txt);
			fileChooser.addChoosableFileFilter(rtf);
			fileChooser.setFileFilter(txt);
			
			if (response == JFileChooser.APPROVE_OPTION) {
				File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
				FileReader fileReader = null;
				try {
					fileReader = new FileReader(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				BufferedReader fileIn = new BufferedReader(fileReader);
				String[] options = {"Adjacency List", "Standardized Format"};
				String option = (String)JOptionPane.showInputDialog(null, "Select a format: ", "Format Option", JOptionPane.OK_OPTION, null, options, "Standardized Format");
				if (option == "Standardized Format") {
					try {
						contentPanel.setGraph(Graph.loadFrom(fileIn));
						JOptionPane.showMessageDialog(null, "Loading file successfully !", "Message", JOptionPane.PLAIN_MESSAGE);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Error Loading Graph From File !", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					try {
						contentPanel.setGraph(Graph.readFile(file.toString()));
						JOptionPane.showMessageDialog(null, "Loading file successfully !", "Message", JOptionPane.PLAIN_MESSAGE);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Error Loading Graph From File !", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
        } else if (event.getActionCommand().equals("Save")) {
        	int reponse = fileChooser.showSaveDialog(null);
        	if (reponse == JFileChooser.APPROVE_OPTION){
        		File file;
        		PrintWriter fileOut = null;
        		// Get the save location we've chosen
        		file = new File(fileChooser.getSelectedFile().getAbsolutePath());
        		try {
					fileOut = new PrintWriter(file);
					contentPanel.getGraph().saveTo(fileOut);
					JOptionPane.showMessageDialog(null, "Saving file successfully !", "Message", JOptionPane.PLAIN_MESSAGE);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				finally {
					fileOut.close();
				}
        	}
 
        } else if (event.getActionCommand().equals("Screen Capture")) {
        	Rectangle rect = contentPanel.getBounds();
        	 
            try {
                String format = "png";
                String fileName = "ScreenCapture" + "." + format;
                BufferedImage captureImage = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
                contentPanel.paint(captureImage.getGraphics());
         
                ImageIO.write(captureImage, format, new File(fileName));
         
                System.out.printf("The screenshot was saved!");
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
		
	}
	
}
