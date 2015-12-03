package org.hotswap.agent.plugin.glassfish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author lysenko
 */
@RunWith(Arquillian.class)
public class InitIT {
    
    @Deployment
    public static WebArchive createTestArchive() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "Test.war");
        archive.addClasses(ForHotswap.class, ActivatorServlet.class);
        archive.addAsResource("hotswap-agent.properties");
        return archive
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

    }
    
    @ArquillianResource public URL url;

    @RunAsClient
    @Test
    public void testHello() throws Exception {
        System.out.println(url);
        hotswap(ForHotswap.class);
        System.out.println("Swap");
        Thread.sleep(20000);
        assertEquals("Hello Hotswap", readResponce());
    }

    public static void hotswap(Class toHotswap) throws Exception {
        String swapedFile = toHotswap.getName().replace(".", "/") + ".class";
        Path source = Paths.get("target/hotswap/" + swapedFile);
        Path target = Paths.get("target/glassfish4/glassfish/domains/domain1/applications/Test/WEB-INF/classes/" + swapedFile);
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    String readResponce() throws IOException, MalformedURLException {
        URL oracle = new URL("http://"+url.getHost()+":"+url.getPort()+"/Test/activator");
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine = in.readLine();
        in.close();
        return inputLine;
    }


}
