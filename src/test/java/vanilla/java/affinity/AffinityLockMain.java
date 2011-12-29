/*
 * Copyright 2011 Peter Lawrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vanilla.java.affinity;

/**
 * @author peter.lawrey
 */
public class AffinityLockMain {
    public static void main(String... args) throws InterruptedException {
        new Thread(new SleepRunnable(), "engine").start();
        new Thread(new SleepRunnable(), "reader").start();
        new Thread(new SleepRunnable(), "writer").start();
        Thread.sleep(100);
        System.out.println(AffinityLock.dumpLocks());
    }

    private static class SleepRunnable implements Runnable {
        public void run() {
            AffinityLock al = AffinityLock.acquireLock();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            } finally {
                al.release();
            }
        }
    }
}
