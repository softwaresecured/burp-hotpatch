package burp_jyconsole.mvc;

public class MVC<TModel extends AbstractModel<?>, TView extends AbstractView<?, TModel, ?>, TController extends AbstractController<?, TModel>> {
    public final TModel model;
    public final TView view;
    public final TController controller;

    public MVC(TModel model, TView view, TController controller) {
        this.model = model;

        this.view = view;
        this.model.addListener(this.view);

        this.controller = controller;
        this.view.addListener(this.controller);
    }

    public TModel getModel() {
        return model;
    }

    public TView getView() {
        return view;
    }

    public TController getController() {
        return controller;
    }
}
