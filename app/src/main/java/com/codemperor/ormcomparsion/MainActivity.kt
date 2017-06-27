package com.codemperor.ormcomparsion

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.codemperor.ormcomparsion.dbflow.DBFlowTest
import com.codemperor.ormcomparsion.greendao.GreenDaoTest

class MainActivity : Activity(), View.OnClickListener {

    private var mGreenDaoBtn: Button? = null
    private var mOrmLiteBtn: Button? = null
    private var mDbFlowBtn: Button? = null
    private var mGreenDaoRes: TextView? = null
    private var mOrmLiteRes: TextView? = null
    private var mDbFlowRes: TextView? = null

    inner class CallbackHandler : Handler() {
        override fun handleMessage(msg: Message) {
            Log.i(TAG, msg.obj as String?);
            when (msg.what) {
                MSG_WHAT_GREENDAO -> mGreenDaoRes!!.text = msg.obj as String
                MSG_WHAT_ORMLITE -> mOrmLiteRes!!.text = msg.obj as String
                MSG_WHAT_DBFLOW -> mDbFlowRes!!.text = msg.obj as String
            }
        }
    }

    private val mHandler = CallbackHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mGreenDaoBtn = findViewById(R.id.green_dao_btn) as Button
        mOrmLiteBtn = findViewById(R.id.orm_lite_btn) as Button
        mDbFlowBtn = findViewById(R.id.db_flow_btn) as Button
        mGreenDaoBtn!!.setOnClickListener(this)
        mOrmLiteBtn!!.setOnClickListener(this)
        mDbFlowBtn!!.setOnClickListener(this)

        mGreenDaoRes = findViewById(R.id.greendao_res) as TextView
        mOrmLiteRes = findViewById(R.id.ormLite_res) as TextView
        mDbFlowRes = findViewById(R.id.db_flow_res) as TextView
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.green_dao_btn -> GreenDaoTest.getInstance().test(this@MainActivity, mHandler)

            R.id.orm_lite_btn -> OrmLite.getInstance().test(this@MainActivity, mHandler)

            R.id.db_flow_btn -> DBFlowTest.getInstance().test(this@MainActivity, mHandler)
        }
    }

    companion object {
        private val TAG = "MainActivity"

        val testTimes = 10 // 整体测试次数，取其均值。
        val batchCount = 10000 // 1万条数据 测试批量
        val singleCount = 1000 // 1千条数据 测试循环单个操作


        val MSG_WHAT_GREENDAO = 1
        val MSG_WHAT_ORMLITE = 2
        val MSG_WHAT_DBFLOW = 3
    }

}
