package sprint_2.task_tracker;

import java.util.*;

import static sprint_2.task_tracker.FileBackedTasksManager.loadFromFile;

public class InMemoryTaskManager implements TaskManager {
    static Scanner scanner = new Scanner(System.in);
    static HashMap<Integer, Task> mapOfTasks = new HashMap<>();
    static HashMap<Integer, Epic> mapOfEpics = new HashMap<>();
    static int idTask = 1;
    static int idEpic = 1;
    static int idSubtask = 1;

    public static void main(String[] args) {
        loadFromFile();
        getUserChoice();
    }

    private static int inputNumber(int MAX_NUM, String str) {
        final int MIN_NUM = 1;
        int num = 0;
        boolean isIncorrect;
        do {
            isIncorrect = false;
            System.out.print(str);
            try {
                num = Integer.parseInt(scanner.nextLine());
            } catch (Exception q) {
                isIncorrect = true;
                System.out.println("You didn't enter number. Input must consist only digits.");
            }
            if (!isIncorrect & (num < MIN_NUM || num > MAX_NUM)) {
                isIncorrect = true;
                System.out.println("Invalid input range.");
            }
        } while (isIncorrect);
        return num;
    }

    private static void outputMenu() {
        System.out.println("1 - Create a record.");
        System.out.println("2 - Output list of records.");
        System.out.println("3 - Delete list of records.");
        System.out.println("4 - Get a record by id.");
        System.out.println("5 - Update status of record.");
        System.out.println("6 - Delete a record by id.");
        System.out.println("7 - Watch history.");
        System.out.println("8 - Delete a record from history by id.");
        System.out.println("9 - Exit.");
    }

    private static void outputTypeOfRecords(String str) {
        System.out.println("1 - " + str + " a task.");
        System.out.println("2 - " + str + " an epic.");
        System.out.println("3 - " + str + " a subtask.");
        System.out.println("4 - Return to menu.");
    }

    private static void outputRecordsToUpdate() {
        System.out.println("1 - Update a task.");
        System.out.println("2 - Update a subtask.");
        System.out.println("3 - Return to menu.");
    }

    private static void getUserChoice() {
        int num = 0;
        while (num != 9) {
            outputMenu();
            num = inputNumber(9, "Input your choice: ");
            executeUserChoice(num);
        }
    }

    private static boolean checkSmth(String str, int num) {
        boolean isIncorrect = false;
        if (str.length() > num) {
            isIncorrect = true;
            System.out.println("Input less long information.");
        }
        return isIncorrect;
    }

    private static Task getInfoFromUser(String str) {
        Task obj = null;
        boolean isIncorrect;
        String description;
        String name;
        do {
            System.out.print("Input name of the " + str + ": ");
            name = scanner.nextLine();
            isIncorrect = checkSmth(name, 12);
        } while (isIncorrect);
        do {
            System.out.print("Input description of the " + str + ": ");
            description = scanner.nextLine();
            isIncorrect = checkSmth(description, 20);
        } while (isIncorrect);
        if (str.equals("task")) {
            obj = new Task(name, description, idTask, Status.NEW);
        } else if (str.equals("epic")) {
            obj = new Epic(name, description, idEpic, Status.NEW);
        } else if (str.equals("subtask")) {
            obj = new Subtask(name, description, idSubtask, Status.NEW);
        }
        return obj;
    }

    @Override
    public TypeOfTask createRecord() {
        int num;
        Task obj;
        boolean isCorrect = true;
        num = inputNumber(4, "Input your choice: ");
        do {
            if (num == 1) {
                isCorrect = false;
                obj = getInfoFromUser("task");
                mapOfTasks.put(idTask, obj);
                idTask++;
                System.out.println("The task has been created.");
                return TypeOfTask.TASK;
            } else if (num == 2) {
                isCorrect = false;
                obj = getInfoFromUser("epic");
                mapOfEpics.put(idEpic, (Epic) obj);
                idEpic++;
                System.out.println("The epic has been created.");
                return TypeOfTask.EPIC;
            } else if (num == 3) {
                Epic epic = chooseEpic();
                if (epic != null) {
                    isCorrect = false;
                    obj = getInfoFromUser("subtask");
                    HashMap<Integer, Subtask> mapOfSubtasks = epic.getMapOfSubtasks();
                    if (mapOfSubtasks != null) {
                        int size = mapOfSubtasks.size();
                        obj.setId(size + 1);
                        mapOfSubtasks.put(size + 1, (Subtask) obj);
                        epic.setMapOfSubtasks(mapOfSubtasks);
                        System.out.println("The subtask has been created.");
                        return TypeOfTask.SUBTASK;
                    } else {
                        mapOfSubtasks = new HashMap<>();
                        mapOfSubtasks.put(1, (Subtask) obj);
                        epic.setMapOfSubtasks(mapOfSubtasks);
                    }
                } else {
                    System.out.println("Firstly add an epic.");
                    outputTypeOfRecords("Create");
                    num = inputNumber(4, "Input your choice: ");
                }
            } else {
                isCorrect = false;
            }
        } while (isCorrect);
        return null;
    }

