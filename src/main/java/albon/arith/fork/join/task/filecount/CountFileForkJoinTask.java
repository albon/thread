package albon.arith.fork.join.task.filecount;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Created by albon on 16/12/11.
 */
public class CountFileForkJoinTask extends RecursiveTask<Integer> {
    private Path dir;

    public CountFileForkJoinTask(Path dir) {
        this.dir = dir;
    }

    /**
     * The main computation performed by this task.
     *
     * @return the result of the computation
     */
    @Override
    protected Integer compute() {
        int count = 0;
        List<CountFileForkJoinTask> subTasks = new ArrayList<>();

        // 读取目录 dir 的子路径。
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
            for (Path subPath : ds) {
                if (Files.isDirectory(subPath, LinkOption.NOFOLLOW_LINKS)) {
                    // 对每个子目录都新建一个子任务。
                    subTasks.add(new CountFileForkJoinTask(subPath));
                } else {
                    // 遇到文件，则计数器增加 1。
                    count++;
                }
            }

            if (!subTasks.isEmpty()) {
                // 在当前的 ForkJoinPool 上调度所有的子任务。
                for (CountFileForkJoinTask subTask : invokeAll(subTasks)) {
                    count += subTask.join();
                }
            }
        } catch (IOException ex) {
            return 0;
        }
        return count;
    }

    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        long start = System.currentTimeMillis();

        CountFileForkJoinTask countTask = new CountFileForkJoinTask(Paths.get(Constant.DIR_PATH));

        ForkJoinTask<Integer> forkJoinTask = forkJoinPool.submit(countTask);
        try {
            System.out.println(forkJoinTask.get());

            System.out.println("time: " + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
