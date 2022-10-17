package com.github.aursu.expressions;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
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
public class App extends JFrame {

	// UI
	private JPanel contentPane;
	private JTextField txtExpression;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JButton btnStore;

	/** Listeners
	 *
	 * exprChange is to disable "Store" button if expression changed
	 */
	DocumentListener exprChange = new DocumentListener() {
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}
		public void changedUpdate(DocumentEvent e) {
			btnStore.setEnabled(false);
		}
	};
	
	/**
	 * exprCheck is to parse and check expression
	 */
	ActionListener exprCheck = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			checkExpression();
		}
	};

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
		/* https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html */
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
		
		Document expr = txtExpression.getDocument();
	    if (expr != null) expr.addDocumentListener(exprChange);

		upperBox.add(txtExpression);
		
		JButton btnCheck = new JButton("Check");
		btnCheck.setPreferredSize(new Dimension(100, 29));		
		btnCheck.addActionListener(exprCheck);
		
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
		
		btnStore = new JButton("Store");
		btnStore.setEnabled(false);
		btnStore.setAlignmentY(Component.TOP_ALIGNMENT);
		btnStore.setMinimumSize(new Dimension(100, 29));
		
		btnStore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		bottomBox.add(btnStore);
		
		contentPane.add(bottomBox);
	}

	public void checkExpression() {
		String expression = txtExpression.getText();

		if (expression.isEmpty()) printMessage("Expression is empty");
		
		Number result = calculateExpression(expression);

		if (result == null) printMessage("Can not calculate expression", true);
		else {
			printMessage(result.toString());
			btnStore.setEnabled(true);
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

	public void printMessage(String msg) {
		printMessage(msg, false);
	}

	public void printMessage(String msg, boolean add) {
		if (add) {
			textArea.append(msg);
		}
		else {
			textArea.setText(msg);
		}
	}

	private void printStackTrace(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		e.printStackTrace(pw);
		
		printMessage(sw.toString());
	}
}
