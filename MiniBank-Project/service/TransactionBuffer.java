package service;

import model.Command;

/** Practical 10: a small shared array buffer; wait() / notifyAll() coordinate producer and consumer. */
public class TransactionBuffer {
    private final Command[] items;
    private int count = 0;
    private int putIndex = 0;
    private int takeIndex = 0;

    public TransactionBuffer(int capacity) {
        this.items = new Command[capacity];
    }

    public synchronized void put(Command command) throws InterruptedException {
        while (count == items.length) {
            wait(); // buffer full: wait for the consumer
        }
        items[putIndex] = command;
        putIndex = (putIndex + 1) % items.length;
        count++;
        notifyAll(); // wake the consumer
    }

    public synchronized Command take() throws InterruptedException {
        while (count == 0) {
            wait(); // buffer empty: wait for the producer
        }
        Command command = items[takeIndex];
        items[takeIndex] = null;
        takeIndex = (takeIndex + 1) % items.length;
        count--;
        notifyAll(); // wake the producer
        return command;
    }
}
