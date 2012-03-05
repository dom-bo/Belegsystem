package multex.extension;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import multex.Exc;
import multex.Failure;
import multex.MultexUtil;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.google.common.base.Charsets;

/**
 * Klasse zum Extrahieren und Speichern von {@link ExcMessage} - Annotationen
 * aus Klassen die auf {@link Exc} & oder {@link Failure} aufbauen. Unterstützt
 * auch das Generieren einer ResourceBundle-jar-Datei und das Hinzufügen
 * derselben zum classpath.
 * 
 * 
 */
public abstract class ExcMessagesToProperties {

        private static final String PACKAGE_NAME_LISTING = "  - ";
        
        public static final String PROP_FILE_ENDING = ".properties";
        public static final String JAR_FILE_ENDING = ".jar";
        public static final String EXC_PROPERTIES_NAME = "exc";
        public static final String PROP_FILE_NAME = EXC_PROPERTIES_NAME + PROP_FILE_ENDING;
        public static final String JAR_FILE_NAME = EXC_PROPERTIES_NAME + JAR_FILE_ENDING;

        private static final String COMMENT_START = "Exception-MessagesPatterns for: ";
        
        private static final String LINE_SEPARATOR = System.getProperty("line.separator");

        @ExcMessage("File ({0}) couldn't be added to classpath.")
        public static final class FileAddToClasspathFailure extends AnnotatedFailure {
                private static final long serialVersionUID = -5206949530626872320L;
        }

        @ExcMessage("File ({0}) is located outside of classpath.")
        public static final class FileLocationOutsideOfClasspathExc extends AnnotatedExc {
                private static final long serialVersionUID = 3018143146653094115L;
        }

        public static void write(File toFile, boolean onClasspath, String... packages) throws IOException {
                if (onClasspath) {
                        toFile = getFileOnClasspath(toFile);
                } else {
                        toFile = getFile(toFile);
                }
                Properties properties = new Properties();
                properties.load(new InputStreamReader(new FileInputStream(toFile), Charsets.ISO_8859_1)); // als ISO-8859-1 einlesen

                if (packages.length == 0) {
                        packages = new String[] { "" };
                }

                Reflections reflections = new Reflections(packages, new TypeAnnotationsScanner());

                Set<String> propertyNames = properties.stringPropertyNames();

                for (Class<?> cls : reflections.getTypesAnnotatedWith(ExcMessage.class)) {
                        if (Exc.class.isAssignableFrom(cls) || Failure.class.isAssignableFrom(cls)) {
                                ExcMessage excMessage = cls.getAnnotation(ExcMessage.class);
                                if (excMessage.export()) {
                                        if (properties.getProperty(cls.getName()) == null || excMessage.overwritePreviousEntryInExport()) {
                                                properties.setProperty(cls.getName(), excMessage.value());
                                        }
                                        propertyNames.remove(cls.getName());
                                }
                        }
                }

                // veraltete (also nicht mehr vorhandene) löschen? ja.
                for (String propertyName : propertyNames) {
                        properties.remove(propertyName);
                }

                // und wieder als ISO-8859-1 speichern!
                properties.store(new OutputStreamWriter(new FileOutputStream(toFile), Charsets.ISO_8859_1), COMMENT_START
                                + LINE_SEPARATOR + getPackagesString(packages));

                

        }

        private static String getPackagesString(final String[] packages) {
                final String packagesString;
                if (packages.length == 1 && packages[0].isEmpty()) {
                        packagesString = PACKAGE_NAME_LISTING + "All packages under src/main/java";
                } else {
                        StringBuilder builder = new StringBuilder();
                        int i = 0;
                        while (i < packages.length - 1) {
                                builder.append(PACKAGE_NAME_LISTING + packages[i] + LINE_SEPARATOR);
                                i++;
                        }
                        builder.append(PACKAGE_NAME_LISTING + packages[i]);
                        packagesString = builder.toString();
                }
                return packagesString;
        }

        private static File getFile(File toFile) throws IOException {
                // if it doesn't have the Properties file Ending...
                if (!toFile.getPath().endsWith(PROP_FILE_ENDING)) {
                        // we create a new file using the previous file.path as directory
                        // above.
                        toFile = createFile(toFile.getPath());
                } else if (!toFile.exists()) {
                        // ok, it has the right ending, but it's not already existing in the
                        // filesystem - so we create it.
                        toFile.createNewFile();
                }
                return toFile;
        }

