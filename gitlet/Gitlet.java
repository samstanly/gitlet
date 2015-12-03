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
import java.nio.file.FileAlreadyExistsException;

public class Gitlet implements Serializable {

	// static CommitTree tree = null;

	// static void startUp() {
	// 	if (initialized) {
	// 		tree = CommitTree.serialRead();
	// 	}
	// }


	/** Initalizes gitlet. */
	static void init() {
  		File dir = new File(".gitlet/");
    	boolean e = dir.mkdir();
    	if (!e) {
    		System.out.println("gitlet version-control system already exists in the current directory");
    	} else {
	  		Commit initial = new Commit("initial commit", null);
	  		CommitTree tree = new CommitTree();


	  		File commitDir = new File(".gitlet/commits/");
	  		File blobDir = new File(".gitlet/blobs/");
	  		File stagedDir = new File(".gitlet/staged/");
	  		commitDir.mkdir();
	  		blobDir.mkdir();
	  		stagedDir.mkdir();

	  		tree.head = Commit.commitToSha(initial);
	  		tree.branches.put("master", tree.head);
	  		tree.currBranch = "master";
	  		// CommitTree.gitletInitialized = true;
	  		Commit.serialWrite(initial, tree.head);
	  		CommitTree.serialWrite(tree);
    		
    	}

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
		if (!file.exists()) {
			System.out.println("File does not exist.");
		} else if (fileModified(file, name)) {
			CommitTree tree = CommitTree.serialRead(); // TREE
			tree.staged.add(name);
			try {
				Files.copy(Paths.get(name), Paths.get(".gitlet/staged/" + name));
			} catch (IOException e) {
				System.out.println(e);
			}

			CommitTree.serialWrite(tree);	
		}


		// try {
		// 	Files.copy(Paths.get(name), Paths.get(".gitlet/staging/" + name));
		// 	tree.staged.add(name);
		// } catch (IOException e) {
		// 	System.out.println(e);
		// }


	}

	static boolean fileModified(File file, String name) {
		CommitTree tree = CommitTree.serialRead(); //TREE

		byte[] b = Utils.readContents(file);
		String sha = Utils.sha1(b);
		// int hash = file.hashCode();
		Commit head = tree.getHeadCommit();

		if (head.fileMap.containsKey(name)) {
			if (head.fileMap.get(name).equals(sha)) {
				return false;
			} else {
				tree.untracked.add(name);
				return true;
			}
		}

		return true;

	}




	// static boolean fileModified(File file, String name) {
	// 	//tell if the file is modified from previous commit;
	// 	int hash = file.hashCode();
	// 	//if hash == get commit value
	// 	Commit previous = CommitTree.getHeadCommit();
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

		System.out.println("=== CommitTreees ===");
		//System.out.println(("*" + currCommitTree));
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
	static void commit(String msg) {
		CommitTree tree = CommitTree.serialRead(); //TREE

		String headSHA = tree.head;

		if (tree.untracked.size() == 0 && tree.staged.size() == 0) {
			System.out.println("No changes added to the commit");
			return;
		}

		Commit c = new Commit(msg, headSHA);
		for (String name : tree.staged) {
			File file = new File(name);
			byte[] b = Utils.readContents(file);
			String sha = Utils.sha1(b);
			c.fileMap.put(name, sha);
			System.out.println(name);

			try {
				Files.copy(Paths.get(".gitlet/staged/" + name), Paths.get(".gitlet/blobs/" + sha));
			} catch (IOException e) {
				if (!(e instanceof FileAlreadyExistsException)) {
					System.out.println("Error moving file to blob");
					return;
				}
			}
			try {
				Files.delete(Paths.get(".gitlet/staged/" + name));
			} catch (IOException e) {
				System.out.println("Error deleting from staging");
				return;
			}
		}


		Commit head = tree.getHeadCommit();

		for (String name : head.fileMap.keySet()) {
			if (!tree.untracked.contains(name)) {
				File file = new File(name);
				byte[] b = Utils.readContents(file);
				String sha = Utils.sha1(b);
				c.fileMap.put(name, sha);

				// try {
				// 	Files.copy(Paths.get(name), Paths.get(".gitlet/blobs/" + sha));
				// } catch (IOException e) {
				// 	System.out.println(e);
				// }
			}
		}


		tree.head = Commit.commitToSha(c);
		tree.branches.put(tree.currBranch, tree.head);
		tree.staged = new HashSet<String>();
		tree.untracked = new HashSet<String>();

		Commit.serialWrite(c, tree.head);
  		CommitTree.serialWrite(tree);


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


  //       Commit previous = CommitTree.getHeadCommit();

  //       for (String name : previous.fileMap.keySet()) {
  //       		File file = new File(name);
		// 		Integer hash = file.hashCode();
  //       	if (!untracked.contains(name)) {
  //       			c.fileMap.put(name, hash);
  //       		}	
  //       }

	}

