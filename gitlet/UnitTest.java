package gitlet;
import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectInput;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileAlreadyExistsException;
import java.util.TreeSet;
import java.util.Arrays;
import java.nio.file.NoSuchFileException;
import org.junit.Before;
import java.nio.charset.StandardCharsets;

/** The suite of all JUnit tests for the gitlet package.
 *  @author Sam Steady and Jamie Ni
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    private static final String gitletDir = ".gitlet/";
    private static final String stagedDir = ".gitlet/staged/";
    private static final String commitDir = ".gitlet/commits/";

    /** A dummy test to avoid complaint. */
    @Before
    public void before() {
    	File f = new File(gitletDir);
    	if (!f.exists()) {
			Gitlet.init();
		}
    }

    @Test
	public void testInit() {
		File s = new File (stagedDir);
		File b = new File (".gitlet/blobs");
		File c = new File(commitDir);
		File tree = new File(".gitlet/tree.ser");
		assertTrue(s.exists());
		assertTrue(b.exists());
		assertTrue(c.exists());
		assertTrue(tree.exists());
	}
	@Test
	public void testBasic() {
		String name = "hello.txt";
		String contents = "hello";
		createFile(name, contents);
		File f = new File(name);
		assertTrue(f.exists());
		Gitlet.add(name);
		File g = new File(stagedDir + name);
		assertTrue(g.exists());
		Gitlet.rm("hello.txt");
		assertFalse(g.exists());
		Gitlet.commit("added hello");
	}

	@Test
	public void testReset() {
		String file1 = "file1.txt";
		createFile(file1, "red");
		String file2 = "file2.txt";
		createFile(file2, "orange");
		Gitlet.add(file1);
		Gitlet.add(file2);
		Gitlet.commit("colors");
		createFile(file1, "pink");
		Gitlet.add(file1);
		Gitlet.commit("colors 2");
		assertEquals("pink", getContents(file1));
		assertEquals("orange", getContents(file2));
		Gitlet.find("colors");
	}

	private static String getContents(String name) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(name));
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			return "";
		}
	}

	private static void createFile(String name, String contents) {
		File f = new File(name);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		writeFile(name, contents);
	}

	private static void writeFile(String name, String contents) {
		FileWriter w = null;
		try {
			File f = new File(name);
			w = new FileWriter(f, false);
			w.write(contents);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}



