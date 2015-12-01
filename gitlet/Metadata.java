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

public class Metadata implements Serializable {
	protected String HEAD;
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