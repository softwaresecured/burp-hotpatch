package burp_hotpatch.controller;

import burp.api.montoya.http.handler.*;
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
import burp_hotpatch.scripts.StdoutLogger;
import burp_hotpatch.util.ResourceLoader;
import burp_hotpatch.enums.EditorState;
import burp_hotpatch.enums.OutputType;
import burp_hotpatch.enums.ScriptLanguage;
import burp_hotpatch.enums.ScriptTypes;
import burp_hotpatch.event.controller.BurpHotpatchControllerEvent;
import burp_hotpatch.model.BurpHotpatchModel;
import burp_hotpatch.mvc.AbstractController;
import burp_hotpatch.scripts.Script;
import burp_hotpatch.util.ExceptionUtil;
import burp_hotpatch.util.Logger;
import burp_hotpatch.util.MontoyaUtil;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.python.util.PythonInterpreter;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.beans.PropertyChangeEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import static burp_hotpatch.enums.ScriptTypes.*;

public class BurpHotpatchController extends AbstractController<BurpHotpatchControllerEvent, BurpHotpatchModel> implements SessionHandlingAction, HttpHandler, PayloadProcessor, ProxyRequestHandler {
    public BurpHotpatchController(BurpHotpatchModel model) {
        super(model);
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
                            new Script(
                                    getModel().getDeDuplicatedScriptName("Untitled"),
                                    ResourceLoader.getInstance().getEditorTemplate(
                                            ScriptTypes.UTILITY,
                                            ScriptLanguage.JYTHON
                                    ),
                                    ScriptTypes.UTILITY,
                                    ScriptLanguage.JYTHON
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
                executeAsRunnable(getModel().getCurrentScript());
                break;
            case CURRENT_SCRIPT_UPDATED:
                getModel().setCurrentScript((Script) next);
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
            case TABLE_VALUE_UPDATED:
                TableModelEvent evt = (TableModelEvent) next;
                if ( evt.getType() == TableModelEvent.UPDATE ) {
                    String id = (String) getModel().getScriptSelectionModel().getValueAt(evt.getFirstRow(), 0);
                    boolean isEnabled = (boolean) getModel().getScriptSelectionModel().getValueAt(evt.getFirstRow(), 2);
                    if ( getModel().getScriptById(id) != null ) {
                        getModel().getScriptById(id).setEnabled(isEnabled);
                    }
                }
                break;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        handleEvent(BurpHotpatchControllerEvent.valueOf(evt.getPropertyName()), evt.getOldValue(), evt.getNewValue());
    }

    public void executeAsRunnable(Script script ) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                executeScript(script, null);
            }
        });
        thread.start();
    }

    private Object executeScript(Script script, Object argument ) {
        Logger.log(
                "INFO",
                String.format(
                        "Running script %s ( %s ) for handler %s",
                        script.getName(),
                        script.getScriptLanguage().name(),
                        script.getScriptType().toString()
                )
        );
        if ( script.scriptLanguage.equals(ScriptLanguage.JYTHON)) {
            return executeJython(script, argument);
        }
        else if ( script.scriptLanguage.equals(ScriptLanguage.JAVASCRIPT)) {
            return executeJavaScript(script, argument);
        }
        return null;
    }

    private Object executeJavaScript(Script script, Object argument ) {
        Object scriptResult = null;

        try {
            StdoutLogger stdoutLogger = new StdoutLogger();
            Context context = Context.enter();
            Scriptable scope = context.initStandardObjects();
            ScriptableObject.putProperty(scope, "logger", Context.javaToJS(stdoutLogger, scope));
            ScriptableObject.putProperty(scope, "montoyaApi", Context.javaToJS(MontoyaUtil.getInstance().getApi(), scope));

            switch ( script.getScriptType() ) {
                case HTTP_HANDLER_REQUEST_TO_BE_SENT:
                    ScriptableObject.putProperty(scope, "httpRequestToBeSent", Context.javaToJS(argument, scope));
                    break;
                case HTTP_HANDLER_RESPONSE_RECEIVED:
                    ScriptableObject.putProperty(scope, "httpResponseReceived", Context.javaToJS(argument, scope));
                    break;
                case PROXY_HANDLER_REQUEST_RECEIVED:
                    ScriptableObject.putProperty(scope, "interceptedRequest", Context.javaToJS(argument, scope));
                    break;
                case PROXY_HANDLER_REQUEST_TO_BE_SENT:
                    ScriptableObject.putProperty(scope, "interceptedRequest", Context.javaToJS(argument, scope));
                    break;
                case SESSION_HANDLING_ACTION:
                    ScriptableObject.putProperty(scope, "sessionHandlingActionData", Context.javaToJS(argument, scope));
                    break;
                case PAYLOAD_PROCESSOR:
                    ScriptableObject.putProperty(scope, "payloadData", Context.javaToJS(argument, scope));
                    break;
            }

            context.evaluateString(
                    scope,
                    ResourceLoader.getInstance().getExecutionScript(
                    script.getScriptType(),
                    script.getScriptLanguage(),
                    script.getContent()
            ),
                    "<mem>",
                    1,
                    null
            );

            if ( stdoutLogger.getLog() != null ) {
                getModel().setStdout(script.getId(), stdoutLogger.getLog());
            }
            Object jsScriptResult = (Object) scope.get("_script_result",scope);
            scriptResult = (Object) Context.jsToJava(jsScriptResult, Object.class);


        } catch ( Exception e ) {
            getModel().setStderr(script.getId(),ExceptionUtil.stackTraceToString(e));
            Logger.log("ERROR", String.format("Error running script %s: %s", script.getName(),ExceptionUtil.stackTraceToString(e)));
        } finally {
            Context.exit();
        }

        return scriptResult;
    }



    private Object executeJython(Script script, Object argument ) {
        Object scriptResult = null;
        try {
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            PrintStream printStreamOutput = new PrintStream(stdout);

            ByteArrayOutputStream stderr = new ByteArrayOutputStream();
            PrintStream printStreamError = new PrintStream(stderr);

            PythonInterpreter pythonInterpreter = new PythonInterpreter();
            pythonInterpreter.setOut(printStreamOutput);
            pythonInterpreter.setErr(printStreamError);
            pythonInterpreter.set("montoyaApi", MontoyaUtil.getInstance().getApi());

            switch ( script.getScriptType() ) {
                case HTTP_HANDLER_REQUEST_TO_BE_SENT:
                    pythonInterpreter.set("httpRequestToBeSent", argument);
                    break;
                case HTTP_HANDLER_RESPONSE_RECEIVED:
                    pythonInterpreter.set("httpResponseReceived", argument);
                    break;
                case PROXY_HANDLER_REQUEST_RECEIVED:
                    pythonInterpreter.set("interceptedRequest", argument);
                    break;
                case PROXY_HANDLER_REQUEST_TO_BE_SENT:
                    pythonInterpreter.set("interceptedRequest", argument);
                    break;
                case SESSION_HANDLING_ACTION:
                    pythonInterpreter.set("sessionHandlingActionData", argument);
                    break;
                case PAYLOAD_PROCESSOR:
                    pythonInterpreter.set("payloadData", argument);
                    break;
            }

            pythonInterpreter.exec(
                    ResourceLoader.getInstance().getExecutionScript(
                            script.getScriptType(),
                            script.getScriptLanguage(),
                            script.getContent()
                    )
            );
            scriptResult = pythonInterpreter.get("_script_result", Object.class);

            String debugStr = pythonInterpreter.get("_debug_str", String.class);
            HttpRequest debugReq = (HttpRequest)pythonInterpreter.get("_debug_req", HttpRequest.class);

            if ( stdout.size() > 0 ) {
                getModel().setStdout(script.getId(),stdout.toString());
            }
            if ( stderr.size() > 0 ) {
                getModel().setStderr(script.getId(),stderr.toString());
            }
            printStreamOutput.close();
            printStreamError.close();
        }
        catch ( Exception e ) {
            getModel().setStderr(script.getId(),ExceptionUtil.stackTraceToString(e));
            Logger.log("ERROR", String.format("Error running script %s: %s", script.getName(),ExceptionUtil.stackTraceToString(e)));
        }
        return scriptResult;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        RequestToBeSentAction response = RequestToBeSentAction.continueWith(httpRequestToBeSent);
        for ( Script script : getModel().getScripts() ) {
            if ( script.getScriptType().equals(HTTP_HANDLER_REQUEST_TO_BE_SENT) && script.isEnabled()) {
                RequestToBeSentAction scriptResult = (RequestToBeSentAction) executeScript(script,httpRequestToBeSent);
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
        for ( Script script : getModel().getScripts() ) {
            if ( script.getScriptType().equals(HTTP_HANDLER_RESPONSE_RECEIVED) && script.isEnabled()) {
                ResponseReceivedAction scriptResult = (ResponseReceivedAction) executeScript(script, httpResponseReceived);
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
        for ( Script script : getModel().getScripts() ) {
            if ( script.getScriptType().equals(SESSION_HANDLING_ACTION) && script.isEnabled()) {
                ActionResult scriptResult = (ActionResult) executeScript(script, sessionHandlingActionData);
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
        for ( Script script : getModel().getScripts() ) {
            if ( script.getScriptType().equals(PAYLOAD_PROCESSOR) && script.isEnabled()) {
                PayloadProcessingResult resultFromScript = (PayloadProcessingResult) executeScript(script, payloadData);
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
        for ( Script script : getModel().getScripts() ) {
            if ( script.getScriptType().equals(HTTP_HANDLER_RESPONSE_RECEIVED) && script.isEnabled()) {
                ProxyRequestReceivedAction scriptResult = (ProxyRequestReceivedAction) executeScript(script, interceptedRequest);
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
        for ( Script script : getModel().getScripts() ) {
            if ( script.getScriptType().equals(PROXY_HANDLER_REQUEST_TO_BE_SENT) && script.isEnabled()) {
                ProxyRequestToBeSentAction scriptResult = (ProxyRequestToBeSentAction) executeScript(script, interceptedRequest);
                if ( scriptResult != null ) {
                    response = scriptResult;
                }
            }
        }
        return response;
    }
}
