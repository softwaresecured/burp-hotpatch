import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraalVMTests {
    public GraalVMTests() {
        System.setProperty("polyglot.engine.WarnInterpreterOnly","false");
    }

    @Test
    @DisplayName("Test Jython local access")
    public void testJythonLocalAccess() {
        TestA test = new TestA(1234);
        Context cx = Context.newBuilder("python")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(className -> true)
                .build();

        cx.getBindings("python").putMember("test",test);
        cx.eval("python", "result = test.getVal()");
        String result = String.valueOf(cx.getBindings("python").getMember("result"));
        assertEquals("1234", result);
    }

    @Test
    @DisplayName("Test JavaScript local access")
    public void testJavaScriptLocalAccess() {
        TestA test = new TestA(1234);
        Context cx = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(className -> true)
                .build();
        cx.getBindings("js").putMember("test",test);
        cx.eval("js", "result = test.getVal()");
        String result = String.valueOf(cx.getBindings("js").getMember("result"));
        assertEquals("1234", result);
    }

    @Test
    @DisplayName("Test Python output")
    public void testPythonOutput() {
        String testPython = "print('test')";
        ByteArrayOutputStream errBuff = new ByteArrayOutputStream();
        ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
        PrintStream stderr = new PrintStream(errBuff);
        PrintStream stdout = new PrintStream(outBuff);
        Context cx = Context.newBuilder("python")
                .allowHostAccess(HostAccess.ALL)
                .out(stdout)
                .err(stderr)
                .allowHostClassLookup(className -> true)
                .build();

        cx.eval("python", testPython);
        System.out.println(outBuff.toString());
        assertEquals("test",outBuff.toString().strip());
    }

    @Test
    @DisplayName("Test JavaScript output")
    public void testJavaScriptOutput() {
        String testPython = "console.log('test')";
        ByteArrayOutputStream errBuff = new ByteArrayOutputStream();
        ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
        PrintStream stderr = new PrintStream(errBuff);
        PrintStream stdout = new PrintStream(outBuff);
        Context cx = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .out(stdout)
                .err(stderr)
                .allowHostClassLookup(className -> true)
                .build();

        cx.eval("js", testPython);
        System.out.println(outBuff.toString());
        assertEquals("test",outBuff.toString().strip());
    }
}
