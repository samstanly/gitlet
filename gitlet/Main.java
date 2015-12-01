package gitlet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Date;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.io.InputStream;
import java.io.InputStreamReader;
import ucb.util.CommandArgs;


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
      switch (args[0]) {
      	case "init":
        	String curr = System.getProperty("user.dir");
        	File dir = new File(curr + "/.gitlet");
        	boolean e = dir.mkdir();
        	if (!e) {
        		System.out.println("gitlet version-control system already exists in the current directory");
          } else {
            //initial commit
            //call commit
          }
        case "add":
        	//file name args[1] save to staging area if not in blobs
        	//iterate through to check if its already there or not
        case "commit":
        	//commit message
        case "rm": 
        	//file name
        case "log":
        case "global-log":
        case "find":
        	//commit message
        case "status":
        case "checkout"
          	//file name
          	//commit id and file name
          	//branch name
        case "branch":
        	//branch name
        case "rm-branch":
        	//branch name
        case "reset":
        	//commit id
        case "merge":
        	//branch name
        default:
        	System.out.println(" No command with that name exists.");
      }
    }

    /** Print the contents of the resource named NAME on the standard error.
     *  The resource can be any file in the class directory.  File
     *  loa/foo.txt, for example, is named simply "loa/foo.txt". */
    static void printResource(String name) {
        try {
            InputStream resourceStream =
                Main.class.getClassLoader().getResourceAsStream(name);
            BufferedReader str =
                new BufferedReader(new InputStreamReader(resourceStream));
            for (String s = str.readLine(); s != null; s = str.readLine())  {
                System.err.println(s);
            }
            str.close();
        } catch (IOException excp) {
            System.out.println("No help found.");
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
