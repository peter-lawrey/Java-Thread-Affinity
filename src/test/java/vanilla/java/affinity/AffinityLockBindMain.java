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
public class AffinityLockBindMain {
    public static void main(String... args) throws InterruptedException {
        AffinityLock al = AffinityLock.acquireLock();
        try {
            AffinityLock readerLock = al.acquireLock(AffinityAssignmentStrategies.DIFFERENT_CORE);
            new Thread(new SleepRunnable(readerLock), "reader").start();
            AffinityLock writerLock = readerLock.acquireLock(AffinityAssignmentStrategies.SAME_CORE);
            new Thread(new SleepRunnable(writerLock), "writer").start();
            Thread.sleep(200);
        } finally {
            al.release();
        }
        new Thread(new SleepRunnable(al), "engine").start();

        Thread.sleep(200);
        System.out.println("\nThe assignment of CPUs is\n" + AffinityLock.dumpLocks());
    }

    private static class SleepRunnable implements Runnable {
        private final AffinityLock affinityLock;

        public SleepRunnable(AffinityLock affinityLock) {
            this.affinityLock = affinityLock;
        }

        public void run() {
            affinityLock.bind();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            } finally {
                affinityLock.release();
            }
        }
    }
}
