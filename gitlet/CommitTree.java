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

public class CommitTree implements Serializable {
	protected String head;
	protected Commit currCommit;
	protected LinkedList<String> commitList = new LinkedList<String>();

	protected HashSet<String> staged = new HashSet<String>();
	protected HashSet<String> untracked =  new HashSet<String>();

	protected static void serialWrite(CommitTree b) {
		try {
			ObjectOutput output = new ObjectOutputStream(new FileOutputStream(".gitlet/branch.ser"));
			output.writeObject(b);
			output.close();
		} catch (IOException e) {
			System.out.println("Error in serialWrite.");
		}
	}

	protected static CommitTree serialRead() {
		CommitTree b = null;
		try {
			ObjectInput input = new ObjectInputStream(new FileInputStream(".gitlet/branch.ser"));

			try {
				b = (CommitTree) input.readObject();
				System.out.println(b);
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

	CommitTree(Commit currCommit) {
		this.currCommit = currCommit;
	}


	Commit getHeadCommit() {
		System.out.println(head);
		return Commit.shaToCommit(head);
	}





	public void printLog() {
		for (String commitSHA : commitList) {
			Commit c = Commit.shaToCommit(commitSHA);
			System.out.println("===");
			System.out.println("Commit " + commitSHA);
			System.out.println(c.getCommitTime());
			System.out.println(c.getCommitMsg());
		}
	}
}