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

/** The commit message. */
private String commitMsg;
/** The commit timestamp. */
private String commitTime;
/** The commit path. */
private String path;
/** The parent and child SHA values. */
private String parentSHA, childSHA;


public class Commit implements Serializable {
	/**
	 * Constructor for Commit object with commit message
	 * MSG, the SHA of the parent commit, PARENTSHA, and
	 * the SHA of the child commit, CHILDSHA.
	 */
	public Commit(String commitMsg, String path, String parentSHA, String childSHA) {
		this.commitMsg = commitMsg;
		this.path = path;
		this.parentSHA = parentSHA;
		this.childSHA = childSHA;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        Date date = new Date();
        commitTime = sdf.format(date);
	}

	/** Returns the commit message. */
	public String getCommitMsg() {
		return commitMsg;
	}

	/** Returns the commit timestamp. */
	public String getCommitTime() {
		return commitTime;
	}

	/** Returns the parent commit SHA. */
	public String getParentSHA() {
		return parentSHA;
	}

	/** Returns the child commit SHA. */
	public String getChildSHA() {
		return childSHA;
	}

	/** Returns the commit path. */
	public String getPath() {
		return path;
	}

	public void printLog() {
		System.out.println("===");
		System.out.println("Commit " + childID);
		System.out.println(commitTime);
		System.out.println(commitMsg);
		// print previous commit logs too
		// if (prevCommit != null)
		// {
		// 	System.out.println();
		// 	prevCommit.printLog();
		// }
	}


}