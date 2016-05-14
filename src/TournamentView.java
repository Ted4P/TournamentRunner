import java.awt.Container;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TournamentView extends JFrame implements Observer, ActionListener{
	TournamentModel model;
	private final JMenuItem newBracket, fromFile, save, saveAs, add;
	private String currPathway;
	public TournamentView(){//Start popup with
		currPathway = null; 
		Container pane = getContentPane();
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		newBracket = new JMenuItem("New Bracket");
		newBracket.addActionListener(this);
		file.add(newBracket);
		fromFile = new JMenuItem("Open");
		fromFile.addActionListener(this);
		file.add(fromFile);
		save = new JMenuItem("Save");
		save.addActionListener(this);
		file.add(save);
		saveAs = new JMenuItem("Save As");
		saveAs.addActionListener(this);
		file.add(saveAs);
		JMenu edit = new JMenu("Edit");
		add = new JMenuItem("Add Competitor");
		add.addActionListener(this);
		edit.add(add);
		bar.add(file);
		bar.add(edit);
		update(null,null);
		//GUI FILLER
		pane.add(new JLabel("HELLO WORLD"));
		super.setJMenuBar(bar);
		setTitle("Tournament Runner");
		setSize(700,400);
		setVisible(true);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void createNewBracket(){
		/*
		 * Startup prompt for number and size of brackets
		 */
		String currPathway = null;
		String numBrackets = JOptionPane.showInputDialog(this,
				"Enter number of brackets", null);
		if(numBrackets!=null){
			try{
				Integer intBracketNum = Integer.parseInt(numBrackets);
				if(intBracketNum < 1)
					JOptionPane.showMessageDialog(this, "Error: A new tournament must contain at least 1 bracket", "Whoops!", JOptionPane.ERROR_MESSAGE);
				else{
					String bracketSize = JOptionPane.showInputDialog(this, "Enter size of each bracket", null);
					if(bracketSize != null){
						Integer intBracketSize = Integer.parseInt(bracketSize);
						if(intBracketSize < 2)
							JOptionPane.showMessageDialog(this, "Error: Each bracket must contain at least 2 competitors", "Whoops!", JOptionPane.ERROR_MESSAGE);
						else{
							model = new TournamentModel(intBracketNum,intBracketSize, "New Tournament");
							model.addObserver(this);
							update(null,null);
						}
					}
				}
			}
			catch(NumberFormatException e){
				JOptionPane.showMessageDialog(this, "Error: Invalid Input", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void addNewPerson(){
		if(model == null)
			return;
		String name = JOptionPane.showInputDialog(this, "Enter the name of the new person");
		if(name == null || name.equals(""))
			return;
		else if(name.equals("TBD"))
			JOptionPane.showMessageDialog(this, "Error: Invalid name");
		else{
			String school = JOptionPane.showInputDialog(this, "Enter the school of the new person");
			if(school == null)
				return;
			else if(school.equals(""))
				school = "Unaffiliated";
			
			//Add "which bracket" w/ dropdown menu for names of each
		}
		//System.out.println(Brackets.getBracket(0).getInfo());
	}

	private void newBracketFromFile(){
		String pathName = System.getProperty("user.dir") + "/";
		JFileChooser fileChooser = new JFileChooser(pathName);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter(null, ".bracket"));
		int result = fileChooser.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			try{
				Scanner scan = new Scanner(file);
				/*Create new bracket based off of a text file, exact format to be specified later*/
			}
			catch(FileNotFoundException e){
				JOptionPane.showMessageDialog(this, "Error: Invalid File", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void save(){
		if(currPathway == null)
			saveAs();
		else{
			writeFile();
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
				}
			}
		}
	}

	public void writeFile(){
		try{
			File newFile = new File(currPathway);
			FileWriter writer = new FileWriter(newFile);
			writer.write(Brackets.getNum() + "\n" + Brackets.getSize() + "\n");
			for(int i=0;i<Brackets.getNum();i++)
				writer.write(Brackets.getBracket(i).getInfo() + "\n");
			writer.close();
			model.setName(currPathway);
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(this, "An error has occured, the file has not been saved", "Whoops!", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void update(Observable arg0, Object arg1) {
		if(model == null){
			add.setEnabled(false);
			save.setEnabled(false);
			saveAs.setEnabled(false);
			setTitle("Tournament Runner");
		}
		else{
			setTitle(model.getName());
			add.setEnabled(true);
			save.setEnabled(true);
			saveAs.setEnabled(true);
		}
	}


	public static void main(String[] args){
		TournamentView view = new TournamentView();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if(source == newBracket)
			createNewBracket();
		else if(source == add)
			addNewPerson();
		else if(source == fromFile)
			newBracketFromFile();
		else if(source == save){
			save();
		}
		else if(source == saveAs)
			saveAs();
	}
}
