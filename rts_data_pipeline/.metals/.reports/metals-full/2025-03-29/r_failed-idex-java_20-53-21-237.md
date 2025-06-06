error id: jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/7.0.0/arrow-vector-7.0.0-sources.jar!/codegen/templates/UnionListWriter.java
file://<WORKSPACE>/jar:file:<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/7.0.0/arrow-vector-7.0.0-sources.jar!/codegen/templates/UnionListWriter.java
### java.lang.Exception: Unexpected symbol '#' at word pos: '35' Line: '<#list  ["List", "LargeList"] as listName>'

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

import org.apache.arrow.memory.ArrowBuf;
import org.apache.arrow.vector.complex.writer.Decimal256Writer;
import org.apache.arrow.vector.complex.writer.DecimalWriter;
import org.apache.arrow.vector.holders.Decimal256Holder;
import org.apache.arrow.vector.holders.DecimalHolder;


import java.lang.UnsupportedOperationException;
import java.math.BigDecimal;

<@pp.dropOutputFile />
<#list ["List", "LargeList"] as listName>

<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/impl/Union${listName}Writer.java" />

<#include "/@includes/license.ftl" />

package org.apache.arrow.vector.complex.impl;

import static org.apache.arrow.memory.util.LargeMemoryUtil.checkedCastToInt;
<#include "/@includes/vv_imports.ftl" />

/*
 * This class is generated using freemarker and the ${.template_name} template.
 */

@SuppressWarnings("unused")
public class Union${listName}Writer extends AbstractFieldWriter {

  protected ${listName}Vector vector;
  protected PromotableWriter writer;
  private boolean inStruct = false;
  private boolean listStarted = false;
  private String structName;
  <#if listName == "LargeList">
  private static final long OFFSET_WIDTH = 8;
  <#else>
  private static final int OFFSET_WIDTH = 4;
  </#if>

  public Union${listName}Writer(${listName}Vector vector) {
    this(vector, NullableStructWriterFactory.getNullableStructWriterFactoryInstance());
  }

  public Union${listName}Writer(${listName}Vector vector, NullableStructWriterFactory nullableStructWriterFactory) {
    this.vector = vector;
    this.writer = new PromotableWriter(vector.getDataVector(), vector, nullableStructWriterFactory);
  }

  public Union${listName}Writer(${listName}Vector vector, AbstractFieldWriter parent) {
    this(vector);
  }

  @Override
  public void allocate() {
    vector.allocateNew();
  }

  @Override
  public void clear() {
    vector.clear();
  }

  @Override
  public Field getField() {
    return vector.getField();
  }

  public void setValueCount(int count) {
    vector.setValueCount(count);
  }

  @Override
  public int getValueCapacity() {
    return vector.getValueCapacity();
  }

  @Override
  public void close() throws Exception {
    vector.close();
    writer.close();
  }

  @Override
  public void setPosition(int index) {
    super.setPosition(index);
  }

  <#list vv.types as type><#list type.minor as minor><#assign name = minor.class?cap_first />
  <#assign fields = minor.fields!type.fields />
  <#assign uncappedName = name?uncap_first/>
  <#if uncappedName == "int" ><#assign uncappedName = "integer" /></#if>
  <#if !minor.typeParams?? >

  @Override
  public ${name}Writer ${uncappedName}() {
    return this;
  }

  @Override
  public ${name}Writer ${uncappedName}(String name) {
    structName = name;
    return writer.${uncappedName}(name);
  }
  </#if>
  </#list></#list>

  @Override
  public DecimalWriter decimal() {
    return this;
  }

  @Override
  public DecimalWriter decimal(String name, int scale, int precision) {
    return writer.decimal(name, scale, precision);
  }

  @Override
  public DecimalWriter decimal(String name) {
    return writer.decimal(name);
  }

  @Override
  public Decimal256Writer decimal256() {
    return this;
  }

  @Override
  public Decimal256Writer decimal256(String name, int scale, int precision) {
    return writer.decimal256(name, scale, precision);
  }

  @Override
  public Decimal256Writer decimal256(String name) {
    return writer.decimal256(name);
  }


  @Override
  public StructWriter struct() {
    inStruct = true;
    return this;
  }

  @Override
  public ListWriter list() {
    return writer;
  }

  @Override
  public ListWriter list(String name) {
    ListWriter listWriter = writer.list(name);
    return listWriter;
  }

  @Override
  public StructWriter struct(String name) {
    StructWriter structWriter = writer.struct(name);
    return structWriter;
  }

  @Override
  public MapWriter map() {
    return writer;
  }

  @Override
  public MapWriter map(String name) {
    MapWriter mapWriter = writer.map(name);
    return mapWriter;
  }

  @Override
  public MapWriter map(boolean keysSorted) {
    writer.map(keysSorted);
    return writer;
  }

  @Override
  public MapWriter map(String name, boolean keysSorted) {
    MapWriter mapWriter = writer.map(name, keysSorted);
    return mapWriter;
  }

  <#if listName == "LargeList">
  @Override
  public void startList() {
    vector.startNewValue(idx());
    writer.setPosition(checkedCastToInt(vector.getOffsetBuffer().getLong((idx() + 1L) * OFFSET_WIDTH)));
    listStarted = true;
  }

  @Override
  public void endList() {
    vector.getOffsetBuffer().setLong((idx() + 1L) * OFFSET_WIDTH, writer.idx());
    setPosition(idx() + 1);
    listStarted = false;
  }
  <#else>
  @Override
  public void startList() {
    vector.startNewValue(idx());
    writer.setPosition(vector.getOffsetBuffer().getInt((idx() + 1L) * OFFSET_WIDTH));
    listStarted = true;
  }

