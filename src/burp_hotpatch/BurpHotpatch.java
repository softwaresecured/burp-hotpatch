package burp_hotpatch;

import burp.VERSION;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.http.RequestOptions;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BurpHotpatch implements BurpExtension, ExtensionUnloadingHandler {
    public static final String EXTENSION_NAME = "Burp Hotpatch";
    private MontoyaApi montoyaApi;
    private MVC<BurpHotpatchModel, BurpHotpatchView, BurpHotpatchController> burpHotpatch;
    private MontoyaConfig config;
    private Thread updateCheckerThread = null;

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
        this.montoyaApi = api;
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
        montoyaUtil.getApi().userInterface().registerContextMenuItemsProvider(burpHotpatch.getController());
        Logger.log("INFO", String.format("Burp %s %s loaded", EXTENSION_NAME, VERSION.getVersionStr()));

        UpdateChecker updateChecker = new UpdateChecker();
        updateCheckerThread = new Thread(updateChecker);
        updateCheckerThread.start();
    }


    @Override
    public void extensionUnloaded() {
        for (AbstractModel<?> model : getModels()) {
            model.save(config);
        }
        if ( updateCheckerThread != null ) {
            try {
                updateCheckerThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class UpdateChecker implements Runnable {
        private String getLatestVersion() throws IOException, URISyntaxException {
            String latestVersion = null;
            HttpRequestResponse httpRequestResponse = montoyaApi.http().sendRequest(HttpRequest.httpRequestFromUrl(VERSION.RELEASE_TAGS_URL), RequestOptions.requestOptions().withUpstreamTLSVerification());
            if ( httpRequestResponse.response().statusCode() == 200 ) {
                Pattern p = Pattern.compile("releases\\/tag\\/([^\"]+)\"");
                Matcher m = p.matcher(httpRequestResponse.response().bodyToString());
                if ( m.find() ) {
                    latestVersion = m.group(1);
                }
            }
            return latestVersion;
        }
        @Override
        public void run() {
            try {
                String latestVersion = getLatestVersion();
                if( latestVersion != null ) {
                    if ( !VERSION.getVersionStrPlain().equals(latestVersion)) {
                        burpHotpatch.getModel().setUpdateAvailableMessage(String.format("<html><center><a href=\"\">Hotpatch %s is available (Click to dismiss)</a></center></html>", latestVersion));
                        Logger.log("INFO", String.format("Update %s is available", latestVersion));
                    }
                }
                else {
                    Logger.log("ERROR", String.format("Error fetching updates - Could not fetch tags from %s", VERSION.RELEASE_TAGS_URL));
                }
            } catch (IOException | URISyntaxException e) {
                Logger.log("ERROR", String.format("Error fetching updates - Could not fetch tags from %s", VERSION.RELEASE_TAGS_URL));
            }
        }
    }
}