import express from 'express';
import http from 'http';
import cors from 'cors';
import jwt from 'jsonwebtoken';
import { WebSocketServer, WebSocket } from 'ws';
import { db } from './database.js';

const JWT_SECRET = process.env.JWT_SECRET || 'newbet_super_secure_jwt_secret_2026';
const PORT = process.env.PORT || 4000;

const app = express();
const server = http.createServer(app);
const wss = new WebSocketServer({ server });

app.use(cors());
app.use(express.json());

// --- Authentication Middleware ---
function authenticateJWT(req, res, next) {
  const authHeader = req.headers.authorization;
  if (authHeader && authHeader.startsWith('Bearer ')) {
    const token = authHeader.substring(7);
    jwt.verify(token, JWT_SECRET, (err, decoded) => {
      if (err) {
        return res.status(403).json({ error: 'Token invalide ou expiré' });
      }
      req.user = decoded;
      next();
    });
  } else {
    // Default fallback to demo user for frictionless client experience
    req.user = { id: 'NB-948210', email: 'christian@newbet.bj' };
    next();
  }
}

// ==========================================
// 1. AUTHENTICATION ROUTES (1-CLIC, GOOGLE, EMAIL)
// ==========================================

/**
 * 1-CLIC REGISTRATION (Zéro Friction, Pas de confirmation de numéro)
 */
app.post('/api/auth/one-click', (req, res) => {
  const { country = 'Bénin', currency = 'XOF', promoCode = 'NEWBET2026', name, phone } = req.body;

  // Generate unique User ID e.g. NB-482910
  const randomSuffix = Math.floor(100000 + Math.random() * 900000);
  const userId = `NB-${randomSuffix}`;
  const autoPassword = `NB_${Math.random().toString(36).substring(2, 10).toUpperCase()}`;

  const displayName = name && name.trim().length > 0 ? name.trim() : `Joueur ${randomSuffix}`;

  const newUser = {
    id: userId,
    username: displayName,
    email: `${userId.toLowerCase()}@player.newbet.bj`,
    phone: phone || null,
    country,
    currency,
    authProvider: 'one_click',
    password: autoPassword,
    balanceXOF: 25000,
    bonusXOF: 50000, // 200% Welcome bonus
    promoCodeUsed: promoCode || 'NEWBET2026',
    referralCode: `NEW-${randomSuffix.toString().slice(-4)}`,
    kycLevel: 'NIVEAU 1 • ENREGISTRÉ',
    createdAt: new Date().toISOString()
  };

  db.createUser(newUser);

  // Generate JWT token
  const token = jwt.sign(
    { id: newUser.id, username: newUser.username, email: newUser.email },
    JWT_SECRET,
    { expiresIn: '30d' }
  );

  return res.status(201).json({
    success: true,
    message: 'Compte 1-Clic créé instantanément !',
    token,
    credentials: {
      userId: newUser.id,
      password: autoPassword
    },
    user: newUser
  });
});

/**
 * GOOGLE SIGN-IN / SIGN-UP
 */
app.post('/api/auth/google', (req, res) => {
  const { email, name, googleId, promoCode } = req.body;

  if (!email) {
    return res.status(400).json({ error: 'Adresse email Google requise' });
  }

  let user = db.findUserByEmail(email);

  if (!user) {
    // Auto-create user from Google profile
    const randomSuffix = Math.floor(100000 + Math.random() * 900000);
    user = {
      id: `NB-${randomSuffix}`,
      username: name || email.split('@')[0],
      email: email.toLowerCase(),
      googleId: googleId || `goog_${Date.now()}`,
      authProvider: 'google',
      balanceXOF: 30000,
      bonusXOF: 50000,
      promoCodeUsed: promoCode || 'NEWBET2026',
      referralCode: `NEW-${randomSuffix.toString().slice(-4)}`,
      kycLevel: 'NIVEAU 2 • VÉRIFIÉ (GOOGLE)',
      createdAt: new Date().toISOString()
    };
    db.createUser(user);
  }

  const token = jwt.sign(
    { id: user.id, username: user.username, email: user.email },
    JWT_SECRET,
    { expiresIn: '30d' }
  );

  return res.json({
    success: true,
    message: 'Connexion Google réussie !',
    token,
    user
  });
});

