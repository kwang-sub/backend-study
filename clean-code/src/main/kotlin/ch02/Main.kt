package ch02

import java.util.logging.Logger


fun main() {
    val logger = Logger.getLogger("main")

    val teamList = listOf(
        Team.create("my team one"),
        Team.create("my team two"),
    )
    val user = User.create("kwang", 10, teamList)
    user.login()

//
//    user.teamList
//        .forEach { team ->
//            println("my team name $team")
//        }
//
//    user.teamList
//        .forEach { println("my team name $it") }




    val myTeamUserNamePairList = user.getMyTeamUserNamePairList()

    println(myTeamUserNamePairList)

}

