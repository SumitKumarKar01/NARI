import android.content.Context
import com.nari.app.JsonParser
import com.nari.app.R
import java.io.BufferedReader
import java.io.InputStreamReader

class JsonDatabaseSync(private val context: Context) {

    fun updateDataFromJson() {
        val jsonString = readJsonFile()
        val jsonParser = JsonParser(context)
        jsonParser.parseAndSaveJson(jsonString)
    }

    private fun readJsonFile(): String {
        val inputStream = context.resources.openRawResource(R.raw.sample_data)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String? = reader.readLine()

        while (line != null) {
            stringBuilder.append(line).append("\n")
            line = reader.readLine()
        }

        return stringBuilder.toString()
    }
}