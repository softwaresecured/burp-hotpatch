package burp_jyconsole.scripts;

import java.util.ArrayList;

public class StdoutLogger {
    private ArrayList<String> log = new ArrayList<>();
    public StdoutLogger() {

    }
    public void logMessage( String message ) {
        log.add(message);
    }

    public String getLog() {
        if (log.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for ( String message : log ) {
            sb.append(String.format("%s\n", message));
        }
        return sb.toString();
    }
}
