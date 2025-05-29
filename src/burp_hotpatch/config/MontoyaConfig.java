package burp_hotpatch.config;

import burp.api.montoya.persistence.PersistedList;
import burp.api.montoya.persistence.PersistedObject;
import burp.api.montoya.persistence.Persistence;
import burp_hotpatch.enums.ConfigKey;

import java.util.ArrayList;
import java.util.List;

public class MontoyaConfig extends AbstractConfig {
    private final Persistence persistence;

    public MontoyaConfig(Persistence persistence) {
        this.persistence = persistence;
    }

    private PersistedObject getData() {
        return persistence.extensionData();
    }

    @Override
    public String getString(ConfigKey key, String def) {
        String value = getData().getString(key.resolve());

        return value != null ? value : def;
    }

    @Override
    public List<String> getStringList(ConfigKey key) {
        PersistedList<String> value = getData().getStringList(key.resolve());

        return value != null ? value : new ArrayList<String>();
    }

    @Override
    public boolean getBoolean(ConfigKey key, boolean def) {
        Boolean value = getData().getBoolean(key.resolve());

        return value != null ? value : def;
    }

    @Override
    public int getInteger(ConfigKey key, int def) {
        Integer value = getData().getInteger(key.resolve());

        return value != null ? value : def;
    }

    @Override
    public void setString(ConfigKey key, String value) {
        getData().setString(key.resolve(), value);
    }

    @Override
    public void setStringList(ConfigKey key, List<String> value) {
        PersistedList<String> list = PersistedList.persistedStringList();

        for (String v : value) {
            list.add(v);
        }

        getData().setStringList(key.resolve(), list);
    }

    @Override
    public void setBoolean(ConfigKey key, boolean value) {
        getData().setBoolean(key.resolve(), value);
    }

    @Override
    public void setInteger(ConfigKey key, int value) {
        getData().setInteger(key.resolve(), value);
    }
}
