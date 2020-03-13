package modac.coingame.ui.attend.data

//data class A (
//    val userList :~~~~
//    val question : String
//)


data class Attendees(
    val userID : String,
    val userNickname: String,
    val queryUser : Boolean,
    val king : Boolean
)