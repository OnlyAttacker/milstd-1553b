package view.Logging;


class BroadcastEvent implements Log {
    private final int lineNumber;
    private final int time;
    private final String message;

    BroadcastEvent(int lineNumber, int time) {
        this.lineNumber = lineNumber;
        this.time = time;
        this.message = "Line #" + lineNumber + " message broadcast.";
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getTime() {
        return time;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
