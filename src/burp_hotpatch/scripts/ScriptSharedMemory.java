package burp_hotpatch.scripts;


import java.util.HashMap;

public class ScriptSharedMemory {

    private HashMap<String,String> strings;
    private HashMap<String,Integer> integers;
    private HashMap<String,Object> objects;

    private static ScriptSharedMemory INSTANCE;
    public ScriptSharedMemory() {
        reset();
    }

    public static ScriptSharedMemory getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ScriptSharedMemory();
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

    public Integer getInt( String name ) {
        return integers.get(name);
    }

    public synchronized void setString( String name, String str ) {
        strings.put(name,str);
    }

    public String getString( String name ) {
        return strings.get(name);
    }

    public synchronized void setObject( String name, Object obj ) {
        objects.put(name,obj);
    }

    public Object getObject( String name ) {
        return objects.get(name);
    }

}