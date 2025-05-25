package burp_jyconsole;

import burp.VERSION;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp_jyconsole.config.MontoyaConfig;
import burp_jyconsole.controller.BurpJyConsoleController;
import burp_jyconsole.model.BurpJyConsoleModel;
import burp_jyconsole.mvc.AbstractModel;
import burp_jyconsole.mvc.AbstractView;
import burp_jyconsole.mvc.MVC;
import burp_jyconsole.ui.BurpJyconsoleTab;
import burp_jyconsole.util.Logger;
import burp_jyconsole.util.MontoyaUtil;
import burp_jyconsole.view.BurpJyConsoleView;

public class BurpJyconsole implements BurpExtension, ExtensionUnloadingHandler {
    public static final String EXTENSION_NAME = "Burp Hotpatch";
    private MontoyaApi montoyaApi;
    private MVC<BurpJyConsoleModel, BurpJyConsoleView, BurpJyConsoleController> burpJyConsole;
    private MontoyaConfig config;

    public BurpJyconsole() {

    }

    private AbstractModel<?>[] getModels() {
        return new AbstractModel[] {
                burpJyConsole.getModel()
        };
    }

    private AbstractView<?, ?, ?>[] getViews() {
        return new AbstractView[] {
                burpJyConsole.getView()
        };
    }

    public void buildMVCs() {
        BurpJyConsoleModel burpJyConsoleModel = new BurpJyConsoleModel();
        this.burpJyConsole = new MVC<>(
                burpJyConsoleModel,
                new BurpJyConsoleView(burpJyConsoleModel),
                new BurpJyConsoleController(burpJyConsoleModel)
        );

    }

    public BurpJyconsoleTab buildTab() {
        BurpJyconsoleTab tab = new BurpJyconsoleTab(
                burpJyConsole.getView()
        );

        for (AbstractView<?, ?, ?> view : getViews()) {
            view.attachListeners();
        }

        return tab;
    }

    @Override
    public void initialize(MontoyaApi api) {
        MontoyaUtil montoyaUtil = MontoyaUtil.getInstance();
        montoyaUtil.setMontoyaApi(api);
        Logger.setLogger(montoyaUtil.getApi().logging());
        montoyaUtil.getApi().extension().setName(EXTENSION_NAME);
        buildMVCs();
        BurpJyconsoleTab tab = buildTab();
        this.config = new MontoyaConfig(api.persistence());
        for (AbstractModel<?> model : getModels()) {
            model.load(config);
        }
        montoyaUtil.getApi().userInterface().registerSuiteTab(EXTENSION_NAME, tab);
        montoyaUtil.getApi().extension().registerUnloadingHandler(this);
        montoyaUtil.getApi().http().registerHttpHandler(burpJyConsole.getController());
        montoyaUtil.getApi().http().registerSessionHandlingAction(burpJyConsole.getController());
        montoyaUtil.getApi().proxy().registerRequestHandler(burpJyConsole.getController());
        montoyaUtil.getApi().intruder().registerPayloadProcessor(burpJyConsole.getController());
        Logger.log("INFO", String.format("Burp %s %s loaded", EXTENSION_NAME, VERSION.getVersionStr()));
    }


    @Override
    public void extensionUnloaded() {
        for (AbstractModel<?> model : getModels()) {
            model.save(config);
        }
    }
}