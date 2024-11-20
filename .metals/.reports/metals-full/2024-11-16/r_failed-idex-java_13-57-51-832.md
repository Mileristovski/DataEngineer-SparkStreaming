error id: jar:file:///C:/Users/Nakamarts/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/12.0.1/arrow-vector-12.0.1-sources.jar!/codegen/templates/DenseUnionReader.java
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


import org.apache.arrow.vector.complex.impl.UnionListReader;
import org.apache.arrow.vector.types.Types.MinorType;
import org.apache.arrow.vector.types.pojo.Field;

<@pp.dropOutputFile />
<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/impl/DenseUnionReader.java" />


<#include "/@includes/license.ftl" />

        package org.apache.arrow.vector.complex.impl;

<#include "/@includes/vv_imports.ftl" />
/**
 * Source code generated using FreeMarker template ${.template_name}
 */
@SuppressWarnings("unused")
public class DenseUnionReader extends AbstractFieldReader {

  private BaseReader[] readers = new BaseReader[Byte.MAX_VALUE + 1];
  public DenseUnionVector data;

  public DenseUnionReader(DenseUnionVector data) {
    this.data = data;
  }

  public MinorType getMinorType() {
    byte typeId = data.getTypeId(idx());
    return data.getVectorByType(typeId).getMinorType();
  }

  public byte getTypeId() {
    return data.getTypeId(idx());
  }

  @Override
  public Field getField() {
    return data.getField();
  }

  public boolean isSet(){
    return !data.isNull(idx());
  }

  public void read(DenseUnionHolder holder) {
    holder.reader = this;
    holder.isSet = this.isSet() ? 1 : 0;
    holder.typeId = getTypeId();
  }

  public void read(int index, UnionHolder holder) {
    byte typeId = data.getTypeId(index);
    getList(typeId).read(index, holder);
  }

  private FieldReader getReaderForIndex(int index) {
    byte typeId = data.getTypeId(index);
    MinorType minorType = data.getVectorByType(typeId).getMinorType();
    FieldReader reader = (FieldReader) readers[typeId];
    if (reader != null) {
      return reader;
    }
    switch (minorType) {
      case NULL:
        reader = NullReader.INSTANCE;
        break;
      case STRUCT:
        reader = (FieldReader) getStruct(typeId);
        break;
      case LIST:
        reader = (FieldReader) getList(typeId);
        break;
    <#list vv.types as type>
      <#list type.minor as minor>
        <#assign name = minor.class?cap_first />
        <#assign uncappedName = name?uncap_first/>
        <#if !minor.typeParams?? || minor.class?starts_with("Decimal")>
      case ${name?upper_case}:
      reader = (FieldReader) get${name}(typeId);
      break;
        </#if>
      </#list>
    </#list>
      default:
        throw new UnsupportedOperationException("Unsupported type: " + MinorType.values()[typeId]);
    }
    return reader;
  }

  private SingleStructReaderImpl structReader;

  private StructReader getStruct(byte typeId) {
    StructReader structReader = (StructReader) readers[typeId];
    if (structReader == null) {
      structReader = (SingleStructReaderImpl) data.getVectorByType(typeId).getReader();
      structReader.setPosition(idx());
      readers[typeId] = structReader;
    }
    return structReader;
  }

  private UnionListReader listReader;

  private FieldReader getList(byte typeId) {
    UnionListReader listReader = (UnionListReader) readers[typeId];
    if (listReader == null) {
      listReader = new UnionListReader((ListVector) data.getVectorByType(typeId));
      listReader.setPosition(idx());
      readers[typeId] = listReader;
    }
    return listReader;
  }

  private UnionMapReader mapReader;

  private FieldReader getMap(byte typeId) {
    UnionMapReader mapReader = (UnionMapReader) readers[typeId];
    if (mapReader == null) {
      mapReader = new UnionMapReader((MapVector) data.getVectorByType(typeId));
      mapReader.setPosition(idx());
      readers[typeId] = mapReader;
    }
    return mapReader;
  }

  @Override
  public java.util.Iterator<String> iterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void copyAsValue(UnionWriter writer) {
    writer.data.copyFrom(idx(), writer.idx(), data);
  }

  <#list ["Object", "BigDecimal", "Short", "Integer", "Long", "Boolean",
          "LocalDateTime", "Duration", "Period", "Double", "Float",
          "Character", "Text", "Byte", "byte[]", "PeriodDuration"] as friendlyType>
  <#assign safeType=friendlyType />
  <#if safeType=="byte[]"><#assign safeType="ByteArray" /></#if>

  @Override
  public ${friendlyType} read${safeType}() {
    return getReaderForIndex(idx()).read${safeType}();
  }

  </#list>

  public int size() {
    return getReaderForIndex(idx()).size();
  }

  <#list vv.types as type>
    <#list type.minor as minor>
      <#assign name = minor.class?cap_first />
      <#assign uncappedName = name?uncap_first/>
      <#assign boxedType = (minor.boxedType!type.boxedType) />
      <#assign javaType = (minor.javaType!type.javaType) />
      <#assign friendlyType = (minor.friendlyType!minor.boxedType!type.boxedType) />
      <#assign safeType=friendlyType />
      <#if safeType=="byte[]"><#assign safeType="ByteArray" /></#if>
      <#if !minor.typeParams?? || minor.class?starts_with("Decimal")>

  private ${name}ReaderImpl get${name}(byte typeId) {
    ${name}ReaderImpl reader = (${name}ReaderImpl) readers[typeId];
    if (reader == null) {
      reader = new ${name}ReaderImpl((${name}Vector) data.getVectorByType(typeId));
      reader.setPosition(idx());
      readers[typeId] = reader;
    }
    return reader;
  }

  public void read(Nullable${name}Holder holder){
    getReaderForIndex(idx()).read(holder);
  }

  public void copyAsValue(${name}Writer writer){
    getReaderForIndex(idx()).copyAsValue(writer);
  }
      </#if>
    </#list>
  </#list>

  @Override
  public void copyAsValue(ListWriter writer) {
    ComplexCopier.copy(this, (FieldWriter) writer);
  }

  @Override
  public void setPosition(int index) {
    super.setPosition(index);
    byte typeId = data.getTypeId(index);
    if (readers[typeId] != null) {
      int offset = data.getOffset(index);
      readers[typeId].setPosition(offset);
    }
  }

  public FieldReader reader(byte typeId, String name){
    return getStruct(typeId).reader(name);
  }

  public FieldReader reader(byte typeId) {
    return getList(typeId).reader();
  }

  public boolean next() {
    return getReaderForIndex(idx()).next();
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