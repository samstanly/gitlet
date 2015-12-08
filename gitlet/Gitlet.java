package gitlet;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Paths;
import java.nio.file.FileAlreadyExistsException;
import java.util.TreeSet;
import java.nio.file.NoSuchFileException;
import java.io.ByteArrayOutputStream;

/** Gitlet class, the tiny stupid version-control system.
 *  @author Sam Steady and Jamie Ni
 */
public class Gitlet implements Serializable {
    /** The initial CommitTree object. */
    protected static CommitTree tree = null;
    /** Initialize the CommitTree object. */
    static void startUp() {
        File gitlet = new File(".gitlet/");
        if (gitlet.exists()) {
            tree = CommitTree.serialRead();
        }
    }

    /** Initalizes gitlet. */
    static void init() {
        File dir = new File(".gitlet/");
        boolean e = dir.mkdir();
        if (!e) {
            System.out.println("gitlet version-control system already"
                + " exists in the current directory");
            System.exit(0);
        } else {
            Commit initial = new Commit("initial commit", null);
            tree = new CommitTree();

            File commitDir = new File(".gitlet/commits/");
            File blobDir = new File(".gitlet/blobs/");
            File stagedDir = new File(".gitlet/staged/");
            commitDir.mkdir();
            blobDir.mkdir();
            stagedDir.mkdir();

            tree.head = Commit.commitToSha(initial);
            tree.branches.put("master", tree.head);
            tree.currBranch = "master";
            Commit.serialWrite(initial, tree.head);
        }
    }

