import express from 'express';
import cors from 'cors';
import jwt from 'jsonwebtoken';
import { supabase } from '../supabaseClient.js';

const JWT_SECRET = process.env.JWT_SECRET || 'newbet_super_secure_jwt_secret_2026';

const app = express();
app.use(cors({ origin: '*' }));
app.use(express.json());

// --- Authentication Middleware ---
async function authenticateUser(req, res, next) {
  const authHeader = req.headers.authorization;
  if (authHeader && authHeader.startsWith('Bearer ')) {
    const token = authHeader.substring(7);
    try {
      const decoded = jwt.verify(token, JWT_SECRET);
      req.user = decoded;
      return next();
    } catch (e) {
      // invalid token
    }
  }
  // Default fallback user for testing
  req.user = { id: 'NB-849201', email: 'christian@newbet.com' };
  next();
}

// ----------------------------------------------------
// Health Check
// ----------------------------------------------------
app.get('/api/health', async (req, res) => {
  try {
    const { count, error } = await supabase.from('fixtures').select('*', { count: 'exact', head: true });
    return res.json({
      status: 'online',
      service: 'Newbet Cloud API',
      database: 'Supabase PostgreSQL Cloud',
      connected: !error,
      fixtures_count: count || 0,
      timestamp: new Date().toISOString()
    });
  } catch (err) {
    return res.status(500).json({ status: 'error', error: err.message });
  }
});

// ----------------------------------------------------
// 1. AUTHENTICATION (1-CLIC, GOOGLE, EMAIL)
// ----------------------------------------------------

