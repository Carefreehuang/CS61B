package gitlet;

import java.io.IOException;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 * 
 * @author TODO
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":// 难度分手就不能做朋友
                // TODO: handle the `init` command
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                validateNumArgs(args, 2);
                Repository.commit(args[1]);
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.log();
                break;
            case "rm":
                validateNumArgs(args, 2);
                Repository.rm(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                Repository.status();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                Repository.globallog();
                break;
            case "find":
                validateNumArgs(args, 2);
                Repository.find(args[1]);
                break;
            case "checkout":
                Repository.checkout(args);
                break;
            case "branch":
                validateNumArgs(args, 2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                Repository.rmbranch(args[1]);
                break;
            case "reset":
                validateNumArgs(args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                validateNumArgs(args, 2);
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
