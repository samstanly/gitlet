package gitlet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashSet;
import java.io.InputStream;
import java.io.InputStreamReader;
import ucb.util.CommandArgs;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


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
      System.out.println(Gitlet.tree);
      System.out.println("000000");
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
          if (args.length == 1) {
            System.out.println("Please enter a commit message.");
          }
          if (args.length == 2) {
            Gitlet.commit(args[1]);
          }
          if (args.length > 2) {
            System.out.println("Please use quotes around your commit message.");
          }
          break;
        case "rm": 
          if (args.length == 1) {
            System.out.println("Please enter a file to remove.");
          } 
          if (args.length == 2) {
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
          }
          if (args.length == 2) {
            Gitlet.find(args[1]);
          }
          if (args.length > 2) {
            System.out.println("Please use quotes arond your commit message.");
          }
          break;
        case "status":
          Gitlet.status();
          break;
        case "checkout":
            if (args.length == 3 && args[1].equals("--")) {
                String filename = args[2];
                Gitlet.checkout(filename);
            } else if (args.length == 2) {
                String branchName = args[1];
                Gitlet.checkoutBranch(branchName);
            } else if (args.length == 4) {
              if (args[2].equals("--")) {
                String commitID = args[1];
                String commitFile = args[3];
                Gitlet.checkout(commitID, commitFile);
              }
            }
            break;
        case "branch":
          if (args.length == 1) {
            System.out.println("Please enter a name for the branch.");
          }
        	else if (args.length == 2) {
            Gitlet.branch(args[1]);
          }
          break;
        case "rm-branch":
        	if (args.length == 1) {
            System.out.println("Please enter branch you want to remove.");
          }
          else if (args.length == 2) {
            Gitlet.removeBranch(args[1]);
          }
          break;
        case "reset":
          //commit id
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
