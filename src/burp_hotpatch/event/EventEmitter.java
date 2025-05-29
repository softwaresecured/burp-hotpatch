package burp_hotpatch.event;

import java.beans.PropertyChangeListener;

public interface EventEmitter<TEvent extends Enum<TEvent>> {
    void addListener(PropertyChangeListener listener);
    void emit(TEvent event, Object old, Object value);
}
