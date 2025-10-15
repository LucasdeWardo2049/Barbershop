package com.pdm.barbershop.ui.feature.services.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pdm.barbershop.R
import com.pdm.barbershop.domain.model.CatalogItem
import com.pdm.barbershop.domain.model.CatalogItemType
import com.pdm.barbershop.ui.common.format.toBRL

@Composable
fun ServiceCard(
    item: CatalogItem,
    modifier: Modifier = Modifier,
) {
    val priceText = item.price.toBRL()
    val durationText = item.durationMinutes?.let { mins ->
        stringResource(R.string.duration_minutes, mins)
    }
    val cardA11y = listOfNotNull(item.name, priceText, durationText).joinToString(
        separator = ", "
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics { this.contentDescription = cardA11y }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = when (item.type) {
                    CatalogItemType.SERVICE -> Icons.Outlined.ContentCut
                    CatalogItemType.PRODUCT -> Icons.Outlined.ShoppingBag
                },
                contentDescription = when (item.type) {
                    CatalogItemType.SERVICE -> stringResource(R.string.services_tab_services)
                    CatalogItemType.PRODUCT -> stringResource(R.string.services_tab_products)
                },
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(8.dp))

                // Chips de preço e duração
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { },
                        label = { Text(priceText) },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.MonetizationOn,
                                contentDescription = priceText
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconContentColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    item.durationMinutes?.let { mins ->
                        AssistChip(
                            onClick = { },
                            label = { Text(stringResource(R.string.duration_minutes, mins)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.AccessTime,
                                    contentDescription = stringResource(R.string.duration_minutes, mins)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
