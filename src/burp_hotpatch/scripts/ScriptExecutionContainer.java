package burp_hotpatch.scripts;

import burp_hotpatch.enums.ScriptLanguage;
import burp_hotpatch.enums.ScriptTypes;
import burp_hotpatch.model.BurpHotpatchModel;
import burp_hotpatch.util.ExceptionUtil;
import burp_hotpatch.util.Logger;
import burp_hotpatch.util.MontoyaUtil;
import burp_hotpatch.util.ResourceLoader;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ScriptExecutionContainer {
    private String id = UUID.randomUUID().toString();
    private HotpatchScript hotpatchScript;
    private Object argument;
    private BurpHotpatchModel burpHotpatchModel;
    private Context cx = null;

    public ScriptExecutionContainer(BurpHotpatchModel burpHotpatchModel, HotpatchScript hotpatchScript, Object argument ) {
        this.burpHotpatchModel = burpHotpatchModel;
        this.hotpatchScript = hotpatchScript;
        this.argument = argument;

    }

    public Object execute() throws ScriptExecutionException {
        Logger.log(
                "INFO",
                String.format(
                        "Running script %s ( %s ) for handler %s",
                        hotpatchScript.getName(),
                        hotpatchScript.getScriptLanguage().name(),
                        hotpatchScript.getScriptType().toString()
                )
        );
        String languageId = getScriptRuntimeName(hotpatchScript.getScriptLanguage());
        String argumentName = getScriptArgumentName(hotpatchScript.getScriptType());

        if ( languageId == null ) {
            throw new ScriptExecutionException("Could not match script type to a graals script type");
        }

        if ( argumentName == null && !hotpatchScript.getScriptType().equals(ScriptTypes.UTILITY)) {
            throw new ScriptExecutionException("Could not find argument name for script");
        }

        Object scriptResult = null;
        ByteArrayOutputStream errBuff = new ByteArrayOutputStream();
        ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
        PrintStream stderr = new PrintStream(errBuff);
        PrintStream stdout = new PrintStream(outBuff);
        try {
            Map<String,String> options = new HashMap<>();
            if ( languageId.equals("python")) {
                options.put("python.EmulateJython","true");
            }
            Thread.currentThread().setContextClassLoader(Context.class.getClassLoader());
            cx = Context.newBuilder(languageId)
                    .out(stdout)
                    .err(stderr)
                    .options(options)
                    .allowAllAccess(true)
                    .allowHostClassLookup(className -> true)
                    .build();

            cx.getBindings(languageId).putMember("memory",ScriptSharedMemory.getInstance());
            cx.getBindings(languageId).putMember("montoyaApi", MontoyaUtil.getInstance().getApi());
            if ( argumentName != null ) {
                cx.getBindings(languageId).putMember(argumentName, argument);
            }
            String scriptContent = ResourceLoader.getInstance().getExecutionScript(
                    hotpatchScript.getScriptType(),
                    hotpatchScript.getScriptLanguage(),
                    hotpatchScript.getContent()
            );
            Logger.log("DEBUG", String.format("Script %s executing", getId()));
            cx.eval(languageId, scriptContent);
            if ( !hotpatchScript.getScriptType().equals(ScriptTypes.UTILITY) && !hotpatchScript.getScriptType().equals(ScriptTypes.CONTEXT_MENU_ACTION)) {
                scriptResult = cx.getBindings(languageId).getMember("_script_result").as(Object.class);
            }
            cx.close();
        } catch ( Exception e ) {
            Logger.log("ERROR",ExceptionUtil.stackTraceToString(e));
            burpHotpatchModel.setStderr(hotpatchScript.getId(), ExceptionUtil.stackTraceToString(e));
        }
        finally {
            if (!errBuff.toString().isEmpty()) {
                burpHotpatchModel.setStderr(hotpatchScript.getId(), errBuff.toString());
            }
            if (!outBuff.toString().isEmpty()) {
                burpHotpatchModel.setStdout(hotpatchScript.getId(), outBuff.toString());
            }
        }
        Logger.log("DEBUG", String.format("Script %s exiting", getId()));
        return scriptResult;
    }

    private String getScriptRuntimeName( ScriptLanguage language ) {
        switch ( language ) {
            case PYTHON:
                return "python";
            case JAVASCRIPT:
                return "js";
            default:
                return null;
        }
    }

    private String getScriptArgumentName(ScriptTypes scriptType) {
        switch ( scriptType ) {
            case HTTP_HANDLER_REQUEST_TO_BE_SENT:
                return "httpRequestToBeSent";
            case HTTP_HANDLER_RESPONSE_RECEIVED:
                return "httpResponseReceived";
            case PROXY_HANDLER_REQUEST_RECEIVED:
                return "interceptedRequest";
            case PROXY_HANDLER_REQUEST_TO_BE_SENT:
                return "interceptedRequest";
            case SESSION_HANDLING_ACTION:
                return "sessionHandlingActionData";
            case PAYLOAD_PROCESSOR:
                return "payloadData";
            case AUDIT_ISSUE_HANDLER:
                return "auditIssue";
            case CONTEXT_MENU_ACTION:
                return "requestResponses";
            default:
                return null;
        }
    }

    public HotpatchScript getScript() {
        return hotpatchScript;
    }

    public String getId() {
        return id;
    }

    public void terminate() {
        if ( cx != null ) {
            try {
                cx.interrupt(Duration.ZERO);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
