package burp_jyconsole.config;

import burp_jyconsole.enums.ConfigKey;

import java.util.List;

public abstract class AbstractConfig {
    public abstract String getString(ConfigKey key, String def);
    public abstract List<String> getStringList(ConfigKey key);
    public abstract boolean getBoolean(ConfigKey key, boolean def);
    public abstract int getInteger(ConfigKey key, int def);

    public abstract void setString(ConfigKey key, String value);
    public abstract void setStringList(ConfigKey key, List<String> value);
    public abstract void setBoolean(ConfigKey key, boolean value);
    public abstract void setInteger(ConfigKey key, int value);

    public String getString(ConfigKey key) {
        return getString(key, "");
    }

    public boolean getBoolean(ConfigKey key) {
        return getBoolean(key, false);
    }

    public int getInteger(ConfigKey key) {
        return getInteger(key, 0);
    }
}