    private static void outputTypeOfTasks(String str) {
        System.out.println("1 - " + str + " all tasks.");
        System.out.println("2 - " + str + " all epics.");
        System.out.println("3 - " + str + " all subtasks.");
        System.out.println("4 - Return to menu.");
    }

    @Override
    public void deleteAllTasks() {
        int num = inputNumber(4, "Input your choice: ");
        if (num == 1) {
            mapOfTasks.clear();
        } else if (num == 2) {
            mapOfEpics.clear();
        } else if (num == 3) {
            Epic epic = chooseEpic();
            if (epic != null) {
                epic.getMapOfSubtasks().clear();
            }
        }
    }

    private static Epic chooseEpic() {
        int size = mapOfEpics.size();
        if (size > 0) {
            outputAllRecords(mapOfEpics, "epics");
            int num = inputNumber(idEpic, "Choose epic by id: ");
            return mapOfEpics.get(num);
        } else {
            return null;
        }
    }

    @Override
    public void outputAllRecords() {
        int num = inputNumber(4, "Input your choice: ");
        if (num == 1) {
            outputAllRecords(mapOfTasks, "tasks");
        } else if (num == 2) {
            outputAllRecords(mapOfEpics, "epics");
        } else if (num == 3) {
            Epic epic = chooseEpic();
            if (epic != null) {
                HashMap<Integer, Subtask> mapOfSubtasks = epic.getMapOfSubtasks();
                outputAllRecords(mapOfSubtasks, "subtasks");
            } else {
                System.out.println("List of subtasks of this epic is empty.");
            }
        }
    }

    private static <T> void outputAllRecords(HashMap<Integer, T> map, String str) {
        if (map.size() > 0) {
            map.values().forEach(System.out::println);
        } else {
            System.out.println("List of " + str + " is empty.");
        }
    }

    private static void outputTypeOfTasksToFind(String str) {
        System.out.println("1 - " + str + " a task by id.");
        System.out.println("2 - " + str + " an epic by id.");
        System.out.println("3 - " + str + " a subtask by id.");
        System.out.println("4 - Return to menu.");
    }

    @Override
    public void getById() {
        int num = inputNumber(4, "Input your choice: ");
        HistoryManager historyManager = Managers.getDefaultHistory();
        if (num == 1) {
            Task task = getById(mapOfTasks, "task", idTask - 1);
            if (task != null) {
                System.out.println(task);
                historyManager.add(task);
                historyManager.linkLast(task);
            }
        } else if (num == 2) {
            Epic epic = getById(mapOfEpics, "epic", idEpic - 1);
            if (epic != null) {
                System.out.println(epic);
                historyManager.add(epic);
                historyManager.linkLast(epic);
            }
        } else if (num == 3) {
            Epic epic = chooseEpic();
            if (epic != null) {
                HashMap<Integer, Subtask> mapOfSubtasks = epic.getMapOfSubtasks();
                Subtask subtask = getById(mapOfSubtasks, "subtask", mapOfSubtasks.size());
                if (subtask != null) {
                    System.out.println(subtask);
                    historyManager.add(subtask);
                    historyManager.linkLast(subtask);
                }
            } else {
                System.out.println("List of subtasks of all epics is empty.");
            }
        }
    }

    private static <T> T getById(HashMap<Integer, T> map, String str, int max) {
        if (map.size() > 0) {
            int id = inputNumber(max, "Input " + str + "-id you want to find: ");
            if (map.get(id) == null) {
                System.out.println("There is no " + str + " with this id.");
            } else {
                return map.get(id);
            }
        } else {
            System.out.println("List of " + str + "s is empty.");
        }
        return null;
    }

