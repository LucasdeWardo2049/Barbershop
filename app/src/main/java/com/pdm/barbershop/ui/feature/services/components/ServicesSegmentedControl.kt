package com.pdm.barbershop.ui.feature.services.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pdm.barbershop.R
import com.pdm.barbershop.ui.feature.services.ServicesTab

@Composable
fun ServicesSegmentedControl(
    selectedTab: ServicesTab,
    onSelect: (ServicesTab) -> Unit
) {
    val items = listOf(
        ServicesTab.Services to Pair(Icons.Outlined.ContentCut, stringResource(R.string.services_tab_services)),
        ServicesTab.Products to Pair(Icons.Outlined.ShoppingBag, stringResource(R.string.services_tab_products)),
    )

    SingleChoiceSegmentedButtonRow {
        items.forEachIndexed { index, (tab, pair) ->
            val (icon, label) = pair
            SegmentedButton(
                selected = selectedTab == tab,
                onClick = { onSelect(tab) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = items.size),
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
            )
        }
    }
}
