package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import graph.Graph;

public class GraphEditorFrame extends JFrame implements ActionListener{

	private GraphEditor editor;
	
	public GraphEditorFrame(String title) {
		this(title, new Graph());
	}
	public GraphEditorFrame(String title, Graph g) {
		super(title);
		add(editor = new GraphEditor(g));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 400);
		JMenuBar menubar = new JMenuBar();
	    setJMenuBar(menubar);
	    JMenu file = new JMenu("File");
	    menubar.add(file);
	    JMenuItem load = new JMenuItem("Load");
	    JMenuItem save = new JMenuItem("Save");
	    file.add(load);
	    file.add(save);
	    load.addActionListener(this);
	    save.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        if (e.getActionCommand().equals("Load")) {
            int returnVal = chooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    editor.setGraph(Graph.loadFrom(new java.io.BufferedReader(
                        new java.io.FileReader(chooser.getSelectedFile().getAbsoluteFile()))));
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                           "Error Loading Graph From File !",
                           "Error",
                           JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else {
            int returnVal = chooser.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    editor.getGraph().saveTo(new java.io.PrintWriter(
                        new java.io.FileWriter(chooser.getSelectedFile().getAbsoluteFile())));
                }
                catch (java.io.IOException ex) {}
            }
        }
    }
	
	public static void main(String[] args) {
		new GraphEditorFrame("Graph Editor", Graph.example()).setVisible(true);
	}

}
