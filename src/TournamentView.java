import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;


public class TournamentView extends JFrame implements Observer, ActionListener{
	private static final String 
	ERROR_NAME_NOT_FOUND = "Error: No bracket with specified name found", 
	ERROR_NO_BRACKET_SPECIFIED = "Error: No bracket specified";
	TournamentModel model;
	private final JMenuItem newBracket, fromFile, save, saveAs, add, changeName, zoomIn, zoomOut, addRoster, print;
	private JTextField name2, school2, tourName, numBrack, brackSize;
	private JComboBox<String> bracketOptions;
	private JButton confirmAdd, cancelAdd, confirmNew, cancelNew;
	private String currPathway;
	private JTabbedPane brackets;
	private JDialog dialogAdd, dialogNew;
	private JCheckBox noSchool;
	private BracketPanel currPanel;


	public TournamentView(){//Start popup with
		currPathway = null; 
		Container pane = getContentPane();
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		newBracket = new JMenuItem("New Bracket");
		addCtrlKey(newBracket, 'N');
		newBracket.addActionListener(this);
		file.add(newBracket);
		fromFile = new JMenuItem("Open");
		addCtrlKey(fromFile,'O');
		fromFile.addActionListener(this);
		file.add(fromFile);
		save = new JMenuItem("Save");
		addCtrlKey(save,'S');
		save.addActionListener(this);
		file.add(save);
		saveAs = new JMenuItem("Save As");
		saveAs.addActionListener(this);
		file.add(saveAs);
		JMenu edit = new JMenu("Edit");
		add = new JMenuItem("Add Competitor");
		addCtrlKey(add,'A');
		add.addActionListener(this);
		print = new JMenuItem("Print");
		addCtrlKey(print,'P');
		print.addActionListener(this);
		file.add(print);
		edit.add(add);
		addRoster = new JMenuItem("Add Team Roster");
		addCtrlKey(addRoster,'T');
		addRoster.addActionListener(this);
		edit.add(addRoster);
		changeName = new JMenuItem("Change Bracket Name");
		addCtrlKey(changeName,'R');
		changeName.addActionListener(this);
		edit.add(changeName);
		JMenu view = new JMenu("View");
		zoomIn = new JMenuItem("Zoom In");
		zoomOut = new JMenuItem("Zoom Out");
		addCtrlKey(zoomIn,'=');
		addCtrlKey(zoomOut,'-');
		zoomIn.addActionListener(this);
		zoomOut.addActionListener(this);
		view.add(zoomIn);
		view.add(zoomOut);
		bar.add(file);
		bar.add(edit);
		bar.add(view);
		update(null,null);
		brackets = new JTabbedPane();
		brackets.setTabPlacement(JTabbedPane.LEFT);
		pane.add(brackets);
		super.setJMenuBar(bar);
		setTitle("Tournament Runner");
		setSize(1600,800);
		setVisible(true);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void addCtrlKey(JMenuItem item, char c) {
		item.setAccelerator(KeyStroke.getKeyStroke(c,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	private void createNewBracket(){
		try{
			int num = Integer.parseInt(numBrack.getText()), size = Integer.parseInt(brackSize.getText());
			String name = tourName.getText();
			if(num < 1){ 
				JOptionPane.showMessageDialog(this, "Error: A new tournament must contain at least 1 bracket", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
			else if(size<=4) size = 8;
			model = new TournamentModel(num,size,name);
			model.addObserver(this);
			update(model,null);
		}
		catch(NumberFormatException e){
			JOptionPane.showMessageDialog(this, "Error: Invalid Input", "Whoops!", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void newTournamentWindow(){
		tourName = new JTextField("Tournament Name");
		numBrack = new JTextField("Number of Brackets");
		brackSize = new JTextField("Bracket Size");
		tourName.setColumns(15);
		numBrack.setColumns(10);
		brackSize.setColumns(10);
		confirmNew = new JButton("Confirm");
		confirmNew.addActionListener(this);
		confirmNew.setEnabled(false);
		cancelNew = new JButton("Cancel");
		cancelNew.addActionListener(this);
		cancelNew.setEnabled(false);
		DocumentListener docListen = new DocumentListener(){
			public void changedUpdate(DocumentEvent e){
				enableButtons();
			}
			public void removeUpdate(DocumentEvent e){
				enableButtons();
			}
			public void insertUpdate(DocumentEvent e){
				enableButtons();
			}
			private void enableButtons(){
				if(tourName.getText().equals("Tournament Name") || tourName.getText().equals("") || 
						numBrack.getText().equals("Number of Brackets") || numBrack.getText().equals("") ||
							brackSize.getText().equals("Bracket Size") || brackSize.getText().equals("")){
					confirmNew.setEnabled(false);
					cancelNew.setEnabled(false);
				}
				else{
					confirmNew.setEnabled(true);
					cancelNew.setEnabled(true);
				}
			}
		};
		tourName.getDocument().addDocumentListener(docListen);
		numBrack.getDocument().addDocumentListener(docListen);
		brackSize.getDocument().addDocumentListener(docListen);
		dialogNew = new JDialog(this, "New Tournament");
		dialogNew.setLayout(new GridLayout(4,1));
		dialogNew.setSize(600,300);
		JPanel buttons = new JPanel();
		buttons.add(confirmNew);
		buttons.add(cancelNew);
		
		dialogNew.add(tourName);
		dialogNew.add(numBrack);
		dialogNew.add(brackSize);
		dialogNew.add(buttons);
		Toolkit tlkt = Toolkit.getDefaultToolkit();
		dialogNew.setLocation((int)(tlkt.getScreenSize().getWidth()-dialogNew.getWidth())/2,(int)(tlkt.getScreenSize().getHeight()-dialogNew.getHeight())/2);
		
		dialogNew.setModal(true);
		dialogNew.setResizable(false);
		dialogNew.setAlwaysOnTop(true);
		dialogNew.setVisible(true);
		dialogNew.pack();
	}

	private void addPersonWindow(){
		if(model == null)
			return;
		name2 = new JTextField("Name");
		name2.setColumns(10);
		school2 = new JTextField("School");
		school2.setColumns(10);
		String[] brackets = new String[Brackets.getNum()];
		for(int i=0;i<brackets.length;i++)
			brackets[i] = Brackets.getBracket(i).getName();
		bracketOptions = new JComboBox<String>(brackets);
		confirmAdd = new JButton("Add");
		confirmAdd.addActionListener(this);
		confirmAdd.setEnabled(false);
		cancelAdd = new JButton("Cancel");
		cancelAdd.addActionListener(this);
		cancelAdd.setEnabled(false);
		noSchool = new JCheckBox("Unaffiliated");
		noSchool.addActionListener(this);
		DocumentListener docListen = new DocumentListener(){
			public void changedUpdate(DocumentEvent e){
				enableButtons();
			}
			public void removeUpdate(DocumentEvent e){
				enableButtons();
			}
			public void insertUpdate(DocumentEvent e){
				enableButtons();
			}
			private void enableButtons(){
				if(name2.getText().equals("Name") || name2.getText().equals("") || ((school2.getText().equals("School") || school2.getText().equals("")) && !noSchool.isSelected())){
					confirmAdd.setEnabled(false);
					cancelAdd.setEnabled(false);
				}
				else{
					confirmAdd.setEnabled(true);
					cancelAdd.setEnabled(true);
				}
			}
		};
		name2.getDocument().addDocumentListener(docListen);
		school2.getDocument().addDocumentListener(docListen);
		dialogAdd = new JDialog(this, "Add new competitor");
		dialogAdd.setLayout(new GridLayout(3,1));
		dialogAdd.setSize(600,300);
		JPanel options = new JPanel();
		options.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridheight = 10;
		constraints.gridwidth = dialogAdd.getWidth();
		constraints.fill = GridBagConstraints.BOTH;
		options.add(name2);
		options.add(school2);
		options.add(noSchool);
		JPanel bracketSelect = new JPanel();
		bracketSelect.setLayout(new GridBagLayout());
		bracketSelect.add(new JLabel("Select bracket: "));
		bracketSelect.add(bracketOptions);
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridBagLayout());
		buttons.add(confirmAdd);
		buttons.add(cancelAdd);
		Toolkit tlkt = Toolkit.getDefaultToolkit();
		dialogAdd.add(options);
		dialogAdd.add(bracketSelect);
		dialogAdd.add(buttons);
		dialogAdd.setModal(true);
		dialogAdd.setResizable(false);
		dialogAdd.setLocation((int)(tlkt.getScreenSize().getWidth()-dialogAdd.getWidth())/2,(int)(tlkt.getScreenSize().getHeight()-dialogAdd.getHeight())/2);
		dialogAdd.setAlwaysOnTop(true);
		dialogAdd.setVisible(true);
		dialogAdd.pack();
	}

	//Add "which bracket" w/ dropdown menu for names of each

	private void addPerson() {
		String name = name2.getText();
		String school = school2.getText();
		String bracket = (String)bracketOptions.getSelectedItem();
		int brackNum = findBrackNum(bracket);
		if(noSchool.isSelected())
			school = "Unaffiliated";
		if(name.equals(""))
			JOptionPane.showMessageDialog(this, "Error: Please enter a name");
		else if(name.equals("TBD"))
			JOptionPane.showMessageDialog(this, "Error: Invalid name");
		else if(bracket.equals(""))
			JOptionPane.showMessageDialog(this, ERROR_NO_BRACKET_SPECIFIED);
		else if(brackNum==-1)
			JOptionPane.showMessageDialog(this, ERROR_NAME_NOT_FOUND);
		else addPerson(name,school,brackNum);
	}
	private void addPerson(String name, String school, int bracket){	
		String bracketName = Brackets.getBracket(bracket).getName();
		boolean success = model.addPerson(new Person(name, school),bracket);
		if(success){
			JOptionPane.showMessageDialog(this, name + " from " + school + " successfully added to bracket " + bracketName);
		}
		else JOptionPane.showMessageDialog(this, "Error: Could not add to bracket " + bracketName);
		update(model, null);	
	}
	private int findBrackNum(String bracketName){
		for(int i = 0; i < Brackets.getNum(); i++)
			if(Brackets.getBracket(i).getName().equals(bracketName)) return i;
		return -1;
	}
	
	public void setMatch(Person p1, Person p2, Person win, String notes, int bracket, int index){
		model.setMatch(p1,p2,win,notes,bracket,index);
	}
	
	public void promotePerson(Person person, int bracket, String notes, int matchID){
		model.advancePerson(person, bracket, notes, matchID);
	}
	
	private void newBracketFromFile(){
		String pathName = System.getProperty("user.dir") + "/";
		JFileChooser fileChooser = new JFileChooser(pathName);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter(null, "bracket"));
		int result = fileChooser.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			try{
				model = new TournamentModel(file,this);
				update(model,null);
			}
			catch(FileNotFoundException e){
				JOptionPane.showMessageDialog(this, "Error: Invalid File", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void addRoster(){
		String school = JOptionPane.showInputDialog(this, "Enter the team name");
		if(school==null||school.equals("")) return;
		if(school.equals(Match.DEFAULT_BLANK_SCHOOL) || school.equals(Match.DEFAULT_WINNER_SCHOOL)){ 
			JOptionPane.showMessageDialog(this, "Error: Invalid school name");
			return;
		}
		JOptionPane.showMessageDialog(this, "Select the text file containing names, with one name per line and per bracket");
		String pathName = System.getProperty("user.dir") + "/";
		JFileChooser fileChooser = new JFileChooser(pathName);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter(null, "txt"));
		int result = fileChooser.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			try {
				model.addRoster(school, file);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "Error: Invalid File", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void loadTabs() {
		brackets.removeAll();
		for(int i = 0; i < Brackets.getNum(); i++){
			JScrollPane scroll = new JScrollPane(new BracketPanel(Brackets.getBracket(i),this));
			brackets.addTab(Brackets.getBracket(i).getName(), scroll);
		}
		currPanel = new BracketPanel(Brackets.getBracket(brackets.getSelectedIndex()),this);
		JScrollPane pane = new JScrollPane(currPanel);
		pane.setFocusable(true);
		brackets.setComponentAt(brackets.getSelectedIndex(), pane);
	}

	private void changeBracketName() {
		String name = JOptionPane.showInputDialog(this, "Enter the bracket to rename");
		if(name==null||name.equals("")) return;
		int index = findBrackNum(name);
		if(index==-1){ 
			JOptionPane.showMessageDialog(this, "Error: Bracket name not found");
			return;
		}
		String newName = JOptionPane.showInputDialog(this, "Enter the new name");
		if(newName==null||newName.equals("")) return;
		model.setBracketName(index,newName);
	}

	private void save(){
		if(currPathway == null)
			saveAs();
		else{
			try {
				model.writeFile(new File(currPathway));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "An error has occured, the file has not been saved", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void saveAs(){
		String fileName = JOptionPane.showInputDialog(this, "Save as:");
		if(fileName != null){
			if(fileName.contains(".") || fileName.contains("\\") || fileName.equals(""))
				JOptionPane.showMessageDialog(this, "Error: Invalid file name", "Whoops!", JOptionPane.ERROR_MESSAGE);
			else{
				JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION){
					File dir = fileChooser.getSelectedFile();
					currPathway = dir.getAbsolutePath() + "/" + fileName + ".bracket";
					try {
						model.writeFile(new File(currPathway));
					} catch (IOException e) {
						JOptionPane.showMessageDialog(this, "An error has occured, the file has not been saved", "Whoops!", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	public void update(Observable arg0, Object arg1) {
		if(model == null){
			add.setEnabled(false);
			save.setEnabled(false);
			saveAs.setEnabled(false);
			changeName.setEnabled(false);
			zoomIn.setEnabled(false);
			zoomOut.setEnabled(false);
			addRoster.setEnabled(false);
			print.setEnabled(false);
			setTitle("Tournament Runner");
		}
		else{
			loadTabs();
			setTitle(((TournamentModel) arg0).getName());
			add.setEnabled(true);
			save.setEnabled(true);
			saveAs.setEnabled(true);
			changeName.setEnabled(true);
			currPanel.paint(this.getGraphics());
			zoomIn.setEnabled(true);
			zoomOut.setEnabled(true);
			addRoster.setEnabled(true);
			print.setEnabled(true);
		}
	}
	private void print(){
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(new BracketPrinter(new BracketPanel(Brackets.getBracket(brackets.getSelectedIndex()), this)));
		boolean doPrint = job.printDialog();
		if(doPrint){
			try{
				job.print();
			}
			catch(PrinterException e){
				JOptionPane.showMessageDialog(this, "Error: Failed to Print", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static void main(String[] args){
		TournamentView view = new TournamentView();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if(source == newBracket)
			newTournamentWindow();
		else if(source == add)
			addPersonWindow();
		else if(source == fromFile)
			newBracketFromFile();
		else if(source == save)
			save();
		else if(source == saveAs)
			saveAs();
		else if(source == changeName)
			changeBracketName();
		else if(source == confirmAdd){
			dialogAdd.dispose();
			addPerson();
		}
		else if(source == confirmNew){
			dialogNew.dispose();
			createNewBracket();
		}
		else if(source == addRoster){
			addRoster();
		}
		else if(source == cancelAdd){
			dialogAdd.dispose();
		}
		else if(source == cancelNew){
			dialogNew.dispose();
		}
		else if(source == noSchool){
			if(name2.getText().equals("Name") || name2.getText().equals("") || ((school2.getText().equals("School") || school2.getText().equals("")) && !noSchool.isSelected())){
				confirmAdd.setEnabled(false);
				cancelAdd.setEnabled(false);
			}
			else{
				confirmAdd.setEnabled(true);
				cancelAdd.setEnabled(true);
			}
			if(noSchool.isSelected()){
				school2.setText("Unaffiliated");
				school2.setEditable(false);
			}
			else{
				school2.setText("School");
				school2.setEditable(true);
			}
		}
		else if(source == zoomIn){
			BracketPanel.zoomIn();
			update(model,null);
		}
		else if(source == zoomOut){
			BracketPanel.zoomOut();
			update(model,null);
		}
		else if(source == print){
			print();
		}
	}
}
