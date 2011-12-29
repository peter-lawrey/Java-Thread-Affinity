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
public enum AffinitySupport {
    ;

    interface IAffinity {
        public long getAffinity();

        public void setAffinity(long affinity);

        public long nanoTime();
    }

    private static final IAffinity affinityImpl;

    static {
        if (NativeAffinity.LOADED)
            affinityImpl = NativeAffinity.INSTANCE;
        else if (JNAAffinity.LOADED)
            affinityImpl = JNAAffinity.INSTANCE;
        else
            affinityImpl = NullAffinity.INSTANCE;
    }

    public static long getAffinity() {
        return affinityImpl.getAffinity();
    }

    public static void setAffinity(long affinity) {
        affinityImpl.setAffinity(affinity);
    }

    public static long nanoTime() {
        return affinityImpl.nanoTime();
    }
}
