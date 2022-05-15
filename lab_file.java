public class lab_data{
    public String name;
    public int roll;
    public double cgpa;
    public String department;

    public lab_data() {
    }

    public lab_data(String name, int roll, double cgpa, String department) {
        this.name = name;
        this.roll = roll;
        this.cgpa = cgpa;
        this.department = department;
    }

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRoll() {
		return this.roll;
	}

	public void setRoll(int roll) {
		this.roll = roll;
	}

	public double getCgpa() {
		return this.cgpa;
	}

	public void setCgpa(double cgpa) {
		this.cgpa = cgpa;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}


}




