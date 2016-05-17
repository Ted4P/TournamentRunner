
public class Person implements Comparable<Person>{		//Represents a person with a given name and school
	private String name, school;
	public Person(String name, String school){
		this.name = name;
		this.school = school;
	}
	public boolean equals(Object other){
		Person per2 = (Person) other;
		return per2.getName().equalsIgnoreCase(name) && per2.getSchool().equalsIgnoreCase(school);
	}
	public String getName(){return name;}
	public String getSchool(){return school;}
	public String toString(){return name + " from " + school;}
	@Override
	public int compareTo(Person other) {
		if(this == other)
			return 0;
		return (name+school).toLowerCase().compareTo((other.getName()+other.getSchool()).toLowerCase());
	}
}