	/** Untrack file and will not be included in the next commit. */
  public static void rm(String name) {
		CommitTree tree = CommitTree.serialRead(); //TREE
		Commit head = tree.getHeadCommit();
		if(head.fileMap.containsKey(name)) {
			try {
				if (tree.staged.contains(name)) {
					tree.staged.remove(name);
					Files.delete(Paths.get(".gitlet/staged/" + name));
				}
				tree.untracked.add(name);
				Files.delete(Paths.get(name));
			} catch (IOException e) {
				System.out.println("Cannot delete.");
			}
		} else if (tree.staged.contains(name)) {
			tree.staged.remove(name);
			try {
				Files.delete(Paths.get(".gitlet/staged/" + name));
			} catch (IOException e) {
				System.out.println("Cannot delete");
			}
		} else {
			System.out.println("No reason to remove the file.");
		}
		CommitTree.serialWrite(tree);
	}

	/** Prints all the commits with time/date and message. */
	public static void log() {
		CommitTree tree = CommitTree.serialRead(); //TREE
		Commit curr = tree.getHeadCommit();
		while (curr.parentSHA != null) {
			curr.print(Commit.commitToSha(curr));
			curr = Commit.shaToCommit(curr.parentSHA);
		}
		curr.print(Commit.commitToSha(curr));
	}

	/** Displays information about all commits. Order doesn't matter. */
	public static void globalLog() {
		CommitTree tree = CommitTree.serialRead(); //TREE
		HashSet<String> printed = new HashSet<String>();

		for (String branchSHA : tree.branches.values()) {
			Commit curr = Commit.shaToCommit(branchSHA);
			String currSHA = branchSHA;

			while (curr.parentSHA != null && !printed.contains(currSHA)) {
				curr.print(currSHA);
				printed.add(currSHA);
				currSHA = curr.parentSHA;
				curr = Commit.shaToCommit(curr.parentSHA);
			}

			if (!printed.contains(currSHA)) {
				curr.print(currSHA);
				printed.add(currSHA);
			}
		}


	}

	/** 
	 * Prints out the ids of all commits that have the given
	 * commit message. 
	 */
	public static void find(String msg) {
		CommitTree tree = CommitTree.serialRead(); //TREE
		HashSet<String> checked = new HashSet<String>();
		HashSet<String> found = new HashSet<String>();

		for (String branchSHA : tree.branches.values()) {
			Commit curr = Commit.shaToCommit(branchSHA);
			String currSHA = branchSHA;

			while (curr.parentSHA != null && !checked.contains(currSHA)) {
				if (msg.equals(curr.getCommitMsg())) {
					System.out.println(currSHA);
					found.add(currSHA);
				}
				checked.add(currSHA);
				currSHA = curr.parentSHA;
				curr = Commit.shaToCommit(curr.parentSHA);
			}

			if (!checked.contains(currSHA)) {
				if (msg.equals(curr.getCommitMsg())) {
					System.out.println(currSHA);
					found.add(currSHA);
				}
				checked.add(currSHA);
			}

		}
		if (found.size() == 0) {
			System.out.println("Found no commit with that message.");
		}


	}

	/** Checkouts using file name, commit id, or branch name. */
	public void checkout() {

	}

	/** Creates a new branch with the given name. */
	public void branch(String name) {

	}

	/** Deletes branch with given name. */
	public void removeCommitTree(CommitTree b) {

	}

	/** 
	 * Checks out all the files tracked by given commit. Moves
	 * current branch's head to that commit node.
	 */
	public void reset(String id) {

	}

	/** Merge files from given branch into current branch. */
	public void merge(CommitTree b) {

	}
}