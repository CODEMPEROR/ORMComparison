package com.codemperor.ormcomparsion.greendao

import android.content.Context
import android.util.Log
import com.codemperor.ormcomparsion.TimeCounter
import com.codemperor.ormcomparsion.model.DaoMaster
import com.codemperor.ormcomparsion.model.Note
import com.codemperor.ormcomparsion.model.NoteDao
import java.util.*

/**
 * Created by feng on 2017/6/21.
 */

class GreenDaoTest private constructor() {

    lateinit var mContext: Context;

    companion object {
        const val TAG: String = "GreenDaoTest"

        lateinit var noteDao: NoteDao

        @JvmStatic
        public fun getInstance(): GreenDaoTest {
            return GreenDaoTest()
        }
    }

    public fun test(ctx: Context) {
        mContext = ctx
        init()
        object : Thread() {
            override fun run() {
                val timeCounter = TimeCounter()
                //                ((TextView)findViewById(R.id.greendao_res)).setText("开始执行");
                for (i in 0..testTimes - 1) {
                    testGreenDAOBatch(timeCounter, batchCount)
                    testGreenDAOSingle(timeCounter, singleCount)
                }
                //                ((TextView)findViewById(R.id.greendao_res)).setText("Test GreenDAO " + testTimes + " times, Average
                // Time : " + timeCounter.toString());
                        Log.i(TAG, "Test GreenDAO " + testTimes + " times, Average Time : " + timeCounter.toString())
            }
        }.start()

    }

    private fun init() {
// init GreenDAO
        if (noteDao == null) {
            val helper = DaoMaster.DevOpenHelper(mContext, "greendao-notes", null)
            val db = helper.writableDatabase
            val daoMaster = DaoMaster(db)
            val daoSession = daoMaster.newSession()
            noteDao = daoSession.noteDao
        }
    }

    private fun testGreenDAOBatch(timeCounter: TimeCounter, count: Int) {
        val affectedRows: Long
        var time: Long

        val noteList = ArrayList<Note>()
        // init data
        for (i in 0..count - 1) {
            val note = Note(null, "title", "comment", Date())
            noteList.add(note)
        }

        Log.i(TAG, "greendao test begin... just wait for result. ")

        // 1. 批量插入
        time = System.currentTimeMillis()
        noteDao.insertInTx(*noteList.toTypedArray())
        time = System.currentTimeMillis() - time
        timeCounter.insertBatchList.add(time)
        Log.i(TAG, "greedDAO —— insert —— batch " + noteList.size + " data, use time " + time + " ms")


        // 2. 批量更新
        for (note in noteList) {
            note.text = "up_batch_title"
            note.comment = "up_batch_comment"
        }
        time = System.currentTimeMillis()
        noteDao.updateInTx(noteList)
        time = System.currentTimeMillis() - time
        timeCounter.updateBatchList.add(time)
        Log.d(TAG, "greedDAO —— update —— batch  $count data, use time $time ms")

        // 3. 批量查询
        time = System.currentTimeMillis()
        val list2 = noteDao.queryBuilder().list()
        time = System.currentTimeMillis() - time
        timeCounter.queryBatchList.add(time)
        Log.i(TAG, "greedDAO —— query —— batch  " + list2.size + " data, use time " + time + " ms")
        Log.i(TAG, "greedDAO batch query data list" + list2)


        // 4. 删除全部
        time = System.currentTimeMillis()
        noteDao.deleteAll()
        time = System.currentTimeMillis() - time
        timeCounter.deleteAllList.add(time)
        Log.d(TAG, "greedDAO —— delete —— all  $count data, use time $time ms")

        // 5. 查询确认是否全部删除
        val list3 = noteDao.queryBuilder().list()
        Log.i(TAG, "greedDAO left data size --------> " + list3.size)
    }


    private fun testGreenDAOSingle(timeCounter: TimeCounter, count: Int) {
        val affectedRows: Long
        var time: Long

        val noteList = ArrayList<Note>()

        // init data
        for (i in 0..count - 1) {
            val note = Note(null, "title", "comment", Date())
            noteList.add(note)
        }

        // 1. 循环插入，每次插入一条数据
        time = System.currentTimeMillis()
        for (note in noteList) {
            noteDao.insert(note)
        }
        time = System.currentTimeMillis() - time
        timeCounter.insertSingleList.add(time)
        Log.i(TAG,
                "greedDAO —— insert —— one-by-one  " + noteList.size + " data, use time " + time + " ms")

        // 2. 循环更新，每次更新一条数据
        time = System.currentTimeMillis()
        for (note in noteList) {
            note.text = "update_title"
            note.comment = "update_comment"
            noteDao.update(note)
        }
        time = System.currentTimeMillis() - time
        timeCounter.updateSingleList.add(time)
        Log.d(TAG, "greedDAO —— update —— one-by-one  $count data, use time $time ms")

        // 3. 循环查询，每次查询一条数据
        time = System.currentTimeMillis()
        for (i in 0..count - 1) {
            noteDao.queryBuilder().where(NoteDao.Properties.Id.eq(1)).list()
        }
        time = System.currentTimeMillis() - time
        timeCounter.querySingleList.add(time)
        Log.i(TAG, "greedDAO —— query —— one-by-one  $count data, use time $time ms")


        // 4. 删除
        time = System.currentTimeMillis()
        noteDao.deleteAll()
        time = System.currentTimeMillis() - time
        timeCounter.deleteAllList.add(time)
        Log.d(TAG, "greedDAO —— delete —— all  $count data, use time $time ms")

        // 5. 查询确认是否全部删除
        val list3 = noteDao.queryBuilder().list()
        Log.i(TAG, "greedDAO left data size --------> " + list3.size)
    }
}
