package com.diegodobelo.ticketmaster.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.diegodobelo.ticketmaster.R
import com.diegodobelo.ticketmaster.ui.theme.largePadding

@Composable
fun CityInput(currentCity: String, onValueChanged: (String) -> Unit) {
    TextField(
        value = currentCity,
        onValueChange = onValueChanged,
        modifier = Modifier.fillMaxWidth().padding(largePadding),
        textStyle = MaterialTheme.typography.bodyLarge,
        label = { Text(text = stringResource(id = R.string.city_input_label)) },
    )
}