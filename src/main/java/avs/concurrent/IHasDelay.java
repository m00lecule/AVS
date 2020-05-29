package avs.concurrent;

public interface IHasDelay {
    int getDelay() throws InterruptedException;
    void decrementDelay(int dec) throws InterruptedException;
}
