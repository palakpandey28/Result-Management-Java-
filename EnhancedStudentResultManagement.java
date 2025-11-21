import java.util.*;
import java.io.*;

// Make all classes Serializable for file operations
class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int rollNumber;
    private String className;
    private String department;

    public Student(String name, int rollNumber, String className, String department) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.className = className;
        this.department = department;
    }

    // Getters and Setters
    public String getName() { return name; }
    public int getRollNumber() { return rollNumber; }
    public String getClassName() { return className; }
    public String getDepartment() { return department; }
    
    @Override
    public String toString() {
        return String.format("%-15s %-10d %-10s %-12s", name, rollNumber, className, department);
    }
}

class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    private int maths;
    private int science;
    private int english;
    private int computerScience;

    public Result(int maths, int science, int english, int computerScience) {
        if (!isValidMarks(maths) || !isValidMarks(science) || 
            !isValidMarks(english) || !isValidMarks(computerScience)) {
            throw new IllegalArgumentException("Marks must be between 0 and 100");
        }
        this.maths = maths;
        this.science = science;
        this.english = english;
        this.computerScience = computerScience;
    }

    private boolean isValidMarks(int marks) {
        return marks >= 0 && marks <= 100;
    }

    public int getMaths() { return maths; }
    public int getScience() { return science; }
    public int getEnglish() { return english; }
    public int getComputerScience() { return computerScience; }

    public int totalMarks() {
        return maths + science + english + computerScience;
    }

    public float averageMarks() {
        return totalMarks() / 4.0f;
    }

    public String grade() {
        float avg = averageMarks();
        if (avg >= 90) return "A+";
        else if (avg >= 80) return "A";
        else if (avg >= 70) return "B";
        else if (avg >= 60) return "C";
        else if (avg >= 50) return "D";
        else if (avg >= 40) return "E";
        else return "F";
    }

    public String getRemarks() {
        return switch (grade()) {
            case "A+" -> "Outstanding";
            case "A" -> "Excellent";
            case "B" -> "Very Good";
            case "C" -> "Good";
            case "D" -> "Average";
            case "E" -> "Poor";
            case "F" -> "Fail";
            default -> "No Remarks";
        };
    }
    
    public boolean isPass() {
        return maths >= 40 && science >= 40 && english >= 40 && computerScience >= 40;
    }
}

class StudentRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private Student student;
    private Result result;

    public StudentRecord(Student student, Result result) {
        this.student = student;
        this.result = result;
    }

    public Student getStudent() { return student; }
    public Result getResult() { return result; }
}

// Custom Exception
class StudentManagementException extends Exception {
    public StudentManagementException(String message) {
        super(message);
    }
}

// Implement the DataStorage interface
class FileDataStorage implements DataStorage {
    private static final String DATA_FILE = "student_data.ser";
    
    @Override
    public void saveData(StudentRecord[] records) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(records);
        }
    }
    
    @Override
    public StudentRecord[] loadData() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            return (StudentRecord[]) ois.readObject();
        }
    }
}

interface DataStorage {
    void saveData(StudentRecord[] records) throws IOException;
    StudentRecord[] loadData() throws IOException, ClassNotFoundException;
}

public class EnhancedStudentResultManagement {
    private static Scanner sc = new Scanner(System.in);
    private static StudentRecord[] records = new StudentRecord[0];
    private static final FileDataStorage dataStorage = new FileDataStorage();

