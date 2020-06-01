package avs.concurrent;

import avs.gateway.truck.Truck;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class TruckQueue extends SwitchableBlockingArrayQueue<Truck> implements IHasDelay {

    private final LinkedList<Integer> delay = new LinkedList<>();
    private int firstTruckDelay;
    private int delaySum;
    private int itemsCount;

    public TruckQueue(int capacity) {
        super(capacity);
    }

    public TruckQueue(int capacity, boolean fair) {
        super(capacity, fair);
    }

    public TruckQueue(int capacity, boolean fair, Collection<? extends Truck> c) {
        super(capacity, fair, c);
    }

    private void pushDelay(Truck t) {
        delaySum += t.weight();
        delay.addLast(t.weight());
        ++this.itemsCount;
    }

    private void forwardGateway() {
        delaySum -= firstTruckDelay;
        firstTruckDelay = delay.get(0);
        delay.remove(0);
        --this.itemsCount;
    }

    @Override
    public int getDelayForId(int id) {
        for (int i = 0; i < this.count; ++i) {

            Object o = this.items[(this.takeIndex + i) % this.items.length];
            if (o == null) {
                break;
            }

            if (((Truck) o).id() == id)
                return delayAt(i);
        }
        return -1;
    }

    @Override
    public int getDelay() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            return delaySum;
        } finally {
            lock.unlock();
        }
    }

    @Override
    void overrideNonSafe(int i, Truck e) {
        final Object[] items = this.items;
        final int index = (i + this.takeIndex) % this.items.length;
        final Truck t = (Truck) items[index];

        if (t == null) {
            throw new IndexOutOfBoundsException(String.format("Overriding not existing item at index %d", i));
        }

        delaySum -= t.weight();
        delaySum += e.weight();
        delay.add(i, e.weight());
        items[index] = e;
    }


    @Override
    public void decrementDelay(int dec) throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            delaySum -= dec;
            firstTruckDelay -= dec;
            if (delaySum < 0) {
                delaySum = 0;
            }
            if (firstTruckDelay < 0) {
                firstTruckDelay = 0;
            }
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void put(Truck t) throws InterruptedException {
        checkNotNull(t);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length)
                notFull.await();
            enqueue(t);
            pushDelay(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Truck poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (count == 0) {
                return null;
            }
            Truck t = dequeue();
            forwardGateway();
            return t;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(Truck e) {
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (count == items.length)
                return false;
            else {
                enqueue(e);
                pushDelay(e);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Truck take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0)
                notEmpty.await();
            forwardGateway();
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    public int delayAt(int index) {
        int delaySum = 0;

        for (Integer del : delay) {
            delaySum += del;
            --index;
            if (index < 0)
                break;
        }

        delaySum += firstTruckDelay;

        return delaySum;
    }


    public void print() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();

        final Object[] items = this.items;

        System.out.print(String.format("WC <= d: %d <=", firstTruckDelay));

        for (Truck t : this) {
            System.out.print(String.format(" [ ID %d W: %d D: %b ] <=", t.id(), t.weight(), t.doc().isValid()));
        }

        System.out.println(" DC C: " + this.itemsCount);

        lock.unlock();
    }
}
