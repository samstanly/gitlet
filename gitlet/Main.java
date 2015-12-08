package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Sam Steady and Jamie Ni
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(1);
        }
        Gitlet.startUp();
        switch (args[0]) {
        case "init":
            Gitlet.init();
            break;
        case "add":
            if (args.length == 2) {
                Gitlet.add(args[1]);
            }
            break;
        case "commit":
            if (args.length == 1 || args[1].trim().equals("")) {
                System.out.println("Please enter a commit message.");
            } else if (args.length == 2) {
                Gitlet.commit(args[1]);
            } else if (args.length > 2) {
                System.out.println("Quote message.");
            }
            break;
        case "rm":
            if (args.length == 1) {
                System.out.println("Please enter a file to remove.");
            } else if (args.length == 2) {
                Gitlet.rm(args[1]);
            }
            break;
        case "log":
            Gitlet.log();
            break;
        case "global-log":
            Gitlet.globalLog();
            break;
        case "find":
            if (args.length == 1) {
                System.out.println("Please enter commit message.");
            } else if (args.length == 2) {
                Gitlet.find(args[1]);
            } else if (args.length > 2) {
                System.out.println("Quote message.");
            }
            break;
        case "status":
            Gitlet.status();
            break;
        case "checkout":
            if (args.length == 3 && args[1].equals("--")) {
                Gitlet.checkout(args[2]);
            } else if (args.length == 2) {
                Gitlet.checkoutBranch(args[1]);
            } else if (args.length == 4 && (args[2].equals("--"))) {
                Gitlet.checkout(args[1], args[3]);
            }
            break;
        case "branch":
            if (args.length == 1) {
                System.out.println("Please enter a name for the branch.");
            } else if (args.length == 2) {
                Gitlet.branch(args[1]);
            }
            break;
        case "rm-branch":
            if (args.length == 1) {
                System.out.println("Enter branch to remove.");
            } else if (args.length == 2) {
                Gitlet.removeBranch(args[1]);
            }
            break;
        case "reset":
            if (args.length == 1) {
                System.out.println("Please enter commit ID.");
            } else if (args.length == 2) {
                Gitlet.reset(args[1]);
            }
            break;
        case "merge":
            if (args.length == 1) {
                System.out.println("Please enter a branch.");
            } else if (args.length == 2) {
                Gitlet.merge(args[1]);
            }
            break;
        default:
            System.out.println("No command with that name exists.");
            return;
        }
        CommitTree.serialWrite(Gitlet.tree);
    }

    /** Report an error and exit program with EXIT as the
     *  exit code if _strict is false; otherwise exit with code 2.
     *  FORMAT is the message format (as for printf), and ARGS any
     *  additional arguments. */
    static void error(int exit, String format, Object... args) {
        error(format, args);
        System.exit(exit);
    }

    /** Report an error.  If _strict, then exit (code 2).  Otherwise,
     *  simply return. FORMAT is the message format (as for printf),
     *  and ARGS any additional arguments. */
    static void error(String format, Object... args) {
        System.err.print("Error: ");
        System.err.printf(format, args);
    }

}
