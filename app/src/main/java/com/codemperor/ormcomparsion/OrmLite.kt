package com.codemperor.ormcomparsion

import android.content.Context
import android.util.Log
import com.codemperor.ormcomparsion.MainActivity.*
import com.codemperor.ormcomparsion.MainActivity.Companion.batchCount
import com.codemperor.ormcomparsion.MainActivity.Companion.singleCount
import com.codemperor.ormcomparsion.MainActivity.Companion.testTimes
import com.codemperor.ormcomparsion.model.Note
import com.litesuits.orm.LiteOrm
import java.util.*

/**
 * Created by feng on 2017/6/21.
 */

class OrmLite private constructor() {
    lateinit private var liteOrm: LiteOrm
    private var TAG = "OrmLite"

    companion object {
        @JvmStatic
        fun getInstance(): OrmLite {
            return OrmLite();
        }
    }

    fun test(ctx: Context, handler: CallbackHandler) {
        init(ctx)
        object : Thread() {
            override fun run() {
                val timeCounter = TimeCounter()
                for (i in 0..testTimes - 1) {
                    testLiteOrmBatch(timeCounter, batchCount)
                    testLiteOrmSingle(timeCounter, singleCount)
                }
                val res = "Test LiteOrm " + testTimes + " times, Average Time : " + timeCounter.toString()
                Log.i(TAG, res)
                var msg = handler.obtainMessage();
                msg.what = MainActivity.MSG_WHAT_ORMLITE
                msg.obj = res
                handler.sendMessage(msg)
            }
        }.start()
    }

    fun init(ctx: Context) {
        liteOrm = LiteOrm.newSingleInstance(ctx, "liteorm-notes")
    }

    private fun testLiteOrmBatch(timeCounter: TimeCounter, count: Int) {
        var affectedRows: Long
        var time: Long

        val noteList = ArrayList<Note>()

        // init data
        for (i in 0..count - 1) {
            val note = Note(null, "title", "comment", Date())
            noteList.add(note)
        }

        Log.i(TAG, "lite-orm test begin... just wait for result. ")

        // 1. 批量插入
        time = System.currentTimeMillis()
        affectedRows = liteOrm.insert(noteList).toLong()
        time = System.currentTimeMillis() - time
        timeCounter.insertBatchList.add(time)
        Log.i(TAG, "lite-orm —— insert —— batch $affectedRows data, use time $time ms")

        // 2. 批量更新
        for (note in noteList) {
            note.text = "up_batch_title"
            note.comment = "up_batch_comment"
        }
        time = System.currentTimeMillis()
        affectedRows = liteOrm.update(noteList).toLong()
        time = System.currentTimeMillis() - time
        timeCounter.updateBatchList.add(time)
        Log.d(TAG, "lite-orm —— update —— batch $affectedRows data, use time $time ms")

        // 3. 批量查询数据
        time = System.currentTimeMillis()
        val list2 = liteOrm.query<Note>(Note::class.java)
        time = System.currentTimeMillis() - time
        timeCounter.queryBatchList.add(time)
        Log.i(TAG, "lite-orm —— query —— batch " + list2.size + " data, use time " + time + " ms")
        Log.i(TAG, "lite-orm —— query —— batch " + list2)


        // 4. 删除数据
        time = System.currentTimeMillis()
        liteOrm.deleteAll<Note>(Note::class.java)
        time = System.currentTimeMillis() - time
        timeCounter.deleteAllList.add(time)
        Log.d(TAG, "lite-orm —— delete —— all $count data, use time $time ms")

        // 5. 查询确认是否全部删除
        val list3 = liteOrm.query<Note>(Note::class.java)
        Log.i(TAG, "lite-orm left data size --------> " + list3.size)
    }

    private fun testLiteOrmSingle(timeCounter: TimeCounter, count: Int) {
        val affectedRows: Long
        var time: Long

        val noteList = ArrayList<Note>()

        // init data
        for (i in 0..count - 1) {
            val note = Note(null, "title", "comment", Date())
            noteList.add(note)
        }

        Log.i(TAG, "lite-orm test begin... just wait for result. ")

        // 1. 循环插入，每次插入一条数据
        time = System.currentTimeMillis()
        for (note in noteList) {
            liteOrm.insert(note)
        }
        time = System.currentTimeMillis() - time
        timeCounter.insertSingleList.add(time)
        Log.i(TAG,
                "lite-orm —— insert —— one-by-one " + noteList.size + " data, use time " + time + " ms")


        // 2. 循环更新，每次更新一条数据
        time = System.currentTimeMillis()
        for (note in noteList) {
            note.text = "update_title"
            note.comment = "update_comment"
            liteOrm.update(note)
        }
        time = System.currentTimeMillis() - time
        timeCounter.updateSingleList.add(time)
        Log.d(TAG, "lite-orm —— update —— one-by-one $count data, use time $time ms")

        // 3. 循环查询数据，每次查询一条数据
        time = System.currentTimeMillis()
        for (i in 0..count - 1) {
            liteOrm.queryById<Note>(1, Note::class.java)
        }
        time = System.currentTimeMillis() - time
        timeCounter.querySingleList.add(time)
        Log.i(TAG, "lite-orm —— query —— one-by-one $count data, use time $time ms")

        // 4. 删除数据
        time = System.currentTimeMillis()
        liteOrm.deleteAll<Note>(Note::class.java)
        time = System.currentTimeMillis() - time
        timeCounter.deleteAllList.add(time)
        Log.d(TAG, "lite-orm —— delete —— all $count data, use time $time ms")

        // 5. 查询确认是否全部删除
        val list3 = liteOrm.query<Note>(Note::class.java)
        Log.i(TAG, "lite-orm left data size --------> " + list3.size)
    }

}
