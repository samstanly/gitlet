package gitlet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/** Commit class for Gitlet, the tiny stupid version-control system.
 *  @author Sam Steady and Jamie Ni
 */
public class Commit implements Serializable {
    /** The commit message. */
    private String commitMsg;
    /** The commit timestamp. */
    private String commitTime;
    /** The parent SHA. */
    protected String parentSHA;
    /** The hashmap of all the files and sha of those files in a Commit. */
    protected HashMap<String, String> fileMap
                    = new HashMap<String, String>();
    /**
     * Constructor for Commit object with commit message
     * MSG and the SHA of the parent commit, SHAPARENT.
     */
    public Commit(String msg, String shaParent) {
        commitMsg = msg;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        Date date = new Date();
        commitTime = sdf.format(date);
        parentSHA = shaParent;
    }
    /** Serializes the current version of the Commit C with name NAME.*/
    protected static void serialWrite(Commit c, String name) {
        try {
            ObjectOutput output
                        = new ObjectOutputStream(
                            new FileOutputStream(".gitlet/commits/"
                                + name + ".ser"));
            output.writeObject(c);
            output.close();
        } catch (IOException e) {
            System.out.println("Error in serialWrite.");
        }
    }
    /** Deserializes and returns the commit with the sha as the NAME. */
    protected static Commit serialRead(String name) {
        Commit c = null;
        try {
            ObjectInput input = new ObjectInputStream(
                        new FileInputStream(".gitlet/commits/"
                            + name + ".ser"));
            try {
                c = (Commit) input.readObject();
                input.close();
            } catch (ClassNotFoundException e2) {
                input.close();
                System.out.println("ClassNotFoundException in serialRead");
            }
        } catch (IOException e) {
            System.out.println("Error in commit serialRead.");
            e.printStackTrace();
        }
        return c;
    }

    /** Return a byte array from an object OBJECT. */
    private static byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        } catch (IOException e) {
            System.out.println("convert error");
            return null;
        }
    }

    /** Returns a String sha from Commit C. */
    public static String commitToSha(Commit c) {
        try {
            byte[] b = convertToBytes(c);
            return Utils.sha1(b);
        } catch (IOException e) {
            System.out.println("commit to sha error");
            return null;
        }
    }

    /** Returns a Commit from a String SHA. */
    protected static Commit shaToCommit(String sha) {
        return serialRead(sha);
    }

    /** Returns the commit message. */
    public String getCommitMsg() {
        return commitMsg;
    }

    /** Prints the Commit log with commit sha MYSHA. */
    public void print(String mySHA) {
        System.out.println("===");
        System.out.println("Commit " + mySHA);
        System.out.println(commitTime);
        System.out.println(commitMsg);
        System.out.println();
    }
}

