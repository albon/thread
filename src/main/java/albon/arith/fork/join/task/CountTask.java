package albon.arith.fork.join.task;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Created by albon on 16/12/11.
 */
public class CountTask extends RecursiveTask<Integer> {

    private static final int THRESHOLD = 2;
    private int start;
    private int end;

    public CountTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * The main computation performed by this task.
     *
     * @return the result of the computation
     */
    @Override
    protected Integer compute() {
        int sum = 0;

        if (end - start <= THRESHOLD) {
            for (int i = start; i <= end; ++i) {
                sum += i;
            }
        } else {
            int middle = (start + end) / 2;
            CountTask leftTask = new CountTask(start, middle);
            CountTask rightTask = new CountTask(middle + 1, end);

            leftTask.fork();
            rightTask.fork();

            Integer left = leftTask.join();
            Integer right = rightTask.join();

            sum = left + right;
        }

        return sum;
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask countTask = new CountTask(1, 10);

        ForkJoinTask<Integer> forkJoinTask = forkJoinPool.submit(countTask);
        try {
            System.out.println(forkJoinTask.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
