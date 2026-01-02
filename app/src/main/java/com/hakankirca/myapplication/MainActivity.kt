package com.hakankirca.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hakankirca.myapplication.presentation.add_edit_contact.AddEditContactScreen
import com.hakankirca.myapplication.presentation.contact_detail.ContactDetailScreen
import com.hakankirca.myapplication.presentation.contacts.ContactsScreen
import com.hakankirca.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "contacts_screen"
                ) {

                    // 1. LİSTE EKRANI
                    composable(
                        route = "contacts_screen",
                        exitTransition = {
                            // Detaya giderken liste hafifçe sola kaysın (Opsiyonel, daha doğal durur)
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300))
                        },
                        popEnterTransition = {
                            // Detaydan geri dönünce liste soldan geri gelsin
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
                        }
                    ) {
                        ContactsScreen(
                            onNavigateToAddContact = {
                                navController.navigate("add_edit_screen")
                            },
                            onNavigateToDetail = { id ->
                                navController.navigate("detail_screen/$id")
                            }
                        )
                    }

                    // 2. DETAY EKRANI (ALTTAN YUKARI KAYACAK - KART EFEKTİ)
                    composable(
                        route = "detail_screen/{contactId}",
                        arguments = listOf(navArgument("contactId") { type = NavType.StringType }),
                        enterTransition = {
                            // ALTTAN YUKARI GİRİŞ (SLIDE UP)
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                animationSpec = tween(400)
                            )
                        },
                        exitTransition = {
                            // Başka bir ekrana (örn: edit) giderse sola kaysın
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300))
                        },
                        popEnterTransition = {
                            // Geri gelirse soldan girsin
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300))
                        },
                        popExitTransition = {
                            // YUKARIDAN AŞAĞI ÇIKIŞ (SLIDE DOWN) - Geri tuşuna basınca
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                                animationSpec = tween(400)
                            )
                        }
                    ) { backStackEntry ->
                        val contactId = backStackEntry.arguments?.getString("contactId") ?: ""

                        ContactDetailScreen(
                            contactId = contactId,
                            onNavigateUp = {
                                navController.popBackStack()
                            }
                        )
                    }


                    // 3. EKLEME / DÜZENLEME EKRANI (BU DA ALTTAN GELEBİLİR)
                    composable(
                        route = "add_edit_screen?contactId={contactId}",
                        arguments = listOf(
                            navArgument("contactId") {
                                type = NavType.StringType
                                defaultValue = "-1"
                            }
                        ),
                        enterTransition = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(400))
                        },
                        popExitTransition = {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(400))
                        }
                    ) {
                        AddEditContactScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}