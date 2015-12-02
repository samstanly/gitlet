package gitlet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
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
import java.util.LinkedList;

public class Branch implements Serializable {
	protected static String head;
	protected static String name;
	protected static Commit currCommit;
	protected static LinkedList<String> commitList = new LinkedList<String>();

	Branch(String name, Commit currCommit) {
		this.name = name;
		this.currCommit = currCommit;
	}

	static Commit shaToCommit(String sha) {
		System.out.println("we" + sha);
		Commit h = null;
		try {
	      	FileInputStream fileIn = new FileInputStream(sha);
	      	ObjectInputStream in = new ObjectInputStream(fileIn);
	       	h = (Commit) in.readObject();
	       	in.close();
	       	fileIn.close();
    	} catch(IOException i) {
    		i.printStackTrace();
       		return null;
    	} catch (ClassNotFoundException i) {
    		i.printStackTrace();
       		return null;
    	}
    	return h;
	}

	static void addCommit(String commitSHA) {
		System.out.println(commitSHA);
		commitList.addFirst(commitSHA);
		head = commitSHA;
	}

	static String getCommit(int num) {
		return commitList.get(num);
	}

	static Commit getHeadCommit() {
		return shaToCommit(head);
	}

	public void printLog() {
		for (String commitSHA : commitList) {
			Commit c = shaToCommit(commitSHA);
			System.out.println("===");
			System.out.println("Commit " + commitSHA);
			System.out.println(c.getCommitTime());
			System.out.println(c.getCommitMsg());
		}
	}
}