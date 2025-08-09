package net.thevpc.ntexup.cmdline;

import net.thevpc.nuts.io.NPath;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Options {
    public List<NPath> paths = new ArrayList<>();
    public Action action = Action.OPEN;
    public OutputFormat outputFormat = OutputFormat.PDF;
    public NPath output;
    public boolean documentation;
    public boolean dump;
    public String templateUrl;
    public boolean viewer;
    public boolean console;
    public boolean showLogs;
    public boolean reopen;
    public Map<String,String> vars=new LinkedHashMap<>();

    public Options requireViewer() {
        this.viewer = true;
        return this;
    }
}
