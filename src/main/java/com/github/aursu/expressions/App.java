package com.github.aursu.expressions;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import java.awt.Component;

@SuppressWarnings("serial")
public class App extends JFrame implements ActionListener {
	
	public static final String CMD_CHECK = "checkExpression";

	// UI
	private JPanel contentPane;
	private JTextField txtExpression;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App frame = new App();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public App() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setSize(450, 300);

		contentPane = new JPanel();
		// contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		// contentPane.setLayout(null);
		
		JLabel lblExpression = new JLabel("Expression");
		// lblExpression.setBounds(10, 26, 70, 16);
		contentPane.add(lblExpression);

		txtExpression = new JTextField();
		// txtExpression.setBounds(85, 20, 250, 29);
		txtExpression.setToolTipText("Enter arithmetic expression");
		contentPane.add(txtExpression);
		txtExpression.setColumns(10);
		
		JButton btnCheck = new JButton("Check");
		btnCheck.setActionCommand(App.CMD_CHECK);
		btnCheck.addActionListener(this);

		// btnCheck.setBounds(335, 20, 100, 29);
		contentPane.add(btnCheck);
		
		JButton btnStore = new JButton("Store");
		btnStore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		// btnStore.setBounds(335, 50, 100, 29);
		contentPane.add(btnStore);
		
		scrollPane = new JScrollPane();
		// scrollPane.setBounds(10, 55, 320, 200);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setRows(512);
		textArea.setColumns(512);
		scrollPane.setViewportView(textArea);
	}

	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
			case App.CMD_CHECK:
				String expression = txtExpression.getText();
				if (expression.isEmpty())
					printErrorMessage("Expression is empty");
				
				Number result = calculateExpression(expression);
				if (result == null) 
				{ /* printErrorMessage("Can not calculate expression"); */ }
				else {
					textArea.setText(result.toString());
				}
				break;
		}
	}

	public Number calculateExpression(String expression) {
		RPNParser parser = new RPNParser(expression);
		try {
			return parser.rpnEvaluate();
		} catch (ParseException e) {
			printStackTrace(e);
		}

		return null;
	}

	public void printErrorMessage(String errMsg) {
		textArea.setText(errMsg);
	}

	private void printStackTrace(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		e.printStackTrace(pw);
		
		printErrorMessage(sw.toString());
	}
}
