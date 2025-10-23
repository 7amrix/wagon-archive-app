package com.wagonarchive.app

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wagonarchive.app.data.WagonDatabase
import com.wagonarchive.app.data.WagonEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class WagonViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = WagonDatabase.getDatabase(app).wagonDao()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val wagons = searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) {
            dao.getAllWagons()
        } else {
            dao.searchWagons(query)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addWagon(serialNumber: String, location: String, notes: String, status: String) {
        viewModelScope.launch {
            val wagon = WagonEntity(
                serialNumber = serialNumber,
                date = System.currentTimeMillis(),
                location = location,
                notes = notes,
                status = status
            )
            dao.insertWagon(wagon)
        }
    }

    fun updateWagon(wagon: WagonEntity) {
        viewModelScope.launch {
            dao.updateWagon(wagon)
        }
    }

    fun deleteWagon(wagon: WagonEntity) {
        viewModelScope.launch {
            dao.deleteWagon(wagon)
        }
    }

    suspend fun checkSerialNumberExists(serialNumber: String): Boolean {
        return dao.getWagonBySerialNumber(serialNumber) != null
    }

    fun exportToCSV(context: Context): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fileName = "wagon_archive_${System.currentTimeMillis()}.csv"
        
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            
            file.bufferedWriter().use { writer ->
                writer.write("Serial Number,Date,Location,Notes,Status\n")
                
                wagons.value.forEach { wagon ->
                    val date = dateFormat.format(Date(wagon.date))
                    writer.write("\"${wagon.serialNumber}\",\"$date\",\"${wagon.location}\",\"${wagon.notes}\",\"${wagon.status}\"\n")
                }
            }
            
            file.absolutePath
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