// 1-CLIC REGISTRATION
app.post('/api/auth/one-click', async (req, res) => {
  try {
    const { country = 'Bénin', currency = 'XOF', promoCode = 'NEWBET200', name, phone } = req.body;
    const randomSuffix = Math.floor(100000 + Math.random() * 900000);
    const userIdDisplay = `NB-${randomSuffix}`;
    const autoPassword = `NB_${Math.random().toString(36).substring(2, 10).toUpperCase()}`;
    const displayName = name && name.trim().length > 0 ? name.trim() : `Joueur ${randomSuffix}`;
    const referralCode = `NEW-${randomSuffix.toString().slice(-4)}`;

    const { data, error } = await supabase.from('profiles').insert([
      {
        user_id_display: userIdDisplay,
        email: `${userIdDisplay.toLowerCase()}@player.newbet.bj`,
        phone: phone || null,
        full_name: displayName,
        auth_provider: 'one_click',
        balance_xof: 25000,
        bonus_xof: 50000, // 200% Welcome bonus
        currency: currency,
        promo_code: promoCode || 'NEWBET200',
        referral_code: referralCode,
        vip_level: 'BRONZE',
        kyc_status: 'VERIFIED'
      }
    ]).select().single();

    if (error) {
      return res.status(500).json({ error: error.message });
    }

    const token = jwt.sign(
      { id: data.user_id_display, email: data.email, full_name: data.full_name },
      JWT_SECRET,
      { expiresIn: '30d' }
    );

    return res.status(201).json({
      success: true,
      message: 'Compte 1-Clic créé instantanément !',
      token,
      credentials: {
        userId: userIdDisplay,
        password: autoPassword
      },
      user: {
        id: data.user_id_display,
        username: data.full_name,
        email: data.email,
        phone: data.phone,
        balanceXOF: Number(data.balance_xof),
        bonusXOF: Number(data.bonus_xof),
        referralCode: data.referral_code,
        kycLevel: data.kyc_status
      }
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// GOOGLE SIGN-IN / SIGN-UP
app.post('/api/auth/google', async (req, res) => {
  try {
    const { email, name, googleId, promoCode } = req.body;
    if (!email) {
      return res.status(400).json({ error: 'Email Google requis' });
    }

    // Check existing
    let { data: existingUser } = await supabase
      .from('profiles')
      .select('*')
      .eq('email', email.toLowerCase())
      .maybeSingle();

    if (!existingUser) {
      const randomSuffix = Math.floor(100000 + Math.random() * 900000);
      const userIdDisplay = `NB-${randomSuffix}`;
      const referralCode = `NEW-${randomSuffix.toString().slice(-4)}`;

      const { data: newUser, error } = await supabase.from('profiles').insert([
        {
          user_id_display: userIdDisplay,
          email: email.toLowerCase(),
          full_name: name || email.split('@')[0],
          auth_provider: 'google',
          balance_xof: 30000,
          bonus_xof: 50000,
          promo_code: promoCode || 'NEWBET200',
          referral_code: referralCode,
          vip_level: 'SILVER',
          kyc_status: 'VERIFIED'
        }
      ]).select().single();

      if (error) {
        return res.status(500).json({ error: error.message });
      }
      existingUser = newUser;
    }

    const token = jwt.sign(
      { id: existingUser.user_id_display, email: existingUser.email, full_name: existingUser.full_name },
      JWT_SECRET,
      { expiresIn: '30d' }
    );

    return res.json({
      success: true,
      message: 'Connexion Google réussie !',
      token,
      user: {
        id: existingUser.user_id_display,
        username: existingUser.full_name,
        email: existingUser.email,
        balanceXOF: Number(existingUser.balance_xof),
        bonusXOF: Number(existingUser.bonus_xof),
        referralCode: existingUser.referral_code,
        kycLevel: 'NIVEAU 2 • VÉRIFIÉ (GOOGLE)'
      }
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// EMAIL REGISTRATION
app.post('/api/auth/register-email', async (req, res) => {
  try {
    const { email, password, name, promoCode } = req.body;
    if (!email || !password) {
      return res.status(400).json({ error: 'Email et mot de passe requis' });
    }

    const { data: existing } = await supabase
      .from('profiles')
      .select('id')
      .eq('email', email.toLowerCase())
      .maybeSingle();

    if (existing) {
      return res.status(409).json({ error: 'Un compte avec cet email existe déjà' });
    }

    const randomSuffix = Math.floor(100000 + Math.random() * 900000);
    const userIdDisplay = `NB-${randomSuffix}`;
    const referralCode = `NEW-${randomSuffix.toString().slice(-4)}`;

    const { data, error } = await supabase.from('profiles').insert([
      {
        user_id_display: userIdDisplay,
        email: email.toLowerCase(),
        full_name: name || email.split('@')[0],
        auth_provider: 'email',
        balance_xof: 25000,
        bonus_xof: 50000,
        promo_code: promoCode || 'NEWBET200',
        referral_code: referralCode,
        vip_level: 'BRONZE',
        kyc_status: 'VERIFIED'
      }
    ]).select().single();

    if (error) {
      return res.status(500).json({ error: error.message });
    }

    const token = jwt.sign(
      { id: data.user_id_display, email: data.email, full_name: data.full_name },
      JWT_SECRET,
      { expiresIn: '30d' }
    );

    return res.status(201).json({
      success: true,
      message: 'Compte créé avec succès !',
      token,
      user: {
        id: data.user_id_display,
        username: data.full_name,
        email: data.email,
        balanceXOF: Number(data.balance_xof),
        bonusXOF: Number(data.bonus_xof),
        referralCode: data.referral_code,
        kycLevel: 'NIVEAU 1 • EMAIL VÉRIFIÉ'
      }
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// LOGIN
app.post('/api/auth/login', async (req, res) => {
  try {
    const { identifier, password } = req.body;
    if (!identifier) {
      return res.status(400).json({ error: 'Identifiant ou email requis' });
    }

    let query = supabase.from('profiles').select('*');
    if (identifier.includes('@')) {
      query = query.eq('email', identifier.toLowerCase());
    } else {
      query = query.eq('user_id_display', identifier.toUpperCase());
    }

    const { data: user, error } = await query.maybeSingle();

    if (!user) {
      return res.status(404).json({ error: 'Compte introuvable' });
    }

    const token = jwt.sign(
      { id: user.user_id_display, email: user.email, full_name: user.full_name },
      JWT_SECRET,
      { expiresIn: '30d' }
    );

    return res.json({
      success: true,
      message: 'Connexion réussie',
      token,
      user: {
        id: user.user_id_display,
        username: user.full_name,
        email: user.email,
        balanceXOF: Number(user.balance_xof),
        bonusXOF: Number(user.bonus_xof),
        referralCode: user.referral_code,
        kycLevel: user.kyc_status
      }
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// GET CURRENT USER
app.get('/api/auth/me', authenticateUser, async (req, res) => {
  try {
    const userId = req.user.id || 'NB-849201';
    const { data: user } = await supabase
      .from('profiles')
      .select('*')
      .eq('user_id_display', userId)
      .maybeSingle();

    if (!user) {
      return res.status(404).json({ error: 'Utilisateur introuvable' });
    }

    return res.json({
      success: true,
      user: {
        id: user.user_id_display,
        username: user.full_name,
        email: user.email,
        phone: user.phone,
        balanceXOF: Number(user.balance_xof),
        bonusXOF: Number(user.bonus_xof),
        currency: user.currency,
        referralCode: user.referral_code,
        kycLevel: user.kyc_status
      }
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// ----------------------------------------------------
// 2. WALLET & MOBILE MONEY
// ----------------------------------------------------
app.get('/api/wallet/balance', authenticateUser, async (req, res) => {
  try {
    const userId = req.user.id || 'NB-849201';
    const { data: user } = await supabase
      .from('profiles')
      .select('balance_xof, bonus_xof')
      .eq('user_id_display', userId)
      .maybeSingle();

    return res.json({
      balanceXOF: user ? Number(user.balance_xof) : 25000,
      bonusXOF: user ? Number(user.bonus_xof) : 50000
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// DEPOSIT
app.post('/api/wallet/deposit', authenticateUser, async (req, res) => {
  try {
    const userId = req.user.id || 'NB-849201';
    const { amountXOF, method = 'Wave', phoneNumber } = req.body;
    const amount = Number(amountXOF);

    if (!amount || amount < 500) {
      return res.status(400).json({ error: 'Le montant minimum est de 500 F CFA' });
    }

    // Get current profile
    const { data: user } = await supabase
      .from('profiles')
      .select('balance_xof, bonus_xof')
      .eq('user_id_display', userId)
      .single();

    const currentBal = user ? Number(user.balance_xof) : 0;
    const currentBonus = user ? Number(user.bonus_xof) : 0;

    const bonusCredit = Math.round(amount * 2.0); // 200% bonus
    const newBal = currentBal + amount;
    const newBonus = currentBonus + bonusCredit;

    // Update profile
    await supabase.from('profiles').update({
      balance_xof: newBal,
      bonus_xof: newBonus
    }).eq('user_id_display', userId);

    const txId = `NB-TX-${Math.floor(100000 + Math.random() * 900000)}`;
    await supabase.from('transactions').insert([
      {
        id: txId,
        user_id_display: userId,
        type: 'DEPOSIT',
        amount_xof: amount,
        method: method,
        reference: `MOMO_${Date.now()}`,
        status: 'COMPLETED'
      }
    ]);

    return res.json({
      success: true,
      message: `Dépôt de ${amount.toLocaleString('fr-FR')} F CFA validé avec succès par ${method} !`,
      transactionId: txId,
      newBalanceXOF: newBal,
      newBonusXOF: newBonus,
      bonusAwardedXOF: bonusCredit
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// WITHDRAWAL
app.post('/api/wallet/withdraw', authenticateUser, async (req, res) => {
  try {
    const userId = req.user.id || 'NB-849201';
    const { amountXOF, method = 'Wave', phoneNumber } = req.body;
    const amount = Number(amountXOF);

    if (!amount || amount < 1000) {
      return res.status(400).json({ error: 'Le montant minimum de retrait est de 1 000 F CFA' });
    }

    const { data: user } = await supabase
      .from('profiles')
      .select('balance_xof')
      .eq('user_id_display', userId)
      .single();

    const currentBal = user ? Number(user.balance_xof) : 0;
    if (currentBal < amount) {
      return res.status(400).json({ error: 'Solde réel insuffisant' });
    }

    const newBal = currentBal - amount;
    await supabase.from('profiles').update({ balance_xof: newBal }).eq('user_id_display', userId);

    const txId = `NB-TX-${Math.floor(100000 + Math.random() * 900000)}`;
    await supabase.from('transactions').insert([
      {
        id: txId,
        user_id_display: userId,
        type: 'WITHDRAWAL',
        amount_xof: amount,
        method: method,
        reference: `PAYOUT_${Date.now()}`,
        status: 'COMPLETED'
      }
    ]);

    return res.json({
      success: true,
      message: `Retrait de ${amount.toLocaleString('fr-FR')} F CFA envoyé vers ${method} (${phoneNumber})`,
      transactionId: txId,
      newBalanceXOF: newBal
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// ----------------------------------------------------
// 3. SPORTS & FIXTURES
// ----------------------------------------------------
app.get('/api/sports/fixtures', async (req, res) => {
  try {
    const { data: fixtures, error } = await supabase
      .from('fixtures')
      .select('*')
      .order('id', { ascending: true });

    if (error) {
      return res.status(500).json({ error: error.message });
    }

    const mapped = fixtures.map(f => ({
      id: f.id,
      sport: f.sport,
      league: f.league,
      homeTeam: f.home_team,
      awayTeam: f.away_team,
      homeFlag: f.home_flag,
      awayFlag: f.away_flag,
      matchTime: f.match_time,
      isLive: f.is_live,
      score: f.score,
      odds: {
        odds1: Number(f.odds_1),
        oddsX: f.odds_x ? Number(f.odds_x) : null,
        odds2: Number(f.odds_2)
      },
      markets: f.markets || []
    }));

    return res.json(mapped);
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// ----------------------------------------------------
// 4. BETS & CASH OUT
// ----------------------------------------------------

// PLACE BET
app.post('/api/bets/place', authenticateUser, async (req, res) => {
  try {
    const userId = req.user.id || 'NB-849201';
    const { selections, stakeXOF } = req.body;
    const stake = Number(stakeXOF);

    if (!selections || !Array.isArray(selections) || selections.length === 0) {
      return res.status(400).json({ error: 'Veuillez sélectionner au moins une cote' });
    }
    if (!stake || stake < 100) {
      return res.status(400).json({ error: 'La mise minimale est de 100 F CFA' });
    }

    const { data: user } = await supabase
      .from('profiles')
      .select('balance_xof')
      .eq('user_id_display', userId)
      .single();

    const currentBal = user ? Number(user.balance_xof) : 0;
    if (currentBal < stake) {
      return res.status(400).json({ error: 'Solde insuffisant pour placer ce pari' });
    }

    let totalOdds = 1.0;
    for (const sel of selections) {
      totalOdds *= Number(sel.odds);
    }
    totalOdds = Math.round(totalOdds * 100) / 100;
    const potentialWin = Math.round(stake * totalOdds);
    const betId = `NB-BET-${Math.floor(100000 + Math.random() * 900000)}`;

    // Deduct stake
    const newBal = currentBal - stake;
    await supabase.from('profiles').update({ balance_xof: newBal }).eq('user_id_display', userId);

    // Insert bet
    const { data: newBet, error } = await supabase.from('bets').insert([
      {
        id: betId,
        user_id_display: userId,
        selections: selections,
        stake_xof: stake,
        total_odds: totalOdds,
        potential_win_xof: potentialWin,
        cash_out_amount_xof: Math.round(stake * 0.95),
        can_cash_out: true,
        status: 'PENDING'
      }
    ]).select().single();

    if (error) {
      return res.status(500).json({ error: error.message });
    }

    return res.status(201).json({
      success: true,
      message: 'Pari enregistré avec succès !',
      bet: {
        id: newBet.id,
        stakeXOF: Number(newBet.stake_xof),
        totalOdds: Number(newBet.total_odds),
        potentialWinXOF: Number(newBet.potential_win_xof),
        cashOutAmountXOF: Number(newBet.cash_out_amount_xof),
        canCashOut: newBet.can_cash_out,
        status: newBet.status,
        createdAt: newBet.created_at,
        selections: newBet.selections
      },
      newBalanceXOF: newBal
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// MY BETS
app.get('/api/bets/my-bets', authenticateUser, async (req, res) => {
  try {
    const userId = req.user.id || 'NB-849201';
    const { data: bets, error } = await supabase
      .from('bets')
      .select('*')
      .eq('user_id_display', userId)
      .order('created_at', { ascending: false });

    if (error) {
      return res.status(500).json({ error: error.message });
    }

    const mapped = (bets || []).map(b => ({
      id: b.id,
      stakeXOF: Number(b.stake_xof),
      totalOdds: Number(b.total_odds),
      potentialWinXOF: Number(b.potential_win_xof),
      cashOutAmountXOF: Number(b.cash_out_amount_xof),
      canCashOut: b.can_cash_out,
      status: b.status,
      createdAt: b.created_at,
      selections: b.selections
    }));

    return res.json(mapped);
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// CASH OUT
app.post('/api/bets/cashout', authenticateUser, async (req, res) => {
  try {
    const userId = req.user.id || 'NB-849201';
    const { betId } = req.body;

    const { data: bet, error } = await supabase
      .from('bets')
      .select('*')
      .eq('id', betId)
      .eq('user_id_display', userId)
      .maybeSingle();

    if (!bet) {
      return res.status(404).json({ error: 'Pari introuvable' });
    }
    if (bet.status !== 'PENDING' || !bet.can_cash_out) {
      return res.status(400).json({ error: 'Ce pari ne peut plus faire l’objet d’un Cash Out' });
    }

    const cashOutAmount = Number(bet.cash_out_amount_xof);

    // Update bet status
    await supabase.from('bets').update({
      status: 'CASHED_OUT',
      can_cash_out: false
    }).eq('id', betId);

    // Credit balance
    const { data: user } = await supabase
      .from('profiles')
      .select('balance_xof')
      .eq('user_id_display', userId)
      .single();

    const currentBal = user ? Number(user.balance_xof) : 0;
    const newBal = currentBal + cashOutAmount;
    await supabase.from('profiles').update({ balance_xof: newBal }).eq('user_id_display', userId);

    return res.json({
      success: true,
      message: `Cash Out effectué ! ${cashOutAmount.toLocaleString('fr-FR')} F CFA ont été crédités sur votre compte.`,
      betId,
      cashedOutAmountXOF: cashOutAmount,
      newBalanceXOF: newBal
    });
  } catch (err) {
    return res.status(500).json({ error: err.message });
  }
});

// ----------------------------------------------------
// 5. COUPON SAVE & LOAD
// ----------------------------------------------------
const savedCoupons = new Map();

app.post('/api/coupon/save', (req, res) => {
  const { selections } = req.body;
  const code = `NB-${Math.random().toString(36).substring(2, 6).toUpperCase()}`;
  savedCoupons.set(code, selections);
  return res.json({ success: true, couponCode: code });
});

app.get('/api/coupon/:code', (req, res) => {
  const code = req.params.code.toUpperCase();
  const selections = savedCoupons.get(code);
  if (!selections) {
    return res.status(404).json({ error: 'Code coupon inexistant ou expiré' });
  }
  return res.json({ success: true, couponCode: code, selections });
});

export default app;
