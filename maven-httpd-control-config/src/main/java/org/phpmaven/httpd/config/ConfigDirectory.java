/**
 * 
 */
package org.phpmaven.httpd.config;

import org.phpmaven.httpd.control.IApacheConfigDirectory;

/**
 * 
 * @author mepeisen
 */
class ConfigDirectory extends AbstractConfigCommon implements IApacheConfigDirectory, IConfigFileLineSection {

    /**
     * The path of this directive.
     */
    private String path;
    
    /**
     * The tool.
     */
    private AbstractConfigTool tool;

    /**
     * Constructor.
     * @param directory
     */
    public ConfigDirectory(String directory, AbstractConfigTool tool) {
        if (directory.startsWith("\"") && directory.endsWith("\"")) {
            directory = directory.substring(1, directory.length() - 1);
        }
        this.path = directory;
        this.tool = tool;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getSectionName() {
        return "Directory";
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("<Directory \"");
        buffer.append(this.path.replace("\\", "/"));
        buffer.append("\">\n");
        buffer.append(super.toString());
        buffer.append("</Directory>\n");
        return buffer.toString();
    }

    @Override
    protected AbstractConfigTool getTooling() {
        return this.tool;
    }
    
}