    private static void executeUserChoice(int num) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager();
        //TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        switch (num) {
            case 1:
                outputTypeOfRecords("Create");
                taskManager.createRecord();
                taskManager.setStatusToEpic();
                break;
            case 2:
                outputTypeOfTasks("Output");
                taskManager.setStatusToEpic();
                taskManager.outputAllRecords();
                break;
            case 3:
                outputTypeOfTasks("Delete");
                taskManager.deleteAllTasks();
                break;
            case 4:
                outputTypeOfTasksToFind("Get");
                taskManager.setStatusToEpic();
                taskManager.getById();
                break;
            case 5:
                outputRecordsToUpdate();
                taskManager.setStatusToEpic();
                taskManager.updateRecords();
                break;
            case 6:
                outputTypeOfTasksToFind("Delete");
                taskManager.setStatusToEpic();
                taskManager.deleteRecordById();
                break;
            case 7:
                ArrayList<Task> list2 = historyManager.getTasks();
                outputHistory(new HashSet<>(list2));
                break;
            case 8:
                int id = inputNumber(idTask - 1, "Input id you want to delete from history: ");
                historyManager.remove(id);
                break;
            case 9:
                System.exit(0);
        }
    }

    private static void outputHistory(Set<Task> listOfHistory) {
        if (!(listOfHistory.isEmpty())) {
            for (Task elem : listOfHistory) {
                System.out.println(elem);
            }
        } else {
            System.out.println("List of history is empty.");
        }
    }

    @Override
    public void updateRecords() {
        boolean isCorrect;
        int num = inputNumber(3, "Input your choice: ");
        if (num == 1) {
            Task task = getById(mapOfTasks, "task", idTask - 1);
            if (task != null) {
                System.out.println(task);
                isCorrect = setStatus(task, "task");
                if (isCorrect) {
                    System.out.println("Updated task: " + task);
                }
            }
        } else if (num == 2) {
            Epic epic = chooseEpic();
            if (epic != null) {
                HashMap<Integer, Subtask> mapOfSubtasks = epic.getMapOfSubtasks();
                Subtask subtask = getById(mapOfSubtasks, "subtask", mapOfSubtasks.size());
                if (subtask != null) {
                    System.out.println(subtask);
                    isCorrect = setStatus(subtask, "subtask");
                    if (isCorrect) {
                        System.out.println("Updated subtask: " + subtask);
                    }
                }
            } else {
                System.out.println("List of subtasks of all epics is empty.");
            }
        }
    }

    static <T> boolean setStatus(T obj, String str) {
        boolean isCorrect = true;
        System.out.print("Input status of " + str + " you want to set(IN_PROGRESS or DONE): ");
        String choice = scanner.nextLine();
        if (str.equals("task")) {
            if (choice.equals("IN_PROGRESS")) {
                ((Task) obj).setStatus(Status.IN_PROGRESS);
            } else if (choice.equals("DONE")) {
                ((Task) obj).setStatus(Status.DONE);
            } else {
                System.out.println("Incorrect status of " + str + ".");
                isCorrect = false;
            }
        } else if (str.equals("subtask")) {
            if (choice.equals("IN_PROGRESS")) {
                ((Subtask) obj).setStatus(Status.IN_PROGRESS);
            } else if (choice.equals("DONE")) {
                ((Subtask) obj).setStatus(Status.DONE);
            } else {
                System.out.println("Incorrect status of " + str + ".");
                isCorrect = false;
            }
        }
        return isCorrect;
    }

    @Override
    public void deleteRecordById() {
        String str;
        int num = inputNumber(4, "Input your choice: ");
        if (num == 1) {
            Task task = getById(mapOfTasks, "task", idTask - 1);
            if (task != null) {
                System.out.println(task);
                System.out.println("Do you want to delete this task ?");
                System.out.print("Input YES or NO: ");
                str = scanner.nextLine();
                if (str.equals("YES")) {
                    mapOfTasks.remove(task.getId());
                    System.out.println("Task was successfully removed.");
                } else if (str.equals("NO")) {
                    System.out.println("Task has not been deleted.");
                } else {
                    System.out.println("Incorrect input.");
                }
            }
        } else if (num == 2) {
            Epic epic = getById(mapOfEpics, "epic", idEpic - 1);
            if (epic != null) {
                System.out.println(epic);
                System.out.println("Do you want to delete this epic ?");
                System.out.print("Input YES or NO: ");
                str = scanner.nextLine();
                if (str.equals("YES")) {
                    mapOfEpics.remove(epic.getId());
                    System.out.println("Epic was successfully removed.");
                } else if (str.equals("NO")) {
                    System.out.println("Epic has not been deleted.");
                } else {
                    System.out.println("Incorrect input.");
                }
            }
        } else if (num == 3) {
            Epic epic = chooseEpic();
            if (epic != null) {
                HashMap<Integer, Subtask> mapOfSubtasks = epic.getMapOfSubtasks();
                Subtask subtask = getById(mapOfSubtasks, "subtask", mapOfSubtasks.size());
                if (subtask != null) {
                    System.out.println(subtask);
                    System.out.println("Do you want to delete this subtask ?");
                    System.out.print("Input YES or NO: ");
                    str = scanner.nextLine();
                    if (str.equals("YES")) {
                        mapOfSubtasks.remove(subtask.getId());
                        System.out.println("Subtask was successfully removed.");
                    } else if (str.equals("NO")) {
                        System.out.println("Subtask has not been deleted.");
                    } else {
                        System.out.println("Incorrect input.");
                    }
                }
            } else {
                System.out.println("List of subtasks of all epics is empty.");
            }
        }
    }

    @Override
    public void setStatusToEpic() {
        boolean isNew = true;
        boolean isDone = true;
        for (Epic epic : mapOfEpics.values()) {
            for (Subtask subtask : epic.getMapOfSubtasks().values()) {
                if (!(subtask.getStatus() == Status.NEW)) {
                    isNew = false;
                }
                if (!(subtask.getStatus() == Status.DONE)) {
                    isDone = false;
                }
            }
            if (epic.getMapOfSubtasks().size() == 0 || isNew) {
                epic.setStatus(Status.NEW);
            } else if (isDone) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}
