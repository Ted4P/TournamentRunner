package tournamentRunner;
//Represents a single bracket, with size set in the constructor
public class Bracket {
	private Match top;
	private String name;
	public Bracket(int size,  String name) {		//Create a bracket with a given size and name
		top = new Match(size, null,1);
		this.name = name;
	}
	//Set the bracket name
	public void setName(String nm){name = nm;}
	/*
	 * Attempt to add a new Person to the bracket, and return false if the given seed is occupied
	 */
	public boolean addPerson(Person per,int seed){
		return top.addPerson(per,seed,1,2);
		}
	
	//Set the winner of the bracket given by MatcchID to the Person winner, and set match notes
	public void recordWin(Person winner, String notes, int matchID){
		top.advancePerson(winner, notes, matchID);
	}
	/*
	 * Returns all state information about the bracket in postfix notation
	 */
	public String getInfo(){
		return top.getInfo();
	}
	//Return the bracket name
	public String getName(){
		return name;
	}
	//Given a Match data array, find the bout indicated by the array and restore data
	public void restoreState(String[] data) {
		top.restoreState(data);
	}
	//Set the match indicated by Index to the information contained
	public void setMatch(Person left, Person right, Person winner,String notes, int index) {
		top.setMatch(left,right,winner,notes,index);
	}
	
	//Promote all people facings byes up until another human opponent
	public void promoteByes(boolean fill){
		top.promoteByes(fill);
	}
}
