import java.util.*;
class Appointment {
    String id;
    String patient;
    String doctor;
    int start;
    int end;
    int priority;
    Appointment(String id, String patient,
                String doctor, int start,
                int end, int priority) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.start = start;
        this.end = end;
        this.priority = priority;
    }
    public String toString() {
        return id +
               " | Patient: " + patient +
               " | Doctor: " + doctor +
               " | Time: " + start +
               "-" + end +
               " | Priority: " + priority;
    }
}
class IntervalNode {
    Appointment appointment;
    int maxEnd;
    IntervalNode left;
    IntervalNode right;
    IntervalNode(Appointment appointment) {
        this.appointment = appointment;
        this.maxEnd = appointment.end;
    }
}
class IntervalTree {
    IntervalNode root;
    boolean overlap(Appointment a,
                    Appointment b) {
        return a.start < b.end &&
               b.start < a.end;
    }
    IntervalNode insert(IntervalNode node,
                        Appointment appointment) {
        if (node == null)
            return new IntervalNode(appointment);
        if (appointment.start <
            node.appointment.start) {
            node.left =
                insert(node.left, appointment);
        } else {
            node.right =
                insert(node.right, appointment);
        }
        node.maxEnd =
            Math.max(node.maxEnd,
                     appointment.end);
        return node;
    }
    void insert(Appointment appointment) {
        root = insert(root, appointment);
    }
    Appointment searchOverlap(
            IntervalNode node,
            Appointment target) {
        if (node == null)
            return null;
        if (overlap(node.appointment, target))
            return node.appointment;
        if (node.left != null &&
            node.left.maxEnd > target.start) {
            return searchOverlap(
                node.left, target);
        }
        return searchOverlap(
            node.right, target);
    }
    Appointment searchOverlap(
            Appointment target) {
        return searchOverlap(root, target);
    }
}
class HospitalScheduler {
    IntervalTree intervalTree =
        new IntervalTree();
    PriorityQueue<Appointment> priorityQueue =
        new PriorityQueue<>(
            Comparator.comparingInt(
                a -> a.priority
            )
        );
    ArrayList<Appointment> appointments =
        new ArrayList<>();
    void addAppointment(Appointment a) {
        Appointment conflict =
            intervalTree.searchOverlap(a);
        if (conflict != null) {
            System.out.println(
                "Conflict detected!");
            System.out.println(
                "Existing: " + conflict);
            return;
        }
        intervalTree.insert(a);
        priorityQueue.add(a);
        appointments.add(a);
        System.out.println(
            "Appointment added successfully.");
        System.out.println(a);
    }
    void deleteAppointment(String id) {
        Appointment found = null;
        for (Appointment a : appointments) {
            if (a.id.equals(id)) {
                found = a;
                break;
            }
        }
        if (found == null) {
            System.out.println(
                "Appointment not found.");
            return;
        }
        appointments.remove(found);
        priorityQueue.remove(found);
        rebuildTree();
        System.out.println(
            "Appointment deleted: " + id);
    }
    void updateAppointment(
            String id,
            int newStart,
            int newEnd) {
        Appointment found = null;
        for (Appointment a : appointments) {
            if (a.id.equals(id)) {
                found = a;
                break;
            }
        }
        if (found == null) {
            System.out.println(
                "Appointment not found.");
            return;
        }
        appointments.remove(found);
        priorityQueue.remove(found);
        rebuildTree();
        found.start = newStart;
        found.end = newEnd;
        Appointment conflict =
            intervalTree.searchOverlap(found);
        if (conflict != null) {
            System.out.println(
                "Update causes conflict with:");
            System.out.println(conflict);
            found.start = newStart;
            found.end = newEnd;
            appointments.add(found);
            priorityQueue.add(found);
            rebuildTree();
            return;
        }
        appointments.add(found);
        priorityQueue.add(found);
        intervalTree.insert(found);
        System.out.println(
            "Appointment updated successfully.");
        System.out.println(found);
    }
    void rebuildTree() {
        intervalTree =
            new IntervalTree();
        for (Appointment a : appointments)
            intervalTree.insert(a);
    }
    void displaySchedule() {
        System.out.println(
            "\nCURRENT SCHEDULE");
        for (Appointment a : appointments)
            System.out.println(a);
    }
    void processPriority() {
        PriorityQueue<Appointment> temp =
            new PriorityQueue<>(
                priorityQueue
            );
        System.out.println(
            "\nPRIORITY ORDER");
        while (!temp.isEmpty()) {
            System.out.println(
                temp.poll());
        }
    }
}
public class HospitalSchedulingSystem {
    public static void main(String[] args) {
        HospitalScheduler hospital =
            new HospitalScheduler();
        System.out.println(
            "\n--- TC1: Insert Appointment ---");
        hospital.addAppointment(
            new Appointment(
                "A01",
                "P01",
                "Dr.Ravi",
                10,
                11,
                3
            )
        );
        System.out.println(
            "\n--- TC2: Overlapping Appointment ---");
        hospital.addAppointment(
            new Appointment(
                "A02",
                "P02",
                "Dr.Anita",
                10,
                12,
                2
            )
        );
        System.out.println(
            "\n--- TC3: Emergency Operation ---");
        hospital.addAppointment(
            new Appointment(
                "S01",
                "P03",
                "Dr.Kumar",
                12,
                13,
                1
            )
        );
        System.out.println(
            "\n--- TC4: Delete Appointment ---");
        hospital.deleteAppointment("A01");
        hospital.addAppointment(
            new Appointment(
                "A03",
                "P04",
                "Dr.Ravi",
                14,
                15,
                3
            )
        );
        System.out.println(
            "\n--- TC5: Retroactive Update ---");
        hospital.updateAppointment(
            "A03",
            15,
            16
        );
        System.out.println(
            "\n--- TC6: Priority Order ---");
        hospital.processPriority();
        hospital.displaySchedule();
    }
}