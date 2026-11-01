package com.newbet.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newbet.app.data.affiliate.AffiliateManager
import com.newbet.app.data.affiliate.AttributionSource
import com.newbet.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    affiliateManager: AffiliateManager,
    onRegistrationSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    var regType by remember { mutableStateOf("one_click") } // one_click, email
    var selectedCountry by remember { mutableStateOf("Bénin (XOF)") }

    // User inputs
    var fullNameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var optionalPhoneInput by remember { mutableStateOf("") }

    // Auto-filled promo code from affiliate tracking
    val initialPromo = remember { affiliateManager.getSavedPromoCode().ifEmpty { "NEWBET2026" } }
    var promoCodeInput by remember { mutableStateOf(initialPromo) }
    var promoValidation by remember { mutableStateOf(affiliateManager.validatePromoCode(initialPromo)) }

    var selectedBonus by remember { mutableStateOf("sport") } // sport, casino
    var isSubmitting by remember { mutableStateOf(false) }

    // Modal state for instant 1-click generated credentials
    var showCredentialsDialog by remember { mutableStateOf(false) }
    var generatedId by remember { mutableStateOf("") }
    var generatedPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCanvas)
            .statusBarsPadding()
    ) {
        // Unibet Header
        Surface(color = UnibetGreenHeader, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = TextWhite
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "INSCRIPTION RAPIDE",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "BONUS 200% SUR PREMIER DÉPÔT",
                        color = UnibetGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(onClick = {
                    Toast.makeText(context, "Support en direct WhatsApp disponible 24/7", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Aide", color = UnibetGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Welcome Title
            Text(
                text = "Créer votre compte joueur",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Inscription ultra-rapide sans confirmation téléphonique",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. GOOGLE ONE-TAP BUTTON (Requested: direct Google Mail auth)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        isSubmitting = true
                        affiliateManager.saveAttribution(promoCodeInput, AttributionSource.MANUAL_PROMO)
                        Toast.makeText(
                            context,
                            "Connexion Google réussie ! Bienvenue sur Newbet !",
                            Toast.LENGTH_SHORT
                        ).show()
                        onRegistrationSuccess()
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4285F4).copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Google Multi-color Icon Representation
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "CONTINUER AVEC GOOGLE",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Connexion instantanée avec votre compte Gmail",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider: "OU INSCRIVEZ-VOUS"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSubtle)
                Text(
                    text = "  OU PAR MÉTHODE RAPIDE  ",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSubtle)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. REGISTRATION METHOD TABS (1-Clic vs Par Email)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RegTypeTab(
                    title = "⚡ En 1 Clic (Sans friction)",
                    subtitle = "Accès immédiat sans mot de passe",
                    isSelected = regType == "one_click",
                    onClick = { regType = "one_click" },
                    modifier = Modifier.weight(1f)
                )

                RegTypeTab(
                    title = "✉️ Par Email",
                    subtitle = "Avec mot de passe personnel",
                    isSelected = regType == "email",
                    onClick = { regType = "email" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- TAB CONTENT 1: EN 1 CLIC ---
            if (regType == "one_click") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚡", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Inscription en 1 Clic instantanée",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Un identifiant de compte et un mot de passe sécurisé seront créés automatiquement.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Country & Currency
                        OutlinedTextField(
                            value = selectedCountry,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pays & Devise", color = TextSecondary, fontSize = 11.sp) },
                            leadingIcon = { Text("🇧🇯", fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardSurfaceElevated,
                                unfocusedContainerColor = CardSurfaceElevated,
                                focusedBorderColor = UnibetGreenAccent,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Full Name (Optional for 1-click)
                        OutlinedTextField(
                            value = fullNameInput,
                            onValueChange = { fullNameInput = it },
                            label = { Text("Nom complet (Optionnel)", color = TextSecondary, fontSize = 11.sp) },
                            placeholder = { Text("Ex: Christian NGK", color = TextMuted) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardSurfaceElevated,
                                unfocusedContainerColor = CardSurfaceElevated,
                                focusedBorderColor = UnibetGreenAccent,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Phone Number (Optional, NO SMS confirmation required)
                        OutlinedTextField(
                            value = optionalPhoneInput,
                            onValueChange = { optionalPhoneInput = it },
                            label = { Text("Téléphone Mobile Money (Optionnel, sans confirmation SMS)", color = TextSecondary, fontSize = 11.sp) },
                            placeholder = { Text("Ex: 97 00 00 00", color = TextMuted) },
                            leadingIcon = { Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardSurfaceElevated,
                                unfocusedContainerColor = CardSurfaceElevated,
                                focusedBorderColor = UnibetGreenAccent,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }
                }
            }

            // --- TAB CONTENT 2: PAR EMAIL ---
            if (regType == "email") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✉️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Inscription avec adresse email",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Renseignez votre email pour recevoir vos confirmations de gains.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Email
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Adresse Email (Gmail, Yahoo...)", color = TextSecondary, fontSize = 11.sp) },
                            placeholder = { Text("votre.email@gmail.com", color = TextMuted) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardSurfaceElevated,
                                unfocusedContainerColor = CardSurfaceElevated,
                                focusedBorderColor = UnibetGreenAccent,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Full Name
                        OutlinedTextField(
                            value = fullNameInput,
                            onValueChange = { fullNameInput = it },
                            label = { Text("Nom & Prénom", color = TextSecondary, fontSize = 11.sp) },
                            placeholder = { Text("Christian NGK", color = TextMuted) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardSurfaceElevated,
                                unfocusedContainerColor = CardSurfaceElevated,
                                focusedBorderColor = UnibetGreenAccent,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Mot de passe", color = TextSecondary, fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = TextMuted
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardSurfaceElevated,
                                unfocusedContainerColor = CardSurfaceElevated,
                                focusedBorderColor = UnibetGreenAccent,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. PROMO CODE CARD (Attribution & Bonus 200%)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (promoValidation.isValid) UnibetNeon else BorderSubtle
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "CODE PROMO PARTENAIRE / PARRAIN",
                            color = if (promoValidation.isValid) UnibetNeon else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        if (promoValidation.isValid) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = UnibetNeon,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Code Validé (+200%)",
                                    color = UnibetNeon,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = promoCodeInput,
                        onValueChange = {
                            promoCodeInput = it.uppercase()
                            promoValidation = affiliateManager.validatePromoCode(it)
                        },
                        placeholder = { Text("Ex: NEWBET2026", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = UnibetGold)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardSurfaceElevated,
                            unfocusedContainerColor = CardSurfaceElevated,
                            focusedBorderColor = UnibetGreenAccent,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. WELCOME BONUS CHOOSER (Sport 200% vs Casino Free Spins)
            Text(
                text = "CHOISISSEZ VOTRE BONUS DE BIENVENUE",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BonusChoiceCard(
                    title = "Bonus Sport",
                    badge = "+200% Offert",
                    detail = "Jusqu'à 250 000 F CFA",
                    isSelected = selectedBonus == "sport",
                    onClick = { selectedBonus = "sport" },
                    modifier = Modifier.weight(1f)
                )

                BonusChoiceCard(
                    title = "Bonus Casino",
                    badge = "+150 Tours",
                    detail = "Sur Sweet Bonanza",
                    isSelected = selectedBonus == "casino",
                    onClick = { selectedBonus = "casino" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. MAIN CTA SUBMIT BUTTON
            Button(
                onClick = {
                    isSubmitting = true
                    affiliateManager.saveAttribution(promoCodeInput, AttributionSource.MANUAL_PROMO)

                    if (regType == "one_click") {
                        // Generate credentials immediately
                        val suffix = (100000..999999).random()
                        generatedId = "NB-$suffix"
                        generatedPassword = "NB_" + (1000..9999).random()
                        showCredentialsDialog = true
                    } else {
                        // Email registration
                        Toast.makeText(context, "Compte Newbet créé ! Bonus 200% débloqué !", Toast.LENGTH_LONG).show()
                        onRegistrationSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = UnibetGold,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = if (regType == "one_click") "CRÉER MON COMPTE EN 1 CLIC" else "S'INSCRIRE AVEC EMAIL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "En vous inscrivant, vous confirmez avoir au moins 18 ans et accepter le règlement de Newbet. Jeu responsable uniquement.",
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Credentials Dialog for 1-Click Registration (ID + Password Copy)
    if (showCredentialsDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎉", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compte Créé en 1 Clic !", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            },
            text = {
                Column {
                    Text(
                        "Bienvenue sur Newbet ! Conservez précieusement vos identifiants générés :",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // ID Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardSurfaceElevated)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Votre Identifiant Joueur", color = TextMuted, fontSize = 10.sp)
                            Text(generatedId, color = UnibetGold, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                        Button(
                            onClick = {
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("ID", generatedId))
                                Toast.makeText(context, "ID $generatedId copié !", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = UnibetGreenAccent, contentColor = TextWhite),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("COPIER", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardSurfaceElevated)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Mot de passe automatique", color = TextMuted, fontSize = 10.sp)
                            Text(generatedPassword, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("Password", generatedPassword))
                                Toast.makeText(context, "Mot de passe copié !", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = UnibetGreenAccent, contentColor = TextWhite),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("COPIER", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(UnibetGreenDark)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🎁 Bonus 200% de 50 000 F CFA activé automatiquement !",
                            color = UnibetGreenHover,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCredentialsDialog = false
                        onRegistrationSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UnibetGold, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ACCÉDER AUX PARIS SPORTIFS", fontWeight = FontWeight.Black)
                }
            },
            containerColor = CardSurface
        )
    }
}

@Composable
private fun RegTypeTab(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) UnibetGreenAccent else CardSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) UnibetNeon else BorderSubtle
        ),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = if (isSelected) TextWhite else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextMuted,
                fontSize = 9.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun BonusChoiceCard(
    title: String,
    badge: String,
    detail: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) UnibetGreenDark else CardSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) UnibetGold else BorderSubtle
        ),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(badge, color = UnibetGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
            Text(detail, color = TextMuted, fontSize = 9.sp)
        }
    }
}
