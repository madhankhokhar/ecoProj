////package com.example.ecoProj.scheduler;
////
////import org.springframework.scheduling.annotation.Scheduled;
////import org.springframework.stereotype.Component;
////
////import java.time.LocalDateTime;
//////@Component
//////public class TestScheduler {
//////
//////    @Scheduled(fixedRate = 2 * 60 * 1000)
//////    public void runTask() throws InterruptedException {
//////        System.out.println("Task Started: " + LocalDateTime.now());
//////
//////        // Sleep for 2 minutes (120,000 milliseconds)
//////        Thread.sleep(3 * 60 * 1000);
//////
//////        System.out.println("Task Finished: " + LocalDateTime.now());
//////    }
//////
//////
//////}
////@Component
////public class TestScheduler {
////
////    // Task A: Runs every 10 seconds, but takes 5 seconds to finish
////    @Scheduled(fixedRate = 20000)
////    public void taskA() throws InterruptedException {
////        System.out.println("Task A START: " + LocalDateTime.now() + " on thread: " + Thread.currentThread().getName());
////        Thread.sleep(100000);
////        System.out.println("Task A END  : " + LocalDateTime.now());
////    }
////
////    // Task B: Also runs every 10 seconds, takes 5 seconds to finish
////    @Scheduled(fixedRate = 20000)
////    public void taskB() throws InterruptedException {
////        System.out.println("Task B START: " + LocalDateTime.now() + " on thread: " + Thread.currentThread().getName());
////        Thread.sleep(100000);
////        System.out.println("Task B END  : " + LocalDateTime.now());
////    }
////}
//
//package com.example.ecoProj.scheduler;
//
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//@Component
//public class TestScheduler {
//
//    private final AtomicBoolean taskARunning =
//            new AtomicBoolean(false);
//
//    private final AtomicBoolean taskBRunning =
//            new AtomicBoolean(false);
//
//    @Async
//    @Scheduled(cron = "0 * * * * *")
//    public void taskA() throws InterruptedException {
//
//        // Prevent overlapping
//        if (!taskARunning.compareAndSet(false, true)) {
//
////            System.out.println(
////                    "Task A skipped because previous execution still running"
////            );
//
//            return;
//        }
//
//        try {
//
//            System.out.println(
//                    "Task A START: " +
//                            LocalDateTime.now() +
//                            " | Thread: " +
//                            Thread.currentThread().getName()
//            );
//
//            Thread.sleep(1000 * 60 * 2);
//
//            System.out.println(
//                    "Task A END  : " +
//                            LocalDateTime.now() +
//                            " | Thread: " +
//                            Thread.currentThread().getName()
//            );
//
//        } finally {
//
//            taskARunning.set(false);
//        }
//    }
//
//    @Async
//    @Scheduled(cron = "0 * * * * *")
//    public void taskB() throws InterruptedException {
//
//        // Prevent overlapping
//        if (!taskBRunning.compareAndSet(false, true)) {
//
////            System.out.println(
////                    "Task B skipped because previous execution still running"
////            );
//
//            return;
//        }
//
//        try {
//
//            System.out.println(
//                    "Task B START: " +
//                            LocalDateTime.now() +
//                            " | Thread: " +
//                            Thread.currentThread().getName()
//            );
//
//            Thread.sleep(1000 * 60 * 2);
//
//            System.out.println(
//                    "Task B END  : " +
//                            LocalDateTime.now() +
//                            " | Thread: " +
//                            Thread.currentThread().getName()
//            );
//
//        } finally {
//
//            taskBRunning.set(false);
//        }
//    }
//}

package com.example.ecoProj.scheduler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TestScheduler {

    private final AtomicBoolean taskARunning =
            new AtomicBoolean(false);

    private final AtomicBoolean taskBRunning =
            new AtomicBoolean(false);

    @Async("schedulerExecutor")
    @Scheduled(cron = "0 * 12 * * *")
    public void taskA() throws InterruptedException {

        if (!taskARunning.compareAndSet(false, true)) {

            System.out.println("Task A skipped");

            return;
        }

        try {

            System.out.println(
                    "Task A START: " +
                            LocalDateTime.now() +
                            " | Thread: " +
                            Thread.currentThread().getName()
            );

            Thread.sleep(1000 * 60 * 2);

            System.out.println(
                    "Task A END  : " +
                            LocalDateTime.now() +
                            " | Thread: " +
                            Thread.currentThread().getName()
            );

        } finally {

            taskARunning.set(false);
        }
    }

    @Async("schedulerExecutor")
    @Scheduled(cron = "0 * 12 * * *")
    public void taskB() throws InterruptedException {

        if (!taskBRunning.compareAndSet(false, true)) {

            System.out.println("Task B skipped");

            return;
        }

        try {

            System.out.println(
                    "Task B START: " +
                            LocalDateTime.now() +
                            " | Thread: " +
                            Thread.currentThread().getName()
            );

            Thread.sleep(1000 * 60 * 2);

            System.out.println(
                    "Task B END  : " +
                            LocalDateTime.now() +
                            " | Thread: " +
                            Thread.currentThread().getName()
            );

        } finally {

            taskBRunning.set(false);
        }
    }
}