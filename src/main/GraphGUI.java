package main;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import basiccomponentmodel.Edge;
import basiccomponentmodel.Node;
import graph.Graph;
import pathfinding.Dijkstra;
import pathfinding.Vert;

@SuppressWarnings("serial")
public class GraphGUI extends JFrame implements ActionListener {

	public ContentPanel contentPanel;
	
	public GraphGUI(String title) {
		this(title, new Graph());
	}
	
	@SuppressWarnings("deprecation")
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
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		JMenuItem loadMenuItem = new JMenuItem("Open");
		loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		loadMenuItem.addActionListener(this);
		loadMenuItem.setIcon(new ImageIcon("folder.png"));
		JMenuItem saveMenuItem = new JMenuItem("Save");
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		saveMenuItem.addActionListener(this);
		saveMenuItem.setIcon(new ImageIcon("floppy.png"));
		fileMenu.add(loadMenuItem);
		fileMenu.add(saveMenuItem);
		JMenuItem screenCaptureItem = new JMenuItem("Screen Capture");
		JMenuItem closeMenuButton = new JMenuItem("Close");
		closeMenuButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		closeMenuButton.addActionListener(event -> {
			System.exit(0);
		});
		closeMenuButton.setIcon(new ImageIcon("close.png"));
		screenCaptureItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		screenCaptureItem.addActionListener(this);
		screenCaptureItem.setIcon(new ImageIcon("picture.png"));
		fileMenu.add(screenCaptureItem);
		fileMenu.add(closeMenuButton);
		JMenuItem helpMenuItem = new JMenuItem("Help");
		helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helpMenuItem.setIcon(new ImageIcon("help.png"));
		helpMenu.add(helpMenuItem);
		helpMenuItem.addActionListener(event -> {
			HelpFrame helpFrame = new HelpFrame();
	        helpFrame.setVisible(true);
		});
	}
	
	public static void main(String[] args) {
		
        try {
        	for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

       
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				GraphGUI graphGUI = new GraphGUI("Graph Path Finding Project", Graph.example());
				graphGUI.setVisible(true);
				Graph graph = graphGUI.contentPanel.getGraph();
				ArrayList<Node> nodes = graph.getNodes();
				Dijkstra pathFinding = new Dijkstra(graph.getNodes(), nodes.get(0));
				ArrayList<Vert> shortestPath = pathFinding.getShortestPath(nodes.get(nodes.size() - 1));
				ArrayList<Node> passedNodes = new ArrayList<Node>();
				for (int i = 0; i < shortestPath.size(); i++) {
					for (Node node : graph.getNodes())
					{
						if (node.getLabel().equals(shortestPath.get(i).getName())) {
							passedNodes.add(node);
						}
					}
				}
				//for (int i = 0; i < passedNodes.size(); i++)
				//	System.out.println(passedNodes.get(i).getLabel());
				for (int i = 0; i < passedNodes.size() - 1; i++) {
					for (Edge e : graph.getEdges()) {
						if (e.isIt(passedNodes.get(i), passedNodes.get(i + 1)))
							break;
						System.out.println(e.getStartNode().getLabel() + "->" + e.getEndNode().getLabel());
					}
				}
				System.out.println();
				graphGUI.contentPanel.update();
				System.out.println(pathFinding.getShortestDistance(nodes.get(nodes.size() - 1)));
				//System.out.println(pathFinding.getShortestPath(nodes.get(nodes.size() - 2)));
				System.out.println(shortestPath);
			}
			
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		
		if (event.getActionCommand().equals("Open")) {
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
         
                System.out.println("The screenshot was saved!");
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
		
	}
	
}