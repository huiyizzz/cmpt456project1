/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.demo;


import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.SmallFloat;
import org.apache.lucene.search.similarities.ClassicSimilarity;

/**
 * Expert: Default scoring implementation which {@link #encodeNormValue(float)
 * encodes} norm values as a single byte before being stored. At search time,
 * the norm byte value is read from the index
 * {@link org.apache.lucene.store.Directory directory} and
 * {@link #decodeNormValue(long) decoded} back to a float <i>norm</i> value.
 * This encoding/decoding, while reducing index size, comes with the price of
 * precision loss - it is not guaranteed that <i>decode(encode(x)) = x</i>. For
 * instance, <i>decode(encode(0.89)) = 0.875</i>.
 * <p>
 * Compression of norm values to a single byte saves memory at search time,
 * because once a field is referenced at search time, its norms - for all
 * documents - are maintained in memory.
 * <p>
 * The rationale supporting such lossy compression of norm values is that given
 * the difficulty (and inaccuracy) of users to express their true information
 * need by a query, only big differences matter. <br>
 * &nbsp;<br>
 * Last, note that search time is too late to modify this <i>norm</i> part of
 * scoring, e.g. by using a different {@link CMPT456Similarity} for search.
 */
public class CMPT456Similarity extends ClassicSimilarity {
  
  public CMPT456Similarity() {}
  
  /** Implemented as <code>sqrt(freq+1)</code>. */
  @Override
  public float tf(float freq) {
    return (float) Math.sqrt(freq + 1);
  }

  /** Implemented as <code>log((docCount+2)/(docFreq+2)) + 1</code>. */
  @Override
  public float idf(long docFreq, long docCount) {
    return (float)(Math.log((docCount+2)/(double)(docFreq+2)) + 1.0);
  }

}
