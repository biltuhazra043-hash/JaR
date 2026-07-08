package com.jarvis.ai.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val temperature: Int = 28,
    val weatherCondition: String = "Partly Cloudy",
    val batteryLevel: Int = 85,
    val ramUsed: String = "4.2 GB",
    val storageUsed: String = "64 GB",
    val cpuUsage: String = "32%",
    val isNetworkAvailable: Boolean = true,
    val upcomingEvents: List<String> = emptyList(),
    val reminders: List<String> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // Load dashboard data
        viewModelScope.launch {
            loadDashboardData()
        }
    }

    private suspend fun loadDashboardData() {
        _uiState.update {
            it.copy(
                upcomingEvents = listOf(
                    "Team meeting at 3:00 PM",
                    "Dinner reservation at 7:30 PM"
                ),
                reminders = listOf(
                    "Pick up groceries",
                    "Call dentist tomorrow",
                    "Submit report by Friday"
                )
            )
        }
    }
}
