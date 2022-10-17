package com.github.aursu.expressions;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

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
import java.awt.Dimension;

import javax.swing.SwingConstants;

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
		setSize(450, 300);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

		JPanel upperBox = new JPanel();
		upperBox.setLayout(new BoxLayout(upperBox, BoxLayout.LINE_AXIS));
		upperBox.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JLabel lblExpression = new JLabel("Expression");
		lblExpression.setHorizontalAlignment(SwingConstants.LEFT);
		lblExpression.setPreferredSize(new Dimension(70, 16));
		upperBox.add(lblExpression);

		txtExpression = new JTextField();
		txtExpression.setPreferredSize(new Dimension(250, 29));
		txtExpression.setToolTipText("Enter arithmetic expression");

		upperBox.add(txtExpression);
		
		JButton btnCheck = new JButton("Check");
		btnCheck.setPreferredSize(new Dimension(100, 29));

		btnCheck.setActionCommand(App.CMD_CHECK);
		btnCheck.addActionListener(this);
		
		upperBox.add(btnCheck);

		contentPane.add(upperBox);
		
		JPanel bottomBox = new JPanel();
		bottomBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		bottomBox.setLayout(new BoxLayout(bottomBox, BoxLayout.LINE_AXIS));

		scrollPane = new JScrollPane();
		scrollPane.setAlignmentY(Component.TOP_ALIGNMENT);

		textArea = new JTextArea();
		textArea.setRows(512);
		textArea.setColumns(512);
		scrollPane.setViewportView(textArea);
		
		bottomBox.add(scrollPane);
		
		JButton btnStore = new JButton("Store");
		btnStore.setAlignmentY(Component.TOP_ALIGNMENT);
		btnStore.setMinimumSize(new Dimension(100, 29));
		
		btnStore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		bottomBox.add(btnStore);
		
		contentPane.add(bottomBox);
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
