import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Scanner;
import java.util.Set;

import java.util.TreeSet;
/*
 * The central governing class of the program, holding state information about the brackets, names and competitors
 */
public class TournamentModel extends Observable{
	private Set<Person>[] competitors;		//Array of Sets of Person of added competitors
	private String tournamentName;
	public TournamentModel(int num, int size, String name){		//Number, size of brackets, and tournament name
		competitors = new Set[num];
		for(int i = 0; i < num; i++) competitors[i] = new TreeSet<Person>();
		tournamentName = name;
		Brackets.setBrackets(num,size);
		setChanged();
		notifyObservers();
	}
	/*
	 * Attempt to add the given person to the bracket with index [bracket]. 
	 * 			If an equal person has already been added, or the bracket is full, return false
	 */
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
	/*
	 * Attempt to advance the given person one match in the bracket. 
	 */
	public void advancePerson(Person person, int bracket, String notes){
		Brackets.getBracket(bracket).recordWin(person, notes);
		setChanged();
		notifyObservers();
	}
	/*
	 * Return a Set<Person> of all competitors in the specified bracket
	 */
	public Set<Person> getCompetitors(int bracket){
		return competitors[bracket];
	}
	//Set the tournament name
	public void setName(String name){
		tournamentName = name;
	}
	//Return the tournament name
	public String getName(){
		return tournamentName;
	}
	//Set the name of the bracket specified by [index] to the string [newName]
	public void setBracketName(int index, String newName) {
		Brackets.getBracket(index).setName(newName);
		setChanged();
		notifyObservers();
	}
	//Attempt to write all state to the file located at currPathway, and throw an error on failure. The file format is specified in the manual
	public void writeFile(File file) throws IOException {
			FileWriter writer = new FileWriter(file);
			writer.write(Brackets.getNum() + "\n" + Brackets.getSize() + "\n" + getName() + "\n");
			for(int i=0;i<Brackets.getNum();i++){
				writer.write(Brackets.getBracket(i).getName() + "\n");
				writer.write(Brackets.getBracket(i).getInfo() + "\n");
			}
			writer.close();
	}
	//Attempt to restore the state to the passed file, and throw an error on failure. The file format is specified in the manual
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
	private void restoreState(String[] data, int i){
		Brackets.getBracket(i).restoreState(data);
		setChanged();
		notifyObservers();
	}
	//Fill empty slots in the tournament with Byes, then advance all competitors until a match featuring no Byes is found
	public void startTournament(){
		int num = Brackets.getNum();
		for(int i = 0; i < num; i++){
			Bracket curr = Brackets.getBracket(i);
			while(curr.addPerson(new Bye()));
			curr.promoteBye();
		}
	}
}
