package hr.java.game.dixitmultiplayergame.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class DocumentationUtils {
    public static void generateDocumentation() {

        StringBuilder documentationGenerator = new StringBuilder();

        String rootPath = Paths.get(".").toAbsolutePath().normalize().toString();
        String path = Paths.get(rootPath, "src", "main", "java").toString();
        System.out.println("Current absolute path is: " + path);

        try {
            List<Path> classNameList = Files.walk(Paths.get(path))
                    .filter(p -> p.getFileName().toString().endsWith(".java"))
                    .filter(p -> !p.getFileName().toString().equals("module-info.java"))
                    .toList();

            for (Path classPath : classNameList) {

                int indexOfHr = classPath.toString().indexOf("hr");
                String fqcn = classPath.toString().substring(indexOfHr);
                fqcn = fqcn.replace('\\', '.');
                fqcn = fqcn.substring(0, fqcn.length() - 5);

                Class<?> documentationClass = Class.forName(fqcn);

                String classModifiers = Modifier.toString(documentationClass.getModifiers());

                documentationGenerator.append("<h2>")
                        .append(classModifiers)
                        .append(" ")
                        .append(fqcn)
                        .append("</h2>\n");

                Field[] classVariables = documentationClass.getDeclaredFields();

                for (Field field : classVariables) {
                    field.setAccessible(true);
                    String modifiers = Modifier.toString(field.getModifiers());
                    documentationGenerator.append("<h3>")
                            .append(modifiers)
                            .append(" ")
                            .append(field.getType().getName())
                            .append(" ")
                            .append(field.getName())
                            .append("</h3>\n");
                }

                Constructor<?>[] classConstructors = documentationClass.getConstructors();

                for (Constructor<?> constructor : classConstructors) {
                    String modifiers = Modifier.toString(constructor.getModifiers());
                    int parameterCount = constructor.getParameterCount();
                    if (parameterCount > 0) {
                        documentationGenerator.append("<h4>")
                                .append(modifiers)
                                .append(" ")
                                .append(constructor.getName())
                                .append(" (")
                                .append(parameterCount)
                                .append(Arrays.toString(constructor.getParameters()))
                                .append(") ")
                                .append("</h4>\n");
                    }
                }



            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                <title>Documentation</title>
                </head>
                <body>
                <h1>List of classes</h1>
                """
                + documentationGenerator +
                """
                </body>
                </html>
                """;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("documentation/doc.html"))) {
            writer.write(html);
            DialogUtils.showSuccessDialog("Documentation was successfully generated!");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
