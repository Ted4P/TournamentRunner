package tournamentRunner;

public class Person implements Comparable<Person>{		//Represents a person with a given name and school
	private String name, school;
	public Person(String name, String school){
		this.name = name;
		this.school = school;
	}
	public boolean equals(Object other){
		if(this.name.equals("BYE"))						//A bye is never equal to another Bye
			return false;
		Person per2 = (Person) other;
		return per2.getName().equalsIgnoreCase(name) && per2.getSchool().equalsIgnoreCase(school);
	}
	public String getName(){return name;}
	public String getSchool(){return school;}
	public String toString(){return name + "(" + school + ")";}
	@Override
	public int compareTo(Person other) {		//Return using string convention, except Byes are never equal
		if(this == other)
			return 0;
		if(this.name.equals("BYE"))
			return -1;
		return (name+school).toLowerCase().compareTo((other.getName()+other.getSchool()).toLowerCase());
	}
}
