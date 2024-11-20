error id: jar:file:///C:/Users/Nakamarts/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/apache/hadoop/hadoop-client-api/3.3.4/hadoop-client-api-3.3.4-sources.jar!/org/apache/hadoop/fs/audit/AuditConstants.java
### java.lang.RuntimeException: Broken file, quote doesn't end.

Java indexer failed with and exception.
```Java
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in org.apache.hadoop.shaded.com.liance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org.apache.hadoop.shaded.org.licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.shaded.org.apache.hadoop.fs.audit;

import org.apache.hadoop.shaded.org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.shaded.org.apache.hadoop.classification.InterfaceStability;

/**
 * Constants related to auditing.
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
public final class AuditConstants {

  private AuditConstants() {
  }

  /**
   * The host from where requests originate: {@value}.
   * example.org.apache.hadoop.shaded.org.is used as the IETF require that it never resolves.
   * This isn't always met by some mobile/consumer DNS services, but
   * we don't worry about that. What is important is that
   * a scan for "example.org.apache.hadoop.shaded.org. in the logs will exclusively find
   * entries from this referrer.
   */
  public static final String REFERRER_ORIGIN_HOST = "audit.example.org.apache.hadoop.shaded.org.;

  /**
   * Header: Command: {@value}.
   * Set by tool runner.
   */
  public static final String PARAM_COMMAND = "cm";

  /**
   * Header: FileSystem ID: {@value}.
   */
  public static final String PARAM_FILESYSTEM_ID = "fs";

  /**
   * Header: operation ID: {@value}.
   */
  public static final String PARAM_ID = "id";

  /**
   * JobID query header: {@value}.
   */
  public static final String PARAM_JOB_ID = "ji";

  /**
   * Header: operation: {@value}.
   * These should be from StoreStatisticNames or similar,
   * and are expected to be at the granularity of FS
   * API operations.
   */
  public static final String PARAM_OP = "op";

  /**
   * Header: first path of operation: {@value}.
   */
  public static final String PARAM_PATH = "p1";

  /**
   * Header: second path of operation: {@value}.
   */
  public static final String PARAM_PATH2 = "p2";

  /**
   * Header: Principal: {@value}.
   */
  public static final String PARAM_PRINCIPAL = "pr";

  /**
   * Header: Process ID: {@value}.
   */
  public static final String PARAM_PROCESS = "ps";

  /**
   * Thread 0: the thread which created a span {@value}.
   */
  public static final String PARAM_THREAD0 = "t0";

  /**
   * Thread 1: the thread making the S3 request: {@value}.
   */
  public static final String PARAM_THREAD1 = "t1";

  /**
   * Timestamp of span creation: {@value}.
   */
  public static final String PARAM_TIMESTAMP = "ts";

}

```


#### Error stacktrace:

```
scala.meta.internal.mtags.JavaToplevelMtags.quotedLiteral$1(JavaToplevelMtags.scala:174)
	scala.meta.internal.mtags.JavaToplevelMtags.parseToken$1(JavaToplevelMtags.scala:228)
	scala.meta.internal.mtags.JavaToplevelMtags.fetchToken(JavaToplevelMtags.scala:262)
	scala.meta.internal.mtags.JavaToplevelMtags.loop(JavaToplevelMtags.scala:73)
	scala.meta.internal.mtags.JavaToplevelMtags.indexRoot(JavaToplevelMtags.scala:42)
	scala.meta.internal.mtags.MtagsIndexer.index(MtagsIndexer.scala:21)
	scala.meta.internal.mtags.MtagsIndexer.index$(MtagsIndexer.scala:20)
	scala.meta.internal.mtags.JavaToplevelMtags.index(JavaToplevelMtags.scala:18)
	scala.meta.internal.mtags.Mtags.indexWithOverrides(Mtags.scala:74)
	scala.meta.internal.mtags.SymbolIndexBucket.indexSource(SymbolIndexBucket.scala:129)
	scala.meta.internal.mtags.SymbolIndexBucket.addSourceFile(SymbolIndexBucket.scala:108)
	scala.meta.internal.mtags.SymbolIndexBucket.$anonfun$addSourceJar$2(SymbolIndexBucket.scala:74)
	scala.collection.immutable.List.flatMap(List.scala:294)
	scala.meta.internal.mtags.SymbolIndexBucket.$anonfun$addSourceJar$1(SymbolIndexBucket.scala:70)
	scala.meta.internal.io.PlatformFileIO$.withJarFileSystem(PlatformFileIO.scala:77)
	scala.meta.internal.io.FileIO$.withJarFileSystem(FileIO.scala:33)
	scala.meta.internal.mtags.SymbolIndexBucket.addSourceJar(SymbolIndexBucket.scala:68)
	scala.meta.internal.mtags.OnDemandSymbolIndex.$anonfun$addSourceJar$2(OnDemandSymbolIndex.scala:85)
	scala.meta.internal.mtags.OnDemandSymbolIndex.tryRun(OnDemandSymbolIndex.scala:131)
	scala.meta.internal.mtags.OnDemandSymbolIndex.addSourceJar(OnDemandSymbolIndex.scala:84)
	scala.meta.internal.metals.Indexer.indexJar(Indexer.scala:561)
	scala.meta.internal.metals.Indexer.addSourceJarSymbols(Indexer.scala:555)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$5(Indexer.scala:381)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	scala.collection.AbstractIterable.foreach(Iterable.scala:935)
	scala.collection.IterableOps$WithFilter.foreach(Iterable.scala:905)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$1(Indexer.scala:372)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$1$adapted(Indexer.scala:371)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	scala.collection.AbstractIterable.foreach(Iterable.scala:935)
	scala.meta.internal.metals.Indexer.indexDependencySources(Indexer.scala:371)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$19(Indexer.scala:192)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.meta.internal.metals.TimerProvider.timedThunk(TimerProvider.scala:25)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$18(Indexer.scala:185)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$18$adapted(Indexer.scala:181)
	scala.collection.immutable.List.foreach(List.scala:334)
	scala.meta.internal.metals.Indexer.indexWorkspace(Indexer.scala:181)
	scala.meta.internal.metals.Indexer.$anonfun$profiledIndexWorkspace$2(Indexer.scala:57)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.meta.internal.metals.TimerProvider.timedThunk(TimerProvider.scala:25)
	scala.meta.internal.metals.Indexer.$anonfun$profiledIndexWorkspace$1(Indexer.scala:57)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.concurrent.Future$.$anonfun$apply$1(Future.scala:687)
	scala.concurrent.impl.Promise$Transformation.run(Promise.scala:467)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	java.base/java.lang.Thread.run(Thread.java:1575)
```
#### Short summary: 

Java indexer failed with and exception.