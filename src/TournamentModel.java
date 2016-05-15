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
}
