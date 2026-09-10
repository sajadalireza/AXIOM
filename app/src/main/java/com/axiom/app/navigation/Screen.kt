package com.axiom.app.navigation

sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Splash : Screen("splash")
    object Activation : Screen("activation")
    object Onboarding : Screen("onboarding")
    object AwakeningComplete : Screen("awakening_complete")
    object Home : Screen("home")
    object Missions : Screen("missions")
    object AddMission : Screen("add_mission")
    data class AddMissionForSkill(val skillId: String) : Screen("add_mission/${encode(skillId)}") {
        companion object {
            const val ROUTE = "add_mission/{skillId}"
        }
    }
    data class MissionDetail(val id: String) : Screen("mission_detail/${encode(id)}") {
        companion object {
            const val ROUTE = "mission_detail/{id}"
        }
    }
    object Dungeons : Screen("dungeons")
    data class DungeonDetail(val id: String) : Screen("dungeon_detail/${encode(id)}") {
        companion object {
            const val ROUTE = "dungeon_detail/{id}"
        }
    }
    object CreateDungeon : Screen("create_dungeon")
    object SkillTree : Screen("skill_tree")
    object ShadowArmy : Screen("shadow_army")
    object Profile : Screen("profile")
    object CharacterStats : Screen("character_stats")
    object MainQuest : Screen("main_quest")
    object SystemVoice : Screen("system_voice")
    object Premium : Screen("premium")
    object Leagues : Screen("leagues")
    object FirstMission : Screen("first_mission")
    object FirstWin : Screen("first_win")
    object BlueprintWizard : Screen("blueprint_wizard")
    object BodyMap : Screen("body_map")
    object DailyCheckin : Screen("daily_checkin")
    object WeeklyAnalytics : Screen("weekly_analytics")
    object FinancialCheckpoint : Screen("financial_checkpoint")
    object WeeklyReview : Screen("weekly_review")
    object DecisionFilter : Screen("decision_filter")

    companion object {
        fun encode(value: String): String = java.net.URLEncoder.encode(value, "UTF-8")
        fun decode(value: String): String = java.net.URLDecoder.decode(value, "UTF-8")
    }
}
