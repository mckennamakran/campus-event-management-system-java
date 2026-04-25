package models;

public class Staff extends User {

    //fields only the staff has
    private String staffNumber;
    private String department;
    private String position;

    //full constructor
    public Staff(String userId, String name, String email, String staffNumber, String department, String position) {
        super(userId, name, email);
        this.staffNumber = staffNumber;
        this.department = department;
        this.position = position;
    }

    //simplified constructor
    public Staff(String userId, String name, String department) {
        this(userId, name, name + "@staff.com", userId, department, "Staff Member");
    }

    //simplified for quick login
    public Staff(String userId, String name) {
        this(userId, name, name + "@staff.com", userId, "General", "Staff");
    }

    //getters
    public String getStaffNumber() { return staffNumber; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }

    //setters
    public void setStaffNumber(String staffNumber) { this.staffNumber = staffNumber; }
    public void setDepartment(String department) { this.department = department; }
    public void setPosition(String position) { this.position = position; }

    @Override
    public String getRole() {
        return "STAFF";
    }

    @Override
    public void displayInfo() {
        System.out.println("\n=== STAFF INFORMATION ===");
        System.out.println("Staff ID: " + userId);
        System.out.println("Staff Number: " + staffNumber);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Department: " + department);
        System.out.println("Position: " + position);
    }
}

/*
MY CODE EXPLANATION

extends User - inherits userId, name, email from parent

Staff-specific fields:
- staffNumber = official employee identifier
- department = which department they work in
- position = USE "Admin", "Professor", "Staff Member"

Constructors:
- Full constructor = initializes all fields including parent fields via super()
- Simplified constructor = creates staff with department but default email and position
- Minimal constructor = used when only basic info is available for quick login

getters and setters = controlled access to private fields

getRole() = Returns "STAFF" for role-based menus

displayInfo() = Shows complete staff information including department and position

Staff privileges (handled by other classes):
- Can create events
- Can update event details
- Can cancel events
- Can view all participants and waitlists
*/