package data;

import models.Event;
import models.Staff;
import models.Student;

import java.util.*;
import java.util.stream.Collectors;

public class DataStore {

    //singleton instance (only this one)
    private static DataStore instance;

    //data storage collections
    private Map<Integer, Event> events;
    private Map<String, Student> students;
    private Map<String, Staff> staff;

    //private constructor prevents instantiation from outside
    private DataStore() {
        events = new HashMap<>();
        students = new HashMap<>();
        staff = new HashMap<>();
    }

    //global access point to get the singleton instance
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    //event operations
    public void addEvent(Event event) {
        events.put(event.getEventId(), event);
    }
    public boolean removeEvent(int eventId) {
        return events.remove(eventId) != null;
    } //true if removed
    public Event getEventById(int eventId) {
        return events.get(eventId);
    } //returns null if not found
    public boolean eventExists(int eventId) {
        return events.containsKey(eventId);
    }
    public List<Event> getAllEvents() {
        return new ArrayList<>(events.values());
    }
    public Map<Integer, Event> getAllEventsMap() {
        return events;
    }
    public int getEventCount() {
        return events.size();
    }

    //student operations
    public void addStudent(Student student) {
        students.put(student.getUserId(), student);
    }
    public Student getStudentById(String studentId) {
        return students.get(studentId);
    } //returns null if not found
    public boolean studentExists(String studentId) {
        return students.containsKey(studentId);
    }
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }
    public Map<String, Student> getAllStudentsMap() {
        return students;
    }
    public int getStudentCount() {
        return students.size();
    }

    //staff operations
    public void addStaff(Staff staffMember) {
        staff.put(staffMember.getUserId(), staffMember);
    }
    public Staff getStaffById(String staffId) {
        return staff.get(staffId);
    } //returns null if not found
    public boolean staffExists(String staffId) {
        return staff.containsKey(staffId);
    }
    public List<Staff> getAllStaff() {
        return new ArrayList<>(staff.values());
    }

    //sorting operations
    public List<Event> getEventsSortedByName() {
        return events.values().stream()
                .sorted((e1, e2) -> e1.getEventName().compareToIgnoreCase(e2.getEventName()))
                .collect(Collectors.toList()); //alphabetical order
    }

    public List<Event> getEventsSortedByDate() {
        return events.values().stream()
                .sorted((e1, e2) -> {
                    int dateCompare = e1.getEventDate().compareTo(e2.getEventDate());
                    if (dateCompare != 0) return dateCompare; //sort by date first
                    return e1.getEventTime().compareTo(e2.getEventTime()); //if same date, sort by time
                })
                .collect(Collectors.toList()); //earliest to latest
    }

    //search operations
    public List<Event> searchEventsByName(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return events.values().stream()
                .filter(e -> e.getEventName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList()); //partial match, case-insensitive
    }

    public List<Event> searchEventsByDate(String date) {
        return events.values().stream()
                .filter(e -> e.getEventDateAsString().equals(date))
                .collect(Collectors.toList()); //exact match on date
    }

    //utility methods
    public void clearAllData() {
        events.clear();
        students.clear();
        staff.clear(); //clears everything
    }
}

/*
MY CODE EXPLANATION

Singleton Pattern = ensures only one DataStore instance exists
- private constructor = prevents external instantiation
- static instance variable = holds the single instance
- getInstance() = global access point

HashMap = lookup by ID - its the key value pair thing
- Events map: Integer (eventId) → Event object
- Students map: String (userId) → Student object
- Staff map: String (userId) → Staff object

Event operations:
- addEvent() = adds or replaces event
- removeEvent() = deletes and returns true if existed
- getEventById() = retrieves or null
- eventExists() = checks existence
- getAllEvents() = returns copy (prevents external modification)

Student operations = same pattern as events
Staff operations = same pattern as events and students

Stream operations = functional programming for data manipulation
- filter() = selects elements matching condition
- sorted() = orders elements by comparator
- collect() = converts stream back to list

getEventsSortedByName() = uses compareToIgnoreCase for alphabetical sorting
getEventsSortedByDate() = compares date first, then time for same date
searchEventsByName() = contains() with toLowerCase() for partial case-insensitive search
searchEventsByDate() = exact match using equals()

clearAllData() = useful for testing or resetting the system

HashMap methods used:
- put(key, value) = adds or replaces entry
- get(key) = retrieves value or null
- containsKey(key) = checks existence
- remove(key) = deletes entry and returns value
- size() = returns number of entries
- values() = returns collection of all values
*/