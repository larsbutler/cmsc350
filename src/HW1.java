import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class HW1 {

    public class Command {
        String command;
        // Types of params, in order of positional arguments.
        Class [] params;
        public Command(String command, Class [] params) {
            this.command = command;
            this.params = params;
        }

    }
    public static final List<String> COMMANDS = Arrays.asList(new String [] {
            "get",
            "set",
            "add",
            "isFull",
            "remove",
            "insert",
            "display",
    });

    public static void printHelp() {
        String [] helpText = {
                "Supported commands:",
                "",
                "  get INDEX",
                "    Get the value at the specified index",
                "  set INDEX VALUE",
                "    Set the element at INDEX to VALUE",
                "  add VALUE",
                "    Append VALUE to the end of the array",
                "  isFull",
                "    Print 'true' if array is full, otherwise 'false'",
                "  remove INDEX",
                "    Remove the item at INDEX from the array",
                "  insert INDEX VALUE",
                "    Insert VALUE at the given INDEX (0 - N)",
                "  display",
                "    Display the current array",
                "  help | h",
                "    Show this help",
                "  quit | q",
                "    Exit the program",
        };
        for (String line: helpText) {
            System.out.println(line);
        }
    }

    /**
     * Get a method by name from an object.
     * NOTE: If there are two methods with the same name (and overloaded
     * signatures), the first one found will be returned. This is fine for HW1,
     * but would not necessarily be satisfactory for other uses.
     * @param o
     * @param name
     * @return
     */
    public static Method getMethodByName(Object o, String name) {
        Method [] meths = o.getClass().getMethods();
        for (Method m: meths) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        return null;
    }

    public static void ui() {
        GenericArray<String> array = new GenericArray<>();
        Scanner scanner = new Scanner(System.in);
        String input = "";
        printHelp();
        do {
            System.out.print("> ");

            input = scanner.nextLine();

            input = input.trim();
            if (input.equals("quit") || input.equals("q")) {
                System.exit(0);
            }
            else if (input == "help" || input == "h") {
                printHelp();
                continue;
            }
            // Split on whitespace
            List<String> tokens = Arrays.asList(input.split("\\s+"));
            String command = tokens.get(0);
            Method cmdMeth = getMethodByName(array, command);
            if (cmdMeth == null) {
                System.out.printf("Unknown command: '%s'\n", command);
                printHelp();
                continue;
            }

            // Command found. Now we need to convert parameters.
            List<String> args = tokens.subList(1, tokens.size());
            int argsCount = args.size();
            Parameter[] params = cmdMeth.getParameters();
            if (argsCount != params.length) {
                // Wrong number of args.
                System.out.printf(
                        "Invalid number of arguments for command '%s'\n",
                        command);
                printHelp();
                continue;
            }
            params[0].getClass();
            System.out.println();
        } while (
                input.toLowerCase() != "q"
                || input.toLowerCase() != "quit");
        // get
        // set
        // add
        // isFull
        // remove
        // insert
        // display
    }

    public static void main(String[] args) {
        ui();
    }

}
