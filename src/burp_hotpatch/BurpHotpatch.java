package burp_hotpatch;

import burp.VERSION;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp_hotpatch.config.MontoyaConfig;
import burp_hotpatch.controller.BurpHotpatchController;
import burp_hotpatch.model.BurpHotpatchModel;
import burp_hotpatch.mvc.AbstractModel;
import burp_hotpatch.mvc.AbstractView;
import burp_hotpatch.mvc.MVC;
import burp_hotpatch.ui.BurpHotpatchTab;
import burp_hotpatch.util.Logger;
import burp_hotpatch.util.MontoyaUtil;
import burp_hotpatch.view.BurpHotpatchView;

public class BurpHotpatch implements BurpExtension, ExtensionUnloadingHandler {
    public static final String EXTENSION_NAME = "Burp Hotpatch";
    private MontoyaApi montoyaApi;
    private MVC<BurpHotpatchModel, BurpHotpatchView, BurpHotpatchController> burpHotpatch;
    private MontoyaConfig config;

    public BurpHotpatch() {

    }

    private AbstractModel<?>[] getModels() {
        return new AbstractModel[] {
                burpHotpatch.getModel()
        };
    }

    private AbstractView<?, ?, ?>[] getViews() {
        return new AbstractView[] {
                burpHotpatch.getView()
        };
    }

    public void buildMVCs() {
        BurpHotpatchModel burpHotpatchModel = new BurpHotpatchModel();
        this.burpHotpatch = new MVC<>(
                burpHotpatchModel,
                new BurpHotpatchView(burpHotpatchModel),
                new BurpHotpatchController(burpHotpatchModel)
        );

    }

    public BurpHotpatchTab buildTab() {
        BurpHotpatchTab tab = new BurpHotpatchTab(
                burpHotpatch.getView()
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
        BurpHotpatchTab tab = buildTab();
        this.config = new MontoyaConfig(api.persistence());
        for (AbstractModel<?> model : getModels()) {
            model.load(config);
        }
        montoyaUtil.getApi().userInterface().registerSuiteTab(EXTENSION_NAME, tab);
        montoyaUtil.getApi().extension().registerUnloadingHandler(this);
        montoyaUtil.getApi().http().registerHttpHandler(burpHotpatch.getController());
        montoyaUtil.getApi().http().registerSessionHandlingAction(burpHotpatch.getController());
        montoyaUtil.getApi().proxy().registerRequestHandler(burpHotpatch.getController());
        montoyaUtil.getApi().intruder().registerPayloadProcessor(burpHotpatch.getController());
        Logger.log("INFO", String.format("Burp %s %s loaded", EXTENSION_NAME, VERSION.getVersionStr()));
    }


    @Override
    public void extensionUnloaded() {
        for (AbstractModel<?> model : getModels()) {
            model.save(config);
        }
    }
}