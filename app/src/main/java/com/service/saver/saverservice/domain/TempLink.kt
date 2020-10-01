package com.service.saver.saverservice.domain

import java.io.Serializable
import java.util.*

class UserLink : Serializable {
    var id: Number = -1;
    var username: String = ""
    var avatar_url: String = ""
    var createDate: Date = Date()

    constructor(id: Number, username: String, avatar_url: String, createDate: Date) {
        this.id = id
        this.username = username
        this.avatar_url = avatar_url
        this.createDate = createDate
    }

    constructor()


    override fun toString(): String {
        return "$username"
    }


    companion object {
        const val TABLE_NAME = "userlink";
        const val TABLE_CREATE = "create table ${TABLE_NAME}(id INTEGER PRIMARY KEY,  username text UNIQUE, avatar_url text, createDate date)"
        const val DROP_TABLE = "drop table if exists ${TABLE_NAME}"
    }
}