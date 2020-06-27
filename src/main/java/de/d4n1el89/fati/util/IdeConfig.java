package de.d4n1el89.fati.util;

import org.jdom2.Element;

import java.io.File;
import java.util.List;

public enum IdeConfig implements XMLElementSelector {

    ECLIPSE("*_client.launch","", "org.eclipse.jdt.launching.PROGRAM_ARGUMENTS"){
        @Override
        public List<Element> getChildren(Element parent) {
            return parent.getChildren("stringAttribute");
        }
    },
    IDEA("Minecraft_Client.xml",".idea" + File.separator + "runConfigurations" + File.separator, "PROGRAM_PARAMETERS") {
        @Override
        public List<Element> getChildren(Element parent) {
            return parent.getChild("configuration").getChildren("option");
        }
    };

    public final String launchFileName;
    public final String path;
    public final String programParamsKey;

    IdeConfig(String launchFileName, String path, String programParamsKey){
        this.launchFileName = launchFileName;
        this.path = path;
        this.programParamsKey = programParamsKey;
    }
}
