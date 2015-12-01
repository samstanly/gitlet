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

public class Metadata implements Serializable {
	protected static String HEAD;

	protected static String GITLET_DIRECTORY;

	static void setDirectory(String path) {
		GITLET_DIRECTORY = path;
	}

	static String getWorking(String path) {
		return GITLET_DIRECTORY;
	}

	static void setHead(Commit initial) {
		HEAD = initial.getPath();
	}

	static Commit getHead() {
		try
    	{
      	FileInputStream fileIn = new FileInputStream(HEAD);
      	ObjectInputStream in = new ObjectInputStream(fileIn);
       	h = (Commit) in.readObject();
       	in.close();
       	fileIn.close();
    	} catch(IOException i) {
    		i.printStackTrace();
       	return null;
    	}
    	return h;
	}
}