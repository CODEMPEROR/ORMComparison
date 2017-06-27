package com.codemperor.ormcomparsion.dbflow

import android.content.Context
import com.codemperor.ormcomparsion.MainActivity
import com.codemperor.ormcomparsion.TimeCounter
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.Delete
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.language.Select
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction
import java.util.*

/**
 * Created by feng on 2017/6/21.
 */

class DBFlowTest private constructor() {
    companion object {
        @JvmStatic
        fun getInstance(): DBFlowTest {
            return DBFlowTest()
        }
    }

    fun test(ctx: Context, handler: MainActivity.CallbackHandler) {
        init(ctx)
        object : Thread() {
            override fun run() {
                val timeCounter = TimeCounter()
                for (i in 0..MainActivity.testTimes - 1) {
                    batch(timeCounter, MainActivity.batchCount)
//                    single(timeCounter, MainActivity.singleCount)
                }
                val res = "Test DBFlow" + MainActivity.testTimes + " times, Average Time : " + timeCounter.toString()
                val msg = handler.obtainMessage()
                msg.what = MainActivity.MSG_WHAT_DBFLOW;
                msg.obj = res
                handler.sendMessage(msg)
            }
        }.start()
    }

    fun init(ctx: Context) {
        FlowManager.init(ctx)
    }

    fun batch(timeCounter: TimeCounter, count: Int) {
        val noteList = ArrayList<NoteDBFlow>()
        val affectedRows: Long
        var time: Long

        for (i in 0..count - 1) {
            val note = NoteDBFlow(i.toLong(), "title", "comment", Date())
            noteList.add(note)
        }

        // 1. 批量插入
        time = System.currentTimeMillis()

        val transaction = ProcessModelTransaction.Builder<NoteDBFlow>(ProcessModelTransaction.ProcessModel { model, wrapper ->
            model.insert()
        }).addAll(noteList).build()

        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction(transaction)

        time = System.currentTimeMillis() - time
        timeCounter.insertBatchList.add(time)

        //2. 批量更新

        time = System.currentTimeMillis()

        var row = SQLite.update(NoteDBFlow::class.java)
                .set(NoteDBFlow_Table.text.eq("up_batch_title"),
                        NoteDBFlow_Table.comment.eq("up_batch_comment"))
                .executeUpdateDelete()

        time = System.currentTimeMillis() - time
        timeCounter.updateBatchList.add(time)

        // 3. 批量查询
        time = System.currentTimeMillis()
        val queryList = Select().from(NoteDBFlow::class.java).queryList()
        time = System.currentTimeMillis() - time
        timeCounter.queryBatchList.add(time)

        // 4. 删除全部
        time = System.currentTimeMillis()
        val deleteTrac = ProcessModelTransaction.Builder<NoteDBFlow>(ProcessModelTransaction.ProcessModel { model, wrapper ->
            model.delete()
        }).addAll(noteList).build()

        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction(deleteTrac)

        time = System.currentTimeMillis() - time
        timeCounter.deleteAllList.add(time)

    }

    fun single(timeCounter: TimeCounter, count: Int) {
        val noteList = ArrayList<NoteDBFlow>()
        val affectedRows: Long
        var time: Long

        for (i in 0..count - 1) {
            val note = NoteDBFlow(i.toLong(), "title", "comment", Date())
            noteList.add(note)
        }

        //1. 循环插入
        time = System.currentTimeMillis()
        for (i in 0..count - 1) {
            val note = noteList.get(i)
            note.save()
        }
        time = System.currentTimeMillis() - time
        timeCounter.insertSingleList.add(time)

        //2. 循环更新
        time = System.currentTimeMillis()
        for (note in noteList) {
            note.text = "update_title"
            note.comment = "update_comment"
            note.update()
        }
        time = System.currentTimeMillis() - time
        timeCounter.updateSingleList.add(time)

// 3. 循环查询，每次查询一条数据
        time = System.currentTimeMillis()
        for (i in 0..count - 1) {
            Select().from(NoteDBFlow::class.java).where(NoteDBFlow_Table.id.eq(i.toLong())).query()
        }
        time = System.currentTimeMillis() - time
        timeCounter.querySingleList.add(time)


        // 4. 删除全部
        time = System.currentTimeMillis()
        val deleteTrac = ProcessModelTransaction.Builder<NoteDBFlow>(ProcessModelTransaction.ProcessModel { model, wrapper ->
            model.delete()
        }).addAll(noteList).build()

        FlowManager.getDatabase(AppDatabase::class.java).executeTransaction(deleteTrac)
        time = System.currentTimeMillis() - time

        timeCounter.deleteAllList.add(time)
    }
}