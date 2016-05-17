import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Scanner;
import java.util.Set;

import java.util.TreeSet;

public class TournamentModel extends Observable{
	private Set<Person>[] competitors;		//Each index is a bracket #
	private String tournamentName;
	public TournamentModel(int num, int size, String name){		//Number, size of brackets, plus if the brackets are double or single elim
		competitors = new Set[num];
		for(int i = 0; i < num; i++) competitors[i] = new TreeSet<Person>();
		tournamentName = name;
		Brackets.setBrackets(num,size);
		setChanged();
		notifyObservers();
	}
	public boolean addPerson(Person person, int bracket){
		if(competitors[bracket].add(person)){
			boolean result = Brackets.getBracket(bracket).addPerson(person);
			if(result){
				setChanged();
				notifyObservers();
			}
			return result;
		}
		return false;
	}
	public void advancePerson(Person person, int bracket, String notes){
		Brackets.getBracket(bracket).recordWin(person, notes);
		setChanged();
		notifyObservers();
	}
	public Set<Person> getCompetitors(int bracket){
		return competitors[bracket];
	}
	public int getNumBrackets(){return Brackets.getNum();}
	public void setName(String name){
		tournamentName = name;
	}
	public String getName(){
		return tournamentName;
	}
	public void restoreState(String[] data, int i){
		Brackets.getBracket(i).restoreState(data);
		setChanged();
		notifyObservers();
	}
	public void setBracketName(int index, String newName) {
		Brackets.getBracket(index).setName(newName);
		setChanged();
		notifyObservers();
	}
	public void writeFile(String currPathway) throws IOException {
			File newFile = new File(currPathway);
			FileWriter writer = new FileWriter(newFile);
			writer.write(Brackets.getNum() + "\n" + Brackets.getSize() + "\n" + getName() + "\n");
			for(int i=0;i<Brackets.getNum();i++){
				writer.write(Brackets.getBracket(i).getName() + "\n");
				writer.write(Brackets.getBracket(i).getInfo() + "\n");
			}
			writer.close();
	}
	public void restoreState(File file) throws FileNotFoundException {
		Scanner scan = new Scanner(file);
		int numBrack = Integer.parseInt(scan.nextLine());
		int brackSize = Integer.parseInt(scan.nextLine());
		tournamentName = scan.nextLine();
		Brackets.setBrackets(numBrack, brackSize);
		
		for(int i = 0; i < numBrack; i++){
			Brackets.getBracket(i).setName(scan.nextLine());
			for(int j = 0; j < brackSize-1; j++){
				String matchLine = scan.nextLine();
				String[] data = matchLine.split(",");
				restoreState(data,i);
				for(int k = 0; k < 2; k++){
					if(!data[k].equals("TBD/")){		//Add people back to set
						competitors[i].add(new Person(data[k].substring(0, data[k].indexOf('/')), data[k].substring(data[k].indexOf('/')+1)));
					}
				}
			}
		}
		
		scan.close();
		setChanged();
		notifyObservers();
	}
	
	public void startTournament(){
		int num = Brackets.getNum();
		for(int i = 0; i < num; i++){
			Bracket curr = Brackets.getBracket(i);
			while(curr.addPerson(new Bye()));
			curr.promoteBye();
		}
	}
}
