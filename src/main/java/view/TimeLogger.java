package view;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import model.PortStatus;
import view.logging.*;

import java.util.*;

import static java.lang.Thread.sleep;

public class TimeLogger {
    private static final long DELAY = 500;
    private static int currentTime = 0;
    private static TextArea textArea;
    static Queue<Log> logs = new LinkedList<>();
    private static int amountOfDenials = 0;
    private static int amountOfFaults = 0;
    private static int amountOfGenerations = 0;
    private static int amountOfEd = 0;
    private static int amountOfBusies = 0;
    private static ArrayList<Integer> times = new ArrayList<>();
    private static Button b1;
    private static Button b2;
    private static Button b3;
    private static Button b4;

    public static void log(String string, int time) {
        delay(time);
        logs.offer(new ControllerEvent(string, currentTime));
    }

    public static void delay(int delay) {
        currentTime += delay;
    }

    public static void logBroadcast(int lineNumber, int time) {
        delay(time);
        logs.offer(new BroadcastEvent(lineNumber, currentTime));
    }

    public static void logHandleMessage(int value, int lineNumber) {
        logs.offer(new MessageEvent(value, lineNumber, " handle.", currentTime));
    }

    public static void logSendMessage(int value, int lineNumber) {
        logs.offer(new MessageEvent(value, lineNumber, " send.", currentTime));
    }

    public static void logChangePortStatus(int EdNumber, int lineNumber, PortStatus status) {
        logs.offer(new ChangePortStatusEvent(EdNumber, lineNumber, status, currentTime));
    }

    static void setTextArea(TextArea textArea) {
        TimeLogger.textArea = textArea;
    }

    static void showLogs() {

        Thread t = new Thread(() -> {
            b1.setDisable(true);
            b2.setDisable(true);
            b3.setDisable(true);
            b4.setDisable(true);


            for (Log log : logs) {
                Platform.runLater(() -> {
                    Formatter formatter = new Formatter();
                    formatter.format("%-30s %s %d\n", log.getMessage(), "Time:", log.getTime());
                    textArea.insertText(textArea.getText().length(), formatter.toString());

                    if (log instanceof BroadcastEvent) {
                        ChangeColor.SetColor(((BroadcastEvent) log).getLineNumber());
                    }
                    if (log instanceof MessageEvent) {
                        ChangeColor.SetColor(
                                ((MessageEvent) log).getLineNumber(),
                                ((MessageEvent) log).getEdNumber());
                    }
                    if (log instanceof ChangePortStatusEvent) {
                        ChangeColor.SetColor(
                                ((ChangePortStatusEvent) log).getEdNumber(),
                                ((ChangePortStatusEvent) log).getLineNumber(),
                                ((ChangePortStatusEvent) log).getPortStatus());
                    }
                    if (log instanceof StartEvent) {
                        ChangeColor.decolor();
                    }

                    if (log instanceof GenerationEvent) {
                        ChangeColor.SetColorGeneration(
                                ((GenerationEvent) log).getLineNumber(),
                                ((GenerationEvent) log).isHasGeneration()
                        );
                    }
                });
                try {
                    sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            b1.setDisable(false);
            b2.setDisable(false);
            b3.setDisable(false);
            b4.setDisable(false);



            logs.clear();
        });
        t.start();
    }

    public static void logGeneration(int lineNumber, boolean hasGeneration, int numberOfGenerator) {
        logs.offer(new GenerationEvent(lineNumber, hasGeneration, numberOfGenerator, currentTime));
    }

    static void logStart(Integer generations, Integer faults, Integer denials, int busies) {
        times.add(currentTime);
        currentTime = 0;
        amountOfDenials += denials;
        amountOfFaults += faults;
        amountOfGenerations += generations;
        amountOfEd = MetaController.amountOfEd;
        amountOfBusies += busies;

        logs.offer(new StartEvent(currentTime));
        for (int i = 1; i <= amountOfEd; i++) {
            logChangePortStatus(i, 1, PortStatus.OK);
        }
    }

    static void setB1(Button b1) {
        TimeLogger.b1 = b1;
    }

    static void setB2(Button b2) {
        TimeLogger.b2 = b2;
    }

    static void setB3(Button b3) {
        TimeLogger.b3 = b3;
    }

    static void setB4(Button b4) {
        TimeLogger.b4 = b4;
    }

    static void endTest() {
        Platform.runLater(() -> {
            double sum = 0;
            for (Integer i : times) {
                sum += i;
            }
            Formatter formatter = new Formatter();
            formatter.format("%-30s\n", "Average time: " + sum / (amountOfEd * times.size()));
            textArea.insertText(textArea.getText().length(), formatter.toString());
            formatter = new Formatter();
            formatter.format("%-30s\n", "Tests: " + times.size() );
            textArea.insertText(textArea.getText().length(), formatter.toString());
            formatter = new Formatter();
            formatter.format("%-30s\n", "Generations: " + amountOfGenerations + " p=" + MetaController.generationProbability * MetaController.deviceProbability);
            textArea.insertText(textArea.getText().length(), formatter.toString());
            formatter = new Formatter();
            formatter.format("%-30s\n", "Faults: " + amountOfFaults + " p=" + MetaController.faultProbability * MetaController.deviceProbability);
            textArea.insertText(textArea.getText().length(), formatter.toString());
            formatter = new Formatter();
            formatter.format("%-30s\n", "Denials: " + amountOfDenials + " p=" + MetaController.denialProbability * MetaController.deviceProbability);
            textArea.insertText(textArea.getText().length(), formatter.toString());
            formatter = new Formatter();
            formatter.format("%-30s\n", "Busies: " + amountOfBusies + " p=" + MetaController.busyProbability);
            textArea.insertText(textArea.getText().length(), formatter.toString());
        });
    }
}