    /** Adds files to storing directory given filename NAME. */
    static void add(String name) {
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
        if (fileModified(file, name) || tree.removed.contains(name)) {
            if (tree.removed.contains(name)) {
                tree.removed.remove(name);
            } else {
                tree.staged.add(name);
            }
            try {
                Files.copy(Paths.get(name), Paths.get(".gitlet/staged/" + name),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Returns a boolean based on whether or not a file FILE has
     * been modified.
     * @param  file File to check if modified
     * @param  name name of the file
     * @return      boolean value
     */
    static boolean fileModified(File file, String name) {
        String sha = Utils.sha1(Utils.readContents(file));
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

    /**
     * Prints all branches, staged files, removed files,
     * modified/deleted files, and untracked files.
     */
    public static void status() {
        System.out.println("=== Branches ===");
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
                        if (!head.fileMap.get(name).equals(newSHA)
                                && !tree.staged.contains(name)) {
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
            }
            if (!inDirectory) {
                if (!tree.removed.contains(name)
                    && !(tree.staged.contains(name))) {
                    modified.add(name + " (deleted)");
                }
            }
        }
        for (String name : modified) {
            System.out.println(name);
        }
        System.out.println("\n=== Untracked Files ===");
        getUntracked();
        for (String name : tree.untracked) {
            if (!tree.staged.contains(name)) {
                System.out.println(name);
            }
        }
        System.out.println();
    }

    /** Gets all the untracked files. */
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

    /** Commits files to commit directory with the given commit MSG. */
    public static void commit(String msg) {
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
            if (tree.untracked.contains(name)) {
                tree.untracked.remove(name);
            }
            try {
                Files.copy(Paths.get(".gitlet/staged/" + name),
                        Paths.get(".gitlet/blobs/" + sha));
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
            }
        }
        tree.head = Commit.commitToSha(c);
        tree.branches.put(tree.currBranch, tree.head);
        tree.staged = new TreeSet<String>();
        tree.notToCommit = new HashSet<String>();
        tree.removed = new TreeSet<String>();
        Commit.serialWrite(c, tree.head);
    }

    /** Uses the correct commit method based on input ARGS. */
    public static void commit(String... args) {
        if (args.length == 1 || args[1].trim().equals("")) {
            System.out.println("Please enter a commit message.");
        } else if (args.length == 2) {
            Gitlet.commit(args[1]);
        } else if (args.length > 2) {
            System.out.println("Please quote your message.");
        }
    }

    /** Uses the remove method based on input ARGS. */
    public static void rm(String... args) {
        if (args.length == 1) {
            System.out.println("Please enter a file to remove.");
        } else if (args.length == 2) {
            Gitlet.rm(args[1]);
        }
    }

    /**
     * Untrack file and will not be included in the next commit.
     * @param name file name to remove
     */
    public static void rm(String name) {
        Commit head = tree.getHeadCommit();
        if (head.fileMap.containsKey(name)) {
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
    }

    /** Prints all the commits with time/date and message. */
    public static void log() {
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

    /** Uses the correct find method based on input ARGS. */
    public static void find(String... args) {
        if (args.length == 1) {
            System.out.println("Please enter commit message to find.");
        } else if (args.length == 2) {
            Gitlet.find(args[1]);
        } else if (args.length > 2) {
            System.out.println("Please use quotes around the message.");
        }
    }

    /**
     * Prints out the ids of all commits that have the given
     * commit message MSG.
     */
    public static void find(String msg) {
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

    /** Uses the correct method based on input ARGS. */
    public static void checkout(String... args) {
        if (args.length == 3 && args[1].equals("--")) {
            Gitlet.checkout(args[2]);
        } else if (args.length == 2) {
            Gitlet.checkoutBranch(args[1]);
        } else if (args.length == 4 && (args[2].equals("--"))) {
            Gitlet.checkout(args[1], args[3]);
        } else {
            System.out.println("Incorrect operands.");
        }
    }

    /** Checkouts using file name NAME. */
    public static void checkout(String name) {
        Commit head = tree.getHeadCommit();
        getFile(name, head);
    }

    /** Checkouts using file name NAME and commit id COMMITID. */
    public static void checkout(String commitID, String name) {
        for (String branchSHA : tree.branches.values()) {
            Commit curr = Commit.shaToCommit(branchSHA);
            String currSHA = branchSHA;

            while (curr.parentSHA != null) {
                if (currSHA.equals(commitID)
                        || currSHA.substring(0, 6).equals(commitID)) {
                    getFile(name, curr);
                    return;
                }
                currSHA = curr.parentSHA;
                curr = Commit.shaToCommit(curr.parentSHA);
            }
            if (currSHA.equals(commitID)
                    || currSHA.substring(0, 6).equals(commitID)) {
                getFile(name, curr);
                return;
            }
        }
        System.out.println("No commit with that id exists.");
    }

    /** Checkouts branch BRANCH. */
    public static void checkoutBranch(String branch) {
        getUntracked();
        if (!tree.branches.containsKey(branch)) {
            System.out.println("No such branch exists.");
            return;
        }
        String sha = tree.branches.get(branch);
        Commit newHead = Commit.shaToCommit(sha);
        if (branch.equals(tree.currBranch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        } else {
            for (String name : newHead.fileMap.keySet()) {
                if (tree.untracked.contains(name)) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it or add it first.");
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
            }
        }
        tree.head = sha;
        for (String name : newHead.fileMap.keySet()) {
            try {
                Files.copy(Paths.get(".gitlet/blobs/"
                    + newHead.fileMap.get(name)), Paths.get(name));
            } catch (IOException e) {
                if (e instanceof NoSuchFileException) {
                    System.out.println("Error copying files from branch.");
                }
            }
        }
        tree.staged = new TreeSet<String>();
    }

    /** Gets the file given the NAME of the file from the commit C. */
    public static void getFile(String name, Commit c) {
        if (c.fileMap.containsKey(name)) {
            try {
                Files.copy(Paths.get(".gitlet/blobs/" + c.fileMap.get(name)),
                    Paths.get(name), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("Error copying file");
            }
        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    /** Uses the branch method based on input ARGS. */
    public static void branch(String...args) {
        if (args.length == 1) {
            System.out.println("Please enter a name for the branch.");
        } else if (args.length == 2) {
            Gitlet.branch(args[1]);
        }
    }
    /** Creates a new branch with the given name NAME. */
    public static void branch(String name) {
        if (!tree.branches.containsKey(name)) {
            tree.branches.put(name, tree.head);
        } else {
            System.out.println("A branch with that name already exists.");
        }
    }

    /** Uses the rm-branch method based on input ARGS. */
    public static void removeBranch(String...args) {
        if (args.length == 1) {
            System.out.println("Enter branch to remove.");
        } else if (args.length == 2) {
            Gitlet.removeBranch(args[1]);
        }
    }

    /** Deletes branch with given name NAME. */
    public static void removeBranch(String name) {
        if (!tree.branches.containsKey(name)) {
            System.out.println("A branch with that name does not exist.");
        } else if (tree.currBranch.equals(name)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            tree.branches.remove(name, tree.head);
        }
    }

    /** Uses the reset method based on input ARGS. */
    public static void reset(String...args) {
        if (args.length == 1) {
            System.out.println("Please enter commit ID.");
        } else if (args.length == 2) {
            Gitlet.reset(args[1]);
        }
    }

    /**
     * Checks out all the files tracked by given commit ID. Moves
     * current branch's head to that commit node.
     */
    public static void reset(String id) {
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
    }

    /**
     * Finds the split point.
     * @param  b1 SHA of given branch
     * @param  b2 SHA of current branch
     * @return    the commit where the split point is
     */
    public static Commit findSplitPoint(String b1, String b2) {
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

    /** Uses the merge method based on input ARGS. */
    public static void merge(String...args) {
        if (args.length == 1) {
            System.out.println("Please enter a branch.");
        } else if (args.length == 2) {
            Gitlet.merge(args[1]);
        }
    }

    /** Merge files from given branch B into current branch. */
    public static void merge(String b) {
        getUntracked();
        Commit sp = findSplitPoint(b, tree.currBranch);
        String spSHA = Commit.commitToSha(sp);
        String givenBrSHA = tree.branches.get(b);
        Commit givenBrHead = Commit.shaToCommit(givenBrSHA);
        Commit currHead = tree.getHeadCommit();
        String currHeadSHA = tree.head;
        String currBranchName = tree.currBranch;
        if (b.equals(tree.currBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        } else if (!tree.branches.containsKey(b)) {
            System.out.println("A branch with that name does not exist.");
            return;
        } else if (tree.branches.get(b).equals(spSHA)) {
            System.out.println("Given branch is an ancestor of the"
                + "current branch.");
            return;
        } else {
            for (String name : sp.fileMap.keySet()) {
                if (tree.untracked.contains(name)) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it or add it first.");
                    return;
                }
            }
        }
        if (tree.head.equals(spSHA)) {
            checkoutBranch(b);
            tree.head = currHeadSHA;
            tree.currBranch = currBranchName;
            System.out.println("Current branch fast-forwarded.");
        }
        HashSet<String> conflicting = new HashSet<String>();
        for (String name : givenBrHead.fileMap.keySet()) {
            if (!sp.fileMap.containsKey(name)) {
                if (!currHead.fileMap.containsKey(name)) {
                    checkout(givenBrSHA, name);
                    add(name);
                } else if (currHead.fileMap.containsKey(name)) {
                    conflicting.add(name);
                }
            } else if (sp.fileMap.containsKey(name)) {
                if (!sp.fileMap.get(name).equals(givenBrHead.fileMap.get(name))
                    && !currHead.fileMap.containsKey(name)) {
                    conflicting.add(name);
                }
            }
        }
         for (String name : currHead.fileMap.keySet()) {
            if (sp.fileMap.containsKey(name)
                && !givenBrHead.fileMap.containsKey(name)) {
                if (sp.fileMap.get(name).equals(currHead.fileMap.get(name))) {
                    rm(name);
                } else {
                    conflicting.add(name);
                }
            } else if (!sp.fileMap.containsKey(name)
                && givenBrHead.fileMap.containsKey(name)
                && !givenBrHead.fileMap.get(name).equals(currHead.fileMap.get(name))) {
                    conflicting.add(name);
            }
        }
        for (String name : conflicting) {
            resolveConflict(name, currHead, givenBrHead);
        }
        if (conflicting.size() > 0) {
            System.out.println("Encountered a merge conflict.");
        } else {
            Gitlet.commit("Merged " + tree.currBranch + " with " + b + ".");
        }
    }

    /** Resolves conflict in filename NAME in commit CURRHEAD in GIVENBRHEAD. */
    public static void resolveConflict(String name, Commit currHead,
            Commit givenBrHead) {
        File output = new File(name);
        File currFile = new File(".gitlet/blobs/" + currHead.fileMap.get(name));
        File givenFile = new File(".gitlet/blobs/"
                + givenBrHead.fileMap.get(name));
        try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                byte[] head = "<<<<<<< HEAD \n".getBytes();
                outputStream.write(head);
                if (currFile.exists()) {
                    byte[] currToWrite = Utils.readContents(currFile);
                    outputStream.write(currToWrite);
                }
                byte[] divide = "======= \n".getBytes();
                outputStream.write(divide);
                if (givenFile.exists()) {
                    byte[] givenToWrite = Utils.readContents(givenFile);
                    outputStream.write(givenToWrite);
                }
                byte[] end = ">>>>>>>".getBytes();
                outputStream.write(end);


                byte[] toWrite = outputStream.toByteArray();
                Utils.writeContents(output, toWrite);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}

