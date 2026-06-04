package br.com.inovagab.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.inovagab.domain.model.IdeaStatus
import br.com.inovagab.domain.model.ProjectStatus
import br.com.inovagab.ui.theme.MediumBlue
import br.com.inovagab.ui.theme.NavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InovaTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    onLogout: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold, color = Color.White) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
            }
        },
        actions = {
            if (onLogout != null) {
                IconButton(onClick = onLogout) {
                    Icon(Icons.Default.Logout, "Sair", tint = Color.White)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
    )
}

@Composable
fun StatCard(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(color, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
            )
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF546E7A))
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(26.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, color = NavyBlue)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF78909C))
            }
        }
    }
}

@Composable
fun IdeaStatusBadge(status: IdeaStatus) {
    val (label, color) = when (status) {
        IdeaStatus.PENDING -> "Pendente" to Color(0xFFF57C00)
        IdeaStatus.UNDER_REVIEW -> "Em análise" to Color(0xFF1565C0)
        IdeaStatus.APPROVED -> "Aprovada" to Color(0xFF2E7D32)
        IdeaStatus.REJECTED -> "Rejeitada" to Color(0xFFD32F2F)
        IdeaStatus.CONVERTED_TO_PROJECT -> "Projeto" to Color(0xFF6A1B9A)
    }
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ProjectStatusBadge(status: ProjectStatus) {
    val (label, color) = when (status) {
        ProjectStatus.PLANNING -> "Planejamento" to Color(0xFF1565C0)
        ProjectStatus.IN_PROGRESS -> "Em andamento" to Color(0xFFF57C00)
        ProjectStatus.ON_HOLD -> "Em espera" to Color(0xFF546E7A)
        ProjectStatus.COMPLETED -> "Concluído" to Color(0xFF2E7D32)
        ProjectStatus.CANCELLED -> "Cancelado" to Color(0xFFD32F2F)
    }
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
