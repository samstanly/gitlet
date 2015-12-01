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

public class Gitlet {
	
	protected Metadata currCommit;
	protected HashSet<String> staged, untracked;

	/** Gitlet constructor. */
	public Gitlet() {
		Metadata currCommit;
		HashSet<String> staged;
		HashSet<String> untracked;
	}

	/** Initalizes gitlet. */
	static void init() {
		Metadata.setDirectory(System.getProperty("user.dir") + ".gitlet/");
    	File dir = new File(Metadata.getDirectory());
    	boolean e = dir.mkdir();
    	if (!e) {
    		System.out.println("gitlet version-control system already exists in the current directory");
      	} else {
      		//Commit initial = new Commit(path);
      		Commit initial = new Commit("initial commit", 0, 0);
      		//Creates single branch called master in metadata?
      		Metadata.setHead(initial);
      		File dir = new File(Metadata.getDirectory() + "staging/");
      	}
	}

	/** Adds files to storing directory. */
	static void add(String name) {
		File filename = new File(System.getProperty("user.dir") + name);
		if (!filename.exists() || filename.isDirectory()) {
			System.out.println("File does not exist");
			return;
		} else {
			try {
				File fileToStaging = Files.copy(filename.getPath(), Metadata.getDirectory() + "staging/",
					StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				error("Cannot add file.");
			}
		}
		
	}

	/** 
	 * Prints all branches, staged files, removed files,
	 * modified/deleted files, and untracked files.
	 */
	public void status() {

		System.out.println("=== Branches ===");
		//System.out.println(("*" + currBranch));
		//print branches from tree of commits

		System.out.println("\n=== Staged Files ===");
		for (String s : staged)
			System.out.println(s);

		System.out.println("\n=== Removed Files ===");
		for (String r : removed)
			System.out.println(r);

		System.out.println("\n=== Modifications Not Staged For Commit ===");
		//junk.txt (deleted)
		//wug3.txt (modified)

		System.out.println("\n=== Untracked Files ===");
		for (String u : untracked)
			System.out.println(u);
	}

	/** Commits files to commit directory. */
	public void commit(String msg) {
		//Commit c = new Commit(msg, parentSHA, childSHA);
	}

	/** Untrack file and will not be included in the next commit. */
	public void rm(String name) {

	}

	/** Prints all the commits with time/date and message. */
	public void log() {
		commit.printLog();
	}

	/** Displays information about all commits. Order doesn't matter. */
	public void globalLog() {

	}

	/** 
	 * Prints out the ids of all commits that have the given
	 * commit message. 
	 */
	public void find(String msg) {
		//System.out.println("Found no commit with that message.");
	}

	/** Checkouts using file name, commit id, or branch name. */
	public void checkout() {

	}

	/** Deletes branch with given name. */
	public void removeBranch(Branch b) {

	}

	/** 
	 * Checks out all the files tracked by given commit. Moves
	 * current branch's head to that commit node.
	 */
	public void reset(String id) {

	}

	/** Merge files from given branch into current branch. */
	public void merge(Branch b) {

	}
}