        private static File getFileOnClasspath(File toFile) throws IOException {
                if (toFile.isAbsolute()) {
                        boolean onClasspath = false;
                        for (String classpath : System.getProperty("java.class.path").split(System.getProperty("path.separator"))) {
                                if (toFile.getAbsolutePath().contains(classpath)) {
                                        onClasspath = true;
                                        break;
                                }
                        }

                        if (onClasspath) {
                                // if it doesn't have the Properties file Ending...
                                if (!toFile.getPath().endsWith(PROP_FILE_ENDING)) {
                                        // we create a new file using the previous file.path as
                                        // directory above.
                                        toFile = createFile(toFile.getPath());
                                } else if (!toFile.exists()) {
                                        // ok, it has the right ending, but it's not already
                                        // existing in the filesystem - so we create it.
                                        toFile.createNewFile();
                                }
                        } else {
                                // throw Exception if not on classpath
                                throw MultexUtil.create(FileLocationOutsideOfClasspathExc.class, toFile);
                        }
                } else {
                        toFile = createFile(getClasspathOfProject() + System.getProperty("file.separator") + toFile.getPath());
                }
                return toFile;
        }

        private static File createFile(String filePath) throws IOException {
                File toFile = new File(filePath);
                if (!toFile.getPath().endsWith(PROP_FILE_ENDING)) {
                        toFile = new File(toFile.getPath() + System.getProperty("file.separator") + PROP_FILE_NAME);
                }
                new File(toFile.getParent()).mkdirs();
                toFile.createNewFile();
                return toFile;
        }

        private static String getClasspathOfProject() {
                String[] classpaths = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
                for (String classpath : classpaths) {
                        if (classpath.contains(System.getProperty("user.dir"))) {
                                return classpath;
                        }
                }
                return classpaths[0];
        }

        public static void createJarAndAddToClasspath(File jarFile, File[] excFiles) throws IOException {
                final byte buffer[] = new byte[10204];

                FileOutputStream stream = new FileOutputStream(jarFile);
                JarOutputStream out = new JarOutputStream(stream, new Manifest());

                for (File excFile : excFiles) {
                        JarEntry jarAdd = new JarEntry(excFile.getName());
                        jarAdd.setTime(excFile.lastModified());
                        out.putNextEntry(jarAdd);

                        // Write file to archive
                        FileInputStream in = new FileInputStream(excFile);
                        while (true) {
                                int nRead = in.read(buffer, 0, buffer.length);
                                if (nRead <= 0)
                                        break;
                                out.write(buffer, 0, nRead);
                        }
                        in.close();
                }
                out.close();
                stream.close();

                addJarFileToClasspath(jarFile);
        }

        private static void addJarFileToClasspath(File jarFile) {
                URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                Class<URLClassLoader> sysclass = URLClassLoader.class;
                try {
                        Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
                        method.setAccessible(true);
                        method.invoke(sysloader, new Object[] { jarFile.toURI().toURL() });
                } catch (Throwable t) {
                        throw MultexUtil.create(FileAddToClasspathFailure.class, jarFile);
                }
        }
        
        @SuppressWarnings("unused") //wird momentan nicht benutzt...
        private static void sortPropertyEntries(File toFile, final String... packages) throws IOException {
                // Sortieren (und ExceptionMessages nach unten falls auch andere
                // Einträge vorhanden sind)
                // dafür lesen wir sie erneut ein:
                List<String> lines = new ArrayList<String>();
                List<String> comments = new ArrayList<String>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(toFile),
                                Charsets.ISO_8859_1)); // als ISO-8859-1 einlesen

                String line;
                while ((line = reader.readLine()) != null) {
                        if (line.startsWith("#")) {
                                comments.add(line);
                        } else if (!line.trim().isEmpty()) {
                                lines.add(line);
                        }
                }

                Collections.sort(lines, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                                if (isExcMessage(o1) && !isExcMessage(o2)) {
                                        return 1;
                                } else if (isExcMessage(o2) && !isExcMessage(o1)) {
                                        return -1;
                                }
                                return o1.compareTo(o2);
                        }

                });

                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(toFile), Charsets.ISO_8859_1);

                boolean excMessagesReached = false;
                for (String sortedLine : lines) {
                        if (!excMessagesReached && isExcMessage(sortedLine)) {
                                excMessagesReached = true;
                                writer.write(LINE_SEPARATOR);
                                for (String comment : comments) {
                                        writer.write(comment + LINE_SEPARATOR);
                                }
                        }
                        writer.write(sortedLine + LINE_SEPARATOR);
                }

                reader.close();
                writer.close();
        }
        
        private static boolean isExcMessage(String propertyLine, final String... packages) {
                for (String packageName : packages) {
                        if (propertyLine.startsWith(packageName)) {
                                return true;
                        }                                       
                }
                return false;
        }

}
 