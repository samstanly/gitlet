package gitlet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Date;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.io.InputStream;
import java.io.InputStreamReader;
import ucb.util.CommandArgs;

public class Gitlet {
	static void init() {
		String curr = System.getProperty("user.dir");
    	File dir = new File(curr + ".gitlet");
    	boolean e = dir.mkdir();
    	if (!e) {
    		System.out.println("gitlet version-control system already exists in the current directory");
      	} else {
        //initial commit
        //call commit
        	
      	}
	}
	static void add(String name) {
		File filename = new File(name);
		if (!filename.exists()) {
			System.out.println("File does not exist");
			return;
		}
		
	}
}