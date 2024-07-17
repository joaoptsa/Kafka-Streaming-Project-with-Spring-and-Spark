from pyspark.sql import SparkSession
from pyspark.sql.functions import  expr,to_timestamp, col, to_json, struct, avg, count, window, current_timestamp, dense_rank,date_format,lit
from pyspark.sql.types import StructType, StructField, StringType, IntegerType,TimestampType
from pyspark.sql.window import Window as SparkWindow
import logging


# Cria uma sessão Spark com configurações de memória
spark = SparkSession.builder \
   .appName("KafkaSparkApp") \
   .config("spark.executor.memory", "1g") \
   .config("spark.driver.memory", "1g") \
   .getOrCreate()

# Define o esquema para os dados JSON
schema = StructType([
    StructField("firstName", StringType(), True),
    StructField("lastName", StringType(), True),
    StructField("country", StringType(), True),
    StructField("gender", StringType(), True),
    StructField("city", StringType(), True),
    StructField("postcode", StringType(), True),
    StructField("email", StringType(), True),
    StructField("age", IntegerType(), True)
])

schema_str = """
    firstName STRING,
    lastName STRING,
    country STRING,
    gender STRING,
    city STRING,
    postcode STRING,
    email STRING,
    age INT
"""

logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s:%(funcName)s:%(levelname)s:%(message)s')
logger = logging.getLogger("spark_structured_streaming")


# Lê dados do Kafka
df = spark.readStream \
   .format("kafka") \
   .option("kafka.bootstrap.servers", "kafka1:19092,kafka2:19093,kafka3:19094") \
   .option("subscribe", "users_data") \
   .option("delimeter",",") \
   .option("startingOffsets", "earliest")\
   .load()

# Adiciona a coluna de timestamp

df = df.withColumn("timestamp",to_timestamp (date_format("timestamp", "yyyy-MM-dd HH:mm:ss")))


# Converte os dados JSON
df = df.selectExpr("CAST(value AS STRING) as json", "timestamp") \
   .selectExpr(f"from_json(json, '{schema_str}') as data", "timestamp") \
   .select("data.*", "timestamp")

# Filtra os dados por idade
filtered_df = df.filter((col("age") > 17) & (col("age") < 60))

# Realiza agregação em janelas de 1 minuto
window_agg_df = filtered_df \
    .withWatermark("timestamp", "2 minute") \
    .groupBy(window(col("timestamp"), "1 minute")) \
    .agg(
        avg("age").alias("average_age")
    )

final_json_df = window_agg_df.selectExpr(
    "CAST(null AS STRING) as key",
    "to_json(struct(*)) as value"
).withColumn("__TypeId__", lit("com.version1.kafka_tests.Kafka"))

final_json_df2 = filtered_df.selectExpr(
    "CAST(null AS STRING) as key",
    "to_json(struct(*)) as value"
).withColumn("__TypeId__", lit("com.version1.kafka_tests.Kafka"))


final_query = final_json_df.writeStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "kafka1:19092,kafka2:19093,kafka3:19094") \
    .option("topic", "aggregated_data") \
    .option("checkpointLocation", "/tmp/checkpoints/final1") \
    .outputMode("update") \
    .start()

final_query2 = final_json_df2.writeStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "kafka1:19092,kafka2:19093,kafka3:19094") \
    .option("topic", "users_select") \
    .option("checkpointLocation", "/tmp/checkpoints/final2") \
    .start()



final_query.awaitTermination()

final_query2.awaitTermination()









###################################################################################################################



