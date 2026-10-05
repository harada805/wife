package com.example.wife.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wife.data.local.entity.MemoryFactEntity
import com.example.wife.ui.components.LarasAvatar
import com.example.wife.ui.theme.PermissionCardShape
import com.example.wife.ui.theme.TerasSenjaTheme
import com.example.wife.ui.theme.TerasSenjaTypography

@Composable
fun MemoryFactsScreen(
    viewModel: MemoryFactsViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val facts by viewModel.facts.collectAsState()

    MemoryFactsScreenContent(
        facts = facts,
        onAddFact = viewModel::addFact,
        onUpdateFact = viewModel::updateFact,
        onDeleteFact = viewModel::deleteFact,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryFactsScreenContent(
    facts: List<MemoryFactEntity>,
    onAddFact: (String, String) -> Unit,
    onUpdateFact: (MemoryFactEntity) -> Unit,
    onDeleteFact: (Long) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingFact by remember { mutableStateOf<MemoryFactEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Buku Catatan Laras",
                        style = TerasSenjaTypography.characterHeader,
                        color = TerasSenjaTheme.colors.ink
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TerasSenjaTheme.colors.ink
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TerasSenjaTheme.colors.paper
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TerasSenjaTheme.colors.terracotta,
                contentColor = TerasSenjaTheme.colors.paperRaised
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Tambah Catatan"
                )
            }
        },
        containerColor = TerasSenjaTheme.colors.paper
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (facts.isEmpty()) {
                // Empty state in Laras's voice
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LarasAvatar(size = 64.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Laras belum mencatat apa-apa tentangmu.",
                        style = TerasSenjaTypography.characterMessage,
                        color = TerasSenjaTheme.colors.ink
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ceritakan sesuatu di obrolan atau buat catatan baru di sini yuk!",
                        style = TerasSenjaTypography.captionStatusTimestamp,
                        color = TerasSenjaTheme.colors.inkMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(facts, key = { it.id }) { fact ->
                        MemoryFactCard(
                            fact = fact,
                            onEdit = { editingFact = fact },
                            onDelete = { onDeleteFact(fact.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddOrEditFactDialog(
            initialCategory = "",
            initialContent = "",
            title = "Tambah Catatan Memori",
            onDismiss = { showAddDialog = false },
            onSave = { category, content ->
                onAddFact(category, content)
                showAddDialog = false
            }
        )
    }

    editingFact?.let { fact ->
        AddOrEditFactDialog(
            initialCategory = fact.category,
            initialContent = fact.content,
            title = "Ubah Catatan Memori",
            onDismiss = { editingFact = null },
            onSave = { category, content ->
                onUpdateFact(fact.copy(category = category, content = content))
                editingFact = null
            }
        )
    }
}

@Composable
fun MemoryFactCard(
    fact: MemoryFactEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = PermissionCardShape,
        color = TerasSenjaTheme.colors.paperRaised,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = Dp.Hairline,
                color = TerasSenjaTheme.colors.hairline,
                shape = PermissionCardShape
            )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = TerasSenjaTheme.colors.terracotta.copy(alpha = 0.15f),
                    shape = PermissionCardShape
                ) {
                    Text(
                        text = fact.category,
                        style = TerasSenjaTypography.captionStatusTimestamp,
                        color = TerasSenjaTheme.colors.terracottaDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Ubah",
                            tint = TerasSenjaTheme.colors.inkMuted
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Hapus",
                            tint = TerasSenjaTheme.colors.danger
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = fact.content,
                style = TerasSenjaTypography.characterMessage,
                color = TerasSenjaTheme.colors.ink
            )
        }
    }
}

@Composable
fun AddOrEditFactDialog(
    initialCategory: String,
    initialContent: String,
    title: String,
    onDismiss: () -> Unit,
    onSave: (category: String, content: String) -> Unit
) {
    var category by remember { mutableStateOf(initialCategory) }
    var content by remember { mutableStateOf(initialContent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = TerasSenjaTypography.characterHeader,
                color = TerasSenjaTheme.colors.ink
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori (opsional)") },
                    placeholder = { Text("misal: Makanan, Jadwal") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerasSenjaTheme.colors.terracotta,
                        unfocusedBorderColor = TerasSenjaTheme.colors.hairline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Isi Catatan") },
                    placeholder = { Text("misal: Suka minum teh chamomile sore hari") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerasSenjaTheme.colors.terracotta,
                        unfocusedBorderColor = TerasSenjaTheme.colors.hairline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(category, content) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TerasSenjaTheme.colors.ink,
                    contentColor = TerasSenjaTheme.colors.onInk
                )
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        containerColor = TerasSenjaTheme.colors.paperRaised
    )
}

@Preview(name = "MemoryFactsScreen Light Mode", showBackground = true)
@Composable
fun MemoryFactsScreenPreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        MemoryFactsScreenContent(
            facts = listOf(
                MemoryFactEntity(id = 1, category = "Sukaan", content = "Sangat menyukai teh chamomile saat sore hari."),
                MemoryFactEntity(id = 2, category = "Kebiasaan", content = "Selalu bangun pukul 05.00 pagi.")
            ),
            onAddFact = { _, _ -> },
            onUpdateFact = {},
            onDeleteFact = {}
        )
    }
}

@Preview(name = "MemoryFactsScreen Dark Mode", showBackground = true)
@Composable
fun MemoryFactsScreenPreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        MemoryFactsScreenContent(
            facts = listOf(
                MemoryFactEntity(id = 1, category = "Sukaan", content = "Sangat menyukai teh chamomile saat sore hari."),
                MemoryFactEntity(id = 2, category = "Kebiasaan", content = "Selalu bangun pukul 05.00 pagi.")
            ),
            onAddFact = { _, _ -> },
            onUpdateFact = {},
            onDeleteFact = {}
        )
    }
}
