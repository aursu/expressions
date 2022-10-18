package com.github.aursu.expressions;

import java.awt.EventQueue;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

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
import javax.swing.JTable;

@SuppressWarnings("serial")
public class App extends JFrame {
	// UI
	private JPanel contentPane;
	private JTextField txtExpression;
	private JTextArea textArea;

	// Database interface
	private JButton btnStore;
	private JButton btnLoad;
	private JTextField txtDBHost;
	private JTextField txtDBUser;
	private JPasswordField passwordField;
	private JTextField txtDBName;

	private JTable table;

	private String dbHost, dbUser, dbPassword, dbName;
	
	// search interface
	private JButton btnSearch;
	private JTextField txtSearch;
	

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
		setSize(450, 550);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		/* https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html */
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

		expressionGUI();
		textGUI();
		databaseGUI();
		contentGui();
		searchGUI();
	}

	private void expressionGUI() {
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

		ActionListener exprCheck = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkExpression();
			}
		};

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
	
	private void textGUI() {
		JPanel textBox = new JPanel();
		textBox.setLayout(new BoxLayout(textBox, BoxLayout.LINE_AXIS));
		textBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setAlignmentY(Component.TOP_ALIGNMENT);
		scrollPane.setMinimumSize(new Dimension(0, 100));

		textArea = new JTextArea();
		textArea.setRows(512);
		textArea.setColumns(512);
		scrollPane.setViewportView(textArea);
		
		textBox.add(scrollPane);
		
		btnStore = new JButton("Store");
		btnStore.setEnabled(false);
		btnStore.setAlignmentY(Component.TOP_ALIGNMENT);
		btnStore.setMinimumSize(new Dimension(100, 29));
		
		btnStore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		textBox.add(btnStore);

		contentPane.add(textBox);
	}
	
	private void databaseGUI() {
		DocumentListener credsChange = new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}
			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}
			public void changedUpdate(DocumentEvent e) {				
				if (setupDBCredentials()) btnLoad.setEnabled(true);
				else btnLoad.setEnabled(false);
			}
		};
		
		JPanel databaseBox = new JPanel();
		databaseBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		GridBagLayout gbl_databaseBox = new GridBagLayout();
		gbl_databaseBox.columnWeights = new double[]{0.0, 1.0};
		databaseBox.setLayout(gbl_databaseBox);

		txtDBHost = new JTextField();
		txtDBHost.setText("localhost");
		txtDBHost.setToolTipText("Enter database host");
		
		Document docDBHost = txtDBHost.getDocument();
	    if (docDBHost != null) docDBHost.addDocumentListener(credsChange);

		JLabel lblDBHost = new JLabel("Database Host");
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

		txtDBUser = new JTextField();
		txtDBUser.setText("expressions");
		txtDBUser.setToolTipText("Enter database user");

		Document docDBUser = txtDBUser.getDocument();
	    if (docDBUser != null) docDBUser.addDocumentListener(credsChange);

		JLabel lblDBUser = new JLabel("Database User");
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

		passwordField = new JPasswordField();
		passwordField.setToolTipText("Enter database password");

		Document docDBPassword = passwordField.getDocument();
	    if (docDBPassword != null) docDBPassword.addDocumentListener(credsChange);

		JLabel lblDBPassword = new JLabel("Database Password");
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

		txtDBName = new JTextField();
		txtDBName.setText("expressions");
		txtDBName.setToolTipText("Enter database name");

		Document docDBName = txtDBName.getDocument();
	    if (docDBName != null) docDBName.addDocumentListener(credsChange);

		JLabel lblDBName = new JLabel("Database Name");
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
	
	private void contentGui() {
		JPanel contentBox = new JPanel();
		contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.LINE_AXIS));
		contentBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JScrollPane contentScrollPane = new JScrollPane();
		contentScrollPane.setAlignmentY(Component.TOP_ALIGNMENT);
		contentScrollPane.setMinimumSize(new Dimension(0, 100));

		String[] columnNames = {
			"Polish",
            "Infix",
            "Value"
        };
		Object[][] data = {};
		
		table = new JTable(data, columnNames);
		table.setFillsViewportHeight(true);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		contentScrollPane.setViewportView(table);

		contentBox.add(contentScrollPane);

		btnLoad = new JButton("Load");
		btnLoad.setEnabled(false);
		btnLoad.setAlignmentY(Component.TOP_ALIGNMENT);
		btnLoad.setPreferredSize(new Dimension(100, 29));
		
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		contentBox.add(btnLoad);

		contentPane.add(contentBox);
	}

	private void searchGUI() {
		DocumentListener srchChange = new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}
			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}
			public void changedUpdate(DocumentEvent e) {
				validateSearch();
			}
		};

		ActionListener resultSearch = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchExpression();
			}
		};

		JPanel searchBox = new JPanel();
		searchBox.setLayout(new BoxLayout(searchBox, BoxLayout.LINE_AXIS));
		searchBox.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JLabel lblSearch = new JLabel("Search");
		lblSearch.setHorizontalAlignment(SwingConstants.CENTER);
		lblSearch.setPreferredSize(new Dimension(70, 16));
		searchBox.add(lblSearch);

		txtSearch = new JTextField();
		txtSearch.setPreferredSize(new Dimension(250, 29));
		txtSearch.setToolTipText("Enter search expression");

		Document expr = txtSearch.getDocument();
	    if (expr != null) expr.addDocumentListener(srchChange);

		searchBox.add(txtSearch);

		btnSearch = new JButton("Search");
		btnSearch.setEnabled(false);
		btnSearch.setPreferredSize(new Dimension(100, 29));		
		btnSearch.addActionListener(resultSearch);

		searchBox.add(btnSearch);

		contentPane.add(searchBox);
	}
	
	public void searchExpression() {
		
	}
	
	private boolean setupDBCredentials() {
		dbHost = txtDBHost.getText();
		dbUser = txtDBUser.getText();
		dbPassword = new String(passwordField.getPassword());
		dbName = txtDBName.getText();
		
		String hostName = dbHost;

		if (dbHost.isEmpty() || dbUser.isEmpty() || dbPassword.isEmpty() || dbName.isEmpty())
			return false;
		
		// check port number
		if (dbHost.indexOf(':') > 0) {
			String hostAddr[] = dbHost.split(":");
			hostName = hostAddr[0];

			try {
				Integer.valueOf(hostAddr[1]);
			}
			catch(NumberFormatException e){
				return false;
			}
		}

	    if (InetAddressValidator.getInstance().isValid(hostName) || DomainValidator.getInstance(true).isValid(hostName))
			return true;

		return false;
	}

	public void checkExpression() {
		String expression = txtExpression.getText();

		if (expression.isEmpty()) {
			printMessage("Expression is empty");
			return;
		}
		
		Number result = calculateExpression(expression);

		if (result == null) printMessage("Can not calculate expression", true);
		else {
			printMessage(result.toString());
			btnStore.setEnabled(true);
		}
	}
	
	public void validateSearch() {
		String search = txtSearch.getText();

		if (search.isEmpty()) {
			printMessage("Search expression is empty");
			btnSearch.setEnabled(false);
			return;
		}

		InputReader input = new InputReader(search);
		OperatorToken opToken = null;
		
		if (OperatorToken.isOperator(input)) {
			opToken = OperatorToken.getToken(OperatorToken.read(input));
		}

		Number result = calculateExpression(input, false);

		if (result == null) {
			printMessage("Search expression is incorrect");
			btnSearch.setEnabled(false);
		}
		else {
			printMessage(result.toString());
			btnSearch.setEnabled(true);
		}
	}
	
	public Number calculateExpression(String expression) {
		return calculateExpression(expression, true);
	}

	public Number calculateExpression(String expression, boolean verbose) {
		return calculateExpression(new InputReader(expression), verbose);
	}
	
	public Number calculateExpression(InputReader expression, boolean verbose) {
		RPNParser parser = new RPNParser(expression);
		try {
			return parser.rpnEvaluate();
		} catch (ParseException e) {
			if (verbose)
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
