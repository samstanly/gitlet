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

    private static Gitlet gitlet;

    public static void main(String... args) {
      if (args.length == 0) {
        System.out.println("Please enter a command.");
        System.exit(1);
      }
      switch (args[0]) {
      	case "init":
          gitlet.init();
          break;
        case "add":
        	//file name args[1] save to staging area if not in blobs
        	//iterate through to check if its already there or not
          gitlet.add(args[1]);
          break;
        case "commit":
        	//commit message
          break;
        case "rm": 
        	//file name
          break;
        case "log":
          break;
        case "global-log":
          break;
        case "find":
        	//commit message
          break;
        case "status":
          break;
        case "checkout":
          	//file name
          	//commit id and file name
          	//branch name
            break;
        case "branch":
        	//branch name
          break;
        case "rm-branch":
        	//branch name
          break;
        case "reset":
        	//commit id
          break;
        case "merge":
        	//branch name
          break;
        default:
        	System.out.println("No command with that name exists.");
          break;
      }
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
