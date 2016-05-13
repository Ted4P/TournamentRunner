import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.TreeMap;
/*Tournament.java is runnable, main method asks what tournament to create, then calls TournamentModel constructor 
 * TournamentModel is the brains of the class, 
 * 			calls singleton class Brackets with # of brackets, size and type (hold info abt bracket)
 * TournamentView displays the bracket, and can tell the Model the results of matches
 */
import java.util.TreeSet;

public class TournamentModel extends Observable{
	private Set<Person>[] competitors;		//Each index is a bracket #
	
	public void setup(int num, int size){		//If dflt no param constructor, init brackets
		competitors = new Set[num];
		
		for(int i = 0; i < num; i++) competitors[i] = new TreeSet<Person>();
		
		Brackets.setBrackets(num,size);
		setChanged();
		notifyObservers();
	}
	public TournamentModel(int num, int size){		//Number, size of brackets, plus if the brackets are double or single elim
		Brackets.setBrackets(num,size);
		setChanged();
		notifyObservers();
	}
	public void addPerson(Person person, int bracket){
		competitors[bracket].add(person);
		Brackets.getBracket(bracket).addPerson(person);
		setChanged();
		notifyObservers();
	}
	public void advancePerson(Person person, int bracket, String notes){
		Brackets.getBracket(bracket).recordWin(person, notes);
	}
	public Set<Person> getCompetitors(int bracket){
		return competitors[bracket];
	}
	public int getNumBrackets(){return Brackets.getNum();}
}
