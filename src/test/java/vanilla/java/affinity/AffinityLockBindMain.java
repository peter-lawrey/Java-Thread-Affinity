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

import static vanilla.java.affinity.AffinityStrategies.*;

/**
 * @author peter.lawrey
 */
public class AffinityLockBindMain {
    public static void main(String... args) throws InterruptedException {
        AffinityLock al = AffinityLock.acquireLock();
        try {
            // find a cpu on a different socket, otherwise a different core.
            AffinityLock readerLock = al.acquireLock(DIFFERENT_SOCKET, DIFFERENT_CORE);
            new Thread(new SleepRunnable(readerLock), "reader").start();
            // find a cpu on the same core, or the same socket, or any free cpu.
            AffinityLock writerLock = readerLock.acquireLock(SAME_CORE, SAME_SOCKET, ANY);
            new Thread(new SleepRunnable(writerLock), "writer").start();
            Thread.sleep(200);
        } finally {
            al.release();
        }
// re-use the same cpu for the engine.
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
