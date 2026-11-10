package com.example.uthtodo.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.uthtodo.data.AuthRepository
import com.example.uthtodo.data.DashboardRepository
import com.example.uthtodo.data.TaskRepository
import com.example.uthtodo.data.TodoRepository
import com.example.uthtodo.model.*
import com.example.uthtodo.ui.screens.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import android.util.Log
import com.example.uthtodo.data.TeachingTaskApi

sealed class AppDestination(val route: String) {
    object Splash : AppDestination("splash")

    object Onboarding : AppDestination("onboarding/{pageIndex}") {
        const val PAGE_INDEX = "pageIndex"

        fun createRoute(index: Int) = "onboarding/$index"
    }

    object Login : AppDestination("login")
    object Home : AppDestination("home")
    object Profile : AppDestination("profile")
    object TaskList : AppDestination("tasks")
    object AddTask : AppDestination("add-task")
}

@Composable
fun TodoNavHost(repository: TodoRepository) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()

    val teachingApi = remember {
        TeachingTaskApi()
    }

    LaunchedEffect(teachingApi) {
        try {
            val apiTasks = teachingApi.getTasks()

            Log.d(
                "UTH_API",
                "Lấy API thành công: ${apiTasks.size} công việc"
            )

            apiTasks.forEach { task ->
                Log.d(
                    "UTH_API",
                    "ID=${task.id}, " +
                            "Tên=${task.title}, " +
                            "Hoàn thành=${task.done}"
                )
            }

        } catch (e: Exception) {
            Log.e(
                "UTH_API",
                "Lỗi gọi API: ${e.message}",
                e
            )
        }
    }


    val authRepository = remember(appContext) {
        AuthRepository(appContext)
    }

    val dashboardRepository = remember {
        DashboardRepository()
    }

    val taskRepository = remember {
        TaskRepository()
    }

    val appInfo = remember(repository) {
        repository.getAppInfo()
    }

    val pages = remember(repository) {
        repository.getPages()
    }

    val artwork = remember(repository) {
        repository.getIllustrationData()
    }

    val taskContent = remember { TaskContent() }
    val dashboardContent = remember { DashboardContent() }
    val profileContent = remember { ProfileContent() }
    val loginContent = remember { LoginContent() }

    var currentUser by remember {
        mutableStateOf(authRepository.getCurrentUser())
    }

    var isGuest by rememberSaveable {
        mutableStateOf(false)
    }

    var isLoggingOut by remember {
        mutableStateOf(false)
    }

    var loginState by remember {
        mutableStateOf(LoginUiState())
    }

    val uid = currentUser?.uid

    val owner = uid?.let {
        "user:$it"
    } ?: if (isGuest) {
        "guest"
    } else {
        "none"
    }

    val latestOwner by rememberUpdatedState(owner)

    var reload by remember {
        mutableStateOf(0)
    }

    var feed by remember(owner) {
        mutableStateOf(
            if (isGuest) {
                TaskFeed(
                    tasks = dashboardRepository.getTasks(),
                    isLoading = false
                )
            } else {
                TaskFeed()
            }
        )
    }

    var operationError by remember(owner) {
        mutableStateOf<String?>(null)
    }

    var retryWrite by remember(owner) {
        mutableStateOf<(() -> Unit)?>(null)
    }

    DisposableEffect(owner, reload) {
        var active = true

        val listener = if (uid != null) {
            feed = feed.copy(
                isLoading = true,
                error = null
            )

            taskRepository.observe(uid) { result ->
                if (active && latestOwner == owner) {
                    feed = result
                }
            }
        } else {
            null
        }

        onDispose {
            active = false
            listener?.remove()
        }
    }

    // Giữ thao tác lỗi để thử lại với cùng ID công việc.
    fun submitWrite(action: ((String) -> Unit) -> Unit) {
        val writeOwner = owner

        fun failed(message: String) {
            if (latestOwner == writeOwner) {
                operationError = message
                retryWrite = {
                    submitWrite(action)
                }
            }
        }

        try {
            action(::failed)
        } catch (error: Exception) {
            failed(
                error.localizedMessage ?: taskContent.errorTitle
            )
        }
    }

    val toggleTask: (String) -> Unit = { id ->
        val task = feed.tasks.find {
            it.id == id
        }

        if (task != null) {
            val completed = !task.isCompleted

            val at = if (completed) {
                System.currentTimeMillis()
            } else {
                null
            }

            if (uid == null && isGuest) {
                feed = feed.copy(
                    tasks = feed.tasks.map {
                        if (it.id == id) {
                            it.copy(
                                isCompleted = completed,
                                completedAtMillis = at
                            )
                        } else {
                            it
                        }
                    }
                )
            } else if (uid != null) {
                submitWrite { onError ->
                    taskRepository.setCompleted(
                        uid,
                        task,
                        completed,
                        at,
                        onError
                    )
                }
            }
        }
    }

    val openHome: () -> Unit = {
        navController.navigate(AppDestination.Home.route) {
            popUpTo(navController.graph.id) {
                inclusive = false
            }

            launchSingleTop = true
        }
    }

    val openLogin: () -> Unit = {
        loginState = LoginUiState()

        navController.navigate(AppDestination.Login.route) {
            launchSingleTop = true
        }
    }

    val replaceWithLogin: () -> Unit = {
        loginState = LoginUiState()

        navController.navigate(AppDestination.Login.route) {
            popUpTo(navController.graph.id) {
                inclusive = false
            }

            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash.route
    ) {
        composable(AppDestination.Splash.route) {
            SplashScreen(
                appInfo = appInfo,
                onFinished = {
                    currentUser = authRepository.getCurrentUser()

                    val next = if (
                        currentUser != null || isGuest
                    ) {
                        AppDestination.Home.route
                    } else {
                        AppDestination.Onboarding.createRoute(0)
                    }

                    navController.navigate(next) {
                        popUpTo(AppDestination.Splash.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = AppDestination.Onboarding.route,
            arguments = listOf(
                navArgument(AppDestination.Onboarding.PAGE_INDEX) {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            val index = (
                    entry.arguments?.getInt(
                        AppDestination.Onboarding.PAGE_INDEX
                    ) ?: 0
                    ).coerceIn(pages.indices)

            OnboardingScreen(
                appInfo = appInfo,
                page = pages[index],
                pageIndex = index,
                pageCount = pages.size,
                artwork = artwork,
                onSkip = openLogin,
                onNext = {
                    if (index == pages.lastIndex) {
                        openLogin()
                    } else {
                        navController.navigate(
                            AppDestination.Onboarding.createRoute(
                                index + 1
                            )
                        ) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(AppDestination.Login.route) {
            LoginScreen(
                appInfo = appInfo,
                content = loginContent,
                uiState = loginState,
                onBack = {
                    if (
                        navController.previousBackStackEntry != null
                    ) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(
                            AppDestination.Onboarding.createRoute(0)
                        ) {
                            popUpTo(AppDestination.Login.route) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                },
                onGuestClick = {
                    currentUser = null
                    isGuest = true
                    openHome()
                },
                onDismissError = {
                    loginState = loginState.copy(
                        errorMessage = null
                    )
                },
                onGoogleClick = {
                    if (!loginState.isLoading) {
                        val activity = context.findActivity()

                        if (activity == null) {
                            loginState = LoginUiState(
                                errorMessage =
                                    loginContent.activityError
                            )
                        } else {
                            loginState = LoginUiState(
                                isLoading = true
                            )

                            scope.launch {
                                try {
                                    when (
                                        val result =
                                            authRepository
                                                .signInWithGoogle(
                                                    activity
                                                )
                                    ) {
                                        is SignInResult.Success -> {
                                            currentUser = result.user
                                            isGuest = false

                                            Toast.makeText(
                                                context,
                                                "${loginContent.successMessage}: " +
                                                        result.user.displayName,
                                                Toast.LENGTH_LONG
                                            ).show()

                                            openHome()
                                        }

                                        is SignInResult.Error -> {
                                            loginState = LoginUiState(
                                                errorMessage =
                                                    result.message
                                            )
                                        }

                                        SignInResult.Cancelled -> {
                                            Toast.makeText(
                                                context,
                                                loginContent.cancelledMessage,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } finally {
                                    loginState = loginState.copy(
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }

        composable(AppDestination.Home.route) {
            if (currentUser == null && !isGuest) {
                LaunchedEffect(Unit) {
                    replaceWithLogin()
                }
            } else {
                HomeScreen(
                    user = currentUser,
                    feed = feed,
                    content = dashboardContent,
                    taskContent = taskContent,
                    onProfile = {
                        navController.navigate(
                            AppDestination.Profile.route
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onList = {
                        navController.navigate(
                            AppDestination.TaskList.route
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onAdd = {
                        navController.navigate(
                            AppDestination.AddTask.route
                        )
                    },
                    onRetry = {
                        reload++
                    },
                    onToggleTask = toggleTask
                )
            }
        }

        composable(AppDestination.TaskList.route) {
            if (currentUser == null && !isGuest) {
                LaunchedEffect(Unit) {
                    replaceWithLogin()
                }
            } else {
                TaskListScreen(
                    feed = feed,
                    content = taskContent,
                    dashboard = dashboardContent,
                    onHome = {
                        navController.popBackStack(
                            AppDestination.Home.route,
                            false
                        )
                    },
                    onAdd = {
                        navController.navigate(
                            AppDestination.AddTask.route
                        )
                    },
                    onToggle = toggleTask,
                    onRetry = {
                        reload++
                    }
                )
            }
        }

        composable(AppDestination.AddTask.route) {
            if (currentUser == null && !isGuest) {
                LaunchedEffect(Unit) {
                    replaceWithLogin()
                }
            } else {
                AddTaskScreen(
                    content = taskContent,
                    onCancel = {
                        navController.popBackStack()
                    },
                    onSave = { task ->
                        if (uid == null && isGuest) {
                            feed = feed.copy(
                                tasks = (feed.tasks + task)
                                    .sortedBy {
                                        it.dueAtMillis
                                    }
                            )
                        } else if (uid != null) {
                            submitWrite { onError ->
                                taskRepository.add(
                                    uid,
                                    task,
                                    onError
                                )
                            }
                        }

                        // Banner ở màn trước thể hiện trạng thái đồng bộ.
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(AppDestination.Profile.route) {
            if (currentUser == null && !isGuest) {
                LaunchedEffect(Unit) {
                    replaceWithLogin()
                }
            } else {
                ProfileScreen(
                    user = currentUser,
                    tasks = feed.tasks,
                    content = profileContent,
                    isLoggingOut = isLoggingOut,
                    onBack = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        if (!isLoggingOut) {
                            if (currentUser == null) {
                                isGuest = false
                                replaceWithLogin()
                            } else {
                                isLoggingOut = true

                                scope.launch {
                                    var credentialError = false

                                    try {
                                        authRepository.signOut()
                                    } catch (
                                        error: CancellationException
                                    ) {
                                        throw error
                                    } catch (_: Exception) {
                                        credentialError = true
                                    } finally {
                                        isLoggingOut = false
                                    }

                                    if (
                                        authRepository
                                            .getCurrentUser() == null
                                    ) {
                                        currentUser = null
                                        isGuest = false
                                        replaceWithLogin()

                                        val message =
                                            if (credentialError) {
                                                profileContent
                                                    .credentialWarning
                                            } else {
                                                profileContent
                                                    .logoutSuccess
                                            }

                                        Toast.makeText(
                                            context,
                                            message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            profileContent.logoutError,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    if (operationError != null) {
        AlertDialog(
            onDismissRequest = {
                operationError = null
            },
            title = {
                Text(taskContent.errorTitle)
            },
            text = {
                Text(operationError.orEmpty())
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val action = retryWrite
                        operationError = null
                        action?.invoke()
                    }
                ) {
                    Text(taskContent.retry)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        operationError = null
                    }
                ) {
                    Text(taskContent.close)
                }
            }
        )
    }
}

private fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}