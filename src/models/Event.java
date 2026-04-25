package models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Event {

    //fields
    private int eventId;
    private String eventName;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String location;
    private int maxParticipants;

    //two distinct states as required
    private List<Student> registeredStudents;
    private Queue<Student> waitlist;  // FIFO queue for fair promotion

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    //onstructor
    public Event(int eventId, String eventName, String eventDate, String eventTime, String location, int maxParticipants) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = LocalDate.parse(eventDate, DATE_FORMATTER);
        this.eventTime = LocalTime.parse(eventTime, TIME_FORMATTER);
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.registeredStudents = new ArrayList<>();
        this.waitlist = new LinkedList<>();
    }

    //getters
    public int getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public LocalDate getEventDate() { return eventDate; }
    public LocalTime getEventTime() { return eventTime; }
    public String getLocation() { return location; }
    public int getMaxParticipants() { return maxParticipants; }

    public String getEventDateAsString() { return eventDate.format(DATE_FORMATTER); }
    public String getEventTimeAsString() { return eventTime.format(TIME_FORMATTER); }

    public int getCurrentRegisteredCount() { return registeredStudents.size(); }
    public int getCurrentWaitlistCount() { return waitlist.size(); }
    public int getAvailableSpots() { return maxParticipants - registeredStudents.size(); }
    public boolean isFull() { return registeredStudents.size() >= maxParticipants; }
    public boolean hasWaitlist() { return !waitlist.isEmpty(); }

    //setters for updatable fields
    public void setEventName(String eventName) {
        if (eventName != null && !eventName.trim().isEmpty()) this.eventName = eventName;
    }
    public void setEventTime(String eventTime) {
        if (eventTime != null && !eventTime.trim().isEmpty()) this.eventTime = LocalTime.parse(eventTime, TIME_FORMATTER);
    }
    public void setLocation(String location) {
        if (location != null && !location.trim().isEmpty()) this.location = location;
    }

    // Registration operations
    public boolean addToRegistered(Student student) {
        if (!isFull() && !registeredStudents.contains(student)) {
            registeredStudents.add(student);
            return true; //true if added successfully
        }
        return false; //false if event is full or already registered
    }

    public boolean addToWaitlist(Student student) {
        if (!waitlist.contains(student)) {
            waitlist.add(student);
            return true; //true if added successfully
        }
        return false; //false if already on waitlist
    }

    public boolean removeFromRegistered(Student student) {
        return registeredStudents.remove(student); //true if removed, false if not found
    }

    public boolean removeFromWaitlist(Student student) {
        return waitlist.remove(student); //true if removed, false if not found
    }

    public boolean isStudentRegistered(Student student) {
        return registeredStudents.contains(student); //true if registered
    }

    public boolean isStudentWaitlisted(Student student) {
        return waitlist.contains(student); //true if on waitlist
    }

    public Student promoteFromWaitlist() {
        if (waitlist.isEmpty()) return null;
        Student promoted = waitlist.poll();
        if (promoted != null) {
            registeredStudents.add(promoted);
        }
        return promoted;
    }

    public List<Student> getRegisteredStudentsList() {
        return new ArrayList<>(registeredStudents);
    }

    public List<Student> getWaitlistAsList() {
        return new ArrayList<>(waitlist);
    }

    public int getWaitlistPosition(Student student) {
        int position = 1;
        for (Student s : waitlist) {
            if (s.getUserId().equals(student.getUserId())) return position; //1 = next to be promoted
            position++;
        }
        return -1; //-1 if not on waitlist
    }

    //display methods
    public void displayBasicInfo() {
        System.out.printf("ID: %d | %s | %s | %s | %s%n",
                eventId, eventName, getEventDateAsString(), getEventTimeAsString(), location); //formatted string
        System.out.printf("    Registered: %d/%d | Waitlist: %d%n",
                registeredStudents.size(), maxParticipants, waitlist.size());
    }

    public void displayFullInfo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("EVENT: " + eventName);
        System.out.println("=".repeat(50));
        System.out.println("Event ID: " + eventId);
        System.out.println("Date: " + getEventDateAsString());
        System.out.println("Time: " + getEventTimeAsString());
        System.out.println("Location: " + location);
        System.out.println("Capacity: " + maxParticipants);
        System.out.println("Registered: " + registeredStudents.size());
        System.out.println("Waitlist: " + waitlist.size());
        System.out.println("Available Spots: " + getAvailableSpots());
    }

    public void displayRegisteredParticipants() {
        System.out.println("\n--- REGISTERED PARTICIPANTS (" + registeredStudents.size() + ") ---");
        if (registeredStudents.isEmpty()) {
            System.out.println("No registered participants.");
        } else {
            for (int i = 0; i < registeredStudents.size(); i++) {
                Student s = registeredStudents.get(i);
                System.out.println((i+1) + ". " + s.getUserId() + " - " + s.getName());
            }
        }
    }

    public void displayWaitlist() {
        System.out.println("\n--- WAITLIST (" + waitlist.size() + ") ---");
        if (waitlist.isEmpty()) {
            System.out.println("No students on waitlist.");
        } else {
            int pos = 1;
            for (Student s : waitlist) {
                System.out.println(pos + ". " + s.getUserId() + " - " + s.getName());
                pos++;
            }
        }
    }

    @Override
    public String toString() {
        return eventId + "|" + eventName + "|" + getEventDateAsString() + "|" +
                getEventTimeAsString() + "|" + location + "|" + maxParticipants;
    }
}

/*
MY CODE EXPLANATION

fields = eventId (unique identifier), eventName, eventDate, eventTime, location, maxParticipants

List vs Queue:
- registeredStudents = List (ArrayList) - maintains order but allows random access
- waitlist = Queue (LinkedList) - FIFO (First In First Out) for fair promotions

Queue = students waiting for spots, first come first served
- poll() = removes and returns first element
- add() = adds to end of queue

LocalDate/LocalTime = Java's built-in date/time objects that allow easy comparison

DateTimeFormatter = converts between String and date/time objects
- "dd/MM/yyyy" format for dates (e.g., "25/12/2024")
- "HH:mm" format for times (e.g., "14:30")

Constructor = parses date/time strings into proper objects, initializes empty collections

getAvailableSpots() = maxParticipants - registeredStudents.size()
isFull() = checks if event has reached capacity
hasWaitlist() = checks if there are people waiting

addToRegistered() = only adds if event is not full and not already registered
addToWaitlist() = prevents duplicate waitlist entries, adds to end of queue

promoteFromWaitlist() = CRITICAL METHOD
- Called when someone cancels
- Removes first student from queue and adds to registered list
- Ensures fair FIFO promotion

getWaitlistPosition() = loops through queue to find student's position (1-indexed for user display)

display methods = show event information in readable format

*/