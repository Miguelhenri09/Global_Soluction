package br.com.inovagab.ui.leader

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
import br.com.inovagab.domain.model.*
import br.com.inovagab.ui.shared.*
import br.com.inovagab.ui.theme.MediumBlue
import br.com.inovagab.ui.theme.NavyBlue

// ── HOME ─────────────────────────────────────────────────────────────

@Composable
fun LeaderHomeScreen(
    uiState: LeaderUiState,
    dashboard: DashboardData,
    onNavigateProjects: () -> Unit,
    onNavigateDashboard: () -> Unit,
    onNavigateGuidelines: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(topBar = { InovaTopBar("InovaGAB — Liderança", onLogout = onLogout) }) { padding ->
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
                        Text(
                            "Olá, ${uiState.user?.name ?: "Liderança"}!",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Visão estratégica de inovação do Grupo",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // KPI row
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(dashboard.totalProjects.toString(), "Projetos\ntotais", MediumBlue, Modifier.weight(1f))
                    StatCard(dashboard.completedProjects.toString(), "Concluídos", Color(0xFF2E7D32), Modifier.weight(1f))
                    StatCard("${"%.1f".format(dashboard.roi)}%", "ROI\ngeral", Color(0xFFF57C00), Modifier.weight(1f))
                }
            }

            item { Text("Ações", fontWeight = FontWeight.Bold, color = NavyBlue, style = MaterialTheme.typography.titleMedium) }

            item {
                QuickActionCard(
                    Icons.Default.BarChart, "Dashboard Executivo",
                    "ROI, investimento, produtividade e resultados",
                    Color(0xFF00838F), onNavigateDashboard
                )
            }
            item {
                QuickActionCard(
                    Icons.Default.Assignment, "Andamento dos Projetos",
                    "Consulte etapa, status e retorno de cada projeto",
                    MediumBlue, onNavigateProjects
                )
            }
            item {
                QuickActionCard(
                    Icons.Default.Flag, "Diretrizes Estratégicas",
                    "Gerencie as orientações para toda a empresa",
                    Color(0xFF6A1B9A), onNavigateGuidelines
                )
            }
        }
    }
}

// ── PROJECTS (read-only for leader) ─────────────────────────────────

@Composable
fun LeaderProjectsScreen(
    projects: List<Project>,
    onBack: () -> Unit
) {
    Scaffold(topBar = { InovaTopBar("Andamento dos Projetos", onBack = onBack) }) { padding ->
        if (projects.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Nenhum projeto cadastrado", color = Color(0xFF90A4AE))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(projects) { project ->
                    LeaderProjectCard(project)
                }
            }
        }
    }
}

@Composable
fun LeaderProjectCard(project: Project) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(project.title, fontWeight = FontWeight.Bold, color = NavyBlue, modifier = Modifier.weight(1f))
                ProjectStatusBadge(project.status)
            }
            if (project.stage.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timeline, null, tint = MediumBlue, modifier = Modifier.size(14.dp))
                    Text("Etapa: ${project.stage}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF546E7A))
                }
            }

            LinearProgressIndicator(
                progress = project.progress / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = MediumBlue,
                trackColor = Color(0xFFE3F2FD)
            )
            Text("Progresso: ${project.progress}%", style = MaterialTheme.typography.labelSmall, color = Color(0xFF546E7A))

            Divider(color = Color(0xFFEEEEEE))

            // Financial info grid
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                FinancialItem("Investimento", "R$ ${"%,.0f".format(project.investment)}")
                FinancialItem("Retorno Real", "R$ ${"%,.0f".format(project.actualReturn)}")
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                FinancialItem("Redução Custo", "R$ ${"%,.0f".format(project.costReduction)}")
                FinancialItem("Ganho Produt.", "${"%.1f".format(project.productivityGain)}%")
            }

            if (project.notes.isNotBlank()) {
                Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        project.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF546E7A),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF90A4AE))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = NavyBlue)
    }
}

// ── DASHBOARD ────────────────────────────────────────────────────────

@Composable
fun LeaderDashboardScreen(
    dashboard: DashboardData,
    projects: List<Project>,
    onBack: () -> Unit
) {
    Scaffold(topBar = { InovaTopBar("Dashboard Executivo", onBack = onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Main KPI cards
            item {
                Text("Visão Geral", fontWeight = FontWeight.Bold, color = NavyBlue, style = MaterialTheme.typography.titleMedium)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(dashboard.totalProjects.toString(), "Total\nProjetos", MediumBlue, Modifier.weight(1f))
                    StatCard(dashboard.completedProjects.toString(), "Concluídos", Color(0xFF2E7D32), Modifier.weight(1f))
                    StatCard(dashboard.inProgressCount.toString(), "Em andamento", Color(0xFFF57C00), Modifier.weight(1f))
                }
            }

            // Financial KPIs
            item {
                Text("Resultados Financeiros", fontWeight = FontWeight.Bold, color = NavyBlue, style = MaterialTheme.typography.titleMedium)
            }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DashKpiRow(
                            label = "Investimento Total",
                            value = "R$ ${"%,.0f".format(dashboard.totalInvestment)}",
                            icon = Icons.Default.AttachMoney,
                            color = MediumBlue
                        )
                        DashKpiRow(
                            label = "Retorno Acumulado",
                            value = "R$ ${"%,.0f".format(dashboard.totalReturn)}",
                            icon = Icons.Default.TrendingUp,
                            color = Color(0xFF2E7D32)
                        )
                        DashKpiRow(
                            label = "ROI Geral",
                            value = "${"%.2f".format(dashboard.roi)}%",
                            icon = Icons.Default.ShowChart,
                            color = if (dashboard.roi >= 0) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                        )
                        DashKpiRow(
                            label = "Redução de Custos",
                            value = "R$ ${"%,.0f".format(dashboard.totalCostReduction)}",
                            icon = Icons.Default.Savings,
                            color = Color(0xFF6A1B9A)
                        )
                        DashKpiRow(
                            label = "Ganho Médio Produtividade",
                            value = "${"%.1f".format(dashboard.avgProductivityGain)}%",
                            icon = Icons.Default.Speed,
                            color = Color(0xFFF57C00)
                        )
                    }
                }
            }

            // Project breakdown
            item {
                Text("Retorno por Projeto", fontWeight = FontWeight.Bold, color = NavyBlue, style = MaterialTheme.typography.titleMedium)
            }
            items(projects) { project ->
                ProjectDashCard(project)
            }
        }
    }
}

