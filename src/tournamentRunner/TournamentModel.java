package tournamentRunner;
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
	private int byeAdded;
	public TournamentModel(int num, int size, String name){		//Number, size of brackets, and tournament name
		int newSize;
		for(newSize=2;newSize<size;newSize*=2);
		int numByes = newSize - size;
		size = newSize;
		competitors = new Set[num];
		for(int i = 0; i < num; i++) competitors[i] = new TreeSet<Person>();
		tournamentName = name;
		Brackets.setBrackets(num,size);
		for(int bracket = 0;bracket<num;bracket++)
			for(int i = 0; i<numByes;i++)
				addPerson(new Bye(),bracket);
		setChanged();
		notifyObservers();
	}
	//Attempt to restore the state to the passed file, and throw an error on failure. The file format is specified in the manual
	public TournamentModel(File file, TournamentView view) throws FileNotFoundException {
		Scanner scan = new Scanner(file);
		int numBrack = Integer.parseInt(scan.nextLine());
		int brackSize = Integer.parseInt(scan.nextLine());
		tournamentName = scan.nextLine();
		Brackets.setBrackets(numBrack, brackSize);
		competitors = new Set[numBrack];
		for(int i = 0; i < numBrack; i++){
			competitors[i]=new TreeSet<Person>();
			Brackets.getBracket(i).setName(scan.nextLine());
			for(int j = 0; j < brackSize-1; j++){
				String matchLine = scan.nextLine();
				String[] data = matchLine.split(",");
				
				Brackets.getBracket(i).restoreState(data);
				for(int k = 0; k < 2; k++){
					if(!(data[k].equals(Match.DEFAULT_BLANK_NAME + "/" + Match.DEFAULT_BLANK_SCHOOL) || data[k].equals(Match.DEFAULT_WINNER_NAME + "/" + Match.DEFAULT_WINNER_SCHOOL))){		//Add people back to set
						competitors[i].add(new Person(data[k].substring(0, data[k].indexOf('/')), data[k].substring(data[k].indexOf('/')+1)));
					}
				}
			}
		}
		
		this.addObserver(view);
		scan.close();
		setChanged();
		notifyObservers();
	}
	/*
	 * Attempt to add the given person to the bracket with index [bracket]. If seeded, add to given seed, if unseeded, add from bottom to top
	 * 			If an equal person has already been added, or the bracket is full, return false
	 */
	public boolean addPerson(Person person, int bracket){
		return addPerson(person,bracket,-1);
	}
	
	public boolean addPerson(Person person, int bracket, int seed){
		if(competitors[bracket].add(person)){
			boolean result;
			if(seed!=-1){
				result = Brackets.getBracket(bracket).addPerson(person,seed);
			}
			else{
				seed = Brackets.getSize();
				while(seed > 0  && !Brackets.getBracket(bracket).addPerson(person,seed--));
				result = seed!=0;
			}
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
	public void advancePerson(Person person, int bracket, String notes, int matchID){
		Brackets.getBracket(bracket).recordWin(person, notes, matchID);
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
	//Set the match in [bracket] with id [index] to the data specified
	public void setMatch(Person left, Person right, Person winner, String notes, int bracket, int index){
		Brackets.getBracket(bracket).setMatch(left,right,winner,notes,index);
		
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

	//Add a roster from a certain school, using the convention specified in the manual
	public void addRoster(String school, File file) throws FileNotFoundException {
		Scanner scan = new Scanner(file);
		int num = Brackets.getNum();
		for(int i = 0; i < num && scan.hasNextLine(); i++){
			String name = scan.nextLine()+",";
			while(name.length()>0){
				addPerson(new Person(name.substring(0,name.indexOf(',')),school),i);
				name = name.substring(name.indexOf(',')+1);
			}
		}
		scan.close();
		setChanged();
		notifyObservers();
	}
}
