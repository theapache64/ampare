import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.get


const val sampleData1 = """{
  "id": 1,
  "name": {
    "first": "Jake",
    "last": "Doe"
  },
  "hobbies": [
    "reading"
  ],
  "projects": [
    {
      "name": "Project A",
      "status": "completed"
    },
    {
      "name": "Project B",
      "status": "in-progress"
    }
  ]
}"""

const val sampleData2 = """{
  "id": 1,
  "name": {
    "first": "John",
    "last": "Doe"
  },
  "hobbies": [
    "reading",
    "gaming"
  ],
  "projects": [
    {
      "name": "Project C",
      "status": "completed"
    },
    {
      "name": "Project B",
      "status": "in-progress"
    }
  ]
}"""



fun main() {
    document.addEventListener("DOMContentLoaded", {
        // Fill sample data
        document.getElementById("event1")?.textContent = sampleData1
        document.getElementById("event2")?.textContent = sampleData2

        // Add event listener to the button
        val compareButton = document.getElementById("compareBtn") as? HTMLButtonElement
        compareButton?.addEventListener("click", {
            try {
                val event1Map = parseEventData("event1")
                val event2Map = parseEventData("event2")

                if (event2Map.isEmpty() && event1Map.isEmpty()) {
                    val resultsSection = document.getElementById("resultsSection")
                    resultsSection?.classList?.add("hidden")
                    return@addEventListener
                }

                // Build diff map
                val addedMap = event2Map.filter { (key, _) ->
                    // Check if the key is not present in event1Map or the value is different
                    !event1Map.containsKey(key)
                }
                val changedMap = event2Map.filter { (key, value) ->
                    // Check if the key is present in event1Map and the value is different
                    event1Map.containsKey(key) && event1Map[key] != value
                }
                val removedMap = event1Map.filter { (key, _) ->
                    // Check if the key is not present in event2Map
                    !event2Map.containsKey(key)
                }
                val sameMap = event1Map.filter { (key, value) ->
                    // Check if the key is present in event2Map and the value is the same
                    event2Map.containsKey(key) && event2Map[key] == value
                }


                // Update cards
                val statsHtml = """<div class="stat-card stat-modified">
                        <div class="stat-number">${changedMap.size}</div>
                        <div class="stat-label">Modified</div>
                    </div>
                    
                    <div class="stat-card stat-added">
                        <div class="stat-number">${addedMap.size}</div>
                        <div class="stat-label">Added</div>
                    </div>
                    <div class="stat-card stat-removed">
                        <div class="stat-number">${removedMap.size}</div>
                        <div class="stat-label">Removed</div>
                    </div>
                    <div class="stat-card stat-same">
                        <div class="stat-number">${sameMap.size}</div>
                        <div class="stat-label">Same</div>
                    </div>"""

                document.getElementById("statsContainer")?.innerHTML = statsHtml;

                // Create tables for changed section
                renderTable("modified", changedMap, event1Map, event2Map)
                renderTable("added", addedMap, event1Map, event2Map)
                renderTable("removed", removedMap, event1Map, event2Map)
                renderTable("same", sameMap, event1Map, event2Map)

                val resultsSection = document.getElementById("resultsSection")
                resultsSection?.classList?.remove("hidden");
            } catch (e: Exception) {
                console.error("QuickTag: :main: Error while comparing events", e)
                window.alert("An error occurred while comparing events. Please check the console for details.")
            }
        })

        // Auto click the compare button in 500ms
        window.setTimeout({
            compareButton?.click()
        }, 500)
    })
}

private fun renderTable(
    tableClass: String,
    resultMap: Map<String, String>,
    event1Map: Map<String, String>,
    event2Map: Map<String, String>
) {
    val section = document.getElementById(tableClass + "Section")
    val tbody = document.getElementById("${tableClass}TableBody")

    if (resultMap.isEmpty()) {
        section?.classList?.add("hidden")
        return
    }

    section?.classList?.remove("hidden")
    tbody?.innerHTML = resultMap.entries.joinToString("") { (key, value) ->
        if (tableClass == "modified") {
            val event1Value = event1Map[key] ?: "N/A"
            val event2Value = event2Map[key] ?: "N/A"
            """
            <tr>
                <td class="key">$key</td>
                <td class="value">${event1Value}</td>
                <td class="value">${event2Value}</td>
            </tr>
        """.trimIndent()
        } else {
            """
            <tr>
                <td class="key">$key</td>
                <td class="value">${value}</td>
            </tr>
        """.trimIndent()
        }
    }
}

private val twoLineKeys = listOf<String>(
    "AmplitudeCountry"
)

