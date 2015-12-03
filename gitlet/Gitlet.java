package gitlet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectInput;
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
import java.nio.file.Path;
import java.nio.file.Paths;

public class Gitlet implements Serializable {



	/** Initalizes gitlet. */
	static void init() {
  		File dir = new File(".gitlet/");
    	boolean e = dir.mkdir();
    	if (!e) {
    		System.out.println("gitlet version-control system already exists in the current directory");
    	}
  		Commit initial = new Commit("initial commit");
  		Branch masterBranch = new Branch("master", initial);
  		masterBranch.head = Integer.toString(initial.hashCode());
  		// Branch.addCommit(Utils.sha1(initial));
  		File commitDir = new File(".gitlet/commits/");
  		File blobDir = new File(".gitlet/blobs/");
  		commitDir.mkdir();
  		blobDir.mkdir();

  		masterBranch.addCommit(Integer.toString(initial.hashCode()));
  		Commit.serialWrite(initial, Integer.toString(initial.hashCode()));
  		Branch.serialWrite(masterBranch);

	}

	/** Adds files to storing directory. */
	static void add(String name) {
		// File file = new File(name);
		// if (fileModified(file, name)) {
		// 	if (!file.exists() || file.isDirectory()) {
		// 		System.out.println("File does not exist");
		// 		return;
		// 	} else {
		// 		staged.add(name);
		// 	}
		// }


		File file = new File(name);
		if (fileModified(file, name)) {
			Branch masterBranch = Branch.serialRead();
			masterBranch.staged.add(name);
			Branch.serialWrite(masterBranch);
			
		}


		// try {
		// 	Files.copy(Paths.get(name), Paths.get(".gitlet/staging/" + name));
		// 	masterBranch.staged.add(name);
		// } catch (IOException e) {
		// 	System.out.println(e);
		// }


	}

	static boolean fileModified(File file, String name) {
		Branch masterBranch = Branch.serialRead();

		int hash = file.hashCode();
		Commit head = masterBranch.getHeadCommit();

		if (head.fileMap.containsKey(name)) {
			if (head.fileMap.get(name).equals(hash)) {
				return false;
			} else {
				masterBranch.untracked.add(name);
				return true;
			}
		}

		return true;

	}



	// static boolean fileModified(File file, String name) {
	// 	//tell if the file is modified from previous commit;
	// 	int hash = file.hashCode();
	// 	//if hash == get commit value
	// 	Commit previous = Branch.getHeadCommit();
	// 	if (!previous.fileMap.containsKey(name)) {
	// 		return false;
	// 	} else if (previous.fileMap.get(name).equals(hash)) {
	// 		untracked.add(name);
	// 		return false;
	// 	}
	// 	return true;
	// }

	/** 
	 * Prints all branches, staged files, removed files,
	 * modified/deleted files, and untracked files.
	 */
	public void status() {

		System.out.println("=== Branches ===");
		//System.out.println(("*" + currBranch));
		//print branches from tree of commits

		System.out.println("\n=== Staged Files ===");
		// for (String s : staged)
		// 	System.out.println(s);

		System.out.println("\n=== Removed Files ===");
		// for (String r : removed)
		// 	System.out.println(r);

		System.out.println("\n=== Modifications Not Staged For Commit ===");
		//junk.txt (deleted)
		//wug3.txt (modified)

		System.out.println("\n=== Untracked Files ===");
		// for (String u : untracked)
		// 	System.out.println(u);
	}

	/** Commits files to commit directory. */
	public void commit(String msg) {
		// Commit c = new Commit(msg);
		// for (String name : staged) {
		// 	try {
		// 		File file = new File(name);
		// 		Integer hash = file.hashCode();
		// 		Files.copy(file.toPath(), Paths.get("/.gitlet/blobs/" + name + hash),
		// 			StandardCopyOption.REPLACE_EXISTING);
		// 		c.fileMap.put(name, hash);

		// 	} catch (IOException e) {
		// 		System.out.println("Cannot add file.");
		// 	}
  //       }


  //       Commit previous = Branch.getHeadCommit();

  //       for (String name : previous.fileMap.keySet()) {
  //       		File file = new File(name);
		// 		Integer hash = file.hashCode();
  //       	if (!untracked.contains(name)) {
  //       			c.fileMap.put(name, hash);
  //       		}	
  //       }

	}

	/** Untrack file and will not be included in the next commit. */
	public void rm(String name) {

	}

	/** Prints all the commits with time/date and message. */
	public void log() {
		// Branch.printLog();
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

	/** Creates a new branch with the given name. */
	public void branch(String name) {

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