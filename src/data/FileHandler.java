package data;

import models.Event;
import models.Student;
import models.Staff;

import java.io.*;
import java.util.*;

public class FileHandler {

    //file names
    private static final String EVENTS_FILE = "events.txt";
    private static final String REGISTRATIONS_FILE = "registrations.txt";
    private static final String WAITLISTS_FILE = "waitlists.txt";
    private static final String STUDENTS_FILE = "students.txt";
    private static final String STAFF_FILE = "staff.txt";

    //save all events to events.txt
    public static void saveEvents(DataStore dataStore) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EVENTS_FILE))) {
            for (Event event : dataStore.getAllEvents()) {
                writer.println(event.toString());
            }
        } catch (IOException e) {
            //silent fail - no console messages
        }
    }

    //save all registrations to registrations.txt (also groups registered students by event)
    public static void saveRegistrations(DataStore dataStore) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REGISTRATIONS_FILE))) {
            for (Event event : dataStore.getAllEvents()) {
                List<Student> registered = event.getRegisteredStudentsList();
                if (registered.isEmpty()) continue;

                //build a string of student IDs separated by commas
                StringBuilder ids = new StringBuilder();
                for (int i = 0; i < registered.size(); i++) {
                    if (i > 0) ids.append(",");
                    ids.append(registered.get(i).getUserId());
                }
                //format: eventId|studentId1,studentId2,studentId3
                writer.println(event.getEventId() + "|" + ids.toString());
            }
        } catch (IOException e) {
            //silent fail
        }
    }

    //save all waitlists to waitlists.txt ( first in list = first to be promoted)
    public static void saveWaitlists(DataStore dataStore) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(WAITLISTS_FILE))) {
            for (Event event : dataStore.getAllEvents()) {
                List<Student> waitlist = event.getWaitlistAsList();
                if (waitlist.isEmpty()) continue;

                //build a string of student IDs in order
                StringBuilder ids = new StringBuilder();
                for (int i = 0; i < waitlist.size(); i++) {
                    if (i > 0) ids.append(",");
                    ids.append(waitlist.get(i).getUserId());
                }
                //format: eventId|studentId1,studentId2,studentId3
                writer.println(event.getEventId() + "|" + ids.toString());
            }
        } catch (IOException e) {
            //silent fail
        }
    }

    //save all students to students.txt
    public static void saveStudents(DataStore dataStore) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student student : dataStore.getAllStudents()) {
                //format: studentId|name|email|studentNumber|course
                writer.println(student.getUserId() + "|" + student.getName() + "|" +
                        student.getEmail() + "|" + student.getStudentNumber() + "|" + student.getCourse());
            }
        } catch (IOException e) {
            //silent fail
        }
    }

    //save all staff to staff.txt
    public static void saveStaff(DataStore dataStore) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STAFF_FILE))) {
            for (Staff staffMember : dataStore.getAllStaff()) {
                //format: staffId|name|email|staffNumber|department|position
                writer.println(staffMember.getUserId() + "|" + staffMember.getName() + "|" +
                        staffMember.getEmail() + "|" + staffMember.getStaffNumber() + "|" +
                        staffMember.getDepartment() + "|" + staffMember.getPosition());
            }
        } catch (IOException e) {
            //silent fail
        }
    }

    //save ALL data
    public static void saveAllData(DataStore dataStore) {
        saveEvents(dataStore);
        saveRegistrations(dataStore);
        saveWaitlists(dataStore);
        saveStudents(dataStore);
        saveStaff(dataStore);
    }

    //load all events from events.txt
    public static void loadEvents(DataStore dataStore) {
        File file = new File(EVENTS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(EVENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                //split the line by the | character
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        //format: id|name|date|time|location|maxParticipants
                        Event event = new Event(id, parts[1], parts[2], parts[3], parts[4], Integer.parseInt(parts[5]));
                        dataStore.addEvent(event);
                    } catch (NumberFormatException e) {
                        //skip invalid line
                    }
                }
            }
        } catch (IOException e) {
            //silent fail
        }
    }

    //load all registrations from registrations.txt
    public static void loadRegistrations(DataStore dataStore) {
        File file = new File(REGISTRATIONS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(REGISTRATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|"); //sed to divide a string into an array of substrings
                if (parts.length == 2) {
                    try {
                        int eventId = Integer.parseInt(parts[0]);
                        String[] studentIds = parts[1].split(",");
                        Event event = dataStore.getEventById(eventId);

                        if (event != null) {
                            for (String studentId : studentIds) {
                                Student student = dataStore.getStudentById(studentId);
                                if (student != null) {
                                    event.addToRegistered(student);
                                    student.addRegisteredEvent(eventId);
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        //skip invalid line
                    }
                }
            }
        } catch (IOException e) {
            //silent fail
        }
    }

    //load all waitlists from waitlists.txt (links students to events they are waitlisted for)
    //order is preserved
    public static void loadWaitlists(DataStore dataStore) {
        File file = new File(WAITLISTS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(WAITLISTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|"); //again split
                if (parts.length == 2) {
                    try {
                        int eventId = Integer.parseInt(parts[0]);
                        String[] studentIds = parts[1].split(",");
                        Event event = dataStore.getEventById(eventId);

                        if (event != null) {
                            int position = 1;
                            for (String studentId : studentIds) {
                                Student student = dataStore.getStudentById(studentId);
                                if (student != null) {
                                    event.addToWaitlist(student);
                                    student.addWaitlistedEvent(eventId, position);
                                    position++;
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        //skip invalid line
                    }
                }
            }
        } catch (IOException e) {
            //silent fail
        }
    }

    //load all students from students.txt
    public static void loadStudents(DataStore dataStore) {
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    //format: studentId|name|email|studentNumber|course
                    Student student = new Student(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    dataStore.addStudent(student);
                }
            }
        } catch (IOException e) {
            //silent fail
        }
    }

    //load all staff from staff.txt
    public static void loadStaff(DataStore dataStore) {
        File file = new File(STAFF_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(STAFF_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    //format: staffId|name|email|staffNumber|department|position
                    Staff staff = new Staff(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                    dataStore.addStaff(staff);
                }
            }
        } catch (IOException e) {
            //silent fail
        }
    }

    //load ALL data
    public static void loadAllData(DataStore dataStore) {
        loadStudents(dataStore);
        loadStaff(dataStore);
        loadEvents(dataStore);
        loadRegistrations(dataStore);
        loadWaitlists(dataStore);
    }
}

/*
MY CODE NOTES

File formats:

events.txt:
- Format: eventId|eventName|date|time|location|maxParticipants
- Example: 101|Java Workshop|15/10/2024|14:00|Lab A|30

registrations.txt:
- Format: eventId|studentId1,studentId2,studentId3
- Example: 101|S001,S002,S003

waitlists.txt:
- Format: eventId|studentId1,studentId2,studentId3
- Example: 101|S005,S006 (order matters - first is next to be promoted)

students.txt:
- Format: studentId|name|email|studentNumber|course
- Example: S001|John Doe|john@student.com|S001|Computer Science

staff.txt:
- Format: staffId|name|email|staffNumber|department|position
- Example: ST001|Jane Smith|jane@staff.com|ST001|IT|Manager

PrintWriter = simplified file writing (has println() method)
BufferedReader = efficient file reading (has readLine() method)
try-with-resources = automatically closes files

saveEvents() = uses event.toString() for consistent formatting
saveStudents() = writes student data
saveStaff() = writes staff data

loadEvents() = reads each line, splits by "|", creates Event objects
loadStudents() = reads student data from file
loadStaff() = reads staff data from file

loadRegistrations() = links students to events after both are loaded
loadWaitlists() = preserves position order using position counter

loadAllData() = loads in correct order:
- Students first (needed for registrations/waitlists)
- Staff second (needed for authentication)
- Events third (needed for registrations/waitlists)
- Registrations fourth (links students to events)
- Waitlists fifth (links students to events)

Silent mode = no console messages during save/load
- Empty catch blocks = no error messages printed
- Clean

saveAllData() = saves everything (call after any data change)
*/