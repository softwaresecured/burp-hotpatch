package burp_jyconsole.mvc;

import java.beans.PropertyChangeListener;

public abstract class AbstractController<TEvent extends Enum<TEvent>, TModel extends AbstractModel<?>> implements PropertyChangeListener {
    private final TModel model;

    public AbstractController(TModel model) {
        this.model = model;
    }

    protected abstract void handleEvent(TEvent event, Object previous, Object next);

    public TModel getModel() {
        return model;
    }
}
