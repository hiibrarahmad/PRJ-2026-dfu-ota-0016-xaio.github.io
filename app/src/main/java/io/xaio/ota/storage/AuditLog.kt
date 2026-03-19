package io.xaio.ota.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.xaio.ota.AppConfig
import io.xaio.ota.model.AuditEntry
import java.io.File

class AuditLog(context: Context) {

    private val db: SQLiteDatabase

    init {
        val helper = object : SQLiteOpenHelper(context, AppConfig.auditDbName, null, 1) {
            override fun onCreate(db: SQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE audit_log (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        timestamp INTEGER NOT NULL,
                        device_id TEXT NOT NULL,
                        from_version TEXT NOT NULL,
                        to_version TEXT NOT NULL,
                        from_channel TEXT NOT NULL,
                        to_channel TEXT NOT NULL,
                        direction TEXT NOT NULL,
                        epoch_from INTEGER NOT NULL,
                        epoch_to INTEGER NOT NULL,
                        result TEXT NOT NULL,
                        reason TEXT NOT NULL
                    )
                    """.trimIndent(),
                )
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
        }
        db = helper.writableDatabase
    }

    fun record(entry: AuditEntry) {
        val values = ContentValues().apply {
            put("timestamp", entry.timestamp)
            put("device_id", entry.deviceId)
            put("from_version", entry.fromVersion)
            put("to_version", entry.toVersion)
            put("from_channel", entry.fromChannel)
            put("to_channel", entry.toChannel)
            put("direction", entry.direction)
            put("epoch_from", entry.epochFrom)
            put("epoch_to", entry.epochTo)
            put("result", entry.result)
            put("reason", entry.reason)
        }
        db.insert("audit_log", null, values)
    }

    fun exportCsv(outputFile: File) {
        val cursor = db.query("audit_log", null, null, null, null, null, "timestamp DESC")
        outputFile.bufferedWriter().use { writer ->
            writer.write("timestamp,device_id,from_version,to_version,from_channel,to_channel,direction,epoch_from,epoch_to,result,reason\n")
            while (cursor.moveToNext()) {
                val row = listOf(
                    cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                    cursor.getString(cursor.getColumnIndexOrThrow("device_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("from_version")),
                    cursor.getString(cursor.getColumnIndexOrThrow("to_version")),
                    cursor.getString(cursor.getColumnIndexOrThrow("from_channel")),
                    cursor.getString(cursor.getColumnIndexOrThrow("to_channel")),
                    cursor.getString(cursor.getColumnIndexOrThrow("direction")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("epoch_from")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("epoch_to")),
                    cursor.getString(cursor.getColumnIndexOrThrow("result")),
                    cursor.getString(cursor.getColumnIndexOrThrow("reason")),
                )
                writer.write(row.joinToString(",") { "\"${it.toString().replace("\"", "\"\"")}\"" })
                writer.write("\n")
            }
        }
        cursor.close()
    }
}

