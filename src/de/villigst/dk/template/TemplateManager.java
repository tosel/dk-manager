package de.villigst.dk.template;

import de.villigst.dk.logic.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public enum TemplateManager {

    MELDESCHILDER {
        @Override
        public String getString(String name, String konvent) {
            try {
                String s = Files.readString(Paths.get("src/de/villigst/dk/template/meldeschild_a4.html"));
                s = s.replace("{{name}}", name);
                s = s.replace("{{gruppe}}", konvent);
                return s;
            }catch(IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }, NAMENSSCHILD_ENTITY {
        @Override
        public String getString(String name, String konvent) {
            try {
                String s = Files.readString(Paths.get("src/de/villigst/dk/template/namensschild_entity.html"));
                s = s.replace("{{name}}", name);
                s = s.replace("{{gruppe}}", konvent);
                return s;
            }catch(IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }, NAMENSSCHILD_WRAPPER {
        public String getString(List<String> htmls) {
            try {
                if(htmls.size() <= NAMENSSCHILDER_PRO_SEITE) {
                    String s = Files.readString(Paths.get("src/de/villigst/dk/template/namensschild_wrapper_a4.html"));
                    String page = "";
                    for(String html : htmls) {
                        page += html;
                    }
                    s = s.replace("{{page}}", page);
                    return s;
                }else {
                    Logger.error("Too many inputs for one page!");
                    return null;
                }
            }catch(IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }, GREMIENSCHILD {
        @Override
        public String getString(String name, String amt) {
            try {
                String s = Files.readString(Paths.get("src/de/villigst/dk/template/gremienschild_a4.html"));
                s = s.replace("{{name}}", name);
                s = s.replace("{{amt}}", amt);
                return s;
            }catch(IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    };

    public static int NAMENSSCHILDER_PRO_SEITE = 12;

    public String getString(String name, String konvent) {
        return null;
    }

    public String getString(List<String> htmls) {
        return null;
    }

}
