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
import java.util.HashMap;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.ByteArrayOutputStream;

public class Commit implements Serializable {

	/** The commit message. */
	private String commitMsg;
	/** The commit timestamp. */
	private String commitTime;

	protected String parentSHA;

	protected HashMap<String, String> fileMap = new HashMap<String, String>();

	/**
	 * Constructor for Commit object with commit message
	 * MSG, the SHA of the parent commit, PARENTSHA, and
	 * the SHA of the child commit, CHILDSHA.
	 */
	public Commit(String commitMsg, String parentSHA) {
		this.commitMsg = commitMsg;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        Date date = new Date();
        commitTime = sdf.format(date);
        this.parentSHA = parentSHA;
	}

	// protected static String getCommitSha(Commit c) {
	// 	for () {
			
	// 	}
	// }

	protected static void serialWrite(Commit c, String name) { //NAME is hashcode or something
		try {
			ObjectOutput output = new ObjectOutputStream(new FileOutputStream(".gitlet/commits/" + name + ".ser"));
			output.writeObject(c);
			output.close();
		} catch (IOException e) {
			System.out.println("Error in serialWrite.");
		}
	}

	protected static Commit serialRead(String name) {
		Commit c = null;
		try {
			ObjectInput input = new ObjectInputStream(new FileInputStream(".gitlet/commits/" + name + ".ser"));
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

	public static String commitToSha(Commit c) {
		try {
			byte[] b = convertToBytes(c);
			return Utils.sha1(b);
		} catch (IOException e) {
			System.out.println("commit to sha error");
			return null;
		}
	}

	protected static Commit shaToCommit(String sha) {
		return serialRead(sha);

	}

	/** Returns the commit message. */
	public String getCommitMsg() {
		return commitMsg;
	}

	/** Returns the commit timestamp. */
	public String getCommitTime() {
		return commitTime;
	}

	public void print(String mySHA) {
		System.out.println("===");
		System.out.println("Commit " + mySHA);
		System.out.println(commitTime);
		System.out.println(commitMsg);
	}



}