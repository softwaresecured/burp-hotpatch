package burp_hotpatch.controller;

import burp.VERSION;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.sessions.ActionResult;
import burp.api.montoya.http.sessions.SessionHandlingAction;
import burp.api.montoya.http.sessions.SessionHandlingActionData;
import burp.api.montoya.intruder.PayloadData;
import burp.api.montoya.intruder.PayloadProcessingResult;
import burp.api.montoya.intruder.PayloadProcessor;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import burp.api.montoya.scanner.audit.AuditIssueHandler;
import burp.api.montoya.scanner.audit.issues.AuditIssue;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp_hotpatch.constants.Constants;
import burp_hotpatch.scripts.HotpatchScript;
import burp_hotpatch.scripts.ScriptExecutionContainer;
import burp_hotpatch.scripts.ScriptExecutionException;
import burp_hotpatch.threads.ScriptExecutionMonitorThread;
import burp_hotpatch.threads.ScriptExecutionThread;
import burp_hotpatch.util.ResourceLoader;
import burp_hotpatch.enums.EditorState;
import burp_hotpatch.enums.OutputType;
import burp_hotpatch.enums.ScriptLanguage;
import burp_hotpatch.enums.ScriptTypes;
import burp_hotpatch.event.controller.BurpHotpatchControllerEvent;
import burp_hotpatch.model.BurpHotpatchModel;
import burp_hotpatch.mvc.AbstractController;
import burp_hotpatch.util.ExceptionUtil;
import burp_hotpatch.util.Logger;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static burp_hotpatch.enums.ScriptTypes.*;

public class BurpHotpatchController extends AbstractController<BurpHotpatchControllerEvent, BurpHotpatchModel> implements ContextMenuItemsProvider, AuditIssueHandler, SessionHandlingAction, HttpHandler, PayloadProcessor, ProxyRequestHandler {
    private ScriptExecutionMonitorThread scriptExecutionMonitor = null;
    private Thread montiorThread = null;
    public BurpHotpatchController(BurpHotpatchModel model) {
        super(model);
    }

    public void startScriptExecutionMonitorThread() {
        scriptExecutionMonitor = new ScriptExecutionMonitorThread(getModel());
        montiorThread = new Thread(scriptExecutionMonitor);
        montiorThread.start();
        Logger.log("INFO", "Started script execution monitor thread");
    }

    public void stopScriptExecutionMonitorThread() {
        if ( montiorThread != null ) {
            try {
                scriptExecutionMonitor.shutdown();
                montiorThread.join();
            } catch (InterruptedException e) {
                Logger.log("ERROR", String.format("Could not stop script execution monitor thread: %s", e.getMessage()));
            }
            Logger.log("INFO", "Script execution monitor stopped");
        }
    }