@Composable
fun DashKpiRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Text(label, color = Color(0xFF546E7A), style = MaterialTheme.typography.bodyMedium)
        }
        Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 16.sp)
    }
}

@Composable
fun ProjectDashCard(project: Project) {
    val roi = if (project.investment > 0)
        ((project.actualReturn - project.investment) / project.investment) * 100
    else 0.0

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(project.title, fontWeight = FontWeight.SemiBold, color = NavyBlue, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                ProjectStatusBadge(project.status)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                MiniKpi("Invest.", "R$ ${"%,.0f".format(project.investment)}", MediumBlue)
                MiniKpi("Retorno", "R$ ${"%,.0f".format(project.actualReturn)}", Color(0xFF2E7D32))
                MiniKpi("ROI", "${"%.1f".format(roi)}%", if (roi >= 0) Color(0xFF2E7D32) else Color(0xFFD32F2F))
                MiniKpi("Produt.", "${"%.0f".format(project.productivityGain)}%", Color(0xFFF57C00))
            }
        }
    }
}

@Composable
fun MiniKpi(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF90A4AE))
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

// ── GUIDELINES MANAGEMENT (CRUD) ─────────────────────────────────────

@Composable
fun LeaderGuidelinesScreen(
    guidelines: List<StrategicGuideline>,
    onNewGuideline: () -> Unit,
    onEditGuideline: (String) -> Unit,
    onDeleteGuideline: (String) -> Unit,
    onBack: () -> Unit
) {
    var deleteTarget by remember { mutableStateOf<StrategicGuideline?>(null) }

    Scaffold(
        topBar = { InovaTopBar("Diretrizes Estratégicas", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewGuideline, containerColor = Color(0xFF6A1B9A)) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        if (guidelines.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Flag, null, modifier = Modifier.size(64.dp), tint = Color(0xFFB0BEC5))
                    Spacer(Modifier.height(8.dp))
                    Text("Nenhuma diretriz cadastrada", color = Color(0xFF90A4AE))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(guidelines) { g ->
                    GuidelineCard(
                        guideline = g,
                        onEdit = { onEditGuideline(g.id) },
                        onDelete = { deleteTarget = g }
                    )
                }
            }
        }
    }

    deleteTarget?.let { g ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Excluir diretriz?") },
            text = { Text("\"${g.title}\" será removida permanentemente.") },
            confirmButton = {
                Button(
                    onClick = { onDeleteGuideline(g.id); deleteTarget = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun GuidelineCard(guideline: StrategicGuideline, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Flag, null, tint = Color(0xFF6A1B9A))
                    Text(guideline.title, fontWeight = FontWeight.Bold, color = NavyBlue)
                }
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = MediumBlue) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color(0xFFD32F2F)) }
                }
            }
            Text(guideline.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF546E7A))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFFF3E5F5), shape = RoundedCornerShape(50)) {
                    Text(guideline.category, color = Color(0xFF6A1B9A), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
                Text("${guideline.year}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF90A4AE))
            }
        }
    }
}

// ── GUIDELINE FORM ───────────────────────────────────────────────────

@Composable
fun LeaderGuidelineFormScreen(
    guideline: StrategicGuideline?,
    currentUserId: String,
    isLoading: Boolean,
    message: String?,
    onSave: (StrategicGuideline) -> Unit,
    onBack: () -> Unit,
    onMessageDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(guideline?.title ?: "") }
    var description by remember { mutableStateOf(guideline?.description ?: "") }
    var category by remember { mutableStateOf(guideline?.category ?: "") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var year by remember { mutableStateOf(guideline?.year?.toString() ?: "2025") }

    val categories = listOf("Estratégia", "Operacional", "Sustentabilidade", "Tecnologia", "Pessoas", "Financeiro", "Inovação")

    if (message != null) {
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(1500)
            onMessageDismiss()
            onBack()
        }
    }

    Scaffold(topBar = { InovaTopBar(if (guideline == null) "Nova Diretriz" else "Editar Diretriz", onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título da diretriz *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição *") },
                modifier = Modifier.fillMaxWidth().height(130.dp),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
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
                value = year,
                onValueChange = { year = it },
                label = { Text("Ano") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
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
                onClick = {
                    val g = (guideline ?: StrategicGuideline()).copy(
                        title = title,
                        description = description,
                        category = category,
                        year = year.toIntOrNull() ?: 2025,
                        createdBy = currentUserId,
                        createdAt = guideline?.createdAt ?: System.currentTimeMillis()
                    )
                    onSave(g)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank() && category.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Salvar Diretriz", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
