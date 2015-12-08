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
            if (args.length == 1) {
                Gitlet.init();
            }
            break;
        case "add":
            if (args.length == 2) {
                Gitlet.add(args[1]);
            }
            break;
        case "commit":
            Gitlet.commit(args);
            break;
        case "rm":
            Gitlet.rm(args);
            break;
        case "log":
            if (args.length == 1) {
                Gitlet.log();
            }
            break;
        case "global-log":
            if (args.length == 1) {
                Gitlet.globalLog();
            }
            break;
        case "find":
            Gitlet.find(args);
            break;
        case "status":
            Gitlet.status();
            break;
        case "checkout":
            Gitlet.checkout(args);
            break;
        case "branch":
            Gitlet.branch(args);
            break;
        case "rm-branch":
            Gitlet.removeBranch(args);
            break;
        case "reset":
            Gitlet.reset(args);
            break;
        case "merge":
            Gitlet.merge(args);
            break;
        default:
            System.out.println("No command with that name exists.");
            return;
        }
        CommitTree.serialWrite(Gitlet.tree);
    }
}
