# Employee Management System

A polished Java console application built for a Data Structures & Algorithms assignment.

## Features

- Add employees with automatically generated IDs (`E101`, `E102`, ...).
- Display all employee records in a formatted directory.
- Search by employee ID using a `HashMap`.
- Search by full or partial name by traversing a custom singly linked list.
- Delete employees by ID.
- Sort employees by salary (highest to lowest).
- Validate input and reject empty fields and salaries less than or equal to zero.
- Load records from `employees.txt` at startup and save changes to the file.

## Data Structures & Concepts

| Requirement | Implementation |
|---|---|
| Employee storage | Custom singly linked list |
| Node | Each node stores one `Employee` object |
| Fast ID lookup | `HashMap<String, Employee>` |
| Name search | Linked-list traversal |
| Salary sorting | Java `Comparator` on a copy of the linked-list data |
| Persistence | Tab-separated `employees.txt` file |

The project does **not** use `java.util.LinkedList`.

## Requirements

- JDK 8 or later
- VS Code (or any Java-compatible editor)

## Run in VS Code Terminal

Open this folder in VS Code, then run:

```powershell
javac EmployeeManagementSystem.java
java EmployeeManagementSystem
```

On first run, the program creates `employees.txt` when data is saved. Keep this file in the project folder so records persist between runs.

## File Format

Each employee is stored on one line:

```text
E101    Anil Rao    IT    50000
```

The actual file uses tab separators.

## Suggested Test Run

1. Choose `1` and add Anil Rao, IT, 50000.
2. Choose `3` and search for `E999` (not found).
3. Choose `4` and delete `E101` (confirm with `yes`).
4. Add Sara Khan, HR, 60000.
5. Choose `5` to see salary order.
6. Choose `6` and search `Anil`.
7. Choose `7` to save and exit.

## Notes

- IDs begin at `E101` and continue from the highest ID found in the data file.
- Salary sorting does not change the linked list's original insertion order.
- Employee data is saved after add/delete and again on exit.
