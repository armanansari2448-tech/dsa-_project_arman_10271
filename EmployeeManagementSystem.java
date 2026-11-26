import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Employee Management System
 * DSA Assignment: Singly Linked List + HashMap + Searching + Sorting + File Handling
 *
 * Compile: javac EmployeeManagementSystem.java
 * Run:     java EmployeeManagementSystem
 */
public class EmployeeManagementSystem {

    // ---------- Terminal theme ----------
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";
    private static final String CYAN = "\u001B[96m";
    private static final String BLUE = "\u001B[94m";
    private static final String GREEN = "\u001B[92m";
    private static final String YELLOW = "\u001B[93m";
    private static final String RED = "\u001B[91m";
    private static final String WHITE = "\u001B[97m";
    private static final String MAGENTA = "\u001B[95m";

    private static final String FILE_NAME = "employees.txt";
    private static final Scanner scanner = new Scanner(System.in);

    private static final EmployeeList employees = new EmployeeList();
    private static final Map<String, Employee> employeeIndex = new HashMap<>();
    private static int nextId = 101;

    // ---------- Employee model ----------
    static class Employee {
        private final String id;
        private final String name;
        private final String department;
        private final long salary;

        Employee(String id, String name, String department, long salary) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        String getId() { return id; }
        String getName() { return name; }
        String getDepartment() { return department; }
        long getSalary() { return salary; }
    }

    // ---------- Custom singly linked list ----------
    static class Node {
        Employee data;
        Node next;

        Node(Employee data) {
            this.data = data;
        }
    }

    static class EmployeeList {
        private Node head;
        private int size;

        void add(Employee employee) {
            Node newNode = new Node(employee);

            if (head == null) {
                head = newNode;
            } else {
                Node current = head;
                while (current.next != null) {
                    current = current.next;
                }
                current.next = newNode;
            }
            size++;
        }

        boolean deleteById(String id) {
            if (head == null) return false;

            if (head.data.getId().equalsIgnoreCase(id)) {
                head = head.next;
                size--;
                return true;
            }

            Node current = head;
            while (current.next != null) {
                if (current.next.data.getId().equalsIgnoreCase(id)) {
                    current.next = current.next.next;
                    size--;
                    return true;
                }
                current = current.next;
            }
            return false;
        }

        Employee searchByName(String name) {
            Node current = head;
            while (current != null) {
                if (current.data.getName().equalsIgnoreCase(name)
                        || current.data.getName().toLowerCase(Locale.ROOT)
                        .contains(name.toLowerCase(Locale.ROOT))) {
                    return current.data;
                }
                current = current.next;
            }
            return null;
        }

        List<Employee> toList() {
            List<Employee> result = new ArrayList<>();
            Node current = head;
            while (current != null) {
                result.add(current.data);
                current = current.next;
            }
            return result;
        }

        int size() { return size; }
        boolean isEmpty() { return head == null; }
    }

    // ---------- Application entry point ----------
    public static void main(String[] args) {
        loadFromFile();

        while (true) {
            showHeader();
            showMenu();
            int choice = readInt("  Select an option  › ", 1, 7);

            switch (choice) {
                case 1: addEmployee(); pause(); break;
                case 2: displayEmployees(); pause(); break;
                case 3: searchById(); pause(); break;
                case 4: deleteEmployee(); pause(); break;
                case 5: sortBySalary(); pause(); break;
                case 6: searchByName(); pause(); break;
                case 7:
                    saveToFile();
                    System.out.println();
                    System.out.println(GREEN + BOLD + "  ✓  All changes saved. Goodbye!" + RESET);
                    System.out.println(DIM + "     Employee Management System • Java DSA Project" + RESET);
                    return;
                default:
                    // readInt already validates the menu range.
                    break;
            }
        }
    }

    private static void showHeader() {
        clearScreen();
        System.out.println(CYAN + BOLD);
        System.out.println("  ╔══════════════════════════════════════════════════════════════╗");
        System.out.println("  ║                                                              ║");
        System.out.println("  ║        E M P L O Y E E   M A N A G E M E N T   S Y S T E M   ║");
        System.out.println("  ║                                                              ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println("  " + DIM + "DSA CONSOLE SUITE" + RESET + "  " + BLUE + "●" + RESET
                + "  " + DIM + "Singly Linked List  |  HashMap  |  File Storage" + RESET);
        System.out.println("  " + "─".repeat(64));
        System.out.println("  " + WHITE + BOLD + "WORKFORCE OVERVIEW" + RESET);
        System.out.println("  " + GREEN + BOLD + String.format("%02d", employees.size()) + RESET
                + DIM + " employees currently in the system" + RESET);
        System.out.println();
    }

