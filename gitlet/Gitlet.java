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
import java.util.TreeSet;
import java.util.Arrays;


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
				tree.notToCommit.add(name);
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
	public static void status() {
		CommitTree tree = CommitTree.serialRead(); //TREE

		System.out.println("=== Branches ===");
		// System.out.println(("*" + tree.currBranch));


		for (String branch : tree.branches.keySet()) {
			if (!branch.equals(tree.currBranch)) {
				System.out.println(branch);
			} else {
				System.out.println("*" + branch);
			}
		}

		System.out.println("\n=== Staged Files ===");
		for (String name : tree.staged) {
			System.out.println(name);
		}

		System.out.println("\n=== Removed Files ===");
		for (String name : tree.removed) {
			System.out.println(name);
		}

		System.out.println("\n=== Modifications Not Staged For Commit ===");
		Commit head = tree.getHeadCommit();
		TreeSet<String> modified = new TreeSet<String>();
		File folder = new File(System.getProperty("user.dir"));
		File[] arrayOfFiles = folder.listFiles();

		for (String name : head.fileMap.keySet()) {
			boolean inDirectory = false;
			for (File file : arrayOfFiles) {
				if (!file.isDirectory()) {
					if (head.fileMap.containsKey(file.getName()) && !tree.staged.contains(name)) {
						inDirectory = true;
						String newSHA = Utils.sha1(Utils.readContents(file));
						if (!head.fileMap.containsValue(newSHA)) {
							modified.add(name + " (modified)");
						}
						break;
					}
				}
			}
			if (!inDirectory) {
				if (!tree.removed.contains(name)) {
					modified.add(name + " (deleted)");
				}
			}
		}
		for (String name : modified) {
			System.out.println(name);
		}

		System.out.println("\n=== Untracked Files ===");
		// File folder = new File(System.getProperty("user.dir"));
		// File[] listOfFiles = folder.listFiles();
		for (File file : arrayOfFiles) {
			if (!(file.isDirectory() || file.isHidden() 
				|| head.fileMap.containsKey(file.getName()) 
				|| tree.staged.contains(file.getName()))) {
				System.out.println(file.getName());
			}
		}

		CommitTree.serialWrite(tree);


		// for (String u : untracked)
		// 	System.out.println(u);
	}

	// private static void printUntrackedFile(File file, CommitTree tree, String toFile) {
	// 	if (file.getName().equals("gitlet") || file.isHidden()) { //FIX THIS?
	// 		return;
	// 	}
	// 	if (file.isDirectory()) {
	// 		for (File f : file.listFiles()) {
	// 			// System.out.println(file.getName());
	// 			printUntrackedFile(f, tree, toFile + file.getName() + "/");
	// 		}
	// 	} else {
	// 		Commit head = tree.getHeadCommit();
	// 		// System.out.println(toFile + file.getName());
	// 		if (!(head.fileMap.containsKey(toFile + file.getName()) || tree.staged.contains(toFile + file.getName()))) {
	// 			System.out.println(toFile + file.getName());
	// 		}
	// 	}
	// }

	/** Commits files to commit directory. */
	static void commit(String msg) {
		CommitTree tree = CommitTree.serialRead(); //TREE

		String headSHA = tree.head;

		if (tree.notToCommit.size() == 0 && tree.staged.size() == 0) {
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
			if (!tree.notToCommit.contains(name)) {
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
		tree.staged = new TreeSet<String>();
		tree.notToCommit = new HashSet<String>();
		tree.removed = new TreeSet<String>();
		// tree.removed = new TreeSet<String>(); //Clear Removed Every COMMIT?

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
				tree.notToCommit.add(name);
				tree.removed.add(name);
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

	/** Checkouts using file name. */
	public static void checkout(String name) {
		//filename
		CommitTree tree = CommitTree.serialRead();
		Commit head = tree.getHeadCommit();
		if (head.fileMap.containsKey(name)) {
			try {
				Files.copy(Paths.get(".gitlet/blobs/" + head.fileMap.get(name), System.getProperty("user.dir") + name));
			} catch (IOException e) {
				System.out.println(e);
			}
		}

		//branch

		CommitTree.serialWrite(tree);
	}

	/** Checkouts using file name and commit id. */
	public static void checkout(String commitID, String name) {

	}

	/** Checkouts branch. */
	public static void checkoutBranch(String branch) {

	}

	/** Creates a new branch with the given name. */
	public static void branch(String name) {
		CommitTree tree = CommitTree.serialRead();
		if (!tree.branches.containsKey(name)) {
			tree.branches.put(name, tree.head);
			tree.currBranch = name;
			CommitTree.serialWrite(tree);
		} else {
			System.out.println("A branch with that name already exists.");
		}
	}

	/** Deletes branch with given name. */
	public static void removeBranch(String name) {
		CommitTree tree = CommitTree.serialRead();
		if (!tree.branches.containsKey(name)) {
			System.out.println("A branch with that name does not exist.");
		} else if (tree.currBranch.equals(name)) {
			System.out.println("Cannot remove the current branch.");
		} else {
			tree.branches.remove(name, tree.head);
			CommitTree.serialWrite(tree);
		}
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