    @Override
    protected void handleEvent(BurpHotpatchControllerEvent event, Object previous, Object next) {
        switch ( event ) {
            case SCRIPT_SELECTION_UPDATED:
                if ((int) next >= 0) {
                    getModel().loadScriptById((String) getModel().getScriptSelectionModel().getValueAt((int) next, 0));
                }
                break;
            case NEW:
                try {
                    getModel().setCurrentScript(
                            new HotpatchScript(
                                    getModel().getDeDuplicatedScriptName("Untitled"),
                                    ResourceLoader.getInstance().getEditorTemplate(
                                            ScriptTypes.UTILITY,
                                            ScriptLanguage.PYTHON
                                    ),
                                    ScriptTypes.UTILITY,
                                    ScriptLanguage.PYTHON
                            )
                    );
                    getModel().setEditorState(EditorState.CREATE);
                }
                catch ( Exception e ) {
                    Logger.log("DEBUG", String.format("Exception: %s", e.getMessage()));
                }
                break;
            case SAVE:
                getModel().saveScript(getModel().getCurrentScript());
                getModel().setEditorState(EditorState.EDIT);
                break;
            case DELETE:
                getModel().deleteScript(getModel().getCurrentScript().getId());
                break;
            case CANCEL:
                if ( getModel().getLastSelectedScriptId() != null ) {
                    getModel().loadScriptById(getModel().getLastSelectedScriptId());
                }
                else {
                    getModel().setEditorState(EditorState.INITIAL);
                }
                break;
            case IMPORT:
                JFileChooser importDialog = new JFileChooser();
                importDialog.setSelectedFile(new File("export.json"));
                if (importDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = importDialog.getSelectedFile();
                        byte[] content = Files.readAllBytes(file.toPath());
                        if ( content != null ) {
                            getModel().importScriptsFromJSON(new String(content));
                        }

                    } catch (IOException e) {
                        Logger.log("ERROR", String.format("Error importing scripts: %s", ExceptionUtil.stackTraceToString(e)));
                    } catch (Exception e) {
                    Logger.log("ERROR", String.format("Error importing scripts: %s", ExceptionUtil.stackTraceToString(e)));
                    }

                }
                break;
            case EXPORT:
                JFileChooser exportDialog = new JFileChooser();
                exportDialog.setSelectedFile(new File("export.json"));
                if (exportDialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = exportDialog.getSelectedFile();
                    try {
                        Files.writeString(file.getAbsoluteFile().toPath(), getModel().exportScriptsAsJSON());
                    } catch (IOException e) {
                        Logger.log("ERROR", String.format("Exception while saving: %s", e.getMessage()));
                    }
                }
                break;
            case NAME_UPDATED:
                if ( getModel().getCurrentScript() != null ) {
                    getModel().getCurrentScript().setName((String)next);
                }
                break;
            case SCRIPT_TYPE_UPDATED:
                if ( getModel().getCurrentScript() != null ) {
                    getModel().getCurrentScript().setScriptType(ScriptTypes.fromFriendlyName((String)next));
                    getModel().setScriptTemplateModified();
                }
                break;
            case SCRIPT_LANGUAGE_UPDATED:
                if ( getModel().getCurrentScript() != null ) {
                    getModel().getCurrentScript().setScriptLanguage(ScriptLanguage.fromFriendlyName((String)next));
                    getModel().setScriptTemplateModified();
                }
                break;
            case CURRENT_SCRIPT_ENABLE_TOGGLE:
                if ( getModel().getCurrentScript() != null ) {
                    getModel().getCurrentScript().setEnabled((boolean) next);
                }
                break;
            case TOGGLE_SCRIPT_EXECUTION:
                executeAsRunnable(getModel().getCurrentScript(),null);
                break;
            case CURRENT_SCRIPT_UPDATED:
                getModel().setCurrentScript((HotpatchScript) next);
                break;
            case CURRENT_SCRIPT_CONTENT_UPDATED:
                if ( getModel().getCurrentScript() != null ) {
                    getModel().getCurrentScript().setContent((String)next);
                }
                break;
            case OUTPUT_TYPE_STDOUT_SELECTED:
                getModel().setSelectedOutputType(OutputType.STDOUT);
                break;
            case OUTPUT_TYPE_STDERR_SELECTED:
                getModel().setSelectedOutputType(OutputType.STDERR);
                break;
            case CLEAR_OUTPUT:
                if ( getModel().getCurrentScript() != null ) {
                    if ( getModel().getSelectedOutputType().equals(OutputType.STDOUT)) {
                        getModel().clearStdout(getModel().getCurrentScript().getId());
                    }
                    else {
                        getModel().clearStderr(getModel().getCurrentScript().getId());
                    }
                }
                break;
            case EXECUTION_ORDER_UPDATED:
                if ( getModel().getCurrentScript() != null ) {
                    getModel().getCurrentScript().setExecutionOrder(((Integer)next));
                }
                break;
            case DISMISS_UPDATE:
                getModel().setUpdateAvailableMessage(null);
                break;
            case TASK_SELECTION_UPDATED:
                if ((int)next >= 0 ) {
                    getModel().setCurrentTaskId((String) getModel().getRunningTasksModel().getValueAt((int)next,0));
                }
                else {
                    getModel().setCurrentTaskId(null);
                }
                break;
            case TERMINATE_TASK:
                if ( getModel().getCurrentTaskId() != null ) {
                    getModel().terminateCurrentTask();
                }
                break;
            case SCRIPTS_TABLE_ENABLED_TOGGLED_CHANGED:
                getModel().toggleCurrentScript((String)next);
                break;
            default:
                break;

        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        handleEvent(BurpHotpatchControllerEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
    }

    public void executeAsRunnable(HotpatchScript hotpatchScript, Object argument) {
        ScriptExecutionThread scriptExecutionThread = new ScriptExecutionThread(new ScriptExecutionContainer(getModel(), hotpatchScript,argument));
        scriptExecutionThread.start();
        getModel().addRunningTask(scriptExecutionThread.getScriptExecutionContainer());
        getModel().addThread(scriptExecutionThread);
    }

    private Object executeScript(HotpatchScript hotpatchScript, Object argument ) {
        Object result = null;
        ScriptExecutionContainer scriptExecutionContainer = new ScriptExecutionContainer(getModel(), hotpatchScript,argument);
        try {
            result = scriptExecutionContainer.execute();
        } catch (ScriptExecutionException e) {
            Logger.log("ERROR", String.format("Failed to execute script: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        RequestToBeSentAction response = RequestToBeSentAction.continueWith(httpRequestToBeSent);
        // Skip scripts on the update URL
        if ( httpRequestToBeSent.url().equals(VERSION.RELEASE_TAGS_URL)) {
            return response;
        }
        for ( HotpatchScript hotpatchScript : getModel().getScripts() ) {
            if ( hotpatchScript.getScriptType().equals(HTTP_HANDLER_REQUEST_TO_BE_SENT) && hotpatchScript.isEnabled()) {
                RequestToBeSentAction scriptResult = (RequestToBeSentAction) executeScript(hotpatchScript,response.request());
                if ( scriptResult != null ) {
                    response = scriptResult;
                }
            }
        }
        return response;
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        ResponseReceivedAction response = ResponseReceivedAction.continueWith(httpResponseReceived);
        for ( HotpatchScript hotpatchScript : getModel().getScripts() ) {
            if ( hotpatchScript.getScriptType().equals(HTTP_HANDLER_RESPONSE_RECEIVED) && hotpatchScript.isEnabled()) {
                ResponseReceivedAction scriptResult = (ResponseReceivedAction) executeScript(hotpatchScript, response.response());
                if ( scriptResult != null ) {
                    response = scriptResult;
                }
            }
        }
        return response;
    }

    @Override
    public String name() {
        return "Burp Hotpatch";
    }

    @Override
    public ActionResult performAction(SessionHandlingActionData sessionHandlingActionData) {
        ActionResult actionResult = ActionResult.actionResult(sessionHandlingActionData.request());
        for ( HotpatchScript hotpatchScript : getModel().getScripts() ) {
            if ( hotpatchScript.getScriptType().equals(SESSION_HANDLING_ACTION) && hotpatchScript.isEnabled()) {
                ActionResult scriptResult = (ActionResult) executeScript(hotpatchScript, sessionHandlingActionData);
                if ( scriptResult != null ) {
                    actionResult = scriptResult;
                }
            }
        }
        return actionResult;
    }

    @Override
    public String displayName() {
        return "Burp Hotpatch";
    }

    @Override
    public PayloadProcessingResult processPayload(PayloadData payloadData) {
        PayloadProcessingResult result = PayloadProcessingResult.skipPayload();
        for ( HotpatchScript hotpatchScript : getModel().getScripts() ) {
            if ( hotpatchScript.getScriptType().equals(PAYLOAD_PROCESSOR) && hotpatchScript.isEnabled()) {
                PayloadProcessingResult resultFromScript = (PayloadProcessingResult) executeScript(hotpatchScript, payloadData);
                if ( resultFromScript != null ) {
                    result = resultFromScript;
                }
            }
        }
        return result;
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        ProxyRequestReceivedAction response = ProxyRequestReceivedAction.continueWith(interceptedRequest);
        for ( HotpatchScript hotpatchScript : getModel().getScripts() ) {
            if ( hotpatchScript.getScriptType().equals(PROXY_HANDLER_REQUEST_RECEIVED) && hotpatchScript.isEnabled()) {
                ProxyRequestReceivedAction scriptResult = (ProxyRequestReceivedAction) executeScript(hotpatchScript, response.request());
                if ( scriptResult != null ) {
                    response = scriptResult;
                }
            }
        }
        return response;
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        ProxyRequestToBeSentAction response = ProxyRequestToBeSentAction.continueWith(interceptedRequest);
        for ( HotpatchScript hotpatchScript : getModel().getScripts() ) {
            if ( hotpatchScript.getScriptType().equals(PROXY_HANDLER_REQUEST_TO_BE_SENT) && hotpatchScript.isEnabled()) {
                ProxyRequestToBeSentAction scriptResult = (ProxyRequestToBeSentAction) executeScript(hotpatchScript, response.request());
                if ( scriptResult != null ) {
                    response = scriptResult;
                }
            }
        }
        return response;
    }

    @Override
    public void handleNewAuditIssue(AuditIssue auditIssue) {
        /*
            This is never called :(
            https://github.com/PortSwigger/burp-extensions-montoya-api/issues/9
         */
        for ( HotpatchScript hotpatchScript : getModel().getScripts() ) {
            if ( hotpatchScript.getScriptType().equals(AUDIT_ISSUE_HANDLER) && hotpatchScript.isEnabled()) {
                executeScript(hotpatchScript, auditIssue);
            }
        }
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event)
    {
        List<Component> menuItemList = new ArrayList<>();
        for ( HotpatchScript hotpatchScript : getModel().getScripts()) {
            if ( !hotpatchScript.isEnabled() || !hotpatchScript.getScriptType().equals(CONTEXT_MENU_ACTION)) {
                continue;
            }
            if (!event.selectedRequestResponses().isEmpty() || event.messageEditorRequestResponse().isPresent()) {
                ArrayList<HttpRequestResponse> requestResponses = getRequests(event);
                if (!requestResponses.isEmpty()) {
                    JMenuItem mnuScriptContextAction = new JMenuItem(hotpatchScript.getName());
                    mnuScriptContextAction.addActionListener(actionEvent -> {
                        executeAsRunnable(hotpatchScript,requestResponses);
                    });
                    menuItemList.add(mnuScriptContextAction);
                }

            }
        }
        return menuItemList;
    }

    private ArrayList<HttpRequestResponse> getRequests(ContextMenuEvent event) {
        ArrayList<HttpRequestResponse> requestResponses = new ArrayList<HttpRequestResponse>();
        if ( event.messageEditorRequestResponse().isPresent() ) {
            requestResponses.add(event.messageEditorRequestResponse().get().requestResponse());
        }
        else {
            requestResponses.addAll(event.selectedRequestResponses());
        }
        return requestResponses;
    }
}
