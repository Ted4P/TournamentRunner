import java.util.Observable;
/*Tournament.java is runnable, main method asks what tournament to create, then calls TournamentModel constructor 
 * TournamentModel is the brains of the class, 
 * 			calls singleton class Brackets with # of brackets, size and type (hold info abt bracket)
 * TournamentView displays the bracket, and can tell the Model the results of matches
 */

public class TournamentModel extends Observable{
	public TournamentModel(int num, int size, boolean doubleElim){		//Number, size of brackets, plus if the brackets are double or single elim
		Brackets.setBrackets(num,size,doubleElim);
		setChanged();
		notifyObservers();
	}
	public void addPerson(Person person, int bracket){
		
	}
	public Bracket getBracket(int num){
		
	}
	
}
