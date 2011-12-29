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

    protected interface IAffinityImpl {
        public long getAffinity();

        public void setAffinity(final long affinity);
    }

    private static final IAffinityImpl affinityImpl;

    static {
        if ( NativeAffinity.LOADED ) {
            System.out.println( "Using JNI-based affinity control implementation" );
            affinityImpl = NativeAffinity.INSTANCE;
        } else if ( JNAAffinity.LOADED ) {
            System.out.println( "Using JNA-based affinity control implementation" );
            affinityImpl = JNAAffinity.INSTANCE;
        } else {
            System.out.println( "WARNING: Using dummy affinity control implementation!" );
            affinityImpl = NullAffinity.INSTANCE;
        }
    }

    public static long getAffinity() {
        return affinityImpl.getAffinity();
    }

    public static void setAffinity(final long affinity) {
        affinityImpl.setAffinity(affinity);
    }
}
