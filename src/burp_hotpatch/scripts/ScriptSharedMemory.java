package burp_hotpatch.scripts;
import java.util.HashMap;

public class ScriptSharedMemory {

    private HashMap<String,String> strings;
    private HashMap<String,Integer> integers;
    private HashMap<String,Object> objects;

    private static ScriptSharedMemory INSTANCE;
    public ScriptSharedMemory() {
    }

    public static ScriptSharedMemory getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ScriptSharedMemory();
            INSTANCE.reset();
        }
        return INSTANCE;
    }

    public void reset() {
        strings = new HashMap<>();
        integers = new HashMap<>();
        objects = new HashMap<>();
    }

    public synchronized void setInt( String name, int num ) {
        integers.put(name,num);
    }

    public int getInt( String name ) throws ScriptSharedMemoryException {
        if ( integers.get(name) == null ) {
            throw new ScriptSharedMemoryException(String.format("Key %s does not exist", name));
        }
        return integers.get(name);
    }

    public synchronized void setString( String name, String str ) {
        strings.put(name,str);
    }

    public String getString( String name ) throws ScriptSharedMemoryException {
        if ( objects.get(name) == null ) {
            throw new ScriptSharedMemoryException(String.format("Key %s does not exist", name));
        }
        return strings.get(name);
    }

    public synchronized void setObject( String name, Object obj ) {
        objects.put(name,obj);
    }

    public Object getObject( String name ) throws ScriptSharedMemoryException {
        if ( objects.get(name) == null ) {
            throw new ScriptSharedMemoryException(String.format("Key %s does not exist", name));
        }
        return objects.get(name);
    }

}