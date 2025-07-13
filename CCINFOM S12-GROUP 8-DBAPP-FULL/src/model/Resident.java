package model;

public class Resident {
    private int residentID;
    private String firstName;
    private String lastName;

    public Resident(int residentID, String firstName, String lastName) {
        this.residentID = residentID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getResidentID() { return residentID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
