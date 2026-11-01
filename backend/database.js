import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const DB_FILE = path.join(__dirname, 'newbet_data.json');

// Initial default state if file doesn't exist
const DEFAULT_DATA = {
  users: [
    {
      id: "NB-948210",
      username: "Christian N.",
      email: "christian@newbet.bj",
      phone: "+229 61 00 00 00",
      authProvider: "google",
      googleId: "google_109283019283",
      balanceXOF: 37600,
      bonusXOF: 5000,
      promoCodeUsed: "NEWBET2026",
      referralCode: "NEW-CHRIS24",
      kycLevel: "NIVEAU 2 • VÉRIFIÉ",
      createdAt: new Date().toISOString()
    }
  ],
  transactions: [
    {
      id: "TX-90124",
      userId: "NB-948210",
      type: "DEPOSIT",
      amountXOF: 5000,
      method: "Wave",
      status: "COMPLETED",
      reference: "WAVE-B72F9",
      createdAt: new Date().toISOString()
    }
  ],
  bets: [
    {
      id: "BET-21594",
      userId: "NB-948210",
      date: "Aujourd'hui, 21:15",
      selections: [
        {
          eventId: "ucl_1",
          matchTitle: "Real Madrid vs Manchester City",
          league: "UEFA Champions League",
          marketLabel: "Real Madrid (1)",
          odds: 2.15
        }
      ],
      stakeXOF: 1000,
      totalOdds: 2.15,
      potentialWinXOF: 2150,
      isLive: true,
      canCashOut: true,
      cashOutAmountXOF: 950,
      status: "PENDING",
      createdAt: new Date().toISOString()
    },
    {
      id: "BET-79102",
      userId: "NB-948210",
      date: "Hier, 22:30",
      selections: [
        {
          eventId: "laliga_1",
          matchTitle: "Barcelone vs Séville",
          league: "La Liga",
          marketLabel: "Barcelone (1)",
          odds: 1.45
        }
      ],
      stakeXOF: 10000,
      totalOdds: 1.45,
      potentialWinXOF: 14500,
      isLive: false,
      canCashOut: false,
      cashOutAmountXOF: 0,
      status: "WON",
      createdAt: new Date(Date.now() - 86400000).toISOString()
    }
  ],
  fixtures: [
    {
      id: "ucl_1",
      sport: "Football",
      league: "UEFA Champions League",
      leagueFlag: "🇪🇺",
      homeTeam: "Real Madrid",
      awayTeam: "Manchester City",
      minute: "76'",
      scoreHome: 2,
      scoreAway: 1,
      isLive: true,
      startTime: "21:00",
      oddsHome: 2.15,
      oddsDraw: 3.60,
      oddsAway: 3.25,
      oddsOver25: 1.62,
      oddsUnder25: 2.20,
      oddsBttsYes: 1.50,
      oddsBttsNo: 2.40
    },
    {
      id: "epl_1",
      sport: "Football",
      league: "Premier League",
      leagueFlag: "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
      homeTeam: "Arsenal FC",
      awayTeam: "Liverpool FC",
      minute: "44'",
      scoreHome: 1,
      scoreAway: 1,
      isLive: true,
      startTime: "20:30",
      oddsHome: 2.40,
      oddsDraw: 3.30,
      oddsAway: 2.85,
      oddsOver25: 1.70,
      oddsUnder25: 2.05,
      oddsBttsYes: 1.58,
      oddsBttsNo: 2.25
    },
    {
      id: "ucl_2",
      sport: "Football",
      league: "UEFA Champions League",
      leagueFlag: "🇪🇺",
      homeTeam: "Paris Saint-Germain",
      awayTeam: "Bayern Munich",
      minute: null,
      scoreHome: null,
      scoreAway: null,
      isLive: false,
      startTime: "Ce soir 21:00",
      oddsHome: 2.25,
      oddsDraw: 3.75,
      oddsAway: 2.90,
      oddsOver25: 1.55,
      oddsUnder25: 2.35,
      oddsBttsYes: 1.48,
      oddsBttsNo: 2.50
    },
    {
      id: "laliga_1",
      sport: "Football",
      league: "La Liga",
      leagueFlag: "🇪🇸",
      homeTeam: "FC Barcelone",
      awayTeam: "Atletico Madrid",
      minute: null,
      scoreHome: null,
      scoreAway: null,
      isLive: false,
      startTime: "Demain 21:00",
      oddsHome: 1.82,
      oddsDraw: 3.70,
      oddsAway: 4.40,
      oddsOver25: 1.80,
      oddsUnder25: 1.95,
      oddsBttsYes: 1.72,
      oddsBttsNo: 2.00
    },
    {
      id: "can_1",
      sport: "Football",
      league: "Coupe d'Afrique des Nations",
      leagueFlag: "🌍",
      homeTeam: "Sénégal",
      awayTeam: "Côte d'Ivoire",
      minute: null,
      scoreHome: null,
      scoreAway: null,
      isLive: false,
      startTime: "Demain 18:00",
      oddsHome: 2.10,
      oddsDraw: 3.10,
      oddsAway: 3.65,
      oddsOver25: 2.15,
      oddsUnder25: 1.65,
      oddsBttsYes: 1.95,
      oddsBttsNo: 1.75
    }
  ]
};

// Safe DB loader and saver
class Database {
  constructor() {
    this.data = this.load();
  }

  load() {
    try {
      if (fs.existsSync(DB_FILE)) {
        const raw = fs.readFileSync(DB_FILE, 'utf8');
        return JSON.parse(raw);
      }
    } catch (e) {
      console.error('Error loading DB file, fallback to default:', e.message);
    }
    this.save(DEFAULT_DATA);
    return JSON.parse(JSON.stringify(DEFAULT_DATA));
  }

  save(dataToSave = this.data) {
    try {
      fs.writeFileSync(DB_FILE, JSON.stringify(dataToSave, null, 2), 'utf8');
    } catch (e) {
      console.error('Error saving DB file:', e.message);
    }
  }

  // --- Users ---
  findUserById(id) {
    return this.data.users.find(u => u.id === id);
  }

  findUserByEmail(email) {
    return this.data.users.find(u => u.email && u.email.toLowerCase() === email.toLowerCase());
  }

  findUserByGoogleId(googleId) {
    return this.data.users.find(u => u.googleId === googleId);
  }

  createUser(userData) {
    this.data.users.push(userData);
    this.save();
    return userData;
  }

  updateUser(id, updates) {
    const user = this.findUserById(id);
    if (user) {
      Object.assign(user, updates);
      this.save();
    }
    return user;
  }

  // --- Bets ---
  getUserBets(userId) {
    return this.data.bets.filter(b => b.userId === userId);
  }

  addBet(bet) {
    this.data.bets.unshift(bet);
    this.save();
    return bet;
  }

  updateBet(id, updates) {
    const bet = this.data.bets.find(b => b.id === id);
    if (bet) {
      Object.assign(bet, updates);
      this.save();
    }
    return bet;
  }

  // --- Transactions ---
  getUserTransactions(userId) {
    return this.data.transactions.filter(t => t.userId === userId);
  }

  addTransaction(tx) {
    this.data.transactions.unshift(tx);
    this.save();
    return tx;
  }

  // --- Fixtures ---
  getFixtures() {
    return this.data.fixtures;
  }
}

export const db = new Database();

