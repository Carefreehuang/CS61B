package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {  //如果参数为空
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs(args,2);
                Repository.add(args[1]);
                break;
            case "commit":
                validateNumArgs(args,1);
                Repository.commit(args[1]);
            // TODO: FILL THE REST IN
            default:  //如果改命令不是上面几个命令，输出....并exit；
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
    public static void validateNumArgs(String[] args, int n) {//判断命令和参数个数是否匹配
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