    private static void showMenu() {
        String[][] items = {
            {"1", "ADD EMPLOYEE", "Create a new employee record"},
            {"2", "VIEW DIRECTORY", "Display every employee"},
            {"3", "FIND BY ID", "Fast lookup using HashMap"},
            {"4", "REMOVE EMPLOYEE", "Delete a record by employee ID"},
            {"5", "SALARY RANKING", "Sort salary from high to low"},
            {"6", "FIND BY NAME", "Search names in the linked list"},
            {"7", "SAVE & EXIT", "Store records and close the system"}
        };

        System.out.println("  " + BOLD + "MAIN MENU" + RESET);
        System.out.println("  " + DIM + "Choose an operation to continue" + RESET);
        System.out.println();
        for (String[] item : items) {
            String numberColor = item[0].equals("7") ? YELLOW : CYAN;
            System.out.printf("  %s[%s]%s  %-22s %s%s%s%n",
                    numberColor, item[0], RESET, item[1], DIM, item[2], RESET);
        }
        System.out.println();
        System.out.println("  " + "─".repeat(64));
    }

    private static void addEmployee() {
        section("NEW EMPLOYEE", "Add a person to the employee directory");

        String name = readNonEmpty("  Full name       › ");
        String department = readNonEmpty("  Department      › ");
        long salary = readPositiveLong("  Monthly salary  › ");

        String id = "E" + nextId++;
        Employee employee = new Employee(id, name, department, salary);
        employees.add(employee);
        employeeIndex.put(id, employee);
        saveToFile();

        System.out.println();
        System.out.println(GREEN + BOLD + "  ✓ EMPLOYEE CREATED SUCCESSFULLY" + RESET);
        System.out.println("  " + DIM + "Generated employee ID" + RESET + "  " + CYAN + BOLD + id + RESET);
        printEmployeeCard(employee);
    }

    private static void displayEmployees() {
        section("EMPLOYEE DIRECTORY", "A complete view of your current workforce");

        if (employees.isEmpty()) {
            emptyState("No employee records yet. Add your first employee to get started.");
            return;
        }

        printTable(employees.toList());
        System.out.println();
        System.out.println("  " + DIM + "Showing " + employees.size() + " employee record(s)." + RESET);
    }

    private static void searchById() {
        section("EMPLOYEE LOOKUP", "Find a record instantly using its unique ID");

        String id = readNonEmpty("  Employee ID  › ").toUpperCase(Locale.ROOT);
        Employee employee = employeeIndex.get(id);

        System.out.println();
        if (employee == null) {
            System.out.println(RED + BOLD + "  ✕  Employee " + id + " not found." + RESET);
            System.out.println(DIM + "     Check the ID and try again." + RESET);
        } else {
            System.out.println(GREEN + BOLD + "  ✓  MATCH FOUND" + RESET);
            printEmployeeCard(employee);
        }
    }

    private static void deleteEmployee() {
        section("REMOVE EMPLOYEE", "Permanently remove a record from the directory");

        String id = readNonEmpty("  Employee ID to delete  › ").toUpperCase(Locale.ROOT);
        Employee employee = employeeIndex.get(id);

        if (employee == null) {
            System.out.println();
            System.out.println(RED + BOLD + "  ✕  Employee " + id + " not found." + RESET);
            return;
        }

        System.out.println();
        System.out.println(YELLOW + "  Record selected: " + employee.getName()
                + "  •  " + employee.getId() + RESET);
        String confirmation = readNonEmpty("  Confirm deletion? (yes/no)  › ");

        if (confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y")) {
            employees.deleteById(id);
            employeeIndex.remove(id);
            saveToFile();
            System.out.println();
            System.out.println(GREEN + BOLD + "  ✓  Employee " + id + " deleted." + RESET);
            System.out.println("  Total employees: " + employees.size());
        } else {
            System.out.println(DIM + "  Deletion cancelled. No changes made." + RESET);
        }
    }

    private static void sortBySalary() {
        section("SALARY RANKING", "Employees ranked from highest salary to lowest");

        List<Employee> sorted = employees.toList();
        if (sorted.isEmpty()) {
            emptyState("There are no employees to rank yet.");
            return;
        }

        // Sorting is performed on a copy; the linked-list insertion order is preserved.
        sorted.sort(Comparator.comparingLong(Employee::getSalary).reversed()
                .thenComparing(Employee::getName, String.CASE_INSENSITIVE_ORDER));

        printTable(sorted);
        System.out.println();
        System.out.println("  " + MAGENTA + "★  TOP EARNER  " + RESET
                + BOLD + sorted.get(0).getName() + RESET
                + DIM + "  •  " + RESET + formatSalary(sorted.get(0).getSalary()));
    }

    private static void searchByName() {
        section("NAME SEARCH", "Search employee records by full or partial name");

        String name = readNonEmpty("  Enter a name  › ");
        Employee employee = employees.searchByName(name);

        System.out.println();
        if (employee == null) {
            System.out.println(RED + BOLD + "  ✕  No employee matching \"" + name + "\" was found." + RESET);
        } else {
            System.out.println(GREEN + BOLD + "  ✓  MATCH FOUND" + RESET);
            printEmployeeCard(employee);
        }
    }

