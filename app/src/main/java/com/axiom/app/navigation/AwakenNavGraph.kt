package com.axiom.app.navigation

/**
 * NAVIGATION HIERARCHY DOCUMENTATION
 *
 * Level 1 (Tab Destinations):
 *  - Home: Screen.Home
 *  - Missions: Screen.Missions (includes Gates & Dungeons)
 *  - Physical: Screen.BodyMap
 *  - Shadows: Screen.ShadowArmy (includes Leagues)
 *  - Hunter: Screen.Profile (includes CharacterStats & SkillTree)
 *
 * Level 2 (Modal/Sheet):
 *  - AddMission, CreateDungeon, DailyCheckin, SystemVoice
 *
 * Level 3 (Deep Detail):
 *  - MissionDetail, DungeonDetail, SkillDetail, CharacterStats
 *
 * Level 4 (Settings/Admin):
 *  - Setup, Premium, WeeklyReview, FinancialCheckpoint, Archive
 */

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.app.Activity
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.axiom.app.presentation.home.HomeScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.axiom.app.data.local.AxiomPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AwakenNavViewModel @Inject constructor(
    val preferences: AxiomPreferences
) : ViewModel()

@Composable
fun AwakenNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: AwakenNavViewModel = hiltViewModel()
) {
    val firstMissionDone by viewModel.preferences.firstMissionDoneFlow
        .collectAsStateWithLifecycle(initialValue = false)
    val blueprintSetupComplete by viewModel.preferences.blueprintSetupCompleteFlow
        .collectAsStateWithLifecycle(initialValue = false)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = {
            slideInVertically { it / 3 } + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(200))
        },
        popExitTransition = {
            slideOutVertically { it / 3 } + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(Screen.Setup.route) {
            com.axiom.app.presentation.setup.LanguageThemeSetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                    // Recreate activity to apply new locale, then splash handles routing
                    (navController.context as? android.app.Activity)?.recreate()
                }
            )
        }
        composable(Screen.Splash.route) {
            // WP-202 routing repair: the LaunchDestination resolved by
            // LaunchRouteResolver (awaits startup readiness, then reads persisted
            // state) is the SOLE authority for the one-shot Splash exit. The
            // stale-at-launch collectAsState flags below are passed for honesty
            // but splashExitRoute deliberately ignores them, so a completed user
            // can never be re-routed to Onboarding by an initialValue=false race.
            com.axiom.app.presentation.onboarding.SplashScreen(
                onDestinationResolved = { resolved ->
                    navController.navigate(
                        splashExitRoute(resolved, firstMissionDone, blueprintSetupComplete)
                    ) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.BlueprintWizard.route) {
            com.axiom.app.presentation.onboarding.blueprint.BlueprintWizardScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.BlueprintWizard.route) { inclusive = true }
                    }
                }
            )
        }
        // Activation is no longer part of the mandatory first-launch path.
        // It stays reachable from Profile ("Save My Progress") for any
        // Hunter who wants to link an email to their anonymous session.
        composable(Screen.Activation.route) {
            com.axiom.app.presentation.activation.ActivationScreen(
                onActivationSuccess = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Onboarding.route) {
            com.axiom.app.presentation.onboarding.OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.AwakeningComplete.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AwakeningComplete.route) {
            com.axiom.app.presentation.onboarding.AwakeningCompleteScreen(
                onBegin = {
                    val nextRoute = if (firstMissionDone) Screen.Home.route else Screen.FirstMission.route
                    navController.navigate(nextRoute) {
                        popUpTo(Screen.AwakeningComplete.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.FirstMission.route) {
            com.axiom.app.presentation.onboarding.FirstMissionScreen(
                onMissionCreatedAndCompleted = {
                    val nextRoute = if (!blueprintSetupComplete) Screen.BlueprintWizard.route else Screen.Home.route
                    navController.navigate(nextRoute) {
                        popUpTo(Screen.FirstMission.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.MainQuest.route) {
            com.axiom.app.presentation.onboarding.MainQuestScreen(
                onFinished = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.MainQuest.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        navigation(startDestination = Screen.Home.route, route = "home_graph") {
            composable(Screen.Home.route) {
                HomeScreen(onNavigate = { route -> navController.navigate(route) })
            }
        }

        navigation(startDestination = Screen.Missions.route, route = "missions_graph") {
            composable(Screen.Missions.route) {
                com.axiom.app.presentation.missions.GatesScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
            composable(Screen.AddMission.route) {
                com.axiom.app.presentation.missions.AddMissionScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.AddMissionForSkill.ROUTE,
                arguments = listOf(navArgument("skillId") { type = NavType.StringType })
            ) { backStackEntry ->
                val skillId = backStackEntry.arguments?.getString("skillId") ?: ""
                com.axiom.app.presentation.missions.AddMissionScreen(
                    onBack = { navController.popBackStack() },
                    prefilledSkillId = Screen.decode(skillId),
                    onMissionCreated = { navController.popBackStack(Screen.SkillTree.route, false) }
                )
            }
            composable(
                route = Screen.MissionDetail.ROUTE,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                com.axiom.app.presentation.missions.MissionDetailScreen(
                    missionId = Screen.decode(id),
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Dungeons.route) {
                com.axiom.app.presentation.dungeon.DungeonsScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
            composable(
                route = Screen.DungeonDetail.ROUTE,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                com.axiom.app.presentation.dungeon.DungeonDetailScreen(
                    dungeonId = Screen.decode(id),
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.CreateDungeon.route) {
                com.axiom.app.presentation.dungeon.CreateDungeonScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        navigation(startDestination = Screen.BodyMap.route, route = "physical_graph") {
            composable(Screen.BodyMap.route) {
                com.axiom.app.presentation.bodymap.BodyMapScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.DailyCheckin.route) {
                com.axiom.app.presentation.habits.DailyCheckinScreen(onBack = { navController.popBackStack() })
            }
        }

        navigation(startDestination = Screen.ShadowArmy.route, route = "shadows_graph") {
            composable(Screen.ShadowArmy.route) {
                com.axiom.app.presentation.shadow.ShadowArmyScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
            composable(Screen.Leagues.route) {
                com.axiom.app.presentation.leagues.LeaguesScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
        }

        navigation(startDestination = Screen.Profile.route, route = "hunter_graph") {
            composable(Screen.Profile.route) {
                com.axiom.app.presentation.profile.ProfileScreen(
                    onNavigateToActivation = {
                        navController.navigate(Screen.Activation.route)
                    },
                    onNavigateToFinancial = {
                        navController.navigate(Screen.FinancialCheckpoint.route)
                    },
                    onNavigateToSkillTree = {
                        navController.navigate(Screen.SkillTree.route)
                    },
                    onNavigateToMainQuest = {
                        navController.navigate(Screen.MainQuest.route)
                    }
                )
            }
            composable(Screen.FinancialCheckpoint.route) {
                com.axiom.app.presentation.financial.FinancialCheckpointScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.CharacterStats.route) {
                com.axiom.app.presentation.profile.CharacterStatsScreen()
            }
            composable(Screen.SkillTree.route) {
                com.axiom.app.presentation.skilltree.SkillTreeScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToAddMission = { skillId ->
                        navController.navigate(Screen.AddMissionForSkill(skillId).route)
                    }
                )
            }
            composable(Screen.SystemVoice.route) {
                com.axiom.app.presentation.systemvoice.SystemVoiceScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
            composable(Screen.Premium.route) {
                com.axiom.app.presentation.premium.PremiumScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.WeeklyReview.route) {
                com.axiom.app.presentation.review.WeeklyReviewScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.WeeklyAnalytics.route) {
                com.axiom.app.presentation.analytics.WeeklyAnalyticsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
            composable(Screen.DecisionFilter.route) {
                com.axiom.app.presentation.decisionfilter.DecisionFilterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(title, color = com.axiom.app.ui.theme.SystemGreen)
    }
}