/**
 * EMAIL REGISTRATION
 */
app.post('/api/auth/register-email', (req, res) => {
  const { email, password, name, promoCode } = req.body;

  if (!email || !password) {
    return res.status(400).json({ error: 'Email et mot de passe requis' });
  }

  const existing = db.findUserByEmail(email);
  if (existing) {
    return res.status(409).json({ error: 'Un compte avec cet email existe déjà' });
  }

  const randomSuffix = Math.floor(100000 + Math.random() * 900000);
  const newUser = {
    id: `NB-${randomSuffix}`,
    username: name || email.split('@')[0],
    email: email.toLowerCase(),
    password, // in prod Argon2/bcrypt
    authProvider: 'email',
    balanceXOF: 25000,
    bonusXOF: 50000,
    promoCodeUsed: promoCode || 'NEWBET2026',
    referralCode: `NEW-${randomSuffix.toString().slice(-4)}`,
    kycLevel: 'NIVEAU 1 • EMAIL VÉRIFIÉ',
    createdAt: new Date().toISOString()
  };

  db.createUser(newUser);

  const token = jwt.sign(
    { id: newUser.id, username: newUser.username, email: newUser.email },
    JWT_SECRET,
    { expiresIn: '30d' }
  );

  return res.status(201).json({
    success: true,
    message: 'Compte créé avec succès !',
    token,
    user: newUser
  });
});

/**
 * LOGIN (Email or User ID + Password)
 */
app.post('/api/auth/login', (req, res) => {
  const { identifier, password } = req.body;

  if (!identifier) {
    return res.status(400).json({ error: 'Identifiant requis' });
  }

  let user = null;
  if (identifier.includes('@')) {
    user = db.findUserByEmail(identifier);
  } else {
    user = db.findUserById(identifier);
  }

  if (!user) {
    return res.status(404).json({ error: 'Utilisateur introuvable' });
  }

  const token = jwt.sign(
    { id: user.id, username: user.username, email: user.email },
    JWT_SECRET,
    { expiresIn: '30d' }
  );

  return res.json({
    success: true,
    message: 'Connexion réussie',
    token,
    user
  });
});

/**
 * CURRENT USER PROFILE & BALANCES
 */
app.get('/api/auth/me', authenticateJWT, (req, res) => {
  const user = db.findUserById(req.user.id) || db.findUserById('NB-948210');
  if (!user) {
    return res.status(404).json({ error: 'Utilisateur introuvable' });
  }
  return res.json({ success: true, user });
});

// ==========================================
// 2. WALLET & MOBILE MONEY CASHIER ROUTES
// ==========================================

app.get('/api/wallet/balance', authenticateJWT, (req, res) => {
  const user = db.findUserById(req.user.id) || db.findUserById('NB-948210');
  return res.json({
    balanceXOF: user.balanceXOF,
    bonusXOF: user.bonusXOF
  });
});

/**
 * DEPOSIT VIA MOBILE MONEY (Wave, MTN, Orange, Moov)
 */
app.post('/api/wallet/deposit', authenticateJWT, (req, res) => {
  const { amountXOF, method = 'Wave', phoneNumber } = req.body;
  const amount = Number(amountXOF);

  if (!amount || amount < 500) {
    return res.status(400).json({ error: 'Le montant minimum de dépôt est de 500 F CFA' });
  }

  const user = db.findUserById(req.user.id) || db.findUserById('NB-948210');
  const newBalance = user.balanceXOF + amount;

  db.updateUser(user.id, { balanceXOF: newBalance });

  const tx = {
    id: `TX-${Date.now().toString().slice(-6)}`,
    userId: user.id,
    type: 'DEPOSIT',
    amountXOF: amount,
    method,
    phoneNumber: phoneNumber || null,
    status: 'COMPLETED',
    reference: `${method.toUpperCase()}-${Math.random().toString(36).substring(2, 8).toUpperCase()}`,
    createdAt: new Date().toISOString()
  };

  db.addTransaction(tx);

  return res.json({
    success: true,
    message: `Dépôt de ${amount.toLocaleString('fr-FR')} F CFA via ${method} validé instantanément !`,
    transaction: tx,
    newBalanceXOF: newBalance
  });
});

