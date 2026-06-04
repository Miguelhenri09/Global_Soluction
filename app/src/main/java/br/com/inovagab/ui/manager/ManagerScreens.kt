package br.com.inovagab.ui.manager

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
import br.com.inovagab.domain.model.*
import br.com.inovagab.ui.shared.*
import br.com.inovagab.ui.theme.MediumBlue
import br.com.inovagab.ui.theme.NavyBlue

// ── HOME ─────────────────────────────────────────────────────────────

@Composable
fun ManagerHomeScreen(
    uiState: ManagerUiState,
    onNavigateIdeas: () -> Unit,
    onNavigateProjects: () -> Unit,
    onNavigateGuidelines: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(topBar = { InovaTopBar("InovaGAB — Gestor", onLogout = onLogout) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = NavyBlue)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Olá, ${uiState.user?.name ?: "Gestor"}!", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Gerencie ideias e projetos do seu time", color = Color.White.copy(0.7f), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val pending = uiState.ideas.count { it.status == IdeaStatus.PENDING }
                    StatCard("$pending", "Ideias\npendentes", Color(0xFFF57C00), Modifier.weight(1f))
                    StatCard(uiState.projects.size.toString(), "Projetos\nativos", MediumBlue, Modifier.weight(1f))
                    StatCard(uiState.ideas.count { it.status == IdeaStatus.APPROVED }.toString(), "Ideias\naprovadas", Color(0xFF2E7D32), Modifier.weight(1f))
                }
            }
            item { Text("Ações", fontWeight = FontWeight.Bold, color = NavyBlue, style = MaterialTheme.typography.titleMedium) }
            item { QuickActionCard(Icons.Default.Lightbulb, "Curadoria de Ideias", "Avalie e priorize ideias dos operadores", Color(0xFF1565C0), onNavigateIdeas) }
            item { QuickActionCard(Icons.Default.Assignment, "Projetos", "Crie e acompanhe projetos de inovação", Color(0xFF00838F), onNavigateProjects) }
            item { QuickActionCard(Icons.Default.Flag, "Diretrizes Estratégicas", "Consulte as orientações da empresa", Color(0xFF6A1B9A), onNavigateGuidelines) }
        }
    }
}

// ── IDEAS CURATION ───────────────────────────────────────────────────

@Composable
fun ManagerIdeasScreen(
    uiState: ManagerUiState,
    onUpdateIdea: (String, IdeaStatus, String, Int) -> Unit,
    onCreateProject: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedIdea by remember { mutableStateOf<Idea?>(null) }
    var filterStatus by remember { mutableStateOf<IdeaStatus?>(null) }

    Scaffold(topBar = { InovaTopBar("Curadoria de Ideias", onBack = onBack) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Filter chips
            ScrollableChipRow(filterStatus) { filterStatus = it }
            val filtered = if (filterStatus == null) uiState.ideas else uiState.ideas.filter { it.status == filterStatus }
            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma ideia neste filtro", color = Color(0xFF90A4AE))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered) { idea ->
                        ManagerIdeaCard(idea = idea, onReview = { selectedIdea = idea }, onCreateProject = { onCreateProject(idea.id) })
                    }
                }
            }
        }
    }

    selectedIdea?.let { idea ->
        IdeaReviewDialog(
            idea = idea,
            onDismiss = { selectedIdea = null },
            onConfirm = { status, comment, priority ->
                onUpdateIdea(idea.id, status, comment, priority)
                selectedIdea = null
            }
        )
    }
}

@Composable
fun ScrollableChipRow(selected: IdeaStatus?, onSelect: (IdeaStatus?) -> Unit) {
    val statuses = listOf(null to "Todos") + IdeaStatus.values().map { it to it.name }
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        statuses.forEach { (status, label) ->
            FilterChip(
                selected = selected == status,
                onClick = { onSelect(status) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) }
            )
        }
    }
}

