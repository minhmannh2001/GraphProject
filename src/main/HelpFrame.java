package main;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class HelpFrame extends JFrame {

	private JButton closeButton;
    private JTextArea helpTextArea;
    private JScrollPane jScrollPane;

    public HelpFrame() {
        initComponents();
    }

    private void initComponents() {

        
        closeButton = new JButton();
        jScrollPane = new JScrollPane();
        helpTextArea = new JTextArea();

     // Make the window appear in middle of screen
        this.setSize(500, 400);
     	this.setLocationRelativeTo(null);
     	this.setResizable(false);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Help");

        closeButton.setText("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	closeButtonActionPerformed(event);
            }
        });

        jScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        helpTextArea.setEditable(false);
        helpTextArea.setColumns(20);
        helpTextArea.setFont(new java.awt.Font(Font.DIALOG_INPUT, Font.TRUETYPE_FONT, 16));
        helpTextArea.setLineWrap(true);
        helpTextArea.setRows(5);
        helpTextArea.setText("      Welcome to our path finding application\n\n1.Node addition or removal, choosing start and end node can be done by right clicking of your mouse. \n2.You can also add new edges, but by clicking on   the starting node and end node.\n3.Edges can be removed again from popup menu, whichappears after right clicking on one.\n4.Graphs can be stored or loaded from files in textformat.");
        jScrollPane.setViewportView(helpTextArea);

        javax.swing.GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeButton))
                    .addComponent(jScrollPane, GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            	.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );

        pack();
    }
    
    private void closeButtonActionPerformed(ActionEvent event) {
        this.dispose();
    }
    
    public static void main(String args[]) {
     
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    try {
						UIManager.setLookAndFeel(info.getClassName());
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| UnsupportedLookAndFeelException e) {
						e.printStackTrace();
					}
                    break;
                }
            }
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HelpFrame().setVisible(true);
            }
        });
    }
}
