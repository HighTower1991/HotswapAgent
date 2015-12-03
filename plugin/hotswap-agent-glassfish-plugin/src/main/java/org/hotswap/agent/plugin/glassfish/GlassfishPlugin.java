package org.hotswap.agent.plugin.glassfish;

import org.hotswap.agent.annotation.*;
import org.hotswap.agent.command.Scheduler;
import org.hotswap.agent.javassist.CannotCompileException;
import org.hotswap.agent.javassist.CtClass;
import org.hotswap.agent.javassist.NotFoundException;
import org.hotswap.agent.logging.AgentLogger;
import org.hotswap.agent.util.PluginManagerInvoker;
import org.hotswap.agent.util.ReflectionHelper;

@Plugin(name = "GlassfishPlugin", description = "Hotswap agent plugin for reload class for deployed projects",
        testedVersions = "4.1.1",
        expectedVersions = "4.1*")
public class GlassfishPlugin {

    public static final String PLUGIN_PACKAGE = "org.hotswap.agent.plugin.glassfish";

    private static AgentLogger LOGGER = AgentLogger.getLogger(GlassfishPlugin.class);
    private static final String WEBAPP_CLASSLOADER = "org.glassfish.web.loader.WebappClassLoader";
    private static final String APP_LOADER = "org.apache.catalina.loader.WebappLoader";

    @OnClassLoadEvent(classNameRegexp = APP_LOADER)
    public static void transformWebappClassLoaader(CtClass ctClass) throws NotFoundException, CannotCompileException {

        String src = PluginManagerInvoker.buildInitializePlugin(GlassfishPlugin.class);
        src += PluginManagerInvoker.buildCallPluginMethod(GlassfishPlugin.class,
                "init",
                "getClassLoader()", "java.lang.Object",
                "getContainer()", "java.lang.Object"
                );
        ctClass.getDeclaredMethod("start").insertAfter(src);

        LOGGER.debug(APP_LOADER + " has been enhanced.");
    }

    @Init
    ClassLoader appClassLoader;

    public void init(Object webappClassloader, Object container) {
        this.webappClasssLoader = webappClassloader;
        this.container = container;
        LOGGER.info("Plugin {} initialized on ClassLoader: {} on Container: {}", getClass(), this.webappClasssLoader, this.container);
        ReflectionHelper.invoke(webappClasssLoader, webappClassloader.getClass(), "start", new Class[0], new Object[0]);
        LOGGER.info("Plugin {} initialize classLoader: {} by start() method", getClass(), this.webappClasssLoader);
    }

    Object webappClasssLoader;
    Object container;

    @Init
    Scheduler scheduler;

}
