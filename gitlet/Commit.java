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


public class Commit implements Serializable {

	/** The commit message. */
	private String commitMsg;
	/** The commit timestamp. */
	private String commitTime;

	protected HashMap<String, Integer> fileMap = new HashMap<String, Integer>();

	/**
	 * Constructor for Commit object with commit message
	 * MSG, the SHA of the parent commit, PARENTSHA, and
	 * the SHA of the child commit, CHILDSHA.
	 */
	public Commit(String commitMsg) {
		this.commitMsg = commitMsg;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        Date date = new Date();
        commitTime = sdf.format(date);
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

	/** Returns the commit message. */
	public String getCommitMsg() {
		return commitMsg;
	}

	/** Returns the commit timestamp. */
	public String getCommitTime() {
		return commitTime;
	}





}