@Composable
fun ManagerIdeaCard(idea: Idea, onReview: () -> Unit, onCreateProject: () -> Unit) {
    Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(3.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(idea.title, fontWeight = FontWeight.Bold, color = NavyBlue, modifier = Modifier.weight(1f))
                IdeaStatusBadge(idea.status)
            }
            Text(idea.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF546E7A), maxLines = 3)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = Color(0xFF90A4AE), modifier = Modifier.size(14.dp))
                Text(idea.authorName, style = MaterialTheme.typography.labelSmall, color = Color(0xFF90A4AE))
                Text("•", color = Color(0xFF90A4AE))
                Text(idea.department, style = MaterialTheme.typography.labelSmall, color = Color(0xFF90A4AE))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onReview, modifier = Modifier.weight(1f)) { Text("Avaliar", style = MaterialTheme.typography.labelMedium) }
                if (idea.status == IdeaStatus.APPROVED) {
                    Button(onClick = onCreateProject, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                        Text("→ Projeto", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun IdeaReviewDialog(idea: Idea, onDismiss: () -> Unit, onConfirm: (IdeaStatus, String, Int) -> Unit) {
    var comment by remember { mutableStateOf(idea.managerComment) }
    var selectedStatus by remember { mutableStateOf(idea.status) }
    var priority by remember { mutableStateOf(idea.priority) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Avaliar: ${idea.title}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Status:", fontWeight = FontWeight.SemiBold)
                val statuses = listOf(IdeaStatus.UNDER_REVIEW, IdeaStatus.APPROVED, IdeaStatus.REJECTED)
                statuses.forEach { s ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedStatus == s, onClick = { selectedStatus = s })
                        val label = when (s) { IdeaStatus.UNDER_REVIEW -> "Em análise"; IdeaStatus.APPROVED -> "Aprovar"; IdeaStatus.REJECTED -> "Rejeitar"; else -> s.name }
                        Text(label)
                    }
                }
                Text("Prioridade:", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0 to "Normal", 1 to "Alta", 2 to "Urgente").forEach { (v, l) ->
                        FilterChip(selected = priority == v, onClick = { priority = v }, label = { Text(l) })
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comentário") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = { Button(onClick = { onConfirm(selectedStatus, comment, priority) }) { Text("Confirmar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

// ── PROJECTS ─────────────────────────────────────────────────────────

@Composable
fun ManagerProjectsScreen(
    projects: List<Project>,
    onNewProject: () -> Unit,
    onEditProject: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { InovaTopBar("Projetos", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewProject, containerColor = MediumBlue) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        if (projects.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Assignment, null, modifier = Modifier.size(64.dp), tint = Color(0xFFB0BEC5))
                    Spacer(Modifier.height(8.dp))
                    Text("Nenhum projeto cadastrado", color = Color(0xFF90A4AE))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(projects) { project ->
                    ProjectCard(project = project, onClick = { onEditProject(project.id) })
                }
            }
        }
    }
}

@Composable
fun ProjectCard(project: Project, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(3.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(project.title, fontWeight = FontWeight.Bold, color = NavyBlue, modifier = Modifier.weight(1f))
                ProjectStatusBadge(project.status)
            }
            Text(project.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF546E7A), maxLines = 2)
            LinearProgressIndicator(
                progress = project.progress / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = MediumBlue,
                trackColor = Color(0xFFE3F2FD)
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Progresso: ${project.progress}%", style = MaterialTheme.typography.labelSmall, color = Color(0xFF546E7A))
                Text("Invest: R$ ${"%,.0f".format(project.investment)}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF546E7A))
            }
        }
    }
}

// ── SAVE PROJECT FORM ────────────────────────────────────────────────

@Composable
fun ManagerProjectFormScreen(
    project: Project?,
    isLoading: Boolean,
    message: String?,
    onSave: (Project) -> Unit,
    onBack: () -> Unit,
    onMessageDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(project?.title ?: "") }
    var description by remember { mutableStateOf(project?.description ?: "") }
    var stage by remember { mutableStateOf(project?.stage ?: "") }
    var investment by remember { mutableStateOf(project?.investment?.toString() ?: "") }
    var expectedReturn by remember { mutableStateOf(project?.expectedReturn?.toString() ?: "") }
    var actualReturn by remember { mutableStateOf(project?.actualReturn?.toString() ?: "") }
    var productivityGain by remember { mutableStateOf(project?.productivityGain?.toString() ?: "") }
    var costReduction by remember { mutableStateOf(project?.costReduction?.toString() ?: "") }
    var progress by remember { mutableStateOf(project?.progress?.toString() ?: "0") }
    var notes by remember { mutableStateOf(project?.notes ?: "") }
    var statusExpanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(project?.status ?: ProjectStatus.PLANNING) }

    if (message != null) {
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(1500)
            onMessageDismiss()
            onBack()
        }
    }

    Scaffold(topBar = { InovaTopBar(if (project == null) "Novo Projeto" else "Editar Projeto", onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(title, { title = it }, label = { Text("Título *") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp))
            OutlinedTextField(description, { description = it }, label = { Text("Descrição *") }, modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(stage, { stage = it }, label = { Text("Etapa atual") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp))

            ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = it }) {
                OutlinedTextField(
                    value = selectedStatus.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(statusExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                    ProjectStatus.values().forEach { s ->
                        DropdownMenuItem(text = { Text(s.name) }, onClick = { selectedStatus = s; statusExpanded = false })
                    }
                }
            }

            Text("Dados Financeiros", fontWeight = FontWeight.Bold, color = NavyBlue)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(investment, { investment = it }, label = { Text("Investimento (R$)") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(expectedReturn, { expectedReturn = it }, label = { Text("Retorno esperado") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(actualReturn, { actualReturn = it }, label = { Text("Retorno real") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(costReduction, { costReduction = it }, label = { Text("Redução custo") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(productivityGain, { productivityGain = it }, label = { Text("Ganho produt. (%)") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(progress, { progress = it }, label = { Text("Progresso (0-100)") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp))
            }
            OutlinedTextField(notes, { notes = it }, label = { Text("Observações") }, modifier = Modifier.fillMaxWidth().height(90.dp), shape = RoundedCornerShape(12.dp))

            message?.let {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32))
                        Text(it, color = Color(0xFF1B5E20))
                    }
                }
            }

            Button(
                onClick = {
                    val p = (project ?: Project()).copy(
                        title = title, description = description, stage = stage,
                        status = selectedStatus,
                        investment = investment.toDoubleOrNull() ?: 0.0,
                        expectedReturn = expectedReturn.toDoubleOrNull() ?: 0.0,
                        actualReturn = actualReturn.toDoubleOrNull() ?: 0.0,
                        productivityGain = productivityGain.toDoubleOrNull() ?: 0.0,
                        costReduction = costReduction.toDoubleOrNull() ?: 0.0,
                        progress = progress.toIntOrNull()?.coerceIn(0, 100) ?: 0,
                        notes = notes,
                        updatedAt = System.currentTimeMillis()
                    )
                    onSave(p)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MediumBlue)
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                else { Icon(Icons.Default.Save, null); Spacer(Modifier.width(8.dp)); Text("Salvar Projeto", fontWeight = FontWeight.Bold) }
            }
        }
    }
}