/**
 * WITHDRAW VIA MOBILE MONEY
 */
app.post('/api/wallet/withdraw', authenticateJWT, (req, res) => {
  const { amountXOF, method = 'Wave', phoneNumber } = req.body;
  const amount = Number(amountXOF);

  const user = db.findUserById(req.user.id) || db.findUserById('NB-948210');

  if (amount > user.balanceXOF) {
    return res.status(400).json({ error: 'Solde retirable insuffisant' });
  }

  const newBalance = user.balanceXOF - amount;
  db.updateUser(user.id, { balanceXOF: newBalance });

  const tx = {
    id: `TX-${Date.now().toString().slice(-6)}`,
    userId: user.id,
    type: 'WITHDRAW',
    amountXOF: amount,
    method,
    phoneNumber,
    status: 'COMPLETED',
    reference: `WDR-${Math.random().toString(36).substring(2, 8).toUpperCase()}`,
    createdAt: new Date().toISOString()
  };

  db.addTransaction(tx);

  return res.json({
    success: true,
    message: `Retrait de ${amount.toLocaleString('fr-FR')} F CFA envoyé vers le ${phoneNumber} !`,
    transaction: tx,
    newBalanceXOF: newBalance
  });
});

app.get('/api/wallet/transactions', authenticateJWT, (req, res) => {
  const user = db.findUserById(req.user.id) || db.findUserById('NB-948210');
  const txs = db.getUserTransactions(user.id);
  return res.json({ success: true, transactions: txs });
});

// ==========================================
// 3. SPORTS FIXTURES & ODDS ROUTES
// ==========================================

app.get('/api/sports/fixtures', (req, res) => {
  const fixtures = db.getFixtures();
  return res.json({
    success: true,
    total: fixtures.length,
    fixtures
  });
});

// ==========================================
// 4. BETS & CASHOUT ENGINE
// ==========================================

app.get('/api/bets/my-bets', authenticateJWT, (req, res) => {
  const user = db.findUserById(req.user.id) || db.findUserById('NB-948210');
  const bets = db.getUserBets(user.id);
  return res.json({ success: true, bets });
});

app.post('/api/bets/place', authenticateJWT, (req, res) => {
  const { selections, stakeXOF } = req.body;
  const stake = Number(stakeXOF);

  if (!selections || selections.length === 0 || !stake || stake <= 0) {
    return res.status(400).json({ error: 'Sélections et mise valide requises' });
  }

  const user = db.findUserById(req.user.id) || db.findUserById('NB-948210');

  if (user.balanceXOF < stake) {
    return res.status(400).json({ error: 'Solde insuffisant pour placer ce pari' });
  }

  const totalOdds = selections.reduce((acc, s) => acc * s.odds, 1.0);
  const potentialWinXOF = Math.round(stake * totalOdds);
  const newBalance = user.balanceXOF - stake;

  db.updateUser(user.id, { balanceXOF: newBalance });

  const bet = {
    id: `BET-${Math.floor(10000 + Math.random() * 90000)}`,
    userId: user.id,
    date: "Aujourd'hui, " + new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }),
    selections,
    stakeXOF: stake,
    totalOdds: Number(totalOdds.toFixed(2)),
    potentialWinXOF,
    isLive: selections.some(s => s.matchTitle && s.matchTitle.toLowerCase().includes('live')),
    canCashOut: true,
    cashOutAmountXOF: Math.round(stake * 0.95),
    status: 'PENDING',
    createdAt: new Date().toISOString()
  };

  db.addBet(bet);

  return res.status(201).json({
    success: true,
    message: `Pari de ${stake.toLocaleString('fr-FR')} F CFA placé avec succès !`,
    bet,
    newBalanceXOF: newBalance
  });
});

