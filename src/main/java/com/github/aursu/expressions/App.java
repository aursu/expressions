package com.github.aursu.expressions;

import java.awt.EventQueue;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.Document;

import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.SwingConstants;
import java.awt.GridBagConstraints;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class App extends JFrame {
	
	// data table columns
	public static final String[] columnIdentifiers = { "Polish", "Infix", "Value" };

	// UI
	private JPanel contentPane;

	// expression parser interface
	private JTextField txtExpression;
	private JTextArea textArea;
	private Expression expression; // expression parser

	// Database connection interface
	private JButton btnStore;
	private JButton btnLoad;
	private JTextField txtDBHost;
	private JTextField txtDBUser;
	private JPasswordField passwordField;
	private JTextField txtDBName;

	private String dbHost, dbUser, dbPassword, dbName;

	// Database storage interface
	private JTable table;
	private Vector<String> columnNames = new Vector<>();
	private DBDriver dbDriver = DBDriver.MYSQL;	
	private ExpressionStorage storage = null;

	// search interface
	private JButton btnSearch;
	private JTextField txtSearch;

	OperatorToken srchToken = null;
	double srchLookup;
	
	// print errors interface
	private ErrorMessenger errors = new ErrorMessenger();

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
		contentGUI();
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
		ActionListener storeData = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				storeExpression();
			}
		};

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
		
		btnStore.addActionListener(storeData);

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

		ActionListener driverSelect = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(e.getActionCommand()) {
					case "PostgreSQL":
						setDBDriver(DBDriver.POSTGRES);
						break;
					default:
						setDBDriver(DBDriver.MYSQL);
				}
			}
		};

		JPanel databaseBox = new JPanel();
		databaseBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		GridBagLayout gbl_databaseBox = new GridBagLayout();
		gbl_databaseBox.columnWeights = new double[]{0.0, 0.5, 1.0};
		databaseBox.setLayout(gbl_databaseBox);

		txtDBHost = new JTextField();
		txtDBHost.setText("localhost");
		txtDBHost.setToolTipText("Enter database host");
		
		Document docDBHost = txtDBHost.getDocument();
	    if (docDBHost != null) docDBHost.addDocumentListener(credsChange);
		
		JRadioButton rdbtnMySQL = new JRadioButton("MySQL");
		rdbtnMySQL.setSelected(true);
		rdbtnMySQL.setActionCommand("MySQL");
		rdbtnMySQL.addActionListener(driverSelect);
		GridBagConstraints gbc_rdbtnMySQL = new GridBagConstraints();
		gbc_rdbtnMySQL.anchor = GridBagConstraints.WEST;
		gbc_rdbtnMySQL.gridx = 1;
		gbc_rdbtnMySQL.gridy = 0;
		databaseBox.add(rdbtnMySQL, gbc_rdbtnMySQL);

		JRadioButton rdbtnPostgres = new JRadioButton("PostgreSQL");
		rdbtnPostgres.setEnabled(false);
		rdbtnPostgres.setSelected(false);
		rdbtnPostgres.setActionCommand("PostgreSQL");
		rdbtnPostgres.addActionListener(driverSelect);
		GridBagConstraints gbc_rdbtnPostgres = new GridBagConstraints();
		gbc_rdbtnPostgres.anchor = GridBagConstraints.WEST;
		gbc_rdbtnPostgres.gridx = 2;
		gbc_rdbtnPostgres.gridy = 0;
		databaseBox.add(rdbtnPostgres, gbc_rdbtnPostgres);

		ButtonGroup dbType = new ButtonGroup();
    	dbType.add(rdbtnMySQL);
    	dbType.add(rdbtnPostgres);

		JLabel lblDBHost = new JLabel("Database Host");
		lblDBHost.setLabelFor(txtDBHost);

		GridBagConstraints gbc_lblDBHost = new GridBagConstraints();
		gbc_lblDBHost.anchor = GridBagConstraints.WEST;
		gbc_lblDBHost.gridx = 0;
		gbc_lblDBHost.gridy = 1;
		databaseBox.add(lblDBHost, gbc_lblDBHost);

		GridBagConstraints gbc_txtDBHost = new GridBagConstraints();
		gbc_txtDBHost.anchor = GridBagConstraints.WEST;
		gbc_txtDBHost.gridx = 1;
		gbc_txtDBHost.gridy = 1;
		gbc_txtDBHost.gridwidth = 2;
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
		gbc_lblDBUser.gridy = 2;
		databaseBox.add(lblDBUser, gbc_lblDBUser);

		GridBagConstraints gbc_txtDBUser = new GridBagConstraints();
		gbc_txtDBUser.anchor = GridBagConstraints.WEST;
		gbc_txtDBUser.gridx = 1;
		gbc_txtDBUser.gridy = 2;
		gbc_txtDBUser.gridwidth = 2;
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
		gbc_lblDBPassword.gridy = 3;
		databaseBox.add(lblDBPassword, gbc_lblDBPassword);

		GridBagConstraints gbc_pwdDBPassword = new GridBagConstraints();
		gbc_pwdDBPassword.anchor = GridBagConstraints.WEST;
		gbc_pwdDBPassword.gridx = 1;
		gbc_pwdDBPassword.gridy = 3;
		gbc_pwdDBPassword.gridwidth = 2;
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
		gbc_lblDBName.gridy = 4;
		databaseBox.add(lblDBName, gbc_lblDBName);

		GridBagConstraints gbc_txtDBName = new GridBagConstraints();
		gbc_txtDBName.anchor = GridBagConstraints.WEST;
		gbc_txtDBName.gridx = 1;
		gbc_txtDBName.gridy = 4;
		gbc_txtDBName.gridwidth = 2;
		gbc_txtDBName.fill = GridBagConstraints.HORIZONTAL;
		databaseBox.add(txtDBName, gbc_txtDBName);
		
		contentPane.add(databaseBox);
	}
	
	private void contentGUI() {
		ActionListener loadData = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadContent();
			}
		};

		JPanel contentBox = new JPanel();
		contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.LINE_AXIS));
		contentBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JScrollPane contentScrollPane = new JScrollPane();
		contentScrollPane.setAlignmentY(Component.TOP_ALIGNMENT);
		contentScrollPane.setMinimumSize(new Dimension(0, 100));

		Collections.addAll(columnNames, App.columnIdentifiers);

		Vector<Vector<Object>> rowData = new Vector<>();
		TableModel model = new DefaultTableModel(rowData, columnNames) {
			// cells in our table are not editable
		    public boolean isCellEditable(int row, int column)
		    {
		    	return false;
		    }
		};
		
		table = new JTable(model);
		
		table.setFillsViewportHeight(true);
		contentScrollPane.setViewportView(table);

		contentBox.add(contentScrollPane);

		btnLoad = new JButton("Load");
		btnLoad.setEnabled(false);
		btnLoad.setAlignmentY(Component.TOP_ALIGNMENT);
		btnLoad.setPreferredSize(new Dimension(100, 29));
		btnLoad.addActionListener(loadData);

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

	protected void setDBDriver(DBDriver drv) {
		dbDriver = drv;
	}

	// store expression into database
	protected void storeExpression() {
		connectDatabase();

		if (storage.checkConnection())
			try {
				if (expression.isValid()) {
					String rpn   = expression.rpn(),
						   infix = expression.infix();
					double value = expression.value();

					storage.store(rpn, infix, value);

					setMessage(String.format("Stored: %s, %s, %f", rpn, infix, value));
				}
			} catch (SQLException e) {
				printStackTrace(e);
			}
	}
	
	protected void loadContent() {
		connectDatabase();
		
		Vector<Vector<Object>> rowData = new Vector<>();

		try {
			rowData = storage.load();
		} catch (SQLException e) {
			printStackTrace(e);
		}

		if (rowData.isEmpty()) return;

		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setDataVector(rowData, columnNames);
	}

	// search expression inside database
	public void searchExpression() {
		connectDatabase();

		Vector<Vector<Object>> rowData = new Vector<>();

		try {
			rowData = storage.lookup(srchLookup, srchToken);
		} catch (SQLException e) {
			printStackTrace(e);
		}

		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setDataVector(rowData, columnNames);
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

	    if (InetAddressValidator.getInstance().isValid(hostName) || DomainValidator.getInstance(true).isValid(hostName)) {
	    	// setup credentials into storage object
			if (storage == null) storage = new ExpressionStorage(dbName, dbHost, dbUser, dbPassword);
			else storage.setup(dbName, dbHost, dbUser, dbPassword);

			return true;
	    }

		return false;
	}
	
	private void connectDatabase() {
		if (setupDBCredentials()) {

			// check if connection established already with proper credentials
			if (storage.checkConnection()) return;

			// close current connection (check above is failed) and setup  new credentials
			if (storage.isConnected())
				storage.close();

			// connect to database
			switch (dbDriver) {
				case MYSQL:
					storage.connectMySQL();
				case POSTGRES:
					// TODO: implement it
				default:
					break;	
			}
		}
	}

	protected void checkExpression() {
		// get expression from UI
		String exprtxt = txtExpression.getText();

		if (exprtxt.isEmpty()) {
			setMessage("Expression is empty");
			return;
		}

		// create new parser for expression evaluation
		expression = new Expression(exprtxt);

		// add parser errors (stack trace) into output
		boolean verbose = true;
	
		// evaluate expression with parser
		Number result = calculateExpression(expression, verbose);
		
		if (result == null)
			addMessage("Can not calculate expression");
		else {
			setMessage(String.format("Expression evaluation: %s", result.toString()));

			if (setupDBCredentials()) btnStore.setEnabled(true);
			else btnStore.setEnabled(false);
		}
	}
	
	public void validateSearch() {
		String search = txtSearch.getText();

		if (search.isEmpty()) {
			setMessage("Search expression is empty");
			btnSearch.setEnabled(false);
			return;
		}

		InputReader input = new InputReader(search);
		OperatorToken opToken = null;

		if (OperatorToken.isOperator(input)) {
			opToken = OperatorToken.getToken(OperatorToken.read(input));
		}

		boolean verbose = false;
		Number result = calculateExpression(input, verbose);

		if (result == null) {
			setMessage("Search expression is incorrect");
			btnSearch.setEnabled(false);
		}
		else {
			setMessage();
			srchToken = opToken;
			srchLookup = result.doubleValue(); 
			btnSearch.setEnabled(true);
		}
	}

	public Number calculateExpression(String expression) {
		return calculateExpression(expression, true);
	}

	public Number calculateExpression(String expression, boolean verbose) {
		return calculateExpression(new InputReader(expression), verbose);
	}

	// parse and calculate expression from InputReader
	public Number calculateExpression(InputReader input, boolean verbose) {
		Expression expression = new Expression(input);
		return calculateExpression(expression, verbose);
	}

	// parser evaluation
	public Number calculateExpression(Expression expression, boolean verbose) {		
		if (expression.isValid()) return expression.value();
		else {
			if (verbose)
				setMessage(expression.stackTrace());
			return null;
		}
	}

	public void printMessage(String msg, boolean add) {
		String msgnl = String.format("%s\n", msg);
		if (add) {
			textArea.append(msgnl);
		}
		else {
			textArea.setText(msgnl);
		}
	}

	private void setMessage() {
		textArea.setText(null);
	}

	public void setMessage(String msg) {
		printMessage(msg, false);
	}
	
	public void addMessage(String msg) {
		printMessage(msg, true);
	}

	private void printStackTrace(Throwable e) {
		errors.process(e);
		setMessage(errors.stack());
	}
}
