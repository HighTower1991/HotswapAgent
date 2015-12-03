package org.hotswap.agent.plugin.glassfish;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.junit.Test;

/**
 *
 * @author lysenko
 */
public class CopyTest {
    @Test
    public void test() throws Exception {
        String helloWorldFile = ForHotswap.class.getName().replace(".", "/") + ".class";
        File[] listFiles = Paths.get("target").toFile().listFiles();
        for (File listFile : listFiles) {
            System.out.println(listFile);
        }
        Path source = Paths.get("target/hotswap/" + helloWorldFile);
        File toFile = Paths.get("target/glassfish4/glassfish/domains/domain1/applications/Test/WEB-INF/classes/").toFile();
        if (!toFile.exists()) {
            toFile.mkdirs();
        }
        Path target = Paths.get("target/glassfish4/glassfish/domains/domain1/applications/Test/WEB-INF/classes/" + helloWorldFile);
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

    }

}