    // ---------- Display helpers ----------
    private static void section(String title, String subtitle) {
        System.out.println();
        System.out.println(CYAN + BOLD + "  ┌─ " + title + " " + "─".repeat(Math.max(2, 52 - title.length())) + "┐" + RESET);
        System.out.println("  " + DIM + subtitle + RESET);
        System.out.println();
    }

    private static void printEmployeeCard(Employee e) {
        System.out.println();
        System.out.println("  " + "┌" + "─".repeat(48) + "┐");
        System.out.printf("  │  %-44s│%n", CYAN + BOLD + e.getName().toUpperCase(Locale.ROOT) + RESET);
        System.out.println("  ├" + "─".repeat(48) + "┤");
        System.out.printf("  │  %-13s %s%-27s│%n", "EMPLOYEE ID", CYAN, e.getId() + RESET);
        System.out.printf("  │  %-13s %-27s│%n", "DEPARTMENT", e.getDepartment());
        System.out.printf("  │  %-13s %s%-27s│%n", "SALARY", GREEN + BOLD,
                formatSalary(e.getSalary()) + RESET);
        System.out.println("  " + "└" + "─".repeat(48) + "┘");
    }

    private static void printTable(List<Employee> list) {
        String line = "  +" + "-".repeat(10) + "+" + "-".repeat(25) + "+"
                + "-".repeat(17) + "+" + "-".repeat(17) + "+";
        System.out.println(CYAN + line + RESET);
        System.out.printf("  | %-8s | %-23s | %-15s | %-15s |%n",
                "ID", "EMPLOYEE NAME", "DEPARTMENT", "SALARY");
        System.out.println(CYAN + line + RESET);

        for (Employee e : list) {
            System.out.printf("  | %-8s | %-23s | %-15s | %15s |%n",
                    shorten(e.getId(), 8), shorten(e.getName(), 23),
                    shorten(e.getDepartment(), 15), formatSalary(e.getSalary()));
        }
        System.out.println(CYAN + line + RESET);
    }

    private static String shorten(String value, int max) {
        if (value.length() <= max) return value;
        return value.substring(0, max - 3) + "...";
    }

    private static String formatSalary(long salary) {
        return "₹" + String.format(Locale.US, "%,d", salary);
    }

    private static void emptyState(String message) {
        System.out.println(YELLOW + "  ◇  " + message + RESET);
    }

    // ---------- Input validation ----------
    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty() && !value.contains("\t")) return value;
            System.out.println(RED + "     Please enter a valid, non-empty value." + RESET);
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) {
                // Show the same friendly validation message below.
            }
            System.out.println(RED + "     Enter a number between " + min + " and " + max + "." + RESET);
        }
    }

    private static long readPositiveLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                long value = Long.parseLong(input);
                if (value > 0) return value;
            } catch (NumberFormatException ignored) {
                // Show the same friendly validation message below.
            }
            System.out.println(RED + "     Salary must be a valid number greater than zero." + RESET);
        }
    }

    private static void pause() {
        System.out.println();
        System.out.print(DIM + "  Press ENTER to return to the main menu..." + RESET);
        scanner.nextLine();
    }

    private static void clearScreen() {
        // ANSI clear works in VS Code's integrated terminal and modern Windows Terminal.
        System.out.print("\u001B[2J\u001B[H");
    }

    // ---------- File persistence ----------
    private static void loadFromFile() {
        Path path = Paths.get(FILE_NAME);
        if (!Files.exists(path)) return;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\t", -1);
                if (parts.length != 4) continue;

                try {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String department = parts[2].trim();
                    long salary = Long.parseLong(parts[3].trim());

                    if (id.isEmpty() || name.isEmpty() || department.isEmpty()
                            || salary <= 0 || employeeIndex.containsKey(id)) {
                        continue;
                    }

                    Employee employee = new Employee(id, name, department, salary);
                    employees.add(employee);
                    employeeIndex.put(id, employee);

                    if (id.matches("E\\d+")) {
                        int numericId = Integer.parseInt(id.substring(1));
                        nextId = Math.max(nextId, numericId + 1);
                    }
                } catch (NumberFormatException ignored) {
                    // Skip malformed records without stopping the application.
                }
            }
        } catch (IOException e) {
            System.out.println(RED + "Could not load employees.txt: " + e.getMessage() + RESET);
        }
    }

    private static void saveToFile() {
        Path path = Paths.get(FILE_NAME);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (Employee e : employees.toList()) {
                writer.write(e.getId() + "\t" + e.getName() + "\t"
                        + e.getDepartment() + "\t" + e.getSalary());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(RED + "  ! Could not save employee data: " + e.getMessage() + RESET);
        }
    }
}
