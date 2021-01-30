package com.bosslur.mtagv2.hawkstorage

import android.content.Context
import com.bosslur.mtagv2.model.User
import com.orhanobut.hawk.Hawk

class HawkStorage {
    companion object{
        private const val USER_KEY = "user_key"
        private const val TOKEN_KEY = "token_key"
        private val hawkstorage = HawkStorage()

        fun instance(context: Context?):HawkStorage{
            Hawk.init(context).build()
            return hawkstorage
        }
    }

    fun setUser(user: User){
        Hawk.put(USER_KEY,user)
    }

    fun getUser():User{
        return Hawk.get(USER_KEY)
    }

    fun setToken(token: String){
        Hawk.put(TOKEN_KEY,token)
    }

    fun getToken(): String{
        val rawToken =  Hawk.get<String>(TOKEN_KEY)
        val token = rawToken.split("|")
        return token[1]
    }

    fun isLogin():Boolean{
        if(Hawk.contains(USER_KEY)){
            return true
        }
        return false
    }

    fun deleteAll(){
        Hawk.deleteAll()
    }
}