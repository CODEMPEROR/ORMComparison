package com.codemperor.ormcomparsion.dbflow

import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration
import com.raizlabs.android.dbflow.sql.migration.BaseMigration
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper

/**
 * Created by feng on 2017/6/22.
 */

@Migration(version = 2, database = AppDatabase::class)
class MyMigration : AlterTableMigration<NoteDBFlow> {

    constructor(table: Class<NoteDBFlow>?) : super(table)

    override fun onPreMigrate() {
        super.onPreMigrate()
        addColumn(SQLiteType.INTEGER, "time")
    }
}