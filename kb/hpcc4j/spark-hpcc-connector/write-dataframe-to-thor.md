# Write a Spark DataFrame to HPCC Thor (Scala + PySpark)

**Works with**
- Spark-HPCC connector (see your project’s Maven coordinates)
- HPCC Platform 9.x+ (TLS notes below)
- Spark 3.x

This page shows minimal, copy‑pasteable examples to write a Spark `DataFrame` to an HPCC Thor logical file. For a longer walkthrough, see the PySpark example in this repo.

---

## Scala example

```scala
import org.hpccsystems.spark.HpccFileWriter
import org.hpccsystems.spark.SparkHPCCWriteOptions
import org.apache.spark.sql.DataFrame

val df: DataFrame = /* your DataFrame */

val writeOpts = new SparkHPCCWriteOptions()
  .setFileName("~thor::examples::spark_write_demo")
  .setTargetCluster("mythor")
  .setRecordDef("STRING name; INTEGER age;")  // ECL layout to create
  .setOverwrite(true)

HpccFileWriter.saveToHPCC(df, writeOpts)