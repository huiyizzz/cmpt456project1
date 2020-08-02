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


import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;


public final class CMPT456Analyzer extends StopwordAnalyzerBase {

    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

    private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;
    /** An unmodifiable set containing some common English words that are usually not
     useful for searching. */
    public CharArraySet STOP_WORDS_SET;


    public CMPT456Analyzer() throws IOException {
        String path = "lucene/demo/src/java/org/apache/lucene/demo/stopwords.txt";
        List<String> stopWords = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        this.STOP_WORDS_SET = new CharArraySet(stopWords, true);
    }

  /**
   * Set the max allowed token length.  Tokens larger than this will be chopped
   * up at this token length and emitted as multiple tokens.  If you need to
   * skip such large tokens, you could increase this max length, and then
   * use {@code LengthFilter} to remove long tokens.  The default is
   * {@link CMPT456Analyzer#DEFAULT_MAX_TOKEN_LENGTH}.
   */
    public void setMaxTokenLength(int length) {
        maxTokenLength = length;
    }

    /** Returns the current maximum token length
     *
     *  @see #setMaxTokenLength */
    public int getMaxTokenLength() {
        return maxTokenLength;
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();
        src.setMaxTokenLength(maxTokenLength);
        TokenStream tok = new StandardFilter(src);
        tok = new LowerCaseFilter(tok);
        tok = new StopFilter(tok, STOP_WORDS_SET);
        tok = new PorterStemFilter(tok);
        return new TokenStreamComponents(src, tok) {
            @Override
            protected void setReader(final Reader reader) {
                // So that if maxTokenLength was changed, the change takes
                // effect next time tokenStream is called:
                src.setMaxTokenLength(CMPT456Analyzer.this.maxTokenLength);
                super.setReader(reader);
            }
        };
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        TokenStream result = new StandardFilter(in);
        result = new LowerCaseFilter(result);
        return result;
    }
}
