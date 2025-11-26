package edu.univ.erp.ui.common;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseBackupUtil {

    public static Result backupDatabase(String pgDumpPath,
                                        String host,
                                        int port,
                                        String database,
                                        String user,
                                        String password,
                                        String outFilePath,
                                        int timeoutSec) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        cmd.add(pgDumpPath);
        cmd.add("-h");
        cmd.add(host);
        cmd.add("-p");
        cmd.add(String.valueOf(port));
        cmd.add("-U");
        cmd.add(user);
        cmd.add("-F");
        cmd.add("c");             // custom format
        cmd.add("-b");            // include large objects
        cmd.add("-v");            // verbose
        cmd.add("-f");
        cmd.add(outFilePath);
        cmd.add(database);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Map<String, String> env = pb.environment();
        env.put("PGPASSWORD", password);

        pb.redirectErrorStream(true);
        Process p = pb.start();

        String output = readStream(p.getInputStream());

        boolean finished;
        if (timeoutSec > 0) {
            finished = p.waitFor(timeoutSec, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                return new Result(false, output, "Process timed out and was killed");
            }
        } else {
            p.waitFor();
        }

        int rc = p.exitValue();
        if (rc == 0) {
            return new Result(true, output, "");
        } else {
            return new Result(false, output, "pg_dump exited with code " + rc);
        }
    }

    public static Result restoreDatabase(String pgRestorePath,
                                         String host,
                                         int port,
                                         String database,
                                         String user,
                                         String password,
                                         String dumpFilePath,
                                         int timeoutSec) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        cmd.add(pgRestorePath);
        cmd.add("-h");
        cmd.add(host);
        cmd.add("-p");
        cmd.add(String.valueOf(port));
        cmd.add("-U");
        cmd.add(user);
        cmd.add("-d");
        cmd.add(database);
        cmd.add("--clean");    // drop database objects before recreating
        cmd.add("--no-owner");
        cmd.add("-v");         // verbose
        cmd.add(dumpFilePath);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Map<String, String> env = pb.environment();
        env.put("PGPASSWORD", password);

        pb.redirectErrorStream(true);
        Process p = pb.start();

        String output = readStream(p.getInputStream());

        boolean finished;
        if (timeoutSec > 0) {
            finished = p.waitFor(timeoutSec, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                return new Result(false, output, "Process timed out and was killed");
            }
        } else {
            p.waitFor();
        }

        int rc = p.exitValue();
        if (rc == 0) {
            return new Result(true, output, "");
        } else {
            return new Result(false, output, "pg_restore exited with code " + rc);
        }
    }

    private static String readStream(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            return sb.toString();
        }
    }

    public static class Result {
        private final boolean success;
        private final String stdout;
        private final String stderr;

        public Result(boolean success, String stdout, String stderr) {
            this.success = success;
            this.stdout = stdout == null ? "" : stdout;
            this.stderr = stderr == null ? "" : stderr;
        }

        public boolean isSuccess() { return success; }
        public String getStdout() { return stdout; }
        public String getStderr() { return stderr; }
    }
}