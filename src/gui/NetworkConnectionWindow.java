package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import network.Network;

@SuppressWarnings("serial")
public class NetworkConnectionWindow extends JFrame{
	JPanel content;
	JButton hostButton;
	JTextField ipTextField;
	JButton joinButton;
	Network network;
	
	public NetworkConnectionWindow() {
		content = new JPanel();
		network = new Network();
		
		ipTextField = new JTextField("127.0.0.1");
		ipTextField.setPreferredSize(new Dimension(400, 50));
		hostButton = new JButton("HOST");
		hostButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent action) {
				network.host();
			}
		});
		joinButton = new JButton("JOIN");
		joinButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent action) {
				network.join(ipTextField.getText());
			}
		});
		
		this.setSize(500, 300);
		this.setTitle("Triangles Connector");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(content);
		GridLayout gridLayout = new GridLayout(1, 2);
		gridLayout.setHgap(5);
		content.setLayout(gridLayout);
		content.add(hostButton);
		JPanel joinPanel = new JPanel();
		joinPanel.setLayout(new BorderLayout(2, 2));
		joinPanel.add(joinButton, BorderLayout.CENTER);
		joinPanel.add(ipTextField, BorderLayout.PAGE_END);
		content.add(joinPanel);
	}
	
	public Network getConnection() {
		this.setVisible(true);
		while (!network.connected) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println("Network Window sleep interrupted");
			}
		}
		this.setVisible(false);
		return network;
	}
}
