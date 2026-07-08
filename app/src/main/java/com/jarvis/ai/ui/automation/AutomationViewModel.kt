package com.jarvis.ai.ui.automation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.ai.data.db.entity.AutomationEntity
import com.jarvis.ai.data.repository.AutomationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AutomationUiState(
    val id: String,
    val name: String,
    val type: String,
    val isEnabled: Boolean
)

@HiltViewModel
class AutomationViewModel @Inject constructor(
    private val automationRepository: AutomationRepository
) : ViewModel() {

    private val _automations = MutableStateFlow<List<AutomationUiState>>(emptyList())
    val automations: StateFlow<List<AutomationUiState>> = _automations.asStateFlow()

    init {
        viewModelScope.launch {
            automationRepository.getAllAutomations().collect { entities ->
                _automations.value = entities.map {
                    AutomationUiState(
                        id = it.id,
                        name = it.name,
                        type = it.type,
                        isEnabled = it.isEnabled
                    )
                }
            }
        }
    }

    fun activatePreset(type: String) {
        viewModelScope.launch {
            val preset = when (type) {
                "morning" -> AutomationEntity(
                    name = "Morning Routine",
                    type = "time",
                    triggerConfig = """{"time":"07:00","days":["mon","tue","wed","thu","fri"]}""",
                    actions = """["weather","news","calendar","reminders"]"""
                )
                "night" -> AutomationEntity(
                    name = "Night Routine",
                    type = "time",
                    triggerConfig = """{"time":"22:00"}""",
                    actions = """["dnd_on","dim_brightness","tomorrow_summary"]"""
                )
                "charging" -> AutomationEntity(
                    name = "Charging Routine",
                    type = "charging",
                    triggerConfig = """{"state":"charging"}""",
                    actions = """["wifi_sync","backup","check_updates"]"""
                )
                else -> return@launch
            }
            automationRepository.saveAutomation(preset)
        }
    }

    fun toggleAutomation(id: String) {
        viewModelScope.launch {
            val automation = automationRepository.getAutomationById(id) ?: return@launch
            automationRepository.updateAutomation(automation.copy(isEnabled = !automation.isEnabled))
        }
    }

    fun deleteAutomation(id: String) {
        viewModelScope.launch {
            automationRepository.deleteAutomation(id)
        }
    }
}
