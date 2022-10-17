package com.github.aursu.expressions;

import java.awt.EventQueue;
import java.awt.GridBagLayout;

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
import java.awt.GridBagConstraints;
import javax.swing.JPasswordField;

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
	private JPasswordField passwordField;

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

		expressionGUI();
		
		JPanel middleBox = new JPanel();
		middleBox.setLayout(new BoxLayout(middleBox, BoxLayout.LINE_AXIS));
		middleBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		scrollPane = new JScrollPane();
		scrollPane.setAlignmentY(Component.TOP_ALIGNMENT);

		textArea = new JTextArea();
		textArea.setRows(512);
		textArea.setColumns(512);
		scrollPane.setViewportView(textArea);
		
		middleBox.add(scrollPane);
		
		btnStore = new JButton("Store");
		btnStore.setEnabled(false);
		btnStore.setAlignmentY(Component.TOP_ALIGNMENT);
		btnStore.setMinimumSize(new Dimension(100, 29));
		
		btnStore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		middleBox.add(btnStore);

		contentPane.add(middleBox);
		
		databaseGUI();
	}
	
	public void expressionGUI() {
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
	}
	
	public void databaseGUI() {
		JPanel databaseBox = new JPanel();
		databaseBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		GridBagLayout gbl_databaseBox = new GridBagLayout();
		gbl_databaseBox.columnWeights = new double[]{0.0, 1.0};
		databaseBox.setLayout(gbl_databaseBox);

		JLabel lblDBHost = new JLabel("Database Host");
		JTextField txtDBHost = new JTextField();
		txtDBHost.setText("localhost");
		txtDBHost.setToolTipText("Enter database host");
		lblDBHost.setLabelFor(txtDBHost);

		GridBagConstraints gbc_lblDBHost = new GridBagConstraints();
		gbc_lblDBHost.anchor = GridBagConstraints.WEST;
		gbc_lblDBHost.gridx = 0;
		gbc_lblDBHost.gridy = 0;
		databaseBox.add(lblDBHost, gbc_lblDBHost);

		GridBagConstraints gbc_txtDBHost = new GridBagConstraints();
		gbc_txtDBHost.anchor = GridBagConstraints.WEST;
		gbc_txtDBHost.gridx = 1;
		gbc_txtDBHost.gridy = 0;
		gbc_txtDBHost.fill = GridBagConstraints.HORIZONTAL;
		databaseBox.add(txtDBHost, gbc_txtDBHost);
		
		JLabel lblDBUser = new JLabel("Database User");
		JTextField txtDBUser = new JTextField();
		txtDBUser.setText("root");
		txtDBUser.setToolTipText("Enter database user");
		lblDBUser.setLabelFor(txtDBUser);

		GridBagConstraints gbc_lblDBUser = new GridBagConstraints();
		gbc_lblDBUser.anchor = GridBagConstraints.WEST;
		gbc_lblDBUser.gridx = 0;
		gbc_lblDBUser.gridy = 1;
		databaseBox.add(lblDBUser, gbc_lblDBUser);
		
		GridBagConstraints gbc_txtDBUser = new GridBagConstraints();
		gbc_txtDBUser.anchor = GridBagConstraints.WEST;
		gbc_txtDBUser.gridx = 1;
		gbc_txtDBUser.gridy = 1;
		gbc_txtDBUser.fill = GridBagConstraints.HORIZONTAL;
		databaseBox.add(txtDBUser, gbc_txtDBUser);

		JLabel lblDBPassword = new JLabel("Database Password");
		passwordField = new JPasswordField();
		passwordField.setToolTipText("Enter database password");
		lblDBPassword.setLabelFor(passwordField);
		
		GridBagConstraints gbc_lblDBPassword = new GridBagConstraints();
		gbc_lblDBPassword.gridx = 0;
		gbc_lblDBPassword.gridy = 2;
		databaseBox.add(lblDBPassword, gbc_lblDBPassword);

		GridBagConstraints gbc_pwdDBPassword = new GridBagConstraints();
		gbc_pwdDBPassword.anchor = GridBagConstraints.WEST;
		gbc_pwdDBPassword.gridx = 1;
		gbc_pwdDBPassword.gridy = 2;
		gbc_pwdDBPassword.fill = GridBagConstraints.HORIZONTAL;
		databaseBox.add(passwordField, gbc_pwdDBPassword);

		JLabel lblDBName = new JLabel("Database Name");
		JTextField txtDBName = new JTextField();
		txtDBName.setText("expressions");
		txtDBName.setToolTipText("Enter database name");
		lblDBName.setLabelFor(txtDBName);

		GridBagConstraints gbc_lblDBName = new GridBagConstraints();
		gbc_lblDBName.anchor = GridBagConstraints.WEST;
		gbc_lblDBName.gridx = 0;
		gbc_lblDBName.gridy = 3;
		databaseBox.add(lblDBName, gbc_lblDBName);

		GridBagConstraints gbc_txtDBName = new GridBagConstraints();
		gbc_txtDBName.anchor = GridBagConstraints.WEST;
		gbc_txtDBName.gridx = 1;
		gbc_txtDBName.gridy = 3;
		gbc_txtDBName.fill = GridBagConstraints.HORIZONTAL;
		databaseBox.add(txtDBName, gbc_txtDBName);
		
		contentPane.add(databaseBox);
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
