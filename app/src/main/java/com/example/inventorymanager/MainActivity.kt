package com.example.inventorymanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventorymanager.ui.theme.InventoryManagerTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Inventory(
    val name: String,
    val count: Int
)

object InventoryRepo {

    fun getInventory() = listOf(
        Inventory("Laptop", 10),
        Inventory("Keyboard", 5),
        Inventory("Mouse", 8),
        Inventory("Monitor", 2),
        Inventory("Headphones", 7)
    )
}

class InventoryViewModel : ViewModel() {

    private val _items = MutableStateFlow(
        InventoryRepo.getInventory()
    )

    val items = _items.asStateFlow()

    fun increaseStock(index: Int) {
        val current = _items.value.toMutableList()
        val item = current[index]

        current[index] = item.copy(
            count = item.count + 1
        )

        _items.value = current
    }

    fun decreaseStock(index: Int) {
        val current = _items.value.toMutableList()
        val item = current[index]

        if (item.count > 0) {
            current[index] = item.copy(
                count = item.count - 1
            )
        }

        _items.value = current
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            InventoryManagerTheme {
                InventoryScreen()
            }
        }
    }
}

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = viewModel()
) {
    val items = viewModel.items.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "Inventory Management",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(items.value.indices.toList()) { index ->

                    InventoryItem(
                        inventory = items.value[index],
                        onIncrease = {
                            viewModel.increaseStock(index)
                        },
                        onDecrease = {
                            viewModel.decreaseStock(index)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryItem(
    inventory: Inventory,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {

    val lowStockThreshold = 2
    val isLowStock = inventory.count <= lowStockThreshold

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = inventory.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Current Stock: ${inventory.count}"
            )


            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isLowStock) {
                    $"Low Stock"
                } else {
                    "Stock Available"
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = onDecrease
                ) {
                    Text("-")
                }

                Button(
                    onClick = onIncrease
                ) {
                    Text("+")
                }
            }
        }
    }
}