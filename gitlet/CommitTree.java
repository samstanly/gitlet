package gitlet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.util.TreeSet;
import java.util.TreeMap;

/** CommitTree class for Gitlet, the tiny stupid version-control system.
 *  @author Sam Steady and Jamie Ni
 */
public class CommitTree implements Serializable {
    /** The head commit SHA. */
    protected String head;
    /** The current branch. */
    protected String currBranch;
    /** A set of the staged file names. */
    protected TreeSet<String> staged = new TreeSet<String>();
    /** A set of file names not to commit. */
    protected HashSet<String> notToCommit =  new HashSet<String>();
    /** A tree map of branches and their SHAs. */
    protected TreeMap<String, String> branches = new TreeMap<String, String>();
    /** A tree map of branches and all commits ever made on those branches. */
    protected TreeMap<String, String> commHist = new TreeMap<String, String>();
    /** A set of file names for removed. */
    protected TreeSet<String> removed = new TreeSet<String>();
    /** A set of untracked file names. */
    protected TreeSet<String> untracked = new TreeSet<String>();

    /** Serializes the current version of the CommitTree B. */
    protected static void serialWrite(CommitTree b) {
        try {
            ObjectOutput output = new ObjectOutputStream(
                            new FileOutputStream(
                                ".gitlet/tree.ser"));
            output.writeObject(b);
            output.close();
        } catch (IOException e) {
            System.out.println("Error in serialWrite.");
        }
    }

    /** Returns and deserializes the CommitTree. */
    protected static CommitTree serialRead() {
        CommitTree b = null;
        try {
            ObjectInput input = new ObjectInputStream(
                                new FileInputStream(
                                    ".gitlet/tree.ser"));

            try {
                b = (CommitTree) input.readObject();
                input.close();
            } catch (ClassNotFoundException e2) {
                input.close();
                System.out.println("ClassNotFoundException in serialRead");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in CommitTree serialRead.");

        }
        return b;
    }

    /** Returns the head commit. */
    Commit getHeadCommit() {
        return Commit.shaToCommit(head);
    }
}
