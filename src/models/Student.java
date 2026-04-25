package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User {

    //fields only the student has
    private String studentNumber;
    private String course;
    private List<Integer> registeredEventIds;
    private Map<Integer, Integer> waitlistedEventIds;

    //full constructor
    public Student(String userId, String name, String email, String studentNumber, String course) {
        super(userId, name, email);
        this.studentNumber = studentNumber;
        this.course = course;
        this.registeredEventIds = new ArrayList<>();
        this.waitlistedEventIds = new HashMap<>();
    }

    //simplified constructor for quick login
    public Student(String userId, String name) {
        this(userId, name, name + "@student.com", userId, "General Studies");
    }

    //getters
    public String getStudentNumber() { return studentNumber; }
    public String getCourse() { return course; }
    public List<Integer> getRegisteredEventIds() { return registeredEventIds; }
    public Map<Integer, Integer> getWaitlistedEventIds() { return waitlistedEventIds; }

    //setters
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public void setCourse(String course) { this.course = course; }

    //add event to student's registered list
    public boolean addRegisteredEvent(int eventId) {
        if (!registeredEventIds.contains(eventId)) {
            registeredEventIds.add(eventId);
            return true;
        }
        return false;
    }

    //remove event from student's registered list
    public boolean removeRegisteredEvent(int eventId) {
        return registeredEventIds.remove(Integer.valueOf(eventId));
    }

    //check if student is registered for an event
    public boolean isRegisteredForEvent(int eventId) {
        return registeredEventIds.contains(eventId);
    }

    //add student to waitlist for an event
    public void addWaitlistedEvent(int eventId, int position) {
        waitlistedEventIds.put(eventId, position);
    }

    //remove student from waitlist for an event
    public boolean removeWaitlistedEvent(int eventId) {
        return waitlistedEventIds.remove(eventId) != null;
    }

    //get waitlist position for an event
    public int getWaitlistPosition(int eventId) {
        return waitlistedEventIds.getOrDefault(eventId, -1);
    }

    //check if student is waitlisted for an event
    public boolean isWaitlistedForEvent(int eventId) {
        return waitlistedEventIds.containsKey(eventId);
    }

    //update waitlist position ...used when someone ahead cancels
    public void updateWaitlistPosition(int eventId, int newPosition) {
        if (waitlistedEventIds.containsKey(eventId)) {
            waitlistedEventIds.put(eventId, newPosition);
        }
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    @Override
    public void displayInfo() {
        System.out.println("\n=== STUDENT INFORMATION ===");
        System.out.println("Student ID: " + userId);
        System.out.println("Student Number: " + studentNumber);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Course: " + course);
        System.out.println("Registered Events: " + registeredEventIds.size());
        System.out.println("Waitlisted Events: " + waitlistedEventIds.size());
    }

    //display just the student's registration status summary
    public void displayRegistrationSummary() {
        System.out.println("\n--- Registration Summary for " + name + " ---");
        System.out.println("Registered for " + registeredEventIds.size() + " event(s)");
        System.out.println("Waitlisted for " + waitlistedEventIds.size() + " event(s)");
    }
}

/*
MY CODE EXPLANATION

extends User - inherits userId, name, email from parent

list = container that stores multiple values, example a student is registered for events 101, 202, 303
map = key, value pairs
- the map would be EventID -> Position
- example: 101 -> 2, for event 101 → student is 2nd in line

super = call parent constructor to initialize common fields

Simplified constructor = used when only basic info is available

addRegisteredEvent() = Prevents duplicate registrations
isRegisteredForEvent() = Quick check for duplicate registration
getRole() = Returns "STUDENT" for role-based menus
*/