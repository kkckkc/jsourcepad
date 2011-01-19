package kkckkc.utils.io;


import junit.framework.Assert;
import org.junit.Test;

import java.io.File;

public class FileUtilsTest {

    @Test
    public void testIsAncestorOf() throws Exception {
        File root = File.listRoots()[0];
        File level1 = root.listFiles()[0];
        File level2 = level1.listFiles()[0];

        // A file is not ancestor to it self
        Assert.assertFalse(FileUtils.isAncestorOf(root, root));
        Assert.assertFalse(FileUtils.isAncestorOf(level1, level1));

        // The positive cases
        Assert.assertTrue(FileUtils.isAncestorOf(level1, root));
        Assert.assertTrue(FileUtils.isAncestorOf(level2, root));
        Assert.assertTrue(FileUtils.isAncestorOf(level2, level1));

        // Negative case
        Assert.assertFalse(FileUtils.isAncestorOf(level1, level2));

        // Null handling
        Assert.assertFalse(FileUtils.isAncestorOf(null, root));
        Assert.assertFalse(FileUtils.isAncestorOf(root, null));
    }

    @Test
    public void testGetBaseName() throws Exception {
        Assert.assertEquals("test", FileUtils.getBaseName(new File("test")));
        Assert.assertEquals("test", FileUtils.getBaseName(new File("test.txt")));
        Assert.assertEquals("test.test", FileUtils.getBaseName(new File("test.test.txt")));
    }

    @Test
    public void testShortenWithTildeNotation() throws Exception {
        Assert.assertEquals("~/test.txt", FileUtils.shortenWithTildeNotation(new File(System.getProperty("user.home") + "/test.txt").getPath()));
        Assert.assertEquals("/test/test.txt", FileUtils.shortenWithTildeNotation(new File("/test/test.txt").getPath()));
    }

    @Test
    public void testExpandTildeNotation() throws Exception {
        Assert.assertEquals("/test/test.txt", FileUtils.expandTildeNotation("/test/test.txt"));
        Assert.assertEquals(new File(System.getProperty("user.home") + "/test.txt").getPath(), FileUtils.expandTildeNotation("~/test.txt"));
    }
}
