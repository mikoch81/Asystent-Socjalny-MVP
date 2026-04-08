package pl.mikoch.asystentsocjalny.core.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import pl.mikoch.asystentsocjalny.core.model.Benefit
import pl.mikoch.asystentsocjalny.core.model.ChecklistStep
import pl.mikoch.asystentsocjalny.core.model.Procedure
import pl.mikoch.asystentsocjalny.core.model.UrgentGuidance
import pl.mikoch.asystentsocjalny.core.model.UrgentScenario

class KnowledgeRepository(private val context: Context) {

    fun loadProcedures(): List<Procedure> {
        val content = readAsset("knowledge/procedures.json")
        val root = JSONObject(content)
        val array = root.getJSONArray("procedures")
        return parseProcedures(array)
    }

    fun loadBenefits(): List<Benefit> {
        val content = readAsset("knowledge/benefits.json")
        val root = JSONObject(content)
        val array = root.getJSONArray("benefits")
        return parseBenefits(array)
    }

    fun loadUrgentScenarios(): List<UrgentScenario> {
        val content = readAsset("knowledge/procedures/urgent_scenarios.json")
        val root = JSONObject(content)
        val array = root.getJSONArray("scenarios")
        return parseUrgentScenarios(array)
    }

    private fun readAsset(path: String): String {
        return context.assets.open(path).bufferedReader().use { it.readText() }
    }

    internal fun parseProcedures(array: JSONArray): List<Procedure> {
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            Procedure(
                id = item.getString("id"),
                title = item.getString("title"),
                situation = item.getString("situation"),
                severity = item.getString("severity"),
                nowSteps = item.getJSONArray("nowSteps").toStringList(),
                notify = item.getJSONArray("notify").toStringList(),
                doNotMiss = item.getJSONArray("doNotMiss").toStringList(),
                legalBasis = item.getJSONArray("legalBasis").toStringList(),
                escalation = item.getString("escalation"),
                documents = item.getJSONArray("documents").toStringList()
            )
        }
    }

    internal fun parseBenefits(array: JSONArray): List<Benefit> {
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            Benefit(
                id = item.getString("id"),
                name = item.getString("name"),
                description = item.getString("description"),
                documents = item.getJSONArray("documents").toStringList(),
                conditions = if (item.has("conditions")) item.getJSONArray("conditions").toStringList() else emptyList(),
                note = item.getString("note")
            )
        }
    }

    internal fun parseUrgentScenarios(array: JSONArray): List<UrgentScenario> {
        return (0 until array.length()).map { index ->
            val obj = array.getJSONObject(index)
            val stepsArray = obj.getJSONArray("steps")
            val guidanceObj = if (obj.has("guidance")) obj.getJSONObject("guidance") else null
            UrgentScenario(
                id = obj.getString("id"),
                title = obj.getString("title"),
                description = obj.getString("description"),
                steps = (0 until stepsArray.length()).map { j ->
                    val step = stepsArray.getJSONObject(j)
                    ChecklistStep(
                        id = step.getString("id"),
                        text = step.getString("text"),
                        isCritical = step.getBoolean("isCritical")
                    )
                },
                guidance = guidanceObj?.let {
                    UrgentGuidance(
                        notify = it.getJSONArray("notify").toStringList(),
                        documents = it.getJSONArray("documents").toStringList(),
                        doNotMiss = it.getJSONArray("doNotMiss").toStringList(),
                        escalationRequired = it.getBoolean("escalationRequired"),
                        escalationNote = it.getString("escalationNote")
                    )
                }
            )
        }
    }
}

private fun JSONArray.toStringList(): List<String> {
    return (0 until length()).map { index -> getString(index) }
}
