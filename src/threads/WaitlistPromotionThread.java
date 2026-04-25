package threads;

import models.Event;
import models.Student;
import data.DataStore;
import data.FileHandler;

public class WaitlistPromotionThread extends Thread {

    private Event event;
    private DataStore dataStore;

    //constructor
    public WaitlistPromotionThread(Event event, DataStore dataStore) {
        this.event = event;
        this.dataStore = dataStore;
    }

    @Override
    public void run() {
        //simulate background processing with a small delay
        try {
            Thread.sleep(100);  //100ms delay to simulate background work
        } catch (InterruptedException e) {
            //thread interrupted - continue anyway
        }

        //promote the first student from waitlist
        Student promotedStudent = event.promoteFromWaitlist();

        if (promotedStudent != null) {
            //update student's records
            promotedStudent.removeWaitlistedEvent(event.getEventId());
            promotedStudent.addRegisteredEvent(event.getEventId());

            //update waitlist positions for remaining students
            updateRemainingWaitlistPositions();

            //display notification message
            System.out.println("\n" + "=".repeat(60));
            System.out.println("NOTIFICATION: Registration cancelled.");
            System.out.println("   Student " + promotedStudent.getUserId() +
                    " has been promoted from the waitlist to event: " + event.getEventName());
            System.out.println("=".repeat(60));
            //CIRCLE BACK

            //save changes to file
            FileHandler.saveAllData(dataStore);
        }
    }

    //update positions for all students still on waitlist
    private void updateRemainingWaitlistPositions() {
        int newPosition = 1;
        for (Student student : event.getWaitlistAsList()) {
            student.updateWaitlistPosition(event.getEventId(), newPosition);
            newPosition++;
        }
    }
}

/*
MY CODE EXPLANATION

extends Thread = allows this class to run as a separate thread

Thread.sleep(100) = simulates background processing with 100ms delay
- This meets the threading requirement for automatic promotion
- Small delay makes the background nature visible

promoteFromWaitlist() = removes first student from queue and adds to registered list

After promotion:
- Remove student from waitlist records (removeWaitlistedEvent)
- Add event to student's registered list (addRegisteredEvent)
- Update positions for remaining waitlisted students (positions shift up by 1)

Notification message = printed when promotion occurs
- Shows which student was promoted to which event

FileHandler.saveAllData() = saves changes immediately after promotion
- Ensures data persistence even if program crashes

updateRemainingWaitlistPositions() = loops through waitlist and updates positions
- First remaining student becomes position 1, second becomes position 2, etc.

This thread is triggered when a student cancels their registration
- Creates a spot in the registered list
- First person on waitlist gets promoted automatically
*/