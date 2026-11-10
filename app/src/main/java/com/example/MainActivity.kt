package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.local.AppSettings
import com.example.ui.components.AppBottomBar
import com.example.ui.components.AppTopBar
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LanguageController
import com.example.ui.localization.ProvideAppLanguage
import com.example.ui.navigation.NavTab
import com.example.ui.screens.BusinessScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SellScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StockScreen
import com.example.ui.theme.HardwareManagerTheme

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.business.BusinessViewModel
import com.example.ui.screens.business.SuppliersScreen

import com.example.data.local.ProfileManager
import com.example.ui.screens.setup.FirstRunSetupScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSettings.init(this)
        com.example.data.local.UnitManager.init(this)
        com.example.data.local.ProfileManager.init(this)
        com.example.data.local.PinLockManager.init(this)
        com.example.data.local.NotificationManager.init(this)
        com.example.data.local.PartyRateManager.init(this)
        enableEdgeToEdge()
        setContent {
            HardwareManagerTheme {
                ProvideAppLanguage {
                    HardwareManagerApp()
                }
            }
        }
    }
}

@Composable
fun HardwareManagerApp() {
    var isFirstRunCompleted by remember { mutableStateOf(ProfileManager.isFirstRunSetupCompleted) }
    val currentLanguage = LanguageController.currentLanguage

    if (!isFirstRunCompleted) {
        FirstRunSetupScreen(
            currentLanguage = currentLanguage,
            onSetupComplete = {
                isFirstRunCompleted = true
            }
        )
        return
    }

    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var showBakiScreen by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }
    var showSuppliersScreen by remember { mutableStateOf(false) }
    var showPartiesScreen by remember { mutableStateOf(false) }
    var showPurchasesScreen by remember { mutableStateOf(false) }
    var showReportsScreen by remember { mutableStateOf(false) }
    var showMarketplaceScreen by remember { mutableStateOf(false) }

    val isSubScreenOpen = showSettingsScreen || showBakiScreen || showSuppliersScreen ||
            showPartiesScreen || showPurchasesScreen || showReportsScreen || showMarketplaceScreen

    // Android back handler to return from sub-screens or to Home tab
    BackHandler(enabled = isSubScreenOpen || currentTab != NavTab.HOME) {
        when {
            showMarketplaceScreen -> showMarketplaceScreen = false
            showSettingsScreen -> showSettingsScreen = false
            showBakiScreen -> showBakiScreen = false
            showSuppliersScreen -> showSuppliersScreen = false
            showPartiesScreen -> showPartiesScreen = false
            showPurchasesScreen -> showPurchasesScreen = false
            showReportsScreen -> showReportsScreen = false
            else -> currentTab = NavTab.HOME
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                title = when {
                    showMarketplaceScreen -> {
                        if (currentLanguage == AppLanguage.BANGLA) "দোকান মাল্টি-ভেন্ডর মার্কেটপ্লেস" else "Dokan Marketplace Hub"
                    }
                    showSettingsScreen -> {
                        if (currentLanguage == AppLanguage.BANGLA) "সেটিংস ও ডাটা ব্যাকআপ" else "Settings & Backup"
                    }
                    showBakiScreen -> {
                        if (currentLanguage == AppLanguage.BANGLA) "বাকির খাতা" else "Customer Ledger"
                    }
                    showSuppliersScreen -> {
                        if (currentLanguage == AppLanguage.BANGLA) "মহাজন ও সাপ্লায়ার খাতা" else "Suppliers Ledger"
                    }
                    showPartiesScreen -> {
                        if (currentLanguage == AppLanguage.BANGLA) "পার্টি তালিকা (গ্রাহক ও মহাজন)" else "Parties Ledger"
                    }
                    showPurchasesScreen -> {
                        if (currentLanguage == AppLanguage.BANGLA) "ক্রয় খাতা ও চালান" else "Purchases & Invoices"
                    }
                    showReportsScreen -> {
                        if (currentLanguage == AppLanguage.BANGLA) "ব্যবসায়িক রিপোর্ট ও হেলথ চেক" else "Reports & Health Check"
                    }
                    else -> currentTab.title(currentLanguage)
                },
                currentLanguage = currentLanguage,
                onLanguageChange = { newLang ->
                    LanguageController.setLanguage(newLang)
                },
                canNavigateBack = isSubScreenOpen || currentTab != NavTab.HOME,
                onNavigateBack = {
                    when {
                        showMarketplaceScreen -> showMarketplaceScreen = false
                        showSettingsScreen -> showSettingsScreen = false
                        showBakiScreen -> showBakiScreen = false
                        showSuppliersScreen -> showSuppliersScreen = false
                        showPartiesScreen -> showPartiesScreen = false
                        showPurchasesScreen -> showPurchasesScreen = false
                        showReportsScreen -> showReportsScreen = false
                        else -> currentTab = NavTab.HOME
                    }
                },
                onOpenSettings = {
                    showMarketplaceScreen = false
                    showBakiScreen = false
                    showSuppliersScreen = false
                    showPartiesScreen = false
                    showPurchasesScreen = false
                    showReportsScreen = false
                    showSettingsScreen = true
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    showMarketplaceScreen = false
                    showSettingsScreen = false
                    showBakiScreen = false
                    showSuppliersScreen = false
                    showPartiesScreen = false
                    showPurchasesScreen = false
                    showReportsScreen = false
                    currentTab = tab
                },
                currentLanguage = currentLanguage
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                showMarketplaceScreen -> {
                    com.example.ui.screens.marketplace.MarketplaceScreen(
                        currentLanguage = currentLanguage,
                        onBack = { showMarketplaceScreen = false },
                        onNavigateToSell = {
                            showMarketplaceScreen = false
                            currentTab = NavTab.SELL
                        }
                    )
                }
                showSettingsScreen -> {
                    SettingsScreen(
                        currentLanguage = currentLanguage,
                        onBack = { showSettingsScreen = false }
                    )
                }
                showBakiScreen -> {
                    com.example.ui.screens.baki.BakiScreen(
                        currentLanguage = currentLanguage,
                        onBack = { showBakiScreen = false }
                    )
                }
                showSuppliersScreen -> {
                    val businessVm: BusinessViewModel = viewModel()
                    SuppliersScreen(
                        viewModel = businessVm,
                        currentLanguage = currentLanguage,
                        onBackToDashboard = { showSuppliersScreen = false }
                    )
                }
                showPartiesScreen -> {
                    com.example.ui.screens.parties.PartiesScreen(
                        currentLanguage = currentLanguage,
                        onBack = { showPartiesScreen = false }
                    )
                }
                showPurchasesScreen -> {
                    com.example.ui.screens.purchases.PurchasesScreen(
                        currentLanguage = currentLanguage,
                        onBack = { showPurchasesScreen = false }
                    )
                }
                showReportsScreen -> {
                    com.example.ui.screens.reports.BusinessReportsScreen(
                        currentLanguage = currentLanguage,
                        onNavigateToBaki = {
                            showReportsScreen = false
                            showBakiScreen = true
                        },
                        onNavigateToSuppliers = {
                            showReportsScreen = false
                            showSuppliersScreen = true
                        },
                        onBack = { showReportsScreen = false }
                    )
                }
                else -> {
                    Crossfade(
                        targetState = currentTab,
                        label = "tab_crossfade"
                    ) { tab ->
                        when (tab) {
                            NavTab.HOME -> HomeScreen(
                                currentLanguage = currentLanguage,
                                onNavigateToSell = { currentTab = NavTab.SELL },
                                onNavigateToStock = { currentTab = NavTab.STOCK },
                                onNavigateToBaki = { showBakiScreen = true },
                                onNavigateToSuppliers = { showSuppliersScreen = true },
                                onNavigateToHistory = { currentTab = NavTab.HISTORY },
                                onNavigateToBusiness = { currentTab = NavTab.BUSINESS },
                                onNavigateToSettings = { showSettingsScreen = true },
                                onNavigateToMarketplace = { showMarketplaceScreen = true }
                            )
                            NavTab.SELL -> SellScreen(currentLanguage = currentLanguage)
                            NavTab.STOCK -> StockScreen(currentLanguage = currentLanguage)
                            NavTab.HISTORY -> HistoryScreen(currentLanguage = currentLanguage)
                            NavTab.BUSINESS -> BusinessScreen(
                                currentLanguage = currentLanguage,
                                onNavigateToBaki = { showBakiScreen = true },
                                onNavigateToReports = { showReportsScreen = true }
                            )
                            NavTab.MORE -> com.example.ui.screens.MoreScreen(
                                currentLanguage = currentLanguage,
                                onNavigateToBaki = { showBakiScreen = true },
                                onNavigateToSuppliers = { showSuppliersScreen = true },
                                onNavigateToParties = { showPartiesScreen = true },
                                onNavigateToPurchases = { showPurchasesScreen = true },
                                onNavigateToReports = { showReportsScreen = true },
                                onNavigateToSettings = { showSettingsScreen = true },
                                onNavigateToBusiness = { currentTab = NavTab.BUSINESS },
                                onNavigateToStock = { currentTab = NavTab.STOCK },
                                onNavigateToSell = { currentTab = NavTab.SELL },
                                onNavigateToMarketplace = { showMarketplaceScreen = true }
                            )
                        }
                    }
                }
            }
        }
    }
}
