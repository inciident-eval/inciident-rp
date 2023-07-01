
package inciident.util.extension;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import inciident.util.logging.Logger;


public class ExtensionLoader {
    
    private static HashMap<String, List<String>> extensionMap;

    
    public static synchronized void unload() {
        if (extensionMap != null) {
            extensionMap.clear();
            extensionMap = null;
        }
    }

    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static synchronized void load() {
        if (extensionMap == null) {
            extensionMap = new HashMap<>();
            getResources().stream() //
                    .filter(ExtensionLoader::filterByFileName) //
                    .peek(Logger::logDebug)
                    .forEach(ExtensionLoader::load);
            final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            for (final Entry<String, List<String>> entry : extensionMap.entrySet()) {
                final String extensionPointId = entry.getKey();
                try {
                    final Class<?> extensionPointClass = systemClassLoader.loadClass(extensionPointId);
                    final Method instanceMethod = extensionPointClass.getDeclaredMethod("getInstance");
                    final ExtensionPoint ep = (ExtensionPoint) instanceMethod.invoke(null);
                    for (final String extensionId : entry.getValue()) {
                        try {
                            final Class<?> extensionClass = systemClassLoader.loadClass(extensionId);
                            Logger.logDebug(extensionClass.toString());
                            ep.addExtension(
                                    (Extension) extensionClass.getConstructor().newInstance());
                        } catch (final Exception e) {
                            Logger.logError(e);
                        }
                    }
                } catch (final Exception e) {
                    Logger.logError(e);
                }
            }
        }
    }

    
    private static boolean filterByFileName(String pathName) {
        try {
            if (pathName != null) {
                return Paths.get(pathName).getFileName().toString().matches("extensions(-.*)?[.]xml");
            }
            return false;
        } catch (final Exception e) {
            Logger.logError(e);
            return false;
        }
    }

    
    private static void load(String file) {
        try {
            final Enumeration<URL> systemResources =
                    ClassLoader.getSystemClassLoader().getResources(file);
            while (systemResources.hasMoreElements()) {
                try {
                    final DocumentBuilder documentBuilder =
                            DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    final Document document =
                            documentBuilder.parse(systemResources.nextElement().openStream());
                    document.getDocumentElement().normalize();

                    final NodeList points = document.getElementsByTagName("point");
                    for (int i = 0; i < points.getLength(); i++) {
                        final Node point = points.item(i);
                        if (point.getNodeType() == Node.ELEMENT_NODE) {
                            final Element pointElement = (Element) point;
                            final String extensionPointId = pointElement.getAttribute("id");
                            List<String> extensionPoint =
                                    extensionMap.computeIfAbsent(extensionPointId, k -> new ArrayList<>());
                            final NodeList extensions = pointElement.getChildNodes();
                            for (int j = 0; j < extensions.getLength(); j++) {
                                final Node extension = extensions.item(j);
                                if (extension.getNodeType() == Node.ELEMENT_NODE) {
                                    final Element extensionElement = (Element) extension;
                                    final String extensionId = extensionElement.getAttribute("id");
                                    extensionPoint.add(extensionId);
                                }
                            }
                        }
                    }
                } catch (final Exception e) {
                    Logger.logError(e);
                }
            }
        } catch (final Exception e) {
            Logger.logError(e);
        }
    }

    
    private static List<String> getResources() {
        final HashSet<String> resources = new HashSet<>();
        final String classPathProperty = System.getProperty("java.class.path", ".");
        final String pathSeparatorProperty = System.getProperty("path.separator");
        for (final String element : classPathProperty.split(pathSeparatorProperty)) {
            final Path path = Paths.get(element);
            Logger.logDebug(path);
            try {
                if (Files.isRegularFile(path)) {
                    try (ZipFile zf = new ZipFile(path.toFile())) {
                        zf.stream().map(ZipEntry::getName).forEach(resources::add);
                    }
                } else if (Files.isDirectory(path)) {
                    Files.walk(path).map(path::relativize).map(Path::toString).forEach(resources::add);
                }
            } catch (final IOException e) {
                Logger.logError(e);
            }
        }
        return new ArrayList<>(resources);
    }
}
