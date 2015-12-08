package gitlet;
import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private static final String GITLET_DIR = ".gitlet/";
    private static final String STAGED_DIR = ".gitlet/staged/";
    private static final String COMMIT_DIR = ".gitlet/commits/";

    /** Initialize Gitlet before testing occurs. */
    @Before
    public void before() {
        File f = new File(GITLET_DIR);
        if (!f.exists()) {
            Gitlet.init();
        }
    }

    /** Tests init. */
    @Test
    public void testInit() {
        File s = new File(STAGED_DIR);
        File b = new File(".gitlet/blobs");
        File c = new File(COMMIT_DIR);
        assertTrue(s.exists());
        assertTrue(b.exists());
        assertTrue(c.exists());
    }

    /** Tests basic commands such as add, remove, and commit. */
    @Test
    public void testBasic() {
        Gitlet.init();
        String name = "hello.txt";
        String contents = "hello";
        createFile(name, contents);
        File f = new File(name);
        assertTrue(f.exists());
        Gitlet.add(name);
        File g = new File(STAGED_DIR + name);
        assertTrue(g.exists());
        Gitlet.rm("hello.txt");
        assertFalse(g.exists());
        Gitlet.commit("added hello");
    }

    /** Tests find and second commit. */
    @Test
    public void testFind() {
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

    /** Gets the contents of the file from filename NAME. */
    private static String getContents(String name) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(name));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    /** Creates a file with the name NAME with the contents CONTENTS. */
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

    /** Writes the file with given NAME and contents CONTENTS. */
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

