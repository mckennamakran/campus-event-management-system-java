import data.DataStore;
import data.FileHandler;
import models.Event;
import models.Staff;
import models.Student;
import threads.WaitlistPromotionThread;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static DataStore dataStore;
    private static boolean isRunning = true;

    public static void main(String[] args) {
        //initialize data store
        dataStore = DataStore.getInstance();

        //load saved data
        FileHandler.loadAllData(dataStore);

        //main program loop
        while (isRunning) {
            displayRoleMenu();
            int choice = getIntInput("Enter your choice: ", 1, 3);

            switch (choice) {
                case 1:
                    handleStudentSession();
                    break;
                case 2:
                    handleStaffSession();
                    break;
                case 3:
                    exitProgram();
                    break;
            }
        }

        scanner.close();
    }

    //display role selection menu
    private static void displayRoleMenu() {
        System.out.println("\n--- SELECT YOUR ROLE ---");
        System.out.println("1. Student");
        System.out.println("2. Staff");
        System.out.println("3. Exit Program");
        System.out.println("-".repeat(25));
    }

    //display student menu options
    private static void displayStudentMenu() {
        System.out.println("\n=== STUDENT MENU ===");
        System.out.println("1. View Available Events");
        System.out.println("2. Register for an Event");
        System.out.println("3. Cancel Registration");
        System.out.println("4. View My Registration Status");
        System.out.println("5. Search Events");
        System.out.println("6. Logout");
        System.out.println("-".repeat(25));
    }

    //display staff menu options
    private static void displayStaffMenu() {
        System.out.println("\n=== STAFF MENU ===");
        System.out.println("1. View All Events");
        System.out.println("2. Create New Event");
        System.out.println("3. Update Event Details");
        System.out.println("4. Cancel an Event");
        System.out.println("5. View Participants & Waitlist");
        System.out.println("6. Search Events");
        System.out.println("7. Logout");
        System.out.println("-".repeat(25));
    }

    //handle a student's session
    private static void handleStudentSession() {
        System.out.println("\n=== STUDENT LOGIN ===");
        System.out.print("Enter Student ID (e.g., S001): ");
        String studentId = scanner.nextLine().trim();
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        Student student = dataStore.getStudentById(studentId);
        if (student == null) {
            student = new Student(studentId, name);
            dataStore.addStudent(student);
            System.out.println("New student account created!");
            FileHandler.saveAllData(dataStore);
        } else {
            System.out.println("Welcome back, " + student.getName() + "!");
        }

        boolean sessionActive = true;
        while (sessionActive) {
            displayStudentMenu();
            int choice = getIntInput("Enter choice: ", 1, 6);

            switch (choice) {
                case 1: viewAvailableEvents(); break;
                case 2: registerForEvent(student); break;
                case 3: cancelRegistration(student); break;
                case 4: viewRegistrationStatus(student); break;
                case 5: searchEvents(); break;
                case 6: sessionActive = false; System.out.println("Logging out..."); break;
            }

            if (sessionActive) pressAnyKeyToContinue();
        }
    }

    //handle a staff session
    private static void handleStaffSession() {
        System.out.println("\n=== STAFF LOGIN ===");
        System.out.print("Enter Staff ID (e.g., ST001): ");
        String staffId = scanner.nextLine().trim();
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter department: ");
        String department = scanner.nextLine().trim();

        Staff staff = dataStore.getStaffById(staffId);
        if (staff == null) {
            staff = new Staff(staffId, name, department);
            dataStore.addStaff(staff);
            System.out.println("New staff account created!");
            FileHandler.saveAllData(dataStore);
        } else {
            System.out.println("Welcome back, " + staff.getName() + "!");
        }

        boolean sessionActive = true;
        while (sessionActive) {
            displayStaffMenu();
            int choice = getIntInput("Enter choice: ", 1, 7);

            switch (choice) {
                case 1: viewAllEvents(); break;
                case 2: createNewEvent(); break;
                case 3: updateEventDetails(); break;
                case 4: cancelEvent(); break;
                case 5: viewParticipantsAndWaitlist(); break;
                case 6: searchEvents(); break;
                case 7: sessionActive = false; System.out.println("Logging out..."); break;
            }

            if (sessionActive) pressAnyKeyToContinue();
        }
    }

    //view all available events (student view)
    private static void viewAvailableEvents() {
        if (dataStore.getEventCount() == 0) {
            System.out.println("\nNo events available.");
            return;
        }

        System.out.println("\nSort by: 1. Event Name | 2. Event Date | 3. No sorting");
        int sortChoice = getIntInput("Enter choice: ", 1, 3);

        List<Event> events;
        switch (sortChoice) {
            case 1:
                events = dataStore.getEventsSortedByName();
                break;
            case 2:
                events = dataStore.getEventsSortedByDate();
                break;
            default:
                events = dataStore.getAllEvents();
                break;
        }

        System.out.println("\n" + "-".repeat(70));
        for (Event event : events) {
            event.displayBasicInfo();
            System.out.println("-".repeat(70));
        }
    }

    //register student for an event
    private static void registerForEvent(Student student) {
        if (dataStore.getEventCount() == 0) {
            System.out.println("\nNo events available to register for.");
            return;
        }

        viewAvailableEvents();
        int eventId = getIntInput("\nEnter Event ID to register for: ", 1, Integer.MAX_VALUE);
        Event event = dataStore.getEventById(eventId);

        if (event == null) {
            System.out.println("Event not found.");
            return;
        }

        if (event.isStudentRegistered(student)) {
            System.out.println("You are already registered for this event.");
            return;
        }

        if (event.isStudentWaitlisted(student)) {
            System.out.println("You are already on the waitlist for this event.");
            return;
        }

        if (!event.isFull()) {
            event.addToRegistered(student);
            student.addRegisteredEvent(eventId);
            System.out.println("Successfully registered for " + event.getEventName() + "!");
        } else {
            event.addToWaitlist(student);
            int position = event.getWaitlistPosition(student);
            student.addWaitlistedEvent(eventId, position);
            System.out.println("Event is full. You have been added to the waitlist at position " + position + ".");
        }

        FileHandler.saveAllData(dataStore);
    }

    //cancel student's registration or waitlist
    private static void cancelRegistration(Student student) {
        if (student.getRegisteredEventIds().isEmpty() && student.getWaitlistedEventIds().isEmpty()) {
            System.out.println("\nYou are not registered for any events.");
            return;
        }

        //display current registrations
        if (!student.getRegisteredEventIds().isEmpty()) {
            System.out.println("\nYour Registered Events:");
            for (int eventId : student.getRegisteredEventIds()) {
                Event e = dataStore.getEventById(eventId);
                if (e != null) System.out.println("  ID: " + eventId + " - " + e.getEventName());
            }
        }

        if (!student.getWaitlistedEventIds().isEmpty()) {
            System.out.println("\nYour Waitlisted Events:");
            for (int eventId : student.getWaitlistedEventIds().keySet()) {
                Event e = dataStore.getEventById(eventId);
                if (e != null) {
                    int pos = student.getWaitlistPosition(eventId);
                    System.out.println("  ID: " + eventId + " - " + e.getEventName() + " (Position: " + pos + ")");
                }
            }
        }

        int eventId = getIntInput("\nEnter Event ID to cancel: ", 1, Integer.MAX_VALUE);
        Event event = dataStore.getEventById(eventId);

        if (event == null) {
            System.out.println("Event not found.");
            return;
        }

        if (event.isStudentRegistered(student)) {
            event.removeFromRegistered(student);
            student.removeRegisteredEvent(eventId);
            System.out.println("Registration cancelled for " + event.getEventName());

            //threaded waitlist promotion
            if (event.hasWaitlist()) {
                WaitlistPromotionThread thread = new WaitlistPromotionThread(event, dataStore);
                thread.start();
            }
        } else if (event.isStudentWaitlisted(student)) {
            event.removeFromWaitlist(student);
            student.removeWaitlistedEvent(eventId);
            System.out.println("Removed from waitlist for " + event.getEventName());
        } else {
            System.out.println("You are not registered or waitlisted for this event.");
        }

        FileHandler.saveAllData(dataStore);
    }

    //view student's registration status
    private static void viewRegistrationStatus(Student student) {
        student.displayRegistrationSummary();

        if (!student.getRegisteredEventIds().isEmpty()) {
            System.out.println("\n--- REGISTERED EVENTS ---");
            for (int eventId : student.getRegisteredEventIds()) {
                Event event = dataStore.getEventById(eventId);
                if (event != null) event.displayBasicInfo();
            }
        } else {
            System.out.println("\nYou are not registered for any events.");
        }

        if (!student.getWaitlistedEventIds().isEmpty()) {
            System.out.println("\n--- WAITLISTED EVENTS ---");
            for (int eventId : student.getWaitlistedEventIds().keySet()) {
                Event event = dataStore.getEventById(eventId);
                if (event != null) {
                    int pos = student.getWaitlistPosition(eventId);
                    System.out.println("\n[WAITLISTED - Position " + pos + "]");
                    event.displayBasicInfo();
                }
            }
        } else {
            System.out.println("\nYou are not waitlisted for any events.");
        }
    }

    //view all events (staff view)
    private static void viewAllEvents() {
        if (dataStore.getEventCount() == 0) {
            System.out.println("\nNo events have been created yet.");
            return;
        }

        System.out.println("\nSort by:");
        System.out.println("1. Event Name | 2. Event Date | 3. No sorting");
        int sortChoice = getIntInput("Enter choice: ", 1, 3);

        List<Event> events;
        switch (sortChoice) {
            case 1: events = dataStore.getEventsSortedByName(); break;
            case 2: events = dataStore.getEventsSortedByDate(); break;
            default: events = dataStore.getAllEvents(); break;
        }

        System.out.println("\n" + "-".repeat(70));
        for (Event event : events) {
            event.displayBasicInfo();
            System.out.println("-".repeat(70));
        }
    }

    //create a new event (staff only)
    private static void createNewEvent() {
        System.out.println("\n--- CREATE NEW EVENT ---");

        int eventId = getIntInput("Enter Event ID (unique integer): ", 1, Integer.MAX_VALUE);
        if (dataStore.eventExists(eventId)) {
            System.out.println("Error: Event ID already exists!");
            return;
        }

        System.out.print("Enter Event Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }

        String date = getValidDateInput();
        String time = getValidTimeInput();

        System.out.print("Enter Location: ");
        String location = scanner.nextLine().trim();
        if (location.isEmpty()) { System.out.println("Location cannot be empty."); return; }

        int maxParticipants = getIntInput("Enter Maximum Participants: ", 1, 1000);

        Event event = new Event(eventId, name, date, time, location, maxParticipants);
        dataStore.addEvent(event);

        System.out.println("\n✓ Event created successfully!");
        event.displayBasicInfo();
        FileHandler.saveAllData(dataStore);
    }

    //update event details (staff only)
    private static void updateEventDetails() {
        if (dataStore.getEventCount() == 0) {
            System.out.println("\nNo events to update.");
            return;
        }

        viewAllEvents();
        int eventId = getIntInput("\nEnter Event ID to update: ", 1, Integer.MAX_VALUE);
        Event event = dataStore.getEventById(eventId);

        if (event == null) {
            System.out.println("Event not found.");
            return;
        }

        System.out.println("\nUpdating: " + event.getEventName());
        System.out.println("(Press Enter to keep current value)");

        System.out.print("New Name [" + event.getEventName() + "]: ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) { event.setEventName(newName); System.out.println("✓ Name updated"); }

        System.out.print("New Time [" + event.getEventTimeAsString() + "]: ");
        String newTime = scanner.nextLine().trim();
        if (!newTime.isEmpty()) {
            if (isValidTime(newTime)) {
                event.setEventTime(newTime);
                System.out.println("Time updated");
            } else {
                System.out.println("Invalid time format - keeping original");
            }
        }

        System.out.print("New Location [" + event.getLocation() + "]: ");
        String newLocation = scanner.nextLine().trim();
        if (!newLocation.isEmpty()) { event.setLocation(newLocation); System.out.println("✓ Location updated"); }

        System.out.println("\n✓ Event updated successfully!");
        FileHandler.saveAllData(dataStore);
    }

    //cancel an event (staff only)
    private static void cancelEvent() {
        if (dataStore.getEventCount() == 0) {
            System.out.println("\nNo events to cancel.");
            return;
        }

        viewAllEvents();
        int eventId = getIntInput("\nEnter Event ID to cancel: ", 1, Integer.MAX_VALUE);
        Event event = dataStore.getEventById(eventId);

        if (event == null) {
            System.out.println("Event not found.");
            return;
        }

        System.out.print("Are you sure you want to cancel '" + event.getEventName() + "'? (y/n): ");
        if (scanner.nextLine().trim().toLowerCase().equals("y")) {
            //remove student references
            for (Student s : event.getRegisteredStudentsList()) s.removeRegisteredEvent(eventId);
            for (Student s : event.getWaitlistAsList()) s.removeWaitlistedEvent(eventId);

            dataStore.removeEvent(eventId);
            System.out.println("✓ Event cancelled and removed.");
            FileHandler.saveAllData(dataStore);
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    //view participants and waitlist (staff only)
    private static void viewParticipantsAndWaitlist() {
        if (dataStore.getEventCount() == 0) {
            System.out.println("\nNo events exist.");
            return;
        }

        viewAllEvents();
        int eventId = getIntInput("\nEnter Event ID to view: ", 1, Integer.MAX_VALUE);
        Event event = dataStore.getEventById(eventId);

        if (event == null) {
            System.out.println("Event not found.");
            return;
        }

        event.displayFullInfo();
        event.displayRegisteredParticipants();
        event.displayWaitlist();
    }

    //search events by name or date
    private static void searchEvents() {
        System.out.println("\n--- SEARCH EVENTS ---");
        System.out.println("1. Search by Event Name");
        System.out.println("2. Search by Event Date");
        int choice = getIntInput("Enter choice: ", 1, 2);

        List<Event> results;

        if (choice == 1) {
            System.out.print("Enter event name (partial match allowed): ");
            String keyword = scanner.nextLine().trim();
            results = dataStore.searchEventsByName(keyword);
            System.out.println("\n--- Results for '" + keyword + "' ---");
        } else {
            String date = getValidDateInput();
            results = dataStore.searchEventsByDate(date);
            System.out.println("\n--- Events on " + date + " ---");
        }

        if (results.isEmpty()) {
            System.out.println("No events found.");
        } else {
            for (Event event : results) {
                event.displayFullInfo();
                System.out.println();
            }
        }
    }

    //get and validate date input
    private static String getValidDateInput() {
        String date;
        while (true) {
            System.out.print("Enter Date (dd/mm/yyyy): ");
            date = scanner.nextLine().trim();
            if (isValidDate(date)) return date;
            System.out.println("Invalid date! Use format dd/mm/yyyy");
        }
    }

    //get and validate time input
    private static String getValidTimeInput() {
        String time;
        while (true) {
            System.out.print("Enter Time (HH:mm): ");
            time = scanner.nextLine().trim();
            if (isValidTime(time)) return time;
            System.out.println("Invalid time! Use format HH:mm (00-23)");
        }
    }

    //validate date format and values
    private static boolean isValidDate(String date) {
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) return false; //CIRCLE BACK HERE
        String[] parts = date.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (month < 1 || month > 12) return false;
        if (day < 1 || day > 31) return false;

        if (month == 4 || month == 6 || month == 9 || month == 11) return day <= 30;
        if (month == 2) {
            boolean isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            return day <= (isLeap ? 29 : 28);
        }
        return true;
    }

    //validate time format and values
    private static boolean isValidTime(String time) {
        if (!time.matches("\\d{2}:\\d{2}")) return false;
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
    }

    //get integer input with validation
    private static int getIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = Integer.parseInt(scanner.nextLine().trim());
                if (input >= min && input <= max) return input;
                System.out.println("Enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
            }
        }
    }

    //press any key to continue
    private static void pressAnyKeyToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    //exit the program cleanly
    private static void exitProgram() {
        FileHandler.saveAllData(dataStore);
        System.out.println("\nThank you for using the Campus Event Management System!");
        System.out.println("Goodbye!");
        isRunning = false;
    }
}