    public static void main(String[] args) {
        System.out.println("=== Enhanced Student Result Management System ===");
        
        try {
            // Try to load existing data using the DataStorage implementation
            records = dataStorage.loadData();
            System.out.println("Loaded existing student data.");
        } catch (Exception e) {
            System.out.println("No existing data found. Starting fresh.");
        }

        if (records.length == 0) {
            System.out.println("Please enter student data first.");
            records = inputStudentData();
        }

        boolean exit = false;
        while (!exit) {
            displayMenu();
            int choice = getValidatedIntInput("Enter your choice: ");
            
            try {
                switch (choice) {
                    case 1 -> displayAllStudents();
                    case 2 -> displaySortedByTotalMarks();
                    case 3 -> displaySortedByName();
                    case 4 -> searchStudent();
                    case 5 -> displayClassStatistics();
                    case 6 -> displayTopper();
                    case 7 -> displayFailedStudents();
                    case 8 -> addNewStudent();
                    case 9 -> updateStudentMarks();
                    case 10 -> deleteStudent();
                    case 11 -> generateReportCard();
                    case 12 -> displaySubjectWiseAnalysis();
                    case 13 -> saveDataToFile();
                    case 14 -> exit = true;
                    default -> System.out.println("Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Thank you for using the system!");
    }

    private static void displayMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. Display All Students");
        System.out.println("2. Display Sorted by Total Marks");
        System.out.println("3. Display Sorted by Name");
        System.out.println("4. Search Student");
        System.out.println("5. Class Statistics");
        System.out.println("6. Display Topper");
        System.out.println("7. Display Failed Students");
        System.out.println("8. Add New Student");
        System.out.println("9. Update Student Marks");
        System.out.println("10. Delete Student");
        System.out.println("11. Generate Report Card");
        System.out.println("12. Subject-wise Analysis");
        System.out.println("13. Save Data to File");
        System.out.println("14. Exit");
    }

    // File Operations using DataStorage implementation
    private static void saveDataToFile() {
        try {
            dataStorage.saveData(records);
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // Add student functionality
    private static void addNewStudent() {
        System.out.println("\n=== Add New Student ===");
        
        System.out.print("Name: ");
        String name = sc.nextLine();
        
        int roll = getValidatedIntInput("Roll Number: ");
        
        // Check if roll number already exists
        for (StudentRecord rec : records) {
            if (rec.getStudent().getRollNumber() == roll) {
                System.out.println("Error: Roll number already exists!");
                return;
            }
        }
        
        System.out.print("Class: ");
        String cls = sc.nextLine();
        
        System.out.print("Department: ");
        String dept = sc.nextLine();

        int maths = getValidatedMarkInput("Marks in Maths: ");
        int science = getValidatedMarkInput("Marks in Science: ");
        int english = getValidatedMarkInput("Marks in English: ");
        int cs = getValidatedMarkInput("Marks in Computer Science: ");

        try {
            Student s = new Student(name, roll, cls, dept);
            Result r = new Result(maths, science, english, cs);
            StudentRecord newRecord = new StudentRecord(s, r);
            
            // Add to records array
            records = Arrays.copyOf(records, records.length + 1);
            records[records.length - 1] = newRecord;
            
            System.out.println("Student added successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Update marks functionality
    private static void updateStudentMarks() {
        System.out.print("Enter roll number to update marks: ");
        int roll = sc.nextInt();
        sc.nextLine();
        
        for (int i = 0; i < records.length; i++) {
            if (records[i].getStudent().getRollNumber() == roll) {
                System.out.println("Updating marks for: " + records[i].getStudent().getName());
                
                int maths = getValidatedMarkInput("New Maths marks: ");
                int science = getValidatedMarkInput("New Science marks: ");
                int english = getValidatedMarkInput("New English marks: ");
                int cs = getValidatedMarkInput("New Computer Science marks: ");
                
                try {
                    Result newResult = new Result(maths, science, english, cs);
                    records[i] = new StudentRecord(records[i].getStudent(), newResult);
                    System.out.println("Marks updated successfully!");
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                return;
            }
        }
        System.out.println("Student with roll number " + roll + " not found!");
    }

    // NEW: Delete student functionality
    private static void deleteStudent() {
        System.out.print("Enter roll number to delete: ");
        int roll = sc.nextInt();
        sc.nextLine();
        
        for (int i = 0; i < records.length; i++) {
            if (records[i].getStudent().getRollNumber() == roll) {
                System.out.println("Are you sure you want to delete " + records[i].getStudent().getName() + "? (yes/no)");
                String confirmation = sc.nextLine();
                
                if (confirmation.equalsIgnoreCase("yes")) {
                    // Create new array without the student to delete
                    StudentRecord[] newRecords = new StudentRecord[records.length - 1];
                    for (int j = 0, k = 0; j < records.length; j++) {
                        if (j != i) {
                            newRecords[k++] = records[j];
                        }
                    }
                    records = newRecords;
                    System.out.println("Student deleted successfully!");
                } else {
                    System.out.println("Deletion cancelled.");
                }
                return;
            }
        }
        System.out.println("Student with roll number " + roll + " not found!");
    }

    // NEW: Subject-wise analysis
    private static void displaySubjectWiseAnalysis() {
        int mathsSum = 0, scienceSum = 0, englishSum = 0, csSum = 0;
        int mathsMax = Integer.MIN_VALUE, scienceMax = Integer.MIN_VALUE;
        int englishMax = Integer.MIN_VALUE, csMax = Integer.MIN_VALUE;
        int mathsMin = Integer.MAX_VALUE, scienceMin = Integer.MAX_VALUE;
        int englishMin = Integer.MAX_VALUE, csMin = Integer.MAX_VALUE;
        int mathsPass = 0, sciencePass = 0, englishPass = 0, csPass = 0;
        
        for (StudentRecord rec : records) {
            // Maths
            int maths = rec.getResult().getMaths();
            mathsSum += maths;
            mathsMax = Math.max(mathsMax, maths);
            mathsMin = Math.min(mathsMin, maths);
            if (maths >= 40) mathsPass++;
            
            // Science
            int science = rec.getResult().getScience();
            scienceSum += science;
            scienceMax = Math.max(scienceMax, science);
            scienceMin = Math.min(scienceMin, science);
            if (science >= 40) sciencePass++;
            
            // English
            int english = rec.getResult().getEnglish();
            englishSum += english;
            englishMax = Math.max(englishMax, english);
            englishMin = Math.min(englishMin, english);
            if (english >= 40) englishPass++;
            
            // Computer Science
            int cs = rec.getResult().getComputerScience();
            csSum += cs;
            csMax = Math.max(csMax, cs);
            csMin = Math.min(csMin, cs);
            if (cs >= 40) csPass++;
        }
        
        int totalStudents = records.length;
        
        System.out.println("\n===== Subject-wise Analysis =====");
        System.out.printf("%-15s %-8s %-8s %-8s %-12s\n", 
            "Subject", "Average", "Highest", "Lowest", "Pass %");
        System.out.println("=".repeat(55));
        
        System.out.printf("%-15s %-8.2f %-8d %-8d %-12.2f\n", 
            "Mathematics", mathsSum/(float)totalStudents, mathsMax, mathsMin, 
            (mathsPass/(float)totalStudents)*100);
            
        System.out.printf("%-15s %-8.2f %-8d %-8d %-12.2f\n", 
            "Science", scienceSum/(float)totalStudents, scienceMax, scienceMin, 
            (sciencePass/(float)totalStudents)*100);
            
        System.out.printf("%-15s %-8.2f %-8d %-8d %-12.2f\n", 
            "English", englishSum/(float)totalStudents, englishMax, englishMin, 
            (englishPass/(float)totalStudents)*100);
            
        System.out.printf("%-15s %-8.2f %-8d %-8d %-12.2f\n", 
            "Computer Science", csSum/(float)totalStudents, csMax, csMin, 
            (csPass/(float)totalStudents)*100);
    }

    // Generate detailed report card
    private static void generateReportCard() {
        int roll = getValidatedIntInput("Enter roll number for report card: ");
        
        for (StudentRecord rec : records) {
            if (rec.getStudent().getRollNumber() == roll) {
                System.out.println("\n" + "=".repeat(50));
                System.out.println("           REPORT CARD");
                System.out.println("=".repeat(50));
                System.out.println("Name: " + rec.getStudent().getName());
                System.out.println("Roll No: " + rec.getStudent().getRollNumber());
                System.out.println("Class: " + rec.getStudent().getClassName());
                System.out.println("Department: " + rec.getStudent().getDepartment());
                System.out.println("-".repeat(50));
                System.out.println("SUBJECTS\tMARKS\tSTATUS");
                System.out.println("-".repeat(50));
                System.out.printf("Mathematics\t%d\t%s\n", rec.getResult().getMaths(), 
                    rec.getResult().getMaths() >= 40 ? "PASS" : "FAIL");
                System.out.printf("Science\t\t%d\t%s\n", rec.getResult().getScience(),
                    rec.getResult().getScience() >= 40 ? "PASS" : "FAIL");
                System.out.printf("English\t\t%d\t%s\n", rec.getResult().getEnglish(),
                    rec.getResult().getEnglish() >= 40 ? "PASS" : "FAIL");
                System.out.printf("Computer Science\t%d\t%s\n", rec.getResult().getComputerScience(),
                    rec.getResult().getComputerScience() >= 40 ? "PASS" : "FAIL");
                System.out.println("-".repeat(50));
                System.out.printf("TOTAL MARKS: %d/400\n", rec.getResult().totalMarks());
                System.out.printf("AVERAGE: %.2f%%\n", rec.getResult().averageMarks());
                System.out.printf("GRADE: %s\n", rec.getResult().grade());
                System.out.printf("REMARKS: %s\n", rec.getResult().getRemarks());
                System.out.printf("OVERALL STATUS: %s\n", rec.getResult().isPass() ? "PASS" : "FAIL");
                System.out.println("=".repeat(50));
                return;
            }
        }
        System.out.println("Student not found!");
    }

    // Input student data
    private static StudentRecord[] inputStudentData() {
        int n = getValidatedIntInput("Enter number of students: ");
        
        if (n <= 0) {
            System.out.println("Invalid number of students.");
            return new StudentRecord[0];
        }

        StudentRecord[] newRecords = new StudentRecord[n];

        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for student " + (i + 1));
            
            System.out.print("Name: ");
            String name = sc.nextLine();
            
            int roll = getValidatedIntInput("Roll Number: ");
            
            System.out.print("Class: ");
            String cls = sc.nextLine();
            
            System.out.print("Department: ");
            String dept = sc.nextLine();

            int maths = getValidatedMarkInput("Marks in Maths: ");
            int science = getValidatedMarkInput("Marks in Science: ");
            int english = getValidatedMarkInput("Marks in English: ");
            int cs = getValidatedMarkInput("Marks in Computer Science: ");

            try {
                Student s = new Student(name, roll, cls, dept);
                Result r = new Result(maths, science, english, cs);
                newRecords[i] = new StudentRecord(s, r);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                i--;
            }
        }
        return newRecords;
    }

    private static int getValidatedIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = sc.nextInt();
                sc.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid integer.");
                sc.nextLine();
            }
        }
    }

    private static int getValidatedMarkInput(String prompt) {
        while (true) {
            int mark = getValidatedIntInput(prompt);
            if (mark >= 0 && mark <= 100) {
                return mark;
            }
            System.out.println("Marks must be between 0 and 100. Please try again.");
        }
    }

    private static void displayAllStudents() {
        System.out.println("\n===== All Student Results =====");
        displayStudentTable(records);
    }

    private static void displaySortedByTotalMarks() {
        StudentRecord[] sortedRecords = records.clone();
        Arrays.sort(sortedRecords, (a, b) -> 
            b.getResult().totalMarks() - a.getResult().totalMarks());
        
        System.out.println("\n===== Student Results (Sorted by Total Marks) =====");
        displayStudentTable(sortedRecords);
    }

    private static void displaySortedByName() {
        StudentRecord[] sortedRecords = records.clone();
        Arrays.sort(sortedRecords, (a, b) -> 
            a.getStudent().getName().compareToIgnoreCase(b.getStudent().getName()));
        
        System.out.println("\n===== Student Results (Sorted by Name) =====");
        displayStudentTable(sortedRecords);
    }

    private static void displayStudentTable(StudentRecord[] records) {
        System.out.printf("%-15s %-10s %-10s %-12s %-7s %-8s %-8s %-10s %-7s %-8s %-6s %-12s\n",
                "Name", "Roll No", "Class", "Department", "Maths", "Science", "English", "Comp Sci", "Total", "Average", "Grade", "Remarks");
        System.out.println("=".repeat(130));

        for (StudentRecord rec : records) {
            System.out.printf("%-15s %-10d %-10s %-12s %-7d %-8d %-8d %-10d %-7d %-8.2f %-6s %-12s\n",
                    rec.getStudent().getName(),
                    rec.getStudent().getRollNumber(),
                    rec.getStudent().getClassName(),
                    rec.getStudent().getDepartment(),
                    rec.getResult().getMaths(),
                    rec.getResult().getScience(),
                    rec.getResult().getEnglish(),
                    rec.getResult().getComputerScience(),
                    rec.getResult().totalMarks(),
                    rec.getResult().averageMarks(),
                    rec.getResult().grade(),
                    rec.getResult().getRemarks());
        }
    }

    private static void searchStudent() {
        System.out.print("Enter student name or roll number to search: ");
        String searchTerm = sc.nextLine();
        
        boolean found = false;
        
        for (StudentRecord rec : records) {
            if (rec.getStudent().getName().equalsIgnoreCase(searchTerm) || 
                String.valueOf(rec.getStudent().getRollNumber()).equals(searchTerm)) {
                
                if (!found) {
                    System.out.println("\n===== Search Results =====");
                    System.out.printf("%-15s %-10s %-10s %-12s %-7s %-8s %-8s %-10s %-7s %-8s %-6s\n",
                            "Name", "Roll No", "Class", "Department", "Maths", "Science", "English", "Comp Sci", "Total", "Average", "Grade");
                    System.out.println("=".repeat(110));
                    found = true;
                }
                
                System.out.printf("%-15s %-10d %-10s %-12s %-7d %-8d %-8d %-10d %-7d %-8.2f %-6s\n",
                        rec.getStudent().getName(),
                        rec.getStudent().getRollNumber(),
                        rec.getStudent().getClassName(),
                        rec.getStudent().getDepartment(),
                        rec.getResult().getMaths(),
                        rec.getResult().getScience(),
                        rec.getResult().getEnglish(),
                        rec.getResult().getComputerScience(),
                        rec.getResult().totalMarks(),
                        rec.getResult().averageMarks(),
                        rec.getResult().grade());
            }
        }
        
        if (!found) {
            System.out.println("No student found with the given search term.");
        }
    }

    private static void displayClassStatistics() {
        int totalStudents = records.length;
        int passed = 0;
        int totalMarksSum = 0;
        int maxTotal = Integer.MIN_VALUE;
        int minTotal = Integer.MAX_VALUE;

        for (StudentRecord rec : records) {
            int total = rec.getResult().totalMarks();
            totalMarksSum += total;
            maxTotal = Math.max(maxTotal, total);
            minTotal = Math.min(minTotal, total);
            
            if (rec.getResult().isPass()) {
                passed++;
            }
        }

        float classAverage = totalMarksSum / (float) totalStudents;
        float passPercentage = (passed / (float) totalStudents) * 100;

        System.out.println("\n===== Class Statistics =====");
        System.out.println("Total Students: " + totalStudents);
        System.out.println("Class Average: " + String.format("%.2f", classAverage));
        System.out.println("Highest Total: " + maxTotal);
        System.out.println("Lowest Total: " + minTotal);
        System.out.println("Students Passed: " + passed);
        System.out.println("Pass Percentage: " + String.format("%.2f%%", passPercentage));
    }

    private static void displayTopper() {
        if (records.length == 0) {
            System.out.println("No students available.");
            return;
        }
        
        StudentRecord topper = records[0];
        for (StudentRecord rec : records) {
            if (rec.getResult().totalMarks() > topper.getResult().totalMarks()) {
                topper = rec;
            }
        }

        System.out.println("\n===== Class Topper =====");
        System.out.printf("%-15s %-10s %-10s %-12s %-7s %-8s %-8s %-10s %-7s %-8s %-6s\n",
                "Name", "Roll No", "Class", "Department", "Maths", "Science", "English", "Comp Sci", "Total", "Average", "Grade");
        System.out.println("=".repeat(110));
        System.out.printf("%-15s %-10d %-10s %-12s %-7d %-8d %-8d %-10d %-7d %-8.2f %-6s\n",
                topper.getStudent().getName(),
                topper.getStudent().getRollNumber(),
                topper.getStudent().getClassName(),
                topper.getStudent().getDepartment(),
                topper.getResult().getMaths(),
                topper.getResult().getScience(),
                topper.getResult().getEnglish(),
                topper.getResult().getComputerScience(),
                topper.getResult().totalMarks(),
                topper.getResult().averageMarks(),
                topper.getResult().grade());
    }

    private static void displayFailedStudents() {
        boolean hasFailures = false;
        
        for (StudentRecord rec : records) {
            if (!rec.getResult().isPass()) {
                if (!hasFailures) {
                    System.out.println("\n===== Failed Students =====");
                    System.out.printf("%-15s %-10s %-10s %-12s %-7s %-8s %-8s %-10s %-7s %-8s %-6s\n",
                            "Name", "Roll No", "Class", "Department", "Maths", "Science", "English", "Comp Sci", "Total", "Average", "Grade");
                    System.out.println("=".repeat(110));
                    hasFailures = true;
                }
                
                System.out.printf("%-15s %-10d %-10s %-12s %-7d %-8d %-8d %-10d %-7d %-8.2f %-6s\n",
                        rec.getStudent().getName(),
                        rec.getStudent().getRollNumber(),
                        rec.getStudent().getClassName(),
                        rec.getStudent().getDepartment(),
                        rec.getResult().getMaths(),
                        rec.getResult().getScience(),
                        rec.getResult().getEnglish(),
                        rec.getResult().getComputerScience(),
                        rec.getResult().totalMarks(),
                        rec.getResult().averageMarks(),
                        rec.getResult().grade());
            }
        }
        
        if (!hasFailures) {
            System.out.println("No failed students! All students passed.");
        }
    }
}




