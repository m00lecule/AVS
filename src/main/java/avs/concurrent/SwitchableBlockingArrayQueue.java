package avs.concurrent;


import java.util.Collection;

/**
 * Enhanced implementation of concurrent queue, that supports operation of
 * switching entries between two queues. All features of ArrayBlockingQueue
 * are preserved.
 * <p>
 * In case of adding more features, please be aware of synchronizing all
 * operations.
 * <p>
 * All operations marked as NonSafe should be called in critical section (after
 * acquiring lock)
 *
 * @param <E> the type of elements held in this collection
 * @author Michał Dygas
 */
public class SwitchableBlockingArrayQueue<E> extends ArrayBlockingQueue<E> {

    public SwitchableBlockingArrayQueue(int capacity) {
        super(capacity);
    }

    public SwitchableBlockingArrayQueue(int capacity, boolean fair) {
        super(capacity, fair);
    }

    public SwitchableBlockingArrayQueue(int capacity, boolean fair, Collection<? extends E> c) {
        super(capacity, fair, c);
    }

    public void pause() throws InterruptedException {
        this.lock.lock();
    }

    public void resume() {
        this.lock.unlock();
    }

    private E getNonSafe(int i) {
        final Object[] items = this.items;
        final int index = (i + this.takeIndex) % this.items.length;

        @SuppressWarnings("unchecked")
        E x = (E) items[index];
        return x;
    }

    void overrideNonSafe(int i, E e) {
        final Object[] items = this.items;
        final int index = (i + this.takeIndex) % this.items.length;

        if (items[index] == null) {
            throw new IndexOutOfBoundsException(String.format("Overriding not existing item at index %d", i));
        }

        items[index] = e;
    }

    public class Switch2 {
        private final SwitchableBlockingArrayQueue<E> firstQueue;
        private final SwitchableBlockingArrayQueue<E> secondQueue;

        public Switch2(SwitchableBlockingArrayQueue<E> q1, SwitchableBlockingArrayQueue<E> q2) {
            this.firstQueue = q1;
            this.secondQueue = q2;
        }

        public void replaceAt(int index) throws InterruptedException {
            if (index == 0)
                return;

            firstQueue.pause();
            secondQueue.pause();

            final E item1 = firstQueue.getNonSafe(index);
            final E item2 = secondQueue.getNonSafe(index);

            System.out.println(item1 + " " + item2);

            if (item1 != null && item2 != null) {
                firstQueue.overrideNonSafe(index, item2);
                secondQueue.overrideNonSafe(index, item1);
            }

            firstQueue.resume();
            secondQueue.resume();
        }
    }

}