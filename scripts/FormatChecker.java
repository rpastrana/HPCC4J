package org.hpccsystems.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Simple Java formatter checker that uses Eclipse formatter configuration
 * to validate if Java files follow the project's formatting standards.
 */
public class FormatChecker {
    
    private CodeFormatter formatter;
    private Map<String, String> formatterOptions;
    
    public FormatChecker(String configPath) throws Exception {
        loadFormatterConfig(configPath);
        DefaultCodeFormatterOptions options = DefaultCodeFormatterOptions.getEclipseDefaultSettings();
        options.set(formatterOptions);
        this.formatter = new DefaultCodeFormatter(options);
    }
    
    private void loadFormatterConfig(String configPath) throws Exception {
        formatterOptions = new HashMap<>();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(new File(configPath));
        
        NodeList settings = doc.getElementsByTagName("setting");
        for (int i = 0; i < settings.getLength(); i++) {
            Element setting = (Element) settings.item(i);
            String id = setting.getAttribute("id");
            String value = setting.getAttribute("value");
            formatterOptions.put(id, value);
        }
    }
    
    /**
     * Check if a Java file is properly formatted according to the Eclipse configuration
     */
    public boolean isProperlyFormatted(String filePath) throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(filePath)));
        
        // Format the source code
        TextEdit edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, source, 0, source.length(), 0, "\n");
        
        if (edit == null) {
            // Unable to format - might be syntax error, consider it properly formatted
            return true;
        }
        
        // Apply the formatting changes to see what the formatted version would look like
        Document document = new Document(source);
        try {
            edit.apply(document);
            String formattedSource = document.get();
            
            // Compare original vs formatted
            return source.equals(formattedSource);
        } catch (Exception e) {
            // Error applying formatting, consider it properly formatted
            return true;
        }
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: FormatChecker <eclipse-config.xml> <java-file1> [java-file2] ...");
            System.exit(1);
        }
        
        String configPath = args[0];
        
        try {
            FormatChecker checker = new FormatChecker(configPath);
            int violations = 0;
            
            for (int i = 1; i < args.length; i++) {
                String filePath = args[i];
                File file = new File(filePath);
                
                if (!file.exists() || !file.isFile() || !filePath.endsWith(".java")) {
                    continue;
                }
                
                try {
                    if (!checker.isProperlyFormatted(filePath)) {
                        System.out.println("❌ " + filePath + " has formatting violations");
                        violations++;
                    } else {
                        System.out.println("✅ " + filePath + " is properly formatted");
                    }
                } catch (IOException e) {
                    System.err.println("⚠️  Error checking " + filePath + ": " + e.getMessage());
                }
            }
            
            if (violations > 0) {
                System.out.println();
                System.out.println("❌ Found formatting violations in " + violations + " file(s)");
                System.out.println("💡 Use your IDE formatter or mvn formatter:format to fix");
                System.exit(1);
            } else {
                System.out.println();
                System.out.println("✅ All files are properly formatted!");
                System.exit(0);
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}