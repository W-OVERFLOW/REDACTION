package net.wyvest.redaction.features.hitbox

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import net.wyvest.redaction.Redaction
import java.io.File
import java.util.Locale

object Hitboxes {

    private val GSON = GsonBuilder().setPrettyPrinting().create()
    private val PARSER = JsonParser()
    private val file = File(Redaction.modDir, "hitboxes.json")

    fun initialize() {
        if (!file.exists() || file.readText().isBlank()) {
            file.createNewFile()
            file.writeText("{  }")
        }

        val readJson = PARSER.parse(file.readText()).asJsonObject
        for (entity in Entity.map.values) {
            val entityName = entity.name.lowercase(Locale.ENGLISH).replace(" ", "_")
            if (!readJson.has(entityName)) {
                readJson.add(entityName, PARSER.parse(GSON.toJson(entity)))
            }
        }
        if (!readJson.has("general")) {
            readJson.add("general", PARSER.parse(GSON.toJson(GeneralConfig(hitboxWidth = 1, forceHitbox = false, disableForSelf = false, accurateHitbox = true))))
        }
        file.writeText(GSON.toJson(readJson))

        try {
            val json = PARSER.parse(file.readText()).asJsonObject
            for (entity in Entity.map) {
                val entityJson = json[entity.value.name.lowercase(Locale.ENGLISH).replace(" ", "_")].asJsonObject
                Entity.map[entity.key]?.hitboxEnabled = entityJson["hitbox_enabled"].asBoolean
                Entity.map[entity.key]?.eyeLineEnabled = entityJson["eyeline_enabled"].asBoolean
                Entity.map[entity.key]?.lineEnabled = entityJson["line_enabled"].asBoolean
                Entity.map[entity.key]?.color = entityJson["color"].asInt
                Entity.map[entity.key]?.crosshairColor = entityJson["crosshair_color"].asInt
                Entity.map[entity.key]?.eyeColor = entityJson["eye_color"].asInt
                Entity.map[entity.key]?.lineColor = entityJson["line_color"].asInt
            }
            val generalJson = json["general"].asJsonObject
            GeneralConfig.config = GeneralConfig(
                hitboxWidth = generalJson["hitbox_width"].asInt,
                forceHitbox = generalJson["force_hitbox"].asBoolean,
                disableForSelf = generalJson["disable_for_self"].asBoolean,
                accurateHitbox = generalJson["accurate_hitbox"].asBoolean
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (!file.delete()) {
                file.writeText("")
            }
            initialize()
        }
    }

    fun writeConfig() {
        val json = PARSER.parse(file.readText()).asJsonObject
        for (entity in Entity.map) {
            val entityJson = json[entity.value.name.lowercase(Locale.ENGLISH).replace(" ", "_")].asJsonObject
            val thing = Entity.map[entity.key]!!
            entityJson.addProperty("hitbox_enabled", thing.hitboxEnabled)
            entityJson.addProperty("eyeline_enabled", thing.eyeLineEnabled)
            entityJson.addProperty("line_enabled", thing.lineEnabled)
            entityJson.addProperty("color", thing.color)
            entityJson.addProperty("eye_color", thing.eyeColor)
            entityJson.addProperty("line_color", thing.lineColor)
            entityJson.addProperty("crosshair_color", thing.crosshairColor)
        }
        val generalJson = json["general"].asJsonObject
        generalJson.addProperty("hitbox_width", GeneralConfig.config.hitboxWidth)
        generalJson.addProperty("force_hitbox", GeneralConfig.config.forceHitbox)
        generalJson.addProperty("disable_for_self", GeneralConfig.config.disableForSelf)
        generalJson.addProperty("accurate_hitbox", GeneralConfig.config.accurateHitbox)
        file.writeText(GSON.toJson(json))
    }


}