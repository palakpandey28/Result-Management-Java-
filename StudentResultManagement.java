import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;

class Student {
    String name;
    int rollNumber;
    String className;

    Student(String name, int rollNumber, String className) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.className = className;
    }
}

class Result {
    int maths;
    int science;
    int english;

    Result(int maths, int science, int english) {
        this.maths = maths;
        this.science = science;
        this.english = english;
    }

    int totalMarks() {
        return maths + science + english;
    }

    float averageMarks() {
        return totalMarks() / 3.0f;
    }

    String grade() {
        float avg = averageMarks();
        if (avg >= 90) return "A";
        else if (avg >= 75) return "B";
        else if (avg >= 60) return "C";
        else if (avg >= 50) return "D";
        else return "F";
    }
}

class StudentRecord {
    Student student;
    Result result;

    StudentRecord(Student student, Result result) {
        this.student = student;
        this.result = result;
    }
}

public class StudentResultManagement {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of students: ");
        int n = sc.nextInt();
        sc.nextLine(); // consume newline

        StudentRecord[] records = new StudentRecord[n];

        // Input student data
        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for student " + (i + 1));
            System.out.print("Name: ");
            String name = sc.nextLine();
            System.out.print("Roll Number: ");
            int roll = sc.nextInt();
            sc.nextLine();
            System.out.print("Class: ");
            String cls = sc.nextLine();

            System.out.print("Marks in Maths: ");
            int maths = sc.nextInt();
            System.out.print("Marks in Science: ");
            int science = sc.nextInt();
            System.out.print("Marks in English: ");
            int english = sc.nextInt();
            sc.nextLine();

            Student s = new Student(name, roll, cls);
            Result r = new Result(maths, science, english);
            records[i] = new StudentRecord(s, r);
        }

        // Sort students by total marks descending
        Arrays.sort(records, new Comparator<StudentRecord>() {
            public int compare(StudentRecord a, StudentRecord b) {
                return b.result.totalMarks() - a.result.totalMarks();
            }
        });

        // Display report card table header
        System.out.println("\n===== Student Results (Sorted by Total Marks) =====");
        System.out.printf("%-15s %-10s %-10s %-7s %-8s %-8s %-7s %-8s %-6s\n",
                "Name", "Roll No", "Class", "Maths", "Science", "English", "Total", "Average", "Grade");
        System.out.println("--------------------------------------------------------------------------------");

        // Display all students
        for (StudentRecord rec : records) {
            System.out.printf("%-15s %-10d %-10s %-7d %-8d %-8d %-7d %-8.2f %-6s\n",
                    rec.student.name,
                    rec.student.rollNumber,
                    rec.student.className,
                    rec.result.maths,
                    rec.result.science,
                    rec.result.english,
                    rec.result.totalMarks(),
                    rec.result.averageMarks(),
                    rec.result.grade());
        }

        // Display topper
        StudentRecord topper = records[0];
        System.out.println("\n===== Topper =====");
        System.out.printf("%-15s %-10s %-10s %-7s %-8s %-8s %-7s %-8s %-6s\n",
                "Name", "Roll No", "Class", "Maths", "Science", "English", "Total", "Average", "Grade");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-15s %-10d %-10s %-7d %-8d %-8d %-7d %-8.2f %-6s\n",
                topper.student.name,
                topper.student.rollNumber,
                topper.student.className,
                topper.result.maths,
                topper.result.science,
                topper.result.english,
                topper.result.totalMarks(),
                topper.result.averageMarks(),
                topper.result.grade());
    }
}