app.post('/api/bets/cashout/:id', authenticateJWT, (req, res) => {
  const betId = req.params.id;
  const user = db.findUserById(req.user.id) || db.findUserById('NB-948210');

  const bets = db.getUserBets(user.id);
  const bet = bets.find(b => b.id === betId);

  if (!bet) {
    return res.status(404).json({ error: 'Pari introuvable' });
  }

  if (bet.status !== 'PENDING' || !bet.canCashOut) {
    return res.status(400).json({ error: 'Ce pari ne peut plus faire l\'objet d\'un Cash Out' });
  }

  const cashOutAmount = bet.cashOutAmountXOF;
  const newBalance = user.balanceXOF + cashOutAmount;

  db.updateUser(user.id, { balanceXOF: newBalance });
  db.updateBet(bet.id, { status: 'CASHED_OUT', canCashOut: false });

  return res.json({
    success: true,
    message: `Cash Out de ${cashOutAmount.toLocaleString('fr-FR')} F CFA validé !`,
    cashedAmountXOF: cashOutAmount,
    newBalanceXOF: newBalance
  });
});

// ==========================================
// 5. CASINO AVIATOR CRASH ENGINE
// ==========================================

app.post('/api/casino/aviator/bet', authenticateJWT, (req, res) => {
  const { amountXOF } = req.body;
  const amount = Number(amountXOF);
  const user = db.findUserById(req.user.id) || db.findUserById('NB-948210');

  if (user.balanceXOF < amount) {
    return res.status(400).json({ error: 'Solde insuffisant' });
  }

  const newBalance = user.balanceXOF - amount;
  db.updateUser(user.id, { balanceXOF: newBalance });

  return res.json({ success: true, newBalanceXOF: newBalance });
});

app.post('/api/casino/aviator/cashout', authenticateJWT, (req, res) => {
  const { winAmountXOF } = req.body;
  const win = Number(winAmountXOF);
  const user = db.findUserById(req.user.id) || db.findUserById('NB-948210');

  const newBalance = user.balanceXOF + win;
  db.updateUser(user.id, { balanceXOF: newBalance });

  return res.json({ success: true, winAmountXOF: win, newBalanceXOF: newBalance });
});

// ==========================================
// 6. WEBSOCKET REAL-TIME LIVE STREAMING
// ==========================================

wss.on('connection', (ws) => {
  console.log('[WebSocket] Client Android connecté');

  // Send initial welcome message
  ws.send(JSON.stringify({ type: 'CONNECTED', message: 'Newbet Live Feed Connecté' }));

  ws.on('close', () => {
    console.log('[WebSocket] Client Android déconnecté');
  });
});

// Broadcast live odds tick to all connected apps every 10 seconds
setInterval(() => {
  const fixtures = db.getFixtures();
  const liveMatch = fixtures.find(f => f.isLive);
  if (liveMatch && wss.clients.size > 0) {
    // Fluctuating live odds
    const delta = (Math.random() * 0.08 - 0.04);
    liveMatch.oddsHome = Number(Math.max(1.10, liveMatch.oddsHome + delta).toFixed(2));
    db.save();

    const payload = JSON.stringify({
      type: 'ODDS_UPDATE',
      eventId: liveMatch.id,
      oddsHome: liveMatch.oddsHome,
      minute: liveMatch.minute
    });

    wss.clients.forEach(client => {
      if (client.readyState === WebSocket.OPEN) {
        client.send(payload);
      }
    });
  }
}, 10000);

// Start Server
server.listen(PORT, '0.0.0.0', () => {
  console.log(`===============================================`);
  console.log(`🚀 SERVEUR NEWBET BACKEND DÉMARRÉ SUR PORT ${PORT}`);
  console.log(`📡 API REST & AUTH: http://localhost:${PORT}/api`);
  console.log(`⚡ WEBSOCKET FEED : ws://localhost:${PORT}`);
  console.log(`===============================================`);
});

