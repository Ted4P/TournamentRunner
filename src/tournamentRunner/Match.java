package tournamentRunner;
/*
 * Represents a bracket using a tree data structure
 */
public class Match {
	private Match left, right, parent;
	private Person lPer, rPer, wPer;
	private String note;
	private int val;
	private boolean leaf;
	public static final String DEFAULT_WINNER_NAME = "No Winner", DEFAULT_WINNER_SCHOOL = "No School", DEFAULT_BLANK_NAME = "TBD", DEFAULT_BLANK_SCHOOL = "TBD";
	public Match(int size, Match parent, int i){		//Create a new Match with the parent match [parent], recursively call with [size] divided by 2 and [i] increased according to heap convention
		val=i;
		lPer = new Person(DEFAULT_BLANK_NAME, DEFAULT_BLANK_SCHOOL);
		rPer = new Person(DEFAULT_BLANK_NAME, DEFAULT_BLANK_SCHOOL);
		wPer = new Person(DEFAULT_WINNER_NAME,DEFAULT_WINNER_SCHOOL);
		note = "No information availible yet";
		this.parent = parent;
		if(size<=2){ 
			leaf = true;
			return;
		}
		left = new Match(size/2, this,i*2);
		right = new Match(size/2, this,i*2+1);
	}
	/*
	 * Add a new Person to the bracket, with the given seed.
	 * Returns true if no the seed exists and is empty, otherwise false
	 */
	public boolean addPerson(Person person, int seed, int curLoc, int size){
		
		if(leaf && (seed==curLoc || seed == (size+1)-curLoc)){
			if(lPer.getName().equals("TBD") && seed == curLoc){lPer = person; return true;}
			else if(rPer.getName().equals("TBD") && seed == (size+1)-curLoc){rPer = person; return true;}
			return false;
		}
		if(!leaf && right.addPerson(person,seed,curLoc,size*2))	return true;
		else if(!leaf && left.addPerson(person,seed,(size+1)-curLoc,size*2))	return true;
		return false;
	}
	
	/*
	 * Attempt to advance the given person one match in the bracket. 
	 * Find the match with a contestant that .equals() the submitted person, then:
	 * 			Move that person to the "winner" slot in the match
	 * 			Advance that person, if possible, to the next round of competition
	 * 			Set the match notes to the submitted notes string
	 */
	public boolean advancePerson(Person person, String notes, int matchID){
		if(matchID == val){
			if(lPer.equals(person))
				return promoteWinner(person, notes);
			else if(rPer.equals(person))
				return promoteWinner(person, notes);
			else
				return false;
		}
		return (!leaf && left.advancePerson(person, notes, matchID))? 
				true: (!leaf && right.advancePerson(person, notes, matchID));
	}
	private boolean promoteWinner(Person per, String notes) {
		note = notes;
		wPer = per;
		if(parent!=null){
			parent.setPerson(per, val);
		}
		return true;
	}
	private void setPerson(Person per, int childVal){
		if(val*2 == childVal)
			rPer = per;
		else
			lPer = per;
	}
	/*
	 * Return a string containing the information for this bracket in postfix notation in the given format:
	 * 			Name/school of the left, right, and winner, followed by match notes and the value of the match, all comma separated
	 * 			One match per line, with children having values val*2 and val*2+1
	 */
	public String getInfo(){
		String result = "";
		if(!leaf){
			result += left.getInfo() + "\n";
			result += right.getInfo() + "\n";
		}
		result += lPer.getName() + "/" + lPer.getSchool() + ","
			+ rPer.getName() + "/" + rPer.getSchool() + ","
			+ wPer.getName() + "/" + wPer.getSchool() + "," + note + "," + val;
		return result;
	}
	/*
	 * Given a Match toString, recursively determine if the given match is equal to the current, 
	 * 			then load information into the Match using the format specified in getInfo
	 */
	public void restoreState(String[] data) {
		if(Integer.parseInt(data[4])==val){
			lPer = new Person(data[0].substring(0,data[0].indexOf('/')),data[0].substring(data[0].indexOf('/')+1));
			rPer = new Person(data[1].substring(0,data[1].indexOf('/')),data[1].substring(data[1].indexOf('/')+1));
			wPer = new Person(data[2].substring(0,data[2].indexOf('/')),data[2].substring(data[2].indexOf('/')+1));
			note = data[3];
			return;
		}
		else{
			if(!leaf){ 
				left.restoreState(data);
				right.restoreState(data);
			}
		}
		
	}
	public void setMatch(Person left2, Person right2, Person winner, String notes, int index) {
		if(val==index){
			lPer= left2;
			rPer = right2;
			wPer = winner;
			note = notes;
			return;
		}
		if(!leaf){ 
			left.setMatch(left2, right2, winner, notes, index);
			right.setMatch(left2, right2, winner, notes, index);
		}
	}
	
	public void promoteByes(){
		if(left != null)
			left.promoteByes();
		if(right !=null)
			right.promoteByes();
		if(lPer instanceof Bye) promoteWinner(rPer,"Bye");
		else if(rPer instanceof Bye) promoteWinner(lPer, "Bye");
	}
}
