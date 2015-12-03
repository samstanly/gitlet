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
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectInput;

public class Branch implements Serializable {
	protected String head;
	protected String name;
	protected Commit currCommit;
	protected LinkedList<String> commitList = new LinkedList<String>();

	protected HashSet<String> staged = new HashSet<String>();
	protected HashSet<String> untracked;

	protected static void serialWrite(Branch b) {
		try {
			ObjectOutput output = new ObjectOutputStream(new FileOutputStream(".gitlet/branch.ser"));
			output.writeObject(b);
			output.close();
		} catch (IOException e) {
			System.out.println("Error in serialWrite.");
		}
	}

	protected static Branch serialRead() {
		Branch b = null;
		try {
			ObjectInput input = new ObjectInputStream(new FileInputStream(".gitlet/branch.ser"));

			try {
				b = (Branch) input.readObject();
				System.out.println(b);
				input.close();
			} catch (ClassNotFoundException e2) {
				input.close();
				System.out.println("ClassNotFoundException in serialRead");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error in Branch serialRead.");

		}
		return b;
	}

	Branch(String name, Commit currCommit) {
		this.name = name;
		this.currCommit = currCommit;
	}

	Commit shaToCommit(String sha) {
		// System.out.println("we" + sha);
		// Commit h = null;
		// try {
	 //      	FileInputStream fileIn = new FileInputStream(sha);
	 //      	ObjectInputStream in = new ObjectInputStream(fileIn);
	 //       	h = (Commit) in.readObject();
	 //       	in.close();
	 //       	fileIn.close();
  //   	} catch(IOException i) {
  //   		i.printStackTrace();
  //      		return null;
  //   	} catch (ClassNotFoundException i) {
  //   		i.printStackTrace();
  //      		return null;
  //   	}
  //   	return h;

		return Commit.serialRead(sha);

	}

	Commit getHeadCommit() {
		return shaToCommit(head);
	}

	void addCommit(String commitSHA) {
		System.out.println(commitSHA);
		commitList.addFirst(commitSHA);
		head = commitSHA;
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