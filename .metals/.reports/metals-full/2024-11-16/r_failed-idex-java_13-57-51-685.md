error id: jar:file:///C:/Users/Nakamarts/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/12.0.1/arrow-vector-12.0.1-sources.jar!/codegen/templates/UnionWriter.java
### java.lang.Exception: Unexpected symbol '#' at word pos: '35' Line: '    <#list  vv.types as type>'

Java indexer failed with and exception.
```Java
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.arrow.util.Preconditions;
import org.apache.arrow.vector.complex.impl.NullableStructWriterFactory;
import org.apache.arrow.vector.types.Types;

<@pp.dropOutputFile />
<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/impl/UnionWriter.java" />


<#include "/@includes/license.ftl" />

package org.apache.arrow.vector.complex.impl;

<#include "/@includes/vv_imports.ftl" />
import org.apache.arrow.vector.complex.writer.BaseWriter;
import org.apache.arrow.vector.types.Types.MinorType;

/*
 * This class is generated using freemarker and the ${.template_name} template.
 */
@SuppressWarnings("unused")
public class UnionWriter extends AbstractFieldWriter implements FieldWriter {

  UnionVector data;
  private StructWriter structWriter;
  private UnionListWriter listWriter;
  private UnionMapWriter mapWriter;
  private List<BaseWriter> writers = new java.util.ArrayList<>();
  private final NullableStructWriterFactory nullableStructWriterFactory;

  public UnionWriter(UnionVector vector) {
    this(vector, NullableStructWriterFactory.getNullableStructWriterFactoryInstance());
  }

  public UnionWriter(UnionVector vector, NullableStructWriterFactory nullableStructWriterFactory) {
    data = vector;
    this.nullableStructWriterFactory = nullableStructWriterFactory;
  }

  @Override
  public void setPosition(int index) {
    super.setPosition(index);
    for (BaseWriter writer : writers) {
      writer.setPosition(index);
    }
  }


  @Override
  public void start() {
    data.setType(idx(), MinorType.STRUCT);
    getStructWriter().start();
  }

  @Override
  public void end() {
    getStructWriter().end();
  }

  @Override
  public void startList() {
    getListWriter().startList();
    data.setType(idx(), MinorType.LIST);
  }

  @Override
  public void endList() {
    getListWriter().endList();
  }

  @Override
  public void startMap() {
    getMapWriter().startMap();
    data.setType(idx(), MinorType.MAP);
  }

  @Override
  public void endMap() {
    getMapWriter().endMap();
  }

  @Override
  public void startEntry() {
    getMapWriter().startEntry();
  }

  @Override
  public MapWriter key() {
    return getMapWriter().key();
  }

  @Override
  public MapWriter value() {
    return getMapWriter().value();
  }

  @Override
  public void endEntry() {
    getMapWriter().endEntry();
  }

  private StructWriter getStructWriter() {
    if (structWriter == null) {
      structWriter = nullableStructWriterFactory.build(data.getStruct());
      structWriter.setPosition(idx());
      writers.add(structWriter);
    }
    return structWriter;
  }

  public StructWriter asStruct() {
    data.setType(idx(), MinorType.STRUCT);
    return getStructWriter();
  }

  private ListWriter getListWriter() {
    if (listWriter == null) {
      listWriter = new UnionListWriter(data.getList(), nullableStructWriterFactory);
      listWriter.setPosition(idx());
      writers.add(listWriter);
    }
    return listWriter;
  }

  public ListWriter asList() {
    data.setType(idx(), MinorType.LIST);
    return getListWriter();
  }

  private MapWriter getMapWriter() {
    if (mapWriter == null) {
      mapWriter = new UnionMapWriter(data.getMap(new ArrowType.Map(false)));
      mapWriter.setPosition(idx());
      writers.add(mapWriter);
    }
    return mapWriter;
  }

  private MapWriter getMapWriter(ArrowType arrowType) {
    if (mapWriter == null) {
      mapWriter = new UnionMapWriter(data.getMap(arrowType));
      mapWriter.setPosition(idx());
      writers.add(mapWriter);
    }
    return mapWriter;
  }

  public MapWriter asMap(ArrowType arrowType) {
    data.setType(idx(), MinorType.MAP);
    return getMapWriter(arrowType);
  }

  BaseWriter getWriter(MinorType minorType) {
    return getWriter(minorType, null);
  }

  BaseWriter getWriter(MinorType minorType, ArrowType arrowType) {
    switch (minorType) {
    case STRUCT:
      return getStructWriter();
    case LIST:
      return getListWriter();
    case MAP:
      return getMapWriter(arrowType);
    <#list vv.types as type>
      <#list type.minor as minor>
        <#assign name = minor.class?cap_first />
        <#assign fields = minor.fields!type.fields />
        <#assign uncappedName = name?uncap_first/>
        <#if !minor.typeParams?? || minor.class?starts_with("Decimal")>
    case ${name?upper_case}:
      return get${name}Writer(<#if minor.class?starts_with("Decimal") >arrowType</#if>);
        </#if>
      </#list>
    </#list>
    default:
      throw new UnsupportedOperationException("Unknown type: " + minorType);
    }
  }
  <#list vv.types as type>
    <#list type.minor as minor>
      <#assign name = minor.class?cap_first />
      <#assign fields = minor.fields!type.fields />
      <#assign uncappedName = name?uncap_first/>
      <#assign friendlyType = (minor.friendlyType!minor.boxedType!type.boxedType) />
      <#if !minor.typeParams?? || minor.class?starts_with("Decimal") >

  private ${name}Writer ${name?uncap_first}Writer;

  private ${name}Writer get${name}Writer(<#if minor.class?starts_with("Decimal")>ArrowType arrowType</#if>) {
    if (${uncappedName}Writer == null) {
      ${uncappedName}Writer = new ${name}WriterImpl(data.get${name}Vector(<#if minor.class?starts_with("Decimal")>arrowType</#if>));
      ${uncappedName}Writer.setPosition(idx());
      writers.add(${uncappedName}Writer);
    }
    return ${uncappedName}Writer;
  }

  public ${name}Writer as${name}(<#if minor.class?starts_with("Decimal")>ArrowType arrowType</#if>) {
    data.setType(idx(), MinorType.${name?upper_case});
    return get${name}Writer(<#if minor.class?starts_with("Decimal")>arrowType</#if>);
  }

  @Override
  public void write(${name}Holder holder) {
    data.setType(idx(), MinorType.${name?upper_case});
    <#if minor.class?starts_with("Decimal")>ArrowType arrowType = new ArrowType.Decimal(holder.precision, holder.scale, ${name}Holder.WIDTH * 8);</#if>
    get${name}Writer(<#if minor.class?starts_with("Decimal")>arrowType</#if>).setPosition(idx());
    get${name}Writer(<#if minor.class?starts_with("Decimal")>arrowType</#if>).write${name}(<#list fields as field>holder.${field.name}<#if field_has_next>, </#if></#list><#if minor.class?starts_with("Decimal")>, arrowType</#if>);
  }

  public void write${minor.class}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list><#if minor.class?starts_with("Decimal")>, ArrowType arrowType</#if>) {
    data.setType(idx(), MinorType.${name?upper_case});
    get${name}Writer(<#if minor.class?starts_with("Decimal")>arrowType</#if>).setPosition(idx());
    get${name}Writer(<#if minor.class?starts_with("Decimal")>arrowType</#if>).write${name}(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list><#if minor.class?starts_with("Decimal")>, arrowType</#if>);
  }
  <#if minor.class?starts_with("Decimal")>
  public void write${name}(${friendlyType} value) {
    data.setType(idx(), MinorType.${name?upper_case});
    ArrowType arrowType = new ArrowType.Decimal(value.precision(), value.scale(), ${name}Vector.TYPE_WIDTH * 8);
    get${name}Writer(arrowType).setPosition(idx());
    get${name}Writer(arrowType).write${name}(value);
  }

  public void writeBigEndianBytesTo${name}(byte[] value, ArrowType arrowType) {
    data.setType(idx(), MinorType.${name?upper_case});
    get${name}Writer(arrowType).setPosition(idx());
    get${name}Writer(arrowType).writeBigEndianBytesTo${name}(value, arrowType);
  }
  </#if>
      </#if>
    </#list>
  </#list>

  public void writeNull() {
  }

  @Override
  public StructWriter struct() {
    data.setType(idx(), MinorType.LIST);
    getListWriter().setPosition(idx());
    return getListWriter().struct();
  }

  @Override
  public ListWriter list() {
    data.setType(idx(), MinorType.LIST);
    getListWriter().setPosition(idx());
    return getListWriter().list();
  }

  @Override
  public ListWriter list(String name) {
    data.setType(idx(), MinorType.STRUCT);
    getStructWriter().setPosition(idx());
    return getStructWriter().list(name);
  }

  @Override
  public StructWriter struct(String name) {
    data.setType(idx(), MinorType.STRUCT);
    getStructWriter().setPosition(idx());
    return getStructWriter().struct(name);
  }

  @Override
  public MapWriter map() {
    data.setType(idx(), MinorType.MAP);
    getListWriter().setPosition(idx());
    return getListWriter().map();
  }

  @Override
  public MapWriter map(boolean keysSorted) {
    data.setType(idx(), MinorType.MAP);
    getListWriter().setPosition(idx());
    return getListWriter().map(keysSorted);
  }

  @Override
  public MapWriter map(String name) {
    data.setType(idx(), MinorType.MAP);
    getStructWriter().setPosition(idx());
    return getStructWriter().map(name);
  }

  @Override
  public MapWriter map(String name, boolean keysSorted) {
    data.setType(idx(), MinorType.MAP);
    getStructWriter().setPosition(idx());
    return getStructWriter().map(name, keysSorted);
  }

  <#list vv.types as type><#list type.minor as minor>
  <#assign lowerName = minor.class?uncap_first />
  <#if lowerName == "int" ><#assign lowerName = "integer" /></#if>
  <#assign upperName = minor.class?upper_case />
  <#assign capName = minor.class?cap_first />
  <#if !minor.typeParams?? || minor.class?starts_with("Decimal") >
  @Override
  public ${capName}Writer ${lowerName}(String name) {
    data.setType(idx(), MinorType.STRUCT);
    getStructWriter().setPosition(idx());
    return getStructWriter().${lowerName}(name);
  }

  @Override
  public ${capName}Writer ${lowerName}() {
    data.setType(idx(), MinorType.LIST);
    getListWriter().setPosition(idx());
    return getListWriter().${lowerName}();
  }
  </#if>
  <#if minor.class?starts_with("Decimal")>
  @Override
  public ${capName}Writer ${lowerName}(String name<#list minor.typeParams as typeParam>, ${typeParam.type} ${typeParam.name}</#list>) {
    data.setType(idx(), MinorType.STRUCT);
    getStructWriter().setPosition(idx());
    return getStructWriter().${lowerName}(name<#list minor.typeParams as typeParam>, ${typeParam.name}</#list>);
  }
  </#if>
  </#list></#list>

  @Override
  public void allocate() {
    data.allocateNew();
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public void close() throws Exception {
    data.close();
  }

  @Override
  public Field getField() {
    return data.getField();
  }

  @Override
  public int getValueCapacity() {
    return data.getValueCapacity();
  }
}

```


#### Error stacktrace:

```
scala.meta.internal.mtags.JavaToplevelMtags.unexpectedCharacter(JavaToplevelMtags.scala:352)
	scala.meta.internal.mtags.JavaToplevelMtags.parseToken$1(JavaToplevelMtags.scala:253)
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