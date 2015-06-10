/**
Copyright (c) 2015, Lars Butler

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Week2 {

    private static Map<String, Class []> getCommands() {
        Map<String, Class []> cmds = new HashMap<>();
        cmds.put("get", new Class [] {Integer.class});
        cmds.put("set", new Class [] {Integer.class, String.class});
        cmds.put("add", new Class [] {Integer.class, String.class});
        cmds.put("remove", new Class [] {Integer.class});
        cmds.put("toArray", new Class [] {});
        cmds.put("size", new Class [] {});
        return cmds;
    }
    public static final Map<String, Class []> COMMANDS = getCommands();

    public static void printHelp() {
        String [] helpText = {
                "Supported commands:",
                "",
                "  get INDEX",
                "    Get the value at the specified index",
                "  set INDEX VALUE",
                "    Set the element at INDEX to VALUE",
                "  add INDEX VALUE",
                "    Insert VALUE to the end of the specified INDEX",
                "  remove INDEX",
                "    Remove the item at INDEX from the array",
                "  toArray",
                "    Get the elements in the collection as an array",
                "  size",
                "    Get the size of the array (number of elements)",
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
        final int arrayCap = 3;
        RootishArrayStack<String> array = new RootishArrayStack<>(arrayCap, String.class);
        System.out.printf("Welcome to the %s interactive shell!", array.getClass().getName());
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        String input = "";
        System.out.printf("Initial array capacity: %d\n\n", arrayCap);
        printHelp();
        do {
            System.out.print("> ");

            input = scanner.nextLine();

            input = input.trim();
            if (input.equals("quit") || input.equals("q")) {
                System.exit(0);
            }
            else if (input.equals("help") || input.equals("h")) {
                printHelp();
                continue;
            }

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
            Parameter [] params = cmdMeth.getParameters();
            if (argsCount != params.length) {
                // Wrong number of args.
                System.out.printf(
                        "Invalid number of arguments for command '%s'\n",
                        command);
                printHelp();
                continue;
            }

            // Correct number of arguments.
            // Now we need to convert the args.
            Class [] argTypes = COMMANDS.get(command);
            Object [] inputArgs = new Object[params.length];
            for (int i = 0; i < argTypes.length; i++) {
                Class c = argTypes[i];
                String a = args.get(i);
                if (c.getName().equals("java.lang.Integer")) {
                    inputArgs[i] = Integer.parseInt(a);
                }
                else {
                    // Assume it's a string
                    inputArgs[i] = a;
                }
            }
            try {
                Object ret = cmdMeth.invoke(array, inputArgs);
                if (ret != null) {
                    // If the return value is an array, print it nicely
                    if (ret instanceof Object[]) {
                        for (Object o: (Object[])ret) {
                            System.out.print(o.toString() + " ");
                        }
                        System.out.println();
                    }
                    else {
                        System.out.println(ret);
                    }
                }
            } catch (InvocationTargetException e) {
                e.getTargetException().printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } while (!(
                input.toLowerCase() == "q"
                || input.toLowerCase() == "quit"));
    }

    public static void main(String[] args) {
        ui();
    }

}
