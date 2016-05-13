import java.awt.Container;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import javax.swing.*;

//import com.sun.glass.events.KeyEvent;

public class TournamentView extends JFrame implements Observer, ActionListener{
	TournamentModel model;
	

	public TournamentView(){		//Start popup with 
		Container pane = getContentPane();
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem newBracket = new JMenuItem("New Bracket");
		newBracket.addActionListener(this);
		file.add(newBracket);
		JMenuItem fromFile = new JMenuItem("Import from file");
		fromFile.addActionListener(this);
		file.add(fromFile);
		JMenu edit = new JMenu("Edit");
		JMenuItem add = new JMenuItem("Add Competitor");
		add.addActionListener(this);
		edit.add(add);
		bar.add(file);
		bar.add(edit);






		//GUI FILLER
		pane.add(new JLabel("HELLO WORLD"));
		super.setJMenuBar(bar);
		for(int i=0;i<super.getJMenuBar().getMenu(1).getItemCount();i++)
			super.getJMenuBar().getMenu(1).getItem(i).setEnabled(false);
		setTitle("Tournament Runner");
		setSize(700,400);
		setVisible(true);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void createNewBracket(){
		/*
		 * Startup prompt for number and size of brackets
		 */
		String numBrackets = JOptionPane.showInputDialog(this,
				"Enter number of brackets", null);
		if(numBrackets!=null){
			try{
				Integer intBracketNum = Integer.parseInt(numBrackets);
				if(intBracketNum < 1)
					JOptionPane.showMessageDialog(this, "Error: A new tournament must contain at least 1 bracket", "Whoops!", JOptionPane.ERROR_MESSAGE);
				else{
					String bracketSize = JOptionPane.showInputDialog(this, "Enter size of each bracket", null);
					Integer intBracketSize = Integer.parseInt(bracketSize);
					if(intBracketSize < 2)
						JOptionPane.showMessageDialog(this, "Error: Each bracket must contain at least 2 competitors", "Whoops!", JOptionPane.ERROR_MESSAGE);
					else{
						model = new TournamentModel(intBracketNum,intBracketSize);
						model.addObserver(this);
						for(int i=0;i<super.getJMenuBar().getMenu(1).getItemCount();i++)
							super.getJMenuBar().getMenu(1).getItem(i).setEnabled(true);
					}
				}
			}
			catch(NumberFormatException e){
				JOptionPane.showMessageDialog(this, "Error: Invalid Input", "Whoops!", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {}//If canceled out, load file?
	}

	private void addNewPerson(){
		if(model == null)
			return;
		String name = JOptionPane.showInputDialog(this, "Enter the name of the new person");
		if(name == null || name.equals("")){
			JOptionPane.showMessageDialog(this, "Error: A competitor's name is required");
		}
		else{
			String school = JOptionPane.showInputDialog(this, "Enter the school of the new person");
			if(school == null || school.equals(""))
				school = "Unaffiliated";
			model.addPerson(new Person(name, school), 0);
		}
	}
	
	private void newBracketFromFile(){
		String pathName = System.getProperty("user.dir") + "/";
		JFileChooser fileChooser = new JFileChooser(pathName);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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
	
	public void update(Observable arg0, Object arg1) {
		//UPDATE BRACKETS
	}
	

	public static void main(String[] args){
		TournamentView view = new TournamentView();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(((AbstractButton)arg0.getSource()).getText().equals("New Bracket"))
			createNewBracket();
		else if(((AbstractButton)arg0.getSource()).getText().equals("Add Competitor"))
			addNewPerson();
		else if(((AbstractButton)arg0.getSource()).getText().equals("Import from file"))
			newBracketFromFile();
	}
}
