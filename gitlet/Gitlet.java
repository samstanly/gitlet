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
import java.nio.file.NoSuchFileException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;


public class Gitlet implements Serializable {

	static CommitTree tree = null;
	
	static void startUp() {
		File gitlet = new File(".gitlet/");
		if (gitlet.exists()) {
			System.out.println("HEREEE");
			tree = CommitTree.serialRead();

		}
	}




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

		String filepath = System.getProperty("user.dir") + "/" + name;
		try {
			if (!file.exists() || !file.getCanonicalPath().equals(filepath)) {
				System.out.println("File does not exist.");
				return;
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		if (fileModified(file, name)) {
			// CommitTree tree = CommitTree.serialRead(); // TREE
			tree.staged.add(name);
			try {
				Files.copy(Paths.get(name), Paths.get(".gitlet/staged/" + name), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println(e);
			}

			// CommitTree.serialWrite(tree);
		}


		// try {
		// 	Files.copy(Paths.get(name), Paths.get(".gitlet/staging/" + name));
		// 	tree.staged.add(name);
		// } catch (IOException e) {
		// 	System.out.println(e);
		// }


	}

	static boolean fileModified(File file, String name) {
		// CommitTree tree = CommitTree.serialRead(); //TREE

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
		// CommitTree tree = CommitTree.serialRead(); //TREE

		System.out.println("=== Branches ===");
		// System.out.println(("*" + tree.currBranch));

		System.out.println(tree);

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
				if (!file.isDirectory() && !file.isHidden()) {
					if (file.getName().equals(name)) {
						inDirectory = true;
						String newSHA = Utils.sha1(Utils.readContents(file));
						if (!head.fileMap.get(name).equals(newSHA) && !tree.staged.contains(name)) {
							modified.add(name + " (modified)");
						} else if (tree.staged.contains(name)) {
							File f = new File(".gitlet/staged/" + name);
							String oldSHA = Utils.sha1(Utils.readContents(f));
							if (!newSHA.equals(oldSHA)) {
								modified.add(name + " (modified)");
							}

						}
					}
				}



				// 	if (head.fileMap.containsKey(file.getName()) || tree.staged.contains(name)) {
				// 		inDirectory = true;
				// 		String newSHA = Utils.sha1(Utils.readContents(file));
				// 		if (tree.staged.contains(name)) {
				// 			File f = new File(".gitlet/staged/" + name);
				// 			String oldSHA = Utils.sha1(Utils.readContents(f));
				// 			if (!oldSHA.equals(newSHA)) {
				// 				modified.add(name + " (modified)");
				// 			}
				// 		}
				// 		if (!head.fileMap.containsValue(newSHA)) {
				// 			modified.add(name + " (modified)");
				// 		}
				// 		break;
				// 	}
				// }



			}
			if (!inDirectory) {
				if (!tree.removed.contains(name) && !(tree.staged.contains(name))) {
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

		getUntracked();
		for (String name : tree.untracked) {
			if (!tree.staged.contains(name)) {
				System.out.println(name);

			}
		}

		// CommitTree.serialWrite(tree);


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

	private static void getUntracked() {
		Commit head = tree.getHeadCommit();
		File folder = new File(System.getProperty("user.dir"));
		File[] arrayOfFiles = folder.listFiles();
		for (File file : arrayOfFiles) {
			if (!(file.isDirectory() || file.isHidden() 
				|| head.fileMap.containsKey(file.getName()) 
				|| tree.staged.contains(file.getName()))) {
				tree.untracked.add(file.getName());
			}
		}
	}	

	/** Commits files to commit directory. */
	static void commit(String msg) {
		// CommitTree tree = CommitTree.serialRead(); //TREE

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
			// System.out.println(name);
			if (tree.untracked.contains(name)) {
				tree.untracked.remove(name);
			}

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
				System.out.println(name);
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
  		// CommitTree.serialWrite(tree);


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
		// CommitTree tree = CommitTree.serialRead(); //TREE
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
		// CommitTree.serialWrite(tree);
	}

	/** Prints all the commits with time/date and message. */
	public static void log() {
		// CommitTree tree = CommitTree.serialRead(); //TREE
		Commit curr = tree.getHeadCommit();
		String currSHA = tree.head;
		while (curr.parentSHA != null) {
			curr.print(currSHA);
			currSHA = curr.parentSHA;
			curr = Commit.shaToCommit(curr.parentSHA);
		}
		curr.print(currSHA);
	}

	/** Displays information about all commits. Order doesn't matter. */
	public static void globalLog() {
		// CommitTree tree = CommitTree.serialRead(); //TREE
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
		// CommitTree tree = CommitTree.serialRead(); //TREE
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
		// CommitTree tree = CommitTree.serialRead();
		Commit head = tree.getHeadCommit();
		getFile(name, head);
		// CommitTree.serialWrite(tree);
	}

	/** Checkouts using file name and commit id. */
	public static void checkout(String commitID, String name) {
		// CommitTree tree = CommitTree.serialRead(); //TREE

		for (String branchSHA : tree.branches.values()) {
			Commit curr = Commit.shaToCommit(branchSHA);
			String currSHA = branchSHA;

			while (curr.parentSHA != null) {
				if (currSHA.equals(commitID)) {
					getFile(name, curr);
					return;
				}
				currSHA = curr.parentSHA;
				curr = Commit.shaToCommit(curr.parentSHA);
			}

			if (currSHA.equals(commitID)) {
				getFile(name, curr);
				return;
			}
		}
		System.out.println("No commit with that id exists.");

	}

/** Checkouts branch. */
	public static void checkoutBranch(String branch) {
		// CommitTree tree = CommitTree.serialRead();
		String sha = tree.branches.get(branch);
		Commit newHead = Commit.shaToCommit(sha);
		getUntracked();
		if (!tree.branches.containsKey(branch)) {
			System.out.println("No such branch exists.");
			return;
		}
		else if (branch.equals(tree.currBranch)) {
			System.out.println("No need to checkout the current branch.");
			return;
		}
		else {
			for (String name : newHead.fileMap.keySet()) {
				if (tree.untracked.contains(name)) {
					System.out.println("There is an untracked file in the way; delete it or add it first.");
					return;
				}
			}
		}

		tree.currBranch = branch;
		Commit head = tree.getHeadCommit();
		for (String name : head.fileMap.keySet()) {
				try {
					Files.delete(Paths.get(name));
				} catch (IOException e) {
					System.out.println("Error deleting files from current branch.");
					// if (!e instanceof FileNotFoundException)
				}
		}
		tree.head = sha;
		for (String name : newHead.fileMap.keySet()) {
			try {
				Files.copy(Paths.get(".gitlet/blobs/" + newHead.fileMap.get(name)),
					Paths.get(name));
			} catch (IOException e) {
				if (e instanceof NoSuchFileException) {
					System.out.println("Error copying files from branch.");
				}
			}
		}

		tree.staged = new TreeSet<String>();
		// CommitTree.serialWrite(tree);

	}

	public static void getFile(String name, Commit c) {
		if (c.fileMap.containsKey(name)) {
			try {
				Files.copy(Paths.get(".gitlet/blobs/" + c.fileMap.get(name)),
					Paths.get(name), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Error copying file"); //Remove
			}
		} else {
			System.out.println("File does not exist in that commit.");
		}
		
	}


	/** Creates a new branch with the given name. */
	public static void branch(String name) {
		// CommitTree tree = CommitTree.serialRead();
		if (!tree.branches.containsKey(name)) {
			tree.branches.put(name, tree.head);
			// CommitTree.serialWrite(tree);
		} else {
			System.out.println("A branch with that name already exists.");
		}
	}

	/** Deletes branch with given name. */
	public static void removeBranch(String name) {
		// CommitTree tree = CommitTree.serialRead();
		if (!tree.branches.containsKey(name)) {
			System.out.println("A branch with that name does not exist.");
		} else if (tree.currBranch.equals(name)) {
			System.out.println("Cannot remove the current branch.");
		} else {
			tree.branches.remove(name, tree.head);
			// CommitTree.serialWrite(tree);
		}
	}

	/** 
	 * Checks out all the files tracked by given commit. Moves
	 * current branch's head to that commit node.
	 */
	public static void reset(String id) {
		// CommitTree tree = CommitTree.serialRead();
		Commit curr = tree.getHeadCommit();
		String currSHA = tree.head;
		while (currSHA != null) {
			if (currSHA.equals(id)) {
				break;
			}
			currSHA = curr.parentSHA;
			if (currSHA == null) {
				System.out.println("No commit with that id exists.");
				return;
			}
			curr = Commit.shaToCommit(currSHA);
		}

		for (String name : curr.fileMap.keySet()) {
			Gitlet.getFile(name, curr);
		}

		tree.head = currSHA;
		// CommitTree.serialWrite(tree);
	}

	public static Commit findSplitPoint(String b1, String b2) {
		// CommitTree tree = CommitTree.serialRead();
		HashSet<String> checked = new HashSet<String>();
		String b1CurrSHA = tree.branches.get(b1);
		Commit b1Curr;

		for (String name : checked) {
			System.out.println(name);
		}

		while (b1CurrSHA != null) {
			b1Curr = Commit.shaToCommit(b1CurrSHA);
			checked.add(b1CurrSHA);
			b1CurrSHA = b1Curr.parentSHA;
		}

		String b2CurrSHA = tree.branches.get(b2);
		Commit b2Curr;


		while (b2CurrSHA != null) {
			b2Curr = Commit.shaToCommit(b2CurrSHA);
			if (checked.contains(b2CurrSHA)) {
				return b2Curr;
			}
			b2CurrSHA = b2Curr.parentSHA;
		}

		return null;

	}

	/** Merge files from given branch into current branch. */
	public static void merge(String b) {
		// CommitTree tree = CommitTree.serialRead();
		getUntracked();
		Commit splitPoint = findSplitPoint(b, tree.currBranch);
		String splitPointSHA = Commit.commitToSha(splitPoint);

		String givenBranchSHA = tree.branches.get(b);
		Commit givenBranchHead = Commit.shaToCommit(givenBranchSHA);
		Commit currHead = tree.getHeadCommit();

		String currHeadSHA = tree.head;
		String currBranchName = tree.currBranch;

		if (b.equals(tree.currBranch)) {
			System.out.println("Cannot merge a branch with itself.");
			return;
		} else if (!tree.branches.containsKey(b)) {
			System.out.println("A branch with that name does not exist.");
			return;
		} else if (tree.branches.get(b).equals(splitPointSHA)) {
			System.out.println("Given branch is an ancestor of the current branch.");
			return;
		} else {
			for (String name : splitPoint.fileMap.keySet()) {
				if (tree.untracked.contains(name)) {
					System.out.println("There is an untracked file in the way; delete it or add it first.");
					return;
				}
			}
			
		}


		if (tree.head.equals(splitPointSHA)) {
			checkoutBranch(b);
			tree.head = currHeadSHA;
			tree.currBranch = currBranchName;
			System.out.println("Current branch fast-forwarded.");
		}

		for (String name : givenBranchHead.fileMap.keySet()) {
			if (!splitPoint.fileMap.containsKey(name)
				&& !splitPoint.fileMap.get(name).equals(givenBranchHead.fileMap.get(name))
				&& !currHead.fileMap.containsKey(name)) {
				checkout(givenBranchSHA, name);
				// System.out.println(givenBranchSHA);
				add(name);
			}
		}

		for (String name : currHead.fileMap.keySet()) {
			if (splitPoint.fileMap.containsKey(name)
				&& splitPoint.fileMap.get(name).equals(currHead.fileMap.get(name))
				&& !givenBranchHead.fileMap.containsKey(name)) {

				// CommitTree.serialWrite(tree);
				rm(name);
				// tree = CommitTree.serialRead();
				//remove

			}
		}

		HashSet<String> conflicting = new HashSet<String>();

		for (String name : splitPoint.fileMap.keySet()) {
			if (!currHead.fileMap.containsKey(name) || !givenBranchHead.fileMap.containsKey(name)) {
				continue;
			} else if (!currHead.fileMap.get(name).equals(splitPoint.fileMap.get(name))
				&& !givenBranchHead.fileMap.get(name).equals(splitPoint.fileMap.get(name))) {
					conflicting.add(name);
			} else if (!givenBranchHead.fileMap.get(name).equals(splitPoint.fileMap.get(name))) {
				checkout(givenBranchSHA, name);
			}
		}

		for (String name : conflicting) {
			File output = new File(name);
			System.out.println(output);

			File currFile = new File(".gitlet/blobs/" + currHead.fileMap.get(name));
			File givenFile = new File(".gitlet/blobs/" + givenBranchHead.fileMap.get(name));

			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				byte[] head = "<<<<<<< HEAD \n".getBytes();
				byte[] currToWrite = Utils.readContents(currFile);
				byte[] divide = "======= \n".getBytes();
				byte[] givenToWrite = Utils.readContents(givenFile);
				byte[] end = ">>>>>>>".getBytes();

				outputStream.write(head);
				outputStream.write(currToWrite);
				outputStream.write(divide);
				outputStream.write(givenToWrite);
				outputStream.write(end);

				byte[] toWrite = outputStream.toByteArray();
				Utils.writeContents(output, toWrite);
				
			} catch (IOException e) {
				e.printStackTrace();
			}


		}


		// HashSet<String> toCheck = new HashSet<String>();
		// HashSet<String> inGiven = new HashSet<String>();
		// HashSet<String> inCurr = new HashSet<String>();

		// for (String name : givenBranchHead.fileMap.keySet()) {
		// 	if (!splitPoint.fileMap.containsKey(name)) {
		// 		inGiven.add(name);
		// 	}
		// }

		// for (String name : currHead.fileMap.keySet()) {
		// 	if (!splitPoint.fileMap.containsKey(name)) {
		// 		inCurr.add(name);
		// 	}
		// }

		// if () {
			
		// }

		if (conflicting.size() > 0) {
			System.out.println("Encountered a merge conflict.");
		} else {
			Gitlet.commit("Merged " + tree.currBranch + " with " + b + ".");
		}
	}

	// public boolean givenFileModified(Commit currBranch, Commit givenBranch, Commit splitPoint) {
	// 	if () {
			
	// 	}
	// }

}