package model;

public class Staff {
    private int staffID;
    private int residentID;
    private String position;

    public Staff(int staffID, int residentID, String position) {
        this.staffID = staffID;
        this.residentID = residentID;
        this.position = position;
    }

    public int getStaffID() { return staffID; }
    public int getResidentID() { return residentID; }
    public String getPosition() { return position; }
}
