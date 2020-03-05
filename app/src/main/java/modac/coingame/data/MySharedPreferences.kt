package modac.coingame.data

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {

    val PREFS_FILENAME = "prefs"
    val PREF_NICK = "user_nick"
    val PREF_USER_ID = "user_id"
    val PREF_ROOM = "room_data"

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME,0)
    val editor = prefs.edit()
    /* 파일 이름과 EditText를 저장할 Key 값을 만들고 prefs 인스턴스 초기화 */


    /* get/set 함수 임의 설정. get 실행 시 저장된 값을 반환하며 default 값은 false
     * set(value) 실행 시 value로 값을 대체한 후 저장 */

    var user_nick : String?
        get() = prefs.getString(PREF_NICK,null)
        set(value) = editor.putString(PREF_NICK,value).apply()

    var room_data : String?
        get() = prefs.getString(PREF_ROOM,null)
        set(value) = editor.putString(PREF_ROOM,value).apply()

    var user_id : String?
        get() = prefs.getString(PREF_USER_ID,null)
        set(value) = editor.putString(PREF_USER_ID,value).apply()
}