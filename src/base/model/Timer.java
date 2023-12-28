package base.model;

public class Timer {
    private long initialTime;
    private long pausedTime;
    private static Timer instance;

    private Timer() {

    }

    public static Timer getInstance() {
        return instance == null ? instance = new Timer() : instance;
    }

    public void start() {
        initialTime = System.currentTimeMillis();
        pausedTime = 0;
    }

    public void pauseTime() {
        pausedTime = System.currentTimeMillis() - initialTime;
    }

    public void continueTime() {
        initialTime = System.currentTimeMillis() - pausedTime;
        pausedTime = 0;
    }
    public String getTime() {
        long time = System.currentTimeMillis() - initialTime;
        int minutes = (int) (time / 60000);
        int seconds = (int) ((time % 60000) / 1000);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