/*
MY CODE EXPLANATION

Scanner = reads user input from console
dataStore = singleton reference for centralized data access
isRunning = controls main program loop

main() = initializes data store, loads saved data, runs main loop

Role menu = shows Student/Staff/Exit options

Student session:
- Prompts for ID and name, creates new account if needed
- Menu: view events, register, cancel registration, view status, search, logout

Staff session:
- Prompts for ID, name, department, creates new account if needed
- Menu: view all events, create event, update event, cancel event, view participants, search, logout

Student functionality:
- viewAvailableEvents() = shows all events with sorting options
- registerForEvent() = checks capacity, adds to registered or waitlist
- cancelRegistration() = removes from registered or waitlist, starts waitlist promotion thread
- viewRegistrationStatus() = shows all registered and waitlisted events

Staff functionality:
- viewAllEvents() = shows all events with statistics
- createNewEvent() = prompts for all event details, validates unique ID
- updateEventDetails() = allows updating name, time, location only
- cancelEvent() = removes event and cleans up student references
- viewParticipantsAndWaitlist() = shows full participant lists

Search functionality = search by name (partial match) or by date (exact match)

Validation methods:
- getValidDateInput() = loops until valid date entered
- getValidTimeInput() = loops until valid time entered
- isValidDate() = checks format and real date values (including leap years)
- isValidTime() = checks format and hour/minute range

Utility methods:
- getIntInput() = validates integer input within range
- pressAnyKeyToContinue() = pauses execution
- exitProgram() = saves data and stops main loop

Waitlist promotion thread = started when student cancels registration
- Automatically promotes first person from waitlist to registered

Data persistence = FileHandler saves/loads all data automatically
- Save after any change (registration, cancellation, event creation, etc.)
- Load at program startup
*/