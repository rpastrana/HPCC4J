## `kb/hpcc4j/spark-hpcc-connector/sparkhpccwriteoptions.md`

```markdown
# SparkHPCCWriteOptions — What to set and why

Use this class to control how Spark writes a `DataFrame` to HPCC Thor.

---

## Core options

- **fileName** — Logical file name
- **targetCluster** — Thor cluster name
- **recordDef** — ECL layout
- **overwrite** — Replace existing file

### Scala

```scala
new SparkHPCCWriteOptions()
  .setFileName("~thor::examples::demo")
  .setTargetCluster("mythor")
  .setRecordDef("STRING name; INTEGER age;")
  .setOverwrite(true)