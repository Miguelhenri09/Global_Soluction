package br.com.inovagab.ui.operator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagab.domain.model.Idea
import br.com.inovagab.domain.model.StrategicGuideline
import br.com.inovagab.ui.shared.*
import br.com.inovagab.ui.theme.MediumBlue
import br.com.inovagab.ui.theme.NavyBlue

// ── HOME ────────────────────────────────────────────────────────────

@Composable
fun OperatorHomeScreen(
    uiState: OperatorUiState,
    onNavigateIdeas: () -> Unit,
    onNavigateNewIdea: () -> Unit,
    onNavigateGuidelines: () -> Unit,
    onLogout: () -> Unit
) {
    val user = uiState.user
    Scaffold(
        topBar = { InovaTopBar("InovaGAB", onLogout = onLogout) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyBlue)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Olá, ${user?.name ?: "Colaborador"}! 👋", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Departamento: ${user?.department ?: "-"}", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(
                        value = uiState.ideas.size.toString(),
                        label = "Ideias\nenviadas",
                        color = MediumBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = uiState.ideas.count { it.status.name == "APPROVED" }.toString(),
                        label = "Ideias\naprovadas",
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = uiState.guidelines.size.toString(),
                        label = "Diretrizes\nativas",
                        color = Color(0xFF6A1B9A),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item { Text("Ações rápidas", fontWeight = FontWeight.Bold, color = NavyBlue, style = MaterialTheme.typography.titleMedium) }
            item {
                QuickActionCard(
                    icon = Icons.Default.Lightbulb,
                    title = "Nova ideia / Problema",
                    subtitle = "Registre uma sugestão ou dor operacional",
                    color = Color(0xFF1565C0),
                    onClick = onNavigateNewIdea
                )
            }
            item {
                QuickActionCard(
                    icon = Icons.Default.List,
                    title = "Minhas ideias",
                    subtitle = "Acompanhe o status das suas ideias",
                    color = Color(0xFF00838F),
                    onClick = onNavigateIdeas
                )
            }
            item {
                QuickActionCard(
                    icon = Icons.Default.Flag,
                    title = "Diretrizes estratégicas",
                    subtitle = "Veja as orientações da empresa",
                    color = Color(0xFF6A1B9A),
                    onClick = onNavigateGuidelines
                )
            }
        }
    }
}

// ── MY IDEAS ────────────────────────────────────────────────────────

@Composable
fun OperatorIdeasScreen(
    ideas: List<Idea>,
    onBack: () -> Unit,
    onNewIdea: () -> Unit
) {
    Scaffold(
        topBar = { InovaTopBar("Minhas Ideias", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewIdea, containerColor = MediumBlue) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        if (ideas.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Lightbulb, null, modifier = Modifier.size(64.dp), tint = Color(0xFFB0BEC5))
                    Spacer(Modifier.height(8.dp))
                    Text("Nenhuma ideia registrada", color = Color(0xFF90A4AE))
                    Text("Toque em + para enviar sua primeira ideia!", style = MaterialTheme.typography.bodySmall, color = Color(0xFF90A4AE))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ideas) { idea -> IdeaCard(idea) }
            }
        }
    }
}

@Composable
fun IdeaCard(idea: Idea) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(idea.title, fontWeight = FontWeight.Bold, color = NavyBlue, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                IdeaStatusBadge(idea.status)
            }
            Text(idea.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF546E7A), maxLines = 2)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(50)) {
                    Text(idea.category, color = MediumBlue, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }
            if (idea.managerComment.isNotBlank()) {
                Surface(color = Color(0xFFF3E5F5), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Comment, null, tint = Color(0xFF6A1B9A), modifier = Modifier.size(14.dp))
                        Text("Gestor: ${idea.managerComment}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4A148C))
                    }
                }
            }
        }
    }
}

// ── NEW IDEA ─────────────────────────────────────────────────────────

@Composable
fun OperatorNewIdeaScreen(
    isLoading: Boolean,
    message: String?,
    onSubmit: (String, String, String) -> Unit,
    onBack: () -> Unit,
    onMessageDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Operacional", "Logística", "Atendimento", "Tecnologia", "Segurança", "Sustentabilidade", "Processos", "Outro")

    if (message != null) {
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(2000)
            onMessageDismiss()
            onBack()
        }
    }

    Scaffold(
        topBar = { InovaTopBar("Nova Ideia", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = MediumBlue)
                    Text("Compartilhe um problema ou sugestão para melhorar nosso dia a dia!", style = MaterialTheme.typography.bodySmall, color = NavyBlue)
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título da ideia *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; categoryExpanded = false })
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição detalhada *") },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(12.dp)
            )

            message?.let {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32))
                        Text(it, color = Color(0xFF1B5E20))
                    }
                }
            }

            Button(
                onClick = { onSubmit(title, description, category) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank() && category.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MediumBlue)
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else { Icon(Icons.Default.Send, null); Spacer(Modifier.width(8.dp)); Text("Enviar Ideia", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

// ── GUIDELINES (read-only) ──────────────────────────────────────────

@Composable
fun OperatorGuidelinesScreen(
    guidelines: List<StrategicGuideline>,
    onBack: () -> Unit
) {
    Scaffold(topBar = { InovaTopBar("Diretrizes Estratégicas", onBack = onBack) }) { padding ->
        if (guidelines.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Nenhuma diretriz cadastrada", color = Color(0xFF90A4AE))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(guidelines) { g ->
                    Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(3.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Flag, null, tint = Color(0xFF6A1B9A))
                                Text(g.title, fontWeight = FontWeight.Bold, color = NavyBlue)
                            }
                            Text(g.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF546E7A))
                            Surface(color = Color(0xFFF3E5F5), shape = RoundedCornerShape(50)) {
                                Text(g.category, color = Color(0xFF6A1B9A), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
