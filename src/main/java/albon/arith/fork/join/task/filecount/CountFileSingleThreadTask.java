package albon.arith.fork.join.task.filecount;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by albon on 16/12/12.
 */
public class CountFileSingleThreadTask {

    public int run(Path dir) {
        int count = 0;

        // 读取目录 dir 的子路径。
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
            for (Path subPath : ds) {
                if (Files.isDirectory(subPath, LinkOption.NOFOLLOW_LINKS)) {
                    // 对每个子目录都新建一个子任务。
                    count += run(subPath);
                } else {
                    // 遇到文件，则计数器增加 1。
                    count++;
                }
            }
        } catch (IOException ex) {
            return 0;
        }
        return count;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int run = new CountFileSingleThreadTask().run(Paths.get(Constant.DIR_PATH));
        System.out.println("count: " + run);
        System.out.println("time: " + (System.currentTimeMillis() - start));
    }

}
