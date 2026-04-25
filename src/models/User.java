package models;

public abstract class User {

    protected String userId;
    protected String name;
    protected String email;

    //constructor
    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    //getters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    //setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    //abstract methods - must be implemented by subclasses
    public abstract String getRole();
    public abstract void displayInfo();

    //method that's inherited by child class
    public void displayBasicInfo() {
        System.out.println("ID: " + userId + " | Name: " + name + " | Email: " + email);
    }
}

/*
MY CODE EXPLANATION

Abstract class:
- I made it abstract so it's used as a template for other classes
- it also forces use of Student or Staff

Encapsulation:
- protects the data
- both staff and students have userId, name and email in common, so I created a shared blueprint

Getters and Setters:
- these provide controlled access to private fields

Abstract Methods:
- users must select a role, otherwise Java throws an error

Constructor:
- initializes common fields when child calls super()

Inheritance:
- student and staff will extend this class
*/