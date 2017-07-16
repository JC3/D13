package d13.web.servlets;

public final class ReportParameters {
    
    public static enum Mode { NEW, PRETTY, PRINT, CSV };
    
    private long id;
    private Mode mode;
    
    public ReportParameters () { this.id = -1; this.mode = Mode.NEW; }
    public ReportParameters (long id, Mode mode) { this.id = id; this.mode = mode; }
    
    public void setId (long id) { this.id = id; }
    public void setMode (Mode mode) { this.mode = mode; }
    
    public long getId () { return id; }
    public boolean isNew () { return mode == Mode.NEW; }
    public boolean isPretty () { return mode == Mode.PRETTY; }
    public boolean isPrint () { return mode == Mode.PRINT; }
    public boolean isCsv () { return mode == Mode.CSV; }
    
}