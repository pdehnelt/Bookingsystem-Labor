package com.example.aufgabe3.ui.add

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    var name by remember { mutableStateOf("") }
    var arrivalDate by remember { mutableStateOf<LocalDate?>(null) }
    var departureDate by remember { mutableStateOf<LocalDate?>(null) }

    var showDateRangePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Booking Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = if (arrivalDate != null && departureDate != null) {
                    "${arrivalDate!!.format(dateFormatter)} - ${departureDate!!.format(dateFormatter)}"
                } else {
                    ""
                },
                onValueChange = {},
                label = { Text("Select Date Range") },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateRangePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )



            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // TODO Error handling and creating new BookingEntry and save in sharedViewModel

                    if (name.isBlank() || arrivalDate == null || departureDate == null) {
                        errorMessage = "Please fill all fields."
                        return@Button
                    }

                    sharedViewModel.addBookingEntry(
                        BookingEntry(
                            arrivalDate = arrivalDate!!,
                            departureDate = departureDate!!,
                            name = name.trim()
                        )
                    )

                    navController.popBackStack()


                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }

    // TODO implement DateRangePicker Dialog logic

    // Überprüft, ob der DateRangePicker angezeigt werden soll.
    // Wenn showDateRangePicker true ist, wird das Dialogfenster geöffnet.
    if (showDateRangePicker) {
        DateRangePickerModal(
            context = context
        ) { start, end ->
            arrivalDate = start
            departureDate = end
            showDateRangePicker = false
        }
    }
}


/**
 * Composable-Funktion zur Anzeige eines Datumsauswahl-Dialogs für einen Datumsbereich.
 *
 * Diese Funktion zeigt zwei aufeinanderfolgende `DatePickerDialog`s an:
 * Der erste Dialog ermöglicht die Auswahl eines Startdatums.
 * Der zweite Dialog wird automatisch angezeigt, um ein Enddatum auszuwählen,
 *   das nach dem Startdatum liegen muss.
 *
 * @param context Der aktuelle Anwendungskontext für die Anzeige des Dialogs.
 * @param onDateSelected Eine Rückruffunktion, die aufgerufen wird, wenn beide Daten
 * korrekt ausgewählt wurden. Sie gibt das Start- und Enddatum im `LocalDate`-Format zurück.
 */
@Composable
fun DateRangePickerModal(
    context: Context,
    onDateSelected: (LocalDate, LocalDate) -> Unit
) {
    // TODO implement DateRangePicker see https://developer.android.com/develop/ui/compose/components/datepickers?hl=de

    val calendar = Calendar.getInstance()
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedStart = LocalDate.of(year, month + 1, dayOfMonth)
            startDate = selectedStart

            DatePickerDialog(
                context,
                { _, endYear, endMonth, endDayOfMonth ->
                    val selectedEnd = LocalDate.of(endYear, endMonth + 1, endDayOfMonth)

                    when {
                        selectedStart == selectedEnd -> {
                            errorMessage = "Start and end dates cannot be the same."
                        }
                        selectedEnd.isBefore(selectedStart) -> {
                            errorMessage = "End date must be after start date."
                        }
                        else -> {
                            errorMessage = null
                            onDateSelected(selectedStart, selectedEnd)
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()

    if (errorMessage != null) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }


}