fun parseEventData(elementId: String): Map<String, String> {
    val element = document.getElementById(elementId) as? HTMLTextAreaElement
    var textContent = element?.value ?: return emptyMap()
    textContent = transformIfJson(textContent)

    for (twoLineKey in twoLineKeys) {
        textContent = textContent.replace("($twoLineKey)\\n(\\w+)\\n(\\w+)".toRegex()) { result ->
            val (_, x, y, z) = result.groupValues
            "$x\n$y,$z"
        }
    }

    return textContent.lines().filter {
        it.isNotBlank() && !it.equals("User Properties", ignoreCase = false) && !it.equals(
            "Event Properties",
            ignoreCase = false
        ) && !it.equals("Other", ignoreCase = false)
    }.map { it.trim().replace("Amplitude", "") }.chunked(2).also {
        println("QuickTag: :parseEventData: size: ${it.size}, elementId: $elementId")
    }.associate {
        println("QuickTag: :parseEventData: it: key -> $it")
        it[0] to it[1]
    }
}

private fun transformIfJson(textContent: String): String {
    return try {
        val cleanedJson = textContent.replace("\\'", "'")
        val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(cleanedJson)
        flattenJson(jsonElement).joinToString("\n")
    } catch (e: Exception) {
        console.error("QuickTag: :transformIfJson: Error parsing JSON", e)
        transformIfHtml(textContent)
    }
}

private fun transformIfHtml(textContent: String): String {
    if (!textContent.startsWith("<button style=\"")) return textContent
    val lines = textContent.split("\n")
    if (lines.size != 1) {
        // This Html + JSON
        val jsonData = lines.drop(1).dropLast(1).joinToString(
            separator = "\n",
            prefix = "{\n",
            postfix = "\n}"
        )

        // Check if it's valid JSON
        return try {
            kotlinx.serialization.json.Json.parseToJsonElement(jsonData)
            transformIfJson(jsonData)
        } catch (e: Exception) {
            console.error("QuickTag: :transformIfHtml: Error parsing JSON from HTML", e)
            textContent // Return original content if parsing fails
        }
    }

    return try {
        val keyValues = mutableListOf<String>()
        val tempDiv = document.createElement("div") as HTMLDivElement
        tempDiv.innerHTML = textContent
        // Parse all `shrproperties-listjubs-w53` divs
        val shrPropertiesDivs = tempDiv.querySelectorAll("div.shrproperty-rowjubs-w59")
        println("QuickTag: :transformIfHtml: ${shrPropertiesDivs.length}")
        shrPropertiesDivs.length.let { length ->
            if (length == 0) {
                return@let textContent // Return original content if no divs found
            }

            // Event name
            val eventName = try {
                tempDiv.querySelector("div.shrsectionjubs-w37")?.let { firstSection ->
                    firstSection.children[firstSection.childElementCount - 2]?.textContent ?: ""
                }
            }catch (e : IndexOutOfBoundsException){
                println("QuickTag: :transformIfHtml: failed to get event name, returning empty string: ${e.message}")
                null
            }

            if(eventName!=null){
                keyValues.add("Event Name\n$eventName\n")
            }

            for (i in 0 until length) {
                val propertyRowDiv = shrPropertiesDivs[i] as HTMLDivElement
                // Should only contain 2 divs: key and value
                if (propertyRowDiv.childElementCount != 2) {
                    console.warn("QuickTag: :transformIfHtml: Unexpected child count in div: ${propertyRowDiv.childElementCount}")
                    continue // Skip if not exactly 2 children
                }

                val key = propertyRowDiv.childNodes[0]?.textContent ?: ""
                val value = propertyRowDiv.childNodes[1]?.textContent ?: ""
                if (key.isNotEmpty() && value.isNotEmpty()) {
                    keyValues.add("$key\n$value\n")
                }
            }
        }

        keyValues.joinToString(separator = "\n").also {
            println("QuickTag: :transformIfHtml: Parsed HTML content, size: ${keyValues.size}")
        }
    } catch (e: Exception) {
        console.error("QuickTag: :transformIfHtml: Error parsing HTML", e)
        textContent // Return original content if parsing fails
    }
}

private fun flattenJson(element: JsonElement, prefix: String = ""): List<String> {
    val result = mutableListOf<String>()

    when (element) {
        is JsonObject -> {
            element.entries.forEach { (key, value) ->
                val currentPath = if (prefix.isEmpty()) key else "$prefix.$key"
                result.addAll(flattenJson(value, currentPath))
            }
        }

        is JsonArray -> {
            element.forEachIndexed { index, value ->
                val currentPath = "$prefix[$index]"
                result.addAll(flattenJson(value, currentPath))
            }
        }

        is JsonPrimitive -> {
            val value = when {
                element.isString -> element.content
                else -> element.toString()
            }
            result.add(prefix)
            result.add(value)
        }
    }

    return result
}