  @Override
  public void endList() {
    vector.getOffsetBuffer().setInt((idx() + 1L) * OFFSET_WIDTH, writer.idx());
    setPosition(idx() + 1);
    listStarted = false;
  }
  </#if>

  @Override
  public void start() {
    writer.start();
  }

  @Override
  public void end() {
    writer.end();
    inStruct = false;
  }

  @Override
  public void write(DecimalHolder holder) {
    writer.write(holder);
    writer.setPosition(writer.idx()+1);
  }

  @Override
  public void write(Decimal256Holder holder) {
    writer.write(holder);
    writer.setPosition(writer.idx()+1);
  }

  @Override
  public void writeNull() {
    if (!listStarted){
      vector.setNull(idx());
    } else {
      writer.writeNull();
    }
  }

  public void writeDecimal(long start, ArrowBuf buffer, ArrowType arrowType) {
    writer.writeDecimal(start, buffer, arrowType);
    writer.setPosition(writer.idx()+1);
  }

  public void writeDecimal(long start, ArrowBuf buffer) {
    writer.writeDecimal(start, buffer);
    writer.setPosition(writer.idx()+1);
  }

  public void writeDecimal(BigDecimal value) {
    writer.writeDecimal(value);
    writer.setPosition(writer.idx()+1);
  }

  public void writeBigEndianBytesToDecimal(byte[] value, ArrowType arrowType){
    writer.writeBigEndianBytesToDecimal(value, arrowType);
    writer.setPosition(writer.idx() + 1);
  }

  public void writeDecimal256(long start, ArrowBuf buffer, ArrowType arrowType) {
    writer.writeDecimal256(start, buffer, arrowType);
    writer.setPosition(writer.idx()+1);
  }

  public void writeDecimal256(long start, ArrowBuf buffer) {
    writer.writeDecimal256(start, buffer);
    writer.setPosition(writer.idx()+1);
  }

  public void writeDecimal256(BigDecimal value) {
    writer.writeDecimal256(value);
    writer.setPosition(writer.idx()+1);
  }

  public void writeBigEndianBytesToDecimal256(byte[] value, ArrowType arrowType){
    writer.writeBigEndianBytesToDecimal256(value, arrowType);
    writer.setPosition(writer.idx() + 1);
  }


  <#list vv.types as type>
    <#list type.minor as minor>
      <#assign name = minor.class?cap_first />
      <#assign fields = minor.fields!type.fields />
      <#assign uncappedName = name?uncap_first/>
      <#if !minor.typeParams?? >
  @Override
  public void write${name}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list>) {
    writer.write${name}(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>);
    writer.setPosition(writer.idx()+1);
  }

  public void write(${name}Holder holder) {
    writer.write${name}(<#list fields as field>holder.${field.name}<#if field_has_next>, </#if></#list>);
    writer.setPosition(writer.idx()+1);
  }

      </#if>
    </#list>
  </#list>
}
</#list>

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
	scala.meta.internal.io.PlatformFileIO$.withJarFileSystem(PlatformFileIO.scala:79)
	scala.meta.internal.io.FileIO$.withJarFileSystem(FileIO.scala:33)
	scala.meta.internal.mtags.SymbolIndexBucket.addSourceJar(SymbolIndexBucket.scala:68)
	scala.meta.internal.mtags.OnDemandSymbolIndex.$anonfun$addSourceJar$2(OnDemandSymbolIndex.scala:85)
	scala.meta.internal.mtags.OnDemandSymbolIndex.tryRun(OnDemandSymbolIndex.scala:131)
	scala.meta.internal.mtags.OnDemandSymbolIndex.addSourceJar(OnDemandSymbolIndex.scala:84)
	scala.meta.internal.metals.Indexer.indexJar(Indexer.scala:565)
	scala.meta.internal.metals.Indexer.addSourceJarSymbols(Indexer.scala:559)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$5(Indexer.scala:387)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	scala.collection.AbstractIterable.foreach(Iterable.scala:935)
	scala.collection.IterableOps$WithFilter.foreach(Iterable.scala:905)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$1(Indexer.scala:378)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$1$adapted(Indexer.scala:377)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	scala.collection.AbstractIterable.foreach(Iterable.scala:935)
	scala.meta.internal.metals.Indexer.indexDependencySources(Indexer.scala:377)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$20(Indexer.scala:198)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.meta.internal.metals.TimerProvider.timedThunk(TimerProvider.scala:25)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$19(Indexer.scala:191)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$19$adapted(Indexer.scala:187)
	scala.collection.immutable.List.foreach(List.scala:334)
	scala.meta.internal.metals.Indexer.indexWorkspace(Indexer.scala:187)
	scala.meta.internal.metals.Indexer.$anonfun$profiledIndexWorkspace$2(Indexer.scala:57)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.meta.internal.metals.TimerProvider.timedThunk(TimerProvider.scala:25)
	scala.meta.internal.metals.Indexer.$anonfun$profiledIndexWorkspace$1(Indexer.scala:57)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.concurrent.Future$.$anonfun$apply$1(Future.scala:687)
	scala.concurrent.impl.Promise$Transformation.run(Promise.scala:467)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	java.base/java.lang.Thread.run(Thread.java:1570)
```
#### Short summary: 

Java indexer failed with and exception.