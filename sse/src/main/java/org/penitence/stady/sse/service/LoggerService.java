package org.penitence.stady.sse.service;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxSink;

import java.io.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

@Service
public class LoggerService {

    private Map<String, LoggerObject> processorCache = new ConcurrentHashMap<>();

    private final DockerClient dockerClient;

    private final StampedLock _lock = new StampedLock();


    public LoggerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public EmitterProcessor<String> findContainerProcessor(String ref) {
        if (!processorCache.containsKey(ref)) {
            long stamp = _lock.writeLock();
            try {
                if (!processorCache.containsKey(ref)) {
                    buildContainerStream(ref);
                }
            } catch (IOException | InterruptedException | DockerException e) {
                e.printStackTrace();
            } finally {
                _lock.unlockWrite(stamp);
            }
        }
        long stamp = _lock.writeLock();
        try {
            processorCache.get(ref).addOne();
            return processorCache.get(ref).getEmitterProcessor();
        } finally {
            _lock.unlockWrite(stamp);
        }
    }

    public void processorCancel(String ref) {

        long stamp = _lock.writeLock();
        try {
            if (processorCache.containsKey(ref)) {
                if (processorCache.get(ref).delOne()) {
                    remove(ref);
                }
            }
        } finally {
            _lock.unlockWrite(stamp);

        }

    }

    private void buildContainerStream(String ref) throws IOException, DockerException, InterruptedException {

        final PipedOutputStream stdOutputStream = new PipedOutputStream();
        final PipedOutputStream errOutputStream = new PipedOutputStream();

        final PipedInputStream stdInputStream = new PipedInputStream(stdOutputStream);
        final PipedInputStream errInputStream = new PipedInputStream(errOutputStream);

        LogStream logStream = dockerClient.logs(ref
                , DockerClient.LogsParam.create("tail", "10")
                , DockerClient.LogsParam.create("follow", "1")
                , DockerClient.LogsParam.create("stdout", "1")
                , DockerClient.LogsParam.create("stderr", "1"));

        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("连接日志输出流");
                logStream.attach(stdOutputStream, errOutputStream);
            } catch (IOException e) {
                remove(ref);
            }
        }).whenComplete((v, e) -> {
            if (e != null) {
                e.printStackTrace();
            }
            remove(ref);

        });

        handleStdoutStream(ref, logStream, stdInputStream, errInputStream);

    }

    private void handleStdoutStream(String ref, LogStream logStream, InputStream stdOutInputStream, InputStream stdErrInputStream) {
        EmitterProcessor<String> emitterProcessor = EmitterProcessor.create(256);
        emitterProcessor.doOnComplete(() -> processorCache.remove(ref));

//        Flux.ge
        CompletableFuture.runAsync(() -> {
            System.out.println("开始读取流");
            FluxSink<String> sink = emitterProcessor.sink();
            try (BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(stdOutInputStream, "utf-8"))) {
                String line;
                while ((line = stdOutReader.readLine()) != null) {
                    sink.next(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                sink.error(e);
            }
        }).whenComplete((v, e) -> {
            if (e != null) {
                e.printStackTrace();
            }
            remove(ref);
        });

        processorCache.put(ref, new LoggerObject(emitterProcessor, logStream, stdOutInputStream, stdErrInputStream));
    }

    private void remove(String ref) {
        if (processorCache.containsKey(ref)){
            if (processorCache.get(ref).destory()) {
                processorCache.remove(ref);
            }
        }

    }


    private static class LoggerObject {
        private EmitterProcessor<String> emitterProcessor;
        private volatile int attchCount = 0;
        private LogStream logStream;
        private InputStream stdOutInputStream;
        private InputStream stdErrInputStream;

        private final Object _lock = new Object();

        public boolean destory() {
            if (!emitterProcessor.isCancelled()){
                emitterProcessor.onComplete();
            }
            try {
                stdOutInputStream.close();
                stdErrInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        public void addOne() {
            synchronized (_lock) {
                attchCount++;
            }
        }

        public boolean delOne() {
            synchronized (_lock) {
                attchCount--;
            }
            return attchCount == 0;
        }

        public LoggerObject(EmitterProcessor<String> emitterProcessor, LogStream logStream, InputStream stdOutInputStream, InputStream stdErrInputStream) {
            this.emitterProcessor = emitterProcessor;
            this.logStream = logStream;
            this.stdOutInputStream = stdOutInputStream;
            this.stdErrInputStream = stdErrInputStream;
        }

        public EmitterProcessor<String> getEmitterProcessor() {
            return emitterProcessor;
        }

        public void setEmitterProcessor(EmitterProcessor<String> emitterProcessor) {
            this.emitterProcessor = emitterProcessor;
        }

        public int getAttchCount() {
            return attchCount;
        }

        public void setAttchCount(int attchCount) {
            this.attchCount = attchCount;
        }

        public LogStream getLogStream() {
            return logStream;
        }

        public void setLogStream(LogStream logStream) {
            this.logStream = logStream;
        }
    }
}
